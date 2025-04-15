package com.example.a10xandroid.di

import com.example.a10xandroid.data.repository.AuthRepository
import com.example.a10xandroid.data.repository.FirebaseAuthRepository
import com.example.a10xandroid.data.repository.FirebaseMovieListRepository
import com.example.a10xandroid.data.repository.FirebaseMovieRepository
import com.example.a10xandroid.data.repository.MovieListRepository
import com.example.a10xandroid.data.repository.MovieRepository
import com.example.a10xandroid.data.repository.RecommendationsRepository
import com.example.a10xandroid.data.repository.TmdbRepository
import com.example.a10xandroid.data.repository.TmdbRepositoryImpl
import com.example.a10xandroid.data.repository.impl.RecommendationsRepositoryImpl
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
    abstract fun bindAuthRepository(
        firebaseAuthRepository: FirebaseAuthRepository
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindMovieRepository(
        firebaseMovieRepository: FirebaseMovieRepository
    ): MovieRepository

    @Binds
    @Singleton
    abstract fun bindMovieListRepository(
        firebaseMovieListRepository: FirebaseMovieListRepository
    ): MovieListRepository

    @Binds
    @Singleton
    abstract fun bindTmdbRepository(
        tmdbRepositoryImpl: TmdbRepositoryImpl
    ): TmdbRepository

    @Binds
    @Singleton
    abstract fun bindRecommendationsRepository(
        recommendationsRepositoryImpl: RecommendationsRepositoryImpl
    ): RecommendationsRepository
}
