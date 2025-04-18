package com.example.a10xandroid.data.db

import com.example.a10xandroid.data.model.MovieList
import com.example.a10xandroid.data.model.MovieListEntry
import com.example.a10xandroid.data.repository.MovieListRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseMovieListRepository @Inject constructor(
    database: FirebaseDatabase
) : MovieListRepository {

    private val listsRef = database.getReference("lists")
    private val entriesRef = database.getReference("entries")

    override fun getUserLists(userId: String): Flow<List<MovieList>> = callbackFlow {
        val listener = listsRef.orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lists = snapshot.children.mapNotNull { it.getValue(MovieList::class.java) }
                    trySend(lists)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })

        awaitClose { listsRef.removeEventListener(listener) }
    }

    override fun getPublicLists(): Flow<List<MovieList>> = callbackFlow {
        val listener = listsRef.orderByChild("isPublic").equalTo(true)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lists = snapshot.children.mapNotNull { it.getValue(MovieList::class.java) }
                    trySend(lists)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })

        awaitClose { listsRef.removeEventListener(listener) }
    }

    override fun getList(listId: String): Flow<MovieList?> = callbackFlow {
        val listener = listsRef.child(listId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.getValue(MovieList::class.java)
                    trySend(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })

        awaitClose { listsRef.child(listId).removeEventListener(listener) }
    }

    override fun getListEntries(listId: String): Flow<List<MovieListEntry>> = callbackFlow {
        val listener = entriesRef.orderByChild("listId").equalTo(listId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val entries = snapshot.children.mapNotNull {
                        it.getValue(MovieListEntry::class.java)
                    }
                    trySend(entries)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })

        awaitClose { entriesRef.removeEventListener(listener) }
    }

    override suspend fun createList(list: MovieList): Result<String> = try {
        val listId = UUID.randomUUID().toString()
        val listWithId = list.copy(id = listId)
        listsRef.child(listId).setValue(listWithId).await()
        Result.success(listId)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateList(list: MovieList): Result<Unit> = try {
        listsRef.child(list.id).setValue(list).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteList(listId: String): Result<Unit> = try {
        listsRef.child(listId).removeValue().await()
        // Also delete all entries associated with this list
        entriesRef.orderByChild("listId").equalTo(listId)
            .get()
            .await()
            .children
            .forEach { entrySnapshot ->
                entrySnapshot.ref.removeValue().await()
            }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun addMovieToList(entry: MovieListEntry): Result<Unit> = try {
        val entryId = UUID.randomUUID().toString()
        val entryWithId = entry.copy(id = entryId)
        entriesRef.child(entryId).setValue(entryWithId).await()

        // Update movie count in the list
        val listSnapshot = listsRef.child(entry.listId).get().await()
        val currentList = listSnapshot.getValue(MovieList::class.java)
        currentList?.let {
            val updatedList = it.copy(movieCount = it.movieCount + 1)
            listsRef.child(entry.listId).setValue(updatedList).await()
        }

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateMovieEntry(entry: MovieListEntry): Result<Unit> = try {
        entriesRef.child(entry.id).setValue(entry).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun removeMovieFromList(listId: String, movieId: String): Result<Unit> = try {
        // Find and delete the entry
        val entrySnapshot = entriesRef
            .orderByChild("listId")
            .equalTo(listId)
            .get()
            .await()

        entrySnapshot.children.forEach { child ->
            val entry = child.getValue(MovieListEntry::class.java)
            if (entry?.movieId == movieId) {
                child.ref.removeValue().await()

                // Update movie count in the list
                val listSnapshot = listsRef.child(listId).get().await()
                val currentList = listSnapshot.getValue(MovieList::class.java)
                currentList?.let {
                    val updatedList = it.copy(movieCount = it.movieCount - 1)
                    listsRef.child(listId).setValue(updatedList).await()
                }
            }
        }

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
