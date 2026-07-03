package com.sruthi.purrrescue.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sruthi.purrrescue.data.repository.AuthRepository
import kotlinx.coroutines.launch

class SignupViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> get() = _success

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.signUp(email, password)
            if (result.isSuccess) {
                _success.value = true
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Sign up failed"
            }
        }
    }
}
