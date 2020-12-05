package edu.syr.smalltalk.ui.main.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
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
import edu.syr.smalltalk.ui.file.FileSelectActivity
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
        layoutManager.stackFromEnd = true
        chat_message_list.layoutManager = layoutManager
        chat_message_list.adapter = adapter

        more_options.setOnClickListener {
            if (more_options_bar.visibility == View.GONE) {
                more_options_bar.visibility = View.VISIBLE
            } else {
                more_options_bar.visibility = View.GONE
            }
        }

        send_message.setOnClickListener {
            if (input_text.text.isNotEmpty()) {
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

        if (args.isGroupChat) {
            more_options_webrtc.visibility = View.GONE
        }

        more_options_image.setOnClickListener {
            more_options_bar.visibility = View.GONE

            val intent = Intent(requireActivity(), FileSelectActivity::class.java)
            intent.putExtra("command", "image")
            intent.putExtra("chatId", args.chatId)
            intent.putExtra("isGroup", args.isGroupChat)
            startActivity(intent)
        }

        more_options_camera.setOnClickListener {
            more_options_bar.visibility = View.GONE

        }

        more_options_webrtc.setOnClickListener {
            more_options_bar.visibility = View.GONE

        }

        more_options_voice.setOnClickListener {
            more_options_bar.visibility = View.GONE

        }

        more_options_file.setOnClickListener {
            more_options_bar.visibility = View.GONE

            val intent = Intent(requireActivity(), FileSelectActivity::class.java)
            intent.putExtra("command", "file")
            intent.putExtra("chatId", args.chatId)
            intent.putExtra("isGroup", args.isGroupChat)
            startActivity(intent)
        }

        more_options_position.setOnClickListener {
            more_options_bar.visibility = View.GONE

        }

        more_options_share.setOnClickListener {
            more_options_bar.visibility = View.GONE

        }

        viewModel.getCurrentMessageList(getUserId(), args.chatId).observe(viewLifecycleOwner) { chatMessageList ->
            chatMessageList.let {
                adapter.submitList(it)
            }
        }

        if (args.isGroupChat) {
            viewModel.getCurrentGroup(args.chatId).observe(viewLifecycleOwner) { group ->
                if (group.isNotEmpty()) {
                    chat_toolbar?.title = group[0].groupName
                }
            }
        } else {
            viewModel.getCurrentContact(args.chatId).observe(viewLifecycleOwner) { contact ->
                if (contact.isNotEmpty()) {
                    chat_toolbar?.title = contact[0].contactName
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_chat, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.navigation_share -> {
                Toast.makeText(requireContext(), "Share Clicked", Toast.LENGTH_SHORT).show()
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
