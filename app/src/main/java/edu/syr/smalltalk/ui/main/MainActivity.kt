package edu.syr.smalltalk.ui.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.ISmallTalkService
import edu.syr.smalltalk.service.ISmallTalkServiceProvider
import edu.syr.smalltalk.service.RootService
import edu.syr.smalltalk.service.eventbus.AlertDialogEvent
import edu.syr.smalltalk.service.eventbus.SignOutEvent
import edu.syr.smalltalk.service.eventbus.ToastEvent
import edu.syr.smalltalk.service.model.entity.SmallTalkContact
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModelFactory
import edu.syr.smalltalk.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_bottom_bar.*
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

            // Use LiveData in Activity
            val contactList = Observer<List<SmallTalkContact>> { cList ->
                if (cList == null) {
                    Log.v("T", "Null")
                } else {
                    Log.v("T", "Contact List Updated - $cList")
                }
            }
            viewModel.contactList.observe(this@MainActivity, contactList)
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

    // Activity Logic
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = PageAdapter(this)
        view_pager.adapter = adapter
        view_pager.currentItem = 0

        view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(p0: Int) {
                var position = p0
                if (p0 < 0) position = 0
                if (p0 > 3) position = 2
                when (position) {
                    0 -> nav_view.selectedItemId = R.id.navigation_message
                    1 -> nav_view.selectedItemId = R.id.navigation_contacts
                    2 -> nav_view.selectedItemId = R.id.navigation_about_me
                    else -> nav_view.selectedItemId = R.id.navigation_message
                }

                super.onPageSelected(position)
            }
        })

        nav_view.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_message -> {
                    main_toolbar.setTitle(R.string.bottom_bar_message)
                    main_toolbar.menu.clear()
                    main_toolbar.inflateMenu(R.menu.menu_recent_message)
                    view_pager.currentItem = 0
                }
                R.id.navigation_contacts -> {
                    main_toolbar.setTitle(R.string.bottom_bar_contacts)
                    main_toolbar.menu.clear()
                    main_toolbar.inflateMenu(R.menu.menu_contacts)
                    view_pager.currentItem = 1
                }
                R.id.navigation_about_me -> {
                    main_toolbar.setTitle(R.string.bottom_bar_about_me)
                    main_toolbar.menu.clear()
                    main_toolbar.inflateMenu(R.menu.menu_profile)
                    view_pager.currentItem = 2
                }
                else -> {
                    main_toolbar.setTitle(R.string.bottom_bar_message)
                    main_toolbar.menu.clear()
                    main_toolbar.inflateMenu(R.menu.menu_recent_message)
                    view_pager.currentItem = 0
                }
            }
            true
        }

        nav_view.selectedItemId = R.id.navigation_message
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSignOutMessage(signOutEvent: SignOutEvent) {
        logout()
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

    private fun logout() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}