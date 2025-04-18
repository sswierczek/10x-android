package com.example.a10xandroid.data.repository

import com.example.a10xandroid.data.model.MovieList
import com.example.a10xandroid.data.model.MovieListEntry
import kotlinx.coroutines.flow.Flow

interface MovieListRepository {
    fun getUserLists(userId: String): Flow<List<MovieList>>

    fun getPublicLists(): Flow<List<MovieList>>

    fun getList(listId: String): Flow<MovieList?>

    fun getListEntries(listId: String): Flow<List<MovieListEntry>>

    suspend fun createList(list: MovieList): Result<String>

    suspend fun updateList(list: MovieList): Result<Unit>

    suspend fun deleteList(listId: String): Result<Unit>

    suspend fun addMovieToList(entry: MovieListEntry): Result<Unit>

    suspend fun updateMovieEntry(entry: MovieListEntry): Result<Unit>

    suspend fun removeMovieFromList(listId: String, movieId: String): Result<Unit>
}
