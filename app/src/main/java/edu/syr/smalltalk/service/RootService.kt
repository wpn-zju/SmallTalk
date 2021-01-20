package edu.syr.smalltalk.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.JobIntentService
import edu.syr.smalltalk.service.android.ASmallTalkService
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication

class RootService : JobIntentService() {
    private lateinit var service: ISmallTalkService

    private val binder = RootServiceBinder()

    inner class RootServiceBinder : Binder() {
        fun getService(): ISmallTalkService = service
    }

    override fun onBind(intent: Intent): IBinder {
        Log.v("Root Service Lifecycle", "Bind")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.v("Root Service Lifecycle", "Unbind")
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        Log.v("Root Service Lifecycle", "Rebind")
        super.onRebind(intent)
    }

    override fun onHandleWork(intent: Intent) {

    }

    override fun onCreate() {
        createNotificationChannel()
        service = ASmallTalkService(application as SmallTalkApplication)
        service.connect()
    }

    override fun onDestroy() {
        service.disconnect()
    }

    private fun createNotificationChannel() {
        val name = "Message"
        val descText = "Message Pushing Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("Message", name, importance).apply {
            description = descText
        }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
