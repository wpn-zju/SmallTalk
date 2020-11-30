package edu.syr.smalltalk.ui.main.chat

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.ISmallTalkServiceProvider
import edu.syr.smalltalk.service.KVPConstant
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModelFactory

class ChatFragment : Fragment(), ChatMessageListAdapter.ChatMessageClickListener {
    private val args: ChatFragmentArgs by navArgs()

    private val userId: Int = PreferenceManager
        .getDefaultSharedPreferences(requireActivity().applicationContext)
        .getInt(KVPConstant.K_CURRENT_USER_ID, 0)

    private val adapter = ChatMessageListAdapter()

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
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.setChatMessageClickListener(this)

        viewModel.getCurrentMessageList(userId, args.chatId).observe(requireActivity(), { chatMessageList ->
            chatMessageList.let {
                adapter.submitList(it)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.navigation_share -> {
                // Todo
            }
            R.id.navigation_profile -> {
                // Todo
            }
        }
        return true
    }

    override fun getChatId(): Int {
        return args.chatId
    }

    override fun getUserId(): Int {
        return userId
    }

    override fun onItemClickListener(view: View, messageId: Int) {
        TODO("Not yet implemented")
    }

    override fun onItemLongClickListener(view: View, messageId: Int) {
        TODO("Not yet implemented")
    }
}
