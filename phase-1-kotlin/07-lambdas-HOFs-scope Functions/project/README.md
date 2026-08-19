# 🔧 Project 7: Configurable Data Pipeline & Event Processing Engine

## 🎯 Overview
Lambdas, Higher-Order Functions, and Scope Functions are the backbone of Kotlin's expressive power — from collection pipelines to Android ViewModel configuration to Retrofit/Room setup. In this project, you will build a complete **Configurable Data Pipeline & Event Processing Engine** that processes, filters, transforms, and reports on real-time application events using every major concept from this module.

---

## 🛠️ Concepts Practiced
- **Lambdas:** Anonymous functions, `it` shorthand, trailing lambda syntax, multi-line lambdas, lambda type notation
- **Higher-Order Functions:** Functions that accept lambdas as parameters, functions that return lambdas (factory pattern), function composition, custom collection extensions
- **Scope Functions:** Idiomatic usage of all 5 scope functions:
  - `let` → null-safe transformations and scoped variables
  - `apply` → object configuration and builder patterns
  - `run` → computing results in an object's context
  - `also` → side effects, logging, and validation in chains
  - `with` → multiple operations on a single object

---

## 📋 Requirements & Features

### 1. Data Models
- `AppEvent`: `id`, `type` (enum: `CLICK`, `SCROLL`, `PURCHASE`, `ERROR`, `NAVIGATION`), `userId`, `timestamp`, `metadata: Map<String, String>?`, `amount: Double?`
- `PipelineConfig`: `name`, `filters: List`, `batchSize`, `isDebugMode`, `outputFormat`
- `ProcessingResult`: `totalEvents`, `processedEvents`, `filteredOut`, `totalRevenue`, `errorCount`, `executionTimeMs`

### 2. Pipeline Builder (Scope Functions)
- Use `apply` to configure `PipelineConfig` with a clean builder DSL
- Use `also` for logging and validation at each pipeline stage
- Use `run` to compute final aggregated results
- Use `with` to generate formatted summary reports
- Use `let` for null-safe metadata extraction

### 3. Event Processing HOFs
- `createEventFilter(type, minAmount, userId)` → returns a `(AppEvent) -> Boolean` lambda (factory pattern)
- `createAggregator(groupBy, reducer)` → returns a function that groups and reduces events
- `List<AppEvent>.process(vararg stages)` → custom extension HOF that chains multiple transformation stages
- `measureAndLog(label, action)` → HOF that wraps any operation with timing and logging

### 4. Analytics Dashboard
- Revenue per event type using `groupBy` + `mapValues` + `sumOf`
- Top N users by activity using HOF with configurable sorting lambda
- Error rate calculation with threshold alerting
- Filtered event stream with debug logging via `also`

---

## 💻 Sample Output

```text
============================================================
    🔧 CONFIGURABLE DATA PIPELINE & EVENT PROCESSING ENGINE   
============================================================

[STAGE 1: Pipeline Configuration]
  ✅ Pipeline "Production Analytics" configured
     Batch Size: 50 | Debug: false | Format: JSON

[STAGE 2: Event Ingestion]
  📥 Loaded 12 events
  📊 Event Types: {PURCHASE=4, CLICK=3, NAVIGATION=2, ERROR=2, SCROLL=1}

[STAGE 3: Filtered Pipeline — Purchase Events Only]
  🔍 Filter: PURCHASE events with amount > $0.00
  ⏳ Processing...
  ✅ Processed 4 of 12 events (8 filtered out)
  💰 Total Revenue: $4,847.00

[STAGE 4: Custom HOF — Top Users by Activity]
  🏆 Top 3 Most Active Users:
    1. User #USR-101 → 5 events ($2,499.00 revenue)
    2. User #USR-103 → 4 events ($1,599.00 revenue)
    3. User #USR-102 → 3 events ($749.00 revenue)

[STAGE 5: Error Rate Analysis]
  ⚠️ Error Rate: 16.7% (2 of 12 events)
  🔴 ALERT: Error rate exceeds 10% threshold!
  Error Details:
    - Event #EVT-008: "NullPointerException in CartFragment" (User: USR-102)
    - Event #EVT-011: "NetworkTimeoutException" (User: USR-103)

[STAGE 6: Full Analytics Report]
┌──────────────────────────────────────────────┐
│         📊 ANALYTICS SUMMARY REPORT          │
├──────────────────────────────────────────────┤
│ Total Events Processed : 12                  │
│ Unique Users           : 3                   │
│ Total Revenue          : $4,847.00           │
│ Average Order Value    : $1,211.75           │
│ Error Rate             : 16.7%               │
│ Pipeline Execution     : 47ms                │
├──────────────────────────────────────────────┤
│ Revenue by Category:                         │
│   PURCHASE   : $4,847.00 (4 events)         │
│   CLICK      : $0.00 (3 events)             │
│   NAVIGATION : $0.00 (2 events)             │
│   ERROR      : $0.00 (2 events)             │
│   SCROLL     : $0.00 (1 event)              │
└──────────────────────────────────────────────┘
============================================================
```

---

## 🚀 How to Run
```bash
kotlinc Solution.kt -include-runtime -d Solution.jar
java -jar Solution.jar
```
