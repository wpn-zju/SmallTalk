package edu.syr.smalltalk.service.android.constant

object ServerConstant {
    const val PSC_TYPE = "passcode_type"
    const val PSC_TYPE_USER_SIGN_UP = "sign_up"
    const val PSC_TYPE_USER_RECOVER_PASSWORD = "recover_password"
    const val PSC_USER_EMAIL = "user_email"

    const val DIR_INVALID_PASSCODE = "/queue/port/invalid_passcode"
    const val DIR_INVALID_SESSION = "/queue/port/invalid_session"
    const val DIR_INVALID_USER_NAME = "/queue/port/invalid_user_name"
    const val DIR_INVALID_USER_EMAIL = "/queue/port/invalid_user_email"
    const val DIR_INVALID_USER_PASSWORD = "/queue/port/invalid_user_password"
    const val DIR_INVALID_GROUP_NAME = "/queue/port/invalid_group_name"

    const val DIR_USER_SIGN_UP_SUCCESS = "/queue/port/user_sign_up_success"
    const val DIR_USER_SIGN_UP_FAILED_EMAIL_EXISTS = "/queue/port/user_sign_up_failed_email_exists"
    const val DIR_USER_SIGN_UP_FAILED_PASSCODE_INCORRECT =
        "/queue/port/user_sign_up_failed_passcode_incorrect"
    const val DIR_USER_SIGN_UP_PASSCODE_REQUEST_SUCCESS = "/queue/port/user_sync"
    const val DIR_USER_SIGN_UP_PASSCODE_REQUEST_FAILED_SERVER_ERROR =
        "/queue/port/user_sign_up_passcode_request_failed_server_error"
    const val DIR_USER_SIGN_UP_PASSCODE_REQUEST_FAILED_EMAIL_EXISTS =
        "/queue/port/user_sign_up_passcode_request_failed_email_exists"
    const val DIR_USER_RECOVER_PASSWORD_SUCCESS = "/queue/port/user_recover_password_success"
    const val DIR_USER_RECOVER_PASSWORD_FAILED_USER_NOT_FOUND =
        "/queue/port/user_recover_password_failed_user_not_found"
    const val DIR_USER_RECOVER_PASSWORD_FAILED_PASSCODE_INCORRECT =
        "/queue/port/user_recover_password_failed_passcode_incorrect"
    const val DIR_USER_RECOVER_PASSWORD_PASSCODE_REQUEST_SUCCESS = "/queue/port/user_sync"
    const val DIR_USER_RECOVER_PASSWORD_PASSCODE_REQUEST_FAILED_SERVER_ERROR =
        "/queue/port/user_recover_password_passcode_request_failed_server_error"
    const val DIR_USER_RECOVER_PASSWORD_PASSCODE_REQUEST_FAILED_USER_NOT_FOUND =
        "/queue/port/user_recover_password_passcode_request_failed_user_not_found"
    const val DIR_USER_SIGN_IN_SUCCESS = "/queue/port/user_sign_in_success"
    const val DIR_USER_SIGN_IN_FAILED_USER_NOT_FOUND =
        "/queue/port/user_sign_in_failed_user_not_found"
    const val DIR_USER_SIGN_IN_FAILED_PASSWORD_INCORRECT =
        "/queue/port/user_sign_in_failed_password_incorrect"
    const val DIR_USER_SIGN_OUT_SUCCESS = "/queue/port/user_sign_out_success"
    const val DIR_USER_SESSION_INVALID = "/queue/port/user_session_invalid"
    const val DIR_USER_SESSION_EXPIRED = "/queue/port/user_session_expired"
    const val DIR_USER_SESSION_REVOKED = "/queue/port/user_session_revoked"
    const val DIR_USER_MODIFY_NAME_SUCCESS = "/queue/port/user_modify_name_success"
    const val DIR_USER_MODIFY_PASSWORD_SUCCESS = "/queue/port/user_modify_password_success"
    const val DIR_USER_SYNC = "/queue/port/user_sync"
    const val DIR_CONTACT_SYNC = "/queue/port/contact_sync"
    const val DIR_GROUP_SYNC = "/queue/port/group_sync"
    const val DIR_REQUEST_SYNC = "/queue/port/request_sync"
    const val DIR_NEW_MESSAGE = "/queue/port/new_message"
    const val DIR_CONTACT_ADD_REQUEST_SUCCESS = "/queue/port/contact_add_request_success"
    const val DIR_CONTACT_ADD_REQUEST_FAILED_ALREADY_CONTACT =
        "/queue/port/contact_add_request_failed_already_contact"
    const val DIR_CONTACT_ADD_REQUEST_FAILED_USER_NOT_FOUND =
        "/queue/port/contact_add_failed_user_not_found"
    const val DIR_GROUP_CREATE_REQUEST_SUCCESS = "/queue/port/group_create_request_success"
    const val DIR_GROUP_MODIFY_NAME_SUCCESS = "/queue/port/group_modify_name_success"
    const val DIR_GROUP_MODIFY_NAME_FAILED_GROUP_NOT_FOUND =
        "/queue/port/group_modify_name_failed_group_not_found"
    const val DIR_GROUP_MODIFY_NAME_FAILED_PERMISSION_DENIED =
        "/queue/port/group_modify_name_failed_permission_denied"
    const val DIR_GROUP_ADD_REQUEST_SUCCESS = "/queue/port/group_add_request_success"
    const val DIR_GROUP_ADD_REQUEST_FAILED_ALREADY_MEMBER =
        "/queue/port/group_add_request_failed_already_member"
    const val DIR_GROUP_ADD_REQUEST_FAILED_GROUP_NOT_FOUND =
        "/queue/port/group_add_request_failed_group_not_found"
    const val DIR_WEBRTC_CALL = "/queue/port/webrtc_call"
    const val DIR_REQUEST_NOT_FOUND = "/queue/port/request_not_found"

    const val TIMESTAMP = "timestamp"
    const val ACC_USER_SYNC__USER_ID = "user_id"
    const val ACC_USER_SYNC__USER_SESSION = "user_session"
    const val ACC_USER_SYNC__USER_EMAIL = "user_email"
    const val ACC_USER_SYNC__USER_NAME = "user_name"
    const val ACC_USER_SYNC__USER_PASSWORD = "user_password"
    const val ACC_USER_SYNC__CONTACT_LIST = "contact_list"
    const val ACC_USER_SYNC__GROUP_LIST = "group_list"
    const val ACC_USER_SYNC__REQUEST_LIST = "request_list"

    const val ACC_CONTACT_SYNC__CONTACT_ID = "contact_id"
    const val ACC_CONTACT_SYNC__CONTACT_NAME = "contact_name"
    const val ACC_CONTACT_SYNC__CONTACT_EMAIL = "contact_email"

    const val ACC_GROUP_SYNC__GROUP_ID = "group_id"
    const val ACC_GROUP_SYNC__GROUP_NAME = "group_name"
    const val ACC_GROUP_SYNC__GROUP_HOST = "group_host"
    const val ACC_GROUP_SYNC__GROUP_MEMBER_LIST = "group_member_list"

    const val ACC_REQUEST_SYNC__REQUEST_ID = "request_id"
    const val ACC_REQUEST_SYNC__REQUEST_STATUS = "request_status"
    const val ACC_REQUEST_SYNC__REQUEST_TYPE = "request_type"
    const val ACC_REQUEST_SYNC__REQUEST_METADATA = "request_metadata"

    const val CHAT_NEW_MESSAGE__SENDER = "sender"
    const val CHAT_NEW_MESSAGE__RECEIVER = "receiver"
    const val CHAT_NEW_MESSAGE__CONTENT = "content"
    const val CHAT_NEW_MESSAGE__CONTENT_TYPE = "content_type"
    const val CHAT_WEBRTC_CALL__SENDER = "sender"
    const val CHAT_WEBRTC_CALL__RECEIVER = "receiver"
    const val CHAT_WEBRTC_CALL__WEBRTC_COMMAND = "webrtc_command"
    const val CHAT_WEBRTC_CALL__WEBRTC_SESSION_DESCRIPTION = "webrtc_session_description"
}