package com.example.a10xandroid.ui.recommendations

import com.example.a10xandroid.ui.common.StateStatus

/**
 * View model for a movie in the recommendations list
 */
data class RecommendationMovieViewModel(
    val id: String,
    val title: String,
    val posterUrl: String? = null,
    val backdropUrl: String? = null,
    val overview: String = "",
    val year: String = "",
    val genre: String = "",
    val rating: Float = 0f,
    val reason: String? = null,
    val saved: Boolean = false
)

/**
 * Represents the UI state for the recommendations screen
 */
data class RecommendationsUiState(
    val status: StateStatus = StateStatus.LOADING,
    val recommendations: List<RecommendationMovieViewModel> = emptyList(),
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val isEmpty: Boolean = false
) {
    val hasRecommendations: Boolean
        get() = recommendations.isNotEmpty()
}
