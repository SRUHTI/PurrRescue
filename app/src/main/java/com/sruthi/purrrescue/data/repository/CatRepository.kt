package com.sruthi.purrrescue.data.repository

import ImgUploader
import android.content.Context
import android.net.Uri
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sruthi.purrrescue.data.model.Cat
import com.sruthi.purrrescue.utils.Constants
import kotlinx.coroutines.tasks.await

class CatRepository {

    private val firestore = Firebase.firestore.collection(Constants.CAT_COLLECTION)

    suspend fun getCatsReportedByUser(uid: String): List<Cat> {
        return firestore
            .whereEqualTo("reportedBy", uid)
            .get()
            .await()
            .toObjects(Cat::class.java)
    }

    suspend fun getCatsByStatus(status: String): List<Cat> {
        return firestore
            .whereEqualTo("status", status)
            .get()
            .await()
            .toObjects(Cat::class.java)
    }


    suspend fun reportCat(cat: Cat, photoUri: Uri?, context: Context) {
        val imageUrl = if (photoUri != null) {
            try {
                ImgUploader.uploadImage(context, photoUri)
            } catch (e: Exception) {
                ""
            }
        } else {
            ""
        }

        val updatedCat = cat.copy(imageUrl = imageUrl)
        firestore.document(updatedCat.catId).set(updatedCat).await()
    }

   /* suspend fun reportCat(cat: Cat, photoUri: Uri) {

         val storageRef = Firebase.storage.reference.child("cat_images/${cat.catId}.jpg")
         storageRef.putFile(photoUri).await()
         val downloadUrl = storageRef.downloadUrl.await()

        val updatedCat = cat.copy(downloadUrl.toString())
        firestore.document(updatedCat.catId).set(updatedCat).await()
    }*/
}