package com.example.a10xandroid.ui.journal

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a10xandroid.data.repository.AuthRepository
import com.example.a10xandroid.data.repository.MovieRepository
import com.example.a10xandroid.data.repository.TmdbRepository
import com.example.a10xandroid.ui.common.StateStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

private const val TAG = "JournalViewModel"

/**
 * ViewModel for the movie journal screen
 */
@HiltViewModel
class JournalViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val tmdbRepository: TmdbRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // UI state as StateFlow
    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState: StateFlow<JournalUiState> = _uiState.asStateFlow()

    init {
        loadMovies()
    }

    /**
     * Load movies from the repository
     */
    fun loadMovies() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(status = StateStatus.LOADING) }

                // Get current user ID
                val currentUser = authRepository.currentUser.first()
                if (currentUser == null) {
                    _uiState.update {
                        it.copy(
                            status = StateStatus.ERROR,
                            errorMessage = "You are not logged in",
                            isRefreshing = false
                        )
                    }
                    return@launch
                }

                movieRepository.getMovieEntriesFlow(currentUser.uid)
                    .catch { error ->
                        Log.e(TAG, "Error loading user movie list", error)
                        _uiState.update {
                            it.copy(
                                status = StateStatus.ERROR,
                                errorMessage = "Failed to load movies",
                                isRefreshing = false
                            )
                        }
                    }
                    .collect { movieList ->
                        if (movieList.isEmpty()) {
                            _uiState.update {
                                it.copy(
                                    status = StateStatus.SUCCESS,
                                    movies = emptyList(),
                                    isRefreshing = false
                                )
                            }
                            return@collect
                        }

                        try {
                            val moviesViewModels = movieList.mapNotNull { movie ->
                                try {
                                    val dateFormat =
                                        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                                    val formattedDate = dateFormat.format(Date(movie.createdAt))

                                    JournalModelForView(
                                        id = movie.id,
                                        tmdbId = movie.tmdbId,
                                        title = movie.title,
                                        posterUrl = movie.posterPath?.let { path ->
                                            "https://image.tmdb.org/t/p/w500$path"
                                        },
                                        year = movie.releaseDate?.take(4) ?: "",
                                        genre = "",
                                        rating = movie.rating,
                                        addedAt = movie.createdAt,
                                        addedAtFormatted = formattedDate
                                    )
                                } catch (e: Exception) {
                                    Log.e(
                                        TAG,
                                        "Error processing movie: ${movie.title}",
                                        e
                                    )
                                    null
                                }
                            }

                            val sortedMovies =
                                sortMovies(moviesViewModels, _uiState.value.sortOrder)

                            _uiState.update {
                                it.copy(
                                    status = StateStatus.SUCCESS,
                                    movies = sortedMovies,
                                    isRefreshing = false
                                )
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error processing movie list", e)
                            _uiState.update {
                                it.copy(
                                    status = StateStatus.ERROR,
                                    errorMessage = "Failed to process movies",
                                    isRefreshing = false
                                )
                            }
                        }
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading user movie list", e)
                _uiState.update {
                    it.copy(
                        status = StateStatus.ERROR,
                        errorMessage = "Failed to load movies",
                        isRefreshing = false
                    )
                }
            }
        }
    }

    /**
     * Refresh the movie list
     */
    fun refreshMovies() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadMovies()
    }

    /**
     * Toggle the sort order
     */
    fun toggleSortOrder() {
        val currentSortOrder = _uiState.value.sortOrder
        val newSortOrder = if (currentSortOrder == SortOrder.DATE_ADDED_DESC) {
            SortOrder.DATE_ADDED_ASC
        } else {
            SortOrder.DATE_ADDED_DESC
        }

        val sortedMovies = sortMovies(_uiState.value.movies, newSortOrder)

        _uiState.update {
            it.copy(
                sortOrder = newSortOrder,
                movies = sortedMovies
            )
        }
    }

    /**
     * Sort the movie list according to specified criteria
     */
    private fun sortMovies(
        movies: List<JournalModelForView>,
        sortOrder: SortOrder
    ): List<JournalModelForView> {
        return when (sortOrder) {
            SortOrder.DATE_ADDED_ASC -> movies.sortedBy { it.addedAt }
            SortOrder.DATE_ADDED_DESC -> movies.sortedByDescending { it.addedAt }
        }
    }

    fun deleteMovie(movieId: String) {
        viewModelScope.launch {
            try {
                movieRepository.deleteMovieEntry(movieId)
                loadMovies()
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting movie", e)
                _uiState.update {
                    it.copy(
                        status = StateStatus.ERROR,
                        errorMessage = "Failed to delete movie"
                    )
                }
            }
        }
    }
}
