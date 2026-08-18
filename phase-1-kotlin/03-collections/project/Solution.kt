/**
 * 📦 E-Commerce Product Catalog & Analytics Engine
 * Demonstrates: List, Set, Map, map, filter, groupBy, sumOf, fold, distinct, associateBy.
 */

// Core Data Structures
class Product(
    val id: Int,
    val name: String,
    val category: String,
    val price: Double,
    val rating: Double,
    val inStockQuantity: Int,
    val tags: Set<String>
)

class OrderItem(
    val productId: Int,
    val quantity: Int,
    val unitPrice: Double
)

class Order(
    val orderId: String,
    val customerId: String,
    val items: List<OrderItem>,
    val status: String // "COMPLETED", "CANCELLED", "PENDING"
)

// Analytics Engine
class CatalogAnalytics(
    private val products: List<Product>,
    private val orders: List<Order>
) {
    private val productMap: Map<Int, Product> = products.associateBy { it.id }

    // 1. Calculate inventory value grouped by category
    fun getInventorySummaryByCategory(): Map<String, Pair<Double, Int>> {
        return products.groupBy { it.category }
            .mapValues { (_, categoryProducts) ->
                val totalValue = categoryProducts.sumOf { it.price * it.inStockQuantity }
                val totalStock = categoryProducts.sumOf { it.inStockQuantity }
                Pair(totalValue, totalStock)
            }
    }

    // 2. Find Top N Best Selling Products from completed orders
    fun getTopSellingProducts(limit: Int = 3): List<Triple<Product, Int, Double>> {
        val completedOrders = orders.filter { it.status == "COMPLETED" }

        // Flatten all items from completed orders
        val productSalesMap = completedOrders
            .flatMap { it.items }
            .groupBy { it.productId }
            .mapValues { (_, items) ->
                val totalQty = items.sumOf { it.quantity }
                val totalRev = items.sumOf { it.quantity * it.unitPrice }
                Pair(totalQty, totalRev)
            }

        return productSalesMap.entries
            .mapNotNull { (productId, sales) ->
                val product = productMap[productId]
                product?.let { Triple(it, sales.first, sales.second) }
            }
            .sortedByDescending { it.second } // sort by quantity sold
            .take(limit)
    }

    // 3. Find high rated items that are low in stock (< 5 units, rating >= 4.0)
    fun getLowStockHighRatedItems(threshold: Int = 5, minRating: Double = 4.0): List<Product> {
        return products
            .filter { it.inStockQuantity < threshold && it.rating >= minRating }
            .sortedBy { it.inStockQuantity }
    }

    // 4. Calculate Customer Lifetime Value (LTV) across completed orders
    fun getCustomerLifetimeValue(): Map<String, Pair<Double, Int>> {
        return orders
            .filter { it.status == "COMPLETED" }
            .groupBy { it.customerId }
            .mapValues { (_, customerOrders) ->
                val totalSpent = customerOrders.sumOf { order ->
                    order.items.sumOf { it.quantity * it.unitPrice }
                }
                val orderCount = customerOrders.size
                Pair(totalSpent, orderCount)
            }
    }

    // 5. Unique Tags Sorted Alphabetically
    fun getAllUniqueTags(): List<String> {
        return products
            .flatMap { it.tags }
            .distinct()
            .sorted()
    }
}

fun formatPrice(amount: Double): String = String.format("$%,.2f", amount)

fun main() {
    val sampleProducts = listOf(
        Product(1, "Sony WH-1000XM5", "Electronics", 399.00, 4.8, 3, setOf("audio", "bluetooth", "noise-cancelling", "wireless")),
        Product(2, "Apple Watch Series 9", "Electronics", 399.00, 4.7, 15, setOf("smart", "bluetooth", "wireless")),
        Product(3, "Nike Air Max 270", "Footwear", 149.99, 4.6, 45, setOf("running", "casual")),
        Product(4, "Adidas Ultraboost Light", "Footwear", 189.99, 4.4, 2, setOf("running", "sports")),
        Product(5, "Minimalist Leather Wallet", "Accessories", 49.99, 4.5, 2, setOf("leather", "casual")),
        Product(6, "Aviator Sunglasses", "Accessories", 129.00, 3.9, 30, setOf("casual", "summer")),
        Product(7, "Dell 27-inch 4K Monitor", "Electronics", 499.00, 4.9, 12, setOf("screen", "display"))
    )

    val sampleOrders = listOf(
        Order(
            orderId = "ORD-001",
            customerId = "CUST-101",
            items = listOf(OrderItem(1, 10, 399.00), OrderItem(3, 4, 149.99)),
            status = "COMPLETED"
        ),
        Order(
            orderId = "ORD-002",
            customerId = "CUST-102",
            items = listOf(OrderItem(2, 11, 399.00)),
            status = "COMPLETED"
        ),
        Order(
            orderId = "ORD-003",
            customerId = "CUST-101",
            items = listOf(OrderItem(1, 8, 399.00), OrderItem(3, 10, 149.99)),
            status = "COMPLETED"
        ),
        Order(
            orderId = "ORD-004",
            customerId = "CUST-103",
            items = listOf(OrderItem(5, 5, 49.99)),
            status = "CANCELLED" // should not count towards analytics
        ),
        Order(
            orderId = "ORD-005",
            customerId = "CUST-103",
            items = listOf(OrderItem(5, 25, 49.99)),
            status = "COMPLETED"
        )
    )

    val analytics = CatalogAnalytics(sampleProducts, sampleOrders)

    println("============================================================")
    println("       📦 E-COMMERCE PRODUCT CATALOG & ANALYTICS ENGINE      ")
    println("============================================================")

    println("📊 1. INVENTORY VALUE PER CATEGORY:")
    analytics.getInventorySummaryByCategory().forEach { (category, data) ->
        println("  - ${category.padEnd(15)}: ${formatPrice(data.first)} (Total Stock: ${data.second} items)")
    }
    println()

    println("⭐ 2. TOP 3 BEST SELLING PRODUCTS:")
    analytics.getTopSellingProducts(3).forEachIndexed { index, (product, qty, revenue) ->
        println("  ${index + 1}. ${product.name.padEnd(24)}: $qty units sold (${formatPrice(revenue)} revenue)")
    }
    println()

    println("⚠️ 3. LOW STOCK / RESTOCK ALERTS (Rating >= 4.0 & Stock < 5):")
    analytics.getLowStockHighRatedItems().forEach { product ->
        println("  - ${product.name} [${product.category}] -> Stock: ${product.inStockQuantity} | Rating: ⭐${product.rating}")
    }
    println()

    println("💰 4. CUSTOMER LIFETIME VALUE (LTV):")
    analytics.getCustomerLifetimeValue().forEach { (customerId, stats) ->
        println("  - Customer #$customerId : ${formatPrice(stats.first)} across ${stats.second} order(s)")
    }
    println()

    println("🏷️ 5. ALL UNIQUE PRODUCT TAGS (Alphabetical):")
    println("  ${analytics.getAllUniqueTags()}")
    println("============================================================")
}
