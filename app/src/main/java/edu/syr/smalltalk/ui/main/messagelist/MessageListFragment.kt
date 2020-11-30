package edu.syr.smalltalk.ui.main.messagelist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.ISmallTalkServiceProvider
import edu.syr.smalltalk.service.KVPConstant
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModelFactory

class MessageListFragment: Fragment(), MessageListAdapter.MessageClickListener {
    private val adapter = MessageListAdapter()

    private val viewModel: SmallTalkViewModel by viewModels {
        SmallTalkViewModelFactory(requireContext().applicationContext as SmallTalkApplication)
    }

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

        adapter.setMessageClickListener(this)

        val userId: Int = PreferenceManager
            .getDefaultSharedPreferences(requireActivity().applicationContext)
            .getInt(KVPConstant.K_CURRENT_USER_ID, 0)

        viewModel.getRecentMessageList(userId).observe(requireActivity(), { messageList ->
            messageList.let {
                adapter.submitList(it)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_clear -> {
                Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show()
            }
        }
        return true
    }

    override fun onItemClickListener(view: View, chatId: Int) {
        val action = MessageListFragmentDirections.recentMessageListEnterChat(chatId)
        view.findNavController().navigate(action)
    }

    override fun onItemLongClickListener(view: View, chatId: Int) {
        TODO("Not yet implemented")
    }
}
