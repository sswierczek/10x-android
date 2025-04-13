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
    private val database: FirebaseDatabase
) : MovieRepository {

    private val moviesRef = database.getReference("movies")

    override suspend fun addMovieEntry(movieEntry: MovieEntry): MovieEntry {
        val entryWithId = movieEntry.copy(id = moviesRef.push().key ?: throw IllegalStateException("Failed to generate key"))
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
                it.getValue(MovieEntry::class.java) 
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
