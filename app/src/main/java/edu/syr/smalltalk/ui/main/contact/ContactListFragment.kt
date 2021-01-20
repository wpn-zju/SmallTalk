package edu.syr.smalltalk.ui.main.contact

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
        recycler_view_contact.layoutManager = LinearLayoutManager(context)
        recycler_view_contact.adapter = adapter

        val userId: Int = PreferenceManager
            .getDefaultSharedPreferences(requireActivity().applicationContext)
            .getInt(KVPConstant.K_CURRENT_USER_ID, 0)

        viewModel.watchCurrentUserInfo(userId).observe(viewLifecycleOwner) { user ->
            if (user.isEmpty()) {
                serviceProvider.getService()?.loadUser(userId)
            } else {
                for (contactId in user[0].contactList) {
                    viewModel.watchCurrentContact(contactId).observe(viewLifecycleOwner) { contact ->
                        if (contact.isEmpty()) {
                            serviceProvider.getService()?.loadContact(contactId)
                        }
                    }
                }
            }
        }

        viewModel.watchContactList(userId).observe(viewLifecycleOwner) { contactList ->
            contactList.let {
                adapter.submitList(it)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_contacts, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_new_contact -> {
                val action = MainFragmentDirections.contactListSearchContact()
                requireView().findNavController().navigate(action)
            }
        }
        return true
    }

    override fun onItemClickListener(view: View, contactId: Int) {
        val action = MainFragmentDirections.contactListViewContact(contactId, true)
        requireView().findNavController().navigate(action)
    }

    override fun onItemLongClickListener(view: View, contactId: Int) {

    }
}
