# Plan implementacji widoku Rekomendacji

## 1. Przegląd
Widok Rekomendacji prezentuje użytkownikowi listę filmów rekomendowanych na podstawie jego preferencji i filmów dodanych do dziennika. Pozwala na przeglądanie, odświeżanie listy rekomendacji oraz przejście do szczegółów wybranego filmu. Jest to jeden z czterech głównych widoków dostępnych z poziomu dolnej nawigacji aplikacji.

## 2. Routing widoku
Ścieżka widoku: `/recommendations`

Ten widok jest jednym z głównych widoków aplikacji i będzie dostępny bezpośrednio z dolnej nawigacji.

## 3. Struktura komponentów
```
RecommendationsScreen
├── TopAppBar z tytułem "Rekomendacje"
└── LoadingStateHandler
    ├── (stan ładowania) CircularProgressIndicator
    ├── (stan błędu) ErrorView z przyciskiem ponowienia
    └── (stan sukcesu) RecommendationsList lub EmptyStateView
        └── MovieCard (wiele instancji)
```

## 4. Szczegóły komponentów
### RecommendationsScreen
- **Opis komponentu**: Główny ekran rekomendacji, zawierający wszystkie komponenty potrzebne do wyświetlania i odświeżania listy rekomendowanych filmów.
- **Główne elementy**: 
  - Scaffold z TopAppBar
  - LoadingStateHandler do obsługi różnych stanów UI
  - RecommendationsList lub EmptyStateView w zależności od stanu
- **Obsługiwane interakcje**:
  - Przejście do szczegółów filmu
  - Odświeżanie listy rekomendacji (pull-to-refresh)
- **Obsługiwana walidacja**: Sprawdzanie, czy użytkownik jest zalogowany
- **Typy**: RecommendationsViewModel, RecommendationsUiState
- **Propsy**: NavController (do nawigacji)

### RecommendationsList
- **Opis komponentu**: Lista rekomendowanych filmów w formie przewijalnej kolumny.
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
- **Opis komponentu**: Widok wyświetlany, gdy nie ma żadnych rekomendacji.
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
  - Stan sukcesu: właściwa zawartość (RecommendationsList lub EmptyStateView)
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

// Rekomendowany film z OpenRouter API
data class RecommendedMovieDTO(
    val tmdbId: String = "",             // Id filmu w TMDB
    val reason: String = ""              // Powód rekomendacji
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
    val id: String,                      // Id filmu w bazie danych (lub "recommendation_{tmdbId}" dla rekomendacji)
    val tmdbId: String,                  // Id filmu w TMDB
    val title: String,                   // Tytuł filmu
    val posterUrl: String?,              // URL plakatu filmu
    val year: String,                    // Rok produkcji
    val genre: String,                   // Główny gatunek filmu
    val reason: String?                  // Powód rekomendacji (tylko dla rekomendacji)
)

// Stan UI dla ekranu rekomendacji
data class RecommendationsUiState(
    val status: StateStatus = StateStatus.LOADING,  // Stan UI
    val errorMessage: String? = null,               // Komunikat błędu
    val recommendations: List<MovieViewModel> = emptyList(), // Lista rekomendacji
    val isRefreshing: Boolean = false               // Czy trwa odświeżanie
)
```

## 6. Zarządzanie stanem
Stan widoku Rekomendacji będzie zarządzany przez RecommendationsViewModel, który będzie odpowiedzialny za:
- Pobieranie listy filmów z dziennika użytkownika
- Generowanie rekomendacji na podstawie dziennika
- Pobieranie szczegółów rekomendowanych filmów z TMDB API
- Zarządzanie stanem UI (ładowanie, błąd, sukces)
- Odświeżanie listy rekomendacji

```kotlin
class RecommendationsViewModel(
    private val movieRepository: MovieRepository,
    private val tmdbRepository: TmdbRepository,
    private val recommendationsRepository: RecommendationsRepository
) : ViewModel() {

    // Stan UI jako StateFlow
    private val _uiState = MutableStateFlow(RecommendationsUiState())
    val uiState: StateFlow<RecommendationsUiState> = _uiState.asStateFlow()

    init {
        loadRecommendations()
    }

    // Ładowanie rekomendacji
    fun loadRecommendations() {
        _uiState.value = _uiState.value.copy(status = StateStatus.LOADING)

        viewModelScope.launch {
            try {
                // Pobieranie filmów z dziennika użytkownika
                val userMovies = movieRepository.getUserMovies()
                
                // Jeśli dziennik jest pusty, zwróć pusty stan
                if (userMovies.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        status = StateStatus.SUCCESS,
                        recommendations = emptyList()
                    )
                    return@launch
                }
                
                // Generowanie rekomendacji na podstawie dziennika
                val recommendedMovies = recommendationsRepository.getRecommendations(userMovies)
                
                // Pobieranie szczegółów rekomendowanych filmów z TMDB API
                val tmdbMovies = recommendedMovies.map { recommendation ->
                    val tmdbId = recommendation.tmdbId
                    tmdbRepository.getMovieDetails(tmdbId) to recommendation
                }
                
                // Przetwarzanie danych na model widoku
                val recommendationsViewModel = processRecommendationsData(tmdbMovies)
                
                _uiState.value = _uiState.value.copy(
                    status = StateStatus.SUCCESS,
                    recommendations = recommendationsViewModel
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    status = StateStatus.ERROR,
                    errorMessage = e.message ?: "Błąd pobierania rekomendacji"
                )
            }
        }
    }

    // Odświeżanie rekomendacji
    fun refreshRecommendations() {
        if (_uiState.value.isRefreshing) return
        
        _uiState.value = _uiState.value.copy(isRefreshing = true)
        
        viewModelScope.launch {
            try {
                // Podobny kod jak w loadRecommendations, ale z isRefreshing = true
                // ...
                
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    // Aktualizacja pozostałych pól
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    errorMessage = e.message ?: "Błąd odświeżania rekomendacji"
                )
            }
        }
    }

    // Przetwarzanie danych z DTO na ViewModel
    private fun processRecommendationsData(
        tmdbMovies: List<Pair<TmdbMovieDTO, RecommendedMovieDTO>>
    ): List<MovieViewModel> {
        // Implementacja przetwarzania danych
        // ...
    }
}
```

## 7. Integracja API
Widok będzie korzystać z trzech głównych źródeł danych:

### Firebase Realtime Database
- Pobieranie listy filmów użytkownika
- Zapytanie: `movieRepository.getUserMovies()`
- Typ odpowiedzi: `List<MovieDTO>`

### OpenRouter API (przez RecommendationsRepository)
- Generowanie rekomendacji na podstawie filmów użytkownika
- Zapytanie: `recommendationsRepository.getRecommendations(userMovies)`
- Parametry: 
  - userMovies: List<MovieDTO> - lista filmów z dziennika użytkownika
- Typ odpowiedzi: `List<RecommendedMovieDTO>`

### TMDB API
- Pobieranie szczegółów rekomendowanych filmów
- Zapytanie: `tmdbRepository.getMovieDetails(tmdbId)`
- Parametry:
  - tmdbId: String - identyfikator filmu w TMDB
- Typ odpowiedzi: `TmdbMovieDTO`

## 8. Interakcje użytkownika
### Przewijanie listy rekomendacji
- Użytkownik może przewijać listę rekomendowanych filmów w górę i w dół
- Implementacja za pomocą LazyColumn w RecommendationsList

### Odświeżanie listy rekomendacji
- Użytkownik może przeciągnąć listę w dół, aby ją odświeżyć
- Gest pull-to-refresh wywołuje `viewModel.refreshRecommendations()`
- PullRefreshIndicator pokazuje animację podczas odświeżania

### Przejście do szczegółów filmu
- Użytkownik może kliknąć na kartę filmu, aby przejść do widoku szczegółów
- Kliknięcie wywołuje nawigację do `/movie/{movieId}`
- Parametr `movieId` w tym przypadku będzie musiał zostać wygenerowany, ponieważ rekomendowane filmy nie mają swojego ID w bazie danych (są tylko rekomendacjami)

## 9. Warunki i walidacja
- **Autentykacja użytkownika**:
  - Widok jest dostępny tylko dla zalogowanych użytkowników
  - Jeśli użytkownik nie jest zalogowany, zostanie przekierowany do ekranu logowania
  - Implementacja w nawigacji głównej

- **Wymagania dotyczące generowania rekomendacji**:
  - Użytkownik musi mieć przynajmniej jeden film w dzienniku, aby otrzymać rekomendacje
  - Jeśli dziennik jest pusty, widok wyświetli EmptyStateView z odpowiednim komunikatem

## 10. Obsługa błędów
- **Brak połączenia internetowego**:
  - Wyświetlenie komunikatu "Brak połączenia z internetem"
  - Przycisk "Spróbuj ponownie" wywołujący `viewModel.loadRecommendations()`

- **Błąd Firebase**:
  - Wyświetlenie komunikatu "Nie udało się pobrać danych z dziennika filmowego"
  - Przycisk "Spróbuj ponownie" wywołujący `viewModel.loadRecommendations()`

- **Błąd generowania rekomendacji**:
  - Wyświetlenie komunikatu "Nie udało się wygenerować rekomendacji"
  - Przycisk "Spróbuj ponownie" wywołujący `viewModel.loadRecommendations()`

- **Błąd TMDB API**:
  - Wyświetlenie częściowych danych rekomendacji (bez szczegółów z TMDB)
  - Komunikat o błędzie pobierania szczegółów filmów

- **Pusty dziennik filmowy**:
  - Wyświetlenie EmptyStateView z komunikatem "Dodaj filmy do dziennika, aby otrzymać rekomendacje"
  - Przycisk "Dodaj film" przekierowujący do widoku dodawania filmu

## 11. Kroki implementacji
1. **Utworzenie szkieletu widoku**:
   - Utworzenie głównego komponentu RecommendationsScreen
   - Implementacja podstawowego layoutu z Scaffold i TopAppBar

2. **Implementacja komponentów pomocniczych**:
   - Implementacja LoadingStateHandler
   - Implementacja EmptyStateView

3. **Implementacja listy rekomendacji**:
   - Utworzenie komponentu RecommendationsList
   - Implementacja MovieCard (lub ponowne użycie z Dziennika Filmowego)
   - Dodanie obsługi pull-to-refresh

4. **Implementacja ViewModel**:
   - Utworzenie RecommendationsViewModel
   - Implementacja metod do pobierania i przetwarzania danych
   - Implementacja logic generowania rekomendacji

5. **Integracja z Firebase**:
   - Implementacja pobierania danych z Firebase Realtime Database

6. **Integracja z OpenRouter API**:
   - Implementacja RecommendationsRepository
   - Implementacja generowania rekomendacji

7. **Integracja z TMDB API**:
   - Implementacja pobierania szczegółów filmów z TMDB API
   - Łączenie danych rekomendacji z danymi TMDB

8. **Implementacja nawigacji**:
   - Dodanie obsługi kliknięcia na film i przekierowania do szczegółów
   - Integracja z głównym systemem nawigacji

9. **Obsługa błędów i stanów brzegowych**:
   - Implementacja obsługi błędów połączenia i API
   - Obsługa przypadku pustego dziennika filmowego

10. **Testowanie i debugowanie**:
    - Testowanie generowania rekomendacji
    - Testowanie interakcji użytkownika
    - Testowanie obsługi błędów

11. **Finalizacja i integracja z resztą aplikacji**:
    - Połączenie z dolną nawigacją
    - Finalne poprawki UI/UX 