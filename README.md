# MovieMind

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Android CI](https://github.com/sswierczek/10x-android/actions/workflows/android.yml/badge.svg)](https://github.com/sswierczek/10x-android/actions/workflows/android.yml)
[![Code Quality](https://github.com/sswierczek/10x-android/actions/workflows/code-quality.yml/badge.svg)](https://github.com/sswierczek/10x-android/actions/workflows/code-quality.yml)
[![Connected Tests](https://github.com/sswierczek/10x-android/actions/workflows/android-connected-tests.yml/badge.svg)](https://github.com/sswierczek/10x-android/actions/workflows/android-connected-tests.yml)
[![Download Latest APK](https://img.shields.io/badge/Download-Latest_APK-brightgreen)](https://github.com/sswierczek/10x-android/actions/workflows/android.yml)

## Najnowsza wersja

Możesz pobrać najnowszą wersję debugową APK z ostatniego udanego przebiegu workflow:

1. Kliknij odznakę "Android CI" powyżej lub przejdź do [zakładki Actions](https://github.com/sswierczek/10x-android/actions/workflows/android.yml)
2. Kliknij na najnowszy udany przebieg workflow
3. Przewiń w dół do sekcji "Artifacts"
4. Pobierz artefakt "app-debug"
5. Rozpakuj plik APK z pobranego archiwum ZIP

> **Uwaga:** Musisz być zalogowany do GitHub, aby pobrać artefakty.

## Spis treści
- [Opis projektu](#opis-projektu)
- [Stos technologiczny](#stos-technologiczny)
- [Rozpoczęcie pracy](#rozpoczęcie-pracy)
- [Dostępne skrypty](#dostępne-skrypty)
- [CI/CD](#cicd)
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
  - Firebase Realtime Database do przechowywania danych filmowych
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
   - Włącz Firebase Realtime Database w Firebase Console

4. (Opcjonalnie) Skonfiguruj TMDB API:
   - Zarejestruj się w [TMDB](https://www.themoviedb.org/settings/api)
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

## CI/CD

Projekt wykorzystuje GitHub Actions do ciągłej integracji i dostarczania (CI/CD). Workflow automatycznie buduje aplikację przy każdym pushu do gałęzi `main` oraz przy tworzeniu pull requestów.

### GitHub Workflow

Workflow wykonuje następujące kroki:
1. Konfiguruje środowisko Java 11
2. Tworzy plik `google-services.json` z sekretu repozytorium
3. Buduje aplikację za pomocą Gradle
4. Przesyła zbudowany APK jako artefakt

### Konfiguracja sekretów

Aby workflow działał poprawnie, należy dodać następujące sekrety do repozytorium:
- `GOOGLE_SERVICES_JSON` - zawartość pliku `google-services.json` z Firebase Console

Szczegółowe instrukcje dotyczące konfiguracji sekretów znajdują się w pliku [.github/workflows/README.md](.github/workflows/README.md).

## Zakres projektu

### MVP (Minimum Viable Product)
- System autentykacji użytkowników
- Podstawowy dziennik filmowy (dodawanie, edycja, usuwanie wpisów)
- Proste rekomendacje filmowe
- Podstawowy interfejs użytkownika

### Przyszłe funkcje
- Zaawansowane rekomendacje z wykorzystaniem AI
- Integracja z serwisami streamingowymi
- Statystyki i analizy preferencji filmowych
- Funkcje społecznościowe (dzielenie się listami filmów)

## Status projektu

Projekt jest obecnie w fazie rozwoju. Główne funkcje MVP są w trakcie implementacji.

## Licencja

Ten projekt jest licencjonowany na podstawie licencji Apache 2.0 - szczegóły znajdują się w pliku [LICENSE](LICENSE).

## Konfiguracja

### Konfiguracja klucza TMDB API

1. Uzyskaj klucz API z [TMDB](https://www.themoviedb.org/settings/api)
2. Utwórz lub edytuj plik `local.properties` w katalogu głównym projektu
3. Dodaj swój klucz API do `local.properties`:
   ```
   TMDB_API_KEY=twój_klucz_api
   ```
4. Zsynchronizuj projekt z plikami Gradle
5. Klucz API zostanie automatycznie załadowany z `local.properties` do BuildConfig

> **Uwaga:** 
> - Nigdy nie commituj swojego rzeczywistego klucza API do kontroli wersji
> - Plik `local.properties` jest już w `.gitignore`
> - Klucz API jest bezpiecznie przechowywany w BuildConfig podczas budowania 