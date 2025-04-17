package com.example.a10xandroid.service

import android.content.Context
import android.util.Log
import com.example.a10xandroid.data.api.OpenRouterApiService
import com.example.a10xandroid.data.api.OpenRouterMessage
import com.example.a10xandroid.data.api.OpenRouterRequest
import com.example.a10xandroid.data.dto.RecommendedMovieDTO
import com.example.a10xandroid.data.model.MovieEntry
import com.example.a10xandroid.data.repository.MovieRepository
import com.example.a10xandroid.data.repository.TmdbRepository
import com.example.a10xandroid.util.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Serwis odpowiedzialny za generowanie rekomendacji filmów dla użytkownika.
 * Wykorzystuje OpenRouter API do generowania rekomendacji na podstawie filmów z dziennika użytkownika.
 */
@Singleton
class RecommendationsService @Inject constructor(
    private val openRouterApiService: OpenRouterApiService,
    private val movieRepository: MovieRepository,
    private val tmdbRepository: TmdbRepository,
    private val json: Json,
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "RecommendationsService"
        private const val MAX_RETRIES = 3
        private const val RETRY_DELAY_MS = 1000L
        private const val CACHE_EXPIRY_MS = 24 * 60 * 60 * 1000 // 24 godziny
    }

    // Cache dla rekomendacji użytkowników
    private val recommendationsCache = ConcurrentHashMap<String, CachedRecommendations>()

    /**
     * Generuje listę rekomendowanych filmów na podstawie filmów z dziennika użytkownika.
     *
     * @param userMovies Lista filmów z dziennika użytkownika
     * @return Flow<Result<List<RecommendedMovieDTO>>> Lista rekomendowanych filmów lub błąd
     */
    suspend fun getRecommendations(userMovies: List<MovieEntry>): Flow<Result<List<RecommendedMovieDTO>>> = flow {
        try {
            if (userMovies.isEmpty()) {
                emit(Result.Success(emptyList()))
                return@flow
            }

            // Generowanie klucza cache na podstawie ID filmów
            val cacheKey = generateCacheKey(userMovies)

            // Sprawdzenie cache
            val cachedRecommendations = recommendationsCache[cacheKey]
            if (cachedRecommendations != null && !isCacheExpired(cachedRecommendations.timestamp)) {
                Log.d(TAG, "Using cached recommendations for key: $cacheKey")
                emit(Result.Success(cachedRecommendations.recommendations))
                return@flow
            }

            // Przygotowanie kontekstu dla modelu AI
            val context = prepareMovieContext(userMovies)

            // Generowanie rekomendacji przez OpenRouter API
            val recommendations = generateRecommendations(context)

            // Zapisanie do cache
            recommendationsCache[cacheKey] = CachedRecommendations(
                recommendations = recommendations,
                timestamp = System.currentTimeMillis()
            )

            emit(Result.Success(recommendations))
        } catch (e: Exception) {
            Log.e(TAG, "Error getting recommendations", e)
            emit(Result.Error(e))
        }
    }

    /**
     * Odświeża listę rekomendacji dla użytkownika.
     *
     * @param userId Identyfikator użytkownika
     * @return Flow<Result<List<RecommendedMovieDTO>>> Zaktualizowana lista rekomendacji lub błąd
     */
    suspend fun refreshRecommendations(userId: String): Flow<Result<List<RecommendedMovieDTO>>> = flow {
        try {
            // Pobranie aktualnej listy filmów użytkownika
            val userMovies = movieRepository.getMovieEntries(userId)

            // Generowanie nowych rekomendacji
            val recommendations = getRecommendations(userMovies)

            emitAll(recommendations)
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing recommendations", e)
            emit(Result.Error(e))
        }
    }

    /**
     * Generuje klucz cache na podstawie ID filmów użytkownika.
     */
    private fun generateCacheKey(userMovies: List<MovieEntry>): String {
        // Sortowanie ID filmów, aby zapewnić spójność klucza
        val sortedIds = userMovies.map { it.tmdbId }.sorted()
        return sortedIds.joinToString("_")
    }

    /**
     * Sprawdza, czy cache wygasł.
     */
    private fun isCacheExpired(timestamp: Long): Boolean {
        return System.currentTimeMillis() - timestamp > CACHE_EXPIRY_MS
    }

    /**
     * Przygotowuje kontekst dla modelu AI na podstawie filmów użytkownika.
     */
    private fun prepareMovieContext(userMovies: List<MovieEntry>): String {
        val movieDescriptions = userMovies.map { movie ->
            """
            Title: ${movie.title}
            Overview: ${movie.overview}
            Rating: ${movie.rating}
            Notes: ${movie.notes ?: "No notes"}
            """.trimIndent()
        }.joinToString("\n\n")

        return """
            Based on the following movies in the user's journal:

            $movieDescriptions

            Please recommend 5 movies that the user might enjoy based on their preferences.
            For each movie, provide:
            1. Title
            2. Brief overview
            3. A specific reason why this movie would appeal to the user based on their journal entries

            Format your response as a JSON object with the following structure:
            {
              "recommendations": [
                {
                  "id": "TMDB_ID",
                  "title": "Movie Title",
                  "overview": "Movie overview",
                  "reason": "Why this movie would appeal to the user"
                }
              ]
            }
        """.trimIndent()
    }

    /**
     * Generuje rekomendacje filmów przy użyciu OpenRouter API.
     *
     * @param context Kontekst filmów użytkownika
     * @return List<RecommendedMovieDTO> Lista rekomendowanych filmów
     */
    private suspend fun generateRecommendations(context: String): List<RecommendedMovieDTO> {
        var retryCount = 0
        var lastException: Exception? = null

        while (retryCount < MAX_RETRIES) {
            try {
                Log.d(TAG, "Generating recommendations, attempt ${retryCount + 1}")

                // Przygotowanie schematu odpowiedzi
                val responseSchema = buildJsonObject {
                    put("type", "object")
                    put("properties", buildJsonObject {
                        put("recommendations", buildJsonObject {
                            put("type", "array")
                            put("items", buildJsonObject {
                                put("type", "object")
                                put("properties", buildJsonObject {
                                    put("id", buildJsonObject {
                                        put("type", "string")
                                        put("description", "TMDB ID filmu")
                                    })
                                    put("title", buildJsonObject {
                                        put("type", "string")
                                        put("description", "Tytuł filmu")
                                    })
                                    put("overview", buildJsonObject {
                                        put("type", "string")
                                        put("description", "Opis filmu")
                                    })
                                    put("reason", buildJsonObject {
                                        put("type", "string")
                                        put("description", "Powód rekomendacji")
                                    })
                                })
                                put("required", JsonArray(listOf("id", "title", "overview", "reason").map { JsonPrimitive(it) }))
                            })
                        })
                    })
                    put("required", JsonArray(listOf("recommendations").map { JsonPrimitive(it) }))
                }

                // Wywołanie OpenRouter API
                val request = OpenRouterRequest(
                    model = "openai/gpt-3.5-turbo",
                    messages = listOf(
                        OpenRouterMessage(
                            role = "user",
                            content = context
                        )
                    ),
                    temperature = 0.7,
                    maxTokens = 1000,
                    responseFormat = responseSchema
                )
                
                val response = openRouterApiService.generateResponse(request)

                // Parsowanie odpowiedzi
                return parseRecommendationsResponse(response)
            } catch (e: IOException) {
                Log.w(TAG, "Network error while generating recommendations", e)
                lastException = e
                retryCount++
                if (retryCount < MAX_RETRIES) {
                    kotlinx.coroutines.delay(RETRY_DELAY_MS * retryCount)
                }
            } catch (e: HttpException) {
                Log.w(TAG, "HTTP error while generating recommendations: ${e.code()}", e)
                lastException = e
                retryCount++
                if (retryCount < MAX_RETRIES) {
                    kotlinx.coroutines.delay(RETRY_DELAY_MS * retryCount)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error while generating recommendations", e)
                throw e
            }
        }

        throw lastException ?: IOException("Failed to generate recommendations after $MAX_RETRIES attempts")
    }

    /**
     * Parsuje odpowiedź z OpenRouter API i konwertuje ją na listę rekomendowanych filmów.
     *
     * @param response Odpowiedź z OpenRouter API
     * @return List<RecommendedMovieDTO> Lista rekomendowanych filmów
     */
    private fun parseRecommendationsResponse(response: String): List<RecommendedMovieDTO> {
        try {
            val jsonResponse = json.parseToJsonElement(response).jsonObject
            val recommendations = jsonResponse["recommendations"]?.jsonObject?.get("items")?.jsonObject
                ?: return emptyList()

            return recommendations.entries.mapNotNull { (_, value) ->
                try {
                    val movieObj = value.jsonObject
                    val id = movieObj["id"]?.jsonPrimitive?.content ?: return@mapNotNull null
                    val title = movieObj["title"]?.jsonPrimitive?.content ?: return@mapNotNull null
                    val overview = movieObj["overview"]?.jsonPrimitive?.content ?: return@mapNotNull null
                    val reason = movieObj["reason"]?.jsonPrimitive?.content ?: return@mapNotNull null

                    RecommendedMovieDTO(
                        id = id,
                        tmdbId = id,
                        title = title,
                        overview = overview,
                        reason = reason
                    )
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to parse recommendation", e)
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse recommendations response", e)
            return emptyList()
        }
    }

    /**
     * Czyści cache rekomendacji.
     */
    fun clearCache() {
        Log.d(TAG, "Clearing recommendations cache")
        recommendationsCache.clear()
    }
}

/**
 * Klasa reprezentująca cache rekomendacji.
 *
 * @property recommendations Lista rekomendowanych filmów
 * @property timestamp Czas utworzenia cache
 */
data class CachedRecommendations(
    val recommendations: List<RecommendedMovieDTO>,
    val timestamp: Long
)
