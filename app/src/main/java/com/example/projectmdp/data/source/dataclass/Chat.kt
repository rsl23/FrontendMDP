package com.example.projectmdp.data.source.dataclass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatMessage(
    val id: String,
    val user_sender: String,
    val user_receiver: String,
    val chat: String,                       // Message content
    val datetime: String,
    val status: String,                     // "sent", "delivered", "read"
    val created_at: String,
    val updated_at: String? = null,
    val deleted_at: String? = null
): Parcelable {

    companion object {
        fun empty() = ChatMessage("", "", "", "", "", "sent", "", null, null)
    }

    // Helper methods
    fun isSent() = status == "sent"
    fun isDelivered() = status == "delivered"
    fun isRead() = status == "read"
    fun isDeleted() = deleted_at != null

    fun isFromUser(userId: String) = user_sender == userId
    fun isToUser(userId: String) = user_receiver == userId
}

@Parcelize
data class Conversation(
    val otherUserId: String,
    val otherUser: User?,                   // Full User object
    val lastMessage: String,
    val lastMessageTime: String,
    val lastMessageStatus: String,
    val lastMessageSender: String
): Parcelable {

    companion object {
        fun empty() = Conversation("", null, "", "", "sent", "")
    }

    // Helper methods
    fun hasUnreadMessages(currentUserId: String) =
        lastMessageSender != currentUserId && lastMessageStatus != "read"

    fun getDisplayName() = otherUser?.username ?: "Unknown User"
    fun getProfilePicture() = otherUser?.profile_picture
}

@Parcelize
data class ChatPagination(
    val currentPage: Int,
    val totalPages: Int,
    val totalMessages: Int,
    val hasNext: Boolean,
    val hasPrev: Boolean,
    val limit: Int
): Parcelable