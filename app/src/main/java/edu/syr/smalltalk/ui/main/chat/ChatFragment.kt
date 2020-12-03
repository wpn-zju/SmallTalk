package edu.syr.smalltalk.ui.main.chat

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.ISmallTalkServiceProvider
import edu.syr.smalltalk.service.KVPConstant
import edu.syr.smalltalk.service.android.constant.ClientConstant
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModelFactory
import kotlinx.android.synthetic.main.fragment_chat.*

class ChatFragment : Fragment(), ChatMessageListAdapter.ChatMessageClickListener {
    private val args: ChatFragmentArgs by navArgs()

    private val adapter = ChatMessageListAdapter()

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
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.setChatMessageClickListener(this)
        val layoutManager = LinearLayoutManager(context)
        chat_message_list.layoutManager = layoutManager
        chat_message_list.adapter = adapter

        emoji.setOnClickListener {
            // Todo
        }

        more_options.setOnClickListener {
            if (more_options_bar.visibility == View.GONE) {
                more_options_bar.visibility = View.VISIBLE
            }
        }

        chat_message_list.setOnClickListener {
            if (more_options_bar.visibility == View.VISIBLE) {
                more_options_bar.visibility = View.GONE
            }
        }

        send_message.setOnClickListener {
            if (!input_text.text.isEmpty()) {
                if (serviceProvider.hasService()) {
                    if (args.isGroupChat) {
                        serviceProvider.getService()!!.messageForwardGroup(
                            getUserId(),
                            args.chatId,
                            input_text.text.toString(),
                            ClientConstant.CHAT_CONTENT_TYPE_TEXT)
                    } else {
                        serviceProvider.getService()!!.messageForward(
                            getUserId(),
                            args.chatId,
                            input_text.text.toString(),
                            ClientConstant.CHAT_CONTENT_TYPE_TEXT)
                    }
                }
                input_text.text.clear()
            }
        }

        // Todo
        more_options_image.setOnClickListener {

        }

        more_options_camera.setOnClickListener {

        }

        more_options_webrtc.setOnClickListener {

        }

        more_options_voice.setOnClickListener {

        }

        more_options_file.setOnClickListener {

        }

        more_options_position.setOnClickListener {

        }

        more_options_share.setOnClickListener {

        }

        viewModel.getCurrentMessageList(getUserId(), args.chatId).observe(requireActivity(), { chatMessageList ->
            chatMessageList.let {
                adapter.submitList(it)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_chat, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.navigation_share -> {
                // Todo
            }
            R.id.navigation_profile -> {
                if (args.isGroupChat) {
                    val action = ChatFragmentDirections.chatRoomViewGroup(args.chatId, true)
                    requireView().findNavController().navigate(action)
                } else {
                    val action = ChatFragmentDirections.chatRoomViewContact(args.chatId, true)
                    requireView().findNavController().navigate(action)
                }
            }
        }
        return true
    }

    override fun getChatId(): Int {
        return args.chatId
    }

    override fun getUserId(): Int {
        return PreferenceManager
            .getDefaultSharedPreferences(requireActivity().applicationContext)
            .getInt(KVPConstant.K_CURRENT_USER_ID, 0)
    }

    override fun onItemClickListener(view: View, messageId: Int) {

    }

    override fun onItemLongClickListener(view: View, messageId: Int) {

    }
}
