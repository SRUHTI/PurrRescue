package com.sruthi.purrrescue.ui.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.sruthi.purrrescue.R
import com.sruthi.purrrescue.databinding.WelcomeFragmentLayoutBinding

class WelcomeFragment : Fragment() {

    private lateinit var binding: WelcomeFragmentLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = WelcomeFragmentLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (FirebaseAuth.getInstance().currentUser != null) {
            findNavController().navigate(
                R.id.home,
                null,
                androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(R.id.nav_graph, true)
                    .build()
            )
            return
        }

        // existing welcome screen click listeners go here
        binding.btnLogin.setOnClickListener {
            findNavController().navigate(WelcomeFragmentDirections.homeToLoginScreen())
        }

        binding.btnSignUp.setOnClickListener {
            findNavController().navigate(WelcomeFragmentDirections.homeToSignupScreen())
        }
    }
}