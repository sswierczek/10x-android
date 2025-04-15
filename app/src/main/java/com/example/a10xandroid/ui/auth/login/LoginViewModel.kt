package com.example.a10xandroid.ui.auth.login

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
 * ViewModel dla ekranu logowania, zarządzający stanem formularza i logiką uwierzytelniania
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Stan UI jako MutableStateFlow
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

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
            passwordError = validatePassword(password)
        )
    }

    // Przełączanie widoczności hasła
    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
    }

    // Logowanie
    fun login() {
        // Sprawdzenie poprawności danych
        val currentState = _uiState.value
        val emailError = validateEmail(currentState.email)
        val passwordError = validatePassword(currentState.password)

        if (emailError != null || passwordError != null) {
            _uiState.value = currentState.copy(
                emailError = emailError,
                passwordError = passwordError
            )
            return
        }

        // Rozpoczęcie logowania
        _uiState.value = currentState.copy(
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            try {
                // Próba logowania
                val result = authRepository.signIn(
                    email = currentState.email,
                    password = currentState.password
                )

                if (result.isSuccess) {
                    // Logowanie udane - nie aktualizujemy stanu,
                    // ponieważ nastąpi nawigacja do głównego ekranu
                } else {
                    // Obsługa błędu z Result
                    val exception = result.exceptionOrNull()?.let {
                        if (it is Exception) it else Exception(
                            it.message ?: "Nieznany błąd podczas logowania"
                        )
                    } ?: Exception("Nieznany błąd podczas logowania")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = getReadableErrorMessage(exception)
                    )
                }
            } catch (e: Exception) {
                // Obsługa błędu logowania
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
            exception.message?.contains("user-not-found") == true ->
                "Użytkownik o podanym adresie e-mail nie istnieje"

            exception.message?.contains("wrong-password") == true ->
                "Niepoprawne hasło"

            exception.message?.contains("user-disabled") == true ->
                "To konto zostało wyłączone"

            exception.message?.contains("invalid-email") == true ->
                "Niepoprawny format adresu e-mail"

            exception.message?.contains("too-many-requests") == true ->
                "Zbyt wiele nieudanych prób logowania. Spróbuj ponownie później"

            exception.message?.contains("network") == true ->
                "Problem z połączeniem internetowym. Sprawdź swoje połączenie i spróbuj ponownie"

            exception.message?.contains("unknown") == true ->
                "Nieznany błąd podczas logowania. Spróbuj ponownie"
            // Ogólny błąd
            else -> exception.message ?: "Wystąpił błąd podczas logowania"
        }
    }

    // Walidacja e-maila
    private fun validateEmail(email: String): String? {
        return if (email.isBlank()) {
            "E-mail jest wymagany"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            "Niepoprawny format adresu e-mail"
        } else {
            null
        }
    }

    // Walidacja hasła
    private fun validatePassword(password: String): String? {
        return if (password.isBlank()) {
            "Hasło jest wymagane"
        } else {
            null
        }
    }

    // Czyszczenie błędu ogólnego
    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }
}
