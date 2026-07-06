    package com.sruthi.purrrescue.ui.login

    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import androidx.core.widget.doOnTextChanged
    import androidx.fragment.app.Fragment
    import androidx.navigation.fragment.findNavController
    import com.google.android.material.textfield.TextInputEditText
    import com.google.android.material.textfield.TextInputLayout
    import com.sruthi.purrrescue.R
    import com.sruthi.purrrescue.base.BaseFragment
    import com.sruthi.purrrescue.databinding.LoginFragmentLayoutBinding
    import com.sruthi.purrrescue.utils.Utils
    import com.sruthi.purrrescue.utils.Utils.isInternetAvailable

    class LoginFragment: BaseFragment() {

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

            binding.apply {
                textWatcher(tilEmail, etEmail)
                textWatcher(tilPassword, etPassword)
            }

            viewModel.success.observe(viewLifecycleOwner) {
                if (it && findNavController().currentDestination?.id == R.id.loginFragment) {
                    findNavController().navigate(LoginFragmentDirections.loginToHomeScreen())
                }
            }

            viewModel.error.observe(viewLifecycleOwner) { error ->
                Utils.showToast(requireContext(), error.toString())
            }

            viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
                if (loading) showLoading() else hideLoading()
            }

            binding.btnLogin.setOnClickListener {
                if (!isInternetAvailable(requireContext())) {
                    Utils.showToast(requireContext(), "No internet connection. Please check and try again.")
                    return@setOnClickListener
                }

                if (!validateLogin()) return@setOnClickListener


                val email = binding.etEmail.text.toString().trim()
                val password = binding.etPassword.text.toString().trim()
                viewModel.login(email, password)
            }

            binding.btnSignUp.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.loginToSignupScreen())
            }
        }

        private fun textWatcher(til: TextInputLayout, et: TextInputEditText) {
            et.doOnTextChanged { text, start, before, count ->
                til.error = null
            }
        }

        private fun validateLogin(): Boolean {
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