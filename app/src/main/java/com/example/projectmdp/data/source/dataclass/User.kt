package com.example.projectmdp.data.source.dataclass

import android.os.Parcelable
import com.example.projectmdp.data.source.local.entity.UserEntity
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
    companion object{
        fun fromUserEntity(u : UserEntity) = User(u.id, u.username, u.password, u.address, u.email, u.phone_number, u.role)
    }

    fun toUserEntity() = UserEntity(
        id, username, password, address, email, phone_number, role
    )

}