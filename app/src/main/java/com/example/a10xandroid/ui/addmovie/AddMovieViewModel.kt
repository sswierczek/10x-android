package com.example.a10xandroid.ui.addmovie

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a10xandroid.data.model.MovieEntry
import com.example.a10xandroid.data.repository.AuthRepository
import com.example.a10xandroid.data.repository.MovieRepository
import com.example.a10xandroid.data.repository.TmdbRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AddMovieViewModel"

/**
 * ViewModel dla ekranu dodawania filmu
 */
@HiltViewModel
class AddMovieViewModel @Inject constructor(
    private val tmdbRepository: TmdbRepository,
    private val movieRepository: MovieRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // Stan UI jako MutableStateFlow
    private val _uiState = MutableStateFlow(AddMovieUiState())
    val uiState: StateFlow<AddMovieUiState> = _uiState.asStateFlow()

    // Opóźnione wyszukiwanie
    @OptIn(FlowPreview::class)
    private val searchQuery = MutableStateFlow("")

    init {
        // Nasłuchuj zmian w zapytaniu wyszukiwania z debounce
        viewModelScope.launch {
            searchQuery
                .debounce(500) // Opóźnienie 500ms
                .filter { it.isNotBlank() && it.length >= 2 }
                .distinctUntilChanged()
                .collect {
                    searchMovies(it)
                }
        }
    }

    /**
     * Aktualizacja zapytania wyszukiwania
     */
    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query
        )
        searchQuery.value = query

        // Resetuj wyniki jeśli zapytanie jest puste
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(
                searchStatus = SearchStatus.INITIAL,
                searchResults = emptyList()
            )
        }
    }

    /**
     * Wyszukiwanie filmów w TMDB
     */
    private fun searchMovies(query: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    searchStatus = SearchStatus.SEARCHING,
                    errorMessage = null
                )

                tmdbRepository.searchMovies(query)
                    .catch { e ->
                        Log.e(TAG, "Error searching movies", e)
                        _uiState.value = _uiState.value.copy(
                            searchStatus = SearchStatus.ERROR,
                            errorMessage = "Error searching: ${e.message}"
                        )
                    }
                    .collect { results ->
                        val movieViewModels = results.map { movie ->
                            // Pobierz szczegóły gatunku dla pierwszego ID gatunku (jeśli istnieje)
                            var genre = ""
                            if (movie.genreIds.isNotEmpty()) {
                                genre = getGenreName(movie.genreIds.first())
                            }

                            MovieSearchItemViewModel(
                                tmdbId = movie.id.toString(),
                                title = movie.title,
                                posterUrl = tmdbRepository.getPosterUrl(
                                    movie.posterPath,
                                    "w500"
                                ),
                                year = if (movie.releaseDate.isNotEmpty()) {
                                    movie.releaseDate.take(
                                        4
                                    )
                                } else {
                                    ""
                                },
                                genre = genre,
                                overview = movie.overview,
                                rating = movie.voteAverage
                            )
                        }

                        _uiState.value = _uiState.value.copy(
                            searchStatus = SearchStatus.RESULTS,
                            searchResults = movieViewModels
                        )
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error searching movies", e)
                _uiState.value = _uiState.value.copy(
                    searchStatus = SearchStatus.ERROR,
                    errorMessage = "Error searching: ${e.message}"
                )
            }
        }
    }

    /**
     * Dodawanie filmu do dziennika użytkownika
     */
    fun addMovieToJournal(movie: MovieSearchItemViewModel) {
        viewModelScope.launch {
            try {
                Log.d(
                    TAG,
                    "Starting to add movie to journal: ${movie.title} (TMDB ID: ${movie.tmdbId})"
                )
                _uiState.value = _uiState.value.copy(
                    isAddingMovie = true,
                    errorMessage = null
                )

                // Pobierz bieżącego użytkownika
                val currentUser = authRepository.currentUser.first()
                if (currentUser == null) {
                    Log.e(TAG, "No user logged in")
                    _uiState.value = _uiState.value.copy(
                        isAddingMovie = false,
                        errorMessage = "You are not logged in"
                    )
                    return@launch
                }
                Log.d(TAG, "Current user: ${currentUser.uid}")

                // Pobierz szczegóły filmu z TMDB
                tmdbRepository.getMovieDetails(movie.tmdbId.toInt()).collect { details ->
                    if (details == null) {
                        Log.e(TAG, "Could not fetch movie details for TMDB ID: ${movie.tmdbId}")
                        _uiState.value = _uiState.value.copy(
                            isAddingMovie = false,
                            errorMessage = "Could not fetch movie details"
                        )
                        return@collect
                    }
                    Log.d(TAG, "Got movie details from TMDB: ${details.title} (ID: ${details.id})")

                    // Utwórz wpis dziennika
                    val movieEntry = MovieEntry(
                        tmdbId = movie.tmdbId,
                        userId = currentUser.uid,
                        title = details.title,
                        overview = details.overview,
                        posterPath = details.posterPath,
                        backdropPath = details.backdropPath,
                        releaseDate = details.releaseDate,
                        watchDate = System.currentTimeMillis(),
                        notes = "",
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                    Log.d(TAG, "Created movie entry with TMDB ID: ${movieEntry.tmdbId}")

                    // Dodaj film do repozytorium
                    val addedEntry = movieRepository.addMovieEntry(movieEntry)
                    Log.d(
                        TAG,
                        "Added movie to repository with Firebase ID: ${addedEntry.id} and TMDB ID: ${addedEntry.tmdbId}"
                    )

                    // Aktualizuj stan UI
                    _uiState.value = _uiState.value.copy(
                        isAddingMovie = false,
                        snackbarMessage = "Movie added to journal"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error adding movie to journal", e)
                _uiState.value = _uiState.value.copy(
                    isAddingMovie = false,
                    errorMessage = "Error adding movie: ${e.message}"
                )
            }
        }
    }

    /**
     * Pobiera nazwę gatunku na podstawie ID
     */
    private fun getGenreName(genreId: Int): String {
        // Mapowanie ID gatunków na nazwy (można przenieść do zasobów)
        return when (genreId) {
            28 -> "Action"
            12 -> "Adventure"
            16 -> "Animation"
            35 -> "Comedy"
            80 -> "Crime"
            99 -> "Documentary"
            18 -> "Drama"
            10751 -> "Family"
            14 -> "Fantasy"
            36 -> "History"
            27 -> "Horror"
            10402 -> "Music"
            9648 -> "Mystery"
            10749 -> "Romance"
            878 -> "Science Fiction"
            10770 -> "TV Movie"
            53 -> "Thriller"
            10752 -> "War"
            37 -> "Western"
            else -> "Unknown"
        }
    }

    /**
     * Czyszczenie komunikatu Snackbar
     */
    fun clearSnackbarMessage() {
        _uiState.value = _uiState.value.copy(
            snackbarMessage = null
        )
    }

    /**
     * Czyszczenie komunikatu błędu
     */
    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }

    /**
     * Czyszczenie wyników wyszukiwania
     */
    fun clearSearchResults() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            searchStatus = SearchStatus.INITIAL,
            searchResults = emptyList()
        )
        searchQuery.value = ""
    }
}
