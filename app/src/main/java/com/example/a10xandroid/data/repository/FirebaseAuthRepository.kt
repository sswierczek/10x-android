package com.example.a10xandroid.data.repository

import android.util.Log
import com.example.a10xandroid.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "FirebaseAuthRepository"

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) : AuthRepository {

    private val usersRef = database.getReference("users")

    init {
        Log.d(TAG, "FirebaseAuthRepository initialized")
        Log.d(
            TAG,
            "Initial auth state: ${auth.currentUser?.uid}, isNull: ${auth.currentUser == null}"
        )
    }

    override val currentUser: Flow<User?> = callbackFlow {
        Log.d(TAG, "Setting up currentUser Flow")

        val listener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            Log.d(
                TAG,
                "Firebase Auth state changed: ${firebaseUser?.uid}, isNull: ${firebaseUser == null}"
            )


            if (firebaseUser != null) {
                // Create user object directly from Firebase Auth user
                val user = User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    displayName = firebaseUser.displayName,
                    photoUrl = firebaseUser.photoUrl?.toString(),
                    isEmailVerified = firebaseUser.isEmailVerified
                )
                Log.d(TAG, "Sending user: ${user.uid}")
                trySend(user)
            } else {
                Log.d(TAG, "Sending null user")
                trySend(null)
            }
        }

        // Send initial state
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val user = User(
                uid = currentUser.uid,
                email = currentUser.email ?: "",
                displayName = currentUser.displayName,
                photoUrl = currentUser.photoUrl?.toString(),
                isEmailVerified = currentUser.isEmailVerified
            )
            Log.d(TAG, "Sending initial user: ${user.uid}")
            trySend(user)
        } else {
            Log.d(TAG, "Sending initial null user")
            trySend(null)
        }

        auth.addAuthStateListener(listener)
        Log.d(TAG, "Auth state listener added")

        awaitClose {
            Log.d(TAG, "Closing currentUser Flow")
            auth.removeAuthStateListener(listener)
        }
    }

    override suspend fun signIn(email: String, password: String): Result<User> = try {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val firebaseUser = result.user
        if (firebaseUser != null) {
            // Get additional user data from Realtime Database
            val snapshot = database.reference.child("users").child(firebaseUser.uid).get().await()
            val userData = snapshot.getValue(User::class.java)
            val user = User(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                displayName = userData?.displayName ?: firebaseUser.displayName,
                photoUrl = userData?.photoUrl ?: firebaseUser.photoUrl?.toString()
            )
            Result.success(user)
        } else {
            Result.failure(Exception("Authentication failed"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun signUp(email: String, password: String): Result<User> = try {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = result.user
        if (firebaseUser != null) {
            // Create a new user entry in the database with additional fields
            val user = User(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                displayName = firebaseUser.displayName,
                photoUrl = firebaseUser.photoUrl?.toString(),
                isEmailVerified = firebaseUser.isEmailVerified,
                createdAt = System.currentTimeMillis(),
                lastLoginAt = System.currentTimeMillis()
            )

            // Ensure the user data is written to the database
            database.reference.child("users").child(firebaseUser.uid)
                .setValue(user)
                .await()

            Log.d(
                "FirebaseAuthRepository",
                "User data initialized in database for uid: ${firebaseUser.uid}"
            )
            Result.success(user)
        } else {
            Result.failure(Exception("User creation failed"))
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
        // For password reset, we need to use a different approach
        // Firebase requires reCAPTCHA verification for password reset
        // We'll use the standard sendPasswordResetEmail method
        // The user will need to complete reCAPTCHA verification in the email they receive
        auth.sendPasswordResetEmail(email).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Error resetting password", e)
        Result.failure(e)
    }

    override suspend fun updateProfile(displayName: String?, photoUrl: String?): Result<Unit> =
        try {
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                // Update Firebase Auth profile
                val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .apply {
                        displayName?.let { setDisplayName(it) }
                        photoUrl?.let { setPhotoUri(android.net.Uri.parse(it)) }
                    }
                    .build()

                firebaseUser.updateProfile(profileUpdates).await()

                // Update user data in Realtime Database
                val updates = mutableMapOf<String, Any?>()
                displayName?.let { updates["displayName"] = it }
                photoUrl?.let { updates["photoUrl"] = it }

                if (updates.isNotEmpty()) {
                    database.reference.child("users").child(firebaseUser.uid)
                        .updateChildren(updates).await()
                }

                Result.success(Unit)
            } else {
                Result.failure(Exception("No user is currently signed in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun refreshUserData(): Result<Unit> = try {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            // Force a token refresh to ensure we have the latest user data
            firebaseUser.getIdToken(true).await()

            // Update the user data in the database if needed
            val user = User(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                displayName = firebaseUser.displayName,
                photoUrl = firebaseUser.photoUrl?.toString()
            )

            database.reference.child("users").child(firebaseUser.uid)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!snapshot.exists()) {
                        // If user doesn't exist in database, create it
                        database.reference.child("users").child(firebaseUser.uid).setValue(user)
                    }
                }

            Result.success(Unit)
        } else {
            Result.failure(Exception("No user is currently signed in"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}

