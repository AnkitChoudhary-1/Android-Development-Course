/**
 * 🛡️ Safe User Profile & API Response Sanitizer
 * Demonstrates: Nullable types (?), Safe Calls (?.), Elvis (?:), Safe Casts (as?), let blocks.
 */

// Raw Backend Models with Nullable Properties
class RawAddress(
    val street: String?,
    val city: String?,
    val zipCode: String?,
    val country: String?
)

class RawPaymentMethod(
    val type: String?,
    val last4: String?,
    val expiryMonth: Int?,
    val expiryYear: Int?
)

class RawUserProfile(
    val id: Int?,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val phone: String?,
    val address: RawAddress?,
    val paymentMethods: List<RawPaymentMethod>?
)

// Clean, Guaranteed Sanitized Domain Models
class SanitizedProfile(
    val id: Int,
    val displayName: String,
    val initials: String,
    val email: String,
    val phone: String,
    val formattedAddress: String,
    val defaultPayment: String,
    val isExpressReady: Boolean,
    val missingRequirements: List<String>
)

// Sanitizer Pipeline Engine
object ProfileSanitizer {

    fun sanitize(raw: RawUserProfile?): SanitizedProfile {
        // Safe ID with fallback
        val id = raw?.id ?: -1

        // Display Name resolution
        val fName = raw?.firstName?.trim()?.takeIf { it.isNotEmpty() }
        val lName = raw?.lastName?.trim()?.takeIf { it.isNotEmpty() }
        val displayName = when {
            fName != null && lName != null -> "$fName $lName"
            fName != null -> fName
            lName != null -> lName
            else -> "Guest User"
        }

        // Initials resolution
        val initials = when {
            fName != null && lName != null -> "${fName.first().uppercase()}${lName.first().uppercase()}"
            fName != null -> "${fName.first().uppercase()}"
            lName != null -> "${lName.first().uppercase()}"
            else -> "GU"
        }

        // Safe Email resolution
        val email = raw?.email?.trim()?.lowercase()?.takeIf { it.contains("@") } ?: "no-email@domain.com"

        // Safe Phone
        val phone = raw?.phone?.trim()?.takeIf { it.isNotEmpty() } ?: "N/A"

        // Safe Address resolution with nested null safety
        val addr = raw?.address
        val formattedAddress = if (addr != null && addr.city != null) {
            val parts = listOfNotNull(
                addr.street?.trim()?.takeIf { it.isNotEmpty() },
                addr.city.trim(),
                addr.zipCode?.trim()?.takeIf { it.isNotEmpty() },
                addr.country?.trim()?.takeIf { it.isNotEmpty() }
            )
            parts.joinToString(", ")
        } else {
            "Address Not Provided"
        }

        // Safe Payment Method resolution
        val primaryPayment = raw?.paymentMethods?.firstOrNull { it.type != null && it.last4 != null }
        val defaultPayment = primaryPayment?.let {
            val exp = if (it.expiryMonth != null && it.expiryYear != null) "exp: ${it.expiryMonth}/${it.expiryYear % 100}" else "no exp"
            "${it.type?.uppercase()} (ending in ${it.last4}, $exp)"
        } ?: "Cash on Delivery"

        // Express checkout readiness check
        val missing = mutableListOf<String>()
        if (raw?.email.isNullOrBlank() || !raw?.email!!.contains("@")) missing.add("valid email")
        if (addr?.city == null || addr.zipCode == null) missing.add("complete shipping address")
        if (primaryPayment == null) missing.add("valid payment card")

        val isExpressReady = missing.isEmpty()

        return SanitizedProfile(
            id = id,
            displayName = displayName,
            initials = initials,
            email = email,
            phone = phone,
            formattedAddress = formattedAddress,
            defaultPayment = defaultPayment,
            isExpressReady = isExpressReady,
            missingRequirements = missing
        )
    }
}

fun printProfileSummary(title: String, profile: SanitizedProfile) {
    println("[$title]")
    println("Sanitized Profile:")
    println("  - User ID       : ${if (profile.id > 0) profile.id else "Unassigned"}")
    println("  - Display Name  : ${profile.displayName}")
    println("  - Initials      : ${profile.initials}")
    println("  - Email         : ${profile.email}")
    println("  - Phone         : ${profile.phone}")
    println("  - Formatted Addr: ${profile.formattedAddress}")
    println("  - Default Pay   : ${profile.defaultPayment}")

    if (profile.isExpressReady) {
        println("  - Express Ready?: ✅ Yes (Eligible for 1-Click Checkout)")
    } else {
        println("  - Express Ready?: ❌ No (Missing: ${profile.missingRequirements.joinToString(", ")})")
    }
    println()
}

fun main() {
    println("==================================================")
    println("           🛡️ SAFE PROFILE SANITIZER             ")
    println("==================================================")

    // User 1: Missing lots of fields
    val incompleteUser = RawUserProfile(
        id = 101,
        firstName = "Rohit",
        lastName = null,
        email = "  ROHIT@GMAIL.COM  ",
        phone = null,
        address = null,
        paymentMethods = null
    )

    val sanitized1 = ProfileSanitizer.sanitize(incompleteUser)
    printProfileSummary("RAW USER 1: Incomplete Payload", sanitized1)

    // User 2: Fully populated
    val completeUser = RawUserProfile(
        id = 102,
        firstName = "Priya",
        lastName = "Sharma",
        email = "priya.sharma@example.com",
        phone = "+91 9876543210",
        address = RawAddress(
            street = "42 MG Road",
            city = "Bangalore",
            zipCode = "560001",
            country = "India"
        ),
        paymentMethods = listOf(
            RawPaymentMethod(type = "VISA", last4 = "4242", expiryMonth = 12, expiryYear = 2028)
        )
    )

    val sanitized2 = ProfileSanitizer.sanitize(completeUser)
    printProfileSummary("RAW USER 2: Full Complete Payload", sanitized2)

    // User 3: Null Root Object check
    val sanitizedNull = ProfileSanitizer.sanitize(null)
    printProfileSummary("RAW USER 3: Completely Null Payload", sanitizedNull)

    println("==================================================")
}
