//package com.example.projectmdp.data.repository
//
//import com.example.projectmdp.data.source.dataclass.Chat
//import com.example.projectmdp.data.source.local.dao.ChatDao
//import com.example.projectmdp.data.source.local.entity.ChatEntity
//import com.example.projectmdp.data.source.remote.ChatApi
//import com.example.projectmdp.data.source.response.*
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.flow.flowOf
//import kotlinx.coroutines.flow.toList
//import kotlinx.coroutines.test.runTest
//import org.junit.Assert.*
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.mockito.Mock
//import org.mockito.Mockito.*
//import org.mockito.junit.MockitoJUnitRunner
//import org.mockito.kotlin.any
//import org.mockito.kotlin.whenever
//
//@RunWith(MockitoJUnitRunner::class)
//class ChatRepositoryTest {
//
//    @Mock
//    private lateinit var chatDao: ChatDao
//
//    @Mock
//    private lateinit var chatApi: ChatApi
//
//    private lateinit var chatRepository: ChatRepository
//
//    // Sample test data
//    private val sampleChat = Chat(
//        chat_id = "1",
//        buyer_id = "buyer1",
//        seller_id = "seller1",
//        product_id = "product1",
//        message = "Hello, is this still available?",
//        sender_id = "buyer1",
//        timestamp = "2024-01-01T10:00:00Z",
//        is_read = false
//    )
//
//    private val sampleChatEntity = ChatEntity(
//        chat_id = "1",
//        buyer_id = "buyer1",
//        seller_id = "seller1",
//        product_id = "product1",
//        message = "Hello, is this still available?",
//        sender_id = "buyer1",
//        timestamp = "2024-01-01T10:00:00Z",
//        is_read = false
//    )
//
//    private val sampleResponseChat = com.example.projectmdp.data.source.response.Chat(
//        chat_id = "1",
//        buyer_id = "buyer1",
//        seller_id = "seller1",
//        product_id = "product1",
//        message = "Hello, is this still available?",
//        sender_id = "buyer1",
//        timestamp = "2024-01-01T10:00:00Z",
//        is_read = false
//    )
//
//    @Before
//    fun setup() {
//        chatRepository = ChatRepository(chatDao, chatApi)
//    }
//
//    // ===== sendMessage Tests =====
//
//    @Test
//    fun `sendMessage should send message and cache it successfully`() = runTest {
//        // Arrange
//        val message = "Is this product still available?"
//        val chatResponse = ChatSingleResponse(chat = sampleResponseChat)
//        val apiResponse = BaseResponse<ChatSingleResponse>(
//            message = "Message sent successfully",
//            error = null,
//            data = chatResponse
//        )
//
//        whenever(chatApi.sendMessage(any(), any(), any(), any())).thenReturn(apiResponse)
//
//        // Act
//        val result = chatRepository.sendMessage("buyer1", "seller1", "product1", message).first()
//
//        // Assert
//        assertTrue("Message should be sent successfully", result.isSuccess)
//        val sentChat = result.getOrNull()
//        assertNotNull("Sent chat should not be null", sentChat)
//        assertEquals("Message should match", message, sentChat!!.message)
//        assertEquals("Sender should be buyer1", "buyer1", sentChat.sender_id)
//
//        // Verify message is cached locally
//        verify(chatDao).insert(any<ChatEntity>())
//    }
//
//    @Test
//    fun `sendMessage should return failure when API fails`() = runTest {
//        // Arrange
//        val apiResponse = BaseResponse<ChatSingleResponse>(
//            message = "Failed to send message",
//            error = "Network error",
//            data = null
//        )
//
//        whenever(chatApi.sendMessage(any(), any(), any(), any())).thenReturn(apiResponse)
//
//        // Act
//        val result = chatRepository.sendMessage("buyer1", "seller1", "product1", "message").first()
//
//        // Assert
//        assertTrue("Message sending should fail", result.isFailure)
//        val exception = result.exceptionOrNull()
//        assertTrue("Error should contain network error",
//            exception!!.message!!.contains("Network error"))
//
//        // Verify message is not cached on failure
//        verify(chatDao, never()).insert(any<ChatEntity>())
//    }
//
//    @Test
//    fun `sendMessage should validate message content`() = runTest {
//        // Test empty message
//        val result = chatRepository.sendMessage("buyer1", "seller1", "product1", "").first()
//
//        assertTrue("Should fail for empty message", result.isFailure)
//        val exception = result.exceptionOrNull()
//        assertTrue("Error should contain message validation",
//            exception!!.message!!.contains("Message cannot be empty"))
//
//        // Verify API is not called for invalid input
//        verify(chatApi, never()).sendMessage(any(), any(), any(), any())
//    }
//
//    @Test
//    fun `sendMessage should validate user IDs`() = runTest {
//        // Test with same buyer and seller ID
//        val result = chatRepository.sendMessage("user1", "user1", "product1", "message").first()
//
//        assertTrue("Should fail when buyer and seller are same", result.isFailure)
//        val exception = result.exceptionOrNull()
//        assertTrue("Error should contain user validation",
//            exception!!.message!!.contains("Buyer and seller cannot be the same"))
//    }
//
//    // ===== getChatHistory Tests =====
//
//    @Test
//    fun `getChatHistory should return cached then remote chat history`() = runTest {
//        // Arrange
//        val buyerId = "buyer1"
//        val sellerId = "seller1"
//        val productId = "product1"
//        val cachedChats = listOf(sampleChatEntity)
//        val remoteChats = listOf(sampleResponseChat)
//        val chatResponse = ChatListResponse(chats = remoteChats)
//        val apiResponse = BaseResponse<ChatListResponse>(
//            message = "Success",
//            error = null,
//            data = chatResponse
//        )
//
//        whenever(chatDao.getChatHistory(buyerId, sellerId, productId)).thenReturn(cachedChats)
//        whenever(chatApi.getChatHistory(buyerId, sellerId, productId)).thenReturn(apiResponse)
//
//        // Act
//        val results = chatRepository.getChatHistory(buyerId, sellerId, productId).toList()
//
//        // Assert
//        assertEquals(2, results.size) // Cached + Remote
//
//        // First emission should be cached data
//        assertTrue("First result should be success", results[0].isSuccess)
//        assertEquals("Should have cached chat", 1, results[0].getOrNull()?.size)
//
//        // Second emission should be remote data
//        assertTrue("Second result should be success", results[1].isSuccess)
//        assertEquals("Should have remote chat", 1, results[1].getOrNull()?.size)
//
//        // Verify cache operations
//        verify(chatDao).getChatHistory(buyerId, sellerId, productId)
//        verify(chatDao).insertAll(any<List<ChatEntity>>())
//    }
//
//    @Test
//    fun `getChatHistory should return cached data when remote fails`() = runTest {
//        // Arrange
//        val buyerId = "buyer1"
//        val sellerId = "seller1"
//        val productId = "product1"
//        val cachedChats = listOf(sampleChatEntity)
//        val apiResponse = BaseResponse<ChatListResponse>(
//            message = "Error",
//            error = "Failed to fetch chat history",
//            data = null
//        )
//
//        whenever(chatDao.getChatHistory(buyerId, sellerId, productId)).thenReturn(cachedChats)
//        whenever(chatApi.getChatHistory(buyerId, sellerId, productId)).thenReturn(apiResponse)
//
//        // Act
//        val results = chatRepository.getChatHistory(buyerId, sellerId, productId).toList()
//
//        // Assert
//        assertEquals(2, results.size)
//        assertTrue("First result should be cached data", results[0].isSuccess)
//        assertTrue("Second result should be cached data (fallback)", results[1].isSuccess)
//        assertEquals("Should have cached chat", 1, results[1].getOrNull()?.size)
//    }
//
//    @Test
//    fun `getChatHistory should return error when no cache and remote fails`() = runTest {
//        // Arrange
//        val apiResponse = BaseResponse<ChatListResponse>(
//            message = "Error",
//            error = "Chat history not found",
//            data = null
//        )
//
//        whenever(chatDao.getChatHistory(any(), any(), any())).thenReturn(emptyList())
//        whenever(chatApi.getChatHistory(any(), any(), any())).thenReturn(apiResponse)
//
//        // Act
//        val results = chatRepository.getChatHistory("buyer1", "seller1", "product1").toList()
//
//        // Assert
//        assertEquals(1, results.size)
//        assertTrue("Should return failure", results[0].isFailure)
//        assertTrue("Error should contain not found message",
//            results[0].exceptionOrNull()?.message?.contains("Chat history not found") == true)
//    }
//
//    // ===== getUserChats Tests =====
//
//    @Test
//    fun `getUserChats should return all chats for user`() = runTest {
//        // Arrange
//        val userId = "user1"
//        val userChats = listOf(sampleChatEntity, sampleChatEntity.copy(chat_id = "2"))
//        val remoteChats = listOf(sampleResponseChat, sampleResponseChat.copy(chat_id = "2"))
//        val chatResponse = ChatListResponse(chats = remoteChats)
//        val apiResponse = BaseResponse<ChatListResponse>(
//            message = "Success",
//            error = null,
//            data = chatResponse
//        )
//
//        whenever(chatDao.getUserChats(userId)).thenReturn(userChats)
//        whenever(chatApi.getUserChats(userId)).thenReturn(apiResponse)
//
//        // Act
//        val results = chatRepository.getUserChats(userId).toList()
//
//        // Assert
//        assertEquals(2, results.size) // Cached + Remote
//        assertTrue("First result should be success", results[0].isSuccess)
//        assertEquals("Should have 2 cached chats", 2, results[0].getOrNull()?.size)
//
//        assertTrue("Second result should be success", results[1].isSuccess)
//        assertEquals("Should have 2 remote chats", 2, results[1].getOrNull()?.size)
//    }
//
//    @Test
//    fun `getUserChats should handle empty chat list`() = runTest {
//        // Arrange
//        val userId = "user_with_no_chats"
//        val chatResponse = ChatListResponse(chats = emptyList())
//        val apiResponse = BaseResponse<ChatListResponse>(
//            message = "No chats found",
//            error = null,
//            data = chatResponse
//        )
//
//        whenever(chatDao.getUserChats(userId)).thenReturn(emptyList())
//        whenever(chatApi.getUserChats(userId)).thenReturn(apiResponse)
//
//        // Act
//        val results = chatRepository.getUserChats(userId).toList()
//
//        // Assert
//        assertEquals(1, results.size) // Only remote call since cache is empty
//        assertTrue("Result should be success", results[0].isSuccess)
//        assertEquals("Should have empty chat list", 0, results[0].getOrNull()?.size)
//    }
//
//    // ===== markAsRead Tests =====
//
//    @Test
//    fun `markAsRead should mark message as read successfully`() = runTest {
//        // Arrange
//        val chatId = "1"
//        val apiResponse = BaseResponse<Any>(
//            message = "Message marked as read",
//            error = null,
//            data = null
//        )
//
//        whenever(chatApi.markAsRead(chatId)).thenReturn(apiResponse)
//
//        // Act
//        val result = chatRepository.markAsRead(chatId).first()
//
//        // Assert
//        assertTrue("Mark as read should be successful", result.isSuccess)
//        assertTrue("Result should indicate success", result.getOrNull() == true)
//
//        // Verify local update
//        verify(chatDao).markAsRead(chatId)
//    }
//
//    @Test
//    fun `markAsRead should handle API failure gracefully`() = runTest {
//        // Arrange
//        val chatId = "1"
//        val apiResponse = BaseResponse<Any>(
//            message = "Failed to mark as read",
//            error = "Chat not found",
//            data = null
//        )
//
//        whenever(chatApi.markAsRead(chatId)).thenReturn(apiResponse)
//
//        // Act
//        val result = chatRepository.markAsRead(chatId).first()
//
//        // Assert
//        assertTrue("Mark as read should fail", result.isFailure)
//        val exception = result.exceptionOrNull()
//        assertTrue("Error should contain not found message",
//            exception!!.message!!.contains("Chat not found"))
//
//        // Verify local update is not called on API failure
//        verify(chatDao, never()).markAsRead(chatId)
//    }
//
//    // ===== getUnreadCount Tests =====
//
//    @Test
//    fun `getUnreadCount should return number of unread messages`() = runTest {
//        // Arrange
//        val userId = "user1"
//        val unreadCount = 5
//        whenever(chatDao.getUnreadCount(userId)).thenReturn(unreadCount)
//
//        // Act
//        val result = chatRepository.getUnreadCount(userId)
//
//        // Assert
//        assertEquals("Unread count should match", unreadCount, result)
//        verify(chatDao).getUnreadCount(userId)
//    }
//
//    @Test
//    fun `getUnreadCount should return zero when no unread messages`() = runTest {
//        // Arrange
//        val userId = "user_no_unread"
//        whenever(chatDao.getUnreadCount(userId)).thenReturn(0)
//
//        // Act
//        val result = chatRepository.getUnreadCount(userId)
//
//        // Assert
//        assertEquals("Unread count should be zero", 0, result)
//    }
//
//    // ===== deleteChat Tests =====
//
//    @Test
//    fun `deleteChat should delete chat successfully`() = runTest {
//        // Arrange
//        val chatId = "1"
//        val apiResponse = BaseResponse<Any>(
//            message = "Chat deleted successfully",
//            error = null,
//            data = null
//        )
//
//        whenever(chatApi.deleteChat(chatId)).thenReturn(apiResponse)
//
//        // Act
//        val result = chatRepository.deleteChat(chatId).first()
//
//        // Assert
//        assertTrue("Delete should be successful", result.isSuccess)
//        assertTrue("Result should indicate success", result.getOrNull() == true)
//
//        // Verify local deletion
//        verify(chatDao).deleteChat(chatId)
//    }
//
//    @Test
//    fun `deleteChat should handle deletion failure`() = runTest {
//        // Arrange
//        val chatId = "1"
//        val apiResponse = BaseResponse<Any>(
//            message = "Failed to delete chat",
//            error = "Chat not found or access denied",
//            data = null
//        )
//
//        whenever(chatApi.deleteChat(chatId)).thenReturn(apiResponse)
//
//        // Act
//        val result = chatRepository.deleteChat(chatId).first()
//
//        // Assert
//        assertTrue("Delete should fail", result.isFailure)
//        val exception = result.exceptionOrNull()
//        assertTrue("Error should contain access denied message",
//            exception!!.message!!.contains("Chat not found or access denied"))
//    }
//
//    // ===== Local Operations Tests =====
//
//    @Test
//    fun `getChatHistoryLocal should return flow of cached chats`() = runTest {
//        // Arrange
//        val chats = listOf(sampleChatEntity)
//        whenever(chatDao.getChatHistoryFlow("buyer1", "seller1", "product1"))
//            .thenReturn(flowOf(chats))
//
//        // Act
//        val result = chatRepository.getChatHistoryLocal("buyer1", "seller1", "product1").first()
//
//        // Assert
//        assertEquals("Should have 1 chat", 1, result.size)
//        assertEquals("Chat message should match", "Hello, is this still available?", result[0].message)
//    }
//
//    @Test
//    fun `getUserChatsLocal should return flow of user chats`() = runTest {
//        // Arrange
//        val userChats = listOf(
//            sampleChatEntity,
//            sampleChatEntity.copy(chat_id = "2", message = "Second message")
//        )
//        whenever(chatDao.getUserChatsFlow("user1")).thenReturn(flowOf(userChats))
//
//        // Act
//        val result = chatRepository.getUserChatsLocal("user1").first()
//
//        // Assert
//        assertEquals("Should have 2 chats", 2, result.size)
//        assertEquals("First chat should match", "Hello, is this still available?", result[0].message)
//        assertEquals("Second chat should match", "Second message", result[1].message)
//    }
//
//    @Test
//    fun `clearChatHistory should clear all chats for conversation`() = runTest {
//        // Act
//        chatRepository.clearChatHistory("buyer1", "seller1", "product1")
//
//        // Assert
//        verify(chatDao).clearChatHistory("buyer1", "seller1", "product1")
//    }
//
//    // ===== Real-time Updates Tests =====
//
//    @Test
//    fun `should handle real-time message updates`() = runTest {
//        // Arrange
//        val initialChats = listOf(sampleChatEntity)
//        val newMessage = sampleChatEntity.copy(
//            chat_id = "2",
//            message = "New real-time message",
//            timestamp = "2024-01-01T11:00:00Z"
//        )
//        val updatedChats = listOf(sampleChatEntity, newMessage)
//
//        whenever(chatDao.getChatHistoryFlow("buyer1", "seller1", "product1"))
//            .thenReturn(flowOf(initialChats, updatedChats))
//
//        // Act
//        val results = chatRepository.getChatHistoryLocal("buyer1", "seller1", "product1").toList()
//
//        // Assert
//        assertEquals("Should have 2 emissions", 2, results.size)
//        assertEquals("First emission should have 1 chat", 1, results[0].size)
//        assertEquals("Second emission should have 2 chats", 2, results[1].size)
//        assertEquals("New message should be present", "New real-time message", results[1][1].message)
//    }
//
//    // ===== Error Handling Tests =====
//
//    @Test
//    fun `should handle network timeouts gracefully`() = runTest {
//        // Arrange
//        whenever(chatApi.sendMessage(any(), any(), any(), any()))
//            .thenThrow(RuntimeException("Network timeout"))
//
//        // Act
//        val result = chatRepository.sendMessage("buyer1", "seller1", "product1", "message").first()
//
//        // Assert
//        assertTrue("Should handle timeout error", result.isFailure)
//        val exception = result.exceptionOrNull()
//        assertTrue("Error should contain timeout message",
//            exception!!.message!!.contains("Network timeout"))
//    }
//
//    @Test
//    fun `should handle database errors gracefully`() = runTest {
//        // Arrange
//        whenever(chatDao.getUserChats(any())).thenThrow(RuntimeException("Database error"))
//        val remoteChats = listOf(sampleResponseChat)
//        val chatResponse = ChatListResponse(chats = remoteChats)
//        val apiResponse = BaseResponse<ChatListResponse>(
//            message = "Success",
//            error = null,
//            data = chatResponse
//        )
//        whenever(chatApi.getUserChats(any())).thenReturn(apiResponse)
//
//        // Act
//        val results = chatRepository.getUserChats("user1").toList()
//
//        // Assert
//        assertEquals("Should have 1 result (remote only)", 1, results.size)
//        assertTrue("Should succeed with remote data", results[0].isSuccess)
//        assertEquals("Should have remote chat", 1, results[0].getOrNull()?.size)
//    }
//
//    // ===== Message Validation Tests =====
//
//    @Test
//    fun `should validate message length`() = runTest {
//        // Test very long message
//        val longMessage = "a".repeat(1001) // Assuming 1000 char limit
//        val result = chatRepository.sendMessage("buyer1", "seller1", "product1", longMessage).first()
//
//        assertTrue("Should fail for message too long", result.isFailure)
//        val exception = result.exceptionOrNull()
//        assertTrue("Error should contain length validation",
//            exception!!.message!!.contains("Message too long"))
//    }
//
//    @Test
//    fun `should handle special characters in messages`() = runTest {
//        // Arrange
//        val messageWithSpecialChars = "Hello! 😊 This costs $100. Is it available? 💯"
//        val responseChat = sampleResponseChat.copy(message = messageWithSpecialChars)
//        val chatResponse = ChatSingleResponse(chat = responseChat)
//        val apiResponse = BaseResponse<ChatSingleResponse>(
//            message = "Success",
//            error = null,
//            data = chatResponse
//        )
//
//        whenever(chatApi.sendMessage(any(), any(), any(), any())).thenReturn(apiResponse)
//
//        // Act
//        val result = chatRepository.sendMessage("buyer1", "seller1", "product1", messageWithSpecialChars).first()
//
//        // Assert
//        assertTrue("Should handle special characters", result.isSuccess)
//        val sentMessage = result.getOrNull()
//        assertEquals("Message should preserve special characters", messageWithSpecialChars, sentMessage!!.message)
//    }
//}
