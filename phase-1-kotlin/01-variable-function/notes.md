# 🚀 Complete Guide to Variables and Functions in Kotlin

![Variables and Functions](./var%20and%20fun.png)

---

## 📦 SECTION 1: VARIABLES

---

## 💡 Part 1: What is a Variable and Why We Need It

### 🛠️ The Core Problem
Imagine you are building an Android weather app. Your app needs to:
- Remember the current temperature
- Store the user's name
- Keep track of whether dark mode is on or off
- Hold the city name the user searched for

All of this information needs to be stored somewhere in memory while your app is running. That "somewhere" is a variable.

---

### 📦 What is a Variable?

> 📦 **VARIABLE:** A named container in memory (RAM) that holds a value.

```
THINK OF IT LIKE LABELED BOXES IN RAM:

┌─────────────────────┐
│   Box Label: age    │
│   Contents:  24     │
└─────────────────────┘

┌─────────────────────┐
│   Box Label: name   │
│   Contents: "Rohit" │
└─────────────────────┘

┌─────────────────────┐
│   Box Label: temp   │
│   Contents:  28.5   │
└─────────────────────┘

- The LABEL is the variable name.
- The CONTENTS is the value stored.
- The BOX is a location in RAM.
```

---

### ⚡ Why We Need Variables

```
WITHOUT VARIABLES:
  If you want to greet a user, you hardcode:
  "Hello, Rohit! Your score is 95. Rohit, you passed!"

  If the name changes, you must find and replace EVERY single occurrence.
  Impossible in large apps.

WITH VARIABLES:
  val name = "Rohit"
  val score = 95
  "Hello, $name! Your score is $score. $name, you passed!"

  Change the name in ONE place, it updates everywhere.
  This is the power of variables.
```

---

### 📱 Android Development Connection

In Android apps, variables are used everywhere:

```kotlin
// Android Real World Examples:
var isLoggedIn = false                // User's login status
var currentSong = "Kesariya"           // Currently playing song
var cartCount = 0                     // Cart item count
var restaurantList = listOf(...)       // API response data
val userName = "Rohit Kumar"          // User's profile name
var isDarkMode = true                 // App theme preference
```

---

## ⚖️ Part 2: val vs var — The Most Important Distinction in Kotlin

### 🔑 The Two Keyword Types
Kotlin has exactly two keywords for declaring variables:

```
val = value     → IMMUTABLE (cannot be changed after assignment)
var = variable  → MUTABLE   (can be changed anytime)
```

---

### 🔒 `val` — Immutable (Read-Only)
`val` declares a variable whose value **CANNOT** change after it is first assigned. It is like writing in **PEN** on paper. Once written, it cannot be erased.

> 💡 **Real-Life Analogy:** Your date of birth is a `val`. It was set once when you were born and never changes.

---

### ✏️ `var` — Mutable (Changeable)
`var` declares a variable whose value **CAN** change as many times as needed. It is like writing in **PENCIL** on paper. You can erase and rewrite whenever needed.

> 💡 **Real-Life Analogy:** Your age is a `var`. It changes every year (23, then 24, then 25...).

---

### 💻 Code Example

```kotlin
fun main() {
    // VAL — cannot be changed
    val userName = "Rohit Kumar"
    val dateOfBirth = "15-Jan-2000"
    val pi = 3.14159

    println(userName)     // Rohit Kumar
    println(dateOfBirth)  // 15-Jan-2000

    // This would cause a COMPILE ERROR:
    // userName = "Priya"
    // ERROR: Val cannot be reassigned

    // VAR — can be changed
    var score = 0
    var isLoggedIn = false
    var currentTemperature = 28.5

    println(score)              // 0
    println(isLoggedIn)         // false

    // Changing var values — perfectly fine
    score = 95
    isLoggedIn = true
    currentTemperature = 31.2

    println(score)              // 95
    println(isLoggedIn)         // true
    println(currentTemperature) // 31.2
}
```

---

### 🛡️ Why Kotlin STRONGLY Prefers `val` Over `var`

1. **SAFETY:** If a variable is `val`, you KNOW it will never change unexpectedly. You can read it anywhere in your code with confidence.
2. **IMMUTABILITY = PREDICTABILITY:** A `val` variable initialized on line 1 will be identical on line 1,000. `var` could have been changed by 500 lines of code in between.
3. **THREAD SAFETY:** When multiple threads run simultaneously, two threads modifying the same `var` can corrupt data (Race Condition). A `val` cannot be modified, making concurrent reads 100% safe.
4. **KOTLIN IDIOM:** Professional Kotlin code uses `val` everywhere possible. Use `var` ONLY when you genuinely need to mutate state.

> [!TIP]
> **THE GOLDEN RULE:** Start with `val`. ALWAYS. Only change to `var` if you MUST mutate the value.

```kotlin
// ANDROID REAL EXAMPLES:

// val — things that do not change after initialization
val API_BASE_URL = "https://api.foodapp.com"
val MAX_RETRY_COUNT = 3
val userId = getUserIdFromDatabase() // fetched once, never changes

// var — things that change during app lifecycle
var isNetworkAvailable = true
var currentPageNumber = 1
var userScore = 0

// In Android ViewModel:
class WeatherViewModel : ViewModel() {
    val cityName = "Bangalore"     // set once, display everywhere
    var currentTemp = 0.0          // updates every time weather refreshes
}
```

---

## 🎨 Part 3: Basic Data Types in Kotlin

### 📊 What is a Data Type?
Every variable holds a specific type of data. Kotlin needs to know what type of data it is so it knows:
1. How much memory to allocate
2. What operations are valid
3. How to handle the value

```
DATA TYPE CONTAINER ANALOGY:

  A glass     → holds liquid    (Double/Float for decimals)
  A box       → holds solid     (Int for whole numbers)
  An envelope → holds text      (String for text)
  A switch    → holds on/off    (Boolean for true/false)

You cannot put liquid in an envelope or on/off in a glass.
Types enforce that the right data goes in the right container.
```

---

### 🔢 1. `Int` — Whole Numbers
- **Range:** $-2,147,483,648$ to $2,147,483,647$
- **Memory:** 32 bits (4 bytes)

```kotlin
val age: Int = 24
val score: Int = 1500
val retryCount: Int = 3
var cartItemCount: Int = 0
var currentPage: Int = 1

// Operations with Int:
val a = 10
val b = 3
println(a + b)   // 13 (addition)
println(a - b)   // 7  (subtraction)
println(a * b)   // 30 (multiplication)
println(a / b)   // 3  (integer division, drops decimal!)
println(a % b)   // 1  (modulo - remainder)
```

---

### 🔢 2. `Long` — Very Large Whole Numbers
- **Range:** $-9,223,372,036,854,775,808$ to $9,223,372,036,854,775,807$
- **Memory:** 64 bits (8 bytes)
- Use **`L`** suffix to declare a `Long` literal.

```kotlin
val worldPopulation: Long = 8_000_000_000L  // 8 billion
val fileSize: Long = 5_368_709_120L          // 5 GB in bytes
val timestamp: Long = 1705312200000L         // Unix timestamp in ms
```

> [!NOTE]
> **When to use `Long` vs `Int`:** Use `Int` for scores, ages, and counts. Use `Long` when numbers exceed $\approx 2.1$ billion (timestamps, file sizes in bytes, database IDs).

---

### 🎯 3. `Double` — Decimal Numbers (Default & Recommended)
- **Precision:** 64 bits (8 bytes) - double precision.
- **DEFAULT** for decimal numbers in Kotlin.

```kotlin
val temperature: Double = 28.5
val rating: Double = 4.7
val latitude: Double = 12.9716
val longitude: Double = 77.5946
val price: Double = 299.99

val a = 10.0
val b = 3.0
println(a / b)   // 3.3333333333333335 (proper decimal division!)
```

---

### 📐 4. `Float` — Single Precision Decimal Numbers
- **Precision:** 32 bits (4 bytes) - single precision.
- Use **`f`** or **`F`** suffix to declare a `Float` literal.

```kotlin
val temperature: Float = 28.5f
val buttonWidth: Float = 200.5f
val animationProgress: Float = 0.75f  // 75% through animation
```

> [!TIP]
> **`Double` vs `Float`:** Prefer `Double` by default. Use `Float` when working with Android graphics/animations (`dp`/`px` UI calculations) or OpenGL APIs.

---

### 🔤 5. `String` — Text Data
Sequence of characters enclosed in double quotes (`"..."`). Immutable.

```kotlin
val name: String = "Rohit Kumar"
val email: String = "rohit@gmail.com"
var searchQuery: String = ""

// Useful String operations:
val message = "Hello World"
println(message.length)                  // 11
println(message.uppercase())             // HELLO WORLD
println(message.lowercase())             // hello world
println(message.contains("World"))       // true
println(message.replace("World", "Kotlin")) // Hello Kotlin
println(message.isEmpty())               // false
println("  ".isBlank())                  // true (whitespace only)

// Multiline String with triple quotes:
val multiLine = """
    Hello Rohit,
    Welcome to our app.
    Your account is ready.
""".trimIndent()
```

---

### 🔘 6. `Boolean` — True or False
Holds logical flags: `true` or `false`.

```kotlin
val isLoggedIn: Boolean = false
val hasInternet: Boolean = true
val isNightMode: Boolean = false

// Logical Operators:
val a = true
val b = false
println(a && b)   // false (AND)
println(a || b)   // true  (OR)
println(!a)       // false (NOT)
```

---

### 🔤 7. `Char` — Single Character
Single character enclosed in single quotes (`'...'`).

```kotlin
val firstLetter: Char = 'R'
val grade: Char = 'A'
val newLine: Char = '\n'

println(firstLetter.isUpperCase())  // true
println(firstLetter.lowercaseChar()) // r
```

---

### 📊 Data Types Summary Matrix

```kotlin
fun main() {
    val userAge: Int = 24
    val fileSize: Long = 5_368_709_120L
    val temperature: Double = 28.5
    val animProgress: Float = 0.5f
    val userName: String = "Rohit Kumar"
    val isLoggedIn: Boolean = true
    val userGrade: Char = 'A'

    println("Age: $userAge | File: $fileSize bytes | Temp: $temperature°C")
    println("User: $userName | Logged in: $isLoggedIn | Grade: $userGrade")
}
```

```text
OUTPUT:
Age: 24 | File: 5368709120 bytes | Temp: 28.5°C
User: Rohit Kumar | Logged in: true | Grade: A
```

---

## 🧠 Part 4: Type Inference — Kotlin's Smart Guessing

### ❓ What is Type Inference?
In Java, explicit types were mandatory:

```java
// Java (Explicit type required):
int age = 24;
String name = "Rohit";
boolean isLoggedIn = true;
```

Kotlin automatically **infers** (figures out) the data type from the initial value assigned:

```kotlin
// Kotlin WITH Type Inference:
val age = 24          // Infers Int
val name = "Rohit"    // Infers String
val temperature = 28.5 // Infers Double
val isLoggedIn = true  // Infers Boolean
val grade = 'A'        // Infers Char
val bigNumber = 8_000_000_000L // Infers Long
val floatNum = 3.14f   // Infers Float
```

---

### 📌 When to Write Types Explicitly

```kotlin
// CASE 1: When declaring a variable without an immediate value
var userScore: Int
userScore = 100

// CASE 2: Overriding default inferred type (e.g., Long without L suffix)
val fileSize: Long = 5000

// CASE 3: Improving readability in complex expressions
val result: Double = calculateComplexTax()
```

```kotlin
// In Android ViewModel (Cleaner with inference):
class MovieViewModel : ViewModel() {
    val movieTitle = "Oppenheimer"  // Infers String
    val movieRating = 8.1           // Infers Double
    val isLoading = false           // Infers Boolean
}
```

---

## 🧵 Part 5: String Templates — The Kotlin Way to Build Strings

### ❌ The Problem With Concatenation

```kotlin
val name = "Rohit"
val age = 24
val city = "Bangalore"

// Ugly concatenation:
val message = "Hello, " + name + "! You are " + age + " years old from " + city + "."
```

---

### ⚡ The Solution: Dollar Sign (`$`) and Braces (`${}`)

```kotlin
fun main() {
    val name = "Rohit"
    val age = 24
    val city = "Bangalore"

    // Simple variable embedding using $
    val message = "Hello, $name! You are $age years old from $city."
    println(message)
    // Output: Hello, Rohit! You are 24 years old from Bangalore.

    // Expressions inside ${ }
    println("Hello, ${name.uppercase()}!")
    println("Name length: ${name.length} characters")
    println("Total: ₹${299.0 * 3}")
    println("Result: ${if (age >= 18) "PASS" else "FAIL"}")
}
```

```kotlin
// REAL ANDROID USAGE:

// 1. API URL Construction:
val baseUrl = "https://api.openweathermap.org"
val city = "Bangalore"
val apiKey = "abc123"
val weatherUrl = "$baseUrl/weather?city=$city&key=$apiKey"

// 2. UI Display Text:
val restaurantName = "Biryani House"
val rating = 4.5
val deliveryTime = 30
val displayText = "$restaurantName ⭐ $rating • $deliveryTime mins"

// 3. Formatted Multiline Receipt:
val subtotal = 450.0
val tax = subtotal * 0.18
val receipt = """
    Subtotal: ₹$subtotal
    Tax (18%): ₹$tax
    Total:    ₹${subtotal + tax}
""".trimIndent()
```

---

## 🔒 Part 6: Constants with `const val`

### 🆚 `val` vs `const val`

| Feature | `val` | `const val` |
| :--- | :--- | :--- |
| **Value Assigned** | At **Runtime** (when app runs) | At **Compile Time** (before app runs) |
| **Allowed Values** | Any value or function call output | Only primitive literals (`Int`, `String`, `Boolean`) |
| **Scope Location** | Anywhere (inside functions, classes, objects) | **Top-level** or inside `object` / `companion object` only |
| **Performance** | Variable lookup at runtime | Inlined directly into bytecode (Zero overhead) |

```kotlin
// TOP LEVEL CONSTANTS (SCREAMING_SNAKE_CASE naming convention):
const val APP_VERSION = "2.1.0"
const val MAX_LOGIN_ATTEMPTS = 3
const val BASE_URL = "https://api.foodapp.com"

fun main() {
    println("App Version: $APP_VERSION")
    
    val currentTime = System.currentTimeMillis() // val (Runtime)
    // const val currentTime = ...              // ERROR! Cannot call functions in const val
}

// INSIDE COMPANION OBJECT:
class NetworkConfig {
    companion object {
        const val CONNECT_TIMEOUT = 15
        const val BASE_API_URL = "https://api.foodapp.com/v2"
    }
}
```

---

## ⚙️ SECTION 2: FUNCTIONS

---

## 🎯 Part 7: What is a Function and Why We Use Them

### 🛠️ Starting With a Problem
Imagine your food delivery app needs to calculate total order price in 4 places: Cart view, Coupon applying, Checkout, and Order History.

Without functions, you write the exact same math 4 times. If tax changes, you must update 4 places.

> ⚙️ **FUNCTION:** A named block of reusable code that performs a specific task, takes optional inputs (parameters), and sends back an optional output (return value).

```
FUNCTION MACHINE ANALOGY:

         ┌─────────────────────────────┐
         │                             │
INPUT →  │    Function: calculateTax   │  → OUTPUT
(amount) │    (does calculation)       │    (tax amount)
         │                             │
         └─────────────────────────────┘
```

---

### 🌟 Why Functions Are Fundamental
1. **REUSABILITY:** Write once, call anywhere.
2. **READABILITY:** Replace 20 complex lines with `val tax = calculateTax(subtotal)`.
3. **MAINTAINABILITY:** Fix bugs or update business logic in ONE place.
4. **TESTABILITY:** Test functions in isolation (`calculateTax(100.0) == 18.0`).
5. **ABSTRACTION:** Hide internal complexity from callers.

---

## 📐 Part 8: How to Declare a Function in Kotlin

### 🏗️ The Basic Structure

```kotlin
fun functionName(parameter1: Type1, parameter2: Type2): ReturnType {
    // function body
    return value
}
```

```
FUN SYNTAX BREAKDOWN:
  fun          → Keyword declaring a function
  functionName → Name of function (camelCase)
  ()           → Encloses input parameters
  : ReturnType → Data type of value returned
  {}           → Function body block
  return       → Keyword returning output value
```

---

### 💻 Your First Functions

```kotlin
// 1. NO PARAMETERS, NO RETURN VALUE:
fun greetUser() {
    println("Hello! Welcome to FoodApp!")
}

// 2. WITH PARAMETERS, NO RETURN VALUE:
fun greetSpecificUser(name: String) {
    println("Hello, $name! Welcome back!")
}

// 3. WITH PARAMETERS AND RETURN VALUE:
fun addNumbers(a: Int, b: Int): Int {
    return a + b
}

// 4. MULTIPLE PARAMETERS & RETURN VALUE:
fun calculateTotal(subtotal: Double, taxRate: Double): Double {
    val tax = subtotal * taxRate
    return subtotal + tax
}

fun main() {
    greetUser()
    greetSpecificUser("Rohit")
    
    val sum = addNumbers(10, 25)
    println("Sum: $sum") // 35

    val total = calculateTotal(500.0, 0.18)
    println("Total with tax: ₹$total") // ₹590.0
}
```

---

## 📥 Part 9: Parameters and Return Types

### 📥 Parameters — Function Inputs

```kotlin
fun displayMovieInfo(title: String, rating: Double, year: Int) {
    println("Movie: $title | Rating: $rating/10 | Year: $year")
}

fun calculateDiscount(originalPrice: Double, discountPercent: Int): Double {
    val discountAmount = originalPrice * discountPercent / 100
    return originalPrice - discountAmount
}

fun isPasswordValid(password: String): Boolean {
    val hasMinLength = password.length >= 8
    val hasUpperCase = password.any { it.isUpperCase() }
    val hasDigit = password.any { it.isDigit() }
    return hasMinLength && hasUpperCase && hasDigit
}
```

---

### 📤 Return Types — Function Outputs

```kotlin
// Returns Int:
fun countWords(text: String): Int = text.trim().split(" ").size

// Returns String:
fun formatPrice(price: Double): String = "₹$price"

// Returns Boolean:
fun isEven(number: Int): Boolean = number % 2 == 0

// Returns Custom Data Class Object:
data class User(val id: Int, val name: String, val email: String)

fun createUser(name: String, email: String): User {
    val generatedId = (1000..9999).random()
    return User(generatedId, name, email)
}
```

```kotlin
// REAL ANDROID FUNCTION PATTERNS:

fun isEmailValid(email: String): Boolean {
    return email.contains("@") && email.contains(".")
}

fun formatOrderId(id: Int): String {
    return "ORD-${id.toString().padStart(6, '0')}"
}

fun calculateDeliveryFee(distanceKm: Double): Double {
    return when {
        distanceKm <= 3.0  -> 0.0
        distanceKm <= 7.0  -> 30.0
        distanceKm <= 15.0 -> 50.0
        else               -> 80.0
    }
}
```

---

## ⚙️ Part 10: Default Parameter Values

### ❌ The Problem Without Defaults
Without defaults, calling functions requires supplying every single argument even if 90% of calls use standard defaults:

```kotlin
// Mandatory 4 parameters:
createAccount("Rohit", "rohit@gmail.com", false, "India")
createAccount("Priya", "priya@gmail.com", false, "India")
```

---

### ⚡ Default Parameters Solution

```kotlin
fun createAccount(
    name: String,
    email: String,
    isPremium: Boolean = false,   // Default: false
    country: String = "India"     // Default: "India"
) {
    println("Account: $name | $email | Premium: $isPremium | $country")
}

fun main() {
    // Calling with required params only:
    createAccount("Rohit", "rohit@gmail.com")
    // Output: Account: Rohit | rohit@gmail.com | Premium: false | India

    // Overriding specific defaults:
    createAccount("Priya", "priya@gmail.com", isPremium = true)
    createAccount("Sam", "sam@gmail.com", country = "USA")
}
```

```kotlin
// SEARCH FUNCTION WITH OPTIONAL DEFAULTS:
fun searchRestaurants(
    query: String,
    city: String = "Bangalore",
    maxDeliveryTime: Int = 60,
    minRating: Double = 3.0,
    sortBy: String = "rating"
) {
    println("Searching '$query' in $city | Max time: ${maxDeliveryTime}m | Min rating: $minRating")
}

fun main() {
    searchRestaurants("Pizza")
    searchRestaurants("Biryani", maxDeliveryTime = 30)
}
```

---

## 🏷️ Part 11: Named Arguments

### 💡 What Are Named Arguments?
Pass arguments by explicitly specifying parameter names at call site.

```kotlin
fun createProfile(name: String, age: Int, city: String, isPremium: Boolean) {
    println("$name | Age: $age | City: $city | Premium: $isPremium")
}

fun main() {
    // Named arguments (Order does not matter!):
    createProfile(
        isPremium = false,
        city = "Mumbai",
        name = "Priya",
        age = 26
    )
}
```

---

### 🛡️ Why Named Arguments Prevent Bugs

```kotlin
// POSITIONAL (Confusing boolean flags):
sendEmail("rohit@gmail.com", "Order Update", "Body text...", true, false, true)

// NAMED (Self-Documenting & Safe):
sendEmail(
    to = "rohit@gmail.com",
    subject = "Order Update",
    body = "Body text...",
    isHtml = true,
    includeCc = false,
    sendNow = true
)
```

---

## ⚡ Part 12: Single Expression Functions

When a function body contains only a single expression, omit curly braces `{}` and `return` keyword using `=`:

```kotlin
// Regular Multi-line Function:
fun addNumbers(a: Int, b: Int): Int {
    return a + b
}

// Single Expression Function:
fun addNumbers(a: Int, b: Int): Int = a + b

// Single Expression with Type Inference:
fun addNumbers(a: Int, b: Int) = a + b
```

```kotlin
// EXAMPLES:
fun formatPrice(price: Double) = "₹$price"
fun calculateTax(amount: Double) = amount * 0.18
fun isAdult(age: Int) = age >= 18
fun maxOf(a: Int, b: Int) = if (a > b) a else b
fun celsiusToFahrenheit(c: Double) = c * 9 / 5 + 32
```

> [!NOTE]
> Use single-expression syntax when functions perform simple 1-line operations. Stick to multi-line syntax for multi-step logic.

---

## 🚫 Part 13: Unit Return Type

`Unit` signifies that a function **does not return any meaningful value**. It is equivalent to `void` in Java/C++, but is an actual object in Kotlin.

```kotlin
// Explicit Unit return type:
fun showWelcomeMessage(name: String): Unit {
    println("Welcome, $name!")
}

// Implicit Unit (Unit can be omitted — default in Kotlin):
fun showWelcomeMessage(name: String) {
    println("Welcome, $name!")
}
```

```kotlin
// COMMON ANDROID UNIT FUNCTIONS:

fun showLoading() {
    progressBar.visibility = View.VISIBLE
}

fun navigateToHome() {
    startActivity(Intent(this, HomeActivity::class.java))
}

fun updateUserName(newName: String) {
    _userName.value = newName
}
```

---

## 🍱 Complete Functions Example — Bringing It All Together

```kotlin
data class Order(
    val id: Int,
    val restaurantName: String,
    val items: List<String>,
    val subtotal: Double
)

const val TAX_RATE = 0.18
const val FREE_DELIVERY_THRESHOLD = 500.0
const val DELIVERY_FEE = 40.0

// Single expression functions:
fun calculateTax(subtotal: Double) = subtotal * TAX_RATE
fun formatCurrency(amount: Double) = "₹${"%.2f".format(amount)}"
fun isEligibleForFreeDelivery(subtotal: Double) = subtotal >= FREE_DELIVERY_THRESHOLD
fun getDeliveryFee(subtotal: Double) = if (isEligibleForFreeDelivery(subtotal)) 0.0 else DELIVERY_FEE

// Default parameters function:
fun createOrder(
    restaurantName: String,
    items: List<String>,
    subtotal: Double,
    id: Int = (1000..9999).random()
): Order = Order(id, restaurantName, items, subtotal)

// Unit function:
fun displayOrderSummary(order: Order, showDetailed: Boolean = false) {
    println("═══════════════════════════════")
    println("Order #${order.id}")
    println("Restaurant: ${order.restaurantName}")

    if (showDetailed) {
        println("Items:")
        order.items.forEachIndexed { index, item -> println("  ${index + 1}. $item") }
    } else {
        println("Items: ${order.items.size} items")
    }

    val tax = calculateTax(order.subtotal)
    val delivery = getDeliveryFee(order.subtotal)
    val total = order.subtotal + tax + delivery

    println("Subtotal:  ${formatCurrency(order.subtotal)}")
    println("Tax (18%): ${formatCurrency(tax)}")
    println("Delivery:  ${if (delivery == 0.0) "FREE" else formatCurrency(delivery)}")
    println("TOTAL:     ${formatCurrency(total)}")
    println("═══════════════════════════════")
}

// Named arguments demonstration function:
fun applyDiscount(
    order: Order,
    discountPercent: Int = 0,
    couponCode: String = "",
    isFirstOrder: Boolean = false
): Double {
    var discount = order.subtotal * discountPercent / 100
    if (isFirstOrder) discount += 50.0
    if (couponCode == "WELCOME10") discount += order.subtotal * 0.10
    return discount
}

fun main() {
    val myOrder = createOrder(
        restaurantName = "Biryani House",
        items = listOf("Chicken Biryani", "Raita", "Cold Drink"),
        subtotal = 450.0
    )

    displayOrderSummary(myOrder)
    println()
    displayOrderSummary(myOrder, showDetailed = true)
    println()

    val discount = applyDiscount(
        order = myOrder,
        isFirstOrder = true,
        couponCode = "WELCOME10"
    )
    println("Your discount: ${formatCurrency(discount)}")
    println("Free delivery? ${isEligibleForFreeDelivery(myOrder.subtotal)}")
}
```

```text
OUTPUT:
═══════════════════════════════
Order #7342
Restaurant: Biryani House
Items: 3 items
Subtotal:  ₹450.00
Tax (18%): ₹81.00
Delivery:  ₹40.00
TOTAL:     ₹571.00
═══════════════════════════════

═══════════════════════════════
Order #7342
Restaurant: Biryani House
Items:
  1. Chicken Biryani
  2. Raita
  3. Cold Drink
Subtotal:  ₹450.00
Tax (18%): ₹81.00
Delivery:  ₹40.00
TOTAL:     ₹571.00
═══════════════════════════════

Your discount: ₹95.00
Free delivery? false
```

---

## 📊 Complete Summary Cheat Sheet

| Concept | Key Points |
| :--- | :--- |
| **`val`** | Immutable read-only variable. Prefer by default. |
| **`var`** | Mutable changeable variable. Use only when value must mutate. |
| **Data Types** | `Int`, `Long` (`L`), `Double`, `Float` (`f`), `String`, `Boolean`, `Char` (`' '`). |
| **Type Inference** | Kotlin automatically infers variable type from assigned value. |
| **String Templates** | Embedded variables (`$var`) and expressions (`${expr}`) inside strings. |
| **`const val`** | Compile-time constant. Top-level or `companion object` only. `SCREAMING_SNAKE_CASE`. |
| **`fun`** | Keyword declaring functions: `fun name(param: Type): ReturnType`. |
| **Default Params** | `param: Type = defaultValue`. Allows callers to skip optional arguments. |
| **Named Args** | `func(paramName = value)`. Improves readability and safety. |
| **Single Expression** | `fun name(params) = expr`. Shorthand for 1-line functions. |
| **`Unit`** | Return type for functions returning no value (like `void`). |

---

## ❓ 5 Quiz Questions

### 🎯 Question 1: `val` vs `var` Decision Making
Decide `val` vs `var` and explain reasoning:
- **a)** Unique account ID fetched from database.
- **b)** Number of items in shopping cart.
- **c)** Base URL of your API server.
- **d)** Weather temperature refreshing every 30 mins.
- **e)** User name profile after initial fetch.
- **f)** Login attempts counter.
- **g)** Keystrokes typed in search bar.

---

### ❓ Question 2: Data Types & Type Inference
- **Part A:** What is the inferred type for: `42`, `42L`, `3.14`, `3.14f`, `"Hello"`, `true`, `'K'`, `1_000_000_000`?
- **Part B (Spot Bugs):**
  1. `const val maxRetries = getMaxRetriesFromConfig()`
  2. `val userName: String = 42`
  3. `var isValid: Boolean = "true"`
  4. `val pi: Int = 3.14159`
  5. `const val appName = "WeatherApp"; fun setup() { appName = "NewName" }`
- **Part C:** Select appropriate Kotlin types for Payment processing: Order amount in rupees & paise, Payment success flag, Transaction reference number (13 digits), Currency symbol (`₹`), Payment method name, Retry attempts.

---

### 📐 Question 3: String Templates & Constants
- **Part A:** Fix String template mistakes:
  1. `println("Hello " + name + ", your score is " + score)`
  2. `println("$name.uppercase() got $score points")`
  3. `val msg = "You have ${items.size()} items"`
- **Part B:** Design `const val` declarations for Chat App: Max message length (500), WebSocket URL, Max file size MB (25), Default room ("General"), Max pinned (3). Explain why `const val` is preferred over `val`.
- **Part C:** Write String template producing: `"User Rohit (ID: 1042) has 3 orders worth ₹750.50. Status: ACTIVE"` using variables `userName`, `userId`, `orderCount`, `totalValue`, `accountStatus = "active"`.

---

### 🔍 Question 4: Functions Analysis
Given `sendPushNotification(userId: Int, title: String, message: String, notificationType: String = "general", badgeCount: Int = 0, soundEnabled: Boolean = true, priority: String = "normal"): Boolean`:
- **a)** How many parameters total? Which have defaults? Which are required?
- **b)** What does the return type signify?
- **c)** Write 4 valid calls: (1) Only required, (2) `soundEnabled = false`, (3) `badgeCount = 5` & `priority = "urgent"`, (4) All parameters in reverse order using named arguments.
- **d)** Convert `fun isValidUserId(userId: Int): Boolean { return userId > 0 }` to single-expression syntax.

---

### 🚀 Question 5: Design and Build Movie Rating App Logic
Write functions for:
1. **`getStarRating(rating: Double): String`**: 9-10 ("⭐⭐⭐⭐⭐ Masterpiece"), 7-8.9 ("⭐⭐⭐⭐ Great"), 5-6.9 ("⭐⭐⭐ Average"), 3-4.9 ("⭐⭐ Below Average"), <3 ("⭐ Poor").
2. **`formatMovieCard(title: String, releaseYear: Int, durationMins: Int, isInCinema: Boolean = false): String`**: Returns e.g. `"Oppenheimer (2023) | 3h 0m | Now in Cinemas"`.
3. **`calculateAverageRating(ratings: List<Double>): Double`**: Single expression returning average.
4. **`displayMovieCard(...)`**: Unit function printing formatted movie summary using 1, 2, 3.
5. `const val` declarations for `MIN_FRESH_RATING` (7.0), `MAX_RATING` (10.0), `UNRATED_LABEL` ("Not Yet Rated"), `APP_NAME` ("CineRate").
6. **`main()`**: Demonstrate all functions together with 3 movies.