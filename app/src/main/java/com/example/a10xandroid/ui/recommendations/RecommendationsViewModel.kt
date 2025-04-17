package com.example.a10xandroid.ui.recommendations

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a10xandroid.data.api.model.TmdbMovieDetailsApiResponse
import com.example.a10xandroid.data.dto.RecommendedMovieDTO
import com.example.a10xandroid.data.model.MovieEntry
import com.example.a10xandroid.data.repository.MovieRepository
import com.example.a10xandroid.data.repository.TmdbRepository
import com.example.a10xandroid.service.RecommendationsService
import com.example.a10xandroid.ui.common.StateStatus
import com.example.a10xandroid.ui.recommendations.model.RecommendationMovieViewModel
import com.example.a10xandroid.ui.recommendations.model.RecommendationsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "RecommendationsViewModel"

/**
 * ViewModel for the RecommendationsScreen
 */
@HiltViewModel
class RecommendationsViewModel @Inject constructor(
    private val recommendationsService: RecommendationsService,
    private val movieRepository: MovieRepository,
    private val tmdbRepository: TmdbRepository
) : ViewModel() {

    // UI state as StateFlow
    private val _uiState = MutableStateFlow(RecommendationsUiState())
    val uiState: StateFlow<RecommendationsUiState> = _uiState.asStateFlow()

    init {
        loadRecommendations()
    }

    /**
     * Loads recommendations for the current user
     */
    fun loadRecommendations() {
        viewModelScope.launch {
            _uiState.update { it.copy(status = StateStatus.LOADING, errorMessage = null) }

            try {
                // Get user movies
                val userMovies = movieRepository.getMovieEntries("current_user")

                // If user has no movies, return empty state
                if (userMovies.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            status = StateStatus.SUCCESS,
                            recommendations = emptyList()
                        )
                    }
                    return@launch
                }

                // Get recommendations from service
                recommendationsService.getRecommendations(userMovies).collect { result ->
                    result.fold(
                        onSuccess = { recommendations ->
                            // Process recommendations to view models
                            val viewModels = processRecommendations(recommendations)

                            _uiState.update {
                                it.copy(
                                    status = StateStatus.SUCCESS,
                                    recommendations = viewModels,
                                    errorMessage = null
                                )
                            }
                        },
                        onFailure = { error ->
                            Log.e(TAG, "Error loading recommendations", error)
                            _uiState.update {
                                it.copy(
                                    status = StateStatus.ERROR,
                                    errorMessage = error.message ?: "Failed to load recommendations"
                                )
                            }
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading recommendations", e)
                _uiState.update {
                    it.copy(
                        status = StateStatus.ERROR,
                        errorMessage = e.message ?: "Failed to load recommendations"
                    )
                }
            }
        }
    }

    /**
     * Refreshes the recommendations list
     */
    fun refreshRecommendations() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    status = StateStatus.LOADING,
                    errorMessage = null,
                    isRefreshing = true
                )
            }

            try {
                // Get user movies
                val userMovies = movieRepository.getMovieEntries("current_user")

                // If user has no movies, return empty state
                if (userMovies.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            status = StateStatus.SUCCESS,
                            recommendations = emptyList(),
                            isRefreshing = false
                        )
                    }
                    return@launch
                }

                // Get recommendations from service
                recommendationsService.getRecommendations(userMovies).collect { result ->
                    result.fold(
                        onSuccess = { recommendations ->
                            // Process recommendations to view models
                            val viewModels = processRecommendations(recommendations)

                            _uiState.update {
                                it.copy(
                                    status = StateStatus.SUCCESS,
                                    recommendations = viewModels,
                                    errorMessage = null,
                                    isRefreshing = false
                                )
                            }
                        },
                        onFailure = { error ->
                            Log.e(TAG, "Error refreshing recommendations", error)
                            _uiState.update {
                                it.copy(
                                    status = StateStatus.ERROR,
                                    errorMessage = error.message
                                        ?: "Failed to refresh recommendations",
                                    isRefreshing = false
                                )
                            }
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing recommendations", e)
                _uiState.update {
                    it.copy(
                        status = StateStatus.ERROR,
                        errorMessage = e.message ?: "Failed to refresh recommendations",
                        isRefreshing = false
                    )
                }
            }
        }
    }

    /**
     * Process recommendations and convert to view models
     */
    private suspend fun processRecommendations(recommendations: List<RecommendedMovieDTO>): List<RecommendationMovieViewModel> {
        return recommendations.map { recommendation ->
            try {
                // Get additional details from TMDB
                var tmdbMovie: TmdbMovieDetailsApiResponse? = null
                tmdbRepository.getMovieDetails(recommendation.tmdbId).collect { response ->
                    tmdbMovie = response
                }

                if (tmdbMovie != null) {
                    // Create view model with additional details from TMDB
                    RecommendationMovieViewModel(
                        id = "recommendation_${recommendation.tmdbId}",
                        tmdbId = recommendation.tmdbId,
                        title = recommendation.title,
                        posterUrl = tmdbMovie?.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
                        backdropUrl = tmdbMovie?.backdropPath?.let { "https://image.tmdb.org/t/p/w500$it" },
                        overview = recommendation.overview,
                        year = tmdbMovie?.releaseDate?.take(4) ?: "",
                        genre = tmdbMovie?.genres?.firstOrNull()?.name ?: "Unknown",
                        rating = tmdbMovie?.voteAverage?.toFloat() ?: 0f,
                        reason = recommendation.reason
                    )
                } else {
                    // Return basic view model without TMDB details
                    RecommendationMovieViewModel(
                        id = "recommendation_${recommendation.tmdbId}",
                        tmdbId = recommendation.tmdbId,
                        title = recommendation.title,
                        posterUrl = null,
                        backdropUrl = null,
                        overview = recommendation.overview,
                        year = "",
                        genre = "Unknown",
                        rating = 0f,
                        reason = recommendation.reason
                    )
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to get details for movie ${recommendation.tmdbId}", e)
                // Return basic view model without TMDB details
                RecommendationMovieViewModel(
                    id = "recommendation_${recommendation.tmdbId}",
                    tmdbId = recommendation.tmdbId,
                    title = recommendation.title,
                    posterUrl = null,
                    backdropUrl = null,
                    overview = recommendation.overview,
                    year = "",
                    genre = "Unknown",
                    rating = 0f,
                    reason = recommendation.reason
                )
            }
        }
    }

    /**
     * Adds a recommended movie to the user's journal
     */
    fun addToJournal(movie: RecommendationMovieViewModel) {
        viewModelScope.launch {
            try {
                // Create a MovieEntry from the recommendation
                val movieEntry = MovieEntry(
                    tmdbId = movie.tmdbId,
                    userId = "current_user",
                    title = movie.title,
                    overview = movie.overview,
                    posterPath = movie.posterUrl?.removePrefix("https://image.tmdb.org/t/p/w500"),
                    backdropPath = movie.backdropUrl?.removePrefix("https://image.tmdb.org/t/p/w500"),
                    releaseDate = "${movie.year}-01-01", // Default to January 1st since we only have the year
                    rating = movie.rating,
                    notes = movie.reason
                )

                // Add the movie to the journal
                val success = movieRepository.addMovieEntry(movieEntry)

                if (success) {
                    Log.d(TAG, "Successfully added movie ${movie.title} to journal")
                    // Update the UI state to mark this recommendation as saved
                    _uiState.update { state ->
                        state.copy(
                            recommendations = state.recommendations.map { recommendation ->
                                if (recommendation.id == movie.id) {
                                    recommendation.copy(saved = true)
                                } else {
                                    recommendation
                                }
                            }
                        )
                    }
                } else {
                    Log.e(TAG, "Failed to add movie ${movie.title} to journal")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error adding movie ${movie.title} to journal", e)
            }
        }
    }
}
