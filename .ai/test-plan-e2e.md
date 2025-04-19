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
class CustomTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
```

## Przypadek testowy: Kompletny przepływ dziennika filmowego

### Scenariusz testowy: Podróż użytkownika - Dodawanie i ocenianie filmu
Ten test zweryfikuje kompletny przepływ użytkownika logującego się, dodającego film do swojego dziennika, oceniającego go i przeglądającego rekomendacje.

### Kroki testowe:
1. Przepływ logowania
   - Uruchomienie aplikacji do ekranu logowania
   - Wprowadzenie prawidłowych danych uwierzytelniających
   - Weryfikacja pomyślnej nawigacji do ekranu dziennika

2. Przepływ dodawania filmu
   - Przejście do ekranu dodawania filmu
   - Wprowadzenie szczegółów filmu:
     - Tytuł: "Skazani na Shawshank"
     - Ocena: 9.3
     - Recenzja: "Arcydzieło o nadziei i przyjaźni"
   - Zatwierdzenie wpisu filmu
   - Weryfikacja pojawienia się filmu na liście dziennika

3. Przeglądanie szczegółów filmu
   - Kliknięcie dodanego filmu
   - Weryfikacja, czy ekran szczegółów filmu pokazuje:
     - Prawidłowy tytuł
     - Prawidłową ocenę
     - Prawidłową recenzję
   - Aktualizacja oceny do 9.5
   - Weryfikacja odzwierciedlenia aktualizacji oceny

4. Sprawdzanie rekomendacji
   - Przejście do ekranu rekomendacji
   - Weryfikacja załadowania rekomendacji opartych na AI
   - Weryfikacja, czy rekomendacje są oparte na dodanym filmie

### Przykład implementacji:
```kotlin
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CompleteUserJourneyTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun completeUserJourney() {
        // Przepływ logowania
        composeTestRule.onNodeWithText("Email").performTextInput("testuser@testuser.pl")
        composeTestRule.onNodeWithText("Hasło").performTextInput(BuildConfig.TESTUSER_PASSWORD)
        composeTestRule.onNodeWithText("Zaloguj").performClick()
        composeTestRule.onNodeWithText("Mój Dziennik").assertExists()

        // Przepływ dodawania filmu
        composeTestRule.onNodeWithContentDescription("Dodaj Film").performClick()
        composeTestRule.onNodeWithText("Tytuł").performTextInput("Skazani na Shawshank")
        composeTestRule.onNodeWithText("Ocena").performTextInput("9.3")
        composeTestRule.onNodeWithText("Recenzja").performTextInput("Arcydzieło o nadziei i przyjaźni")
        composeTestRule.onNodeWithText("Zapisz").performClick()

        // Weryfikacja filmu w dzienniku
        composeTestRule.onNodeWithText("Skazani na Shawshank").assertExists()

        // Przeglądanie i aktualizacja szczegółów filmu
        composeTestRule.onNodeWithText("Skazani na Shawshank").performClick()
        composeTestRule.onNodeWithText("9.3").assertExists()
        composeTestRule.onNodeWithContentDescription("Aktualizuj Ocenę").performClick()
        composeTestRule.onNodeWithText("Ocena").performTextInput("9.5")
        composeTestRule.onNodeWithText("Zapisz").performClick()
        composeTestRule.onNodeWithText("9.5").assertExists()

        // Sprawdzanie rekomendacji
        composeTestRule.onNodeWithText("Rekomendacje").performClick()
        composeTestRule.onNodeWithContentDescription("Wskaźnik ładowania AI").assertExists()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Na podstawie twoich preferencji").assertExists()
    }
}
```

### Wymagania dotyczące danych testowych:
- Konto testowe: testuser@testuser.pl z hasłem zdefiniowanym w BuildConfig.TESTUSER_PASSWORD
- Zamockowane odpowiedzi dla API danych filmów
- Zamockowane odpowiedzi dla API rekomendacji

### Wymagania środowiska testowego:
1. Czysty stan aplikacji przed każdym testem
2. Zamockowane zależności zewnętrzne:
   - Firebase Authentication
   - API bazy danych filmów
   - Serwis rekomendacji

### Kryteria sukcesu:
1. Wszystkie interakcje UI wykonują się bez błędów
2. Weryfikacja persystencji danych między ekranami
3. Zarządzanie stanem (MVVM) poprawnie aktualizuje UI
4. Przepływ nawigacji kończy się zgodnie z oczekiwaniami
5. Wszystkie asercje przechodzą

### Uwagi:
- Test wykorzystuje API testowe Compose zamiast Espresso
- Hilt dostarcza zależności testowe
- Test koncentruje się na funkcjonalności widocznej dla użytkownika
- Obsługuje operacje asynchroniczne za pomocą waitForIdle()
- Weryfikuje zarówno elementy UI, jak i przepływ danych 