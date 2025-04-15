package com.example.a10xandroid.ui.recommendations

/**
 * Reprezentuje różne stany UI
 */
enum class StateStatus {
    LOADING,
    SUCCESS,
    ERROR
}

/**
 * Model widoku filmu dla listy rekomendacji
 */
data class MovieViewModel(
    val id: String,
    val title: String,
    val posterUrl: String? = null,
    val backdropUrl: String? = null,
    val overview: String = "",
    val year: String = "",
    val genre: String = "",
    val rating: Float = 0f,
    val reason: String? = null
)

/**
 * Reprezentuje stan UI dla ekranu rekomendacji
 */
data class RecommendationsUiState(
    val status: StateStatus = StateStatus.LOADING,
    val recommendations: List<MovieViewModel> = emptyList(),
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false
) {
    val hasRecommendations: Boolean
        get() = recommendations.isNotEmpty()
        
    val isEmpty: Boolean
        get() = status == StateStatus.SUCCESS && !hasRecommendations
} 