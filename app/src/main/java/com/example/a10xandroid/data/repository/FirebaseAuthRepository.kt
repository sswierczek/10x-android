package com.example.a10xandroid.data.repository

import com.example.a10xandroid.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) : AuthRepository {

    private val usersRef = database.getReference("users")

    override val currentUser: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.toUser())
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signIn(email: String, password: String): Result<User> = try {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val user = result.user?.toUser()
        if (user != null) {
            // Update last login time in the database
            usersRef.child(user.uid).child("lastLoginAt").setValue(System.currentTimeMillis()).await()
            Result.success(user)
        } else {
            Result.failure(Exception("Failed to sign in"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun signUp(email: String, password: String): Result<User> = try {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user?.toUser()
        if (user != null) {
            // Store user data in the database
            usersRef.child(user.uid).setValue(user).await()
            Result.success(user)
        } else {
            Result.failure(Exception("Failed to create user"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun signOut(): Result<Unit> = try {
        auth.signOut()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun resetPassword(email: String): Result<Unit> = try {
        auth.sendPasswordResetEmail(email).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateProfile(displayName: String?, photoUrl: String?): Result<Unit> = try {
        val profileUpdates = UserProfileChangeRequest.Builder().apply {
            displayName?.let { setDisplayName(it) }
            photoUrl?.let { setPhotoUri(android.net.Uri.parse(it)) }
        }.build()

        auth.currentUser?.updateProfile(profileUpdates)?.await()
        
        // Update user data in the database
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userData = mapOf(
                "displayName" to (displayName ?: currentUser.displayName),
                "photoUrl" to (photoUrl ?: currentUser.photoUrl?.toString())
            )
            usersRef.child(currentUser.uid).updateChildren(userData).await()
        }
        
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    private fun FirebaseUser.toUser(): User {
        return User(
            uid = uid,
            email = email ?: "",
            displayName = displayName,
            photoUrl = photoUrl?.toString(),
            isEmailVerified = isEmailVerified,
            createdAt = metadata?.creationTimestamp ?: System.currentTimeMillis(),
            lastLoginAt = metadata?.lastSignInTimestamp ?: System.currentTimeMillis()
        )
    }
}

