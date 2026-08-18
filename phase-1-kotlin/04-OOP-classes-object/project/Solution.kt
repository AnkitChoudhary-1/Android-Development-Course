import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

/**
 * 🏦 Secure Digital Wallet & Ledger System
 * Demonstrates: Primary/Secondary Constructors, init, encapsulation, custom getters, lazy, companion object.
 */

enum class TransactionType {
    INITIAL, DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT
}

class TransactionRecord(
    val txnId: String,
    val type: TransactionType,
    val amount: Double,
    val timestamp: String,
    val description: String
)

class WalletAccount(
    val accountId: String,
    val ownerName: String,
    initialBalance: Double = 0.0
) {
    // Encapsulated Balance: Public Read, Private Write
    var balance: Double = 0.0
        private set

    // Audit log of all transactions
    private val _transactions = mutableListOf<TransactionRecord>()
    val transactions: List<TransactionRecord> get() = _transactions.toList()

    // Custom Formatted Getter
    val formattedBalance: String
        get() = String.format("$%,.2f", balance)

    // Lazy Security Checksum (computed only on first access)
    val securityChecksum: String by lazy {
        val raw = "$accountId:$ownerName:${System.currentTimeMillis()}"
        "SHA256:" + raw.hashCode().toString(16).padStart(32, '0')
    }

    // Input validation in init block
    init {
        require(ownerName.isNotBlank()) { "Account owner name cannot be blank." }
        require(initialBalance >= 0.0) { "Initial balance cannot be negative." }

        if (initialBalance > 0.0) {
            balance = initialBalance
            _transactions.add(
                TransactionRecord(
                    txnId = Companion.generateTxnId(),
                    type = TransactionType.INITIAL,
                    amount = initialBalance,
                    timestamp = getCurrentTimestamp(),
                    description = "Initial Account Opening Deposit"
                )
            )
        }
    }

    // Deposit Method
    fun deposit(amount: Double): TransactionRecord {
        require(amount > 0.0) { "Deposit amount must be greater than zero." }
        balance += amount
        val record = TransactionRecord(
            txnId = Companion.generateTxnId(),
            type = TransactionType.DEPOSIT,
            amount = amount,
            timestamp = getCurrentTimestamp(),
            description = "Direct Deposit"
        )
        _transactions.add(record)
        return record
    }

    // Withdrawal Method
    fun withdraw(amount: Double): Result<TransactionRecord> {
        if (amount <= 0.0) return Result.failure(IllegalArgumentException("Withdrawal must be positive."))
        if (amount > balance) {
            return Result.failure(IllegalStateException("Insufficient funds. Available: $formattedBalance, Requested: ${String.format("$%,.2f", amount)}"))
        }

        balance -= amount
        val record = TransactionRecord(
            txnId = Companion.generateTxnId(),
            type = TransactionType.WITHDRAWAL,
            amount = amount,
            timestamp = getCurrentTimestamp(),
            description = "Direct Withdrawal"
        )
        _transactions.add(record)
        return Result.success(record)
    }

    // Internal hook for transfers
    internal fun recordTransfer(type: TransactionType, amount: Double, counterPartyId: String): TransactionRecord {
        if (type == TransactionType.TRANSFER_OUT) {
            balance -= amount
        } else {
            balance += amount
        }
        val record = TransactionRecord(
            txnId = Companion.generateTxnId(),
            type = type,
            amount = amount,
            timestamp = getCurrentTimestamp(),
            description = "Peer Transfer ${if (type == TransactionType.TRANSFER_OUT) "to" else "from"} #$counterPartyId"
        )
        _transactions.add(record)
        return record
    }

    companion object {
        private var txnCounter = 98120

        fun generateTxnId(): String {
            return "TXN-2026-${txnCounter++}"
        }

        private fun getCurrentTimestamp(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            return sdf.format(Date())
        }

        // Factory Method
        fun createAccount(name: String, startingAmount: Double = 0.0): WalletAccount {
            val id = "ACC-${(1000..9999).random()}"
            return WalletAccount(id, name, startingAmount)
        }
    }
}

// Transfer Orchestrator
object TransferManager {
    fun transfer(from: WalletAccount, to: WalletAccount, amount: Double): Result<String> {
        if (from.accountId == to.accountId) {
            return Result.failure(IllegalArgumentException("Cannot transfer to the same account."))
        }
        if (amount <= 0.0) {
            return Result.failure(IllegalArgumentException("Transfer amount must be positive."))
        }
        if (from.balance < amount) {
            return Result.failure(IllegalStateException("Insufficient funds. Available: ${from.formattedBalance}, Requested: ${String.format("$%,.2f", amount)}"))
        }

        from.recordTransfer(TransactionType.TRANSFER_OUT, amount, to.accountId)
        to.recordTransfer(TransactionType.TRANSFER_IN, amount, from.accountId)

        return Result.success("Successfully transferred ${String.format("$%,.2f", amount)} from ${from.ownerName} to ${to.ownerName}.")
    }
}

fun main() {
    println("============================================================")
    println("         🏦 SECURE DIGITAL WALLET & LEDGER SYSTEM           ")
    println("============================================================")

    val rohitWallet = WalletAccount("ACC-1001", "Rohit Kumar", 1000.0)
    val priyaWallet = WalletAccount("ACC-1002", "Priya Sharma", 500.0)

    println("[CREATING ACCOUNTS]")
    println("✅ Account Created: #${rohitWallet.accountId} (Owner: ${rohitWallet.ownerName}, Balance: ${rohitWallet.formattedBalance})")
    println("✅ Account Created: #${priyaWallet.accountId} (Owner: ${priyaWallet.ownerName}, Balance: ${priyaWallet.formattedBalance})\n")

    println("[TRANSACTION 1: Deposit Funds]")
    println("Depositing $250.00 to Rohit's wallet...")
    val depRecord = rohitWallet.deposit(250.0)
    println("✅ Deposit Success! New Balance: ${rohitWallet.formattedBalance} (Txn ID: ${depRecord.txnId})\n")

    println("[TRANSACTION 2: Peer-to-Peer Transfer]")
    println("Transferring $300.00 from Rohit -> Priya...")
    val transferResult = TransferManager.transfer(rohitWallet, priyaWallet, 300.0)
    if (transferResult.isSuccess) {
        println("✅ Transfer Success!")
        println("  - Rohit's Balance : ${rohitWallet.formattedBalance}")
        println("  - Priya's Balance : ${priyaWallet.formattedBalance}\n")
    }

    println("[TRANSACTION 3: Overdraft / Insufficient Funds Check]")
    println("Attempting to withdraw $1,500.00 from Priya's wallet...")
    val withdrawResult = priyaWallet.withdraw(1500.0)
    withdrawResult.onFailure { error ->
        println("❌ Transaction Failed: ${error.message}\n")
    }

    println("[TRANSACTION AUDIT HISTORY FOR ${rohitWallet.ownerName.uppercase()}]")
    println("----------------------------------------------------------------------")
    println("Timestamp           | Type         | Amount      | Txn ID")
    println("----------------------------------------------------------------------")
    rohitWallet.transactions.forEach { txn ->
        val sign = if (txn.type in listOf(TransactionType.INITIAL, TransactionType.DEPOSIT, TransactionType.TRANSFER_IN)) "+" else "-"
        println("${txn.timestamp} | ${txn.type.name.padEnd(12)} | $sign${String.format("$%,.2f", txn.amount).padEnd(10)} | ${txn.txnId}")
    }
    println("----------------------------------------------------------------------")
    println("Security Checksum: ${rohitWallet.securityChecksum}")
    println("============================================================")
}
