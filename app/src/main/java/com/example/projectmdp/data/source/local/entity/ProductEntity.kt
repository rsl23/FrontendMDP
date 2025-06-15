package com.example.projectmdp.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "products")
class ProductEntity {
    @PrimaryKey(autoGenerate = false)
    val id: String = ""
    val description: String = ""
    val image: String = ""
    val name: String = ""
    val user_id : String = ""
    val created_at : Long = Date().time
    val updated_at : Long = Date().time
    val deleted_at : Long = Date().time
}