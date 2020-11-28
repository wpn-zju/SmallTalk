package edu.syr.smalltalk.ui.init

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.ISmallTalkService
import edu.syr.smalltalk.service.RootService
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModelFactory

class InitActivity : AppCompatActivity() {
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

    override fun onStart() {
        super.onStart()

        Intent(this, RootService::class.java).also {
                intent -> bindService(intent, connection, Context.BIND_AUTO_CREATE) }
    }

    override fun onStop() {
        super.onStop()

        unbindService(connection)
        bound = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)
    }
}