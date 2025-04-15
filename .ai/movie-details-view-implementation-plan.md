# Plan implementacji widoku Szczegółów Filmu

## 1. Przegląd
Widok Szczegółów Filmu prezentuje szczegółowe informacje o wybranym filmie z dziennika użytkownika. Zawiera pełne dane o filmie, w tym tytuł, reżysera, rok produkcji, gatunki, ocenę, notatkę oraz datę obejrzenia. Użytkownik może edytować wpis, usunąć go lub powrócić do widoku dziennika. Widok oferuje również opcję udostępniania informacji o filmie.

## 2. Routing widoku
Ścieżka widoku: `/movie-details/{movieId}`

Ten widok jest dostępny przez:
- Kliknięcie na pozycję filmu w Dzienniku Filmowym
- Bezpośredni dostęp przez URL z identyfikatorem filmu

## 3. Struktura komponentów
```
MovieDetailsScreen
├── TopAppBar
│   ├── BackButton
│   ├── MovieTitle
│   ├── ShareButton
│   └── EditButton
├── MovieInfoSection
│   ├── MoviePoster
│   ├── MovieBasicInfo
│   │   ├── Title
│   │   ├── Director
│   │   ├── ReleaseYear
│   │   └── Genres
│   ├── RatingSection
│   │   ├── RatingStars
│   │   └── RatingValue
│   └── WatchDateInfo
├── MovieNotesSection
│   └── NotesText
├── ActionsSection
│   ├── EditButton (alternatywny, jeśli brak w TopAppBar)
│   └── DeleteButton
└── LoadingIndicator
```

## 4. Szczegóły komponentów
### MovieDetailsScreen
- **Opis komponentu**: Główny kontener dla widoku szczegółów filmu, organizujący wszystkie sekcje informacji.
- **Główne elementy**:
  - Scaffold z:
    - TopAppBar z przyciskami nawigacji i akcji
    - Column z sekcjami informacji o filmie
    - LoadingIndicator (widoczny podczas ładowania danych)
- **Obsługiwane interakcje**:
  - Nawigacja powrotna do dziennika
  - Przejście do edycji filmu
  - Usunięcie wpisu o filmie
  - Udostępnianie informacji o filmie
- **Obsługiwana walidacja**: Brak
- **Typy**: MovieDetailsViewModel, MovieDetailsUiState
- **Propsy**: NavController, movieId: String

### TopAppBar
- **Opis komponentu**: Górny pasek aplikacji z tytułem filmu i przyciskami akcji.
- **Główne elementy**:
  - CenterAlignedTopAppBar z:
    - NavigationIcon (BackButton) 
    - Title (MovieTitle)
    - Actions (ShareButton, EditButton)
- **Obsługiwane interakcje**:
  - Powrót do poprzedniego ekranu
  - Udostępnianie filmu
  - Przejście do edycji filmu
- **Obsługiwana walidacja**: Brak
- **Typy**: Brak specyficznych
- **Propsy**:
  - title: String
  - onBackClick: () -> Unit
  - onShareClick: () -> Unit
  - onEditClick: () -> Unit

### MovieInfoSection
- **Opis komponentu**: Sekcja zawierająca podstawowe informacje o filmie, w tym plakat, dane podstawowe i ocenę.
- **Główne elementy**:
  - Card z:
    - Row zawierający MoviePoster i Column z MovieBasicInfo
    - RatingSection
    - WatchDateInfo
- **Obsługiwane interakcje**: Brak bezpośrednich interakcji
- **Obsługiwana walidacja**: Brak
- **Typy**: MovieEntry
- **Propsy**: movieEntry: MovieEntry

### MoviePoster
- **Opis komponentu**: Komponent wyświetlający plakat filmu.
- **Główne elementy**:
  - AsyncImage lub Placeholder z obrazem plakatu filmu
- **Obsługiwane interakcje**: Brak
- **Obsługiwana walidacja**: Brak
- **Typy**: Brak specyficznych
- **Propsy**:
  - posterUrl: String?
  - contentDescription: String

### MovieBasicInfo
- **Opis komponentu**: Sekcja z podstawowymi informacjami o filmie.
- **Główne elementy**:
  - Column z:
    - Title (tytuł filmu, większa czcionka)
    - Director (reżyser)
    - ReleaseYear (rok produkcji)
    - Genres (gatunki jako Row z Chip dla każdego gatunku)
- **Obsługiwane interakcje**: Brak
- **Obsługiwana walidacja**: Brak
- **Typy**: Brak specyficznych
- **Propsy**:
  - title: String
  - director: String
  - releaseYear: Int
  - genres: List<String>

### RatingSection
- **Opis komponentu**: Sekcja wyświetlająca ocenę filmu.
- **Główne elementy**:
  - Row z:
    - RatingStars (wizualna reprezentacja oceny)
    - RatingValue (numeryczna wartość oceny)
- **Obsługiwane interakcje**: Brak (tylko wyświetlanie)
- **Obsługiwana walidacja**: Brak
- **Typy**: Brak specyficznych
- **Propsy**:
  - rating: Float
  - maxRating: Int (domyślnie 5)

### RatingStars
- **Opis komponentu**: Wizualna reprezentacja oceny w formie gwiazdek.
- **Główne elementy**:
  - Row z IconButton (gwiazdki) dla każdej pozycji oceny
- **Obsługiwane interakcje**: Brak (tylko wyświetlanie)
- **Obsługiwana walidacja**: Brak
- **Typy**: Brak specyficznych
- **Propsy**:
  - rating: Float
  - maxRating: Int (domyślnie 5)

### WatchDateInfo
- **Opis komponentu**: Informacja o dacie obejrzenia filmu.
- **Główne elementy**:
  - Row z:
    - Icon (kalendarz)
    - Text z sformatowaną datą obejrzenia
- **Obsługiwane interakcje**: Brak
- **Obsługiwana walidacja**: Brak
- **Typy**: Brak specyficznych
- **Propsy**:
  - watchDate: LocalDate

### MovieNotesSection
- **Opis komponentu**: Sekcja zawierająca notatkę użytkownika o filmie.
- **Główne elementy**:
  - Card z:
    - Title "Notatki"
    - NotesText (treść notatki)
- **Obsługiwane interakcje**: Brak
- **Obsługiwana walidacja**: Brak
- **Typy**: Brak specyficznych
- **Propsy**:
  - notes: String?

### ActionsSection
- **Opis komponentu**: Sekcja z przyciskami akcji dla wpisu o filmie.
- **Główne elementy**:
  - Row z:
    - EditButton (jeśli nie jest w TopAppBar)
    - Spacer
    - DeleteButton
- **Obsługiwane interakcje**:
  - Przejście do edycji filmu
  - Usunięcie wpisu o filmie (z dialogiem potwierdzenia)
- **Obsługiwana walidacja**: Brak
- **Typy**: Brak specyficznych
- **Propsy**:
  - onEditClick: () -> Unit
  - onDeleteClick: () -> Unit

### DeleteButton
- **Opis komponentu**: Przycisk usuwania wpisu o filmie.
- **Główne elementy**:
  - Button (w stylu destrukcyjnym, czerwony) z:
    - Icon (ikona kosza)
    - Text "Usuń"
  - AlertDialog (potwierdzenie usunięcia)
- **Obsługiwane interakcje**:
  - Kliknięcie przycisku (wyświetla dialog potwierdzenia)
  - Potwierdzenie usunięcia
  - Anulowanie usunięcia
- **Obsługiwana walidacja**: Brak
- **Typy**: Brak specyficznych
- **Propsy**:
  - onDeleteConfirmed: () -> Unit

### LoadingIndicator
- **Opis komponentu**: Wskaźnik ładowania wyświetlany podczas pobierania danych filmu.
- **Główne elementy**:
  - CircularProgressIndicator w Box na całym ekranie
- **Obsługiwane interakcje**: Brak
- **Obsługiwana walidacja**: Brak
- **Typy**: Brak specyficznych
- **Propsy**:
  - isVisible: Boolean

## 5. Typy
### DTO
```kotlin
// Dane wejściowe dla widoku szczegółów filmu
data class MovieDetailsParams(
    val movieId: String    // ID filmu w bazie danych
)

// Pełne dane filmu
data class MovieEntry(
    val id: String,            // Identyfikator filmu w bazie
    val title: String,         // Tytuł filmu
    val director: String,      // Reżyser
    val releaseYear: Int,      // Rok produkcji
    val genres: List<String>,  // Lista gatunków
    val rating: Float,         // Ocena użytkownika (0-5)
    val watchDate: LocalDate,  // Data obejrzenia
    val notes: String?,        // Notatka użytkownika (opcjonalna)
    val posterUrl: String?     // URL do plakatu filmu (opcjonalny)
)
```

### ViewModels
```kotlin
// Stan UI dla ekranu szczegółów filmu
data class MovieDetailsUiState(
    val isLoading: Boolean = true,          // Czy trwa ładowanie danych
    val movieEntry: MovieEntry? = null,      // Dane filmu (null jeśli jeszcze nie załadowane)
    val isDeleteDialogVisible: Boolean = false, // Czy dialog potwierdzenia usunięcia jest widoczny
    val errorMessage: String? = null,        // Komunikat błędu (jeśli wystąpił)
    val isDeleted: Boolean = false           // Czy film został usunięty (do nawigacji powrotnej)
)
```

## 6. Zarządzanie stanem
Stan widoku Szczegółów Filmu będzie zarządzany przez MovieDetailsViewModel, który będzie odpowiedzialny za:
- Pobieranie danych filmu z repozytorium
- Przygotowanie i formatowanie danych do wyświetlenia
- Obsługę akcji użytkownika (edycja, usuwanie, udostępnianie)
- Zarządzanie stanem UI (ładowanie, błędy)

```kotlin
class MovieDetailsViewModel(
    private val movieRepository: MovieRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val movieId: String = checkNotNull(savedStateHandle["movieId"])
    
    // Stan UI jako StateFlow
    private val _uiState = MutableStateFlow(MovieDetailsUiState())
    val uiState: StateFlow<MovieDetailsUiState> = _uiState.asStateFlow()
    
    init {
        loadMovieDetails()
    }
    
    // Ładowanie szczegółów filmu
    private fun loadMovieDetails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            try {
                val movie = movieRepository.getMovieById(movieId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    movieEntry = movie
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Nie udało się załadować danych filmu"
                )
            }
        }
    }
    
    // Pokazywanie dialogu usuwania
    fun showDeleteDialog() {
        _uiState.value = _uiState.value.copy(
            isDeleteDialogVisible = true
        )
    }
    
    // Ukrywanie dialogu usuwania
    fun hideDeleteDialog() {
        _uiState.value = _uiState.value.copy(
            isDeleteDialogVisible = false
        )
    }
    
    // Usuwanie filmu
    fun deleteMovie() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                isDeleteDialogVisible = false
            )
            
            try {
                movieRepository.deleteMovie(movieId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isDeleted = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Nie udało się usunąć filmu"
                )
            }
        }
    }
    
    // Przygotowanie tekstu do udostępniania
    fun prepareShareText(): String {
        val movie = _uiState.value.movieEntry ?: return ""
        return buildString {
            append("Film: ${movie.title}\n")
            append("Reżyser: ${movie.director}\n")
            append("Rok: ${movie.releaseYear}\n")
            append("Ocena: ${movie.rating}/5\n")
            if (movie.notes != null && movie.notes.isNotBlank()) {
                append("Moje przemyślenia: ${movie.notes}\n")
            }
            append("Obejrzane: ${movie.watchDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}")
        }
    }
    
    // Czyszczenie komunikatu błędu
    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }
}
```

## 7. Integracja API
Widok będzie korzystać z MovieRepository do pobierania i zarządzania danymi filmu:

### MovieRepository
- Pobieranie szczegółów filmu:
  - Zapytanie: `movieRepository.getMovieById(movieId)`
  - Parametry: movieId: String (identyfikator filmu)
  - Typ odpowiedzi: MovieEntry (Success) lub wyjątek (Failure)

- Usuwanie filmu:
  - Zapytanie: `movieRepository.deleteMovie(movieId)`
  - Parametry: movieId: String (identyfikator filmu do usunięcia)
  - Typ odpowiedzi: Brak (Success) lub wyjątek (Failure)

## 8. Interakcje użytkownika
### Widok szczegółów
- Użytkownik otwiera szczegóły filmu:
  - Pobranie danych filmu na podstawie movieId
  - Wyświetlenie wszystkich informacji o filmie
  - W przypadku błędu, wyświetlenie komunikatu błędu

### Edycja filmu
- Użytkownik klika przycisk "Edytuj":
  - Nawigacja do widoku edycji filmu z przekazaniem movieId
  - Ścieżka: `/edit-movie/{movieId}`

### Usuwanie filmu
- Użytkownik klika przycisk "Usuń":
  - Wyświetlenie dialogu potwierdzenia usunięcia
  - Jeśli użytkownik potwierdzi, wywołanie metody `viewModel.deleteMovie()`
  - Po udanym usunięciu, nawigacja powrotna do Dziennika Filmowego
  - W przypadku błędu, wyświetlenie komunikatu błędu

### Udostępnianie
- Użytkownik klika przycisk "Udostępnij":
  - Przygotowanie tekstu do udostępnienia za pomocą `viewModel.prepareShareText()`
  - Wywołanie systemowego dialogu udostępniania

### Nawigacja
- Użytkownik klika przycisk "Powrót":
  - Powrót do widoku Dziennika Filmowego

## 9. Obsługa błędów
- **Film nie istnieje**:
  - Wyświetlenie komunikatu "Film nie został znaleziony"
  - Opcja powrotu do Dziennika Filmowego

- **Błąd podczas pobierania danych**:
  - Wyświetlenie komunikatu "Nie udało się załadować danych filmu"
  - Opcja ponowienia próby

- **Błąd podczas usuwania filmu**:
  - Wyświetlenie komunikatu "Nie udało się usunąć filmu"
  - Opcja ponowienia próby

- **Brak połączenia z internetem**:
  - Wyświetlenie komunikatu "Brak połączenia z internetem"
  - Opcja ponowienia próby po przywróceniu połączenia

## 10. Kroki implementacji
1. **Utworzenie modeli danych**:
   - Definicja MovieDetailsParams
   - Definicja MovieDetailsUiState

2. **Implementacja MovieDetailsViewModel**:
   - Utworzenie klasy viewmodelu
   - Implementacja metody ładowania danych filmu
   - Implementacja logiki usuwania filmu
   - Implementacja przygotowania tekstu do udostępniania

3. **Implementacja komponentów UI**:
   - Utworzenie MovieDetailsScreen jako głównego komponentu
   - Implementacja TopAppBar z przyciskami
   - Implementacja MovieInfoSection z MoviePoster i MovieBasicInfo
   - Implementacja RatingSection
   - Implementacja MovieNotesSection
   - Implementacja ActionsSection z przyciskami akcji
   - Implementacja LoadingIndicator
   - Implementacja dialogu potwierdzenia usunięcia

4. **Integracja z nawigacją**:
   - Dodanie ekranu szczegółów filmu do grafu nawigacji
   - Konfiguracja przekazywania parametru movieId
   - Implementacja nawigacji do/z ekranu szczegółów

5. **Integracja z MovieRepository**:
   - Implementacja pobierania danych filmu
   - Implementacja usuwania filmu

6. **Implementacja funkcji udostępniania**:
   - Utworzenie funkcji przygotowującej tekst do udostępnienia
   - Integracja z systemowym dialogiem udostępniania

7. **Implementacja obsługi błędów**:
   - Wyświetlanie odpowiednich komunikatów błędów
   - Implementacja mechanizmu ponownego ładowania danych

8. **Testowanie**:
   - Testowanie pobierania danych filmu
   - Testowanie usuwania filmu
   - Testowanie udostępniania
   - Testowanie obsługi błędów
   - Testowanie nawigacji

9. **Optymalizacje UI/UX**:
   - Dodanie animacji podczas ładowania danych
   - Implementacja przejść między ekranami
   - Dostosowanie layoutu do różnych rozmiarów ekranów 