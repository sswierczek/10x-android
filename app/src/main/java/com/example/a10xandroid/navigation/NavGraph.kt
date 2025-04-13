package com.example.a10xandroid.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.a10xandroid.ui.screens.ConnectionTestScreen
import com.example.a10xandroid.ui.screens.HomeScreen

object NavRoutes {
    const val HOME = "home"
    const val CONNECTION_TEST = "connection_test"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME,
        modifier = modifier
    ) {
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