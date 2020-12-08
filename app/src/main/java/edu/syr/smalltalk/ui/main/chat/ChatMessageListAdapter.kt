package edu.syr.smalltalk.ui.main.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.android.constant.ClientConstant
import edu.syr.smalltalk.service.model.entity.SmallTalkMessage

class ChatMessageListAdapter
    :ListAdapter<SmallTalkMessage, ChatMessageListAdapter.MessageViewHolder>(ChatMessageListDiffCallback()){

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = getItem(position)
        holder.avatar.setImageResource(R.mipmap.ic_launcher)

        when (message.contentType) {
            ClientConstant.CHAT_CONTENT_TYPE_TEXT -> {
                val rHolder = holder as TextViewHolder
                rHolder.content.text = message.content
            }
            ClientConstant.CHAT_CONTENT_TYPE_IMAGE -> {
                val rHolder = holder as ImageViewHolder
                Picasso.Builder(holder.itemView.context).listener { _, _, e -> e.printStackTrace() }.build()
                    .load(message.content).into(rHolder.content)
            }
            ClientConstant.CHAT_CONTENT_TYPE_AUDIO -> {
                val rHolder = holder as AudioViewHolder
                rHolder.content.text = message.content
            }
            ClientConstant.CHAT_CONTENT_TYPE_VIDEO -> {
                val rHolder = holder as VideoViewHolder
                rHolder.content.setVideoPath(message.content)
            }
            ClientConstant.CHAT_CONTENT_TYPE_FILE -> {
                val rHolder = holder as FileViewHolder
                rHolder.fileImage.setImageResource(R.mipmap.ic_launcher) // Todo
                rHolder.fileName.text = message.content.substring(message.content.lastIndexOf('/') + 1) // Todo
                rHolder.fileSize.text = "Unknown" // Todo
                holder.itemView.setOnClickListener {
                    if (chatMessageClickListener != null) {
                        if (position != RecyclerView.NO_POSITION) {
                            chatMessageClickListener!!.openBrowser(message.content)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            MSG_TXT_R -> {
                TextViewHolder(layoutInflater.inflate(R.layout.layout_prefab_message_text_right, parent, false))
            }
            MSG_TXT_L -> {
                TextViewHolder(layoutInflater.inflate(R.layout.layout_prefab_message_text_left, parent, false))
            }
            MSG_IMG_R -> {
                ImageViewHolder(layoutInflater.inflate(R.layout.layout_prefab_message_image_right, parent, false))
            }
            MSG_IMG_L -> {
                ImageViewHolder(layoutInflater.inflate(R.layout.layout_prefab_message_image_left, parent, false))
            }
            MSG_AUDIO_R -> {
                AudioViewHolder(layoutInflater.inflate(R.layout.layout_prefab_message_audio_right, parent, false))
            }
            MSG_AUDIO_L -> {
                AudioViewHolder(layoutInflater.inflate(R.layout.layout_prefab_message_audio_left, parent, false))
            }
            MSG_VIDEO_R -> {
                VideoViewHolder(layoutInflater.inflate(R.layout.layout_prefab_message_video_right, parent, false))
            }
            MSG_VIDEO_L -> {
                VideoViewHolder(layoutInflater.inflate(R.layout.layout_prefab_message_video_left, parent, false))
            }
            MSG_FILE_R -> {
                FileViewHolder(layoutInflater.inflate(R.layout.layout_prefab_message_file_right, parent, false))
            }
            MSG_FILE_L -> {
                FileViewHolder(layoutInflater.inflate(R.layout.layout_prefab_message_file_left, parent, false))
            }
            else -> {
                TextViewHolder(layoutInflater.inflate(R.layout.layout_prefab_message_text_left, parent, false))
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message: SmallTalkMessage = getItem(position)
        val userId: Int = chatMessageClickListener!!.getUserId()
        val isSubject: Boolean = message.sender == userId
        return when (message.contentType) {
            ClientConstant.CHAT_CONTENT_TYPE_TEXT -> {
                if (isSubject) MSG_TXT_R else MSG_TXT_L
            }
            ClientConstant.CHAT_CONTENT_TYPE_IMAGE -> {
                if (isSubject) MSG_IMG_R else MSG_IMG_L
            }
            ClientConstant.CHAT_CONTENT_TYPE_AUDIO -> {
                if (isSubject) MSG_AUDIO_R else MSG_AUDIO_L
            }
            ClientConstant.CHAT_CONTENT_TYPE_VIDEO -> {
                if (isSubject) MSG_VIDEO_R else MSG_VIDEO_L
            }
            ClientConstant.CHAT_CONTENT_TYPE_FILE -> {
                if (isSubject) MSG_FILE_R else MSG_FILE_L
            }
            else -> MSG_SYS_M
        }
    }

    open inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.message_avatar)
    }

    inner class TextViewHolder(view: View) : MessageViewHolder(view) {
        val content: TextView = view.findViewById(R.id.message_text_content)
    }

    inner class ImageViewHolder(view: View) : MessageViewHolder(view) {
        val content: ImageView = view.findViewById(R.id.message_image_content)
    }

    inner class AudioViewHolder(view: View) : MessageViewHolder(view) {
        val content: TextView = view.findViewById(R.id.message_text_content)
    }

    inner class VideoViewHolder(view: View) : MessageViewHolder(view) {
        val content: VideoView = view.findViewById(R.id.message_video_content)
    }

    inner class FileViewHolder(view: View) : MessageViewHolder(view) {
        val fileImage: ImageView = view.findViewById(R.id.message_file_image)
        val fileName: TextView = view.findViewById(R.id.message_file_name)
        val fileSize: TextView = view.findViewById(R.id.message_file_size)
    }

    private var chatMessageClickListener: ChatMessageClickListener? = null

    fun setChatMessageClickListener(listener: ChatMessageClickListener) {
        chatMessageClickListener = listener
    }

    interface ChatMessageClickListener {
        fun getUserId(): Int
        fun getChatId(): Int
        fun openBrowser(url: String)
    }

    companion object {
        const val MSG_TXT_L: Int = 0
        const val MSG_TXT_R: Int = 1
        const val MSG_IMG_L: Int = 2
        const val MSG_IMG_R: Int = 3
        const val MSG_AUDIO_L: Int = 4
        const val MSG_AUDIO_R: Int = 5
        const val MSG_VIDEO_L: Int = 6
        const val MSG_VIDEO_R: Int = 7
        const val MSG_FILE_L: Int = 8
        const val MSG_FILE_R: Int = 9
        const val MSG_SYS_M: Int = 10
    }
}

class ChatMessageListDiffCallback : DiffUtil.ItemCallback<SmallTalkMessage>() {
    override fun areItemsTheSame(oldItem: SmallTalkMessage, newItem: SmallTalkMessage): Boolean {
        return oldItem.messageId == newItem.messageId
    }

    override fun areContentsTheSame(oldItem: SmallTalkMessage, newItem: SmallTalkMessage): Boolean {
        return oldItem.messageId == newItem.messageId
    }
}
