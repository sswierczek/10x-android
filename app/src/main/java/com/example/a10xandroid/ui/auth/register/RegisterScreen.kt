package com.example.a10xandroid.ui.auth.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.TextButton
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
 * Main registration screen containing all components needed for registration.
 */
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo aplikacji
            TopLogo()

            Spacer(modifier = Modifier.height(32.dp))

            // Formularz rejestracji
            RegisterForm(
                email = uiState.email,
                password = uiState.password,
                confirmPassword = uiState.confirmPassword,
                acceptedTerms = uiState.acceptedTerms,
                isPasswordVisible = uiState.isPasswordVisible,
                isConfirmPasswordVisible = uiState.isConfirmPasswordVisible,
                emailError = uiState.emailError,
                passwordError = uiState.passwordError,
                confirmPasswordError = uiState.confirmPasswordError,
                termsError = uiState.termsError,
                isLoading = uiState.isLoading,
                onEmailChange = {
                    viewModel.updateEmail(it)
                    if (uiState.errorMessage != null) {
                        viewModel.clearErrorMessage()
                    }
                },
                onPasswordChange = {
                    viewModel.updatePassword(it)
                    if (uiState.errorMessage != null) {
                        viewModel.clearErrorMessage()
                    }
                },
                onConfirmPasswordChange = {
                    viewModel.updateConfirmPassword(it)
                    if (uiState.errorMessage != null) {
                        viewModel.clearErrorMessage()
                    }
                },
                onTermsChange = {
                    viewModel.updateTermsAcceptance(it)
                    if (uiState.errorMessage != null) {
                        viewModel.clearErrorMessage()
                    }
                },
                onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
                onToggleConfirmPasswordVisibility = viewModel::toggleConfirmPasswordVisibility,
                onRegisterClick = {
                    scope.launch {
                        viewModel.register()
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Link do logowania
            LoginLink(
                onClick = {
                    // Nawigacja do ekranu logowania
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.REGISTER) { inclusive = true }
                    }
                }
            )
        }

        // Wyświetlanie błędu rejestracji
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
 * Wskaźnik ładowania wyświetlany podczas rejestracji.
 */
@Composable
fun LoadingIndicator(
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        CircularProgressIndicator(modifier = modifier)
    }
}

/**
 * Komunikat błędu wyświetlany w przypadku nieudanej rejestracji.
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

/**
 * Link do ekranu logowania.
 */
@Composable
fun LoginLink(
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Masz już konto?",
            style = MaterialTheme.typography.bodyMedium
        )

        TextButton(
            onClick = onClick,
            contentPadding = PaddingValues(4.dp)
        ) {
            Text(
                text = "Zaloguj się",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
