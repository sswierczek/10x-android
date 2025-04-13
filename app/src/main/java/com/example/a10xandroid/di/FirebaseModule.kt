package com.example.a10xandroid.di

import com.example.a10xandroid.data.repository.AuthRepository
import com.example.a10xandroid.data.repository.FirebaseAuthRepository
import com.example.a10xandroid.data.repository.FirebaseMovieRepository
import com.example.a10xandroid.data.repository.MovieRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(firebaseAuthRepository: FirebaseAuthRepository): AuthRepository = firebaseAuthRepository

    @Provides
    @Singleton
    fun provideMovieRepository(firebaseMovieRepository: FirebaseMovieRepository): MovieRepository = firebaseMovieRepository
} 