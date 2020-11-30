package edu.syr.smalltalk.ui.main.messagelist

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

class MessageListAdapter
    : ListAdapter<SmallTalkMessage, MessageListAdapter.MessageListViewHolder>(MessageListDiffCallback()) {

    override fun onBindViewHolder(holder: MessageListViewHolder, position: Int) {
        val message = getItem(position)
        holder.chatAvatar.setImageResource(R.mipmap.ic_launcher)
        holder.chatName.text = message.chatId.toString() // Todo
        holder.chatPreview.text = message.content // Todo
        // Todo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.layout_prefab_recent_message, parent, false)
        return MessageListViewHolder(view)
    }

    inner class MessageListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val chatAvatar: ImageView = view.findViewById(R.id.user_icon)
        val chatName: TextView = view.findViewById(R.id.user_name)
        val chatPreview: TextView = view.findViewById(R.id.preview)
        val chatTimestamp: TextView = view.findViewById(R.id.recent_time)
        val chatUnreadNumber: TextView = view.findViewById(R.id.message_number)

        init {
            val message: SmallTalkMessage = getItem(adapterPosition)!!

            view.setOnClickListener {
                if (messageClickListener != null) {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        messageClickListener!!.onItemClickListener(view, message.chatId)
                    }
                }
            }

            view.setOnLongClickListener {
                if (messageClickListener != null) {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        messageClickListener!!.onItemLongClickListener(view, message.chatId)
                    }
                }
                true
            }
        }
    }

    private var messageClickListener: MessageClickListener? = null

    fun setMessageClickListener(listener: MessageClickListener) {
        messageClickListener = listener
    }

    interface MessageClickListener {
        fun onItemClickListener(view: View, chatId: Int)
        fun onItemLongClickListener(view: View, chatId: Int)
    }
}

class MessageListDiffCallback : DiffUtil.ItemCallback<SmallTalkMessage>() {
    override fun areItemsTheSame(oldItem: SmallTalkMessage, newItem: SmallTalkMessage): Boolean {
        return oldItem.chatId == newItem.chatId
    }

    override fun areContentsTheSame(oldItem: SmallTalkMessage, newItem: SmallTalkMessage): Boolean {
        return oldItem.messageId == newItem.messageId
    }
}
