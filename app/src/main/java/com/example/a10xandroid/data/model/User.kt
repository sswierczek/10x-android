package com.example.a10xandroid.data.model

/**
 * Data class representing a user in the application.
 */
data class User(
    val id: String,
    val email: String,
    val displayName: String?,
    val createdAt: Long,
)
