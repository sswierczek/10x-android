# Plan implementacji widoku Dziennik Filmowy

## 1. Przegląd
Widok Dziennik Filmowy jest głównym ekranem aplikacji MovieMind, który wyświetla listę filmów dodanych przez użytkownika. Pozwala na przeglądanie, sortowanie i odświeżanie listy filmów oraz przejście do szczegółów wybranego filmu. Jest to jeden z czterech głównych widoków dostępnych z poziomu dolnej nawigacji aplikacji.

## 2. Routing widoku
Ścieżka widoku: `/journal`

Ten widok jest jednym z głównych widoków aplikacji i będzie dostępny bezpośrednio z dolnej nawigacji. Po pomyślnym zalogowaniu użytkownik zostanie automatycznie przekierowany do tego widoku.

## 3. Struktura komponentów
```
JournalScreen
├── TopAppBar z tytułem "Dziennik"
├── SortingControl
└── LoadingStateHandler
    ├── (stan ładowania) CircularProgressIndicator
    ├── (stan błędu) ErrorView z przyciskiem ponowienia
    └── (stan sukcesu) MoviesList lub EmptyStateView
        └── MovieCard (wiele instancji)
```

## 4. Szczegóły komponentów
### JournalScreen
- **Opis komponentu**: Główny ekran dziennika filmowego, zawierający wszystkie komponenty potrzebne do wyświetlania, sortowania i odświeżania listy filmów użytkownika.
- **Główne elementy**: 
  - Scaffold z TopAppBar
  - SortingControl do zarządzania sortowaniem
  - LoadingStateHandler do obsługi różnych stanów UI
  - MoviesList lub EmptyStateView w zależności od stanu
- **Obsługiwane interakcje**:
  - Przejście do szczegółów filmu
  - Zmiana sortowania
  - Odświeżanie listy filmów (pull-to-refresh)
- **Obsługiwana walidacja**: Sprawdzanie, czy użytkownik jest zalogowany
- **Typy**: JournalViewModel, JournalUiState
- **Propsy**: NavController (do nawigacji)

### SortingControl
- **Opis komponentu**: Komponent umożliwiający zmianę sposobu sortowania listy filmów.
- **Główne elementy**:
  - IconButton z ikoną sortowania
  - Wskaźnik aktualnego trybu sortowania
- **Obsługiwane interakcje**: Kliknięcie przycisku w celu zmiany trybu sortowania
- **Obsługiwana walidacja**: Brak
- **Typy**: SortOrder
- **Propsy**:
  - currentSortOrder: SortOrder - aktualny tryb sortowania
  - onSortOrderChanged: (SortOrder) -> Unit - funkcja wywoływana przy zmianie trybu sortowania

### MoviesList
- **Opis komponentu**: Lista filmów w formie przewijalnej kolumny.
- **Główne elementy**:
  - LazyColumn z elementami MovieCard
  - PullRefreshIndicator do odświeżania
- **Obsługiwane interakcje**:
  - Przewijanie listy
  - Odświeżanie listy (pull-to-refresh)
  - Kliknięcie na film
- **Obsługiwana walidacja**: Brak
- **Typy**: List<MovieViewModel>
- **Propsy**:
  - movies: List<MovieViewModel> - lista filmów do wyświetlenia
  - isRefreshing: Boolean - czy trwa odświeżanie
  - onRefresh: () -> Unit - funkcja wywoływana przy odświeżaniu
  - onMovieClick: (String) -> Unit - funkcja wywoływana po kliknięciu na film

### MovieCard
- **Opis komponentu**: Karta przedstawiająca podstawowe informacje o filmie.
- **Główne elementy**:
  - Card z Row zawierającym:
    - AsyncImage dla plakatu filmu
    - Column z tekstem (tytuł, rok, gatunek)
- **Obsługiwane interakcje**: Kliknięcie na kartę
- **Obsługiwana walidacja**: Brak
- **Typy**: MovieViewModel
- **Propsy**:
  - movie: MovieViewModel - dane filmu do wyświetlenia
  - onClick: () -> Unit - funkcja wywoływana po kliknięciu na kartę

### EmptyStateView
- **Opis komponentu**: Widok wyświetlany, gdy użytkownik nie ma żadnych filmów w dzienniku.
- **Główne elementy**:
  - Column z:
    - Icon lub ilustracją
    - Text z komunikatem
    - Opcjonalnie Button z akcją
- **Obsługiwane interakcje**: Opcjonalnie kliknięcie przycisku akcji
- **Obsługiwana walidacja**: Brak
- **Typy**: Brak
- **Propsy**:
  - message: String - komunikat do wyświetlenia
  - actionLabel: String? - opcjonalna etykieta przycisku akcji
  - onActionClick: (() -> Unit)? - opcjonalna funkcja wywoływana po kliknięciu przycisku akcji

### LoadingStateHandler
- **Opis komponentu**: Komponent zarządzający różnymi stanami UI (ładowanie, błąd, sukces).
- **Główne elementy**: Różne w zależności od stanu:
  - Stan ładowania: CircularProgressIndicator
  - Stan błędu: Column z komunikatem i przyciskiem ponowienia
  - Stan sukcesu: właściwa zawartość (MoviesList lub EmptyStateView)
- **Obsługiwane interakcje**: Kliknięcie przycisku ponowienia w przypadku błędu
- **Obsługiwana walidacja**: Brak
- **Typy**: Enum StateStatus (LOADING, ERROR, SUCCESS)
- **Propsy**:
  - stateStatus: StateStatus - aktualny stan
  - errorMessage: String? - opcjonalny komunikat błędu
  - onRetry: () -> Unit - funkcja wywoływana po kliknięciu przycisku ponowienia
  - content: @Composable () -> Unit - zawartość wyświetlana w stanie sukcesu

## 5. Typy
### Enumeracje
```kotlin
enum class SortOrder {
    DATE_ADDED_ASC,    // Sortowanie po dacie dodania (rosnąco)
    DATE_ADDED_DESC    // Sortowanie po dacie dodania (malejąco)
}

enum class StateStatus {
    LOADING,   // Trwa ładowanie danych
    ERROR,     // Wystąpił błąd
    SUCCESS    // Dane załadowane pomyślnie
}
```

### DTO
```kotlin
// Dane filmu z Firebase Realtime Database
data class MovieDTO(
    val id: String = "",                 // Id filmu w bazie danych
    val tmdbId: String = "",             // Id filmu w TMDB
    val addedAt: Long = 0,               // Timestamp dodania filmu
    val userId: String = ""              // Id użytkownika, który dodał film
)

// Dane filmu z TMDB API
data class TmdbMovieDTO(
    val id: Int = 0,                     // Id filmu w TMDB
    val title: String = "",              // Tytuł filmu
    val posterPath: String? = null,      // Ścieżka do plakatu filmu
    val releaseDate: String = "",        // Data premiery filmu
    val genres: List<GenreDTO> = emptyList() // Gatunki filmu
)

data class GenreDTO(
    val id: Int = 0,                     // Id gatunku
    val name: String = ""                // Nazwa gatunku
)
```

### ViewModels
```kotlin
// Model widoku filmu
data class MovieViewModel(
    val id: String,                      // Id filmu w bazie danych
    val tmdbId: String,                  // Id filmu w TMDB
    val title: String,                   // Tytuł filmu
    val posterUrl: String?,              // URL plakatu filmu
    val year: String,                    // Rok produkcji
    val genre: String,                   // Główny gatunek filmu
    val addedAt: Long,                   // Timestamp dodania filmu
    val addedAtFormatted: String         // Sformatowana data dodania filmu
)

// Stan UI dla ekranu dziennika
data class JournalUiState(
    val status: StateStatus = StateStatus.LOADING,  // Stan UI
    val errorMessage: String? = null,               // Komunikat błędu
    val movies: List<MovieViewModel> = emptyList(), // Lista filmów
    val sortOrder: SortOrder = SortOrder.DATE_ADDED_DESC, // Porządek sortowania
    val isRefreshing: Boolean = false               // Czy trwa odświeżanie
)
```

## 6. Zarządzanie stanem
Stan widoku Dziennik Filmowy będzie zarządzany przez JournalViewModel, który będzie odpowiedzialny za:
- Pobieranie listy filmów z Firebase Realtime Database
- Pobieranie szczegółów filmów z TMDB API
- Zarządzanie stanem UI (ładowanie, błąd, sukces)
- Sortowanie filmów według określonego kryterium
- Odświeżanie listy filmów

```kotlin
class JournalViewModel(
    private val movieRepository: MovieRepository,
    private val tmdbRepository: TmdbRepository
) : ViewModel() {

    // Stan UI jako StateFlow
    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState: StateFlow<JournalUiState> = _uiState.asStateFlow()

    init {
        loadMovies()
    }

    // Ładowanie filmów
    fun loadMovies() {
        // Implementacja
    }

    // Przełączanie trybu sortowania
    fun toggleSortOrder() {
        // Implementacja
    }

    // Odświeżanie listy filmów
    fun refreshMovies() {
        // Implementacja
    }

    // Przetwarzanie danych z DTO na ViewModel
    private fun processMoviesData(movies: List<MovieDTO>, tmdbMovies: Map<String, TmdbMovieDTO>): List<MovieViewModel> {
        // Implementacja
    }
}
```

## 7. Integracja API
Widok będzie korzystać z dwóch głównych źródeł danych:

### Firebase Realtime Database
- Pobieranie listy filmów użytkownika
- Zapytanie: `movieRepository.getUserMovies(userId)`
- Typ odpowiedzi: `Flow<List<MovieDTO>>`
- Dostęp do danych w czasie rzeczywistym przez nasłuchiwanie zmian

### TMDB API
- Pobieranie szczegółów filmów
- Zapytanie: `tmdbRepository.getMovieDetails(tmdbId)`
- Typ odpowiedzi: `TmdbMovieDTO`
- Dane będą pobierane równolegle dla wszystkich filmów z listy

## 8. Interakcje użytkownika
### Przewijanie listy filmów
- Użytkownik może przewijać listę filmów w górę i w dół
- Implementacja za pomocą LazyColumn w MoviesList

### Sortowanie filmów
- Użytkownik może kliknąć przycisk sortowania, aby zmienić kolejność sortowania
- Kliknięcie przycisku wywołuje `viewModel.toggleSortOrder()`
- Lista automatycznie aktualizuje się po zmianie trybu sortowania

### Odświeżanie listy filmów
- Użytkownik może przeciągnąć listę w dół, aby ją odświeżyć
- Gest pull-to-refresh wywołuje `viewModel.refreshMovies()`
- PullRefreshIndicator pokazuje animację podczas odświeżania

### Przejście do szczegółów filmu
- Użytkownik może kliknąć na kartę filmu, aby przejść do widoku szczegółów
- Kliknięcie wywołuje nawigację do `/movie/{movieId}`

## 9. Warunki i walidacja
- **Autentykacja użytkownika**:
  - Widok jest dostępny tylko dla zalogowanych użytkowników
  - Jeśli użytkownik nie jest zalogowany, zostanie przekierowany do ekranu logowania
  - Implementacja w nawigacji głównej

- **Dostęp do danych**:
  - Użytkownik ma dostęp tylko do swoich filmów
  - Implementacja na poziomie Firebase Security Rules

## 10. Obsługa błędów
- **Brak połączenia internetowego**:
  - Wyświetlenie komunikatu "Brak połączenia z internetem"
  - Przycisk "Spróbuj ponownie" wywołujący `viewModel.loadMovies()`

- **Błąd Firebase**:
  - Wyświetlenie komunikatu "Nie udało się pobrać listy filmów"
  - Przycisk "Spróbuj ponownie" wywołujący `viewModel.loadMovies()`

- **Błąd TMDB API**:
  - Wyświetlenie podstawowych danych z Firebase (bez szczegółów z TMDB)
  - Komunikat o błędzie pobierania szczegółów filmów

- **Pusty dziennik filmowy**:
  - Wyświetlenie EmptyStateView z komunikatem "Twój dziennik filmowy jest pusty"
  - Przycisk "Dodaj pierwszy film" przekierowujący do widoku dodawania filmu

## 11. Kroki implementacji
1. **Utworzenie szkieletu widoku**:
   - Utworzenie głównego komponentu JournalScreen
   - Implementacja podstawowego layoutu z Scaffold i TopAppBar

2. **Implementacja komponentów pomocniczych**:
   - Implementacja LoadingStateHandler
   - Implementacja EmptyStateView
   - Implementacja SortingControl

3. **Implementacja listy filmów**:
   - Utworzenie komponentu MoviesList
   - Implementacja MovieCard
   - Dodanie obsługi pull-to-refresh

4. **Implementacja ViewModel**:
   - Utworzenie JournalViewModel
   - Implementacja metod do pobierania, przetwarzania i sortowania danych

5. **Integracja z Firebase**:
   - Implementacja pobierania danych z Firebase Realtime Database
   - Nasłuchiwanie zmian w czasie rzeczywistym

6. **Integracja z TMDB API**:
   - Implementacja pobierania szczegółów filmów z TMDB API
   - Łączenie danych z Firebase i TMDB

7. **Implementacja nawigacji**:
   - Dodanie obsługi kliknięcia na film i przekierowania do szczegółów
   - Integracja z głównym systemem nawigacji

8. **Obsługa błędów i stanów brzegowych**:
   - Implementacja obsługi błędów połączenia i API
   - Obsługa pustego stanu dziennika

9. **Testowanie i debugowanie**:
   - Testowanie wszystkich interakcji użytkownika
   - Debugowanie potencjalnych problemów z wydajnością

10. **Finalizacja i integracja z resztą aplikacji**:
    - Połączenie z dolną nawigacją
    - Finalne poprawki UI/UX 