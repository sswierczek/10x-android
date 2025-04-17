package com.example.a10xandroid.di

import com.example.a10xandroid.data.api.OpenRouterApiService
import com.example.a10xandroid.data.repository.OpenRouterRepository
import com.example.a10xandroid.data.repository.OpenRouterRepositoryImpl
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OpenRouterModule {

    @Provides
    @Singleton
    fun provideOpenRouterApiService(
        okHttpClient: OkHttpClient,
        json: Json
    ): OpenRouterApiService {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("https://openrouter.ai/api/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(OpenRouterApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOpenRouterRepository(apiService: OpenRouterApiService): OpenRouterRepository {
        return OpenRouterRepositoryImpl(apiService)
    }
}
