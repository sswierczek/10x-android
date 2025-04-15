package com.example.a10xandroid.ui.recommendations

/**
 * Stany ładowania danych
 */
enum class StateStatus {
    LOADING,   // Trwa ładowanie danych
    ERROR,     // Wystąpił błąd
    SUCCESS    // Dane załadowane pomyślnie
}

/**
 * Model widoku filmu rekomendowanego
 */
data class MovieViewModel(
    val id: String,                      // Id filmu w bazie danych (lub "recommendation_{tmdbId}" dla rekomendacji)
    val tmdbId: String,                  // Id filmu w TMDB
    val title: String,                   // Tytuł filmu
    val posterUrl: String?,              // URL plakatu filmu
    val year: String,                    // Rok produkcji
    val genre: String,                   // Główny gatunek filmu
    val reason: String?                  // Powód rekomendacji (tylko dla rekomendacji)
)

/**
 * Stan UI dla ekranu rekomendacji
 */
data class RecommendationsUiState(
    val status: StateStatus = StateStatus.LOADING,  // Stan UI
    val errorMessage: String? = null,               // Komunikat błędu
    val recommendations: List<MovieViewModel> = emptyList(), // Lista rekomendacji
    val isRefreshing: Boolean = false               // Czy trwa odświeżanie
) 