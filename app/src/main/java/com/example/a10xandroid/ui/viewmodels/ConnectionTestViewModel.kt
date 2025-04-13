package com.example.a10xandroid.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ConnectionTestViewModel @Inject constructor(
    private val database: FirebaseDatabase
) : ViewModel() {

    fun checkConnection(callback: (Boolean) -> Unit) {
        database.getReference(".info/connected")
            .addValueEventListener(object : com.google.firebase.database.ValueEventListener {
                override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                    val connected = snapshot.getValue(Boolean::class.java) ?: false
                    callback(connected)
                }

                override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                    callback(false)
                }
            })
    }
} 