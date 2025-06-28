package com.example.projectmdp.ui.module.chat

import androidx.compose.runtime.mutableStateListOf

class ChatViewModel {
    private val _messages = mutableStateListOf<Message>()
    val messages: List<Message> = _messages
    
}