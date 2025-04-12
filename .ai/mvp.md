# MovieMind - Definicja MVP

## 🎯 Główny Problem
Użytkownicy mają trudności z:
- Zapamiętywaniem i organizowaniem swoich doświadczeń związanych z oglądaniem filmów
- Otrzymywaniem spersonalizowanych spostrzeżeń dotyczących ich preferencji filmowych
- Znalezieniem znaczących rekomendacji filmów na podstawie ich gustu

## 🚀 Funkcje MVP

### 1. Autentykacja (Firebase)
- [x] Logowanie przez email/hasło
- [x] Podstawowy profil użytkownika
- [x] Funkcjonalność wylogowania

### 2. Dziennik Filmowy (CRUD)
- [x] Dodawanie nowego wpisu filmowego
  - Tytuł
  - Data obejrzenia
  - Osobista ocena (1-5 gwiazdek)
  - Krótka recenzja
- [x] Przeglądanie listy wpisów
- [x] Edycja istniejących wpisów
- [x] Usuwanie wpisów
- [x] Podstawowe sortowanie (po dacie, ocenie)

### 3. Integracja AI (OpenRouter)
- [x] Generowanie podstawowej analizy recenzji użytkownika
- [x] Dostarczanie jednej rekomendacji filmu na podstawie recenzji
- [x] Przechowywanie odpowiedzi AI w Firebase

### 4. Testowanie
- [x] Jeden test end-to-end dla operacji CRUD wpisów filmowych
- [x] Podstawowy pipeline CI do weryfikacji budowania

### 5. Opcjonalnie: Integracja z API TMDB
- [ ] Wyszukiwanie filmów po tytule
- [ ] Automatyczne uzupełnianie szczegółów filmu przy dodawaniu wpisu
- [ ] Podstawowe metadane filmu (rok, reżyser, gatunek)
- [ ] Powrót do ręcznego wprowadzania, jeśli API zawiedzie

## ⛔ Nie w MVP

### Autentykacja
- Logowanie przez media społecznościowe
- Odzyskiwanie hasła
- Dostosowanie profilu
- Ustawienia użytkownika

### Funkcje Filmowe
- Plakaty/obrazy filmów
- Zaawansowane filtrowanie/wyszukiwanie
- Kategorie/tagi
- Lista "do obejrzenia"

### Funkcje AI
- Analiza emocjonalna
- Profil osobowości filmowej
- Wiele rekomendacji
- Analiza powiązań między filmami
- Generowanie tematów do dyskusji

### Funkcje Społecznościowe
- Śledzenie innych użytkowników
- Udostępnianie recenzji
- Komentarze/dyskusje
- Kluby filmowe

## ✅ Kryteria Sukcesu

### Zaangażowanie Użytkownika
- Użytkownik może ukończyć podstawowy przepływ w < 2 minuty
- Użytkownik może dodać wpis filmowy w < 30 sekund
- Odpowiedź AI generowana w < 10 sekund

### Techniczne
- < 3 sekundy czasu ładowania listy filmów
- Wszystkie operacje CRUD działają offline
- Udane buildy CI > 90%

### Biznesowe
- 70% użytkowników dodaje więcej niż jeden film
- 50% użytkowników wraca w ciągu tygodnia
- 30% użytkowników korzysta z funkcji AI

## 🎬 Przepływ Użytkownika MVP
1. Użytkownik rejestruje się/loguje
2. Przegląda pustą listę filmów
3. Dodaje pierwszy film z recenzją
4. Otrzymuje spostrzeżenie wygenerowane przez AI
5. Przegląda zaktualizowaną listę filmów
6. Może edytować/usunąć wpis

## 📋 Lista Kontrolna Uruchomienia MVP
- [ ] Skonfigurowana autentykacja Firebase
- [ ] Zaimplementowany podstawowy UI/UX
- [ ] Działające operacje CRUD
- [ ] Funkcjonalna integracja AI
- [ ] Przechodzący jeden test end-to-end
- [ ] Skonfigurowany pipeline CI 