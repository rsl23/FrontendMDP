package com.example.projectmdp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.projectmdp.data.repository.AuthRepository
import com.example.projectmdp.ui.module.login.LoginScreen
import com.example.projectmdp.ui.module.login.LoginViewModel
import com.example.projectmdp.ui.module.register.RegisterScreen
import com.example.projectmdp.ui.module.register.RegisterViewModel
@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier){
    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(LoginViewModel(), modifier ,navController)
        }
        composable(Routes.REGISTER) {
            RegisterScreen(RegisterViewModel(AuthRepository()), modifier ,navController)
        }
    }
}
