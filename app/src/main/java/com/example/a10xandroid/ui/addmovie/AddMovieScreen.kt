package com.example.a10xandroid.ui.addmovie

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch

/**
 * Główny ekran dodawania filmu do dziennika
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMovieScreen(
    navController: NavController,
    viewModel: AddMovieViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Obsługa komunikatów Snackbar
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbarMessage()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dodaj film") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Powrót"
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
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Pasek wyszukiwania
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::updateSearchQuery,
                    onSearch = {
                        if (uiState.searchQuery.isNotBlank()) {
                            focusManager.clearFocus()
                        }
                    },
                    onClearQuery = viewModel::clearSearchResults,
                    isEnabled = uiState.searchStatus != SearchStatus.SEARCHING && !uiState.isAddingMovie,
                    modifier = Modifier.padding(16.dp)
                )
                
                // Wyniki wyszukiwania
                when (uiState.searchStatus) {
                    SearchStatus.INITIAL -> {
                        InitialSearchState(
                            modifier = Modifier.weight(1f)
                        )
                    }
                    SearchStatus.SEARCHING -> {
                        SearchingIndicator(
                            modifier = Modifier.padding(top = 32.dp)
                        )
                    }
                    SearchStatus.RESULTS -> {
                        if (uiState.searchResults.isEmpty()) {
                            NoSearchResultsMessage(
                                query = uiState.searchQuery,
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 8.dp)
                            ) {
                                items(
                                    items = uiState.searchResults,
                                    key = { it.tmdbId }
                                ) { movie ->
                                    MovieSearchResultItem(
                                        movie = movie,
                                        onAddClick = {
                                            scope.launch {
                                                viewModel.addMovieToJournal(it)
                                            }
                                        },
                                        isAddingEnabled = !uiState.isAddingMovie
                                    )
                                }
                            }
                        }
                    }
                    SearchStatus.ERROR -> {
                        SearchErrorMessage(
                            message = uiState.errorMessage ?: "Wystąpił nieznany błąd podczas wyszukiwania",
                            onRetry = {
                                viewModel.updateSearchQuery(uiState.searchQuery)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            // Wskaźnik dodawania filmu
            AddingIndicator(isVisible = uiState.isAddingMovie)
        }
    }
} 