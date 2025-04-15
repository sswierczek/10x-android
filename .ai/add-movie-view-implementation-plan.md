# Plan implementacji widoku Dodawania Filmu

## 1. Przegląd
Widok Dodawania Filmu umożliwia użytkownikom wyszukiwanie filmów za pomocą API TMDB i dodawanie ich do swojego dziennika filmowego. Zawiera pole wyszukiwania z funkcją autouzupełniania, listę wyników wyszukiwania oraz możliwość dodania wybranego filmu do dziennika. Jest to jeden z czterech głównych widoków dostępnych z poziomu dolnej nawigacji aplikacji.

## 2. Routing widoku
Ścieżka widoku: `/add-movie`

Ten widok jest jednym z głównych widoków aplikacji i będzie dostępny bezpośrednio z dolnej nawigacji.

## 3. Struktura komponentów
```
AddMovieScreen
├── TopAppBar z tytułem "Dodaj Film"
├── SearchBar
├── LoadingStateHandler
│   ├── (stan wyszukiwania) SearchResults
│   │   └── MovieSearchItem (wiele instancji)
│   ├── (stan ładowania) CircularProgressIndicator
│   ├── (stan błędu) ErrorView z komunikatem błędu
│   └── (stan początkowy) InitialView z instrukcją wyszukiwania
└── Snackbar (potwierdzenie dodania filmu)
```

## 4. Szczegóły komponentów
### AddMovieScreen
- **Opis komponentu**: Główny ekran dodawania filmu, zawierający wszystkie komponenty potrzebne do wyszukiwania i dodawania filmów.
- **Główne elementy**: 
  - Scaffold z TopAppBar
  - SearchBar do wyszukiwania filmów
  - LoadingStateHandler do obsługi różnych stanów UI
  - Snackbar do wyświetlania komunikatów
- **Obsługiwane interakcje**:
  - Wyszukiwanie filmów
  - Dodawanie filmu do dziennika
- **Obsługiwana walidacja**: Sprawdzanie, czy użytkownik jest zalogowany
- **Typy**: AddMovieViewModel, AddMovieUiState
- **Propsy**: NavController (do nawigacji)

### SearchBar
- **Opis komponentu**: Pole wyszukiwania z funkcją autouzupełniania.
- **Główne elementy**:
  - OutlinedTextField z:
    - Label "Wyszukaj film"
    - Ikona search
    - Przycisk czyszczenia tekstu
    - Keyboard type (search)
- **Obsługiwane interakcje**: 
  - Wprowadzanie i edycja tekstu
  - Czyszczenie pola wyszukiwania
  - Zatwierdzanie wyszukiwania
- **Obsługiwana walidacja**: Weryfikacja czy pole nie jest puste przed wyszukiwaniem
- **Typy**: Brak specyficznych
- **Propsy**:
  - query: String - aktualny tekst wyszukiwania
  - onQueryChanged: (String) -> Unit - funkcja wywoływana przy zmianie tekstu
  - onSearch: () -> Unit - funkcja wywoływana przy zatwierdzeniu wyszukiwania
  - onClear: () -> Unit - funkcja wywoływana przy czyszczeniu pola

### SearchResults
- **Opis komponentu**: Lista wyników wyszukiwania filmów.
- **Główne elementy**:
  - LazyColumn z elementami MovieSearchItem
- **Obsługiwane interakcje**:
  - Przewijanie listy
  - Kliknięcie na film
- **Obsługiwana walidacja**: Brak
- **Typy**: List<MovieSearchItemViewModel>
- **Propsy**:
  - searchResults: List<MovieSearchItemViewModel> - lista wyników wyszukiwania
  - onMovieClick: (MovieSearchItemViewModel) -> Unit - funkcja wywoływana po kliknięciu na film

### MovieSearchItem
- **Opis komponentu**: Element listy wyników wyszukiwania reprezentujący pojedynczy film.
- **Główne elementy**:
  - Card z Row zawierającym:
    - AsyncImage dla plakatu filmu
    - Column z tekstem (tytuł, rok, gatunek)
    - IconButton do dodania filmu do dziennika
- **Obsługiwane interakcje**: 
  - Kliknięcie na kartę filmu
  - Kliknięcie przycisku dodawania
- **Obsługiwana walidacja**: Brak
- **Typy**: MovieSearchItemViewModel
- **Propsy**:
  - movie: MovieSearchItemViewModel - dane filmu do wyświetlenia
  - onMovieClick: () -> Unit - funkcja wywoływana po kliknięciu na kartę
  - onAddClick: () -> Unit - funkcja wywoływana po kliknięciu przycisku dodawania

### InitialView
- **Opis komponentu**: Widok wyświetlany przed rozpoczęciem wyszukiwania.
- **Główne elementy**:
  - Column z:
    - Icon lub ilustracją search
    - Text z instrukcją "Wyszukaj film, aby dodać go do swojego dziennika"
- **Obsługiwane interakcje**: Brak
- **Obsługiwana walidacja**: Brak
- **Typy**: Brak specyficznych
- **Propsy**: Brak

### ErrorView
- **Opis komponentu**: Widok wyświetlany w przypadku błędu wyszukiwania.
- **Główne elementy**:
  - Column z:
    - Icon lub ilustracją error
    - Text z komunikatem błędu
- **Obsługiwane interakcje**: Brak
- **Obsługiwana walidacja**: Brak
- **Typy**: Brak specyficznych
- **Propsy**:
  - errorMessage: String - komunikat błędu do wyświetlenia

## 5. Typy
### Enumeracje
```kotlin
enum class SearchStatus {
    INITIAL,    // Stan początkowy (przed wyszukiwaniem)
    SEARCHING,  // Trwa wyszukiwanie
    RESULTS,    // Wyniki wyszukiwania
    ERROR       // Błąd wyszukiwania
}
```

### DTO
```kotlin
// Dane wyszukiwania z TMDB API
data class TmdbSearchResultDTO(
    val page: Int = 1,                   // Numer strony wyników
    val results: List<TmdbMovieDTO> = emptyList(), // Lista znalezionych filmów
    val totalPages: Int = 0,             // Całkowita liczba stron
    val totalResults: Int = 0            // Całkowita liczba wyników
)

// Dane filmu z TMDB API
data class TmdbMovieDTO(
    val id: Int = 0,                     // Id filmu w TMDB
    val title: String = "",              // Tytuł filmu
    val posterPath: String? = null,      // Ścieżka do plakatu filmu
    val releaseDate: String = "",        // Data premiery filmu
    val genreIds: List<Int> = emptyList(), // Identyfikatory gatunków filmu
    val overview: String = ""            // Opis filmu
)

// Dane gatunku filmu
data class GenreDTO(
    val id: Int = 0,                     // Id gatunku
    val name: String = ""                // Nazwa gatunku
)

// Dane filmu do zapisania w Firebase
data class MovieDTO(
    val id: String = "",                 // Id filmu w bazie danych
    val tmdbId: String = "",             // Id filmu w TMDB
    val addedAt: Long = 0,               // Timestamp dodania filmu
    val userId: String = ""              // Id użytkownika, który dodał film
)
```

### ViewModels
```kotlin
// Model widoku elementu wyszukiwania
data class MovieSearchItemViewModel(
    val tmdbId: String,                  // Id filmu w TMDB
    val title: String,                   // Tytuł filmu
    val posterUrl: String?,              // URL plakatu filmu
    val year: String,                    // Rok produkcji
    val genre: String,                   // Główny gatunek filmu
    val overview: String                 // Opis filmu
)

// Stan UI dla ekranu dodawania filmu
data class AddMovieUiState(
    val searchQuery: String = "",                     // Aktualne zapytanie wyszukiwania
    val searchStatus: SearchStatus = SearchStatus.INITIAL, // Stan wyszukiwania
    val searchResults: List<MovieSearchItemViewModel> = emptyList(), // Wyniki wyszukiwania
    val errorMessage: String? = null,                 // Komunikat błędu
    val isAddingMovie: Boolean = false,               // Czy trwa dodawanie filmu
    val snackbarMessage: String? = null               // Komunikat Snackbar
)
```

## 6. Zarządzanie stanem
Stan widoku Dodawania Filmu będzie zarządzany przez AddMovieViewModel, który będzie odpowiedzialny za:
- Przechowywanie i walidację zapytania wyszukiwania
- Wyszukiwanie filmów w TMDB API
- Dodawanie filmów do dziennika użytkownika
- Zarządzanie stanem UI (wyszukiwanie, wyniki, błąd)

```kotlin
class AddMovieViewModel(
    private val tmdbRepository: TmdbRepository,
    private val movieRepository: MovieRepository
) : ViewModel() {

    // Stan UI jako StateFlow
    private val _uiState = MutableStateFlow(AddMovieUiState())
    val uiState: StateFlow<AddMovieUiState> = _uiState.asStateFlow()

    // Aktualizacja zapytania wyszukiwania
    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    // Wyczyszczenie pola wyszukiwania
    fun clearSearchQuery() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            searchStatus = SearchStatus.INITIAL
        )
    }

    // Wyszukiwanie filmów
    fun searchMovies() {
        val query = _uiState.value.searchQuery.trim()
        
        // Walidacja zapytania
        if (query.isEmpty()) {
            return
        }
        
        // Ustawienie stanu wyszukiwania
        _uiState.value = _uiState.value.copy(searchStatus = SearchStatus.SEARCHING)
        
        viewModelScope.launch {
            try {
                // Wyszukiwanie filmów w TMDB API
                val searchResults = tmdbRepository.searchMovies(query)
                
                // Przetwarzanie wyników wyszukiwania
                val searchItemViewModels = processSearchResults(searchResults)
                
                _uiState.value = _uiState.value.copy(
                    searchStatus = if (searchItemViewModels.isEmpty()) SearchStatus.ERROR else SearchStatus.RESULTS,
                    searchResults = searchItemViewModels,
                    errorMessage = if (searchItemViewModels.isEmpty()) "Nie znaleziono filmów" else null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    searchStatus = SearchStatus.ERROR,
                    errorMessage = e.message ?: "Błąd wyszukiwania filmów"
                )
            }
        }
    }

    // Dodawanie filmu do dziennika
    fun addMovieToJournal(movie: MovieSearchItemViewModel) {
        if (_uiState.value.isAddingMovie) {
            return
        }
        
        _uiState.value = _uiState.value.copy(isAddingMovie = true)
        
        viewModelScope.launch {
            try {
                // Tworzenie obiektu MovieDTO
                val movieDto = MovieDTO(
                    tmdbId = movie.tmdbId,
                    addedAt = System.currentTimeMillis()
                    // userId zostanie dodany przez repository
                )
                
                // Dodawanie filmu do dziennika
                movieRepository.addMovie(movieDto)
                
                _uiState.value = _uiState.value.copy(
                    isAddingMovie = false,
                    snackbarMessage = "Film '${movie.title}' został dodany do dziennika"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isAddingMovie = false,
                    snackbarMessage = "Błąd dodawania filmu: ${e.message}"
                )
            }
        }
    }

    // Wyczyszczenie komunikatu Snackbar
    fun clearSnackbarMessage() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    // Przetwarzanie wyników wyszukiwania
    private suspend fun processSearchResults(searchResults: TmdbSearchResultDTO): List<MovieSearchItemViewModel> {
        // Pobranie informacji o gatunkach
        val genres = tmdbRepository.getGenres()
        
        // Mapowanie wyników na model widoku
        return searchResults.results.map { movieDto ->
            MovieSearchItemViewModel(
                tmdbId = movieDto.id.toString(),
                title = movieDto.title,
                posterUrl = movieDto.posterPath?.let { path -> "https://image.tmdb.org/t/p/w500$path" },
                year = movieDto.releaseDate.take(4).takeIf { it.isNotEmpty() } ?: "Nieznany",
                genre = getGenreNameById(movieDto.genreIds.firstOrNull(), genres),
                overview = movieDto.overview
            )
        }
    }

    // Pobranie nazwy gatunku po ID
    private fun getGenreNameById(genreId: Int?, genres: List<GenreDTO>): String {
        return genreId?.let { id ->
            genres.find { it.id == id }?.name
        } ?: "Nieznany"
    }
}
```

## 7. Integracja API
Widok będzie korzystać z dwóch głównych źródeł danych:

### TMDB API
- Wyszukiwanie filmów
- Zapytanie: `tmdbRepository.searchMovies(query)`
- Parametry:
  - query: String - tekst wyszukiwania
- Typ odpowiedzi: `TmdbSearchResultDTO`

- Pobieranie listy gatunków
- Zapytanie: `tmdbRepository.getGenres()`
- Typ odpowiedzi: `List<GenreDTO>`

### Firebase Realtime Database
- Dodawanie filmu do dziennika
- Zapytanie: `movieRepository.addMovie(movieDto)`
- Parametry:
  - movieDto: MovieDTO - dane filmu do dodania
- Typ odpowiedzi: Brak (Success) lub wyjątek (Failure)

## 8. Interakcje użytkownika
### Wyszukiwanie filmów
- Użytkownik wprowadza tekst w polu wyszukiwania
  - Aktualizacja stanu `searchQuery` poprzez `setSearchQuery()`
- Użytkownik zatwierdza wyszukiwanie (np. klikając przycisk lub naciskając Enter)
  - Wywołanie metody `searchMovies()` w ViewModel
  - Wyświetlenie wskaźnika ładowania podczas wyszukiwania
  - Wyświetlenie listy wyników lub komunikatu o błędzie

### Dodawanie filmu do dziennika
- Użytkownik klika przycisk dodawania przy wybranym filmie
  - Wywołanie metody `addMovieToJournal()` w ViewModel z odpowiednim modelem filmu
  - Wyświetlenie komunikatu Snackbar o pomyślnym dodaniu lub błędzie

### Czyszczenie pola wyszukiwania
- Użytkownik klika przycisk czyszczenia w polu wyszukiwania
  - Wywołanie metody `clearSearchQuery()` w ViewModel
  - Wyczyszczenie pola wyszukiwania i powrót do stanu początkowego

## 9. Warunki i walidacja
- **Autentykacja użytkownika**:
  - Widok jest dostępny tylko dla zalogowanych użytkowników
  - Jeśli użytkownik nie jest zalogowany, zostanie przekierowany do ekranu logowania
  - Implementacja w nawigacji głównej

- **Walidacja zapytania wyszukiwania**:
  - Zapytanie nie może być puste
  - Implementacja w ViewModel przed wywołaniem wyszukiwania

## 10. Obsługa błędów
- **Brak połączenia internetowego**:
  - Wyświetlenie komunikatu "Brak połączenia z internetem"
  - Komunikat wyświetlany w widoku ErrorView

- **Błąd TMDB API**:
  - Wyświetlenie komunikatu o błędzie z API
  - Komunikat wyświetlany w widoku ErrorView

- **Brak wyników wyszukiwania**:
  - Wyświetlenie komunikatu "Nie znaleziono filmów dla zapytania: [query]"
  - Komunikat wyświetlany w widoku ErrorView

- **Błąd dodawania filmu**:
  - Wyświetlenie komunikatu o błędzie w Snackbar
  - Możliwość ponowienia dodawania

## 11. Kroki implementacji
1. **Utworzenie szkieletu widoku**:
   - Utworzenie głównego komponentu AddMovieScreen
   - Implementacja podstawowego layoutu z Scaffold i TopAppBar

2. **Implementacja komponentów wyszukiwania**:
   - Implementacja SearchBar
   - Implementacja InitialView
   - Implementacja ErrorView

3. **Implementacja wyników wyszukiwania**:
   - Implementacja SearchResults
   - Implementacja MovieSearchItem

4. **Implementacja ViewModel**:
   - Utworzenie AddMovieViewModel
   - Implementacja metod do wyszukiwania filmów
   - Implementacja metod do dodawania filmów do dziennika

5. **Integracja z TMDB API**:
   - Implementacja metod wyszukiwania w TmdbRepository
   - Implementacja metody pobierania gatunków

6. **Integracja z Firebase**:
   - Implementacja metody dodawania filmu do dziennika

7. **Obsługa stanów UI**:
   - Implementacja różnych stanów wyszukiwania
   - Dodanie wskaźnika ładowania
   - Implementacja Snackbar do komunikatów

8. **Implementacja interakcji użytkownika**:
   - Obsługa wprowadzania tekstu
   - Obsługa kliknięć na filmy
   - Obsługa dodawania filmów

9. **Obsługa błędów i stanów brzegowych**:
   - Implementacja obsługi błędów API
   - Obsługa przypadku braku wyników

10. **Testowanie i debugowanie**:
    - Testowanie wyszukiwania
    - Testowanie dodawania filmów
    - Testowanie obsługi błędów

11. **Finalne poprawki i optymalizacje**:
    - Optymalizacja wydajności
    - Poprawki UI/UX
    - Integracja z dolną nawigacją 