package com.example.a10xandroid.data.model

data class MovieListEntry(
    val id: String = "",
    val listId: String = "",
    val movieId: String = "",
    val addedAt: Long = System.currentTimeMillis(),
    val notes: String = "",
    val rating: Int? = null,
    val isWatched: Boolean = false
) 