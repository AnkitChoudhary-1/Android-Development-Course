/**
 * Phase 1 Project 1b: Data Types Operations & Practical Examples
 * 
 * This program shows:
 * - Operations on each data type
 * - Type conversions
 * - Practical real-world examples
 */

fun main() {
    println("═══════════════════════════════════════════════════")
    println("  DATA TYPES: OPERATIONS & REAL-WORLD EXAMPLES")
    println("═══════════════════════════════════════════════════")
    println()

    // ============ INT OPERATIONS ============
    println("📌 INT OPERATIONS")
    println("─────────────────────────────────────")
    
    val a = 10
    val b = 3
    
    println("val a = $a")
    println("val b = $b")
    println()
    println("Arithmetic Operations:")
    println("  Addition:       a + b = ${a + b}")
    println("  Subtraction:    a - b = ${a - b}")
    println("  Multiplication: a * b = ${a * b}")
    println("  Division:       a / b = ${a / b}  (note: drops decimal for Int)")
    println("  Modulo:         a % b = ${a % b}  (remainder)")
    println()

    // ============ DOUBLE OPERATIONS ============
    println("📌 DOUBLE OPERATIONS (Proper Decimals)")
    println("─────────────────────────────────────")
    
    val x = 10.0
    val y = 3.0
    
    println("val x = $x")
    println("val y = $y")
    println()
    println("Arithmetic Operations:")
    println("  Division: x / y = ${x / y}  (keeps decimal!)")
    println()

    // ============ STRING OPERATIONS ============
    println("📌 STRING OPERATIONS")
    println("─────────────────────────────────────")
    
    val str = "Hello World"
    println("val str = \"$str\"")
    println()
    println("String Methods:")
    println("  length:         ${str.length}")
    println("  uppercase():    ${str.uppercase()}")
    println("  lowercase():    ${str.lowercase()}")
    println("  first():        ${str.first()}")
    println("  last():         ${str.last()}")
    println("  reversed():     ${str.reversed()}")
    println("  contains(\"Wor\"): ${str.contains("Wor")}")
    println("  startsWith(\"H\"): ${str.startsWith("H")}")
    println("  endsWith(\"ld\"): ${str.endsWith("ld")}")
    println("  substring(0, 5): ${str.substring(0, 5)}")
    println("  replace(\"World\", \"Kotlin\"): ${str.replace("World", "Kotlin")}")
    println()

    // ============ BOOLEAN OPERATIONS ============
    println("📌 BOOLEAN OPERATIONS")
    println("─────────────────────────────────────")
    
    val p = true
    val q = false
    val r = true
    
    println("val p = $p")
    println("val q = $q")
    println("val r = $r")
    println()
    println("Logical Operations:")
    println("  p && q (AND):  ${p && q}")
    println("  p || q (OR):   ${p || q}")
    println("  !p (NOT):      ${!p}")
    println("  p == r (EQUAL): ${p == r}")
    println("  p != q (NOT EQUAL): ${p != q}")
    println()

    // ============ COMPARISON OPERATIONS ============
    println("📌 COMPARISON OPERATIONS (Returns Boolean)")
    println("─────────────────────────────────────")
    
    val num1 = 15
    val num2 = 10
    
    println("val num1 = $num1")
    println("val num2 = $num2")
    println()
    println("Comparisons:")
    println("  num1 > num2:   ${num1 > num2}")
    println("  num1 < num2:   ${num1 < num2}")
    println("  num1 >= 15:    ${num1 >= 15}")
    println("  num1 <= 10:    ${num1 <= 10}")
    println("  num1 == num2:  ${num1 == num2}")
    println("  num1 != num2:  ${num1 != num2}")
    println()

    // ============ REAL-WORLD EXAMPLE 1: Bill Calculator ============
    println()
    println("📌 REAL-WORLD EXAMPLE 1: BILL CALCULATOR")
    println("─────────────────────────────────────")
    
    val itemPrice = 500.0
    val taxRate = 0.18  // 18% tax
    val deliveryFee = 40.0
    
    val tax = itemPrice * taxRate
    val totalBill = itemPrice + tax + deliveryFee
    
    println("Food Delivery Order:")
    println("  Item Price:  ₹$itemPrice")
    println("  Tax (18%):   ₹$tax")
    println("  Delivery:    ₹$deliveryFee")
    println("  ───────────────────────")
    println("  Total Bill:  ₹$totalBill")
    println()
    
    // Formatted output
    val formattedTotal = "₹${'%.2f'.format(totalBill)}"
    println("  Formatted:   $formattedTotal")
    println()

    // ============ REAL-WORLD EXAMPLE 2: Temperature Converter ============
    println()
    println("📌 REAL-WORLD EXAMPLE 2: TEMPERATURE CONVERTER")
    println("─────────────────────────────────────")
    
    val celsius = 25.0
    val fahrenheit = (celsius * 9 / 5) + 32
    
    println("Temperature Conversion:")
    println("  $celsius°C = $fahrenheit°F")
    println()

    // ============ REAL-WORLD EXAMPLE 3: Age Validator ============
    println()
    println("📌 REAL-WORLD EXAMPLE 3: AGE VALIDATOR")
    println("─────────────────────────────────────")
    
    val userAge = 17
    val isAdult = userAge >= 18
    val canVote = userAge >= 18
    val canDrive = userAge >= 18
    
    println("User Age: $userAge")
    println("  Can vote?   $canVote")
    println("  Can drive?  $canDrive")
    println("  Is adult?   $isAdult")
    println()

    // ============ REAL-WORLD EXAMPLE 4: Discount Calculator ============
    println()
    println("📌 REAL-WORLD EXAMPLE 4: DISCOUNT CALCULATOR")
    println("─────────────────────────────────────")
    
    val originalPrice = 1000.0
    val discountPercent = 20
    val discountAmount = originalPrice * discountPercent / 100
    val finalPrice = originalPrice - discountAmount
    
    println("Discount Offer:")
    println("  Original Price:  ₹$originalPrice")
    println("  Discount:        ${discountPercent}% (₹$discountAmount)")
    println("  Final Price:     ₹$finalPrice")
    println("  You save:        ₹${discountAmount.toInt()}")
    println()

    // ============ REAL-WORLD EXAMPLE 5: Score Grader ============
    println()
    println("📌 REAL-WORLD EXAMPLE 5: SCORE GRADER")
    println("─────────────────────────────────────")
    
    val score = 85
    val totalMarks = 100
    val percentage = (score * 100) / totalMarks
    
    val grade = when {
        percentage >= 90 -> "A+ (Excellent!)"
        percentage >= 80 -> "A (Good!)"
        percentage >= 70 -> "B (Average)"
        percentage >= 60 -> "C (Pass)"
        else -> "F (Fail)"
    }
    
    println("Score Report:")
    println("  Marks Obtained: $score / $totalMarks")
    println("  Percentage:     $percentage%")
    println("  Grade:          $grade")
    println()

    println("═══════════════════════════════════════════════════")
    println("  ✅ ALL EXAMPLES COMPLETE!")
    println("═══════════════════════════════════════════════════")
}
