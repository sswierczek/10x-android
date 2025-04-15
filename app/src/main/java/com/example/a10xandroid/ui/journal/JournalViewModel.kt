package com.example.a10xandroid.ui.journal

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a10xandroid.data.repository.FirebaseMovieListRepository
import com.example.a10xandroid.data.repository.TmdbRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

private const val TAG = "JournalViewModel"

/**
 * ViewModel dla ekranu dziennika filmowego
 */
@HiltViewModel
class JournalViewModel @Inject constructor(
    private val movieListRepository: FirebaseMovieListRepository,
    private val tmdbRepository: TmdbRepository
) : ViewModel() {

    // Stan UI jako StateFlow
    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState: StateFlow<JournalUiState> = _uiState.asStateFlow()

    init {
        loadMovies()
    }

    /**
     * Ładowanie filmów z repozytorium
     */
    fun loadMovies() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(status = StateStatus.LOADING)
                
                // Obserwuj zmiany w liście filmów użytkownika
                movieListRepository.getUserMoviesList().collectLatest { movieList ->
                    if (movieList.isEmpty()) {
                        // Pusta lista filmów
                        _uiState.value = _uiState.value.copy(
                            status = StateStatus.SUCCESS,
                            movies = emptyList(),
                            isRefreshing = false
                        )
                        return@collectLatest
                    }
                    
                    try {
                        // Pobierz szczegóły filmów z TMDB
                        val moviesViewModels = mutableListOf<MovieViewModel>()
                        
                        for (movie in movieList) {
                            try {
                                val tmdbMovie = tmdbRepository.getMovieDetails(movie.tmdbId)
                                
                                // Formatowanie daty dodania
                                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                                val formattedDate = dateFormat.format(Date(movie.addedAt))
                                
                                // Utworzenie viewmodelu filmu
                                val movieViewModel = MovieViewModel(
                                    id = movie.id,
                                    tmdbId = movie.tmdbId,
                                    title = tmdbMovie.title,
                                    posterUrl = if (tmdbMovie.posterPath != null) {
                                        "https://image.tmdb.org/t/p/w500${tmdbMovie.posterPath}"
                                    } else null,
                                    year = tmdbMovie.releaseDate.take(4),
                                    genre = if (tmdbMovie.genres.isNotEmpty()) tmdbMovie.genres.first().name else "",
                                    addedAt = movie.addedAt,
                                    addedAtFormatted = formattedDate
                                )
                                
                                moviesViewModels.add(movieViewModel)
                            } catch (e: Exception) {
                                Log.e(TAG, "Error fetching movie details for ID: ${movie.tmdbId}", e)
                                // Kontynuuj pętlę, pomijając film z błędem
                            }
                        }
                        
                        // Sortowanie filmów
                        val sortedMovies = sortMovies(moviesViewModels, _uiState.value.sortOrder)
                        
                        // Aktualizacja stanu UI
                        _uiState.value = _uiState.value.copy(
                            status = StateStatus.SUCCESS,
                            movies = sortedMovies,
                            isRefreshing = false
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing movie list", e)
                        _uiState.value = _uiState.value.copy(
                            status = StateStatus.ERROR,
                            errorMessage = "Wystąpił błąd podczas pobierania danych filmów",
                            isRefreshing = false
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading user movie list", e)
                _uiState.value = _uiState.value.copy(
                    status = StateStatus.ERROR, 
                    errorMessage = "Nie udało się pobrać listy filmów",
                    isRefreshing = false
                )
            }
        }
    }

    /**
     * Odświeżanie listy filmów
     */
    fun refreshMovies() {
        _uiState.value = _uiState.value.copy(isRefreshing = true)
        loadMovies()
    }

    /**
     * Przełączanie trybu sortowania
     */
    fun toggleSortOrder() {
        val currentSortOrder = _uiState.value.sortOrder
        val newSortOrder = if (currentSortOrder == SortOrder.DATE_ADDED_DESC) {
            SortOrder.DATE_ADDED_ASC
        } else {
            SortOrder.DATE_ADDED_DESC
        }
        
        val sortedMovies = sortMovies(_uiState.value.movies, newSortOrder)
        
        _uiState.value = _uiState.value.copy(
            sortOrder = newSortOrder,
            movies = sortedMovies
        )
    }

    /**
     * Sortowanie listy filmów według określonego kryterium
     */
    private fun sortMovies(movies: List<MovieViewModel>, sortOrder: SortOrder): List<MovieViewModel> {
        return when (sortOrder) {
            SortOrder.DATE_ADDED_ASC -> movies.sortedBy { it.addedAt }
            SortOrder.DATE_ADDED_DESC -> movies.sortedByDescending { it.addedAt }
        }
    }
} 