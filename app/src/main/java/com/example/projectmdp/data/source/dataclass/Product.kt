package com.example.projectmdp.data.source.dataclass

import android.os.Parcelable
import com.example.projectmdp.data.source.local.entity.ProductEntity
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Product(
    val product_id: String,
    val name: String,
    val price: Double,
    val description: String?,
    val category: String = "",              // Default empty string sesuai backend
    val image: String = "",                 // Default empty string sesuai backend
    val user_id: String,                    // Seller ID
    val created_at: String,
    val deleted_at: String? = null
): Parcelable {

    companion object {
        fun fromProductEntity(p: ProductEntity) = Product(
            product_id = p.product_id,
            name = p.name,
            price = p.price,
            description = p.description,
            category = p.category,
            image = p.image,
            user_id = p.user_id,
            created_at = p.created_at,
            deleted_at = p.deleted_at
        )

        fun empty() = Product("", "", 0.0, null, "", "", "", "", null)
    }

    fun toProductEntity() = ProductEntity(
        product_id = product_id,
        name = name,
        price = price,
        description = description,
        category = category,
        image = image,
        user_id = user_id,
        created_at = created_at,
        deleted_at = deleted_at
    )

    // Helper methods
    fun isDeleted() = deleted_at != null
    fun hasImage() = image.isNotEmpty()
    fun hasCategory() = category.isNotEmpty()
}
