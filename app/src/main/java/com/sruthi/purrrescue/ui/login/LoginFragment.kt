package com.sruthi.purrrescue.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sruthi.purrrescue.databinding.LoginFragmentLayoutBinding
import com.sruthi.purrrescue.utils.Utils

class LoginFragment : Fragment() {

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

        val viewModel = LoginViewModel()

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            viewModel.login(email, password)

            viewModel.success.observe(viewLifecycleOwner) {
                if (it) {
                    findNavController().navigate(LoginFragmentDirections.loginToHomeScreen())
                }
            }

            viewModel.error.observe(viewLifecycleOwner) { error ->
                Utils.showToast(requireContext(),error.toString())
            }
        }

        binding.btnSignUp.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.loginToSignupScreen())
        }
    }


}