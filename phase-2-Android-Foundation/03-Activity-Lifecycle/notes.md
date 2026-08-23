# 🔄 Complete Guide to the Android Activity Lifecycle

---

## 🤔 Part 1: What is a Lifecycle and Why Does Android Need One?

### ⚠️ The Core Problem

```text
On a normal computer, when you open a program:
  - It runs from start to finish
  - YOU control when it starts and stops
  - It has plenty of RAM and CPU
  - It is plugged into power

On a PHONE, the situation is completely different:
  - The user can leave your app at ANY moment (phone call, notification)
  - The phone has LIMITED RAM (2-8 GB shared among all apps)
  - The phone runs on BATTERY (every CPU cycle matters)
  - The OS can KILL your app without warning to free memory
  - The user can rotate the screen, splitting the app
  - Multiple apps compete for resources simultaneously

BECAUSE OF THESE CONSTRAINTS, Android cannot let your app
run freely like a desktop program. The OS must MANAGE
your app's existence — creating it, pausing it, resuming it,
and destroying it based on what the USER is doing and
what RESOURCES are available.

This management system is called the LIFECYCLE.
```

---

### 🍽️ The Restaurant Analogy

```text
Think of your Activity as a TABLE in a busy restaurant:

LIFECYCLE OF A RESTAURANT TABLE:

  onCreate()    → Table is SET UP (cleaned, plates placed, menu given)
  onStart()     → Table becomes VISIBLE (guests can see it from entrance)
  onResume()    → Guests are SEATED and actively ordering/eating
  onPause()     → Guests step away briefly (bathroom break — table still theirs)
  onStop()      → Guests leave the dining area (table is reserved but empty)
  onRestart()   → Guests come back from outside (they were in the waiting area)
  onDestroy()   → Table is CLEARED and given to new guests (resources freed)

The RESTAURANT MANAGER (Android OS) decides:
  - When to set up a table (user opens your app)
  - When to pause service (user switches to another app)
  - When to clear the table (OS needs the space for other guests)

YOUR JOB as the developer:
  - Save the guests' order before they leave (save state)
  - Restore the order when they come back (restore state)
  - Don't keep cooking if the guests left (stop background work)
  - Don't waste ingredients on an empty table (free resources)
```

---

### 📋 Why the Lifecycle Exists — Three Reasons

```text
REASON 1: RESOURCE MANAGEMENT
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Your phone has limited RAM. If 20 apps kept running
  at full power simultaneously, the phone would crash.
  
  The lifecycle lets Android:
    - PAUSE apps that are not visible (save CPU)
    - STOP apps that are hidden (save RAM)
    - DESTROY apps when memory is critically low
    - RESUME apps when user returns to them

REASON 2: BATTERY PRESERVATION
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  If your app keeps downloading data, playing animations,
  and using GPS while the user is not looking at it,
  the battery would drain in minutes.
  
  The lifecycle tells your app:
    - "You are no longer visible → stop animations"
    - "You are in background → stop GPS tracking"
    - "You are paused → stop video playback"

REASON 3: USER EXPERIENCE CONSISTENCY
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  When the user rotates their phone, Android DESTROYS
  and RECREATES your Activity. Without lifecycle awareness,
  all data on screen would be lost.
  
  The lifecycle lets you:
    - Save the user's form input before rotation
    - Restore it after rotation
    - The user never notices the recreation happened
```

---

---

## 📊 Part 2: The Complete Activity Lifecycle — Visual Diagram

```text
┌──────────────────────────────────────────────────────────────────┐
│                    ACTIVITY LIFECYCLE DIAGRAM                    │
│                                                                  │
│                    ┌──────────────┐                              │
│                    │   ACTIVITY   │                              │
│                    │   CREATED    │                              │
│                    └──────┬───────┘                              │
│                           │                                      │
│                           ▼                                      │
│                    ┌──────────────┐                              │
│                    │  onCreate()  │ ← Activity is being created  │
│                    │  Set up UI   │   Initialize variables       │
│                    │  Load data   │   setContentView()           │
│                    └──────┬───────┘                              │
│                           │                                      │
│                           ▼                                      │
│                    ┌──────────────┐                              │
│                    │  onStart()   │ ← Activity becomes VISIBLE   │
│                    │  Now visible │   User can see it but        │
│                    │  to user     │   cannot interact yet        │
│                    └──────┬───────┘                              │
│                           │                                      │
│                           ▼                                      │
│                    ┌──────────────┐                              │
│           ┌───────│  onResume()  │ ← Activity is in FOREGROUND  │
│           │       │  User can    │   User can touch, type,      │
│           │       │  interact    │   interact fully             │
│           │       └──────┬───────┘                              │
│           │              │                                      │
│           │    [APP IS RUNNING — USER IS USING IT]              │
│           │              │                                      │
│           │              ▼                                      │
│           │       ┌──────────────┐                              │
│           │       │  onPause()   │ ← Activity is PARTIALLY      │
│           │       │  Partially   │   hidden. Another activity   │
│           │       │  hidden      │   or dialog is on top.       │
│           │       │  STOP heavy  │   Stop animations, pause     │
│           │       │  work here   │   video, save quick data     │
│           │       └──────┬───────┘                              │
│           │              │                                      │
│           │     ┌────────┴────────┐                             │
│           │     │                 │                             │
│           │     │ User returns    │ User leaves completely      │
│           │     │ (dialog closes) │ (presses Home)              │
│           │     │                 │                             │
│           │     ▼                 ▼                             │
│           │  onResume()    ┌──────────────┐                     │
│           │  (back to      │  onStop()    │ ← Activity is FULLY │
│           │   top)         │  No longer   │   hidden. User      │
│           │                │  visible     │   cannot see it.    │
│           │                │  Release     │   Release heavy     │
│           │                │  resources   │   resources here    │
│           │                └──────┬───────┘                     │
│           │                       │                             │
│           │              ┌────────┴────────┐                    │
│           │              │                 │                    │
│           │              │ User returns    │ OS kills app       │
│           │              │ to the app      │ (needs memory)     │
│           │              │                 │                    │
│           │              ▼                 ▼                    │
│           │       ┌──────────────┐  ┌──────────────┐            │
│           │       │ onRestart()  │  │ onDestroy()  │            │
│           │       │ Coming back  │  │ Activity is  │            │
│           │       │ from stopped │  │ being killed │            │
│           │       └──────┬───────┘  │ Clean up     │            │
│           │              │          │ EVERYTHING   │            │
│           │              ▼          └──────────────┘            │
│           │         onStart()                                   │
│           │         (then onResume)                             │
│           │                                                     │
│           └───────────── (cycle repeats) ──────────────────────┘
│                                                                  │
│  THREE KEY STATES:                                               │
│  ┌─────────────────────────────────────────────────────────┐     │
│  │ RESUMED   (onResume → onPause)  = In foreground, active │     │
│  │ PAUSED    (onPause → onStop)    = Partially visible     │     │
│  │ STOPPED   (onStop → onRestart)  = Completely hidden     │     │
│  └─────────────────────────────────────────────────────────┘     │
└──────────────────────────────────────────────────────────────────┘
```

---

---

## 🔍 Part 3: Each Lifecycle Method — Deep Explanation

### 🟢 `onCreate()` — "Birth"

```text
WHEN IT IS CALLED:
  The VERY FIRST time the Activity is created.
  This is the "birth" of your screen.

WHAT HAPPENS HERE:
  - The Activity object is created in memory
  - You set up the user interface (setContentView)
  - You initialize variables and data structures
  - You set up click listeners
  - You restore saved state if the Activity was previously destroyed
  - This is called ONLY ONCE per Activity instance

REAL SCENARIOS WHEN onCreate() RUNS:
  ✅ User taps your app icon for the first time
  ✅ User rotates the screen (Activity is destroyed and recreated!)
  ✅ User was on your app, OS killed it for memory, user reopens it
  ✅ User navigates to this Activity from another Activity
```

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)  // MUST be first!
    
    // Set up the UI:
    setContentView(R.layout.activity_main)
    
    // Initialize variables:
    val username = "Rohit"
    val productList = mutableListOf<Product>()
    
    // Set up click listeners:
    findViewById<Button>(R.id.btnLogin).setOnClickListener {
        // handle login
    }
    
    // Restore saved state (if Activity was recreated):
    if (savedInstanceState != null) {
        val savedText = savedInstanceState.getString("search_query")
        searchEditText.setText(savedText)
    }
    
    Log.d("Lifecycle", "onCreate called")
}
```

> **📌 Important:**
> - `savedInstanceState` is **null** on FIRST launch
> - `savedInstanceState` is **NOT null** when Activity is RECREATED (after rotation, after OS kill)
> - Do **NOT** start long-running operations here that should pause when the app goes to background (explained in Part 6)

---

### 🟡 `onStart()` — "Becoming Visible"

```text
WHEN IT IS CALLED:
  Right after onCreate(), when the Activity becomes VISIBLE
  to the user. The user can SEE the screen but cannot
  interact with it yet (that happens in onResume).

WHAT HAPPENS HERE:
  - The Activity's window is added to the screen
  - The UI is visible but might be behind another window
  - Good place to register broadcast receivers
  - Good place to start UI-related updates

REAL SCENARIOS:
  ✅ Immediately after onCreate() (first launch)
  ✅ After onRestart() (user returns from background)
```

```kotlin
override fun onStart() {
    super.onStart()
    Log.d("Lifecycle", "onStart called — Activity is now visible")
    
    // Example: Start listening for connectivity changes
    val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
    registerReceiver(networkReceiver, filter)
}
```

```text
WHY SEPARATE FROM onCreate()?
  onCreate() = one-time setup (happens once per instance)
  onStart()  = visibility setup (can happen multiple times)
  
  An Activity can go through onStart() → onStop() → onRestart()
  → onStart() many times WITHOUT being destroyed and recreated.
  So onStart() is for things that need to happen every time
  the Activity becomes visible, not just the first time.
```

---

### 🟢 `onResume()` — "Fully Active"

```text
WHEN IT IS CALLED:
  When the Activity comes to the FOREGROUND and the user
  can fully interact with it. This is the "running" state.

WHAT HAPPENS HERE:
  - The Activity is at the TOP of the screen
  - User can touch, type, swipe, interact
  - Start animations, video playback, camera preview
  - Resume any paused operations
  - Start listening for sensor data (GPS, accelerometer)

REAL SCENARIOS:
  ✅ After onStart() on first launch
  ✅ After onPause() when a dialog is dismissed
  ✅ After onStart() when returning from background
```

```kotlin
override fun onResume() {
    super.onResume()
    Log.d("Lifecycle", "onResume called — Activity is active!")
    
    // Resume camera preview:
    cameraPreview.start()
    
    // Resume GPS tracking:
    locationManager.requestLocationUpdates(...)
    
    // Resume video playback:
    videoPlayer.play()
    
    // Start animation:
    loadingAnimation.start()
}
```

> **💡 Key Insight:** `onResume()` does NOT mean the Activity was just created. It means the Activity is now the **TOPMOST, INTERACTIVE** screen. It can be called many times during the Activity's life.

---

### 🟠 `onPause()` — "Partially Hidden"

```text
WHEN IT IS CALLED:
  When the Activity is about to lose focus but is STILL
  PARTIALLY VISIBLE. Something else is coming on top.

WHAT HAPPENS HERE:
  - Another Activity or dialog is appearing on top
  - The Activity is still visible behind the new element
  - You MUST save any unsaved data here
  - Stop animations, pause video, release camera
  - This method must execute QUICKLY (next Activity waits for it!)

REAL SCENARIOS:
  ✅ User taps a button that opens a dialog/popup
  ✅ User receives a phone call (call screen appears on top)
  ✅ User pulls down the notification shade (partially covers app)
  ✅ Another app's Activity opens on top (semi-transparent)
  ✅ User presses Home button (onPause → onStop quickly)
  ✅ User opens the recent apps view
```

```kotlin
override fun onPause() {
    super.onPause()
    Log.d("Lifecycle", "onPause called — Activity losing focus")
    
    // Pause video playback:
    videoPlayer.pause()
    
    // Stop camera preview (another app might need camera):
    cameraPreview.stop()
    
    // Stop GPS to save battery:
    locationManager.removeUpdates(locationListener)
    
    // Save unsaved data QUICKLY:
    saveDraftMessage()
    
    // ⚠️ DO NOT do heavy work here!
    // The next Activity is WAITING for onPause() to finish.
    // If you take too long, the transition will be slow/janky.
}
```

> **🔴 Critical Rule:** `onPause()` is **GUARANTEED** to be called before the Activity is killed. `onStop()` and `onDestroy()` are **NOT** guaranteed. If the OS is critically low on memory, it can kill your Activity right after `onPause()` without calling `onStop()`. **THEREFORE: Save critical data in `onPause()`, not `onStop()`.**

---

### 🔴 `onStop()` — "Fully Hidden"

```text
WHEN IT IS CALLED:
  When the Activity is NO LONGER VISIBLE to the user.
  It is completely hidden behind another Activity or the home screen.

WHAT HAPPENS HERE:
  - The Activity is in the background
  - User cannot see it at all
  - Release heavy resources (database connections, large bitmaps)
  - Unregister broadcast receivers
  - Stop any ongoing operations that are not needed in background

REAL SCENARIOS:
  ✅ User presses Home button (after onPause)
  ✅ User opens another app that covers yours completely
  ✅ User navigates to a new Activity in your app (full screen)
  ✅ User locks the phone screen
```

```kotlin
override fun onStop() {
    super.onStop()
    Log.d("Lifecycle", "onStop called — Activity is hidden")
    
    // Release database connection (not needed in background):
    database.close()
    
    // Unregister broadcast receiver:
    unregisterReceiver(networkReceiver)
    
    // Release large bitmaps to free RAM:
    largeBitmap?.recycle()
    largeBitmap = null
    
    // Stop background music (if appropriate):
    // musicPlayer.stop()
}
```

```text
onPause() vs onStop():
  onPause()  = Activity is PARTIALLY visible (dialog on top)
  onStop()   = Activity is COMPLETELY hidden (home screen)
  
  If a small dialog appears: onPause() only (Activity still visible behind)
  If user presses Home:      onPause() → onStop() (Activity fully hidden)
```

---

### ⚫ `onDestroy()` — "Death"

```text
WHEN IT IS CALLED:
  When the Activity is about to be DESTROYED and removed
  from memory entirely. This is the "death" of the Activity.

WHAT HAPPENS HERE:
  - The Activity object will be garbage collected after this
  - Clean up ALL remaining resources
  - Close all connections, cancel all coroutines
  - This is the LAST callback you will ever receive

REAL SCENARIOS:
  ✅ User presses the BACK button on this Activity
  ✅ Activity called finish() programmatically
  ✅ OS is killing the Activity to free memory
  ✅ Screen rotation (old Activity destroyed, new one created)
  ✅ User swipes the app away from recent apps
```

```kotlin
override fun onDestroy() {
    super.onDestroy()
    Log.d("Lifecycle", "onDestroy called — Activity is dying")
    
    // Cancel all running coroutines:
    viewModelScope.cancel()  // (if using ViewModel)
    
    // Close all open connections:
    webSocketConnection.close()
    
    // Release all resources:
    mediaPlayer?.release()
    mediaPlayer = null
}
```

> **⚠️ Important:** `onDestroy()` is **NOT GUARANTEED** to be called! If the OS kills your app's process abruptly (extreme low memory), it may skip `onDestroy()` entirely. This is why you should save critical data in `onPause()`, not in `onDestroy()`.

---

### 🔵 `onRestart()` — "Coming Back from the Dead (Almost)"

```text
WHEN IT IS CALLED:
  When the Activity was STOPPED (fully hidden) and is now
  coming back to the foreground. It runs BETWEEN onStop()
  and onStart().

WHAT HAPPENS HERE:
  - The Activity was in the background and is returning
  - It was NOT destroyed — it was just stopped
  - Good place to refresh data that might have changed
    while the Activity was hidden

REAL SCENARIOS:
  ✅ User pressed Home, then reopened the app from recent apps
  ✅ User switched to another app, then came back
  ✅ User locked the phone, then unlocked it
```

```kotlin
override fun onRestart() {
    super.onRestart()
    Log.d("Lifecycle", "onRestart called — Activity returning from background")
    
    // Refresh data that might have changed while hidden:
    // For example, if the user received a new message
    // while the app was in background:
    refreshNotificationCount()
}
```

```text
THE FULL RETURN PATH:
  When user returns to a stopped Activity:
    onRestart() → onStart() → onResume()
  
  When user returns to a destroyed Activity:
    onCreate() → onStart() → onResume()
    (completely new instance!)
```

---

---

## 🎬 Part 4: Real Scenarios — Tracing the Lifecycle

### 📖 Scenario 1: App Opens for the First Time

```text
USER ACTION: Taps your app icon on the home screen

LIFECYCLE CALLS (in order):
  1. onCreate()    → "Setting up the screen for the first time"
  2. onStart()     → "Screen is now visible to the user"
  3. onResume()    → "User can now interact with the screen"

LOG OUTPUT:
  D/Lifecycle: onCreate called
  D/Lifecycle: onStart called
  D/Lifecycle: onResume called

STATE: Activity is RESUMED (foreground, active)
```

---

### 📖 Scenario 2: User Presses Home Button

```text
USER ACTION: Presses the Home button while using your app

LIFECYCLE CALLS (in order):
  1. onPause()     → "Activity is losing focus (Home screen appearing)"
  2. onStop()      → "Activity is now fully hidden behind Home screen"

LOG OUTPUT:
  D/Lifecycle: onPause called
  D/Lifecycle: onStop called

STATE: Activity is STOPPED (hidden, in background)
NOTE: Activity is NOT destroyed. It stays in RAM.
      If user returns quickly, it will be restarted, not recreated.
```

---

### 📖 Scenario 3: User Comes Back to the App

```text
USER ACTION: Taps your app icon again (or opens from recent apps)

LIFECYCLE CALLS (in order):
  1. onRestart()   → "Activity is coming back from stopped state"
  2. onStart()     → "Screen is visible again"
  3. onResume()    → "User can interact again"

LOG OUTPUT:
  D/Lifecycle: onRestart called
  D/Lifecycle: onStart called
  D/Lifecycle: onResume called

STATE: Activity is RESUMED again
NOTE: onCreate() is NOT called! The same Activity instance is reused.
      This is faster than creating a new one.
```

---

### 📖 Scenario 4: User Rotates the Screen

```text
USER ACTION: Rotates phone from portrait to landscape

THIS IS THE TRICKIEST SCENARIO!

LIFECYCLE CALLS (in order):
  1. onPause()     → "Current Activity losing focus"
  2. onStop()      → "Current Activity hidden"
  3. onDestroy()   → "Current Activity DESTROYED!"
  4. onCreate()    → "NEW Activity instance created for new orientation"
  5. onStart()     → "New Activity visible"
  6. onResume()    → "New Activity active"

LOG OUTPUT:
  D/Lifecycle: onPause called
  D/Lifecycle: onStop called
  D/Lifecycle: onDestroy called     ← OLD instance dies
  D/Lifecycle: onCreate called      ← NEW instance born!
  D/Lifecycle: onStart called
  D/Lifecycle: onResume called

STATE: A COMPLETELY NEW Activity instance is running!

WHY DOES THIS HAPPEN?
  When the screen rotates, the screen dimensions change.
  Android needs to reload a DIFFERENT layout for landscape mode.
  (res/layout/activity_main.xml vs res/layout-land/activity_main.xml)
  The easiest way to do this is to destroy and recreate the Activity.
```

> **⚠️ The Danger:** If the user typed "Hello" in an EditText and rotated the phone, the text **DISAPPEARS** because the Activity was destroyed and recreated.

**Solution:** Save state in `onSaveInstanceState()` and restore in `onCreate()`:

```kotlin
override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putString("search_text", searchEditText.text.toString())
}

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    
    if (savedInstanceState != null) {
        val savedText = savedInstanceState.getString("search_text")
        searchEditText.setText(savedText)  // Restore!
    }
}
```

---

### 📖 Scenario 5: User Presses Back Button

```text
USER ACTION: Presses the Back button on your Activity

LIFECYCLE CALLS (in order):
  1. onPause()     → "Activity losing focus"
  2. onStop()      → "Activity hidden"
  3. onDestroy()   → "Activity DESTROYED permanently"

LOG OUTPUT:
  D/Lifecycle: onPause called
  D/Lifecycle: onStop called
  D/Lifecycle: onDestroy called

STATE: Activity is DESTROYED. It is gone from memory.
       If user opens the app again, a NEW instance is created.
       This is different from pressing Home (which only stops).

BACK vs HOME:
  Home button:  onPause → onStop (Activity stays in RAM, can restart)
  Back button:  onPause → onStop → onDestroy (Activity is GONE)
```

---

### 📖 Scenario 6: Another App's Dialog Appears on Top

```text
USER ACTION: A phone call comes in, or a permission dialog appears,
             or another app shows a transparent Activity on top

LIFECYCLE CALLS:
  1. onPause()     → "Activity is partially covered by the dialog"

LOG OUTPUT:
  D/Lifecycle: onPause called

STATE: Activity is PAUSED (partially visible behind the dialog)
NOTE: onStop() is NOT called because your Activity is still
      PARTIALLY visible behind the semi-transparent dialog.

WHEN THE DIALOG DISMISSES:
  2. onResume()    → "Dialog gone, Activity is fully active again"

LOG OUTPUT:
  D/Lifecycle: onResume called

THE FULL CYCLE: onResume → onPause → onResume
(Activity never left the screen, just lost focus briefly)
```

---

### 📊 Complete Scenario Summary Table

```text
┌──────────────────────────────┬────────────────────────────────────────┐
│ USER ACTION                  │ LIFECYCLE CALLS                        │
├──────────────────────────────┼────────────────────────────────────────┤
│ Open app (first time)        │ onCreate → onStart → onResume          │
│ Press Home button            │ onPause → onStop                       │
│ Return from Home             │ onRestart → onStart → onResume         │
│ Rotate screen                │ onPause → onStop → onDestroy →         │
│                              │ onCreate → onStart → onResume          │
│ Press Back button            │ onPause → onStop → onDestroy           │
│ Dialog/phone call on top     │ onPause                                │
│ Dialog dismissed             │ onResume                               │
│ Open new Activity (B)        │ A: onPause → onStop                    │
│                              │ B: onCreate → onStart → onResume       │
│ Back from B to A             │ B: onPause → onStop → onDestroy        │
│                              │ A: onRestart → onStart → onResume      │
│ OS kills app (low memory)    │ onPause → (maybe onStop, onDestroy)    │
│ Lock screen                  │ onPause → onStop                       │
│ Unlock screen                │ onRestart → onStart → onResume         │
│ Swipe from recent apps       │ onPause → onStop → onDestroy           │
└──────────────────────────────┴────────────────────────────────────────┘
```

---

---

## 🐛 Part 5: Why Understanding Lifecycle Prevents Bugs and Crashes

### 🐛 Bug 1: Memory Leak — Forgetting to Unregister

```kotlin
// ❌ BUGGY CODE:
class MainActivity : AppCompatActivity() {

    val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            updateUI(event)  // Updates a TextView
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_UI)
        // Registered in onResume ✅
    }

    // ❌ BUG: Forgot to unregister in onPause!
    // The sensor keeps sending data even when Activity is hidden.
    // The listener holds a reference to the Activity.
    // Even after onDestroy(), the sensor manager still references it.
    // The Activity CANNOT be garbage collected → MEMORY LEAK!
}
```

```kotlin
// ✅ CORRECT CODE:
class MainActivity : AppCompatActivity() {

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorListener)  // ✅ Unregistered!
        // No more references → Activity can be garbage collected
    }
}
```

---

### 🐛 Bug 2: Crash — Updating UI After Activity is Destroyed

```kotlin
// ❌ BUGGY CODE:
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvResult = findViewById<TextView>(R.id.tvResult)

        // Start a background thread to fetch data:
        Thread {
            Thread.sleep(5000)  // Simulate 5-second network call
            val data = fetchDataFromServer()
            
            // ❌ CRASH! User pressed Back during the 5 seconds.
            // Activity is DESTROYED. tvResult no longer exists.
            // Updating a destroyed view throws an exception!
            tvResult.text = data
        }.start()
    }
}
```

```kotlin
// ✅ CORRECT CODE (using lifecycle-aware approach):
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvResult = findViewById<TextView>(R.id.tvResult)

        // Use lifecycleScope — automatically cancels when Activity is destroyed:
        lifecycleScope.launch {
            val data = withContext(Dispatchers.IO) {
                fetchDataFromServer()  // 5-second network call
            }
            // If Activity was destroyed during the 5 seconds,
            // this coroutine is CANCELLED automatically.
            // This line never runs → no crash! ✅
            tvResult.text = data
        }
    }
}
```

---

### 🐛 Bug 3: Data Loss on Rotation

```kotlin
// ❌ BUGGY CODE:
class MainActivity : AppCompatActivity() {
    var userScore = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // userScore is always 0 on creation!
        // If user scored 500 points and rotated the screen,
        // the Activity is destroyed and recreated.
        // userScore resets to 0. User loses all progress!
    }
}
```

```kotlin
// ✅ CORRECT CODE:
class MainActivity : AppCompatActivity() {
    var userScore = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Restore score if Activity was recreated:
        savedInstanceState?.let {
            userScore = it.getInt("score", 0)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save score before Activity is destroyed:
        outState.putInt("score", userScore)
    }
}
```

---

---

## ⚡ Part 6: Real Example — Why NOT to Start Network Calls in `onCreate()`

### ❌ The Problem

```kotlin
// ❌ BAD PRACTICE: Starting a continuous network operation in onCreate()

class LiveScoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_score)

        // ❌ Starting a live score update that runs FOREVER
        // This runs even when the app is in the background!
        lifecycleScope.launch {
            while (true) {
                val score = fetchLiveScore()  // Network call every 5 seconds
                updateScoreUI(score)
                delay(5000)
            }
        }
    }
}
```

```text
WHY THIS IS BAD:
  1. User presses Home → Activity is stopped but the loop CONTINUES
  2. Network calls keep happening in the background → wastes battery
  3. Data is being downloaded but nobody is looking at it → wastes data
  4. updateScoreUI() might crash if Activity is destroyed → NullPointerException
  5. The loop runs forever until the process is killed → memory leak
```

---

### ✅ The Correct Approach

```kotlin
// ✅ CORRECT: Start in onResume(), stop in onPause()

class LiveScoreActivity : AppCompatActivity() {

    private var scoreJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_score)
        // Only do ONE-TIME setup here (UI, listeners, etc.)
    }

    override fun onResume() {
        super.onResume()
        // START the live updates when user is actively watching:
        scoreJob = lifecycleScope.launch {
            while (isActive) {  // isActive becomes false when coroutine is cancelled
                val score = withContext(Dispatchers.IO) {
                    fetchLiveScore()
                }
                updateScoreUI(score)
                delay(5000)
            }
        }
        Log.d("LiveScore", "Live updates STARTED")
    }

    override fun onPause() {
        super.onPause()
        // STOP the live updates when user leaves:
        scoreJob?.cancel()
        scoreJob = null
        Log.d("LiveScore", "Live updates STOPPED — saving battery!")
    }
}
```

```text
WHAT HAPPENS NOW:
  User opens app:
    onCreate → onStart → onResume → Live updates START ✅

  User presses Home:
    onPause → Live updates STOP ✅ (battery saved!)
    onStop

  User returns:
    onRestart → onStart → onResume → Live updates START again ✅

  User rotates screen:
    onPause → onStop → onDestroy (updates stopped)
    onCreate → onStart → onResume (updates restart on new instance) ✅

BATTERY SAVED. DATA SAVED. NO CRASHES.
```

---

### 📋 Where to Put Different Types of Work

```text
┌──────────────────────────────────────────────────────────────┐
│         WHERE TO PUT DIFFERENT OPERATIONS                    │
├───────────────────────────┬──────────────────────────────────┤
│ LIFECYCLE METHOD          │ PUT THIS HERE                    │
├───────────────────────────┼──────────────────────────────────┤
│ onCreate()                │ One-time setup:                  │
│                           │ - setContentView()               │
│                           │ - Initialize variables           │
│                           │ - Set up click listeners         │
│                           │ - Create ViewModel               │
│                           │ - Restore saved state            │
├───────────────────────────┼──────────────────────────────────┤
│ onStart()                 │ Visibility-related setup:        │
│                           │ - Register broadcast receivers   │
│                           │ - Start UI animations            │
├───────────────────────────┼──────────────────────────────────┤
│ onResume()                │ Active operations:               │
│                           │ - Start camera preview           │
│                           │ - Start GPS tracking             │
│                           │ - Start live data updates        │
│                           │ - Resume video/audio playback    │
│                           │ - Start sensor listeners         │
├───────────────────────────┼──────────────────────────────────┤
│ onPause()                 │ Pause active operations:         │
│                           │ - Pause video/audio              │
│                           │ - Stop camera                    │
│                           │ - Stop GPS                       │
│                           │ - Save unsaved data (CRITICAL!)  │
│                           │ - Stop animations                │
├───────────────────────────┼──────────────────────────────────┤
│ onStop()                  │ Release heavy resources:         │
│                           │ - Close database connections     │
│                           │ - Release large bitmaps          │
│                           │ - Unregister receivers           │
├───────────────────────────┼──────────────────────────────────┤
│ onDestroy()               │ Final cleanup:                   │
│                           │ - Cancel all coroutines          │
│                           │ - Close all connections          │
│                           │ - Release all resources          │
└───────────────────────────┴──────────────────────────────────┘
```

---

---

## 🧩 Part 7: Lifecycle in Jetpack Compose (Brief Awareness)

```text
In Jetpack Compose, you don't override lifecycle methods directly.
Instead, you use COMPOSABLE EFFECTS that are lifecycle-aware.

COMPOSE LIFECYCLE EQUIVALENTS:

XML/TRADITIONAL:                    COMPOSE:
onCreate()                     →   No direct equivalent (setup in composable)
onResume() / onPause()         →   LaunchedEffect + LifecycleEventEffect
onDestroy()                    →   DisposableEffect { onDispose { } }
```

```kotlin
@Composable
fun LiveScoreScreen() {

    // LaunchedEffect: Runs when composable enters composition
    // (similar to onResume)
    LaunchedEffect(Unit) {
        // Start live score updates
        while (true) {
            val score = fetchLiveScore()
            updateScore(score)
            delay(5000)
        }
        // Automatically CANCELLED when composable leaves composition
        // (similar to onPause/onDestroy cleanup)
    }

    // DisposableEffect: Runs cleanup when composable leaves
    // (similar to onDestroy)
    DisposableEffect(Unit) {
        val listener = registerSomeListener()
        
        onDispose {
            // This runs when the composable is removed
            listener.unregister()  // Cleanup!
        }
    }

    // UI code:
    Text("Live Score: $score")
}
```

```text
KEY DIFFERENCE:
  In traditional Android: Lifecycle is tied to the ACTIVITY
  In Compose: Lifecycle is tied to the COMPOSABLE (UI component)
  
  Composables can enter and leave the screen independently
  of the Activity lifecycle. This is more granular and flexible.

DON'T WORRY about mastering this now.
You will learn Compose lifecycle deeply in a later phase.
For now, understand that the CONCEPT is the same:
  "Start work when visible, stop work when hidden,
   clean up when destroyed."
```

---

---

## 📋 Complete Summary

```text
┌──────────────────────────────────────────────────────────────┐
│              ACTIVITY LIFECYCLE SUMMARY                      │
├───────────────────┬──────────────────────────────────────────┤
│ METHOD            │ WHEN IT RUNS                             │
├───────────────────┼──────────────────────────────────────────┤
│ onCreate()        │ Activity first created (or recreated)    │
│                   │ Set up UI, initialize, restore state     │
├───────────────────┼──────────────────────────────────────────┤
│ onStart()         │ Activity becomes visible                 │
│                   │ Register receivers, start UI updates     │
├───────────────────┼──────────────────────────────────────────┤
│ onResume()        │ Activity is in foreground, interactive   │
│                   │ Start camera, GPS, animations, live data │
├───────────────────┼──────────────────────────────────────────┤
│ onPause()         │ Activity partially hidden                │
│                   │ Pause heavy work, SAVE DATA (critical!)  │
├───────────────────┼──────────────────────────────────────────┤
│ onStop()          │ Activity fully hidden                    │
│                   │ Release heavy resources                  │
├───────────────────┼──────────────────────────────────────────┤
│ onRestart()       │ Activity returning from stopped state    │
│                   │ Refresh stale data                       │
├───────────────────┼──────────────────────────────────────────┤
│ onDestroy()       │ Activity being destroyed                 │
│                   │ Final cleanup (NOT guaranteed!)          │
├───────────────────┼──────────────────────────────────────────┤
│ onSaveInstanceState│ Before Activity might be destroyed      │
│                   │ Save UI state for restoration            │
└───────────────────┴──────────────────────────────────────────┘

GOLDEN RULES:
  1. Save critical data in onPause() (guaranteed to be called)
  2. Start active operations in onResume(), stop in onPause()
  3. One-time setup in onCreate(), recurring setup in onStart()
  4. Never assume onDestroy() will be called
  5. Always handle screen rotation (save and restore state)
  6. Use lifecycleScope for coroutines (auto-cancellation)
```

---

---

## 📝 Quiz — Test Your Understanding

> Answer these in your head or write them out before checking!

---

### ❓ Question 1: Lifecycle Tracing

```text
For each scenario, write the EXACT sequence of lifecycle
methods that will be called, in order.

SCENARIO A:
  User opens your app for the first time, uses it for 10 seconds,
  then presses the Back button.

SCENARIO B:
  User opens your app, presses Home, opens Instagram,
  uses Instagram for 5 minutes, then reopens your app
  from the recent apps list. (Assume OS did NOT kill your app)

SCENARIO C:
  User is filling a form in your app. A phone call comes in.
  User answers the call, talks for 2 minutes, hangs up,
  and returns to your app.

SCENARIO D:
  User is watching a video in your app in portrait mode.
  User rotates the phone to landscape mode.
  Then rotates back to portrait.
  List ALL lifecycle calls for BOTH rotations.

SCENARIO E:
  User opens Activity A, then navigates to Activity B
  (both in your app). Then presses Back on Activity B.
  List the lifecycle calls for BOTH Activity A and Activity B
  at each step. (This is the most complex scenario!)
```

---

### ❓ Question 2: Lifecycle Bug Investigation

Read this code and find **ALL** lifecycle-related bugs:

```kotlin
class MusicPlayerActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)

        mediaPlayer = MediaPlayer.create(this, R.raw.song)
        
        // Start playing music immediately
        mediaPlayer?.start()
        isPlaying = true

        val btnPlay = findViewById<Button>(R.id.btnPlay)
        btnPlay.setOnClickListener {
            if (isPlaying) {
                mediaPlayer?.pause()
                isPlaying = false
            } else {
                mediaPlayer?.start()
                isPlaying = true
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // Save playback position
        val position = mediaPlayer?.currentPosition ?: 0
        getPreferences(MODE_PRIVATE).edit()
            .putInt("playback_position", position)
            .apply()
    }
}
```

```text
Find at least 5 bugs and explain:
a) What happens when the user presses Home? Does music stop?
b) What happens when the user rotates the screen?
c) What happens when the user presses Back?
d) Is the playback position saved reliably?
e) Is the MediaPlayer released properly?
f) Rewrite the corrected code using proper lifecycle methods.
```

---

### ❓ Question 3: `onPause()` vs `onStop()` vs `onDestroy()`

```text
a) Explain the EXACT difference between onPause(), onStop(),
   and onDestroy() using a real-life analogy of your own
   (not the restaurant one).

b) A developer saves all their data in onDestroy():

   override fun onDestroy() {
       super.onDestroy()
       database.saveUserData(currentUser)
   }

   Explain TWO specific scenarios where this data will be LOST.
   Why is onDestroy() unreliable for saving data?

c) Another developer puts heavy network calls in onPause():

   override fun onPause() {
       super.onPause()
       uploadLargeFileToServer()  // Takes 30 seconds
   }

   What will the user experience? Why is this problematic?
   What is the correct approach?

d) Explain this statement:
   "onPause() is the only lifecycle method that is GUARANTEED
    to be called before your Activity is killed."
   What does this mean for your data-saving strategy?

e) Can onStop() be called WITHOUT onPause() being called first?
   Can onDestroy() be called WITHOUT onStop() being called first?
   Explain with scenarios.
```

---

### ❓ Question 4: Screen Rotation Deep Dive

```text
a) Why does Android destroy and recreate the Activity on rotation?
   What is the technical reason? Why not just resize the existing Activity?

b) You have this Activity:

   class QuizActivity : AppCompatActivity() {
       var currentQuestion = 1
       var score = 0
       var selectedAnswers = mutableListOf<String>()
       var timerSeconds = 120

       override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           setContentView(R.layout.activity_quiz)
           loadQuestion(currentQuestion)
           startTimer()
       }
   }

   The user is on question 5, has a score of 40,
   selected answers for 4 questions, and has 65 seconds left.
   
   They rotate the phone.
   
   What happens to ALL of this data?
   Write the complete code to save and restore ALL four variables
   using onSaveInstanceState() and onCreate().

c) What is the difference between:
   - savedInstanceState (in onCreate)
   - intent.extras (in onCreate)
   
   When is each one non-null?
   Can both be non-null at the same time? Give a scenario.

d) How can you PREVENT Activity recreation on rotation?
   Is this a good idea? Why or why not?
   (Hint: android:configChanges in the manifest)
```

---

### ❓ Question 5: Design a Lifecycle-Aware Feature

```text
You are building a Fitness Tracking app with a RunTrackerActivity.
This Activity must:

  - Track the user's GPS location continuously during a run
  - Show a live timer counting up
  - Display current speed and distance
  - Play audio cues every kilometer ("You've run 1 km!")
  - Keep the screen on during the run
  - Save the run data if the Activity is destroyed

Design the complete lifecycle management:

a) Which lifecycle method should you START GPS tracking in?
   Which method should you STOP it in? Why these specifically?

b) Where should you start and stop the live timer?
   What happens to the timer if the user gets a phone call
   during their run? Should the timer pause?

c) Where should you acquire and release the WAKE_LOCK
   (keeps screen on)? What happens if you forget to release it?

d) Where should you save the run data (distance, time, route)?
   Consider these scenarios:
   - User rotates the phone mid-run
   - User gets a phone call and the OS kills your app
   - User's battery dies during the run
   Which lifecycle method gives you the best chance of saving?

e) Write the complete RunTrackerActivity skeleton with all
   lifecycle methods properly implemented. Include:
   - GPS start/stop
   - Timer start/pause/stop
   - Wake lock acquire/release
   - Data saving in the right place
   - State restoration on recreation
   - Proper cleanup in onDestroy

f) Explain how using lifecycleScope.launch { } instead of
   Thread { } prevents crashes when the Activity is destroyed.
   Connect this to your knowledge of coroutines and the Main Thread.
```