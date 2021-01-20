package edu.syr.smalltalk.ui.main.file

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.model.entity.SmallTalkFile
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel
import edu.syr.smalltalk.ui.file.FileUploadTask

class FileAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: SmallTalkViewModel)
    : ListAdapter<SmallTalkFile, FileAdapter.FileViewHolder>(FileDiffCallback()) {

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = getItem(position)
        holder.filePreview.setImageResource(R.drawable.ic_outline_insert_drive_file_48)
        holder.fileName.text = file.fileName
        holder.fileSize.text = FileUploadTask.fileSizeToString(file.fileSize.toLong())
        holder.fileUploadTime.text = file.fileUploadTime.toString()
        holder.fileExpireTime.text = file.fileExpireTime.toString()
        holder.fileDownloads.text = file.fileDownloads.toString()
        holder.itemView.setOnClickListener {
            fileClickListener?.onItemClickListener(holder.itemView, file.fileLink)
        }

        viewModel.watchCurrentContact(file.fileUploader).observe(lifecycleOwner) {
            if (it.isNotEmpty()) {
                val uploaderInfo = it[0]
                holder.fileUploader.text = uploaderInfo.contactName
            } else {
                fileClickListener?.loadContact(file.fileUploader)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.layout_prefab_file, parent, false)
        return FileViewHolder(view)
    }

    inner class FileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val filePreview: ImageView = view.findViewById(R.id.file_preview)
        val fileName: TextView = view.findViewById(R.id.file_name)
        val fileUploader: TextView = view.findViewById(R.id.file_uploader)
        val fileSize: TextView = view.findViewById(R.id.file_size)
        val fileUploadTime: TextView = view.findViewById(R.id.file_upload_time)
        val fileExpireTime: TextView = view.findViewById(R.id.file_expire_time)
        val fileDownloads: TextView = view.findViewById(R.id.file_downloads)
    }

    private var fileClickListener: FileClickListener? = null

    fun setFileClickListener(listener: FileClickListener) {
        fileClickListener = listener
    }

    interface FileClickListener {
        fun onItemClickListener(view: View, fileLink: String)
        fun loadContact(contactId: Int)
    }
}

class FileDiffCallback : DiffUtil.ItemCallback<SmallTalkFile>() {
    override fun areItemsTheSame(oldItem: SmallTalkFile, newItem: SmallTalkFile): Boolean {
        return oldItem.fileId == newItem.fileId
    }

    override fun areContentsTheSame(oldItem: SmallTalkFile, newItem: SmallTalkFile): Boolean {
        return oldItem.fileEquals(newItem)
    }
}