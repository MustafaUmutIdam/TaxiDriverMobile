package com.taxidriver.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.taxidrivermobile.data.api.RetrofitClient
import com.example.taxidrivermobile.data.local.TokenManager
import com.example.taxidrivermobile.data.repository.AuthRepository
import com.example.taxidrivermobile.ui.screens.home.HomeScreen
import com.example.taxidrivermobile.ui.screens.login.LoginScreen
import com.example.taxidrivermobile.ui.screens.login.LoginViewModel
import com.example.taxidrivermobile.ui.screens.register.RegisterScreen
import com.example.taxidrivermobile.ui.screens.register.RegisterViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
}

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun NavGraph(
    navController: NavHostController,
    tokenManager: TokenManager
) {
    val repository = AuthRepository(RetrofitClient.apiService, tokenManager)

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            val viewModel = LoginViewModel(repository)
            LoginScreen(
                viewModel = viewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            val viewModel = RegisterViewModel(repository)
            RegisterScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen()
        }
    }
}