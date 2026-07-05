package com.sruthi.purrrescue.ui.home

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.firebase.ktx.Firebase
import com.sruthi.purrrescue.adapter.CatListAdapter
import com.sruthi.purrrescue.base.BaseFragment
import com.sruthi.purrrescue.data.repository.AuthRepository
import com.sruthi.purrrescue.databinding.HomeFragmentLayoutBinding
import com.sruthi.purrrescue.utils.Constants
import com.sruthi.purrrescue.utils.Utils
import java.util.Calendar

class HomeFragment : BaseFragment() {

    private lateinit var binding: HomeFragmentLayoutBinding
    private val viewModel: HomeViewModel by viewModels()
    private val repository = AuthRepository()

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

        viewModel.getCatByStatus(Constants.CAT_REPORTED)

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            if (loading) showLoading() else hideLoading()
        }

        val user = repository.currentUser()
        val name = user?.displayName?.takeIf { it.isNotBlank() } ?: "Cat Friend"

        binding.tvGreetings.text = getGreeting(name)

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
            if (cats.isNullOrEmpty()) {
                binding.rvCatList.visibility = View.GONE
                binding.llNoCatFound.visibility = View.VISIBLE
            } else {
                binding.rvCatList.visibility = View.VISIBLE
                binding.llNoCatFound.visibility = View.GONE

                binding.rvCatList.adapter = CatListAdapter(cats) {
                    val action = HomeFragmentDirections.homeToCatsDetailScreen(it)
                    findNavController().navigate(action)
                }
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            Utils.showToast(requireContext(), message)
        }
    }

    private fun getGreeting(userName: String): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "Good morning, $userName!"
            in 12..16 -> "Good afternoon, $userName!"
            in 17..20 -> "Good evening, $userName!"
            else -> "Good night, $userName!"
        }
    }

}