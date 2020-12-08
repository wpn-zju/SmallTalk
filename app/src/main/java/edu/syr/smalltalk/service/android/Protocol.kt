package edu.syr.smalltalk.service.android

import com.google.gson.annotations.SerializedName
import edu.syr.smalltalk.service.android.constant.ClientConstant

class ContactAddConfirmMessage (
    @SerializedName(ClientConstant.CHAT_CONTACT_ADD_CONFIRM_REQUEST_ID)
    private val requestId: Int
)

class ContactAddRefuseMessage (
    @SerializedName(ClientConstant.CHAT_CONTACT_ADD_REFUSE_REQUEST_ID)
    private val requestId: Int
)

class ContactAddRequestMessage (
    @SerializedName(ClientConstant.CHAT_CONTACT_ADD_REQUEST_CONTACT_EMAIL)
    private val contactEmail: String
)

class GroupAddConfirmMessage (
    @SerializedName(ClientConstant.CHAT_GROUP_ADD_CONFIRM_REQUEST_ID)
    private val requestId: Int
)

class GroupAddRefuseMessage (
    @SerializedName(ClientConstant.CHAT_GROUP_ADD_REFUSE_REQUEST_ID)
    private val requestId: Int
)

class GroupAddRequestMessage (
    @SerializedName(ClientConstant.CHAT_GROUP_ADD_REQUEST_GROUP_ID)
    private val groupId: Int
)

class GroupCreateRequestMessage (
    @SerializedName(ClientConstant.CHAT_GROUP_CREATE_REQUEST_GROUP_NAME)
    private val groupName: String,
    @SerializedName(ClientConstant.CHAT_GROUP_CREATE_REQUEST_MEMBER_LIST)
    private val memberList: String
)

class GroupModifyNameMessage (
    @SerializedName(ClientConstant.CHAT_GROUP_MODIFY_NAME_GROUP_ID)
    private val groupId: Int,
    @SerializedName(ClientConstant.CHAT_GROUP_MODIFY_NAME_NEW_GROUP_NAME)
    private val newGroupName: String
)

class GroupInviteMemberMessage (
    @SerializedName(ClientConstant.CHAT_GROUP_INVITE_MEMBER_GROUP_ID)
    private val groupId: Int,
    @SerializedName(ClientConstant.CHAT_GROUP_INVITE_MEMBER_MEMBER_ID)
    private val memberId: Int
)

class LoadContactMessage (
    @SerializedName(ClientConstant.LOAD_CONTACT_CONTACT_ID)
    private val contactId: Int
)

class LoadContactByEmailMessage (
    @SerializedName(ClientConstant.LOAD_CONTACT_BY_EMAIL_CONTACT_EMAIL)
    private val contactEmail: String
)

class LoadGroupMessage (
    @SerializedName(ClientConstant.LOAD_GROUP_GROUP_ID)
    private val groupId: Int
)

class LoadRequestMessage (
    @SerializedName(ClientConstant.LOAD_REQUEST_REQUEST_ID)
    private val requestId: Int
)

class LoadUserMessage

class MessageForwardGroupMessage (
    @SerializedName(ClientConstant.CHAT_MESSAGE_FORWARD_GROUP_SENDER)
    private val sender: Int,
    @SerializedName(ClientConstant.CHAT_MESSAGE_FORWARD_GROUP_RECEIVER)
    private val receiver: Int,
    @SerializedName(ClientConstant.CHAT_MESSAGE_FORWARD_GROUP_CONTENT)
    private val content: String,
    @SerializedName(ClientConstant.CHAT_MESSAGE_FORWARD_GROUP_CONTENT_TYPE)
    private val contentType: String,
    @SerializedName(ClientConstant.TIMESTAMP)
    private val timestamp: String
)

class MessageForwardMessage (
    @SerializedName(ClientConstant.CHAT_MESSAGE_FORWARD_SENDER)
    private val sender: Int,
    @SerializedName(ClientConstant.CHAT_MESSAGE_FORWARD_RECEIVER)
    private val receiver: Int,
    @SerializedName(ClientConstant.CHAT_MESSAGE_FORWARD_CONTENT)
    private val content: String,
    @SerializedName(ClientConstant.CHAT_MESSAGE_FORWARD_CONTENT_TYPE)
    private val contentType: String,
    @SerializedName(ClientConstant.TIMESTAMP)
    private val timestamp: String
)

class UserModifyNameMessage (
    @SerializedName(ClientConstant.USER_MODIFY_USER_NAME_NEW_USER_NAME)
    private val newUserName: String
)

class UserModifyPasswordMessage (
    @SerializedName(ClientConstant.USER_MODIFY_USER_PASSWORD_NEW_USER_PASSWORD)
    private val newUserPassword: String
)

class UserRecoverPasswordMessage (
    @SerializedName(ClientConstant.USER_RECOVER_PASSWORD_USER_EMAIL)
    private val userEmail: String,
    @SerializedName(ClientConstant.USER_RECOVER_PASSWORD_USER_PASSWORD)
    private val userPassword: String,
    @SerializedName(ClientConstant.USER_RECOVER_PASSWORD_PASSCODE)
    private val passcode: String? = null
)

class UserRecoverPasswordPasscodeRequestMessage (
    @SerializedName(ClientConstant.USER_RECOVER_PASSWORD_PASSCODE_REQUEST_USER_EMAIL)
    private val userEmail: String
)

class UserSessionSignInMessage (
    @SerializedName(ClientConstant.USER_SESSION_SIGN_IN_SESSION_TOKEN)
    private val sessionToken: String
)

class UserSessionSignOutMessage

class UserSignInMessage (
    @SerializedName(ClientConstant.USER_SIGN_IN_USER_EMAIL)
    private val userEmail: String,
    @SerializedName(ClientConstant.USER_SIGN_IN_USER_PASSWORD)
    private val userPassword: String
)

class UserSignUpMessage (
    @SerializedName(ClientConstant.USER_SIGN_UP_USER_EMAIL)
    private val userEmail: String,
    @SerializedName(ClientConstant.USER_SIGN_UP_USER_PASSWORD)
    private val userPassword: String,
    @SerializedName(ClientConstant.USER_SIGN_UP_PASSCODE)
    private val passcode: String
)

class UserSignUpPasscodeRequestMessage (
    @SerializedName(ClientConstant.USER_SIGN_UP_PASSCODE_REQUEST_USER_EMAIL)
    private val userEmail: String
)

class WebRTCCallMessage (
    @SerializedName(ClientConstant.CHAT_WEBRTC_CALL_SENDER)
    private val sender: Int,
    @SerializedName(ClientConstant.CHAT_WEBRTC_CALL_RECEIVER)
    private val receiver: Int,
    @SerializedName(ClientConstant.CHAT_WEBRTC_CALL_WEBRTC_COMMAND)
    private val webRTCCommand: String,
    @SerializedName(ClientConstant.CHAT_WEBRTC_CALL_WEBRTC_SESSION_DESCRIPTION)
    private val webRTCSessionDescription: String
)
