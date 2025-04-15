package com.example.a10xandroid.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.a10xandroid.data.repository.AuthRepository
import com.example.a10xandroid.ui.auth.login.LoginScreen
import com.example.a10xandroid.ui.screens.ConnectionTestScreen
import com.example.a10xandroid.ui.screens.HomeScreen

object NavRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val CONNECTION_TEST = "connection_test"
    const val JOURNAL = "journal"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    authRepository: AuthRepository,
    modifier: Modifier = Modifier
) {
    val currentUser by authRepository.currentUser.collectAsState(initial = null)

    LaunchedEffect(key1 = currentUser) {
        if (currentUser == null) {
            if (navController.currentDestination?.route != NavRoutes.LOGIN && 
                navController.currentDestination?.route != NavRoutes.REGISTER) {
                navController.navigate(NavRoutes.LOGIN) {
                    popUpTo(0) { inclusive = true }
                }
            }
        } else {
            if (navController.currentDestination?.route == NavRoutes.LOGIN || 
                navController.currentDestination?.route == NavRoutes.REGISTER) {
                navController.navigate(NavRoutes.JOURNAL) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (currentUser != null) NavRoutes.JOURNAL else NavRoutes.LOGIN,
        modifier = modifier
    ) {
        composable(NavRoutes.LOGIN) {
            LoginScreen(
                navController = navController
            )
        }
        
        composable(NavRoutes.REGISTER) {
            // TODO: Implementacja ekranu rejestracji
            LoginScreen(
                navController = navController
            )
        }
        
        composable(NavRoutes.JOURNAL) {
            HomeScreen(
                onCheckConnectionClick = {
                    navController.navigate(NavRoutes.CONNECTION_TEST)
                }
            )
        }
        
        composable(NavRoutes.HOME) {
            HomeScreen(
                onCheckConnectionClick = {
                    navController.navigate(NavRoutes.CONNECTION_TEST)
                }
            )
        }
        
        composable(NavRoutes.CONNECTION_TEST) {
            ConnectionTestScreen()
        }
    }
} 