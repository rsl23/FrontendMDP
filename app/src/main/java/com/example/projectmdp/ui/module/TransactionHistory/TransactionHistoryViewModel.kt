package com.example.projectmdp.ui.module.TransactionHistory

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class Transaction(
    val transactionId: String = "",
    val productId: String = "",
    val productName: String = "",
    val amount: Double = 0.0,
    val status: TransactionStatus = TransactionStatus.PENDING,
    val timestamp: Long = 0L,
    val sellerId: String = "",
    val sellerName: String = "",
    val buyerId: String = ""
)

enum class TransactionStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    CANCELLED
}

data class TransactionHistoryUiState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class TransactionHistoryViewModel @Inject constructor() : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(TransactionHistoryUiState())
    val uiState: StateFlow<TransactionHistoryUiState> = _uiState.asStateFlow()

    init {
        loadTransactionHistory()
    }

    private fun loadTransactionHistory() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            viewModelScope.launch {
                try {
                    _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

                    // Query transactions where the current user is the buyer
                    val transactionsSnapshot = firestore.collection("transactions")
                        .whereEqualTo("buyer_id", currentUser.uid)
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .get()
                        .await()

                    val transactions = mutableListOf<Transaction>()

                    for (document in transactionsSnapshot.documents) {
                        val data = document.data
                        if (data != null) {
                            // Get seller information
                            val sellerId = data["seller_id"] as? String ?: ""
                            var sellerName = ""

                            if (sellerId.isNotEmpty()) {
                                try {
                                    val sellerDoc = firestore.collection("users")
                                        .document(sellerId)
                                        .get()
                                        .await()
                                    sellerName = sellerDoc.data?.get("username") as? String ?: "Unknown Seller"
                                } catch (e: Exception) {
                                    Log.w("TransactionHistoryViewModel", "Could not fetch seller name for $sellerId", e)
                                    sellerName = "Unknown Seller"
                                }
                            }

                            val transaction = Transaction(
                                transactionId = document.id,
                                productId = data["product_id"] as? String ?: "",
                                productName = data["product_name"] as? String ?: "Unknown Product",
                                amount = (data["amount"] as? Number)?.toDouble() ?: 0.0,
                                status = parseTransactionStatus(data["status"] as? String),
                                timestamp = (data["timestamp"] as? Number)?.toLong() ?: 0L,
                                sellerId = sellerId,
                                sellerName = sellerName,
                                buyerId = currentUser.uid
                            )
                            transactions.add(transaction)
                        }
                    }

                    _uiState.value = _uiState.value.copy(
                        transactions = transactions,
                        isLoading = false
                    )

                    Log.d("TransactionHistoryViewModel", "Loaded ${transactions.size} transactions")

                } catch (e: Exception) {
                    Log.e("TransactionHistoryViewModel", "Error loading transaction history", e)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to load transaction history: ${e.message}"
                    )
                }
            }
        } else {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = "User not authenticated"
            )
        }
    }

    private fun parseTransactionStatus(status: String?): TransactionStatus {
        return when (status?.uppercase()) {
            "PENDING" -> TransactionStatus.PENDING
            "PROCESSING" -> TransactionStatus.PROCESSING
            "COMPLETED" -> TransactionStatus.COMPLETED
            "CANCELLED" -> TransactionStatus.CANCELLED
            else -> TransactionStatus.PENDING
        }
    }

    fun refreshTransactions() {
        loadTransactionHistory()
    }
}
