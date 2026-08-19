/**
 * 🔧 Configurable Data Pipeline & Event Processing Engine
 * Demonstrates: Lambdas, Higher-Order Functions, and all 5 Scope Functions (let, apply, run, also, with).
 */

// ─── Data Models ────────────────────────────────────────────

enum class EventType { CLICK, SCROLL, PURCHASE, ERROR, NAVIGATION }

data class AppEvent(
    val id: String,
    val type: EventType,
    val userId: String,
    val timestamp: Long,
    val metadata: Map<String, String>?,
    val amount: Double?
)

class PipelineConfig(
    var name: String = "Unnamed Pipeline",
    var batchSize: Int = 100,
    var isDebugMode: Boolean = false,
    var outputFormat: String = "JSON",
    var errorThreshold: Double = 0.10
)

data class ProcessingResult(
    val totalEvents: Int,
    val processedEvents: Int,
    val filteredOut: Int,
    val totalRevenue: Double,
    val errorCount: Int,
    val executionTimeMs: Long
)

// ─── HOF: Factory Functions That Return Lambdas ─────────────

/**
 * Factory HOF: Creates a reusable event filter lambda.
 * Returns a (AppEvent) -> Boolean predicate.
 */
fun createEventFilter(
    type: EventType? = null,
    minAmount: Double? = null,
    userId: String? = null
): (AppEvent) -> Boolean {
    return { event ->
        val matchesType = type == null || event.type == type
        val matchesAmount = minAmount == null || (event.amount != null && event.amount >= minAmount)
        val matchesUser = userId == null || event.userId == userId
        matchesType && matchesAmount && matchesUser
    }
}

/**
 * Factory HOF: Creates a sorter for ranking users.
 * Returns a (Map.Entry) -> Comparable lambda for sorting.
 */
fun createUserRanker(
    by: String = "activity" // "activity" or "revenue"
): (Map.Entry<String, List<AppEvent>>) -> Double {
    return when (by) {
        "revenue" -> { entry -> -(entry.value.sumOf { it.amount ?: 0.0 }) }
        else      -> { entry -> -entry.value.size.toDouble() }
    }
}

// ─── HOF: Execution Wrappers ────────────────────────────────

/**
 * HOF: Measures execution time and logs result.
 * Takes a label and an action lambda, returns the action's result.
 */
fun <T> measureAndLog(label: String, action: () -> T): Pair<T, Long> {
    val start = System.currentTimeMillis()
    val result = action()
    val elapsed = System.currentTimeMillis() - start
    println("  ⏱️ [$label] completed in ${elapsed}ms")
    return Pair(result, elapsed)
}

/**
 * HOF: Retry wrapper — retries an action up to maxAttempts times.
 */
fun retry(maxAttempts: Int, action: (attempt: Int) -> Boolean): Boolean {
    for (i in 1..maxAttempts) {
        println("  🔄 Attempt $i of $maxAttempts...")
        if (action(i)) return true
    }
    return false
}

// ─── Extension HOF: Custom Pipeline Processor ───────────────

/**
 * Extension HOF on List<AppEvent>: Chains multiple filter stages.
 */
fun List<AppEvent>.pipeline(
    vararg filters: (AppEvent) -> Boolean,
    debugLog: Boolean = false
): List<AppEvent> {
    var current = this
    filters.forEachIndexed { index, filter ->
        current = current.filter(filter)
        if (debugLog) {
            println("    [Stage ${index + 1}] ${current.size} events remaining")
        }
    }
    return current
}

// ─── Analytics Engine (Uses All 5 Scope Functions) ──────────

class AnalyticsEngine {

    // APPLY: Configure pipeline
    fun configurePipeline(block: PipelineConfig.() -> Unit): PipelineConfig {
        return PipelineConfig().apply(block)
            .also { config ->
                // ALSO: Validate configuration (side effect)
                require(config.name.isNotBlank()) { "Pipeline name cannot be blank" }
                require(config.batchSize > 0) { "Batch size must be positive" }
                println("  ✅ Pipeline \"${config.name}\" configured")
                println("     Batch Size: ${config.batchSize} | Debug: ${config.isDebugMode} | Format: ${config.outputFormat}")
            }
    }

    // LET + ALSO: Ingest and log events
    fun ingestEvents(source: () -> List<AppEvent>?): List<AppEvent> {
        return source()
            ?.let { events ->
                // LET: transform nullable list into validated non-null list
                events.filter { it.id.isNotBlank() }
            }
            ?.also { events ->
                // ALSO: log ingestion stats (side effect, returns the list)
                println("  📥 Loaded ${events.size} events")
                val typeCounts = events.groupBy { it.type }.mapValues { it.value.size }
                println("  📊 Event Types: $typeCounts")
            }
            ?: emptyList<AppEvent>().also {
                println("  ⚠️ No events loaded (source returned null)")
            }
    }

    // RUN: Process filtered events and compute result
    fun processFiltered(
        events: List<AppEvent>,
        filter: (AppEvent) -> Boolean,
        config: PipelineConfig
    ): ProcessingResult {
        return events.run {
            // RUN: compute in the list's context, return ProcessingResult
            val filtered = this.filter(filter)
            val revenue = filtered.sumOf { it.amount ?: 0.0 }
            val errors = filtered.count { it.type == EventType.ERROR }

            ProcessingResult(
                totalEvents = this.size,
                processedEvents = filtered.size,
                filteredOut = this.size - filtered.size,
                totalRevenue = revenue,
                errorCount = errors,
                executionTimeMs = 0 // placeholder, set by measureAndLog
            )
        }.also { result ->
            // ALSO: log processing result
            println("  ✅ Processed ${result.processedEvents} of ${result.totalEvents} events (${result.filteredOut} filtered out)")
            println("  💰 Total Revenue: $${String.format("%,.2f", result.totalRevenue)}")
        }
    }

    // HOF + LET: Top N users by configurable criteria
    fun getTopUsers(
        events: List<AppEvent>,
        limit: Int = 3,
        ranker: (Map.Entry<String, List<AppEvent>>) -> Double
    ): List<Pair<String, Pair<Int, Double>>> {
        return events
            .groupBy { it.userId }
            .entries
            .sortedBy { ranker(it) }
            .take(limit)
            .map { (userId, userEvents) ->
                val revenue = userEvents.sumOf { it.amount ?: 0.0 }
                userId to Pair(userEvents.size, revenue)
            }
            .also { topUsers ->
                // ALSO: print the leaderboard
                println("  🏆 Top $limit Most Active Users:")
                topUsers.forEachIndexed { index, (userId, stats) ->
                    println("    ${index + 1}. User #$userId → ${stats.first} events ($${String.format("%,.2f", stats.second)} revenue)")
                }
            }
    }

    // LET + ALSO: Error analysis with threshold alerting
    fun analyzeErrors(events: List<AppEvent>, threshold: Double = 0.10) {
        val errorEvents = events.filter { it.type == EventType.ERROR }
        val errorRate = if (events.isNotEmpty()) errorEvents.size.toDouble() / events.size else 0.0

        println("  ⚠️ Error Rate: ${"%.1f".format(errorRate * 100)}% (${errorEvents.size} of ${events.size} events)")

        if (errorRate > threshold) {
            println("  🔴 ALERT: Error rate exceeds ${"%.0f".format(threshold * 100)}% threshold!")
        } else {
            println("  🟢 Error rate within acceptable limits.")
        }

        // LET: only print details if there are errors
        errorEvents.takeIf { it.isNotEmpty() }?.let { errors ->
            println("  Error Details:")
            errors.forEach { event ->
                val message = event.metadata?.let { meta ->
                    // LET: null-safe metadata extraction
                    meta["error_message"] ?: "Unknown error"
                } ?: "No error details"
                println("    - Event #${event.id}: \"$message\" (User: ${event.userId})")
            }
        }
    }

    // WITH: Generate formatted summary report
    fun generateReport(
        events: List<AppEvent>,
        result: ProcessingResult,
        executionMs: Long
    ): String {
        return with(StringBuilder()) {
            // WITH: build complex string using StringBuilder's context
            appendLine("┌──────────────────────────────────────────────┐")
            appendLine("│         📊 ANALYTICS SUMMARY REPORT          │")
            appendLine("├──────────────────────────────────────────────┤")
            appendLine("│ Total Events Processed : ${result.totalEvents.toString().padEnd(21)}│")
            appendLine("│ Unique Users           : ${events.map { it.userId }.distinct().size.toString().padEnd(21)}│")
            appendLine("│ Total Revenue          : $${String.format("%,.2f", result.totalRevenue).padEnd(20)}│")

            val avgOrder = if (result.processedEvents > 0) result.totalRevenue / events.count { it.type == EventType.PURCHASE } else 0.0
            appendLine("│ Average Order Value    : $${String.format("%,.2f", avgOrder).padEnd(20)}│")

            val errorRate = if (result.totalEvents > 0) result.errorCount.toDouble() / result.totalEvents * 100 else 0.0
            appendLine("│ Error Rate             : ${"%.1f".format(errorRate)}%${" ".repeat(18 - "%.1f".format(errorRate).length)}│")
            appendLine("│ Pipeline Execution     : ${executionMs}ms${" ".repeat(19 - "${executionMs}ms".length)}│")
            appendLine("├──────────────────────────────────────────────┤")
            appendLine("│ Revenue by Category:                         │")

            events.groupBy { it.type }
                .mapValues { (_, evts) -> Pair(evts.sumOf { it.amount ?: 0.0 }, evts.size) }
                .entries
                .sortedByDescending { it.value.first }
                .forEach { (type, stats) ->
                    val label = type.name.padEnd(12)
                    val rev = "$${String.format("%,.2f", stats.first)}"
                    val count = if (stats.second == 1) "1 event" else "${stats.second} events"
                    appendLine("│   $label: ${rev.padEnd(10)} ($count)${" ".repeat(maxOf(0, 8 - count.length))}│")
                }

            appendLine("└──────────────────────────────────────────────┘")
            toString() // WITH returns this String
        }
    }
}

// ─── Main: Wiring Everything Together ───────────────────────

fun main() {
    println("============================================================")
    println("    🔧 CONFIGURABLE DATA PIPELINE & EVENT PROCESSING ENGINE   ")
    println("============================================================")

    val engine = AnalyticsEngine()

    // ── STAGE 1: Pipeline Configuration (APPLY + ALSO) ──────
    println("\n[STAGE 1: Pipeline Configuration]")
    val config = engine.configurePipeline {
        name = "Production Analytics"
        batchSize = 50
        isDebugMode = false
        outputFormat = "JSON"
        errorThreshold = 0.10
    }

    // ── STAGE 2: Event Ingestion (LET + ALSO) ───────────────
    println("\n[STAGE 2: Event Ingestion]")
    val now = System.currentTimeMillis()
    val events = engine.ingestEvents {
        listOf(
            AppEvent("EVT-001", EventType.CLICK,      "USR-101", now - 50000, mapOf("page" to "home"),              null),
            AppEvent("EVT-002", EventType.PURCHASE,    "USR-101", now - 45000, mapOf("item" to "iPhone 15"),         999.00),
            AppEvent("EVT-003", EventType.NAVIGATION,  "USR-102", now - 40000, mapOf("from" to "home", "to" to "cart"), null),
            AppEvent("EVT-004", EventType.SCROLL,      "USR-101", now - 35000, mapOf("page" to "product"),          null),
            AppEvent("EVT-005", EventType.PURCHASE,    "USR-103", now - 30000, mapOf("item" to "AirPods Pro"),       249.00),
            AppEvent("EVT-006", EventType.CLICK,       "USR-101", now - 25000, mapOf("button" to "add_to_cart"),    null),
            AppEvent("EVT-007", EventType.PURCHASE,    "USR-101", now - 20000, mapOf("item" to "MacBook Air"),      1500.00),
            AppEvent("EVT-008", EventType.ERROR,       "USR-102", now - 15000, mapOf("error_message" to "NullPointerException in CartFragment"), null),
            AppEvent("EVT-009", EventType.NAVIGATION,  "USR-103", now - 10000, mapOf("from" to "cart", "to" to "checkout"), null),
            AppEvent("EVT-010", EventType.PURCHASE,    "USR-103", now - 8000,  mapOf("item" to "Galaxy S24"),       1350.00),
            AppEvent("EVT-011", EventType.ERROR,       "USR-103", now - 5000,  mapOf("error_message" to "NetworkTimeoutException"), null),
            AppEvent("EVT-012", EventType.CLICK,       "USR-102", now - 2000,  mapOf("button" to "retry"),          null)
        )
    }

    // ── STAGE 3: Filtered Pipeline — Purchases (HOF Factory + RUN + ALSO) ──
    println("\n[STAGE 3: Filtered Pipeline — Purchase Events Only]")
    val purchaseFilter = createEventFilter(type = EventType.PURCHASE, minAmount = 0.0)
    println("  🔍 Filter: PURCHASE events with amount > \$0.00")
    println("  ⏳ Processing...")
    val (result, elapsed) = measureAndLog("Purchase Pipeline") {
        engine.processFiltered(events, purchaseFilter, config)
    }

    // ── STAGE 4: Top Users (HOF + Extension Pipeline) ───────
    println("\n[STAGE 4: Custom HOF — Top Users by Activity]")
    val activityRanker = createUserRanker("activity")
    engine.getTopUsers(events, limit = 3, ranker = activityRanker)

    // ── STAGE 5: Error Analysis (LET for null-safe metadata) ─
    println("\n[STAGE 5: Error Rate Analysis]")
    engine.analyzeErrors(events, threshold = config.errorThreshold)

    // ── STAGE 6: Full Report (WITH + StringBuilder) ─────────
    println("\n[STAGE 6: Full Analytics Report]")
    val report = engine.generateReport(events, result, elapsed)
    println(report)

    // ── BONUS: Extension HOF Pipeline with Debug Logging ────
    println("[BONUS: Multi-Stage Pipeline with Debug Logging]")
    val debugFiltered = events.pipeline(
        { it.type != EventType.ERROR },       // Stage 1: exclude errors
        { it.userId == "USR-101" },            // Stage 2: only USR-101
        { it.amount == null || it.amount > 0 },// Stage 3: non-zero or null amount
        debugLog = true
    )
    println("  Final pipeline output: ${debugFiltered.size} events for USR-101 (no errors)")
    debugFiltered.forEach { event ->
        event.metadata?.let { meta ->
            val detail = meta.entries.joinToString(", ") { "${it.key}=${it.value}" }
            println("    - ${event.id} [${event.type}] $detail")
        } ?: println("    - ${event.id} [${event.type}] (no metadata)")
    }

    println("\n============================================================")
}
