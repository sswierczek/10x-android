package com.example.a10xandroid.ui.recommendations

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
        loadRecommendations()
    }

    /**
     * Loads recommendations
     */
    private fun loadRecommendations() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(status = StateStatus.LOADING) }

                val recommendations = recommendationsRepository.getRecommendations()
                val movies = recommendations.map { movie ->
                    RecommendationMovie.fromMovieRecommendation(movie)
                }

                _uiState.update { state ->
                    state.copy(
                        status = StateStatus.SUCCESS,
                        recommendations = movies,
                        isEmpty = movies.isEmpty(),
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
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
        viewModelScope.launch {
            try {
                recommendationsRepository.dismissRecommendation(movieId)
                _uiState.update { state ->
                    val updatedRecommendations = state.recommendations.filterNot { it.id == movieId }
                    state.copy(
                        recommendations = updatedRecommendations,
                        isEmpty = updatedRecommendations.isEmpty(),
                        status = if (updatedRecommendations.isEmpty()) StateStatus.SUCCESS else state.status
                    )
                }
            } catch (e: Exception) {
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
        viewModelScope.launch {
            try {
                recommendationsRepository.saveToWatchlist(movieId)
                _uiState.update { state ->
                    state.copy(
                        recommendations = state.recommendations.map { movie ->
                            if (movie.id == movieId) {
                                movie.copy(saved = true)
                            } else {
                                movie
                            }
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        errorMessage = "Failed to save recommendation"
                    ) 
                }
            }
        }
    }
}
