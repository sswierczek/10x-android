package com.example.a10xandroid.data.tmbd

import com.example.a10xandroid.BuildConfig
import com.example.a10xandroid.data.tmbd.model.TmdbMovieDetailsApiResponse
import com.example.a10xandroid.data.tmbd.model.TmdbMovieSearchApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit service interface for TMDB API.
 * This interface defines all the API endpoints we'll use to interact with TMDB.
 */
interface TmdbApiService {

    /**
     * Search for movies by title.
     *
     * @param apiKey The API key
     * @param query The search query
     * @param page The page number (default: 1)
     * @return Search response containing movie results
     */
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
    ): TmdbMovieSearchApiResponse

    /**
     * Get movie details by ID.
     *
     * @param movieId The TMDB movie ID
     * @param apiKey The API key
     * @return Movie details
     */
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: String,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): TmdbMovieDetailsApiResponse

    /**
     * Get popular movies.
     *
     * @param apiKey The API key
     * @param page The page number (default: 1)
     * @return Popular movies response
     */
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY,
        @Query("page") page: Int = 1
    ): TmdbMovieSearchApiResponse

    /**
     * Get upcoming movies.
     *
     * @param apiKey The API key
     * @param page The page number (default: 1)
     * @return Upcoming movies response
     */
    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY,
        @Query("page") page: Int = 1
    ): TmdbMovieSearchApiResponse

    /**
     * Get top rated movies.
     *
     * @param apiKey The API key
     * @param page The page number (default: 1)
     * @return Top rated movies response
     */
    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY,
        @Query("page") page: Int = 1
    ): TmdbMovieSearchApiResponse
}
