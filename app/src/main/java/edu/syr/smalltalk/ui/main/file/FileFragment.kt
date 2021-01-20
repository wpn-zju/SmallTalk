package edu.syr.smalltalk.ui.main.file

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import edu.syr.smalltalk.R
import edu.syr.smalltalk.service.ISmallTalkServiceProvider
import edu.syr.smalltalk.service.model.logic.SmallTalkApplication
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModel
import edu.syr.smalltalk.service.model.logic.SmallTalkViewModelFactory
import kotlinx.android.synthetic.main.fragment_file.*

class FileFragment: Fragment(), FileAdapter.FileClickListener {
    private val args: FileFragmentArgs by navArgs()

    private val viewModel: SmallTalkViewModel by viewModels {
        SmallTalkViewModelFactory(requireContext().applicationContext as SmallTalkApplication)
    }

    private lateinit var adapter: FileAdapter
    private lateinit var serviceProvider: ISmallTalkServiceProvider

    override fun onAttach(context: Context) {
        super.onAttach(context)

        serviceProvider = requireActivity() as ISmallTalkServiceProvider
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_file, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FileAdapter(viewLifecycleOwner, viewModel)
        adapter.setFileClickListener(this)
        recycler_view_file.layoutManager = LinearLayoutManager(context)
        recycler_view_file.adapter = adapter

        serviceProvider.getService()?.loadFileList(args.firstSelector, args.secondSelector)

        viewModel.watchFileList(args.firstSelector, args.secondSelector).observe(viewLifecycleOwner) { fileList ->
            fileList.let {
                adapter.submitList(it)
            }
        }
    }

    override fun onItemClickListener(view: View, fileLink: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(fileLink)))
    }

    override fun loadContact(contactId: Int) {
        serviceProvider.getService()?.loadContact(contactId)
    }
}