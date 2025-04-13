package com.example.a10xandroid.data.repository

import com.example.a10xandroid.data.model.MovieEntry
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseMovieRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : MovieRepository {

    private val movieCollection = firestore.collection("movies")

    override suspend fun addMovieEntry(movieEntry: MovieEntry): MovieEntry {
        val entryWithId = movieEntry.copy(id = movieCollection.document().id)
        movieCollection
            .document(entryWithId.id)
            .set(entryWithId)
            .await()
        return entryWithId
    }

    override suspend fun updateMovieEntry(movieEntry: MovieEntry): MovieEntry {
        val updatedEntry = movieEntry.copy(updatedAt = System.currentTimeMillis())
        movieCollection
            .document(updatedEntry.id)
            .set(updatedEntry)
            .await()
        return updatedEntry
    }

    override suspend fun deleteMovieEntry(movieEntryId: String): Boolean {
        return try {
            movieCollection
                .document(movieEntryId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getMovieEntry(movieEntryId: String): MovieEntry? {
        return try {
            movieCollection
                .document(movieEntryId)
                .get()
                .await()
                .toObject(MovieEntry::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getMovieEntries(userId: String): List<MovieEntry> {
        return movieCollection
            .whereEqualTo("userId", userId)
            .orderBy("watchDate", Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(MovieEntry::class.java)
    }

    override fun getMovieEntriesFlow(userId: String): Flow<List<MovieEntry>> = callbackFlow {
        val subscription = movieCollection
            .whereEqualTo("userId", userId)
            .orderBy("watchDate", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val entries = snapshot.toObjects(MovieEntry::class.java)
                    trySend(entries)
                }
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun searchMovieEntries(userId: String, query: String): List<MovieEntry> {
        return movieCollection
            .whereEqualTo("userId", userId)
            .orderBy("title")
            .startAt(query)
            .endAt(query + '\uf8ff')
            .get()
            .await()
            .toObjects(MovieEntry::class.java)
    }
}
