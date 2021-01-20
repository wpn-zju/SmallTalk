package edu.syr.smalltalk.ui.webrtc

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import edu.syr.smalltalk.service.KVPConstant
import org.webrtc.*
import org.webrtc.PeerConnection.*

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
    private val mListener: RtcListener,
    private val pcParams: PeerConnectionParameters
) {
    interface RtcListener {
        fun onSendMessage(payload: String)
        fun onStatusChanged(newStatus: String)

        fun onLocalTrack(localTrack: MediaStreamTrack)
        fun onAddRemoteTrack(remoteTrack: MediaStreamTrack, endPoint: Int)
        fun onRemoveRemoteTrack(endPoint: Int)
        fun onLocalStream(localStream: MediaStream)
        fun onAddRemoteStream(remoteStream: MediaStream, endPoint: Int)
        fun onRemoveRemoteStream(endPoint: Int)
    }

    private val endPoints = BooleanArray(MAX_PEER) { false }
    private val peerFactory by lazy { buildPeerConnectionFactory() }
    private val peers = hashMapOf<Int, Peer>()
    private val iceServer = listOf(IceServer.builder("stun:stun.l.google.com:19302").createIceServer())
    private val pcConstraints = MediaConstraints()
    private val rootEglBase = EglBase.create()
    private val videoCapturer by lazy { getVideoCapture(context) }

    private lateinit var localStream : MediaStream
    private lateinit var localVideoSource: VideoSource
    private lateinit var localVideoTrack: VideoTrack
    private lateinit var localAudioSource: AudioSource
    private lateinit var localAudioTrack: AudioTrack

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
                val from = PreferenceManager.getDefaultSharedPreferences(context)
                    .getInt(KVPConstant.K_CURRENT_USER_ID, 0)
                val to = id
                val payload = JsonObject()
                payload.addProperty("from", from)
                payload.addProperty("to", to)
                payload.addProperty("type", p0.type.canonicalForm())
                payload.addProperty("sdp", p0.description)
                mListener.onSendMessage(Gson().toJson(payload))

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

        override fun onIceConnectionReceivingChange(p0: Boolean) {
            Log.w("Peer", "onIceConnectionReceivingChange$p0")
        }

        override fun onIceGatheringChange(p0: IceGatheringState) {
            Log.w("Peer", "onIceGatheringChange$p0")
        }

        override fun onIceCandidate(p0: IceCandidate) {
            val from = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(KVPConstant.K_CURRENT_USER_ID, 0)
            val to = id
            val payload = JsonObject()
            payload.addProperty("from", from)
            payload.addProperty("to", to)
            payload.addProperty("type", "candidate")
            payload.addProperty("label", p0.sdpMLineIndex)
            payload.addProperty("id", p0.sdpMid)
            payload.addProperty("candidate", p0.sdp)
            mListener.onSendMessage(Gson().toJson(payload))

            pc?.addIceCandidate(p0)
        }

        override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
            Log.w("Peer", "onIceCandidatesRemoved$p0")
        }

        override fun onAddStream(p0: MediaStream) {
            Log.w("Peer", "onAddStream$p0")

            mListener.onAddRemoteStream(p0, endPoint)
        }

        override fun onRemoveStream(p0: MediaStream) {
            Log.w("Peer", "onRemoveStream$p0")

            mListener.onRemoveRemoteStream(endPoint)
        }

        override fun onDataChannel(p0: DataChannel) {
            Log.w("Peer", "onDataChannel$p0")
        }

        override fun onRenegotiationNeeded() {
            Log.w("Peer", "onRenegotiationNeeded")
        }

        override fun onAddTrack(p0: RtpReceiver, p1: Array<out MediaStream>) {
            // p1.forEach { stream ->
            //     stream.videoTracks.forEach { track -> mListener.onAddRemoteTrack(track, endPoint) }
            //     stream.audioTracks.forEach { track -> mListener.onAddRemoteTrack(track, endPoint) }
            // }
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

    // Do for each video view on the user interface
    fun initSurfaceView(view: SurfaceViewRenderer) = view.run {
        setMirror(true)
        setEnableHardwareScaler(true)
        init(rootEglBase.eglBaseContext, null)
    }

    fun startLocalVideoCapture(localVideoOutput: SurfaceViewRenderer) {
        localStream = peerFactory.createLocalMediaStream("local_stream")

        // Local Video
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

        // Local Audio
        localAudioSource = peerFactory.createAudioSource(MediaConstraints())
        localAudioTrack = peerFactory.createAudioTrack("local_audio_track", localAudioSource)
        localStream.addTrack(localAudioTrack)

        mListener.onLocalStream(localStream)
        // mListener.onLocalTrack(localVideoTrack)
        // mListener.onLocalTrack(localAudioTrack)
    }

    private fun addPeer(id: Int, endPoint: Int) : Peer {
        val peer = Peer(id, endPoint)
        peers[id] = peer
        endPoints[endPoint] = true
        return peer
    }

    private fun removePeer(id: Int) {
        val peer = peers.getValue(id)
        mListener.onRemoveRemoteTrack(peer.endPoint)
        peer.pc?.close()
        peers.remove(peer.id)
        endPoints[peer.endPoint] = false
    }

    private fun findEndPoint(): Int {
        for (i in 0 until MAX_PEER) if (!endPoints[i]) return i
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
                    // localStream.audioTracks.forEach { peer.pc.addTrack(it) }
                    // localStream.videoTracks.forEach { peer.pc.addTrack(it) }
                    peer.pc?.createOffer(peer, pcConstraints)
                }
            }
        }
    }

    fun onMessage(payload: String) {
        val data = Gson().fromJson(payload, JsonObject::class.java)
        val from = data.get("from").asInt
        val to = data.get("to").asInt
        when (val type = data.get("type").asString) {
            "offer" -> {
                val sdp = data.get("sdp").asString
                if (!peers.containsKey(from)) {
                    val endPoint = findEndPoint()
                    if (endPoint != MAX_PEER) {
                        val peer = addPeer(from, endPoint)
                        peer.pc?.addStream(localStream)
                        // localStream.audioTracks.forEach { peer.pc.addTrack(it) }
                        // localStream.videoTracks.forEach { peer.pc.addTrack(it) }
                        peer.pc?.setRemoteDescription(peer,
                            SessionDescription(SessionDescription.Type.fromCanonicalForm(type), sdp))
                        peer.pc?.createAnswer(peer, pcConstraints)
                    }
                }
            }
            "answer" -> {
                val sdp = data.get("sdp").asString
                if (peers.containsKey(from)) {
                    peers[from]?.let { peer ->
                        peer.pc?.let { pc ->
                            pc.addStream(localStream)
                            // localStream.audioTracks.forEach { pc.addTrack(it) }
                            // localStream.videoTracks.forEach { pc.addTrack(it) }
                            pc.setRemoteDescription(peer,
                                SessionDescription(SessionDescription.Type.fromCanonicalForm(type), sdp))
                        }
                    }
                }
            }
            "candidate" -> {
                if (peers.containsKey(from)) {
                    peers[from]?.let { peer ->
                        peer.pc?.let { pc ->
                            pc.addStream(localStream)
                            // localStream.audioTracks.forEach { pc.addTrack(it) }
                            // localStream.videoTracks.forEach { pc.addTrack(it) }
                            if (pc.remoteDescription != null) {
                                val candidate = IceCandidate(
                                    data.get("id").asString,
                                    data.get("label").asInt,
                                    data.get("candidate").asString
                                )
                                pc.addIceCandidate(candidate)
                            }
                        }
                    }
                }
            }
        }
    }

    fun switchCamera() {
        videoCapturer.switchCamera(null)
    }

    fun onDestroy() {
        videoCapturer.stopCapture()
        peers.forEach { peer -> peer.value.pc?.dispose() }
        localVideoSource.dispose()
        peerFactory.dispose()
    }

    companion object {
        const val MAX_PEER = 4
    }
}
