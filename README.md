# MovieMind

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)

## Spis treści
- [Opis projektu](#opis-projektu)
- [Stos technologiczny](#stos-technologiczny)
- [Rozpoczęcie pracy](#rozpoczęcie-pracy)
- [Dostępne skrypty](#dostępne-skrypty)
- [Zakres projektu](#zakres-projektu)
- [Status projektu](#status-projektu)
- [Licencja](#licencja)

## Opis projektu

MovieMind to aplikacja mobilna dla systemu Android, która pomaga użytkownikom organizować ich doświadczenia związane z oglądaniem filmów. Aplikacja umożliwia użytkownikom dodawanie filmów do swojego dziennika, ocenianie ich oraz otrzymywanie spersonalizowanych rekomendacji na podstawie ich preferencji.

### Główne funkcje
- **System autentykacji** oparty na Firebase (email/hasło)
- **Dziennik filmowy** z możliwością dodawania i usuwania wpisów
- **Rekomendacje filmów** na podstawie preferencji użytkownika
- **Integracja z TMDB API** (opcjonalna) do wyszukiwania i auto-uzupełniania danych

### Grupa docelowa
- Kinomani, którzy chcą śledzić i organizować swoje doświadczenia związane z oglądaniem filmów
- Użytkownicy poszukujący spersonalizowanych rekomendacji filmowych
- Osoby, które chcą lepiej zrozumieć swoje preferencje filmowe

## Stos technologiczny

### Główne technologie
- **Firebase** - backend i autentykacja
  - Firebase Authentication dla logowania użytkowników
  - Firebase Firestore do przechowywania danych filmowych
  - Firebase Security Rules do zabezpieczenia danych

- **Jetpack Compose** - nowoczesny framework UI
  - Material3 dla spójnego wyglądu aplikacji
  - Compose Navigation dla nawigacji między ekranami
  - Compose Animation dla płynnych przejść i interakcji

- **MVVM (Model-View-ViewModel)** - wzorzec architektoniczny
  - ViewModel do zarządzania stanem UI
  - LiveData do obserwowania zmian danych
  - Repository pattern do abstrakcji źródeł danych

- **Kotlin Coroutines** - obsługa operacji asynchronicznych
  - Structured concurrency dla bezpiecznego zarządzania wątkami
  - Flow dla strumieni danych
  - Suspend functions dla operacji asynchronicznych

- **Retrofit z Kotlin Serialization** - komunikacja sieciowa
  - Integracja z TMDB API
  - Integracja z OpenRouter API dla rekomendacji

### Wersje kluczowych bibliotek
- Kotlin: 2.0.21
- Jetpack Compose: 2024.09.00
- Firebase: najnowsza wersja
- Retrofit: najnowsza wersja
- Ktlint: 11.6.1

## Rozpoczęcie pracy

### Wymagania wstępne
- Android Studio (najnowsza wersja)
- JDK 17 lub nowszy
- Android SDK
- Urządzenie Android lub emulator

### Konfiguracja środowiska
1. Sklonuj repozytorium:
   ```bash
   git clone https://github.com/yourusername/moviemind.git
   cd moviemind
   ```

2. Otwórz projekt w Android Studio

3. Skonfiguruj Firebase:
   - Utwórz projekt w [Firebase Console](https://console.firebase.google.com/)
   - Pobierz plik `google-services.json` i umieść go w katalogu `app/`
   - Włącz autentykację email/hasło w Firebase Console

4. (Opcjonalnie) Skonfiguruj TMDB API:
   - Zarejestruj się w [TMDB](https://www.themoviedb.org/documentation/api)
   - Uzyskaj klucz API
   - Dodaj klucz API do pliku konfiguracyjnego

### Uruchomienie aplikacji
1. Podłącz urządzenie Android lub uruchom emulator
2. Uruchom aplikację z Android Studio lub użyj skryptu `run.bat`

## Dostępne skrypty

### run.bat
Skrypt do budowania i instalacji aplikacji na urządzeniu Android:
```bash
./run.bat
```
Skrypt wykonuje następujące operacje:
- Sprawdza połączenie z urządzeniem Android
- Buduje aplikację
- Instaluje aplikację na urządzeniu
- Uruchamia aplikację

## Zakres projektu

### Funkcje MVP
- System autentykacji oparty na Firebase (email/hasło)
- Dziennik filmowy z możliwością dodawania i usuwania wpisów
- Proste rekomendacje filmów na podstawie preferencji użytkownika
- Opcjonalna integracja z TMDB API do wyszukiwania i auto-uzupełniania danych

### Ograniczenia MVP
- Brak systemu tagów i kategoryzacji
- Brak funkcji powiadomień i przypomnień
- Brak procesu onboardingu
- Brak eksportu danych
- Brak możliwości dodawania notatek do rekomendacji
- Brak możliwości oceniania rekomendacji
- Brak funkcji udostępniania recenzji
- Brak listy "do obejrzenia"

### Metryki sukcesu
- Użytkownik może dodać wpis filmowy w < 30 sekund
- AI generuje rekomendacje w < 10 sekund
- Lista filmów ładuje się w < 3 sekundy
- 70% użytkowników dodaje więcej niż jeden film
- 50% użytkowników wraca w ciągu tygodnia
- 30% użytkowników korzysta z funkcji AI

## Status projektu

Projekt jest obecnie w fazie rozwoju MVP. Główne funkcje są w trakcie implementacji.

## Licencja

Ten projekt jest licencjonowany na podstawie licencji Apache 2.0 - szczegóły znajdziesz w pliku [LICENSE](LICENSE). 