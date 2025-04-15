package com.example.a10xandroid.ui.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.a10xandroid.navigation.NavRoutes

/**
 * Główny ekran dziennika filmowego
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    navController: NavController,
    viewModel: JournalViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            JournalTopAppBar(
                currentSortOrder = uiState.sortOrder,
                onSortOrderChanged = {
                    viewModel.toggleSortOrder()
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(NavRoutes.ADD_MOVIE)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Dodaj film"
                )
            }
        }
    ) { paddingValues ->
        LoadingStateHandler(
            stateStatus = uiState.status,
            errorMessage = uiState.errorMessage,
            onRetry = { viewModel.loadMovies() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.movies.isEmpty()) {
                EmptyStateView(
                    message = "Twój dziennik filmowy jest pusty",
                    actionLabel = "Dodaj pierwszy film",
                    onActionClick = {
                        navController.navigate(NavRoutes.ADD_MOVIE)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                MoviesList(
                    movies = uiState.movies,
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = { viewModel.refreshMovies() },
                    onMovieClick = { movieId ->
                        navController.navigate("${NavRoutes.MOVIE_DETAILS}/$movieId")
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/**
 * TopAppBar dla ekranu dziennika
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalTopAppBar(
    currentSortOrder: SortOrder,
    onSortOrderChanged: () -> Unit
) {
    TopAppBar(
        title = { Text("Dziennik") },
        actions = {
            SortingControl(
                currentSortOrder = currentSortOrder,
                onSortOrderChanged = onSortOrderChanged
            )
        }
    )
}

/**
 * Komponent kontrolujący sortowanie filmów
 */
@Composable
fun SortingControl(
    currentSortOrder: SortOrder,
    onSortOrderChanged: () -> Unit
) {
    // Ikona sortowania zależna od aktualnego trybu
    val sortIcon = when (currentSortOrder) {
        SortOrder.DATE_ADDED_ASC -> Icons.Default.ArrowUpward
        SortOrder.DATE_ADDED_DESC -> Icons.Default.ArrowDownward
    }
    
    val sortDescription = when (currentSortOrder) {
        SortOrder.DATE_ADDED_ASC -> "Sortowanie od najstarszych"
        SortOrder.DATE_ADDED_DESC -> "Sortowanie od najnowszych"
    }
    
    IconButton(onClick = onSortOrderChanged) {
        Icon(
            imageVector = sortIcon,
            contentDescription = sortDescription
        )
    }
}

/**
 * Komponent zarządzający różnymi stanami UI
 */
@Composable
fun LoadingStateHandler(
    stateStatus: StateStatus,
    errorMessage: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        when (stateStatus) {
            StateStatus.LOADING -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            StateStatus.ERROR -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Błąd",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = errorMessage ?: "Wystąpił nieznany błąd",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(onClick = onRetry) {
                        Text("Spróbuj ponownie")
                    }
                }
            }
            StateStatus.SUCCESS -> {
                content()
            }
        }
    }
}

/**
 * Widok wyświetlany, gdy dziennik filmowy jest pusty
 */
@Composable
fun EmptyStateView(
    message: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Movie,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        
        if (actionLabel != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(onClick = onActionClick) {
                Text(actionLabel)
            }
        }
    }
}

/**
 * Lista filmów z obsługą pull-to-refresh
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MoviesList(
    movies: List<MovieViewModel>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onMovieClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefresh
    )
    
    Box(
        modifier = modifier
            .pullRefresh(pullRefreshState)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = movies,
                key = { it.id }
            ) { movie ->
                MovieCard(
                    movie = movie,
                    onClick = { onMovieClick(movie.id) }
                )
            }
        }
        
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

/**
 * Karta filmu w dzienniku
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieCard(
    movie: MovieViewModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            // Plakat filmu
            PosterImage(
                posterUrl = movie.posterUrl,
                title = movie.title,
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
            )
            
            // Informacje o filmie
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                // Tytuł filmu
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Rok i gatunek
                Text(
                    text = "${movie.year} • ${movie.genre}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Data dodania
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = "Dodano: ${movie.addedAtFormatted}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Komponent wyświetlający plakat filmu
 */
@Composable
fun PosterImage(
    posterUrl: String?,
    title: String,
    modifier: Modifier = Modifier
) {
    if (posterUrl != null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(posterUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Plakat filmu $title",
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Movie,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
} 