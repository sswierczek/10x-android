package com.example.a10xandroid.di

import com.example.a10xandroid.data.repository.AuthRepository
import com.example.a10xandroid.data.repository.FirebaseAuthRepository
import com.example.a10xandroid.data.repository.FirebaseMovieRepository
import com.example.a10xandroid.data.repository.MovieRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


private const val URL = "https://moviemind-548bd-default-rtdb.europe-west1.firebasedatabase.app"

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        val database = FirebaseDatabase.getInstance(URL)
        database.setPersistenceEnabled(true)
        return database
    }

    @Provides
    @Singleton
    fun provideAuthRepository(firebaseAuthRepository: FirebaseAuthRepository): AuthRepository =
        firebaseAuthRepository

    @Provides
    @Singleton
    fun provideMovieRepository(firebaseMovieRepository: FirebaseMovieRepository): MovieRepository =
        firebaseMovieRepository
}
