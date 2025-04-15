# Plan implementacji widoku Rejestracji

## 1. Przegląd
Widok Rejestracji umożliwia nowym użytkownikom utworzenie konta w aplikacji. Zawiera formularz z polami do wprowadzenia adresu e-mail, hasła oraz potwierdzenia hasła. Po pomyślnej rejestracji, użytkownik zostaje przekierowany do głównego widoku aplikacji lub opcjonalnie do strony uzupełnienia profilu. Widok zawiera również link powrotu do ekranu logowania dla użytkowników posiadających już konto.

## 2. Routing widoku
Ścieżka widoku: `/register`

Ten widok jest dostępny przez:
- Link "Nie masz konta? Zarejestruj się" z ekranu logowania
- Bezpośredni dostęp przez URL

## 3. Struktura komponentów
```
RegisterScreen
├── TopLogo
├── RegisterForm
│   ├── EmailField
│   ├── PasswordField
│   ├── ConfirmPasswordField
│   ├── RegisterButton
│   └── TermsAndConditionsCheckbox
├── LoginLink
├── LoadingIndicator (wyświetlany podczas rejestracji)
└── ErrorMessage (wyświetlany w przypadku błędu)
```

## 4. Szczegóły komponentów
### RegisterScreen
- **Opis komponentu**: Główny ekran rejestracji, zawierający wszystkie komponenty potrzebne do utworzenia konta.
- **Główne elementy**:
  - Scaffold z Column jako głównym kontenerem
  - Komponenty rejestracji (opisane poniżej)
  - LoadingIndicator (widoczny tylko podczas próby rejestracji)
  - ErrorMessage (widoczny tylko po nieudanej próbie rejestracji)
- **Obsługiwane interakcje**:
  - Rejestracja
  - Nawigacja do ekranu logowania
- **Obsługiwana walidacja**: Brak na poziomie ekranu
- **Typy**: RegisterViewModel, RegisterUiState
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

### RegisterForm
- **Opis komponentu**: Formularz zawierający pola do wprowadzenia danych rejestracji.
- **Główne elementy**:
  - Column zawierająca:
    - EmailField
    - PasswordField
    - ConfirmPasswordField
    - TermsAndConditionsCheckbox
    - RegisterButton
- **Obsługiwane interakcje**:
  - Wprowadzanie danych rejestracji
  - Kliknięcie przycisku rejestracji
  - Zaznaczenie/odznaczenie akceptacji regulaminu
- **Obsługiwana walidacja**:
  - Walidacja e-maila (format)
  - Walidacja hasła (siła, długość)
  - Walidacja zgodności haseł
  - Walidacja akceptacji regulaminu
- **Typy**: RegisterCredentials
- **Propsy**:
  - onRegisterClick: (RegisterCredentials) -> Unit
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
  - Minimalna długość hasła
  - Wymagane znaki specjalne
- **Typy**: Brak specyficznych
- **Propsy**:
  - value: String
  - onValueChange: (String) -> Unit
  - error: String?
  - isEnabled: Boolean

### ConfirmPasswordField
- **Opis komponentu**: Pole tekstowe do potwierdzenia hasła.
- **Główne elementy**:
  - OutlinedTextField z:
    - Label "Potwierdź hasło"
    - Placeholder "Wprowadź hasło ponownie"
    - VisualTransformation dla ukrywania hasła
    - Ikona hasła
    - Przycisk pokazywania/ukrywania hasła
- **Obsługiwane interakcje**:
  - Wprowadzanie tekstu
  - Przełączanie widoczności hasła
  - Obsługa błędów walidacji
- **Obsługiwana walidacja**:
  - Zgodność z wprowadzonym hasłem
- **Typy**: Brak specyficznych
- **Propsy**:
  - value: String
  - onValueChange: (String) -> Unit
  - error: String?
  - isEnabled: Boolean

### TermsAndConditionsCheckbox
- **Opis komponentu**: Pole wyboru akceptacji regulaminu.
- **Główne elementy**:
  - Row z:
    - Checkbox
    - ClickableText "Akceptuję regulamin i politykę prywatności"
- **Obsługiwane interakcje**:
  - Zaznaczenie/odznaczenie checkboxa
  - Kliknięcie w link do regulaminu
  - Obsługa błędów walidacji
- **Obsługiwana walidacja**:
  - Checkbox musi być zaznaczony
- **Typy**: Brak specyficznych
- **Propsy**:
  - checked: Boolean
  - onCheckedChange: (Boolean) -> Unit
  - onTermsClick: () -> Unit
  - error: String?

### RegisterButton
- **Opis komponentu**: Przycisk do zatwierdzenia formularza rejestracji.
- **Główne elementy**:
  - Button z:
    - Text "Zarejestruj się"
    - W stanie ładowania: CircularProgressIndicator
- **Obsługiwane interakcje**:
  - Kliknięcie przycisku
- **Obsługiwana walidacja**: Brak
- **Typy**: Brak specyficznych
- **Propsy**:
  - onClick: () -> Unit
  - isEnabled: Boolean
  - isLoading: Boolean

### LoginLink
- **Opis komponentu**: Link do ekranu logowania.
- **Główne elementy**:
  - Row z:
    - Text "Masz już konto?"
    - TextButton z "Zaloguj się"
- **Obsługiwane interakcje**:
  - Kliknięcie przycisku "Zaloguj się"
- **Obsługiwana walidacja**: Brak
- **Typy**: Brak specyficznych
- **Propsy**:
  - onClick: () -> Unit

### LoadingIndicator
- **Opis komponentu**: Wskaźnik ładowania wyświetlany podczas rejestracji.
- **Główne elementy**:
  - CircularProgressIndicator
- **Obsługiwane interakcje**: Brak
- **Obsługiwana walidacja**: Brak
- **Typy**: Brak specyficznych
- **Propsy**:
  - isVisible: Boolean

### ErrorMessage
- **Opis komponentu**: Komunikat błędu wyświetlany w przypadku nieudanej rejestracji.
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
// Dane rejestracji
data class RegisterCredentials(
    val email: String,    // Adres e-mail użytkownika
    val password: String, // Hasło użytkownika
    val confirmPassword: String, // Potwierdzenie hasła
    val termsAccepted: Boolean   // Akceptacja regulaminu
)
```

### ViewModels
```kotlin
// Stan UI dla ekranu rejestracji
data class RegisterUiState(
    val email: String = "",              // Wprowadzony e-mail
    val password: String = "",           // Wprowadzone hasło
    val confirmPassword: String = "",    // Potwierdzenie hasła
    val termsAccepted: Boolean = false,  // Akceptacja regulaminu
    val isPasswordVisible: Boolean = false, // Czy hasło jest widoczne
    val isConfirmPasswordVisible: Boolean = false, // Czy potwierdzenie hasła jest widoczne
    val emailError: String? = null,      // Błąd walidacji e-maila
    val passwordError: String? = null,   // Błąd walidacji hasła
    val confirmPasswordError: String? = null, // Błąd walidacji potwierdzenia hasła
    val termsError: String? = null,      // Błąd walidacji regulaminu
    val isLoading: Boolean = false,      // Czy trwa rejestracja
    val errorMessage: String? = null     // Ogólny komunikat błędu
)
```

## 6. Zarządzanie stanem
Stan widoku Rejestracji będzie zarządzany przez RegisterViewModel, który będzie odpowiedzialny za:
- Przechowywanie wprowadzonych danych rejestracji
- Walidację wprowadzonych danych
- Komunikację z AuthRepository w celu rejestracji użytkownika
- Zarządzanie stanem UI (ładowanie, błędy)

```kotlin
class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Stan UI jako MutableState
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    // Aktualizacja e-maila
    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = validateEmail(email)
        )
    }

    // Aktualizacja hasła
    fun updatePassword(password: String) {
        val passwordError = validatePassword(password)
        val confirmPasswordError = validatePasswordMatch(password, _uiState.value.confirmPassword)
        
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError
        )
    }

    // Aktualizacja potwierdzenia hasła
    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = validatePasswordMatch(_uiState.value.password, confirmPassword)
        )
    }

    // Aktualizacja akceptacji regulaminu
    fun updateTermsAccepted(accepted: Boolean) {
        _uiState.value = _uiState.value.copy(
            termsAccepted = accepted,
            termsError = if (accepted) null else "Musisz zaakceptować regulamin"
        )
    }

    // Przełączanie widoczności hasła
    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
    }

    // Przełączanie widoczności potwierdzenia hasła
    fun toggleConfirmPasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isConfirmPasswordVisible = !_uiState.value.isConfirmPasswordVisible
        )
    }

    // Rejestracja
    fun register() {
        // Sprawdzenie poprawności wszystkich danych
        val currentState = _uiState.value
        val emailError = validateEmail(currentState.email)
        val passwordError = validatePassword(currentState.password)
        val confirmPasswordError = validatePasswordMatch(currentState.password, currentState.confirmPassword)
        val termsError = if (!currentState.termsAccepted) "Musisz zaakceptować regulamin" else null

        if (emailError != null || passwordError != null || confirmPasswordError != null || termsError != null) {
            _uiState.value = currentState.copy(
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError,
                termsError = termsError
            )
            return
        }

        // Rozpoczęcie rejestracji
        _uiState.value = currentState.copy(
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            try {
                // Próba rejestracji
                authRepository.register(
                    RegisterCredentials(
                        email = currentState.email,
                        password = currentState.password,
                        confirmPassword = currentState.confirmPassword,
                        termsAccepted = currentState.termsAccepted
                    )
                )
                
                // Rejestracja udana - nie trzeba aktualizować stanu,
                // ponieważ nastąpi nawigacja do głównego ekranu
            } catch (e: Exception) {
                // Obsługa błędu rejestracji
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Wystąpił błąd podczas rejestracji"
                )
            }
        }
    }

    // Walidacja e-maila
    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "E-mail jest wymagany"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Niepoprawny format adresu e-mail"
            else -> null
        }
    }

    // Walidacja hasła
    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Hasło jest wymagane"
            password.length < 8 -> "Hasło musi mieć co najmniej 8 znaków"
            !password.any { it.isDigit() } -> "Hasło musi zawierać co najmniej jedną cyfrę"
            !password.any { it.isUpperCase() } -> "Hasło musi zawierać co najmniej jedną wielką literę"
            !password.any { it.isLowerCase() } -> "Hasło musi zawierać co najmniej jedną małą literę"
            !password.any { !it.isLetterOrDigit() } -> "Hasło musi zawierać co najmniej jeden znak specjalny"
            else -> null
        }
    }

    // Walidacja zgodności haseł
    private fun validatePasswordMatch(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isBlank() -> "Potwierdzenie hasła jest wymagane"
            confirmPassword != password -> "Hasła nie są zgodne"
            else -> null
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
Widok będzie korzystać z AuthRepository do rejestracji użytkownika:

### AuthRepository
- Rejestracja użytkownika:
  - Zapytanie: `authRepository.register(registerCredentials)`
  - Parametry: RegisterCredentials (email, password, confirmPassword, termsAccepted)
  - Typ odpowiedzi: Brak (Success) lub wyjątek (Failure)

## 8. Interakcje użytkownika
### Rejestracja
- Użytkownik wprowadza adres e-mail
  - Wywołanie metody `viewModel.updateEmail(email)`
  - Walidacja formatu e-maila
  - Wyświetlenie błędu w przypadku niepoprawnego formatu

- Użytkownik wprowadza hasło
  - Wywołanie metody `viewModel.updatePassword(password)`
  - Walidacja wymagań dla hasła
  - Wyświetlenie błędu w przypadku niespełnienia wymagań

- Użytkownik wprowadza potwierdzenie hasła
  - Wywołanie metody `viewModel.updateConfirmPassword(confirmPassword)`
  - Walidacja zgodności z hasłem
  - Wyświetlenie błędu w przypadku niezgodności

- Użytkownik zaznacza akceptację regulaminu
  - Wywołanie metody `viewModel.updateTermsAccepted(accepted)`
  - Walidacja akceptacji
  - Wyświetlenie błędu w przypadku braku akceptacji

- Użytkownik przełącza widoczność hasła/potwierdzenia hasła
  - Wywołanie metody `viewModel.togglePasswordVisibility()` lub `viewModel.toggleConfirmPasswordVisibility()`
  - Zmiana wyświetlania hasła (widoczne/ukryte)

- Użytkownik klika przycisk "Zarejestruj się"
  - Wywołanie metody `viewModel.register()`
  - Wyświetlenie wskaźnika ładowania
  - W przypadku powodzenia, nawigacja do głównego ekranu
  - W przypadku błędu, wyświetlenie komunikatu błędu

### Nawigacja
- Użytkownik klika link "Masz już konto? Zaloguj się"
  - Nawigacja do ekranu logowania

## 9. Warunki i walidacja
### Walidacja e-maila
- E-mail nie może być pusty
- E-mail musi mieć poprawny format (zgodny z Patterns.EMAIL_ADDRESS)

### Walidacja hasła
- Hasło nie może być puste
- Hasło musi mieć co najmniej 8 znaków
- Hasło musi zawierać co najmniej jedną cyfrę
- Hasło musi zawierać co najmniej jedną wielką literę
- Hasło musi zawierać co najmniej jedną małą literę
- Hasło musi zawierać co najmniej jeden znak specjalny

### Walidacja potwierdzenia hasła
- Potwierdzenie hasła nie może być puste
- Potwierdzenie hasła musi być zgodne z wprowadzonym hasłem

### Walidacja akceptacji regulaminu
- Checkbox akceptacji regulaminu musi być zaznaczony

### Walidacja formularza
- Przycisk "Zarejestruj się" jest aktywny tylko jeśli wszystkie pola są poprawnie wypełnione
- Błędy walidacji są wyświetlane pod odpowiednimi polami

## 10. Obsługa błędów
- **Adres e-mail jest już używany**:
  - Wyświetlenie komunikatu "Adres e-mail jest już używany"
  - Sugestia logowania

- **Słabe hasło**:
  - Wyświetlenie komunikatu z wymaganiami dotyczącymi hasła
  - Możliwość wprowadzenia silniejszego hasła

- **Brak połączenia z internetem**:
  - Wyświetlenie komunikatu "Brak połączenia z internetem"
  - Możliwość ponowienia próby rejestracji

- **Błąd serwera**:
  - Wyświetlenie komunikatu "Wystąpił błąd podczas rejestracji"
  - Możliwość ponowienia próby rejestracji

- **Inne błędy Firebase Authentication**:
  - Wyświetlenie odpowiedniego komunikatu błędu
  - Możliwość ponowienia próby rejestracji

## 11. Kroki implementacji
1. **Utworzenie modeli danych**:
   - Definicja RegisterCredentials
   - Definicja RegisterUiState

2. **Implementacja RegisterViewModel**:
   - Utworzenie klasy viewmodelu
   - Implementacja metod aktualizacji stanu
   - Implementacja logiki walidacji
   - Implementacja logiki rejestracji

3. **Implementacja komponentów UI**:
   - Utworzenie RegisterScreen jako głównego komponentu
   - Implementacja TopLogo
   - Implementacja RegisterForm z komponentami EmailField, PasswordField, ConfirmPasswordField, TermsAndConditionsCheckbox, RegisterButton
   - Implementacja LoginLink
   - Implementacja LoadingIndicator
   - Implementacja ErrorMessage

4. **Integracja z nawigacją**:
   - Dodanie ekranu rejestracji do grafu nawigacji
   - Konfiguracja nawigacji do/z ekranu rejestracji

5. **Integracja z AuthRepository**:
   - Implementacja logiki rejestracji z Firebase Authentication
   - Obsługa różnych przypadków użycia

6. **Implementacja obsługi błędów**:
   - Obsługa różnych błędów Firebase Authentication
   - Wyświetlanie odpowiednich komunikatów błędów

7. **Testowanie**:
   - Testowanie walidacji formularza
   - Testowanie różnych scenariuszy rejestracji
   - Testowanie obsługi błędów
   - Testowanie nawigacji

8. **Optymalizacje UI/UX**:
   - Dodanie animacji podczas przejść między ekranami
   - Implementacja wskaźnika siły hasła
   - Sprawdzenie dostępności UI (accessibility)
