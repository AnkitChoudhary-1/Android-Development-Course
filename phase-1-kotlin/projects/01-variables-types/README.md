# Phase 1 Project 1: Variables & Data Types

## Overview

This folder contains executable Kotlin programs demonstrating variables and data types from the lecture notes.

## Files

### 1. **VariablesDemo.kt** ⭐ START HERE
Comprehensive demonstration of:
- `val` (immutable) vs `var` (mutable)
- All 7 basic data types: Int, Long, Double, Float, String, Boolean, Char
- Type inference
- String templates with `$` and `${}`
- Real-world examples

**Run it:**
```bash
kotlinc VariablesDemo.kt -include-runtime -d VariablesDemo.jar
java -jar VariablesDemo.jar
```

Or in IntelliJ: Right-click → Run 'VariablesDemoKt'

### 2. **DataTypesDemo.kt**
Detailed examples of:
- Operations on each data type
- String methods (substring, replace, uppercase, etc.)
- Boolean logic
- Comparison operators
- 5 Real-world applications:
  - Bill Calculator
  - Temperature Converter
  - Age Validator
  - Discount Calculator
  - Score Grader

**Run it:**
```bash
kotlinc DataTypesDemo.kt -include-runtime -d DataTypesDemo.jar
java -jar DataTypesDemo.jar
```

## Learning Path

1. **Read the notes first:**
   - `../01-variable-function/notes.md` (Parts 1-6)

2. **Run VariablesDemo.kt:**
   - See all concepts in action
   - Read the output carefully
   - Match output to code

3. **Run DataTypesDemo.kt:**
   - See practical applications
   - Understand how types work together

4. **Experiment:**
   - Modify values in the code
   - Try your own calculations
   - Create new examples

5. **Solve exercises:**
   - `../exercises/Exercise1.md`

## Common Modifications to Try

### Modify VariablesDemo.kt:

```kotlin
// Change the values
val userName = "YOUR_NAME"
var score = 100  // instead of 0
val temperature = 35.5  // instead of 28.5
```

### Change the output format:

```kotlin
// Print with different formatting
println("Score: $score points!")
println("Rating: ${String.format("%.1f", rating)}/5")
```

### Perform different calculations:

```kotlin
// In DataTypesDemo.kt, try:
val itemPrice = 1000.0  // change price
val taxRate = 0.05      // change tax
val discountPercent = 50  // change discount
```

## Expected Output

When you run `VariablesDemo.kt`, you should see:

```
═══════════════════════════════════════════════════
  PHASE 1: VARIABLES & DATA TYPES DEMONSTRATION
═══════════════════════════════════════════════════

📌 SECTION 1: val vs var
─────────────────────────────────────
val userName = "Rohit Kumar"  (Cannot be changed)
val dateOfBirth = "15-Jan-2000"  (Cannot be changed)
...
```

## Troubleshooting

**Q: "Cannot find kotlinc command"**
A: Install Kotlin or use IntelliJ IDEA

**Q: "No output appears"**
A: Make sure you're looking at the console/terminal output

**Q: "Error: Unresolved reference"**
A: Copy the entire code block, not just snippets

## Next Steps

Once comfortable with variables & data types:

1. Complete Exercise1.md
2. Review the quiz in `notes.md`
3. Move to Phase 1 Project 2: **Functions**

## Additional Resources

- **Kotlin Playground:** https://play.kotlinlang.org/
  (Run code online without installation)
- **Official Docs:** https://kotlinlang.org/docs/basic-types.html
- **Try it online:** Copy any code here and paste into Kotlin Playground

---

**Pro Tip:** Modify one value at a time and re-run. See how the output changes. This is the best way to learn!
