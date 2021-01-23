package edu.syr.smalltalk.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.gson.JsonObject
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.ISmallTalkServiceProvider
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModelFactory
import edu.syr.smalltalk.ui.file.FileUploadTask
import edu.syr.smalltalk.ui.file.getFileName
import edu.syr.smalltalk.ui.file.getFileSize
import kotlinx.android.synthetic.main.fragment_image_upload.*

class ImageUploadFragment: Fragment() {
    private val args: ImageUploadFragmentArgs by navArgs()

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
        return inflater.inflate(R.layout.fragment_image_upload, container, false)
    }

    private var image: FileUploadTask? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (args.uploadType) {
            "user_avatar" -> {
                viewModel.watchCurrentUserInfo(args.uploadId).observe(viewLifecycleOwner) { user ->
                    if (user.isNotEmpty()) {
                        val userInfo = user[0]
                        SmallTalkApplication.picasso(
                            userInfo.userAvatarLink,
                            image_upload_preview
                        )
                        image_select_btn.setOnClickListener {
                            Intent(Intent.ACTION_GET_CONTENT).also {
                                it.type = "*/*"
                                it.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/png", "image/jpeg", "image/x-ms-bmp"))
                                it.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                                startActivityForResult(it, REQUEST_CODE_PICK_IMAGE)
                            }
                        }
                        image_upload_btn.setOnClickListener {
                            image?.let { image ->
                                if (image.fileSize > IMAGE_SIZE_MAX) {
                                    Toast.makeText(requireContext(), getString(R.string.exception_image_too_large), Toast.LENGTH_LONG).show()
                                } else {
                                    uploadAvatar(false)
                                    requireActivity().onBackPressed()
                                }
                            }
                        }
                    } else {
                        serviceProvider.getService()?.loadUser(args.uploadId)
                    }
                }
            }
            "group_avatar" -> {
                viewModel.watchCurrentGroup(args.uploadId).observe(viewLifecycleOwner) { group ->
                    if (group.isNotEmpty()) {
                        val groupInfo = group[0]
                        SmallTalkApplication.picasso(
                            groupInfo.groupAvatarLink,
                            image_upload_preview
                        )
                        image_select_btn.setOnClickListener {
                            Intent(Intent.ACTION_GET_CONTENT).also {
                                it.type = "*/*"
                                it.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/png", "image/jpeg", "image/x-ms-bmp"))
                                it.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                                startActivityForResult(it, REQUEST_CODE_PICK_IMAGE)
                            }
                        }
                        image_upload_btn.setOnClickListener {
                            image?.let { image ->
                                if (image.fileSize > IMAGE_SIZE_MAX) {
                                    Toast.makeText(requireContext(), getString(R.string.exception_image_too_large), Toast.LENGTH_LONG).show()
                                } else {
                                    uploadAvatar(true)
                                    requireActivity().onBackPressed()
                                }
                            }
                        }
                    } else {
                        serviceProvider.getService()?.loadGroup(args.uploadId)
                    }
                }
            }
            else -> requireActivity().onBackPressed()
        }
    }

    private fun uploadAvatar(isGroup: Boolean) {
        image?.let { image ->
            image.startUploading(requireActivity()) { downloadLink ->
                serviceProvider.getService()?.let { service ->
                    val messagePayload = JsonObject()
                    messagePayload.addProperty("file_name", image.fileName)
                    messagePayload.addProperty("file_size", image.fileSizeString)
                    messagePayload.addProperty("file_url", downloadLink)
                    if (isGroup) {
                        service.groupModifyInfo(
                            groupId = args.uploadId,
                            groupName = null,
                            groupInfo = null,
                            groupAvatarLink = downloadLink
                        )
                    } else {
                        service.userModifyInfo(
                            userId = args.uploadId,
                            userName = null,
                            userPassword = null,
                            userGender = null,
                            userAvatarLink = downloadLink,
                            userInfo = null,
                            userLocation = null
                        )
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (data?.clipData != null) {
                val clipData = data.clipData!!
                if (clipData.itemCount > 0) {
                    val uri = clipData.getItemAt(0).uri
                    image = FileUploadTask(uri,
                        requireActivity().contentResolver.getFileName(uri),
                        requireActivity().contentResolver.getFileSize(uri))
                    image_upload_preview.setImageURI(uri)
                }
            } else if (data?.data != null) {
                val uri = data.data!!
                image = FileUploadTask(uri,
                    requireActivity().contentResolver.getFileName(uri),
                    requireActivity().contentResolver.getFileSize(uri))
                image_upload_preview.setImageURI(uri)
            }
        }
    }

    companion object {
        const val REQUEST_CODE_PICK_IMAGE = 200
        const val IMAGE_SIZE_MAX = 1 * 1024 * 1024
    }
}
