package edu.syr.smalltalk.ui.main

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
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.ISmallTalkService
import edu.syr.smalltalk.service.ISmallTalkServiceProvider
import edu.syr.smalltalk.service.RootService
import edu.syr.smalltalk.service.eventbus.AlertDialogEvent
import edu.syr.smalltalk.service.eventbus.ToastEvent
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModelFactory
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ChatRoomActivity : AppCompatActivity(), ISmallTalkServiceProvider {
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
        setContentView(R.layout.activity_chat_room)
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
            .setPositiveButton("Confirm", null)
            .show()
    }
}
