package com.sruthi.purrrescue.ui.myreports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sruthi.purrrescue.adapter.CatListAdapter
import com.sruthi.purrrescue.base.BaseFragment
import com.sruthi.purrrescue.databinding.MyreportsFragmentLayoutBinding
import com.sruthi.purrrescue.ui.home.HomeFragmentDirections
import com.sruthi.purrrescue.utils.Utils

class MyReportsFragment : BaseFragment() {

    private lateinit var binding: MyreportsFragmentLayoutBinding
    private val viewModel: MyReportsViewModel by viewModels()
    private lateinit var adapter: CatListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MyreportsFragmentLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CatListAdapter(emptyList()) { cat ->
            val action = HomeFragmentDirections.homeToCatsDetailScreen(cat)
            findNavController().navigate(action)
        }

        binding.rvCatList.adapter = adapter

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            if (loading) showLoading() else hideLoading()
        }

        viewModel.reports.observe(viewLifecycleOwner) { cats ->
            adapter.updateCatList(cats)
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            Utils.showToast(requireContext(), message)
        }

        viewModel.loadMyReports()
    }
}