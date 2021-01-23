package edu.syr.smalltalk.ui.file

import android.app.Activity
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.OpenableColumns
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.ISmallTalkService
import edu.syr.smalltalk.service.ISmallTalkServiceProvider
import edu.syr.smalltalk.service.RootService
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import kotlinx.android.synthetic.main.activity_file_select.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream

class FileSelectActivity
    : AppCompatActivity(), ISmallTalkServiceProvider, FileListAdapter.UploadTaskListener {
    private val fileList: ArrayList<FileUploadTask> = ArrayList()
    private val adapter = FileListAdapter(this, fileList)

    private lateinit var service: ISmallTalkService
    private var bound: Boolean = false
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            service = (binder as RootService.RootServiceBinder).getService()
            bound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            bound = false
        }
    }

    override fun hasService(): Boolean {
        return bound
    }

    override fun getService(): ISmallTalkService? {
        return if (bound) {
            service
        } else {
            null
        }
    }

    override fun onStart() {
        super.onStart()

        Intent(this, RootService::class.java).also { intent -> bindService(
            intent,
            connection,
            Context.BIND_AUTO_CREATE
        ) }
    }

    override fun onStop() {
        super.onStop()

        unbindService(connection)
        bound = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_select)

        adapter.setFileClickListener(this)

        val layoutManager = LinearLayoutManager(this)
        file_upload_list.layoutManager = layoutManager
        file_upload_list.adapter = adapter

        btn_select.setOnClickListener {
            openContentPicker()
        }

        btn_upload.setOnClickListener {
            fileList.forEach {
                if (it.status == FileUploadTask.UPLOAD_STATUS_PENDING) {
                    try {
                        uploadFile(it)
                    } catch (e: FileUploadFailedException) {
                        it.status = FileUploadTask.UPLOAD_STATUS_FAILED
                        Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openContentPicker() {
        if (intent.getStringExtra("command") == "image") {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "*/*"
                val mimeTypes = arrayOf("image/png", "image/jpeg", "image/gif", "image/x-ms-bmp", "video/mp4", "video/webm")
                it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                it.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                startActivityForResult(it, REQUEST_CODE_PICK_MEDIA)
            }
        } else {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "*/*"
                it.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                startActivityForResult(it, REQUEST_CODE_PICK_FILE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val lastCount = fileList.size

        if (resultCode == Activity.RESULT_OK) {
            if (data?.clipData != null) {
                val clipData = data.clipData!!
                val count: Int = clipData.itemCount
                for (i in 0 until count) {
                    val uri = clipData.getItemAt(i).uri
                    val file = FileUploadTask(uri,
                        contentResolver.getFileName(uri),
                        contentResolver.getFileSize(uri))
                    fileList.add(file)
                }
            } else if (data?.data != null) {
                val uri = data.data!!
                val file = FileUploadTask(uri,
                    contentResolver.getFileName(uri),
                    contentResolver.getFileSize(uri))
                fileList.add(file)
            }
        }

        adapter.notifyItemRangeInserted(lastCount, fileList.size - lastCount)
    }

    private fun uploadFile(fileInfo: FileUploadTask) {
        if (fileInfo.fileSize > FILE_SIZE_MAX) {
            throw FileUploadFailedException(getString(R.string.exception_file_too_large))
        }

        val userId = SmallTalkApplication.getCurrentUserId(this)
        val command = intent.getStringExtra("command")
        val isGroup = intent.getBooleanExtra("isGroup", false)
        val chatId = intent.getIntExtra("chatId", 0)

        fileInfo.startUploading(this) { downloadLink ->
            getService()?.let { service ->
                val messagePayload = JsonObject()
                messagePayload.addProperty("file_name", fileInfo.fileName)
                messagePayload.addProperty("file_size", fileInfo.fileSizeString)
                messagePayload.addProperty("file_url", downloadLink)
                if (isGroup) {
                    service.messageForwardGroup(
                        userId,
                        chatId,
                        messagePayload.toString(),
                        fileInfo.fileType)
                } else {
                    service.messageForward(
                        userId,
                        chatId,
                        messagePayload.toString(),
                        fileInfo.fileType)
                }
                if (command == "file") {
                    if (isGroup) {
                        service.fileArchive(chatId, 0, fileInfo.fileName, downloadLink, userId, fileInfo.fileSize.toInt())
                    } else {
                        if (chatId < userId) {
                            service.fileArchive(chatId, userId, fileInfo.fileName, downloadLink, userId, fileInfo.fileSize.toInt())
                        } else {
                            service.fileArchive(userId, chatId, fileInfo.fileName, downloadLink, userId, fileInfo.fileSize.toInt())
                        }
                    }
                }
            }
        }
    }

    override fun onItemCanceledListener(view: View, fileId: Int) {
        fileList.removeAt(fileId)
        adapter.notifyItemRemoved(fileId)
    }

    override fun onDestroy() {
        file_upload_list.adapter = null
        super.onDestroy()
    }

    companion object {
        const val REQUEST_CODE_PICK_MEDIA = 101
        const val REQUEST_CODE_PICK_FILE = 102
        const val MIME_TYPE_WILDCARD_IMAGE = "image/*"
        const val MIME_TYPE_WILDCARD_AUDIO = "audio/*"
        const val MIME_TYPE_WILDCARD_VIDEO = "video/*"
        const val FILE_SIZE_MAX = 30 * 1024 * 1024
    }
}

class UploadRequestBody(
    private val file: File,
    private val fileUploader: FileUploadTask,
    private val contentType: String
) : RequestBody() {

    override fun contentType(): MediaType? {
        return "$contentType/*".toMediaTypeOrNull()
    }

    override fun contentLength(): Long {
        return file.length()
    }

    override fun writeTo(sink: BufferedSink) {
        val length = file.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val fileInputStream = FileInputStream(file)
        var uploaded = 0L
        fileInputStream.use { inputStream ->
            var read: Int
            val handler = Handler(Looper.getMainLooper())
            while (inputStream.read(buffer).also { read = it } != -1) {
                handler.post(ProgressUpdater(uploaded, length))
                uploaded += read
                sink.write(buffer, 0, read)
            }
        }
    }

    interface UploadCallback {
        fun onStartUpload()
        fun onUploadFailed()
        fun onUploadResponse()
        fun onProgressUpdate(percentage: Int)
    }

    inner class ProgressUpdater(private val uploaded: Long, private val total: Long) : Runnable {
        override fun run() {
            fileUploader.onProgressUpdate((uploaded * 100 / total).toInt())
        }
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 2048
    }
}

fun ContentResolver.getFileName(uri: Uri): String {
    var name = String()
    val returnCursor = this.query(uri, null, null, null, null)
    returnCursor?.let {
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        name = returnCursor.getString(nameIndex)
        returnCursor.close()
    }
    return name
}

fun ContentResolver.getFileSize(uri: Uri): Long {
    var size = 0L
    val returnCursor = this.query(uri, null, null, null, null)
    returnCursor?.let {
        val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        size = returnCursor.getLong(sizeIndex)
        returnCursor.close()
    }
    return size
}

class FileUploadFailedException(message: String) : Exception(message)