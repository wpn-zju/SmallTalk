package edu.syr.smalltalk.service.android

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.JsonParser
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.KVPConstant
import edu.syr.smalltalk.service.android.constant.ClientConstant
import edu.syr.smalltalk.service.android.constant.ServerConstant
import edu.syr.smalltalk.service.eventbus.*
import edu.syr.smalltalk.service.model.entity.*
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkDao
import edu.syr.smalltalk.ui.main.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import java.time.Instant

class AWebSocketManager(private val context: Context) {
    private val stompClient: StompClient = Stomp.over(Stomp.ConnectionProvider.JWS, SmallTalkApplication.BASE_URL + "/small_talk_websocket/websocket")
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private lateinit var smalltalkDao: SmallTalkDao

    fun setDataAccessor(smallTalkDao: SmallTalkDao) {
        if (!this::smalltalkDao.isInitialized) { smalltalkDao = smallTalkDao }
    }

    fun connect() {
        stompClient.connect()
        compositeDisposable.add(stompClient.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { lifecycleEvent: LifecycleEvent ->
                when (lifecycleEvent.type) {
                    LifecycleEvent.Type.OPENED -> {
                        Log.v("WebSocket Stomp Client Info", "Connected")
                    }
                    LifecycleEvent.Type.CLOSED -> {
                        Log.v("WebSocket Stomp Client Info", "Disconnected")
                        stompClient.reconnect()
                    }
                    LifecycleEvent.Type.ERROR -> {
                        Log.v("WebSocket Stomp Client Info", "Error - " + lifecycleEvent.exception)
                    }
                    LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> {
                        Log.v("WebSocket Stomp Client Info", "Heartbeat Failed")
                    }
                    else -> {
                        Log.v("WebSocket Stomp Client Info", "Null LifecycleEvent")
                    }
                }
            })
        subscribe()
    }

    fun disconnect() {
        compositeDisposable.dispose()
        stompClient.disconnect()
    }

    fun send(endPoint: String, message: String) {
        stompClient.send("/app$endPoint", message).subscribe()
    }

    private fun subscribe() {
        // Examples
        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_SYNC).subscribe {
            val userInfo: SmallTalkUser = Gson().fromJson(it.payload, SmallTalkUser::class.java)
            smalltalkDao.insertUser(userInfo)
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(KVPConstant.K_CURRENT_USER_ID, userInfo.userId).apply()
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(KVPConstant.K_LAST_SESSION, userInfo.userSession).apply()
            EventBus.getDefault().post(SignInEvent())
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_CONTACT_SYNC).subscribe {
            val contactInfo: SmallTalkContact = Gson().fromJson(it.payload, SmallTalkContact::class.java)
            smalltalkDao.insertContact(contactInfo)
            EventBus.getDefault().post(SearchContactSuccessEvent(contactInfo.contactId))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_GROUP_SYNC).subscribe {
            val groupInfo: SmallTalkGroup = Gson().fromJson(it.payload, SmallTalkGroup::class.java)
            smalltalkDao.insertGroup(groupInfo)
            EventBus.getDefault().post(SearchGroupSuccessEvent(groupInfo.groupId))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_REQUEST_SYNC).subscribe {
            val requestInfo: SmallTalkRequest = Gson().fromJson(it.payload, SmallTalkRequest::class.java)
            smalltalkDao.insertRequest(requestInfo)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_CONTACT_SYNC_FAILED_USER_NOT_FOUND).subscribe {
            EventBus.getDefault().post(AlertDialogEvent("Error",
                "User not found!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_GROUP_SYNC_FAILED_GROUP_NOT_FOUND).subscribe {
            EventBus.getDefault().post(AlertDialogEvent("Error",
                "Group not found!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_REQUEST_SYNC_FAILED_REQUEST_NOT_FOUND).subscribe {
            EventBus.getDefault().post(AlertDialogEvent("Error",
                "Request not found!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_INVALID_PASSCODE).subscribe {
            EventBus.getDefault().post(AlertDialogEvent("Error",
                "Passcode should be a string with 6 characters (numbers and letters only)!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_INVALID_SESSION).subscribe {
            EventBus.getDefault().post(AlertDialogEvent("Error",
                "Session Token should be a string with 36 characters (numbers and letters only)!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_INVALID_USER_NAME).subscribe {
            EventBus.getDefault().post(AlertDialogEvent("Error",
                "The length of a valid user name must be 2-16 characters!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_INVALID_USER_EMAIL).subscribe {
            EventBus.getDefault().post(AlertDialogEvent("Error", "Invalid Email Address"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_INVALID_USER_PASSWORD).subscribe {
            EventBus.getDefault().post(AlertDialogEvent("Error",
                "The length of a valid password must be 2 - 16 characters. " +
                        "You should only put numbers and letters in your password (and must both of them)!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_INVALID_GROUP_NAME).subscribe {
            EventBus.getDefault().post(AlertDialogEvent("Error",
                "The length of a valid group name must be 2-16 characters!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_UP_SUCCESS).subscribe {
            EventBus.getDefault().post(ToastEvent("Sign up successfully!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_UP_FAILED_EMAIL_EXISTS).subscribe {
            EventBus.getDefault().post(ToastEvent("Sign up failed - User Exists!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_UP_FAILED_PASSCODE_INCORRECT).subscribe {
            EventBus.getDefault().post(ToastEvent("Sign up failed - Passcode Incorrect!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_UP_PASSCODE_REQUEST_SUCCESS).subscribe {
            EventBus.getDefault().post(ToastEvent("Passcode sent to your email!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_UP_PASSCODE_REQUEST_FAILED_SERVER_ERROR).subscribe {
            EventBus.getDefault().post(ToastEvent("Error - Server Error!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_UP_PASSCODE_REQUEST_FAILED_EMAIL_EXISTS).subscribe {
            EventBus.getDefault().post(ToastEvent("Error - User Exists!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_RECOVER_PASSWORD_SUCCESS).subscribe {
            EventBus.getDefault().post(ToastEvent("Password Reset!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_RECOVER_PASSWORD_FAILED_USER_NOT_FOUND).subscribe {
            EventBus.getDefault().post(ToastEvent("User not found!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_RECOVER_PASSWORD_FAILED_PASSCODE_INCORRECT).subscribe {
            EventBus.getDefault().post(ToastEvent("Recover password failed - Passcode Incorrect!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_RECOVER_PASSWORD_PASSCODE_REQUEST_SUCCESS).subscribe {
            EventBus.getDefault().post(ToastEvent("Passcode sent to your email!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_RECOVER_PASSWORD_PASSCODE_REQUEST_FAILED_SERVER_ERROR).subscribe {
            EventBus.getDefault().post(ToastEvent("Error - Server Error!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_RECOVER_PASSWORD_PASSCODE_REQUEST_FAILED_USER_NOT_FOUND).subscribe {
            EventBus.getDefault().post(ToastEvent("Error - User not found!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_IN_SUCCESS).subscribe {
            EventBus.getDefault().post(ToastEvent("Sign In - Success"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_IN_FAILED_USER_NOT_FOUND).subscribe {
            EventBus.getDefault().post(ToastEvent("Error - User not found!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_IN_FAILED_PASSWORD_INCORRECT).subscribe {
            EventBus.getDefault().post(ToastEvent("Error - Password Incorrect!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_OUT_SUCCESS).subscribe {
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(KVPConstant.K_LAST_SESSION, "null").apply()
            EventBus.getDefault().post(SignOutEvent())
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_SESSION_INVALID).subscribe {
            EventBus.getDefault().post(SessionExpiredEvent())
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_SESSION_EXPIRED).subscribe {
            EventBus.getDefault().post(SessionExpiredEvent())
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_SESSION_REVOKED).subscribe {
            EventBus.getDefault().post(SessionExpiredEvent())
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_MODIFY_NAME_SUCCESS).subscribe {
            EventBus.getDefault().post(ToastEvent("User name modified!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_MODIFY_PASSWORD_SUCCESS).subscribe {
            EventBus.getDefault().post(ToastEvent("Password modified!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_NEW_MESSAGE).subscribe {
            val messageObj = JsonParser.parseString(it.payload).asJsonObject
            val user: Int = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(KVPConstant.K_CURRENT_USER_ID, 0)
            val sender: Int = messageObj.get(ServerConstant.CHAT_NEW_MESSAGE__SENDER).asInt
            val receiver: Int = messageObj.get(ServerConstant.CHAT_NEW_MESSAGE__RECEIVER).asInt
            val chatId: Int = if (user == sender) receiver else if (user == receiver) sender else receiver
            val content: String = messageObj.get(ServerConstant.CHAT_NEW_MESSAGE__CONTENT).asString
            val contentType: String = messageObj.get(ServerConstant.CHAT_NEW_MESSAGE__CONTENT_TYPE).asString
            val timestamp = Instant.parse(messageObj.get(ServerConstant.TIMESTAMP).asString)
            val smallTalkMessage = SmallTalkMessage(user, chatId, sender, receiver, content, contentType, timestamp)
            smalltalkDao.insertMessage(smallTalkMessage)

            if (!PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean(KVPConstant.K_IS_FOREGROUND, true)) {
                val notificationText: String = when (contentType) {
                    ClientConstant.CHAT_CONTENT_TYPE_TEXT -> content
                    ClientConstant.CHAT_CONTENT_TYPE_IMAGE -> "[Image]"
                    ClientConstant.CHAT_CONTENT_TYPE_AUDIO -> "[Audio]"
                    ClientConstant.CHAT_CONTENT_TYPE_VIDEO -> "[Video]"
                    ClientConstant.CHAT_CONTENT_TYPE_FILE -> "[File]"
                    else -> "New Message" }
                val intent = Intent(context, MainActivity::class.java)
                intent.putExtra("command", "notification_start")
                intent.putExtra("chatId", chatId)
                intent.putExtra("isGroup", receiver != user)
                val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
                val builder = NotificationCompat.Builder(context, "Message")
                    .setSmallIcon(R.drawable.ic_baseline_message_24)
                    .setContentTitle("Small Talk")
                    .setContentText(notificationText)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                with(NotificationManagerCompat.from(context)) {
                    notify(10000, builder.build())
                }
            }
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_CONTACT_ADD_REQUEST_SUCCESS).subscribe {
            EventBus.getDefault().post(ToastEvent("Contact request sent!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_CONTACT_ADD_REQUEST_FAILED_ALREADY_CONTACT).subscribe {
            EventBus.getDefault().post(ToastEvent("Contact request failed - You are already friends!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_CONTACT_ADD_REQUEST_FAILED_USER_NOT_FOUND).subscribe {
            EventBus.getDefault().post(ToastEvent("Contact request failed - User not found!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_GROUP_CREATE_REQUEST_SUCCESS).subscribe {
            EventBus.getDefault().post(ToastEvent("New group created!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_GROUP_MODIFY_NAME_SUCCESS).subscribe {
            EventBus.getDefault().post(ToastEvent("Group name modified!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_GROUP_MODIFY_NAME_FAILED_GROUP_NOT_FOUND).subscribe {
            EventBus.getDefault().post(ToastEvent("Group name modification failed - Group not found!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_GROUP_MODIFY_NAME_FAILED_PERMISSION_DENIED).subscribe {
            EventBus.getDefault().post(ToastEvent("Group name modification failed - Only group host can modify group name!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_GROUP_ADD_REQUEST_SUCCESS).subscribe {
            EventBus.getDefault().post(ToastEvent("Group request sent!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_GROUP_ADD_REQUEST_FAILED_ALREADY_MEMBER).subscribe {
            EventBus.getDefault().post(ToastEvent("Group request failed - You are already a member of this group!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_GROUP_ADD_REQUEST_FAILED_GROUP_NOT_FOUND).subscribe {
            EventBus.getDefault().post(ToastEvent("Group request failed - Group not found!"))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_WEBRTC_CALL).subscribe {
            EventBus.getDefault().post(Gson().fromJson(it.payload, WebRTCEvent::class.java))
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_REQUEST_NOT_FOUND).subscribe {
            EventBus.getDefault().post(ToastEvent("Request ID not found!"))
        })
    }
}
