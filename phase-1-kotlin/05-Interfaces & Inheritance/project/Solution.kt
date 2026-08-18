import java.util.UUID

/**
 * 🏛️ Multi-Provider Payment Gateway & Notification Engine
 * Demonstrates: abstract classes, interfaces, default methods, polymorphism, multiple interfaces.
 */

// Domain Result Type
class PaymentResult(
    val isSuccess: Boolean,
    val transactionId: String,
    val message: String
)

// Interfaces
interface PaymentMethod {
    val providerName: String
    fun isAvailable(): Boolean = true
    fun processPayment(amount: Double): PaymentResult
}

interface Refundable {
    fun refund(transactionId: String, amount: Double): Boolean {
        println("  [Refundable] Generic refund requested for Txn #$transactionId ($${String.format("%.2f", amount)})")
        return true
    }
}

interface BiometricCapable {
    fun authenticateBiometrics(userId: String): Boolean {
        println("  [Biometrics] Authenticating user #$userId with Fingerprint/FaceID...")
        return true
    }
}

interface ReceiptNotifiable {
    fun sendReceipt(toEmail: String, amount: Double, txnId: String) {
        println("  [Receipt] 📧 Receipt emailed to $toEmail for amount $${String.format("%.2f", amount)} (Txn: $txnId)")
    }
}

// Abstract Base Class
abstract class BasePaymentProcessor(
    override val providerName: String
) : PaymentMethod {

    protected var attemptCount = 0

    // Common validation for all payment processors
    protected fun validateAmount(amount: Double): Boolean {
        attemptCount++
        return amount > 0.0
    }

    abstract fun executeTransaction(amount: Double): PaymentResult

    override fun processPayment(amount: Double): PaymentResult {
        if (!validateAmount(amount)) {
            return PaymentResult(false, "", "Invalid payment amount: $amount")
        }
        if (!isAvailable()) {
            return PaymentResult(false, "", "$providerName service is currently unavailable.")
        }
        return executeTransaction(amount)
    }
}

// Concrete Implementations
class UpiPaymentProcessor(
    val upiId: String
) : BasePaymentProcessor("UPI"), Refundable, BiometricCapable {

    override fun executeTransaction(amount: Double): PaymentResult {
        println("Processing UPI payment of $${String.format("%.2f", amount)} (VPA: $upiId)...")
        val txnId = "UPI-" + (100000000..999999999).random()
        return PaymentResult(true, txnId, "UPI payment processed successfully.")
    }

    override fun refund(transactionId: String, amount: Double): Boolean {
        println("Initiating instant UPI refund of $${String.format("%.2f", amount)} for Txn ID: $transactionId...")
        println("✅ Refund processed via NPCI / UPI Gateway.")
        return true
    }
}

class CreditCardPaymentProcessor(
    val cardNumber: String,
    val cardHolder: String,
    val expiry: String
) : BasePaymentProcessor("Credit Card"), Refundable, ReceiptNotifiable {

    private val maskedCard: String
        get() = "**** **** **** " + cardNumber.takeLast(4)

    override fun executeTransaction(amount: Double): PaymentResult {
        println("Validating Card ($maskedCard) for $cardHolder...")
        val txnId = "CC-" + (10000000..99999999).random()
        return PaymentResult(true, txnId, "Credit card charged successfully.")
    }

    override fun refund(transactionId: String, amount: Double): Boolean {
        println("Initiating Credit Card chargeback refund for Txn ID: $transactionId ($${String.format("%.2f", amount)})...")
        println("✅ 5-7 business days refund initiated.")
        return true
    }
}

class CryptoPaymentProcessor(
    val walletAddress: String,
    val network: String = "Ethereum"
) : BasePaymentProcessor("Crypto ($network)") {

    override fun executeTransaction(amount: Double): PaymentResult {
        val shortAddr = "${walletAddress.take(6)}...${walletAddress.takeLast(4)}"
        println("Processing Crypto transfer of $${String.format("%.2f", amount)} to $shortAddr on $network...")
        val txnId = "0x" + UUID.randomUUID().toString().replace("-", "").take(16)
        return PaymentResult(true, txnId, "Blockchain transaction broadcasted.")
    }
}

// Polymorphic Checkout Dispatcher
class CheckoutDispatcher {
    fun checkout(
        paymentMethod: PaymentMethod,
        amount: Double,
        userEmail: String = "user@example.com",
        userId: String = "USR-801"
    ): PaymentResult {
        // Biometric auth if supported
        if (paymentMethod is BiometricCapable) {
            val authenticated = paymentMethod.authenticateBiometrics(userId)
            if (!authenticated) {
                return PaymentResult(false, "", "Biometric authentication failed.")
            }
            println("  ✅ Biometrics Verified!")
        }

        // Process polymorphically
        val result = paymentMethod.processPayment(amount)

        if (result.isSuccess) {
            println("✅ Payment Successful! Txn ID: ${result.transactionId}")

            // Send receipt if supported
            if (paymentMethod is ReceiptNotifiable) {
                paymentMethod.sendReceipt(userEmail, amount, result.transactionId)
            }
        } else {
            println("❌ Payment Failed: ${result.message}")
        }
        return result
    }
}

fun main() {
    println("============================================================")
    println("      🏛️ MULTI-PROVIDER PAYMENT GATEWAY & NOTIFIER          ")
    println("============================================================")

    val dispatcher = CheckoutDispatcher()

    // 1. UPI Checkout
    println("[TRANSACTION 1: UPI Fast Checkout]")
    val upi = UpiPaymentProcessor("rohit@okaxis")
    val upiResult = dispatcher.checkout(upi, 45.00)
    println()

    // 2. Credit Card Checkout
    println("[TRANSACTION 2: Credit Card Checkout]")
    val card = CreditCardPaymentProcessor("4532890123458841", "Rohit Kumar", "12/28")
    dispatcher.checkout(card, 299.00, userEmail = "rohit.dev@gmail.com")
    println()

    // 3. Crypto Checkout
    println("[TRANSACTION 3: Crypto Payment Checkout]")
    val crypto = CryptoPaymentProcessor("0x71C8366420A80041da106362548a80d53c3aF91a")
    dispatcher.checkout(crypto, 1200.00)
    println("⚠️ Note: Crypto transactions are non-refundable.\n")

    // 4. Processing Refund Polymorphically
    println("[TRANSACTION 4: Processing Refund on UPI]")
    if (upiResult.isSuccess) {
        upi.refund(upiResult.transactionId, 45.00)
    }

    println("============================================================")
}
