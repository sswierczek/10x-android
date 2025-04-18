package com.example.a10xandroid.ui.recommendations.model

import com.example.a10xandroid.data.model.MovieRecommendation
import com.example.a10xandroid.data.model.RecommendationStatus
import com.example.a10xandroid.data.openrouter.RecommendedMovieDTO
import com.example.a10xandroid.ui.common.StateStatus

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
                backdropUrl = null,
                overview = dto.overview,
                year = dto.releaseDate.take(4),
                genre = "",
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
                genre = "",
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
    val status: StateStatus = StateStatus.LOADING,
    val errorMessage: String? = null,
    val recommendations: List<RecommendationMovieViewModel> = emptyList(),
    val isRefreshing: Boolean = false
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
