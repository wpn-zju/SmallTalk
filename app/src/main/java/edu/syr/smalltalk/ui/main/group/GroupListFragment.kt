package edu.syr.smalltalk.ui.main.group

import android.content.Context
import android.os.Bundle
import android.view.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
        recycler_view_group.layoutManager = LinearLayoutManager(context)
        recycler_view_group.adapter = adapter

        val userId: Int = PreferenceManager
            .getDefaultSharedPreferences(requireActivity().applicationContext)
            .getInt(KVPConstant.K_CURRENT_USER_ID, 0)

        viewModel.getCurrentUserInfo(userId).observe(requireActivity(), {  user ->
            if (user.isEmpty()) {
                if (serviceProvider.hasService()) {
                    serviceProvider.getService()!!.loadUser()
                }
            } else {
                for (groupId in user[0].groupList) {
                    viewModel.getCurrentGroup(groupId).observe(requireActivity(), { group ->
                        if (group.isEmpty()) {
                            if (serviceProvider.hasService()) {
                                serviceProvider.getService()!!.loadGroup(groupId)
                            }
                        }
                    })
                }
            }
        })

        viewModel.getGroupList(userId).observe(requireActivity(), { groupList ->
            groupList.let {
                adapter.submitList(it)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_groups, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_join_group -> {
                val action = MainFragmentDirections.groupListSearchGroup()
                requireView().findNavController().navigate(action)
            }
            R.id.navigation_create_group -> {
                val action = MainFragmentDirections.groupListCreateGroup()
                requireView().findNavController().navigate(action)
            }
        }
        return true
    }

    override fun onItemClickListener(view: View, groupId: Int) {
        val action = MainFragmentDirections.groupListViewGroup(groupId, true)
        requireView().findNavController().navigate(action)
    }

    override fun onItemLongClickListener(view: View, groupId: Int) {

    }
}
