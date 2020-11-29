package edu.syr.smalltalk.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.ISmallTalkServiceProvider
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModelFactory

class PreferenceFragment : Fragment() {
    private val viewModel: SmallTalkViewModel by viewModels {
        SmallTalkViewModelFactory(requireContext().applicationContext as SmallTalkApplication)
    }

    private lateinit var serviceProvider: ISmallTalkServiceProvider

    override fun onAttach(context: Context) {
        super.onAttach(context)

        serviceProvider = requireActivity() as ISmallTalkServiceProvider
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_preferences_, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PreferenceFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}