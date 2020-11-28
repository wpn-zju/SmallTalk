package edu.syr.smalltalk.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.ISmallTalkService
import edu.syr.smalltalk.service.RootService
import edu.syr.smalltalk.service.model.entity.SmallTalkUser
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_bottom_bar.*


class MainActivity : AppCompatActivity() {
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

        Intent(this, RootService::class.java).also { intent -> bindService(
            intent,
            connection,
            Context.BIND_AUTO_CREATE
        ) }
    }

    override fun onStop() {
        super.onStop()

        unbindService(connection)
        bound = false
    }

    // Activity Logic
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Use LiveData in Activity
        val userInfo = Observer<SmallTalkUser> { it ->
            Log.v("T", it.userName!!)
        }
        viewModel.currentUserInfo.observe(this, userInfo)

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
}