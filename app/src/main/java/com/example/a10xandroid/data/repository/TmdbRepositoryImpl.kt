package com.example.a10xandroid.data.repository

import com.example.a10xandroid.BuildConfig
import com.example.a10xandroid.data.api.TmdbApiService
import com.example.a10xandroid.data.api.model.TmdbMovieApiResult
import com.example.a10xandroid.data.api.model.TmdbMovieDetailsApiResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log

private const val TAG = "TmdbRepositoryImpl"

/**
 * Implementation of the TMDB repository.
 */
@Singleton
class TmdbRepositoryImpl @Inject constructor(
    private val apiService: TmdbApiService,
) : TmdbRepository {

    private val apiKey: String
        get() = BuildConfig.TMDB_API_KEY

    private val imageBaseUrl: String
        get() = "https://image.tmdb.org/t/p/"

    override suspend fun searchMovies(query: String, page: Int): Flow<List<TmdbMovieApiResult>> =
        flow {
            try {
                val response = apiService.searchMovies(
                    apiKey = apiKey,
                    query = query,
                    page = page
                )
                emit(response.results)
            } catch (e: Exception) {
                emit(emptyList())
            }
        }

    override suspend fun getMovieDetails(movieId: String): Flow<TmdbMovieDetailsApiResponse?> =
        flow {
            Log.d(TAG, "Getting movie details for ID: $movieId")
            val response = apiService.getMovieDetails(
                movieId = movieId,
                apiKey = apiKey
            )
            Log.d(TAG, "Movie details received: ${response?.title}")
            emit(response)
        }.catch { e ->
            Log.e(TAG, "Error getting movie details for ID: $movieId", e)
        }

    override suspend fun getPopularMovies(page: Int): Flow<List<TmdbMovieApiResult>> = flow {
        try {
            val response = apiService.getPopularMovies(
                apiKey = apiKey,
                page = page
            )
            emit(response.results)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun getUpcomingMovies(page: Int): Flow<List<TmdbMovieApiResult>> = flow {
        try {
            val response = apiService.getUpcomingMovies(
                apiKey = apiKey,
                page = page
            )
            emit(response.results)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun getTopRatedMovies(page: Int): Flow<List<TmdbMovieApiResult>> = flow {
        try {
            val response = apiService.getTopRatedMovies(
                apiKey = apiKey,
                page = page
            )
            emit(response.results)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override fun getPosterUrl(posterPath: String?, size: String): String? {
        return posterPath?.let {
            "$imageBaseUrl$size$it"
        }
    }

    override fun getBackdropUrl(backdropPath: String?, size: String): String? {
        return backdropPath?.let {
            "$imageBaseUrl$size$it"
        }
    }
}
