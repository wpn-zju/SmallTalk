package edu.syr.smalltalk.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import edu.syr.smalltalk.ui.main.contactlist.ContactListFragment
import edu.syr.smalltalk.ui.main.messagelist.MessageListFragment

class PageAdapter(fragmentManager: FragmentActivity) : androidx.viewpager2.adapter.FragmentStateAdapter(fragmentManager) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(p0: Int): Fragment {
        var position = p0
        if (p0 < 0) position = 0
        if (p0 > itemCount) position = itemCount - 1

        return when (position) {
            0 -> MessageListFragment()
            1 -> ContactListFragment()
            2 -> ProfileFragment()
            else -> MessageListFragment()
        }
    }
}
