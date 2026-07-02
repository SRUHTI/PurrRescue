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

    private var _cats = MutableLiveData<List<Cat>>()
    val cat: LiveData<List<Cat>> = _cats
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error


    fun getCatByStatus(status: String) {
        viewModelScope.launch {

            try {
                _cats.value = repository.getCatsByStatus(status)
            } catch (e: Exception) {
                _error.value = e.message ?: "Something went wrong"
            }
        }
    }

}