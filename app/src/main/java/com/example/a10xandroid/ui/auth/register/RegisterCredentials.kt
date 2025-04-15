package com.example.a10xandroid.ui.auth.register

/**
 * Dane uwierzytelniające do rejestracji użytkownika
 */
data class RegisterCredentials(
    val email: String,
    val password: String,
    val confirmPassword: String
)
