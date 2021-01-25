package edu.syr.smalltalk.ui.webrtc

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonObject
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.webrtc.*
import org.webrtc.PeerConnection.*
import kotlin.math.max
import kotlin.math.min

@Suppress("unused")
class PeerConnectionParameters(
    val videoCallEnabled: Boolean, val loopback: Boolean,
    val videoWidth: Int, val videoHeight: Int, val videoFps: Int, val videoStartBitrate: Int,
    val videoCodec: String, val videoCodecHwAcceleration: Boolean,
    val audioStartBitrate: Int, val audioCodec: String,
    val cpuOveruseDetection: Boolean
)

class RTCClient(
    private val context: Application,
    private val pcParams: PeerConnectionParameters,
    private val localSink: VideoSink,
    private val remoteSink: VideoSink,
    private val transferCallback: (String) -> Unit
) {
    private val peerFactory by lazy { buildPeerConnectionFactory() }
    private val peers = mutableMapOf<Int, Peer>()
    private val iceServer = listOf(IceServer.builder("stun:stun.stunprotocol.org").createIceServer())
    private val pcConstraints = MediaConstraints()
    private val rootEglBase = EglBase.create()
    private val videoCapturer by lazy { getVideoCapture(context) }

    private lateinit var localStream : MediaStream
    private lateinit var localVideoSource: VideoSource
    private lateinit var localVideoTrack: VideoTrack
    private lateinit var localAudioSource: AudioSource
    private lateinit var localAudioTrack: AudioTrack

    private val remoteEndPoints = BooleanArray(MAX_PEER) { false }
    private val remoteStreamMap = hashMapOf<Int, MediaStream>()
    private var curRemoteEndPoint: Int = 0
    private var curRemoteTrack: VideoTrack? = null

    init {
        val options = PeerConnectionFactory.InitializationOptions.builder(context)
            .setEnableInternalTracer(true)
            .setFieldTrials("WebRTC-H264HighProfile/Enabled/")
            .createInitializationOptions()
        PeerConnectionFactory.initialize(options)
        pcConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
        pcConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        pcConstraints.optional.add(MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"))
    }

    private inner class Peer(val id: Int, val endPoint: Int) : SdpObserver, Observer {
        val pc = peerFactory.createPeerConnection(iceServer, this)

        override fun onCreateSuccess(p0: SessionDescription) {
            pc?.let { pc ->
                val from = SmallTalkApplication.getCurrentUserId(context)
                val to = id
                val payload = JsonObject()
                payload.addProperty("from", from)
                payload.addProperty("to", to)
                payload.addProperty("type", p0.type.canonicalForm())
                payload.addProperty("sdp", p0.description)
                transferCallback(Gson().toJson(payload))
                pc.setLocalDescription(this, p0)
            }
        }

        override fun onSetSuccess() {
            Log.w("Peer", "onSetSuccess")
        }

        override fun onCreateFailure(p0: String) {
            Log.w("Peer", "onCreateFailure$p0")
        }

        override fun onSetFailure(p0: String) {
            Log.w("Peer", "onSetFailure$p0")
        }

        override fun onSignalingChange(p0: SignalingState) {
            Log.w("Peer", "onSignalingChange$p0")
        }

        override fun onIceConnectionChange(p0: IceConnectionState) {
            Log.w("Peer", "onIceConnectionChange$p0")

            if (p0 == IceConnectionState.DISCONNECTED) {
                removePeer(id)
            }
        }

        override fun onStandardizedIceConnectionChange(newState: IceConnectionState?) {
            Log.w("Peer", "onStandardizedIceConnectionChange$newState")
        }

        override fun onConnectionChange(newState: PeerConnectionState?) {
            Log.w("Peer", "onConnectionChange$newState")
        }

        override fun onIceConnectionReceivingChange(p0: Boolean) {
            Log.w("Peer", "onIceConnectionReceivingChange$p0")
        }

        override fun onIceGatheringChange(p0: IceGatheringState) {
            Log.w("Peer", "onIceGatheringChange$p0")
        }

        override fun onIceCandidate(p0: IceCandidate) {
            pc?.let { pc ->
                val from = SmallTalkApplication.getCurrentUserId(context)
                val to = id
                val payload = JsonObject()
                payload.addProperty("from", from)
                payload.addProperty("to", to)
                payload.addProperty("type", "candidate")
                payload.addProperty("label", p0.sdpMLineIndex)
                payload.addProperty("id", p0.sdpMid)
                payload.addProperty("candidate", p0.sdp)
                transferCallback(Gson().toJson(payload))
                pc.addIceCandidate(p0)
            }
        }

        override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
            Log.w("Peer", "onIceCandidatesRemoved$p0")
        }

        override fun onAddStream(p0: MediaStream) {
            Log.w("Peer", "onAddStream$p0")

            val isFirstStream = remoteStreamMap.isEmpty()
            remoteStreamMap[endPoint] = p0
            if (isFirstStream) {
                curRemoteEndPoint = endPoint
                remoteStreamMap[endPoint]?.let {
                    curRemoteTrack = it.videoTracks[0]
                    curRemoteTrack?.addSink(remoteSink)
                }
            }
        }

        override fun onRemoveStream(p0: MediaStream) {
            Log.w("Peer", "onRemoveStream$p0")

            if (curRemoteEndPoint == endPoint) {
                remoteStreamMap[endPoint]?.let {
                    if (curRemoteTrack === it.videoTracks[0]) {
                        curRemoteTrack?.removeSink(remoteSink)
                    } else {
                        Toast.makeText(context, context.getString(R.string.toast_video_chat_internal_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            remoteStreamMap.remove(endPoint)
            if (remoteStreamMap.isNotEmpty()) {
                curRemoteTrack = remoteStreamMap.entries.iterator().next().value.videoTracks[0]
                curRemoteTrack?.addSink(remoteSink)
            } else {
                curRemoteEndPoint = 0
                curRemoteTrack = null
            }
        }

        override fun onDataChannel(p0: DataChannel) {
            Log.w("Peer", "onDataChannel$p0")
        }

        override fun onRenegotiationNeeded() {
            Log.w("Peer", "onRenegotiationNeeded")
        }

        override fun onAddTrack(p0: RtpReceiver, p1: Array<out MediaStream>) {
            Log.w("Peer", "onAddTrack")
        }
    }

    private fun buildPeerConnectionFactory(): PeerConnectionFactory {
        return PeerConnectionFactory.builder()
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(rootEglBase.eglBaseContext))
            .setVideoEncoderFactory(
                DefaultVideoEncoderFactory(
                    rootEglBase.eglBaseContext,
                    true,
                    true
                )
            )
            .setOptions(PeerConnectionFactory.Options().apply {
                disableEncryption = true
                disableNetworkMonitor = true
            }).createPeerConnectionFactory()
    }

    private fun getVideoCapture(context: Context) =
        Camera2Enumerator(context).run {
            deviceNames.find {
                isFrontFacing(it)
            } ?.let {
                createCapturer(it, null)
            } ?: throw IllegalStateException()
        }

    fun initSurfaceView(view: SurfaceViewRenderer) = view.run {
        setMirror(true)
        setEnableHardwareScaler(true)
        init(rootEglBase.eglBaseContext, null)
    }

    fun startLocalVideoCapture(localVideoOutput: SurfaceViewRenderer) {
        localStream = peerFactory.createLocalMediaStream("local_stream")

        if (pcParams.videoCallEnabled) {
            localVideoSource = peerFactory.createVideoSource(false)

            val surfaceTextureHelper = SurfaceTextureHelper.create(
                Thread.currentThread().name,
                rootEglBase.eglBaseContext
            )

            (videoCapturer as VideoCapturer).initialize(
                surfaceTextureHelper,
                localVideoOutput.context,
                localVideoSource.capturerObserver
            )

            videoCapturer.startCapture(pcParams.videoWidth, pcParams.videoHeight, pcParams.videoFps)

            localVideoTrack = peerFactory.createVideoTrack("local_video_track", localVideoSource)
            localStream.addTrack(localVideoTrack)
        }

        localAudioSource = peerFactory.createAudioSource(MediaConstraints())
        localAudioTrack = peerFactory.createAudioTrack("local_audio_track", localAudioSource)
        localStream.addTrack(localAudioTrack)
        localVideoTrack.addSink(localSink)
    }

    private fun addPeer(id: Int, endPoint: Int): Peer {
        val peer = Peer(id, endPoint)
        peers[id] = peer
        remoteEndPoints[endPoint] = true
        return peer
    }

    private fun removePeer(id: Int) {
        val peer = peers.getValue(id)
        peers.remove(peer.id)
        if (curRemoteEndPoint == peer.endPoint) {
            curRemoteTrack?.removeSink(remoteSink)
            curRemoteTrack = null
        }
        remoteStreamMap.remove(peer.endPoint)
        remoteEndPoints[peer.endPoint] = false
        GlobalScope.launch {
            peer.pc?.close()
        }
    }

    private fun findEndPoint(): Int {
        for (i in 0 until MAX_PEER) if (!remoteEndPoints[i]) return i
        return MAX_PEER
    }

    fun initConnections(payload: String) {
        val data = Gson().fromJson(payload, JsonObject::class.java)
        val candidates = data.get("candidates").asJsonArray
        candidates.forEach { candidate ->
            val candidateId = candidate.asInt
            if (!peers.containsKey(candidateId)) {
                val endPoint = findEndPoint()
                if (endPoint != MAX_PEER) {
                    val peer = addPeer(candidateId, endPoint)
                    peer.pc?.addStream(localStream)
                    peer.pc?.createOffer(peer, pcConstraints)
                }
            }
        }
    }

    fun onMessage(payload: String) {
        val data = Gson().fromJson(payload, JsonObject::class.java)
        val from = data.get("from").asInt
        @Suppress("UNUSED_VARIABLE")
        val to = data.get("to").asInt
        if (!peers.containsKey(from)) {
            val endPoint = findEndPoint()
            val peer = addPeer(from, endPoint)
            peer.pc?.addStream(localStream)
        }
        when (val type = data.get("type").asString) {
            "offer" -> {
                val sdp = data.get("sdp").asString
                peers[from]?.let { peer ->
                    peer.pc?.setRemoteDescription(peer,
                        SessionDescription(SessionDescription.Type.fromCanonicalForm(type), sdp))
                    peer.pc?.createAnswer(peer, pcConstraints)
                }
            }
            "answer" -> {
                val sdp = data.get("sdp").asString
                peers[from]?.let { peer ->
                    peer.pc?.setRemoteDescription(peer,
                        SessionDescription(SessionDescription.Type.fromCanonicalForm(type), sdp))
                }
            }
            "candidate" -> {
                peers[from]?.let { peer ->
                    peer.pc?.let { pc ->
                        if (pc.remoteDescription != null) {
                            val candidate = IceCandidate(
                                data.get("id").asString,
                                data.get("label").asInt,
                                data.get("candidate").asString)
                            pc.addIceCandidate(candidate)
                        }
                    }
                }
            }
        }
    }

    fun switchCamera() {
        videoCapturer.switchCamera(null)
    }

    fun prevRemotePeer(sink: VideoSink) {
        curRemoteEndPoint = max(curRemoteEndPoint - 1, 0)
        curRemoteTrack?.removeSink(sink)
        if (remoteStreamMap.containsKey(curRemoteEndPoint)) {
            curRemoteTrack = remoteStreamMap[curRemoteEndPoint]?.let { it.videoTracks[0] }
            curRemoteTrack?.addSink(sink)
        }
    }

    fun nextRemotePeer(sink: VideoSink) {
        curRemoteEndPoint = min(curRemoteEndPoint + 1, MAX_PEER - 1)
        curRemoteTrack?.removeSink(sink)
        if (remoteStreamMap.containsKey(curRemoteEndPoint)) {
            curRemoteTrack = remoteStreamMap[curRemoteEndPoint]?.let { it.videoTracks[0] }
            curRemoteTrack?.addSink(sink)
        }
    }

    fun onDestroy() {
        videoCapturer.stopCapture()
        videoCapturer.dispose()
        localVideoSource.dispose()
        localAudioSource.dispose()
        localStream.dispose()
        peerFactory.stopAecDump()
        peers.forEach { peer -> peer.value.pc?.close() }
        peers.clear()
        peerFactory.dispose()
        PeerConnectionFactory.stopInternalTracingCapture()
        PeerConnectionFactory.shutdownInternalTracer()
        rootEglBase.release()
    }

    companion object {
        const val MAX_PEER = 4
    }
}
