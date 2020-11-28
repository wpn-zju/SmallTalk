package edu.syr.smalltalk.ui.init

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.syr.smalltalk.R
import kotlinx.android.synthetic.main.fragment_create_user.*

class CreateUserFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btn_new_user_send_passcode.setOnClickListener {

        }

        btn_new_user_next.setOnClickListener {

        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateUserFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}