package edu.syr.smalltalk.ui.main.message

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.model.entity.SmallTalkRecentMessage
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MessageListAdapter(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: SmallTalkViewModel)
    : ListAdapter<SmallTalkRecentMessage, MessageListAdapter.MessageListViewHolder>(MessageListDiffCallback()) {

    override fun onBindViewHolder(holder: MessageListViewHolder, position: Int) {
        val message = getItem(position).message
        val unreadNum = getItem(position).unreadNum
        if (message.isGroup) {
            holder.preview.text = message.content
            holder.timestamp.text = instantToTimeString(context, message.timestamp)
            holder.unreadNum.text = unreadNum.toString()
            viewModel.watchCurrentGroup(message.chatId).observe(lifecycleOwner) {
                if (it.isNotEmpty()) {
                    val groupInfo = it[0]
                    Picasso.Builder(holder.itemView.context).listener { _, _, e -> e.printStackTrace() }.build()
                        .load(groupInfo.groupAvatarLink).error(R.mipmap.ic_smalltalk).into(holder.avatar)
                    holder.name.text = groupInfo.groupName
                } else {
                    messageClickListener?.loadGroup(message.chatId)
                }
            }
        } else {
            holder.preview.text = message.content
            holder.timestamp.text = instantToTimeString(context, message.timestamp)
            holder.unreadNum.text = unreadNum.toString()
            viewModel.watchCurrentContact(message.chatId).observe(lifecycleOwner) {
                if (it.isNotEmpty()) {
                    val contactInfo = it[0]
                    Picasso.Builder(holder.itemView.context).listener { _, _, e -> e.printStackTrace() }.build()
                        .load(contactInfo.contactAvatarLink).error(R.mipmap.ic_smalltalk).into(holder.avatar)
                    holder.name.text = contactInfo.contactName
                } else {
                    messageClickListener?.loadContact(message.chatId)
                }
            }
        }

        holder.itemView.setOnClickListener {
            if (position != RecyclerView.NO_POSITION) {
                messageClickListener?.onItemClickListener(holder.itemView, message.chatId)
            }
        }

        holder.itemView.setOnLongClickListener {
            if (position != RecyclerView.NO_POSITION) {
                messageClickListener?.onItemLongClickListener(holder.itemView, message.chatId)
            }
            true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.layout_prefab_recent_message, parent, false)
        return MessageListViewHolder(view)
    }

    inner class MessageListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.recent_message_avatar)
        val name: TextView = view.findViewById(R.id.recent_message_name)
        val preview: TextView = view.findViewById(R.id.recent_message_preview)
        val timestamp: TextView = view.findViewById(R.id.recent_message_timestamp)
        val unreadNum: TextView = view.findViewById(R.id.recent_message_unread_num)
    }

    private var messageClickListener: MessageClickListener? = null

    fun setMessageClickListener(listener: MessageClickListener) {
        messageClickListener = listener
    }

    interface MessageClickListener {
        fun onItemClickListener(view: View, chatId: Int)
        fun onItemLongClickListener(view: View, chatId: Int)
        fun loadContact(contactId: Int)
        fun loadGroup(groupId: Int)
    }

    companion object {
        fun instantToTimeString(context: Context, instant: Instant): String {
            val dateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
            val dateTimeNow = ZonedDateTime.now(ZoneId.systemDefault())
            return when {
                dateTime.dayOfYear == dateTimeNow.dayOfYear -> {
                    dateTime.format(DateTimeFormatter.ofPattern(context.getString(R.string.time_pattern_hm), Locale.ROOT))
                }
                dateTime.year == dateTimeNow.year -> {
                    dateTime.format(DateTimeFormatter.ofPattern(context.getString(R.string.time_pattern_md), Locale.ROOT))
                }
                else -> {
                    dateTime.format(DateTimeFormatter.ofPattern(context.getString(R.string.time_pattern_mdy), Locale.ROOT))
                }
            }
        }
    }
}

class MessageListDiffCallback : DiffUtil.ItemCallback<SmallTalkRecentMessage>() {
    override fun areItemsTheSame(oldItem: SmallTalkRecentMessage, newItem: SmallTalkRecentMessage): Boolean {
        return oldItem.message.messageId == newItem.message.messageId
    }

    override fun areContentsTheSame(oldItem: SmallTalkRecentMessage, newItem: SmallTalkRecentMessage): Boolean {
        return oldItem.message.messageId == newItem.message.messageId && oldItem.unreadNum == newItem.unreadNum
    }
}
