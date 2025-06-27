package com.example.projectmdp.ui.module.UserDashboard

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectmdp.data.repository.ProductRepository
import com.example.projectmdp.data.source.dataclass.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class UserDashboardViewModel @Inject constructor(
    private val productRepository: ProductRepository? = null
) : ViewModel() {

    open var searchQuery by mutableStateOf("")
        protected set

    open var products by mutableStateOf<List<Product>>(emptyList())
        protected set

    var isLoading by mutableStateOf(false)
        protected set

    open var userInitials by mutableStateOf("U")
        protected set

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        loadUserInitials()
        loadProducts()
    }

    open fun onSearchQueryChange(query: String) {
        searchQuery = query
        if (query.isEmpty()) {
            loadProducts()
        }
    }

    open fun searchProducts() {
        if (searchQuery.isBlank()) {
            loadProducts()
            return
        }

        if (productRepository != null) {
            searchProductsWithRepository()
        } else {
            searchProductsWithFirestore()
        }
    }

    private fun searchProductsWithRepository() {
        viewModelScope.launch {
            isLoading = true
            try {
                productRepository?.searchProducts(searchQuery)?.collectLatest { result ->
                    result.fold(
                        onSuccess = { searchResults ->
                            products = searchResults
                            Log.d("Dashboard", "Search completed: ${searchResults.size} products found")
                        },
                        onFailure = { error ->
                            Log.e("Dashboard", "Search failed: ${error.message}")
                        }
                    )
                    isLoading = false
                }
            } catch (e: Exception) {
                Log.e("Dashboard", "Search failed: ${e.message}")
                isLoading = false
            }
        }
    }

    private fun searchProductsWithFirestore() {
        viewModelScope.launch {
            isLoading = true
            try {
                val querySnapshot = firestore.collection("products")
                    .whereGreaterThanOrEqualTo("name", searchQuery)
                    .whereLessThanOrEqualTo("name", searchQuery + '\uf8ff')
                    .get()
                    .await()

                val searchResults = mutableListOf<Product>()
                for (document in querySnapshot.documents) {
                    // Convert Firestore document to the new Product class structure
                    val data = document.data
                    if (data != null) {
                        val product = Product(
                            product_id = document.id,
                            name = data["name"] as? String ?: "",
                            price = (data["price"] as? Number)?.toDouble() ?: 0.0,
                            description = data["description"] as? String,
                            category = data["category"] as? String ?: "",
                            image = data["image"] as? String ?: "",
                            user_id = data["user"] as? String ?: "",
                            created_at = (data["created_at"] as? com.google.firebase.Timestamp)?.toDate()?.toString() ?: "",
                            deleted_at = (data["deleted_at"] as? com.google.firebase.Timestamp)?.toDate()?.toString()
                        )
                        searchResults.add(product)
                    }
                }

                products = searchResults
                Log.d("Dashboard", "Search completed: ${searchResults.size} products found")
            } catch (e: Exception) {
                Log.e("Dashboard", "Search failed: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun loadProducts() {
        if (productRepository != null) {
            loadProductsWithRepository()
        } else {
            loadProductsWithFirestore()
        }
    }

    private fun loadProductsWithRepository() {
        viewModelScope.launch {
            isLoading = true
            try {
                productRepository?.getAllProducts(forceRefresh = true)?.collectLatest { result ->
                    result.fold(
                        onSuccess = { productWithPagination ->
                            products = productWithPagination.products
                            Log.d("Dashboard", "Products loaded: ${products.size}")
                        },
                        onFailure = { error ->
                            Log.e("Dashboard", "Failed to load products: ${error.message}")
                            products = emptyList()
                        }
                    )
                    isLoading = false
                }
            } catch (e: Exception) {
                Log.e("Dashboard", "Failed to load products: ${e.message}")
                products = emptyList()
                isLoading = false
            }
        }
    }

    private fun loadProductsWithFirestore() {
        viewModelScope.launch {
            isLoading = true
            try {
                val querySnapshot = firestore.collection("products")
                    .limit(50)
                    .get()
                    .await()

                val productList = mutableListOf<Product>()
                for (document in querySnapshot.documents) {
                    // Convert Firestore document to the new Product class structure
                    val data = document.data
                    if (data != null) {
                        val product = Product(
                            product_id = document.id,
                            name = data["name"] as? String ?: "",
                            price = (data["price"] as? Number)?.toDouble() ?: 0.0,
                            description = data["description"] as? String,
                            category = data["category"] as? String ?: "",
                            image = data["image"] as? String ?: "",
                            user_id = data["user"] as? String ?: "",
                            created_at = (data["created_at"] as? com.google.firebase.Timestamp)?.toDate()?.toString() ?: "",
                            deleted_at = (data["deleted_at"] as? com.google.firebase.Timestamp)?.toDate()?.toString()
                        )
                        productList.add(product)
                    }
                }

                products = productList
                Log.d("Dashboard", "Products loaded: ${productList.size}")
            } catch (e: Exception) {
                Log.e("Dashboard", "Failed to load products: ${e.message}")
                // Just show an empty list instead of sample data
                products = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    private fun loadUserInitials() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val email = currentUser.email
            val displayName = currentUser.displayName

            userInitials = when {
                !displayName.isNullOrBlank() -> {
                    val names = displayName.split(" ")
                    if (names.size >= 2) {
                        "${names[0][0]}${names[1][0]}".uppercase()
                    } else {
                        displayName.take(2).uppercase()
                    }
                }
                !email.isNullOrBlank() -> email.take(2).uppercase()
                else -> "U"
            }
        }
    }

    fun buyProduct(product: Product) {
        // Implement buy functionality
        Log.d("Dashboard", "Buy product: ${product.name}")
    }

    fun chatWithSeller(sellerId: String) {
        // Implement chat functionality
        Log.d("Dashboard", "Chat with seller: $sellerId")
    }
}
