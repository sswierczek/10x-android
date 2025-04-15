package com.example.a10xandroid.ui.auth.register

/**
 * Dane uwierzytelniające do rejestracji użytkownika
 */
data class RegisterCredentials(
    val email: String,                // Adres e-mail użytkownika
    val password: String,             // Hasło użytkownika
    val confirmPassword: String       // Potwierdzenie hasła użytkownika
) 