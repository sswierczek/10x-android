package com.example.a10xandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.a10xandroid.ui.auth.AuthScreen
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
                var isAuthenticated by remember { mutableStateOf(false) }
                val authViewModel: AuthViewModel = hiltViewModel()

                LaunchedEffect(Unit) {
                    authViewModel.currentUser.collect { user ->
                        isAuthenticated = user != null
                    }
                }

                if (isAuthenticated) {
                    ProfileScreen(
                        onSignOut = {
                            isAuthenticated = false
                        }
                    )
                } else {
                    AuthScreen(
                        onAuthSuccess = {
                            isAuthenticated = true
                        }
                    )
                }
            }
        }
    }
}
