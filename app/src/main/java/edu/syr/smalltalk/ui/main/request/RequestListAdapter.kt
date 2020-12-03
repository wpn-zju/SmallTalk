package edu.syr.smalltalk.ui.main.request

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.model.entity.SmallTalkRequest

class RequestListAdapter
    : ListAdapter<SmallTalkRequest, RequestListAdapter.RequestListViewHolder>(RequestListDiffCallback()){

    override fun onBindViewHolder(holder: RequestListViewHolder, position: Int) {
        val request = getItem(position)
        holder.requestAvatar.setImageResource(R.mipmap.ic_launcher)
        holder.requestTitle.text = "Request Title" // Todo
        holder.requestDetail.text = "Request Detail" // Todo
        holder.requestStatus.text = request.requestStatus

        if (request.requestStatus == "request_pending") {
            holder.requestAccept.visibility = View.VISIBLE
            holder.requestDecline.visibility = View.VISIBLE
            holder.requestAccept.setOnClickListener {
                if (requestClickListener != null) {
                    if (position != RecyclerView.NO_POSITION) {
                        requestClickListener!!.onConfirmListener(holder.itemView, request.requestType, request.requestId)
                    }
                }
            }
            holder.requestDecline.setOnClickListener {
                if (requestClickListener != null) {
                    if (position != RecyclerView.NO_POSITION) {
                        requestClickListener!!.onDeclineListener(holder.itemView, request.requestType, request.requestId)
                    }
                }
            }
        } else {
            holder.requestAccept.visibility = View.GONE
            holder.requestDecline.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.layout_prefab_request, parent, false)
        return RequestListViewHolder(view)
    }

    inner class RequestListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val requestAvatar: ImageView = view.findViewById(R.id.request_icon)
        val requestTitle: TextView = view.findViewById(R.id.request_title)
        val requestDetail: TextView = view.findViewById(R.id.request_detail)
        val requestStatus: TextView = view.findViewById(R.id.request_status)
        val requestAccept: TextView = view.findViewById(R.id.btn_request_confirm)
        val requestDecline: TextView = view.findViewById(R.id.btn_request_refuse)
    }

    private var requestClickListener: RequestClickListener? = null

    fun setRequestClickListener(listener: RequestClickListener) {
        requestClickListener = listener
    }

    interface RequestClickListener {
        fun onConfirmListener(view: View, requestType: String, requestId: Int)
        fun onDeclineListener(view: View, requestType: String, requestId: Int)
        fun onItemClickListener(view: View, requestId: Int)
        fun onItemLongClickListener(view: View, requestId: Int)
    }
}

class RequestListDiffCallback : DiffUtil.ItemCallback<SmallTalkRequest>() {
    override fun areItemsTheSame(oldItem: SmallTalkRequest, newItem: SmallTalkRequest): Boolean {
        return oldItem.requestId == newItem.requestId
    }

    override fun areContentsTheSame(oldItem: SmallTalkRequest, newItem: SmallTalkRequest): Boolean {
        return oldItem.requestEquals(newItem)
    }
}
