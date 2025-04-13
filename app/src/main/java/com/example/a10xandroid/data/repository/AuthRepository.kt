package com.example.a10xandroid.data.repository

import com.example.a10xandroid.data.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Interface for authentication operations.
 */
interface AuthRepository {
    suspend fun signIn(email: String, password: String): User?

    suspend fun signUp(email: String, password: String, displayName: String?): User?

    suspend fun signOut()

    suspend fun getCurrentUser(): User?

    fun getAuthState(): Flow<User?>
}
