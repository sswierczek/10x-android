package com.example.a10xandroid.ui.addmovie

/**
 * Stany wyszukiwania
 */
enum class SearchStatus {
    INITIAL,
    SEARCHING,
    RESULTS,
    ERROR
}

/**
 * Model widoku elementu wyszukiwania
 */
data class MovieSearchItemViewModel(
    val tmdbId: String,
    val title: String,
    val posterUrl: String?,
    val year: String,
    val genre: String,
    val overview: String
)

/**
 * Stan UI dla ekranu dodawania filmu
 */
data class AddMovieUiState(
    val searchQuery: String = "",
    val searchStatus: SearchStatus = SearchStatus.INITIAL,
    val searchResults: List<MovieSearchItemViewModel> = emptyList(),
    val errorMessage: String? = null,
    val isAddingMovie: Boolean = false,
    val snackbarMessage: String? = null
)
