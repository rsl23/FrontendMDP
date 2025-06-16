package com.example.projectmdp.data.source.dataclass

import android.os.Parcelable
import com.example.projectmdp.data.source.local.entity.ProductEntity
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Product(
    val id: String,
    var description: String,
    var image: String,
    var name: String,
    var user_id: String,
    var created_at: Date = Date(),
    var updated_at: Date = Date(),
    var deleted_at: Date? = null
): Parcelable {
    companion object{
        fun fromProductEntity(p : ProductEntity) =
            Product(p.id, p.description, p.image, p.name, p.user_id, Date(p.created_at), Date(p.updated_at), if(p.deleted_at == null) null else Date(p.created_at))
    }
    fun toProductEntity() = ProductEntity(
        id, description, image, name, user_id, created_at.time, updated_at.time, deleted_at?.time
    )
}
