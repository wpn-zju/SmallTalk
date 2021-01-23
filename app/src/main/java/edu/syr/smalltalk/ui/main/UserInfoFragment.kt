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
import kotlinx.android.synthetic.main.fragment_user_info.*

class UserInfoFragment: Fragment() {
    private val args: UserInfoFragmentArgs by navArgs()

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
        return inflater.inflate(R.layout.fragment_user_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.watchCurrentUserInfo(args.userId).observe(viewLifecycleOwner) { user ->
            if (user.isNotEmpty()) {
                val currentUser = user[0]
                SmallTalkApplication.picasso(currentUser.userAvatarLink, user_avatar_preview)
                user_avatar_set.setOnClickListener {
                    val action = UserInfoFragmentDirections.userInfoUploadImage("user_avatar", args.userId)
                    requireView().findNavController().navigate(action)
                }

                user_name_preview.text = currentUser.userName
                user_name_set.setOnClickListener {
                    val action = UserInfoFragmentDirections.userInfoUploadText("user_name", args.userId)
                    requireView().findNavController().navigate(action)
                }

                user_password_preview.text = '*'.toString().repeat(currentUser.userPassword.length)
                user_password_set.setOnClickListener {
                    val action = UserInfoFragmentDirections.userInfoUploadText("user_password", args.userId)
                    requireView().findNavController().navigate(action)
                }

                user_gender_preview.text = when (currentUser.userGender) {
                    0 -> getString(R.string.gender_hide)
                    1 -> getString(R.string.gender_male)
                    2 -> getString(R.string.gender_female)
                    else -> getString(R.string.gender_others)
                }
                user_gender_set.setOnClickListener {
                    // TODO: MODIFY GENDER
                }

                user_info_preview.text = currentUser.userInfo
                user_info_set.setOnClickListener {
                    val action = UserInfoFragmentDirections.userInfoUploadText("user_info", args.userId)
                    requireView().findNavController().navigate(action)
                }

                user_location_preview.text = currentUser.userLocation
                user_location_set.setOnClickListener {
                    val action = UserInfoFragmentDirections.userInfoUploadText("user_location", args.userId)
                    requireView().findNavController().navigate(action)
                }
            } else {
                serviceProvider.getService()?.loadUser(args.userId)
            }
        }
    }
}