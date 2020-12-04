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

        viewModel.getCurrentContact(args.contactId).observe(requireActivity(), { contact ->
            if (contact.isEmpty()) {
                image_contact_avatar.setImageResource(R.mipmap.ic_launcher)
                text_contact_name.text = "Loading..."
                text_contact_email.text = "Loading..."
                text_what_s_up_label.text = "Loading..."
                text_what_s_up.text = "Loading..."
                contact_enter_chat.visibility = View.GONE
                contact_send_request.visibility = View.GONE
            } else {
                contact_detail_toolbar.title = contact[0].contactName
                image_contact_avatar.setImageResource(R.mipmap.ic_launcher)
                text_contact_name.text = contact[0].contactName
                text_contact_email.text = contact[0].contactEmail
                text_what_s_up_label.text = "What's Up"
                text_what_s_up.text = "Loading..."
                if (args.isContact) {
                    contact_enter_chat.visibility = View.VISIBLE
                    contact_send_request.visibility = View.GONE
                    contact_enter_chat.setOnClickListener {
                        val action = ContactDetailFragmentDirections.contactDetailEnterChat(contact[0].contactId, false)
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
                        if (serviceProvider.hasService()) {
                            serviceProvider.getService()!!.contactAddRequest(contact[0].contactEmail)
                        }
                    }
                }
            }
        })
    }
}
