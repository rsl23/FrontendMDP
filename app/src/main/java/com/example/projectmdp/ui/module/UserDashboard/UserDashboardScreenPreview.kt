package com.example.projectmdp.ui.module.UserDashboard

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.rememberNavController
import com.example.projectmdp.data.model.product.Product

// Preview ViewModel implementation
class PreviewUserDashboardViewModel : UserDashboardViewModel() {

    init {
        // Override with sample data
        loadSampleData()
    }

    private fun loadSampleData() {
        products = listOf(
            Product(
                id = "1",
                name = "iPhone 12 Pro Max - Excellent Condition",
                price = "8,500,000",
                imageUrl = "https://via.placeholder.com/300x300/4285F4/FFFFFF?text=iPhone+12+Pro",
                sellerId = "seller1",
                sellerName = "John Doe",
                sellerLocation = "Surabaya, East Java",
                category = "smartphones",
                description = "Good condition iPhone 12 Pro with original charger"
            ),
            Product(
                id = "2",
                name = "Samsung Galaxy S21 Ultra",
                price = "7,200,000",
                imageUrl = "https://via.placeholder.com/300x300/34A853/FFFFFF?text=Galaxy+S21",
                sellerId = "seller2",
                sellerName = "Jane Smith",
                sellerLocation = "Jakarta, DKI Jakarta",
                category = "smartphones",
                description = "Excellent condition Samsung Galaxy S21 with S Pen"
            ),
            Product(
                id = "3",
                name = "MacBook Air M1 2020",
                price = "12,000,000",
                imageUrl = "https://via.placeholder.com/300x300/EA4335/FFFFFF?text=MacBook+Air",
                sellerId = "seller3",
                sellerName = "Bob Wilson",
                sellerLocation = "Bandung, West Java",
                category = "laptops",
                description = "Like new MacBook Air with M1 chip, 8GB RAM, 256GB SSD"
            ),
            Product(
                id = "4",
                name = "iPad Pro 11\" 2021 with Apple Pencil",
                price = "9,800,000",
                imageUrl = "https://via.placeholder.com/300x300/FBBC05/FFFFFF?text=iPad+Pro",
                sellerId = "seller4",
                sellerName = "Alice Brown",
                sellerLocation = "Medan, North Sumatra",
                category = "tablets",
                description = "iPad Pro 11 inch with Apple Pencil and Smart Keyboard"
            ),
            Product(
                id = "5",
                name = "Dell XPS 13 Laptop",
                price = "10,500,000",
                imageUrl = "https://via.placeholder.com/300x300/9C27B0/FFFFFF?text=Dell+XPS",
                sellerId = "seller5",
                sellerName = "Charlie Davis",
                sellerLocation = "Yogyakarta, DI Yogyakarta",
                category = "laptops",
                description = "Dell XPS 13 with Intel i7, 16GB RAM, 512GB SSD"
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

