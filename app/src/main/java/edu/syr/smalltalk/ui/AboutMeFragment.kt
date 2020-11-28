package edu.syr.smalltalk.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.model.entity.SmallTalkUser
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModelFactory

class AboutMeFragment : Fragment() {
    private val viewModel: SmallTalkViewModel by viewModels {
        SmallTalkViewModelFactory(requireContext().applicationContext as SmallTalkApplication)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }

        // Use LiveData in Fragment
        val n = Observer<SmallTalkUser> { it ->
            Log.v("T", it.userName)
        }
        viewModel.currentUserInfo.observe(viewLifecycleOwner, n);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_about_me, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AboutMeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}