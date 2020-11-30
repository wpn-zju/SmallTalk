package edu.syr.smalltalk.ui.main.contactlist

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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.layout_prefab_contact, parent, false)
        return ContactListViewHolder(view)
    }

    inner class ContactListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contactAvatar: ImageView = view.findViewById(R.id.user_icon)
        val contactName: TextView = view.findViewById(R.id.user_name)

        init {
            val contact: SmallTalkContact = getItem(adapterPosition)!!

            view.setOnClickListener {
                if (contactClickListener != null) {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        contactClickListener!!.onItemClickListener(view, contact.contactId)
                    }
                }
            }

            view.setOnLongClickListener {
                if (contactClickListener != null) {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        contactClickListener!!.onItemLongClickListener(view, contact.contactId)
                    }
                }
                true
            }
        }
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
