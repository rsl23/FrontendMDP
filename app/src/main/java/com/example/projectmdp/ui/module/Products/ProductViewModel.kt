package com.example.projectmdp.ui.module.Products

import android.content.Context // <--- ADD THIS IMPORT
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectmdp.data.repository.ProductRepository
import com.example.projectmdp.data.repository.UserRepository
import com.example.projectmdp.data.source.dataclass.Product
import com.example.projectmdp.data.source.dataclass.User
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext // <--- ADD THIS IMPORT for Hilt's ApplicationContext qualifier
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle,
    @ApplicationContext private val applicationContext: Context // <--- INJECT APPLICATION CONTEXT HERE
) : ViewModel() {
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _selectedProduct = MutableLiveData<Product?>()
    val selectedProduct: LiveData<Product?> = _selectedProduct

    val productId: String = savedStateHandle.get<String>("productId")
        ?: ""

    private val _selectedProductSeller = MutableLiveData<User?>()
    val selectedProductSeller: LiveData<User?> = _selectedProductSeller

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _productCreationSuccess = MutableLiveData<Boolean>()
    val productCreationSuccess: LiveData<Boolean> = _productCreationSuccess

    init {
        if (productId.isNotEmpty()) {
            fetchProductById(productId)
        }
    }

    fun setSelectedProduct(product: Product) {
        _selectedProduct.value = product
        fetchSelectedProductSeller()
    }

    fun fetchSelectedProductSeller() {
        _isLoading.value = true
        _errorMessage.value = null
        _selectedProductSeller.value = null

        viewModelScope.launch {
            val userId = _selectedProduct.value?.user_id
            if (userId != null) {
                userRepository.getUserById(userId).collect { result ->
                    _isLoading.value = false

                    result.onSuccess { user ->
                        _selectedProductSeller.value = user
                        _errorMessage.value = null
                    }.onFailure { throwable ->
                        _errorMessage.value = throwable.message ?: "Failed to load seller information."
                        _selectedProductSeller.value = null
                    }
                }
            } else {
                _errorMessage.value = "No user ID available for the selected product."
                _isLoading.value = false
            }
        }
    }

    fun fetchProductById(productId: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            // Note: productRepository.getProductById doesn't need context, only addProduct/updateProduct
            productRepository.getProductById(productId).collect { result ->
                _isLoading.value = false
                result.onSuccess { product ->
                    _selectedProduct.value = product
                    _errorMessage.value = null
                    fetchSelectedProductSeller()
                }.onFailure { throwable ->
                    _selectedProduct.value = null
                    _errorMessage.value = throwable.message ?: "Unknown error fetching product."
                }
            }
        }
    }

    fun fetchAllProducts(page: Int, limit: Int, forceRefresh: Boolean) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            productRepository.getAllProducts(page, limit, forceRefresh).collect { result ->
                _isLoading.value = false
                result.onSuccess { productWithPagination ->
                    _products.value = productWithPagination.products
                }.onFailure { throwable ->
                    _errorMessage.value = throwable.message ?: "Failed to load products."
                }
            }
        }
    }

    // --- THIS IS THE CRITICAL CHANGE ---
    fun createProduct(name: String, description: String, price: Double, category: String, imageUri: Uri?) {
        _isLoading.value = true
        _errorMessage.value = null
        _productCreationSuccess.value = false

        viewModelScope.launch {
            productRepository.addProduct(
                applicationContext, // <--- PASS THE INJECTED APPLICATION CONTEXT HERE
                name,
                description,
                price,
                category,
                imageUri
            ).collect { result ->
                _isLoading.value = false
                result.onSuccess { createdProduct ->
                    _errorMessage.value = null
                    _productCreationSuccess.value = true
                }.onFailure { throwable ->
                    _errorMessage.value = throwable.message ?: "Failed to create product."
                    _productCreationSuccess.value = false
                }
            }
        }
    }

    // You might also need to update this if you use imageUri in updateProduct

    fun resetProductCreationStatus() {
        _productCreationSuccess.value = false
    }

    // Add a function to clear error messages if needed for UI control
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}