package com.example.a10xandroid.data.repository.impl

import com.example.a10xandroid.data.model.MovieRecommendation
import com.example.a10xandroid.data.model.RecommendationStatus
import com.example.a10xandroid.data.model.toMap
import com.example.a10xandroid.data.repository.AuthRepository
import com.example.a10xandroid.data.repository.MovieRepository
import com.example.a10xandroid.data.repository.RecommendationsRepository
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecommendationsRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase,
    private val authRepository: AuthRepository,
    private val movieRepository: MovieRepository
) : RecommendationsRepository {

    private val recommendationsRef get() = database.getReference("recommendations")
    
    override suspend fun getRecommendations(): List<MovieRecommendation> {
        val userId = authRepository.getCurrentUserId() ?: return emptyList()
        
        // Get recommendations assigned to the current user with PENDING status
        val snapshot = recommendationsRef
            .orderByChild("userId")
            .equalTo(userId)
            .get()
            .await()
            
        val recommendations = mutableListOf<MovieRecommendation>()
        
        for (childSnapshot in snapshot.children) {
            val recommendation = childSnapshot.getValue(Map::class.java) as? Map<String, Any?>
            recommendation?.let {
                val movieRec = MovieRecommendation.fromMap(it)
                // Include only active recommendations
                if (movieRec.status == RecommendationStatus.PENDING) {
                    recommendations.add(movieRec)
                }
            }
        }
        
        return recommendations
    }
    
    override suspend fun dismissRecommendation(recommendationId: String): Boolean {
        return try {
            val userId = authRepository.getCurrentUserId() ?: return false
            
            // Get the recommendation
            val snapshot = recommendationsRef.child(recommendationId).get().await()
            val recommendationData = snapshot.getValue(Map::class.java) as? Map<String, Any?> ?: return false
            
            // Check if the recommendation belongs to the current user
            if (recommendationData["userId"] != userId) {
                return false
            }
            
            // Update recommendation status
            val updatedRecommendation = MovieRecommendation.fromMap(recommendationData).copy(
                status = RecommendationStatus.DISMISSED
            )
            
            // Save changes
            recommendationsRef.child(recommendationId).updateChildren(updatedRecommendation.toMap()).await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun saveToWatchlist(recommendationId: String): Boolean {
        return try {
            val userId = authRepository.getCurrentUserId() ?: return false
            
            // Get the recommendation
            val snapshot = recommendationsRef.child(recommendationId).get().await()
            val recommendationData = snapshot.getValue(Map::class.java) as? Map<String, Any?> ?: return false
            
            // Check if the recommendation belongs to the current user
            if (recommendationData["userId"] != userId) {
                return false
            }
            
            val recommendation = MovieRecommendation.fromMap(recommendationData)
            
            // Add movie to watchlist
            val savedToWatchlist = movieRepository.addMovieToWatchlist(
                title = recommendation.title,
                overview = recommendation.overview,
                posterPath = recommendation.posterPath,
                backdropPath = recommendation.backdropPath,
                releaseDate = recommendation.releaseDate
            )
            
            if (savedToWatchlist) {
                // Update recommendation status
                val updatedRecommendation = recommendation.copy(
                    status = RecommendationStatus.SAVED
                )
                
                // Save changes
                recommendationsRef.child(recommendationId).updateChildren(updatedRecommendation.toMap()).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
} 