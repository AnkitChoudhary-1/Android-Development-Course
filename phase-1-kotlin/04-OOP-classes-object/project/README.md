# 🏦 Project 4: Digital Wallet & Secure Ledger System

## 🎯 Overview
Object-Oriented Programming in Kotlin eliminates tedious Java-style boilerplate through primary constructors, custom property accessors (`get()`/`set()`), initialization blocks (`init`), `lazy`/`lateinit` semantics, and companion objects. In this project, you will build an enterprise **Digital Wallet & Secure Transaction Ledger System**.

---

## 🛠️ Concepts Practiced
- Primary & Secondary Constructors with parameter validation
- `init` blocks for initialization safety
- Encapsulation with `private`, `protected`, `internal`, and `public`
- Custom property getters and setters with the `field` identifier
- `lateinit var` vs `val by lazy` initialization patterns
- `object` singletons and `companion object` factory methods

---

## 📋 Requirements & Features

### 1. `WalletAccount` Class
- **Primary Constructor:** `val accountId: String`, `val ownerName: String`, `initialBalance: Double = 0.0`
- **Balance Encapsulation:** `balance` property has a `public` getter but a `private` setter so external code cannot arbitrarily modify money.
- **Custom Formatted Balance:** `formattedBalance` read-only getter returning formatted currency (e.g. `$1,250.50`).
- **Lazy Security Profile:** A `securityProfile` object initialized via `by lazy` that computes cryptographic hash checksums only when queried.
- **Validation:** Account creation fails if `ownerName` is blank or `initialBalance < 0.0`.

### 2. Transaction Auditing
- **Transaction Types:** `DEPOSIT`, `WITHDRAWAL`, `TRANSFER_IN`, `TRANSFER_OUT`
- **Tamper-Proof IDs:** Companion object utility generating unique transaction tokens (`TXN-YYYY-XXXX`).
- **Audit Log:** Running immutable list of all completed transactions with timestamps, amounts, and statuses.

### 3. Transfer Orchestrator
- Atomic transfer between two `WalletAccount` instances.
- Rejects transfers if balance is insufficient, amount is negative, or recipient is identical to sender.

---

## 💻 Sample Output

```text
============================================================
         🏦 SECURE DIGITAL WALLET & LEDGER SYSTEM           
============================================================
[CREATING ACCOUNTS]
✅ Account Created: #ACC-1001 (Owner: Rohit Kumar, Balance: $1,000.00)
✅ Account Created: #ACC-1002 (Owner: Priya Sharma, Balance: $500.00)

[TRANSACTION 1: Deposit Funds]
Depositing $250.00 to Rohit's wallet...
✅ Deposit Success! New Balance: $1,250.00 (Txn ID: TXN-2026-98124)

[TRANSACTION 2: Peer-to-Peer Transfer]
Transferring $300.00 from Rohit -> Priya...
✅ Transfer Success!
  - Rohit's Balance : $950.00
  - Priya's Balance : $800.00

[TRANSACTION 3: Overdraft / Insufficient Funds Check]
Attempting to withdraw $1,500.00 from Priya's wallet...
❌ Transaction Failed: Insufficient funds. Available: $800.00, Requested: $1,500.00

[TRANSACTION AUDIT HISTORY FOR ROHIT]
------------------------------------------------------------
Timestamp             | Type        | Amount     | Txn ID
------------------------------------------------------------
2026-08-18 20:15:00   | INITIAL     | +$1,000.00 | TXN-2026-98120
2026-08-18 20:15:01   | DEPOSIT     | +$250.00   | TXN-2026-98124
2026-08-18 20:15:02   | TRANSFER_OUT| -$300.00   | TXN-2026-98125
------------------------------------------------------------
Security Token Hash: SHA256:7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1f
============================================================
```

---

## 🚀 How to Run
```bash
kotlinc Solution.kt -include-runtime -d Solution.jar
java -jar Solution.jar
```
