package edu.syr.smalltalk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_bar.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = PageAdapter(this)
        view_pager.adapter = adapter
        view_pager.currentItem = 0

        view_pager.registerOnPageChangeCallback (object: ViewPager2.OnPageChangeCallback() {
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