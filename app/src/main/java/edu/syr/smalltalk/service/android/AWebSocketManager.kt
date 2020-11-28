package edu.syr.smalltalk.service.android

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.gson.Gson
import edu.syr.smalltalk.service.IntentAction
import edu.syr.smalltalk.service.android.constant.ServerConstant
import edu.syr.smalltalk.service.eventbus.UserRefreshEvent
import edu.syr.smalltalk.service.model.entity.SmallTalkContact
import edu.syr.smalltalk.service.model.entity.SmallTalkGroup
import edu.syr.smalltalk.service.model.entity.SmallTalkRequest
import edu.syr.smalltalk.service.model.entity.SmallTalkUser
import edu.syr.smalltalk.service.model.logic.SmallTalkDao
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent

class AWebSocketManager(private val context: Context) {
    private val stompClient: StompClient = Stomp.over(Stomp.ConnectionProvider.JWS, "http://10.0.2.2:8080/small_talk_websocket/websocket")
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private lateinit var smalltalkDao: SmallTalkDao

    fun setDataAccessor(smallTalkDao: SmallTalkDao) {
        if (!this::smalltalkDao.isInitialized) { smalltalkDao = smallTalkDao }
    }

    init {
        connect()
    }

    fun connect() {
        stompClient.connect()
        compositeDisposable.add(stompClient.lifecycle().subscribeOn(Schedulers.io())
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
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_CONTACT_SYNC).subscribe {
            Log.v("Receive Data", it.payload)
            val contactInfo: SmallTalkContact = Gson().fromJson(it.payload, SmallTalkContact::class.java)
            smalltalkDao.insertContact(contactInfo)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_GROUP_SYNC).subscribe {
            val groupInfo: SmallTalkGroup = Gson().fromJson(it.payload, SmallTalkGroup::class.java)
            smalltalkDao.insertGroup(groupInfo)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_REQUEST_SYNC).subscribe {
            val requestInfo: SmallTalkRequest = Gson().fromJson(it.payload, SmallTalkRequest::class.java)
            smalltalkDao.insertRequest(requestInfo)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_INVALID_PASSCODE).subscribe {
            AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage("Passcode should be a 6-digit number!")
                .setPositiveButton("Yes", null)
                .show()
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_INVALID_SESSION).subscribe {
            Log.v("Greetings", it.payload)

            EventBus.getDefault().post(UserRefreshEvent())

            val intent: Intent = Intent()
            intent.action = IntentAction.INTENT_REFRESH_USER
            context.sendBroadcast(intent)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_INVALID_USER_NAME).subscribe {
            Log.v("Greetings", it.payload)

            val intent: Intent = Intent()
            intent.action = IntentAction.INTENT_REFRESH_USER
            context.sendBroadcast(intent)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_INVALID_USER_EMAIL).subscribe {
            Log.v("Greetings", it.payload)

            val intent: Intent = Intent()
            intent.action = IntentAction.INTENT_REFRESH_USER
            context.sendBroadcast(intent)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_INVALID_USER_PASSWORD).subscribe {
            Log.v("Greetings", it.payload)

            val intent: Intent = Intent()
            intent.action = IntentAction.INTENT_REFRESH_USER
            context.sendBroadcast(intent)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_INVALID_GROUP_NAME).subscribe {
            Log.v("Greetings", it.payload)

            val intent: Intent = Intent()
            intent.action = IntentAction.INTENT_REFRESH_USER
            context.sendBroadcast(intent)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_UP_SUCCESS).subscribe {
            Log.v("Greetings", it.payload)

            val intent: Intent = Intent()
            intent.action = IntentAction.INTENT_REFRESH_USER
            context.sendBroadcast(intent)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_UP_FAILED_EMAIL_EXISTS).subscribe {
            Log.v("Greetings", it.payload)

            val intent: Intent = Intent()
            intent.action = IntentAction.INTENT_REFRESH_USER
            context.sendBroadcast(intent)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_UP_FAILED_PASSCODE_INCORRECT).subscribe {
            Log.v("Greetings", it.payload)

            val intent: Intent = Intent()
            intent.action = IntentAction.INTENT_REFRESH_USER
            context.sendBroadcast(intent)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_UP_PASSCODE_REQUEST_SUCCESS).subscribe {
            Log.v("Greetings", it.payload)

            val intent: Intent = Intent()
            intent.action = IntentAction.INTENT_REFRESH_USER
            context.sendBroadcast(intent)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_UP_PASSCODE_REQUEST_FAILED_SERVER_ERROR).subscribe {
            Log.v("Greetings", it.payload)

            val intent: Intent = Intent()
            intent.action = IntentAction.INTENT_REFRESH_USER
            context.sendBroadcast(intent)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_UP_PASSCODE_REQUEST_FAILED_EMAIL_EXISTS).subscribe {
            Log.v("Greetings", it.payload)

            val intent: Intent = Intent()
            intent.action = IntentAction.INTENT_REFRESH_USER
            context.sendBroadcast(intent)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_RECOVER_PASSWORD_SUCCESS).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_RECOVER_PASSWORD_FAILED_USER_NOT_FOUND).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_RECOVER_PASSWORD_FAILED_PASSCODE_INCORRECT).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_RECOVER_PASSWORD_PASSCODE_REQUEST_SUCCESS).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_RECOVER_PASSWORD_PASSCODE_REQUEST_FAILED_SERVER_ERROR).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_RECOVER_PASSWORD_PASSCODE_REQUEST_FAILED_USER_NOT_FOUND).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_IN_SUCCESS).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_IN_FAILED_USER_NOT_FOUND).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_IN_FAILED_PASSWORD_INCORRECT).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_SIGN_OUT_SUCCESS).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_SESSION_INVALID).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_SESSION_EXPIRED).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_SESSION_REVOKED).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_MODIFY_NAME_SUCCESS).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_USER_MODIFY_PASSWORD_SUCCESS).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_INVALID_PASSCODE).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_INVALID_PASSCODE).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_INVALID_PASSCODE).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_INVALID_PASSCODE).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_NEW_MESSAGE).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_CONTACT_ADD_REQUEST_SUCCESS).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_CONTACT_ADD_REQUEST_FAILED_ALREADY_CONTACT).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_CONTACT_ADD_REQUEST_FAILED_USER_NOT_FOUND).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_GROUP_CREATE_REQUEST_SUCCESS).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_GROUP_MODIFY_NAME_SUCCESS).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_GROUP_MODIFY_NAME_FAILED_GROUP_NOT_FOUND).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_GROUP_MODIFY_NAME_FAILED_PERMISSION_DENIED).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_GROUP_ADD_REQUEST_SUCCESS).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_GROUP_ADD_REQUEST_FAILED_ALREADY_MEMBER).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_GROUP_ADD_REQUEST_FAILED_GROUP_NOT_FOUND).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_WEBRTC_CALL).subscribe {
            Log.v("Greetings", it.payload)
        })

        compositeDisposable.add(stompClient.topic("/user" + ServerConstant.DIR_REQUEST_NOT_FOUND).subscribe {
            Log.v("Greetings", it.payload)
        })
    }
}