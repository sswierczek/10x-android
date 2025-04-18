package com.example.a10xandroid.di

import com.example.a10xandroid.data.auth.AuthRepository
import com.example.a10xandroid.data.db.FirebaseAuthRepository
import com.example.a10xandroid.data.db.FirebaseMovieListRepository
import com.example.a10xandroid.data.db.FirebaseMovieRepository
import com.example.a10xandroid.data.openrouter.RecommendationsRepositoryImpl
import com.example.a10xandroid.data.repository.MovieListRepository
import com.example.a10xandroid.data.repository.MovieRepository
import com.example.a10xandroid.data.repository.RecommendationsRepository
import com.example.a10xandroid.data.tmbd.TmdbRepository
import com.example.a10xandroid.data.tmbd.TmdbRepositoryImpl
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
