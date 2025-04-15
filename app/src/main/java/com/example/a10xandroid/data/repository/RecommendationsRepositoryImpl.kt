package com.example.a10xandroid.data.repository

import com.example.a10xandroid.data.model.MovieRecommendation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecommendationsRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) : RecommendationsRepository {

    override suspend fun getRecommendations(): List<MovieRecommendation> {
        val userId = auth.currentUser?.uid ?: return emptyList()

        return try {
            val recommendationsRef = database.reference
                .child("recommendations")
                .child(userId)
                .get()
                .await()

            val recommendations = mutableListOf<MovieRecommendation>()

            for (snapshot in recommendationsRef.children) {
                snapshot.getValue(MovieRecommendation::class.java)?.let {
                    recommendations.add(it)
                }
            }

            recommendations
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun dismissRecommendation(recommendationId: String): Boolean {
        val userId = auth.currentUser?.uid ?: return false

        return try {
            database.reference
                .child("recommendations")
                .child(userId)
                .child(recommendationId)
                .child("dismissed")
                .setValue(true)
                .await()

            database.reference
                .child("dismissed_recommendations")
                .child(userId)
                .child(recommendationId)
                .setValue(true)
                .await()

            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun saveToWatchlist(recommendationId: String): Boolean {
        val userId = auth.currentUser?.uid ?: return false

        return try {
            // Pobierz dane rekomendacji
            val recommendationSnapshot = database.reference
                .child("recommendations")
                .child(userId)
                .child(recommendationId)
                .get()
                .await()

            val recommendation =
                recommendationSnapshot.getValue(MovieRecommendation::class.java) ?: return false

            // Zapisz do watchlisty
            database.reference
                .child("watchlist")
                .child(userId)
                .child(recommendationId)
                .setValue(recommendation)
                .await()

            // Zaznacz jako zapisane w rekomendacjach
            database.reference
                .child("recommendations")
                .child(userId)
                .child(recommendationId)
                .child("savedToWatchlist")
                .setValue(true)
                .await()

            true
        } catch (e: Exception) {
            false
        }
    }
}
