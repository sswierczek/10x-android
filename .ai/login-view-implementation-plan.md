# Plan implementacji widoku Logowania

## 1. Przegląd
Widok Logowania to ekran, który pozwala istniejącym użytkownikom na zalogowanie się do aplikacji poprzez wprowadzenie adresu e-mail i hasła. Umożliwia również przejście do ekranu rejestracji dla nowych użytkowników oraz opcjonalnie zawiera funkcję resetowania hasła. Po pomyślnym zalogowaniu, użytkownik zostaje przekierowany do głównego widoku aplikacji (Dziennik Filmowy).

## 2. Routing widoku
Ścieżka widoku: `/login`

Ten widok jest dostępny przez:
- Ekran powitalny/startowy (główna ścieżka `/login` aplikacji dla niezalogowanych użytkowników)
- Link "Masz już konto? Zaloguj się" z ekranu rejestracji
- Automatyczne przekierowanie po wylogowaniu użytkownika

## 3. Struktura komponentów
```
LoginScreen
├── TopLogo
├── LoginForm
│   ├── EmailField
│   ├── PasswordField
│   ├── LoginButton
│   └── ForgotPasswordLink
├── RegistrationLink
├── LoadingIndicator (wyświetlany podczas logowania)
└── ErrorMessage (wyświetlany w przypadku błędu)
```

## 4. Szczegóły komponentów
### LoginScreen
- **Opis komponentu**: Główny ekran logowania, zawierający wszystkie komponenty potrzebne do logowania.
- **Główne elementy**:
  - Scaffold z Column jako głównym kontenerem
  - Komponenty logowania (opisane poniżej)
  - LoadingIndicator (widoczny tylko podczas próby logowania)
  - ErrorMessage (widoczny tylko po nieudanej próbie logowania)
- **Obsługiwane interakcje**:
  - Logowanie
  - Nawigacja do ekranu rejestracji
  - Nawigacja do ekranu resetowania hasła
- **Obsługiwana walidacja**: Brak na poziomie ekranu
- **Typy**: LoginViewModel, LoginUiState
- **Propsy**: NavController

### TopLogo
- **Opis komponentu**: Logo aplikacji wyświetlane na górze ekranu.
- **Główne elementy**:
  - Image z logo aplikacji
  - Text z nazwą aplikacji
- **Obsługiwane interakcje**: Brak
- **Obsługiwana walidacja**: Brak
- **Typy**: Brak specyficznych
- **Propsy**: Brak

### LoginForm
- **Opis komponentu**: Formularz zawierający pola do wprowadzenia danych logowania.
- **Główne elementy**:
  - Column zawierająca:
    - EmailField
    - PasswordField
    - LoginButton
    - ForgotPasswordLink
- **Obsługiwane interakcje**:
  - Wprowadzanie danych logowania
  - Kliknięcie przycisku logowania
  - Kliknięcie linku resetowania hasła
- **Obsługiwana walidacja**:
  - Walidacja e-maila (format)
  - Walidacja hasła (niepuste)
- **Typy**: LoginCredentials
- **Propsy**:
  - onLoginClick: (LoginCredentials) -> Unit
  - onForgotPasswordClick: () -> Unit
  - isLoading: Boolean

### EmailField
- **Opis komponentu**: Pole tekstowe do wprowadzenia adresu e-mail.
- **Główne elementy**:
  - OutlinedTextField z:
    - Label "E-mail"
    - Placeholder "Wprowadź adres e-mail"
    - KeyboardType.Email
    - Ikona e-mail
- **Obsługiwane interakcje**:
  - Wprowadzanie tekstu
  - Obsługa błędów walidacji
- **Obsługiwana walidacja**:
  - Format e-maila
- **Typy**: Brak specyficznych
- **Propsy**:
  - value: String
  - onValueChange: (String) -> Unit
  - error: String?
  - isEnabled: Boolean

### PasswordField
- **Opis komponentu**: Pole tekstowe do wprowadzenia hasła.
- **Główne elementy**:
  - OutlinedTextField z:
    - Label "Hasło"
    - Placeholder "Wprowadź hasło"
    - VisualTransformation dla ukrywania hasła
    - Ikona hasła
    - Przycisk pokazywania/ukrywania hasła
- **Obsługiwane interakcje**:
  - Wprowadzanie tekstu
  - Przełączanie widoczności hasła
  - Obsługa błędów walidacji
- **Obsługiwana walidacja**:
  - Niepuste hasło
- **Typy**: Brak specyficznych
- **Propsy**:
  - value: String
  - onValueChange: (String) -> Unit
  - error: String?
  - isEnabled: Boolean

### LoginButton
- **Opis komponentu**: Przycisk do zatwierdzenia formularza logowania.
- **Główne elementy**:
  - Button z:
    - Text "Zaloguj się"
    - W stanie ładowania: CircularProgressIndicator
- **Obsługiwane interakcje**:
  - Kliknięcie przycisku
- **Obsługiwana walidacja**: Brak
- **Typy**: Brak specyficznych
- **Propsy**:
  - onClick: () -> Unit
  - isEnabled: Boolean
  - isLoading: Boolean

### ForgotPasswordLink
- **Opis komponentu**: Link do ekranu resetowania hasła.
- **Główne elementy**:
  - TextButton z:
    - Text "Zapomniałeś hasła?"
- **Obsługiwane interakcje**:
  - Kliknięcie linku
- **Obsługiwana walidacja**: Brak
- **Typy**: Brak specyficznych
- **Propsy**:
  - onClick: () -> Unit

### RegistrationLink
- **Opis komponentu**: Link do ekranu rejestracji.
- **Główne elementy**:
  - Row z:
    - Text "Nie masz konta?"
    - TextButton z "Zarejestruj się"
- **Obsługiwane interakcje**:
  - Kliknięcie przycisku "Zarejestruj się"
- **Obsługiwana walidacja**: Brak
- **Typy**: Brak specyficznych
- **Propsy**:
  - onClick: () -> Unit

### LoadingIndicator
- **Opis komponentu**: Wskaźnik ładowania wyświetlany podczas logowania.
- **Główne elementy**:
  - CircularProgressIndicator
- **Obsługiwane interakcje**: Brak
- **Obsługiwana walidacja**: Brak
- **Typy**: Brak specyficznych
- **Propsy**:
  - isVisible: Boolean

### ErrorMessage
- **Opis komponentu**: Komunikat błędu wyświetlany w przypadku nieudanego logowania.
- **Główne elementy**:
  - Surface z:
    - Row z:
      - Icon (błąd)
      - Text z komunikatem błędu
- **Obsługiwane interakcje**: Brak
- **Obsługiwana walidacja**: Brak
- **Typy**: Brak specyficznych
- **Propsy**:
  - message: String
  - isVisible: Boolean

## 5. Typy
### DTO
```kotlin
// Dane logowania
data class LoginCredentials(
    val email: String,    // Adres e-mail użytkownika
    val password: String  // Hasło użytkownika
)
```

### ViewModels
```kotlin
// Stan UI dla ekranu logowania
data class LoginUiState(
    val email: String = "",              // Wprowadzony e-mail
    val password: String = "",           // Wprowadzone hasło
    val isPasswordVisible: Boolean = false, // Czy hasło jest widoczne
    val emailError: String? = null,      // Błąd walidacji e-maila
    val passwordError: String? = null,   // Błąd walidacji hasła
    val isLoading: Boolean = false,      // Czy trwa logowanie
    val errorMessage: String? = null     // Ogólny komunikat błędu
)
```

## 6. Zarządzanie stanem
Stan widoku Logowania będzie zarządzany przez LoginViewModel, który będzie odpowiedzialny za:
- Przechowywanie wprowadzonych danych logowania
- Walidację wprowadzonych danych
- Komunikację z AuthRepository w celu logowania użytkownika
- Zarządzanie stanem UI (ładowanie, błędy)

```kotlin
class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Stan UI jako MutableState
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Aktualizacja e-maila
    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = validateEmail(email)
        )
    }

    // Aktualizacja hasła
    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = validatePassword(password)
        )
    }

    // Przełączanie widoczności hasła
    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
    }

    // Logowanie
    fun login() {
        // Sprawdzenie poprawności danych
        val currentState = _uiState.value
        val emailError = validateEmail(currentState.email)
        val passwordError = validatePassword(currentState.password)

        if (emailError != null || passwordError != null) {
            _uiState.value = currentState.copy(
                emailError = emailError,
                passwordError = passwordError
            )
            return
        }

        // Rozpoczęcie logowania
        _uiState.value = currentState.copy(
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            try {
                // Próba logowania
                authRepository.login(
                    LoginCredentials(
                        email = currentState.email,
                        password = currentState.password
                    )
                )
                
                // Logowanie udane - nie trzeba aktualizować stanu,
                // ponieważ nastąpi nawigacja do głównego ekranu
            } catch (e: Exception) {
                // Obsługa błędu logowania
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Wystąpił błąd podczas logowania"
                )
            }
        }
    }

    // Walidacja e-maila
    private fun validateEmail(email: String): String? {
        return if (email.isBlank()) {
            "E-mail jest wymagany"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            "Niepoprawny format adresu e-mail"
        } else {
            null
        }
    }

    // Walidacja hasła
    private fun validatePassword(password: String): String? {
        return if (password.isBlank()) {
            "Hasło jest wymagane"
        } else {
            null
        }
    }

    // Czyszczenie błędu ogólnego
    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }
}
```

## 7. Integracja API
Widok będzie korzystać z AuthRepository do logowania użytkownika:

### AuthRepository
- Logowanie użytkownika:
  - Zapytanie: `authRepository.login(loginCredentials)`
  - Parametry: LoginCredentials (email, password)
  - Typ odpowiedzi: Brak (Success) lub wyjątek (Failure)

## 8. Interakcje użytkownika
### Logowanie
- Użytkownik wprowadza adres e-mail
  - Wywołanie metody `viewModel.updateEmail(email)`
  - Walidacja formatu e-maila
  - Wyświetlenie błędu w przypadku niepoprawnego formatu

- Użytkownik wprowadza hasło
  - Wywołanie metody `viewModel.updatePassword(password)`
  - Walidacja niepustego hasła
  - Wyświetlenie błędu w przypadku pustego hasła

- Użytkownik przełącza widoczność hasła
  - Wywołanie metody `viewModel.togglePasswordVisibility()`
  - Zmiana wyświetlania hasła (widoczne/ukryte)

- Użytkownik klika przycisk "Zaloguj się"
  - Wywołanie metody `viewModel.login()`
  - Wyświetlenie wskaźnika ładowania
  - W przypadku powodzenia, nawigacja do głównego ekranu
  - W przypadku błędu, wyświetlenie komunikatu błędu

### Nawigacja
- Użytkownik klika link "Zapomniałeś hasła?"
  - Nawigacja do ekranu resetowania hasła

- Użytkownik klika przycisk "Zarejestruj się"
  - Nawigacja do ekranu rejestracji

## 9. Warunki i walidacja
### Walidacja e-maila
- E-mail nie może być pusty
- E-mail musi mieć poprawny format (zgodny z Patterns.EMAIL_ADDRESS)

### Walidacja hasła
- Hasło nie może być puste

### Walidacja formularza
- Przycisk "Zaloguj się" jest aktywny tylko jeśli wszystkie pola są poprawnie wypełnione
- Błędy walidacji są wyświetlane pod odpowiednimi polami

## 10. Obsługa błędów
- **Niepoprawne dane logowania**:
  - Wyświetlenie komunikatu "Niepoprawny e-mail lub hasło"
  - Możliwość ponowienia próby logowania

- **Użytkownik nie istnieje**:
  - Wyświetlenie komunikatu "Użytkownik o podanym adresie e-mail nie istnieje"
  - Sugestia rejestracji

- **Konto wyłączone**:
  - Wyświetlenie komunikatu "Konto zostało wyłączone, skontaktuj się z administratorem"

- **Brak połączenia z internetem**:
  - Wyświetlenie komunikatu "Brak połączenia z internetem"
  - Możliwość ponowienia próby logowania

- **Inne błędy Firebase Authentication**:
  - Wyświetlenie odpowiedniego komunikatu błędu
  - Możliwość ponowienia próby logowania

## 11. Kroki implementacji
1. **Utworzenie modeli danych**:
   - Definicja LoginCredentials
   - Definicja LoginUiState

2. **Implementacja LoginViewModel**:
   - Utworzenie klasy viewmodelu
   - Implementacja metod aktualizacji stanu
   - Implementacja logiki walidacji
   - Implementacja logiki logowania

3. **Implementacja komponentów UI**:
   - Utworzenie LoginScreen jako głównego komponentu
   - Implementacja TopLogo
   - Implementacja LoginForm z komponentami EmailField, PasswordField, LoginButton, ForgotPasswordLink
   - Implementacja RegistrationLink
   - Implementacja LoadingIndicator
   - Implementacja ErrorMessage

4. **Integracja z nawigacją**:
   - Dodanie ekranu logowania do grafu nawigacji
   - Konfiguracja nawigacji do/z ekranu logowania

5. **Integracja z AuthRepository**:
   - Implementacja logiki logowania z Firebase Authentication
   - Obsługa różnych przypadków użycia

6. **Implementacja obsługi błędów**:
   - Obsługa różnych błędów Firebase Authentication
   - Wyświetlanie odpowiednich komunikatów błędów

7. **Testowanie**:
   - Testowanie walidacji formularza
   - Testowanie różnych scenariuszy logowania
   - Testowanie obsługi błędów
   - Testowanie nawigacji

8. **Optymalizacje UI/UX**:
   - Dodanie animacji podczas przejść między ekranami
   - Implementacja automatycznego wypełniania danych logowania (jeśli dostępne)
   - Sprawdzenie dostępności UI (accessibility) 