package com.example.a10xandroid.ui.auth.register

/**
 * Stan UI dla ekranu rejestracji
 */
data class RegisterUiState(
    val email: String = "",              // Wprowadzony e-mail
    val password: String = "",           // Wprowadzone hasło
    val confirmPassword: String = "",    // Potwierdzenie hasła
    val acceptedTerms: Boolean = false,  // Akceptacja warunków korzystania
    val isPasswordVisible: Boolean = false, // Czy hasło jest widoczne
    val isConfirmPasswordVisible: Boolean = false, // Czy potwierdzenie hasła jest widoczne
    val emailError: String? = null,      // Błąd walidacji e-maila
    val passwordError: String? = null,   // Błąd walidacji hasła
    val confirmPasswordError: String? = null, // Błąd walidacji potwierdzenia hasła
    val termsError: String? = null,      // Błąd walidacji akceptacji warunków
    val isLoading: Boolean = false,      // Czy trwa rejestracja
    val errorMessage: String? = null     // Ogólny komunikat błędu
) 