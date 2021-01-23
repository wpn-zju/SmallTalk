package edu.syr.smalltalk.ui.main.group

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
import kotlinx.android.synthetic.main.fragment_groups.*

class GroupListFragment: Fragment(), GroupListAdapter.GroupClickListener {
    private val adapter = GroupListAdapter()

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
        return inflater.inflate(R.layout.fragment_groups, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.setGroupClickListener(this)
        recycler_view_group.layoutManager = LinearLayoutManager(requireContext())
        recycler_view_group.adapter = adapter

        SmallTalkApplication.getCurrentUserId(requireContext()).let { userId ->
            viewModel.watchCurrentUserInfo(userId).observe(viewLifecycleOwner) { user ->
                if (user.isNotEmpty()) {
                    for (groupId in user[0].groupList) {
                        viewModel.watchCurrentGroup(groupId).observe(viewLifecycleOwner) { group ->
                            if (group.isEmpty()) {
                                serviceProvider.getService()?.loadGroup(groupId)
                            }
                        }
                    }
                } else {
                    serviceProvider.getService()?.loadUser(userId)
                }
            }

            viewModel.watchGroupList(userId).observe(viewLifecycleOwner) { groupList ->
                adapter.submitList(groupList)
            }
        }
    }

    override fun onItemClickListener(view: View, groupId: Int) {
        val action = MainFragmentDirections.groupListViewGroup(groupId, true)
        requireView().findNavController().navigate(action)
    }

    override fun onItemLongClickListener(view: View, groupId: Int) {

    }

    override fun onDestroyView() {
        recycler_view_group.adapter = null
        super.onDestroyView()
    }
}
