package edu.syr.smalltalk.ui.main.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.model.entity.SmallTalkMessage

class ChatMessageListAdapter
    :ListAdapter<SmallTalkMessage, ChatMessageListAdapter.ChatMessageListViewHolder>(ChatMessageListDiffCallback()){

    override fun onBindViewHolder(holder: ChatMessageListViewHolder, position: Int) {
        val message = getItem(position)
        holder.avatar.setImageResource(R.mipmap.ic_launcher)
        holder.content.text = message.content
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            0 -> {
                ChatMessageListViewHolder(layoutInflater.inflate(R.layout.layout_prefab_message_left, parent, false))
            }
            1 -> {
                ChatMessageListViewHolder(layoutInflater.inflate(R.layout.layout_prefab_message_right, parent, false))
            }
            else -> {
                ChatMessageListViewHolder(layoutInflater.inflate(R.layout.layout_prefab_message_left, parent, false))
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message: SmallTalkMessage = getItem(position)

        // Todo: Check Message Type and direction
        return 1
    }

    inner class ChatMessageListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.chat_message_avatar)
        val content: TextView = view.findViewById(R.id.chat_message_content)

        init {
            val message: SmallTalkMessage = getItem(adapterPosition)!!

            view.setOnClickListener {
                if (chatMessageClickListener != null) {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        chatMessageClickListener!!.onItemClickListener(view, message.messageId)
                    }
                }
            }

            view.setOnLongClickListener {
                if (chatMessageClickListener != null) {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        chatMessageClickListener!!.onItemLongClickListener(view, message.messageId)
                    }
                }
                true
            }
        }
    }

    private var chatMessageClickListener: ChatMessageClickListener? = null

    fun setChatMessageClickListener(listener: ChatMessageClickListener) {
        chatMessageClickListener = listener
    }

    interface ChatMessageClickListener {
        fun getUserId(): Int
        fun getChatId(): Int
        fun onItemClickListener(view: View, messageId: Int)
        fun onItemLongClickListener(view: View, messageId: Int)
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
