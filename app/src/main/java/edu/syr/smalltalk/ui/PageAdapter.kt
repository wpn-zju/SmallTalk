package edu.syr.smalltalk.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import edu.syr.smalltalk.ui.bottom.contacts.ContactsFragment
import edu.syr.smalltalk.ui.bottom.message.MessageFragment
import edu.syr.smalltalk.ui.bottom.profile.ProfileFragment

class PageAdapter(fragmentManager: FragmentActivity) : androidx.viewpager2.adapter.FragmentStateAdapter(fragmentManager) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(p0: Int): Fragment {
        var position = p0
        if (p0 < 0) position = 0
        if (p0 > itemCount) position = itemCount - 1

        return when (position) {
            0 -> MessageFragment()
            1 -> ContactsFragment()
            2 -> ProfileFragment()
            else -> MessageFragment()
        }
    }
}