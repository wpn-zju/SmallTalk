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

class VideoChatActivity : AppCompatActivity(), ISmallTalkServiceProvider {
    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        private const val RECORD_PERMISSION = Manifest.permission.RECORD_AUDIO
    }

    private lateinit var service: ISmallTalkService
    private var bound: Boolean = false
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            service = (binder as RootService.RootServiceBinder).getService()
            bound = true
            getService()?.webRTCCall(channel, "connect", "connect")
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
    private lateinit var channel: String
    private var isFrontCamera = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_chat)
        intent.getStringExtra("channel")?.let { channel = it } ?: finish()

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

        rtcClient = RTCClient(application, params, local_view, remote_view) {
            getService()?.webRTCCall(channel, "transfer", it)
        }
        rtcClient.initSurfaceView(remote_view)
        rtcClient.initSurfaceView(local_view)

        switch_button.isClickable = true
        switch_button.setOnClickListener {
            isFrontCamera = !isFrontCamera
            local_view.setMirror(isFrontCamera)
            rtcClient.switchCamera()
        }

        prev_remote_button.isClickable = true
        prev_remote_button.setOnClickListener {
            rtcClient.prevRemotePeer(remote_view)
        }

        next_remote_button.isClickable = true
        next_remote_button.setOnClickListener {
            rtcClient.nextRemotePeer(remote_view)
        }

        enterFullScreenMode()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.setOnApplyWindowInsetsListener { _, insets ->
                if (insets.isVisible(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())) {
                    switch_button.visibility = View.VISIBLE
                    prev_remote_button.visibility = View.VISIBLE
                    next_remote_button.visibility = View.VISIBLE
                    window.decorView.postDelayed({
                        enterFullScreenMode()
                    }, 3000)
                } else {
                    switch_button.visibility = View.INVISIBLE
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
                    prev_remote_button.visibility = View.VISIBLE
                    next_remote_button.visibility = View.VISIBLE
                    window.decorView.postDelayed({
                        enterFullScreenMode()
                    }, 3000)
                } else {
                    switch_button.visibility = View.INVISIBLE
                    prev_remote_button.visibility = View.INVISIBLE
                    next_remote_button.visibility = View.INVISIBLE
                }
            }
        }

        checkPermission()
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, RECORD_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission()
        } else {
            onPermissionGranted()
        }
    }

    private fun requestPermission(dialogShown: Boolean = false) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, CAMERA_PERMISSION) &&
            ActivityCompat.shouldShowRequestPermissionRationale(this, RECORD_PERMISSION) && !dialogShown) {
            showPermissionRationaleDialog()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(CAMERA_PERMISSION, RECORD_PERMISSION),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun onPermissionGranted() {
        rtcClient.startLocalVideoCapture(local_view)
    }

    private fun enterFullScreenMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_TOUCH
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWebRTCMessage(event: WebRTCEvent) {
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
                requestPermission(true)
            }
            .setNegativeButton(getString(R.string.alert_dialog_camera_permission_deny)) { dialog, _ ->
                dialog.dismiss()
                onPermissionDenied()
            }
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }

    private fun onPermissionDenied() {
        Toast.makeText(this, getString(R.string.toast_camera_permission_denied), Toast.LENGTH_LONG).show()

        requestPermission()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.app_name))
            .setMessage(getString(R.string.alert_dialog_video_chat_exit_hint))
            .setPositiveButton(getString(R.string.alert_dialog_confirm)) { dialog, _ ->
                dialog.dismiss()
                getService()?.webRTCCall(channel, "disconnect", "disconnect")
                finish()
            }
            .setNegativeButton(getString(R.string.alert_dialog_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroy() {
        local_view.release()
        remote_view.release()
        rtcClient.onDestroy()
        super.onDestroy()
    }
}
