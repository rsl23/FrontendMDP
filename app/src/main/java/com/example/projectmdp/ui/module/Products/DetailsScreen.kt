package com.example.projectmdp.ui.module.Products

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.* // Import Material 3 components
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel // Or hiltViewModel() if using Hilt
import androidx.navigation.NavController
import coil.compose.AsyncImage // Correct import for Coil's AsyncImage
import com.example.projectmdp.ui.module.Products.ProductViewModel // Assuming ProductViewModel is in this package
import java.text.SimpleDateFormat
import java.util.Locale

// Remember to annotate with @OptIn for experimental Material 3 APIs if necessary,
// like for TopAppBar or BottomAppBar content, though basic usage is stable.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    navController: NavController,
    productViewModel: ProductViewModel = viewModel(),
    productId: String
) {
    productViewModel.fetchProductById(productId)
    val product = productViewModel.selectedProduct.value
    productViewModel.fetchSelectedProductSeller()
    val seller = productViewModel.selectedProductSeller.value
    if (product == null) {
        // Log error or show a message before popping back
        // Log.e("DetailsScreen", "Product details not found.")
        navController.popBackStack()
        return
    }

    Scaffold(
        topBar = {
            TopAppBar( // Material 3 TopAppBar
                title = { Text(product.name) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Handle share */ }) {
                        Icon(Icons.Filled.Share, contentDescription = "Share")
                    }
                    IconButton(onClick = { /* TODO: Handle favorite */ }) {
                        Icon(Icons.Filled.FavoriteBorder, contentDescription = "Favorite")
                    }
                }
            )
        },

        bottomBar = {
            BottomAppBar( // Material 3 BottomAppBar
                modifier = Modifier.fillMaxWidth(),
            ) {
                Spacer(Modifier.weight(1f)) // Pushes content to the end
                Button( // Material 3 Button
                    onClick = { /* TODO: Handle Buy Now */ },
                    modifier = Modifier.padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary // Using M3 primary color
                    )
                ) {
                    Text("Buy Now")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding from Scaffold
                .padding(horizontal = 16.dp) // Use horizontal padding for content
        ) {
            Spacer(modifier = Modifier.height(16.dp)) // Add some top space
            AsyncImage( // Using Coil's AsyncImage
                model = product.image, // URL or URI for the image
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineMedium, // Material 3 typography
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium, // Material 3 typography
                fontWeight = FontWeight.SemiBold
            )
            product.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge // Material 3 typography
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Price: $${product.price}",
                style = MaterialTheme.typography.titleMedium, // Material 3 typography
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Created At: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(product.created_at)}",
                style = MaterialTheme.typography.bodyMedium // Material 3 typography
            )
            Spacer(modifier = Modifier.weight(1f)) // Pushes content to the bottom
            Text(
                text = "Seller: ${seller?.username}",
                style = MaterialTheme.typography.titleMedium, // Material 3 typography
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Location: ${seller?.address}",
                style = MaterialTheme.typography.bodyMedium // Material 3 typography
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}