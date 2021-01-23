package edu.syr.smalltalk.ui.main.request

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
import com.google.gson.Gson
import com.google.gson.JsonObject
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.android.constant.RequestConstant
import edu.syr.smalltalk.service.model.entity.SmallTalkRequest
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel

class RequestListAdapter(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: SmallTalkViewModel)
    : ListAdapter<SmallTalkRequest, RequestListAdapter.RequestListViewHolder>(RequestListDiffCallback()) {

    override fun onBindViewHolder(holder: RequestListViewHolder, position: Int) {
        val request = getItem(position)

        when (request.requestType) {
            RequestConstant.REQUEST_CONTACT_ADD -> {
                val metadata = Gson().fromJson(request.requestMetadata, JsonObject::class.java)
                val requester = metadata.get(RequestConstant.REQUEST_CONTACT_ADD_SENDER).asInt
                val receiver = metadata.get(RequestConstant.REQUEST_CONTACT_ADD_RECEIVER).asInt

                holder.requestDetail.text = context.getString(R.string.new_contact_request_display_text)
                holder.requestStatus.text = displayRequestStatus(context, request.requestStatus)

                viewModel.watchCurrentContact(requester).observe(lifecycleOwner) {
                    if (it.isNotEmpty()) {
                        val requesterInfo = it[0]
                        holder.requestTitle.text = requesterInfo.contactName
                    } else {
                        requestClickListener?.loadContact(requester)
                    }
                }

                when (SmallTalkApplication.getCurrentUserId(context)) {
                    requester -> {
                        viewModel.watchCurrentContact(receiver).observe(lifecycleOwner) {
                            if (it.isNotEmpty()) {
                                val receiverInfo = it[0]
                                SmallTalkApplication.picasso(
                                    receiverInfo.contactAvatarLink,
                                    holder.requestAvatar
                                )
                            } else {
                                requestClickListener?.loadContact(receiver)
                            }
                        }
                        holder.requestAccept.visibility = View.GONE
                        holder.requestDecline.visibility = View.GONE
                        if (request.requestStatus == "request_pending") {
                            holder.requestRevoke.visibility = View.VISIBLE
                            holder.requestRevoke.setOnClickListener {
                                if (position != RecyclerView.NO_POSITION) {
                                    requestClickListener?.onRevokeListener(holder.itemView, request.requestType, request.requestId)
                                }
                            }
                        } else {
                            holder.requestRevoke.visibility = View.GONE
                        }
                    }
                    receiver -> {
                        viewModel.watchCurrentContact(requester).observe(lifecycleOwner) {
                            if (it.isNotEmpty()) {
                                val requesterInfo = it[0]
                                SmallTalkApplication.picasso(
                                    requesterInfo.contactAvatarLink,
                                    holder.requestAvatar
                                )
                            } else {
                                requestClickListener?.loadContact(receiver)
                            }
                        }
                        holder.requestRevoke.visibility = View.GONE
                        if (request.requestStatus == "request_pending") {
                            holder.requestAccept.visibility = View.VISIBLE
                            holder.requestDecline.visibility = View.VISIBLE
                            holder.requestAccept.setOnClickListener {
                                if (position != RecyclerView.NO_POSITION) {
                                    requestClickListener?.onConfirmListener(holder.itemView, request.requestType, request.requestId)
                                }
                            }
                            holder.requestDecline.setOnClickListener {
                                if (position != RecyclerView.NO_POSITION) {
                                    requestClickListener?.onDeclineListener(holder.itemView, request.requestType, request.requestId)
                                }
                            }
                        } else {
                            holder.requestAccept.visibility = View.GONE
                            holder.requestDecline.visibility = View.GONE
                        }
                    }
                    else -> {
                        holder.requestAvatar.setImageResource(R.mipmap.ic_smalltalk)
                        holder.requestAccept.visibility = View.GONE
                        holder.requestDecline.visibility = View.GONE
                        holder.requestRevoke.visibility = View.GONE
                    }
                }
            }
            RequestConstant.REQUEST_GROUP_ADD -> {
                val metadata = Gson().fromJson(request.requestMetadata, JsonObject::class.java)
                val groupId = metadata.get(RequestConstant.REQUEST_GROUP_ADD_GROUP_ID).asInt
                val requester = metadata.get(RequestConstant.REQUEST_GROUP_ADD_SENDER).asInt
                val receiver = metadata.get(RequestConstant.REQUEST_GROUP_ADD_RECEIVER).asInt

                holder.requestStatus.text = displayRequestStatus(context, request.requestStatus)

                viewModel.watchCurrentContact(requester).observe(lifecycleOwner) {
                    if (it.isNotEmpty()) {
                        val requesterInfo = it[0]
                        holder.requestTitle.text = requesterInfo.contactName
                    } else {
                        requestClickListener?.loadContact(requester)
                    }
                }

                viewModel.watchCurrentGroup(groupId).observe(lifecycleOwner) {
                    if (it.isNotEmpty()) {
                        val groupInfo = it[0]
                        holder.requestDetail.text = String.format(
                            context.getString(R.string.new_member_request_template),
                            context.getString(R.string.new_member_request_display_text),
                            groupInfo.groupName)
                    } else {
                        requestClickListener?.loadGroup(groupId)
                    }
                }

                when (SmallTalkApplication.getCurrentUserId(context)) {
                    requester -> {
                        viewModel.watchCurrentGroup(groupId).observe(lifecycleOwner) {
                            if (it.isNotEmpty()) {
                                val groupInfo = it[0]
                                SmallTalkApplication.picasso(
                                    groupInfo.groupAvatarLink,
                                    holder.requestAvatar
                                )
                            } else {
                                requestClickListener?.loadContact(receiver)
                            }
                        }
                        holder.requestAccept.visibility = View.GONE
                        holder.requestDecline.visibility = View.GONE
                        if (request.requestStatus == "request_pending") {
                            holder.requestRevoke.visibility = View.VISIBLE
                            holder.requestRevoke.setOnClickListener {
                                if (position != RecyclerView.NO_POSITION) {
                                    requestClickListener?.onRevokeListener(holder.itemView, request.requestType, request.requestId)
                                }
                            }
                        } else {
                            holder.requestRevoke.visibility = View.GONE
                        }
                    }
                    receiver -> {
                        viewModel.watchCurrentContact(requester).observe(lifecycleOwner) {
                            if (it.isNotEmpty()) {
                                val requesterInfo = it[0]
                                SmallTalkApplication.picasso(
                                    requesterInfo.contactAvatarLink,
                                    holder.requestAvatar
                                )
                            } else {
                                requestClickListener?.loadContact(receiver)
                            }
                        }
                        holder.requestRevoke.visibility = View.GONE
                        if (request.requestStatus == "request_pending") {
                            holder.requestAccept.visibility = View.VISIBLE
                            holder.requestDecline.visibility = View.VISIBLE
                            holder.requestAccept.setOnClickListener {
                                if (position != RecyclerView.NO_POSITION) {
                                    requestClickListener?.onConfirmListener(holder.itemView, request.requestType, request.requestId)
                                }
                            }
                            holder.requestDecline.setOnClickListener {
                                if (position != RecyclerView.NO_POSITION) {
                                    requestClickListener?.onDeclineListener(holder.itemView, request.requestType, request.requestId)
                                }
                            }
                        } else {
                            holder.requestAccept.visibility = View.GONE
                            holder.requestDecline.visibility = View.GONE
                        }
                    }
                    else -> {
                        holder.requestAvatar.setImageResource(R.mipmap.ic_smalltalk)
                        holder.requestAccept.visibility = View.GONE
                        holder.requestDecline.visibility = View.GONE
                        holder.requestRevoke.visibility = View.GONE
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
        val requestAvatar: ImageView = view.findViewById(R.id.request_avatar)
        val requestTitle: TextView = view.findViewById(R.id.request_title)
        val requestDetail: TextView = view.findViewById(R.id.request_detail)
        val requestStatus: TextView = view.findViewById(R.id.request_status)
        val requestAccept: TextView = view.findViewById(R.id.request_btn_confirm)
        val requestDecline: TextView = view.findViewById(R.id.request_btn_refuse)
        val requestRevoke: TextView = view.findViewById(R.id.request_btn_revoke)
    }

    private var requestClickListener: RequestClickListener? = null

    fun setRequestClickListener(listener: RequestClickListener) {
        requestClickListener = listener
    }

    interface RequestClickListener {
        fun onConfirmListener(view: View, requestType: String, requestId: Int)
        fun onDeclineListener(view: View, requestType: String, requestId: Int)
        fun onRevokeListener(view: View, requestType: String, requestId: Int)
        fun onItemClickListener(view: View, requestId: Int)
        fun onItemLongClickListener(view: View, requestId: Int)
        fun loadContact(contactId: Int)
        fun loadGroup(groupId: Int)
    }

    companion object {
        private fun displayRequestStatus(context: Context, status: String): String {
            return when (status) {
                "request_pending" -> context.getString(R.string.request_pending)
                "request_accepted" -> context.getString(R.string.request_accepted)
                "request_refused" -> context.getString(R.string.request_declined)
                "request_revoked" -> context.getString(R.string.request_revoked)
                else -> context.getString(R.string.request_pending)
            }
        }
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
