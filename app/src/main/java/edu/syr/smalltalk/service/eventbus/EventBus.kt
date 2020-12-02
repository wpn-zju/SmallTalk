package edu.syr.smalltalk.service.eventbus

import com.google.gson.annotations.SerializedName
import edu.syr.smalltalk.service.android.constant.ClientConstant
import java.io.Serializable

class SignInEvent

class SignOutEvent

class SessionExpiredEvent

class WebRTCEvent (
    @SerializedName(ClientConstant.CHAT_WEBRTC_CALL_SENDER)
    val sender: Int,
    @SerializedName(ClientConstant.CHAT_WEBRTC_CALL_RECEIVER)
    val receiver: Int,
    @SerializedName(ClientConstant.CHAT_WEBRTC_CALL_WEBRTC_COMMAND)
    val webRTCCommand: String,
    @SerializedName(ClientConstant.CHAT_WEBRTC_CALL_WEBRTC_SESSION_DESCRIPTION)
    val webRTCSessionDescription: String
) : Serializable

class ToastEvent (val message: String)

class AlertDialogEvent (val title: String, val message: String)

class SearchContactSuccessEvent (val contactId: Int)

class SearchGroupSuccessEvent (val groupId: Int)

class CreateGroupSuccessEvent (val groupId: Int)
