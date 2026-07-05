package com.sruthi.purrrescue.ui.reports

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sruthi.purrrescue.data.model.Cat
import com.sruthi.purrrescue.data.repository.CatRepository
import kotlinx.coroutines.launch

class ReportCatViewModel: ViewModel() {

    private var repository = CatRepository()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> get() = _success

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun reportCat(cat: Cat, photoUri: Uri,context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.reportCat(cat, photoUri, context)
                _success.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to report cat"
            } finally {
                _isLoading.value = false
            }
        }
    }

}