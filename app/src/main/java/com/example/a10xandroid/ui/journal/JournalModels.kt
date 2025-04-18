package com.example.a10xandroid.ui.journal

import com.example.a10xandroid.ui.common.StateStatus

/**
 * Opcje sortowania listy filmów
 */
enum class SortOrder {
    DATE_ADDED_ASC,
    DATE_ADDED_DESC
}

/**
 * Model widoku filmu dla listy w dzienniku filmowym
 */
data class JournalModelForView(
    val id: String,
    val tmdbId: String,
    val title: String,
    val posterUrl: String?,
    val year: String,
    val genre: String,
    val rating: Int,
    val addedAt: Long,
    val addedAtFormatted: String
)

/**
 * Stan UI dla ekranu dziennika
 */
data class JournalUiState(
    val status: StateStatus = StateStatus.LOADING,
    val errorMessage: String? = null,
    val movies: List<JournalModelForView> = emptyList(),
    val sortOrder: SortOrder = SortOrder.DATE_ADDED_DESC,
    val isRefreshing: Boolean = false
)
