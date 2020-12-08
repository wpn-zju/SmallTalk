package edu.syr.smalltalk.service.android

import android.content.Context
import com.google.gson.Gson
import edu.syr.smalltalk.service.ISmallTalkService
import edu.syr.smalltalk.service.android.constant.ClientConstant
import edu.syr.smalltalk.service.model.logic.SmallTalkDao
import java.time.Instant

// Consider combine AWebSocketManager and ASmallTalkService in ONE class to reduce unnecessary dependency injection
class ASmallTalkService(context: Context) : ISmallTalkService {
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

    override fun userSignUp(userEmail: String, userPassword: String, passcode: String) {
        webSocketManager.send(
            ClientConstant.API_USER_SIGN_UP,
            Gson().toJson(UserSignUpMessage(
                userEmail,
                userPassword,
                passcode
            ))
        )
    }

    override fun userSignUpPasscodeRequest(userEmail: String) {
        webSocketManager.send(
            ClientConstant.API_USER_SIGN_UP_PASSCODE_REQUEST,
            Gson().toJson(UserSignUpPasscodeRequestMessage(
                userEmail,
            ))
        )
    }

    override fun userRecoverPassword(userEmail: String, userPassword: String, passcode: String) {
        webSocketManager.send(
            ClientConstant.API_USER_RECOVER_PASSWORD,
            Gson().toJson(UserRecoverPasswordMessage(
                userEmail,
                userPassword,
                passcode
            ))
        )
    }

    override fun userRecoverPasswordPasscodeRequest(userEmail: String) {
        webSocketManager.send(
            ClientConstant.API_USER_RECOVER_PASSWORD_PASSCODE_REQUEST,
            Gson().toJson(UserRecoverPasswordPasscodeRequestMessage(
                userEmail,
            ))
        )
    }

    override fun userSignIn(userEmail: String, userPassword: String) {
        webSocketManager.send(
            ClientConstant.API_USER_SIGN_IN,
            Gson().toJson(UserSignInMessage(
                userEmail,
                userPassword,
            ))
        )
    }

    override fun userSessionSignIn(sessionToken: String) {
        webSocketManager.send(
            ClientConstant.API_USER_SESSION_SIGN_IN,
            Gson().toJson(UserSessionSignInMessage(
                sessionToken
            ))
        )
    }

    override fun userSessionSignOut() {
        webSocketManager.send(
            ClientConstant.API_USER_SESSION_SIGN_OUT,
            Gson().toJson(UserSessionSignOutMessage(

            ))
        )
    }

    override fun userModifyName(newUserName: String) {
        webSocketManager.send(
            ClientConstant.API_USER_MODIFY_NAME,
            Gson().toJson(UserModifyNameMessage(
                newUserName
            ))
        )
    }

    override fun userModifyPassword(newUserPassword: String) {
        webSocketManager.send(
            ClientConstant.API_USER_MODIFY_PASSWORD,
            Gson().toJson(UserModifyPasswordMessage(
                newUserPassword
            ))
        )
    }

    override fun loadUser() {
        webSocketManager.send(
            ClientConstant.API_LOAD_USER,
            Gson().toJson(LoadUserMessage(

            ))
        )
    }

    override fun loadContact(contactId: Int) {
        webSocketManager.send(
            ClientConstant.API_LOAD_CONTACT,
            Gson().toJson(LoadContactMessage(
                contactId
            ))
        )
    }

    override fun loadContactByEmail(contactEmail: String) {
        webSocketManager.send(
            ClientConstant.API_LOAD_CONTACT_BY_EMAIL,
            Gson().toJson(LoadContactByEmailMessage(
                contactEmail
            ))
        )
    }

    override fun loadGroup(groupId: Int) {
        webSocketManager.send(
            ClientConstant.API_LOAD_GROUP,
            Gson().toJson(LoadGroupMessage(
                groupId
            ))
        )
    }

    override fun loadRequest(requestId: Int) {
        webSocketManager.send(
            ClientConstant.API_LOAD_REQUEST,
            Gson().toJson(LoadRequestMessage(
                requestId
            ))
        )
    }

    override fun messageForward(senderId: Int, receiverId: Int, content: String, contentType: String) {
        webSocketManager.send(
            ClientConstant.API_CHAT_MESSAGE_FORWARD,
            Gson().toJson(MessageForwardMessage(
                senderId,
                receiverId,
                content,
                contentType,
                Instant.now().toString()
            ))
        )
    }

    override fun messageForwardGroup(senderId: Int, receiverId: Int, content: String, contentType: String) {
        webSocketManager.send(
            ClientConstant.API_CHAT_MESSAGE_FORWARD_GROUP,
            Gson().toJson(MessageForwardGroupMessage(
                senderId,
                receiverId,
                content,
                contentType,
                Instant.now().toString()
            ))
        )
    }

    override fun contactAddRequest(contactEmail: String) {
        webSocketManager.send(
            ClientConstant.API_CHAT_CONTACT_ADD_REQUEST,
            Gson().toJson(ContactAddRequestMessage(
                contactEmail
            ))
        )
    }

    override fun contactAddConfirm(requestId: Int) {
        webSocketManager.send(
            ClientConstant.API_CHAT_CONTACT_ADD_CONFIRM,
            Gson().toJson(ContactAddConfirmMessage(
                requestId
            ))
        )
    }

    override fun contactAddRefuse(requestId: Int) {
        webSocketManager.send(
            ClientConstant.API_CHAT_CONTACT_ADD_REFUSE,
            Gson().toJson(ContactAddRefuseMessage(
                requestId
            ))
        )
    }

    override fun groupCreateRequest(groupName: String, memberList: String) {
        webSocketManager.send(
            ClientConstant.API_CHAT_GROUP_CREATE_REQUEST,
            Gson().toJson(GroupCreateRequestMessage(
                groupName,
                memberList
            ))
        )
    }

    override fun groupModifyName(groupId: Int, newGroupName: String) {
        webSocketManager.send(
            ClientConstant.API_CHAT_GROUP_MODIFY_NAME,
            Gson().toJson(GroupModifyNameMessage(
                groupId,
                newGroupName
            ))
        )
    }

    override fun groupInviteMember(groupId: Int, memberId: Int) {
        webSocketManager.send(
            ClientConstant.API_CHAT_GROUP_INVITE_MEMBER,
            Gson().toJson(GroupInviteMemberMessage(
                groupId,
                memberId
            ))
        )
    }

    override fun groupAddRequest(groupId: Int) {
        webSocketManager.send(
            ClientConstant.API_CHAT_GROUP_ADD_REQUEST,
            Gson().toJson(GroupAddRequestMessage(
                groupId
            ))
        )
    }

    override fun groupAddConfirm(requestId: Int) {
        webSocketManager.send(
            ClientConstant.API_CHAT_GROUP_ADD_CONFIRM,
            Gson().toJson(GroupAddConfirmMessage(
                requestId
            ))
        )
    }

    override fun groupAddRefuse(requestId: Int) {
        webSocketManager.send(
            ClientConstant.API_CHAT_GROUP_ADD_REFUSE,
            Gson().toJson(GroupAddRefuseMessage(
                requestId
            ))
        )
    }

    override fun webrtcCall(senderId: Int, receiverId: Int, webrtcCommand: String, webrtcSessionDescription: String) {
        webSocketManager.send(
            ClientConstant.API_CHAT_WEBRTC_CALL,
            Gson().toJson(WebRTCCallMessage(
                senderId,
                receiverId,
                webrtcCommand,
                webrtcSessionDescription
            ))
        )
    }
}
