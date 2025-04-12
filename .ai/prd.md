# Dokument wymagań produktu (PRD) - MovieMind

## 1. Przegląd produktu

MovieMind to aplikacja mobilna dla systemu Android, która pomaga użytkownikom organizować ich doświadczenia związane z oglądaniem filmów. Aplikacja umożliwia użytkownikom dodawanie filmów do swojego dziennika, ocenianie ich oraz otrzymywanie spersonalizowanych rekomendacji na podstawie ich preferencji. W wersji MVP aplikacja skupia się na podstawowych funkcjonalnościach, zapewniając prosty i intuicyjny interfejs użytkownika.

### 1.1 Cel produktu
Celem MovieMind jest dostarczenie użytkownikom narzędzia do organizowania i analizowania ich doświadczeń związanych z oglądaniem filmów, a także pomocy w odkrywaniu nowych filmów, które mogą im się spodobać.

### 1.2 Grupa docelowa
- Kinomani, którzy chcą śledzić i organizować swoje doświadczenia związane z oglądaniem filmów
- Użytkownicy poszukujący spersonalizowanych rekomendacji filmowych
- Osoby, które chcą lepiej zrozumieć swoje preferencje filmowe

### 1.3 Kluczowe funkcje
- System autentykacji oparty na Firebase
- Dziennik filmowy z możliwością dodawania i usuwania wpisów
- Proste rekomendacje filmów na podstawie preferencji użytkownika
- Opcjonalna integracja z TMDB API do wyszukiwania i auto-uzupełniania danych

## 2. Problem użytkownika

Użytkownicy zmagają się z następującymi problemami:
- Trudność w zapamiętywaniu i organizowaniu swoich doświadczeń związanych z oglądaniem filmów
- Brak możliwości otrzymywania spersonalizowanych rekomendacji filmowych
- Trudność w znalezieniu filmów, które mogą im się spodobać, na podstawie ich preferencji

MovieMind rozwiązuje te problemy poprzez:
- Dostarczenie prostego narzędzia do dodawania i organizowania filmów
- Generowanie rekomendacji na podstawie preferencji użytkownika
- Umożliwienie użytkownikom lepszego zrozumienia swoich preferencji filmowych

## 3. Wymagania funkcjonalne

### 3.1 System autentykacji
- System autentykacji oparty na Firebase (email/hasło)
- Możliwość rejestracji nowego konta
- Możliwość logowania do istniejącego konta
- Możliwość wylogowania z konta

### 3.2 Dziennik filmowy
- Dodawanie nowego wpisu filmowego (tytuł, ocena, krótki opcjonalny opis)
- Przeglądanie listy wpisów filmowych
- Usuwanie wpisów filmowych
- Podstawowe sortowanie wpisów (po dacie, ocenie)
- Wyszukiwanie wpisów po tytule

### 3.3 Rekomendacje filmów
- Generowanie 3 prostych rekomendacji filmów na podstawie najczęściej lubianych gatunków użytkownika
- Rekomendacje generowane są tylko na żądanie użytkownika (przycisk "Zaproponuj film")
- Rekomendacje oparte są na danych wprowadzonych przez użytkownika

### 3.4 Integracja z TMDB API (opcjonalna)
- Wyszukiwanie filmów po tytule
- Auto-uzupełnianie danych filmowych przy dodawaniu wpisu
- Podstawowe metadane filmów (rok, reżyser, gatunek)
- Fallback do ręcznego wprowadzania danych, jeśli API zawiedzie

## 4. Granice produktu

### 4.1 Funkcje, które nie będą dostępne w MVP
- System tagów i kategoryzacji
- Funkcje powiadomień i przypomnień
- Proces onboardingu
- Eksport danych
- Możliwość dodawania notatek do rekomendacji
- Możliwość oceniania rekomendacji
- Funkcja udostępniania recenzji
- Lista "do obejrzenia"
- Możliwość zapisywania rekomendacji do historii oglądania
- Możliwość dodawania komentarzy do rekomendacji
- Możliwość wyboru liczby gwiazdek dla rekomendacji
- Możliwość dodawania daty oglądania dla rekomendacji
- Możliwość dodawania recenzji dla rekomendacji
- Możliwość edycji wpisów filmowych
- Logowanie przez media społecznościowe
- Odzyskiwanie hasła
- Dostosowanie profilu użytkownika
- Ustawienia użytkownika
- Plakaty/obrazy filmów
- Zaawansowane filtrowanie/wyszukiwanie
- Analiza emocjonalna
- Profil osobowości filmowej
- Analiza powiązań między filmami
- Generowanie tematów do dyskusji
- Śledzenie innych użytkowników
- Komentarze/dyskusje
- Kluby filmowe

### 4.2 Ograniczenia techniczne
- Aplikacja wymaga połączenia z internetem
- Dane przechowywane są tylko w Firebase do momentu usunięcia przez użytkownika
- Rekomendacje oparte są na gatunkach najczęściej lubianych przez użytkownika
- System zawsze pokazuje dokładnie 3 rekomendacje
- Rekomendacje są generowane tylko na żądanie użytkownika

## 5. Historyjki użytkowników

### US-001: Rejestracja użytkownika
**Opis:** Jako nowy użytkownik chcę móc zarejestrować się w aplikacji, aby rozpocząć korzystanie z niej.

**Kryteria akceptacji:**
- Użytkownik może wprowadzić adres e-mail i hasło
- System weryfikuje poprawność adresu e-mail
- System weryfikuje siłę hasła (minimum 6 znaków)
- Po pomyślnej rejestracji użytkownik jest automatycznie logowany
- W przypadku błędu system wyświetla odpowiedni komunikat

### US-002: Logowanie użytkownika
**Opis:** Jako zarejestrowany użytkownik chcę móc zalogować się do aplikacji, aby uzyskać dostęp do mojego konta.

**Kryteria akceptacji:**
- Użytkownik może wprowadzić adres e-mail i hasło
- System weryfikuje poprawność danych logowania
- Po pomyślnym logowaniu użytkownik ma dostęp do swojego konta
- W przypadku błędnych danych system wyświetla odpowiedni komunikat

### US-003: Wylogowanie użytkownika
**Opis:** Jako zalogowany użytkownik chcę móc wylogować się z aplikacji, aby zabezpieczyć moje dane.

**Kryteria akceptacji:**
- Użytkownik może kliknąć przycisk "Wyloguj"
- Po wylogowaniu użytkownik jest przekierowany do ekranu logowania
- Użytkownik nie ma już dostępu do swojego konta bez ponownego zalogowania

### US-004: Dodawanie nowego wpisu filmowego
**Opis:** Jako zalogowany użytkownik chcę móc dodać nowy wpis filmowy do mojego dziennika, aby śledzić obejrzane filmy.

**Kryteria akceptacji:**
- Użytkownik może wprowadzić tytuł filmu
- Użytkownik może wprowadzić ocenę filmu
- Użytkownik może opcjonalnie dodać krótki opis filmu
- Po dodaniu wpisu film jest wyświetlany w liście wpisów
- Wpis jest zapisywany w bazie danych Firebase

### US-005: Przeglądanie listy wpisów filmowych
**Opis:** Jako zalogowany użytkownik chcę móc przeglądać listę moich wpisów filmowych, aby zobaczyć historię obejrzanych filmów.

**Kryteria akceptacji:**
- Lista wpisów filmowych jest wyświetlana po zalogowaniu
- Każdy wpis zawiera tytuł filmu, ocenę i opcjonalny opis
- Lista jest sortowana domyślnie po dacie dodania (od najnowszych)
- Użytkownik może przewijać listę, aby zobaczyć wszystkie wpisy

### US-006: Usuwanie wpisu filmowego
**Opis:** Jako zalogowany użytkownik chcę móc usunąć wpis filmowy z mojego dziennika, aby usunąć niepotrzebne wpisy.

**Kryteria akceptacji:**
- Użytkownik może kliknąć przycisk usuwania przy wybranym wpisie
- System wyświetla potwierdzenie usunięcia
- Po potwierdzeniu wpis jest usuwany z listy i z bazy danych
- Użytkownik otrzymuje potwierdzenie usunięcia

### US-007: Sortowanie wpisów filmowych
**Opis:** Jako zalogowany użytkownik chcę móc sortować moje wpisy filmowe, aby łatwiej znajdować interesujące mnie filmy.

**Kryteria akceptacji:**
- Użytkownik może wybrać opcję sortowania (po dacie, ocenie)
- Lista wpisów jest automatycznie aktualizowana zgodnie z wybranym sortowaniem
- Domyślne sortowanie to data dodania (od najnowszych)

### US-008: Wyszukiwanie wpisów filmowych
**Opis:** Jako zalogowany użytkownik chcę móc wyszukiwać moje wpisy filmowe po tytule, aby szybko znaleźć konkretny film.

**Kryteria akceptacji:**
- Użytkownik może wprowadzić tytuł filmu w pole wyszukiwania
- Lista wpisów jest automatycznie filtrowana zgodnie z wprowadzonym tytułem
- Wyszukiwanie jest niewrażliwe na wielkość liter
- Wyszukiwanie działa na częściowych dopasowaniach tytułu

### US-009: Generowanie rekomendacji filmów
**Opis:** Jako zalogowany użytkownik chcę móc otrzymać rekomendacje filmów, które mogą mi się spodobać, aby odkryć nowe filmy.

**Kryteria akceptacji:**
- Użytkownik może kliknąć przycisk "Zaproponuj film"
- System generuje 3 rekomendacje filmów na podstawie najczęściej lubianych gatunków użytkownika
- Rekomendacje są wyświetlane użytkownikowi
- Rekomendacje są generowane w czasie nie dłuższym niż 10 sekund

### US-010: Integracja z TMDB API (opcjonalna)
**Opis:** Jako zalogowany użytkownik chcę móc wyszukiwać filmy w bazie TMDB, aby łatwiej dodawać nowe wpisy.

**Kryteria akceptacji:**
- Użytkownik może wyszukiwać filmy po tytule w bazie TMDB
- Wyniki wyszukiwania są wyświetlane użytkownikowi
- Użytkownik może wybrać film z wyników wyszukiwania
- Dane filmu są automatycznie uzupełniane w formularzu dodawania wpisu
- W przypadku braku połączenia z API, użytkownik może ręcznie wprowadzić dane filmu

## 6. Metryki sukcesu

### 6.1 Metryki zaangażowania użytkownika
- Użytkownik może dodać wpis filmowy w < 30 sekund
- AI generuje rekomendacje w < 10 sekund
- Lista filmów ładuje się w < 3 sekundy
- 70% użytkowników dodaje więcej niż jeden film
- 50% użytkowników wraca w ciągu tygodnia
- 30% użytkowników korzysta z funkcji AI

### 6.2 Metryki techniczne
- CI pipeline działa poprawnie w > 90% przypadków
- Aplikacja działa stabilnie na różnych urządzeniach z systemem Android
- Czas uruchamiania aplikacji jest krótszy niż 3 sekundy

### 6.3 Metryki biznesowe
- Liczba aktywnych użytkowników miesięcznie
- Średni czas spędzony w aplikacji na użytkownika
- Liczba dodanych wpisów filmowych na użytkownika
- Liczba wygenerowanych rekomendacji na użytkownika 