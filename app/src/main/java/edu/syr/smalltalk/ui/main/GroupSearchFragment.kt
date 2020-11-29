package edu.syr.smalltalk.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.syr.smalltalk.R

class GroupSearchFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_group_search, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            GroupSearchFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
