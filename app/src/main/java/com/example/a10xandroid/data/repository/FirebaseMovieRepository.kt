package com.example.a10xandroid.data.repository

import android.util.Log
import com.example.a10xandroid.data.model.MovieEntry
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "FirebaseMovieRepository"

@Singleton
class FirebaseMovieRepository @Inject constructor(
    private val database: FirebaseDatabase
) : MovieRepository {

    private val moviesRef = database.getReference("movies")

    override suspend fun addMovieEntry(movieEntry: MovieEntry): MovieEntry {
        Log.d(
            TAG,
            "Starting to add movie entry: ${movieEntry.title} with TMDB ID: ${movieEntry.tmdbId}"
        )
        return try {
            Log.d(TAG, "Generating Firebase key for new movie entry")
            val newMovieRef = moviesRef.push()
            val firebaseId =
                newMovieRef.key ?: throw IllegalStateException("Failed to generate Firebase key")
            Log.d(TAG, "Generated Firebase ID: $firebaseId for TMDB ID: ${movieEntry.tmdbId}")

            Log.d(TAG, "Attempting to write to Firebase at path: movies/$firebaseId")
            val movieWithId = movieEntry.copy(id = firebaseId)

            try {
                newMovieRef.setValue(movieWithId).await()
                Log.d(TAG, "Successfully wrote to Firebase at path: movies/$firebaseId")
            } catch (e: Exception) {
                Log.e(TAG, "Firebase write failed", e)
                when (e) {
                    is IOException -> Log.e(
                        TAG,
                        "Network error while writing to Firebase: ${e.message}"
                    )

                    is FirebaseNetworkException -> Log.e(
                        TAG,
                        "Firebase network error: ${e.message}"
                    )

                    is FirebaseAuthException -> Log.e(TAG, "Firebase auth error: ${e.message}")
                    else -> Log.e(TAG, "Unknown Firebase error: ${e.message}")
                }
                throw e
            }

            Log.d(TAG, "Verifying write by reading back the entry")
            val verificationRef = moviesRef.child(firebaseId)
            val snapshot = verificationRef.get().await()

            if (!snapshot.exists()) {
                Log.e(
                    TAG,
                    "Write verification failed - entry not found at path: movies/$firebaseId"
                )
                throw IllegalStateException("Failed to verify movie entry creation")
            }

            Log.d(TAG, "Write verified successfully")
            movieWithId
        } catch (e: Exception) {
            Log.e(TAG, "Error adding movie entry", e)
            Log.e(TAG, "Error type: ${e.javaClass.simpleName}")
            Log.e(TAG, "Error message: ${e.message}")
            Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
            throw e
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
