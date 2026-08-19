# 🔗 Complete Guide to Lambdas, Higher Order Functions, and Scope Functions

---

## ❓ Part 1: What is a Lambda?

### 💡 Starting With the Problem

Before understanding lambdas, understand WHY they exist.

```kotlin
// SCENARIO: You want to perform different operations on two numbers.
// Without lambdas, you need a separate function for EACH operation:

fun add(a: Int, b: Int): Int = a + b
fun subtract(a: Int, b: Int): Int = a - b
fun multiply(a: Int, b: Int): Int = a * b
fun divide(a: Int, b: Int): Int = a / b

// What if you need 20 different operations?
// 20 separate named functions — cluttering your code.
// What if the operation is only used ONCE?
// Naming it seems wasteful.

// LAMBDAS solve this: define the logic INLINE, right where you need it.
```

---

### 🧩 What is a Lambda?

```text
A LAMBDA is an anonymous function — a function WITHOUT a name.

Instead of declaring a function with 'fun':
  fun double(x: Int): Int { return x * 2 }

You write a lambda:
  { x: Int -> x * 2 }

Same logic. No name. Can be used inline or stored in a variable.

ANALOGY:
  Named function = A recipe in a cookbook (has a name, lives in a book)
  Lambda = Writing a recipe on a sticky note (no formal name, used once, thrown away)

Both ARE recipes (functions).
One has a permanent name. The other is temporary and inline.
```

---

### ✍️ Lambda Syntax — Step by Step

```kotlin
fun main() {

    // FULL LAMBDA SYNTAX:
    // { parameter: Type -> body }

    // Lambda with ONE parameter:
    val double = { x: Int -> x * 2 }
    println(double(5))    // 10
    println(double(21))   // 42

    // Lambda with TWO parameters:
    val add = { a: Int, b: Int -> a + b }
    println(add(3, 7))    // 10
    println(add(15, 25))  // 40

    // Lambda with NO parameters:
    val greet = { println("Hello from lambda!") }
    greet()   // Hello from lambda!

    // Lambda with multiple lines (last expression = return value):
    val complexOperation = { a: Int, b: Int ->
        val sum = a + b
        val doubled = sum * 2
        doubled + 10    // this is the RETURN VALUE (last expression)
    }
    println(complexOperation(5, 3))   // (5+3)*2+10 = 26

    // LAMBDA TYPE NOTATION:
    // (ParameterTypes) -> ReturnType

    val multiply: (Int, Int) -> Int = { a, b -> a * b }
    //             ↑ takes two Ints ↑    ↑ returns Int ↑
    // When type is declared outside, Kotlin infers parameter types inside

    val sayHello: () -> Unit = { println("Hello!") }
    //             ↑ no params ↑  ↑ returns nothing ↑

    val isEven: (Int) -> Boolean = { number -> number % 2 == 0 }

    println(multiply(6, 7))      // 42
    sayHello()                    // Hello!
    println(isEven(4))            // true
    println(isEven(7))            // false
}
```

---

### ⚡ The `it` Keyword — Shorthand for Single Parameter

```kotlin
fun main() {

    // When a lambda has EXACTLY ONE parameter,
    // you can skip naming it and use 'it' instead:

    // WITHOUT 'it' (explicit parameter name):
    val double = { x: Int -> x * 2 }

    // WITH 'it' (when type is inferred from context):
    val double2: (Int) -> Int = { it * 2 }
    //                            ↑ 'it' refers to the single Int parameter

    println(double(10))   // 20
    println(double2(10))  // 20

    // 'it' is extremely common with collection operations:
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

    // filter uses a lambda: (Int) -> Boolean
    // 'it' = each element being checked:
    val evens = numbers.filter { it % 2 == 0 }
    println(evens)   // [2, 4, 6, 8, 10]

    // map uses a lambda: (Int) -> SomeType
    // 'it' = each element being transformed:
    val squared = numbers.map { it * it }
    println(squared)   // [1, 4, 9, 16, 25, 36, 49, 64, 81, 100]

    // 'it' with strings:
    val names = listOf("rohit", "priya", "arjun")
    val capitalized = names.map { it.capitalize() }
    println(capitalized)   // [Rohit, Priya, Arjun]

    // WHEN TO USE 'it' vs named parameter:
    // USE 'it': when the lambda is short and 'it' is obvious
    numbers.filter { it > 5 }           // ✅ clear

    // USE named parameter: when lambda is complex or 'it' is ambiguous
    numbers.filter { number -> number > 5 && number % 3 == 0 }  // ✅ clearer
}
```

> [!TIP]
> Use `it` for short, one-liner lambdas. Use a named parameter when the lambda body is more than one line or when `it` would be ambiguous (e.g., nested lambdas).

---

### 📦 Storing Lambdas in Variables

```kotlin
fun main() {

    // Lambdas can be stored in variables just like any value:

    // The type annotation shows: (InputType) -> OutputType
    val greet: (String) -> String = { name -> "Hello, $name!" }
    val addNumbers: (Int, Int) -> Int = { a, b -> a + b }
    val isBlank: (String) -> Boolean = { it.isBlank() }
    val printLine: (String) -> Unit = { println(it) }

    // CALLING stored lambdas:
    println(greet("Rohit"))          // Hello, Rohit!
    println(addNumbers(10, 32))      // 42
    println(isBlank(""))             // true
    println(isBlank("hello"))        // false
    printLine("Printing from lambda!") // Printing from lambda!

    // STORING lambdas that take no parameters:
    val currentTime: () -> Long = { System.currentTimeMillis() }
    val randomDice: () -> Int = { (1..6).random() }

    println("Time: ${currentTime()}")
    println("Dice: ${randomDice()}")

    // REASSIGNING lambda variables:
    var operation: (Int, Int) -> Int = { a, b -> a + b }
    println(operation(5, 3))   // 8 (addition)

    operation = { a, b -> a * b }  // now it multiplies
    println(operation(5, 3))   // 15 (multiplication)

    // Lambdas stored in variables behave EXACTLY like functions.
    // The variable IS the function. Call it with parentheses: variable(args)
}
```

---

### 🚀 Passing Lambdas as Arguments

```kotlin
// This is where lambdas get really powerful.
// You can pass behavior (a lambda) to a function.

// A function that takes a lambda as parameter:
fun performOperation(a: Int, b: Int, operation: (Int, Int) -> Int): Int {
    println("Performing operation on $a and $b")
    return operation(a, b)  // calls the lambda
}

fun executeWithLogging(message: String, action: () -> Unit) {
    println("START: $message")
    action()  // calls the lambda
    println("END: $message")
}

fun main() {

    // PASSING lambda inline:
    val result1 = performOperation(10, 5) { a, b -> a + b }
    println("Result: $result1")   // 15

    val result2 = performOperation(10, 5) { a, b -> a * b }
    println("Result: $result2")   // 50

    val result3 = performOperation(10, 5) { a, b -> maxOf(a, b) }
    println("Result: $result3")   // 10

    // PASSING stored lambda:
    val divideOp: (Int, Int) -> Int = { a, b -> a / b }
    val result4 = performOperation(20, 4, divideOp)
    println("Result: $result4")   // 5

    // TRAILING LAMBDA SYNTAX:
    // When the LAST parameter of a function is a lambda,
    // you can move it OUTSIDE the parentheses:

    // Normal syntax:
    executeWithLogging("Database Query", { println("Querying database...") })

    // Trailing lambda syntax (preferred — cleaner):
    executeWithLogging("Database Query") {
        println("Querying database...")
    }

    // If lambda is the ONLY parameter, parentheses can be empty or removed:
    val numbers = listOf(1, 2, 3)
    numbers.forEach({ println(it) })   // normal
    numbers.forEach() { println(it) }  // trailing lambda
    numbers.forEach { println(it) }    // cleanest ← this is what you always see!
}
```

---

## 🏗️ Part 2: Higher Order Functions

### 🧩 What is a Higher Order Function?

```text
A HIGHER ORDER FUNCTION (HOF) is a function that:
  1. Takes another function as a PARAMETER, OR
  2. RETURNS another function (or both)

HOFs treat functions as first-class citizens —
just like you pass Int or String as arguments,
you can pass functions as arguments.

WHY this makes code powerful:
  Without HOF: Write separate code for each variation of behavior
  With HOF: Write ONE function that accepts BEHAVIOR as parameter

ANALOGY:
  Without HOF:
    buildRedHouse(...)
    buildBlueHouse(...)
    buildGreenHouse(...)
    // Separate function for each color!

  With HOF:
    buildHouse(color: Color, ...)  ← behavior (color) as parameter
    // ONE function handles ALL colors!
```

---

### 📥 Functions That Take Functions as Parameters

```kotlin
// BASIC HOF EXAMPLES:

// HOF 1: Takes a lambda, executes it:
fun execute(action: () -> Unit) {
    println("Before execution")
    action()
    println("After execution")
}

// HOF 2: Takes a predicate (condition function), uses it:
fun filterNumbers(numbers: List<Int>, predicate: (Int) -> Boolean): List<Int> {
    val result = mutableListOf<Int>()
    for (number in numbers) {
        if (predicate(number)) {  // call the predicate lambda
            result.add(number)
        }
    }
    return result
}

// HOF 3: Takes a transformer function, applies it to all elements:
fun transformNumbers(numbers: List<Int>, transformer: (Int) -> Int): List<Int> {
    return numbers.map { transformer(it) }
}

// HOF 4: Measure execution time — extremely useful!
fun measureTime(operation: () -> Unit): Long {
    val start = System.currentTimeMillis()
    operation()
    val end = System.currentTimeMillis()
    return end - start
}

fun main() {

    // USING execute:
    execute {
        println("This is the action being executed!")
        println("Multiple lines work fine!")
    }
    // Before execution
    // This is the action being executed!
    // Multiple lines work fine!
    // After execution

    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

    // USING filterNumbers with different predicates:
    val evens = filterNumbers(numbers) { it % 2 == 0 }
    println("Evens: $evens")   // [2, 4, 6, 8, 10]

    val greaterThan5 = filterNumbers(numbers) { it > 5 }
    println(">5: $greaterThan5")   // [6, 7, 8, 9, 10]

    val divisibleBy3 = filterNumbers(numbers) { it % 3 == 0 }
    println("÷3: $divisibleBy3")   // [3, 6, 9]

    // ONE function. THREE different behaviors. Clean!

    // USING transformNumbers:
    val doubled = transformNumbers(numbers) { it * 2 }
    println("Doubled: $doubled")   // [2, 4, 6, 8, 10, 12, 14, 16, 18, 20]

    val squared = transformNumbers(numbers) { it * it }
    println("Squared: $squared")   // [1, 4, 9, 16, 25, 36, 49, 64, 81, 100]

    // USING measureTime:
    val timeMs = measureTime {
        Thread.sleep(100)   // simulate a slow operation
        println("Operation completed!")
    }
    println("Time taken: ${timeMs}ms")   // approximately 100ms
}
```

---

### 📤 Functions That Return Functions

```kotlin
// A function that CREATES and RETURNS another function:

// FACTORY for validator functions:
fun createValidator(minLength: Int, maxLength: Int): (String) -> Boolean {
    // Returns a lambda that validates string length:
    return { text ->
        text.length in minLength..maxLength
    }
}

// FACTORY for greeting functions:
fun createGreeting(language: String): (String) -> String {
    return when (language) {
        "hindi"  -> { name -> "Namaste, $name!" }
        "french" -> { name -> "Bonjour, $name!" }
        "tamil"  -> { name -> "Vanakam, $name!" }
        else     -> { name -> "Hello, $name!" }
    }
}

// FACTORY for tax calculators:
fun createTaxCalculator(taxRate: Double): (Double) -> Double {
    return { price -> price + (price * taxRate) }
}

// FUNCTION COMPOSITION — build complex from simple:
fun <A, B, C> compose(f: (B) -> C, g: (A) -> B): (A) -> C {
    return { a -> f(g(a)) }
}

fun main() {

    // CREATING validators for different fields:
    val validateUsername = createValidator(minLength = 3, maxLength = 20)
    val validatePassword = createValidator(minLength = 8, maxLength = 50)
    val validateBio = createValidator(minLength = 0, maxLength = 160)

    println(validateUsername("Rohit"))         // true (5 chars)
    println(validateUsername("Ro"))            // false (2 chars — too short)
    println(validateUsername("a".repeat(25)))  // false (too long)
    println(validatePassword("Pass123!"))      // true
    println(validatePassword("abc"))           // false

    // CREATING greetings:
    val hindiGreet = createGreeting("hindi")
    val frenchGreet = createGreeting("french")
    val englishGreet = createGreeting("english")

    println(hindiGreet("Rohit"))   // Namaste, Rohit!
    println(frenchGreet("Marie"))  // Bonjour, Marie!
    println(englishGreet("John"))  // Hello, John!

    // CREATING tax calculators for different countries:
    val calculateGST = createTaxCalculator(0.18)     // India: 18% GST
    val calculateVAT = createTaxCalculator(0.20)     // UK: 20% VAT
    val calculateTaxFree = createTaxCalculator(0.0)  // Tax-free zone

    println("With GST:  ₹${calculateGST(1000.0)}")    // ₹1180.0
    println("With VAT:  £${calculateVAT(1000.0)}")    // £1200.0
    println("Tax-free: ₹${calculateTaxFree(1000.0)}") // ₹1000.0

    // FUNCTION COMPOSITION:
    val addTax: (Double) -> Double = { it * 1.18 }
    val roundToTwoDecimals: (Double) -> Double = { 
        Math.round(it * 100.0) / 100.0 
    }

    // Compose: first add tax, then round:
    val priceWithRoundedTax = compose(roundToTwoDecimals, addTax)
    println("Price: ₹${priceWithRoundedTax(333.33)}")  // ₹393.33
}
```

---

### 🔗 HOF and Collection Operations — The Connection

```kotlin
// filter, map, forEach, reduce ARE higher order functions!
// They take lambdas as parameters. This is HOF in action.

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val category: String,
    val rating: Double,
    val inStock: Boolean
)

val products = listOf(
    Product(1, "iPhone 15",     99999.0, "Electronics", 4.8, true),
    Product(2, "Samsung S24",   79999.0, "Electronics", 4.6, true),
    Product(3, "Nike Air Max",  12999.0, "Footwear",    4.5, false),
    Product(4, "Levi's Jeans",   4999.0, "Clothing",    4.2, true),
    Product(5, "MacBook Pro",  199999.0, "Electronics", 4.9, true),
    Product(6, "Adidas Ultra",  15999.0, "Footwear",    4.7, true),
    Product(7, "H&M T-Shirt",     999.0, "Clothing",    3.8, true)
)

fun main() {

    // CHAINING HOFs for powerful data pipelines:

    // 1. Get names of all in-stock electronics sorted by price:
    val topElectronics = products
        .filter { it.category == "Electronics" && it.inStock }  // HOF: filter
        .sortedByDescending { it.rating }                        // HOF: sortedByDescending
        .map { "${it.name} — ₹${it.price} (⭐${it.rating})" }  // HOF: map
        .also { println("Electronics count: ${it.size}") }      // HOF: also (peek)

    topElectronics.forEach { println("  $it") }                 // HOF: forEach

    println()

    // 2. Complex transformation pipeline:
    val report = products
        .filter { it.inStock }                          // keep only in-stock
        .groupBy { it.category }                        // group by category (HOF)
        .mapValues { (_, items) ->                      // transform each group (HOF)
            mapOf(
                "count" to items.size,
                "avgPrice" to items.map { it.price }.average(),
                "avgRating" to items.map { it.rating }.average()
            )
        }

    report.forEach { (category, stats) ->
        println("$category: ${stats["count"]} items | " +
                "Avg Price: ₹${"%.0f".format(stats["avgPrice"])} | " +
                "Avg Rating: ${"%.1f".format(stats["avgRating"])}")
    }

    println()

    // 3. CUSTOM HOF for repeated pipeline:
    fun List<Product>.topByCategory(
        category: String,
        limit: Int = 3,
        sortCriteria: (Product) -> Double = { it.rating }
    ): List<Product> {
        return this
            .filter { it.category == category && it.inStock }
            .sortedByDescending(sortCriteria)
            .take(limit)
    }

    val topFootwear = products.topByCategory("Footwear")
    println("Top Footwear:")
    topFootwear.forEach { println("  ${it.name} ⭐${it.rating}") }

    val cheapestElectronics = products.topByCategory(
        "Electronics",
        limit = 2,
        sortCriteria = { -it.price }  // negative = ascending order
    )
    println("\nCheapest Electronics:")
    cheapestElectronics.forEach { println("  ${it.name} ₹${it.price}") }
}
```

> [!IMPORTANT]
> Every Kotlin collection operation you use daily — `filter`, `map`, `forEach`, `reduce`, `groupBy`, `sortedBy` — is a **Higher Order Function**. Understanding HOFs means you already know why Kotlin's collection API is so powerful.

---

## 🔧 Part 3: Scope Functions

### 🧩 What Are Scope Functions?

```text
SCOPE FUNCTIONS are special functions that execute a block of code
in the CONTEXT of an object.

They let you write more concise code by:
  - Accessing object members without repeating the object name
  - Chaining operations cleanly
  - Making intent clear (configuring, transforming, side effects)

Kotlin has 5 scope functions:
  let   — run block if not null, 'it' = object, returns block result
  apply — configure an object, 'this' = object, returns the OBJECT
  run   — like apply but returns BLOCK RESULT, 'this' = object
  also  — side effects, 'it' = object, returns the OBJECT
  with  — call multiple methods, 'this' = object, returns block result

The differences: what 'this' or 'it' refers to, and what is returned.
```

### 📋 Scope Functions Comparison Table

| Function | Object referred to as | Return value | Primary use |
|---|---|---|---|
| `let` | `it` | Block result | Null safety + transform |
| `apply` | `this` | The object | Object configuration |
| `run` | `this` | Block result | Compute + object context |
| `also` | `it` | The object | Side effects (logging) |
| `with` | `this` | Block result | Multiple operations |

---

## 🛡️ Part 4: `let` — Run Code When Not Null

### 🔍 How `let` Works

```text
let:
  - Object referred to as: IT
  - Returns: The RESULT of the block (last expression)
  - Use when: Null safety checks, transformations
  
PATTERN: object?.let { it -> doSomethingWith(it) }
"If object is not null, do something with it"
```

```kotlin
data class UserProfile(
    val id: Int,
    val name: String,
    val email: String,
    val profilePictureUrl: String?,
    val bio: String?
)

fun main() {

    val user = UserProfile(
        id = 1042,
        name = "Rohit Kumar",
        email = "rohit@gmail.com",
        profilePictureUrl = "https://cdn.app.com/rohit.jpg",
        bio = null
    )

    // ─── USE CASE 1: Null safety ──────────────────────────────

    // WITHOUT let — verbose:
    val url = user.profilePictureUrl
    if (url != null) {
        println("Loading image: $url")
        println("URL length: ${url.length}")
    }

    // WITH let — clean and concise:
    user.profilePictureUrl?.let { url ->
        println("Loading image: $url")
        println("URL length: ${url.length}")
    }
    // If profilePictureUrl is null → entire block is SKIPPED

    // bio is null — this block does NOT run:
    user.bio?.let { bio ->
        println("Bio: $bio")     // never prints — bio is null
    }

    // ─── USE CASE 2: let RETURNS the block result ─────────────

    val displayUrl: String = user.profilePictureUrl?.let { url ->
        // Transform the URL for display:
        val filename = url.substringAfterLast("/")
        "Image: $filename"
    } ?: "No profile picture"

    println(displayUrl)   // Image: rohit.jpg

    // If bio is null, Elvis provides the default:
    val displayBio: String = user.bio?.let { bio ->
        bio.uppercase()
    } ?: "No bio yet"
    println(displayBio)   // No bio yet

    // ─── USE CASE 3: Scoped temporary variables ──────────────

    // WITHOUT let — temporary variable in outer scope:
    val temp = "  Rohit Kumar  ".trim().uppercase()
    println(temp)  // ROHIT KUMAR
    // 'temp' now exists in the whole function scope — pollutes scope

    // WITH let — temporary variable scoped to the block:
    "  Rohit Kumar  ".trim().let { trimmed ->
        println(trimmed.uppercase())    // ROHIT KUMAR
        println(trimmed.length)         // 11
        // 'trimmed' only exists inside this let block
    }

    // ─── USE CASE 4: Chaining transformations ────────────────

    val processedEmail = user.email
        .let { email -> email.trim() }          // trim whitespace
        .let { email -> email.lowercase() }      // lowercase
        .let { email -> email.replace("@gmail.com", "") }  // extract username

    println(processedEmail)   // rohit

    // ─── USE CASE 5: API response handling ───────────────────

    data class ApiResponse(val user: UserProfile?, val error: String?)

    val response = ApiResponse(user = user, error = null)

    val result = response.user?.let { fetchedUser ->
        "Welcome back, ${fetchedUser.name}!"
    } ?: response.error?.let { errorMsg ->
        "Error: $errorMsg"
    } ?: "Something went wrong"

    println(result)   // Welcome back, Rohit Kumar!

    // ─── IT vs named parameter ────────────────────────────────

    // Using 'it' (shorter, for simple cases):
    user.profilePictureUrl?.let {
        println("URL: $it")
    }

    // Named parameter (clearer, for complex cases):
    user.profilePictureUrl?.let { photoUrl ->
        val domain = photoUrl.substringAfter("://").substringBefore("/")
        println("Domain: $domain")
    }
}
```

---

## ⚙️ Part 5: `apply` — Configure an Object

### 🔍 How `apply` Works

```text
apply:
  - Object referred to as: THIS (implicit — you access members directly)
  - Returns: THE OBJECT ITSELF (not the block result!)
  - Use when: Configuring/initializing an object's properties

PATTERN: object.apply { property = value; anotherProperty = value }
"Configure this object and return it"

The name 'apply' comes from "apply these configurations to this object"
```

```kotlin
data class NetworkRequest(
    var url: String = "",
    var method: String = "GET",
    var headers: MutableMap<String, String> = mutableMapOf(),
    var body: String? = null,
    var timeoutSeconds: Int = 30,
    var retryCount: Int = 3
)

class TextView {
    var text: String = ""
    var textSize: Float = 14f
    var textColor: String = "#000000"
    var isBold: Boolean = false
    var isItalic: Boolean = false
    var maxLines: Int = Int.MAX_VALUE
    var hint: String = ""

    fun show() = println("TextView: '$text' (size=$textSize, color=$textColor, bold=$isBold)")
}

fun main() {

    // ─── WITHOUT apply — repetitive object name ───────────────

    val request1 = NetworkRequest()
    request1.url = "https://api.foodapp.com/restaurants"
    request1.method = "POST"
    request1.headers["Authorization"] = "Bearer token123"
    request1.headers["Content-Type"] = "application/json"
    request1.body = """{"city": "Bangalore"}"""
    request1.timeoutSeconds = 60
    request1.retryCount = 5
    // "request1." repeated 7 times — verbose!

    // ─── WITH apply — clean object setup ──────────────────────

    val request2 = NetworkRequest().apply {
        url = "https://api.foodapp.com/restaurants"   // this.url = ...
        method = "POST"                                // this.method = ...
        headers["Authorization"] = "Bearer token123"  // this.headers[...] = ...
        headers["Content-Type"] = "application/json"
        body = """{"city": "Bangalore"}"""
        timeoutSeconds = 60
        retryCount = 5
        // 'this' is implicit — you access members DIRECTLY
    }
    // apply RETURNS request2 (the object)!

    println("URL: ${request2.url}")
    println("Method: ${request2.method}")
    println("Timeout: ${request2.timeoutSeconds}")

    // ─── apply in a CHAIN ─────────────────────────────────────

    // Because apply returns the object, you can chain:
    val finalRequest = NetworkRequest()
        .apply {
            url = "https://api.app.com/users"
            method = "GET"
        }
        .apply {
            headers["Accept"] = "application/json"
            headers["App-Version"] = "2.1.0"
        }
        .apply {
            timeoutSeconds = 15
        }

    println(finalRequest.headers)

    // ─── apply with VIEW CONFIGURATION (real Android pattern) ──

    val textView = TextView().apply {
        text = "Hello, Android!"
        textSize = 18f
        textColor = "#FF6200EE"
        isBold = true
        maxLines = 2
    }
    textView.show()
    // TextView: 'Hello, Android!' (size=18.0, color=#FF6200EE, bold=true)

    // ─── apply RETURNS THE OBJECT — use in return statements ──

    fun createLoginRequest(email: String, password: String): NetworkRequest {
        return NetworkRequest().apply {
            url = "https://api.app.com/auth/login"
            method = "POST"
            headers["Content-Type"] = "application/json"
            body = """{"email": "$email", "password": "$password"}"""
            timeoutSeconds = 30
        }
        // No need for a separate variable — apply returns the object directly!
    }

    val loginReq = createLoginRequest("rohit@gmail.com", "password123")
    println("Login request URL: ${loginReq.url}")
    println("Login request body: ${loginReq.body}")

    // ─── apply vs WITHOUT apply — side by side ────────────────

    // Without apply:
    val list1 = mutableListOf<String>()
    list1.add("Item 1")
    list1.add("Item 2")
    list1.add("Item 3")
    list1.sort()

    // With apply:
    val list2 = mutableListOf<String>().apply {
        add("Item 1")
        add("Item 2")
        add("Item 3")
        sort()
    }

    println(list1 == list2)  // true — identical results, cleaner code
}
```

---

## 🧮 Part 6: `run` — Execute Block and Return Result

### 🔍 How `run` Works

```text
run:
  - Object referred to as: THIS (like apply)
  - Returns: The RESULT of the block (like let)
  - Use when: Need object's context AND want to return a computed result
  - TWO forms: object.run { ... } and standalone run { ... }

DIFFERENCE FROM apply:
  apply  → returns the OBJECT
  run    → returns the BLOCK RESULT

DIFFERENCE FROM let:
  let    → 'it' refers to object (explicit)
  run    → 'this' refers to object (implicit — access members directly)
```

```kotlin
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val isPremium: Boolean,
    val orderCount: Int
)

fun main() {

    val user = User(
        id = 1042,
        name = "Rohit Kumar",
        email = "rohit@gmail.com",
        isPremium = true,
        orderCount = 25
    )

    // ─── FORM 1: object.run { } ─────────────────────────────

    // WITHOUT run — need to repeat 'user.':
    val userSummary1 = "ID: ${user.id} | Name: ${user.name} | " +
                       "Premium: ${user.isPremium} | Orders: ${user.orderCount}"

    // WITH run — access members directly via 'this':
    val userSummary2 = user.run {
        // Inside here, 'this' = user
        // Access all properties directly:
        "ID: $id | Name: $name | Premium: $isPremium | Orders: $orderCount"
        // Last expression = return value
    }

    println(userSummary1)  // identical output
    println(userSummary2)

    // ─── run returning computed value ─────────────────────────

    val loyaltyStatus = user.run {
        // Complex computation using multiple properties:
        val basePoints = orderCount * 10
        val premiumBonus = if (isPremium) basePoints / 2 else 0
        val totalPoints = basePoints + premiumBonus
        when {
            totalPoints >= 500 -> "Gold Member 🥇 ($totalPoints pts)"
            totalPoints >= 200 -> "Silver Member 🥈 ($totalPoints pts)"
            else               -> "Bronze Member 🥉 ($totalPoints pts)"
        }
    }
    println(loyaltyStatus)   // Gold Member 🥇 (375 pts)

    // ─── FORM 2: Standalone run { } ─────────────────────────

    // run without an object — useful for grouping logic:
    val config = run {
        val baseUrl = "https://api.app.com"
        val version = "v2"
        val debug = false
        // Last expression is returned:
        mapOf(
            "url" to "$baseUrl/$version",
            "debug" to debug.toString(),
            "timeout" to "30"
        )
    }
    println(config)   // {url=https://api.app.com/v2, debug=false, timeout=30}

    // ─── run with nullable object ────────────────────────────

    val nullableUser: User? = null
    val result = nullableUser?.run {
        "Found user: $name"     // only runs if nullableUser is not null
    } ?: "No user found"

    println(result)   // No user found

    // ─── apply vs run comparison ─────────────────────────────

    data class Config(var host: String = "", var port: Int = 0, var isSecure: Boolean = false)

    // apply: configure object, return the OBJECT:
    val config2 = Config().apply {
        host = "api.app.com"
        port = 443
        isSecure = true
    }
    // config2 is a Config object

    // run: use object's context, return COMPUTED VALUE:
    val connectionString = Config().run {
        host = "api.app.com"
        port = 443
        isSecure = true
        "${if (isSecure) "https" else "http"}://$host:$port"
        // Returns the STRING, not the Config object!
    }
    println(connectionString)   // https://api.app.com:443
}
```

---

## 👀 Part 7: `also` — For Side Effects

### 🔍 How `also` Works

```text
also:
  - Object referred to as: IT (like let)
  - Returns: THE OBJECT ITSELF (like apply)
  - Use when: Perform a SIDE EFFECT but KEEP the object in the chain
              ("also do this, but don't change what we're working with")

A SIDE EFFECT = something that doesn't change the main object:
  - Logging
  - Printing debug info
  - Validation checks
  - Sending analytics events
  - Saving to database

also = "do this thing ALSO (in addition to the main pipeline) and continue"
```

```kotlin
fun main() {

    // ─── BASIC also USAGE ────────────────────────────────────

    val numbers = mutableListOf(1, 2, 3, 4, 5)

    // also lets you peek into the pipeline without disrupting it:
    val result = numbers
        .also { println("Original list: $it") }   // SIDE EFFECT: log original
        .filter { it % 2 == 0 }
        .also { println("After filter: $it") }     // SIDE EFFECT: log filtered
        .map { it * 10 }
        .also { println("After map: $it") }        // SIDE EFFECT: log final

    println("Final result: $result")

    // Output:
    // Original list: [1, 2, 3, 4, 5]
    // After filter: [2, 4]
    // After map: [20, 40]
    // Final result: [20, 40]

    // ─── also for VALIDATION in chains ───────────────────────

    data class Order(
        val id: String,
        val items: List<String>,
        val totalAmount: Double
    )

    fun processOrder(order: Order): Order {
        return order
            .also {
                // Validate before processing:
                require(it.items.isNotEmpty()) { "Order cannot be empty" }
                require(it.totalAmount > 0) { "Amount must be positive" }
                println("✅ Order ${it.id} validated")
            }
            .also {
                // Log for analytics:
                println("📊 Analytics: Order ${it.id} processed — ₹${it.totalAmount}")
            }
            .also {
                // Save to audit trail:
                println("💾 Audit: Order ${it.id} saved to history")
            }
        // Returns the original Order object — all .also() just "peeked" at it
    }

    val myOrder = Order("ORD-001", listOf("Biryani", "Raita"), 330.0)
    val processedOrder = processOrder(myOrder)
    println("Order returned: ${processedOrder.id}")

    // ─── also for LOGGING in production Android code ──────────

    data class User(val id: Int, val name: String, val email: String, val isPremium: Boolean, val orderCount: Int)

    fun fetchUser(userId: Int): User? {
        return User(1042, "Rohit", "rohit@gmail.com", true, 25)
            .also { user ->
                println("DEBUG: User fetched — ID: ${user.id}, Name: ${user.name}")
                // Log to analytics, crash reporting, etc.
            }
    }

    val user = fetchUser(1042)
    println("Got user: ${user?.name}")

    // ─── also vs apply ───────────────────────────────────────

    // apply: for CONFIGURING the object (you change it)
    // also: for SIDE EFFECTS (you don't change the object)
}
```

> [!NOTE]
> Think of `also` as a "transparent observer" — it lets you peek at the object at any point in a chain without changing the chain's result. Perfect for logging and debugging.

---

## 📦 Part 8: `with` — Multiple Operations on an Object

### 🔍 How `with` Works

```text
with:
  - Object referred to as: THIS (implicit, like apply)
  - Returns: The RESULT of the block (like run)
  - Syntax: with(object) { ... }  ← object is the ARGUMENT, not receiver!
  - Use when: Performing multiple operations on an object,
              NOT for chains, when object is NOT nullable

KEY DIFFERENCE from run:
  run  → object.run { }  (extension function — can be null-safe with ?.)
  with → with(object) { } (regular function — object passed as argument)
  with is NOT suitable for nullable objects (use run or let instead)
```

```kotlin
data class Restaurant(
    val name: String,
    val cuisine: String,
    val rating: Double,
    val deliveryTime: Int,
    val minimumOrder: Double,
    val address: String,
    val isOpen: Boolean,
    val menu: List<String>
)

fun main() {

    val restaurant = Restaurant(
        name = "Biryani House",
        cuisine = "Indian",
        rating = 4.5,
        deliveryTime = 30,
        minimumOrder = 200.0,
        address = "42 MG Road, Bangalore",
        isOpen = true,
        menu = listOf("Chicken Biryani", "Veg Biryani", "Raita", "Kebab")
    )

    // ─── WITHOUT with — repeating 'restaurant.' everywhere ───

    println("=== Restaurant Info ===")
    println("Name: ${restaurant.name}")
    println("Cuisine: ${restaurant.cuisine}")
    println("Rating: ⭐${restaurant.rating}")
    println("Delivery: ${restaurant.deliveryTime} mins")
    println("Min Order: ₹${restaurant.minimumOrder}")
    println("Address: ${restaurant.address}")
    println("Status: ${if (restaurant.isOpen) "Open" else "Closed"}")
    println("Menu: ${restaurant.menu.joinToString(", ")}")

    // ─── WITH 'with' — access members directly ───────────────

    with(restaurant) {
        // Inside here, 'this' = restaurant (implicit)
        println("\n=== Restaurant Info (with 'with') ===")
        println("Name: $name")                              // this.name
        println("Cuisine: $cuisine")
        println("Rating: ⭐$rating")
        println("Delivery: $deliveryTime mins")
        println("Min Order: ₹$minimumOrder")
        println("Address: $address")
        println("Status: ${if (isOpen) "Open" else "Closed"}")
        println("Menu: ${menu.joinToString(", ")}")
    }

    // ─── with returning a value ───────────────────────────────

    val restaurantCard = with(restaurant) {
        // Build a formatted card string:
        buildString {
            appendLine("┌─────────────────────────────┐")
            appendLine("│ $name")
            appendLine("│ $cuisine | ⭐$rating")
            appendLine("│ 🕐 $deliveryTime mins | Min: ₹$minimumOrder")
            appendLine("│ 📍 $address")
            appendLine("│ ${if (isOpen) "🟢 Open Now" else "🔴 Closed"}")
            appendLine("└─────────────────────────────┘")
        }
    }
    println(restaurantCard)

    // ─── with for building complex objects ───────────────────

    val sb = StringBuilder()
    val html = with(sb) {
        append("<html>")
        append("<body>")
        append("<h1>Hello from Kotlin!</h1>")
        append("</body>")
        append("</html>")
        toString()  // return value of the block
    }
    println(html)

    // ─── with(object) { } vs object.run { } ──────────────────

    // Prefer 'run' for nullable objects and chaining:
    val nullableRestaurant: Restaurant? = null
    val info = nullableRestaurant?.run {
        "Found: $name"
    } ?: "No restaurant"

    // Prefer 'with' for working with non-nullable objects
    // when you want to call multiple methods or access multiple properties:
    val summary = with(restaurant) {
        "${name} serves $cuisine with rating $rating"
    }
    println(summary)
}
```

---

## 🧭 Part 9: Complete Scope Function Decision Guide

```kotlin
// WHEN TO USE EACH SCOPE FUNCTION:

fun scopeFunctionGuide() {

    data class User(var name: String, var email: String, var isPremium: Boolean)
    val nullableUser: User? = User("Rohit", "rohit@gmail.com", false)
    val user = User("Priya", "priya@gmail.com", true)

    // ─── let ─────────────────────────────────────────────────
    // "If not null, do something with it and return result"
    // Use for: null checks, transformations, scoped variables

    val displayName: String = nullableUser?.let { u ->
        "Welcome, ${u.name}!"    // transform and return
    } ?: "Please log in"
    println(displayName)

    // ─── apply ────────────────────────────────────────────────
    // "Configure this object and give it back"
    // Use for: object initialization, builder pattern

    val configuredUser = User("", "", false).apply {
        name = "New User"
        email = "new@gmail.com"
        isPremium = false
    }
    // Returns the User object
    println(configuredUser.name)

    // ─── run ──────────────────────────────────────────────────
    // "Do work in object's context and return a result"
    // Use for: computation using object properties, non-null check

    val userInfo: String = user.run {
        "$name (${if (isPremium) "Premium" else "Free"})"
    }
    println(userInfo)

    // ─── also ─────────────────────────────────────────────────
    // "Do something extra (side effect) and keep the object"
    // Use for: logging, validation, debugging in chains

    val processedUser = user
        .also { println("Processing: ${it.name}") }    // log
        .also { require(it.email.contains("@")) }       // validate
        // Returns user — not affected by also blocks

    // ─── with ─────────────────────────────────────────────────
    // "Work with this object's context, return result"
    // Use for: multiple operations on same object, not in chains

    val summary = with(user) {
        "$name | $email | Premium: $isPremium"
    }
    println(summary)
}
```

---

## 📱 Part 10: Real Android Examples

### 🔧 `apply` — Configuring RecyclerView

```kotlin
// REAL ANDROID: apply for RecyclerView and View configuration

class ProductListFragment {

    fun setupRecyclerView(recyclerView: RecyclerView, products: List<Product>) {

        // WITHOUT apply — verbose, repeats 'recyclerView.':
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ProductAdapter(products)
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recyclerView.itemAnimator = DefaultItemAnimator()

        // WITH apply — clean, no repetition:
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ProductAdapter(products)
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            itemAnimator = DefaultItemAnimator()
        }
    }

    // apply for creating and configuring views:
    fun createSearchView(): SearchView {
        return SearchView(context).apply {
            queryHint = "Search products..."
            isIconified = false
            maxWidth = Int.MAX_VALUE
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?) = true
                override fun onQueryTextChange(newText: String?) = true
            })
        }
        // Returns the SearchView — ready to use!
    }

    // apply for AlertDialog.Builder (common Android pattern):
    fun showConfirmationDialog(message: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(context).apply {
            setTitle("Confirm")
            setMessage(message)
            setPositiveButton("Yes") { _, _ -> onConfirm() }
            setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            setCancelable(false)
        }.create().show()
    }
}
```

---

### 🛡️ `let` — Handling Nullable API Response

```kotlin
// REAL ANDROID: let for handling nullable API responses in ViewModel

data class Product(val id: Int, val name: String, val price: Double, val category: String)

sealed class UiState {
    object Loading : UiState()
    data class Success(val products: List<Product>) : UiState()
    data class Error(val message: String) : UiState()
}

class ProductViewModel {

    private var _uiState: UiState = UiState.Loading
    val uiState: UiState get() = _uiState

    private var selectedProduct: Product? = null

    // let for null-safe processing of API response:
    fun handleApiResponse(products: List<Product>?) {
        _uiState = products?.let { productList ->
            // productList is guaranteed non-null here
            if (productList.isEmpty()) {
                UiState.Error("No products available")
            } else {
                UiState.Success(productList)
            }
        } ?: UiState.Error("Failed to load products")

        println("State updated: ${_uiState::class.simpleName}")
    }

    // let for processing selected product (might be null):
    fun onProductSelected(productId: Int, products: List<Product>) {
        selectedProduct = products.find { it.id == productId }

        // Only navigate if product exists:
        selectedProduct?.let { product ->
            println("Navigating to detail for: ${product.name}")
            // In real Android: navController.navigate(...)
        } ?: println("Product not found!")
    }

    // Chaining let for complex transformations:
    fun getFormattedProductInfo(productId: Int, products: List<Product>): String {
        return products
            .find { it.id == productId }                     // returns Product?
            ?.let { product ->
                "📦 ${product.name}"                         // transform
                    .let { name -> "$name (${product.category})" }  // chain let
                    .let { nameWithCat -> "$nameWithCat — ₹${product.price}" }
            }
            ?: "Product #$productId not found"
    }
}

// COMBINING all scope functions in a real feature:
class OrderProcessor {

    data class Order(
        var id: String = "",
        var userId: Int = 0,
        var items: List<String> = emptyList(),
        var totalAmount: Double = 0.0,
        var status: String = "pending"
    )

    fun processOrder(userId: Int, items: List<String>?): String {

        // let: handle nullable items
        val validatedItems = items?.let { itemList ->
            itemList.filter { it.isNotBlank() }
        } ?: run {
            println("No items provided")
            return "Order failed: no items"
        }

        // apply: build the order object
        val order = Order().apply {
            id = "ORD-${System.currentTimeMillis()}"
            this.userId = userId
            this.items = validatedItems
            totalAmount = validatedItems.size * 100.0  // simplified pricing
            status = "created"
        }

        // also: logging and validation (side effects)
        order.also { o ->
            println("📋 Order created: ${o.id}")
            println("   User: ${o.userId} | Items: ${o.items.size} | Total: ₹${o.totalAmount}")
        }.also { o ->
            require(o.totalAmount > 0) { "Order amount must be positive" }
            println("✅ Order validated")
        }

        // run: compute the result message
        return order.run {
            "Order $id placed successfully! " +
            "Total: ₹$totalAmount | Status: $status"
        }
    }
}

fun main() {

    println("═══ REAL ANDROID PATTERNS ═══\n")

    // ViewModel + let pattern:
    val viewModel = ProductViewModel()

    println("--- Null API Response ---")
    viewModel.handleApiResponse(null)

    println("\n--- Empty API Response ---")
    viewModel.handleApiResponse(emptyList())

    println("\n--- Valid API Response ---")
    val products = listOf(
        Product(1, "iPhone 15", 99999.0, "Electronics"),
        Product(2, "Samsung S24", 79999.0, "Electronics"),
        Product(3, "Nike Shoes", 12999.0, "Footwear")
    )
    viewModel.handleApiResponse(products)

    println("\n--- Product Selection ---")
    viewModel.onProductSelected(2, products)
    viewModel.onProductSelected(99, products)

    println("\n--- Formatted Product Info ---")
    println(viewModel.getFormattedProductInfo(1, products))
    println(viewModel.getFormattedProductInfo(99, products))

    // Order processing:
    println("\n--- Order Processing ---")
    val processor = OrderProcessor()
    println(processor.processOrder(1042, listOf("Biryani", "Raita", "Cold Drink")))
    println()
    println(processor.processOrder(2051, null))
}
```

### 📟 Output:

```text
═══ REAL ANDROID PATTERNS ═══

--- Null API Response ---
State updated: Error

--- Empty API Response ---
State updated: Error

--- Valid API Response ---
State updated: Success

--- Product Selection ---
Navigating to detail for: Samsung S24
Product not found!

--- Formatted Product Info ---
📦 iPhone 15 (Electronics) — ₹99999.0
Product #99 not found

--- Order Processing ---
📋 Order created: ORD-1705312200000
   User: 1042 | Items: 3 | Total: ₹300.0
✅ Order validated
Order ORD-1705312200000 placed successfully! Total: ₹300.0 | Status: created

No items provided
Order failed: no items
```

---

## 📋 Complete Summary

### 🗺️ Lambdas, HOF & Scope Functions — Master Reference

| Concept | Key Points |
|---|---|
| **Lambda** | Function without a name `{ -> }`. `it` for single parameter. Last expression = return value. Trailing lambda syntax. |
| **Higher Order Function** | Takes or returns functions. `filter`, `map`, `forEach` are HOFs. Makes code reusable and clean. |
| **`let`** | `it`, returns block result. Best for null safety checks. `obj?.let { it -> do something }` |
| **`apply`** | `this`, returns THE OBJECT. Best for object configuration. `obj.apply { property = value }` |
| **`run`** | `this`, returns block result. Like `apply` but returns computed value. `obj.run { compute with this }` |
| **`also`** | `it`, returns THE OBJECT. Best for side effects (logging). `obj.also { log(it) }` |
| **`with`** | `this`, returns block result. Best for multiple ops on one object. `with(obj) { doManyThings() }` |

---

## ❓ Quiz Questions

---

### 📝 Question 1: Lambda Fundamentals

**PART A:** Rewrite each using the appropriate lambda shorthand:

1. `val tripled: (Int) -> Int = { x -> x * 3 }` — Rewrite using `it`
2. `listOf(1,2,3,4,5).filter({ number -> number > 3 })` — Rewrite using trailing lambda syntax
3. `val isLong: (String) -> Boolean = { text -> text.length > 10 }` — Rewrite using `it`

**PART B:** What is the TYPE of each lambda?

- **a)** `{ x: Int, y: Int -> x.toString() + y.toString() }`
- **b)** `{ -> println("hello") }`
- **c)** `{ name: String -> name.length > 5 }`
- **d)** `{ a: Double, b: Double, c: Double -> a + b + c }`

**PART C:** What does this code print? Explain each line.

```kotlin
val operation: (Int) -> (Int) -> Int = { x -> { y -> x + y } }
val addFive = operation(5)
println(addFive(3))      // Line 1
println(addFive(10))     // Line 2
println(operation(2)(8)) // Line 3
```

---

### 📝 Question 2: Higher Order Functions

**PART A:** Write these HOF from scratch:

- **a)** Write a function `repeatAction` that:
  - Takes an `Int` (how many times to repeat)
  - Takes a lambda: `() -> Unit` (the action to repeat)
  - Executes the action that many times
  - Example: `repeatAction(3) { println("Hello") }` prints "Hello" three times

- **b)** Write a function `transformIf` that:
  - Takes a `List<Int>`
  - Takes a predicate: `(Int) -> Boolean`
  - Takes a transformer: `(Int) -> Int`
  - Returns a new list where ONLY elements matching the predicate are transformed (others stay as-is)
  - Example: `transformIf(listOf(1,2,3,4,5), { it % 2 == 0 }, { it * 10 })` returns `[1, 20, 3, 40, 5]`

- **c)** Write a function `retry` that:
  - Takes `maxAttempts: Int`
  - Takes an action: `() -> Boolean` (returns `true` on success, `false` on failure)
  - Retries the action up to `maxAttempts` times
  - Returns `true` if any attempt succeeded, `false` if all failed
  - Prints `"Attempt X of Y"` for each try

**PART B:** Explain what HOF makes possible that regular functions cannot. Why is this code powerful?

```kotlin
fun List<Employee>.getTopPerformers(
    evaluator: (Employee) -> Double,
    limit: Int = 5
): List<Employee> = this.sortedByDescending(evaluator).take(limit)

val topBySalary = employees.getTopPerformers { it.salary }
val topByExperience = employees.getTopPerformers { it.yearsOfExperience.toDouble() }
val topByProjects = employees.getTopPerformers { it.completedProjects.toDouble() }
```

---

### 📝 Question 3: Scope Function Selection

For each scenario, choose the **BEST** scope function and explain **WHY**. Then write the actual code.

- **a)** You have a nullable `String?` called `searchQuery`. If it is not null and not blank, print `"Searching for: $searchQuery"`. Otherwise print `"No search query"`.

- **b)** You are creating a Retrofit instance (Android networking library): `val retrofit = Retrofit.Builder()`. You need to set: `baseUrl`, `addConverterFactory`, `addCallAdapterFactory`, `client`, `validateEagerly`. The Builder has a `build()` method at the end. Which scope function makes this clean?

- **c)** You are filtering a list and want to print the intermediate list size at each step for debugging, without interrupting the chain. Which function fills the `???` slots?

```kotlin
someList
    .filter { it.isActive }
    ??? { println("After filter: ${it.size}") }
    .map { it.name }
    ??? { println("After map: ${it.size}") }
```

- **d)** You have a `User` object and need to compute a String: `"Rohit Kumar | rohit@gmail.com | Member since 2023"`. You access three properties of `User` to build this. You do NOT need the `User` object itself as the result. Which scope function? Why not `apply`?

- **e)** You have a nullable `ApiResponse?`. If not null, extract the `data` field (a `List<Product>?`), filter it, and return the names. Handle all null cases.

---

### 📝 Question 4: Scope Function Deep Dive

Study this code and answer **ALL** questions:

```kotlin
data class NetworkConfig(
    var baseUrl: String = "",
    var timeout: Int = 30,
    var retries: Int = 3,
    var isDebug: Boolean = false,
    var headers: MutableMap<String, String> = mutableMapOf()
)

fun createConfig(): NetworkConfig {
    return NetworkConfig().apply {          // Block A
        baseUrl = "https://api.app.com"
        timeout = 60
        headers["Authorization"] = "Bearer token"
    }
}

fun validateAndLog(config: NetworkConfig?): NetworkConfig? {
    return config
        ?.also {                           // Block B
            println("Validating: ${it.baseUrl}")
            require(it.baseUrl.isNotBlank()) { "URL cannot be blank" }
        }
        ?.also {                           // Block C
            println("Config valid ✅")
        }
}

fun getConnectionString(config: NetworkConfig): String {
    return config.run {                    // Block D
        val protocol = if (isDebug) "http" else "https"
        val host = baseUrl.removePrefix("https://").removePrefix("http://")
        "$protocol://$host?timeout=$timeout&retries=$retries"
    }
}

fun summarize(configs: List<NetworkConfig>): String {
    return with(configs.first()) {         // Block E
        "Primary config: $baseUrl | timeout: ${timeout}s"
    }
}
```

- **a)** In Block A: What does `this` refer to? What does `apply` return? Could you replace `apply` with `run` here? What would change?
- **b)** In Block B and C: What does `it` refer to? What do these `.also()` calls return? What happens if `config` is `null`?
- **c)** In Block D: What does `this` refer to? What does `run` return — the `NetworkConfig` object or the `String`? Could you replace `run` with `apply` here? Explain.
- **d)** In Block E: How is `with` different from the other scope functions? What does `with` return here? What would happen if you used `with(null) { }`?
- **e)** Add one more function using `let`:
  ```kotlin
  fun processConfigResult(config: NetworkConfig?): String
  ```
  - If `config` is not null AND `baseUrl` is not blank → `"Config ready: $baseUrl"`
  - Otherwise → `"Config not ready"`
  - Write it using `let` and the Elvis operator.

---

### 🚀 Question 5: Complete Feature Implementation

Build a complete **PRODUCT SEARCH** feature using lambdas, HOFs, and scope functions.

**Given:**

```kotlin
data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val category: String,
    val rating: Double,
    val description: String?,
    val tags: List<String>
)
```

**TASK 1:** Write a HOF called `searchProducts`:
- Takes `products: List<Product>`, `query: String?`, `filter: (Product) -> Boolean` (default: includes all), `sorter: (Product) -> Comparable<*>` (default: by name), `limit: Int` (default: 10)
- Use `let` for null-safe query handling: if query is blank/null → return all products; if query exists → search name, category, description, tags

**TASK 2:** Write a HOF called `createPriceFilter`:
- Takes `minPrice: Double` (default 0.0), `maxPrice: Double` (default `Double.MAX_VALUE`)
- RETURNS a lambda: `(Product) -> Boolean`

**TASK 3:** Write a function using `with`:
- `fun buildProductReport(products: List<Product>): String`
- Using `with(StringBuilder()) { }`, build a formatted report showing: total products, average price, average rating, categories present, most expensive product, highest rated product

**TASK 4:** Write a function using `also`:
- `fun loadAndLogProducts(source: () -> List<Product>?): List<Product>`
- Calls `source()` to get nullable `List<Product>?`
- Uses `let` to handle null (if null, return `emptyList()`)
- Uses `also` to log: `"Loaded X products"` and `"Categories: [unique categories]"`

**TASK 5:** Write `main()` that:
- Creates a list of at least 8 products
- Uses `createPriceFilter` to create two filters (budget and premium)
- Calls `searchProducts` with different combinations
- Calls `buildProductReport`
- Calls `loadAndLogProducts` with a lambda source

**BONUS:**
- **a)** Why is `{ it * 2 }` a lambda but `fun double(x: Int) = x * 2` is not?
- **b)** How do scope functions prevent the "pyramid of doom" (deeply nested null checks)?
- **c)** When would you choose `let` over `also` even though both use `it`?