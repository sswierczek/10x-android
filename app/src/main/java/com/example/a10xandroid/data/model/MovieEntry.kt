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
    // Original TMDB rating (0-10 scale)
    val tmdbRating: Float? = null,
    // User's personal rating (1-5 scale)
    val userRating: Int? = null,
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
        tmdbRating = 0f,
        userRating = 1,
        watchDate = System.currentTimeMillis(),
        notes = null,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )
}
