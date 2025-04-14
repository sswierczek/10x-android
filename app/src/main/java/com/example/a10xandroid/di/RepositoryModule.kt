package com.example.a10xandroid.di

import com.example.a10xandroid.data.repository.FirebaseMovieListRepository
import com.example.a10xandroid.data.repository.MovieListRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindMovieListRepository(
        firebaseMovieListRepository: FirebaseMovieListRepository
    ): MovieListRepository
} 