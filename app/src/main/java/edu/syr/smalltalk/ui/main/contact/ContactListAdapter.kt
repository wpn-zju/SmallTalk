package edu.syr.smalltalk.ui.main.contact

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.model.entity.SmallTalkContact

class ContactListAdapter
    : ListAdapter<SmallTalkContact, ContactListAdapter.ContactListViewHolder>(ContactListDiffCallback()) {

    override fun onBindViewHolder(holder: ContactListViewHolder, position: Int) {
        val contact = getItem(position)
        holder.contactAvatar.setImageResource(R.mipmap.ic_launcher)
        holder.contactName.text = contact.contactName

        holder.itemView.setOnClickListener {
            if (contactClickListener != null) {
                if (position != RecyclerView.NO_POSITION) {
                    contactClickListener!!.onItemClickListener(holder.itemView, contact.contactId)
                }
            }
        }

        holder.itemView.setOnLongClickListener {
            if (contactClickListener != null) {
                if (position != RecyclerView.NO_POSITION) {
                    contactClickListener!!.onItemLongClickListener(holder.itemView, contact.contactId)
                }
            }
            true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.layout_prefab_contact, parent, false)
        return ContactListViewHolder(view)
    }

    inner class ContactListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contactAvatar: ImageView = view.findViewById(R.id.user_icon)
        val contactName: TextView = view.findViewById(R.id.user_name)
    }

    private var contactClickListener: ContactClickListener? = null

    fun setContactClickListener(listener: ContactClickListener) {
        contactClickListener = listener
    }

    interface ContactClickListener {
        fun onItemClickListener(view: View, contactId: Int)
        fun onItemLongClickListener(view: View, contactId: Int)
    }
}

class ContactListDiffCallback : DiffUtil.ItemCallback<SmallTalkContact>() {
    override fun areItemsTheSame(oldItem: SmallTalkContact, newItem: SmallTalkContact): Boolean {
        return oldItem.contactId == newItem.contactId
    }

    override fun areContentsTheSame(oldItem: SmallTalkContact, newItem: SmallTalkContact): Boolean {
        return oldItem.contactEquals(newItem)
    }
}
