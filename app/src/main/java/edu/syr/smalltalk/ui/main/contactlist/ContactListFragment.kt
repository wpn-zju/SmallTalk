package edu.syr.smalltalk.ui.main.contactlist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.ISmallTalkServiceProvider
import edu.syr.smalltalk.service.KVPConstant
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModelFactory

class ContactListFragment: Fragment(), ContactListAdapter.ContactClickListener {
    private val adapter = ContactListAdapter()

    private val userId: Int = PreferenceManager
        .getDefaultSharedPreferences(requireActivity().applicationContext)
        .getInt(KVPConstant.K_CURRENT_USER_ID, 0)

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

        viewModel.getContactList(userId).observe(requireActivity(), { contactList ->
            contactList.let {
                adapter.submitList(it)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_new_contact -> {
                val action = ContactListFragmentDirections.contactListStartSearch()
                requireView().findNavController().navigate(action)
            }
        }
        return true
    }

    override fun onItemClickListener(view: View, contactId: Int) {
        val action = ContactListFragmentDirections.contactListViewContact(contactId, true)
        view.findNavController().navigate(action)
    }

    override fun onItemLongClickListener(view: View, contactId: Int) {
        TODO("Not yet implemented")
    }
}
