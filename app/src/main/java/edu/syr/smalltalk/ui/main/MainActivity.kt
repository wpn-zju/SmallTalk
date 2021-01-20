package edu.syr.smalltalk.ui.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.ISmallTalkService
import edu.syr.smalltalk.service.ISmallTalkServiceProvider
import edu.syr.smalltalk.service.KVPConstant
import edu.syr.smalltalk.service.RootService
import edu.syr.smalltalk.service.eventbus.AlertDialogEvent
import edu.syr.smalltalk.service.eventbus.SessionExpiredEvent
import edu.syr.smalltalk.service.eventbus.SignOutEvent
import edu.syr.smalltalk.service.eventbus.ToastEvent
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModelFactory
import edu.syr.smalltalk.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity(), ISmallTalkServiceProvider {
    private val viewModel: SmallTalkViewModel by viewModels {
        SmallTalkViewModelFactory(application as SmallTalkApplication)
    }

    private lateinit var service: ISmallTalkService
    private var bound: Boolean = false
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            service = (binder as RootService.RootServiceBinder).getService()
            bound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            bound = false
        }
    }

    override fun hasService(): Boolean {
        return bound
    }

    override fun getService(): ISmallTalkService? {
        return if (bound) {
            service
        } else {
            null
        }
    }

    override fun onStart() {
        super.onStart()

        Intent(this, RootService::class.java).also { intent -> bindService(
            intent,
            connection,
            Context.BIND_AUTO_CREATE
        ) }

        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()

        unbindService(connection)
        bound = false

        EventBus.getDefault().unregister(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { i ->
            if (i.getStringExtra("command") == "notification_start") {
                val chatId = i.getIntExtra("chatId", 0)
                val isGroup = i.getBooleanExtra("isGroup", false)
                val action = MainFragmentDirections.recentMessageListEnterChat(chatId, isGroup)
                nav_host_main.findNavController().navigate(action)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_recent_message, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        when (savedInstanceState.getString("fragment")) {
            "chat" -> {
                val isGroup = savedInstanceState.getBoolean("isGroup")
                val chatId = savedInstanceState.getInt("chatId")
                val action = MainFragmentDirections.recentMessageListEnterChat(chatId, isGroup)
                nav_host_main.findNavController().navigate(action)
            }
            null -> return
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSignOutMessage(signOutEvent: SignOutEvent) {
        logout()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSessionExpired(sessionExpiredEvent: SessionExpiredEvent) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.alert_dialog_session_expired))
            .setMessage(getString(R.string.alert_dialog_session_expired_detail))
            .setPositiveButton(getString(R.string.alert_dialog_confirm)) { _, _ -> logout() }
            .show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToastMessage(toastEvent: ToastEvent) {
        Toast.makeText(this, toastEvent.message, Toast.LENGTH_SHORT).show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAlertDialogMessage(alertDialogEvent: AlertDialogEvent) {
        AlertDialog.Builder(this)
            .setTitle(alertDialogEvent.title)
            .setMessage(alertDialogEvent.message)
            .setPositiveButton(getString(R.string.alert_dialog_confirm), null)
            .show()
    }

    private fun logout() {
        PreferenceManager.getDefaultSharedPreferences(this)
            .edit().putString(KVPConstant.K_USER_STATUS, KVPConstant.V_USER_STATUS_LOGOUT).apply()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
