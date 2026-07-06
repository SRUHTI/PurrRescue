package com.sruthi.purrrescue.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sruthi.purrrescue.data.model.Cat
import com.sruthi.purrrescue.data.repository.CatRepository
import com.sruthi.purrrescue.utils.Utils.showToast
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private var repository = CatRepository()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var _cats = MutableLiveData<List<Cat>>()
    val cat: LiveData<List<Cat>> = _cats

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _tabCounts = MutableLiveData<Pair<Int, Int>>()
    val tabCounts: LiveData<Pair<Int, Int>> = _tabCounts


    fun loadTabCounts() {
        viewModelScope.launch {
            try {
                val counts = repository.getCatCounts()
                _tabCounts.value = counts
            } catch (e: Exception) {
            }
        }
    }

    fun getCatByStatus(status: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _cats.value = repository.getCatsByStatus(status)
            } catch (e: Exception) {
                _error.value = e.message ?: "Something went wrong"
            } finally {
                _isLoading.value = false
            }
        }
    }

}