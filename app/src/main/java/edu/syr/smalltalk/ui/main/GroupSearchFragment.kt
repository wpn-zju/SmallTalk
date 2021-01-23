package edu.syr.smalltalk.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.ISmallTalkServiceProvider
import edu.syr.smalltalk.service.eventbus.SearchGroupSuccessEvent
import kotlinx.android.synthetic.main.fragment_group_search.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class GroupSearchFragment : Fragment() {
    private lateinit var serviceProvider: ISmallTalkServiceProvider

    override fun onStart() {
        super.onStart()

        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()

        EventBus.getDefault().unregister(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        serviceProvider = requireActivity() as ISmallTalkServiceProvider
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_group_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_search_group.setOnClickListener {
            serviceProvider.getService()?.loadGroup(input_search_group_id.text.toString().toInt())
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSearchGroupSuccessMessage(searchGroupSuccessEvent: SearchGroupSuccessEvent) {
        val action = GroupSearchFragmentDirections
            .groupSearchViewDetail(searchGroupSuccessEvent.groupId, false)
        requireView().findNavController().navigate(action)
    }
}
