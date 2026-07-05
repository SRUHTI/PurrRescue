package com.sruthi.purrrescue.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.sruthi.purrrescue.base.BaseFragment
import com.sruthi.purrrescue.databinding.ProfileFragmentLayoutBinding
import com.sruthi.purrrescue.data.repository.AuthRepository
import com.sruthi.purrrescue.utils.Utils

class ProfileFragment: BaseFragment() {

    private lateinit var binding: ProfileFragmentLayoutBinding
    private val repository = AuthRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ProfileFragmentLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindUserInfo()

        binding.tvMyReports.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.profileToMyReportsScreen())
        }

        binding.tvInvite.setOnClickListener {
            Utils.shareAppLink(requireContext())
        }

        binding.tvWhoWeAre.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.profileToAboutUsScreen())
        }

        binding.btnLogOut.setOnClickListener {
            repository.signOut()
            findNavController().navigate(ProfileFragmentDirections.profileToWelcomeScreen())
        }
    }

    private fun bindUserInfo() {
        val user = repository.currentUser()
        binding.tvUserName.text = user?.displayName?.takeIf { it.isNotBlank() } ?: "Cat Friend"
        binding.tvEmail.text = user?.email ?: ""
    }
}