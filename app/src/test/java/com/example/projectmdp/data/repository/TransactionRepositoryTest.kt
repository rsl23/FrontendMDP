//package com.example.projectmdp.data.repository
//
//import com.example.projectmdp.data.source.dataclass.Transaction
//import com.example.projectmdp.data.source.local.dao.TransactionDao
//import com.example.projectmdp.data.source.local.entity.TransactionEntity
//import com.example.projectmdp.data.source.remote.TransactionApi
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
//class TransactionRepositoryTest {
//
//    @Mock
//    private lateinit var transactionDao: TransactionDao
//
//    @Mock
//    private lateinit var transactionApi: TransactionApi
//
//    private lateinit var transactionRepository: TransactionRepository
//
//    // Sample test data
//    private val sampleTransaction = Transaction(
//        transaction_id = "1",
//        buyer_id = "buyer1",
//        seller_id = "seller1",
//        product_id = "product1",
//        quantity = 2,
//        total_price = 200.0,
//        status = "pending",
//        created_at = "2024-01-01T10:00:00Z",
//        updated_at = "2024-01-01T10:00:00Z"
//    )
//
//    private val sampleTransactionEntity = TransactionEntity(
//        transaction_id = "1",
//        buyer_id = "buyer1",
//        seller_id = "seller1",
//        product_id = "product1",
//        quantity = 2,
//        total_price = 200.0,
//        status = "pending",
//        created_at = "2024-01-01T10:00:00Z",
//        updated_at = "2024-01-01T10:00:00Z"
//    )
//
//    private val sampleResponseTransaction = com.example.projectmdp.data.source.response.Transaction(
//        transaction_id = "1",
//        buyer_id = "buyer1",
//        seller_id = "seller1",
//        product_id = "product1",
//        quantity = 2,
//        total_price = 200.0,
//        status = "pending",
//        created_at = "2024-01-01T10:00:00Z",
//        updated_at = "2024-01-01T10:00:00Z"
//    )
//
//    @Before
//    fun setup() {
//        transactionRepository = TransactionRepository(transactionDao, transactionApi)
//    }
//
//    // ===== createTransaction Tests =====
//
//    @Test
//    fun `createTransaction should create transaction successfully`() = runTest {
//        // Arrange
//        val buyerId = "buyer1"
//        val sellerId = "seller1"
//        val productId = "product1"
//        val quantity = 2
//        val totalPrice = 200.0
//
//        val transactionResponse = TransactionSingleResponse(transaction = sampleResponseTransaction)
//        val apiResponse = BaseResponse<TransactionSingleResponse>(
//            message = "Transaction created successfully",
//            error = null,
//            data = transactionResponse
//        )
//
//        whenever(transactionApi.createTransaction(buyerId, sellerId, productId, quantity, totalPrice))
//            .thenReturn(apiResponse)
//
//        // Act
//        val result = transactionRepository.createTransaction(buyerId, sellerId, productId, quantity, totalPrice).first()
//
//        // Assert
//        assertTrue("Transaction creation should be successful", result.isSuccess)
//        val transaction = result.getOrNull()
//        assertNotNull("Transaction should not be null", transaction)
//        assertEquals("Buyer ID should match", buyerId, transaction!!.buyer_id)
//        assertEquals("Seller ID should match", sellerId, transaction.seller_id)
//        assertEquals("Product ID should match", productId, transaction.product_id)
//        assertEquals("Quantity should match", quantity, transaction.quantity)
//        assertEquals("Total price should match", totalPrice, transaction.total_price, 0.01)
//
//        // Verify transaction is cached locally
//        verify(transactionDao).insert(any<TransactionEntity>())
//    }
//
//    @Test
//    fun `createTransaction should validate quantity is positive`() = runTest {
//        // Act
//        val result = transactionRepository.createTransaction("buyer1", "seller1", "product1", 0, 100.0).first()
//
//        // Assert
//        assertTrue("Should fail for zero quantity", result.isFailure)
//        val exception = result.exceptionOrNull()
//        assertTrue("Error should contain quantity validation",
//            exception!!.message!!.contains("Quantity must be greater than 0"))
//
//        // Verify API is not called for invalid input
//        verify(transactionApi, never()).createTransaction(any(), any(), any(), any(), any())
//    }
//
//    @Test
//    fun `createTransaction should validate total price is positive`() = runTest {
//        // Act
//        val result = transactionRepository.createTransaction("buyer1", "seller1", "product1", 1, -10.0).first()
//
//        // Assert
//        assertTrue("Should fail for negative price", result.isFailure)
//        val exception = result.exceptionOrNull()
//        assertTrue("Error should contain price validation",
//            exception!!.message!!.contains("Total price must be greater than 0"))
//    }
//
//    @Test
//    fun `createTransaction should validate buyer and seller are different`() = runTest {
//        // Act
//        val result = transactionRepository.createTransaction("user1", "user1", "product1", 1, 100.0).first()
//
//        // Assert
//        assertTrue("Should fail when buyer and seller are same", result.isFailure)
//        val exception = result.exceptionOrNull()
//        assertTrue("Error should contain user validation",
//            exception!!.message!!.contains("Buyer and seller cannot be the same"))
//    }
//
//    @Test
//    fun `createTransaction should handle API failure`() = runTest {
//        // Arrange
//        val apiResponse = BaseResponse<TransactionSingleResponse>(
//            message = "Transaction failed",
//            error = "Insufficient stock",
//            data = null
//        )
//
//        whenever(transactionApi.createTransaction(any(), any(), any(), any(), any()))
//            .thenReturn(apiResponse)
//
//        // Act
//        val result = transactionRepository.createTransaction("buyer1", "seller1", "product1", 5, 500.0).first()
//
//        // Assert
//        assertTrue("Transaction creation should fail", result.isFailure)
//        val exception = result.exceptionOrNull()
//        assertTrue("Error should contain stock message",
//            exception!!.message!!.contains("Insufficient stock"))
//
//        // Verify transaction is not cached on failure
//        verify(transactionDao, never()).insert(any<TransactionEntity>())
//    }
//
//    // ===== getTransactionById Tests =====
//
//    @Test
//    fun `getTransactionById should return cached then remote transaction`() = runTest {
//        // Arrange
//        val transactionId = "1"
//        val transactionResponse = TransactionSingleResponse(transaction = sampleResponseTransaction)
//        val apiResponse = BaseResponse<TransactionSingleResponse>(
//            message = "Success",
//            error = null,
//            data = transactionResponse
//        )
//
//        whenever(transactionDao.getTransactionById(transactionId)).thenReturn(sampleTransactionEntity)
//        whenever(transactionApi.getTransactionById(transactionId)).thenReturn(apiResponse)
//
//        // Act
//        val results = transactionRepository.getTransactionById(transactionId).toList()
//
//        // Assert
//        assertEquals(2, results.size) // Cached + Remote
//        assertTrue("First result should be cached data", results[0].isSuccess)
//        assertTrue("Second result should be remote data", results[1].isSuccess)
//
//        assertEquals("Transaction ID should match", transactionId, results[0].getOrNull()?.transaction_id)
//        assertEquals("Transaction ID should match", transactionId, results[1].getOrNull()?.transaction_id)
//
//        // Verify cache operations
//        verify(transactionDao).getTransactionById(transactionId)
//        verify(transactionDao).insert(any<TransactionEntity>())
//    }
//
//    @Test
//    fun `getTransactionById should return cached data when remote fails`() = runTest {
//        // Arrange
//        val transactionId = "1"
//        val apiResponse = BaseResponse<TransactionSingleResponse>(
//            message = "Error",
//            error = "Transaction not found",
//            data = null
//        )
//
//        whenever(transactionDao.getTransactionById(transactionId)).thenReturn(sampleTransactionEntity)
//        whenever(transactionApi.getTransactionById(transactionId)).thenReturn(apiResponse)
//
//        // Act
//        val results = transactionRepository.getTransactionById(transactionId).toList()
//
//        // Assert
//        assertEquals(1, results.size) // Only cached data
//        assertTrue("Should return cached data", results[0].isSuccess)
//        assertEquals("Transaction ID should match", transactionId, results[0].getOrNull()?.transaction_id)
//    }
//
//    @Test
//    fun `getTransactionById should return error when not found anywhere`() = runTest {
//        // Arrange
//        val transactionId = "999"
//        val apiResponse = BaseResponse<TransactionSingleResponse>(
//            message = "Error",
//            error = "Transaction not found",
//            data = null
//        )
//
//        whenever(transactionDao.getTransactionById(transactionId)).thenReturn(null)
//        whenever(transactionApi.getTransactionById(transactionId)).thenReturn(apiResponse)
//
//        // Act
//        val results = transactionRepository.getTransactionById(transactionId).toList()
//
//        // Assert
//        assertEquals(1, results.size)
//        assertTrue("Should return failure", results[0].isFailure)
//        assertTrue("Error should contain not found message",
//            results[0].exceptionOrNull()?.message?.contains("Transaction not found") == true)
//    }
//
//    // ===== getUserTransactions Tests =====
//
//    @Test
//    fun `getUserTransactions should return cached then remote transactions`() = runTest {
//        // Arrange
//        val userId = "user1"
//        val cachedTransactions = listOf(sampleTransactionEntity)
//        val remoteTransactions = listOf(sampleResponseTransaction)
//        val transactionResponse = TransactionListResponse(transactions = remoteTransactions)
//        val apiResponse = BaseResponse<TransactionListResponse>(
//            message = "Success",
//            error = null,
//            data = transactionResponse
//        )
//
//        whenever(transactionDao.getUserTransactions(userId)).thenReturn(cachedTransactions)
//        whenever(transactionApi.getUserTransactions(userId)).thenReturn(apiResponse)
//
//        // Act
//        val results = transactionRepository.getUserTransactions(userId).toList()
//
//        // Assert
//        assertEquals(2, results.size) // Cached + Remote
//        assertTrue("First result should be cached data", results[0].isSuccess)
//        assertTrue("Second result should be remote data", results[1].isSuccess)
//
//        assertEquals("Should have 1 cached transaction", 1, results[0].getOrNull()?.size)
//        assertEquals("Should have 1 remote transaction", 1, results[1].getOrNull()?.size)
//
//        // Verify cache operations
//        verify(transactionDao).getUserTransactions(userId)
//        verify(transactionDao).insertAll(any<List<TransactionEntity>>())
//    }
//
//    @Test
//    fun `getUserTransactions should handle empty results`() = runTest {
//        // Arrange
//        val userId = "user_no_transactions"
//        val transactionResponse = TransactionListResponse(transactions = emptyList())
//        val apiResponse = BaseResponse<TransactionListResponse>(
//            message = "No transactions found",
//            error = null,
//            data = transactionResponse
//        )
//
//        whenever(transactionDao.getUserTransactions(userId)).thenReturn(emptyList())
//        whenever(transactionApi.getUserTransactions(userId)).thenReturn(apiResponse)
//
//        // Act
//        val results = transactionRepository.getUserTransactions(userId).toList()
//
//        // Assert
//        assertEquals(1, results.size) // Only remote call since cache is empty
//        assertTrue("Result should be success", results[0].isSuccess)
//        assertEquals("Should have empty transaction list", 0, results[0].getOrNull()?.size)
//    }
//
//    // ===== updateTransactionStatus Tests =====
//
//    @Test
//    fun `updateTransactionStatus should update status successfully`() = runTest {
//        // Arrange
//        val transactionId = "1"
//        val newStatus = "completed"
//        val updatedTransaction = sampleResponseTransaction.copy(
//            status = newStatus,
//            updated_at = "2024-01-01T12:00:00Z"
//        )
//        val transactionResponse = TransactionSingleResponse(transaction = updatedTransaction)
//        val apiResponse = BaseResponse<TransactionSingleResponse>(
//            message = "Status updated successfully",
//            error = null,
//            data = transactionResponse
//        )
//
//        whenever(transactionApi.updateTransactionStatus(transactionId, newStatus)).thenReturn(apiResponse)
//
//        // Act
//        val result = transactionRepository.updateTransactionStatus(transactionId, newStatus).first()
//
//        // Assert
//        assertTrue("Status update should be successful", result.isSuccess)
//        val transaction = result.getOrNull()
//        assertNotNull("Transaction should not be null", transaction)
//        assertEquals("Status should be updated", newStatus, transaction!!.status)
//        assertNotEquals("Updated timestamp should change", sampleTransaction.updated_at, transaction.updated_at)
//
//        // Verify local cache is updated
//        verify(transactionDao).update(any<TransactionEntity>())
//    }
//
//    @Test
//    fun `updateTransactionStatus should validate status transition`() = runTest {
//        // Test invalid status transition
//        val result = transactionRepository.updateTransactionStatus("1", "invalid_status").first()
//
//        assertTrue("Should fail for invalid status", result.isFailure)
//        val exception = result.exceptionOrNull()
//        assertTrue("Error should contain status validation",
//            exception!!.message!!.contains("Invalid transaction status"))
//
//        // Verify API is not called for invalid status
//        verify(transactionApi, never()).updateTransactionStatus(any(), any())
//    }
//
//    @Test
//    fun `updateTransactionStatus should handle unauthorized access`() = runTest {
//        // Arrange
//        val transactionId = "1"
//        val newStatus = "completed"
//        val apiResponse = BaseResponse<TransactionSingleResponse>(
//            message = "Access denied",
//            error = "You are not authorized to update this transaction",
//            data = null
//        )
//
//        whenever(transactionApi.updateTransactionStatus(transactionId, newStatus)).thenReturn(apiResponse)
//
//        // Act
//        val result = transactionRepository.updateTransactionStatus(transactionId, newStatus).first()
//
//        // Assert
//        assertTrue("Should fail for unauthorized access", result.isFailure)
//        val exception = result.exceptionOrNull()
//        assertTrue("Error should contain authorization message",
//            exception!!.message!!.contains("You are not authorized"))
//    }
//
//    // ===== getTransactionsByStatus Tests =====
//
//    @Test
//    fun `getTransactionsByStatus should filter transactions by status`() = runTest {
//        // Arrange
//        val userId = "user1"
//        val status = "pending"
//        val filteredTransactions = listOf(sampleTransactionEntity)
//
//        whenever(transactionDao.getTransactionsByStatus(userId, status)).thenReturn(filteredTransactions)
//
//        // Act
//        val result = transactionRepository.getTransactionsByStatus(userId, status)
//
//        // Assert
//        assertEquals("Should have 1 pending transaction", 1, result.size)
//        assertEquals("Transaction status should match", status, result[0].status)
//        assertEquals("Transaction user should match", userId, result[0].buyer_id)
//    }
//
//    @Test
//    fun `getTransactionsByStatus should handle unknown status`() = runTest {
//        // Arrange
//        val userId = "user1"
//        val unknownStatus = "unknown"
//
//        whenever(transactionDao.getTransactionsByStatus(userId, unknownStatus)).thenReturn(emptyList())
//
//        // Act
//        val result = transactionRepository.getTransactionsByStatus(userId, unknownStatus)
//
//        // Assert
//        assertEquals("Should have no transactions for unknown status", 0, result.size)
//    }
//
//    // ===== cancelTransaction Tests =====
//
//    @Test
//    fun `cancelTransaction should cancel transaction successfully`() = runTest {
//        // Arrange
//        val transactionId = "1"
//        val cancelledTransaction = sampleResponseTransaction.copy(
//            status = "cancelled",
//            updated_at = "2024-01-01T12:00:00Z"
//        )
//        val transactionResponse = TransactionSingleResponse(transaction = cancelledTransaction)
//        val apiResponse = BaseResponse<TransactionSingleResponse>(
//            message = "Transaction cancelled successfully",
//            error = null,
//            data = transactionResponse
//        )
//
//        whenever(transactionApi.cancelTransaction(transactionId)).thenReturn(apiResponse)
//
//        // Act
//        val result = transactionRepository.cancelTransaction(transactionId).first()
//
//        // Assert
//        assertTrue("Cancellation should be successful", result.isSuccess)
//        val transaction = result.getOrNull()
//        assertNotNull("Transaction should not be null", transaction)
//        assertEquals("Status should be cancelled", "cancelled", transaction!!.status)
//
//        // Verify local cache is updated
//        verify(transactionDao).update(any<TransactionEntity>())
//    }
//
//    @Test
//    fun `cancelTransaction should validate cancellation rules`() = runTest {
//        // Arrange
//        val transactionId = "1"
//        val apiResponse = BaseResponse<TransactionSingleResponse>(
//            message = "Cannot cancel",
//            error = "Transaction cannot be cancelled after completion",
//            data = null
//        )
//
//        whenever(transactionApi.cancelTransaction(transactionId)).thenReturn(apiResponse)
//
//        // Act
//        val result = transactionRepository.cancelTransaction(transactionId).first()
//
//        // Assert
//        assertTrue("Cancellation should fail", result.isFailure)
//        val exception = result.exceptionOrNull()
//        assertTrue("Error should contain cancellation rules",
//            exception!!.message!!.contains("Transaction cannot be cancelled"))
//    }
//
//    // ===== Statistics Tests =====
//
//    @Test
//    fun `getUserTransactionStats should return transaction statistics`() = runTest {
//        // Arrange
//        val userId = "user1"
//        val totalTransactions = 10
//        val completedTransactions = 8
//        val pendingTransactions = 2
//        val totalSpent = 1000.0
//
//        whenever(transactionDao.getTotalTransactionsCount(userId)).thenReturn(totalTransactions)
//        whenever(transactionDao.getCompletedTransactionsCount(userId)).thenReturn(completedTransactions)
//        whenever(transactionDao.getPendingTransactionsCount(userId)).thenReturn(pendingTransactions)
//        whenever(transactionDao.getTotalSpentAmount(userId)).thenReturn(totalSpent)
//
//        // Act
//        val stats = transactionRepository.getUserTransactionStats(userId)
//
//        // Assert
//        assertEquals("Total transactions should match", totalTransactions, stats.totalTransactions)
//        assertEquals("Completed transactions should match", completedTransactions, stats.completedTransactions)
//        assertEquals("Pending transactions should match", pendingTransactions, stats.pendingTransactions)
//        assertEquals("Total spent should match", totalSpent, stats.totalSpent, 0.01)
//    }
//
//    @Test
//    fun `getTransactionsByDateRange should filter by date range`() = runTest {
//        // Arrange
//        val userId = "user1"
//        val startDate = "2024-01-01"
//        val endDate = "2024-01-31"
//        val filteredTransactions = listOf(sampleTransactionEntity)
//
//        whenever(transactionDao.getTransactionsByDateRange(userId, startDate, endDate))
//            .thenReturn(filteredTransactions)
//
//        // Act
//        val result = transactionRepository.getTransactionsByDateRange(userId, startDate, endDate)
//
//        // Assert
//        assertEquals("Should have 1 transaction in range", 1, result.size)
//        assertEquals("Transaction should be within date range", "1", result[0].transaction_id)
//    }
//
//    // ===== Local Operations Tests =====
//
//    @Test
//    fun `getUserTransactionsLocal should return flow of cached transactions`() = runTest {
//        // Arrange
//        val userId = "user1"
//        val transactions = listOf(sampleTransactionEntity)
//        whenever(transactionDao.getUserTransactionsFlow(userId)).thenReturn(flowOf(transactions))
//
//        // Act
//        val result = transactionRepository.getUserTransactionsLocal(userId).first()
//
//        // Assert
//        assertEquals("Should have 1 transaction", 1, result.size)
//        assertEquals("Transaction ID should match", "1", result[0].transaction_id)
//    }
//
//    @Test
//    fun `clearTransactionHistory should clear all transactions for user`() = runTest {
//        // Arrange
//        val userId = "user1"
//
//        // Act
//        transactionRepository.clearTransactionHistory(userId)
//
//        // Assert
//        verify(transactionDao).clearUserTransactions(userId)
//    }
//
//    // ===== Error Handling Tests =====
//
//    @Test
//    fun `should handle network errors gracefully`() = runTest {
//        // Arrange
//        whenever(transactionApi.createTransaction(any(), any(), any(), any(), any()))
//            .thenThrow(RuntimeException("Network error"))
//
//        // Act
//        val result = transactionRepository.createTransaction("buyer1", "seller1", "product1", 1, 100.0).first()
//
//        // Assert
//        assertTrue("Should handle network error", result.isFailure)
//        val exception = result.exceptionOrNull()
//        assertTrue("Error should contain network error message",
//            exception!!.message!!.contains("Network error"))
//    }
//
//    @Test
//    fun `should handle database errors gracefully`() = runTest {
//        // Arrange
//        whenever(transactionDao.getUserTransactions(any())).thenThrow(RuntimeException("Database error"))
//        val remoteTransactions = listOf(sampleResponseTransaction)
//        val transactionResponse = TransactionListResponse(transactions = remoteTransactions)
//        val apiResponse = BaseResponse<TransactionListResponse>(
//            message = "Success",
//            error = null,
//            data = transactionResponse
//        )
//        whenever(transactionApi.getUserTransactions(any())).thenReturn(apiResponse)
//
//        // Act
//        val results = transactionRepository.getUserTransactions("user1").toList()
//
//        // Assert
//        assertEquals("Should have 1 result (remote only)", 1, results.size)
//        assertTrue("Should succeed with remote data", results[0].isSuccess)
//        assertEquals("Should have remote transaction", 1, results[0].getOrNull()?.size)
//    }
//
//    // ===== Complex Transaction Flows Tests =====
//
//    @Test
//    fun `should handle complex transaction workflow`() = runTest {
//        // Test complete transaction lifecycle: create -> update -> complete
//
//        // 1. Create transaction
//        val createResponse = TransactionSingleResponse(transaction = sampleResponseTransaction)
//        val createApiResponse = BaseResponse<TransactionSingleResponse>(
//            message = "Created", error = null, data = createResponse
//        )
//        whenever(transactionApi.createTransaction(any(), any(), any(), any(), any())).thenReturn(createApiResponse)
//
//        val createResult = transactionRepository.createTransaction("buyer1", "seller1", "product1", 1, 100.0).first()
//        assertTrue("Transaction creation should succeed", createResult.isSuccess)
//
//        // 2. Update status to processing
//        val processingTransaction = sampleResponseTransaction.copy(status = "processing")
//        val updateResponse = TransactionSingleResponse(transaction = processingTransaction)
//        val updateApiResponse = BaseResponse<TransactionSingleResponse>(
//            message = "Updated", error = null, data = updateResponse
//        )
//        whenever(transactionApi.updateTransactionStatus("1", "processing")).thenReturn(updateApiResponse)
//
//        val updateResult = transactionRepository.updateTransactionStatus("1", "processing").first()
//        assertTrue("Status update should succeed", updateResult.isSuccess)
//        assertEquals("Status should be processing", "processing", updateResult.getOrNull()?.status)
//
//        // 3. Complete transaction
//        val completedTransaction = sampleResponseTransaction.copy(status = "completed")
//        val completeResponse = TransactionSingleResponse(transaction = completedTransaction)
//        val completeApiResponse = BaseResponse<TransactionSingleResponse>(
//            message = "Completed", error = null, data = completeResponse
//        )
//        whenever(transactionApi.updateTransactionStatus("1", "completed")).thenReturn(completeApiResponse)
//
//        val completeResult = transactionRepository.updateTransactionStatus("1", "completed").first()
//        assertTrue("Transaction completion should succeed", completeResult.isSuccess)
//        assertEquals("Status should be completed", "completed", completeResult.getOrNull()?.status)
//
//        // Verify all cache operations
//        verify(transactionDao, times(3)).insert(any<TransactionEntity>()) // Create
//        verify(transactionDao, times(2)).update(any<TransactionEntity>()) // Two updates
//    }
//}
