package com.sruthi.purrrescue.ui.myreports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sruthi.purrrescue.data.model.Cat
import com.sruthi.purrrescue.data.repository.CatRepository
import kotlinx.coroutines.launch

class MyReportsViewModel(
    private val repository: CatRepository = CatRepository()
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _reports = MutableLiveData<List<Cat>>()
    val reports: LiveData<List<Cat>> get() = _reports

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun loadMyReports() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            _error.value = "You need to be logged in to view your reports"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                _reports.value = repository.getCatsReportedByUser(uid)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load your reports"
            } finally {
                _isLoading.value = false
            }
        }
    }
}