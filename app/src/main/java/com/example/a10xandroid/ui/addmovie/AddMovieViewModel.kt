package com.example.a10xandroid.ui.addmovie

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a10xandroid.data.auth.AuthRepository
import com.example.a10xandroid.data.model.MovieEntry
import com.example.a10xandroid.data.repository.MovieRepository
import com.example.a10xandroid.data.tmbd.TmdbRepository
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
 * ViewModel for the add movie screen
 */
@HiltViewModel
class AddMovieViewModel @Inject constructor(
    private val tmdbRepository: TmdbRepository,
    private val movieRepository: MovieRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // UI state as MutableStateFlow
    private val _uiState = MutableStateFlow(AddMovieUiState())
    val uiState: StateFlow<AddMovieUiState> = _uiState.asStateFlow()

    // Delayed search
    @OptIn(FlowPreview::class)
    private val searchQuery = MutableStateFlow("")

    init {
        // Listen for search query changes with debounce
        viewModelScope.launch {
            Log.d(TAG, "Initializing search query listener")
            searchQuery
                .debounce(500) // 500ms delay
                .filter { query ->
                    val shouldSearch = query.isNotBlank() && query.length >= 2
                    Log.d(TAG, "Query '$query' filtered: $shouldSearch (length: ${query.length})")
                    shouldSearch
                }
                .distinctUntilChanged()
                .collect { query ->
                    Log.d(TAG, "Collecting search query: '$query'")
                    searchMovies(query)
                }
        }

        // Sync search query with UI state
        viewModelScope.launch {
            searchQuery.collect { query ->
                _uiState.value = _uiState.value.copy(
                    searchQuery = query,
                    searchStatus = if (query.isBlank()) SearchStatus.INITIAL else _uiState.value.searchStatus
                )
            }
        }
    }

    /**
     * Update search query
     */
    fun updateSearchQuery(query: String) {
        Log.d(TAG, "Updating search query to: '$query'")
        viewModelScope.launch {
            _uiState.emit(_uiState.value.copy(searchQuery = query))
            searchQuery.emit(query)
        }
    }

    /**
     * Search for movies in TMDB
     */
    private fun searchMovies(query: String) {
        Log.d(TAG, "Starting movie search for query: '$query'")
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    searchStatus = SearchStatus.SEARCHING,
                    errorMessage = null
                )
                Log.d(TAG, "Set search status to SEARCHING")

                tmdbRepository.searchMovies(query)
                    .catch { e ->
                        Log.e(TAG, "Error searching movies for query '$query'", e)
                        _uiState.value = _uiState.value.copy(
                            searchStatus = SearchStatus.ERROR,
                            errorMessage = "Error searching: ${e.message}"
                        )
                    }
                    .collect { results ->
                        Log.d(TAG, "Received ${results.size} search results for query '$query'")
                        val movieViewModels = results.map { movie ->
                            // Get genre details for first genre ID (if exists)
                            var genre = ""
                            if (movie.genreIds.isNotEmpty()) {
                                genre = getGenreName(movie.genreIds.first())
                            }

                            MovieSearchItemViewModel(
                                tmdbId = movie.id.toString(),
                                title = movie.title,
                                posterPath = tmdbRepository.getPosterUrl(
                                    movie.posterPath,
                                    "w500"
                                ),
                                year = if (movie.releaseDate.isNotEmpty()) {
                                    movie.releaseDate.take(4)
                                } else {
                                    ""
                                },
                                genre = genre,
                                overview = movie.overview,
                                rating = movie.voteAverage.toFloat(),
                                releaseDate = movie.releaseDate
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
                _uiState.value = _uiState.value.copy(
                    isAddingMovie = true,
                    errorMessage = null
                )
                Log.d(
                    TAG,
                    "Starting to add movie to journal: ${movie.title} (TMDB ID: ${movie.tmdbId})"
                )

                val currentUser = authRepository.currentUser.first()
                if (currentUser == null) {
                    Log.e(TAG, "No user logged in")
                    _uiState.value = _uiState.value.copy(
                        isAddingMovie = false,
                        errorMessage = "You must be logged in to add movies"
                    )
                    return@launch
                }
                Log.d(TAG, "Current user: ${currentUser.uid}")

                tmdbRepository.getMovieDetails(movie.tmdbId).collect { movieDetails ->
                    Log.d(TAG, "Fetched movie details for TMDB ID: ${movie.tmdbId}")

                    val movieEntry = MovieEntry(
                        id = "",
                        tmdbId = movie.tmdbId,
                        userId = currentUser.uid,
                        title = movieDetails?.title ?: "",
                        overview = movieDetails?.overview ?: "",
                        posterPath = movieDetails?.posterPath ?: "",
                        backdropPath = movieDetails?.backdropPath ?: "",
                        releaseDate = movieDetails?.releaseDate ?: "",
                        rating = movie.rating,
                        watchDate = System.currentTimeMillis(),
                        notes = "",
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )

                    val firebaseId = movieRepository.addMovieEntry(movieEntry)
                    Log.d(TAG, "Added movie to repository with Firebase ID: $firebaseId")
                }


                _uiState.value = _uiState.value.copy(
                    isAddingMovie = false,
                    snackbarMessage = "Movie added to journal"
                )
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
     * Clear search results and query
     */
    fun clearSearchResults() {
        Log.d(TAG, "Clearing search results and query")
        viewModelScope.launch {
            searchQuery.emit("")
            _uiState.emit(
                _uiState.value.copy(
                    searchQuery = "",
                    searchStatus = SearchStatus.INITIAL,
                    searchResults = emptyList()
                )
            )
        }
    }
}
