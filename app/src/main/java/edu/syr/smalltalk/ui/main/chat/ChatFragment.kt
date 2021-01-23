package edu.syr.smalltalk.ui.main.chat

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.ISmallTalkServiceProvider
import edu.syr.smalltalk.service.android.constant.ClientConstant
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModelFactory
import edu.syr.smalltalk.ui.file.FileSelectActivity
import edu.syr.smalltalk.ui.webrtc.VideoChatActivity
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onStart() {
        SmallTalkApplication.setCurrentChatId(requireContext(), args.chatId)
        super.onStart()
    }

    override fun onStop() {
        SmallTalkApplication.setCurrentChatId(requireContext(), 0)
        super.onStop()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ChatMessageListAdapter(requireContext(), viewLifecycleOwner, viewModel)
        adapter.setChatMessageClickListener(this)

        val layoutManager = LinearLayoutManager(requireContext())
        chat_message_list.layoutManager = layoutManager
        chat_message_list.adapter = adapter

        var init = false
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                chat_message_list.postDelayed({
                    if (layoutManager.findLastVisibleItemPosition() + 2 >= positionStart) {
                        if (init) {
                            init = true
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

        val userId = SmallTalkApplication.getCurrentUserId(requireContext())
        send_message.setOnClickListener {
            if (input_text.text.isNotEmpty()) {
                serviceProvider.getService()?.let { service ->
                    if (args.isGroupChat) {
                        service.messageForwardGroup(
                            userId,
                            args.chatId,
                            input_text.text.toString(),
                            ClientConstant.CHAT_CONTENT_TYPE_TEXT)
                    } else {
                        service.messageForward(
                            userId,
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
            Toast.makeText(requireContext(), getString(R.string.toast_not_supported_yet), Toast.LENGTH_LONG).show()
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
            Toast.makeText(requireContext(), getString(R.string.toast_not_supported_yet), Toast.LENGTH_LONG).show()
        }

        more_options_position.setOnClickListener {
            more_options_bar.visibility = View.GONE
            Toast.makeText(requireContext(), getString(R.string.toast_not_supported_yet), Toast.LENGTH_LONG).show()
        }

        more_options_share.setOnClickListener {
            more_options_bar.visibility = View.GONE
            Toast.makeText(requireContext(), getString(R.string.toast_not_supported_yet), Toast.LENGTH_LONG).show()
        }

        viewModel.watchCurrentMessageList(userId, args.chatId).observe(viewLifecycleOwner) { messageList ->
            adapter.submitList(messageList)
        }

        if (args.isGroupChat) {
            chat_toolbar?.let { bar ->
                bar.menu.clear()
                bar.inflateMenu(R.menu.menu_chat_group)
                bar.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.navigation_profile -> {
                            val action = ChatFragmentDirections.chatRoomViewGroup(args.chatId, true)
                            requireView().findNavController().navigate(action)
                            true
                        }
                        R.id.navigation_file_list -> {
                            val action = ChatFragmentDirections.chatRoomViewFileArchive(args.chatId, 0)
                            requireView().findNavController().navigate(action)
                            true
                        }
                        R.id.navigation_group_view_member_list -> {
                            // TODO: VIEW MEMBER LIST
                            true
                        }
                        R.id.navigation_group_settings -> {
                            val action = ChatFragmentDirections.chatRoomModifyGroupInfo(args.chatId)
                            requireView().findNavController().navigate(action)
                            true
                        }
                        R.id.navigation_share -> {
                            Toast.makeText(requireContext(), getString(R.string.toast_share_clicked), Toast.LENGTH_SHORT).show()
                            true
                        }
                        else -> false
                    }
                }
            }

            viewModel.watchCurrentGroup(args.chatId).observe(viewLifecycleOwner) { group ->
                if (group.isNotEmpty()) {
                    chat_toolbar?.let { bar ->
                        bar.title = group[0].groupName
                    }
                } else {
                    serviceProvider.getService()?.loadGroup(args.chatId)
                }
            }
        } else {
            chat_toolbar?.let { bar ->
                bar.menu.clear()
                bar.inflateMenu(R.menu.menu_chat_contact)
                bar.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.navigation_profile -> {
                            val action = ChatFragmentDirections.chatRoomViewContact(args.chatId, true)
                            requireView().findNavController().navigate(action)
                            true
                        }
                        R.id.navigation_file_list -> {
                            val action = if (args.chatId < userId) {
                                ChatFragmentDirections.chatRoomViewFileArchive(args.chatId, userId)
                            } else {
                                ChatFragmentDirections.chatRoomViewFileArchive(userId, args.chatId)
                            }
                            requireView().findNavController().navigate(action)
                            true
                        }
                        R.id.navigation_share -> {
                            Toast.makeText(requireContext(), getString(R.string.toast_share_clicked), Toast.LENGTH_SHORT).show()
                            true
                        }
                        else -> false
                    }
                }
            }

            viewModel.watchCurrentContact(args.chatId).observe(viewLifecycleOwner) { contact ->
                if (contact.isNotEmpty()) {
                    chat_toolbar?.let { bar ->
                        bar.title = contact[0].contactName
                    }
                } else {
                    serviceProvider.getService()?.loadContact(args.chatId)
                }
            }
        }

        GlobalScope.launch {
            viewModel.readMessage(userId, args.chatId)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putString("fragment", "Chat")
            putBoolean("isGroup", args.isGroupChat)
            putInt("chatId", args.chatId)
        }
        super.onSaveInstanceState(outState)
    }

    override fun openBrowser(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    override fun loadContact(contactId: Int) {
        serviceProvider.getService()?.loadContact(contactId)
    }

    override fun onDestroyView() {
        chat_message_list.adapter = null
        super.onDestroyView()
    }

    private fun getChannel(): String {
        return if (args.isGroupChat) {
            "G - %d".format(args.chatId)
        } else {
            SmallTalkApplication.getCurrentUserId(requireContext()).let { userId ->
                if (args.chatId < userId) {
                    "P - %d - %d".format(args.chatId, userId)
                } else {
                    "P - %d - %d".format(userId, args.chatId)
                }
            }
        }
    }
}
