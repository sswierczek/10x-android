# Plan testów E2E dla aplikacji 10x Android

## Przegląd
Ten dokument przedstawia plan testów end-to-end dla aplikacji 10x Android, koncentrując się na testowaniu interfejsu użytkownika Jetpack Compose zintegrowanego z wstrzykiwaniem zależności Hilt i architekturą MVVM.

## Konfiguracja środowiska testowego

### Wymagane zależności
```kotlin
// Testowanie Compose
androidTestImplementation(platform(libs.androidx.compose.bom))
androidTestImplementation(libs.androidx.ui.test.junit4)
debugImplementation(libs.androidx.ui.test.manifest)

// Testowanie Hilt
androidTestImplementation("com.google.dagger:hilt-android-testing:${libs.versions.hilt.get()}")
kaptAndroidTest("com.google.dagger:hilt-android-compiler:${libs.versions.hilt.get()}")
```

### Niestandardowy Runner testów
```kotlin
@HiltAndroidTest
class HiltRunnerCustom : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
```

## Przypadek testowy: Kompletny przepływ użytkownika

### Scenariusz testowy: Podróż użytkownika - Wyszukiwanie i ocenianie filmu
Ten test weryfikuje kompletny przepływ użytkownika logującego się, wyszukującego film, dodającego go do dziennika i sprawdzającego rekomendacje.

### Kroki testowe:
1. Przepływ logowania
   - Wprowadzenie danych logowania:
     - Email: testuser@testuser.pl
     - Hasło: [z BuildConfig.TESTUSER_PASSWORD]
   - Weryfikacja pomyślnego zalogowania poprzez obecność ekranu dziennika

2. Przepływ dodawania filmu
   - Kliknięcie przycisku dodawania filmu (identyfikator: "addmoviebutton")
   - Wyszukiwanie filmu:
     - Wprowadzenie tekstu "Shawshank" w polu wyszukiwania
     - Kliknięcie "Add to journal"
   - Weryfikacja dodania filmu poprzez sprawdzenie obecności tytułu "Skazani na Shawshank"

3. Przeglądanie szczegółów filmu
   - Kliknięcie na dodany film
   - Weryfikacja wyświetlenia oceny "9.3"
   - Aktualizacja oceny:
     - Kliknięcie przycisku aktualizacji oceny
     - Wprowadzenie nowej oceny "9.5"
     - Zapisanie zmian
   - Weryfikacja zaktualizowanej oceny

4. Sprawdzanie rekomendacji
   - Przejście do ekranu rekomendacji
   - Weryfikacja obecności wskaźnika ładowania AI
   - Sprawdzenie wyświetlenia rekomendacji opartych na preferencjach

### Przykład implementacji:
```kotlin
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CompleteUserJourneyTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var authRepository: AuthRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun completeUserJourney() {
        // Przepływ logowania
        composeRule.onNodeWithText("Email")
            .performTextInput("testuser@testuser.pl")
        composeRule.onNodeWithText("Password")
            .performTextInput(BuildConfig.TESTUSER_PASSWORD)
        composeRule.onNodeWithText("Login")
            .performClick()

        // Weryfikacja zalogowania
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText("Journal")
                .fetchSemanticsNodes().size == 1
        }

        // Dodawanie filmu
        composeRule.onNodeWithContentDescription("addmoviebutton")
            .performClick()
        composeRule.onNodeWithText("Find movies to rate them.")
            .performTextInput("Shawshank")
        composeRule.onNodeWithText("Add to journal")
            .performClick()

        // Weryfikacja dodania filmu
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText("Skazani na Shawshank")
                .fetchSemanticsNodes().size == 1
        }

        // Szczegóły i aktualizacja filmu
        composeRule.onNodeWithText("Skazani na Shawshank")
            .performClick()
        composeRule.onNodeWithText("9.3")
            .assertExists()
        composeRule.onNodeWithContentDescription("Aktualizuj Ocenę")
            .performClick()
        composeRule.onNodeWithText("Ocena")
            .performTextInput("9.5")
        composeRule.onNodeWithText("Zapisz")
            .performClick()

        // Weryfikacja aktualizacji
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText("9.5")
                .fetchSemanticsNodes().size == 1
        }

        // Sprawdzanie rekomendacji
        composeRule.onNodeWithText("Rekomendacje")
            .performClick()
        composeRule.onNodeWithContentDescription("Wskaźnik ładowania AI")
            .assertExists()
        composeRule.waitUntil(timeoutMillis = 10000) {
            composeRule.onAllNodesWithText("Na podstawie twoich preferencji")
                .fetchSemanticsNodes().size == 1
        }
    }
}
```

### Wymagania dotyczące danych testowych:
- Konto testowe: testuser@testuser.pl z hasłem zdefiniowanym w BuildConfig.TESTUSER_PASSWORD
- Zamockowane odpowiedzi dla API wyszukiwania filmów
- Zamockowane odpowiedzi dla API rekomendacji

### Wymagania środowiska testowego:
1. Czysty stan aplikacji przed każdym testem
2. Skonfigurowane zależności Hilt:
   - Wstrzyknięty AuthRepository
   - MainActivity jako główna aktywność testowa
3. Timeout dla operacji asynchronicznych: 5000ms (standardowy) i 10000ms (dla rekomendacji)

### Kryteria sukcesu:
1. Pomyślne zalogowanie użytkownika
2. Poprawne wyszukanie i dodanie filmu
3. Skuteczna aktualizacja oceny filmu
4. Wyświetlenie rekomendacji AI
5. Wszystkie operacje asynchroniczne kończą się w określonym czasie

### Uwagi techniczne:
- Wykorzystanie createAndroidComposeRule zamiast createComposeRule
- Zastosowanie waitUntil dla operacji asynchronicznych
- Weryfikacja elementów UI poprzez semantykę Compose
- Obsługa timeoutów dla różnych operacji
- Wstrzykiwanie zależności poprzez @Inject 