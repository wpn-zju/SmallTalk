package edu.syr.smalltalk.service.android

import com.google.gson.annotations.SerializedName
import edu.syr.smalltalk.service.android.constant.ClientConstant

class ContactAddRequestMessage (
    @SerializedName(ClientConstant.CHAT_CONTACT_ADD_REQUEST_CONTACT_EMAIL)
    private val contactEmail: String
)

class ContactAddRevokeMessage (
    @SerializedName(ClientConstant.CHAT_CONTACT_ADD_REVOKE_REQUEST_ID)
    private val requestId: Int
)

class ContactAddConfirmMessage (
    @SerializedName(ClientConstant.CHAT_CONTACT_ADD_CONFIRM_REQUEST_ID)
    private val requestId: Int
)

class ContactAddRefuseMessage (
    @SerializedName(ClientConstant.CHAT_CONTACT_ADD_REFUSE_REQUEST_ID)
    private val requestId: Int
)

class GroupCreateRequestMessage (
    @SerializedName(ClientConstant.CHAT_GROUP_CREATE_REQUEST_GROUP_NAME)
    private val groupName: String,
    @SerializedName(ClientConstant.CHAT_GROUP_CREATE_REQUEST_MEMBER_LIST)
    private val memberList: String
)

class GroupAddRequestMessage (
    @SerializedName(ClientConstant.CHAT_GROUP_ADD_REQUEST_GROUP_ID)
    private val groupId: Int
)

class GroupAddRevokeMessage (
    @SerializedName(ClientConstant.CHAT_GROUP_ADD_REVOKE_REQUEST_ID)
    private val requestId: Int
)

class GroupAddConfirmMessage (
    @SerializedName(ClientConstant.CHAT_GROUP_ADD_CONFIRM_REQUEST_ID)
    private val requestId: Int
)

class GroupAddRefuseMessage (
    @SerializedName(ClientConstant.CHAT_GROUP_ADD_REFUSE_REQUEST_ID)
    private val requestId: Int
)

class GroupInviteMemberMessage (
    @SerializedName(ClientConstant.CHAT_GROUP_INVITE_MEMBER_GROUP_ID)
    private val groupId: Int,
    @SerializedName(ClientConstant.CHAT_GROUP_INVITE_MEMBER_MEMBER_ID)
    private val memberId: Int
)

class LoadUserMessage (
    @SerializedName(ClientConstant.LOAD_USER_USER_ID)
    private val userId: Int
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

class LoadFileListMessage (
    @SerializedName(ClientConstant.LOAD_FILE_LIST_FIRST_SELECTOR)
    private val firstSelector: Int,
    @SerializedName(ClientConstant.LOAD_FILE_LIST_SECOND_SELECTOR)
    private val secondSelector: Int
)

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

class UserModifyInfoMessage (
    @SerializedName(ClientConstant.USER_MODIFY_INFO_USER_ID)
    private val userId: Int,
    @SerializedName(ClientConstant.USER_MODIFY_INFO_USER_NAME)
    private val userName: String?,
    @SerializedName(ClientConstant.USER_MODIFY_INFO_USER_PASSWORD)
    private val userPassword: String?,
    @SerializedName(ClientConstant.USER_MODIFY_INFO_USER_GENDER)
    private val userGender: Int?,
    @SerializedName(ClientConstant.USER_MODIFY_INFO_USER_AVATAR_LINK)
    private val userAvatarLink: String?,
    @SerializedName(ClientConstant.USER_MODIFY_INFO_USER_INFO)
    private val userInfo: String?,
    @SerializedName(ClientConstant.USER_MODIFY_INFO_USER_LOCATION)
    private val userLocation: String?
)

class GroupModifyInfoMessage (
    @SerializedName(ClientConstant.GROUP_MODIFY_INFO_GROUP_ID)
    private val groupId: Int,
    @SerializedName(ClientConstant.GROUP_MODIFY_INFO_GROUP_NAME)
    private val groupName: String?,
    @SerializedName(ClientConstant.GROUP_MODIFY_INFO_GROUP_INFO)
    private val groupInfo:String?,
    @SerializedName(ClientConstant.GROUP_MODIFY_INFO_GROUP_AVATAR_LINK)
    private val groupAvatarLink: String?
)

class UserSignInMessage (
    @SerializedName(ClientConstant.USER_SIGN_IN_USER_EMAIL)
    private val userEmail: String,
    @SerializedName(ClientConstant.USER_SIGN_IN_USER_PASSWORD)
    private val userPassword: String
)

class UserSessionSignInMessage (
    @SerializedName(ClientConstant.USER_SESSION_SIGN_IN_SESSION_TOKEN)
    private val sessionToken: String
)

class UserSessionSignOutMessage

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

class WebRTCCallMessage (
    @SerializedName(ClientConstant.CHAT_WEBRTC_CALL_CHANNEL)
    private val channel: String,
    @SerializedName(ClientConstant.CHAT_WEBRTC_CALL_COMMAND)
    private val command: String,
    @SerializedName(ClientConstant.CHAT_WEBRTC_CALL_PAYLOAD)
    private val payload: String
)

class FileArchiveMessage (
    @SerializedName(ClientConstant.FILE_ARCHIVE_FIRST_SELECTOR)
    private val firstSelector: Int,
    @SerializedName(ClientConstant.FILE_ARCHIVE_SECOND_SELECTOR)
    private val secondSelector: Int,
    @SerializedName(ClientConstant.FILE_ARCHIVE_FILE_NAME)
    private val fileName: String,
    @SerializedName(ClientConstant.FILE_ARCHIVE_FILE_LINK)
    private val fileLink: String,
    @SerializedName(ClientConstant.FILE_ARCHIVE_FILE_UPLOADER)
    private val fileUploader: Int,
    @SerializedName(ClientConstant.FILE_ARCHIVE_FILE_SIZE)
    private val fileSize: Int,
)
