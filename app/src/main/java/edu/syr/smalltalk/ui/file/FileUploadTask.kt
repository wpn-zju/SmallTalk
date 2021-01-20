package edu.syr.smalltalk.ui.file

import android.content.Context
import android.net.Uri
import android.view.View
import android.webkit.MimeTypeMap
import androidx.core.content.MimeTypeFilter
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.android.constant.ClientConstant
import edu.syr.smalltalk.service.android.http.SmallTalkAPI
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.apache.commons.io.FilenameUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class FileUploadTask (
    val fileUri: Uri,
    val fileName: String,
    val fileSize: Long
) : UploadRequestBody.UploadCallback {
    val fileType: String = getTypeFromFileName(fileName)
    val fileSizeString: String = fileSizeToString(fileSize)
    var status: Int = UPLOAD_STATUS_PENDING
    var progress: Int = 0
    var holder: FileListAdapter.FileListViewHolder? = null

    override fun onStartUpload() {
        status = UPLOAD_STATUS_UPLOADING
        holder?.cancel?.visibility = View.GONE
        onProgressUpdate(0)
    }

    override fun onUploadFailed() {
        status = UPLOAD_STATUS_FAILED
        progress = 0
        holder?.cancel?.visibility = View.VISIBLE
        onProgressUpdate(0)
    }

    override fun onUploadResponse() {
        status = UPLOAD_STATUS_UPLOADED
        progress = 100
        holder?.cancel?.visibility = View.GONE
        onProgressUpdate(100)
    }

    override fun onProgressUpdate(percentage: Int) {
        progress = percentage
        holder?.progressBar?.progress = progress
    }

    fun startUploading(context: Context, completeCallback: (String) -> Unit) {
        onStartUpload()
        val parcelFileDescriptor = context.contentResolver
            .openFileDescriptor(fileUri, "r", null)
            ?: throw FileUploadFailedException(context.getString(R.string.exception_file_open_descriptor_failed))
        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(context.cacheDir, context.contentResolver.getFileName(fileUri))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        val body = UploadRequestBody(file, this,"application")
        SmallTalkAPI()
            .uploadFile(
                MultipartBody.Part.createFormData("file", fileName, body),
                "base", "json".toRequestBody("multipart/form-data".toMediaTypeOrNull()))
            .enqueue(object : Callback<Void> {
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    onUploadFailed()
                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    onUploadResponse()
                    response.body().let { file }
                    completeCallback(SmallTalkApplication.HTTP_URL + "/download/base/" + fileName)
                }
            })
    }

    companion object {
        const val UPLOAD_STATUS_PENDING = 100
        const val UPLOAD_STATUS_UPLOADING = 101
        const val UPLOAD_STATUS_UPLOADED = 102
        const val UPLOAD_STATUS_FAILED = 103

        fun fileSizeToString(fileSize: Long): String {
            return when {
                fileSize < 1024 -> "%d B".format(fileSize)
                fileSize < 1024 * 1024 -> "%.2f KB".format(fileSize.toFloat() / 1024)
                else -> "%.2f MB".format(fileSize.toFloat() / 1024 / 1024)
            }
        }

        fun getTypeFromFileName(fileName: String): String {
            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(FilenameUtils.getExtension(fileName))
            return when {
                MimeTypeFilter.matches(mimeType, FileSelectActivity.MIME_TYPE_WILDCARD_IMAGE) -> ClientConstant.CHAT_CONTENT_TYPE_IMAGE
                MimeTypeFilter.matches(mimeType, FileSelectActivity.MIME_TYPE_WILDCARD_AUDIO) -> ClientConstant.CHAT_CONTENT_TYPE_AUDIO
                MimeTypeFilter.matches(mimeType, FileSelectActivity.MIME_TYPE_WILDCARD_VIDEO) -> ClientConstant.CHAT_CONTENT_TYPE_VIDEO
                else -> ClientConstant.CHAT_CONTENT_TYPE_FILE
            }
        }
    }
}
