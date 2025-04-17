package com.example.a10xandroid.data.model

/**
 * Represents a movie entry in the user's journal
 */
data class MovieEntry(
    val id: String = "",
    val tmdbId: String = "",
    val userId: String = "",
    val title: String = "",
    val overview: String = "",
    val posterPath: String? = null,
    val backdropPath: String? = null,
    val releaseDate: String? = null,
    val rating: Float = 0f,
    val watchDate: Long = System.currentTimeMillis(),
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
) {
    // No-argument constructor required by Firebase
    constructor() : this(
        id = "",
        tmdbId = "",
        userId = "",
        title = "",
        overview = "",
        posterPath = null,
        backdropPath = null,
        releaseDate = null,
        rating = 0f,
        watchDate = System.currentTimeMillis(),
        notes = null,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )
}
