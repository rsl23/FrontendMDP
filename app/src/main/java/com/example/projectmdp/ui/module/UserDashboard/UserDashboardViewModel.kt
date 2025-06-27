package com.example.projectmdp.ui.module.UserDashboard

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectmdp.data.source.dataclass.Product
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
open class UserDashboardViewModel @Inject constructor() : ViewModel() {

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
                        "${names[0].first().uppercaseChar()}${names[1].first().uppercaseChar()}"
                    } else {
                        names[0].take(2).uppercase()
                    }
                }
                !email.isNullOrBlank() -> {
                    email.first().uppercaseChar().toString()
                }
                else -> "U"
            }
        }
    }

    open fun buyProduct(product: Product) {
        // TODO: Implement buy product functionality
        Log.d("Dashboard", "Buy product: ${product.name}")
        // Navigate to purchase screen or show purchase dialog
    }

    open fun chatWithSeller(sellerId: String) {
        // TODO: Implement chat functionality
        Log.d("Dashboard", "Chat with seller: $sellerId")
        // Navigate to chat screen
    }
}
