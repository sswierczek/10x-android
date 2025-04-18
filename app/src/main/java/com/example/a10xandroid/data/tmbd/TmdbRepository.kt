package com.example.a10xandroid.data.tmbd

import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for TMDB API operations.
 */
interface TmdbRepository {
    /**
     * Search for movies by title.
     *
     * @param query The search query
     * @param page The page number for pagination
     * @return Flow of Result containing a list of movies
     */
    suspend fun searchMovies(query: String, page: Int = 1): Flow<List<TmdbMovieApiResult>>

    /**
     * Get detailed information about a movie.
     *
     * @param movieId The TMDB movie ID
     * @return Flow of Result containing movie details
     */
    suspend fun getMovieDetails(movieId: String): Flow<TmdbMovieDetailsApiResponse?>

    /**
     * Get popular movies.
     *
     * @param page The page number for pagination
     * @return Flow of Result containing a list of popular movies
     */
    suspend fun getPopularMovies(page: Int = 1): Flow<List<TmdbMovieApiResult>>

    /**
     * Get upcoming movies.
     *
     * @param page The page number for pagination
     * @return Flow of Result containing a list of upcoming movies
     */
    suspend fun getUpcomingMovies(page: Int = 1): Flow<List<TmdbMovieApiResult>>

    /**
     * Get top rated movies.
     *
     * @param page The page number for pagination
     * @return Flow of Result containing a list of top rated movies
     */
    suspend fun getTopRatedMovies(page: Int = 1): Flow<List<TmdbMovieApiResult>>

    /**
     * Get the full URL for a movie poster.
     *
     * @param posterPath The poster path from the API
     * @param size The size of the poster (e.g., "w500")
     * @return The full URL for the poster, or null if posterPath is null
     */
    fun getPosterUrl(posterPath: String?, size: String): String?

    /**
     * Get the full URL for a movie backdrop.
     *
     * @param backdropPath The backdrop path from the API
     * @param size The size of the backdrop (e.g., "original")
     * @return The full URL for the backdrop, or null if backdropPath is null
     */
    fun getBackdropUrl(backdropPath: String?, size: String): String?
}
