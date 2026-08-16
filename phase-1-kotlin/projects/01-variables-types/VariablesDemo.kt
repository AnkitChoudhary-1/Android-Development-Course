/**
 * Phase 1 Project 1: Variables and Data Types Demo
 * 
 * This program demonstrates:
 * - Declaring variables with val and var
 * - All basic Kotlin data types (Int, Long, Double, Float, String, Boolean, Char)
 * - Type inference
 * - String templates
 * 
 * Run this to see everything in action!
 */

fun main() {
    println("═══════════════════════════════════════════════════")
    println("  PHASE 1: VARIABLES & DATA TYPES DEMONSTRATION")
    println("═══════════════════════════════════════════════════")
    println()

    // ============ SECTION 1: val vs var ============
    println("📌 SECTION 1: val vs var")
    println("─────────────────────────────────────")
    
    // val = immutable (cannot change after assignment)
    val userName = "Rohit Kumar"
    val dateOfBirth = "15-Jan-2000"
    val pi = 3.14159
    
    println("val userName = \"$userName\"  (Cannot be changed)")
    println("val dateOfBirth = \"$dateOfBirth\"  (Cannot be changed)")
    println("val pi = $pi  (Cannot be changed)")
    println()
    
    // This would cause an ERROR (uncomment to see):
    // userName = "Priya"  // ERROR: Val cannot be reassigned
    
    // var = mutable (can change anytime)
    var score = 0
    var isLoggedIn = false
    var currentTemperature = 28.5
    
    println("var score = $score  (Can be changed)")
    println("var isLoggedIn = $isLoggedIn  (Can be changed)")
    println("var currentTemperature = $currentTemperature  (Can be changed)")
    println()
    
    // Now let's change the var values
    score = 95
    isLoggedIn = true
    currentTemperature = 31.2
    
    println("After modification:")
    println("var score = $score  ✓ Changed successfully")
    println("var isLoggedIn = $isLoggedIn  ✓ Changed successfully")
    println("var currentTemperature = $currentTemperature  ✓ Changed successfully")
    println()

    // ============ SECTION 2: Data Types ============
    println()
    println("📌 SECTION 2: DATA TYPES")
    println("─────────────────────────────────────")
    
    // Int - Whole numbers (32 bits)
    val age: Int = 24
    val score2: Int = 1500
    val retryCount: Int = 3
    
    println("Int Examples:")
    println("  val age: Int = $age")
    println("  val score: Int = $score2")
    println("  val retryCount: Int = $retryCount")
    println()
    
    // Long - Very large whole numbers (64 bits)
    val worldPopulation: Long = 8_000_000_000L
    val fileSize: Long = 5_368_709_120L
    val timestamp: Long = 1705312200000L
    
    println("Long Examples (for very large numbers):")
    println("  val worldPopulation: Long = $worldPopulation")
    println("  val fileSize: Long = $fileSize bytes")
    println("  val timestamp: Long = $timestamp ms")
    println()
    
    // Double - Decimal numbers (64 bits, default for decimals)
    val temperature: Double = 28.5
    val rating: Double = 4.7
    val latitude: Double = 12.9716
    val longitude: Double = 77.5946
    
    println("Double Examples (default for decimals):")
    println("  val temperature: Double = $temperature°C")
    println("  val rating: Double = $rating/5")
    println("  Location: $latitude, $longitude")
    println()
    
    // Float - Single precision decimal (32 bits)
    val animationProgress: Float = 0.75f
    val buttonWidth: Float = 200.5f
    
    println("Float Examples (less precise but smaller):")
    println("  val animationProgress: Float = $animationProgress (75%)")
    println("  val buttonWidth: Float = ${buttonWidth}dp")
    println()
    
    // String - Text data
    val email: String = "rohit@gmail.com"
    val message: String = "Welcome to Kotlin!"
    val multiLine = """
        Hello Rohit,
        Welcome to Phase 1!
        This is a multiline string.
    """.trimIndent()
    
    println("String Examples:")
    println("  val email: String = \"$email\"")
    println("  val message: String = \"$message\"")
    println("  Multiline string:")
    println(multiLine)
    println()
    
    // String operations
    println("String Operations:")
    println("  message.length = ${message.length}")
    println("  message.uppercase() = ${message.uppercase()}")
    println("  message.lowercase() = ${message.lowercase()}")
    println("  message.contains(\"Kotlin\") = ${message.contains("Kotlin")}")
    println()
    
    // Boolean - True/False
    val isLoggedIn2: Boolean = true
    val hasInternet: Boolean = false
    val isNightMode: Boolean = false
    
    println("Boolean Examples:")
    println("  val isLoggedIn: Boolean = $isLoggedIn2")
    println("  val hasInternet: Boolean = $hasInternet")
    println("  val isNightMode: Boolean = $isNightMode")
    println()
    
    // Boolean operations
    val a = true
    val b = false
    println("Boolean Operations:")
    println("  true && false = ${a && b}  (AND)")
    println("  true || false = ${a || b}  (OR)")
    println("  !true = ${!a}  (NOT)")
    println()
    
    // Char - Single character
    val firstLetter: Char = 'R'
    val grade: Char = 'A'
    val newLine: Char = '\n'
    
    println("Char Examples:")
    println("  val firstLetter: Char = '$firstLetter'")
    println("  val grade: Char = '$grade'")
    println("  Character operations:")
    println("    firstLetter.isUpperCase() = ${firstLetter.isUpperCase()}")
    println("    firstLetter.lowercaseChar() = ${firstLetter.lowercaseChar()}")
    println()

    // ============ SECTION 3: Type Inference ============
    println()
    println("📌 SECTION 3: TYPE INFERENCE (Kotlin Guesses Types)")
    println("─────────────────────────────────────")
    
    // Without explicit types, Kotlin infers from the assigned value
    val inferredInt = 24                    // Kotlin infers: Int
    val inferredString = "Rohit"           // Kotlin infers: String
    val inferredDouble = 28.5              // Kotlin infers: Double
    val inferredBoolean = true             // Kotlin infers: Boolean
    val inferredChar = 'A'                 // Kotlin infers: Char
    val inferredLong = 8_000_000_000L      // Kotlin infers: Long
    val inferredFloat = 3.14f              // Kotlin infers: Float
    
    println("Kotlin automatically infers types:")
    println("  val inferredInt = 24  →  Type: Int")
    println("  val inferredString = \"Rohit\"  →  Type: String")
    println("  val inferredDouble = 28.5  →  Type: Double")
    println("  val inferredBoolean = true  →  Type: Boolean")
    println("  val inferredChar = 'A'  →  Type: Char")
    println("  val inferredLong = 8_000_000_000L  →  Type: Long")
    println("  val inferredFloat = 3.14f  →  Type: Float")
    println()

    // ============ SECTION 4: String Templates ============
    println()
    println("📌 SECTION 4: STRING TEMPLATES (Dollar Sign Embedding)")
    println("─────────────────────────────────────")
    
    val name = "Rohit"
    val age2 = 24
    val city = "Bangalore"
    
    // Simple embedding with $
    val introduction = "Hello, $name! You are $age2 years old from $city."
    println("Simple embedding:")
    println("  \"Hello, \$name! You are \$age2 years old from \$city.\"")
    println("  Result: $introduction")
    println()
    
    // Expressions with ${}
    println("Expressions with \${...}:")
    println("  \"Name length: \${name.length} characters\"")
    println("  Result: Name length: ${name.length} characters")
    println()
    
    println("  \"Next year age: \${age2 + 1}\"")
    println("  Result: Next year age: ${age2 + 1}")
    println()
    
    println("  \"Is adult? \${age2 >= 18}\"")
    println("  Result: Is adult? ${age2 >= 18}")
    println()
    
    // Real Android usage example
    val restaurantName = "Biryani House"
    val rating2 = 4.5
    val deliveryTime = 30
    val displayText = "$restaurantName ⭐ $rating2 • $deliveryTime mins"
    
    println("Real-world example (like in food delivery apps):")
    println("  $displayText")
    println()

    // ============ SECTION 5: Constants ============
    println()
    println("📌 SECTION 5: CONSTANTS (const val)")
    println("─────────────────────────────────────")
    println("(These are defined at top-level or in companion object)")
    println()
    println("Examples from this program:")
    println("  const val APP_VERSION = \"1.0.0\"")
    println("  const val MAX_RETRIES = 3")
    println("  const val BASE_URL = \"https://api.foodapp.com\"")
    println()

    // ============ SUMMARY ============
    println()
    println("═══════════════════════════════════════════════════")
    println("  ✅ ALL DATA TYPES DEMONSTRATED!")
    println("═══════════════════════════════════════════════════")
    println()
    println("📚 Next: Read phase-1-kotlin/01-variable-function/notes.md")
    println("🎯 Challenge: Modify this code and experiment!")
    println()
}
