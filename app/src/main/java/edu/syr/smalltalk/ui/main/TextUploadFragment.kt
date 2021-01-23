package edu.syr.smalltalk.ui.main

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.ISmallTalkServiceProvider
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModelFactory
import kotlinx.android.synthetic.main.fragment_text_upload.*

class TextUploadFragment: Fragment() {
    private val args: TextUploadFragmentArgs by navArgs()

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
        return inflater.inflate(R.layout.fragment_text_upload, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (args.uploadType) {
            "user_name" -> {
                viewModel.watchCurrentUserInfo(args.uploadId).observe(viewLifecycleOwner) { user ->
                    if (user.isNotEmpty()) {
                        val userInfo = user[0]
                        text_upload_input.hint = getString(R.string.hint_new_nickname)
                        text_upload_input.setText(userInfo.userName)
                        text_upload_input.inputType = InputType.TYPE_CLASS_TEXT
                        text_upload_btn.setOnClickListener {
                            serviceProvider.getService()?.userModifyInfo(
                                userId = args.uploadId,
                                userName = text_upload_input.text.toString(),
                                userPassword = null,
                                userGender = null,
                                userAvatarLink = null,
                                userInfo = null,
                                userLocation = null
                            )
                            requireActivity().onBackPressed()
                        }
                    } else {
                        serviceProvider.getService()?.loadUser(args.uploadId)
                    }
                }
            }
            "user_password" -> {
                viewModel.watchCurrentUserInfo(args.uploadId).observe(viewLifecycleOwner) { user ->
                    if (user.isNotEmpty()) {
                        text_upload_input.hint = getString(R.string.hint_new_password)
                        text_upload_input.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                        text_upload_btn.setOnClickListener {
                            serviceProvider.getService()?.userModifyInfo(
                                userId = args.uploadId,
                                userName = null,
                                userPassword = text_upload_input.text.toString(),
                                userGender = null,
                                userAvatarLink = null,
                                userInfo = null,
                                userLocation = null
                            )
                            requireActivity().onBackPressed()
                        }
                    } else {
                        serviceProvider.getService()?.loadUser(args.uploadId)
                    }
                }
            }
            "user_info" -> {
                viewModel.watchCurrentUserInfo(args.uploadId).observe(viewLifecycleOwner) { user ->
                    if (user.isNotEmpty()) {
                        val userInfo = user[0]
                        text_upload_input.hint = getString(R.string.hint_new_about)
                        text_upload_input.setText(userInfo.userInfo)
                        text_upload_input.inputType = InputType.TYPE_CLASS_TEXT
                        text_upload_btn.setOnClickListener {
                            serviceProvider.getService()?.userModifyInfo(
                                userId = args.uploadId,
                                userName = null,
                                userPassword = null,
                                userGender = null,
                                userAvatarLink = null,
                                userInfo = text_upload_input.text.toString(),
                                userLocation = null
                            )
                            requireActivity().onBackPressed()
                        }
                    } else {
                        serviceProvider.getService()?.loadUser(args.uploadId)
                    }
                }
            }
            "user_location" -> {
                viewModel.watchCurrentUserInfo(args.uploadId).observe(viewLifecycleOwner) { user ->
                    if (user.isNotEmpty()) {
                        val userInfo = user[0]
                        text_upload_input.hint = getString(R.string.hint_new_location)
                        text_upload_input.setText(userInfo.userLocation)
                        text_upload_input.inputType = InputType.TYPE_CLASS_TEXT
                        text_upload_btn.setOnClickListener {
                            serviceProvider.getService()?.userModifyInfo(
                                userId = args.uploadId,
                                userName = null,
                                userPassword = null,
                                userGender = null,
                                userAvatarLink = null,
                                userInfo = null,
                                userLocation = text_upload_input.text.toString()
                            )
                            requireActivity().onBackPressed()
                        }
                    } else {
                        serviceProvider.getService()?.loadUser(args.uploadId)
                    }
                }
            }
            "group_name" -> {
                viewModel.watchCurrentGroup(args.uploadId).observe(viewLifecycleOwner) { group ->
                    if (group.isNotEmpty()) {
                        val groupInfo = group[0]
                        text_upload_input.hint = getString(R.string.hint_new_group_name)
                        text_upload_input.setText(groupInfo.groupName)
                        text_upload_input.inputType = InputType.TYPE_CLASS_TEXT
                        text_upload_btn.setOnClickListener {
                            serviceProvider.getService()?.groupModifyInfo(
                                groupId = args.uploadId,
                                groupName = text_upload_input.text.toString(),
                                groupInfo = null,
                                groupAvatarLink = null
                            )
                            requireActivity().onBackPressed()
                        }
                    } else {
                        serviceProvider.getService()?.loadGroup(args.uploadId)
                    }
                }
            }
            "group_info" -> {
                viewModel.watchCurrentGroup(args.uploadId).observe(viewLifecycleOwner) { group ->
                    if (group.isNotEmpty()) {
                        val groupInfo = group[0]
                        text_upload_input.hint = getString(R.string.hint_new_group_about)
                        text_upload_input.setText(groupInfo.groupInfo)
                        text_upload_input.inputType = InputType.TYPE_CLASS_TEXT
                        text_upload_btn.setOnClickListener {
                            serviceProvider.getService()?.groupModifyInfo(
                                groupId = args.uploadId,
                                groupName = null,
                                groupInfo = text_upload_input.text.toString(),
                                groupAvatarLink = null
                            )
                            requireActivity().onBackPressed()
                        }
                    } else {
                        serviceProvider.getService()?.loadGroup(args.uploadId)
                    }
                }
            }
            else -> requireActivity().onBackPressed()
        }
    }
}