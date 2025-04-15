package com.example.a10xandroid.ui.addmovie

/**
 * Stany wyszukiwania
 */
enum class SearchStatus {
    INITIAL,    // Stan początkowy (przed wyszukiwaniem)
    SEARCHING,  // Trwa wyszukiwanie
    RESULTS,    // Wyniki wyszukiwania
    ERROR       // Błąd wyszukiwania
}

/**
 * Model widoku elementu wyszukiwania
 */
data class MovieSearchItemViewModel(
    val tmdbId: String,                  // Id filmu w TMDB
    val title: String,                   // Tytuł filmu
    val posterUrl: String?,              // URL plakatu filmu
    val year: String,                    // Rok produkcji
    val genre: String,                   // Główny gatunek filmu
    val overview: String                 // Opis filmu
)

/**
 * Stan UI dla ekranu dodawania filmu
 */
data class AddMovieUiState(
    val searchQuery: String = "",                     // Aktualne zapytanie wyszukiwania
    val searchStatus: SearchStatus = SearchStatus.INITIAL, // Stan wyszukiwania
    val searchResults: List<MovieSearchItemViewModel> = emptyList(), // Wyniki wyszukiwania
    val errorMessage: String? = null,                 // Komunikat błędu
    val isAddingMovie: Boolean = false,               // Czy trwa dodawanie filmu
    val snackbarMessage: String? = null               // Komunikat Snackbar
) 