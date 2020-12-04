package edu.syr.smalltalk.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.ISmallTalkServiceProvider
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModelFactory
import kotlinx.android.synthetic.main.fragment_group_detail.*
import kotlinx.android.synthetic.main.layout_group_detail.*

class GroupDetailFragment : Fragment() {
    private val args: GroupDetailFragmentArgs by navArgs()

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
        return inflater.inflate(R.layout.fragment_group_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getCurrentGroup(args.groupId).observe(requireActivity(), { group ->
            if (group.isEmpty()) {
                image_group_avatar.setImageResource(R.mipmap.ic_launcher)
                text_group_name.text = "Loading..."
                text_group_description.text = "Loading..."
                group_enter_chat.visibility = View.GONE
                group_send_request.visibility = View.GONE
            } else {
                group_detail_toolbar.title = group[0].groupName
                image_group_avatar.setImageResource(R.mipmap.ic_launcher)
                text_group_name.text = group[0].groupName
                text_group_description.text = group[0].groupName
                if (args.isMember) {
                    group_enter_chat.visibility = View.VISIBLE
                    group_send_request.visibility = View.GONE
                    group_enter_chat.setOnClickListener {
                        val action = GroupDetailFragmentDirections.groupDetailEnterChat(group[0].groupId, true)
                        view.findNavController().navigate(action)
                    }
                    group_send_request.setOnClickListener {

                    }
                } else {
                    group_enter_chat.visibility = View.GONE
                    group_send_request.visibility = View.VISIBLE
                    group_enter_chat.setOnClickListener {

                    }
                    group_send_request.setOnClickListener {
                        if (serviceProvider.hasService()) {
                            serviceProvider.getService()!!.groupAddRequest(group[0].groupId)
                        }
                    }
                }
            }
        })
    }
}
