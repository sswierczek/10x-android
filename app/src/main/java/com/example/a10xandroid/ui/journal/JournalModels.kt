package com.example.a10xandroid.ui.journal

import com.example.a10xandroid.ui.common.StateStatus

/**
 * Opcje sortowania listy filmów
 */
enum class SortOrder {
    DATE_ADDED_ASC,    // Sortowanie po dacie dodania (rosnąco)
    DATE_ADDED_DESC    // Sortowanie po dacie dodania (malejąco)
}

/**
 * Model widoku filmu dla listy w dzienniku filmowym
 */
data class JournalMovieViewModel(
    val id: String,                      // Id filmu w bazie danych
    val tmdbId: String,                  // Id filmu w TMDB
    val title: String,                   // Tytuł filmu
    val posterUrl: String?,              // URL plakatu filmu
    val year: String,                    // Rok produkcji
    val genre: String,                   // Główny gatunek filmu
    val addedAt: Long,                   // Timestamp dodania filmu
    val addedAtFormatted: String         // Sformatowana data dodania filmu
)

/**
 * Stan UI dla ekranu dziennika
 */
data class JournalUiState(
    val status: StateStatus = StateStatus.LOADING,  // Stan UI
    val errorMessage: String? = null,               // Komunikat błędu
    val movies: List<JournalMovieViewModel> = emptyList(), // Lista filmów
    val sortOrder: SortOrder = SortOrder.DATE_ADDED_DESC, // Porządek sortowania
    val isRefreshing: Boolean = false               // Czy trwa odświeżanie
) 