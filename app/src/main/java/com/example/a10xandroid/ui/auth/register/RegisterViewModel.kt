package com.example.a10xandroid.ui.auth.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a10xandroid.data.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the registration screen, managing form state and registration logic
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // UI state as MutableStateFlow
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    // Update email
    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = validateEmail(email)
        )
    }

    // Update password
    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = validatePassword(password),
            confirmPasswordError = validateConfirmPassword(password, _uiState.value.confirmPassword)
        )
    }

    // Update confirm password
    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = validateConfirmPassword(_uiState.value.password, confirmPassword)
        )
    }

    // Update terms acceptance
    fun updateTermsAcceptance(accepted: Boolean) {
        _uiState.value = _uiState.value.copy(
            acceptedTerms = accepted,
            termsError = if (accepted) null else "You need to accept the terms of service"
        )
    }

    // Toggle password visibility
    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
    }

    // Toggle confirm password visibility
    fun toggleConfirmPasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isConfirmPasswordVisible = !_uiState.value.isConfirmPasswordVisible
        )
    }

    // Register
    fun register() {
        // Validate data
        val currentState = _uiState.value
        val emailError = validateEmail(currentState.email)
        val passwordError = validatePassword(currentState.password)
        val confirmPasswordError =
            validateConfirmPassword(currentState.password, currentState.confirmPassword)
        val termsError =
            if (currentState.acceptedTerms) null else "You need to accept the terms of service"

        if (emailError != null || passwordError != null || confirmPasswordError != null || termsError != null) {
            _uiState.value = currentState.copy(
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError,
                termsError = termsError
            )
            return
        }

        // Start registration
        _uiState.value = currentState.copy(
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            try {
                // Attempt registration
                val result = authRepository.signUp(
                    email = currentState.email,
                    password = currentState.password
                )

                if (result.isSuccess) {
                    // Registration successful - we don't update state,
                    // as navigation to the main screen will occur
                } else {
                    // Handle Result error
                    val exception = result.exceptionOrNull()?.let {
                        if (it is Exception) {
                            it
                        } else {
                            Exception(
                                it.message ?: "Unknown error"
                            )
                        }
                    } ?: Exception("Unknown error")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = getReadableErrorMessage(exception)
                    )
                }
            } catch (e: Exception) {
                // Handle registration error
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = getReadableErrorMessage(e)
                )
            }
        }
    }

    // Function mapping errors to user-friendly messages
    private fun getReadableErrorMessage(exception: Exception): String {
        return when {
            // Firebase Auth specific errors
            exception.message?.contains("email-already-in-use") == true ->
                "This email address is already in use by another account"

            exception.message?.contains("weak-password") == true ->
                "Password is too weak. Use at least 6 characters"

            exception.message?.contains("invalid-email") == true ->
                "Invalid email format"

            exception.message?.contains("network") == true ->
                "Network connection issue. Check your connection and try again"

            exception.message?.contains("unknown") == true ->
                "Unknown error during registration. Please try again"
            // General error
            else -> exception.message ?: "An error occurred during registration"
        }
    }

    // Email validation
    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email is required"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
            else -> null
        }
    }

    // Password validation
    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Password is required"
            password.length < 6 -> "Password must contain at least 6 characters"
            !password.any { it.isDigit() } -> "Password must contain at least one digit"
            !password.any { it.isLetter() } -> "Password must contain at least one letter"
            else -> null
        }
    }

    // Confirm password validation
    private fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isBlank() -> "Password confirmation is required"
            confirmPassword != password -> "Passwords do not match"
            else -> null
        }
    }

    // Clear general error
    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }
}
