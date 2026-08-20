/**
 * 🧬 Multi-Source Data Aggregation Engine
 * Demonstrates: Generics, Coroutines (suspend, launch, async, Dispatchers),
 *               and Flow (flow builder, operators, collect).
 */

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.system.measureTimeMillis

// ═══════════════════════════════════════════════════════════════
//  PART 1: GENERICS — Type-Safe Containers & Transformations
// ═══════════════════════════════════════════════════════════════

// ─── Generic Data Packet ────────────────────────────────────

enum class Priority { LOW, MEDIUM, HIGH, CRITICAL }

/**
 * A generic wrapper that can hold ANY type of data.
 * <T> is the type parameter — replaced with a real type at usage site.
 */
data class DataPacket<T>(
    val source: String,
    val timestamp: Long = System.currentTimeMillis(),
    val data: T,
    val priority: Priority = Priority.MEDIUM
) {
    fun describe(): String {
        val typeName = when (data) {
            is String -> "STRING"
            is Int    -> "INT"
            is List<*> -> "LIST"
            is Double -> "DOUBLE"
            else      -> data!!::class.simpleName ?: "UNKNOWN"
        }
        return "[$typeName] source=$source, data=$data"
    }
}

// ─── Result<T> Sealed Class — The Android Pattern ───────────

/**
 * Sealed class with generic type — used EVERYWHERE in Android.
 * Wraps either a successful result, an error, or a loading state.
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error<T>(val message: String, val code: Int = -1) : Result<T>()
    data object Loading : Result<Nothing>()

    /** Safe accessor: returns data or null */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    /** Transform the success data using a lambda */
    fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> Error(message, code)
        is Loading -> Loading
    }

    fun describe(): String = when (this) {
        is Success -> "✅ Success: $data"
        is Error -> "❌ Error: $message (code: $code)"
        is Loading -> "⏳ Loading..."
    }
}

// ─── SortableCache<T> — Bounded Generic (T must be Comparable) ──

/**
 * A cache that only accepts Comparable types, so it can sort them.
 * The constraint <T : Comparable<T>> is an UPPER BOUND.
 */
class SortableCache<T : Comparable<T>>(private val maxSize: Int = 100) {
    private val items = mutableListOf<T>()

    fun add(item: T) {
        if (items.size >= maxSize) items.removeAt(0)
        items.add(item)
    }

    fun addAll(newItems: List<T>) = newItems.forEach { add(it) }

    fun sorted(): List<T> = items.sorted()

    fun topN(n: Int): List<T> = items.sortedDescending().take(n)

    fun contains(item: T): Boolean = items.contains(item)

    fun size(): Int = items.size

    fun clear() = items.clear()
}

// ─── DataTransformer<I, O> — Multiple Type Parameters ───────

/**
 * Generic interface with TWO type parameters: Input and Output.
 * Any class implementing this must define how to transform I → O.
 */
interface DataTransformer<I, O> {
    fun transform(input: I): O
    fun transformBatch(inputs: List<I>): List<O> = inputs.map { transform(it) }
}

/** Concrete transformer: String → its length */
class StringLengthTransformer : DataTransformer<String, Int> {
    override fun transform(input: String): Int = input.length
}

/** Concrete transformer: Number → formatted currency string */
class CurrencyFormatter : DataTransformer<Double, String> {
    override fun transform(input: Double): String = "$${String.format("%.2f", input)}"
}

/** Generic chaining transformer: chains A→B then B→C to produce A→C */
class ChainedTransformer<A, B, C>(
    private val first: DataTransformer<A, B>,
    private val second: DataTransformer<B, C>
) : DataTransformer<A, C> {
    override fun transform(input: A): C = second.transform(first.transform(input))
}

// ─── Generic Utility Functions ──────────────────────────────

/** Generic function to find the best item using a custom comparator lambda */
fun <T> findBest(items: List<T>, isBetter: (T, T) -> Boolean): T? {
    if (items.isEmpty()) return null
    var best = items[0]
    for (i in 1 until items.size) {
        if (isBetter(items[i], best)) best = items[i]
    }
    return best
}

/** Generic extension function: safely get or provide a default */
fun <T> Result<T>.getOrDefault(default: T): T = when (this) {
    is Result.Success -> data
    else -> default
}


// ═══════════════════════════════════════════════════════════════
//  PART 2: COROUTINES — Concurrent Data Fetching
// ═══════════════════════════════════════════════════════════════

// ─── Data Classes for Simulated Sources ─────────────────────

data class User(val id: Int, val name: String, val email: String)
data class Product(val id: Int, val title: String, val price: Double)
data class Order(val id: Int, val userId: Int, val productId: Int, val total: Double)
data class AnalyticsEvent(val metric: String, val value: Double)

// ─── Suspend Functions — Simulated Data Sources ─────────────

suspend fun fetchUsers(): List<User> {
    delay(2000)  // Simulates 2-second network call
    return listOf(
        User(1, "Rahul Sharma", "rahul@dev.io"),
        User(2, "Priya Patel", "priya@dev.io"),
        User(3, "Amit Kumar", "amit@dev.io"),
        User(4, "Sneha Gupta", "sneha@dev.io"),
        User(5, "Vikram Singh", "vikram@dev.io")
    )
}

suspend fun fetchProducts(): List<Product> {
    delay(3000)  // Simulates 3-second network call (slowest source)
    return listOf(
        Product(1, "Kotlin in Action", 2999.0),
        Product(2, "Android Tablet", 18999.0),
        Product(3, "Mechanical Keyboard", 4599.0),
        Product(4, "USB-C Hub", 1299.0)
    )
}

suspend fun fetchOrders(): List<Order> {
    delay(2000)  // Simulates 2-second database query
    return listOf(
        Order(1, 1, 1, 2999.0),
        Order(2, 1, 3, 4599.0),
        Order(3, 2, 2, 18999.0),
        Order(4, 3, 4, 1299.0),
        Order(5, 4, 1, 2999.0),
        Order(6, 5, 3, 4599.0)
    )
}

suspend fun fetchAnalytics(): List<AnalyticsEvent> {
    delay(1000)  // Simulates 1-second analytics fetch
    return listOf(
        AnalyticsEvent("daily_active_users", 1250.0),
        AnalyticsEvent("conversion_rate", 3.7),
        AnalyticsEvent("avg_session_minutes", 12.5)
    )
}


// ═══════════════════════════════════════════════════════════════
//  PART 3: FLOW — Real-Time Streaming
// ═══════════════════════════════════════════════════════════════

// ─── Flow Builders ──────────────────────────────────────────

/** Emits simulated stock price ticks every 500ms */
fun priceTickerFlow(symbol: String, basePrice: Double): Flow<Pair<String, Double>> = flow {
    var currentPrice = basePrice
    var tick = 0
    while (true) {
        val change = (-300..300).random() / 100.0
        currentPrice += change
        emit(symbol to currentPrice)
        tick++
        delay(500)
    }
}

/** Emits simulated sensor temperature readings */
fun sensorDataFlow(): Flow<Pair<String, Double>> = flow {
    val sensors = listOf("sensor-01", "sensor-02", "sensor-03")
    repeat(10) { i ->
        val sensorId = sensors[i % sensors.size]
        val temperature = 20.0 + (0..300).random() / 10.0  // 20.0 - 50.0°C
        emit(sensorId to temperature)
        delay(400)
    }
}

/** Demonstrates List vs Flow timing */
fun slowListApproach(): List<Int> {
    val result = mutableListOf<Int>()
    for (i in 1..5) {
        Thread.sleep(200)  // Simulate computation (blocking!)
        result.add(i)
    }
    return result
}

fun slowFlowApproach(): Flow<Int> = flow {
    for (i in 1..5) {
        delay(200)  // Simulate computation (non-blocking)
        emit(i)
    }
}

// ─── Generic DataSource Interface ───────────────────────────

/**
 * Combines all three concepts:
 * - Generic interface (<T>)
 * - Suspend function (coroutine)
 * - Returns Result<T> (generic sealed class)
 */
interface DataSource<T> {
    val name: String
    suspend fun fetch(): Result<List<T>>
}

class UserSource : DataSource<User> {
    override val name = "Users"
    override suspend fun fetch(): Result<List<User>> {
        return try {
            val data = fetchUsers()
            Result.Success(data)
        } catch (e: Exception) {
            Result.Error("Failed to fetch users: ${e.message}")
        }
    }
}

class ProductSource : DataSource<Product> {
    override val name = "Products"
    override suspend fun fetch(): Result<List<Product>> {
        return try {
            val data = fetchProducts()
            Result.Success(data)
        } catch (e: Exception) {
            Result.Error("Failed to fetch products: ${e.message}")
        }
    }
}

class OrderSource : DataSource<Order> {
    override val name = "Orders"
    override suspend fun fetch(): Result<List<Order>> {
        return try {
            val data = fetchOrders()
            Result.Success(data)
        } catch (e: Exception) {
            Result.Error("Failed to fetch orders: ${e.message}")
        }
    }
}

class AnalyticsSource : DataSource<AnalyticsEvent> {
    override val name = "Analytics"
    override suspend fun fetch(): Result<List<AnalyticsEvent>> {
        return try {
            val data = fetchAnalytics()
            Result.Success(data)
        } catch (e: Exception) {
            Result.Error("Failed to fetch analytics: ${e.message}")
        }
    }
}

/** Flow that reports progress as each source completes */
fun <T> DataSource<T>.fetchWithProgress(): Flow<String> = flow {
    emit("⏳ Fetching ${name}...")
    val result = fetch()
    when (result) {
        is Result.Success -> emit("✅ ${name} complete (${result.data.size} records)")
        is Result.Error -> emit("❌ ${name} failed: ${result.message}")
        is Result.Loading -> emit("⏳ ${name} loading...")
    }
}


// ═══════════════════════════════════════════════════════════════
//  MAIN — Run All Stages
// ═══════════════════════════════════════════════════════════════

fun main() = runBlocking {

    println("════════════════════════════════════════════════════════════════")
    println("   🧬 MULTI-SOURCE DATA AGGREGATION ENGINE                    ")
    println("════════════════════════════════════════════════════════════════")

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    //  STAGE 1: GENERICS
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    println("\n━━━ STAGE 1: Generics — Type-Safe Containers ━━━━━━━━━━━━━━━━\n")

    // --- DataPacket<T> ---
    println("  📦 DataPacket Demo:")
    val packet1 = DataPacket(source = "API", data = "Hello Generics")
    val packet2 = DataPacket(source = "Sensor", data = 42, priority = Priority.HIGH)
    val packet3 = DataPacket(source = "DB", data = listOf("Kotlin", "Coroutines", "Flow"))

    println("    Packet 1: ${packet1.describe()}")
    println("    Packet 2: ${packet2.describe()}")
    println("    Packet 3: ${packet3.describe()}")

    // --- Result<T> ---
    println("\n  🎯 Result<T> Sealed Class:")
    val successResult: Result<User> = Result.Success(User(1, "Rahul", "rahul@dev.io"))
    val errorResult: Result<User> = Result.Error("Network timeout after 5000ms", 408)
    val loadingResult: Result<User> = Result.Loading

    println("    ${successResult.describe()}")
    println("    ${errorResult.describe()}")
    println("    ${loadingResult.describe()}")

    // map() transforms the Success data type-safely
    val userName: Result<String> = successResult.map { it.name }
    println("    Mapped to name: ${userName.describe()}")

    // getOrDefault using our generic extension function
    val fallback = errorResult.getOrDefault(User(0, "Guest", "guest@dev.io"))
    println("    Error getOrDefault: $fallback")

    // --- SortableCache<T : Comparable<T>> ---
    println("\n  📊 SortableCache<Int>:")
    val cache = SortableCache<Int>()
    val nums = listOf(42, 17, 85, 3, 61, 29)
    cache.addAll(nums)
    println("    Added: $nums")
    println("    Sorted: ${cache.sorted()}")
    println("    Top 3: ${cache.topN(3)}")
    println("    Contains 42? ${cache.contains(42)}")

    // Works with Strings too (Comparable)
    val stringCache = SortableCache<String>()
    stringCache.addAll(listOf("Kotlin", "Java", "Dart", "Swift"))
    println("    String cache sorted: ${stringCache.sorted()}")

    // --- DataTransformer<I, O> ---
    println("\n  🔄 DataTransformer<String, Int> (String → Length):")
    val lengthTransformer = StringLengthTransformer()
    listOf("Kotlin", "Coroutines", "Flow").forEach { word ->
        println("    \"$word\" → ${lengthTransformer.transform(word)}")
    }

    println("\n  💲 CurrencyFormatter (Double → String):")
    val currencyFmt = CurrencyFormatter()
    listOf(2999.0, 18999.50, 4599.99).forEach { price ->
        println("    $price → ${currencyFmt.transform(price)}")
    }

    // Chained transformer: String → Int (length) → String (currency)
    // This doesn't make semantic sense but demonstrates generic chaining!
    println("\n  🔗 Chained Transformer (String → length → currency format):")
    val chained = ChainedTransformer(
        StringLengthTransformer(),                          // String → Int
        CurrencyFormatter().let { fmt ->                    // Int → String
            object : DataTransformer<Int, String> {
                override fun transform(input: Int): String = fmt.transform(input.toDouble())
            }
        }
    )
    println("    \"Kotlin\" → ${chained.transform("Kotlin")}")     // "Kotlin" → 6 → "$6.00"

    // --- Generic findBest() function ---
    println("\n  🏆 Generic findBest():")
    val products = listOf(
        Product(1, "Kotlin in Action", 2999.0),
        Product(2, "Android Tablet", 18999.0),
        Product(3, "Keyboard", 4599.0)
    )
    val mostExpensive = findBest(products) { a, b -> a.price > b.price }
    println("    Most expensive product: ${mostExpensive?.title} (${currencyFmt.transform(mostExpensive?.price ?: 0.0)})")

    val cheapest = findBest(products) { a, b -> a.price < b.price }
    println("    Cheapest product: ${cheapest?.title} (${currencyFmt.transform(cheapest?.price ?: 0.0)})")


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    //  STAGE 2: COROUTINES — Sequential vs Parallel
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    println("\n━━━ STAGE 2: Coroutines — Sequential vs Parallel ━━━━━━━━━━━━\n")

    // --- Sequential fetching (SLOW) ---
    println("  🐌 Sequential Fetching:")
    val seqTime = measureTimeMillis {
        print("    ⏳ Fetching users...       ")
        val users = fetchUsers()
        println("(got ${users.size})")

        print("    ⏳ Fetching products...    ")
        val prods = fetchProducts()
        println("(got ${prods.size})")

        print("    ⏳ Fetching orders...      ")
        val orders = fetchOrders()
        println("(got ${orders.size})")

        print("    ⏳ Fetching analytics...   ")
        val analytics = fetchAnalytics()
        println("(got ${analytics.size})")
    }
    println("    ✅ All fetched in ${seqTime}ms")

    // --- Parallel fetching (FAST) ---
    println("\n  🚀 Parallel Fetching (async/await):")
    println("    ⏳ All 4 sources launched simultaneously...")

    lateinit var parUsers: List<User>
    lateinit var parProducts: List<Product>
    lateinit var parOrders: List<Order>
    lateinit var parAnalytics: List<AnalyticsEvent>

    val parTime = measureTimeMillis {
        coroutineScope {
            val usersDeferred = async { fetchUsers() }
            val productsDeferred = async { fetchProducts() }
            val ordersDeferred = async { fetchOrders() }
            val analyticsDeferred = async { fetchAnalytics() }

            parUsers = usersDeferred.await()
            parProducts = productsDeferred.await()
            parOrders = ordersDeferred.await()
            parAnalytics = analyticsDeferred.await()
        }
    }

    println("    ✅ All fetched in ${parTime}ms")
    val improvement = ((seqTime - parTime).toDouble() / seqTime * 100).toInt()
    println("    ⚡ Speed improvement: ${improvement}% faster!")

    println("\n  📊 Results:")
    println("    Users:     ${parUsers.size} records")
    parUsers.forEach { println("      → ${it.name} (${it.email})") }
    println("    Products:  ${parProducts.size} records")
    parProducts.forEach { println("      → ${it.title} — ${currencyFmt.transform(it.price)}") }
    println("    Orders:    ${parOrders.size} records")
    println("    Analytics: ${parAnalytics.size} records")
    parAnalytics.forEach { println("      → ${it.metric}: ${it.value}") }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    //  STAGE 3: FLOW — Real-Time Streams
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    println("\n━━━ STAGE 3: Flow — Real-Time Data Streams ━━━━━━━━━━━━━━━━━━\n")

    // --- Stock price ticker ---
    println("  📈 Stock Price Ticker (5 ticks):")
    var prevPrice = 150.0
    priceTickerFlow("GOOG", 150.0)
        .take(5)
        .collect { (symbol, price) ->
            val arrow = if (price >= prevPrice) "▲" else "▼"
            println("    $symbol: ${String.format("$%.2f", price)}  $arrow")
            prevPrice = price
        }

    // --- Sensor stream with filter + map ---
    println("\n  🌡️ Sensor Stream (filtered > 30°C, mapped to alerts):")
    sensorDataFlow()
        .filter { (_, temp) -> temp > 30.0 }                    // Only high temps
        .map { (sensor, temp) ->                                  // Transform to alert string
            "⚠️ HIGH TEMP: ${String.format("%.1f", temp)}°C at $sensor"
        }
        .onEach { /* could log to analytics here */ }             // Side effect
        .take(4)                                                  // Max 4 alerts
        .collect { alert -> println("    $alert") }

    // --- List vs Flow timing comparison ---
    println("\n  ⏱️ List vs Flow Timing:")

    print("    List approach:  ")
    val listTime = measureTimeMillis {
        val list = slowListApproach()
        print("waited, got all at once → $list")
    }
    println("  (${listTime}ms)")

    print("    Flow approach:  ")
    val flowValues = mutableListOf<Int>()
    val flowTime = measureTimeMillis {
        slowFlowApproach().collect { value ->
            flowValues.add(value)
        }
    }
    println("got each as ready → $flowValues  (${flowTime}ms)")

    // --- flowOf and operator chaining ---
    println("\n  🔗 Flow Operator Chaining:")
    print("    flowOf(1..10) → filter(even) → map(*3) → take(4) → ")
    val operatorResult = flowOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        .filter { it % 2 == 0 }       // Keep even: 2, 4, 6, 8, 10
        .map { it * 3 }               // Triple: 6, 12, 18, 24, 30
        .take(4)                       // First 4: 6, 12, 18, 24
        .toList()
    println(operatorResult)


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    //  STAGE 4: COMBINED PIPELINE — All Three Together
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    println("\n━━━ STAGE 4: Combined Pipeline — All Three Together ━━━━━━━━━\n")

    println("  🔄 Aggregation Pipeline Starting...")

    // Create generic data sources
    val sources: List<DataSource<*>> = listOf(
        UserSource(),
        ProductSource(),
        OrderSource(),
        AnalyticsSource()
    )

    // Fetch all sources in parallel, collecting progress via Flow
    val sourceResults = mutableMapOf<String, Result<List<*>>>()
    val pipelineTime = measureTimeMillis {
        // Create a flow that merges progress from all sources
        coroutineScope {
            val jobs = sources.map { source ->
                async {
                    val startMsg = "    ⏳ Fetching ${source.name}..."
                    println(startMsg)
                    @Suppress("UNCHECKED_CAST")
                    val result = (source as DataSource<Any>).fetch()
                    sourceResults[source.name] = result
                    when (result) {
                        is Result.Success -> println("    ✅ ${source.name} complete (${result.data.size} records)")
                        is Result.Error -> println("    ❌ ${source.name} failed: ${result.message}")
                        is Result.Loading -> println("    ⏳ ${source.name} loading...")
                    }
                }
            }
            jobs.forEach { it.await() }
        }
    }

    // Generate summary report
    val totalRecords = sourceResults.values.sumOf { result ->
        when (result) {
            is Result.Success -> result.data.size
            else -> 0
        }
    }
    val successCount = sourceResults.values.count { it is Result.Success }
    val failedCount = sourceResults.values.count { it is Result.Error }

    println()
    println("  ┌──────────────────────────────────────────────┐")
    println("  │       📊 AGGREGATION SUMMARY REPORT          │")
    println("  ├──────────────────────────────────────────────┤")
    println("  │ Total Sources       : ${sources.size}                      │")
    println("  │ Total Records       : %-25s│".format(totalRecords))
    println("  │ Successful Sources  : %-25s│".format(successCount))
    println("  │ Failed Sources      : %-25s│".format(failedCount))
    println("  │ Total Fetch Time    : %-25s│".format("~${pipelineTime}ms (parallel)"))
    println("  │ Records by Source:                            │")
    sourceResults.forEach { (name, result) ->
        when (result) {
            is Result.Success -> {
                val status = "✅"
                println("  │   %-12s: %-5d records  %-13s│".format(name, result.data.size, status))
            }
            is Result.Error -> {
                println("  │   %-12s: FAILED        ❌              │".format(name))
            }
            else -> {}
        }
    }
    println("  └──────────────────────────────────────────────┘")

    // --- Bonus: Use Flow to stream a final summary of top spending users ---
    println("\n  📈 Bonus — Top Spenders (Flow + Generics):")

    val orderData = sourceResults["Orders"]
    if (orderData is Result.Success) {
        @Suppress("UNCHECKED_CAST")
        val orders = orderData.data as List<Order>
        val userData = sourceResults["Users"]

        // Create a flow from order data, aggregate by user
        flowOf(*orders.toTypedArray())
            .map { order -> order.userId to order.total }    // Extract userId and total
            .toList()                                        // Collect pairs
            .groupBy({ it.first }, { it.second })           // Group by userId
            .mapValues { (_, totals) -> totals.sum() }      // Sum per user
            .entries
            .sortedByDescending { it.value }                // Rank by spending
            .forEachIndexed { index, (userId, total) ->
                val userName = if (userData is Result.Success) {
                    @Suppress("UNCHECKED_CAST")
                    (userData.data as List<User>).find { it.id == userId }?.name ?: "User #$userId"
                } else "User #$userId"
                println("    ${index + 1}. $userName → ${currencyFmt.transform(total)}")
            }
    }

    println("\n════════════════════════════════════════════════════════════════")
    println("  ✅ All stages complete! Generics + Coroutines + Flow = 🚀   ")
    println("════════════════════════════════════════════════════════════════")
}
