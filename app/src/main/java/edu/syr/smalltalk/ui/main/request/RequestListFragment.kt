package edu.syr.smalltalk.ui.main.request

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.ISmallTalkServiceProvider
import edu.syr.smalltalk.service.android.constant.RequestConstant
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModelFactory
import kotlinx.android.synthetic.main.fragment_requests.*

class RequestListFragment: Fragment(), RequestListAdapter.RequestClickListener {
    private val viewModel: SmallTalkViewModel by viewModels {
        SmallTalkViewModelFactory(requireContext().applicationContext as SmallTalkApplication)
    }

    private lateinit var adapter: RequestListAdapter

    private lateinit var serviceProvider: ISmallTalkServiceProvider

    override fun onAttach(context: Context) {
        super.onAttach(context)

        serviceProvider = requireActivity() as ISmallTalkServiceProvider
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = RequestListAdapter(requireContext(), viewLifecycleOwner, viewModel)
        adapter.setRequestClickListener(this)
        recycler_view_request.layoutManager = LinearLayoutManager(requireContext())
        recycler_view_request.adapter = adapter

        SmallTalkApplication.getCurrentUserId(requireContext()).let { userId ->
            viewModel.watchCurrentUserInfo(userId).observe(viewLifecycleOwner) { user ->
                if (user.isNotEmpty()) {
                    for (requestId in user[0].requestList) {
                        viewModel.watchCurrentRequest(requestId).observe(viewLifecycleOwner) { request ->
                            if (request.isEmpty()) {
                                serviceProvider.getService()?.loadRequest(requestId)
                            }
                        }
                    }
                } else {
                    serviceProvider.getService()?.loadUser(userId)
                }
            }

            viewModel.watchRequestList(userId).observe(viewLifecycleOwner) { requestList ->
                adapter.submitList(requestList)
            }
        }
    }

    override fun onConfirmListener(view: View, requestType: String, requestId: Int) {
        when (requestType) {
            RequestConstant.REQUEST_CONTACT_ADD -> {
                serviceProvider.getService()?.contactAddConfirm(requestId)
            }
            RequestConstant.REQUEST_GROUP_ADD -> {
                serviceProvider.getService()?.groupAddConfirm(requestId)
            }
        }
    }

    override fun onDeclineListener(view: View, requestType: String, requestId: Int) {
        when (requestType) {
            RequestConstant.REQUEST_CONTACT_ADD -> {
                serviceProvider.getService()?.contactAddRefuse(requestId)
            }
            RequestConstant.REQUEST_GROUP_ADD -> {
                serviceProvider.getService()?.groupAddRefuse(requestId)
            }
        }
    }

    override fun onRevokeListener(view: View, requestType: String, requestId: Int) {
        when (requestType) {
            RequestConstant.REQUEST_CONTACT_ADD -> {
                serviceProvider.getService()?.contactAddRevoke(requestId)
            }
            RequestConstant.REQUEST_GROUP_ADD -> {
                serviceProvider.getService()?.groupAddRevoke(requestId)
            }
        }
    }

    override fun onItemClickListener(view: View, requestId: Int) {

    }

    override fun onItemLongClickListener(view: View, requestId: Int) {

    }

    override fun loadContact(contactId: Int) {
        serviceProvider.getService()?.loadContact(contactId)
    }

    override fun loadGroup(groupId: Int) {
        serviceProvider.getService()?.loadGroup(groupId)
    }

    override fun onDestroyView() {
        recycler_view_request.adapter = null
        super.onDestroyView()
    }
}
