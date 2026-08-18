/**
 * 🍔 QuickBite CLI Food Delivery Pricing & Split Calculator
 * Demonstrates: val/var, type inference, string templates, default/named arguments, single-expression functions.
 */

// Single-expression function for tax calculation
fun calculateTax(amount: Double, taxRate: Double = 0.05): Double = amount * taxRate

// Single-expression function for delivery fee
fun calculateDeliveryFee(distanceKm: Double, baseFee: Double = 3.50, perKmRate: Double = 0.75): Double =
    baseFee + (distanceKm * perKmRate)

// Calculate promo discount with validation
fun calculateDiscount(subtotal: Double, deliveryFee: Double, promoCode: String): Pair<Double, Double> {
    var foodDiscount = 0.0
    var deliveryDiscount = 0.0

    when (promoCode.uppercase()) {
        "WELCOME50" -> {
            val potentialDiscount = subtotal * 0.50
            foodDiscount = if (potentialDiscount > 10.0) 10.0 else potentialDiscount
        }
        "FREESHIP" -> {
            deliveryDiscount = deliveryFee
        }
        "FLAT5" -> {
            if (subtotal >= 25.0) {
                foodDiscount = 5.0
            }
        }
    }
    return Pair(foodDiscount, deliveryDiscount)
}

// Single-expression function for tip
fun calculateTip(subtotal: Double, tipPercent: Double = 15.0): Double =
    subtotal * (tipPercent / 100.0)

// Helper to format currency
fun formatCurrency(amount: Double): String = String.format("$%.2f", amount)

// Main order processor and receipt printer
fun generateReceipt(
    orderId: String,
    items: List<Triple<String, Int, Double>>, // Name, Quantity, Unit Price
    distanceKm: Double,
    promoCode: String = "",
    tipPercent: Double = 15.0,
    splitCount: Int = 1
) {
    println("========================================")
    println("           🍔 QUICKBITE RECEIPT          ")
    println("========================================")
    println("Order ID: $orderId")
    println("Items:")

    var subtotal = 0.0
    for ((name, qty, unitPrice) in items) {
        val lineTotal = qty * unitPrice
        subtotal += lineTotal
        println("  - ${qty}x ${name.padEnd(20)} : ${formatCurrency(lineTotal)}")
    }

    println("----------------------------------------")
    println("Subtotal                 : ${formatCurrency(subtotal)}")

    val initialDeliveryFee = calculateDeliveryFee(distanceKm)
    val (foodDiscount, deliveryDiscount) = calculateDiscount(subtotal, initialDeliveryFee, promoCode)

    val discountedSubtotal = subtotal - foodDiscount
    val finalDeliveryFee = initialDeliveryFee - deliveryDiscount

    if (foodDiscount > 0.0 || deliveryDiscount > 0.0) {
        val totalDiscount = foodDiscount + deliveryDiscount
        println("Promo Applied ($promoCode): -${formatCurrency(totalDiscount)}")
        println("Discounted Subtotal      : ${formatCurrency(discountedSubtotal)}")
    }

    val tax = calculateTax(discountedSubtotal)
    val tip = calculateTip(discountedSubtotal, tipPercent)
    val grandTotal = discountedSubtotal + tax + finalDeliveryFee + tip

    println("Taxes (5.0%)             : ${formatCurrency(tax)}")
    println("Delivery Fee (${distanceKm} km)    : ${formatCurrency(finalDeliveryFee)}")
    println("Tip (${tipPercent}%)              : ${formatCurrency(tip)}")
    println("----------------------------------------")
    println("GRAND TOTAL              : ${formatCurrency(grandTotal)}")
    println("----------------------------------------")

    if (splitCount > 1) {
        val perPerson = grandTotal / splitCount
        println("Split among $splitCount people     : ${formatCurrency(perPerson)} per person")
    }

    println("========================================")
    println("Thank you for ordering with QuickBite!\n")
}

fun main() {
    val orderItems = listOf(
        Triple("Chicken Burger", 2, 8.99),
        Triple("Large Peri Fries", 1, 4.50),
        Triple("Cold Coffee", 2, 3.50)
    )

    // Using Named Arguments for readability
    generateReceipt(
        orderId = "#QB-89412",
        items = orderItems,
        distanceKm = 4.2,
        promoCode = "WELCOME50",
        tipPercent = 15.0,
        splitCount = 3
    )

    // Another example without split and free shipping promo
    val singleOrder = listOf(
        Triple("Margherita Pizza", 1, 14.99),
        Triple("Garlic Bread", 1, 5.99)
    )

    generateReceipt(
        orderId = "#QB-89413",
        items = singleOrder,
        distanceKm = 6.5,
        promoCode = "FREESHIP",
        tipPercent = 10.0,
        splitCount = 1
    )
}
