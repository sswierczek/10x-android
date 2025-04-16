package com.example.a10xandroid.data.model

data class MovieEntry(
    val id: String = "",
    val tmdbId: String = "",
    val userId: String = "",
    val title: String = "",
    val overview: String = "",
    val posterPath: String? = null,
    val backdropPath: String? = null,
    val releaseDate: String? = null,
    val rating: Float = 0f,
    val watchDate: Long = System.currentTimeMillis(),
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
) {
    companion object {
        fun fromMap(map: Map<String, Any?>): MovieEntry {
            return MovieEntry(
                id = map["id"] as? String ?: "",
                tmdbId = map["tmdbId"] as? String ?: "",
                userId = map["userId"] as? String ?: "",
                title = map["title"] as? String ?: "",
                overview = map["overview"] as? String ?: "",
                posterPath = map["posterPath"] as? String,
                backdropPath = map["backdropPath"] as? String,
                releaseDate = map["releaseDate"] as? String,
                rating = (map["rating"] as? Number)?.toFloat() ?: 0f,
                watchDate = (map["watchDate"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                notes = map["notes"] as? String,
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        }
    }
}
