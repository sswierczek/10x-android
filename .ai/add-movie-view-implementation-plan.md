# Plan implementacji widoku Dodawania Filmów

## 1. Przegląd
Widok Dodawania Filmów umożliwia użytkownikowi wyszukiwanie filmów w bazie TMDB i dodawanie ich do swojego dziennika filmowego. Widok ten składa się z paska wyszukiwania, listy wyników wyszukiwania oraz możliwości dodania wybranego filmu do dziennika.

## 2. Routing widoku
Ścieżka widoku: `/add-movie`

Ten widok jest dostępny z poziomu dziennika filmowego, poprzez przycisk "Dodaj pierwszy film" w pustym stanie dziennika oraz przez przycisk FloatingActionButton w wypełnionym dzienniku.

## 3. Struktura komponentów
```
AddMovieScreen
├── TopAppBar z przyciskiem powrotu
├── SearchBar
└── Zależnie od stanu wyszukiwania:
    ├── (stan początkowy) InitialSearchState
    ├── (stan wyszukiwania) SearchingIndicator
    ├── (stan wyników) LazyColumn z MovieSearchResultItem
    │   lub NoSearchResultsMessage
    └── (stan błędu) SearchErrorMessage
```

## 4. Szczegóły komponentów
### AddMovieScreen
- **Opis komponentu**: Główny ekran umożliwiający wyszukiwanie i dodawanie filmów do dziennika.
- **Główne elementy**: 
  - Scaffold z TopAppBar i przyciskiem powrotu
  - SearchBar do wprowadzania zapytania wyszukiwania
  - Widok wyników wyszukiwania zależny od stanu
  - AddingIndicator - wskaźnik dodawania filmu
- **Obsługiwane interakcje**:
  - Wprowadzanie i czyszczenie zapytania wyszukiwania
  - Kliknięcie przycisku dodawania filmu
  - Powrót do dziennika
- **Obsługiwana walidacja**: Sprawdzanie, czy zapytanie ma co najmniej 2 znaki
- **Typy**: AddMovieViewModel, SearchStatus
- **Propsy**: NavController (do nawigacji)

### SearchBar
- **Opis komponentu**: Pasek wyszukiwania z polem tekstowym i przyciskiem czyszczenia.
- **Główne elementy**:
  - OutlinedTextField do wprowadzania zapytania
  - Ikona wyszukiwania
  - Przycisk czyszczenia zapytania
- **Obsługiwane interakcje**: 
  - Wprowadzanie zapytania
  - Czyszczenie zapytania
  - Wyszukiwanie po wciśnięciu przycisku "Search" na klawiaturze
- **Obsługiwana walidacja**: Brak
- **Typy**: Brak
- **Propsy**:
  - query: String - aktualne zapytanie wyszukiwania
  - onQueryChange: (String) -> Unit - funkcja wywoływana przy zmianie zapytania
  - onSearch: () -> Unit - funkcja wywoływana przy wyszukiwaniu
  - onClearQuery: () -> Unit - funkcja wywoływana przy czyszczeniu zapytania
  - isEnabled: Boolean - czy pasek jest aktywny

### MovieSearchResultItem
- **Opis komponentu**: Element listy wyników wyszukiwania, przedstawiający podstawowe informacje o filmie.
- **Główne elementy**:
  - Card z Row zawierającym:
    - PosterImage dla plakatu filmu
    - Column z tekstem (tytuł, rok, gatunek, opis)
    - Button "Dodaj" do dodawania filmu do dziennika
- **Obsługiwane interakcje**: Kliknięcie przycisku "Dodaj"
- **Obsługiwana walidacja**: Brak
- **Typy**: MovieSearchItemViewModel
- **Propsy**:
  - movie: MovieSearchItemViewModel - dane filmu do wyświetlenia
  - onAddClick: (MovieSearchItemViewModel) -> Unit - funkcja wywoływana po kliknięciu przycisku dodawania
  - isAddingEnabled: Boolean - czy przycisk dodawania jest aktywny

### InitialSearchState
- **Opis komponentu**: Widok wyświetlany przed rozpoczęciem wyszukiwania.
- **Główne elementy**:
  - Icon wyszukiwania
  - Text z instrukcją dla użytkownika
- **Obsługiwane interakcje**: Brak
- **Obsługiwana walidacja**: Brak
- **Typy**: Brak
- **Propsy**: modifier: Modifier - modyfikator layoutu

### SearchingIndicator
- **Opis komponentu**: Wskaźnik ładowania wyników wyszukiwania.
- **Główne elementy**: CircularProgressIndicator
- **Obsługiwane interakcje**: Brak
- **Obsługiwana walidacja**: Brak
- **Typy**: Brak
- **Propsy**: modifier: Modifier - modyfikator layoutu

### NoSearchResultsMessage
- **Opis komponentu**: Komunikat wyświetlany, gdy nie znaleziono filmów dla zapytania.
- **Główne elementy**:
  - Icon SearchOff
  - Text z komunikatem o braku wyników
  - Text z sugestią zmiany zapytania
- **Obsługiwane interakcje**: Brak
- **Obsługiwana walidacja**: Brak
- **Typy**: Brak
- **Propsy**:
  - query: String - zapytanie wyszukiwania
  - modifier: Modifier - modyfikator layoutu

### SearchErrorMessage
- **Opis komponentu**: Komunikat błędu wyszukiwania.
- **Główne elementy**:
  - Icon Error
  - Text z komunikatem błędu
  - Button "Spróbuj ponownie"
- **Obsługiwane interakcje**: Kliknięcie przycisku ponowienia
- **Obsługiwana walidacja**: Brak
- **Typy**: Brak
- **Propsy**:
  - message: String - komunikat błędu
  - onRetry: () -> Unit - funkcja wywoływana po kliknięciu przycisku ponowienia
  - modifier: Modifier - modyfikator layoutu

### AddingIndicator
- **Opis komponentu**: Wskaźnik dodawania filmu do dziennika.
- **Główne elementy**:
  - Surface z przyciemnieniem
  - CircularProgressIndicator
  - Text z komunikatem "Dodawanie filmu..."
- **Obsługiwane interakcje**: Brak
- **Obsługiwana walidacja**: Brak
- **Typy**: Brak
- **Propsy**:
  - isVisible: Boolean - czy wskaźnik jest widoczny

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
// Model filmu z TMDB API - już zdefiniowany w projekcie
// TmdbMovieApiResult, TmdbMovieDetailsApiResponse
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
Stan widoku Dodawania Filmów będzie zarządzany przez AddMovieViewModel, który będzie odpowiedzialny za:
- Wyszukiwanie filmów w TMDB API
- Zarządzanie stanem wyszukiwania (początkowy, wyszukiwanie, wyniki, błąd)
- Dodawanie filmów do dziennika użytkownika
- Obsługę błędów i komunikatów

```kotlin
class AddMovieViewModel(
    private val tmdbRepository: TmdbRepository,
    private val movieRepository: MovieRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // Stan UI jako StateFlow
    private val _uiState = MutableStateFlow(AddMovieUiState())
    val uiState: StateFlow<AddMovieUiState> = _uiState.asStateFlow()

    // Opóźnione wyszukiwanie z debounce
    private val searchQuery = MutableStateFlow("")
    
    init {
        // Nasłuchuj zmian w zapytaniu wyszukiwania z debounce
        viewModelScope.launch {
            searchQuery
                .debounce(500) // Opóźnienie 500ms
                .filter { it.isNotBlank() && it.length >= 2 }
                .distinctUntilChanged()
                .collect {
                    searchMovies(it)
                }
        }
    }

    // Aktualizacja zapytania wyszukiwania
    fun updateSearchQuery(query: String) {
        // Implementacja
    }

    // Wyszukiwanie filmów w TMDB
    private fun searchMovies(query: String) {
        // Implementacja
    }

    // Dodawanie filmu do dziennika użytkownika
    fun addMovieToJournal(movie: MovieSearchItemViewModel) {
        // Implementacja
    }

    // Czyszczenie komunikatów
    fun clearSnackbarMessage() { /* ... */ }
    fun clearErrorMessage() { /* ... */ }
    fun clearSearchResults() { /* ... */ }
}
```

## 7. Integracja API
Widok będzie korzystać z dwóch głównych źródeł danych:

### TMDB API
- Wyszukiwanie filmów
- Zapytanie: `tmdbRepository.searchMovies(query)`
- Typ odpowiedzi: `Flow<List<TmdbMovieApiResult>>`
- Pobieranie szczegółów filmów: `tmdbRepository.getMovieDetails(tmdbId)`
- Typ odpowiedzi: `Flow<TmdbMovieDetailsApiResponse?>`

### Firebase Realtime Database
- Dodawanie filmu do dziennika użytkownika
- Zapytanie: `movieRepository.addMovieEntry(movieEntry)`
- Typ odpowiedzi: `MovieEntry`

## 8. Interakcje użytkownika
### Wyszukiwanie filmów
- Użytkownik wprowadza tekst w polu wyszukiwania
- ViewModel nasłuchuje zmian z opóźnieniem (debounce)
- Po 500ms bezczynności, jeśli tekst ma co najmniej 2 znaki, automatycznie rozpoczyna się wyszukiwanie
- Stan wyszukiwania zmienia się z INITIAL na SEARCHING
- Po otrzymaniu wyników, stan zmienia się na RESULTS i wyświetlana jest lista filmów lub komunikat o braku wyników
- W przypadku błędu, stan zmienia się na ERROR i wyświetlany jest komunikat błędu

### Dodawanie filmu do dziennika
- Użytkownik klika przycisk "Dodaj" przy wybranym filmie
- ViewModel pobiera szczegóły filmu z TMDB API
- ViewModel tworzy nowy wpis dziennika
- ViewModel dodaje wpis do repozytorium
- Po pomyślnym dodaniu, wyświetlany jest komunikat Snackbar "Film został dodany do dziennika"
- W przypadku błędu, wyświetlany jest komunikat błędu

### Czyszczenie wyszukiwania
- Użytkownik klika przycisk czyszczenia w pasku wyszukiwania
- ViewModel czyści zapytanie wyszukiwania i wyniki
- Stan wyszukiwania wraca do INITIAL i wyświetlany jest widok początkowy

### Nawigacja
- Użytkownik klika przycisk powrotu w górnym pasku aplikacji
- Następuje powrót do widoku dziennika

## 9. Warunki i walidacja
- **Walidacja zapytania wyszukiwania**:
  - Zapytanie musi mieć co najmniej 2 znaki, aby rozpoczęło się wyszukiwanie
  - Implementacja w warstwie ViewModel

- **Autentykacja użytkownika**:
  - Widok jest dostępny tylko dla zalogowanych użytkowników
  - Jeśli użytkownik nie jest zalogowany, nie może dodać filmu (dodatkowe sprawdzenie w ViewModel)
  - Implementacja w warstwie ViewModel i nawigacji

## 10. Obsługa błędów
- **Brak połączenia internetowego**:
  - Wyświetlenie komunikatu "Brak połączenia z internetem" w SearchErrorMessage
  - Przycisk "Spróbuj ponownie" pozwala na ponowienie próby wyszukiwania

- **Błąd API TMDB**:
  - Wyświetlenie komunikatu z informacją o błędzie w SearchErrorMessage
  - Przycisk "Spróbuj ponownie" pozwala na ponowienie próby wyszukiwania

- **Błąd dodawania filmu**:
  - Wyświetlenie komunikatu o błędzie jako komunikat Snackbar
  - Możliwość ponownej próby dodania filmu

- **Brak wyników wyszukiwania**:
  - Wyświetlenie NoSearchResultsMessage z sugestią zmiany zapytania wyszukiwania

## 11. Kroki implementacji
1. **Utworzenie modeli danych**:
   - Implementacja MovieSearchItemViewModel
   - Implementacja AddMovieUiState
   - Implementacja enumeracji SearchStatus

2. **Implementacja ViewModel**:
   - Utworzenie AddMovieViewModel
   - Implementacja wyszukiwania z opóźnieniem (debounce)
   - Implementacja dodawania filmów do dziennika

3. **Implementacja komponentów pomocniczych**:
   - Implementacja SearchBar
   - Implementacja InitialSearchState
   - Implementacja SearchingIndicator
   - Implementacja NoSearchResultsMessage
   - Implementacja SearchErrorMessage
   - Implementacja AddingIndicator

4. **Implementacja komponentu wyświetlania wyników**:
   - Implementacja MovieSearchResultItem
   - Implementacja PosterImage

5. **Implementacja głównego ekranu**:
   - Utworzenie AddMovieScreen
   - Integracja wszystkich komponentów pomocniczych
   - Implementacja logiki stanu wyszukiwania

6. **Integracja z nawigacją**:
   - Dodanie trasy ADD_MOVIE do NavRoutes
   - Dodanie kompozycji do NavGraph
   - Aktualizacja JournalScreen, aby umożliwić nawigację do ekranu dodawania filmu

7. **Testowanie i debugowanie**:
   - Testowanie wyszukiwania z różnymi zapytaniami
   - Testowanie obsługi błędów
   - Testowanie dodawania filmów do dziennika

8. **Finalizacja i integracja z resztą aplikacji**:
   - Sprawdzenie kompatybilności ze stylami aplikacji
   - Finalne poprawki UI/UX 