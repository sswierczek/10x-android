package com.example.a10xandroid.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.a10xandroid.data.repository.AuthRepository
import com.example.a10xandroid.ui.addmovie.AddMovieScreen
import com.example.a10xandroid.ui.auth.login.LoginScreen
import com.example.a10xandroid.ui.auth.register.RegisterScreen
import com.example.a10xandroid.ui.auth.forgotpassword.ForgotPasswordScreen
import com.example.a10xandroid.ui.journal.JournalScreen
import com.example.a10xandroid.ui.movie.MovieDetailsScreen
import com.example.a10xandroid.ui.profile.ProfileScreen
import com.example.a10xandroid.ui.recommendations.RecommendationsScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

private const val TAG = "NavGraph"

object NavRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val JOURNAL = "journal"
    const val MOVIE_DETAILS = "movie-details"
    const val ADD_MOVIE = "add-movie"
    const val RECOMMENDATIONS = "recommendations"
    const val PROFILE = "profile"
    const val FORGOT_PASSWORD = "forgot-password"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    authRepository: AuthRepository,
    modifier: Modifier = Modifier
) {
    var startDestination by remember { mutableStateOf<String?>(null) }
    var isInitialized by remember { mutableStateOf(false) }
    val currentUser by authRepository.currentUser.collectAsState(initial = null)

    LaunchedEffect(Unit) {
        Log.d(TAG, "NavGraph LaunchedEffect started")
        delay(1000) // Give Firebase Auth time to initialize
        val initialUser = authRepository.currentUser.first()
        Log.d(TAG, "Initial auth state: ${initialUser?.uid}, isNull: ${initialUser == null}")

        startDestination = if (initialUser != null) {
            Log.d(TAG, "Setting start destination to JOURNAL")
            NavRoutes.JOURNAL
        } else {
            Log.d(TAG, "Setting start destination to LOGIN")
            NavRoutes.LOGIN
        }
        isInitialized = true
    }

    LaunchedEffect(key1 = currentUser) {
        Log.d(
            TAG,
            "LaunchedEffect triggered with currentUser: ${currentUser?.uid}, isNull: ${currentUser == null}"
        )
        Log.d(TAG, "Current destination: ${navController.currentDestination?.route}")

        if (currentUser == null) {
            Log.d(TAG, "User is null, checking if we need to navigate to login")
            if (navController.currentDestination?.route != NavRoutes.LOGIN &&
                navController.currentDestination?.route != NavRoutes.REGISTER
            ) {
                Log.d(TAG, "No user, navigating to login")
                try {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error navigating to login", e)
                }
            } else {
                Log.d(TAG, "Already on login or register screen, no navigation needed")
            }
        } else {
            Log.d(TAG, "User is logged in, checking if we need to navigate to journal")
            if (navController.currentDestination?.route == NavRoutes.LOGIN ||
                navController.currentDestination?.route == NavRoutes.REGISTER
            ) {
                Log.d(TAG, "User logged in, navigating to journal")
                try {
                    navController.navigate(NavRoutes.JOURNAL) {
                        popUpTo(0) { inclusive = true }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error navigating to journal", e)
                }
            } else {
                Log.d(TAG, "Already on a screen other than login/register, no navigation needed")
            }
        }
    }

    if (!isInitialized) {
        Log.d(TAG, "Waiting for initialization...")
        return
    }

    Log.d(TAG, "Creating NavHost with startDestination: $startDestination")
    NavHost(
        navController = navController,
        startDestination = startDestination ?: NavRoutes.LOGIN,
        modifier = modifier
    ) {
        composable(NavRoutes.LOGIN) {
            Log.d(TAG, "Composing LOGIN screen")
            LoginScreen(navController = navController)
        }

        composable(NavRoutes.REGISTER) {
            Log.d(TAG, "Composing REGISTER screen")
            RegisterScreen(navController = navController)
        }

        composable(NavRoutes.FORGOT_PASSWORD) {
            Log.d(TAG, "Composing FORGOT_PASSWORD screen")
            ForgotPasswordScreen(navController = navController)
        }

        composable(NavRoutes.JOURNAL) {
            Log.d(TAG, "Composing JOURNAL screen")
            JournalScreen(authRepository = authRepository, navController = navController)
        }

        composable(NavRoutes.ADD_MOVIE) {
            Log.d(TAG, "Composing ADD_MOVIE screen")
            AddMovieScreen(navController = navController)
        }

        composable(NavRoutes.RECOMMENDATIONS) {
            Log.d(TAG, "Composing RECOMMENDATIONS screen")
            RecommendationsScreen(navController = navController)
        }

        composable(NavRoutes.PROFILE) {
            Log.d(TAG, "Composing PROFILE screen")
            ProfileScreen(
                onSignOut = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "${NavRoutes.MOVIE_DETAILS}/{movieId}",
            arguments = listOf(
                navArgument("movieId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            Log.d(
                TAG,
                "Composing MovieDetailsScreen with movieId: " +
                    "${backStackEntry.arguments?.getString("movieId")}"
            )
            MovieDetailsScreen(
                navController = navController,
                movieId = backStackEntry.arguments?.getString("movieId") ?: ""
            )
        }
    }
}
