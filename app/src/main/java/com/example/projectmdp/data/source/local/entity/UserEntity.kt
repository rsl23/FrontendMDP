package com.example.projectmdp.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

//@Entity(tableName = "users")
//class UserEntity (
//    @PrimaryKey(autoGenerate = false)
//    val id:String,
//    val username: String,
////    val password: String,
//    val address: String,
//    val email: String,
//    val phone_number: String,
//    val role: String,
////    val created_at : Long = Date().time,
////    val updated_at : Long = Date().time,
////    val deleted_at : Long? = null,
//)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val email: String,
    val username: String?,
    val address: String,
    val phone_number: String,
    val role: String,
    val firebase_uid: String?,
    val profile_picture: String?,
    val auth_provider: String,
    val created_at: String,
    val deleted_at: String?,
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long = System.currentTimeMillis()
)