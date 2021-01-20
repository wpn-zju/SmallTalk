package edu.syr.smalltalk.ui.login

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.ISmallTalkService
import edu.syr.smalltalk.service.ISmallTalkServiceProvider
import edu.syr.smalltalk.service.KVPConstant
import edu.syr.smalltalk.service.RootService
import edu.syr.smalltalk.service.eventbus.AlertDialogEvent
import edu.syr.smalltalk.service.eventbus.SignInEvent
import edu.syr.smalltalk.service.eventbus.ToastEvent
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModelFactory
import edu.syr.smalltalk.ui.main.MainActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class LoginActivity : AppCompatActivity(), ISmallTalkServiceProvider {
    private val viewModel: SmallTalkViewModel by viewModels {
        SmallTalkViewModelFactory(application as SmallTalkApplication)
    }

    private lateinit var service: ISmallTalkService
    private var bound: Boolean = false
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            service = (binder as RootService.RootServiceBinder).getService()
            bound = true

            PreferenceManager.getDefaultSharedPreferences(this@LoginActivity)
                .getString(KVPConstant.K_LATEST_SESSION, "null")?.let { lastSession ->
                    if (lastSession != "null") {
                        service.userSessionSignIn(lastSession)
                    }
                }
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

        // Write following lines in the first activity
        Intent(this, RootService::class.java).also { intent -> startService(
            intent
        ) }

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
        setContentView(R.layout.activity_login)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSignInMessage(signInEvent: SignInEvent) {
        login()
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

    private fun login() {
        PreferenceManager.getDefaultSharedPreferences(this)
            .edit().putString(KVPConstant.K_USER_STATUS, KVPConstant.V_USER_STATUS_LOGIN).apply()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
