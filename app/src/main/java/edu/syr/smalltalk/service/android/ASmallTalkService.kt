package edu.syr.smalltalk.service.android

import com.google.gson.Gson
import edu.syr.smalltalk.service.ISmallTalkService
import edu.syr.smalltalk.service.android.constant.ClientConstant
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import java.time.Instant

class ASmallTalkService(application: SmallTalkApplication) : ISmallTalkService {
    private val webSocketManager: AWebSocketManager = AWebSocketManager(application)

    override fun connect() {
        webSocketManager.connect()
    }

    override fun disconnect() {
        webSocketManager.disconnect()
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

    override fun userModifyInfo(
        userId: Int,
        userName: String?,
        userPassword: String?,
        userGender: Int?,
        userAvatarLink: String?,
        userInfo: String?,
        userLocation: String?
    ) {
        webSocketManager.send(
            ClientConstant.API_USER_MODIFY_INFO,
            Gson().toJson(UserModifyInfoMessage(
                userId,
                userName,
                userPassword,
                userGender,
                userAvatarLink,
                userInfo,
                userLocation
            ))
        )
    }

    override fun loadUser(userId: Int) {
        webSocketManager.send(
            ClientConstant.API_LOAD_USER,
            Gson().toJson(LoadUserMessage(
                userId
            ))
        )
        loadContact(userId)
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

    override fun loadFileList(firstSelector: Int, secondSelector: Int) {
        webSocketManager.send(
            ClientConstant.API_LOAD_FILE_LIST,
            Gson().toJson(LoadFileListMessage(
                firstSelector,
                secondSelector
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

    override fun contactAddRevoke(requestId: Int) {
        webSocketManager.send(
            ClientConstant.API_CHAT_CONTACT_ADD_REVOKE,
            Gson().toJson(ContactAddRevokeMessage(
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

    override fun groupModifyInfo(
        groupId: Int,
        groupName: String?,
        groupInfo: String?,
        groupAvatarLink: String?
    ) {
        webSocketManager.send(
            ClientConstant.API_GROUP_MODIFY_INFO,
            Gson().toJson(GroupModifyInfoMessage(
                groupId,
                groupName,
                groupInfo,
                groupAvatarLink
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

    override fun groupAddRevoke(requestId: Int) {
        webSocketManager.send(
            ClientConstant.API_CHAT_GROUP_ADD_REVOKE,
            Gson().toJson(GroupAddRevokeMessage(
                requestId
            ))
        )
    }

    override fun webRTCCall(channel: String, command: String, data: String) {
        webSocketManager.send(
            ClientConstant.API_CHAT_WEBRTC_CALL,
            Gson().toJson(WebRTCCallMessage(
                channel,
                command,
                data
            ))
        )
    }

    override fun fileArchive(
        firstSelector: Int,
        secondSelector: Int,
        fileName: String,
        fileLink: String,
        fileUploader: Int,
        fileSize: Int
    ) {
        webSocketManager.send(
            ClientConstant.API_FILE_ARCHIVE,
            Gson().toJson(FileArchiveMessage(
                firstSelector,
                secondSelector,
                fileName,
                fileLink,
                fileUploader,
                fileSize
            ))
        )
    }
}
