package com.example.a10xandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.a10xandroid.data.auth.AuthRepository
import com.example.a10xandroid.navigation.NavGraph
import com.example.a10xandroid.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                // Nadrzędny kontener z motywem aplikacji
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Controller nawigacji
                    val navController = rememberNavController()

                    // Graf nawigacji z przekazanym repozytorium auth
                    NavGraph(
                        navController = navController,
                        authRepository = authRepository
                    )
                }
            }
        }
    }
}
