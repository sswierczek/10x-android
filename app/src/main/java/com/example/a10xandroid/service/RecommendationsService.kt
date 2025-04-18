package com.example.a10xandroid.service

import android.util.Log
import com.example.a10xandroid.BuildConfig
import com.example.a10xandroid.data.api.OpenRouterApiService
import com.example.a10xandroid.data.api.OpenRouterMessage
import com.example.a10xandroid.data.api.OpenRouterRequest
import com.example.a10xandroid.data.dto.RecommendedMovieDTO
import com.example.a10xandroid.data.model.MovieEntry
import com.example.a10xandroid.data.repository.TmdbRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
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

    /**
     * Generates movie recommendations based on the user's movie entries.
     *
     * @param userMovies List of user's movie entries
     * @return List of recommended movies
     */
    suspend fun getRecommendations(userMovies: List<MovieEntry>): List<RecommendedMovieDTO> {
        if (userMovies.isEmpty()) {
            Log.w(TAG, "No user movies provided for recommendations")
            return emptyList()
        }

        val context = prepareMovieContext(userMovies)
        return generateRecommendations(context)
    }

    private fun prepareMovieContext(userMovies: List<MovieEntry>): String {
        val movieList = userMovies.joinToString("\n") { entry ->
            "- ${entry.title} (${entry.rating}/10): ${entry.notes}"
        }

        return if (movieList.isNotEmpty()) {
            """
            Based on the following movies the user has watched and rated:

            $movieList

            Please recommend 3 different movies that the user might enjoy.
            DO NOT recommend the same TMDB ID as all listed above.
            Return ONLY a JSON object with the following format:
            {"movieIds": ["TMDB_ID1", "TMDB_ID2", "TMDB_ID3"]}

            Make sure to use valid TMDB IDs. Do not include any additional text or explanation.
            """
        } else {
            """
            The user has not rated any movies yet.

            Please recommend 3 popular movies that most people enjoy.
            Return ONLY a JSON object with the following format:
            {"movieIds": ["TMDB_ID1", "TMDB_ID2", "TMDB_ID3"]}

            Make sure to use valid TMDB IDs. Do not include any additional text or explanation.
            """
        }
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

                // Prepare the request according to OpenRouter's chat completion format
                val request = OpenRouterRequest(
                    model = "openai/gpt-4",
                    messages = listOf(
                        OpenRouterMessage(
                            role = "system",
                            content = """
                                You are a movie recommendation assistant that ONLY returns valid JSON objects.
                                You MUST verify each movie exists in TMDB before including it.
                                You MUST use exact TMDB IDs from themoviedb.org.
                                You MUST follow the exact response format specified.

                                Your response MUST be a valid JSON object with EXACTLY this structure:
                                {
                                  "movieIds": ["TMDB_ID1", "TMDB_ID2", "TMDB_ID3"]
                                }

                                CRITICAL REQUIREMENTS:
                                1. ONLY include valid TMDB IDs as strings (e.g., "550" for Fight Club)
                                2. ONLY include movies that you have verified exist in TMDB
                                3. DO NOT include any text outside the JSON structure
                                4. DO NOT include any explanations or additional information
                                5. DO NOT include any movies without valid TMDB IDs
                                6. The response must be a single, valid JSON object
                            """.trimIndent()
                        ),
                        OpenRouterMessage(
                            role = "user",
                            content = context
                        )
                    ),
                    temperature = 0.3, // Lower temperature for more consistent output
                    maxTokens = 2000
                )

                Log.d(TAG, "Sending request to OpenRouter API")
                val response = openRouterApiService.createChatCompletion(
                    authorization = "Bearer ${BuildConfig.OPEN_ROUTER_KEY}",
                    httpReferer = "https://github.com/10xdevs/10x-android",
                    xTitle = "10x Android App",
                    request = request
                )

                Log.d(TAG, "Received response from OpenRouter API")

                // Get the content from the first choice's message
                val content = response.choices.firstOrNull()?.message?.content
                    ?: throw IOException("Empty response from OpenRouter API")

                Log.d(TAG, "Response content: ${content.take(500)}...")

                // Parse the content to get movie IDs
                val movieIds = extractMovieIdsFromContent(content)

                // Check if we got any valid movie IDs
                if (movieIds.isEmpty()) {
                    Log.w(TAG, "No valid movie IDs found in the response")
                    // If this is the last retry, throw an exception
                    if (retryCount == MAX_RETRIES - 1) {
                        throw IOException("No valid movie IDs found in the response")
                    }
                    // Otherwise, retry
                    retryCount++
                    delay(RETRY_DELAY_MS * retryCount)
                    continue
                }

                // Fetch movie details from TMDB API
                return fetchMovieDetails(movieIds)
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
     * Extracts movie IDs from the content of the OpenRouter API response.
     *
     * @param content The content from the OpenRouter API response
     * @return List of movie IDs
     */
    private fun extractMovieIdsFromContent(content: String): List<String> {
        try {
            Log.d(TAG, "Extracting movie IDs from content")

            // Parse the content as JSON to get the movie IDs
            val movieIdsJson = json.parseToJsonElement(content).jsonObject

            // Get the movie IDs array
            val movieIdsArray = movieIdsJson["movieIds"]?.jsonArray
                ?: return emptyList()

            Log.d(TAG, "Found ${movieIdsArray.size} movie IDs")

            val validMovieIds = mutableListOf<String>()

            for (item in movieIdsArray) {
                try {
                    val movieId = item.jsonPrimitive.content

                    // Validate TMDB ID
                    if (!validateTmdbId(movieId)) {
                        Log.w(TAG, "Invalid TMDB ID: $movieId")
                        continue
                    }

                    validMovieIds.add(movieId)
                    Log.d(TAG, "Successfully parsed movie ID: $movieId")
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to parse movie ID", e)
                }
            }

            return validMovieIds
        } catch (e: Exception) {
            Log.e(TAG, "Failed to extract movie IDs from content", e)
            return emptyList()
        }
    }

    /**
     * Fetches movie details from the TMDB API for the given movie IDs.
     *
     * @param movieIds List of movie IDs
     * @return List of recommended movies
     */
    private suspend fun fetchMovieDetails(movieIds: List<String>): List<RecommendedMovieDTO> {
        val recommendations = mutableListOf<RecommendedMovieDTO>()

        for (movieId in movieIds) {
            try {
                Log.d(TAG, "Fetching movie details for ID: $movieId")
                val movieDetails = tmdbRepository.getMovieDetails(movieId).first()

                if (movieDetails != null) {
                    recommendations.add(
                        RecommendedMovieDTO(
                            id = movieId,
                            tmdbId = movieId,
                            title = movieDetails.title,
                            overview = movieDetails.overview,
                            reason = "Recommended based on your preferences"
                        )
                    )
                    Log.d(
                        TAG,
                        "Successfully fetched movie details: ${movieDetails.title} (ID: $movieId)"
                    )
                } else {
                    Log.w(TAG, "Failed to fetch movie details for ID: $movieId (null response)")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error fetching movie details for ID: $movieId", e)
            }
        }

        return recommendations
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
