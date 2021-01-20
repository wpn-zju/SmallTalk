package edu.syr.smalltalk.ui.main.chat

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.ISmallTalkServiceProvider
import edu.syr.smalltalk.service.KVPConstant
import edu.syr.smalltalk.service.android.constant.ClientConstant
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModelFactory
import edu.syr.smalltalk.ui.file.FileSelectActivity
import edu.syr.smalltalk.ui.webrtc.VideoChatActivity
import kotlinx.android.synthetic.main.fragment_chat.*

class ChatFragment : Fragment(), ChatMessageListAdapter.ChatMessageClickListener {
    private val args: ChatFragmentArgs by navArgs()

    private val viewModel: SmallTalkViewModel by viewModels {
        SmallTalkViewModelFactory(requireContext().applicationContext as SmallTalkApplication)
    }

    private lateinit var adapter: ChatMessageListAdapter
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

        adapter = ChatMessageListAdapter(requireActivity(), viewLifecycleOwner, viewModel)
        adapter.setChatMessageClickListener(this)

        val layoutManager = LinearLayoutManager(context)
        chat_message_list.layoutManager = layoutManager
        chat_message_list.adapter = adapter

        var inited = false
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                chat_message_list.postDelayed({
                    if (layoutManager.findLastVisibleItemPosition() + 2 >= positionStart) {
                        if (inited) {
                            inited = true
                            chat_message_list.scrollToPosition(positionStart + itemCount)
                        } else {
                            chat_message_list.smoothScrollToPosition(positionStart + itemCount)
                        }
                    }
                }, 100)
            }
        })

        more_options.setOnClickListener {
            if (more_options_bar.visibility == View.GONE) {
                more_options_bar.visibility = View.VISIBLE
            } else {
                more_options_bar.visibility = View.GONE
            }
        }

        send_message.setOnClickListener {
            if (input_text.text.isNotEmpty()) {
                serviceProvider.getService()?.let { service ->
                    if (args.isGroupChat) {
                        service.messageForwardGroup(
                            getUserId(),
                            args.chatId,
                            input_text.text.toString(),
                            ClientConstant.CHAT_CONTENT_TYPE_TEXT)
                    } else {
                        service.messageForward(
                            getUserId(),
                            args.chatId,
                            input_text.text.toString(),
                            ClientConstant.CHAT_CONTENT_TYPE_TEXT)
                    }
                }
                input_text.text.clear()
            }
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
            Toast.makeText(requireActivity(), getString(R.string.toast_not_supported_yet), Toast.LENGTH_LONG).show()
        }

        more_options_webrtc.setOnClickListener {
            more_options_bar.visibility = View.GONE
            val intent = Intent(requireActivity(), VideoChatActivity::class.java)
            intent.putExtra("channel", getChannel())
            startActivity(intent)
        }

        more_options_file.setOnClickListener {
            more_options_bar.visibility = View.GONE
            val intent = Intent(requireActivity(), FileSelectActivity::class.java)
            intent.putExtra("command", "file")
            intent.putExtra("chatId", args.chatId)
            intent.putExtra("isGroup", args.isGroupChat)
            startActivity(intent)
        }

        more_options_voice.setOnClickListener {
            more_options_bar.visibility = View.GONE
            Toast.makeText(requireActivity(), getString(R.string.toast_not_supported_yet), Toast.LENGTH_LONG).show()
        }

        more_options_position.setOnClickListener {
            more_options_bar.visibility = View.GONE
            Toast.makeText(requireActivity(), getString(R.string.toast_not_supported_yet), Toast.LENGTH_LONG).show()
        }

        more_options_share.setOnClickListener {
            more_options_bar.visibility = View.GONE
            Toast.makeText(requireActivity(), getString(R.string.toast_not_supported_yet), Toast.LENGTH_LONG).show()
        }

        viewModel.watchCurrentMessageList(getUserId(), args.chatId).observe(viewLifecycleOwner) { chatMessageList ->
            chatMessageList.let {
                adapter.submitList(it) { }
            }
        }

        if (args.isGroupChat) {
            viewModel.watchCurrentGroup(args.chatId).observe(viewLifecycleOwner) { group ->
                if (group.isNotEmpty()) {
                    chat_toolbar?.title = group[0].groupName
                }
            }
        } else {
            viewModel.watchCurrentContact(args.chatId).observe(viewLifecycleOwner) { contact ->
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
                Toast.makeText(requireContext(), getString(R.string.toast_share_clicked), Toast.LENGTH_SHORT).show()
            }
            R.id.navigation_file_list -> {
                val action = if (args.isGroupChat) {
                    ChatFragmentDirections.chatRoomViewFileArchive(args.chatId, 0)
                } else {
                    if (args.chatId < getUserId()) {
                        ChatFragmentDirections.chatRoomViewFileArchive(args.chatId, getUserId())
                    } else {
                        ChatFragmentDirections.chatRoomViewFileArchive(getUserId(), args.chatId)
                    }
                }
                requireView().findNavController().navigate(action)
            }
            R.id.navigation_profile -> {
                if (args.isGroupChat) {
                    val action = ChatFragmentDirections.chatRoomModifyGroupInfo(args.chatId)
                    requireView().findNavController().navigate(action)
                } else {
                    val action = ChatFragmentDirections.chatRoomViewContact(args.chatId, true)
                    requireView().findNavController().navigate(action)
                }
            }
        }
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putString("fragment", "Chat")
            putBoolean("isGroup", args.isGroupChat)
            putInt("chatId", args.chatId)
        }
        super.onSaveInstanceState(outState)
    }

    override fun getChatId(): Int {
        return args.chatId
    }

    override fun getUserId(): Int {
        return PreferenceManager
            .getDefaultSharedPreferences(requireActivity().applicationContext)
            .getInt(KVPConstant.K_CURRENT_USER_ID, 0)
    }

    override fun openBrowser(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    override fun loadContact(contactId: Int) {
        serviceProvider.getService()?.loadContact(contactId)
    }

    private fun getChannel(): String {
        return if (args.isGroupChat) {
            "G - %d".format(getChatId())
        } else {
            if (getChatId() < getUserId()) {
                "P - %d - %d".format(getChatId(), getUserId())
            } else {
                "P - %d - %d".format(getUserId(), getChatId())
            }
        }
    }
}
