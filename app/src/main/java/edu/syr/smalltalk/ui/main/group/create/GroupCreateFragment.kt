package edu.syr.smalltalk.ui.main.group.create

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
import kotlinx.android.synthetic.main.fragment_group_create.*
import java.util.stream.Collectors

class GroupCreateFragment : Fragment(), GroupCreateListAdapter.GroupCreateContactListener {
    private val checkedMap = mutableMapOf<Int, Boolean>()

    private val adapter = GroupCreateListAdapter()

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
        return inflater.inflate(R.layout.fragment_group_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.setGroupCreateListener(this)
        recycler_view_group_create_list.layoutManager = LinearLayoutManager(requireContext())
        recycler_view_group_create_list.adapter = adapter

        viewModel.watchContactList(SmallTalkApplication.getCurrentUserId(requireContext()))
            .observe(viewLifecycleOwner) { contactList ->
                adapter.submitList(contactList.stream()
                    .map { contact ->
                        Pair(contact, checkedMap.getOrDefault(contact.contactId, false))
                    }.collect(Collectors.toList()))
            }

        create_group_btn_create.setOnClickListener {
            serviceProvider.getService()?.groupCreateRequest("New Group", checkedMap.toList().stream()
                .filter { kvp -> kvp.second }
                .map { kvp -> kvp.first }
                .collect(Collectors.toList()).toString())
            val action = GroupCreateFragmentDirections.groupCreateReturnMain()
            requireView().findNavController().navigate(action)
        }
    }

    override fun onItemChecked(view: View, contactId: Int, isChecked: Boolean) {
        checkedMap[contactId] = isChecked
    }

    override fun onDestroyView() {
        recycler_view_group_create_list.adapter = null
        super.onDestroyView()
    }
}
