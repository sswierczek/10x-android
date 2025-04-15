package com.example.a10xandroid.ui.recommendations

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a10xandroid.data.model.MovieRecommendation
import com.example.a10xandroid.data.repository.AuthRepository
import com.example.a10xandroid.data.repository.RecommendationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "RecommendationsViewModel"

/**
 * Status of the UI state
 */
enum class StateStatus {
    LOADING,
    SUCCESS,
    ERROR
}

/**
 * View model for movie recommendation item
 */
data class MovieViewModel(
    val id: String,
    val title: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val overview: String,
    val year: String,
    val rating: Float,
    val reason: String,
    val saved: Boolean = false
)

/**
 * UI state for recommendations screen
 */
data class RecommendationsUiState(
    val recommendations: List<MovieViewModel> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoggedIn: Boolean = false,
    val status: StateStatus = StateStatus.LOADING,
    val errorMessage: String? = null
)

/**
 * ViewModel for managing movie recommendations screen
 */
@HiltViewModel
class RecommendationsViewModel @Inject constructor(
    private val recommendationsRepository: RecommendationsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecommendationsUiState(isLoading = true))
    val uiState: StateFlow<RecommendationsUiState> = _uiState.asStateFlow()

    init {
        checkAuthStatus()
        loadRecommendations()
    }

    /**
     * Checks if the user is logged in
     */
    private fun checkAuthStatus() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            _uiState.update { it.copy(isLoggedIn = currentUser != null) }
        }
    }

    /**
     * Loads movie recommendations
     */
    fun loadRecommendations() {
        if (!_uiState.value.isLoggedIn) {
            _uiState.update { it.copy(status = StateStatus.ERROR, errorMessage = "User not logged in") }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(status = StateStatus.LOADING, errorMessage = null) }
                
                val recommendations = recommendationsRepository.getRecommendations()
                
                // Map to view model
                val recommendationViewModels = recommendations.map { recommendation ->
                    MovieViewModel(
                        id = recommendation.id,
                        title = recommendation.title,
                        posterUrl = recommendation.posterPath,
                        backdropUrl = recommendation.backdropPath,
                        overview = recommendation.overview,
                        year = recommendation.releaseDate?.take(4) ?: "",
                        rating = recommendation.rating ?: 0f,
                        reason = recommendation.reason
                    )
                }
                
                _uiState.update { 
                    it.copy(
                        status = StateStatus.SUCCESS,
                        recommendations = recommendationViewModels,
                        isRefreshing = false
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading recommendations", e)
                _uiState.update { 
                    it.copy(
                        status = StateStatus.ERROR,
                        errorMessage = "Failed to load recommendations: ${e.localizedMessage}",
                        isRefreshing = false
                    )
                }
            }
        }
    }

    /**
     * Refreshes the recommendations list
     */
    fun refreshRecommendations() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadRecommendations()
    }
    
    /**
     * Clears the error message
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Dismisses a recommendation
     */
    fun dismissRecommendation(recommendationId: String) {
        viewModelScope.launch {
            try {
                val success = recommendationsRepository.dismissRecommendation(recommendationId)
                if (success) {
                    _uiState.update { currentState ->
                        val updatedList = currentState.recommendations.filter { it.id != recommendationId }
                        currentState.copy(recommendations = updatedList)
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        errorMessage = "Failed to dismiss recommendation: ${e.message}"
                    ) 
                }
            }
        }
    }

    /**
     * Saves a recommendation to watchlist
     */
    fun saveToWatchlist(recommendationId: String) {
        viewModelScope.launch {
            try {
                val success = recommendationsRepository.saveToWatchlist(recommendationId)
                if (success) {
                    _uiState.update { currentState ->
                        val updatedList = currentState.recommendations.map { 
                            if (it.id == recommendationId) it.copy(saved = true) else it
                        }
                        currentState.copy(recommendations = updatedList)
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        errorMessage = "Failed to save movie to watchlist: ${e.message}"
                    ) 
                }
            }
        }
    }
} 