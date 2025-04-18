package com.example.a10xandroid.data.openrouter

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenRouterApiService {
    @POST("v1/chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") authorization: String,
        @Header("HTTP-Referer") httpReferer: String,
        @Header("X-Title") xTitle: String,
        @Body request: OpenRouterRequest
    ): OpenRouterResponse

    @POST("v1/chat/completions")
    suspend fun generateResponse(
        @Header("Authorization") authorization: String,
        @Header("HTTP-Referer") httpReferer: String,
        @Header("X-Title") xTitle: String,
        @Body request: OpenRouterRequest
    ): String
}

@Serializable
data class OpenRouterRequest(
    val model: String,
    val messages: List<OpenRouterMessage>,
    val temperature: Double = 0.7,
    @SerialName("max_tokens")
    val maxTokens: Int = 1000,
    val responseFormat: JsonElement? = null
)

@Serializable
data class OpenRouterMessage(
    val role: String,
    val content: String
)

@Serializable
data class OpenRouterResponse(
    val id: String,
    val model: String,
    val created: Long,
    val choices: List<OpenRouterChoice>
)

@Serializable
data class OpenRouterChoice(
    val message: OpenRouterMessage,
    @SerialName("finish_reason")
    val finishReason: String
)
