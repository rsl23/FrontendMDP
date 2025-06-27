package com.example.projectmdp.data.source.dataclass

import android.os.Parcelable
import com.example.projectmdp.data.source.local.entity.UserEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String,
    val email: String,
    val username: String?,
    val address: String = "",
    val phone_number: String = "",
    val role: String = "buyer",
    val firebase_uid: String? = null,
    val profile_picture: String? = null,
    val auth_provider: String = "local",
    val created_at: String,
    val deleted_at: String? = null
): Parcelable {

    companion object {
        fun fromUserEntity(u: UserEntity) = User(
            id = u.id,
            email = u.email,
            username = u.username,
            address = u.address,
            phone_number = u.phone_number,
            role = u.role,
            firebase_uid = u.firebase_uid,
            profile_picture = u.profile_picture,
            auth_provider = u.auth_provider,
            created_at = u.created_at,
            deleted_at = u.deleted_at
        )

        fun empty() = User("", "", null, "", "", "buyer", null, null, "local", "", null)
    }

    fun toUserEntity() = UserEntity(
        id = id,
        email = email,
        username = username,
        address = address,
        phone_number = phone_number,
        role = role,
        firebase_uid = firebase_uid,
        profile_picture = profile_picture,
        auth_provider = auth_provider,
        created_at = created_at,
        deleted_at = deleted_at
    )
}