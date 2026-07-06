package com.sruthi.purrrescue.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cat(
    val catId: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val country: String = "",
    val state: String = "",
    val city: String = "",
    val street: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val status: String = "Reported",
    val reportedBy: String = "",
    val reportedByName: String = "",
    val reportedAt: Long = 0L,

    val rescuedBy: String? = null,
    val rescuedOn: Long? = null
): Parcelable
