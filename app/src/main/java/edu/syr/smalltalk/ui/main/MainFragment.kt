package edu.syr.smalltalk.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.ISmallTalkServiceProvider
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModelFactory
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {
    private val viewModel: SmallTalkViewModel by viewModels {
        SmallTalkViewModelFactory(requireContext().applicationContext as SmallTalkApplication)
    }

    private lateinit var serviceProvider: ISmallTalkServiceProvider

    override fun onAttach(context: Context) {
        super.onAttach(context)

        serviceProvider = requireActivity() as ISmallTalkServiceProvider
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(main_toolbar)

        val adapter = PageAdapter(requireActivity())
        view_pager.adapter = adapter
        view_pager.currentItem = 0
        view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(p0: Int) {
                var position = p0
                if (p0 < 0) position = 0
                if (p0 >= adapter.itemCount) position = adapter.itemCount - 1
                when (position) {
                    0 -> bottom_nav.selectedItemId = R.id.navigation_message
                    1 -> bottom_nav.selectedItemId = R.id.navigation_contacts
                    2 -> bottom_nav.selectedItemId = R.id.navigation_groups
                    3 -> bottom_nav.selectedItemId = R.id.navigation_about_me
                    else -> bottom_nav.selectedItemId = R.id.navigation_message
                }
                super.onPageSelected(position)
            }
        })

        bottom_nav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_message -> {
                    main_toolbar.setTitle(R.string.bottom_bar_message_list)
                    main_toolbar.menu.clear()
                    main_toolbar.inflateMenu(R.menu.menu_recent_message)
                    view_pager.currentItem = 0
                    bottom_nav.visibility = View.VISIBLE
                }
                R.id.navigation_contacts -> {
                    main_toolbar.setTitle(R.string.bottom_bar_contact_list)
                    main_toolbar.menu.clear()
                    main_toolbar.inflateMenu(R.menu.menu_contacts)
                    view_pager.currentItem = 1
                    bottom_nav.visibility = View.VISIBLE
                }
                R.id.navigation_groups -> {
                    main_toolbar.setTitle(R.string.bottom_bar_group_list)
                    main_toolbar.menu.clear()
                    main_toolbar.inflateMenu(R.menu.menu_groups)
                    view_pager.currentItem = 2
                    bottom_nav.visibility = View.VISIBLE
                }
                R.id.navigation_about_me -> {
                    main_toolbar.setTitle(R.string.bottom_bar_about)
                    main_toolbar.menu.clear()
                    main_toolbar.inflateMenu(R.menu.menu_profile)
                    view_pager.currentItem = 3
                    bottom_nav.visibility = View.VISIBLE
                }
                else -> {
                    main_toolbar.setTitle(R.string.bottom_bar_message_list)
                    main_toolbar.menu.clear()
                    main_toolbar.inflateMenu(R.menu.menu_recent_message)
                    view_pager.currentItem = 0
                    bottom_nav.visibility = View.VISIBLE
                }
            }
            true
        }

        bottom_nav.selectedItemId = R.id.navigation_message
    }
}
