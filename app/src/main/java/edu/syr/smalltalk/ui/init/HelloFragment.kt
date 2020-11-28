package edu.syr.smalltalk.ui.init

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import edu.syr.smalltalk.R
import kotlinx.android.synthetic.main.fragment_hello.*

class HelloFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hello, container, false)
        btn_signIn.setOnClickListener {

        }
        btn_signup.setOnClickListener {
            val action = HelloFragmentDirections.actionFragmentHelloToFragmentCreateUser()
            it.findNavController().navigate(action)
        }
        btn_recover_password.setOnClickListener {
            val action = HelloFragmentDirections.actionFragmentHelloToFragmentRecoverPassword()
            it.findNavController().navigate(action)
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HelloFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}