package com.example.projectmdp.ui.module.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectmdp.data.repository.ChatRepository
import com.example.projectmdp.data.source.dataclass.Conversation
import com.example.projectmdp.data.source.dataclass.User // You might not need this if Conversation already has User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _conversations = MutableStateFlow<List<ConversationItem>>(emptyList())
    val conversations: StateFlow<List<ConversationItem>> = _conversations

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    var errorMessage: String? = null
    init {
        loadConversations()
    }
    fun loadConversations() {
        _isLoading.value = true
        viewModelScope.launch {
            Log.d("ChatListViewModel", "Loading conversations...")
            chatRepository.getUserConversations().collect { result ->
                result.onSuccess { list ->
                    Log.d("ChatListViewModel", "Received conversations: $list")
                    val items = list.map { conversation ->
                        ConversationItem(
                            otherUserId = conversation.otherUserId,
                            otherUser = UserItem(
                                id = conversation.otherUser?.id ?: "",
                                name = conversation.otherUser?.username ?: "Unknown",
                                profilePicture = conversation.otherUser?.profile_picture
                            ),
                            lastMessage = conversation.lastMessage ?: "",
                            lastMessageTime = conversation.lastMessageTime ?: "",
                            unreadCount = 0 // Replace with real unread count if available
                        )
                    }
                    Log.d("ChatListViewModel", "Mapped conversations: $items")
                    _conversations.value = items
                }.onFailure {
                    errorMessage = it.localizedMessage ?: "Failed to load conversations"
                    Log.e("ChatListViewModel", "Error loading conversations: ${it.localizedMessage}")
                }
                _isLoading.value = false
            }
        }
    }
}