package com.example.a10xandroid.ui.addmovie

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.a10xandroid.ui.components.AppTopBar
import com.example.a10xandroid.ui.components.LoadingStateHandler
import kotlinx.coroutines.launch

@Composable
fun MovieSearchResultItem(
    movie: MovieSearchItemViewModel,
    onAddClick: (MovieSearchItemViewModel) -> Unit,
    isAddingEnabled: Boolean
) {
    var showRatingDialog by remember { mutableStateOf(false) }
    var rating by remember { mutableFloatStateOf(0f) }

    if (showRatingDialog) {
        AlertDialog(
            onDismissRequest = { showRatingDialog = false },
            title = { Text("Rate ${movie.title}") },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(5) { index ->
                            val starValue = index + 1
                            Icon(
                                imageVector = if (rating >= starValue) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = "Rate $starValue stars",
                                tint = if (rating >= starValue) {
                                    MaterialTheme.colorScheme.tertiaryContainer
                                } else {
                                    MaterialTheme.colorScheme.tertiary
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable { rating = starValue.toFloat() }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRatingDialog = false
                        onAddClick(movie.copy(rating = rating))
                    }
                ) {
                    Text("Add to Journal")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRatingDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Button(
        onClick = { showRatingDialog = true },
        enabled = isAddingEnabled
    ) {
        Text("Add to Journal")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMovieScreen(
    navController: NavController,
    viewModel: AddMovieViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Show snackbar messages
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
                viewModel.clearSnackbarMessage()
            }
        }
    }

    // Show error messages
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
                viewModel.clearErrorMessage()
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Search",
                onBackClick = { navController.navigateUp() }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var searchText by remember { mutableStateOf(uiState.searchQuery) }

                TextField(
                    value = searchText,
                    onValueChange = { query ->
                        searchText = query
                        viewModel.updateSearchQuery(query)
                    },
                    placeholder = { Text("Find movies to rate them...") },
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    singleLine = true
                )

                if (searchText.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            searchText = ""
                            viewModel.clearSearchResults()
                        }
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            }

            // Search results or loading state
            LoadingStateHandler(
                isLoading = uiState.searchStatus == SearchStatus.SEARCHING || uiState.isAddingMovie,
                content = {
                    when (uiState.searchStatus) {
                        SearchStatus.INITIAL -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Search for a movie to add to your journal.")
                            }
                        }

                        SearchStatus.RESULTS -> {
                            if (uiState.searchResults.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No results found")
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(uiState.searchResults) { movie ->
                                        MovieSearchResultCard(
                                            movie = movie,
                                            onAddClick = { viewModel.addMovieToJournal(it) },
                                            isAddingEnabled = !uiState.isAddingMovie
                                        )
                                    }
                                }
                            }
                        }

                        SearchStatus.ERROR -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Error: ${uiState.errorMessage ?: "Unknown error"}")
                            }
                        }

                        else -> {} // SEARCHING is handled by LoadingStateHandler
                    }
                }
            )
        }
    }
}

@Composable
fun MovieSearchResultCard(
    movie: MovieSearchItemViewModel,
    onAddClick: (MovieSearchItemViewModel) -> Unit,
    isAddingEnabled: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Movie poster
            AsyncImage(
                model = movie.posterPath,
                contentDescription = "Poster for ${movie.title}",
                modifier = Modifier
                    .width(80.dp)
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Movie details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (movie.year.isNotEmpty()) {
                    Text(
                        text = movie.year,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (movie.genre.isNotEmpty()) {
                    Text(
                        text = movie.genre,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                MovieSearchResultItem(
                    movie = movie,
                    onAddClick = onAddClick,
                    isAddingEnabled = isAddingEnabled
                )
            }
        }
    }
}
