package com.example.projectmdp.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "users")
class UserEntity {
    @PrimaryKey(autoGenerate = false)
    val id:String = ""
    val username: String = ""
    val password: String = ""
    val address: String = ""
    val email: String = ""
    val phone_number: String = ""
    val role: String = ""
    val created_at : Long = Date().time
    val updated_at : Long = Date().time
    val deleted_at : Long = Date().time
}