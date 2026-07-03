package com.sruthi.purrrescue.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sruthi.purrrescue.data.model.Cat
import com.sruthi.purrrescue.data.repository.CatRepository

class CatDetailsViewModel {
    private var repository = CatRepository()

    private var _cats = MutableLiveData<List<Cat>>()
    val cat: LiveData<List<Cat>> = _cats

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error


}