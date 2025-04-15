package com.example.a10xandroid.data.model

import java.util.Date

/**
 * Data model representing a movie recommendation for a user
 */
data class MovieRecommendation(
    val id: String = "",
    val movieId: String = "",
    val userId: String = "",
    val title: String = "",
    val overview: String = "",
    val posterPath: String? = null,
    val backdropPath: String? = null,
    val releaseDate: String? = null,
    val rating: Float? = null,
    val reason: String = "",
    val status: RecommendationStatus = RecommendationStatus.PENDING,
    val createdAt: Date = Date()
) {
    companion object {
        /**
         * Creates a MovieRecommendation object from a Firebase data map
         */
        fun fromMap(map: Map<String, Any?>): MovieRecommendation {
            return MovieRecommendation(
                id = map["id"] as? String ?: "",
                movieId = map["movieId"] as? String ?: "",
                userId = map["userId"] as? String ?: "",
                title = map["title"] as? String ?: "",
                overview = map["overview"] as? String ?: "",
                posterPath = map["posterPath"] as? String,
                backdropPath = map["backdropPath"] as? String,
                releaseDate = map["releaseDate"] as? String,
                rating = map["rating"] as? Float,
                reason = map["reason"] as? String ?: "",
                status = RecommendationStatus.valueOf(map["status"] as? String ?: RecommendationStatus.PENDING.toString()),
                createdAt = Date(map["createdAt"] as? Long ?: System.currentTimeMillis())
            )
        }
    }
}

/**
 * Enum representing recommendation status
 */
enum class RecommendationStatus {
    PENDING,     // Waiting for user action
    SAVED,       // Saved to watchlist
    DISMISSED    // Dismissed by user
}

/**
 * Converts object to a map for Firebase storage
 */
fun MovieRecommendation.toMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "movieId" to movieId,
        "userId" to userId,
        "title" to title,
        "overview" to overview,
        "posterPath" to posterPath,
        "backdropPath" to backdropPath,
        "releaseDate" to releaseDate,
        "rating" to rating,
        "reason" to reason,
        "status" to status.toString(),
        "createdAt" to createdAt.time
    )
} 