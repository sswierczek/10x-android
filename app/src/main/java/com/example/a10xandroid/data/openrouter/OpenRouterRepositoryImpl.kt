package com.example.a10xandroid.data.openrouter

import android.util.Log
import com.example.a10xandroid.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class OpenRouterRepositoryImpl @Inject constructor(
    private val apiService: OpenRouterApiService
) : OpenRouterRepository {

    companion object {
        private const val TAG = "OpenRouterRepository"
        private const val APP_NAME = "10x Android"
        private const val APP_URL = "https://github.com/10xdevs/10x-android"
    }

    override suspend fun generateResponse(
        messages: List<OpenRouterMessage>,
        model: String
    ): Flow<String> = flow {
        try {
            Log.d(TAG, "Generating response with model: $model")

            val request = OpenRouterRequest(
                model = model,
                messages = messages
            )

            val response = apiService.createChatCompletion(
                authorization = "Bearer ${BuildConfig.OPEN_ROUTER_KEY}",
                httpReferer = APP_URL,
                xTitle = APP_NAME,
                request = request
            )

            val generatedText = response.choices.firstOrNull()?.message?.content
                ?: throw IllegalStateException("No response generated")

            Log.d(TAG, "Successfully generated response")
            emit(generatedText)

        } catch (e: Exception) {
            Log.e(TAG, "Error generating response", e)
            throw e
        }
    }
}
