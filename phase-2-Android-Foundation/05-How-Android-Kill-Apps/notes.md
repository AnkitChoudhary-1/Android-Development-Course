# ☠️ How and Why Android Kills Apps — Complete Guide

---

## 🤔 Part 1: Why Does Android Kill Apps at All?

### ⚠️ The Fundamental Problem

```text
Your phone is NOT a computer. It has severe constraints:

DESKTOP/LAPTOP:
  RAM: 8-32 GB
  Storage: 512 GB - 2 TB
  Power: Plugged into wall outlet
  CPU: Powerful, active cooling
  Users typically run: 3-5 apps at a time

ANDROID PHONE:
  RAM: 2-8 GB (shared among EVERYTHING)
  Storage: 32-256 GB
  Power: Small battery (3000-5000 mAh)
  CPU: Mobile processor, passive cooling
  Users typically run: 20-50 apps "simultaneously"

THE RAM PROBLEM IN NUMBERS:

  Total RAM on a typical phone:          6 GB (6144 MB)
  ─────────────────────────────────────────────────
  Android OS itself:                    -1500 MB (always running)
  System services (WiFi, Bluetooth, etc): -500 MB
  Phone/Telephony service:               -200 MB
  GPU / Display:                         -300 MB
  ─────────────────────────────────────────────────
  Available for apps:                   ~3600 MB

  Now imagine the user has these apps open:
    Chrome (5 tabs):                     -800 MB
    Instagram:                           -400 MB
    WhatsApp:                            -300 MB
    YouTube:                             -500 MB
    Spotify:                             -200 MB
    Maps:                                -350 MB
    Camera:                              -250 MB
    Your FoodApp:                        -300 MB
    Gmail:                               -200 MB
    Twitter:                             -250 MB
  ─────────────────────────────────────────────────
  Total requested:                      -3550 MB

  That is ALMOST all available RAM!
  What happens when the user opens ONE MORE app?
  There is NO RAM left. Something MUST give.
```

---

### 💡 The Solution: Kill Apps

```text
Android's philosophy is:
  "The user's CURRENT experience matters most."

  If the user is actively using App A,
  but App B is hidden in the background doing nothing,
  Android will KILL App B to free RAM for App A.

  This is not a bug. This is a FEATURE.
  It is how Android keeps the phone running smoothly
  despite having limited resources.

ANALOGY: A small parking lot

  Your phone's RAM is a parking lot with 20 spaces.
  Each app is a car.
  
  When the lot is full and a VIP car arrives
  (the app the user is currently using),
  the parking attendant (Android OS) must
  TOW AWAY the car that has been parked the longest
  and is least likely to be needed soon.
  
  When the owner of the towed car comes back,
  they find their car gone. They must park again
  (app restarts from scratch).
  
  The attendant didn't do anything wrong.
  The lot is simply too small for all the cars.
```

---

---

## 🔪 Part 2: The Low Memory Killer (LMK)

### 🤔 What is the LMK?

```text
The LOW MEMORY KILLER (LMK) is a component of the
Linux Kernel (remember: Android is built on Linux!)
that monitors RAM usage and kills processes when
memory gets critically low.

HOW IT WORKS:

1. The LMK constantly monitors available RAM.

2. When RAM drops below certain THRESHOLDS,
   the LMK activates and starts killing processes.

3. The LMK does NOT kill randomly.
   It follows a PRIORITY SYSTEM to decide
   which process to kill first.

4. It kills the LOWEST priority process first.
   If that is not enough, it kills the next lowest.
   It continues until enough RAM is freed.

THRESHOLD EXAMPLE (simplified):

  Available RAM > 1000 MB → Everything is fine. No killing.
  Available RAM < 800 MB  → Start killing EMPTY processes.
  Available RAM < 500 MB  → Start killing BACKGROUND processes.
  Available RAM < 300 MB  → Start killing SERVICE processes.
  Available RAM < 150 MB  → Start killing VISIBLE processes.
  Available RAM < 50 MB   → Start killing FOREGROUND processes.
                              (This almost never happens —
                               the phone would show a warning first)
```

> **🔴 The LMK is Ruthless:**
> - Does NOT ask your app's permission
> - Does NOT call `onDestroy()` reliably
> - Does NOT wait for your app to save data
> - Simply terminates the Linux process
> - Your app's RAM is **instantly** reclaimed

---

### 📊 The `oom_adj` Score

```text
Behind the scenes, every process has an "oom_adj" score
(Out Of Memory Adjustment). This score determines
how likely the process is to be killed.

  LOWER score = MORE important = killed LAST
  HIGHER score = LESS important = killed FIRST

  Score Range (simplified):
    -900 to -800  → System critical (never killed)
    -700           → Foreground app (almost never killed)
    -600           → Visible app
    -400           → Service
    0 to 400       → Background app
    500 to 999     → Empty/cached app (killed FIRST)

  The LMK scans all processes, finds the one with
  the HIGHEST oom_adj score, and kills it.
```

---

---

## 📊 Part 3: Process Priority Levels in Android

### 🏆 The Five Priority Levels

```text
Android categorizes every running process into one of
FIVE priority levels. The LMK uses these to decide
what to kill first.

┌──────────────────────────────────────────────────────────────┐
│          ANDROID PROCESS PRIORITY LEVELS                     │
│          (From most important to least important)            │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  1. FOREGROUND PROCESS (Highest Priority)                   │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━                     │
│  What: The app the user is ACTIVELY using RIGHT NOW.        │
│  Conditions (ANY of these makes it foreground):             │
│    ✅ Activity is in onResume() state (user interacting)    │
│    ✅ Service is running in foreground (e.g., music player  │
│       with a notification)                                   │
│    ✅ BroadcastReceiver is currently executing              │
│    ✅ Service is bound to a foreground Activity              │
│  Killed: Almost NEVER. Only under extreme memory pressure.  │
│  Example: The WhatsApp chat you are typing in right now.    │
│                                                              │
│  2. VISIBLE PROCESS                                         │
│  ━━━━━━━━━━━━━━━━━━━━                                        │
│  What: The app is VISIBLE but not in the foreground.        │
│  Conditions:                                                │
│    ✅ Activity is in onPause() state (partially covered     │
│       by a dialog or transparent Activity)                   │
│    ✅ Service is bound to a visible Activity                 │
│  Killed: Only when memory is very low.                      │
│  Example: Your app is behind a phone call screen.           │
│           User can still see your app behind the call UI.    │
│                                                              │
│  3. SERVICE PROCESS                                         │
│  ━━━━━━━━━━━━━━━━━━━                                         │
│  What: Running a background service that the user is        │
│        aware of but not directly looking at.                 │
│  Conditions:                                                │
│    ✅ Started service running (startService)                 │
│    ✅ Not bound to any visible Activity                      │
│  Killed: When memory is needed for foreground/visible apps. │
│  Example: Spotify playing music in the background.          │
│           Download manager downloading a file.              │
│                                                              │
│  4. BACKGROUND PROCESS (also called CACHED)                 │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━                        │
│  What: App is completely hidden. User is not interacting.   │
│  Conditions:                                                │
│    ✅ All Activities are in onStop() state                   │
│    ✅ No services running                                    │
│    ✅ App is in the "recent apps" list but not visible      │
│  Killed: FIRST when memory is needed. These are the         │
│          primary targets of the LMK.                         │
│  Example: Instagram that you used 10 minutes ago and        │
│           then pressed Home. It is in recent apps but       │
│           doing nothing.                                    │
│                                                              │
│  5. EMPTY PROCESS (Lowest Priority)                         │
│  ━━━━━━━━━━━━━━━━━━━━                                        │
│  What: Process has no active components at all.             │
│  Conditions:                                                │
│    ✅ No Activities, no Services, no BroadcastReceivers     │
│    ✅ Process is kept alive only for caching purposes       │
│  Killed: IMMEDIATELY when memory is needed.                 │
│  Example: An app you opened and closed hours ago.           │
│           The process is still in RAM "just in case"        │
│           you open it again (for faster warm start).        │
│                                                              │
└──────────────────────────────────────────────────────────────┘

KILL ORDER (who dies first):
  EMPTY → BACKGROUND → SERVICE → VISIBLE → FOREGROUND
  (first)                                          (last)
```

---

### 🔄 Priority Changes in Real Time

```text
Your app's priority CHANGES as the user interacts with it:

SCENARIO: User opens your FoodApp, browses, then leaves.

TIME    USER ACTION           YOUR APP'S PRIORITY
━━━━    ━━━━━━━━━━━           ━━━━━━━━━━━━━━━━━━━
0s      User taps icon        FOREGROUND (onResume)
30s     User browsing menu    FOREGROUND (onResume)
60s     Phone call comes in   VISIBLE (onPause — behind call screen)
90s     User answers call     BACKGROUND (onStop — call is full screen)
120s    Call ends, user       FOREGROUND again (onRestart → onResume)
        returns to app
150s    User presses Home     BACKGROUND (onStop — hidden)
180s    User opens 5 games    EMPTY (OS downgrades your process)
210s    RAM critically low    💀 KILLED by LMK!
240s    User reopens your app NEW PROCESS created (cold start)

Notice how the priority DROPPED over time:
  FOREGROUND → VISIBLE → BACKGROUND → EMPTY → KILLED

This is why your app can be killed even though the user
did not explicitly close it!
```

---

---

## 💾 Part 4: What Happens to Your App's Data When It Is Killed

### ⚡ The Critical Distinction

```text
When Android kills your app's process, what happens to data?

DATA STORED IN RAM (Volatile — LOST when killed):
  ❌ All variables and objects in memory
  ❌ All Activity instances and their state
  ❌ All View states (text in EditText, scroll position)
  ❌ All data in your ViewModel (if not saved)
  ❌ All running threads and coroutines
  ❌ All network connections
  ❌ All unsaved form data
  ❌ All temporary caches in memory

DATA STORED ON DISK (Persistent — SURVIVES killing):
  ✅ Files saved to internal/external storage
  ✅ SharedPreferences (saved to XML files)
  ✅ Room/SQLite database (saved to .db files)
  ✅ Data saved via onSaveInstanceState() (saved to disk by OS)
  ✅ Files downloaded to the device

THIS IS WHY:
  When your app is killed and reopened, it looks like
  it "restarted from scratch." The RAM was wiped clean.
  Only data saved to disk survives.
```

---

### 🖼️ Visual Representation

```text
BEFORE KILL (app running in background):

  RAM (Process PID: 18472):
  ┌──────────────────────────────────────┐
  │  Activity instance (MainActivity)    │ ← Will be LOST
  │  ViewModel with restaurant list      │ ← Will be LOST
  │  User's form input: "Rohit Kumar"    │ ← Will be LOST
  │  Scroll position: item #42           │ ← Will be LOST
  │  Network connection to API           │ ← Will be LOST
  │  Bitmap cache (50 MB of images)      │ ← Will be LOST
  └──────────────────────────────────────┘

  DISK (Persistent storage):
  ┌──────────────────────────────────────┐
  │  SharedPreferences: login_token      │ ← Will SURVIVE
  │  Room Database: order history        │ ← Will SURVIVE
  │  Saved files: downloaded images      │ ← Will SURVIVE
  │  SavedInstanceState bundle           │ ← Will SURVIVE (if saved!)
  └──────────────────────────────────────┘

AFTER KILL (process terminated):

  RAM (Process PID: 18472):
  ┌──────────────────────────────────────┐
  │  (empty — process no longer exists)  │
  └──────────────────────────────────────┘

  DISK (unchanged):
  ┌──────────────────────────────────────┐
  │  SharedPreferences: login_token      │ ← Still here!
  │  Room Database: order history        │ ← Still here!
  │  Saved files: downloaded images      │ ← Still here!
  │  SavedInstanceState bundle           │ ← Still here!
  └──────────────────────────────────────┘

WHEN USER REOPENS THE APP:
  New process created (PID: 21034)
  RAM is fresh and empty
  App must reload data from DISK into RAM
  If you saved state → restore from SavedInstanceState
  If you did NOT save → everything is gone, start from scratch
```

---

---

## 💀 Part 5: Three Types of App Death

### 🔴 Type 1: Killed by System (Low Memory)

```text
WHAT HAPPENS:
  - The LMK decides your process is expendable
  - The Linux kernel sends SIGKILL to your process
  - Your process is terminated INSTANTLY
  - onDestroy() is NOT reliably called
  - onPause() and onStop() WERE called earlier
    (when the app went to background)

WHY IT HAPPENS:
  - Another app needs more RAM
  - Your app was in BACKGROUND or EMPTY priority
  - Phone is running low on memory

USER EXPERIENCE:
  - User reopens app from recent apps
  - App appears to "restart from scratch"
  - If you saved state → data is restored seamlessly
  - If you did NOT save → user loses all progress
  - User is confused: "I was just here, where did my data go?"

TECHNICAL DETAILS:
  - Process is gone but Activity back stack is preserved
    (Android saves the back stack to disk before killing)
  - When user returns, Android recreates the Activity
    and passes the saved instance state bundle
  - onCreate() receives a NON-NULL savedInstanceState
```

---

### 🟡 Type 2: Killed by User (Swipe from Recents)

```text
WHAT HAPPENS:
  - User opens the recent apps view
  - User swipes your app away (left/right or up)
  - Android kills your process AND clears the back stack
  - All Activities in your task are destroyed

WHY IT HAPPENS:
  - User explicitly wants to close your app
  - User is "cleaning up" their recent apps

USER EXPERIENCE:
  - When user reopens the app, it starts COMPLETELY fresh
  - Launcher Activity is shown (not where they left off)
  - savedInstanceState is NULL (back stack was cleared)
  - This is like a fresh cold start

KEY DIFFERENCE FROM SYSTEM KILL:
  System kill: Back stack preserved → user returns to same screen
  User swipe:  Back stack cleared → user starts from home screen

  System kill: savedInstanceState is NOT null (state preserved)
  User swipe:  savedInstanceState IS null (everything cleared)
```

---

### 🔵 Type 3: App Crashed (Exception)

```text
WHAT HAPPENS:
  - Your code throws an unhandled exception
  - The Android Runtime (ART) catches it
  - The process is terminated
  - A crash dialog may appear: "FoodApp has stopped"
  - Crash is logged to Logcat and (if configured) Crashlytics

WHY IT HAPPENS:
  - NullPointerException (accessing null object)
  - IndexOutOfBoundsException (accessing invalid list index)
  - NetworkOnMainThreadException (network call on UI thread)
  - OutOfMemoryError (too many bitmaps loaded)
  - IllegalStateException (wrong lifecycle state)

USER EXPERIENCE:
  - App suddenly closes (or shows crash dialog)
  - User is frustrated
  - When reopened, app starts fresh
  - User may leave a 1-star review

TECHNICAL DETAILS:
  - The crash kills the entire process, not just one Activity
  - All Activities in the back stack are destroyed
  - Similar to user swipe — fresh start on reopen
  - The crash stack trace is available in Logcat:
    adb logcat | grep "FATAL EXCEPTION"
```

---

### 📊 Comparison Table

```text
┌────────────────────┬───────────────────┬──────────────────┬──────────────────┐
│                    │ SYSTEM KILL       │ USER SWIPE       │ CRASH            │
│                    │ (Low Memory)      │ (Recent Apps)    │ (Exception)      │
├────────────────────┼───────────────────┼──────────────────┼──────────────────┤
│ Who initiates?     │ Android OS (LMK)  │ User             │ Your buggy code  │
├────────────────────┼───────────────────┼──────────────────┼──────────────────┤
│ onDestroy() called?│ NOT reliably      │ Maybe            │ NO               │
├────────────────────┼───────────────────┼──────────────────┼──────────────────┤
│ Back stack         │ PRESERVED         │ CLEARED          │ CLEARED          │
│ preserved?         │                   │                  │                  │
├────────────────────┼───────────────────┼──────────────────┼──────────────────┤
│ savedInstanceState │ NOT null          │ NULL             │ NULL             │
│ on reopen?         │ (state saved)     │ (fresh start)    │ (fresh start)    │
├────────────────────┼───────────────────┼──────────────────┼──────────────────┤
│ User returns to    │ Same screen       │ Launcher screen  │ Launcher screen  │
│ which screen?      │                   │                  │                  │
├────────────────────┼───────────────────┼──────────────────┼──────────────────┤
│ Can you prevent?   │ NO (OS decision)  │ NO (user choice) │ YES (fix bugs!)  │
├────────────────────┼───────────────────┼──────────────────┼──────────────────┤
│ Can you prepare?   │ YES (save state!) │ N/A              │ YES (try-catch,  │
│                    │                   │                  │ error handling)  │
├────────────────────┼───────────────────┼──────────────────┼──────────────────┤
│ User frustration   │ Medium            │ Low              │ HIGH             │
│ level              │ (if data lost)    │ (expected)       │ (unexpected)     │
└────────────────────┴───────────────────┴──────────────────┴──────────────────┘
```

---

---

## 🔄 Part 6: Why Apps "Restart from Scratch" When Reopened

### 🔗 Connecting to the Activity Lifecycle

```text
When your app is killed and the user reopens it,
Android does NOT just resume where it left off.
It must RECREATE everything from scratch.

WHY?
  Because the process is DEAD. All RAM is freed.
  There is no Activity instance, no ViewModel,
  no variables, no objects. Everything is gone.
  Android must build it all again.

WHAT ANDROID DOES ON REOPEN (after system kill):

  1. Creates a NEW process (new PID)
  2. Creates a NEW Application instance
  3. Looks at the saved back stack (preserved on disk)
  4. Recreates the TOP Activity from the back stack
  5. Calls onCreate() with the saved instance state
  6. Calls onStart() and onResume()
  7. The screen appears

  The user SEES the same screen they were on before.
  But it is a COMPLETELY NEW Activity instance.
  All the old objects are gone.

THE DANGER:
  If you did not save the user's data before the kill,
  the recreated Activity will be EMPTY.
  The user sees a blank screen where their data used to be.
  This is the #1 source of user frustration on Android.
```

---

### 🔧 The "Don't Keep Activities" Developer Option

```text
You can SIMULATE process death on your phone for testing:

  Settings → Developer Options → "Don't keep activities"

  When enabled:
    Every Activity is destroyed as soon as the user leaves it.
    This simulates what happens when the OS kills your app.
    
  Use this to test:
    - Does my form data survive rotation?
    - Does my scroll position survive navigation?
    - Does my app crash when Activities are recreated?
```

> **⚠️ Warning:** Turn this **OFF** after testing! It makes your phone unusable for daily use.

---

---

## 🛟 Part 7: `onSaveInstanceState()` — Your Lifeline

### 💡 What is `onSaveInstanceState()`?

```text
onSaveInstanceState() is a lifecycle callback that Android
calls BEFORE your Activity might be destroyed.

It gives you a Bundle (a key-value map) to store your data.
Android saves this Bundle to DISK automatically.
When the Activity is recreated, Android passes this Bundle
back to you in onCreate() and onRestoreInstanceState().

THIS IS YOUR ONLY GUARANTEED CHANCE to save UI state
before the OS kills your process.
```

---

### 🏗️ How It Works — Complete Code

```kotlin
class OrderFormActivity : AppCompatActivity() {

    // Views
    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etAddress: EditText
    private lateinit var etSpecialInstructions: EditText
    private lateinit var tvOrderTotal: TextView

    // Data that needs to survive process death
    private var orderTotal: Double = 0.0
    private var selectedPaymentMethod: String = "UPI"
    private var isCouponApplied: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_form)

        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etAddress = findViewById(R.id.etAddress)
        etSpecialInstructions = findViewById(R.id.etSpecialInstructions)
        tvOrderTotal = findViewById(R.id.tvOrderTotal)

        // ─── RESTORE STATE ────────────────────────────────
        // savedInstanceState is NOT null when Activity is being
        // RECREATED after process death or rotation.
        // It IS null on first launch.
        if (savedInstanceState != null) {
            // Restore our custom data:
            orderTotal = savedInstanceState.getDouble("ORDER_TOTAL", 0.0)
            selectedPaymentMethod = savedInstanceState.getString("PAYMENT_METHOD", "UPI")
            isCouponApplied = savedInstanceState.getBoolean("COUPON_APPLIED", false)

            Log.d("OrderForm", "State restored! Total: $orderTotal")
        }

        // Update UI with restored data:
        tvOrderTotal.text = "Total: ₹$orderTotal"
    }

    // ─── SAVE STATE ───────────────────────────────────────
    // Called BEFORE the Activity might be destroyed.
    // This is your chance to save everything!
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Save custom data to the Bundle:
        outState.putDouble("ORDER_TOTAL", orderTotal)
        outState.putString("PAYMENT_METHOD", selectedPaymentMethod)
        outState.putBoolean("COUPON_APPLIED", isCouponApplied)

        Log.d("OrderForm", "State saved! Total: $orderTotal")

        // NOTE: EditText text, scroll positions, and checkbox
        // states are saved AUTOMATICALLY by Android IF the views
        // have android:id attributes in the XML layout.
        // You do NOT need to manually save EditText content.
        // But you DO need to save your own variables.
    }

    // ─── ALTERNATIVE RESTORE POINT ────────────────────────
    // Called AFTER onStart(), only when state was saved.
    // You can restore here instead of in onCreate().
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        // Same data available here as in onCreate():
        orderTotal = savedInstanceState.getDouble("ORDER_TOTAL", 0.0)
        // This is called AFTER onStart(), so views are fully ready.
    }
}
```

---

### ✅ What Android Saves Automatically

```text
GOOD NEWS: Android automatically saves and restores
some UI state for you, IF your views have IDs:

AUTO-SAVED (if view has android:id):
  ✅ EditText text content and cursor position
  ✅ CheckBox checked state
  ✅ RadioButton selected state
  ✅ Switch on/off state
  ✅ ScrollView scroll position
  ✅ RecyclerView scroll position (with LayoutManager)
  ✅ Spinner selected item
  ✅ RatingBar rating

NOT AUTO-SAVED (you must save manually):
  ❌ Your custom variables (orderTotal, selectedPaymentMethod)
  ❌ ViewModel data (use SavedStateHandle instead)
  ❌ Data loaded from network
  ❌ Bitmaps and images
  ❌ Database query results
  ❌ RecyclerView item data (the adapter data)
  ❌ Custom View state (unless you implement it)

WHY SOME THINGS ARE AUTO-SAVED:
  Android's View hierarchy has a built-in mechanism.
  Each View with an ID saves its state to a SparseArray.
  When the Activity is recreated, each View restores
  its own state from this array.
  This is why android:id is REQUIRED for state saving!
```

---

### ⚠️ Limitations of `onSaveInstanceState()`

```text
CRITICAL LIMITATIONS:

1. SIZE LIMIT: The Bundle has a limit of ~500 KB to 1 MB.
   If you try to save too much data, you get:
   "TransactionTooLargeException" → CRASH!
   
   ❌ DO NOT save: Large lists, bitmaps, entire database results
   ✅ DO save: Small values — IDs, strings, booleans, positions

2. NOT FOR PERSISTENT DATA:
   onSaveInstanceState() is for TEMPORARY UI state.
   It is NOT a replacement for a database.
   The saved state can be cleared at any time.
   
   ❌ DO NOT save: User's order, payment info, messages
   ✅ DO save: Scroll position, selected tab, form draft

3. TIMING IS UNPREDICTABLE:
   onSaveInstanceState() is called AFTER onStop()
   on Android 9+ (API 28+), but BEFORE onStop()
   on older versions. Do not rely on exact timing.

4. NOT CALLED ON BACK PRESS:
   When the user presses Back, onSaveInstanceState()
   is NOT called because the Activity is intentionally
   being destroyed (not killed by the system).
```

---

---

## 🏆 Part 8: Best Practices to Handle Process Death

### ✅ Practice 1: Save Early, Save Often

```kotlin
// ❌ BAD: Only saving in onSaveInstanceState()
// If the app crashes before onSaveInstanceState() is called,
// all data is lost.

// ✅ GOOD: Save critical data to persistent storage immediately
fun onOrderPlaced(order: Order) {
    // Save to database IMMEDIATELY (survives any kill):
    database.orderDao().insertOrder(order)
    
    // Also save to ViewModel (survives rotation):
    viewModel.currentOrder = order
    
    // onSaveInstanceState() will handle the rest
}
```

---

### ✅ Practice 2: Use ViewModel with `SavedStateHandle`

```kotlin
// ViewModel survives rotation but NOT process death.
// SavedStateHandle bridges this gap.

class OrderViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // This survives BOTH rotation AND process death!
    var orderTotal: Double
        get() = savedStateHandle.get<Double>("ORDER_TOTAL") ?: 0.0
        set(value) { savedStateHandle["ORDER_TOTAL"] = value }

    var selectedPayment: String
        get() = savedStateHandle.get<String>("PAYMENT") ?: "UPI"
        set(value) { savedStateHandle["PAYMENT"] = value }

    // SavedStateHandle automatically saves to the
    // onSaveInstanceState() Bundle behind the scenes.
    // You get ViewModel convenience + process death survival!
}
```

---

### ✅ Practice 3: Use Persistent Storage for Important Data

```kotlin
// For data that MUST survive anything:
// Use Room Database or SharedPreferences

class UserRepository(private val database: AppDatabase) {

    // Save user's draft order to database:
    suspend fun saveDraftOrder(order: Order) {
        database.orderDao().insertDraft(order)
    }

    // Restore draft when app reopens:
    suspend fun getDraftOrder(): Order? {
        return database.orderDao().getLatestDraft()
    }
}

// In your Activity/Fragment:
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    lifecycleScope.launch {
        // Check for saved draft (survives process death!)
        val draft = userRepository.getDraftOrder()
        if (draft != null) {
            restoreFormFromDraft(draft)
            showSnackbar("Draft order restored!")
        }
    }
}
```

---

### ✅ Practice 4: Handle the "Recreated" State Gracefully

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    if (savedInstanceState != null) {
        // Activity was RECREATED (process death or rotation)
        // Restore UI state, show a subtle indicator
        Log.d("App", "Activity recreated — restoring state")
        
        // Do NOT re-fetch data from network if you have
        // cached data. Use the cached version first.
        loadCachedData()
    } else {
        // Activity is FRESH (first launch)
        Log.d("App", "Fresh launch — loading from network")
        fetchFreshData()
    }
}
```

---

### ✅ Practice 5: Test for Process Death

```kotlin
// HOW TO TEST:

// Method 1: Developer Options
// Settings → Developer Options → "Don't keep activities"
// Navigate away from your Activity → come back
// Does your data survive?

// Method 2: Background Process Limit
// Settings → Developer Options → "Background process limit"
// Set to "No background processes"
// Open your app → switch to another app → switch back
// Was your app killed and recreated?

// Method 3: adb command (most reliable)
// 1. Open your app and navigate to the screen you want to test
// 2. Press Home (put app in background)
// 3. Run this command in terminal:
//    adb shell am kill com.rohit.foodapp
// 4. Reopen your app from recent apps
// 5. Check if your data is restored correctly

// Method 4: Android Studio Logcat
// Watch for these log messages:
// "Process com.rohit.foodapp (pid 18472) has died"
// This confirms the process was killed.
```

---

---

## 🍕 Part 9: Real Example — The Form Filling Scenario

### 📖 The Complete Scenario

```text
SCENARIO:
  1. User opens FoodApp and starts filling a long order form
  2. User types their name, phone, address, special instructions
  3. User selects payment method and applies a coupon
  4. A phone call comes in
  5. User answers the call (your app goes to background)
  6. While on the call, user opens Maps to check an address
  7. Maps uses a lot of RAM
  8. Android's LMK kills your FoodApp process to free RAM
  9. User finishes the call and reopens FoodApp
  10. WHAT DOES THE USER SEE?
```

---

### ❌ Without Proper State Saving (BAD)

```kotlin
// ❌ BAD IMPLEMENTATION:

class OrderFormActivity : AppCompatActivity() {

    private var orderTotal = 450.0
    private var couponCode = ""
    private var paymentMethod = "UPI"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_form)
        
        // No state restoration code!
        // When Activity is recreated after process death:
        // - orderTotal resets to 450.0 (user's custom total is gone)
        // - couponCode resets to "" (user's coupon is gone)
        // - paymentMethod resets to "UPI" (user's choice is gone)
        // - EditText fields MIGHT retain text (auto-saved by Android)
        // - But all your custom logic state is LOST
    }
}
```

```text
USER EXPERIENCE:
  User reopens the app after the phone call.
  They see the form but:
    ❌ Their coupon is gone
    ❌ Their payment method reset to default
    ❌ The order total is wrong
    ❌ They have to start over
    ❌ User is frustrated → 1-star review → uninstalls app
```

---

### ✅ With Proper State Saving (GOOD)

```kotlin
// ✅ GOOD IMPLEMENTATION:

class OrderFormActivity : AppCompatActivity() {

    // UI Views
    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etAddress: EditText
    private lateinit var etInstructions: EditText
    private lateinit var tvTotal: TextView
    private lateinit var rgPayment: RadioGroup
    private lateinit var etCoupon: EditText
    private lateinit var btnApplyCoupon: Button

    // State that needs saving
    private var orderTotal: Double = 0.0
    private var originalTotal: Double = 0.0
    private var couponDiscount: Double = 0.0
    private var isCouponApplied: Boolean = false
    private var selectedPaymentMethod: String = "UPI"
    private var restaurantId: Int = 0
    private var cartItemCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_form)

        // Initialize views
        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etAddress = findViewById(R.id.etAddress)
        etInstructions = findViewById(R.id.etInstructions)
        tvTotal = findViewById(R.id.tvTotal)
        rgPayment = findViewById(R.id.rgPayment)
        etCoupon = findViewById(R.id.etCoupon)

        // Get data from Intent (from previous screen)
        restaurantId = intent.getIntExtra("RESTAURANT_ID", 0)
        originalTotal = intent.getDoubleExtra("ORDER_TOTAL", 0.0)

        // ─── RESTORE STATE IF RECREATED ──────────────────
        if (savedInstanceState != null) {
            // Restore all custom state:
            orderTotal = savedInstanceState.getDouble("ORDER_TOTAL")
            originalTotal = savedInstanceState.getDouble("ORIGINAL_TOTAL")
            couponDiscount = savedInstanceState.getDouble("COUPON_DISCOUNT")
            isCouponApplied = savedInstanceState.getBoolean("COUPON_APPLIED")
            selectedPaymentMethod = savedInstanceState.getString("PAYMENT", "UPI")
            restaurantId = savedInstanceState.getInt("RESTAURANT_ID")
            cartItemCount = savedInstanceState.getInt("CART_COUNT")

            // Restore UI elements that are NOT auto-saved:
            if (isCouponApplied) {
                etCoupon.setText(savedInstanceState.getString("COUPON_CODE"))
                etCoupon.isEnabled = false
                btnApplyCoupon.text = "Applied ✅"
            }

            // Restore payment method selection:
            when (selectedPaymentMethod) {
                "UPI" -> rgPayment.check(R.id.rbUPI)
                "Card" -> rgPayment.check(R.id.rbCard)
                "Cash" -> rgPayment.check(R.id.rbCash)
            }

            Log.d("OrderForm", "✅ State restored after process death!")
            Toast.makeText(this, "Form restored", Toast.LENGTH_SHORT).show()
        } else {
            // Fresh start — initialize defaults
            orderTotal = originalTotal
            Log.d("OrderForm", "Fresh launch")
        }

        // Update UI with current state:
        updateTotalDisplay()
    }

    // ─── SAVE STATE BEFORE POTENTIAL DEATH ────────────────
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Save ALL custom state:
        outState.putDouble("ORDER_TOTAL", orderTotal)
        outState.putDouble("ORIGINAL_TOTAL", originalTotal)
        outState.putDouble("COUPON_DISCOUNT", couponDiscount)
        outState.putBoolean("COUPON_APPLIED", isCouponApplied)
        outState.putString("PAYMENT", selectedPaymentMethod)
        outState.putInt("RESTAURANT_ID", restaurantId)
        outState.putInt("CART_COUNT", cartItemCount)

        if (isCouponApplied) {
            outState.putString("COUPON_CODE", etCoupon.text.toString())
        }

        Log.d("OrderForm", "💾 State saved! Total: $orderTotal")
    }

    private fun updateTotalDisplay() {
        tvTotal.text = "Total: ₹$orderTotal"
    }

    private fun applyCoupon(code: String) {
        couponDiscount = originalTotal * 0.10  // 10% off
        orderTotal = originalTotal - couponDiscount
        isCouponApplied = true
        updateTotalDisplay()
    }
}
```

---

### 🌟 What the User Experiences with the Good Implementation

```text
STEP-BY-STEP USER EXPERIENCE:

1. User opens FoodApp → selects "Biryani House"
   → navigates to order form
   → Activity created, state is fresh

2. User fills the form:
   Name: "Rohit Kumar"          ← Auto-saved by Android (EditText)
   Phone: "9876543210"          ← Auto-saved by Android (EditText)
   Address: "42 MG Road"        ← Auto-saved by Android (EditText)
   Payment: "Credit Card"       ← Saved in onSaveInstanceState()
   Coupon: "SAVE10" applied     ← Saved in onSaveInstanceState()
   Total: ₹405 (was ₹450)       ← Saved in onSaveInstanceState()

3. Phone call comes in:
   → onPause() called (Activity partially hidden)
   → onStop() called (call screen covers app)
   → onSaveInstanceState() called (OS saves state to disk!)
   → App is now in BACKGROUND priority

4. User opens Maps during the call:
   → Maps uses 500 MB of RAM
   → LMK activates: "Need more RAM!"
   → LMK sees FoodApp is BACKGROUND priority
   → LMK kills FoodApp's process (PID: 18472)
   → FoodApp's RAM is freed
   → FoodApp is GONE from memory
   → But the saved instance state is on DISK ✅

5. Call ends, user reopens FoodApp from recent apps:
   → Android creates NEW process (PID: 21034)
   → Android sees saved back stack: OrderFormActivity was on top
   → Android creates NEW OrderFormActivity instance
   → Android calls onCreate() with NON-NULL savedInstanceState
   → Your code reads the Bundle:
       orderTotal = 405.0 ✅
       paymentMethod = "Credit Card" ✅
       isCouponApplied = true ✅
       couponCode = "SAVE10" ✅
   → EditText fields auto-restore:
       Name: "Rohit Kumar" ✅
       Phone: "9876543210" ✅
       Address: "42 MG Road" ✅
   → User sees a Toast: "Form restored"
   → User sees the EXACT SAME form they were filling out
   → User is happy 😊 → 5-star review ⭐⭐⭐⭐⭐

THE USER NEVER KNEW THE APP WAS KILLED!
This is the gold standard of Android development.
```

---

---

## 📋 Complete Summary

```text
┌──────────────────────────────────────────────────────────────┐
│              APP KILLING SUMMARY                             │
├───────────────────────┬──────────────────────────────────────┤
│ CONCEPT               │ KEY POINTS                           │
├───────────────────────┼──────────────────────────────────────┤
│ Why Android kills     │ Limited RAM (2-8 GB shared)          │
│                       │ Must free memory for active apps     │
│                       │ Background apps are expendable       │
├───────────────────────┼──────────────────────────────────────┤
│ Low Memory Killer     │ Linux kernel component               │
│                       │ Monitors RAM, kills when low         │
│                       │ Kills lowest priority first          │
│                       │ Does NOT call onDestroy()            │
├───────────────────────┼──────────────────────────────────────┤
│ Priority Levels       │ Foreground (safe) → Visible →        │
│                       │ Service → Background → Empty (dead)  │
│                       │ Priority changes as user interacts   │
├───────────────────────┼──────────────────────────────────────┤
│ Data on kill          │ RAM data: LOST                       │
│                       │ Disk data: SURVIVES                  │
│                       │ SavedInstanceState: SURVIVES         │
├───────────────────────┼──────────────────────────────────────┤
│ System kill           │ Back stack preserved                 │
│                       │ savedInstanceState NOT null          │
│                       │ Returns to same screen               │
├───────────────────────┼──────────────────────────────────────┤
│ User swipe            │ Back stack cleared                   │
│                       │ savedInstanceState IS null           │
│                       │ Returns to launcher screen           │
├───────────────────────┼──────────────────────────────────────┤
│ Crash                 │ Process terminated                   │
│                       │ Back stack cleared                   │
│                       │ Fix the bug!                         │
├───────────────────────┼──────────────────────────────────────┤
│ onSaveInstanceState() │ Called before potential death        │
│                       │ Save to Bundle (key-value pairs)     │
│                       │ Restored in onCreate()               │
│                       │ Size limit ~1 MB                     │
│                       │ Auto-saves EditText, CheckBox, etc.  │
├───────────────────────┼──────────────────────────────────────┤
│ Best Practices        │ Save critical data to disk (Room)    │
│                       │ Use ViewModel + SavedStateHandle     │
│                       │ Save early, save often               │
│                       │ Test with "Don't keep activities"    │
│                       │ Handle savedInstanceState in onCreate│
└───────────────────────┴──────────────────────────────────────┘
```

---

---

## 📝 Quiz — Test Your Understanding

> Answer these in your head or write them out before checking!

---

### ❓ Question 1: Process Killing Fundamentals

```text
a) Why does Android kill apps? Explain the fundamental
   hardware constraint that makes this necessary.
   Compare phone RAM vs laptop RAM to illustrate the problem.

b) What is the Low Memory Killer (LMK)?
   Is it part of Android's Java framework or the Linux Kernel?
   Does it call your Activity's onDestroy() before killing?
   Why or why not?

c) List all 5 process priority levels in order from
   MOST important (killed last) to LEAST important (killed first).
   For each level, give a real example of when your FoodApp
   would be at that priority level.

d) Your app is playing music in the background using a
   foreground service (with a notification).
   A user opens a heavy 3D game that needs lots of RAM.
   Will the LMK kill your music app? Why or why not?
   What priority level is your app at?
```

---

### ❓ Question 2: Types of App Death

```text
For each scenario, identify the type of death
(System Kill, User Swipe, or Crash) and explain
what happens to the back stack and savedInstanceState:

SCENARIO A:
  User is browsing your app, presses Home, opens 10 other apps.
  30 minutes later, they reopen your app from recent apps.
  Your app shows the home screen instead of where they left off.
  The Logcat shows: "Process com.rohit.foodapp (pid 18472) has died"

SCENARIO B:
  User is on the checkout screen. They accidentally divide by zero
  in your price calculation code. The app suddenly closes.
  When reopened, it starts from the home screen.

SCENARIO C:
  User finishes using your app and swipes it away from recent apps.
  When they reopen it, it starts from the splash screen.
  All their browsing history within the app is gone.

SCENARIO D:
  User is on the restaurant detail screen. They press Home.
  They open a heavy video editor. The OS kills your app.
  When they reopen your app from recent apps, they see the
  restaurant detail screen again (not the home screen).
  But the restaurant data needs to reload from the network.

For each scenario, also answer:
  - Is savedInstanceState null or not null when Activity recreates?
  - Can the developer prevent this type of death?
  - Can the developer prepare for it?
```

---

### ❓ Question 3: `onSaveInstanceState()` Deep Dive

```text
a) Explain the complete flow of onSaveInstanceState():
   - WHEN is it called in the lifecycle?
   - WHAT is the Bundle parameter?
   - WHERE does Android store the Bundle data?
   - HOW is it passed back to your Activity?
   - In which lifecycle methods can you read it back?

b) A developer tries to save a large list of 5000 Product
   objects in onSaveInstanceState():

   override fun onSaveInstanceState(outState: Bundle) {
       super.onSaveInstanceState(outState)
       outState.putParcelableArrayList("products", productList)
       // productList has 5000 items, each ~2 KB
       // Total: ~10 MB
   }

   What will happen? Why?
   What is the correct approach for saving large datasets?

c) Which of these are saved AUTOMATICALLY by Android?
   Which must you save MANUALLY?
   
   1. Text typed in an EditText (with android:id)
   2. The checked state of a CheckBox (with android:id)
   3. A variable: var selectedTabIndex = 2
   4. The scroll position of a RecyclerView (with LayoutManager)
   5. A list of restaurants loaded from an API
   6. The selected item in a Spinner (with android:id)
   7. A boolean: var isDarkMode = true
   8. The current page number in a ViewPager2

d) Why is onSaveInstanceState() NOT called when the user
   presses the Back button?
   What is the difference between "Activity being destroyed
   because user is done with it" vs "Activity being destroyed
   because OS needs memory"?
```

---

### ❓ Question 4: Real-World Debugging Scenario

```text
A user reports this bug:
  "I was filling out the registration form in your app.
   I got a phone call and talked for 5 minutes.
   When I came back to the app, all my form data was gone.
   I had to type everything again. Very frustrating!"

Your investigation reveals:
  - The registration form has 8 EditText fields
  - All fields have android:id set correctly
  - The Activity does NOT override onSaveInstanceState()
  - The ViewModel stores the form data in regular variables
  - The app does NOT use SavedStateHandle

Answer:
a) What EXACTLY happened to the user's app during the phone call?
   Trace the lifecycle events step by step.

b) The EditText fields have IDs, so Android should auto-save
   their text content. Why did the user still lose data?
   (Hint: Think about what ELSE might have been lost)

c) The ViewModel stored form data in regular variables:
   var formData = RegistrationFormData(...)
   Why did this data not survive the process death?
   Doesn't ViewModel survive configuration changes?

d) Write the COMPLETE fix for this bug. Include:
   - Proper onSaveInstanceState() implementation
   - OR SavedStateHandle in the ViewModel
   - OR persistent storage approach
   Explain which approach you chose and why.

e) How would you TEST this fix before releasing it?
   Describe the exact steps to reproduce the bug
   and verify the fix works.
```

---

### ❓ Question 5: Architecture Design for Process Death

```text
You are building a multi-step checkout flow for a food delivery app:

  Step 1: CartActivity (shows cart items, total)
  Step 2: AddressActivity (delivery address form)
  Step 3: PaymentActivity (payment method selection)
  Step 4: ConfirmActivity (order summary, place order button)

The user can spend 5-10 minutes going through all 4 steps.
At any point, the OS might kill the app due to low memory.

Design a robust architecture that handles process death:

a) Where should you store the checkout data (cart items,
   address, payment method) so it survives process death?
   Compare these options and choose the best one:
   - onSaveInstanceState() Bundle
   - ViewModel with SavedStateHandle
   - Room Database
   - SharedPreferences
   - In-memory singleton object

b) If the app is killed while the user is on Step 3
   (PaymentActivity), what should happen when they reopen?
   - Should they see Step 3 again? (back stack preserved)
   - Should they start from Step 1? (fresh start)
   - What data should be pre-filled?

c) Write the code for PaymentActivity that:
   - Saves the selected payment method and card details
   - Restores them if the Activity is recreated
   - Handles both rotation AND process death
   - Uses the best approach from part (a)

d) The user fills in their credit card number on Step 3.
   The app is killed. When they return, the card number
   is restored. Is this a SECURITY concern?
   How would you handle sensitive data differently?

e) Write a testing checklist for this checkout flow:
   List at least 6 scenarios you would test to ensure
   process death is handled correctly at every step.
```