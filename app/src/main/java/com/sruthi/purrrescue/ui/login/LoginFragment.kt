package com.sruthi.purrrescue.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sruthi.purrrescue.databinding.LoginFragmentLayoutBinding

class LoginFragment: Fragment() {

    private lateinit var binding: LoginFragmentLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = LoginFragmentLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnLogin.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.loginToHomeScreen())
        }

        binding.btnSignUp.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.loginToSignupScreen())
        }
    }

}