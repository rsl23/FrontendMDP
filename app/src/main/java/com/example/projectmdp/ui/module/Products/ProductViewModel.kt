package com.example.projectmdp.ui.module.Products

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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository, // Inject the repository
    private val userRepository: UserRepository,
    private val  savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _selectedProduct = MutableLiveData<Product?>() // Use nullable Product
    val selectedProduct: LiveData<Product?> = _selectedProduct

    val productId: String = savedStateHandle.get<String>("productId")
        ?: throw IllegalStateException("Product ID 'productId' is missing from SavedStateHandle. Check navigation route arguments.")

    // New LiveData for the seller information
    private val _selectedProductSeller = MutableLiveData<User?>()
    val selectedProductSeller: LiveData<User?> = _selectedProductSeller // This is what the UI observes

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // LiveData for product creation success (as discussed previously)
    private val _productCreationSuccess = MutableLiveData<Boolean>()
    val productCreationSuccess: LiveData<Boolean> = _productCreationSuccess
    init {
        fetchProductById(productId) // Load details for the specific product
    }
    fun setSelectedProduct(product: Product) {
        _selectedProduct.value = product
        // Automatically fetch seller information when a product is selected
        fetchSelectedProductSeller() // Call the function to trigger the fetch
    }

    // Corrected function to fetch seller information
    // It should NOT return User? directly, as the result is asynchronous.
    // Instead, it updates the LiveData that the UI observes.
    fun fetchSelectedProductSeller() { // Changed return type to Unit (or simply omit it)
        _isLoading.value = true
        _errorMessage.value = null // Clear previous errors
        _selectedProductSeller.value = null // Clear previous seller data

        viewModelScope.launch {
            val userId = _selectedProduct.value?.user_id
            if (userId != null) {
                // Collect the Flow<Result<User>> and handle the result
                userRepository.getUserById(userId).collect { result ->
                    _isLoading.value = false // Stop loading after result is processed

                    result.onSuccess { user ->
                        _selectedProductSeller.value = user // Post the User object to LiveData
                        _errorMessage.value = null // Clear error on success
                    }.onFailure { throwable ->
                        _errorMessage.value = throwable.message ?: "Failed to load seller information."
                        _selectedProductSeller.value = null // Clear seller on error
                    }
                }
            } else {
                _errorMessage.value = "No user ID available for the selected product."
                _isLoading.value = false
            }
        }
        // REMOVED: return _selectedProductSeller.value
        // The return statement here would execute immediately, before the LiveData is updated.
    }

    // This is the function to fetch a product by ID
    fun fetchProductById(productId: String) {
        _isLoading.value = true
        _errorMessage.value = null // Clear any previous error

        viewModelScope.launch {
            productRepository.getProductById(productId).collect { result ->
                _isLoading.value = false // Stop loading regardless of success or failure
                result.onSuccess { product ->
                    _selectedProduct.value = product
                    _errorMessage.value = null // Clear error on success
                    fetchSelectedProductSeller() // Fetch seller after product is successfully loaded
                }.onFailure { throwable ->
                    _selectedProduct.value = null // Clear product on error
                    _errorMessage.value = throwable.message ?: "Unknown error fetching product."
                }
            }
        }
    }

    // You can also add functions for other repository operations here, e.g.:
    fun fetchAllProducts(page: Int, limit: Int, forceRefresh: Boolean) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            productRepository.getAllProducts(page, limit, forceRefresh).collect { result ->
                _isLoading.value = false
                result.onSuccess { productWithPagination ->
                    _products.value = productWithPagination.products
                    // You might want to expose pagination info as well
                }.onFailure { throwable ->
                    _errorMessage.value = throwable.message ?: "Failed to load products."
                }
            }
        }
    }


    fun createProduct(name: String, description: String, price: Double, category: String, imageUri: Uri?) {
        _isLoading.value = true
        _errorMessage.value = null
        _productCreationSuccess.value = false // Reset success flag before starting

        viewModelScope.launch {
            productRepository.addProduct(name, description, price, category, imageUri)
                .collect { result ->
                    _isLoading.value = false
                    result.onSuccess { createdProduct ->
                        _errorMessage.value = null
                        _productCreationSuccess.value = true // Set to true on success
                    }.onFailure { throwable ->
                        _errorMessage.value = throwable.message ?: "Failed to create product."
                        _productCreationSuccess.value = false // Ensure false on failure
                    }
                }
        }
    }

    // Function to reset the creation success flag after navigation
    fun resetProductCreationStatus() {
        _productCreationSuccess.value = false
    }
}