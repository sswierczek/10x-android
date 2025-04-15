package com.example.a10xandroid.ui.auth.login

/**
 * Stan UI dla ekranu logowania
 */
data class LoginUiState(
    val email: String = "",              // Wprowadzony e-mail
    val password: String = "",           // Wprowadzone hasło
    val isPasswordVisible: Boolean = false, // Czy hasło jest widoczne
    val emailError: String? = null,      // Błąd walidacji e-maila
    val passwordError: String? = null,   // Błąd walidacji hasła
    val isLoading: Boolean = false,      // Czy trwa logowanie
    val errorMessage: String? = null     // Ogólny komunikat błędu
) 