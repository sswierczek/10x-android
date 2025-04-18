package com.example.a10xandroid.data.tmbd

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiServiceImpl : TmdbApiService {
    @GET("search/movie")
    override suspend fun searchMovies(
        @Query(value = "api_key") apiKey: String,
        @Query(value = "query") query: String,
        @Query(value = "page") page: Int,
    ): TmdbMovieSearchApiResponse

    @GET("movie/{movie_id}")
    override suspend fun getMovieDetails(
        @Path("movie_id") movieId: String,
        @Query("api_key") apiKey: String
    ): TmdbMovieDetailsApiResponse

    @GET("movie/popular")
    override suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): TmdbMovieSearchApiResponse

    @GET("movie/upcoming")
    override suspend fun getUpcomingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): TmdbMovieSearchApiResponse

    @GET("movie/top_rated")
    override suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): TmdbMovieSearchApiResponse
}
