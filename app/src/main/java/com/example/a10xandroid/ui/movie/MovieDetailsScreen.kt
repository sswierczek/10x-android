package com.example.a10xandroid.ui.movie

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.a10xandroid.data.model.MovieEntry
import com.example.a10xandroid.ui.components.AppTopBar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    movieId: String,
    navController: NavController,
    viewModel: MovieDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = uiState.movieEntry?.title ?: "Movie Details",
                onBackClick = { navController.popBackStack() },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }

                uiState.movieEntry != null -> {
                    MovieDetailsContent(
                        movieEntry = uiState.movieEntry!!,
                        isUpdating = uiState.isUpdating,
                        onRatingChange = viewModel::updateRating
                    )
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Movie") },
                text = { Text("Are you sure you want to delete this movie?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteMovie()
                            showDeleteDialog = false
                            navController.popBackStack()
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun MovieDetailsContent(
    movieEntry: MovieEntry,
    isUpdating: Boolean,
    onRatingChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = movieEntry.title,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))


        Text(
            text = "⭐ ${
                String.format(
                    locale = Locale.UK,
                    "%.1f",
                    movieEntry.rating,
                )
            }/10 • ${movieEntry.releaseDate}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Rating",
            style = MaterialTheme.typography.titleMedium
        )

        Column(
            modifier = Modifier.padding(vertical = 8.dp),
        ) {
            Row {
                repeat(5) { index ->
                    val starValue = index + 1
                    IconButton(
                        onClick = {
                            if (!isUpdating) {
                                onRatingChange(starValue)
                            }
                        },
                        enabled = !isUpdating
                    ) {
                        Icon(
                            imageVector = if (movieEntry.rating >= starValue) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Rate $starValue stars",
                            tint = if (movieEntry.rating >= starValue) {
                                MaterialTheme.colorScheme.tertiary
                            } else {
                                MaterialTheme.colorScheme.tertiaryContainer
                            },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Overview",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = movieEntry.overview ?: "No overview available",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Watched on: ${formatDateFromTimestamp(movieEntry.watchDate)}",
                style = MaterialTheme.typography.bodyMedium
            )

            if (!movieEntry.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Notes",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = movieEntry.notes,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

private fun formatDateFromTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    return SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(date)
}
