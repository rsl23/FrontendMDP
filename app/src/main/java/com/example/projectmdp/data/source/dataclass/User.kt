package com.example.projectmdp.data.source.dataclass

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String,
    var username: String,
    var password: String,
    var address: String,
    var email: String,
    var phone_number: String,
    var role: String,
): Parcelable {
    init {
    }
}