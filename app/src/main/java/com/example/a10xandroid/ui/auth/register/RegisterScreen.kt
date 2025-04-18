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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.example.a10xandroid.ui.components.AppTopBar
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

    // Effect for automatically hiding error message after some time
    LaunchedEffect(key1 = uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            delay(5000) // Show error for 5 seconds
            viewModel.clearErrorMessage()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Create Account",
                onBackClick = { navController.navigateUp() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Registration form
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

                // Login link
                LoginLink(
                    onClick = {
                        navController.navigate(NavRoutes.LOGIN) {
                            popUpTo(NavRoutes.REGISTER) { inclusive = true }
                        }
                    }
                )
            }

            // Display registration error
            if (uiState.errorMessage != null) {
                ErrorMessage(
                    message = uiState.errorMessage!!,
                    isVisible = true,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                )
            }

            // Loading indicator
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
}

/**
 * Loading indicator displayed during registration.
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
 * Error message displayed in case of failed registration.
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
 * Link to the login screen.
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
            text = "Already have an account?",
            style = MaterialTheme.typography.bodyMedium
        )

        TextButton(
            onClick = onClick,
            contentPadding = PaddingValues(4.dp)
        ) {
            Text(
                text = "Log in",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
