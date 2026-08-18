# 🍔 Project 1: CLI Food Delivery Cost & Tip Calculator

## 🎯 Overview
In this project, you will build a command-line food delivery pricing and split-billing calculator in Kotlin. This project is designed to cement your understanding of Kotlin fundamentals: variables (`val` vs `var`), type inference, string templates, functions with default and named arguments, and single-expression functions.

---

## 🛠️ Concepts Practiced
- Immutability with `val` vs reassignable `var`
- Basic data types (`Double`, `Int`, `String`, `Boolean`) & Type Inference
- String interpolation & multi-line formatted strings
- Functions with default parameter values
- Named arguments for readable function calls
- Single-expression functions (`fun calculateTax(subtotal: Double): Double = subtotal * 0.05`)

---

## 📋 Requirements & Features

### 1. Pricing Engine
- **Base Fee Calculation:** Calculate subtotal based on item counts and base prices.
- **Dynamic Delivery Fee:** Fixed base delivery fee ($3.50) + per-kilometer charge ($0.75/km).
- **Tax Calculation:** Standard GST/VAT rate (default 5%).
- **Discount & Promo Codes:**
  - `"WELCOME50"`: 50% discount on food subtotal (up to a max of $10.00).
  - `"FREESHIP"`: 100% discount on delivery fee.
  - `"FLAT5"`: Flat $5.00 discount on orders above $25.00.
- **Tip Calculator:** Calculate custom tip percentages (0%, 10%, 15%, 20%).

### 2. Group Bill Splitting
- Ability to split total bill evenly among `N` friends.
- Calculate individual contributions and format currency nicely.

### 3. Formatted Receipt Generator
- Print a clean, receipt-style breakdown in the console with line items, applied discounts, delivery fee, taxes, tips, grand total, and per-person split.

---

## 💻 Sample Output

```text
========================================
           🍔 QUICKBITE RECEIPT          
========================================
Order ID: #QB-89412
Items:
  - 2x Chicken Burger    : $17.98
  - 1x Large Peri Fries  : $4.50
  - 2x Cold Coffee       : $7.00
----------------------------------------
Subtotal                 : $29.48
Promo Applied (WELCOME50): -$10.00
Discounted Subtotal      : $19.48
Taxes (5.0%)             : $0.97
Delivery Fee (4.2 km)    : $6.65
Tip (15.0%)              : $2.92
----------------------------------------
GRAND TOTAL              : $30.02
----------------------------------------
Split among 3 people     : $10.01 per person
========================================
Thank you for ordering with QuickBite!
```

---

## 🚀 How to Run
Run the solution using `kotlinc` or inside IntelliJ IDEA / Android Studio:

```bash
kotlinc Solution.kt -include-runtime -d Solution.jar
java -jar Solution.jar
```
