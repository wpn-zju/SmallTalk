package edu.syr.smalltalk.ui.bottom.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import edu.syr.smalltalk.R

class ContactsFragment : Fragment() {

    private lateinit var contactsViewModel: ContactsViewModel

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
    ): View? {
        contactsViewModel =
            ViewModelProviders.of(this).get(ContactsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_contacts, container, false)
//    val textView: TextView = root.findViewById(R.id.text_dashboard)
//    dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
//      textView.text = it
//    })
        return root
    }
}