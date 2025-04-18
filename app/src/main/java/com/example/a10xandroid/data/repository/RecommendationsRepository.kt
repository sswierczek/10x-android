package com.example.a10xandroid.data.repository

import com.example.a10xandroid.data.model.MovieRecommendation

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
     * Adds a recommendation to the user's journal and marks it as saved
     * @param recommendationId ID of the recommendation to add
     * @return Whether the operation was successful
     */
    suspend fun addToJournal(recommendationId: String): Boolean
}
