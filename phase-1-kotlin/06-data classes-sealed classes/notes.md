# 🗃️ Complete Guide to Data Classes and Sealed Classes in Kotlin

![Data and Sealed Classes](./data%20and%20sealed%20classes.png)

---

## ❓ Part 1: What is a Data Class and Why Does It Exist?

### 💡 Starting With the Problem

Imagine you are building an Android app and need to represent a User. With a regular class, you write this:

```kotlin
// REGULAR CLASS — what you have to deal with:
class User(val name: String, val email: String, val age: Int)

fun main() {
    val user1 = User("Rohit", "rohit@gmail.com", 24)
    val user2 = User("Rohit", "rohit@gmail.com", 24)

    // PROBLEM 1: Equality comparison is BROKEN
    println(user1 == user2)   // false ❌
    // They have identical data but Kotlin says they are NOT equal!
    // Regular classes compare by MEMORY REFERENCE, not content.
    // user1 and user2 are two different objects in memory → not equal.

    // PROBLEM 2: toString() is useless
    println(user1)   // User@7852e922 ❌
    // Shows memory address, not the actual data!
    // Completely useless for debugging.

    // PROBLEM 3: Copying is painful
    // You want a copy of user1 but with age changed to 25.
    // With regular class: you must manually create a new object:
    val updatedUser = User(user1.name, user1.email, 25)
    // What if User has 15 properties? You copy ALL 15 manually.
    // Miss one? Bug. Tedious and error-prone.

    // PROBLEM 4: Cannot destructure
    // val (name, email, age) = user1  // ❌ does not work with regular class
}
```

> [!IMPORTANT]
> All four problems disappear with a single `data` keyword.

---

### 🧩 What is a Data Class?

```text
A DATA CLASS is a class specifically designed to HOLD DATA.
Kotlin automatically generates all the boilerplate code
you would otherwise write manually.

Just add the 'data' keyword before 'class' and Kotlin gives you:

  ✅ equals()      — compares by content, not memory reference
  ✅ hashCode()    — consistent with equals()
  ✅ toString()    — readable string with all properties
  ✅ copy()        — create modified copies easily
  ✅ componentN()  — enables destructuring declarations

These are generated based on properties in the PRIMARY CONSTRUCTOR.
```

```kotlin
// DATA CLASS — just add 'data' keyword:
data class User(val name: String, val email: String, val age: Int)

fun main() {
    val user1 = User("Rohit", "rohit@gmail.com", 24)
    val user2 = User("Rohit", "rohit@gmail.com", 24)
    val user3 = User("Priya", "priya@gmail.com", 26)

    // PROBLEM 1 SOLVED: Equality by content
    println(user1 == user2)   // true ✅ (same content = equal)
    println(user1 == user3)   // false ✅ (different content = not equal)

    // PROBLEM 2 SOLVED: Useful toString()
    println(user1)
    // User(name=Rohit, email=rohit@gmail.com, age=24) ✅

    // PROBLEM 3 SOLVED: Easy copying (shown in Part 3)
    val updatedUser = user1.copy(age = 25)
    println(updatedUser)
    // User(name=Rohit, email=rohit@gmail.com, age=25) ✅

    // PROBLEM 4 SOLVED: Destructuring (shown in Part 4)
    val (name, email, age) = user1
    println("$name | $email | $age")
    // Rohit | rohit@gmail.com | 24 ✅
}
```

---

## ⚙️ Part 2: What Kotlin Auto-Generates

### 🔑 `equals()` and `hashCode()`

```kotlin
data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val category: String
)

fun main() {

    // ─── equals() ────────────────────────────────────────────────

    val product1 = Product(1, "iPhone 15", 99999.0, "Electronics")
    val product2 = Product(1, "iPhone 15", 99999.0, "Electronics")
    val product3 = Product(2, "Samsung S24", 79999.0, "Electronics")
    val product4 = Product(1, "iPhone 15", 89999.0, "Electronics")  // different price

    // == checks content (calls equals() internally):
    println(product1 == product2)  // true  — identical content
    println(product1 == product3)  // false — different id, name, price
    println(product1 == product4)  // false — different price

    // === checks reference (are they the exact same object in memory?):
    println(product1 === product2)  // false — different objects in memory
    println(product1 === product1)  // true  — literally the same object

    // WHAT Kotlin generates for equals():
    // It compares EACH property declared in the primary constructor:
    // product1.equals(product2) checks:
    //   product1.id == product2.id &&
    //   product1.name == product2.name &&
    //   product1.price == product2.price &&
    //   product1.category == product2.category

    // ─── hashCode() ──────────────────────────────────────────────

    // hashCode() is consistent with equals():
    // If two objects are equal → they MUST have the same hashCode
    println(product1.hashCode() == product2.hashCode())  // true ✅
    println(product1.hashCode() == product3.hashCode())  // false (usually)

    // WHY hashCode() matters: used by HashMap, HashSet, etc.
    val productSet = mutableSetOf(product1, product2, product3)
    println(productSet.size)  // 2 (product1 and product2 are "same" — set keeps unique)

    val productMap = mapOf(product1 to "In Stock", product3 to "Out of Stock")
    println(productMap[product2])  // "In Stock" (product2 == product1, same key!)
}
```

---

### 📝 `toString()`

```kotlin
data class OrderItem(
    val productId: Int,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double
)

data class Order(
    val orderId: String,
    val customerId: Int,
    val items: List<OrderItem>,
    val status: String
)

fun main() {

    val item1 = OrderItem(1, "Chicken Biryani", 2, 280.0)
    val item2 = OrderItem(2, "Raita", 1, 50.0)

    // AUTOMATIC toString() — reads like data:
    println(item1)
    // OrderItem(productId=1, productName=Chicken Biryani, quantity=2, unitPrice=280.0)

    val order = Order(
        orderId = "ORD-2024-001",
        customerId = 1042,
        items = listOf(item1, item2),
        status = "Preparing"
    )

    println(order)
    // Order(orderId=ORD-2024-001, customerId=1042,
    //       items=[OrderItem(productId=1, ...), OrderItem(productId=2, ...)],
    //       status=Preparing)

    // INCREDIBLE FOR DEBUGGING:
    // You can log data class objects directly and see all their values!
    // In Android: Log.d("TAG", "Order: $order")

    // Regular class vs Data class:
    class RegularOrder(val id: String, val status: String)
    val regularOrder = RegularOrder("001", "Preparing")
    println(regularOrder)  // RegularOrder@1b6d3586 (useless!)
    println(order)         // Full readable data (useful!) ✅
}
```

---

## 📋 Part 3: `copy()` — The Most Useful Feature

### 🔍 Understanding `copy()`

```text
copy() creates a NEW object with the SAME values,
except for the properties you specifically change.

This is called "immutable update" — the original object
is NOT changed. A modified copy is created.

WHY THIS MATTERS:
  In Android with Jetpack Compose and LiveData,
  you want immutable data. Instead of mutating objects,
  you create new copies with changes.
  UI then sees the new object and updates.
```

```kotlin
data class UserProfile(
    val id: Int,
    val name: String,
    val email: String,
    val profilePictureUrl: String?,
    val bio: String?,
    val isPremium: Boolean,
    val followersCount: Int,
    val city: String
)

fun main() {

    val originalProfile = UserProfile(
        id = 1042,
        name = "Rohit Kumar",
        email = "rohit@gmail.com",
        profilePictureUrl = null,
        bio = null,
        isPremium = false,
        followersCount = 0,
        city = "Bangalore"
    )

    println("Original:")
    println(originalProfile)

    // COPY changing ONE property:
    val premiumProfile = originalProfile.copy(isPremium = true)
    println("\nAfter upgrading to premium:")
    println(premiumProfile)
    // Only isPremium changed. ALL others are identical to original.

    // COPY changing MULTIPLE properties:
    val updatedProfile = originalProfile.copy(
        name = "Rohit K.",
        bio = "Android Developer | Coffee Lover",
        profilePictureUrl = "https://cdn.app.com/rohit.jpg",
        city = "Mumbai"
    )
    println("\nAfter profile update:")
    println(updatedProfile)

    // ORIGINAL is UNCHANGED (immutable):
    println("\nOriginal is untouched:")
    println(originalProfile)

    // COPY with incremented counter:
    val afterFirstFollower = originalProfile.copy(
        followersCount = originalProfile.followersCount + 1
    )
    println("\nAfter gaining a follower:")
    println(afterFirstFollower)
    // followersCount = 1, all others unchanged

    // CHAINING copies (each creates a new object):
    val finalProfile = originalProfile
        .copy(isPremium = true)
        .copy(bio = "Kotlin Developer")
        .copy(followersCount = 100)
    println("\nFinal profile after multiple updates:")
    println(finalProfile)
}
```

---

### 📱 `copy()` in Android ViewModel Pattern

```kotlin
// REAL ANDROID PATTERN — Immutable State Updates

data class CartState(
    val items: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val appliedCoupon: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class CartItem(
    val productId: Int,
    val name: String,
    val price: Double,
    val quantity: Int
)

class CartViewModel {

    // Current state — immutable data class
    private var _state = CartState()
    val state: CartState get() = _state

    fun addItem(item: CartItem) {
        val newItems = _state.items + item  // create new list with item added
        _state = _state.copy(
            items = newItems,
            totalPrice = newItems.sumOf { it.price * it.quantity }
        )
        println("State after adding item: $_state")
    }

    fun applyCoupon(code: String) {
        _state = _state.copy(appliedCoupon = code)
    }

    fun setLoading(loading: Boolean) {
        _state = _state.copy(isLoading = loading)
    }

    fun clearError() {
        _state = _state.copy(errorMessage = null)
    }

    // STATE TRANSITIONS are clean and clear:
    // Before: CartState(items=[], total=0.0, ...)
    // After:  CartState(items=[...], total=280.0, ...)
    // The DIFF is immediately obvious!
}

fun main() {
    val viewModel = CartViewModel()

    println("Initial: ${viewModel.state}")

    viewModel.addItem(CartItem(1, "Biryani", 280.0, 1))
    viewModel.addItem(CartItem(2, "Raita", 50.0, 2))
    viewModel.applyCoupon("SAVE10")
    viewModel.setLoading(true)

    println("\nFinal state:")
    println(viewModel.state)
}
```

---

## 🔀 Part 4: Destructuring Declarations

### 🧩 What is Destructuring?

```text
DESTRUCTURING lets you unpack the properties of a data class
into separate variables in one single line.

Kotlin generates componentN() functions for each property
in the primary constructor (in order):
  component1() → first property
  component2() → second property
  component3() → third property
  ... and so on

These are what power destructuring.
```

```kotlin
data class Movie(
    val id: Int,
    val title: String,
    val rating: Double,
    val genre: String,
    val releaseYear: Int
)

fun main() {

    val movie = Movie(1, "Oppenheimer", 8.1, "Drama", 2023)

    // WITHOUT destructuring:
    val id = movie.id
    val title = movie.title
    val rating = movie.rating
    // 3 separate lines to extract 3 properties

    // WITH destructuring — one clean line:
    val (movieId, movieTitle, movieRating, movieGenre, movieYear) = movie
    println("$movieTitle ($movieYear) - ⭐$movieRating [$movieGenre]")
    // Oppenheimer (2023) - ⭐8.1 [Drama]

    // SKIP properties you don't need with underscore:
    val (_, titleOnly, ratingOnly) = movie   // skip id (component1)
    println("$titleOnly: $ratingOnly")
    // Oppenheimer: 8.1

    // ─── DESTRUCTURING IN FOR LOOPS ──────────────────────────

    val movies = listOf(
        Movie(1, "Oppenheimer", 8.1, "Drama", 2023),
        Movie(2, "Spider-Man", 8.7, "Action", 2023),
        Movie(3, "Inception", 8.8, "Sci-Fi", 2010)
    )

    // Clean iteration with destructuring:
    for ((id2, title2, rating2) in movies) {
        println("[$id2] $title2 — ⭐$rating2")
    }
    // [1] Oppenheimer — ⭐8.1
    // [2] Spider-Man — ⭐8.7
    // [3] Inception — ⭐8.8

    // ─── DESTRUCTURING IN MAPS ───────────────────────────────

    val movieRatings = mapOf("Oppenheimer" to 8.1, "Spider-Man" to 8.7)

    for ((title3, rating3) in movieRatings) {
        println("$title3 has rating $rating3")
    }
    // Map.Entry is already destructurable — this works natively!

    // ─── DESTRUCTURING IN LAMBDA PARAMETERS ──────────────────

    movies.forEach { (id3, title3, rating3) ->
        if (rating3 > 8.5) println("Highly rated: $title3 (⭐$rating3)")
    }

    // ─── DESTRUCTURING FROM FUNCTION RETURN ──────────────────

    fun getTopMovie(): Movie = movies.maxBy { it.rating }

    val (_, topTitle, topRating) = getTopMovie()
    println("Top movie: $topTitle with $topRating")
    // Top movie: Inception with 8.8

    // COMPONENT FUNCTIONS (what Kotlin generates internally):
    println(movie.component1())  // 1 (id)
    println(movie.component2())  // Oppenheimer (title)
    println(movie.component3())  // 8.1 (rating)

    // The above are what the compiler uses for destructuring.
    // You rarely call them directly.
}
```

---

## ⚖️ Part 5: When to Use Data Class vs Regular Class

```kotlin
// ─── USE DATA CLASS when: ─────────────────────────────────────────

// 1. Class exists to HOLD DATA (no complex behavior):
data class Coordinate(val latitude: Double, val longitude: Double)
data class Temperature(val value: Double, val unit: String)
data class ApiError(val code: Int, val message: String)

// 2. Equality by content matters:
data class CacheKey(val endpoint: String, val params: Map<String, String>)
// Two CacheKeys with same endpoint+params should be "equal"

// 3. Used as Map keys or in Sets:
data class StudentId(val year: Int, val rollNumber: Int)
// Can be used as map key because hashCode is well-defined

// 4. Models from API responses:
data class WeatherResponse(
    val city: String,
    val temperature: Double,
    val humidity: Int,
    val description: String
)

// 5. UI state representation:
data class LoginScreenState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false
)

// ─── USE REGULAR CLASS when: ──────────────────────────────────────

// 1. Class manages BEHAVIOR, not just data:
class UserRepository(private val database: Database) {
    fun getUserById(id: Int) = database.find(id)
    fun saveUser(user: User) = database.save(user)
    // This is about behavior, not data storage
}

// 2. SINGLE instance should exist (Singleton pattern):
class AppConfig private constructor() {
    companion object {
        private var instance: AppConfig? = null
        fun getInstance() = instance ?: AppConfig().also { instance = it }
    }
}
// Two AppConfig objects being "equal" makes no sense

// 3. Complex lifecycle or initialization:
class DatabaseManager(context: Context) {
    private val db = Room.databaseBuilder(context, AppDatabase::class.java, "app_db").build()
    // Managing resources — not a simple data holder
}

// 4. Class has identity beyond its properties:
class UserSession(val userId: Int) {
    val sessionId = generateUniqueSessionId()  // each session is truly unique
    // Even two UserSession objects with same userId should NOT be "equal"
    // They are different sessions!
}
```

---

### 🚧 Limitations of Data Classes

```kotlin
// LIMITATION 1: Must have at least ONE primary constructor parameter
// data class Empty()  // ❌ COMPILE ERROR — no properties

// LIMITATION 2: Cannot be abstract, open, sealed, or inner
// abstract data class Shape()  // ❌ COMPILE ERROR
// (Kotlin 1.9+ allows data class to implement interfaces, but not be abstract)

// LIMITATION 3: Properties NOT in primary constructor are IGNORED
// by equals(), hashCode(), toString(), and copy()

data class User(
    val id: Int,
    val name: String
) {
    // This property is NOT in primary constructor:
    var loginCount: Int = 0   // ← NOT included in equals/hashCode/toString!
}

fun main() {
    val user1 = User(1, "Rohit")
    user1.loginCount = 5

    val user2 = User(1, "Rohit")
    user2.loginCount = 100

    println(user1 == user2)   // true ❌ (loginCount IGNORED by equals!)
    println(user1)             // User(id=1, name=Rohit) — loginCount NOT shown!
    // loginCount is completely invisible to generated functions

    // SOLUTION: If a property matters for equality, put it in the primary constructor!
    // If it is truly secondary state, keep it in the body but be aware of this behavior.
}

// LIMITATION 4: Inheritance is restricted
// Data classes cannot extend other data classes:
// data class Animal(val name: String)
// data class Dog(val name: String, val breed: String) : Animal(name)  // ❌ COMPILE ERROR
// Data classes CAN extend regular (open) classes and interfaces
```

> [!NOTE]
> Properties defined **inside the class body** (not the primary constructor) are completely invisible to `equals()`, `hashCode()`, `toString()`, and `copy()`. Always put identity-defining properties in the primary constructor.

---

## 🔒 SECTION 2: SEALED CLASSES

## ❓ Part 6: What is a Sealed Class?

### 💡 The Problem Sealed Classes Solve

```kotlin
// IMAGINE: You need to represent the result of an API call.
// It can be: Success (with data) or Error (with message) or Loading.

// ATTEMPT 1: Using Boolean — terrible:
fun fetchUser(id: Int): User? {
    return null  // null means error? or loading? ambiguous!
}

// ATTEMPT 2: Using exceptions — wrong tool:
fun fetchUser(id: Int): User {
    throw NetworkException("Failed")  // not all failures are exceptional
}

// ATTEMPT 3: Using a regular class hierarchy — problematic:
open class Result
class Success(val data: User) : Result()
class Error(val message: String) : Result()
class Loading : Result()

// Someone OUTSIDE your module can add:
class UnknownResult : Result()  // you cannot prevent this!
// Now when you do when(result), you cannot be exhaustive
// because an unknown subclass might exist!

fun handleResult(result: Result) {
    when (result) {
        is Success -> println(result.data)
        is Error   -> println(result.message)
        is Loading -> println("Loading...")
        // You MUST have 'else' because someone might add more subclasses
        else -> println("Unknown state!")  // ← annoying and risky
    }
}
```

> [!TIP]
> Sealed classes solve this perfectly by making the hierarchy **closed** — the compiler knows every possible subclass at compile time.

---

### 🧩 What is a Sealed Class?

```text
A SEALED CLASS is a restricted class hierarchy where:

  ✅ All subclasses must be defined in the SAME FILE
     (Kotlin 1.5+ allows same package for sealed interfaces)
  ✅ The compiler KNOWS ALL possible subclasses at compile time
  ✅ The set of subclasses is CLOSED (nobody else can add more)
  ✅ when() expressions become EXHAUSTIVE — no 'else' needed
  ✅ Each subclass can be a class, object, or data class

ANALOGY:
  Sealed class = A closed envelope with known contents.
  The envelope can ONLY contain the specific documents you put in.
  No surprises. No unknowns. The compiler knows exactly what's inside.

  Regular class = An open box — anyone can put anything in it.
  Sealed class = A sealed, labeled box — contents are fixed and known.
```

```kotlin
// SEALED CLASS definition:
sealed class NetworkResult {
    // ALL subclasses defined HERE, in the same file:

    // Data class for success (carries the actual data):
    data class Success<T>(val data: T) : NetworkResult()

    // Data class for errors (carries error info):
    data class Error(
        val message: String,
        val code: Int? = null,
        val exception: Exception? = null
    ) : NetworkResult()

    // Object for loading state (no data needed, singleton):
    object Loading : NetworkResult()

    // Object for idle/empty state:
    object Empty : NetworkResult()
}

// USING the sealed class:
fun handleResult(result: NetworkResult) {
    when (result) {
        is NetworkResult.Success -> {
            println("✅ Success! Data: ${result.data}")
        }
        is NetworkResult.Error -> {
            println("❌ Error: ${result.message} (Code: ${result.code})")
        }
        NetworkResult.Loading -> {
            println("⏳ Loading...")
        }
        NetworkResult.Empty -> {
            println("📭 No data available")
        }
        // NO 'else' needed! Compiler knows ALL possible subclasses.
        // If you add a new subclass later, the compiler FORCES you
        // to handle it here. Cannot accidentally miss it!
    }
}

fun main() {
    val success: NetworkResult = NetworkResult.Success("User data loaded!")
    val error: NetworkResult = NetworkResult.Error("Network timeout", 408)
    val loading: NetworkResult = NetworkResult.Loading
    val empty: NetworkResult = NetworkResult.Empty

    handleResult(success)   // ✅ Success! Data: User data loaded!
    handleResult(error)     // ❌ Error: Network timeout (Code: 408)
    handleResult(loading)   // ⏳ Loading...
    handleResult(empty)     // 📭 No data available
}
```

---

## 📊 Part 7: Sealed Class vs `enum` vs Regular Class

### ⚔️ The Three-Way Comparison

```kotlin
// ─── ENUM CLASS ───────────────────────────────────────────────────
// Good for: Fixed set of named constants
// Limited: All instances have the SAME structure

enum class OrderStatus {
    PENDING, PREPARING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED
    // Each is just a named constant.
    // Cannot carry different data per instance.
    // PENDING cannot have a "reason", CANCELLED cannot have "cancellation details"
    // All instances have same type, same structure.
}

// ─── SEALED CLASS ─────────────────────────────────────────────────
// Good for: Fixed set of classes with DIFFERENT structures
// Powerful: Each subclass can have its own properties

sealed class OrderState {
    object Pending : OrderState()

    data class Preparing(
        val estimatedMinutes: Int,
        val restaurantName: String
    ) : OrderState()

    data class OutForDelivery(
        val driverName: String,
        val driverPhone: String,
        val estimatedArrival: String,
        val currentLocation: Pair<Double, Double>
    ) : OrderState()

    data class Delivered(
        val deliveredAt: String,
        val driverRating: Int?
    ) : OrderState()

    data class Cancelled(
        val reason: String,
        val refundAmount: Double,
        val cancelledBy: String  // "user" or "restaurant" or "system"
    ) : OrderState()
}

fun compareAll() {

    // ENUM — all same structure:
    val status = OrderStatus.PENDING
    when (status) {
        OrderStatus.PENDING    -> println("Pending")
        OrderStatus.PREPARING  -> println("Preparing")
        // ... etc. No data attached.
        else -> {}
    }

    // SEALED — each carries its own data:
    val state: OrderState = OrderState.OutForDelivery(
        driverName = "Ravi Singh",
        driverPhone = "9876543210",
        estimatedArrival = "15 minutes",
        currentLocation = Pair(12.9716, 77.5946)
    )

    when (state) {
        is OrderState.Pending -> println("Order received!")

        is OrderState.Preparing -> {
            println("Chef is cooking...")
            println("Ready in ${state.estimatedMinutes} mins")
            println("At: ${state.restaurantName}")
        }

        is OrderState.OutForDelivery -> {
            println("${state.driverName} is on the way!")
            println("Call driver: ${state.driverPhone}")
            println("ETA: ${state.estimatedArrival}")
        }

        is OrderState.Delivered -> {
            println("Order delivered at ${state.deliveredAt}")
            state.driverRating?.let { println("You rated driver: $it/5") }
        }

        is OrderState.Cancelled -> {
            println("Order cancelled by ${state.cancelledBy}")
            println("Reason: ${state.reason}")
            println("Refund: ₹${state.refundAmount}")
        }
    }
}
```

### 📋 Cheat-Sheet: `enum` vs `sealed` vs Regular Class

| Feature | `enum class` | `sealed class` | Regular class |
|---|---|---|---|
| Hierarchy | Fixed constants | Fixed hierarchy | Open hierarchy |
| Structure | Same for all | Different per subclass | Same for all |
| Carries data | Only shared fields | Each subclass owns its data | Yes, but open |
| `when()` exhaustive | ✅ Yes | ✅ Yes | ❌ Needs `else` |
| Use for | Status flags, directions, colors | UI states, API results, error types | General OOP |

---

## 📱 Part 8: Sealed Classes for UI States

### 🎯 The Perfect Use Case

```kotlin
// SEALED CLASS for UI state — the most common Android pattern:

// Represents loading a list of movies from an API:
sealed class MovieListState {
    object Idle : MovieListState()
    object Loading : MovieListState()
    data class Success(val movies: List<Movie>) : MovieListState()
    data class Error(val message: String, val isRetryable: Boolean = true) : MovieListState()
    data class Empty(val reason: String = "No movies found") : MovieListState()
}

data class Movie(val id: Int, val title: String, val rating: Double)

// HOW the UI handles each state:
fun renderMovieListUI(state: MovieListState) {
    when (state) {
        MovieListState.Idle -> {
            // Show nothing — initial state before any action
            println("UI: Show welcome screen")
        }

        MovieListState.Loading -> {
            // Show spinner, hide content
            println("UI: Show loading spinner")
            println("UI: Hide movie list")
            println("UI: Hide error view")
        }

        is MovieListState.Success -> {
            // Hide spinner, show content
            println("UI: Hide loading spinner")
            println("UI: Show movie list with ${state.movies.size} movies")
            state.movies.forEach { println("     - ${it.title} ⭐${it.rating}") }
        }

        is MovieListState.Error -> {
            // Show error with optional retry button
            println("UI: Hide loading spinner")
            println("UI: Show error: ${state.message}")
            if (state.isRetryable) println("UI: Show RETRY button")
        }

        is MovieListState.Empty -> {
            // Show empty state illustration
            println("UI: Show empty state")
            println("UI: Message: ${state.reason}")
        }
    }
}

fun main() {
    println("=== UI STATE DEMONSTRATION ===\n")

    val states = listOf(
        MovieListState.Idle,
        MovieListState.Loading,
        MovieListState.Success(listOf(
            Movie(1, "Oppenheimer", 8.1),
            Movie(2, "Dune Part 2", 8.2)
        )),
        MovieListState.Error("No internet connection", isRetryable = true),
        MovieListState.Empty("No movies in this genre")
    )

    states.forEach { state ->
        println("--- State: ${state::class.simpleName} ---")
        renderMovieListUI(state)
        println()
    }
}
```

---

## 🔗 Part 9: Sealed Interface (Kotlin 1.5+)

### 🧩 What is a Sealed Interface?

```text
Sealed Interface = The same concept as sealed class,
but as an interface instead of a class.

KEY DIFFERENCES from sealed class:
  ✅ Subclasses can be in DIFFERENT FILES (same package)
  ✅ A subclass can implement MULTIPLE sealed interfaces
  ✅ More flexible than sealed class
  ✅ Cannot have constructor parameters (it is an interface)

WHEN TO USE:
  - Use sealed class when you need constructor/state in parent
  - Use sealed interface when you want more flexibility
    about where subclasses are defined
  - Use sealed interface when a subclass needs to
    implement multiple sealed types
```

```kotlin
// SEALED INTERFACE example:
sealed interface AuthState {
    object Unauthenticated : AuthState
    object Authenticating : AuthState
    data class Authenticated(val userId: Int, val token: String) : AuthState
    data class AuthError(val message: String) : AuthState
}

// A subclass CAN implement multiple sealed interfaces:
sealed interface DataState<out T> {
    object Loading : DataState<Nothing>
    data class Success<T>(val data: T) : DataState<T>
    data class Error(val throwable: Throwable) : DataState<Nothing>
}

// COMBINING both: a class implementing multiple sealed interfaces:
// (imagine a state that is both an auth event AND a data event)
class SessionExpired : AuthState, DataState<Nothing> {
    // This is allowed because both are interfaces!
}

fun handleAuthState(state: AuthState) {
    when (state) {
        AuthState.Unauthenticated   -> println("Please log in")
        AuthState.Authenticating    -> println("Verifying credentials...")
        is AuthState.Authenticated  -> println("Welcome! User ${state.userId}")
        is AuthState.AuthError      -> println("Auth failed: ${state.message}")
    }
    // No else needed! Sealed interface is exhaustive too.
}

fun main() {
    handleAuthState(AuthState.Unauthenticated)
    handleAuthState(AuthState.Authenticating)
    handleAuthState(AuthState.Authenticated(1042, "token_abc123"))
    handleAuthState(AuthState.AuthError("Invalid credentials"))
}
```

> [!NOTE]
> Use `sealed interface` over `sealed class` when subclasses live in different files (same package) or when a subclass needs to satisfy multiple sealed type contracts at once.

---

## 🎬 Part 10: Complete Real Android Example

### 🏗️ The Complete ViewModel + Sealed Class Pattern

> [!IMPORTANT]
> This is the most important pattern in modern Android development. Every production app uses some variation of this architecture.

```kotlin
// ─────────────────────────────────────────────────────────────────
// COMPLETE ANDROID EXAMPLE: Movie App with ViewModel + Sealed Class
// This is EXACTLY how real production Android apps work
// ─────────────────────────────────────────────────────────────────

// ─── DATA MODELS ──────────────────────────────────────────────────

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val rating: Double,
    val releaseYear: Int,
    val posterUrl: String?,
    val genres: List<String>,
    val durationMinutes: Int
) {
    val durationFormatted: String
        get() {
            val hours = durationMinutes / 60
            val minutes = durationMinutes % 60
            return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
        }

    val ratingFormatted: String get() = "⭐ ${"%.1f".format(rating)}/10"
}

data class MovieFilter(
    val genre: String? = null,
    val minRating: Double = 0.0,
    val maxYear: Int = 2024,
    val minYear: Int = 1900,
    val sortBy: SortOption = SortOption.RATING
)

enum class SortOption { RATING, YEAR, TITLE, DURATION }

// ─── SEALED CLASS: API RESULT ─────────────────────────────────────

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(
        val message: String,
        val code: Int? = null,
        val isNetworkError: Boolean = false
    ) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}

// ─── SEALED CLASS: UI STATE ───────────────────────────────────────

sealed class MovieUiState {

    // Before user takes any action:
    object Idle : MovieUiState()

    // Loading the movie list:
    object LoadingMovies : MovieUiState()

    // Movies loaded successfully:
    data class MoviesLoaded(
        val movies: List<Movie>,
        val appliedFilter: MovieFilter,
        val totalCount: Int
    ) : MovieUiState() {
        val isEmpty get() = movies.isEmpty()
        val filteredCount get() = movies.size
    }

    // No movies match the filter:
    data class NoResults(val filter: MovieFilter) : MovieUiState()

    // Error loading movies:
    data class LoadError(
        val message: String,
        val isNetworkError: Boolean,
        val isRetryable: Boolean = true
    ) : MovieUiState()

    // Loading a single movie's details:
    data class LoadingDetail(val movieId: Int) : MovieUiState()

    // Movie detail loaded:
    data class MovieDetail(val movie: Movie) : MovieUiState()
}

// ─── SEALED CLASS: USER ACTIONS/EVENTS ───────────────────────────

sealed class MovieEvent {
    object LoadMovies : MovieEvent()
    data class SearchMovies(val query: String) : MovieEvent()
    data class FilterMovies(val filter: MovieFilter) : MovieEvent()
    data class SelectMovie(val movieId: Int) : MovieEvent()
    object RetryLoad : MovieEvent()
    object ClearFilters : MovieEvent()
}

// ─── SIMULATED REPOSITORY ─────────────────────────────────────────

object MovieRepository {

    private val sampleMovies = listOf(
        Movie(1, "Oppenheimer", "The atomic bomb story", 8.1, 2023,
            "https://img.com/opp.jpg", listOf("Drama", "History"), 180),
        Movie(2, "Spider-Man: Across Spider-Verse", "Miles Morales multiverse",
            8.7, 2023, "https://img.com/spidey.jpg", listOf("Animation", "Action"), 140),
        Movie(3, "Inception", "Dream heist", 8.8, 2010,
            "https://img.com/inception.jpg", listOf("Sci-Fi", "Thriller"), 148),
        Movie(4, "Parasite", "Class struggle", 8.5, 2019,
            "https://img.com/parasite.jpg", listOf("Thriller", "Drama"), 132),
        Movie(5, "Dune Part 2", "Desert planet", 8.2, 2024,
            "https://img.com/dune2.jpg", listOf("Sci-Fi", "Adventure"), 166),
        Movie(6, "The Godfather", "Mafia family saga", 9.2, 1972,
            "https://img.com/godfather.jpg", listOf("Crime", "Drama"), 175),
        Movie(7, "Interstellar", "Space exploration", 8.6, 2014,
            "https://img.com/interstellar.jpg", listOf("Sci-Fi", "Drama"), 169),
        Movie(8, "RRR", "Indian freedom fighters", 7.9, 2022,
            "https://img.com/rrr.jpg", listOf("Action", "Drama"), 187)
    )

    // Returns sealed class ApiResult:
    fun getMovies(filter: MovieFilter): ApiResult<List<Movie>> {
        return try {
            // Simulate network delay:
            Thread.sleep(100)

            val filtered = sampleMovies
                .filter { movie ->
                    (filter.genre == null || filter.genre in movie.genres) &&
                    movie.rating >= filter.minRating &&
                    movie.releaseYear in filter.minYear..filter.maxYear
                }
                .let { movies ->
                    when (filter.sortBy) {
                        SortOption.RATING   -> movies.sortedByDescending { it.rating }
                        SortOption.YEAR     -> movies.sortedByDescending { it.releaseYear }
                        SortOption.TITLE    -> movies.sortedBy { it.title }
                        SortOption.DURATION -> movies.sortedBy { it.durationMinutes }
                    }
                }

            ApiResult.Success(filtered)

        } catch (e: Exception) {
            ApiResult.Error("Failed to load movies: ${e.message}", isNetworkError = true)
        }
    }

    fun getMovieById(id: Int): ApiResult<Movie> {
        val movie = sampleMovies.find { it.id == id }
        return if (movie != null) {
            ApiResult.Success(movie)
        } else {
            ApiResult.Error("Movie with ID $id not found", code = 404)
        }
    }
}

// ─── VIEWMODEL ────────────────────────────────────────────────────

class MovieViewModel {

    // Current UI state — always a MovieUiState:
    private var _uiState: MovieUiState = MovieUiState.Idle
    val uiState: MovieUiState get() = _uiState

    private var currentFilter = MovieFilter()
    private var searchQuery: String = ""

    // SINGLE entry point for all user interactions:
    fun handleEvent(event: MovieEvent) {
        when (event) {
            MovieEvent.LoadMovies    -> loadMovies()
            MovieEvent.RetryLoad     -> loadMovies()
            MovieEvent.ClearFilters  -> {
                currentFilter = MovieFilter()
                loadMovies()
            }
            is MovieEvent.SearchMovies -> {
                searchQuery = event.query
                loadMovies()
            }
            is MovieEvent.FilterMovies -> {
                currentFilter = event.filter
                loadMovies()
            }
            is MovieEvent.SelectMovie -> loadMovieDetail(event.movieId)
        }
    }

    private fun loadMovies() {
        // Set loading state:
        _uiState = MovieUiState.LoadingMovies
        notifyStateChanged()

        // Fetch from repository:
        val result = MovieRepository.getMovies(currentFilter)

        // Handle result using when() — exhaustive!
        _uiState = when (result) {
            is ApiResult.Success -> {
                val movies = if (searchQuery.isBlank()) {
                    result.data
                } else {
                    result.data.filter {
                        it.title.contains(searchQuery, ignoreCase = true) ||
                        it.overview.contains(searchQuery, ignoreCase = true)
                    }
                }

                if (movies.isEmpty()) {
                    MovieUiState.NoResults(currentFilter)
                } else {
                    MovieUiState.MoviesLoaded(
                        movies = movies,
                        appliedFilter = currentFilter,
                        totalCount = result.data.size
                    )
                }
            }

            is ApiResult.Error -> MovieUiState.LoadError(
                message = result.message,
                isNetworkError = result.isNetworkError
            )

            ApiResult.Loading -> MovieUiState.LoadingMovies  // stays loading
        }

        notifyStateChanged()
    }

    private fun loadMovieDetail(movieId: Int) {
        _uiState = MovieUiState.LoadingDetail(movieId)
        notifyStateChanged()

        val result = MovieRepository.getMovieById(movieId)

        _uiState = when (result) {
            is ApiResult.Success -> MovieUiState.MovieDetail(result.data)
            is ApiResult.Error   -> MovieUiState.LoadError(result.message, result.isNetworkError)
            ApiResult.Loading    -> MovieUiState.LoadingDetail(movieId)
        }

        notifyStateChanged()
    }

    // In real Android: this would use LiveData or StateFlow
    // to automatically notify the UI of changes
    private fun notifyStateChanged() {
        renderUI(_uiState)
    }
}

// ─── UI RENDERER (simulates Android UI updates) ───────────────────

fun renderUI(state: MovieUiState) {
    println("\n┌─────────────────────────────────────────────┐")
    println("│  UI STATE: ${state::class.simpleName?.uppercase()?.padEnd(32)}│")
    println("└─────────────────────────────────────────────┘")

    when (state) {
        MovieUiState.Idle -> {
            println("  → Show: Welcome screen with search bar")
        }

        MovieUiState.LoadingMovies -> {
            println("  → Show: Circular progress indicator")
            println("  → Show: Shimmer loading placeholders (3 rows)")
            println("  → Hide: Movie list, Error view")
        }

        is MovieUiState.MoviesLoaded -> {
            println("  → Hide: Loading indicator, Error view")
            println("  → Show: Movie list")
            println("  → Count: ${state.filteredCount} of ${state.totalCount} movies")
            println("  → Filter: Genre=${state.appliedFilter.genre ?: "All"} | " +
                    "MinRating=${state.appliedFilter.minRating} | " +
                    "Sort=${state.appliedFilter.sortBy}")
            println("  → Movies:")
            state.movies.take(3).forEach { movie ->
                println("     [${movie.id}] ${movie.title} ${movie.ratingFormatted} " +
                        "(${movie.releaseYear}) | ${movie.durationFormatted}")
            }
            if (state.movies.size > 3) println("     ... and ${state.movies.size - 3} more")
        }

        is MovieUiState.NoResults -> {
            println("  → Show: Empty state illustration")
            println("  → Show: 'No movies found' message")
            println("  → Show: 'Clear Filters' button")
            println("  → Applied filter: ${state.filter}")
        }

        is MovieUiState.LoadError -> {
            println("  → Show: Error message: ${state.message}")
            if (state.isNetworkError) println("  → Show: 'Check internet connection' hint")
            if (state.isRetryable) println("  → Show: 'Retry' button")
        }

        is MovieUiState.LoadingDetail -> {
            println("  → Show: Movie detail skeleton loader for ID: ${state.movieId}")
        }

        is MovieUiState.MovieDetail -> {
            val m = state.movie
            println("  → Show: Movie detail screen")
            println("  → Title: ${m.title} ${m.ratingFormatted}")
            println("  → Year: ${m.releaseYear} | Duration: ${m.durationFormatted}")
            println("  → Genres: ${m.genres.joinToString(", ")}")
            println("  → Overview: ${m.overview}")
        }
    }
}

// ─── MAIN DEMONSTRATION ───────────────────────────────────────────

fun main() {
    println("╔════════════════════════════════════════════════╗")
    println("║      MOVIE APP — COMPLETE SEALED CLASS DEMO   ║")
    println("╚════════════════════════════════════════════════╝")

    val viewModel = MovieViewModel()

    // 1. Initial load:
    println("\n═══ SCENARIO 1: Load All Movies ═══")
    viewModel.handleEvent(MovieEvent.LoadMovies)

    // 2. Filter by genre:
    println("\n═══ SCENARIO 2: Filter by Sci-Fi ═══")
    viewModel.handleEvent(MovieEvent.FilterMovies(
        MovieFilter(genre = "Sci-Fi", sortBy = SortOption.RATING)
    ))

    // 3. Search:
    println("\n═══ SCENARIO 3: Search 'inter' ═══")
    viewModel.handleEvent(MovieEvent.ClearFilters)
    viewModel.handleEvent(MovieEvent.SearchMovies("inter"))

    // 4. No results:
    println("\n═══ SCENARIO 4: Filter With No Results ═══")
    viewModel.handleEvent(MovieEvent.FilterMovies(
        MovieFilter(genre = "Horror", minRating = 9.5)
    ))

    // 5. Movie detail:
    println("\n═══ SCENARIO 5: Select Movie ID 3 ═══")
    viewModel.handleEvent(MovieEvent.SelectMovie(3))

    // 6. Select non-existent movie:
    println("\n═══ SCENARIO 6: Select Invalid Movie ═══")
    viewModel.handleEvent(MovieEvent.SelectMovie(999))

    // 7. Sort by year:
    println("\n═══ SCENARIO 7: Sort By Year ═══")
    viewModel.handleEvent(MovieEvent.ClearFilters)
    viewModel.handleEvent(MovieEvent.FilterMovies(
        MovieFilter(sortBy = SortOption.YEAR)
    ))

    // ─── DEMONSTRATE: when() IS EXHAUSTIVE ────────────────────

    println("\n═══ SEALED CLASS EXHAUSTIVENESS DEMO ═══")
    val currentState: MovieUiState = viewModel.uiState

    // This when() has NO else — compiler ensures all cases covered:
    val description = when (currentState) {
        MovieUiState.Idle          -> "App just opened"
        MovieUiState.LoadingMovies -> "Loading in progress"
        is MovieUiState.MoviesLoaded -> "Showing ${currentState.filteredCount} movies"
        is MovieUiState.NoResults  -> "No results for filter"
        is MovieUiState.LoadError  -> "Error: ${currentState.message}"
        is MovieUiState.LoadingDetail -> "Loading movie ${currentState.movieId}"
        is MovieUiState.MovieDetail -> "Viewing: ${currentState.movie.title}"
    }
    println("Current state description: $description")

    // ─── DATA CLASS FEATURES ON SEALED SUBCLASSES ─────────────

    println("\n═══ DATA CLASS FEATURES IN SEALED CLASSES ═══")

    val state1 = MovieUiState.LoadError("Network error", true)
    val state2 = MovieUiState.LoadError("Network error", true)
    val state3 = MovieUiState.LoadError("Server error", false)

    println("state1 == state2: ${state1 == state2}")  // true (same content)
    println("state1 == state3: ${state1 == state3}")  // false (different content)
    println("state1: $state1")  // readable toString()

    // copy() on sealed subclass (since it is a data class):
    val retriable = state3.copy(isRetryable = true)
    println("Modified state3: $retriable")
}
```

---

## 📋 Complete Summary

### 🗃️ Data Classes vs Sealed Classes — Master Reference

| Topic | Data Class | Sealed Class |
|---|---|---|
| **What it is** | Class designed to hold data | Restricted class hierarchy |
| **Keyword** | `data class` | `sealed class` / `sealed interface` |
| **Auto-generates** | `equals()`, `hashCode()`, `toString()`, `copy()`, `componentN()` | Nothing — but enables exhaustive `when()` |
| **`copy()`** | Creates new object with selective changes, original unchanged | Available on `data class` subclasses only |
| **Destructuring** | `val (a, b, c) = obj` — follows primary constructor order | Not applicable |
| **Hierarchy** | Cannot extend other data classes | Subclasses: `object`, `class`, or `data class` |
| **Limitations** | Body props ignored by `equals`/`toString`; must have ≥1 constructor prop | All subclasses must be in same file (sealed class) or same package (sealed interface) |
| **Primary use** | API responses, UI state models, cache keys, map keys | UI states, API results, user events, navigation events |
| **vs enum** | — | `enum`: same structure per value; `sealed`: different structure per subclass |

---

## ❓ Quiz Questions

---

### 📝 Question 1: Data Class Deep Understanding

**PART A:** For each scenario, decide if `data class` is appropriate. Justify every answer.

- **a)** A class representing a GPS coordinate (`latitude`, `longitude`). Two coordinates with same lat/long should be considered equal.
- **b)** A class representing a `UserSession` that manages active connections, timers, and WebSocket state.
- **c)** A class representing an API request (URL, method, headers, body). Two requests with same data should be "equal" for caching.
- **d)** A singleton `AppDatabase` manager class.
- **e)** A class representing a playing card (`suit`, `number`).

**PART B:** What exactly does this code print? Explain **WHY** for each line.

```kotlin
data class Point(val x: Int, val y: Int) {
    val label: String = "Point($x, $y)"  // NOT in primary constructor
    var clickCount: Int = 0              // NOT in primary constructor
}

val p1 = Point(3, 4)
val p2 = Point(3, 4)
p1.clickCount = 5

println(p1 == p2)                        // Line 1
println(p1.hashCode() == p2.hashCode())  // Line 2
println(p1)                              // Line 3
println(p1.label)                        // Line 4

val p3 = p1.copy(x = 10)
println(p3)           // Line 5
println(p3.clickCount) // Line 6 — what is this?
println(p3.label)     // Line 7 — what is this?

val (x, y) = p1
println("x=$x, y=$y") // Line 8
```

**PART C:** You have a data class:

```kotlin
data class Config(
    val apiUrl: String,
    val timeout: Int,
    val retryCount: Int,
    val enableLogging: Boolean,
    val headers: Map<String, String>
)

val productionConfig = Config(
    apiUrl = "https://api.production.com",
    timeout = 30,
    retryCount = 3,
    enableLogging = false,
    headers = mapOf("Accept" to "application/json")
)
```

Write **ONE** `copy()` call that creates a development config that:
- Uses `"https://api.dev.com"` as the URL
- Has timeout of 60 seconds
- Has 5 retry attempts
- Enables logging
- Adds `"X-Debug-Mode: true"` to headers while keeping the `Accept` header

---

### 📝 Question 2: Sealed Class Design

**PART A:** Design sealed classes for each scenario. Show complete Kotlin code with all subclasses.

**SCENARIO 1:** A file download feature. The download can be in these states:
- Not started (idle)
- Queued (waiting, knows position in queue: `Int`)
- Downloading (knows: `fileName`, `currentBytes`, `totalBytes`, `speedBytesPerSec`)
- Paused (knows: `fileName`, `downloadedBytes`, `reason: String`)
- Completed (knows: `fileName`, `filePath`, `fileSizeBytes`)
- Failed (knows: `fileName`, `errorMessage`, `isRetryable`)
- Cancelled (knows: `fileName`, `cancelledAt` timestamp)

**SCENARIO 2:** A form validation system. Validating a form field can result in:
- Valid (no message needed)
- Invalid (list of error messages)
- Warning (message, but allows submission anyway)
- Pending (async validation in progress)

**PART B:** For the `DownloadState` from Scenario 1, write:

- **a)** `fun getProgressPercent(state: DownloadState): Double?`
  - Returns 0.0–100.0 when downloading
  - Returns 100.0 when completed
  - Returns the paused progress when paused
  - Returns `null` for all other states

- **b)** `fun getDisplayText(state: DownloadState): String`
  - Returns human-readable text for each state
  - For Downloading: `"Downloading file.pdf - 45% (2.3 MB/s)"`
  - Use `when()` **WITHOUT** `else`

- **c)** `fun canRetry(state: DownloadState): Boolean`
  - Returns `true` only for `Failed` state with `isRetryable = true`

**PART C:** Explain what happens at compile time if you add a new subclass `Verifying` to `DownloadState`:

```kotlin
data class Verifying(val fileName: String) : DownloadState()
```

Which of your functions from Part B would give a compile error? What does that compile error mean? Why is this **GOOD** behavior?

---

### 📝 Question 3: `when()` with Sealed Classes

Given this sealed class:

```kotlin
sealed class PaymentState {
    object Idle : PaymentState()
    data class EnteringDetails(val method: String) : PaymentState()
    data class Processing(val amount: Double, val orderId: String) : PaymentState()
    data class Success(
        val transactionId: String,
        val amount: Double,
        val method: String
    ) : PaymentState()
    data class Failed(
        val reason: String,
        val isRetryable: Boolean,
        val attemptsRemaining: Int
    ) : PaymentState()
    data class Refunding(val transactionId: String, val amount: Double) : PaymentState()
    object Refunded : PaymentState()
}
```

Answer **ALL** questions:

- **a)** Write `fun getButtonLabel(state: PaymentState): String`:
  - Idle: `"Choose Payment Method"`
  - EnteringDetails: `"Pay Now"`
  - Processing: `"Processing... Please wait"`
  - Success: `"Done ✅"`
  - Failed (retryable): `"Retry Payment"` / Failed (not retryable): `"Cancel"`
  - Refunding: `"Processing Refund..."`
  - Refunded: `"Refund Complete"`
  - Use `when()` **without** `else`.

- **b)** Write `fun isUserInteractionAllowed(state: PaymentState): Boolean`:
  - Allowed: Idle, EnteringDetails, Failed (retryable), Refunded
  - Not allowed: Processing, Refunding, Success, Failed (not retryable)

- **c)** Write `fun logAnalyticsEvent(state: PaymentState)`:
  - Log only for: Processing, Success, Failed, Refunded
  - For others: do nothing (but handle exhaustively!)

- **d)** What is the **KEY advantage** of using `when()` with sealed classes vs regular class hierarchy? What specifically does the compiler **GUARANTEE**?

- **e)** Show what happens (compile error? runtime error? works fine?) when you add `object TimedOut : PaymentState()` as a new subclass WITHOUT updating the `when()` expressions in your functions.

---

### 📝 Question 4: Sealed Class vs `enum`

You are designing a system for a traffic light controller.

**APPROACH A — enum:**
```kotlin
enum class TrafficLight {
    RED, YELLOW, GREEN
}
```

**APPROACH B — sealed class:**
```kotlin
sealed class TrafficLightState {
    data class Red(val durationSeconds: Int) : TrafficLightState()
    data class Yellow(val isTransitioningToGreen: Boolean) : TrafficLightState()
    data class Green(val durationSeconds: Int, val isPedestrianActive: Boolean) : TrafficLightState()
}
```

Answer each question:

- **a)** For a **SIMPLE** traffic light app that just displays the current color, which approach is better? Why?
- **b)** For an **ADVANCED** traffic management system that needs duration, yellow transition direction, and pedestrian status — which approach is better? Why?
- **c)** Write the `when()` expression for **BOTH** approaches to determine: `"Can cars proceed?"`
  - RED → No / YELLOW → No / GREEN → Yes (only if no pedestrians in Approach B)
- **d)** When would you use **BOTH** an `enum` AND a `sealed class` together? Show a realistic example.
- **e)** Convert this enum to an appropriate sealed class structure, where each error type carries relevant data:

```kotlin
enum class ApiError {
    NETWORK_TIMEOUT,
    NO_INTERNET,
    SERVER_ERROR,
    UNAUTHORIZED,
    NOT_FOUND,
    UNKNOWN
}
```

---

### 🚀 Question 5: Complete Feature Design

You are building a **SEARCH** feature for an e-commerce Android app.

**REQUIREMENT 1: Search Input State** — Users can be in these states:
- Empty (no query typed)
- Typing (query exists, user still typing, show suggestions)
- Submitted (query finalized, search initiated)

**REQUIREMENT 2: Search Result State** — The results can be:
- Idle (no search done yet)
- Loading (searching, show skeleton)
- Success with results (list of products, query used, total count)
- Success but empty (query returned nothing)
- Error (message, whether to show retry)

**REQUIREMENT 3: Product data model** — Products have: `id`, `name`, `price`, `imageUrl?`, `rating`, `category`

---

**TASK 1:** Create `SearchInputState` sealed class.

**TASK 2:** Create `SearchResultState` sealed class (`Success` subclass should be a `data class` with `copy()` ability).

**TASK 3:** Create a `SearchViewModel` class that:
- Holds both states (input + results)
- Has `onQueryChanged(query: String)` function
- Has `onSearchSubmit()` function
- Has `onRetry()` function
- Has `clearSearch()` function
- Each function updates the appropriate state.

**TASK 4:** Write a `renderSearchUI(inputState, resultState)` function that:
- Uses `when()` on **BOTH** states
- For each combination, prints what the UI would show
- Has **no** `else` on any `when()` (fully exhaustive)

**TASK 5:** Write `main()` that simulates this user journey:
1. User opens search screen (idle)
2. User types `"iph"` (typing, show suggestions)
3. User types `"iphone"` (still typing)
4. User presses search (submitted, loading)
5. Results arrive (success with 3 iPhone products)
6. User searches `"abcxyz123"` (empty results)
7. User searches and network fails (error state)
8. User presses retry (loading again)

**BONUS:** Explain why the combination of `data class` (for the product model and success state) and `sealed class` (for the overall state) is the **IDEAL** approach for this feature in an Android app. Specifically: How does `data class`'s `copy()` help when implementing "sort search results by price" without modifying the original state?