package com.sruthi.purrrescue.ui.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sruthi.purrrescue.base.BaseFragment
import com.sruthi.purrrescue.databinding.SignupFragmentLayoutBinding
import com.sruthi.purrrescue.utils.Utils

class SignupFragment: BaseFragment() {

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

        val viewModel = SignupViewModel()

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            if (loading) showLoading() else hideLoading()
        }

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(SignupFragmentDirections.signupToLoginScreen())
        }

        binding.btnSignUp.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            viewModel.signUp(email, password,name)

            viewModel.success.observe(viewLifecycleOwner) {
                if (it) {
                    // Navigate to home or show success
                    findNavController().navigate(SignupFragmentDirections.signupToLoginScreen())
                }
            }

            viewModel.error.observe(viewLifecycleOwner) { error ->
                Utils.showToast(requireContext(),error.toString())
            }
        }
    }


}