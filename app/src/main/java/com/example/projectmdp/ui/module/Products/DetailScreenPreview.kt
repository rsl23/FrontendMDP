package com.example.projectmdp.ui.module.Products

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.projectmdp.data.source.dataclass.Product
import com.example.projectmdp.ui.theme.ProjectMDPTheme
import java.time.LocalDate
import java.util.Date

@Preview(showBackground = true)
@Composable
fun DetailScreenPreview() {
    val navController: NavController = rememberNavController() // Creates a mock NavController
    val viewModel: ProductViewModel = viewModel() // Creates an instance of your ViewModel
    viewModel.setSelectedProduct(
        Product(
            "1",
            "Dummy Product",
            100000.0,
            "This is a dummy product for preview purposes.",
            "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3c/B-21_Raider_front_high.jpg/250px-B-21_Raider_front_high.jpg",
            "US001",
            "US002",
            "17-09-2023",
            null,
        )
    )
    ProjectMDPTheme { // Applies your app's theme
        Surface { // A Material Design surface for background and elevation
            DetailsScreen (
                navController = navController,
                productViewModel = viewModel,
                productId = "1"
            )
        }
    }
}