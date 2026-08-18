# 🏛️ Project 5: Multi-Provider Payment Gateway & Notification Engine

## 🎯 Overview
Polymorphism and interface-driven design allow enterprise apps to swap underlying third-party SDKs (like Stripe, Razorpay, UPI, PayPal) without altering core business logic. In this project, you will build an extensible **Multi-Provider Payment Gateway & Notification Engine** in Kotlin.

---

## 🛠️ Concepts Practiced
- Open classes vs `abstract` classes
- Abstract methods and default method implementations in Kotlin interfaces
- Multiple interface implementation & conflict resolution (`super<InterfaceName>.method()`)
- Dynamic polymorphism & runtime dispatch
- Upcasting & smart type checking with `is` / `as`

---

## 📋 Requirements & Features

### 1. Interface Hierarchy
- `PaymentMethod`: `val providerName: String`, `fun processPayment(amount: Double): PaymentResult`, `fun isAvailable(): Boolean`
- `Refundable`: `fun refund(transactionId: String, amount: Double): Boolean`
- `BiometricCapable`: `fun authenticateBiometrics(userId: String): Boolean`
- `ReceiptNotifiable`: `fun sendReceipt(to: String, amount: Double, txnId: String)`

### 2. Base Abstract Class
- `BasePaymentProcessor`: Maintains attempt counts, transaction rate-limiting, and validation logic.

### 3. Concrete Implementations
- `UpiPaymentProcessor`: Implements `PaymentMethod`, `Refundable`, `BiometricCapable`. Requires UPI ID validation.
- `CreditCardPaymentProcessor`: Implements `PaymentMethod`, `Refundable`, `ReceiptNotifiable`. Validates 16-digit card number & CVV.
- `CryptoPaymentProcessor`: Implements `PaymentMethod` (non-refundable). Verifies blockchain wallet address.

### 4. Checkout Dispatcher
- A polymorphic `CheckoutDispatcher` that takes any `PaymentMethod` at runtime and processes transactions without hardcoded `if/else` checks for specific classes.

---

## 💻 Sample Output

```text
============================================================
      🏛️ MULTI-PROVIDER PAYMENT GATEWAY & NOTIFIER          
============================================================
[TRANSACTION 1: UPI Fast Checkout]
Authenticating user #USR-801 with Fingerprint...
✅ Biometrics Verified!
Processing UPI payment of $45.00 (VPA: rohit@upi)...
✅ Payment Successful! Txn ID: UPI-849204128
Dispatching SMS & In-App Alert...

[TRANSACTION 2: Credit Card Checkout]
Validating Card ending in 8841...
Processing Credit Card payment of $299.00...
✅ Payment Successful! Txn ID: CC-99128471
📧 Receipt emailed to user@domain.com for amount $299.00

[TRANSACTION 3: Crypto Payment Checkout]
Processing Crypto transfer of $1,200.00 to 0x71C...3aF...
✅ Blockchain Transaction Broadcasted! Txn ID: CRYPTO-0x98124
⚠️ Note: Crypto transactions are non-refundable.

[TRANSACTION 4: Processing Refund on UPI]
Initiating refund of $45.00 for Txn ID: UPI-849204128...
✅ Refund Processed via UPI Gateway.
============================================================
```

---

## 🚀 How to Run
```bash
kotlinc Solution.kt -include-runtime -d Solution.jar
java -jar Solution.jar
```
