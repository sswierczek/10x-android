package com.example.a10xandroid.data.repository

import com.example.a10xandroid.data.model.MovieEntry
import kotlinx.coroutines.flow.Flow

/**
 * Interface for movie entry operations.
 */
interface MovieRepository {
    suspend fun addMovieEntry(movieEntry: MovieEntry): Boolean

    suspend fun updateMovieEntry(movieEntry: MovieEntry): MovieEntry

    suspend fun deleteMovieEntry(movieEntryId: String): Boolean

    suspend fun getMovieEntry(movieEntryId: String): MovieEntry?

    suspend fun getMovieEntries(userId: String): List<MovieEntry>

    fun getMovieEntriesFlow(userId: String): Flow<List<MovieEntry>>

    suspend fun searchMovieEntries(userId: String, query: String): List<MovieEntry>
}
