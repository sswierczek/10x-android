package com.example.a10xandroid.ui.recommendations.model

import com.example.a10xandroid.data.dto.RecommendedMovieDTO
import com.example.a10xandroid.data.model.MovieRecommendation
import com.example.a10xandroid.data.model.RecommendationStatus
import com.example.a10xandroid.ui.common.StateStatus

/**
 * View model for a movie recommendation
 */
data class RecommendationMovieViewModel(
    val id: String,                      // Movie ID in the database (or "recommendation_{tmdbId}" for recommendations)
    val tmdbId: String,                  // Movie ID in TMDB
    val title: String,                   // Movie title
    val posterUrl: String?,              // Movie poster URL
    val backdropUrl: String?,            // Movie backdrop URL
    val overview: String,                // Movie overview
    val year: String,                    // Production year
    val genre: String,                   // Main genre
    val rating: Float,                   // Movie rating
    val reason: String?,                 // Recommendation reason (only for recommendations)
    val saved: Boolean = false           // Whether the movie is saved to the journal
) {
    companion object {
        /**
         * Creates a RecommendationMovieViewModel from a RecommendedMovieDTO
         */
        fun fromDTO(dto: RecommendedMovieDTO): RecommendationMovieViewModel {
            return RecommendationMovieViewModel(
                id = "recommendation_${dto.id}",
                tmdbId = dto.id.toString(),
                title = dto.title,
                posterUrl = dto.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
                backdropUrl = null, // Not available in DTO
                overview = dto.overview,
                year = dto.releaseDate.take(4),
                genre = "", // Not available in DTO
                rating = dto.voteAverage.toFloat(),
                reason = dto.reason
            )
        }

        /**
         * Creates a RecommendationMovieViewModel from a MovieRecommendation
         */
        fun fromMovieRecommendation(movie: MovieRecommendation): RecommendationMovieViewModel {
            return RecommendationMovieViewModel(
                id = movie.id,
                tmdbId = movie.movieId,
                title = movie.title,
                posterUrl = movie.posterPath?.let { path ->
                    "https://image.tmdb.org/t/p/w500$path"
                },
                backdropUrl = movie.backdropPath?.let { path ->
                    "https://image.tmdb.org/t/p/w500$path"
                },
                overview = movie.overview,
                year = movie.releaseDate?.take(4) ?: "",
                genre = "", // Not available in MovieRecommendation
                rating = movie.rating ?: 0f,
                reason = movie.reason,
                saved = movie.status == RecommendationStatus.SAVED
            )
        }
    }
}

/**
 * UI state for the recommendations screen
 */
data class RecommendationsUiState(
    val status: StateStatus = StateStatus.LOADING,  // UI state
    val errorMessage: String? = null,               // Error message
    val recommendations: List<RecommendationMovieViewModel> = emptyList(), // List of recommendations
    val isRefreshing: Boolean = false               // Whether refreshing is in progress
) {
    val isLoading: Boolean
        get() = status == StateStatus.LOADING

    val hasError: Boolean
        get() = status == StateStatus.ERROR

    val isEmpty: Boolean
        get() = status == StateStatus.SUCCESS && recommendations.isEmpty()

    val hasRecommendations: Boolean
        get() = status == StateStatus.SUCCESS && recommendations.isNotEmpty()
}
