package edu.syr.smalltalk.ui.main.group

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.model.entity.SmallTalkGroup

class GroupListAdapter
    : ListAdapter<SmallTalkGroup, GroupListAdapter.GroupListViewHolder>(GroupListDiffCallback()) {

    override fun onBindViewHolder(holder: GroupListAdapter.GroupListViewHolder, position: Int) {
        val group = getItem(position)
        holder.groupAvatar.setImageResource(R.mipmap.ic_launcher)
        holder.groupName.text = group.groupName

        holder.itemView.setOnClickListener {
            Log.v("T","T")
            if (groupClickListener != null) {
                if (position != RecyclerView.NO_POSITION) {
                    groupClickListener!!.onItemClickListener(holder.itemView, group.groupId)
                }
            }
        }

        holder.itemView.setOnLongClickListener {
            if (groupClickListener != null) {
                if (position != RecyclerView.NO_POSITION) {
                    groupClickListener!!.onItemLongClickListener(holder.itemView, group.groupId)
                }
            }
            true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.layout_prefab_group, parent, false)
        return GroupListViewHolder(view)
    }

    inner class GroupListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val groupAvatar: ImageView = view.findViewById(R.id.group_icon)
        val groupName: TextView = view.findViewById(R.id.group_name)
    }

    private var groupClickListener: GroupListAdapter.GroupClickListener? = null

    fun setGroupClickListener(listener: GroupListAdapter.GroupClickListener) {
        groupClickListener = listener
    }

    interface GroupClickListener {
        fun onItemClickListener(view: View, groupId: Int)
        fun onItemLongClickListener(view: View, groupId: Int)
    }
}

class GroupListDiffCallback : DiffUtil.ItemCallback<SmallTalkGroup>() {
    override fun areItemsTheSame(oldItem: SmallTalkGroup, newItem: SmallTalkGroup): Boolean {
        return oldItem.groupId == newItem.groupId
    }

    override fun areContentsTheSame(oldItem: SmallTalkGroup, newItem: SmallTalkGroup): Boolean {
        return oldItem.groupEquals(newItem)
    }
}
