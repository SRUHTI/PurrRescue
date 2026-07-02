package com.sruthi.purrrescue.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sruthi.purrrescue.data.model.Cat
import com.sruthi.purrrescue.utils.Constants
import kotlinx.coroutines.tasks.await

class CatRepository {

    private val firestore = FirebaseFirestore.getInstance().collection(Constants.CAT_COLLECTION)

    suspend fun getCatsByStatus(status: String): List<Cat> {
        return firestore
            .whereEqualTo("status", status)
            .get()
            .await()
            .toObjects(Cat::class.java)
    }

}