package com.example.projectmdp.ui.module.Midtrans

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectmdp.data.source.dataclass.Product
import com.example.projectmdp.data.source.remote.RetrofitInstance
import com.example.projectmdp.data.source.response.MidtransResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MidtransViewModel @Inject constructor() : ViewModel() {

    // UI States
    private val _isLoading = mutableStateOf(false)
    val isLoading = _isLoading

    // Payment Data
    private val _paymentUrl = MutableStateFlow<String?>(null)
    val paymentUrl: StateFlow<String?> = _paymentUrl.asStateFlow()

    private val _paymentToken = MutableStateFlow<String?>(null)
    val paymentToken: StateFlow<String?> = _paymentToken.asStateFlow()

    // Selected product for payment
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    // Error handling
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Navigation events
    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    // Payment status
    private val _paymentStatus = MutableStateFlow(PaymentStatus.PENDING)
    val paymentStatus: StateFlow<PaymentStatus> = _paymentStatus.asStateFlow()

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun setSelectedProduct(product: Product) {
        _selectedProduct.value = product
    }

    fun createTransaction(quantity: Int = 1) {
        val product = _selectedProduct.value ?: return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Product details for Midtrans
                val orderDetails = mapOf(
                    "product_id" to product.product_id,
                    "quantity" to quantity,
                    "price" to product.price
                )

                // Call API to create transaction
                val response = RetrofitInstance.Transactionapi.createTransaction(orderDetails)

                if (response.isSuccess()) {
                    val data = response.data
                    if (data != null && data.token != null && data.redirect_url != null) {
                        _paymentToken.value = data.token
                        _paymentUrl.value = data.redirect_url
                        _paymentStatus.value = PaymentStatus.PENDING
                    } else {
                        _errorMessage.value = "Invalid payment data received"
                    }
                } else {
                    _errorMessage.value = response.error ?: "Failed to create transaction"
                }
            } catch (e: Exception) {
                Log.e("MidtransViewModel", "Error creating transaction", e)
                _errorMessage.value = "Failed to create transaction: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun checkPaymentStatus(orderId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.Transactionapi.checkTransactionStatus(orderId)

                if (response.isSuccess()) {
                    val status = response.data?.transaction_status
                    _paymentStatus.value = when (status?.lowercase()) {
                        "settlement", "capture" -> PaymentStatus.SUCCESS
                        "pending" -> PaymentStatus.PENDING
                        "deny", "cancel", "expire", "failure" -> PaymentStatus.FAILED
                        else -> PaymentStatus.PENDING
                    }
                } else {
                    _errorMessage.value = response.error ?: "Failed to check payment status"
                }
            } catch (e: Exception) {
                Log.e("MidtransViewModel", "Error checking payment status", e)
                _errorMessage.value = "Failed to check payment status: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetPayment() {
        _paymentUrl.value = null
        _paymentToken.value = null
        _paymentStatus.value = PaymentStatus.PENDING
    }
}

enum class PaymentStatus {
    PENDING, SUCCESS, FAILED
}