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
import com.squareup.picasso.Picasso
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.ISmallTalkServiceProvider
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModelFactory
import kotlinx.android.synthetic.main.fragment_contact_detail.*
import kotlinx.android.synthetic.main.layout_contact_detail.*

class ContactDetailFragment : Fragment() {
    private val args: ContactDetailFragmentArgs by navArgs()

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
        return inflater.inflate(R.layout.fragment_contact_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.watchCurrentContact(args.contactId).observe(viewLifecycleOwner) { contact ->
            if (contact.isEmpty()) {
                serviceProvider.getService()?.loadContact(args.contactId)
            } else {
                val contactInfo = contact[0]
                Picasso.Builder(requireActivity()).listener { _, _, e -> e.printStackTrace() }.build()
                    .load(contactInfo.contactAvatarLink).error(R.mipmap.ic_smalltalk).into(contact_avatar)
                contact_detail_toolbar.title = contactInfo.contactName
                contact_name.text = contactInfo.contactName
                contact_email.text = contactInfo.contactEmail
                contact_about_content.text = contactInfo.contactInfo
                if (args.isContact) {
                    contact_enter_chat.visibility = View.VISIBLE
                    contact_send_request.visibility = View.GONE
                    contact_enter_chat.setOnClickListener {
                        val action = ContactDetailFragmentDirections.contactDetailEnterChat(contactInfo.contactId, false)
                        view.findNavController().navigate(action)
                    }
                    contact_send_request.setOnClickListener {

                    }
                } else {
                    contact_enter_chat.visibility = View.GONE
                    contact_send_request.visibility = View.VISIBLE
                    contact_enter_chat.setOnClickListener {

                    }
                    contact_send_request.setOnClickListener {
                        serviceProvider.getService()?.contactAddRequest(contactInfo.contactEmail)
                    }
                }
            }
        }
    }
}
