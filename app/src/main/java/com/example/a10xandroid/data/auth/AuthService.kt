package com.example.a10xandroid.data.auth

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthService @Inject constructor(
    private val authRepository: AuthRepository,
) {
    val currentUser = authRepository.currentUser

    suspend fun signIn(email: String, password: String) = authRepository.signIn(email, password)

    suspend fun signUp(email: String, password: String) = authRepository.signUp(email, password)

    suspend fun signOut() = authRepository.signOut()

    suspend fun resetPassword(email: String) = authRepository.resetPassword(email)

    suspend fun updateProfile(displayName: String?, photoUrl: String?) =
        authRepository.updateProfile(
            displayName,
            photoUrl
        )

    suspend fun refreshUserData() = authRepository.refreshUserData()
}
