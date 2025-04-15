package com.example.a10xandroid.data.repository

import com.example.a10xandroid.data.model.MovieEntry
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseMovieRepository @Inject constructor(
    private val database: FirebaseDatabase,
    private val authRepository: AuthRepository
) : MovieRepository {

    private val moviesRef = database.getReference("movies")
    private val watchlistRef = database.getReference("watchlist")

    override suspend fun addMovieEntry(movieEntry: MovieEntry): MovieEntry {
        val entryWithId = movieEntry.copy(
            id = moviesRef.push().key ?: throw IllegalStateException("Failed to generate key")
        )
        moviesRef.child(entryWithId.id).setValue(entryWithId).await()
        return entryWithId
    }

    override suspend fun updateMovieEntry(movieEntry: MovieEntry): MovieEntry {
        val updatedEntry = movieEntry.copy(updatedAt = System.currentTimeMillis())
        moviesRef.child(updatedEntry.id).setValue(updatedEntry).await()
        return updatedEntry
    }

    override suspend fun deleteMovieEntry(movieEntryId: String): Boolean {
        return try {
            moviesRef.child(movieEntryId).removeValue().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getMovieEntry(movieEntryId: String): MovieEntry? {
        return try {
            moviesRef.child(movieEntryId).get().await().getValue(MovieEntry::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getMovieEntries(userId: String): List<MovieEntry> {
        return try {
            moviesRef.orderByChild("userId").equalTo(userId).get().await().children.mapNotNull {
                it.getValue(
                    MovieEntry::class.java
                )
            }.sortedByDescending { it.watchDate }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun getMovieEntriesFlow(userId: String): Flow<List<MovieEntry>> = callbackFlow {
        val listener = moviesRef.orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val entries = snapshot.children.mapNotNull {
                        it.getValue(MovieEntry::class.java)
                    }.sortedByDescending { it.watchDate }
                    trySend(entries)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error if needed
                }
            })
        awaitClose { moviesRef.removeEventListener(listener) }
    }

    override suspend fun searchMovieEntries(userId: String, query: String): List<MovieEntry> {
        return try {
            moviesRef.orderByChild("userId").equalTo(userId).get().await().children
                .mapNotNull { it.getValue(MovieEntry::class.java) }
                .filter { it.title.contains(query, ignoreCase = true) }
                .sortedByDescending { it.watchDate }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun addMovieToWatchlist(
        title: String,
        overview: String,
        posterPath: String?,
        backdropPath: String?,
        releaseDate: String?
    ): Boolean {
        return try {
            // Create a watchlist entry using necessary fields from the movie
            val entryId =
                watchlistRef.push().key ?: throw IllegalStateException("Failed to generate key")
            val userId = authRepository.getCurrentUserId() ?: return false

            val currentTime = System.currentTimeMillis()
            val watchlistEntry = MovieEntry(
                id = entryId,
                userId = userId,
                title = title,
                overview = overview,
                posterPath = posterPath,
                backdropPath = backdropPath,
                releaseDate = releaseDate,
                createdAt = currentTime,
                updatedAt = currentTime
            )

            // Save to watchlist collection
            watchlistRef.child(userId).child(entryId).setValue(watchlistEntry).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
