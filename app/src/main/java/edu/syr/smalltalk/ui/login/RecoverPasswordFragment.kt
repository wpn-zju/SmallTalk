package edu.syr.smalltalk.ui.login

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
import kotlinx.android.synthetic.main.fragment_recover_password.*

class RecoverPasswordFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_recover_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_recover_send_passcode.setOnClickListener {
            if (serviceProvider.hasService()) {
                serviceProvider.getService()!!
                    .userRecoverPasswordPasscodeRequest(input_recover_email.text.toString())
            }
        }

        btn_recover_next.setOnClickListener {
            if (serviceProvider.hasService()) {
                serviceProvider.getService()!!.userRecoverPassword(
                    input_recover_email.text.toString(),
                    input_recover_password.text.toString(),
                    input_recover_passcode.text.toString())
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            RecoverPasswordFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}