package com.kotlin.pdfextractor.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kotlin.pdfextractor.adapters.CustomAdapterFiles
import com.kotlin.pdfextractor.comparators.FileComparatorSortDate
import com.kotlin.pdfextractor.comparators.FileComparatorSortName
import com.kotlin.pdfextractor.comparators.FileComparatorSortSize
import com.kotlin.pdfextractor.databinding.FragmentHomeBinding
import com.kotlin.pdfextractor.models.FileItem
import com.kotlin.pdfextractor.viewModels.HomeViewModel
import java.util.*


class Home : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: CustomAdapterFiles

    private fun initializations(view: View) {
        val application = (view.context as Activity).application
        viewModel = ViewModelProvider(this, HomeViewModel.HomeViewModelProviderFactory(application))[HomeViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializations(view)
        initRecyclerView()
        initViewModel()

        binding!!.apply {
            btnSortDateHome.setOnClickListener { onSortClick(0) }
            btnSortNameHome.setOnClickListener { onSortClick(1) }
            btnSortSizeHome.setOnClickListener { onSortClick(2) }
        }
    }

    private fun initRecyclerView() {
        adapter = CustomAdapterFiles()
        binding!!.rvFilesMain.adapter = adapter
    }

    private fun initViewModel() {
        viewModel.fetchPdfFiles()
        viewModel.fileListMutableLiveData.observe(viewLifecycleOwner) {
            binding!!.progressBarHome.visibility = View.GONE
            adapter.submitList(it)
        }
    }

    private fun onSortClick(sortType: Int) {
        when (sortType) {
            0 -> {
                val updatedList: List<FileItem> = ArrayList(adapter.currentList)
                Collections.sort(updatedList, FileComparatorSortDate())
                adapter.submitList(updatedList.asReversed())
            }
            1 -> {
                val updatedList: List<FileItem> = ArrayList(adapter.currentList)
                Collections.sort(updatedList, FileComparatorSortName())
                adapter.submitList(updatedList)
            }
            2 -> {
                val updatedList: List<FileItem> = ArrayList(adapter.currentList)
                Collections.sort(updatedList, FileComparatorSortSize())
                adapter.submitList(updatedList.asReversed())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}