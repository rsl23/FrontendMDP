package com.example.projectmdp.ui.module.UserDashboard

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.rememberNavController
import com.example.projectmdp.data.model.product.Product
import com.google.firebase.Timestamp

// Preview ViewModel implementation
class PreviewUserDashboardViewModel : UserDashboardViewModel() {

    init {
        // Override with sample data
        loadSampleData()
    }

    private fun loadSampleData() {
        products = listOf(
            Product(
                product_id = "1",
                name = "iPhone 12 Pro",
                price = "8,500,000",
                description = "Good condition iPhone 12 Pro",
                image = "https://via.placeholder.com/300x300?text=iPhone+12+Pro",
                user = "US001",
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
                user = "US001",
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
                user = "US002",
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
                user = "US002",
                created_at = Timestamp.now(),
                updated_at = Timestamp.now(),
                deleted_at = null,
                sellerName = "Alice Brown",
                sellerLocation = "Medan, North Sumatra"
            )
        )
        userInitials = "JD"
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UserDashboardScreenPreview() {
    val previewViewModel = PreviewUserDashboardViewModel()

    MaterialTheme {
        UserDashboardScreen(
            viewModel = previewViewModel,
            navController = rememberNavController()
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
fun UserDashboardScreenLoadingPreview() {
    val previewViewModel = PreviewUserDashboardViewModel().apply {
        isLoading = true
    }

    MaterialTheme {
        UserDashboardScreen(
            viewModel = previewViewModel,
            navController = rememberNavController()
        )
    }
}

