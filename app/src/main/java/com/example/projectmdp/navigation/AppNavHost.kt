package com.example.projectmdp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.projectmdp.ui.module.login.LoginScreen
import com.example.projectmdp.ui.module.register.RegisterScreen

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
    }
}