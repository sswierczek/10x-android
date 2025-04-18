package com.example.a10xandroid.data.openrouter

import android.util.Log
import com.example.a10xandroid.BuildConfig
import com.example.a10xandroid.data.model.MovieEntry
import com.example.a10xandroid.data.tmbd.TmdbRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

private const val TAG = "RecommendationsService"
private const val MAX_RETRIES = 3
private const val RETRY_DELAY_MS = 1000L

class RecommendationsService @Inject constructor(
    private val openRouterApiService: OpenRouterApiService,
    private val tmdbRepository: TmdbRepository
) {
    private val json = Json { ignoreUnknownKeys = true }

    private val systemPrompt = """
        You are a movie recommendation assistant that ONLY returns valid TMDB IDs.
        You MUST verify each movie exists in TMDB before including it.
        You MUST use exact TMDB IDs from themoviedb.org.
        You MUST follow the exact response format specified.

        Your response MUST be a list of TMDB IDs, one per line.
        Example:
        12345
        67890
        24680

        CRITICAL REQUIREMENTS:
        1. ONLY include valid TMDB IDs as strings.
        2. ONLY include movies that you have verified exist in TMDB
        3. DO NOT include movies on the list passed by user.
        4. DO NOT include any text outside the IDs
        5. DO NOT include any explanations or additional information
        6. DO NOT include any movies without valid TMDB IDs
        7. RETURN DIFFERENT MOVIES EVERY TIME.
        8. AVOID extremely common recommendations like Pulp Fiction, Fight Club, The Godfather, Inception, etc.
        9. RECOMMEND MOVIES FROM DIFFERENT GENRES, ERAS, AND STYLES.
        10. INCLUDE SOME LESSER-KNOWN BUT HIGHLY RATED FILMS.
        11. CONSIDER THE USER'S RATINGS TO DETERMINE THEIR PREFERENCES.
    """.trimIndent()

    /**
     * Generates movie recommendations based on the user's movie entries.
     *
     * @param userMovies List of user's movie entries
     * @return List of recommended movies
     */
    suspend fun getRecommendations(userMovies: List<MovieEntry>): List<RecommendedMovieDTO> {
        val context = prepareContext(userMovies)
        return generateRecommendations(context)
    }

    private fun prepareContext(movies: List<MovieEntry>): String {
        val movieList = movies.joinToString("\n") {
            "- ${it.title} (${it.releaseDate?.take(4) ?: "Unknown Year"}) - Rating: ${it.rating}/10"
        }

        return """
            Based on the user's movie history:

            $movieList

            Please recommend 3 different movies that the user might enjoy.
            Consider the user's ratings to determine their preferences.
            Recommend movies from different genres, eras, and styles.
            Include some lesser-known but highly rated films.
            AVOID extremely common recommendations like Pulp Fiction, Fight Club, The Godfather, Inception, etc.
            DO NOT recommend any movies from the user's history.

            For each movie, provide ONLY the TMDB ID as a string.
        """.trimIndent()
    }

    /**
     * Generates movie recommendations using the OpenRouter API.
     *
     * @param context The context of the user's movies
     * @return List of recommended movies
     */
    private suspend fun generateRecommendations(context: String): List<RecommendedMovieDTO> {
        var retryCount = 0
        var lastException: Exception? = null

        while (retryCount < MAX_RETRIES) {
            try {
                Log.d(TAG, "Generating recommendations, attempt ${retryCount + 1}")

                val request = OpenRouterRequest(
                    model = "anthropic/claude-3-opus-20240229",
                    messages = listOf(
                        OpenRouterMessage(
                            role = "system",
                            content = systemPrompt
                        ),
                        OpenRouterMessage(
                            role = "user",
                            content = context
                        )
                    ),
                    temperature = 0.7,
                    maxTokens = 500
                )

                val response = openRouterApiService.createChatCompletion(
                    authorization = "Bearer ${BuildConfig.OPEN_ROUTER_KEY}",
                    httpReferer = "https://github.com/10xdevs/10x-android",
                    xTitle = "10x Android",
                    request = request
                )

                Log.d(TAG, "Received response from OpenRouter API")

                // Extract TMDB IDs from the response
                val movieIds = response.choices.firstOrNull()?.message?.content
                    ?.split("\n")
                    ?.filter { it.isNotBlank() }
                    ?.map { it.trim() }
                    ?: emptyList()

                Log.d(TAG, "Extracted ${movieIds.size} movie IDs from response")

                // Fetch movie details for each ID
                val recommendations = movieIds.mapNotNull { movieId ->
                    try {
                        val movieDetails = tmdbRepository.getMovieDetails(movieId).first()
                        if (movieDetails != null) {
                            RecommendedMovieDTO(
                                id = movieId,
                                tmdbId = movieId,
                                title = movieDetails.title,
                                overview = movieDetails.overview,
                                posterPath = movieDetails.posterPath,
                                backdropPath = movieDetails.backdropPath,
                                releaseDate = movieDetails.releaseDate,
                                voteAverage = movieDetails.voteAverage,
                                reason = "Recommended based on your preferences"
                            )
                        } else {
                            Log.w(TAG, "Movie details not found for ID: $movieId")
                            null
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error fetching movie details for ID: $movieId", e)
                        null
                    }
                }

                if (recommendations.isNotEmpty()) {
                    return recommendations
                } else {
                    Log.w(TAG, "No valid recommendations found in the response")
                    // If this is the last retry, throw an exception
                    if (retryCount == MAX_RETRIES - 1) {
                        throw IOException("No valid recommendations found in the response")
                    }
                    // Otherwise, retry
                    retryCount++
                    delay(RETRY_DELAY_MS * retryCount)
                }
            } catch (e: IOException) {
                Log.w(TAG, "Network error while generating recommendations", e)
                lastException = e
                retryCount++
                if (retryCount < MAX_RETRIES) {
                    delay(RETRY_DELAY_MS * retryCount)
                }
            } catch (e: HttpException) {
                Log.w(TAG, "HTTP error while generating recommendations: ${e.code()}", e)
                lastException = e
                retryCount++
                if (retryCount < MAX_RETRIES) {
                    delay(RETRY_DELAY_MS * retryCount)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error while generating recommendations", e)
                throw e
            }
        }

        throw lastException
            ?: IOException("Failed to generate recommendations after $MAX_RETRIES attempts")
    }

    /**
     * Validates a TMDB ID.
     *
     * @param id The TMDB ID to validate
     * @return True if the ID is valid, false otherwise
     */
    private fun validateTmdbId(id: String): Boolean {
        return try {
            // Check if the ID is a valid integer
            val numericId = id.toInt()
            // TMDB IDs are typically positive integers
            numericId > 0
        } catch (e: NumberFormatException) {
            false
        }
    }
}
