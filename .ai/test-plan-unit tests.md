# Plan testów jednostkowych dla aplikacji 10x-android

## 1. Warstwa ViewModel

### 1.1. JournalViewModel

- **Testy metody `loadUserMovies`**
  - Sprawdzenie, czy prawidłowo pobiera filmy użytkownika
  - Weryfikacja obsługi różnych stanów (ładowanie, sukces, błąd)
  - Sprawdzenie sortowania filmów według daty

- **Testy dla metody `addMovieToJournal`**
  - Weryfikacja sukcesu dodawania filmu
  - Sprawdzenie obsługi błędów
  - Test aktualizacji stanu UI po dodaniu filmu

### 1.2. RecommendationsViewModel

- **Testy metody `loadRecommendations`**
  - Sprawdzenie poprawnego pobierania rekomendacji
  - Weryfikacja obsługi pustej listy rekomendacji
  - Sprawdzenie obsługi błędów
  - Weryfikacja poprawnej transformacji danych z DTO do modelu widoku

- **Testy metody `refreshRecommendations`**
  - Sprawdzenie, czy odświeżanie działa poprawnie
  - Weryfikacja obsługi stanu ładowania podczas odświeżania
  - Sprawdzenie, czy zachowuje poprzednie dane podczas ładowania

- **Testy metody `addToJournal`**
  - Weryfikacja poprawnego dodania rekomendacji do dziennika
  - Sprawdzenie, czy stan UI jest aktualizowany po dodaniu do dziennika

### 1.3. MovieDetailsViewModel

- **Testy metody `loadMovieDetails`**
  - Sprawdzenie poprawnego ładowania szczegółów filmu
  - Weryfikacja obsługi różnych stanów (ładowanie, sukces, błąd)
  
- **Testy metody `updateRating`**
  - Weryfikacja poprawnej aktualizacji oceny filmu
  - Sprawdzenie, czy stan ładowania jest prawidłowo zarządzany
  - Weryfikacja obsługi błędów podczas aktualizacji

### 1.4. RegisterViewModel

- **Testy walidacji danych wejściowych**
  - Weryfikacja walidacji adresu email
  - Sprawdzenie walidacji hasła
  - Sprawdzenie walidacji potwierdzenia hasła
  - Test walidacji akceptacji regulaminu
  
- **Testy metody `register`**
  - Sprawdzenie poprawnej rejestracji użytkownika
  - Weryfikacja obsługi błędów (np. email już istnieje)
  - Sprawdzenie, czy stan UI jest aktualizowany odpowiednio

### 1.5. LoginViewModel

- **Testy walidacji danych logowania**
  - Weryfikacja walidacji adresu email
  - Sprawdzenie walidacji hasła
  
- **Testy metody `login`**
  - Sprawdzenie poprawnego logowania
  - Weryfikacja obsługi błędów (np. nieprawidłowe dane logowania)
  - Sprawdzenie, czy stan UI jest aktualizowany odpowiednio

### 1.6. ProfileViewModel

- **Testy metody `loadProfile`**
  - Weryfikacja poprawnego ładowania profilu użytkownika
  - Sprawdzenie obsługi różnych stanów (ładowanie, sukces, błąd)
  
- **Testy metody `saveProfile`**
  - Sprawdzenie poprawnej aktualizacji danych profilu
  - Weryfikacja obsługi błędów podczas aktualizacji
  - Sprawdzenie, czy stan UI jest aktualizowany odpowiednio

- **Testy metody `signOut`**
  - Weryfikacja poprawnego wylogowania użytkownika
  - Sprawdzenie, czy stan UI jest aktualizowany odpowiednio

### 1.7. AddMovieViewModel

- **Testy metody `searchMovies`**
  - Sprawdzenie poprawnego wyszukiwania filmów
  - Weryfikacja obsługi pustych wyników wyszukiwania
  - Sprawdzenie obsługi błędów podczas wyszukiwania
  
- **Testy metody `addMovieToJournal`**
  - Weryfikacja poprawnego dodania filmu do dziennika
  - Sprawdzenie obsługi błędów podczas dodawania
  - Sprawdzenie, czy stan UI jest aktualizowany odpowiednio

## 2. Warstwa Service

### 2.1. RecommendationsService

- **Testy metody `getRecommendations`**
  - Sprawdzenie, czy poprawnie generuje rekomendacje na podstawie historii użytkownika
  - Weryfikacja obsługi pustej historii filmów
  - Sprawdzenie obsługi błędów API
  - Weryfikacja mechanizmu ponownych prób
  
- **Testy metody `prepareContext`**
  - Sprawdzenie, czy kontekst jest poprawnie przygotowany z historii filmów
  - Weryfikacja, czy format kontekstu jest zgodny z wymaganiami API

- **Testy metody `generateRecommendations`**
  - Weryfikacja przetwarzania odpowiedzi API
  - Sprawdzenie poprawnej ekstrakcji identyfikatorów TMDB
  - Weryfikacja pobierania szczegółów filmów na podstawie ID
  - Sprawdzenie obsługi nieprawidłowych ID

## 3. Warstwa Repository

### 3.1. MovieRepository (FirebaseMovieRepository)

- **Testy metody `addMovieEntry`**
  - Weryfikacja poprawnego dodania wpisu filmowego
  - Sprawdzenie obsługi błędów podczas dodawania
  
- **Testy metody `updateMovieEntry`**
  - Sprawdzenie poprawnej aktualizacji wpisu filmowego
  - Weryfikacja obsługi błędów podczas aktualizacji
  
- **Testy metody `getMovieEntries`**
  - Weryfikacja poprawnego pobierania listy filmów użytkownika
  - Sprawdzenie obsługi pustej listy filmów
  
- **Testy metody `getMovieEntriesFlow`**
  - Sprawdzenie, czy Flow prawidłowo emituje aktualizacje
  - Weryfikacja reakcji na zmiany w danych

- **Testy metody `searchMovieEntries`**
  - Sprawdzenie poprawności wyszukiwania filmów
  - Weryfikacja obsługi pustych wyników wyszukiwania

### 3.2. AuthRepository (FirebaseAuthRepository)

- **Testy metody `signIn`**
  - Weryfikacja poprawnego logowania
  - Sprawdzenie obsługi błędnych danych logowania
  
- **Testy metody `signUp`**
  - Sprawdzenie poprawnej rejestracji użytkownika
  - Weryfikacja obsługi istniejącego adresu email
  
- **Testy metody `signOut`**
  - Weryfikacja poprawnego wylogowania użytkownika
  
- **Testy metody `resetPassword`**
  - Sprawdzenie poprawnego żądania resetowania hasła
  - Weryfikacja obsługi nieistniejącego adresu email
  
- **Testy metody `updateProfile`**
  - Sprawdzenie poprawnej aktualizacji profilu
  - Weryfikacja obsługi błędów podczas aktualizacji

### 3.3. TmdbRepository (TmdbRepositoryImpl)

- **Testy metody `searchMovies`**
  - Weryfikacja poprawnego wyszukiwania filmów
  - Sprawdzenie obsługi pustych wyników wyszukiwania
  - Weryfikacja paginacji
  
- **Testy metody `getMovieDetails`**
  - Sprawdzenie poprawnego pobierania szczegółów filmu
  - Weryfikacja obsługi nieistniejącego filmu
  
- **Testy metody `getPopularMovies`**
  - Weryfikacja poprawnego pobierania popularnych filmów
  - Sprawdzenie paginacji
  
- **Testy metody `getPosterUrl`**
  - Sprawdzenie poprawnego generowania URL dla plakatów
  - Weryfikacja obsługi braku plakatu

### 3.4. RecommendationsRepository (RecommendationsRepositoryImpl)

- **Testy metody `getRecommendations`**
  - Weryfikacja poprawnego pobierania rekomendacji
  - Sprawdzenie filtrowania rekomendacji według statusu
  
- **Testy metody `dismissRecommendation`**
  - Sprawdzenie poprawnego odrzucania rekomendacji
  - Weryfikacja obsługi błędów podczas aktualizacji statusu
  
- **Testy metody `addToJournal`**
  - Weryfikacja poprawnego dodawania rekomendacji do dziennika
  - Sprawdzenie aktualizacji statusu rekomendacji po dodaniu
  - Weryfikacja obsługi błędów

## 4. Klasy pomocnicze i konwertery

### 4.1. Konwertery danych

- **Testy konwersji DTO na modele widoku**
  - Weryfikacja poprawnej konwersji `RecommendedMovieDTO` na `RecommendationMovieViewModel`
  - Sprawdzenie konwersji `MovieEntry` na modele widoku
  
- **Testy konwersji odpowiedzi API na modele domeny**
  - Weryfikacja poprawnej konwersji odpowiedzi TMDB API na modele domeny

### 4.2. Walidatory

- **Testy walidatorów danych w ViewModelach**
  - Sprawdzenie walidacji adresu email
  - Weryfikacja walidacji hasła
  - Sprawdzenie walidacji ocen filmów

## 5. Mockowanie zależności

Dla wszystkich testów należy przygotować odpowiednie mocki zależności:

- Mockowanie `AuthRepository` dla testów ViewModeli związanych z autentykacją
- Mockowanie `MovieRepository` dla testów ViewModeli związanych z filmami
- Mockowanie `TmdbRepository` dla testów związanych z danymi z TMDB
- Mockowanie `RecommendationsService` dla testów związanych z rekomendacjami
- Mockowanie `OpenRouterApiService` dla testów `RecommendationsService`
- Mockowanie `FirebaseAuth` i `FirebaseDatabase` dla testów repozytoriów

## 6. Strategia testowania

1. Użycie bibliotek:
   - JUnit 5 dla podstawowego frameworka testów
   - Mockito/MockK do mockowania zależności
   - Turbine do testowania Flows
   - Coroutines Test dla testowania kodu asynchronicznego

2. Struktura testów:
   - Testy powinny być podzielone na sekcje: przygotowanie (given), akcja (when), asercja (then)
   - Dla każdej klasy utworzenie odrębnej klasy testowej
   - Grupowanie testów dla powiązanych funkcjonalności

3. Pokrycie testami:
   - Dążenie do co najmniej 80% pokrycia dla logiki biznesowej
   - Priorytetyzacja testów dla krytycznych ścieżek (np. autentykacja, dodawanie filmów, generowanie rekomendacji) 