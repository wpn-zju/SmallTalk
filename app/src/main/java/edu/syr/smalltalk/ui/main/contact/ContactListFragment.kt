package edu.syr.smalltalk.ui.main.contact

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
import kotlinx.android.synthetic.main.fragment_contacts.*

class ContactListFragment: Fragment(), ContactListAdapter.ContactClickListener {
    private val adapter = ContactListAdapter()

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
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.setContactClickListener(this)
        recycler_view_contact.layoutManager = LinearLayoutManager(requireContext())
        recycler_view_contact.adapter = adapter

        SmallTalkApplication.getCurrentUserId(requireContext()).let { userId ->
            viewModel.watchCurrentUserInfo(userId).observe(viewLifecycleOwner) { user ->
                if (user.isNotEmpty()) {
                    for (contactId in user[0].contactList) {
                        viewModel.watchCurrentContact(contactId).observe(viewLifecycleOwner) { contact ->
                            if (contact.isEmpty()) {
                                serviceProvider.getService()?.loadContact(contactId)
                            }
                        }
                    }
                } else {
                    serviceProvider.getService()?.loadUser(userId)
                }
            }

            viewModel.watchContactList(userId).observe(viewLifecycleOwner) { contactList ->
                adapter.submitList(contactList)
            }
        }
    }

    override fun onItemClickListener(view: View, contactId: Int) {
        val action = MainFragmentDirections.contactListViewContact(contactId, true)
        requireView().findNavController().navigate(action)
    }

    override fun onItemLongClickListener(view: View, contactId: Int) {

    }

    override fun onDestroyView() {
        recycler_view_contact.adapter = null
        super.onDestroyView()
    }
}
