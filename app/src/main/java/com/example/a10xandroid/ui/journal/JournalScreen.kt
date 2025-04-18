package com.example.a10xandroid.ui.journal

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.a10xandroid.R
import com.example.a10xandroid.data.auth.AuthRepository
import com.example.a10xandroid.navigation.NavRoutes
import com.example.a10xandroid.ui.common.StateStatus
import com.example.a10xandroid.ui.components.AppTopBar

private const val TAG = "JournalScreen"

/**
 * Główny ekran dziennika filmowego
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    navController: NavController,
    viewModel: JournalViewModel = hiltViewModel(),
    authRepository: AuthRepository
) {
    Log.d(TAG, "JournalScreen composable called")
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by authRepository.currentUser.collectAsStateWithLifecycle(initialValue = null)

    // Log when the screen becomes active
    val lifecycleOwner = LocalLifecycleOwner.current
    remember(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                Log.d(TAG, "JournalScreen resumed")
            }
        })
    }

    var showAddMovieDialog by remember { mutableStateOf(false) }
    var showRecommendationsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Journal",
                onProfileClick = { navController.navigate(NavRoutes.PROFILE) }
            )
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                FloatingActionButton(
                    onClick = { navController.navigate(NavRoutes.RECOMMENDATIONS) },
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_magic_wand),
                        contentDescription = "Get Recommendations"
                    )
                }
                FloatingActionButton(
                    onClick = { navController.navigate(NavRoutes.ADD_MOVIE) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Movie"
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Sort order selector
            SortOrderSelector(
                currentOrder = uiState.sortOrder,
                onOrderSelected = { viewModel.toggleSortOrder() }
            )

            // Main content
            LoadingStateHandler(
                state = uiState,
                navController = navController,
                content = {
                    if (uiState.movies.isEmpty()) {
                        Log.d(TAG, "Movies list is empty, showing EmptyStateView")
                        EmptyStateView(navController)
                    } else {
                        Log.d(TAG, "Showing MoviesList with ${uiState.movies.size} movies")
                        MoviesList(
                            movies = uiState.movies,
                            onRemove = { movieId ->
                                viewModel.deleteMovie(movieId)
                            },
                            navController = navController,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Komponent wyboru sortowania
 */
@Composable
fun SortOrderSelector(
    currentOrder: SortOrder,
    onOrderSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onOrderSelected
        ) {
            Text(
                text = if (currentOrder == SortOrder.DATE_ADDED_DESC) "Newest First" else "Oldest First"
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = if (currentOrder == SortOrder.DATE_ADDED_DESC) {
                    Icons.Default.KeyboardArrowDown
                } else {
                    Icons.Default.KeyboardArrowUp
                },
                contentDescription = if (currentOrder == SortOrder.DATE_ADDED_DESC) {
                    "Sort descending"
                } else {
                    "Sort ascending"
                }
            )
        }
    }
}

/**
 * Komponent zarządzający różnymi stanami UI
 */
@Composable
fun LoadingStateHandler(
    state: JournalUiState,
    navController: NavController,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Log.d(TAG, "LoadingStateHandler called with state: ${state.status}")
    Box(modifier = modifier) {
        when (state.status) {
            StateStatus.LOADING -> LoadingIndicator()
            StateStatus.ERROR -> ErrorView(message = state.errorMessage ?: "Unknown error")
            StateStatus.SUCCESS -> {
                if (state.movies.isEmpty()) {
                    Log.d(TAG, "SUCCESS state with empty movies, showing EmptyStateView")
                    EmptyStateView(navController)
                } else {
                    content()
                }
            }
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorView(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = "Error",
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )
    }
}

/**
 * Widok wyświetlany, gdy dziennik filmowy jest pusty
 */
@Composable
fun EmptyStateView(navController: NavController) {
    Log.d(TAG, "EmptyStateView called")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = "Empty state",
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Your journal is empty",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Add movies you've watched to see them here",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Log.d(TAG, "Showing Add Movie button")
        Button(
            onClick = {
                try {
                    Log.d(TAG, "Add Movie button clicked, navigating to ADD_MOVIE")
                    navController.navigate(NavRoutes.ADD_MOVIE)
                } catch (e: Exception) {
                    Log.e(TAG, "Error navigating to ADD_MOVIE", e)
                }
            }
        ) {
            Text("Add Movie")
        }
    }
}

/**
 * Lista filmów z obsługą pull-to-refresh
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesList(
    movies: List<JournalModelForView>,
    onRemove: (String) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        state = lazyListState
    ) {
        items(movies) { movie ->
            MovieCard(
                movie = movie,
                onRemove = { onRemove(movie.id) },
                navController = navController
            )
        }
    }
}

/**
 * Karta filmu w dzienniku
 */
@Composable
fun MovieCard(
    movie: JournalModelForView,
    onRemove: () -> Unit,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("${NavRoutes.MOVIE_DETAILS}/${movie.id}")
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(movie.posterUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Movie poster",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = movie.year,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    repeat(5) { index ->
                        val starValue = index + 1
                        Icon(
                            imageVector = if (movie.rating >= starValue) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Rate $starValue stars",
                            tint = if (movie.rating >= starValue) {
                                MaterialTheme.colorScheme.tertiary
                            } else {
                                MaterialTheme.colorScheme.tertiaryContainer
                            },
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = "Added date",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Added ${movie.addedAtFormatted}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = onRemove,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Remove",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Remove from journal")
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
            contentDescription = "Movie poster for $title",
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
                imageVector = Icons.Filled.Favorite,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
