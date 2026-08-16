# 🚀 Quick Start Guide — Run This Course Immediately

> This guide walks you through setting up your development environment and running your first Kotlin programs in **under 10 minutes**.

---

## Step 1: Install Kotlin (Choose Your Path)

### Option A: IntelliJ IDEA Community (Recommended) — Windows / Mac / Linux

**Why?** Best IDE for Kotlin. Free. Built-in Kotlin support. No setup needed.

1. **Download:** https://www.jetbrains.com/idea/download/
2. **Install:** Follow the installer wizard.
3. **Launch:** Open IntelliJ IDEA.
4. **Create New Project:**
   - File → New Project
   - Select **Kotlin** (left panel)
   - Choose **JVM** target
   - Click **Create**
5. **Create your first file:**
   - Right-click `src` folder → New → Kotlin File
   - Name it: `HelloWorld.kt`

---

### Option B: Command Line Kotlin (Advanced) — Windows / Mac / Linux

**For developers who prefer terminal/minimal setup:**

#### Mac / Linux:
```bash
# Install Kotlin via Homebrew (Mac)
brew install kotlin

# Install Kotlin via SDKMAN (Mac/Linux)
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install kotlin
```

#### Windows:
```powershell
# Using Chocolatey
choco install kotlin

# Or download from: https://github.com/JetBrains/kotlin/releases
```

**Verify installation:**
```bash
kotlin -version
```

---

## Step 2: Download This Course

```bash
git clone https://github.com/AnkitChoudhary-1/Android-Development-Course.git
cd Android-Development-Course
```

---

## Step 3: Run Your First Kotlin Program

### Using IntelliJ IDEA:

1. **Open the project:**
   - File → Open → Select the cloned folder

2. **Navigate to Phase 1 projects:**
   - `phase-1-kotlin/projects/01-variables-types/VariablesDemo.kt`

3. **Run the program:**
   - Right-click the file → Run 'VariablesDemoKt'
   - OR press `Shift + F10` (Windows) / `Control + R` (Mac)

### Using Command Line:

```bash
# Compile the Kotlin file
kotlinc phase-1-kotlin/projects/01-variables-types/VariablesDemo.kt -include-runtime -d VariablesDemo.jar

# Run it
java -jar VariablesDemo.jar
```

---

## Step 4: Project Structure Overview

```
Android-Development-Course/
│
├── phase-0-computer-engineer/        ✅ Theory (no code)
│   ├── 01-cpu-ram-storage/notes.md
│   ├── 02-process-thread/notes.md
│   └── ...
│
├── phase-1-kotlin/                   🟢 Theory + Code
│   ├── 01-variable-function/         Lecture notes
│   │   └── notes.md
│   │
│   ├── projects/                     ⭐ Executable programs
│   │   ├── 01-variables-types/
│   │   │   ├── VariablesDemo.kt      Run this first!
│   │   │   ├── DataTypesDemo.kt
│   │   │   └── Solution.md           Solutions to exercises
│   │   │
│   │   ├── 02-functions-basics/
│   │   │   ├── FunctionsDemo.kt
│   │   │   └── TemperatureConverter.kt
│   │   │
│   │   ├── 03-real-world-apps/
│   │   │   ├── BillCalculator.kt     Food delivery bill app
│   │   │   ├── PasswordValidator.kt  String manipulation
│   │   │   └── BankingSystem.kt      Comprehensive example
│   │   │
│   │   └── README.md                 How to run each project
│   │
│   └── exercises/
│       ├── Exercise1.md              5 problems to solve
│       ├── Exercise2.md
│       └── solutions/
│           ├── Exercise1-Solution.kt
│           └── Exercise2-Solution.kt
│
└── QUICK_START.md                    (You are here)
```

---

## Step 5: Learning Path

### Day 1: Variables & Data Types
```
1. Read: phase-1-kotlin/01-variable-function/notes.md (Part 1-3)
2. Run: phase-1-kotlin/projects/01-variables-types/VariablesDemo.kt
3. Experiment: Modify the code, change values, see output
4. Exercise: phase-1-kotlin/exercises/Exercise1.md
5. Check: phase-1-kotlin/exercises/solutions/Exercise1-Solution.kt
```

### Day 2: Functions
```
1. Read: phase-1-kotlin/01-variable-function/notes.md (Part 7-13)
2. Run: phase-1-kotlin/projects/02-functions-basics/FunctionsDemo.kt
3. Run: phase-1-kotlin/projects/02-functions-basics/TemperatureConverter.kt
4. Exercise: phase-1-kotlin/exercises/Exercise2.md
```

### Day 3-5: Real-World Applications
```
1. Run: phase-1-kotlin/projects/03-real-world-apps/BillCalculator.kt
2. Read the code. Understand how all concepts combine.
3. Modify it: Add coupon discount, add delivery fee, add taxes.
4. Challenge: Build your own app using Phase 1 concepts.
```

---

## Step 6: IDE Keyboard Shortcuts

**IntelliJ IDEA Essentials:**

| Action | Windows | Mac |
|--------|---------|-----|
| Run Program | `Shift + F10` | `Control + R` |
| Format Code | `Ctrl + Alt + L` | `Cmd + Option + L` |
| Find Symbol | `Ctrl + Shift + O` | `Cmd + Shift + O` |
| Debug | `Shift + F9` | `Control + D` |
| Autocomplete | `Ctrl + Space` | `Control + Space` |
| Rename Variable | `Shift + F6` | `Shift + F6` |

---

## Step 7: Troubleshooting

### Error: "Cannot find kotlinc command"
**Solution:** Kotlin not added to PATH. Either:
- Use IntelliJ IDEA instead (easier)
- OR manually add Kotlin to PATH (see installation steps above)

### Error: "Module not found" in IntelliJ
**Solution:**
- File → Project Structure → Project SDK
- Select JDK 11 or higher
- If not available, download it (IDE will prompt)

### Error: "No main function found"
**Solution:** Every Kotlin program needs a main function:
```kotlin
fun main() {
    println("Hello World")
}
```

### Program compiled but no output?
**Solution:** Make sure you're using `println()` to print to console, not just expressions.

---

## Step 8: Next Steps After Phase 1

Once you complete all Phase 1 projects:

1. **Review Phase 1 Quiz:** Take the quiz at the end of `notes.md`
2. **Read Phase 2 (Android Foundations)** when available
3. **Build Your Own Project:** Create a small CLI app using only Phase 1 concepts
4. **Share on GitHub:** Push your project to GitHub, share the link

---

## Quick Links

- **Kotlin Official Docs:** https://kotlinlang.org/docs/
- **IntelliJ IDEA Shortcuts:** https://resources.jetbrains.com/storage/products/intellij-idea/IntelliJ_IDEA_ReferenceCard.pdf
- **Run Kotlin Online:** https://play.kotlinlang.org/ (no setup needed!)
- **Community Forum:** https://discuss.kotlinlang.org/

---

## 🎯 Your First Task

1. **Install IntelliJ IDEA**
2. **Clone the repository**
3. **Open `phase-1-kotlin/projects/01-variables-types/VariablesDemo.kt`**
4. **Click "Run" (green play button)**
5. **See output in console at bottom**

**That's it!** You're now running Kotlin code. 🎉

---

**Questions?** Open an issue in the repository or check Kotlin docs.
