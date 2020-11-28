package edu.syr.smalltalk.service.android

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import edu.syr.smalltalk.service.ISmallTalkService
import edu.syr.smalltalk.service.android.constant.ClientConstant
import edu.syr.smalltalk.service.model.logic.SmallTalkDao
import java.time.Instant

// Consider combine AWebSocketManager and ASmallTalkService in ONE class to reduce unnecessary dependency injection
class ASmallTalkService(private val context: Context) : ISmallTalkService {
    private val webSocketManager: AWebSocketManager = AWebSocketManager(context)

    override fun connect() {
        webSocketManager.connect()
    }

    override fun disconnect() {
        webSocketManager.disconnect()
    }

    override fun setDataAccessor(smallTalkDao: SmallTalkDao) {
        webSocketManager.setDataAccessor(smallTalkDao)
    }

    override fun testSend(payload: Int) {
        webSocketManager.send(
            ClientConstant.API_TEST_SEND,
            "{\"" + ClientConstant.API_TEST_SEND_PAYLOAD + "\":\"$payload\"" + "}")
    }

    override fun userSignUp(userEmail: String, userPassword: String, passcode: String) {
        webSocketManager.send(
            ClientConstant.API_USER_SIGN_UP,
            Gson().toJson(object {
                @SerializedName(ClientConstant.USER_SIGN_UP_USER_EMAIL)
                val user_email: String = userEmail
                @SerializedName(ClientConstant.USER_SIGN_UP_USER_PASSWORD)
                val user_password: String = userPassword
                @SerializedName(ClientConstant.USER_SIGN_UP_PASSCODE)
                val passcode: String = passcode
            })
        )
    }

    override fun userSignUpPasscodeRequest(userEmail: String) {
        webSocketManager.send(
            ClientConstant.API_USER_SIGN_UP_PASSCODE_REQUEST,
            Gson().toJson(object {
                @SerializedName(ClientConstant.USER_SIGN_UP_PASSCODE_REQUEST_USER_EMAIL)
                val user_email: String = userEmail
            })
        )
    }

    override fun userRecoverPassword(userEmail: String, userPassword: String, passcode: String) {
        webSocketManager.send(
            ClientConstant.API_USER_RECOVER_PASSWORD,
            Gson().toJson(object {
                @SerializedName(ClientConstant.USER_RECOVER_PASSWORD_USER_EMAIL)
                val user_email: String = userEmail
                @SerializedName(ClientConstant.USER_RECOVER_PASSWORD_USER_PASSWORD)
                val user_password: String = userPassword
                @SerializedName(ClientConstant.USER_RECOVER_PASSWORD_PASSCODE)
                val passcode: String = passcode
            })
        )
    }

    override fun userRecoverPasswordPasscodeRequest(userEmail: String) {
        webSocketManager.send(
            ClientConstant.API_USER_RECOVER_PASSWORD_PASSCODE_REQUEST,
            Gson().toJson(object {
                @SerializedName(ClientConstant.USER_RECOVER_PASSWORD_PASSCODE_REQUEST_USER_EMAIL)
                val user_email: String = userEmail
            })
        )
    }

    override fun userSignIn(userEmail: String, userPassword: String) {
        webSocketManager.send(
            ClientConstant.API_USER_SIGN_IN,
            Gson().toJson(object {
                @SerializedName(ClientConstant.USER_SIGN_IN_USER_EMAIL)
                val user_email: String = userEmail
                @SerializedName(ClientConstant.USER_SIGN_IN_USER_PASSWORD)
                val user_password: String = userPassword
            })
        )
    }

    override fun userSessionSignIn(sessionToken: String) {
        webSocketManager.send(
            ClientConstant.API_USER_SESSION_SIGN_IN,
            Gson().toJson(object {
                @SerializedName(ClientConstant.USER_SESSION_SIGN_IN_SESSION_TOKEN)
                val session_token: String = sessionToken
            })
        )
    }

    override fun userSessionSignOut() {
        webSocketManager.send(
            ClientConstant.API_USER_SESSION_SIGN_OUT,
            Gson().toJson(object {

            })
        )
    }

    override fun userModifyName(newUserName: String) {
        webSocketManager.send(
            ClientConstant.API_USER_MODIFY_NAME,
            Gson().toJson(object {
                @SerializedName(ClientConstant.USER_MODIFY_USER_NAME_NEW_USER_NAME)
                val new_user_name: String = newUserName
            })
        )
    }

    override fun userModifyPassword(newUserPassword: String) {
        webSocketManager.send(
            ClientConstant.API_USER_MODIFY_PASSWORD,
            Gson().toJson(object {
                @SerializedName(ClientConstant.USER_MODIFY_USER_PASSWORD_NEW_USER_PASSWORD)
                val new_user_password: String = newUserPassword
            })
        )
    }

    override fun loadUser() {
        webSocketManager.send(
            ClientConstant.API_LOAD_USER,
            Gson().toJson(object {

            })
        )
    }

    override fun loadContact(contactId: Int) {
        webSocketManager.send(
            ClientConstant.API_LOAD_CONTACT,
            Gson().toJson(object {
                @SerializedName(ClientConstant.LOAD_CONTACT_CONTACT_ID)
                val contact_id: Int = contactId
            })
        )
    }

    override fun loadGroup(groupId: Int) {
        webSocketManager.send(
            ClientConstant.API_LOAD_GROUP,
            Gson().toJson(object {
                @SerializedName(ClientConstant.LOAD_GROUP_GROUP_ID)
                val group_id: Int = groupId
            })
        )
    }

    override fun loadRequest(requestId: Int) {
        webSocketManager.send(
            ClientConstant.API_LOAD_REQUEST,
            Gson().toJson(object {
                @SerializedName(ClientConstant.LOAD_REQUEST_REQUEST_ID)
                val request_id: Int = requestId
            })
        )
    }

    override fun messageForward(senderId: Int, receiverId: Int, content: String, contentType: String) {
        webSocketManager.send(
            ClientConstant.API_CHAT_MESSAGE_FORWARD,
            Gson().toJson(object {
                @SerializedName(ClientConstant.CHAT_MESSAGE_FORWARD_SENDER)
                val sender_id: Int = senderId
                @SerializedName(ClientConstant.CHAT_MESSAGE_FORWARD_RECEIVER)
                val receiver_id: Int = receiverId
                @SerializedName(ClientConstant.CHAT_MESSAGE_FORWARD_CONTENT)
                val content: String = content
                @SerializedName(ClientConstant.CHAT_MESSAGE_FORWARD_CONTENT_TYPE)
                val content_type: String = contentType
                @SerializedName(ClientConstant.TIMESTAMP)
                val timestamp: String = Instant.now().toString()
            })
        )
    }

    override fun messageForwardGroup(senderId: Int, receiverId: Int, content: String, contentType: String) {
        webSocketManager.send(
            ClientConstant.API_CHAT_MESSAGE_FORWARD_GROUP,
            Gson().toJson(object {
                @SerializedName(ClientConstant.CHAT_MESSAGE_FORWARD_SENDER)
                val sender_id: Int = senderId
                @SerializedName(ClientConstant.CHAT_MESSAGE_FORWARD_RECEIVER)
                val receiver_id: Int = receiverId
                @SerializedName(ClientConstant.CHAT_MESSAGE_FORWARD_CONTENT)
                val content: String = content
                @SerializedName(ClientConstant.CHAT_MESSAGE_FORWARD_CONTENT_TYPE)
                val content_type: String = contentType
                @SerializedName(ClientConstant.TIMESTAMP)
                val timestamp: String = Instant.now().toString()
            })
        )
    }

    override fun contactAddRequest(contactEmail: String) {
        webSocketManager.send(
            ClientConstant.API_CHAT_CONTACT_ADD_REQUEST,
            Gson().toJson(object {
                @SerializedName(ClientConstant.CHAT_CONTACT_ADD_REQUEST_CONTACT_EMAIL)
                val contact_email: String = contactEmail
            })
        )
    }

    override fun contactAddConfirm(requestId: Int) {
        webSocketManager.send(
            ClientConstant.API_CHAT_CONTACT_ADD_CONFIRM,
            Gson().toJson(object {
                @SerializedName(ClientConstant.CHAT_CONTACT_ADD_CONFIRM_REQUEST_ID)
                val request_id: Int = requestId
            })
        )
    }

    override fun contactAddRefuse(requestId: Int) {
        webSocketManager.send(
            ClientConstant.API_CHAT_CONTACT_ADD_REFUSE,
            Gson().toJson(object {
                @SerializedName(ClientConstant.CHAT_CONTACT_ADD_REFUSE_REQUEST_ID)
                val request_id: Int = requestId
            })
        )
    }

    override fun groupCreateRequest(groupName: String) {
        webSocketManager.send(
            ClientConstant.API_CHAT_GROUP_CREATE_REQUEST,
            Gson().toJson(object {
                @SerializedName(ClientConstant.CHAT_GROUP_CREATE_REQUEST_GROUP_NAME)
                val group_name: String = groupName
            })
        )
    }

    override fun groupModifyName(groupId: Int, newGroupName: String) {
        webSocketManager.send(
            ClientConstant.API_CHAT_GROUP_MODIFY_NAME,
            Gson().toJson(object {
                @SerializedName(ClientConstant.CHAT_GROUP_MODIFY_NAME_GROUP_ID)
                val group_id: Int = groupId
                @SerializedName(ClientConstant.CHAT_GROUP_MODIFY_NAME_NEW_GROUP_NAME)
                val new_group_name: String = newGroupName
            })
        )
    }

    override fun groupAddRequest(groupId: Int) {
        webSocketManager.send(
            ClientConstant.API_CHAT_GROUP_ADD_REQUEST,
            Gson().toJson(object {
                @SerializedName(ClientConstant.CHAT_GROUP_ADD_REQUEST_GROUP_ID)
                val group_id: Int = groupId
            })
        )
    }

    override fun groupAddConfirm(requestId: Int) {
        webSocketManager.send(
            ClientConstant.API_CHAT_GROUP_ADD_CONFIRM,
            Gson().toJson(object {
                @SerializedName(ClientConstant.CHAT_GROUP_ADD_CONFIRM_REQUEST_ID)
                val request_id: Int = requestId
            })
        )
    }

    override fun groupAddRefuse(requestId: Int) {
        webSocketManager.send(
            ClientConstant.API_CHAT_GROUP_ADD_REFUSE,
            Gson().toJson(object {
                @SerializedName(ClientConstant.CHAT_GROUP_ADD_REFUSE_REQUEST_ID)
                val request_id: Int = requestId
            })
        )
    }

    override fun webrtcCall(senderId: Int, receiverId: Int, webrtcCommand: String, webrtcSessionDescription: String) {
        webSocketManager.send(
            ClientConstant.API_CHAT_WEBRTC_CALL,
            Gson().toJson(object {
                @SerializedName(ClientConstant.CHAT_WEBRTC_CALL_SENDER)
                val sender_id: Int = senderId
                @SerializedName(ClientConstant.CHAT_WEBRTC_CALL_RECEIVER)
                val receiver_id: Int = receiverId
                @SerializedName(ClientConstant.CHAT_WEBRTC_CALL_WEBRTC_COMMAND)
                val webrtc_command: String = webrtcCommand
                @SerializedName(ClientConstant.CHAT_WEBRTC_CALL_WEBRTC_SESSION_DESCRIPTION)
                val webrtc_session_desc: String = webrtcSessionDescription
            })
        )
    }
}