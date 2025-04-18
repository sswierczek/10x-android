package com.example.a10xandroid.ui.movie

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a10xandroid.data.model.MovieEntry
import com.example.a10xandroid.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MovieDetailsUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val movieEntry: MovieEntry? = null,
    val isUpdating: Boolean = false
)

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val movieId: String = checkNotNull(savedStateHandle["movieId"])

    private val _uiState = MutableStateFlow(MovieDetailsUiState())
    val uiState: StateFlow<MovieDetailsUiState> = _uiState.asStateFlow()

    init {
        loadMovieDetails()
    }

    private fun loadMovieDetails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val movie = movieRepository.getMovieEntry(movieId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    movieEntry = movie
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load movie details"
                )
            }
        }
    }

    fun updateRating(newRating: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isUpdating = true,
                errorMessage = null
            )

            try {
                val currentMovie = _uiState.value.movieEntry
                if (currentMovie != null) {
                    val updatedMovie = currentMovie.copy(rating = newRating.toFloat())
                    val result = movieRepository.updateMovieEntry(updatedMovie)
                    _uiState.value = _uiState.value.copy(
                        isUpdating = false,
                        movieEntry = result
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUpdating = false,
                    errorMessage = e.message ?: "Failed to update rating"
                )
            }
        }
    }

    fun deleteMovie() {
        viewModelScope.launch {
            try {
                movieRepository.deleteMovieEntry(movieId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Failed to delete movie"
                )
            }
        }
    }
}
