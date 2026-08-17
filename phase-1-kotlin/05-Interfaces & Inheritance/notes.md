# 🏛️ Complete Guide to Interfaces and Inheritance in Kotlin

![Interfaces & Inheritance](./Interfaces%20&%20Inheritance.png)

---

## ❓ Part 1: What is Inheritance and Why Does It Exist?

### 💡 Starting With the Problem

Imagine building a food delivery app. You need to model different user types: Customers, Delivery Drivers, and Restaurant Owners.

```kotlin
// WITHOUT INHERITANCE — Massive Code Duplication:

class Customer {
    var id: Int = 0
    var name: String = ""
    var email: String = ""
    fun login() { println("$name logged in") }
    fun placeOrder() { println("$name placed an order") }
}

class DeliveryDriver {
    var id: Int = 0
    var name: String = ""
    var email: String = "" // DUPLICATED!
    fun login() { println("$name logged in") } // DUPLICATED!
    fun acceptDelivery() { println("$name accepted delivery") }
}

class RestaurantOwner {
    var id: Int = 0
    var name: String = ""
    var email: String = "" // DUPLICATED!
    fun login() { println("$name logged in") } // DUPLICATED!
    fun addMenuItem() { println("$name added menu item") }
}

// PROBLEMS:
// ❌ Duplicated fields and methods across all 3 classes.
// ❌ Fix a bug in login() → must update in ALL 3 places.
// ❌ Violates DRY (Don't Repeat Yourself) principle.
```

---

### 🧬 What is Inheritance?

> 🧬 **INHERITANCE:** A mechanism where a child class (**subclass**) inherits properties and methods from a parent class (**superclass**), enabling code reuse and customization via overriding.

```
FAMILY INHERITANCE ANALOGY:

Grandfather (Person): Name, Age, Walk(), Eat()
   └─► Father (Employee): Inherits Person + Adds EmployeeID, Salary, Work()
          └─► Son (SoftwareEngineer): Inherits Person & Employee + Adds ProgrammingLanguages, WriteCode()
```

- **Relationship:** **"IS-A"** Relationship (e.g., `Customer` IS-A `User`, `Dog` IS-AN `Animal`).

---

## 🔓 Part 2: The `open` Keyword — Closed by Default

> 🔒 **KOTLIN'S DEFAULT:** All classes and methods in Kotlin are **`final` by default** (cannot be inherited or overridden). To allow inheritance, you must explicitly mark them with the **`open`** keyword.

```kotlin
// By DEFAULT — classes are FINAL:
class Animal

// class Dog : Animal() // ❌ COMPILE ERROR: Animal is final!

// ALLOW INHERITANCE — mark class and overridable methods as open:
open class Animal {
    fun breathe() { println("Breathing") } // Cannot be overridden (final)

    open fun makeSound() { println("...") } // OPEN — Can be overridden by subclasses
}

class Dog : Animal() {
    override fun makeSound() { println("Woof! 🐶") } // OVERRIDE parent method
}
```

> [!NOTE]
> **Why `final` by default?** Prevents the **Fragile Base Class Problem** where unexpected subclass overrides break parent implementation logic. Follows Joshua Bloch's rule: *"Design for inheritance or prohibit it."*

---

## 🚀 Part 3: Extending a Class — Inheritance in Action

```kotlin
// PARENT CLASS (Superclass):
open class User(
    val id: Int,
    var name: String,
    var email: String,
    var phoneNumber: String
) {
    open val userType: String = "User"

    fun login() {
        println("[$userType] $name logged in")
    }

    open fun sendNotification(message: String) {
        println("📱 Notification to $name: $message")
    }

    open fun getPermissions(): List<String> = listOf("READ_PROFILE", "EDIT_PROFILE")
}

// CHILD CLASS 1:
class Customer(
    id: Int, name: String, email: String, phoneNumber: String,
    var deliveryAddress: String
) : User(id, name, email, phoneNumber) { // Invokes parent constructor

    override val userType: String = "Customer"

    override fun sendNotification(message: String) {
        super.sendNotification(message) // Invoke parent implementation
        println("   → Delivery target: $deliveryAddress")
    }

    override fun getPermissions(): List<String> =
        super.getPermissions() + listOf("PLACE_ORDER", "TRACK_ORDER")

    fun placeOrder(item: String) {
        println("✅ $name ordered: $item")
    }
}

// CHILD CLASS 2:
class DeliveryDriver(
    id: Int, name: String, email: String, phoneNumber: String,
    val vehicleType: String
) : User(id, name, email, phoneNumber) {

    override val userType: String = "DeliveryDriver"

    override fun getPermissions(): List<String> =
        super.getPermissions() + listOf("ACCEPT_DELIVERY", "VIEW_MAP")
}

fun main() {
    val customer = Customer(1, "Rohit", "rohit@gmail.com", "9876543210", "42 MG Road")
    customer.login()                           // [Customer] Rohit logged in
    customer.sendNotification("Order prepared")// 📱 Notification to Rohit... → Delivery target...
    customer.placeOrder("Biryani")             // ✅ Rohit ordered: Biryani
}
```

---

## ⬆️ Part 4: The `super` Keyword

> ⬆️ **`super`:** Refers to the immediate parent superclass. Used to call parent constructors (`super(...)`), parent methods (`super.method()`), or parent properties (`super.property`).

```kotlin
open class Vehicle(val brand: String, val speed: Int) {
    open fun describe(): String = "$brand vehicle ($speed km/h)"
}

class ElectricCar(brand: String, speed: Int, val batteryCapacity: Double) : Vehicle(brand, speed) {
    override fun describe(): String {
        val parentDesc = super.describe() // Invoke parent implementation
        return "$parentDesc | Battery: ${batteryCapacity}kWh"
    }
}
```

---

## 🎨 Part 5: Abstract Classes

> 🎨 **ABSTRACT CLASS:** A parent class that **cannot be instantiated directly**. Can contain both **abstract members** (no implementation; subclasses MUST implement) and **concrete members** (with implementations).

```
SHAPE ANALOGY:
You cannot draw "a generic shape" — you draw a Circle, Square, or Triangle.
All shapes have an Area, but each calculates area differently!
```

```kotlin
abstract class Shape(val color: String) {
    abstract val name: String            // ABSTRACT property
    abstract fun calculateArea(): Double // ABSTRACT method

    // CONCRETE method shared by all shapes:
    fun describe() {
        println("$color $name | Area: ${"%.2f".format(calculateArea())} cm²")
    }
}

class Circle(color: String, val radius: Double) : Shape(color) {
    override val name: String = "Circle"
    override fun calculateArea(): Double = Math.PI * radius * radius
}

class Rectangle(color: String, val width: Double, val height: Double) : Shape(color) {
    override val name: String = "Rectangle"
    override fun calculateArea(): Double = width * height
}

fun main() {
    val circle: Shape = Circle("Red", 5.0)
    val rectangle: Shape = Rectangle("Blue", 4.0, 6.0)

    circle.describe()    // Red Circle | Area: 78.54 cm²
    rectangle.describe() // Blue Rectangle | Area: 24.00 cm²
}
```

---

### 📊 Abstract Class vs Open Class

| Feature | `abstract class` | `open class` |
| :--- | :--- | :--- |
| **Instantiation** | ❌ **Cannot create direct objects** | ✅ Can instantiate direct objects |
| **Abstract Members**| ✅ Allowed (`abstract fun`) | ❌ **Not allowed** (All methods must have body) |
| **Subclass Rule** | Subclasses **MUST** implement abstract members | Subclasses **MAY** optionally override `open` members |

---

## 📜 Part 6: Interfaces — The Pure Contract

> 📜 **INTERFACE:** Defines a **contract/job description**. Specifies WHAT methods/properties a class must have, without specifying HOW they are implemented. Supports **Multiple Inheritance of Behavior**!

```kotlin
interface Clickable {
    fun onClick()                          // Abstract method
    fun onDoubleClick() { println("Double clicked!") } // Default implementation
}

interface Animatable {
    fun startAnimation()
}

interface Shareable {
    val shareTitle: String
    fun share()
}

// MULTIPLE INTERFACES IMPLEMENTATION:
class ImageCard(val url: String) : Clickable, Animatable, Shareable {
    override val shareTitle: String = "Check out this image!"

    override fun onClick() { println("Image clicked: $url") }
    override fun startAnimation() { println("Starting fade-in animation") }
    override fun share() { println("Sharing $shareTitle ($url)") }
}
```

> [!TIP]
> **Multiple Interfaces vs Single Class Inheritance:** In Kotlin, a class can extend only **ONE parent class** (abstract or open), but can implement **UNLIMITED interfaces** (`Clickable`, `Animatable`, `Shareable`).

---

## 🎭 Part 7: Polymorphism — Many Forms

> 🎭 **POLYMORPHISM:** The ability of different object types to respond to the **same method call** in their own specific way.

$$\text{Same Method Call} \xrightarrow{\text{Dynamic Dispatch}} \text{Subclass-Specific Behavior}$$

```kotlin
abstract class Animal(val name: String) {
    abstract fun makeSound(): String
}

class Dog(name: String) : Animal(name) { override fun makeSound() = "Woof! 🐶" }
class Cat(name: String) : Animal(name) { override fun makeSound() = "Meow! 🐱" }
class Duck(name: String) : Animal(name) { override fun makeSound() = "Quack! 🦆" }

fun main() {
    // List of general 'Animal' type containing specific subclasses:
    val zoo: List<Animal> = listOf(Dog("Rex"), Cat("Whiskers"), Duck("Donald"))

    // POLYMORPHISM IN ACTION:
    zoo.forEach { animal ->
        println("${animal.name} says: ${animal.makeSound()}")
    }
}
```

```text
OUTPUT:
Rex says: Woof! 🐶
Whiskers says: Meow! 🐱
Donald says: Quack! 🦆
```

---

## 💳 Part 8: Real Android Example — Payment System Architecture

```kotlin
// STRATEGY PATTERN WITH INTERFACES AND POLYMORPHISM

interface PaymentMethod {
    val methodName: String
    val methodIcon: String
    fun processPayment(amount: Double, orderId: String): PaymentResult
}

interface Refundable {
    fun processRefund(transactionId: String, amount: Double): PaymentResult
}

data class PaymentResult(val success: Boolean, val transactionId: String?, val amount: Double)

// ABSTRACT BASE PAYMENT METHOD:
abstract class BasePaymentMethod : PaymentMethod {
    fun formatAmount(amount: Double): String = "₹${"%.2f".format(amount)}"
}

// CONCRETE IMPLEMENTATION 1: Credit Card
class CreditCard(val cardNumber: String) : BasePaymentMethod(), Refundable {
    override val methodName = "Credit Card"
    override val methodIcon = "💳"

    override fun processPayment(amount: Double, orderId: String): PaymentResult {
        val txId = "CC-${System.currentTimeMillis()}"
        println("$methodIcon Authorizing ${formatAmount(amount)} on card ending in ${cardNumber.takeLast(4)}")
        return PaymentResult(true, txId, amount)
    }

    override fun processRefund(transactionId: String, amount: Double): PaymentResult {
        return PaymentResult(true, "REF-$transactionId", amount)
    }
}

// CONCRETE IMPLEMENTATION 2: UPI
class UPI(val upiId: String) : BasePaymentMethod(), Refundable {
    override val methodName = "UPI"
    override val methodIcon = "📱"

    override fun processPayment(amount: Double, orderId: String): PaymentResult {
        val txId = "UPI-${System.currentTimeMillis()}"
        println("$methodIcon Processing ${formatAmount(amount)} transfer via $upiId")
        return PaymentResult(true, txId, amount)
    }

    override fun processRefund(transactionId: String, amount: Double): PaymentResult {
        return PaymentResult(true, "UPI-REF-$transactionId", amount)
    }
}

// POLYMORPHIC PAYMENT PROCESSOR:
class PaymentProcessor {
    fun executeCheckout(paymentMethod: PaymentMethod, amount: Double, orderId: String) {
        println("\n=== EXECUTING CHECKOUT ===")
        val result = paymentMethod.processPayment(amount, orderId)
        if (result.success) {
            println("✅ Success! Transaction ID: ${result.transactionId}")
        }
    }
}

fun main() {
    val processor = PaymentProcessor()
    val card = CreditCard("4532015112830366")
    val upi = UPI("rohit@paytm")

    processor.executeCheckout(card, 1499.0, "ORD-101")
    processor.executeCheckout(upi, 499.0, "ORD-102")
}
```

---

## 📊 Complete Summary Cheat Sheet

| Concept | Syntax Example | Key Distinction |
| :--- | :--- | :--- |
| **Inheritance** | `class Sub : Parent()` | Reuses code via `IS-A` relationship. |
| **`open` Keyword** | `open class Parent` | Enables class/method to be inherited or overridden. |
| **`override`** | `override fun draw()` | Replaces parent's method with custom logic. |
| **`super`** | `super.draw()` | References parent implementation from child. |
| **Abstract Class** | `abstract class Shape` | Cannot instantiate; contains concrete + abstract methods. |
| **Interface** | `interface Clickable` | Pure contract; supports **multiple inheritance**. |
| **Polymorphism** | `val a: Animal = Dog()` | Single interface invocation triggers subclass-specific logic. |

---

## ❓ 5 Quiz Questions

### 🎯 Question 1: Inheritance & `open` Keyword
- **Part A:** Categorize as `abstract`, `open`, or `final`: `DatabaseConnection`, `BaseRepository`, `MathUtils`, `Animal`, `Button`.
- **Part B:** Identify compile errors in trying to extend a non-`open` class or overriding a non-`open` method.
- **Part C:** Explain what `open val primaryColor: String` enables on properties.

---

### 🎨 Question 2: Abstract Classes vs Interfaces
- **Scenario A:** Designing a notification system (`PushNotification`, `EmailNotification`, `SMSNotification`). Choose `abstract class` vs `interface` and write skeleton.
- **Scenario B:** Adding drag gestures to unrelated UI views (`Button`, `ImageView`). Choose `abstract class` vs `interface`.
- **Scenario C:** Write a scenario combining both an `abstract class` and `interface` on the same class hierarchy.

---

### 📜 Question 3: Interface Implementation & Conflicts
Given interfaces `Clickable`, `Animatable`, `Shareable`:
- **a)** Write class `Poster` implementing all 3 interfaces.
- **b)** Override interface default method `erase()` and invoke `super.erase()`.
- **c)** How do you resolve method signature conflicts when 2 interfaces share an identical default method name? (e.g., `super<Logger>.log()`).

---

### 🎭 Question 4: Polymorphism in Action
Given abstract `Notification(title, message, priority)` and subclasses `PushNotification`, `EmailNotification`, `SMSNotification`:
- **a)** Write polymorphic function `sendAllNotifications(list: List<Notification>): Int`.
- **b)** Write function `getHighPriorityNotifications()` filtering `priority == 3` sorted by `channelName`.
- **c)** Trace output when calling `formatForDisplay()` polymorphically.

---

### 🚀 Question 5: Build a Multi-Factor Auth System Architecture
Design a complete authentication system:
- **Interfaces:** `Authenticator` (`authenticate()`, `isAvailable()`), `TwoFactorCapable` (`enable2FA()`).
- **Abstract Class:** `BaseAuthenticator` with attempt counting and lockout checking.
- **Concrete Classes:** `EmailPasswordAuth`, `GoogleAuth`, `BiometricAuth`, `PhoneOTPAuth`.
- **Auth Manager:** Polymorphic `AuthManager` handling authentication without `if/else` branching.