package edu.syr.smalltalk.service.eventbus

import com.google.gson.annotations.SerializedName
import edu.syr.smalltalk.service.android.constant.ClientConstant
import java.io.Serializable

class SignInEvent

class SignOutEvent

class SessionExpiredEvent

class WebRTCEvent (
    @SerializedName(ClientConstant.CHAT_WEBRTC_CALL_CHANNEL)
    val channel: String,
    @SerializedName(ClientConstant.CHAT_WEBRTC_CALL_COMMAND)
    val command: String,
    @SerializedName(ClientConstant.CHAT_WEBRTC_CALL_PAYLOAD)
    val payload: String
) : Serializable

class ToastEvent (val message: String)

class AlertDialogEvent (val title: String, val message: String)

class SearchContactSuccessEvent (val contactId: Int)

class SearchGroupSuccessEvent (val groupId: Int)
