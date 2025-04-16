package com.example.a10xandroid.ui.auth.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.a10xandroid.navigation.NavRoutes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Główny ekran logowania, zawierający wszystkie komponenty potrzebne do logowania.
 */
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    // Efekt dla automatycznego ukrywania komunikatu błędu po pewnym czasie
    LaunchedEffect(key1 = uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            delay(5000) // Pokazuj błąd przez 5 sekund
            viewModel.clearErrorMessage()
        }
    }

    LaunchedEffect(key1 = uiState.isLoading) {
        // Jeśli logowanie zostało zakończone i nie ma błędu, to nawigujemy do ekranu głównego
        if (!uiState.isLoading && uiState.errorMessage == null) {
            // Sprawdzamy, czy użytkownik się zalogował (obserwując zewnętrzne źródło - AuthRepository)
            // To będzie zaimplementowane w nawigacji na podstawie przepływu autentykacji
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo aplikacji
            TopLogo()

            Spacer(modifier = Modifier.height(32.dp))

            // Formularz logowania
            LoginForm(
                email = uiState.email,
                password = uiState.password,
                isPasswordVisible = uiState.isPasswordVisible,
                emailError = uiState.emailError,
                passwordError = uiState.passwordError,
                isLoading = uiState.isLoading,
                onEmailChange = { email ->
                    viewModel.updateEmail(email)
                    if (uiState.errorMessage != null) {
                        viewModel.clearErrorMessage()
                    }
                },
                onPasswordChange = { password ->
                    viewModel.updatePassword(password)
                    if (uiState.errorMessage != null) {
                        viewModel.clearErrorMessage()
                    }
                },
                onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
                onLoginClick = {
                    scope.launch {
                        viewModel.login()
                    }
                },
                onForgotPasswordClick = {
                    // Nawigacja do ekranu resetowania hasła
                    // W przyszłej implementacji
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Link do rejestracji
            RegistrationLink(
                onClick = {
                    // Nawigacja do ekranu rejestracji
                    navController.navigate(NavRoutes.REGISTER)
                }
            )
        }

        // Wyświetlanie błędu logowania
        if (uiState.errorMessage != null) {
            ErrorMessage(
                message = uiState.errorMessage!!,
                isVisible = true,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }

        // Wskaźnik ładowania
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxSize()
                ) {
                    LoadingIndicator(
                        isVisible = true,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

/**
 * Logo aplikacji wyświetlane na górze ekranu.
 */
@Composable
fun TopLogo() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = "Logo aplikacji",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "MovieMind",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/**
 * Wskaźnik ładowania wyświetlany podczas logowania.
 */
@Composable
fun LoadingIndicator(
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Signing in...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Komunikat błędu wyświetlany w przypadku nieudanego logowania.
 */
@Composable
fun ErrorMessage(
    message: String,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            shape = MaterialTheme.shapes.medium,
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
