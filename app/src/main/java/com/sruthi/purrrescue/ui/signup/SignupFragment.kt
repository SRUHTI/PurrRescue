package com.sruthi.purrrescue.ui.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sruthi.purrrescue.databinding.SignupFragmentLayoutBinding

class SignupFragment: Fragment() {

    private lateinit var binding: SignupFragmentLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = SignupFragmentLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(SignupFragmentDirections.signupToLoginScreen())
        }

        binding.btnSignUp.setOnClickListener {
        }

    }

}