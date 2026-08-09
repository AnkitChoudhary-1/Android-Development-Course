# 🔌 Complete Guide to APIs and JSON for Android Developers

![APIs and JSON](./apis.png)

---

## 🍽️ Part 1: What is an API?

### 💡 Starting With a Problem
Imagine you are building a weather app for Android. Your app needs to show today's temperature, humidity, wind speed, and a 7-day forecast.

> ❓ **Where does this weather data come from?**
> You cannot measure global temperature yourself — you don't own satellites or thousands of sensors. But companies like OpenWeatherMap do! They have servers full of live weather information.

How do you access their data from your Android app? **The answer is an API.**

---

### 🍽️ The Restaurant Analogy — The Best Way to Understand APIs

```
┌─────────────────────────────────────────────────────────────┐
│                      RESTAURANT                             │
│                                                             │
│  ┌──────────┐      ┌──────────┐      ┌──────────────────┐  │
│  │          │      │          │      │                  │  │
│  │   YOU    │      │  WAITER  │      │     KITCHEN      │  │
│  │(Customer)│      │  (API)   │      │    (Server/DB)   │  │
│  │          │      │          │      │                  │  │
│  └──────────┘      └──────────┘      └──────────────────┘  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

1. **STEP 1: You look at the MENU** → API documentation tells developers what requests they can make.
2. **STEP 2: You tell the WAITER your order** → Your Android app sends an HTTP request in a format the API understands.
3. **STEP 3: The WAITER takes your order to the KITCHEN** → The API communicates with the server/database behind the scenes.
4. **STEP 4: The KITCHEN prepares the food** → The server processes the request, queries the database, and prepares data.
5. **STEP 5: WAITER brings food back to YOU** → The API returns data to your app in **JSON** format.
6. **STEP 6: YOU enjoy the meal** → Your Android app parses the JSON and renders it on screen.

---

### 📖 The Formal Definition

> 🔌 **API (Application Programming Interface):** A set of defined rules that allows two software applications to talk to each other.

- **Simple Version:** An API is a waiter sitting between your app and a server, taking requests and bringing back responses.
- **Key Insight:** You don't need to know **HOW** the server works internally. You only need to know **WHAT** to ask for and **HOW** to format your request.

---

### 📱 Real-World Examples of APIs in Action

- 🌤️ **Weather Apps:** OpenWeatherMap API returns live temperatures.
- 🗺️ **Ola / Uber:** Google Maps API provides maps, turn-by-turn navigation, and live traffic.
- 💳 **Swiggy / Zomato:** Razorpay / PayU API handles secure online payments.
- 🔐 **Google Sign-In:** Google Auth API verifies user identity without storing passwords.
- 📱 **Android System:** Camera2 API allows apps to capture hardware photos safely.

---

## 🌐 Part 2: What is a REST API?

### 🔀 Types of APIs
- **REST:** Most popular style for web & mobile apps (uses HTTP + JSON).
- **GraphQL:** Flexible query language where clients ask for exact fields.
- **gRPC:** High-performance RPC system used for internal microservice communication.

---

### 🏛️ The 6 Principles of REST (Representational State Transfer)

1. **Client-Server Separation:** UI logic remains in the Android app; data & business logic stay on the server.
2. **Stateless:** Every request from client to server must contain **ALL** credentials & context needed. The server remembers no state between requests.
3. **Uniform Interface:** Standardized HTTP methods (`GET`, `POST`, `PUT`, `DELETE`) and URLs.
4. **Cacheable:** Server responses declare whether data can be saved locally to reduce network usage.
5. **Layered System:** Clients connect to API endpoints without needing to manage proxies, load balancers, or gateways.
6. **Resource-Oriented:** Every entity (user, order, photo) is a **Resource** identified by a unique URL.

---

### 📍 Resources and Endpoints

```
PATTERNS:
  /resource-name          → Collection of items
  /resource-name/{id}     → One specific item

FOOD DELIVERY API ENDPOINTS:
  GET    /restaurants         → Fetch all restaurants
  GET    /restaurants/42      → Fetch restaurant #42
  POST   /restaurants         → Create a new restaurant
  PUT    /restaurants/42      → Update restaurant #42
  DELETE /restaurants/42      → Delete restaurant #42
```

---

## 📱 Part 3: How an Android App Uses an API (Step-by-Step)

```
SCENARIO: User opens Movie App → App loads popular movies

1. User opens App → Trigger code in Fragment/Activity
2. App determines Endpoint: GET https://api.themoviedb.org/3/movie/popular
3. Switch to Background Thread (IO Dispatcher)
4. Construct HTTP Request (Headers + Auth token)
5. Send Request over Internet (DNS + HTTPS Encryption)
6. Server processes request & returns HTTP 200 OK + JSON
7. Android App parses JSON into Kotlin objects (deserialization)
8. Switch to Main Thread & update UI (RecyclerView)
```

```
VISUAL FLOW:

┌────────────────┐         ┌─────────────────┐        ┌──────────────┐
│  ANDROID APP   │         │    INTERNET     │        │   TMDB API   │
│ 1. User opens  │         │                 │        │   SERVER     │
│ 2. Show spinner│         │                 │        │              │
│ 3. Background  │──GET────────────────────────────→  │ 4. Validate   │
│    thread call │         │                 │        │    API key   │
│                │         │                 │        │ 5. Query DB  │
│                │←─200 OK─────────────────────────── │ 6. Return    │
│                │  + JSON │                 │        │    JSON      │
│ 7. Parse JSON  │         │                 │        │              │
│ 8. Main Thread │         │                 │        │              │
│    updates UI  │         │                 │        │              │
└────────────────┘         └─────────────────┘        └──────────────┘
```

---

## 📦 Part 4: What is JSON?

> 📦 **JSON (JavaScript Object Notation):** A lightweight, language-independent, text-based data format for exchanging structured data across networks.

### 🆚 JSON vs XML

```xml
<!-- XML (Old way) -->
<restaurant>
  <name>Biryani House</name>
  <rating>4.5</rating>
  <isOpen>true</isOpen>
</restaurant>
```

```json
// JSON (Modern way)
{
  "name": "Biryani House",
  "rating": 4.5,
  "isOpen": true
}
```

- ✅ **Compact:** Less payload overhead over cellular networks.
- ✅ **Human-Readable:** Easily inspectable during debugging.
- ✅ **Native Mapping:** Directly serializes to Kotlin objects.

---

## 🔤 Part 5: JSON Syntax & Data Types

JSON supports exactly **6 primitive & complex data types**:

```json
{
  "name": "Biryani House",             // 1. String (double quotes)
  "rating": 4.5,                       // 2. Number (integer or decimal)
  "isOpen": true,                      // 3. Boolean (true / false)
  "discount": null,                    // 4. Null (no value)
  "address": {                         // 5. Object (nested key-values)
    "city": "Bangalore",
    "pincode": "560001"
  },
  "cuisines": ["Indian", "Mughlai"]     // 6. Array (ordered list)
}
```

> [!CAUTION]
> **COMMON JSON SYNTAX ERRORS:**
> 1. Single quotes (`'name'`) → **MUST** use double quotes (`"name"`).
> 2. Keys without quotes (`name: "Rohit"`) → **Keys MUST be quoted**.
> 3. Trailing commas (`"age": 24, }`) → **No trailing comma on the last item**.
> 4. Capitalized booleans (`"isOpen": True`) → **Booleans MUST be lowercase** (`true` / `false`).
> 5. Comments (`// comment`) → **JSON does NOT support comments**.

---

## 📖 Part 6: Reading Real JSON Responses

### 🌦️ Weather API Example

```json
{
  "city": "Bangalore",
  "coordinates": {
    "latitude": 12.9716,
    "longitude": 77.5946
  },
  "current": {
    "temperature": 28.5,
    "humidity": 65,
    "isDay": true
  },
  "forecast": [
    { "day": "Tuesday", "maxTemp": 31.0, "rainChance": 10 },
    { "day": "Wednesday", "maxTemp": 29.0, "rainChance": 25 }
  ]
}
```

---

### 🚨 Error Response Example

```json
{
  "success": false,
  "statusCode": 401,
  "error": "Unauthorized",
  "message": "Invalid API key provided.",
  "timestamp": "2024-01-15T14:30:00Z"
}
```

---

## 🔑 Part 7: What is an API Key?

An **API Key** is a unique secret string identifying the requesting developer or application.

```
WHY APIS REQUIRE KEYS:

1. Identification → Distinguishes legitimate apps from malicious scripts.
2. Rate Limiting  → Prevents server overload (e.g. max 1,000 req/day for Free Tier).
3. Billing        → Tracks paid API usage per account.
4. Access Control → Revokes compromised keys instantly without shutting down the API.
```

```http
// Preferred: Authorizing via Authorization Header
GET /3/movie/popular HTTP/1.1
Host: api.themoviedb.org
Authorization: Bearer eyJhbGciOiJSUzI1NiJ9...
```

> [!WARNING]
> **SECURITY WARNING:** Never hardcode secret API keys directly inside Kotlin source code. Reverse engineering tools can extract string constants from `.apk` files. Store API keys securely inside `local.properties` and inject them via `BuildConfig`.

---

## 📬 Part 8: What is Postman?

> 📬 **Postman:** An industry-standard API client GUI for sending requests, inspecting raw JSON responses, testing headers, and debugging backends without building Android frontend code.

```
POSTMAN WORKFLOW:
1. Select Method (GET / POST / PUT / DELETE)
2. Input API Endpoint URL
3. Add Headers (Authorization, Content-Type) & Body
4. Press "Send"
5. Inspect Status Code, Execution Time (ms), and JSON Payload
```

---

## 💻 Part 9: How JSON Connects to Kotlin Code

### 🔄 Deserialization: JSON → Kotlin Data Class

```json
{
  "id": 872585,
  "title": "Oppenheimer",
  "rating": 8.1,
  "genres": [
    { "id": 18, "name": "Drama" }
  ]
}
```

```kotlin
// Matching Kotlin Data Classes:
data class Movie(
    val id: Int,
    val title: String,
    val rating: Double,
    val genres: List<Genre>
)

data class Genre(
    val id: Int,
    val name: String
)
```

```kotlin
// Clean Retrofit API Call in Android ViewModel:
class MovieViewModel(private val apiService: MovieApiService) : ViewModel() {

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    fun fetchPopularMovies() {
        viewModelScope.launch {
            try {
                // Retrofit automatically deserializes JSON to MovieResponse
                val response = apiService.getPopularMovies()
                _movies.value = response.results
            } catch (e: Exception) {
                // Handle network error
            }
        }
    }
}
```

---

## 📊 Complete Summary Cheat Sheet

| Concept | Key Points |
| :--- | :--- |
| **API** | Waiter bridge between Android app and backend server. |
| **REST API** | Standardized architectural style using HTTP methods + endpoints + JSON payload. |
| **JSON** | Lightweight text format for structured data. 6 types: String, Number, Boolean, Null, Object `{ }`, Array `[ ]`. |
| **API Key** | Secret key for identifying clients, enforcing rate limits, and billing. Never commit to Git! |
| **Postman** | API debugging tool to test endpoints before writing Android Kotlin code. |
| **Deserialization** | Automatic conversion of incoming raw JSON strings into Kotlin `data class` objects via Moshi / Gson. |

---

## ❓ 5 Questions to Test Your Understanding

### 🎯 Question 1: REST API Endpoint Design
> Design REST API endpoints for a Library System (Books, Members, Borrows):
> - **a)** Get all books
> - **b)** Get details for book ID 156
> - **c)** Add a new book
> - **d)** Borrow book ID 156 by member ID 42

---

### ❓ Question 2: JSON Analysis
> Inspect this JSON and answer:
> ```json
> {
>   "matchId": "IND-AUS-2024-01",
>   "status": "live",
>   "venue": { "city": "Mumbai", "capacity": 33108 },
>   "currentBatsmen": [
>     { "name": "Virat Kohli", "runs": 82, "isOnStrike": true }
>   ]
> }
> ```
> - **a)** What is the data type of `capacity`?
> - **b)** What is `currentBatsmen`?
> - **c)** Write the field access path for `city`.

---

### 📐 Question 3: Writing JSON
> Write a valid JSON object representing a User Profile (User ID, name, email, height decimal, list of 3 fitness goals, nested workout schedule object, null for deleted field).

---

### 🔒 Question 4: Security Audit
> A developer hardcodes an API key in Kotlin and pushes the repo to public GitHub:
> - **a)** Identify 3 security vulnerabilities.
> - **b)** How should `local.properties` and `BuildConfig` be configured to fix this?

---

### 🚀 Question 5: End-to-End Comprehension
> A recipe app searches for `"chocolate cake"`:
> - **a)** Which HTTP method is used and why?
> - **b)** Write the corresponding Kotlin `data class` matching a recipe JSON item.
> - **c)** What does HTTP status `429 Too Many Requests` mean, and how should your app respond?