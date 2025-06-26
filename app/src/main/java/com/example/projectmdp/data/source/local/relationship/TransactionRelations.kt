package com.example.projectmdp.data.source.local.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.example.projectmdp.data.source.dataclass.Transaction
import com.example.projectmdp.data.source.local.entity.ProductEntity
import com.example.projectmdp.data.source.local.entity.TransactionEntity
import com.example.projectmdp.data.source.local.entity.UserEntity
import com.example.projectmdp.data.source.response.UserSeller

data class TransactionWithDetails(
    @Embedded val transaction: TransactionEntity,
    @Relation(
        parentColumn = "user_seller_id",
        entityColumn = "id"
    )
    val seller: UserEntity?,
    @Relation(
        parentColumn = "product_id",
        entityColumn = "product_id"
    )
    val product: ProductEntity?
) {
    // Extension function untuk convert ke domain model
//    fun toTransaction(): Transaction {
//        return Transaction(
//            transaction_id = transaction.transaction_id,
//            user_seller = seller?.let {
//                UserSeller(
//                    id = it.id,
//                    name = it.name,
//                    email = it.email,
//                    phone = it.phone_number,
//                    profile_picture = it.profile_picture
//                )
//            } ?: UserSeller.empty(),
//            email_buyer = transaction.email_buyer,
//            product = product?.let {
//                ProductInfo(
//                    product_id = it.product_id,
//                    name = it.name,
//                    description = it.description ?: "",
//                    price = it.price,
//                    category = it.category ?: "",
//                    image_url = it.image
//                )
//            } ?: ProductInfo.empty(),
//            quantity = transaction.quantity,
//            total_price = transaction.total_price,
//            datetime = transaction.datetime,
//            payment_id = transaction.payment_id,
//            payment_status = transaction.payment_status,
//            payment_description = transaction.payment_description
//        )
//    }
}

// Helper objects untuk empty states
//fun UserSeller.Companion.empty() = UserSeller("", "", "", null, null)
//fun ProductInfo.Companion.empty() = ProductInfo("", "", "", 0.0, "", null)