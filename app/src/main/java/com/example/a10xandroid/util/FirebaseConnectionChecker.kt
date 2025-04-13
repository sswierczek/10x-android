package com.example.a10xandroid.util

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseConnectionChecker @Inject constructor(
    private val database: FirebaseDatabase
) {
    suspend fun checkConnection(): Boolean {
        return try {
            // Try to get the connection state from Realtime Database
            database.getReference(".info/connected")
                .get()
                .await()
                .getValue(Boolean::class.java) ?: false
        } catch (e: Exception) {
            false
        }
    }
} 