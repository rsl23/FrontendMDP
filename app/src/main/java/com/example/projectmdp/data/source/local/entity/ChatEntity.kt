package com.example.projectmdp.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val user_sender: String,
    val user_receiver: String,
    val chat: String,
    val datetime: String,
    val status: String,
    val created_at: String,
    val updated_at: String?,
    val deleted_at: String?,
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long = System.currentTimeMillis()
)