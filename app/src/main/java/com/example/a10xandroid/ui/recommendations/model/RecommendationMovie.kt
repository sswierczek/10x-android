package com.example.a10xandroid.ui.recommendations.model

import com.example.a10xandroid.data.model.MovieRecommendation
import com.example.a10xandroid.data.model.RecommendationStatus

/**
 * Data model representing a movie recommendation
 */
data class RecommendationMovie(
    val id: String,
    val title: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val overview: String,
    val year: String,
    val genre: String,
    val rating: Float,
    val reason: String?,
    val saved: Boolean
) {
    companion object {
        fun fromMovieRecommendation(movie: MovieRecommendation): RecommendationMovie {
            return RecommendationMovie(
                id = movie.id,
                title = movie.title,
                posterUrl = movie.posterPath?.let { path ->
                    "https://image.tmdb.org/t/p/w500$path"
                },
                backdropUrl = movie.backdropPath?.let { path ->
                    "https://image.tmdb.org/t/p/w500$path"
                },
                overview = movie.overview,
                year = movie.releaseDate?.take(4) ?: "",
                genre = "",
                rating = movie.rating ?: 0f,
                reason = movie.reason,
                saved = movie.status == RecommendationStatus.SAVED
            )
        }
    }
} 