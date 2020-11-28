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
import edu.syr.smalltalk.service.model.entity.SmallTalkContact
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_bottom_bar.*
import java.time.Instant
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    private lateinit var service: ISmallTalkService
    private var bound: Boolean = false
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            service = (binder as RootService.RootServiceBinder).getService()
            service.setDataAccessor((application as SmallTalkApplication).repository.getDataAccessor())
            bound = true

            // Use LiveData in Activity
            val contactList = Observer<List<SmallTalkContact>> { cList ->
                if (cList == null) {
                    Log.v("T", "Null")
                } else {
                    Log.v("T", cList.toString())
                }
            }
            viewModel.contactList.observe(this@MainActivity, contactList)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            bound = false
        }
    }

    private val viewModel: SmallTalkViewModel by viewModels {
        SmallTalkViewModelFactory(application as SmallTalkApplication)
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

                // Test
                if(this@MainActivity::service.isInitialized) {
                    val random: Int = Random(Instant.now().toEpochMilli()).nextInt()
                    Log.v("Send Test Request", "With Payload - $random")
                    service.testSend(random)
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