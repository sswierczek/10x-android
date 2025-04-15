package com.example.a10xandroid.data.repository

import com.example.a10xandroid.data.model.MovieRecommendation
import kotlinx.coroutines.flow.Flow

/**
 * Data model for a recommended movie
 */
data class RecommendedMovie(
    val tmdbId: String,     // TMDB movie ID
    val reason: String      // Reason for recommendation
)

/**
 * Repository responsible for managing movie recommendations
 */
interface RecommendationsRepository {
    /**
     * Gets a list of pending movie recommendations for the current user
     * @return List of movie recommendations
     */
    suspend fun getRecommendations(): List<MovieRecommendation>
    
    /**
     * Dismisses a recommendation by marking it as dismissed
     * @param recommendationId ID of the recommendation to dismiss
     * @return Whether the operation was successful
     */
    suspend fun dismissRecommendation(recommendationId: String): Boolean
    
    /**
     * Saves a recommendation to the user's watchlist and marks it as saved
     * @param recommendationId ID of the recommendation to save
     * @return Whether the operation was successful
     */
    suspend fun saveToWatchlist(recommendationId: String): Boolean
} 