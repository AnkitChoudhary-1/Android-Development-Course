# 🧬 Project 8: Multi-Source Data Aggregation Engine

## 🎯 Overview
Generics, Coroutines, and Flow are the **core pillars** of modern Kotlin and Android development — from type-safe API layers to non-blocking network calls to reactive data streams. In this project, you will build a complete **Multi-Source Data Aggregation Engine** that fetches data from multiple simulated sources concurrently, processes it through type-safe generic pipelines, and streams real-time results via Flow — exercising every major concept from this module.

---

## 🛠️ Concepts Practiced
- **Generics:** Generic classes, generic functions, bounded type parameters (`<T : Comparable<T>>`), multiple type parameters (`<K, V>`), generic interfaces, real-world `Result<T>` wrapper pattern
- **Coroutines:** `suspend` functions, `launch`, `async`/`await` for parallel execution, `delay` vs `Thread.sleep`, `Dispatchers` (IO/Default), `withContext`, `coroutineScope`, `measureTimeMillis` for performance comparison
- **Flow:** `flow {}` builder, `emit`, `collect`, `map`, `filter`, `take`, `onEach`, `toList`, `flowOf`, combining flows, real-time streaming patterns

---

## 📋 Requirements & Features

### 1. Generic Data Models & Type-Safe Containers
- `DataPacket<T>`: Generic wrapper with `source`, `timestamp`, `data: T`, `priority`
- `Result<T>`: Sealed class with `Success<T>`, `Error<T>`, and `Loading` — the exact pattern used in Android ViewModels
- `SortableCache<T : Comparable<T>>`: Bounded generic cache that stores items and can return them sorted
- `DataTransformer<I, O>`: Generic interface for input→output transformations with multiple type params

### 2. Coroutine-Powered Data Fetching
- Simulate 4 data sources (Users, Products, Orders, Analytics) each with different latencies
- **Sequential** fetching: demonstrate the slow `2 + 3 + 2 + 1 = 8 second` approach
- **Parallel** fetching with `async`/`await`: demonstrate the fast `max(2, 3, 2, 1) = 3 second` approach
- `withContext(Dispatchers.Default)` for CPU-heavy aggregation work
- Error handling with `try/catch` inside coroutines

### 3. Flow-Based Real-Time Streaming
- `sensorDataStream()`: Emits simulated sensor readings every 500ms
- `priceTickerFlow()`: Emits stock price updates with random fluctuations
- Flow operators pipeline: `filter` → `map` → `onEach` (logging) → `take` → `collect`
- Demonstrate List vs Flow timing difference

### 4. Combined System — The Full Pipeline
- Generic `DataSource<T>` interface with `suspend fun fetch(): Result<T>`
- Coroutine-powered parallel fetching of all sources
- Flow-based progress reporting that emits status updates as each source completes
- Final aggregated report using all three concepts together

---

## 💻 Sample Output

```text
════════════════════════════════════════════════════════════════
   🧬 MULTI-SOURCE DATA AGGREGATION ENGINE                    
════════════════════════════════════════════════════════════════

━━━ STAGE 1: Generics — Type-Safe Containers ━━━━━━━━━━━━━━━━

  📦 DataPacket Demo:
    Packet 1: [STRING] source=API, data="Hello Generics"
    Packet 2: [INT]    source=Sensor, data=42
    Packet 3: [LIST]   source=DB, data=[Kotlin, Coroutines, Flow]

  🎯 Result<T> Sealed Class:
    ✅ Success: User(name=Rahul, email=rahul@dev.io)
    ❌ Error: Network timeout after 5000ms
    ⏳ Loading...

  📊 SortableCache<Int>:
    Added: [42, 17, 85, 3, 61, 29]
    Sorted: [3, 17, 29, 42, 61, 85]
    Top 3: [85, 61, 42]
    Contains 42? true

  🔄 DataTransformer<String, Int> (String → Length):
    "Kotlin" → 6
    "Coroutines" → 10
    "Flow" → 4

━━━ STAGE 2: Coroutines — Sequential vs Parallel ━━━━━━━━━━━━

  🐌 Sequential Fetching:
    ⏳ Fetching users...       (2000ms)
    ⏳ Fetching products...    (3000ms)
    ⏳ Fetching orders...      (2000ms)
    ⏳ Fetching analytics...   (1000ms)
    ✅ All fetched in ~8000ms

  🚀 Parallel Fetching (async/await):
    ⏳ All 4 sources launched simultaneously...
    ✅ All fetched in ~3000ms
    ⚡ Speed improvement: 62% faster!

  📊 Results:
    Users:     5 records
    Products:  4 records
    Orders:    6 records
    Analytics: 3 records

━━━ STAGE 3: Flow — Real-Time Data Streams ━━━━━━━━━━━━━━━━━━

  📈 Stock Price Ticker (5 ticks):
    [0.0s] GOOG: $150.32  ▲
    [0.5s] GOOG: $148.91  ▼
    [1.0s] GOOG: $151.05  ▲
    [1.5s] GOOG: $149.73  ▼
    [2.0s] GOOG: $152.18  ▲

  🌡️ Sensor Stream (filtered > 30°C, mapped to alerts):
    ⚠️ HIGH TEMP: 35.2°C at sensor-01
    ⚠️ HIGH TEMP: 41.7°C at sensor-03
    ⚠️ HIGH TEMP: 38.9°C at sensor-01

  ⏱️ List vs Flow Timing:
    List approach:  waited 5s, got all at once → [1, 2, 3, 4, 5]
    Flow approach:  got each value as it was ready (1s apart)

━━━ STAGE 4: Combined Pipeline — All Three Together ━━━━━━━━━

  🔄 Aggregation Pipeline Starting...
    [0.0s] 📡 Status: Fetching from 4 sources in parallel...
    [1.0s] ✅ Analytics source complete (3 records)
    [2.0s] ✅ Users source complete (5 records)
    [2.0s] ✅ Orders source complete (6 records)
    [3.0s] ✅ Products source complete (4 records)

  ┌──────────────────────────────────────────────┐
  │       📊 AGGREGATION SUMMARY REPORT          │
  ├──────────────────────────────────────────────┤
  │ Total Sources       : 4                      │
  │ Total Records       : 18                     │
  │ Successful Sources  : 4                      │
  │ Failed Sources      : 0                      │
  │ Total Fetch Time    : ~3000ms (parallel)     │
  │ Records by Source:                            │
  │   Users      : 5 records  ✅                 │
  │   Products   : 4 records  ✅                 │
  │   Orders     : 6 records  ✅                 │
  │   Analytics  : 3 records  ✅                 │
  └──────────────────────────────────────────────┘

════════════════════════════════════════════════════════════════
```

---

## 🚀 How to Run

> **Note:** This project requires the `kotlinx-coroutines-core` library.

```bash
# Option 1: Using kotlinc with coroutines JAR
kotlinc Solution.kt -cp kotlinx-coroutines-core-1.7.3.jar -include-runtime -d Solution.jar
java -cp Solution.jar:kotlinx-coroutines-core-1.7.3.jar MainKt

# Option 2: Paste into Kotlin Playground (play.kotlinlang.org) — coroutines are pre-included

# Option 3: Run in IntelliJ/Android Studio with coroutines dependency in build.gradle:
#   implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
```
