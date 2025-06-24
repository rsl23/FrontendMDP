package com.example.projectmdp.data.model.product

import android.os.Parcelable
import com.example.projectmdp.data.source.local.entity.ProductEntity
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Product(
    val product_id: String = "",
    val name: String = "",
    val price: String = "",
    val description: String = "",
    val category: String = "",
    val image: String = "",
    val user_id: String = "",
    val created_at: Timestamp = Timestamp.now(),
    val updated_at: Timestamp = Timestamp.now(),
    val deleted_at: Timestamp? = null,

    // Additional fields for UI display
    val sellerName: String = "",
    val sellerLocation: String = ""
) : Parcelable {

    companion object {
        fun fromProductEntity(p: ProductEntity): Product {
            return Product(
                product_id = p.id,
                name = p.name,
                price = p.price ?: "",
                description = p.description,
                category = p.category ?: "",
                image = p.image,
                user_id = p.user_id,
                created_at = Timestamp(Date(p.created_at)),
                updated_at = Timestamp(Date(p.updated_at)),
                deleted_at = p.deleted_at?.let { Timestamp(Date(it)) }
            )
        }
    }

    fun toProductEntity() = ProductEntity(
        id = product_id,
        name = name,
        price = price,
        description = description,
        category = category,
        image = image,
        user_id = user_id,
        created_at = created_at.toDate().time,
        updated_at = updated_at.toDate().time,
        deleted_at = deleted_at?.toDate()?.time
    )
}