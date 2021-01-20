package edu.syr.smalltalk.ui.webrtc

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.ISmallTalkService
import edu.syr.smalltalk.service.ISmallTalkServiceProvider
import edu.syr.smalltalk.service.RootService
import edu.syr.smalltalk.service.eventbus.WebRTCEvent
import kotlinx.android.synthetic.main.activity_video_chat.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.webrtc.MediaStream
import org.webrtc.MediaStreamTrack
import org.webrtc.VideoTrack
import kotlin.math.max
import kotlin.math.min


class VideoChatActivity : AppCompatActivity(), RTCClient.RtcListener, ISmallTalkServiceProvider {
    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
    }

    private lateinit var service: ISmallTalkService
    private var bound: Boolean = false
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            service = (binder as RootService.RootServiceBinder).getService()
            bound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            bound = false
        }
    }

    override fun hasService(): Boolean {
        return bound
    }

    override fun getService(): ISmallTalkService? {
        return if (bound) {
            service
        } else {
            null
        }
    }

    override fun onStart() {
        super.onStart()

        // Write following lines in the first activity
        Intent(this, RootService::class.java).also { intent -> startService(
            intent
        ) }

        Intent(this, RootService::class.java).also { intent -> bindService(
            intent,
            connection,
            Context.BIND_AUTO_CREATE
        ) }

        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()

        unbindService(connection)
        bound = false

        EventBus.getDefault().unregister(this)
    }

    private lateinit var rtcClient: RTCClient
    private var localStream: MediaStream? = null
    private val streamMap = hashMapOf<Int, MediaStream>()
    private var currentRemoteEndPoint: Int = 0
    private var lastRemoteTrack: VideoTrack? = null
    private var isFrontCamera = true
    private var channel: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_chat)
        channel = intent.getStringExtra("channel")
        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission()
        } else {
            onCameraPermissionGranted()
        }
    }

    private fun requestCameraPermission(dialogShown: Boolean = false) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, CAMERA_PERMISSION) && !dialogShown) {
            showPermissionRationaleDialog()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(CAMERA_PERMISSION),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    // Init after grant camera permission
    private fun onCameraPermissionGranted() {
        val params = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val bounds = windowManager.currentWindowMetrics.bounds
            PeerConnectionParameters(
                videoCallEnabled = true,
                loopback = false,
                videoWidth = bounds.width(),
                videoHeight = bounds.height(),
                videoFps = 30,
                videoStartBitrate = 1,
                videoCodec = "VP9",
                videoCodecHwAcceleration = true,
                audioStartBitrate = 1,
                audioCodec = "opus",
                cpuOveruseDetection = true
            )
        } else {
            val size = Point()
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getSize(size)
            PeerConnectionParameters(
                videoCallEnabled = true,
                loopback = false,
                videoWidth = size.x,
                videoHeight = size.y,
                videoFps = 30,
                videoStartBitrate = 1,
                videoCodec = "VP9",
                videoCodecHwAcceleration = true,
                audioStartBitrate = 1,
                audioCodec = "opus",
                cpuOveruseDetection = true
            )
        }

        rtcClient = RTCClient(application, this, params)
        rtcClient.initSurfaceView(remote_view)
        rtcClient.initSurfaceView(local_view)
        rtcClient.startLocalVideoCapture(local_view)

        switch_button.isClickable = true
        switch_button.setOnClickListener {
            isFrontCamera = !isFrontCamera
            local_view.setMirror(isFrontCamera)
            rtcClient.switchCamera()
        }

        call_button.isClickable = true
        call_button.setOnClickListener {
            channel?.let { channel ->
                getService()?.let { service ->
                    call_button.isClickable = false
                    hang_up_button.isClickable = true
                    service.webRTCCall(channel, "connect", "connect")
                }
            }
        }

        hang_up_button.isClickable = false
        hang_up_button.setOnClickListener {
            channel?.let { channel ->
                getService()?.let { service ->
                    call_button.isClickable = true
                    hang_up_button.isClickable = false
                    service.webRTCCall(channel, "disconnect", "disconnect")
                }
            }
        }

        prev_remote_button.isClickable = true
        prev_remote_button.setOnClickListener {
            currentRemoteEndPoint = max(currentRemoteEndPoint - 1, 0)
            lastRemoteTrack?.removeSink(remote_view)
            if (streamMap.containsKey(currentRemoteEndPoint)) {
                lastRemoteTrack = streamMap[currentRemoteEndPoint]?.let { it.videoTracks[0] }
                lastRemoteTrack?.addSink(remote_view)
            }
        }

        next_remote_button.isClickable = true
        next_remote_button.setOnClickListener {
            currentRemoteEndPoint = min(currentRemoteEndPoint + 1, RTCClient.MAX_PEER - 1)
            lastRemoteTrack?.removeSink(remote_view)
            if (streamMap.containsKey(currentRemoteEndPoint)) {
                lastRemoteTrack = streamMap[currentRemoteEndPoint]?.let { it.videoTracks[0] }
                lastRemoteTrack?.addSink(remote_view)
            }
        }

        immersiveMode()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.setOnApplyWindowInsetsListener { _, insets ->
                if (insets.isVisible(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())) {
                    switch_button.visibility = View.VISIBLE
                    call_button.visibility = View.VISIBLE
                    hang_up_button.visibility = View.VISIBLE
                    prev_remote_button.visibility = View.VISIBLE
                    next_remote_button.visibility = View.VISIBLE

                    window.decorView.postDelayed({
                        immersiveMode()
                    }, 3000)
                } else {
                    switch_button.visibility = View.INVISIBLE
                    call_button.visibility = View.INVISIBLE
                    hang_up_button.visibility = View.INVISIBLE
                    prev_remote_button.visibility = View.INVISIBLE
                    next_remote_button.visibility = View.INVISIBLE
                }
                insets
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
                if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                    switch_button.visibility = View.VISIBLE
                    call_button.visibility = View.VISIBLE
                    hang_up_button.visibility = View.VISIBLE
                    prev_remote_button.visibility = View.VISIBLE
                    next_remote_button.visibility = View.VISIBLE
                    window.decorView.postDelayed({
                        immersiveMode()
                    }, 3000)
                } else {
                    switch_button.visibility = View.INVISIBLE
                    call_button.visibility = View.INVISIBLE
                    hang_up_button.visibility = View.INVISIBLE
                    prev_remote_button.visibility = View.INVISIBLE
                    next_remote_button.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun immersiveMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWebRTCTestMessage(event: WebRTCEvent) {
        if (event.command == "init") {
            rtcClient.initConnections(event.payload)
        } else if (event.command == "transfer") {
            rtcClient.onMessage(event.payload)
        }
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.alert_dialog_camera_permission_required))
            .setMessage(getString(R.string.alert_dialog_camera_permission_required_detail))
            .setPositiveButton(getString(R.string.alert_dialog_camera_permission_grant)) { dialog, _ ->
                dialog.dismiss()
                requestCameraPermission(true)
            }
            .setNegativeButton(getString(R.string.alert_dialog_camera_permission_deny)) { dialog, _ ->
                dialog.dismiss()
                onCameraPermissionDenied()
            }
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            onCameraPermissionGranted()
        } else {
            onCameraPermissionDenied()
        }
    }

    private fun onCameraPermissionDenied() {
        Toast.makeText(this, getString(R.string.toast_camera_permission_denied), Toast.LENGTH_LONG).show()

        requestCameraPermission()
    }

    override fun onSendMessage(payload: String) {
        channel?.let { channel ->
            getService()?.webRTCCall(channel, "transfer", payload)
        }
    }

    override fun onStatusChanged(newStatus: String) {
        Log.w("RTC Callback", "onStatusChanged$newStatus")
    }

    override fun onLocalTrack(localTrack: MediaStreamTrack) {

    }

    override fun onAddRemoteTrack(remoteTrack: MediaStreamTrack, endPoint: Int) {

    }

    override fun onRemoveRemoteTrack(endPoint: Int) {

    }

    override fun onLocalStream(localStream: MediaStream) {
        this.localStream = localStream
        localStream.videoTracks[0].addSink(local_view)
    }

    override fun onAddRemoteStream(remoteStream: MediaStream, endPoint: Int) {
        val isFirstStream = streamMap.isEmpty()
        streamMap[endPoint] = remoteStream
        if (isFirstStream) {
            currentRemoteEndPoint = endPoint
            streamMap[endPoint]?.let {
                it.videoTracks[0].addSink(remote_view)
            }
        }
    }

    override fun onRemoveRemoteStream(endPoint: Int) {
        if (currentRemoteEndPoint == endPoint) {
            streamMap[endPoint]?.let {
                it.videoTracks[0].removeSink(remote_view)
            }
        }
        streamMap.remove(endPoint)
        if (streamMap.isNotEmpty()) {
            streamMap.entries.iterator().next().value.videoTracks[0].addSink(remote_view)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        localStream?.let {
            it.videoTracks[0].removeSink(local_view)
        }
        rtcClient.onDestroy()
        super.onDestroy()
    }
}
