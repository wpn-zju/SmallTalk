package edu.syr.smalltalk.ui.main.request

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.ISmallTalkServiceProvider
import edu.syr.smalltalk.service.KVPConstant
import edu.syr.smalltalk.service.android.constant.RequestConstant
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModelFactory
import kotlinx.android.synthetic.main.fragment_requests.*

class RequestListFragment: Fragment(), RequestListAdapter.RequestClickListener {
    private val adapter = RequestListAdapter()

    private val viewModel: SmallTalkViewModel by viewModels {
        SmallTalkViewModelFactory(requireContext().applicationContext as SmallTalkApplication)
    }

    private lateinit var serviceProvider: ISmallTalkServiceProvider

    override fun onAttach(context: Context) {
        super.onAttach(context)

        serviceProvider = requireActivity() as ISmallTalkServiceProvider
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.setRequestClickListener(this)
        recycler_view_request.layoutManager = LinearLayoutManager(context)
        recycler_view_request.adapter = adapter

        val userId: Int = PreferenceManager
            .getDefaultSharedPreferences(requireActivity().applicationContext)
            .getInt(KVPConstant.K_CURRENT_USER_ID, 0)

        viewModel.getCurrentUserInfo(userId).observe(requireActivity(), { user ->
            if (user.isEmpty()) {
                if (serviceProvider.hasService()) {
                    serviceProvider.getService()!!.loadUser()
                }
            } else {
                for (requestId in user[0].requestList) {
                    viewModel.getCurrentRequest(requestId).observe(requireActivity(), { request ->
                        if (request.isEmpty()) {
                            if (serviceProvider.hasService()) {
                                serviceProvider.getService()!!.loadRequest(requestId)
                            }
                        }
                    })
                }
            }
        })

        viewModel.getRequestList(userId).observe(requireActivity(), { requestList ->
            requestList.let {
                adapter.submitList(it)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onConfirmListener(view: View, requestType: String, requestId: Int) {
        when (requestType) {
            RequestConstant.REQUEST_CONTACT_ADD -> {
                if (serviceProvider.hasService()) {
                    serviceProvider.getService()!!.contactAddConfirm(requestId)
                }
            }
            RequestConstant.REQUEST_GROUP_ADD -> {
                if (serviceProvider.hasService()) {
                    serviceProvider.getService()!!.groupAddConfirm(requestId)
                }
            }
        }
    }

    override fun onDeclineListener(view: View, requestType: String, requestId: Int) {
        when (requestType) {
            RequestConstant.REQUEST_CONTACT_ADD -> {
                if (serviceProvider.hasService()) {
                    serviceProvider.getService()!!.contactAddRefuse(requestId)
                }
            }
            RequestConstant.REQUEST_GROUP_ADD -> {
                if (serviceProvider.hasService()) {
                    serviceProvider.getService()!!.groupAddRefuse(requestId)
                }
            }
        }
    }

    override fun onItemClickListener(view: View, requestId: Int) {

    }

    override fun onItemLongClickListener(view: View, requestId: Int) {

    }
}
