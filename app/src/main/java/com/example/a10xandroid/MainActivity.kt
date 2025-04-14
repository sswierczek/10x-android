package com.example.a10xandroid

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.a10xandroid.ui.auth.AuthScreen
import com.example.a10xandroid.ui.auth.AuthUiState
import com.example.a10xandroid.ui.auth.AuthViewModel
import com.example.a10xandroid.ui.profile.ProfileScreen
import com.example.a10xandroid.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val authViewModel: AuthViewModel = hiltViewModel()
                val currentUser by authViewModel.currentUser.collectAsState(initial = null)

                // Log auth state changes
                LaunchedEffect(currentUser) {
                    Log.d("MainActivity", "Firebase Auth state changed - User: ${currentUser?.uid}")
                }

                // Show profile screen if Firebase Auth has a current user, otherwise show auth screen
                if (currentUser != null) {
                    Log.d("MainActivity", "Firebase Auth user exists, showing profile screen")
                    ProfileScreen(
                        onSignOut = {
                            Log.d("MainActivity", "Sign out triggered")
                            authViewModel.signOut()
                        }
                    )
                } else {
                    Log.d("MainActivity", "No Firebase Auth user, showing auth screen")
                    AuthScreen(
                        onAuthSuccess = {
                            Log.d("MainActivity", "Auth success callback triggered")
                        }
                    )
                }
            }
        }
    }
}
