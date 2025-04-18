package com.example.a10xandroid.ui.recommendations

import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.a10xandroid.ui.components.AppTopBar
import kotlin.math.cos
import kotlin.math.sin

/**
 * Main screen for displaying movie recommendations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationsScreen(
    navController: NavController,
    viewModel: RecommendationsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show snackbar when message is available
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbarMessage()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Recommendations",
                onBackClick = { navController.navigateUp() },
                actions = {
                    IconButton(onClick = { viewModel.refreshRecommendations() }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh recommendations",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    AILoadingIndicator()
                }

                uiState.hasError -> {
                    ErrorView(
                        message = uiState.errorMessage ?: "An error occurred",
                        onRetry = { viewModel.loadRecommendations() }
                    )
                }

                uiState.isEmpty -> {
                    EmptyStateView(
                        message = "No recommendations available yet. Add some movies to your journal to get personalized recommendations!",
                        actionLabel = "Add Movies",
                        onActionClick = { navController.navigate("add_movie") }
                    )
                }

                else -> {
                    RecommendationsList(
                        recommendations = uiState.recommendations,
                        onAddToJournal = { movie -> viewModel.addToJournal(movie) }
                    )
                }
            }
        }
    }
}

/**
 * AI-powered animated loading indicator that visualizes the recommendation process
 */
@Composable
fun AILoadingIndicator() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // AI Brain animation
        val infiniteTransition = rememberInfiniteTransition()

        val radiusMultiplier by infiniteTransition.animateFloat(
            initialValue = 0.6f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        val rotationAnim by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(10000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )

        val nodeColors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.primary
        )

        Canvas(
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp)
        ) {
            // Draw brain network connections
            val centerX = size.width / 2
            val centerY = size.height / 2
            val radius = size.width / 2 * radiusMultiplier

            // Draw circles representing movie data nodes
            val numNodes = 10
            for (i in 0 until numNodes) {
                val angle = (i * (360 / numNodes) + rotationAnim) % 360
                val x = centerX + radius * cos(Math.toRadians(angle.toDouble())).toFloat()
                val y = centerY + radius * sin(Math.toRadians(angle.toDouble())).toFloat()

                // Draw connection lines to center
                drawLine(
                    start = Offset(x, y),
                    end = Offset(centerX, centerY),
                    color = nodeColors[i % nodeColors.size].copy(alpha = 0.5f),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )

                // Draw nodes
                drawCircle(
                    color = nodeColors[i % nodeColors.size],
                    radius = 10f,
                    center = Offset(x, y)
                )
            }

            // Draw center brain node
            drawCircle(
                color = nodeColors[0].copy(alpha = 0.5f),
                radius = 20f,
                center = Offset(centerX, centerY)
            )

            // Draw orbit
            drawCircle(
                color = Color.Gray.copy(alpha = 0.2f),
                radius = radius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2f)
            )
        }

        // Pulse effect for data nodes
        Box(
            modifier = Modifier.padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Multiple pulsing dots
            MovieDataNodes()
        }

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("AI ")
                }
                append("is analyzing your movie preferences")
            },
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = "Finding personalized recommendations based on your taste profile",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, start = 32.dp, end = 32.dp)
        )
    }
}

@Composable
fun MovieDataNodes() {
    val nodeInfo = listOf(
        "Action" to MaterialTheme.colorScheme.primary,
        "Drama" to MaterialTheme.colorScheme.secondary,
        "Comedy" to MaterialTheme.colorScheme.tertiary,
        "Sci-Fi" to MaterialTheme.colorScheme.error,
        "Horror" to MaterialTheme.colorScheme.primary
    )

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            nodeInfo.forEachIndexed { index, (text, color) ->
                val delayMillis = index * 500
                PulsingDot(color = color, delayMillis = delayMillis)
                if (index < nodeInfo.size - 1) {
                    Spacer(modifier = Modifier.width(18.dp))
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            nodeInfo.forEachIndexed { index, (text, _) ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.width(72.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp,
                    fontStyle = FontStyle.Italic
                )
                if (index < nodeInfo.size - 1) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}

@Composable
fun PulsingDot(color: Color, delayMillis: Int = 0) {
    val infiniteTransition = rememberInfiniteTransition()

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, delayMillis = delayMillis, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        )
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, delayMillis = delayMillis, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .size(12.dp * scale)
            .alpha(alpha)
            .clip(CircleShape)
            .background(color)
    )
}

/**
 * Error view component
 */
@Composable
fun ErrorView(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Try Again")
        }
    }
}

/**
 * Empty state view component
 */
@Composable
fun EmptyStateView(
    message: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
 * List of recommendations component
 */
@Composable
fun RecommendationsList(
    recommendations: List<RecommendationMovieViewModel>,
    onAddToJournal: (RecommendationMovieViewModel) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(recommendations) { movie ->
            RecommendationMovieCard(
                movie = movie,
                onAddToJournal = { onAddToJournal(movie) }
            )
        }
    }
}

/**
 * Movie card component
 */
@Composable
fun RecommendationMovieCard(
    movie: RecommendationMovieViewModel,
    onAddToJournal: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Movie poster and title row
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Movie poster
                if (movie.posterUrl != null) {
                    AsyncImage(
                        model = movie.posterUrl,
                        contentDescription = "Poster for ${movie.title}",
                        modifier = Modifier
                            .width(80.dp)
                            .height(120.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }

                // Movie details
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "⭐ ${String.format("%.1f", movie.rating)}/10 • ${movie.year}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Overview
            Text(
                text = movie.overview,
                style = MaterialTheme.typography.bodyMedium
            )

            // Recommendation reason
            if (movie.reason != null) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "AI recommendation reason:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = movie.reason,
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action button
            if (movie.saved) {
                // Show a disabled button with "Added" text when movie is already saved
                Button(
                    onClick = { /* No action needed */ },
                    modifier = Modifier.align(Alignment.End),
                    enabled = false
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Added to Journal")
                }
            } else {
                // Show the regular "Add to Journal" button
                Button(
                    onClick = onAddToJournal,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add to Journal")
                }
            }
        }
    }
}
