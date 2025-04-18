package com.example.a10xandroid.ui.recommendations

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a10xandroid.data.auth.AuthRepository
import com.example.a10xandroid.data.model.MovieEntry
import com.example.a10xandroid.data.openrouter.RecommendationsService
import com.example.a10xandroid.data.openrouter.RecommendedMovieDTO
import com.example.a10xandroid.data.repository.MovieRepository
import com.example.a10xandroid.data.tmbd.TmdbRepository
import com.example.a10xandroid.ui.common.StateStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "RecommendationsViewModel"

data class RecommendationsUiState(
    val status: StateStatus = StateStatus.LOADING,
    val recommendations: List<RecommendationMovieViewModel> = emptyList(),
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false,
    val snackbarMessage: String? = null
) {
    val isLoading: Boolean
        get() = status == StateStatus.LOADING
    val hasError: Boolean
        get() = status == StateStatus.ERROR
    val isEmpty: Boolean
        get() = !isLoading && !hasError && recommendations.isEmpty()
}

/**
 * ViewModel for the RecommendationsScreen
 */
@HiltViewModel
class RecommendationsViewModel @Inject constructor(
    private val recommendationsService: RecommendationsService,
    private val movieRepository: MovieRepository,
    private val tmdbRepository: TmdbRepository,
    private val authRepository: AuthRepository
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
                // Get current user ID
                val currentUser = authRepository.currentUser.first()
                if (currentUser == null) {
                    Log.e(TAG, "No user logged in")
                    _uiState.update {
                        it.copy(
                            status = StateStatus.ERROR,
                            errorMessage = "You must be logged in to get recommendations",
                            snackbarMessage = "Please log in to see recommendations"
                        )
                    }
                    return@launch
                }

                // Get user movies
                val userMovies = movieRepository.getMovieEntries(currentUser.uid)
                Log.d(TAG, "Found ${userMovies.size} movies in user's journal")

                if (userMovies.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            status = StateStatus.SUCCESS,
                            recommendations = emptyList(),
                            snackbarMessage = "Add some movies to your journal to get recommendations"
                        )
                    }
                    return@launch
                }

                // Get recommendations from service
                Log.d(TAG, "Requesting recommendations from service")
                val recommendations = recommendationsService.getRecommendations(userMovies)
                Log.d(TAG, "Received ${recommendations.size} recommendations from service")

                // Process recommendations to view models
                val viewModels = processRecommendations(recommendations)
                Log.d(TAG, "Processed ${viewModels.size} recommendations to view models")

                _uiState.update {
                    it.copy(
                        status = StateStatus.SUCCESS,
                        recommendations = viewModels,
                        errorMessage = null,
                        snackbarMessage = "Found ${viewModels.size} recommendations for you"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading recommendations", e)
                _uiState.update {
                    it.copy(
                        status = StateStatus.ERROR,
                        errorMessage = e.message ?: "Failed to load recommendations",
                        snackbarMessage = "Error: ${e.message}"
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
                // Get current user ID
                val currentUser = authRepository.currentUser.first()
                if (currentUser == null) {
                    Log.e(TAG, "No user logged in")
                    _uiState.update {
                        it.copy(
                            status = StateStatus.ERROR,
                            errorMessage = "You must be logged in to get recommendations",
                            isRefreshing = false,
                            snackbarMessage = "Please log in to refresh recommendations"
                        )
                    }
                    return@launch
                }

                // Get user movies
                val userMovies = movieRepository.getMovieEntries(currentUser.uid)

                // If user has no movies, return empty state
                if (userMovies.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            status = StateStatus.SUCCESS,
                            recommendations = emptyList(),
                            isRefreshing = false,
                            snackbarMessage = "Add some movies to your journal to get recommendations"
                        )
                    }
                    return@launch
                }

                // Get recommendations from service
                val recommendations = recommendationsService.getRecommendations(userMovies)
                // Process recommendations to view models
                val viewModels = processRecommendations(recommendations)

                _uiState.update {
                    it.copy(
                        status = StateStatus.SUCCESS,
                        recommendations = viewModels,
                        errorMessage = null,
                        isRefreshing = false,
                        snackbarMessage = "Refreshed! Found ${viewModels.size} recommendations"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing recommendations", e)
                _uiState.update {
                    it.copy(
                        status = StateStatus.ERROR,
                        errorMessage = e.message ?: "Failed to refresh recommendations",
                        isRefreshing = false,
                        snackbarMessage = "Error: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Process recommendations and convert to view models
     */
    private suspend fun processRecommendations(recommendations: List<RecommendedMovieDTO>): List<RecommendationMovieViewModel> {
        Log.d(TAG, "Processing ${recommendations.size} recommendations")
        return recommendations.map { recommendation ->
            try {
                Log.d(
                    TAG,
                    "Processing recommendation: ${recommendation.title} (TMDB ID: ${recommendation.tmdbId})"
                )
                // Get additional details from TMDB
                val tmdbMovie = tmdbRepository.getMovieDetails(recommendation.tmdbId).first()
                Log.d(
                    TAG,
                    "Received TMDB details for ${recommendation.title}: ${tmdbMovie != null}"
                )

                if (tmdbMovie != null) {
                    // Create view model with additional details from TMDB
                    Log.d(TAG, "Creating view model with TMDB details for ${recommendation.title}")
                    RecommendationMovieViewModel(
                        id = "recommendation_${recommendation.tmdbId}",
                        tmdbId = recommendation.tmdbId,
                        title = recommendation.title,
                        posterUrl = tmdbMovie.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
                        backdropUrl = tmdbMovie.backdropPath?.let { "https://image.tmdb.org/t/p/w500$it" },
                        overview = recommendation.overview,
                        year = tmdbMovie.releaseDate?.take(4) ?: "",
                        genre = "",
                        rating = tmdbMovie.voteAverage?.toFloat() ?: 0f,
                        reason = recommendation.reason
                    )
                } else {
                    // Return basic view model without TMDB details
                    Log.d(
                        TAG,
                        "Creating basic view model without TMDB details for ${recommendation.title}"
                    )
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
                // Get current user ID
                val currentUser = authRepository.currentUser.first()
                if (currentUser == null) {
                    Log.e(TAG, "No user logged in")
                    _uiState.update {
                        it.copy(
                            status = StateStatus.ERROR,
                            errorMessage = "You must be logged in to add movies",
                            snackbarMessage = "Please log in to add movies to your journal"
                        )
                    }
                    return@launch
                }

                // Create a MovieEntry from the recommendation
                val movieEntry = MovieEntry(
                    tmdbId = movie.tmdbId,
                    userId = currentUser.uid,
                    title = movie.title,
                    overview = movie.overview,
                    posterPath = movie.posterUrl?.removePrefix("https://image.tmdb.org/t/p/w500"),
                    backdropPath = movie.backdropUrl?.removePrefix(
                        "https://image.tmdb.org/t/p/w500"
                    ),
                    releaseDate = "${movie.year}-01-01",
                    tmdbRating = movie.rating,
                    userRating = 5,
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
                            },
                            snackbarMessage = "${movie.title} added to your journal"
                        )
                    }
                } else {
                    Log.e(TAG, "Failed to add movie ${movie.title} to journal")
                    _uiState.update {
                        it.copy(snackbarMessage = "Failed to add ${movie.title} to journal")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error adding movie to journal", e)
                _uiState.update {
                    it.copy(
                        status = StateStatus.ERROR,
                        errorMessage = e.message ?: "Failed to add movie to journal",
                        snackbarMessage = "Error: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
