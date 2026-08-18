/**
 * 🗃️ MVI-Style Android UI State Machine Simulator
 * Demonstrates: data classes, copy(), destructuring, sealed classes, sealed interfaces, exhaustive when expressions.
 */

// Data Class for Product Model
data class Product(
    val id: Int,
    val title: String,
    val price: Double,
    val rating: Double,
    val category: String,
    val inStock: Boolean
)

// Sealed Interface for User Actions / Intents (MVI)
sealed interface ShopEvent {
    object LoadCatalog : ShopEvent
    data class Search(val query: String) : ShopEvent
    data class FilterCategory(val category: String?) : ShopEvent
    data class SortByPrice(val ascending: Boolean) : ShopEvent
    object Retry : ShopEvent
    object ClearFilters : ShopEvent
}

// Sealed Class for Screen UI State
sealed class ShopUiState {
    object Idle : ShopUiState()
    object Loading : ShopUiState()

    data class Success(
        val products: List<Product>,
        val activeFilter: String? = null,
        val searchQuery: String = "",
        val isAscendingPrice: Boolean? = null
    ) : ShopUiState() {
        val count get() = products.size
    }

    data class Empty(
        val query: String,
        val filter: String?
    ) : ShopUiState()

    data class Error(
        val message: String,
        val isNetworkError: Boolean,
        val canRetry: Boolean = true
    ) : ShopUiState()
}

// Simulated Repository
object ShopRepository {
    private val inventory = listOf(
        Product(101, "Pixel 8 Pro", 999.00, 4.8, "Phones", true),
        Product(102, "Galaxy S24 Ultra", 1199.00, 4.7, "Phones", true),
        Product(103, "MacBook Air M3", 1099.00, 4.9, "Laptops", true),
        Product(104, "Sony WH-1000XM5", 399.00, 4.8, "Audio", true),
        Product(105, "AirPods Pro 2", 249.00, 4.6, "Audio", true),
        Product(106, "Dell XPS 15", 1499.00, 4.5, "Laptops", false)
    )

    fun fetchCatalog(): List<Product> = inventory
}

// ViewModel Managing Immutable State Transitions
class ShopViewModel {

    private var _state: ShopUiState = ShopUiState.Idle
    val state: ShopUiState get() = _state

    private var currentFilter: String? = null
    private var currentQuery: String = ""
    private var currentSortAsc: Boolean? = null

    fun onEvent(event: ShopEvent) {
        when (event) {
            ShopEvent.LoadCatalog -> loadData()
            is ShopEvent.Search -> {
                currentQuery = event.query
                applyFilterAndSearch()
            }
            is ShopEvent.FilterCategory -> {
                currentFilter = event.category
                applyFilterAndSearch()
            }
            is ShopEvent.SortByPrice -> {
                currentSortAsc = event.ascending
                applySort()
            }
            ShopEvent.Retry -> loadData()
            ShopEvent.ClearFilters -> {
                currentFilter = null
                currentQuery = ""
                currentSortAsc = null
                loadData()
            }
        }
    }

    private fun loadData() {
        _state = ShopUiState.Loading
        renderUI(_state)

        val rawList = ShopRepository.fetchCatalog()
        _state = ShopUiState.Success(
            products = rawList,
            activeFilter = currentFilter,
            searchQuery = currentQuery,
            isAscendingPrice = currentSortAsc
        )
        renderUI(_state)
    }

    private fun applyFilterAndSearch() {
        val allProducts = ShopRepository.fetchCatalog()

        val filtered = allProducts.filter { product ->
            val matchesCategory = currentFilter == null || product.category.equals(currentFilter, ignoreCase = true)
            val matchesQuery = currentQuery.isBlank() || product.title.contains(currentQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }

        _state = if (filtered.isEmpty()) {
            ShopUiState.Empty(query = currentQuery, filter = currentFilter)
        } else {
            val currentState = _state
            if (currentState is ShopUiState.Success) {
                // Immutable update using data class copy()
                currentState.copy(
                    products = filtered,
                    activeFilter = currentFilter,
                    searchQuery = currentQuery
                )
            } else {
                ShopUiState.Success(filtered, currentFilter, currentQuery, currentSortAsc)
            }
        }
        renderUI(_state)
    }

    private fun applySort() {
        val currentState = _state
        if (currentState is ShopUiState.Success && currentSortAsc != null) {
            val sortedList = if (currentSortAsc == true) {
                currentState.products.sortedBy { it.price }
            } else {
                currentState.products.sortedByDescending { it.price }
            }
            // Immutable State Reduction using copy()
            _state = currentState.copy(
                products = sortedList,
                isAscendingPrice = currentSortAsc
            )
            renderUI(_state)
        }
    }

    fun simulateNetworkError(msg: String) {
        _state = ShopUiState.Error(message = msg, isNetworkError = true, canRetry = true)
        renderUI(_state)
    }
}

// Exhaustive UI Renderer
fun renderUI(state: ShopUiState) {
    println("┌─────────────────────────────────────────────┐")
    println("│  UI STATE: ${state::class.simpleName?.uppercase()?.padEnd(32)}│")
    println("└─────────────────────────────────────────────┘")

    when (state) {
        ShopUiState.Idle -> {
            println("  💤 Welcome to QuickShop! Ready to explore.")
        }
        ShopUiState.Loading -> {
            println("  ⏳ [Progress Indicator] Fetching catalog from server...")
        }
        is ShopUiState.Success -> {
            val filterText = state.activeFilter ?: "All"
            val sortText = state.isAscendingPrice?.let { if (it) " [Sort: Price Low->High]" else " [Sort: Price High->Low]" } ?: ""
            println("  📦 Showing ${state.count} product(s) (Filter: $filterText, Query: \"${state.searchQuery}\")$sortText")

            state.products.forEach { (id, title, price, rating, category, inStock) ->
                // Using Destructuring Declaration on Product Data Class
                val stockBadge = if (inStock) "In Stock" else "Out of Stock"
                println("  - [$id] ${title.padEnd(20)} - $${String.format("%.2f", price).padEnd(8)} (⭐$rating) [$category] ($stockBadge)")
            }
        }
        is ShopUiState.Empty -> {
            println("  📭 No products found matching query \"${state.query}\" in category \"${state.filter ?: "All"}\".")
            println("  👉 [Button: Clear Search & Filters]")
        }
        is ShopUiState.Error -> {
            println("  ❌ Error: ${state.message}")
            if (state.isNetworkError) println("  📡 Please check your internet connection.")
            if (state.canRetry) println("  👉 [Button: 🔄 Retry Connection]")
        }
    }
    println()
}

fun main() {
    println("============================================================")
    println("      🗃️ MVI-STYLE ANDROID UI STATE MACHINE SIMULATOR        ")
    println("============================================================")

    val viewModel = ShopViewModel()

    // 1. Initial Load
    println("[ACTION 1: User opens screen -> LoadCatalog]")
    viewModel.onEvent(ShopEvent.LoadCatalog)

    // 2. Search for "sony"
    println("[ACTION 2: User types \"sony\" -> Search]")
    viewModel.onEvent(ShopEvent.Search("sony"))

    // 3. Filter category "Phones"
    println("[ACTION 3: User filters by Category \"Phones\"]")
    viewModel.onEvent(ShopEvent.ClearFilters)
    viewModel.onEvent(ShopEvent.FilterCategory("Phones"))

    // 4. Sort Phones by Price Descending
    println("[ACTION 4: User sorts by Price High -> Low]")
    viewModel.onEvent(ShopEvent.SortByPrice(ascending = false))

    // 5. Search for non-existent product
    println("[ACTION 5: User searches \"microwave\" -> Search (No Results)]")
    viewModel.onEvent(ShopEvent.Search("microwave"))

    // 6. Network error simulation
    println("[ACTION 6: Network Failure Simulation -> Error]")
    viewModel.simulateNetworkError("503 Service Unavailable")

    // 7. Retry
    println("[ACTION 7: User presses Retry -> LoadCatalog]")
    viewModel.onEvent(ShopEvent.Retry)

    println("============================================================")
}
