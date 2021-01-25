package edu.syr.smalltalk.service.android

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.android.constant.ClientConstant
import edu.syr.smalltalk.service.android.constant.ServerConstant
import edu.syr.smalltalk.service.eventbus.*
import edu.syr.smalltalk.service.model.entity.*
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.ui.main.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import java.time.Instant

class AWebSocketManager(application: SmallTalkApplication) {
    private val stompClient: StompClient = Stomp.over(
        Stomp.ConnectionProvider.JWS,
        SmallTalkApplication.WS_URL + "/small_talk_websocket/websocket"
    )
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val repository = application.repository
    private val context = application.applicationContext

    fun connect() {
        stompClient.connect()
        compositeDisposable.add(stompClient.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { lifecycleEvent ->
                when (lifecycleEvent.type) {
                    LifecycleEvent.Type.OPENED -> {
                        Log.v("WebSocket Stomp Client Info", "Connected")
                        registerSubscriptions()
                        tryReplaceSession()
                    }
                    LifecycleEvent.Type.CLOSED -> {
                        Log.v("WebSocket Stomp Client Info", "Disconnected")
                    }
                    LifecycleEvent.Type.ERROR -> {
                        Log.v("WebSocket Stomp Client Info", lifecycleEvent.exception.stackTraceToString())
                    }
                    LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> {
                        Log.v("WebSocket Stomp Client Info", "Heartbeat Failed")
                    }
                    else -> {
                        Log.v("WebSocket Stomp Client Info", "Null LifecycleEvent")
                    }
                }
            })
    }

    fun disconnect() {
        compositeDisposable.dispose()
        stompClient.disconnect()
    }

    fun send(endPoint: String, message: String) {
        compositeDisposable.add(stompClient
            .send("/app$endPoint", message).subscribe ({
                Log.v("WEBSOCKET MANAGER", "MESSAGE SENT")
            }, {
                Log.e("WEBSOCKET MANAGER", it.stackTrace.toString())
            }))
    }

    private fun tryReplaceSession() {
        SmallTalkApplication.getLastSession(context)?.let { lastSession ->
            send(ClientConstant.API_USER_REPLACE_SESSION, Gson().toJson(SessionReplaceMessage(lastSession)))
        }
    }

    private fun registerSubscriptions() {
        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_USER_SYNC).subscribe {
                val userInfo = Gson().fromJson(it.payload, SmallTalkUser::class.java)
                repository.insertUser(userInfo)
                SmallTalkApplication.setCurrentUserId(context, userInfo.userId)
                SmallTalkApplication.setLastSession(context, userInfo.userSession)
                EventBus.getDefault().post(SignInEvent())
            })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_CONTACT_SYNC).subscribe {
                val contactInfo = Gson().fromJson(it.payload, SmallTalkContact::class.java)
                repository.insertContact(contactInfo)
                EventBus.getDefault().post(SearchContactSuccessEvent(contactInfo.contactId))
            })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_GROUP_SYNC).subscribe {
                val groupInfo = Gson().fromJson(it.payload, SmallTalkGroup::class.java)
                repository.insertGroup(groupInfo)
                EventBus.getDefault().post(SearchGroupSuccessEvent(groupInfo.groupId))
            })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_REQUEST_SYNC).subscribe {
                val requestInfo = Gson().fromJson(it.payload, SmallTalkRequest::class.java)
                repository.insertRequest(requestInfo)
            })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_FILE_LIST_SYNC).subscribe {
                val fileList = Gson().fromJson<List<SmallTalkFile>>(it.payload, object : TypeToken<List<SmallTalkFile>>(){}.type)
                fileList.forEach { file -> repository.insertFile(file) }
            })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_CONTACT_SYNC_FAILED_USER_NOT_FOUND)
                .subscribe {
                    EventBus.getDefault().post(
                        AlertDialogEvent(
                            context.getString(R.string.alert_dialog_error),
                            context.getString(R.string.alert_dialog_user_not_found)
                        )
                    )
                })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_GROUP_SYNC_FAILED_GROUP_NOT_FOUND)
                .subscribe {
                    EventBus.getDefault().post(
                        AlertDialogEvent(
                            context.getString(R.string.alert_dialog_error),
                            context.getString(R.string.alert_dialog_group_not_found)
                        )
                    )
                })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_REQUEST_SYNC_FAILED_REQUEST_NOT_FOUND)
                .subscribe {
                    EventBus.getDefault().post(
                        AlertDialogEvent(
                            context.getString(R.string.alert_dialog_error),
                            context.getString(R.string.alert_dialog_request_not_found)
                        )
                    )
                })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_INVALID_PASSCODE).subscribe {
                EventBus.getDefault().post(
                    AlertDialogEvent(
                        context.getString(R.string.alert_dialog_error),
                        context.getString(R.string.alert_dialog_invalid_passcode)
                    )
                )
            })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_INVALID_SESSION).subscribe {
                EventBus.getDefault().post(
                    AlertDialogEvent(
                        context.getString(R.string.alert_dialog_error),
                        context.getString(R.string.alert_dialog_invalid_session_token)
                    )
                )
            })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_INVALID_USER_NAME).subscribe {
                EventBus.getDefault().post(
                    AlertDialogEvent(
                        context.getString(R.string.alert_dialog_error),
                        context.getString(R.string.alert_dialog_invalid_user_name)
                    )
                )
            })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_INVALID_USER_EMAIL).subscribe {
                EventBus.getDefault().post(
                    AlertDialogEvent(
                        context.getString(R.string.alert_dialog_error),
                        context.getString(R.string.alert_dialog_invalid_email)
                    )
                )
            })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_INVALID_USER_PASSWORD).subscribe {
                EventBus.getDefault().post(
                    AlertDialogEvent(
                        context.getString(R.string.alert_dialog_error),
                        context.getString(R.string.alert_dialog_invalid_password)
                    )
                )
            })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_INVALID_GROUP_NAME).subscribe {
                EventBus.getDefault().post(
                    AlertDialogEvent(
                        context.getString(R.string.alert_dialog_error),
                        context.getString(R.string.alert_dialog_invalid_group_name)
                    )
                )
            })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_UP_SUCCESS).subscribe {
                EventBus.getDefault().post(ToastEvent(context.getString(R.string.toast_sign_up)))
            })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_UP_FAILED_EMAIL_EXISTS)
                .subscribe {
                    EventBus.getDefault().post(ToastEvent(context.getString(R.string.toast_sign_up_failed_user_exists)))
                })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_UP_FAILED_PASSCODE_INCORRECT)
                .subscribe {
                    EventBus.getDefault().post(ToastEvent(context.getString(R.string.toast_sign_up_failed_passcode_incorrect)))
                })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_UP_PASSCODE_REQUEST_SUCCESS)
                .subscribe {
                    EventBus.getDefault().post(ToastEvent(context.getString(R.string.toast_passcode_sent)))
                })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_UP_PASSCODE_REQUEST_FAILED_SERVER_ERROR)
                .subscribe {
                    EventBus.getDefault().post(ToastEvent(context.getString(R.string.toast_server_error)))
                })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_UP_PASSCODE_REQUEST_FAILED_EMAIL_EXISTS)
                .subscribe {
                    EventBus.getDefault().post(ToastEvent(context.getString(R.string.toast_user_exists)))
                })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_USER_RECOVER_PASSWORD_SUCCESS)
                .subscribe {
                    EventBus.getDefault().post(ToastEvent(context.getString(R.string.toast_password_reset)))
                })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_USER_RECOVER_PASSWORD_FAILED_USER_NOT_FOUND)
                .subscribe {
                    EventBus.getDefault().post(ToastEvent(context.getString(R.string.toast_user_not_found)))
                })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_USER_RECOVER_PASSWORD_FAILED_PASSCODE_INCORRECT)
                .subscribe {
                    EventBus.getDefault()
                        .post(ToastEvent(context.getString(R.string.toast_password_reset_failed_passcode_incorrect)))
                })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_USER_RECOVER_PASSWORD_PASSCODE_REQUEST_SUCCESS)
                .subscribe {
                    EventBus.getDefault().post(ToastEvent(context.getString(R.string.toast_passcode_sent)))
                })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_USER_RECOVER_PASSWORD_PASSCODE_REQUEST_FAILED_SERVER_ERROR)
                .subscribe {
                    EventBus.getDefault().post(ToastEvent(context.getString(R.string.toast_server_error)))
                })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_USER_RECOVER_PASSWORD_PASSCODE_REQUEST_FAILED_USER_NOT_FOUND)
                .subscribe {
                    EventBus.getDefault().post(ToastEvent(context.getString(R.string.toast_user_not_found)))
                })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_IN_SUCCESS).subscribe {
                EventBus.getDefault().post(ToastEvent(context.getString(R.string.toast_sign_in)))
            })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_IN_FAILED_USER_NOT_FOUND)
                .subscribe {
                    EventBus.getDefault().post(ToastEvent(context.getString(R.string.toast_user_not_found)))
                })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_IN_FAILED_PASSWORD_INCORRECT)
                .subscribe {
                    EventBus.getDefault().post(ToastEvent(context.getString(R.string.toast_password_incorrect)))
                })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_OUT_SUCCESS).subscribe {
                SmallTalkApplication.setLastSession(context, null)
                EventBus.getDefault().post(SignOutEvent())
            })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_USER_SESSION_INVALID).subscribe {
                EventBus.getDefault().post(SessionExpiredEvent())
            })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_USER_SESSION_EXPIRED).subscribe {
                EventBus.getDefault().post(SessionExpiredEvent())
            })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_USER_SESSION_REVOKED).subscribe {
                EventBus.getDefault().post(SessionExpiredEvent())
            })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_NEW_MESSAGE).subscribe {
                insertMessage(it.payload, false)
            })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_NEW_GROUP_MESSAGE).subscribe {
                insertMessage(it.payload, true)
            })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_CONTACT_ADD_REQUEST_SUCCESS).subscribe {
                EventBus.getDefault().post(ToastEvent(context.getString(R.string.toast_contact_request_sent)))
            })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_CONTACT_ADD_REQUEST_FAILED_ALREADY_CONTACT)
                .subscribe {
                    EventBus.getDefault()
                        .post(ToastEvent(context.getString(R.string.toast_contact_request_failed_already_friends)))
                })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_CONTACT_ADD_REQUEST_FAILED_USER_NOT_FOUND)
                .subscribe {
                    EventBus.getDefault()
                        .post(ToastEvent(context.getString(R.string.toast_contact_request_failed_user_not_found)))
                })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_GROUP_ADD_REQUEST_SUCCESS).subscribe {
                EventBus.getDefault().post(ToastEvent(context.getString(R.string.toast_group_request_sent)))
            })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_GROUP_ADD_REQUEST_FAILED_ALREADY_MEMBER)
                .subscribe {
                    EventBus.getDefault()
                        .post(ToastEvent(context.getString(R.string.toast_group_request_failed_already_member)))
                })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_GROUP_ADD_REQUEST_FAILED_GROUP_NOT_FOUND)
                .subscribe {
                    EventBus.getDefault()
                        .post(ToastEvent(context.getString(R.string.toast_group_request_failed_group_not_found)))
                })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_WEBRTC_CALL).subscribe {
                EventBus.getDefault().post(Gson().fromJson(it.payload, WebRTCEvent::class.java))
            })

        compositeDisposable.add(
            stompClient.topic("/user" + ServerConstant.DIR_REQUEST_NOT_FOUND).subscribe {
                EventBus.getDefault().post(ToastEvent(context.getString(R.string.toast_request_not_found)))
            })
    }

    private fun insertMessage(payload: String, isGroup: Boolean) {
        val messageObj = JsonParser.parseString(payload).asJsonObject
        val userId: Int = SmallTalkApplication.getCurrentUserId(context)
        val senderId: Int = messageObj.get("sender").asInt
        val receiverId: Int = messageObj.get("receiver").asInt
        val chatId: Int = if (userId == senderId) receiverId else if (userId == receiverId) senderId else receiverId
        val content: String = messageObj.get("content").asString
        val contentType: String = messageObj.get("content_type").asString
        val timestamp = Instant.parse(messageObj.get(ClientConstant.TIMESTAMP).asString)
        val hasRead = chatId == SmallTalkApplication.getCurrentChatId(context)
        val smallTalkMessage = SmallTalkMessage(
            userId, isGroup, hasRead,
            chatId, senderId, receiverId, content, contentType, timestamp
        )
        repository.insertMessage(smallTalkMessage)
        GlobalScope.launch { sendNotification(smallTalkMessage); }
    }

    private fun sendNotification(message: SmallTalkMessage) {
        if (!SmallTalkApplication.getIsForeGround(context)) {
            val chatName = if (message.isGroup) {
                repository.getGroup(message.chatId)?.groupName ?: context.getString(R.string.app_name)
            } else {
                repository.getContact(message.chatId)?.contactName ?: context.getString(R.string.app_name)
            }
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("command", "notification_start")
            intent.putExtra("chatId", message.chatId)
            intent.putExtra("isGroup", message.isGroup)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            val builder = NotificationCompat.Builder(context, "Message")
                .setSmallIcon(R.mipmap.ic_smalltalk)
                .setContentTitle(chatName)
                .setContentText(message.getContentPreview(context))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
            with(NotificationManagerCompat.from(context)) {
                notify(10000, builder.build())
            }
        }
    }
}
