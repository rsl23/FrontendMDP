package com.example.projectmdp.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

//@Entity(tableName = "products")
//class ProductEntity (
//    @PrimaryKey(autoGenerate = false)
//    val id: String,
//    val description: String ,
//    val image: String ,
//    val name: String ,
//    val user_id : String ,
////    val created_at : Long = Date().time,
////    val updated_at : Long = Date().time,
//    val deleted_at : Long? = null
//)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = false)
    val product_id: String,
    val name: String,
    val price: Double,
    val description: String?,
    val category: String,
    val image: String,
    val user_id: String,
    val created_at: String,
    val deleted_at: String?,
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long = System.currentTimeMillis()
)