package edu.syr.smalltalk.ui.main.request

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.android.constant.RequestConstant
import edu.syr.smalltalk.service.model.entity.SmallTalkRequest
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModelFactory

class RequestListAdapter
    : ListAdapter<SmallTalkRequest, RequestListAdapter.RequestListViewHolder>(RequestListDiffCallback()){

    override fun onBindViewHolder(holder: RequestListViewHolder, position: Int) {
        val request = getItem(position)

        when (request.requestType) {
            RequestConstant.REQUEST_CONTACT_ADD -> {
                val requester = request.requestMetadata.get(RequestConstant.REQUEST_CONTACT_ADD_SENDER).asInt
                val receiver = request.requestMetadata.get(RequestConstant.REQUEST_CONTACT_ADD_RECEIVER).asInt

                holder.requestAvatar.setImageResource(R.mipmap.ic_launcher)
                holder.requestTitle.text = "Loading info....." // Todo
                holder.requestDetail.text = "New contact request" // Todo
                holder.requestStatus.text = when (request.requestStatus) {
                    "request_pending" -> "Pending"
                    "request_accepted" -> "Accepted"
                    "request_refused" -> "Declined"
                    "request_revoked" -> "Revoked"
                    else -> "Pending"
                }

                if (requestClickListener != null) {
                    if (requester == requestClickListener!!.getUserId()) {
                        holder.requestAccept.visibility = View.GONE
                        holder.requestDecline.visibility = View.GONE
                    } else if (receiver == requestClickListener!!.getUserId() && request.requestStatus == "request_pending") {
                        holder.requestAccept.visibility = View.VISIBLE
                        holder.requestDecline.visibility = View.VISIBLE
                        holder.requestAccept.setOnClickListener {
                            if (position != RecyclerView.NO_POSITION) {
                                requestClickListener!!.onConfirmListener(holder.itemView, request.requestType, request.requestId)
                            }
                        }
                        holder.requestDecline.setOnClickListener {
                            if (position != RecyclerView.NO_POSITION) {
                                requestClickListener!!.onDeclineListener(holder.itemView, request.requestType, request.requestId)
                            }
                        }
                    } else {
                        holder.requestAccept.visibility = View.GONE
                        holder.requestDecline.visibility = View.GONE
                    }
                }
            }
            RequestConstant.REQUEST_GROUP_ADD -> {
                val groupId = request.requestMetadata.get(RequestConstant.REQUEST_GROUP_ADD_GROUP_ID).asInt
                val requester = request.requestMetadata.get(RequestConstant.REQUEST_GROUP_ADD_SENDER).asInt
                val receiver = request.requestMetadata.get(RequestConstant.REQUEST_GROUP_ADD_RECEIVER).asInt

                holder.requestAvatar.setImageResource(R.mipmap.ic_launcher)
                holder.requestTitle.text = "Loading info....." // Todo
                holder.requestDetail.text = String.format("Join group %d request", groupId)// Todo
                holder.requestStatus.text = when (request.requestStatus) {
                    "request_pending" -> "Pending"
                    "request_accepted" -> "Accepted"
                    "request_refused" -> "Declined"
                    "request_revoked" -> "Revoked"
                    else -> "Pending"
                }

                if (requestClickListener != null) {
                    if (requester == requestClickListener!!.getUserId()) {
                        holder.requestAccept.visibility = View.GONE
                        holder.requestDecline.visibility = View.GONE
                    } else if (receiver == requestClickListener!!.getUserId() && request.requestStatus == "request_pending") {
                        holder.requestAccept.visibility = View.VISIBLE
                        holder.requestDecline.visibility = View.VISIBLE
                        holder.requestAccept.setOnClickListener {
                            if (position != RecyclerView.NO_POSITION) {
                                requestClickListener!!.onConfirmListener(holder.itemView, request.requestType, request.requestId)
                            }
                        }
                        holder.requestDecline.setOnClickListener {
                            if (position != RecyclerView.NO_POSITION) {
                                requestClickListener!!.onDeclineListener(holder.itemView, request.requestType, request.requestId)
                            }
                        }
                    } else {
                        holder.requestAccept.visibility = View.GONE
                        holder.requestDecline.visibility = View.GONE
                    }
                }
            }
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
        fun getUserId(): Int
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
