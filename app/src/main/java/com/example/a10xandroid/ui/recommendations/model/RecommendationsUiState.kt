package com.example.a10xandroid.ui.recommendations.model

import com.example.a10xandroid.ui.common.StateStatus

/**
 * UI state for recommendations screen
 */
data class RecommendationsUiState(
    val status: StateStatus = StateStatus.LOADING,
    val recommendations: List<RecommendationMovie> = emptyList(),
    val errorMessage: String? = null,
    val isEmpty: Boolean = true
)
