package edu.syr.smalltalk.service

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.JobIntentService
import edu.syr.smalltalk.service.android.ASmallTalkService
import edu.syr.smalltalk.service.blockchain.BCContractManager
import edu.syr.smalltalk.service.blockchain.BCSmallTalkService

class RootService : JobIntentService() {
    // TODO: 1. change service here
    // private val service: ISmallTalkService = ASmallTalkService(this)
    private val service: ISmallTalkService = BCSmallTalkService(this)

    private val binder = RootServiceBinder()

    inner class RootServiceBinder : Binder() {
        fun getService(): ISmallTalkService = service
    }

    override fun onBind(intent: Intent): IBinder {
        Log.v("Bind", "Bind")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.v("Unbind", "Unbind")
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        Log.v("Rebind", "Rebind")
        super.onRebind(intent)
    }

    override fun onHandleWork(intent: Intent) {

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        service.connect()
    }

    override fun onDestroy() {
        service.disconnect()
    }
}
