package com.example.projectmdp.ui.module.Products

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue // Import this
import androidx.compose.runtime.livedata.observeAsState // Import this
import androidx.compose.runtime.remember // Import this for NumberFormat
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.projectmdp.data.source.dataclass.Product
import com.example.projectmdp.data.source.dataclass.User
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date // Make sure this is imported if you derive a Date type

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    navController: NavController,
    productViewModel: ProductViewModel = hiltViewModel(),
    productId: String
) {
    // Correctly observe LiveData as Compose State
    val product: Product? by productViewModel.selectedProduct.observeAsState()
    val seller: User? by productViewModel.selectedProductSeller.observeAsState()
    val isLoading: Boolean by productViewModel.isLoading.observeAsState(initial = false)
    val errorMessage: String? by productViewModel.errorMessage.observeAsState()

    // Assuming ProductViewModel's init block calls fetchProductById(productId)
    // No explicit fetch calls here in the Composable body on every recomposition.

    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product?.name ?: "Loading Product...") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = { /* TODO: Handle Buy Now */ },
                    modifier = Modifier.padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Buy Now")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            // In DetailsScreen within the 'when' block:
            when {
                isLoading -> {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
                errorMessage != null -> {
                    Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
                }
                product == null -> {
                    Text("Product with ID $productId not found.")
                }
                else -> {
                    // Explicitly assert that 'product' is non-null here.
                    // The 'when' block guarantees this logic.
                    ProductDetailsContent(product = product!!, seller = seller, currencyFormat = currencyFormat)
                }
            }
        }
    }
}

// Extract the main content into a separate composable for clarity
@Composable
private fun ProductDetailsContent(product: Product, seller: User?, currencyFormat: NumberFormat) {
    Column(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = product.image,
            contentDescription = product.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = product.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Description",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        product.description?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Price: ${currencyFormat.format(product.price)}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Handle date formatting
        val formattedDate = remember(product.created_at) { // Use remember to avoid recalculating on every recomposition
            try {
                // Assuming product.created_at is in a format like "yyyy-MM-dd HH:mm:ss" or ISO 8601
                // Adjust this SimpleDateFormat pattern to match your backend's string exactly
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()) // Example ISO 8601
                parser.timeZone = java.util.TimeZone.getTimeZone("UTC") // If your backend dates are UTC
                val date = parser.parse(product.created_at)
                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(date) // Format for display
            } catch (e: Exception) {
                // Log the error or return a fallback string
                e.printStackTrace()
                product.created_at // Fallback to raw string if parsing fails
            }
        }
        Text(
            text = "Created At: $formattedDate",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Seller: ${seller?.username ?: "N/A"}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Location: ${seller?.address ?: "N/A"}",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}