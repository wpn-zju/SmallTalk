package edu.syr.smalltalk.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.ISmallTalkServiceProvider
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModelFactory
import kotlinx.android.synthetic.main.layout_contact_detail.*

class ProfileFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_about_me, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contact_enter_chat.visibility = View.GONE
        contact_send_request.visibility = View.GONE

        SmallTalkApplication.getCurrentUserId(requireContext()).let { userId ->
            viewModel.watchCurrentUserInfo(userId).observe(viewLifecycleOwner) { user ->
                if (user.isNotEmpty()) {
                    val userInfo = user[0]
                    SmallTalkApplication.picasso(userInfo.userAvatarLink, contact_avatar)
                    contact_name.text = userInfo.userName
                    contact_id.text = userInfo.userId.toString()
                    contact_email.text = userInfo.userEmail
                    contact_about_content.text = userInfo.userInfo
                    contact_location_content.text = userInfo.userLocation
                } else {
                    serviceProvider.getService()?.loadUser(userId)
                }
            }
        }
    }
}
