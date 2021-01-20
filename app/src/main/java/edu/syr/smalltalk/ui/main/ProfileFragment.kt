package edu.syr.smalltalk.ui.main

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.squareup.picasso.Picasso
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.ISmallTalkServiceProvider
import edu.syr.smalltalk.service.KVPConstant
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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

        val userId: Int = PreferenceManager
            .getDefaultSharedPreferences(requireActivity().applicationContext)
            .getInt(KVPConstant.K_CURRENT_USER_ID, 0)

        viewModel.watchCurrentUserInfo(userId).observe(viewLifecycleOwner) { user ->
            if (user.isEmpty()) {
                serviceProvider.getService()?.loadUser(userId)
            } else {
                val userInfo = user[0]
                Picasso.Builder(requireActivity()).listener { _, _, e -> e.printStackTrace() }.build()
                    .load(userInfo.userAvatarLink).error(R.mipmap.ic_smalltalk).into(contact_avatar)
                contact_name.text = userInfo.userName
                contact_email.text = userInfo.userEmail
                contact_about_content.text = userInfo.userInfo
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_profile, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_settings -> {
                val userId = PreferenceManager.getDefaultSharedPreferences(requireActivity())
                    .getInt(KVPConstant.K_CURRENT_USER_ID, 0)
                val action = MainFragmentDirections.profileModifyInfo(userId)
                requireView().findNavController().navigate(action)
            }
            R.id.navigation_view_request -> {
                val action = MainFragmentDirections.profileViewRequest()
                requireView().findNavController().navigate(action)
            }
            R.id.navigation_share_me -> {
                Toast.makeText(requireContext(), getString(R.string.toast_share_clicked), Toast.LENGTH_SHORT).show()
            }
            R.id.navigation_sign_out -> {
                serviceProvider.getService()?.userSessionSignOut()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
