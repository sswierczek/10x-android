package com.example.a10xandroid.ui.recommendations.model

/**
 * Data class representing a movie recommendation item in the UI
 */
data class RecommendationMovieModel(
    val id: String,
    val title: String,
    val posterUrl: String?,
    val releaseYear: Int?,
    val genre: String?,
    val saved: Boolean = false
)
