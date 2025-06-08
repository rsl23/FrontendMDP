package com.example.projectmdp.ui.module.UserDashboard

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectmdp.data.model.product.Product
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

    open var isLoading by mutableStateOf(false)
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
                    document.toObject<Product>()?.let { product ->
                        searchResults.add(product.copy(product_id = document.id))
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
                    document.toObject<Product>()?.let { product ->
                        productList.add(product.copy(product_id = document.id))
                    }
                }

                products = productList
                Log.d("Dashboard", "Products loaded: ${productList.size}")
            } catch (e: Exception) {
                Log.e("Dashboard", "Failed to load products: ${e.message}")
                // Load sample data as fallback
                loadSampleProducts()
            } finally {
                isLoading = false
            }
        }
    }

    private fun loadSampleProducts() {
        products = listOf(
            Product(
                product_id = "1",
                name = "iPhone 12 Pro",
                price = "8,500,000",
                description = "Good condition iPhone 12 Pro",
                image = "https://via.placeholder.com/300x300?text=iPhone+12+Pro",
                user = "seller1",
                created_at = Timestamp.now(),
                updated_at = Timestamp.now(),
                deleted_at = null,
                sellerName = "John Doe",
                sellerLocation = "Surabaya, East Java"
            ),
            Product(
                product_id = "2",
                name = "Samsung Galaxy S21",
                price = "7,200,000",
                description = "Excellent condition Samsung Galaxy S21",
                image = "https://via.placeholder.com/300x300?text=Galaxy+S21",
                user = "seller2",
                created_at = Timestamp.now(),
                updated_at = Timestamp.now(),
                deleted_at = null,
                sellerName = "Jane Smith",
                sellerLocation = "Jakarta, DKI Jakarta"
            ),
            Product(
                product_id = "3",
                name = "MacBook Air M1",
                price = "12,000,000",
                description = "Like new MacBook Air with M1 chip",
                image = "https://via.placeholder.com/300x300?text=MacBook+Air",
                user = "seller3",
                created_at = Timestamp.now(),
                updated_at = Timestamp.now(),
                deleted_at = null,
                sellerName = "Bob Wilson",
                sellerLocation = "Bandung, West Java"
            ),
            Product(
                product_id = "4",
                name = "iPad Pro 11\"",
                price = "9,800,000",
                description = "iPad Pro 11 inch with Apple Pencil",
                image = "https://via.placeholder.com/300x300?text=iPad+Pro",
                user = "seller4",
                created_at = Timestamp.now(),
                updated_at = Timestamp.now(),
                deleted_at = null,
                sellerName = "Alice Brown",
                sellerLocation = "Medan, North Sumatra"
            )
        )
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

