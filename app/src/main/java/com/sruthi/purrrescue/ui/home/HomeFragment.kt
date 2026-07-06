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
import com.sruthi.purrrescue.R
import com.sruthi.purrrescue.adapter.CatListAdapter
import com.sruthi.purrrescue.base.BaseFragment
import com.sruthi.purrrescue.data.repository.AuthRepository
import com.sruthi.purrrescue.databinding.HomeFragmentLayoutBinding
import com.sruthi.purrrescue.utils.Constants
import com.sruthi.purrrescue.utils.Utils
import com.sruthi.purrrescue.utils.Utils.isInternetAvailable
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

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Reported"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Rescued"))

        viewModel.loadTabCounts()

        viewModel.tabCounts.observe(viewLifecycleOwner) { (reportedCount, rescuedCount) ->
            binding.tabLayout.getTabAt(0)?.text = "Reported ($reportedCount)"
            binding.tabLayout.getTabAt(1)?.text = "Rescued ($rescuedCount)"
        }

        viewModel.getCatByStatus(Constants.CAT_REPORTED)

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            if (loading) showLoading() else hideLoading()
        }

        val user = repository.currentUser()
        val name = user?.displayName?.takeIf { it.isNotBlank() } ?: "Cat Friend"

        binding.tvGreetings.text = getGreeting(name)

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> viewModel.getCatByStatus(Constants.CAT_REPORTED)
                    1 -> viewModel.getCatByStatus(Constants.CAT_RESCUED)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        viewModel.getCatByStatus(Constants.CAT_REPORTED)

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

    override fun onResume() {
        super.onResume()
        viewModel.loadTabCounts()
    }

}