package edu.syr.smalltalk.ui.main.message

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.ISmallTalkServiceProvider
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModelFactory
import edu.syr.smalltalk.ui.main.MainFragmentDirections
import kotlinx.android.synthetic.main.fragment_recent_message.*

class MessageListFragment: Fragment(), MessageListAdapter.MessageClickListener {
    private val viewModel: SmallTalkViewModel by viewModels {
        SmallTalkViewModelFactory(requireContext().applicationContext as SmallTalkApplication)
    }

    private lateinit var adapter: MessageListAdapter
    private lateinit var serviceProvider: ISmallTalkServiceProvider

    override fun onAttach(context: Context) {
        super.onAttach(context)

        serviceProvider = requireActivity() as ISmallTalkServiceProvider
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recent_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MessageListAdapter(requireContext(), viewLifecycleOwner, viewModel)
        adapter.setMessageClickListener(this)
        recycler_view_message.layoutManager = LinearLayoutManager(requireContext())
        recycler_view_message.adapter = adapter

        viewModel.watchRecentMessageList(SmallTalkApplication.getCurrentUserId(requireContext()))
            .observe(viewLifecycleOwner) { messageList ->
                adapter.submitList(messageList)
            }
    }

    override fun onItemClickListener(view: View, chatId: Int) {
        SmallTalkApplication.getCurrentUserId(requireContext()).let { userId ->
            viewModel.watchCurrentUserInfo(userId).observe(viewLifecycleOwner) { user ->
                if (user.isNotEmpty()) {
                    if (user[0].contactList.contains(chatId)) {
                        val action = MainFragmentDirections.recentMessageListEnterChat(chatId, false)
                        requireView().findNavController().navigate(action)
                    } else if (user[0].groupList.contains(chatId)) {
                        val action = MainFragmentDirections.recentMessageListEnterChat(chatId, true)
                        requireView().findNavController().navigate(action)
                    }
                } else {
                    serviceProvider.getService()?.loadUser(userId)
                }
            }
        }
    }

    override fun onItemLongClickListener(view: View, chatId: Int) {

    }

    override fun loadContact(contactId: Int) {
        serviceProvider.getService()?.loadContact(contactId)
    }

    override fun loadGroup(groupId: Int) {
        serviceProvider.getService()?.loadGroup(groupId)
    }

    override fun onDestroyView() {
        recycler_view_message.adapter = null
        super.onDestroyView()
    }
}
