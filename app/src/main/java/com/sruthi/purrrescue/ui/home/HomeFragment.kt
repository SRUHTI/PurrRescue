package com.sruthi.purrrescue.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayout
import com.sruthi.purrrescue.adapter.CatListAdapter
import com.sruthi.purrrescue.databinding.HomeFragmentLayoutBinding
import com.sruthi.purrrescue.utils.Constants
import com.sruthi.purrrescue.utils.Utils

class HomeFragment : Fragment() {

    private lateinit var binding: HomeFragmentLayoutBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = HomeFragmentLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Reported"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Rescued"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

                when (tab?.position) {
                    0 -> {
                        viewModel.getCatByStatus(Constants.CAT_REPORTED)
                    }

                    1 -> {
                        viewModel.getCatByStatus(Constants.CAT_RESCUED)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        viewModel.cat.observe(viewLifecycleOwner) { cats ->
            binding.rvCatList.adapter = CatListAdapter(cats)
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            Utils.showToast(requireContext(), message)
        }
    }

}