package com.sruthi.purrrescue.ui.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sruthi.purrrescue.base.BaseFragment
import com.sruthi.purrrescue.databinding.SignupFragmentLayoutBinding
import com.sruthi.purrrescue.utils.Utils
import com.sruthi.purrrescue.utils.Utils.isInternetAvailable

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

        binding.apply {
            textWatcher(tilName, etName)
            textWatcher(tilEmail, etEmail)
            textWatcher(tilPassword, etPassword)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            if (loading) showLoading() else hideLoading()
        }

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(SignupFragmentDirections.signupToLoginScreen())
        }

        binding.btnSignUp.setOnClickListener {
            if (!isInternetAvailable(requireContext())) {
                Utils.showToast(requireContext(), "No internet connection. Please check and try again.")
                return@setOnClickListener
            }

            if (!validateSignUp()) return@setOnClickListener

            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            viewModel.signUp(email, password,name)

            viewModel.success.observe(viewLifecycleOwner) {
                if (it) {
                    findNavController().navigate(SignupFragmentDirections.signupToLoginScreen())
                }
            }

            viewModel.error.observe(viewLifecycleOwner) { error ->
                Utils.showToast(requireContext(),error.toString())
            }
        }
    }


    private fun textWatcher(til: TextInputLayout, et: TextInputEditText) {
        et.doOnTextChanged { text, start, before, count ->
            til.error = null
        }
    }

    private fun validateSignUp(): Boolean {
        if (binding.etName.text.toString().trim().isEmpty()) {
            binding.tilName.error = "Please enter valid Name"
            return false
        }

        if (binding.etEmail.text.toString().trim().isEmpty()) {
            binding.tilEmail.error = "Please enter valid E-mail"
            return false
        }

        if (binding.etPassword.text.toString().trim().isEmpty()) {
            binding.tilPassword.error = "Please enter password"
            return false
        }
        return true
    }


}