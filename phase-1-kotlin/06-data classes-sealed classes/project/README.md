# 🗃️ Project 6: MVI / Redux-Style Android UI State Machine

## 🎯 Overview
In modern Android (Jetpack Compose and unidirectional data flow / MVI), state is modeled using immutable `data class` models, and UI states / user actions are modeled with `sealed class` and `sealed interface` hierarchies. In this project, you will build a complete **Terminal-Simulated Android Movie & Shopping Screen State Machine**.

---

## 🛠️ Concepts Practiced
- `data class` with auto-generated `equals()`, `hashCode()`, `toString()`, and `copy()`
- Immutability and state reduction (`state.copy(...)`)
- Destructuring declarations (`val (id, title, price) = product`)
- `sealed class` and `sealed interface` for closed type hierarchies
- Exhaustive `when` expressions without fallback `else` branches
- Unidirectional Data Flow (User Event -> ViewModel -> New Immutable State -> UI Render)

---

## 📋 Requirements & Features

### 1. Data Model
- `Product`: `id`, `title`, `price`, `rating`, `category`, `inStock`

### 2. User Intent / Events (Sealed Interface)
- `ShopEvent`:
  - `LoadCatalog`: triggers initial fetch
  - `Search(query: String)`: filters by keyword
  - `FilterCategory(category: String?)`: filters by category
  - `SortByPrice(ascending: Boolean)`: sorts existing list using `copy()`
  - `Retry`: re-attempts loading after error
  - `ClearFilters`: resets all filters

### 3. Screen UI State Hierarchy (Sealed Class)
- `ShopUiState`:
  - `Idle`: initial screen before action
  - `Loading`: shows loading spinner and shimmer
  - `Success`: contains `products: List<Product>`, `activeFilter: String?`, `searchQuery: String`, `totalCount: Int`
  - `Empty`: shows "No products found for query" with clear filters button
  - `Error`: contains `message: String`, `isNetworkError: Boolean`, `canRetry: Boolean`

### 4. Terminal UI Renderer
- Fully exhaustive renderer that inspects `ShopUiState` and displays appropriate UI mockups without `else` blocks.

---

## 💻 Sample Output

```text
============================================================
      🗃️ MVI-STYLE ANDROID UI STATE MACHINE SIMULATOR        
============================================================
[ACTION: User opens screen -> LoadCatalog]
┌─────────────────────────────────────────────┐
│  UI STATE: LOADING                          │
└─────────────────────────────────────────────┘
  ⏳ [Progress Indicator] Fetching catalog from server...

┌─────────────────────────────────────────────┐
│  UI STATE: SUCCESS                          │
└─────────────────────────────────────────────┘
  📦 Showing 6 products (Filter: All, Query: "")
  - [101] Pixel 8 Pro          - $999.00 (⭐4.8) [Phones]
  - [102] Galaxy S24 Ultra     - $1,199.00 (⭐4.7) [Phones]
  - [103] MacBook Air M3       - $1,099.00 (⭐4.9) [Laptops]
  - [104] Sony WH-1000XM5      - $399.00 (⭐4.8) [Audio]
  ...

[ACTION: User types "sony" -> Search]
┌─────────────────────────────────────────────┐
│  UI STATE: SUCCESS                          │
└─────────────────────────────────────────────┘
  📦 Showing 1 product (Filter: All, Query: "sony")
  - [104] Sony WH-1000XM5      - $399.00 (⭐4.8) [Audio]

[ACTION: User searches "microwave" -> Search (No Results)]
┌─────────────────────────────────────────────┐
│  UI STATE: EMPTY                            │
└─────────────────────────────────────────────┘
  📭 No products found matching "microwave".
  [Button: Clear Search & Filters]

[ACTION: Network Failure Simulation -> Error]
┌─────────────────────────────────────────────┐
│  UI STATE: ERROR                            │
└─────────────────────────────────────────────┘
  ❌ Failed to reach backend: 503 Service Unavailable
  [Button: 🔄 Retry Connection]
============================================================
```

---

## 🚀 How to Run
```bash
kotlinc Solution.kt -include-runtime -d Solution.jar
java -jar Solution.jar
```
