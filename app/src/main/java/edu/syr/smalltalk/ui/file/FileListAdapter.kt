package edu.syr.smalltalk.ui.file

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.syr.smalltalk.R

class FileListAdapter(private val fileList: ArrayList<FileUploadTask>)
    : RecyclerView.Adapter<FileListAdapter.FileListViewHolder>() {

    override fun onBindViewHolder(holder: FileListViewHolder, position: Int) {
        val file = fileList[position]

        file.holder = holder

        // holder.filePreview.setImageURI(file.fileUri)
        holder.fileName.text = file.fileName
        holder.fileSize.text = longToSizeString(file.fileSize)
        holder.progressBar.progress = file.progress

        holder.cancel.setOnClickListener {
            if (uploadTaskListener != null) {
                uploadTaskListener!!.onItemCanceledListener(holder.itemView, position)
            }
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
        val progressBar: ProgressBar = view.findViewById(R.id.progress_bar)
        val cancel: ImageView = view.findViewById(R.id.file_cancel)
    }

    private var uploadTaskListener: UploadTaskListener? = null

    fun setFileClickListener(listener: UploadTaskListener) {
        uploadTaskListener = listener
    }

    interface UploadTaskListener {
        fun onItemCanceledListener(view: View, fileId: Int)
    }

    private fun longToSizeString(fileSize: Long): String {
        return when {
            fileSize < 1024 -> {
                "%d B".format(fileSize)
            }
            fileSize < 1024 * 1024 -> {
                "%.2f KB".format(fileSize.toFloat() / 1024)
            }
            else -> {
                "%.2f MB".format(fileSize.toFloat() / 1024 / 1024)
            }
        }
    }
}
