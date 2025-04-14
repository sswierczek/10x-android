package com.example.a10xandroid.data.repository

import com.example.a10xandroid.data.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Interface for authentication operations.
 */
interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signUp(email: String, password: String): Result<User>
    suspend fun signOut(): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun updateProfile(displayName: String?, photoUrl: String?): Result<Unit>
    suspend fun refreshUserData(): Result<Unit>
}

data class User(
    val uid: String,
    val email: String,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val isEmailVerified: Boolean = false
)
 