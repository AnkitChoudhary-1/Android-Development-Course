# ⚡ Compose Performance Basics — Foundational Awareness Guide

---

## 📋 Prerequisites

This lesson ties together everything you have learned so far:

- State management (`remember`, `mutableStateOf`)
- `LazyColumn` with `key`
- Side effects (`LaunchedEffect`)
- Animations (`animate*AsState`)

---

---

## 🗺️ Chapter 0: How to Read This Lesson

Before we start, here is a clear map of what you need to do now versus what you just need to know exists.

```text
╔═══════════════════════════════════════════════════════════════════════╗
║                  YOUR PERFORMANCE LEARNING MAP                        ║
╠═══════════════════════════════════════════════════════════════════════╣
║                                                                       ║
║  🟢 UNDERSTAND NOW (you should be able to apply these today):         ║
║     • What recomposition is and why excessive recomposition is bad    ║
║     • Using remember{} to avoid resetting state                       ║
║     • Using key{} in LazyColumn items                                 ║
║     • Not creating new objects inside composable bodies               ║
║                                                                       ║
║  🟡 BE AWARE OF NOW (know the concept, don't stress about it):        ║
║     • Stability — what "stable" and "unstable" types mean             ║
║     • Lambda recreation causing recomposition                         ║
║     • Layout Inspector showing recomposition counts                   ║
║                                                                       ║
║  🔵 MASTER IN PHASE 9 (future deep dive):                             ║
║     • @Stable and @Immutable annotations                              ║
║     • CompositionLocal performance implications                       ║
║     • Derived state and derivedStateOf                                ║
║     • Snapshot system internals                                       ║
║     • Compose compiler metrics and reports                            ║
║     • Profiling with Android Studio Profiler                          ║
║     • Skipping recomposition with equals checks                       ║
║     • Lazy layout prefetching and beyondBoundsItemCount               ║
║                                                                       ║
╚═══════════════════════════════════════════════════════════════════════╝
```

> **💡 The Goal of This Lesson:** Build good habits **NOW** so you don't have to fix terrible performance problems **LATER**. Think of it like learning to hold a guitar correctly on day one — you won't play a concert yet, but you won't develop bad habits either.

---

---

## 🔄 Chapter 1: Recomposition — What It Is and Why Too Much Is Bad

### 🔁 Quick Recap (from the State lesson)

```text
COMPOSITION    = Compose runs your @Composable function for the FIRST time.
RECOMPOSITION  = Compose runs it AGAIN because some State changed.
```

---

### ✅ Why Recomposition Is Normal and Good

Recomposition is not a bug. It is how Compose works. Without it, your UI would never update.

```text
User taps a button    → state changes → recomposition → UI updates ✅
User types in a field → state changes → recomposition → text appears ✅
Data arrives from API → state changes → recomposition → list shows ✅
```

---

### ⚠️ Why TOO MUCH Recomposition Is Bad

Every recomposition costs CPU time. Compose has to:

```text
1. Re-run your @Composable function
2. Compare the new UI description with the old one
3. Figure out what actually changed
4. Update only the changed parts on screen
```

If this happens too often or for too many composables, your app:

- 🔴 Drops frames (stuttering/janky scrolling)
- 🔴 Drains battery faster
- 🔴 Feels sluggish to the user
- 🔴 May freeze on low-end devices

---

### 🍽️ The Restaurant Analogy (Extended)

```text
Imagine a restaurant where the chef re-cooks EVERY dish on the menu
every time ONE customer orders a coffee.

Customer: "Can I get a coffee?"
Chef:     "Sure! Let me re-cook all 200 dishes on the menu just to be safe."
          → 200 dishes wasted
          → Kitchen is overwhelmed
          → Everyone's food is late

Good chef: "Coffee? Got it. Just making the coffee."
          → 1 dish prepared
          → Kitchen is efficient
          → Everyone is happy
```

Compose works the same way. It tries to recompose **ONLY** the composables that read the changed state. But if you write your code poorly, you accidentally force Compose to recompose **EVERYTHING**.

---

### 📊 Visualizing Good vs Bad Recomposition

```text
GOOD: Only affected composables recompose
─────────────────────────────────────────
Screen
├── Header              ← NOT recomposed (doesn't read the changed state)
├── ProfileCard         ← RECOMPOSED ✅ (reads the changed name)
├── SettingsList        ← NOT recomposed
└── Footer              ← NOT recomposed

BAD: Everything recomposes unnecessarily
─────────────────────────────────────────
Screen                  ← RECOMPOSED 😱
├── Header              ← RECOMPOSED 😱 (wasted!)
├── ProfileCard         ← RECOMPOSED ✅ (needed)
├── SettingsList        ← RECOMPOSED 😱 (wasted!)
└── Footer              ← RECOMPOSED 😱 (wasted!)
```

> **🟡 Awareness Level:**
> - **For now:** Understand that recomposition is normal but excessive recomposition is bad. You don't need to measure or profile it yet. Just know that the patterns in the rest of this lesson help prevent it.
> - **In Phase 9:** You will learn to use the Compose Compiler Metrics and Android Studio Profiler to find and fix recomposition hotspots.

---

---

## 💾 Chapter 2: `remember{}` — Preventing Unnecessary Work

### 🧠 You Already Know This (from the State Lesson)

`remember` preserves a value across recompositions. Without it, the value is recreated every time the composable re-runs.

---

### ⚡ The Performance Angle

`remember` isn't just about correctness (keeping your counter from resetting). It is also about **performance** — avoiding expensive work on every recomposition.

---

### ❌ BAD: Expensive Calculation on Every Recomposition

```kotlin
@Composable
fun UserProfile(userId: String) {
    // ❌ This runs on EVERY recomposition!
    // If this composable recomposes 60 times per second (during animation),
    // you're sorting this list 60 times per second!
    val sortedFriends = getFriendsList(userId).sortedBy { it.name }

    LazyColumn {
        items(sortedFriends) { friend ->
            Text(friend.name)
        }
    }
}
```

---

### ✅ GOOD: Calculate Once, Remember the Result

```kotlin
@Composable
fun UserProfile(userId: String) {
    // ✅ This runs only ONCE (when the composable first enters).
    // On recomposition, the remembered result is returned instantly.
    val sortedFriends = remember(userId) {
        getFriendsList(userId).sortedBy { it.name }
    }

    LazyColumn {
        items(sortedFriends, key = { it.id }) { friend ->
            Text(friend.name)
        }
    }
}
```

---

### ❌ BAD: Creating a New Painter on Every Recomposition

```kotlin
@Composable
fun Avatar() {
    // ❌ Creates a new Painter object on every recomposition
    val painter = painterResource(id = R.drawable.avatar)

    Image(painter = painter, contentDescription = "Avatar")
}
```

---

### ✅ GOOD: Remember the Painter

```kotlin
@Composable
fun Avatar() {
    // ✅ painterResource already uses remember internally,
    // so this is actually fine in practice.
    // But for custom expensive objects, remember them explicitly.
    val painter = remember { loadCustomPainter() }

    Image(painter = painter, contentDescription = "Avatar")
}
```

> **🟢 Understand Now:** Always wrap expensive computations and object creation in `remember{}`. If the computation depends on a parameter, use `remember(param) {}` so it re-runs only when the parameter changes.

---

---

## 🚫 Chapter 3: Avoid Creating New Objects Inside Composables

### 🛑 The Problem

Every time a composable recomposes, all the code inside it runs again. If you create new objects in the body, you create new objects on every recomposition. This wastes memory and can trigger more recomposition.

---

### ❌ BAD: New List on Every Recomposition

```kotlin
@Composable
fun SettingsScreen() {
    // ❌ Creates a BRAND NEW list object on EVERY recomposition!
    // Even though the contents are identical, it's a different object
    // in memory each time. This can confuse Compose's comparison logic.
    val settings = listOf(
        "Wi-Fi",
        "Bluetooth",
        "Display",
        "Sound",
        "Battery"
    )

    LazyColumn {
        items(settings) { setting ->
            Text(setting, modifier = Modifier.padding(16.dp))
        }
    }
}
```

---

### ✅ GOOD: Remember the List

```kotlin
@Composable
fun SettingsScreen() {
    // ✅ Created ONCE. Same object on every recomposition.
    // No wasted memory. No confused comparisons.
    val settings = remember {
        listOf("Wi-Fi", "Bluetooth", "Display", "Sound", "Battery")
    }

    LazyColumn {
        items(settings) { setting ->
            Text(setting, modifier = Modifier.padding(16.dp))
        }
    }
}
```

---

### ❌ BAD: New Modifier Chain on Every Recomposition

```kotlin
@Composable
fun MyCard() {
    // ❌ Creates new Modifier and PaddingValues objects every recomposition
    val myPadding = PaddingValues(16.dp)
    val myModifier = Modifier
        .fillMaxWidth()
        .padding(myPadding)
        .background(Color.Gray)

    Card(modifier = myModifier) {
        Text("Hello")
    }
}
```

---

### ✅ GOOD: Inline Modifiers or Remember Them

```kotlin
@Composable
fun MyCard() {
    // ✅ Modifiers are lightweight and Compose handles them efficiently
    // when inlined. No need to extract to a variable.
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.Gray)
    ) {
        Text("Hello")
    }
}
```

---

### ❌ BAD: New Data Class Instance on Every Recomposition

```kotlin
data class Theme(val primaryColor: Long, val fontSize: Int)

@Composable
fun ThemedScreen() {
    // ❌ New Theme object every recomposition!
    // Even though the values are the same, it's a new object.
    val theme = Theme(primaryColor = 0xFF6200EE, fontSize = 16)

    Text("Hello", color = Color(theme.primaryColor))
}
```

---

### ✅ GOOD: Remember or Move Outside

```kotlin
// ✅ Option A: Move to a top-level val (best for constants)
private val AppTheme = Theme(primaryColor = 0xFF6200EE, fontSize = 16)

@Composable
fun ThemedScreen() {
    Text("Hello", color = Color(AppTheme.primaryColor))
}

// ✅ Option B: Remember inside the composable
@Composable
fun ThemedScreen() {
    val theme = remember { Theme(primaryColor = 0xFF6200EE, fontSize = 16) }
    Text("Hello", color = Color(theme.primaryColor))
}
```

> **🟢 Understand Now:** Rule of thumb: If you are creating an object (list, data class, lambda, configuration) inside a composable body and its contents don't change, wrap it in `remember {}` or move it outside the function.

---

---

## 🔑 Chapter 4: `key{}` in Lazy Lists — Preventing Unnecessary Rebuilding

### 🧠 You Already Know This (from the LazyColumn Lesson)

Keys tell Compose which items are which, so it doesn't rebuild everything when the list changes.

---

### ⚡ The Performance Angle — Quick Recap

```kotlin
// ❌ BAD: No keys
LazyColumn {
    items(messages) { message ->
        MessageCard(message)
    }
}
// When you delete item #2, Compose thinks items #3, #4, #5 ALL changed
// because their positions shifted. Rebuilds all of them. 😱

// ✅ GOOD: With keys
LazyColumn {
    items(messages, key = { it.id }) { message ->
        MessageCard(message)
    }
}
// When you delete item #2, Compose knows items #3, #4, #5 are the SAME
// items (same IDs, just different positions). Doesn't rebuild them. ✅
```

---

### 📊 Performance Impact Visualized

```text
List of 100 messages. User deletes message #3.

WITHOUT keys:
  Compose recomposes messages #3 through #100  → 97 recompositions 😱
  (because their positions all shifted by 1)

WITH keys:
  Compose removes message #3                    → 1 change ✅
  Messages #4-#100 keep their identity           → 0 recompositions ✅
  Only the layout position changes (cheap)
```

> **🟢 Understand Now:** Always use `key = { it.uniqueId }` in `items()` when your list items have a stable unique identifier. This is one of the easiest and most impactful performance wins in Compose.

---

---

## ⚖️ Chapter 5: Stability and Lambdas — Brief Awareness

> **🟡 This Section Is AWARENESS ONLY:** You do not need to master this now. But you should know the vocabulary so that when you encounter performance issues in Phase 9, you understand what people are talking about.

---

### 🤔 What Is "Stability"?

Compose classifies types as **stable** or **unstable**.

```text
STABLE type:
  → Compose can guarantee that if the object's properties haven't changed,
    the object itself hasn't changed.
  → Compose can SKIP recomposition if the same stable object is passed.
  → Examples: String, Int, Float, Boolean, List<String>, data classes
    with all stable properties

UNSTABLE type:
  → Compose CANNOT guarantee that the object hasn't changed internally.
  → Compose MUST recompose to be safe, even if the object looks the same.
  → Examples: Interfaces, classes with mutable properties, List<Any>,
    third-party classes that Compose can't inspect
```

---

### 💡 Why This Matters (Simple Example)

```kotlin
// ✅ STABLE data class — Compose can skip recomposition
data class User(val id: Int, val name: String)
// All properties are stable (Int, String) → the class is stable

// ❌ UNSTABLE data class — Compose must recompose
data class UserScreen(val user: User, val callback: () -> Unit)
// () -> Unit (lambda) is unstable → the class is unstable
```

---

### ⚠️ The Lambda Problem (Brief)

Every time a composable recomposes, lambdas are recreated.

```kotlin
@Composable
fun ParentScreen() {
    var count by remember { mutableStateOf(0) }

    // ❌ This lambda is a NEW object on every recomposition of ParentScreen.
    // When ParentScreen recomposes (because count changed), a new lambda
    // is created. ChildScreen sees a "different" lambda and recomposes too,
    // even though the lambda does the same thing.
    ChildScreen(onClick = { println("Clicked!") })
}

@Composable
fun ChildScreen(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text("Click me")
    }
}
```

---

### 🛠️ The Fix (Preview — You Will Master This in Phase 9)

```kotlin
@Composable
fun ParentScreen() {
    var count by remember { mutableStateOf(0) }

    // ✅ remember the lambda so it's the SAME object across recompositions
    val onClick = remember { { println("Clicked!") } }

    ChildScreen(onClick = onClick)
}
```

---

### 🟡 Awareness Level Summary

```text
What to know NOW:
  • "Stable" types let Compose skip recomposition
  • "Unstable" types force Compose to recompose
  • Lambdas are recreated on every recomposition
  • This CAN cause unnecessary child recomposition

What to do NOW:
  • Nothing special. Just be aware.
  • If you notice a child recomposing when it shouldn't,
    remember this section and investigate lambdas.

What to master in PHASE 9:
  • @Stable and @Immutable annotations
  • Using remember for lambdas passed to children
  • The Compose compiler's stability inference rules
  • Reading stability reports from the compiler
```

---

---

## 🔍 Chapter 6: Layout Inspector — Brief Awareness

> **🟡 This Section Is AWARENESS ONLY**

---

### 🧐 What Is the Layout Inspector?

The **Layout Inspector** is a tool built into Android Studio that lets you see the live UI tree of your running app.

---

### 🔢 The Recomposition Counter

In the Layout Inspector, you can see how many times each composable has recomposed:

```text
How to access it:
  1. Run your app on a device/emulator
  2. In Android Studio: Tools → Layout Inspector
  3. Select your app's process
  4. Look at the composable tree on the left
  5. Each composable shows a recomposition count like:
     "Text × 3"        (recomposed 3 times)
     "LazyColumn × 1"  (composed once, never recomposed)
```

---

### 🎯 What to Look For

```text
🟢 Normal:
   Header × 1          ← Composed once, never recomposed. Good!
   CounterText × 15    ← Recomposed 15 times because the counter
                          changed 15 times. Expected!

🔴 Suspicious:
   Header × 15         ← Wait, the header recomposed 15 times?
                          It doesn't show the counter!
                          Something is causing unnecessary recomposition.
   SettingsList × 200  ← 200 recompositions? That's way too many.
                          Investigate!
```

> **🟡 Awareness Level:**
> - **For now:** Just know this tool exists. You don't need to use it yet. When you start building real apps and notice jank or stuttering, open the Layout Inspector and check the recomposition counts.
> - **In Phase 9:** You will learn to systematically profile your app, identify recomposition hotspots, and fix them.

---

---

## 📋 Chapter 7: Summary — Bad vs Good Patterns Cheat Sheet

```text
╔═══════════════════════════════════════════════════════════════════════╗
║              PERFORMANCE: BAD vs GOOD PATTERNS                        ║
╠═══════════════════════════════════════════════════════════════════════╣
║                                                                       ║
║  1. EXPENSIVE CALCULATIONS                                            ║
║     ❌ val sorted = list.sortedBy { it.name }                         ║
║     ✅ val sorted = remember(list) { list.sortedBy { it.name } }      ║
║                                                                       ║
║  2. OBJECT CREATION                                                   ║
║     ❌ val items = listOf("A", "B", "C")  // inside composable        ║
║     ✅ val items = remember { listOf("A", "B", "C") }                 ║
║     ✅ private val items = listOf("A", "B", "C")  // outside          ║
║                                                                       ║
║  3. LAZY LIST KEYS                                                    ║
║     ❌ items(data) { item -> Card(item) }                             ║
║     ✅ items(data, key = { it.id }) { item -> Card(item) }            ║
║                                                                       ║
║  4. STATE PLACEMENT                                                   ║
║     ❌ State in a parent that causes all children to recompose         ║
║     ✅ State hoisted to the SMALLEST composable that needs it          ║
║                                                                       ║
║  5. LAMBDAS (awareness)                                               ║
║     ❌ ChildScreen(onClick = { doSomething() })  // new each time     ║
║     ✅ val click = remember { { doSomething() } }                     ║
║        ChildScreen(onClick = click)                                   ║
║                                                                       ║
║  6. MODIFIERS                                                         ║
║     ❌ val mod = Modifier.padding(16.dp).background(...)              ║
║     ✅ Inline modifiers directly on the composable                    ║
║                                                                       ║
╚═══════════════════════════════════════════════════════════════════════╝
```

---

---

## 🏆 Chapter 8: The Golden Rule for Beginners

```text
╔═══════════════════════════════════════════════════════════════════════╗
║                                                                       ║
║   DON'T PREMATURELY OPTIMIZE.                                         ║
║                                                                       ║
║   Write clean, correct, readable Compose code first.                  ║
║   Use remember{} and keys as you've been taught.                      ║
║   Don't worry about micro-optimizing every composable.                ║
║                                                                       ║
║   Compose is ALREADY very fast out of the box.                        ║
║   The Compose compiler does a LOT of optimization for you.            ║
║                                                                       ║
║   Only optimize when you MEASURE a real problem:                      ║
║     1. User reports jank/lag                                          ║
║     2. Layout Inspector shows suspicious recomposition counts          ║
║     3. Profiler shows CPU spikes during scrolling                     ║
║                                                                       ║
║   Premature optimization makes code harder to read and maintain       ║
║   for problems that might not even exist.                             ║
║                                                                       ║
╚═══════════════════════════════════════════════════════════════════════╝
```

---

---

## 📝 Quiz — Test Your Understanding

> Answer each question, then check the answer below it.

---

### ❓ Question 1

```kotlin
@Composable
fun ProductList() {
    val products = listOf(
        Product(1, "Phone"),
        Product(2, "Laptop"),
        Product(3, "Tablet")
    )

    LazyColumn {
        items(products) { product ->
            ProductCard(product)
        }
    }
}
```

What are the **TWO** performance issues in this code?

```text
A) LazyColumn should be replaced with Column, and the list should be sorted
B) The list is recreated on every recomposition (should be remembered),
   and the items() call has no key parameter (should use key = { it.id })
C) The list is too short for LazyColumn — use Column with verticalScroll
D) ProductCard should be inlined instead of extracted to a separate function
```

<details> <summary>Click to reveal answer</summary>

**Answer: B**

There are two issues:

1. **Issue 1:** `listOf(...)` is called inside the composable body without `remember`. Every time `ProductList` recomposes, a brand new list object is created in memory. Even though the contents are identical, it is a different object.
   - **The fix:** `val products = remember { listOf(...) }`.

2. **Issue 2:** `items(products)` has no `key` parameter. If the list changes (an item is added, removed, or reordered), Compose cannot identify which items are the same and which are different. It falls back to comparing by position, which causes unnecessary recomposition of items that didn't actually change.
   - **The fix:** `items(products, key = { it.id })`.

- Option C is wrong — `LazyColumn` works fine for small lists too, and using it consistently is good practice.
- Option D is wrong — extracting composables into separate functions is actually **GOOD** for performance because it gives Compose more opportunities to skip recomposition of unchanged parts.

</details>

---

### ❓ Question 2

Your app has a screen with a header, a counter, and a footer. The counter updates every second. You notice in the Layout Inspector that the header and footer are recomposing every second too, even though they don't display the counter. What is the most likely cause?

```text
A) The header and footer are reading the counter state directly
B) The counter state is declared in a parent composable that also contains
   the header and footer, so when the counter changes, the entire parent
   recomposes, which causes all its children to recompose as well
C) Compose always recomposes the entire screen on every state change —
   this is normal and cannot be fixed
D) The header and footer are using remember incorrectly
```

<details> <summary>Click to reveal answer</summary>

**Answer: B**

This is the most common cause of unnecessary recomposition. If the state (`counter`) is declared in a parent composable that also contains the header and footer, then when the counter changes, the parent recomposes. During recomposition, the parent re-invokes all its children, including the header and footer.

The fix is to move the state down to the smallest composable that actually needs it. If only the counter display needs the counter state, extract the counter into its own composable and let it own the state:

```kotlin
@Composable
fun Screen() {
    Header()         // Never recomposes due to counter ✅
    CounterWidget()  // Owns its own state, recomposes independently ✅
    Footer()         // Never recomposes due to counter ✅
}

@Composable
fun CounterWidget() {
    var count by remember { mutableStateOf(0) }
    // Only THIS composable recomposes when count changes
    Text("Count: $count")
}
```

Option C is wrong — Compose is smart enough to skip recomposition of composables that don't read the changed state, **BUT** only if they are separate composable function calls (not inline code in the same function).

</details>

---

### ❓ Question 3

What does "stability" mean in the context of Compose performance?

```text
A) A stable app is one that doesn't crash
B) A stable type is one that Compose can guarantee hasn't changed if its
   observable properties haven't changed, allowing Compose to skip
   recomposition when the same instance is passed again
C) A stable composable is one that never recomposes
D) Stability refers to the animation frame rate being consistent
```

<details> <summary>Click to reveal answer</summary>

**Answer: B**

In Compose, "stability" is a specific technical concept related to recomposition optimization. A stable type is one where Compose can confidently say: *"If I compare this object to the previous version and all its properties are equal, then nothing has changed, and I can skip recomposing the composables that received this object."*

Primitive types (`Int`, `Float`, `Boolean`), `String`, and data classes with all stable properties are stable by default. Interfaces, classes with mutable properties, and types Compose can't inspect are unstable, meaning Compose must recompose to be safe, even if the object looks the same.

This is a 🟡 awareness-level concept for now. You will dive deep into stability rules, the `@Stable` annotation, and compiler stability reports in Phase 9.

</details>

---

### ❓ Question 4

```kotlin
@Composable
fun ExpensiveScreen() {
    var showDetails by remember { mutableStateOf(false) }

    // This function takes 50ms to run (heavy computation)
    val analyticsReport = generateAnalyticsReport()

    Column {
        Button(onClick = { showDetails = !showDetails }) {
            Text("Toggle Details")
        }

        if (showDetails) {
            Text("Details: ...")
        }

        Text("Report: $analyticsReport")
    }
}
```

The user taps the button rapidly 10 times. How many times does `generateAnalyticsReport()` run?

```text
A) 1 time — it's outside the if block so it only runs once
B) 10 times — every time showDetails changes, the entire
   ExpensiveScreen composable recomposes, and generateAnalyticsReport()
   is called again because it's not wrapped in remember
C) 0 times — Compose automatically caches function results
D) 2 times — once for the initial composition and once for the first toggle
```

<details> <summary>Click to reveal answer</summary>

**Answer: B**

Every time `showDetails` changes, `ExpensiveScreen` recomposes. During recomposition, all the code in the function body runs again, including `generateAnalyticsReport()`. Since it is not wrapped in `remember {}`, Compose has no way to know that the result should be cached. So it runs the expensive 50ms computation on every single recomposition.

10 taps = 10 state changes = 10 recompositions = 10 × 50ms = **500ms of wasted computation!**

**The fix:**

```kotlin
val analyticsReport = remember { generateAnalyticsReport() }
```

Now the report is generated only once. On recomposition, the remembered result is returned instantly.

If the report depends on some input that can change, use:

```kotlin
val analyticsReport = remember(userId) { generateAnalyticsReport(userId) }
```

This re-runs only when `userId` changes, not on every recomposition.

</details>

---

### ❓ Question 5

Which of the following statements about Compose performance is **TRUE**?

```text
A) You should optimize every composable for maximum performance from
   day one, even if the app runs smoothly
B) Recomposition is a bug in Compose that should be eliminated entirely —
   a well-written app should have zero recompositions
C) Compose is already fast by default, and you should only optimize when
   you measure a real performance problem. Use basic best practices
   (remember, keys, proper state placement) and defer deep optimization
   to when it's actually needed
D) LazyColumn is slower than Column with a for loop because of the
   overhead of lazy loading, so you should always prefer Column
```

<details> <summary>Click to reveal answer</summary>

**Answer: C**

This is the golden rule of performance optimization in Compose (and in software engineering in general): **don't prematurely optimize**.

Compose is designed to be fast out of the box. The Compose compiler performs many optimizations automatically (skipping recomposition of unchanged composables, grouping updates, etc.). By following the basic best practices you learned in this lesson — using `remember`, providing `key` in lazy lists, placing state correctly, and avoiding unnecessary object creation — your app will perform well in the vast majority of cases.

Only dive into deep optimization (Phase 9 topics like stability annotations, compiler metrics, derived state, and profiling) when you have a measured problem: janky scrolling, high CPU usage, battery drain, or user complaints.

- **Option A** is wrong — premature optimization wastes development time and makes code harder to read.
- **Option B** is wrong — recomposition is the fundamental mechanism that makes Compose reactive; zero recompositions would mean a frozen, unresponsive UI.
- **Option D** is wrong — `LazyColumn` is dramatically faster than `Column` for large lists because it only creates visible items.

</details>

---

---

## 🎉 Conclusion & Key Takeaways

Congratulations! 🎉 You now have a solid foundational understanding of Compose performance. You know the common pitfalls, the basic best practices, and — most importantly — you know what you don't need to worry about yet.

### 🎯 What You Take Away From This Lesson

```text
✅ DO from today:
   • Use remember{} for expensive computations and constant objects
   • Always use key = { it.id } in LazyColumn/LazyRow items
   • Place state as close to where it's used as possible
   • Don't create new lists/objects inside composable bodies

🟡 KNOW EXISTS (use when needed):
   • Stability and lambda recreation
   • Layout Inspector recomposition counts

🔵 FUTURE (Phase 9):
   • Deep profiling, compiler metrics, @Stable, derivedStateOf
```