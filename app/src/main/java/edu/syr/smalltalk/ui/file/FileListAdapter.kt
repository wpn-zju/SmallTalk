package edu.syr.smalltalk.ui.file

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.android.constant.ClientConstant

class FileListAdapter(
    private val context: Context,
    private val fileList: ArrayList<FileUploadTask>)
    : RecyclerView.Adapter<FileListAdapter.FileListViewHolder>() {

    override fun onBindViewHolder(holder: FileListViewHolder, position: Int) {
        val file = fileList[position]
        file.holder = holder
        if (file.fileType == ClientConstant.CHAT_CONTENT_TYPE_IMAGE) {
            holder.filePreview.setImageURI(file.fileUri)
        } else {
            holder.filePreview.setImageResource(R.mipmap.ic_smalltalk)
        }
        holder.fileName.text = file.fileName
        holder.fileSize.text = file.fileSizeString
        file.setOnStatusChangeCallback { status ->
            holder.fileStatus.text = when (status) {
                FileUploadTask.UPLOAD_STATUS_PENDING -> context.getString(R.string.file_status_pending)
                FileUploadTask.UPLOAD_STATUS_UPLOADING -> context.getString(R.string.file_status_uploading)
                FileUploadTask.UPLOAD_STATUS_UPLOADED -> context.getString(R.string.file_status_uploaded)
                FileUploadTask.UPLOAD_STATUS_FAILED -> context.getString(R.string.file_status_failed)
                else -> context.getString(R.string.file_status_unknown)
            }
        }
        holder.progressBar.progress = file.progress
        holder.cancel.setOnClickListener {
            uploadTaskListener?.onItemCanceledListener(holder.itemView, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.layout_prefab_file_upload, parent,false)
        return FileListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    inner class FileListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val filePreview: ImageView = view.findViewById(R.id.file_preview)
        val fileName: TextView = view.findViewById(R.id.file_name)
        val fileSize: TextView = view.findViewById(R.id.file_size)
        val fileStatus: TextView = view.findViewById(R.id.file_status)
        val progressBar: ProgressBar = view.findViewById(R.id.file_progress_bar)
        val cancel: ImageView = view.findViewById(R.id.file_cancel)
    }

    private var uploadTaskListener: UploadTaskListener? = null

    fun setFileClickListener(listener: UploadTaskListener) {
        uploadTaskListener = listener
    }

    interface UploadTaskListener {
        fun onItemCanceledListener(view: View, fileId: Int)
    }
}
