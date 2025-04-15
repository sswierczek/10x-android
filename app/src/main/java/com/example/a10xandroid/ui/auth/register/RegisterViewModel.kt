package com.example.a10xandroid.ui.auth.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a10xandroid.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel dla ekranu rejestracji, zarządzający stanem formularza i logiką rejestracji
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Stan UI jako MutableStateFlow
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    // Aktualizacja e-maila
    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = validateEmail(email)
        )
    }

    // Aktualizacja hasła
    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = validatePassword(password),
            confirmPasswordError = validateConfirmPassword(password, _uiState.value.confirmPassword)
        )
    }

    // Aktualizacja potwierdzenia hasła
    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = validateConfirmPassword(_uiState.value.password, confirmPassword)
        )
    }

    // Aktualizacja akceptacji warunków
    fun updateTermsAcceptance(accepted: Boolean) {
        _uiState.value = _uiState.value.copy(
            acceptedTerms = accepted,
            termsError = if (accepted) null else "Musisz zaakceptować warunki korzystania"
        )
    }

    // Przełączanie widoczności hasła
    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
    }

    // Przełączanie widoczności potwierdzenia hasła
    fun toggleConfirmPasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isConfirmPasswordVisible = !_uiState.value.isConfirmPasswordVisible
        )
    }

    // Rejestracja
    fun register() {
        // Sprawdzenie poprawności danych
        val currentState = _uiState.value
        val emailError = validateEmail(currentState.email)
        val passwordError = validatePassword(currentState.password)
        val confirmPasswordError = validateConfirmPassword(currentState.password, currentState.confirmPassword)
        val termsError = if (currentState.acceptedTerms) null else "Musisz zaakceptować warunki korzystania"

        if (emailError != null || passwordError != null || confirmPasswordError != null || termsError != null) {
            _uiState.value = currentState.copy(
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError,
                termsError = termsError
            )
            return
        }

        // Rozpoczęcie rejestracji
        _uiState.value = currentState.copy(
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            try {
                // Próba rejestracji
                val result = authRepository.signUp(
                    email = currentState.email,
                    password = currentState.password
                )
                
                if (result.isSuccess) {
                    // Rejestracja udana - nie aktualizujemy stanu,
                    // ponieważ nastąpi nawigacja do głównego ekranu
                } else {
                    // Obsługa błędu z Result
                    val exception = result.exceptionOrNull() ?: Exception("Nieznany błąd podczas rejestracji")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = getReadableErrorMessage(exception)
                    )
                }
            } catch (e: Exception) {
                // Obsługa błędu rejestracji
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = getReadableErrorMessage(e)
                )
            }
        }
    }
    
    // Funkcja mapująca błędy na przyjazne komunikaty dla użytkownika
    private fun getReadableErrorMessage(exception: Exception): String {
        return when {
            // Błędy specyficzne dla Firebase Auth
            exception.message?.contains("email-already-in-use") == true -> 
                "Ten adres e-mail jest już używany przez inne konto"
            exception.message?.contains("weak-password") == true -> 
                "Hasło jest zbyt słabe. Użyj co najmniej 6 znaków"
            exception.message?.contains("invalid-email") == true -> 
                "Niepoprawny format adresu e-mail"
            exception.message?.contains("network") == true -> 
                "Problem z połączeniem internetowym. Sprawdź swoje połączenie i spróbuj ponownie"
            exception.message?.contains("unknown") == true -> 
                "Nieznany błąd podczas rejestracji. Spróbuj ponownie"
            // Ogólny błąd
            else -> exception.message ?: "Wystąpił błąd podczas rejestracji"
        }
    }

    // Walidacja e-maila
    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "E-mail jest wymagany"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Niepoprawny format adresu e-mail"
            else -> null
        }
    }

    // Walidacja hasła
    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Hasło jest wymagane"
            password.length < 6 -> "Hasło musi zawierać co najmniej 6 znaków"
            !password.any { it.isDigit() } -> "Hasło musi zawierać co najmniej jedną cyfrę"
            !password.any { it.isLetter() } -> "Hasło musi zawierać co najmniej jedną literę"
            else -> null
        }
    }

    // Walidacja potwierdzenia hasła
    private fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isBlank() -> "Potwierdzenie hasła jest wymagane"
            confirmPassword != password -> "Hasła nie są identyczne"
            else -> null
        }
    }

    // Czyszczenie błędu ogólnego
    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }
} 