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
import kotlinx.android.synthetic.main.fragment_group_info.*

class GroupInfoFragment: Fragment() {
    private val args: GroupInfoFragmentArgs by navArgs()

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
        return inflater.inflate(R.layout.fragment_group_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.watchCurrentGroup(args.groupId).observe(viewLifecycleOwner) { group ->
            if (group.isNotEmpty()) {
                val currentGroup = group[0]
                SmallTalkApplication.picasso(currentGroup.groupAvatarLink, group_avatar_preview)
                group_avatar_set.setOnClickListener {
                    val action = GroupInfoFragmentDirections.groupInfoUploadImage("group_avatar", args.groupId)
                    requireView().findNavController().navigate(action)
                }

                group_name_preview.text = currentGroup.groupName
                group_name_set.setOnClickListener {
                    val action = GroupInfoFragmentDirections.groupInfoUploadText("group_name", args.groupId)
                    requireView().findNavController().navigate(action)
                }

                group_info_preview.text = currentGroup.groupInfo
                group_info_set.setOnClickListener {
                    val action = GroupInfoFragmentDirections.groupInfoUploadText("group_info", args.groupId)
                    requireView().findNavController().navigate(action)
                }
            } else {
                serviceProvider.getService()?.loadGroup(args.groupId)
            }
        }
    }
}