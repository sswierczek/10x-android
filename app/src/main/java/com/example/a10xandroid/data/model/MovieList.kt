package com.example.a10xandroid.data.model

data class MovieList(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val description: String = "",
    val isPublic: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val movieCount: Int = 0
) 