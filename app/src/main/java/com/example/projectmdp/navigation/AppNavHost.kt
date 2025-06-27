package com.example.projectmdp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.projectmdp.ui.module.Products.CreateProductScreen
import com.example.projectmdp.ui.module.Products.DetailsScreen
import com.example.projectmdp.ui.module.login.LoginScreen
import com.example.projectmdp.ui.module.register.RegisterScreen
import com.example.projectmdp.ui.module.UserDashboard.UserDashboardScreen

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
        modifier = modifier
    ) {
        composable(Routes.LOGIN) {
            // Using hiltViewModel() to properly inject dependencies
            LoginScreen(
                viewModel = hiltViewModel(),
                navController = navController
            )
        }
        composable(Routes.REGISTER) {
            // Using hiltViewModel() to properly inject dependencies
            RegisterScreen(
                viewModel = hiltViewModel(),
                navController = navController
            )
        }
        composable(Routes.USER_DASHBOARD) {
            UserDashboardScreen(
                viewModel = hiltViewModel(),
                navController = navController
            )
        }
        composable(
            route = Routes.PRODUCT_DETAIL,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            // Now, backStackEntry.arguments?.getString("productId") is guaranteed to be non-null
            // because we defined the route to expect a non-nullable String.
            val productId = backStackEntry.arguments?.getString("productId")!! // Use !! as navigation guarantees non-null
            DetailsScreen(
                productViewModel = hiltViewModel(),
                navController = navController,
                productId = productId // Pass the non-nullable productId
            )
        }
        composable(route = Routes.ADD_PRODUCT) {
            CreateProductScreen(
                viewModel = hiltViewModel(),
                navController = navController
            )
        }
    }
}