package com.example.a10xandroid.ui.addmovie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a10xandroid.data.api.model.TmdbGenreApiResponse
import com.example.a10xandroid.data.model.MovieEntry
import com.example.a10xandroid.data.repository.AuthRepository
import com.example.a10xandroid.data.repository.MovieRepository
import com.example.a10xandroid.data.repository.TmdbRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

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
                        _uiState.value = _uiState.value.copy(
                            searchStatus = SearchStatus.ERROR,
                            errorMessage = "Błąd podczas wyszukiwania: ${e.message}"
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
                                posterUrl = tmdbRepository.getPosterUrl(movie.posterPath, "w500"),
                                year = if (movie.releaseDate.isNotEmpty()) movie.releaseDate.take(4) else "",
                                genre = genre,
                                overview = movie.overview
                            )
                        }
                        
                        _uiState.value = _uiState.value.copy(
                            searchStatus = SearchStatus.RESULTS,
                            searchResults = movieViewModels
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    searchStatus = SearchStatus.ERROR,
                    errorMessage = "Błąd podczas wyszukiwania: ${e.message}"
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
                _uiState.value = _uiState.value.copy(
                    isAddingMovie = true,
                    errorMessage = null
                )
                
                // Pobierz bieżącego użytkownika
                val currentUser = authRepository.currentUser.first()
                if (currentUser == null) {
                    _uiState.value = _uiState.value.copy(
                        isAddingMovie = false,
                        errorMessage = "Nie jesteś zalogowany"
                    )
                    return@launch
                }
                
                // Pobierz szczegóły filmu z TMDB
                tmdbRepository.getMovieDetails(movie.tmdbId.toInt()).collect { details ->
                    if (details == null) {
                        _uiState.value = _uiState.value.copy(
                            isAddingMovie = false,
                            errorMessage = "Nie można pobrać szczegółów filmu"
                        )
                        return@collect
                    }
                    
                    // Utwórz wpis dziennika
                    val movieEntry = MovieEntry(
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
                    
                    // Dodaj film do repozytorium
                    val addedEntry = movieRepository.addMovieEntry(movieEntry)
                    
                    // Aktualizuj stan UI
                    _uiState.value = _uiState.value.copy(
                        isAddingMovie = false,
                        snackbarMessage = "Film został dodany do dziennika"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isAddingMovie = false,
                    errorMessage = "Błąd podczas dodawania filmu: ${e.message}"
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
            28 -> "Akcja"
            12 -> "Przygodowy"
            16 -> "Animacja"
            35 -> "Komedia"
            80 -> "Kryminał"
            99 -> "Dokument"
            18 -> "Dramat"
            10751 -> "Familijny"
            14 -> "Fantasy"
            36 -> "Historyczny"
            27 -> "Horror"
            10402 -> "Muzyczny"
            9648 -> "Tajemnica"
            10749 -> "Romans"
            878 -> "Sci-Fi"
            10770 -> "TV Film"
            53 -> "Thriller"
            10752 -> "Wojenny"
            37 -> "Western"
            else -> "Inny"
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