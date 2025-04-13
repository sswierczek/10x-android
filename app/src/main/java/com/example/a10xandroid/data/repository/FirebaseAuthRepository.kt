package com.example.a10xandroid.data.repository

import com.example.a10xandroid.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
) : AuthRepository {
    override suspend fun signIn(
        email: String,
        password: String,
    ): User? {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { mapFirebaseUserToUser(it) }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun signUp(
        email: String,
        password: String,
        displayName: String?,
    ): User? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { firebaseUser ->
                displayName?.let {
                    val profileUpdates = userProfileChangeRequest {
                        this.displayName = it
                    }
                    firebaseUser.updateProfile(profileUpdates).await()
                }
                mapFirebaseUserToUser(firebaseUser)
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun getCurrentUser(): User? {
        return auth.currentUser?.let { mapFirebaseUserToUser(it) }
    }

    override fun getAuthState(): Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.let { mapFirebaseUserToUser(it) })
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    private fun mapFirebaseUserToUser(firebaseUser: FirebaseUser): User {
        return User(
            id = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            displayName = firebaseUser.displayName,
            createdAt = firebaseUser.metadata?.creationTimestamp ?: System.currentTimeMillis(),
        )
    }
}

