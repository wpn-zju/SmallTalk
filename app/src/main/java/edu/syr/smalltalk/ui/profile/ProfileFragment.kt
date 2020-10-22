package edu.syr.smalltalk.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import edu.syr.smalltalk.R

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_about_me, container, false)
//    val textView: TextView = root.findViewById(R.id.text_notifications)
//    notificationsViewModel.text.observe(viewLifecycleOwner, Observer {
//      textView.text = it
//    })
        return root
    }
}