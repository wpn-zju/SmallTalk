package edu.syr.smalltalk.service

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.JobIntentService
import edu.syr.smalltalk.service.android.ASmallTalkService

class RootService : JobIntentService() {
    private val service: ISmallTalkService = ASmallTalkService(this)
    private val binder = RootServiceBinder()

    inner class RootServiceBinder : Binder() {
        fun getService(): ISmallTalkService = service
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onHandleWork(intent: Intent) {

    }
}