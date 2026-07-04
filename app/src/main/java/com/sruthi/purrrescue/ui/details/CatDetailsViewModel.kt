package com.sruthi.purrrescue.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.sruthi.purrrescue.utils.Constants

class CatDetailsViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> get() = _success

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun markRescued(catId: String, rescuedAt: Long) {
        db.collection(Constants.CAT_COLLECTION)
            .document(catId)
            .update(
                mapOf(
                    "status" to Constants.CAT_RESCUED,
                    "rescuedAt" to rescuedAt
                )
            )
            .addOnSuccessListener {
                _success.value = true
            }
            .addOnFailureListener { e ->
                _error.value = e.message ?: "Failed to update status"
            }
    }
}