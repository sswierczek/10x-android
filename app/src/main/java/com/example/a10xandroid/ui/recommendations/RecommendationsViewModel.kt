package com.example.a10xandroid.ui.recommendations

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a10xandroid.data.repository.RecommendationsRepository
import com.example.a10xandroid.ui.recommendations.model.RecommendationMovie
import com.example.a10xandroid.ui.recommendations.model.RecommendationsUiState
import com.example.a10xandroid.ui.common.StateStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "RecommendationsViewModel"

/**
 * ViewModel for managing recommendations screen UI state
 */
@HiltViewModel
class RecommendationsViewModel @Inject constructor(
    private val recommendationsRepository: RecommendationsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecommendationsUiState())
    val uiState: StateFlow<RecommendationsUiState> = _uiState.asStateFlow()

    init {
        Log.d(TAG, "ViewModel initialized")
        loadRecommendations()
    }

    /**
     * Loads recommendations
     */
    fun loadRecommendations() {
        Log.d(TAG, "loadRecommendations called")
        viewModelScope.launch {
            try {
                Log.d(TAG, "Setting loading state")
                _uiState.update { it.copy(status = StateStatus.LOADING) }

                Log.d(TAG, "Fetching recommendations from repository")
                val recommendations = recommendationsRepository.getRecommendations()
                Log.d(TAG, "Received ${recommendations.size} recommendations")
                
                val movies = recommendations.map { movie ->
                    RecommendationMovie.fromMovieRecommendation(movie)
                }
                Log.d(TAG, "Mapped to ${movies.size} view models")

                _uiState.update { state ->
                    Log.d(TAG, "Updating UI state to SUCCESS")
                    state.copy(
                        status = StateStatus.SUCCESS,
                        recommendations = movies,
                        isEmpty = movies.isEmpty(),
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading recommendations", e)
                _uiState.update { state ->
                    state.copy(
                        status = StateStatus.ERROR,
                        errorMessage = e.message ?: "Failed to load recommendations"
                    )
                }
            }
        }
    }

    /**
     * Clears the error message
     */
    fun clearError() {
        Log.d(TAG, "clearError called")
        _uiState.update { 
            it.copy(
                errorMessage = null,
                status = if (it.recommendations.isEmpty()) StateStatus.LOADING else StateStatus.SUCCESS
            ) 
        }
    }

    /**
     * Dismisses a recommendation
     */
    fun dismissRecommendation(movieId: String) {
        Log.d(TAG, "dismissRecommendation called for movieId: $movieId")
        viewModelScope.launch {
            try {
                Log.d(TAG, "Calling repository to dismiss recommendation")
                recommendationsRepository.dismissRecommendation(movieId)
                _uiState.update { state ->
                    val updatedRecommendations = state.recommendations.filterNot { it.id == movieId }
                    Log.d(TAG, "Updated recommendations list size: ${updatedRecommendations.size}")
                    state.copy(
                        recommendations = updatedRecommendations,
                        isEmpty = updatedRecommendations.isEmpty(),
                        status = if (updatedRecommendations.isEmpty()) StateStatus.SUCCESS else state.status
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error dismissing recommendation", e)
                _uiState.update { 
                    it.copy(
                        errorMessage = "Failed to dismiss recommendation"
                    ) 
                }
            }
        }
    }

    /**
     * Saves a recommendation to watchlist
     */
    fun saveRecommendation(movieId: String) {
        Log.d(TAG, "saveRecommendation called for movieId: $movieId")
        viewModelScope.launch {
            try {
                Log.d(TAG, "Calling repository to save recommendation")
                recommendationsRepository.saveToWatchlist(movieId)
                _uiState.update { state ->
                    val updatedRecommendations = state.recommendations.map { movie ->
                        if (movie.id == movieId) {
                            movie.copy(saved = true)
                        } else {
                            movie
                        }
                    }
                    Log.d(TAG, "Updated recommendations with saved status")
                    state.copy(
                        recommendations = updatedRecommendations
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving recommendation", e)
                _uiState.update { 
                    it.copy(
                        errorMessage = "Failed to save recommendation"
                    ) 
                }
            }
        }
    }
}
