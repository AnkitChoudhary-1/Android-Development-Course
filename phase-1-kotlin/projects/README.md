# Phase 1: Practical Kotlin Projects

> **This folder contains executable Kotlin programs that bring theory to life.**

## Structure

```
projects/
├── 01-variables-types/           Variables & Data Types
│   ├── VariablesDemo.kt          ⭐ START HERE
│   ├── DataTypesDemo.kt          Detailed examples
│   └── README.md
│
├── 02-functions-basics/          Functions & Reusability
│   ├── FunctionsDemo.kt          Function basics
│   ├── TemperatureConverter.kt   Practical example
│   └── README.md
│
├── 03-real-world-apps/           Comprehensive Applications
│   ├── BillCalculator.kt         Food delivery bill
│   ├── PasswordValidator.kt      String validation
│   ├── BankingSystem.kt          Full system
│   └── README.md
│
└── README.md                      (You are here)
```

## How to Run

### Option 1: IntelliJ IDEA (Easiest)
1. Open the repository in IntelliJ
2. Navigate to the file you want to run
3. Right-click → Run 'FileName.kt'
4. Output appears in console

### Option 2: Command Line
```bash
# Compile
kotlinc phase-1-kotlin/projects/01-variables-types/VariablesDemo.kt -include-runtime -d VariablesDemo.jar

# Run
java -jar VariablesDemo.jar
```

### Option 3: Kotlin Playground (Online)
1. Go to https://play.kotlinlang.org/
2. Copy-paste any code here
3. Press "Run" (no installation needed!)

## Learning Path

### Week 1: Variables & Types
- Read: `../01-variable-function/notes.md` (Parts 1-6)
- Run: `01-variables-types/VariablesDemo.kt`
- Run: `01-variables-types/DataTypesDemo.kt`
- Do: `../exercises/Exercise1.md`

### Week 2: Functions
- Read: `../01-variable-function/notes.md` (Parts 7-13)
- Run: `02-functions-basics/FunctionsDemo.kt`
- Run: `02-functions-basics/TemperatureConverter.kt`
- Do: `../exercises/Exercise2.md`

### Week 3: Real Applications
- Run: `03-real-world-apps/BillCalculator.kt`
- Run: `03-real-world-apps/PasswordValidator.kt`
- Run: `03-real-world-apps/BankingSystem.kt`
- Build: Your own CLI app

## Key Concepts Per Project

### Project 1: Variables & Data Types
✓ val vs var  
✓ Int, Long, Double, Float, String, Boolean, Char  
✓ Type inference  
✓ String templates  
✓ Real-world calculations  

### Project 2: Functions
✓ Function declaration  
✓ Parameters & return types  
✓ Default parameters  
✓ Named arguments  
✓ Single-expression functions  
✓ Practical utilities  

### Project 3: Real-World Apps
✓ All Phase 1 concepts combined  
✓ User input (stdin)  
✓ Complex logic  
✓ Error handling basics  
✓ Production-like code  

## Experiment & Modify

**Don't just run these programs—modify them!**

### Ideas:

1. **VariablesDemo.kt:**
   - Change user data to your own
   - Add more variables
   - Create new calculations

2. **TemperatureConverter.kt:**
   - Add conversion between Celsius/Fahrenheit/Kelvin
   - Add input validation
   - Format output differently

3. **BillCalculator.kt:**
   - Add multiple items
   - Add coupons/discounts
   - Add tips percentage

## Solutions Available

For each exercise, solutions are in:
```
../exercises/solutions/
```

But try solving first! Only check when stuck.

## What's NOT Here (Yet)

These projects are **CLI (command-line) only**. No Android UI.

Android projects come in **Phase 4** (Jetpack Compose).

But you'll use the Kotlin skills from here every single day in Android!

## Troubleshooting

| Issue | Solution |
|-------|----------|
| "kotlin command not found" | Install Kotlin or use IntelliJ |
| "No output" | Check console/terminal panel |
| "Code doesn't match" | Copy entire file, not snippets |
| "Compile error" | Check Kotlin version (1.8+) |
| "Want to run online?" | Use Kotlin Playground |

## Next After Phase 1

Once you finish all 3 projects + exercises:

1. **Take the Phase 1 Quiz** (in notes.md)
2. **Build your own project:**
   - Use only Phase 1 concepts
   - Create something useful (bill splitter, age calculator, etc.)
   - Share on GitHub
3. **Move to Phase 2** when available (Android Foundations)

## Questions?

- Check README.md in each project folder
- Review lecture notes
- Try Kotlin Playground for quick experiments
- Open an issue in the repo

---

**Happy coding! 🚀**
