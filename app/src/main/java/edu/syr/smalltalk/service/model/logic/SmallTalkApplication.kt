package edu.syr.smalltalk.service.model.logic

import android.app.Application
import android.content.Context
import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.preference.PreferenceManager
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.KVPConstant

class SmallTalkApplication : Application(), LifecycleObserver {
    private val database by lazy { SmallTalkRoomDatabase.getDatabase(this) }
    val repository by lazy { SmallTalkRepository(database.smallTalkDao()) }

    override fun onCreate() {
        super.onCreate()
        observeLifecycle()
        setupPicassoDownloader()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        setIsForeGround(this, false)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        setIsForeGround(this, true)
    }

    private fun observeLifecycle() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    private fun setupPicassoDownloader() {
        val builder = Picasso.Builder(this)
        builder.downloader(OkHttp3Downloader(this, Long.MAX_VALUE))
        val built = builder.build()
        built.setIndicatorsEnabled(true)
        built.isLoggingEnabled = true
        Picasso.setSingletonInstance(built)
    }

    companion object {
        // const val HTTP_URL = "http://192.168.1.224:8079"
        // const val WS_URL = "http://192.168.1.224:8079"
        const val HTTP_URL = "https://smalltalknow.com"
        const val WS_URL = "http://smalltalknow.com:8079"

        fun picasso(url: String, target: ImageView) {
            Picasso.get()
                .load(url)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(target, object : Callback {
                    override fun onSuccess() {

                    }

                    override fun onError(e: Exception?) {
                        Picasso.get()
                            .load(url)
                            .placeholder(R.mipmap.ic_smalltalk)
                            .error(R.mipmap.ic_smalltalk)
                            .into(target)
                    }
                })
        }

        fun getCurrentUserId(context: Context): Int {
            return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(KVPConstant.K_CURRENT_USER_ID, 0)
        }

        fun setCurrentUserId(context: Context, userId: Int) {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putInt(KVPConstant.K_CURRENT_USER_ID, userId).apply()
        }

        fun getCurrentChatId(context: Context): Int {
            return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(KVPConstant.K_CURRENT_CHAT_ID, 0)
        }

        fun setCurrentChatId(context: Context, chatId: Int) {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putInt(KVPConstant.K_CURRENT_CHAT_ID, chatId).apply()
        }

        fun getLastSession(context: Context): String? {
            return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KVPConstant.K_LATEST_SESSION, null)
        }

        fun setLastSession(context: Context, session: String?) {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putString(KVPConstant.K_LATEST_SESSION, session).apply()
        }

        fun getIsForeGround(context: Context): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KVPConstant.K_IS_FOREGROUND, false)
        }

        fun setIsForeGround(context: Context, isForeGround: Boolean) {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putBoolean(KVPConstant.K_IS_FOREGROUND, isForeGround).apply()
        }
    }
}
