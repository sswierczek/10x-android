package com.example.a10xandroid.data.openrouter

import kotlinx.coroutines.flow.Flow

interface OpenRouterRepository {
    suspend fun generateResponse(
        messages: List<OpenRouterMessage>,
        model: String = "openai/gpt-3.5-turbo"
    ): Flow<String>
}
