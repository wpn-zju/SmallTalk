package edu.syr.smalltalk.service.model.logic

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.preference.PreferenceManager
import edu.syr.smalltalk.service.KVPConstant

class SmallTalkApplication : Application(), LifecycleObserver {
    private val database by lazy { SmallTalkRoomDatabase.getDatabase(this) }
    val repository by lazy { SmallTalkRepository(database.smallTalkDao()) }

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        PreferenceManager.getDefaultSharedPreferences(this).edit()
            .putBoolean(KVPConstant.K_IS_FOREGROUND, false).apply()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        PreferenceManager.getDefaultSharedPreferences(this).edit()
            .putBoolean(KVPConstant.K_IS_FOREGROUND, true).apply()
    }

    companion object {
        // const val HTTP_URL = "http://192.168.1.224:8079"
        // const val WS_URL = "http://192.168.1.224:8079"
        const val HTTP_URL = "https://smalltalknow.com:8078"
        const val WS_URL = "https://smalltalknow.com:8079"
    }
}
