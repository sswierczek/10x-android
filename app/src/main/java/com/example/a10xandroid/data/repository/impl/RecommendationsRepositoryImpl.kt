package com.example.a10xandroid.data.repository.impl

import android.util.Log
import com.example.a10xandroid.data.auth.AuthRepository
import com.example.a10xandroid.data.auth.getCurrentUserId
import com.example.a10xandroid.data.model.MovieEntry
import com.example.a10xandroid.data.model.MovieRecommendation
import com.example.a10xandroid.data.model.RecommendationStatus
import com.example.a10xandroid.data.model.toMap
import com.example.a10xandroid.data.repository.MovieRepository
import com.example.a10xandroid.data.repository.RecommendationsRepository
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "RecommendationsRepositoryImpl"

@Singleton
class RecommendationsRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase,
    private val authRepository: AuthRepository,
    private val movieRepository: MovieRepository
) : RecommendationsRepository {

    private val recommendationsRef = database.getReference("recommendations")

    override suspend fun getRecommendations(): List<MovieRecommendation> {
        val userId = authRepository.getCurrentUserId() ?: return emptyList()
        return try {
            recommendationsRef
                .orderByChild("userId")
                .equalTo(userId)
                .get()
                .await()
                .children
                .mapNotNull { snapshot ->
                    val data = snapshot.getValue<Map<String, Any?>>()
                    if (data != null) {
                        // Ensure the Firebase document ID is properly set
                        val recommendationWithId = MovieRecommendation.fromMap(data).copy(
                            id = snapshot.key ?: return@mapNotNull null
                        )
                        Log.d(
                            TAG,
                            "Retrieved recommendation - Firebase ID: ${recommendationWithId.id}, TMDB ID: ${recommendationWithId.movieId}"
                        )
                        recommendationWithId
                    } else {
                        null
                    }
                }
                .filter { it.status == RecommendationStatus.PENDING }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting recommendations", e)
            emptyList()
        }
    }

    override suspend fun dismissRecommendation(recommendationId: String): Boolean {
        return try {
            val userId = authRepository.getCurrentUserId() ?: return false

            // Get the recommendation
            val snapshot = recommendationsRef.child(recommendationId).get().await()
            val recommendationData =
                snapshot.getValue<Map<String, Any?>>() ?: return false

            // Check if the recommendation belongs to the current user
            if (recommendationData["userId"] != userId) {
                return false
            }

            // Update recommendation status
            val updatedRecommendation = MovieRecommendation.fromMap(recommendationData).copy(
                id = recommendationId,
                status = RecommendationStatus.DISMISSED
            )

            // Save changes
            recommendationsRef.child(recommendationId).updateChildren(updatedRecommendation.toMap())
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error dismissing recommendation", e)
            false
        }
    }

    override suspend fun addToJournal(recommendationId: String): Boolean {
        return try {
            val userId = authRepository.getCurrentUserId() ?: return false

            // Get the recommendation
            val snapshot = recommendationsRef.child(recommendationId).get().await()
            val recommendationData =
                snapshot.getValue<Map<String, Any?>>() ?: return false

            // Check if the recommendation belongs to the current user
            if (recommendationData["userId"] != userId) {
                return false
            }

            val recommendation = MovieRecommendation.fromMap(recommendationData).copy(
                id = recommendationId
            )

            Log.d(
                TAG,
                "Adding recommendation to journal - Firebase ID: ${recommendation.id}, TMDB ID: ${recommendation.movieId}"
            )

            // Create movie entry
            val movieEntry = MovieEntry(
                tmdbId = recommendation.movieId,
                userId = userId,
                title = recommendation.title,
                overview = recommendation.overview,
                posterPath = recommendation.posterPath,
                backdropPath = recommendation.backdropPath,
                releaseDate = recommendation.releaseDate,
                watchDate = System.currentTimeMillis(),
                notes = "",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            // Add to journal
            movieRepository.addMovieEntry(movieEntry)

            // Update recommendation status
            val updatedRecommendation = recommendation.copy(
                status = RecommendationStatus.SAVED
            )

            // Save changes
            recommendationsRef.child(recommendationId)
                .updateChildren(updatedRecommendation.toMap()).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error adding recommendation to journal", e)
            false
        }
    }
}
