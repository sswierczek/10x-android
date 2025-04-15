package com.example.a10xandroid.data.repository

import com.example.a10xandroid.data.model.MovieEntry
import kotlinx.coroutines.flow.Flow

/**
 * Interface for movie entry operations.
 */
interface MovieRepository {
    suspend fun addMovieEntry(movieEntry: MovieEntry): MovieEntry

    suspend fun updateMovieEntry(movieEntry: MovieEntry): MovieEntry

    suspend fun deleteMovieEntry(movieEntryId: String): Boolean

    suspend fun getMovieEntry(movieEntryId: String): MovieEntry?

    suspend fun getMovieEntries(userId: String): List<MovieEntry>

    fun getMovieEntriesFlow(userId: String): Flow<List<MovieEntry>>

    suspend fun searchMovieEntries(userId: String, query: String): List<MovieEntry>
    
    /**
     * Adds a movie to the user's watchlist
     * @param title Movie title
     * @param overview Movie overview/description
     * @param posterPath Relative path to the movie poster image
     * @param backdropPath Relative path to the movie backdrop image
     * @param releaseDate Movie release date in ISO format (yyyy-MM-dd)
     * @return Whether the operation was successful
     */
    suspend fun addMovieToWatchlist(
        title: String,
        overview: String,
        posterPath: String?,
        backdropPath: String?,
        releaseDate: String?
    ): Boolean
}
