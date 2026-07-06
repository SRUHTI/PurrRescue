package com.sruthi.purrrescue.ui.myreports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sruthi.purrrescue.R
import com.sruthi.purrrescue.adapter.CatListAdapter
import com.sruthi.purrrescue.base.BaseFragment
import com.sruthi.purrrescue.databinding.MyreportsFragmentLayoutBinding
import com.sruthi.purrrescue.ui.home.HomeFragmentDirections
import com.sruthi.purrrescue.utils.Utils
import com.sruthi.purrrescue.utils.Utils.isInternetAvailable

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


        if (!isInternetAvailable(requireContext())) {
            Utils.showToast(requireContext(), getString(R.string.no_internet_connection))

            binding.rvCatList.visibility = View.GONE
            binding.llNoCatFound.visibility = View.VISIBLE
            binding.tvEmptyMessageOne.text = getString(R.string.no_internet_connection)
            binding.tvEmptyMessageTwo.text = getString(R.string.please_check_your_network_and_try_again)
            binding.ivEmpty.setImageResource(R.drawable.no_internet)
            binding.ivDownArrow.visibility = View.GONE
            return
        }

        binding.ivArrowBack.setOnClickListener {
            findNavController().popBackStack(R.id.profile, false)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            if (loading) showLoading() else hideLoading()
        }

        adapter = CatListAdapter(emptyList()) { cat ->
            val action = MyReportsFragmentDirections.reportsToCatsDetailScreen(cat)
            findNavController().navigate(action)
        }
        binding.rvCatList.adapter = adapter

        viewModel.reports.observe(viewLifecycleOwner) { cats ->
            if (cats.isNullOrEmpty()) {
                binding.rvCatList.visibility = View.GONE
                binding.llNoCatFound.visibility = View.VISIBLE
            } else {
                binding.rvCatList.visibility = View.VISIBLE
                binding.llNoCatFound.visibility = View.GONE
                adapter.updateCatList(cats)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            Utils.showToast(requireContext(), message)
        }

        viewModel.loadMyReports()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadMyReports()
    }

}