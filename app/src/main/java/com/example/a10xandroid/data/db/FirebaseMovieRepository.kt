package com.example.a10xandroid.data.db

import android.util.Log
import com.example.a10xandroid.data.model.MovieEntry
import com.example.a10xandroid.data.repository.MovieRepository
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

private const val TAG = "FirebaseMovieRepository"

@Singleton
class FirebaseMovieRepository @Inject constructor(
    private val database: FirebaseDatabase
) : MovieRepository {

    private val moviesRef = database.getReference("movies")

    override suspend fun addMovieEntry(movieEntry: MovieEntry): Boolean {
        return try {
            Log.d(
                TAG,
                "Starting to add movie entry: ${movieEntry.title} (TMDB ID: ${movieEntry.tmdbId})"
            )

            val key = moviesRef.push().key ?: throw Exception("Failed to generate Firebase key")
            Log.d(TAG, "Generated Firebase key: $key")

            val movieMap = mapOf(
                "id" to key,
                "tmdbId" to movieEntry.tmdbId,
                "userId" to movieEntry.userId,
                "title" to movieEntry.title,
                "overview" to movieEntry.overview,
                "posterPath" to movieEntry.posterPath,
                "backdropPath" to movieEntry.backdropPath,
                "releaseDate" to movieEntry.releaseDate,
                "rating" to movieEntry.rating,
                "watchDate" to movieEntry.watchDate,
                "notes" to movieEntry.notes,
                "createdAt" to movieEntry.createdAt,
                "updatedAt" to movieEntry.updatedAt
            )

            Log.d(TAG, "Attempting to write to Firebase at path: ${moviesRef.child(key).path}")
            moviesRef.child(key).setValue(movieMap).await()

            // Verify the write by reading back the entry
            val snapshot = moviesRef.child(key).get().await()
            val success = snapshot.exists()
            Log.d(TAG, "Write verification: ${if (success) "Success" else "Failed"}")

            success
        } catch (e: Exception) {
            Log.e(TAG, "Error adding movie entry", e)
            false
        }
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
}
