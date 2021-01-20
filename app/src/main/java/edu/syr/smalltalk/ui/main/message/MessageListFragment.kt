package edu.syr.smalltalk.ui.main.message

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.ISmallTalkServiceProvider
import edu.syr.smalltalk.service.KVPConstant
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recent_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MessageListAdapter(requireActivity(), viewLifecycleOwner, viewModel)
        adapter.setMessageClickListener(this)
        recycler_view_message.layoutManager = LinearLayoutManager(context)
        recycler_view_message.adapter = adapter

        val userId: Int = PreferenceManager
            .getDefaultSharedPreferences(requireActivity().applicationContext)
            .getInt(KVPConstant.K_CURRENT_USER_ID, 0)

        viewModel.watchRecentMessageList(userId).observe(viewLifecycleOwner) { messageList ->
            messageList.let {
                adapter.submitList(it)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_recent_message, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_mark_as_read -> {
                Toast.makeText(requireContext(), getString(R.string.toast_mark_as_read_clicked), Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClickListener(view: View, chatId: Int) {
        val userId: Int = PreferenceManager
            .getDefaultSharedPreferences(requireActivity().applicationContext)
            .getInt(KVPConstant.K_CURRENT_USER_ID, 0)

        viewModel.watchCurrentUserInfo(userId).observe(viewLifecycleOwner) { user ->
            if (user.isEmpty()) {
                serviceProvider.getService()?.loadUser(userId)
            } else {
                if (user[0].contactList.contains(chatId)) {
                    val action = MainFragmentDirections.recentMessageListEnterChat(chatId, false)
                    requireView().findNavController().navigate(action)
                } else if (user[0].groupList.contains(chatId)) {
                    val action = MainFragmentDirections.recentMessageListEnterChat(chatId, true)
                    requireView().findNavController().navigate(action)
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
}
