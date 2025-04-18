package com.example.a10xandroid.ui.journal

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import kotlinx.coroutines.delay

private const val TAG = "JournalScreen"

/**
 * Główny ekran dziennika filmowego
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
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

    // Floating action button animations
    val fabScale = remember { Animatable(0.8f) }

    LaunchedEffect(key1 = Unit) {
        fabScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

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
                    onClick = {
                        navController.navigate(NavRoutes.RECOMMENDATIONS)
                    },
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier
                        .scale(fabScale.value)
                        .graphicsLayer {
                            rotationY = fabScale.value * 360
                        }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_magic_wand),
                        contentDescription = "Get Recommendations"
                    )
                }
                FloatingActionButton(
                    onClick = {
                        navController.navigate(NavRoutes.ADD_MOVIE)
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .scale(fabScale.value)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Movie"
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
        ) {
            // Sort order selector - now first in the Column with animation
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                SortOrderSelector(
                    currentOrder = uiState.sortOrder,
                    onOrderSelected = { viewModel.toggleSortOrder() }
                )
            }

            // Spacer to create some separation
            Spacer(modifier = Modifier.height(4.dp))

            // Main content - takes remaining space
            Box(modifier = Modifier.weight(1f)) {
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
}

/**
 * Komponent wyboru sortowania
 */
@Composable
fun SortOrderSelector(
    currentOrder: SortOrder,
    onOrderSelected: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val buttonColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onOrderSelected,
            interactionSource = interactionSource
        ) {
            Text(
                text = if (currentOrder == SortOrder.DATE_ADDED_DESC) "Newest First" else "Oldest First"
            )
            Spacer(modifier = Modifier.width(4.dp))

            // Animated icon rotation
            val rotation by animateFloatAsState(
                targetValue = if (currentOrder == SortOrder.DATE_ADDED_DESC) 0f else 180f,
                animationSpec = tween<Float>(durationMillis = 300, easing = FastOutSlowInEasing)
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = if (currentOrder == SortOrder.DATE_ADDED_DESC) {
                    "Sort descending"
                } else {
                    "Sort ascending"
                },
                modifier = Modifier.graphicsLayer {
                    rotationZ = rotation
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
        AnimatedVisibility(
            visible = state.status == StateStatus.LOADING,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LoadingIndicator()
        }

        AnimatedVisibility(
            visible = state.status == StateStatus.ERROR,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            ErrorView(message = state.errorMessage ?: "Unknown error")
        }

        AnimatedVisibility(
            visible = state.status == StateStatus.SUCCESS,
            enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
            exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.Center)
        ) {
            if (state.movies.isEmpty()) {
                Log.d(TAG, "SUCCESS state with empty movies, showing EmptyStateView")
                EmptyStateView(navController)
            } else {
                content()
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
        val infiniteTransition = rememberInfiniteTransition()
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )

        CircularProgressIndicator(
            modifier = Modifier.graphicsLayer {
                rotationZ = rotation
            }
        )
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
        val infiniteTransition = rememberInfiniteTransition()
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = "Error",
            modifier = Modifier
                .size(48.dp)
                .scale(scale),
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
 * Lista filmów z animacjami
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
        itemsIndexed(
            items = movies,
            key = { _, movie -> movie.id }
        ) { index, movie ->
            // Staggered animation for each item
            var itemAlpha by remember { mutableStateOf(0f) }
            var itemScale by remember { mutableStateOf(0.8f) }

            LaunchedEffect(key1 = movie.id) {
                itemAlpha = 0f
                itemScale = 0.8f
                delay(index * 50L)
                itemAlpha = 1f
                itemScale = 1f
            }

            MovieCard(
                movie = movie,
                onRemove = { onRemove(movie.id) },
                navController = navController,
                modifier = Modifier
                    .graphicsLayer {
                        alpha = itemAlpha
                        scaleX = itemScale
                        scaleY = itemScale
                    }
                    .animateContentSize()
            )
        }
    }
}

/**
 * Karta filmu w dzienniku z animacjami
 */
@Composable
fun MovieCard(
    movie: JournalModelForView,
    onRemove: () -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var cardElevation by remember { mutableStateOf(2.dp) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("${NavRoutes.MOVIE_DETAILS}/${movie.id}")
            }
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation)
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

                // Animated stars
                Row {
                    repeat(5) { index ->
                        val starValue = index + 1
                        val filled = movie.rating >= starValue

                        val starColor by animateColorAsState(
                            targetValue = if (filled)
                                MaterialTheme.colorScheme.tertiary
                            else
                                MaterialTheme.colorScheme.tertiaryContainer,
                            animationSpec = tween<Color>(durationMillis = 300)
                        )

                        Icon(
                            imageVector = if (filled) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Rate $starValue stars",
                            tint = starColor,
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

                // Button with pulse animation on hover
                OutlinedButton(
                    onClick = {
                        onRemove()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
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
