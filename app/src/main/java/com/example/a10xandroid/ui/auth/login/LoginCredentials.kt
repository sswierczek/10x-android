package com.example.a10xandroid.ui.auth.login

/**
 * Dane uwierzytelniające do logowania użytkownika
 */
data class LoginCredentials(
    val email: String,    // Adres e-mail użytkownika
    val password: String  // Hasło użytkownika
) 