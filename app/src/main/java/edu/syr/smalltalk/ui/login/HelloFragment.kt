package edu.syr.smalltalk.ui.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.ISmallTalkServiceProvider
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModelFactory
import kotlinx.android.synthetic.main.fragment_hello.*

class HelloFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_hello, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_signIn.setOnClickListener {
            // TODO: 2. change to BC
            if (serviceProvider.hasService()) {
                serviceProvider.getService()!!
                    .userSignIn(input_email.text.toString(), input_password.text.toString())
                // serviceProvider.getService()!!.userSignIn("","")
            }
        }
        btn_signup.setOnClickListener {
            val action = HelloFragmentDirections.actionFragmentHelloToFragmentCreateUser()
            it.findNavController().navigate(action)
        }
        btn_recover_password.setOnClickListener {
            val action = HelloFragmentDirections.actionFragmentHelloToFragmentRecoverPassword()
            it.findNavController().navigate(action)
        }
    }
}
