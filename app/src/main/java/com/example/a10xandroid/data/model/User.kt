package com.example.a10xandroid.data.model

/**
 * Data class representing a user in the application.
 * This model aligns with Firebase Authentication user data.
 */
data class User(
    val uid: String,
    val email: String,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val isEmailVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis()
) {
    // No-argument constructor required by Firebase
    constructor() : this(
        uid = "",
        email = "",
        displayName = null,
        photoUrl = null,
        isEmailVerified = false,
        createdAt = System.currentTimeMillis(),
        lastLoginAt = System.currentTimeMillis()
    )
}
