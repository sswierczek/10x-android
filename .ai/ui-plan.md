# Architektura UI dla MovieMind

## 1. Przegląd struktury UI

Aplikacja MovieMind jest zbudowana wokół nawigacji dolnej (Bottom Navigation) z czterema głównymi sekcjami: Dziennik, Rekomendacje, Dodaj Film i Profil. Przed dostępem do głównej funkcjonalności aplikacji użytkownicy muszą przejść przez proces autentykacji. Architektura UI jest zgodna z wytycznymi Material Design 3 i koncentruje się na prostocie oraz łatwości obsługi.

Główne elementy struktury:
- Ekran autentykacji (logowanie/rejestracja) jako punkt wejścia do aplikacji
- Widok główny z Bottom Navigation jako centralny punkt nawigacji
- Widoki szczegółów jako nakładki na główne sekcje
- Wszystkie widoki zbudowane przy użyciu komponentów Scaffold dla spójności layoutu

## 2. Lista widoków

### 2.1. Ekran Logowania
- **Ścieżka widoku**: `/login`
- **Główny cel**: Umożliwienie użytkownikom zalogowania się do istniejącego konta
- **Kluczowe informacje**:
  - Pola do wprowadzenia e-maila i hasła
  - Przyciski logowania i przejścia do rejestracji
- **Kluczowe komponenty**:
  - Formularz logowania z walidacją
  - Przycisk akcji (logowanie)
  - Link do ekranu rejestracji
- **UX, dostępność i bezpieczeństwo**:
  - Walidacja danych wejściowych w czasie rzeczywistym
  - Jasne komunikaty o błędach
  - Wskaźnik ładowania podczas procesu logowania
  - Zabezpieczenie przed wielokrotnymi próbami logowania

### 2.2. Ekran Rejestracji
- **Ścieżka widoku**: `/register`
- **Główny cel**: Umożliwienie użytkownikom utworzenia nowego konta
- **Kluczowe informacje**:
  - Pola do wprowadzenia e-maila i hasła
  - Przycisk rejestracji
- **Kluczowe komponenty**:
  - Formularz rejestracji z walidacją
  - Przycisk akcji (rejestracja)
  - Link powrotu do ekranu logowania
- **UX, dostępność i bezpieczeństwo**:
  - Walidacja danych wejściowych w czasie rzeczywistym
  - Jasne komunikaty o błędach
  - Wskaźnik ładowania podczas procesu rejestracji
  - Wskazówki dotyczące wymagań bezpieczeństwa hasła

### 2.3. Dziennik Filmowy
- **Ścieżka widoku**: `/journal`
- **Główny cel**: Wyświetlenie listy filmów dodanych przez użytkownika
- **Kluczowe informacje**:
  - Lista filmów w formie kart z miniaturami plakatów, tytułami i podstawowymi informacjami
  - Opcje sortowania (po dacie dodania, rosnąco/malejąco)
- **Kluczowe komponenty**:
  - LazyColumn z kartami filmów
  - Przycisk sortowania
  - Pull-to-refresh do odświeżania zawartości
  - Wskaźnik stanu (ładowanie/błąd/sukces)
- **UX, dostępność i bezpieczeństwo**:
  - Płynne przewijanie listy
  - Wyraźne stany ładowania
  - Obsługa pustego stanu (gdy brak filmów)
  - Zabezpieczenie przed przypadkowym usunięciem

### 2.4. Szczegóły Filmu
- **Ścieżka widoku**: `/movie/{movieId}`
- **Główny cel**: Wyświetlenie szczegółowych informacji o wybranym filmie
- **Kluczowe informacje**:
  - Plakat filmu w pełnym rozmiarze
  - Tytuł, reżyser, obsada
  - Rok produkcji, gatunek
  - Ocena, długość filmu
  - Opis
- **Kluczowe komponenty**:
  - Przycisk usuwania filmu z dziennika
  - Wskaźnik ładowania podczas pobierania danych z TMDB API
  - Przycisk powrotu
- **UX, dostępność i bezpieczeństwo**:
  - Łatwa nawigacja powrotna
  - Dialog potwierdzenia przy usuwaniu filmu
  - Obsługa błędów API (np. film niedostępny w TMDB)

### 2.5. Rekomendacje
- **Ścieżka widoku**: `/recommendations`
- **Główny cel**: Wyświetlenie listy rekomendowanych filmów
- **Kluczowe informacje**:
  - Lista rekomendowanych filmów w formie kart
  - Miniatura plakatu, tytuł i podstawowe informacje dla każdego filmu
- **Kluczowe komponenty**:
  - LazyColumn z kartami filmów
  - Pull-to-refresh do odświeżania rekomendacji
  - Wskaźnik stanu (ładowanie/błąd/sukces)
- **UX, dostępność i bezpieczeństwo**:
  - Płynne przewijanie listy
  - Wyraźne stany ładowania
  - Obsługa pustego stanu (gdy brak rekomendacji)
  - Informacja o tym, jak generowane są rekomendacje

### 2.6. Dodawanie Filmu
- **Ścieżka widoku**: `/add-movie`
- **Główny cel**: Umożliwienie użytkownikom wyszukiwania i dodawania filmów do dziennika
- **Kluczowe informacje**:
  - Pole wyszukiwania
  - Lista wyników wyszukiwania z TMDB API
- **Kluczowe komponenty**:
  - Pole wyszukiwania z autouzupełnianiem
  - LazyColumn z wynikami wyszukiwania
  - Przyciski dodawania filmu do dziennika
  - Wskaźnik ładowania podczas wyszukiwania
- **UX, dostępność i bezpieczeństwo**:
  - Wyświetlanie wyników w czasie rzeczywistym
  - Jasne wskaźniki ładowania
  - Informacja zwrotna po dodaniu filmu
  - Obsługa braku wyników wyszukiwania

### 2.7. Profil Użytkownika
- **Ścieżka widoku**: `/profile`
- **Główny cel**: Wyświetlenie podstawowych informacji o użytkowniku i umożliwienie wylogowania
- **Kluczowe informacje**:
  - Podstawowe dane użytkownika (email)
  - Statystyki (liczba filmów w dzienniku)
- **Kluczowe komponenty**:
  - Przycisk wylogowania
  - Wyświetlanie danych użytkownika
- **UX, dostępność i bezpieczeństwo**:
  - Dialog potwierdzenia przy wylogowaniu
  - Jasne oznaczenie aktualnie zalogowanego użytkownika

## 3. Mapa podróży użytkownika

### 3.1. Proces autentykacji
1. Użytkownik otwiera aplikację
2. Jeśli nie jest zalogowany, widzi ekran logowania
3. Może:
   - Wprowadzić dane logowania i zalogować się
   - Przejść do ekranu rejestracji, aby utworzyć nowe konto
4. Po udanej autentykacji użytkownik jest przekierowywany do widoku dziennika filmowego

### 3.2. Przeglądanie dziennika filmowego
1. Użytkownik przegląda listę swoich filmów
2. Może:
   - Przewijać listę filmów
   - Sortować filmy według daty dodania (rosnąco/malejąco)
   - Odświeżyć listę gestą "pull-to-refresh"
   - Kliknąć na film, aby zobaczyć jego szczegóły

### 3.3. Przeglądanie szczegółów filmu
1. Użytkownik widzi szczegółowe informacje o wybranym filmie
2. Może:
   - Przeglądać wszystkie dostępne informacje o filmie
   - Usunąć film z dziennika (z potwierdzeniem)
   - Wrócić do widoku dziennika

### 3.4. Przeglądanie rekomendacji
1. Użytkownik przechodzi do sekcji rekomendacji przez Bottom Navigation
2. Przegląda listę rekomendowanych filmów
3. Może:
   - Przewijać listę rekomendacji
   - Odświeżyć listę gestą "pull-to-refresh"
   - Kliknąć na film, aby zobaczyć jego szczegóły

### 3.5. Dodawanie nowego filmu
1. Użytkownik przechodzi do sekcji dodawania filmu przez Bottom Navigation
2. Wpisuje tytuł filmu w polu wyszukiwania
3. Przegląda wyniki wyszukiwania pojawiające się w czasie rzeczywistym
4. Klika na wybrany film, aby dodać go do dziennika
5. Otrzymuje potwierdzenie dodania filmu i może:
   - Dodać kolejny film
   - Przejść do dziennika, aby zobaczyć dodany film

### 3.6. Zarządzanie profilem
1. Użytkownik przechodzi do sekcji profilu przez Bottom Navigation
2. Przegląda swoje podstawowe informacje
3. Może wylogować się z aplikacji (z potwierdzeniem)

## 4. Układ i struktura nawigacji

Architektura nawigacji MovieMind opiera się na dwóch głównych poziomach:

### 4.1. Nawigacja główna
- **Bottom Navigation Bar** - Główny element nawigacyjny dostępny we wszystkich głównych widokach aplikacji
  - Zawiera cztery ikony odpowiadające głównym sekcjom:
    - Dziennik
    - Rekomendacje
    - Dodaj Film
    - Profil
  - Umożliwia szybkie przełączanie między głównymi funkcjami aplikacji
  - Aktywna sekcja jest wyraźnie wyróżniona

### 4.2. Nawigacja drugiego poziomu
- **Przejścia szczegółowe** - Nawigacja do widoków szczegółowych z głównych sekcji
  - Z dziennika/rekomendacji do szczegółów filmu
  - Z ekranu logowania do rejestracji i z powrotem
- **Nawigacja powrotna** - Obecna we wszystkich widokach poza głównymi sekcjami
  - Przycisk powrotu w górnym pasku aplikacji
  - Gest systemowy powrotu
  - Zawsze prowadzi do poprzedniego logicznego widoku

### 4.3. Przepływ nawigacji autentykacji
- Ekran logowania/rejestracji jako punkt wejścia
- Po udanej autentykacji automatyczne przekierowanie do widoku dziennika
- Po wylogowaniu automatyczne przekierowanie do ekranu logowania

## 5. Kluczowe komponenty

### 5.1. MovieCard
- Wielokrotnie używany komponent do wyświetlania podstawowych informacji o filmie
- Wykorzystywany w dzienniku filmowym i rekomendacjach
- Zawiera:
  - Miniaturę plakatu filmu
  - Tytuł filmu
  - Rok produkcji
  - Podstawowe informacje (np. gatunek)
- Obsługuje kliknięcie prowadzące do szczegółów filmu

### 5.2. LoadingStateHandler
- Komponent do zarządzania stanami ładowania, błędu i sukcesu
- Używany we wszystkich widokach z operacjami asynchronicznymi
- Wyświetla:
  - Animowany wskaźnik podczas ładowania
  - Komunikaty o błędach z opcją ponowienia
  - Właściwą zawartość w przypadku sukcesu
- Zapewnia spójne doświadczenie użytkownika w całej aplikacji

### 5.3. SearchBar
- Komponent pola wyszukiwania z autouzupełnianiem
- Używany w widoku dodawania filmu
- Funkcje:
  - Wyszukiwanie w czasie rzeczywistym
  - Wyświetlanie wyników wyszukiwania w formie rozwijanej listy
  - Obsługa stanu pustego i błędu

### 5.4. ConfirmationDialog
- Komponent dialogu potwierdzenia dla akcji destrukcyjnych
- Używany przy usuwaniu filmu i wylogowywaniu
- Zawiera:
  - Jasny komunikat o konsekwencjach akcji
  - Przyciski potwierdzenia i anulowania
  - Opcjonalne dodatkowe informacje

### 5.5. SortingControl
- Komponent kontrolujący sortowanie listy
- Używany w widoku dziennika filmowego
- Umożliwia:
  - Przełączanie między sortowaniem rosnącym i malejącym
  - Wyraźne wskazanie aktualnego stanu sortowania

### 5.6. EmptyStateView
- Komponent wyświetlany, gdy lista nie zawiera żadnych elementów
- Używany w dzienniku i rekomendacjach
- Zawiera:
  - Ilustrację reprezentującą pusty stan
  - Komunikat wyjaśniający przyczynę
  - Opcjonalną akcję (np. przycisk "Dodaj pierwszy film") 