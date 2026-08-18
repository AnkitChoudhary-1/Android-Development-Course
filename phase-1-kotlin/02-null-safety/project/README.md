# 🛡️ Project 2: Safe User Profile & API Response Sanitizer

## 🎯 Overview
In modern Android development, backend APIs often return incomplete, missing, or `null` JSON payloads. In this project, you will build a robust **Null-Safe API Response Sanitizer** in Kotlin that processes raw, unreliable user profiles and checkout payloads into guaranteed, clean, crash-proof domain models without throwing `NullPointerException`.

---

## 🛠️ Concepts Practiced
- Nullable types (`String?`, `Int?`, `Double?`)
- Safe call operator (`?.`) for navigating nested object graphs
- Elvis operator (`?:`) for providing sensible fallbacks and defaults
- Safe cast (`as?`) for dynamic types
- Not-Null assertion (`!!`) considerations & why to avoid them
- Scope function `let` for executing blocks only when values are non-null
- Smart casting with Kotlin compiler

---

## 📋 Requirements & Features

### 1. Raw Unreliable Input Models
Represent raw server payloads where almost everything could be null:
- `RawAddress`: `street?`, `city?`, `zipCode?`, `country?`
- `RawPaymentMethod`: `type?`, `last4?`, `expiryMonth?`, `expiryYear?`
- `RawUserProfile`: `id?`, `firstName?`, `lastName?`, `email?`, `phone?`, `address?`, `paymentMethods?`

### 2. Sanitization Pipeline
Implement sanitization functions:
- **Display Name:** Combine `firstName` and `lastName`. If both null, fallback to `"Guest User"`. If one is null, use the other trimmed.
- **Email Validation & Formatting:** Return lowercase trimmed email, or fallback to `"no-email@domain.com"`.
- **Address Formatting:** Format into `"Street, City, ZipCode, Country"`. If entire address or city is missing, fallback to `"Address Not Provided"`.
- **Primary Payment Sanitizer:** Find the first active/valid payment method or fallback to a default `"Cash on Delivery"`.
- **Avatar Generator:** Generate user initials (e.g., `"RK"` for Rohit Kumar, or `"GU"` for Guest User).

### 3. Order Processing Safety Check
- Verify if a user profile is complete enough for "1-Click Express Checkout".
- Express Checkout requires: valid non-empty email, complete address with zip code, and at least one valid payment method.

---

## 💻 Sample Output

```text
==================================================
           🛡️ SAFE PROFILE SANITIZER             
==================================================
[RAW USER 1: Incomplete Payload]
Raw Data: RawUser(id=101, firstName=Rohit, lastName=null, email=  ROHIT@GMAIL.COM  , address=null)

Sanitized Profile:
  - User ID       : 101
  - Display Name  : Rohit
  - Initials      : R
  - Email         : rohit@gmail.com
  - Phone         : N/A
  - Formatted Addr: Address Not Provided
  - Default Pay   : Cash on Delivery
  - Express Ready?: ❌ No (Missing shipping address & payment)

--------------------------------------------------
[RAW USER 2: Full Complete Payload]
Sanitized Profile:
  - User ID       : 102
  - Display Name  : Priya Sharma
  - Initials      : PS
  - Email         : priya.sharma@example.com
  - Phone         : +91 9876543210
  - Formatted Addr: 42 MG Road, Bangalore, 560001, India
  - Default Pay   : VISA (ending in 4242, exp: 12/28)
  - Express Ready?: ✅ Yes (Eligible for 1-Click Checkout)
==================================================
```

---

## 🚀 How to Run
```bash
kotlinc Solution.kt -include-runtime -d Solution.jar
java -jar Solution.jar
```
