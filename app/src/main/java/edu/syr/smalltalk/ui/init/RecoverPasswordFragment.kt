package edu.syr.smalltalk.ui.init

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.syr.smalltalk.R
import edu.syr.smalltalk.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_recover_password.*

class RecoverPasswordFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recover_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_recover_send_passcode.setOnClickListener {

        }

        btn_recover_next.setOnClickListener {
            startActivity(Intent(activity, MainActivity::class.java))
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecoverPasswordFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}