package com.example.a10xandroid.ui.recommendations

/**
 * View model for a movie recommendation
 */
data class RecommendationMovieViewModel(
    val id: String,
    val tmdbId: String,
    val title: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val overview: String,
    val year: String,
    val genre: String,
    val rating: Float,
    val reason: String?,
    val saved: Boolean = false
)
