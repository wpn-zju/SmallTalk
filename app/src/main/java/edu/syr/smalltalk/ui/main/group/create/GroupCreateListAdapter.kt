package edu.syr.smalltalk.ui.main.group.create

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.model.entity.SmallTalkContact
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication

class GroupCreateListAdapter
    : ListAdapter<Pair<SmallTalkContact, Boolean>, GroupCreateListAdapter.GroupCreateContactViewHolder>(GroupCreateContactDiffCallback()) {

    override fun onBindViewHolder(holder: GroupCreateContactViewHolder, position: Int) {
        val contact = getItem(position).first
        val checked = getItem(position).second
        holder.checkbox.isChecked = checked
        SmallTalkApplication.picasso(contact.contactAvatarLink, holder.contactAvatar)
        holder.contactName.text = contact.contactName
        holder.checkbox.setOnCheckedChangeListener { v, isChecked ->
            if (position != RecyclerView.NO_POSITION) {
                groupCreateListener?.onItemChecked(v, contact.contactId, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupCreateContactViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.layout_prefab_contact_selectable, parent, false)
        return GroupCreateContactViewHolder(view)
    }

    inner class GroupCreateContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkbox: CheckBox = view.findViewById(R.id.group_create_check_box)
        val contactAvatar: ImageView = view.findViewById(R.id.group_create_contact_avatar)
        val contactName: TextView = view.findViewById(R.id.group_create_contact_name)
    }

    private var groupCreateListener: GroupCreateContactListener? = null

    fun setGroupCreateListener(listener: GroupCreateContactListener) {
        groupCreateListener = listener
    }

    interface GroupCreateContactListener {
        fun onItemChecked(view: View, contactId: Int, isChecked: Boolean)
    }
}

class GroupCreateContactDiffCallback : DiffUtil.ItemCallback<Pair<SmallTalkContact, Boolean>>() {
    override fun areItemsTheSame(oldItem: Pair<SmallTalkContact, Boolean>, newItem: Pair<SmallTalkContact, Boolean>): Boolean {
        return oldItem.first.contactId == newItem.first.contactId
    }

    override fun areContentsTheSame(oldItem: Pair<SmallTalkContact, Boolean>, newItem: Pair<SmallTalkContact, Boolean>): Boolean {
        return oldItem.first.contactEquals(newItem.first)
                && oldItem.second == newItem.second
    }
}
