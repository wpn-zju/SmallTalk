package edu.syr.smalltalk.service.android.constant

object ServerConstant {
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
    const val DIR_USER_SIGN_UP_PASSCODE_REQUEST_SUCCESS =
        "/queue/port/user_sign_up_passcode_request_success"
    const val DIR_USER_SIGN_UP_PASSCODE_REQUEST_FAILED_SERVER_ERROR =
        "/queue/port/user_sign_up_passcode_request_failed_server_error"
    const val DIR_USER_SIGN_UP_PASSCODE_REQUEST_FAILED_EMAIL_EXISTS =
        "/queue/port/user_sign_up_passcode_request_failed_email_exists"
    const val DIR_USER_RECOVER_PASSWORD_SUCCESS = "/queue/port/user_recover_password_success"
    const val DIR_USER_RECOVER_PASSWORD_FAILED_USER_NOT_FOUND =
        "/queue/port/user_recover_password_failed_user_not_found"
    const val DIR_USER_RECOVER_PASSWORD_FAILED_PASSCODE_INCORRECT =
        "/queue/port/user_recover_password_failed_passcode_incorrect"
    const val DIR_USER_RECOVER_PASSWORD_PASSCODE_REQUEST_SUCCESS =
        "/queue/port/user_recover_passcode_passcode_request_success"
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
    const val DIR_USER_SYNC = "/queue/port/user_sync"
    const val DIR_CONTACT_SYNC = "/queue/port/contact_sync"
    const val DIR_CONTACT_SYNC_FAILED_USER_NOT_FOUND =
        "/queue/port/contact_sync_failed_user_not_found"
    const val DIR_GROUP_SYNC = "/queue/port/group_sync"
    const val DIR_GROUP_SYNC_FAILED_GROUP_NOT_FOUND =
        "/queue/port/group_sync_failed_group_not_found"
    const val DIR_REQUEST_SYNC = "/queue/port/request_sync"
    const val DIR_REQUEST_SYNC_FAILED_REQUEST_NOT_FOUND =
        "/queue/port/request_sync_failed_request_not_found"
    const val DIR_FILE_LIST_SYNC = "/queue/port/file_list_sync"
    const val DIR_NEW_MESSAGE = "/queue/port/new_message"
    const val DIR_NEW_GROUP_MESSAGE = "/queue/port/new_group_message"
    const val DIR_CONTACT_ADD_REQUEST_SUCCESS = "/queue/port/contact_add_request_success"
    const val DIR_CONTACT_ADD_REQUEST_FAILED_ALREADY_CONTACT =
        "/queue/port/contact_add_request_failed_already_contact"
    const val DIR_CONTACT_ADD_REQUEST_FAILED_USER_NOT_FOUND =
        "/queue/port/contact_add_failed_user_not_found"
    const val DIR_GROUP_ADD_REQUEST_SUCCESS = "/queue/port/group_add_request_success"
    const val DIR_GROUP_ADD_REQUEST_FAILED_ALREADY_MEMBER =
        "/queue/port/group_add_request_failed_already_member"
    const val DIR_GROUP_ADD_REQUEST_FAILED_GROUP_NOT_FOUND =
        "/queue/port/group_add_request_failed_group_not_found"
    const val DIR_WEBRTC_CALL = "/queue/port/webrtc_call"
    const val DIR_REQUEST_NOT_FOUND = "/queue/port/request_not_found"
}
