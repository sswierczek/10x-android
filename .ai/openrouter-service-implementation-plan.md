# Plan Implementacji Usługi OpenRouter

## 1. Opis Usługi

Usługa OpenRouter to implementacja oparta na Kotlinie, która integruje się z API OpenRouter w celu zapewnienia funkcjonalności czatu opartego na LLM. Usługa została zaprojektowana jako modułowa, łatwa w utrzymaniu i bezpieczna, zgodnie z zasadami czystej architektury i wzorcami wstrzykiwania zależności.

## 2. Opis Konstruktora

Usługa zostanie zaimplementowana jako klasa singleton wykorzystująca wstrzykiwanie zależności Hilt. Konstruktor będzie wymagał:

```kotlin
@Inject
constructor(
    private val apiService: OpenRouterApiService,
    private val json: Json,
    @ApplicationContext private val context: Context
)
```

## 3. Publiczne Metody i Pola

### Metody

1. `generateResponse`
```kotlin
suspend fun generateResponse(
    messages: List<OpenRouterMessage>,
    model: String = "openai/gpt-3.5-turbo",
    temperature: Double = 0.7,
    maxTokens: Int = 1000,
    responseFormat: JsonObject? = null
): Flow<String>
```

2. `generateStructuredResponse`
```kotlin
suspend fun <T> generateStructuredResponse(
    messages: List<OpenRouterMessage>,
    model: String = "openai/gpt-3.5-turbo",
    responseSchema: JsonObject,
    responseType: KClass<T>
): Flow<T>
```

3. `clearConversation`
```kotlin
fun clearConversation()
```

### Pola

1. `conversationHistory`
```kotlin
private val _conversationHistory = MutableStateFlow<List<OpenRouterMessage>>(emptyList())
val conversationHistory: StateFlow<List<OpenRouterMessage>> = _conversationHistory.asStateFlow()
```

## 4. Prywatne Metody i Pola

### Metody

1. `createSystemMessage`
```kotlin
private fun createSystemMessage(content: String): OpenRouterMessage
```

2. `createUserMessage`
```kotlin
private fun createUserMessage(content: String): OpenRouterMessage
```

3. `createAssistantMessage`
```kotlin
private fun createAssistantMessage(content: String): OpenRouterMessage
```

4. `handleError`
```kotlin
private fun handleError(error: Throwable): Flow<String>
```

### Pola

1. `systemPrompt`
```kotlin
private val systemPrompt = """
    Jesteś pomocnym asystentem AI. Twoje odpowiedzi powinny być jasne, zwięzłe i dokładne.
    Na pytania o filmy odpowiadaj na podstawie swojej bazy wiedzy.
""".trimIndent()
```

## 5. Obsługa Błędów

### Scenariusze Błędów

1. Błędy Sieciowe
   - Przekroczenie czasu oczekiwania na połączenie
   - Brak połączenia internetowego
   - Ograniczenie liczby zapytań API
   - Nieprawidłowy klucz API

2. Błędy Odpowiedzi
   - Nieprawidłowy format odpowiedzi
   - Pusta odpowiedź
   - Nieprawidłowy JSON
   - Błąd walidacji schematu

3. Błędy Logiki Biznesowej
   - Nieprawidłowa nazwa modelu
   - Nieprawidłowy format wiadomości
   - Przekroczenie limitu tokenów
   - Przekroczenie długości kontekstu

### Implementacja Obsługi Błędów

```kotlin
sealed class OpenRouterError : Exception() {
    data class NetworkError(override val message: String) : OpenRouterError()
    data class ResponseError(override val message: String) : OpenRouterError()
    data class BusinessError(override val message: String) : OpenRouterError()
}
```

## 6. Zagadnienia Bezpieczeństwa

1. Zarządzanie Kluczem API
   - Przechowywanie klucza API w local.properties
   - Używanie BuildConfig do dostępu w czasie wykonania
   - Nigdy nie ujawniaj klucza API w logach ani komunikatach błędów

2. Walidacja Zapytań
   - Waliduj wszystkie parametry wejściowe
   - Sanityzuj wiadomości użytkownika
   - Zaimplementuj ograniczenie liczby zapytań
   - Dodaj limity czasu oczekiwania na zapytania

3. Walidacja Odpowiedzi
   - Waliduj format odpowiedzi
   - Zaimplementuj walidację schematu
   - Sanityzuj zawartość odpowiedzi
   - Obsługuj wrażliwe informacje

## 7. Kroki Implementacji

### Krok 1: Konfiguracja Zależności

1. Dodaj wtyczkę Kotlin serialization
2. Dodaj wymagane zależności
3. Skonfiguruj build.gradle.kts

### Krok 2: Utworzenie Modeli Danych

1. Utwórz klasę danych OpenRouterMessage
2. Utwórz klasę danych OpenRouterRequest
3. Utwórz klasę danych OpenRouterResponse
4. Utwórz klasy obsługi błędów

### Krok 3: Implementacja Usługi API

1. Utwórz interfejs OpenRouterApiService
2. Zaimplementuj endpointy API
3. Dodaj adnotacje serializacji
4. Skonfiguruj instancję Retrofit

### Krok 4: Utworzenie Warstwy Repozytorium

1. Utwórz interfejs OpenRouterRepository
2. Zaimplementuj OpenRouterRepositoryImpl
3. Dodaj obsługę błędów
4. Zaimplementuj parsowanie odpowiedzi

### Krok 5: Utworzenie Warstwy Usługi

1. Utwórz klasę OpenRouterService
2. Zaimplementuj zarządzanie konwersacją
3. Dodaj formatowanie odpowiedzi
4. Zaimplementuj obsługę błędów

### Krok 6: Konfiguracja Wstrzykiwania Zależności

1. Utwórz OpenRouterModule
2. Zapewnij zależności
3. Skonfiguruj zakresy
4. Dodaj interceptory obsługi błędów

### Krok 7: Dodanie Wsparcia dla Formatowania Odpowiedzi

1. Utwórz konstruktor schematu JSON
2. Zaimplementuj walidację schematu
3. Dodaj bezpieczne parsowanie typów
4. Obsługuj błędy walidacji

### Krok 8: Testowanie

1. Dodaj testy jednostkowe
2. Dodaj testy integracyjne
3. Dodaj testy scenariuszy błędów
4. Dodaj testy wydajności

## 8. Przykłady Użycia

### Podstawowy Czat

```kotlin
val messages = listOf(
    OpenRouterMessage(role = "user", content = "Opowiedz mi o filmie Incepcja")
)

viewModel.generateResponse(messages)
    .collect { response ->
        // Obsłuż odpowiedź
    }
```

### Strukturyzowana Odpowiedź

```kotlin
val schema = JsonObject(
    mapOf(
        "type" to "object",
        "properties" to JsonObject(
            mapOf(
                "title" to JsonObject(mapOf("type" to "string")),
                "year" to JsonObject(mapOf("type" to "integer")),
                "rating" to JsonObject(mapOf("type" to "number"))
            )
        ),
        "required" to JsonArray(listOf("title", "year", "rating"))
    )
)

viewModel.generateStructuredResponse<MovieInfo>(
    messages = messages,
    responseSchema = schema
).collect { movieInfo ->
    // Obsłuż strukturyzowaną odpowiedź
}
```

## 9. Najlepsze Praktyki

1. Zawsze używaj korutyn do operacji asynchronicznych
2. Zaimplementuj prawidłową obsługę błędów
3. Używaj strukturyzowanych odpowiedzi, gdy to możliwe
4. Waliduj wszystkie dane wejściowe i wyjściowe
5. Przestrzegaj wytycznych bezpieczeństwa
6. Zaimplementuj prawidłowe logowanie
7. Używaj wstrzykiwania zależności
8. Przestrzegaj zasad czystej architektury 