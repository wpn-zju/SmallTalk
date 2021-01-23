package edu.syr.smalltalk.ui.main.chat

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.android.constant.ClientConstant
import edu.syr.smalltalk.service.model.entity.SmallTalkMessage
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel

class ChatMessageListAdapter(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: SmallTalkViewModel)
    :ListAdapter<SmallTalkMessage, ChatMessageListAdapter.MessageViewHolder>(ChatMessageListDiffCallback()){

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = getItem(position)

        viewModel.watchCurrentContact(message.sender).observe(lifecycleOwner) {
            if (it.isNotEmpty()) {
                val contactInfo = it[0]
                holder.sender.text = contactInfo.contactName
                SmallTalkApplication.picasso(contactInfo.contactAvatarLink, holder.avatar)
            } else {
                chatMessageClickListener?.loadContact(message.sender)
            }
        }

        when (message.contentType) {
            ClientConstant.CHAT_CONTENT_TYPE_TEXT -> {
                val rHolder = holder as TextViewHolder
                rHolder.content.text = message.content
            }
            ClientConstant.CHAT_CONTENT_TYPE_IMAGE -> {
                val fileDescription = Gson().fromJson(message.content, JsonObject::class.java)
                val url = fileDescription.get("file_url").asString
                val rHolder = holder as ImageViewHolder
                SmallTalkApplication.picasso(url, rHolder.content)
            }
            ClientConstant.CHAT_CONTENT_TYPE_AUDIO -> {
                // TODO: TEST AUDIO PLAY
                val fileDescription = Gson().fromJson(message.content, JsonObject::class.java)
                val url = fileDescription.get("file_url").asString
                val rHolder = holder as AudioViewHolder
                val mp = MediaPlayer.create(context, Uri.parse(url))
                rHolder.playButtonImage.setOnClickListener {
                    if (mp.isPlaying) {
                        mp.stop()
                        rHolder.playButtonImage.setImageResource(R.drawable.ic_outline_play_circle_outline_48)
                    } else {
                        mp.start()
                        rHolder.playButtonImage.setImageResource(R.drawable.ic_baseline_pause_circle_outline_48)
                        rHolder.progressText.post {
                            while (mp.isPlaying) {
                                val progress: Int = mp.currentPosition * 100 / mp.duration
                                rHolder.seekBar.progress = progress
                                rHolder.progressText.text = String.format("%d:%d / %d:%d",
                                    mp.currentPosition / 1000 / 60,
                                    mp.currentPosition / 1000 % 60,
                                    mp.duration / 1000 / 60,
                                    mp.duration / 1000 % 60)
                                Thread.sleep(1000)
                            }
                        }
                    }
                }
                mp.release()
                mp.setOnCompletionListener {
                    rHolder.playButtonImage.setImageResource(R.drawable.ic_outline_play_circle_outline_48)
                    val progress: Int = mp.currentPosition * 100 / mp.duration
                    rHolder.seekBar.progress = progress
                    rHolder.progressText.text = String.format("%d:%d / %d:%d",
                        mp.currentPosition / 1000 / 60,
                        mp.currentPosition / 1000 % 60,
                        mp.duration / 1000 / 60,
                        mp.duration / 1000 % 60)
                }
            }
            ClientConstant.CHAT_CONTENT_TYPE_VIDEO -> {
                val fileDescription = Gson().fromJson(message.content, JsonObject::class.java)
                val url = fileDescription.get("file_url").asString
                val rHolder = holder as VideoViewHolder
                val controller = MediaController(holder.content.context)
                controller.setAnchorView(rHolder.content)
                rHolder.content.setMediaController(controller)
                rHolder.content.setVideoPath(url)
            }
            ClientConstant.CHAT_CONTENT_TYPE_FILE -> {
                val fileDescription = Gson().fromJson(message.content, JsonObject::class.java)
                val rHolder = holder as FileViewHolder
                rHolder.fileImage.setImageResource(R.mipmap.ic_smalltalk)
                rHolder.fileName.text = fileDescription.get("file_name").asString
                rHolder.fileSize.text = fileDescription.get("file_size").asString
                holder.itemView.setOnClickListener {
                    if (position != RecyclerView.NO_POSITION) {
                        chatMessageClickListener?.openBrowser(fileDescription.get("file_url").asString)
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
        val userId: Int = SmallTalkApplication.getCurrentUserId(context)
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
        val sender: TextView = view.findViewById(R.id.message_sender)
    }

    inner class TextViewHolder(view: View) : MessageViewHolder(view) {
        val content: TextView = view.findViewById(R.id.message_content)
    }

    inner class ImageViewHolder(view: View) : MessageViewHolder(view) {
        val content: ImageView = view.findViewById(R.id.message_content)
    }

    inner class AudioViewHolder(view: View) : MessageViewHolder(view) {
        val playButtonImage: ImageView = view.findViewById(R.id.audio_player_button_image)
        val seekBar: SeekBar = view.findViewById(R.id.audio_player_seek_bar)
        val progressText: TextView = view.findViewById(R.id.audio_player_progress_text)
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
        fun openBrowser(url: String)
        fun loadContact(contactId: Int)
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
