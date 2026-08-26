# ⚙️ Complete Guide to Configuration Changes in Android

---

## 🔄 Part 1: What is a Configuration Change?

### 📖 The Definition

```text
A CONFIGURATION CHANGE is any event that alters the
"configuration" of the device — the settings that describe
the environment your app is running in.

When the configuration changes, the resources your app
needs might change too. For example, a landscape layout
is different from a portrait layout.
```

---

### 📊 Types of Configuration Changes

```text
┌──────────────────────────────────────────────────────────────┐
│              CONFIGURATION CHANGE TYPES                       │
├───────────────────────────┬──────────────────────────────────┤
│ CHANGE                    │ WHEN IT HAPPENS                  │
├───────────────────────────┼──────────────────────────────────┤
│ Screen Rotation           │ User rotates phone portrait ↔    │
│ (orientation)             │ landscape. MOST COMMON.          │
├───────────────────────────┼──────────────────────────────────┤
│ Language Change           │ User changes device language     │
│ (locale)                  │ in Settings (English → Hindi)    │
├───────────────────────────┼──────────────────────────────────┤
│ Dark Mode Toggle          │ User switches between Light      │
│ (uiMode)                  │ and Dark theme in Settings       │
├───────────────────────────┼──────────────────────────────────┤
│ Font Size Change          │ User changes font size in        │
│ (fontScale)               │ Settings → Accessibility         │
├───────────────────────────┼──────────────────────────────────┤
│ Keyboard Connected        │ User connects/disconnects a      │
│ (keyboard)                │ physical keyboard (Bluetooth)    │
├───────────────────────────┼──────────────────────────────────┤
│ Screen Size Change        │ App enters split-screen or       │
│ (screenSize)              │ multi-window mode                │
├───────────────────────────┼──────────────────────────────────┤
│ Smallest Screen Width     │ Device is folded/unfolded        │
│ (smallestScreenWidth)     │ (foldable phones like Fold)      │
├───────────────────────────┼──────────────────────────────────┤
│ Display Density           │ User changes display size in     │
│ (density)                 │ Settings → Display               │
├───────────────────────────┼──────────────────────────────────┤
│ Night Mode                │ Automatic night mode kicks in    │
│ (night mode)              │ based on time of day             │
└───────────────────────────┴──────────────────────────────────┘

THE MOST COMMON one you will deal with: SCREEN ROTATION.
Every time the user turns their phone sideways, a configuration
change happens. This will happen THOUSANDS of times in your
app's lifetime. You MUST handle it correctly.
```

---

### 📁 The Resource Connection

```text
WHY do configuration changes matter?

Because Android loads DIFFERENT resources based on configuration.

Your app's res/ folder can have multiple versions of the same file:

  res/
  ├── layout/
  │   └── activity_main.xml          ← Default (portrait)
  ├── layout-land/
  │   └── activity_main.xml          ← Landscape version!
  ├── values/
  │   └── strings.xml                ← Default (English)
  ├── values-hi/
  │   └── strings.xml                ← Hindi version!
  ├── values-night/
  │   └── colors.xml                 ← Dark mode colors!
  ├── drawable/
  │   └── logo.png                   ← Default density
  ├── drawable-xxhdpi/
  │   └── logo.png                   ← High density screens

When the configuration changes:
  Portrait → Landscape: Android needs to load layout-land/
  English → Hindi:      Android needs to load values-hi/
  Light → Dark:         Android needs to load values-night/

The easiest way for Android to reload all resources correctly
is to DESTROY the Activity and RECREATE it from scratch.
That is exactly what happens by default.
```

---

---

## 🔁 Part 2: What Happens During a Configuration Change

### ⚡ The Default Behavior: Destroy and Recreate

```text
When a configuration change occurs, Android's DEFAULT behavior is:

  1. Call onPause() on the current Activity
  2. Call onStop() on the current Activity
  3. Call onSaveInstanceState() ← YOUR CHANCE TO SAVE DATA
  4. Call onDestroy() ← Activity is DESTROYED
  5. Create a BRAND NEW Activity instance
  6. Call onCreate() with the saved Bundle ← RESTORE DATA
  7. Call onStart()
  8. Call onResume()
  9. New Activity is displayed with new resources

THE OLD ACTIVITY IS DEAD. A NEW ONE IS BORN.
Same screen, same class, but a completely new object in memory.
```

---

### 🔀 Visual Lifecycle Flow

```text
CONFIGURATION CHANGE (e.g., screen rotation):

  ┌──────────────────────────────────────────────────────────┐
  │  OLD ACTIVITY INSTANCE (dying)                          │
  │                                                         │
  │  onPause()          → "Losing focus"                    │
  │       ↓                                                 │
  │  onStop()           → "No longer visible"               │
  │       ↓                                                 │
  │  onSaveInstanceState() → "Saving state to Bundle!"      │
  │       ↓                  (Android saves this to disk)   │
  │  onDestroy()        → "Goodbye, I am destroyed" 💀      │
  │                                                         │
  └──────────────────────┬───────────────────────────────────┘
                         │
                    Android creates
                    NEW instance
                         │
  ┌──────────────────────▼───────────────────────────────────┐
  │  NEW ACTIVITY INSTANCE (born)                            │
  │                                                         │
  │  onCreate(savedBundle) → "I am new! Restoring state..." │
  │       ↓                  (receives the saved Bundle)    │
  │  onStart()          → "Becoming visible"                │
  │       ↓                                                 │
  │  onRestoreInstanceState() → "Restoring UI state"        │
  │       ↓                                                 │
  │  onResume()         → "Ready for interaction!"          │
  │                                                         │
  └──────────────────────────────────────────────────────────┘
```

> **📌 Important:** The saved Bundle is the **BRIDGE** between the old and new Activity. It carries your data across the destruction.

---

### 🤔 WHY Does Android Destroy and Recreate?

```text
This seems extreme. Why not just resize the existing Activity?

REASON 1: RESOURCE RELOADING
━━━━━━━━━━━━━━━━━━━━━━━━━━━
  When you rotate from portrait to landscape, the screen
  dimensions change dramatically:
    Portrait:  1080 × 2400 pixels (tall and narrow)
    Landscape: 2400 × 1080 pixels (wide and short)

  Your portrait layout might have:
    - A vertical list of items
    - A bottom navigation bar
    - A single-column design

  Your landscape layout might have:
    - A two-column grid
    - A side navigation rail
    - A master-detail view

  These are COMPLETELY DIFFERENT layouts.
  The easiest way to switch is to destroy the old Activity
  (which loaded the portrait layout) and create a new one
  (which loads the landscape layout).

REASON 2: CONSISTENCY
━━━━━━━━━━━━━━━━━━━━
  Destroying and recreating ensures a CLEAN STATE.
  No leftover views, no stale references, no partial updates.
  Everything starts fresh with the correct configuration.

REASON 3: SIMPLICITY FOR THE FRAMEWORK
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  If Android tried to dynamically swap resources in a running
  Activity, it would need to:
    - Replace every View with the correct configuration version
    - Re-measure and re-layout the entire view hierarchy
    - Handle edge cases where views exist in one config but not another
    - Manage memory for both old and new resources simultaneously

  This would be incredibly complex and bug-prone.
  Destroy + recreate is simpler and more reliable.

REASON 4: IT WORKS FOR ALL CONFIG CHANGES
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  The same mechanism handles rotation, language change,
  dark mode, font size, etc. One solution for all cases.
```

---

---

## 🐛 Part 3: Why Configuration Changes Cause Bugs

### ⚠️ The Three Major Problems

```text
PROBLEM 1: DATA LOSS
━━━━━━━━━━━━━━━━━━━━
  All variables in your Activity are stored in RAM.
  When the Activity is destroyed, ALL variables are gone.

  Example:
    var score = 500       ← Gone after rotation!
    var userName = "Rohit" ← Gone after rotation!
    var productList = [...] ← Gone after rotation!

  When the new Activity is created, all variables reset
  to their initial values. The user loses their progress.

PROBLEM 2: RESTARTED OPERATIONS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  If you started a network call, timer, or animation in
  onCreate(), it will RESTART when the Activity is recreated.

  Example:
    override fun onCreate(...) {
        fetchWeatherFromAPI()  // Called on first launch
        // User rotates phone
        // Activity destroyed and recreated
        // fetchWeatherFromAPI() called AGAIN!
        // Wasted network call, wasted battery, wasted data
    }

PROBLEM 3: MEMORY LEAKS
━━━━━━━━━━━━━━━━━━━━━━━
  If a background thread or coroutine holds a reference to
  the OLD Activity, that Activity cannot be garbage collected
  even after onDestroy(). The old Activity leaks in memory.

  Example:
    override fun onCreate(...) {
        Thread {
            Thread.sleep(10000)
            // This thread holds a reference to the OLD Activity
            // Even after rotation destroys it, the thread keeps
            // the old Activity alive in memory → MEMORY LEAK
            textView.text = "Done"  // CRASH! Old Activity is dead
        }.start()
    }
```

---

### 🔴 Real Bug Examples

```kotlin
// ❌ BUG 1: Counter resets on rotation
class CounterActivity : AppCompatActivity() {
    var count = 0  // Stored in Activity's RAM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_counter)

        val tvCount = findViewById<TextView>(R.id.tvCount)
        val btnIncrement = findViewById<Button>(R.id.btnIncrement)

        btnIncrement.setOnClickListener {
            count++
            tvCount.text = "Count: $count"
        }
        tvCount.text = "Count: $count"
    }
}

// User taps button 10 times → count = 10
// User rotates phone → Activity destroyed → count resets to 0
// User sees "Count: 0" → confused and frustrated!


// ❌ BUG 2: Network call restarts on rotation
class WeatherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        // This runs EVERY time Activity is created
        // Including after rotation!
        fetchWeatherData()  // Wasteful duplicate call
    }

    fun fetchWeatherData() {
        lifecycleScope.launch {
            val data = api.getWeather("Bangalore")  // Network call
            showWeather(data)
        }
    }
}

// User opens app → fetches weather (1 API call)
// User rotates phone → fetches weather AGAIN (2nd API call!)
// User rotates back → fetches AGAIN (3rd API call!)
// 3 identical API calls for no reason!


// ❌ BUG 3: Crash from stale reference
class ImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val imageView = findViewById<ImageView>(R.id.imageView)

        Thread {
            Thread.sleep(5000)  // Simulate slow image download
            val bitmap = downloadImage()

            // If user rotated during the 5 seconds,
            // this Activity is DESTROYED. imageView is dead.
            // This line CRASHES with NullPointerException!
            imageView.setImageBitmap(bitmap)
        }.start()
    }
}
```

---

---

## 💾 Part 4: `onSaveInstanceState()` and `onRestoreInstanceState()`

### 📋 Saving Temporary UI State

```text
onSaveInstanceState() is your tool for saving SMALL amounts
of temporary UI state across configuration changes.

WHAT TO SAVE:
  ✅ Scroll positions
  ✅ Selected tab index
  ✅ Form input that is not yet submitted
  ✅ Current page in a ViewPager
  ✅ Expanded/collapsed state of UI elements
  ✅ Small variables (IDs, strings, booleans, counts)

WHAT NOT TO SAVE:
  ❌ Large lists of data (use ViewModel or database)
  ❌ Bitmaps or images (too large for Bundle)
  ❌ Network responses (re-fetch or cache properly)
  ❌ Database query results (re-query from database)
  ❌ Anything over ~500 KB (Bundle size limit)
```

---

### 🏗️ Complete Code Example

```kotlin
class QuizActivity : AppCompatActivity() {

    // UI State that needs to survive rotation:
    private var currentQuestionIndex = 0
    private var score = 0
    private var selectedAnswerIndex = -1
    private var isTimerRunning = false
    private var timeRemainingSeconds = 120

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        // ─── RESTORE STATE ────────────────────────────────
        if (savedInstanceState != null) {
            // Activity was recreated (rotation or process death)
            currentQuestionIndex = savedInstanceState.getInt("QUESTION_INDEX", 0)
            score = savedInstanceState.getInt("SCORE", 0)
            selectedAnswerIndex = savedInstanceState.getInt("SELECTED_ANSWER", -1)
            isTimerRunning = savedInstanceState.getBoolean("TIMER_RUNNING", false)
            timeRemainingSeconds = savedInstanceState.getInt("TIME_REMAINING", 120)

            Log.d("Quiz", "State restored! Question: $currentQuestionIndex, Score: $score")
        } else {
            // Fresh launch — initialize from scratch
            Log.d("Quiz", "Fresh launch")
            startQuiz()
        }

        // Display the current question (works for both fresh and restored):
        displayQuestion(currentQuestionIndex)
        updateScoreDisplay(score)
        updateTimerDisplay(timeRemainingSeconds)
    }

    // ─── SAVE STATE ───────────────────────────────────────
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt("QUESTION_INDEX", currentQuestionIndex)
        outState.putInt("SCORE", score)
        outState.putInt("SELECTED_ANSWER", selectedAnswerIndex)
        outState.putBoolean("TIMER_RUNNING", isTimerRunning)
        outState.putInt("TIME_REMAINING", timeRemainingSeconds)

        Log.d("Quiz", "State saved! Question: $currentQuestionIndex, Score: $score")
    }

    // ─── ALTERNATIVE RESTORE POINT ────────────────────────
    // Called AFTER onStart(), only when savedInstanceState exists.
    // Use this if you need views to be fully laid out before restoring.
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        // Same data available here:
        selectedAnswerIndex = savedInstanceState.getInt("SELECTED_ANSWER", -1)
        if (selectedAnswerIndex != -1) {
            highlightSelectedAnswer(selectedAnswerIndex)
        }
    }

    // ─── GAME LOGIC ───────────────────────────────────────
    fun onAnswerSelected(answerIndex: Int) {
        selectedAnswerIndex = answerIndex
        if (checkAnswer(answerIndex)) {
            score += 10
            updateScoreDisplay(score)
        }
    }

    fun onNextQuestion() {
        currentQuestionIndex++
        selectedAnswerIndex = -1
        displayQuestion(currentQuestionIndex)
    }
}
```

---

### 📋 The Lifecycle Order with Configuration Change

```text
Complete order of calls during screen rotation:

  OLD ACTIVITY:
    1. onPause()
    2. onStop()
    3. onSaveInstanceState()    ← Save your data here!
    4. onDestroy()

  NEW ACTIVITY:
    5. onCreate(savedBundle)    ← Bundle is NOT null
    6. onStart()
    7. onRestoreInstanceState() ← Alternative restore point
    8. onResume()
```

> **💡 Note:** `onRestoreInstanceState()` is **ONLY** called when `savedInstanceState` is not null. It is NOT called on fresh launch. This makes it a safe place to restore state without needing an `if`-check.

---

---

## 🏗️ Part 5: ViewModel — The Configuration Change Survivor

### ⚠️ The Problem ViewModel Solves

```text
onSaveInstanceState() works for SMALL data.
But what about LARGE data like:
  - A list of 500 restaurants from an API
  - A complex form with 20 fields
  - Downloaded images or large objects
  - Active network connections

You CANNOT put these in a Bundle (size limit ~1 MB).
You need something that SURVIVES configuration changes
without being destroyed and recreated.

Enter: VIEWMODEL.
```

---

### 💡 What is ViewModel?

```text
A ViewModel is a class that stores and manages UI-related data
in a lifecycle-aware way. Its key superpower:

  ViewModel SURVIVES configuration changes!

  When the Activity is destroyed and recreated due to rotation,
  the ViewModel is NOT destroyed. It stays in memory.
  The new Activity gets the SAME ViewModel instance.

ANALOGY:
  Activity = A whiteboard in a classroom
  ViewModel = The teacher's notes on their clipboard

  When the whiteboard is erased and replaced (rotation),
  the teacher's clipboard still has all the notes.
  The new whiteboard can copy from the clipboard.

  The clipboard (ViewModel) survives the whiteboard change.
```

---

### 🔀 How ViewModel Survives

```text
BEFORE ROTATION:
  ┌──────────────────────────────────────┐
  │  Activity Instance #1 (PID: 18472)   │
  │  ┌────────────────────────────────┐  │
  │  │  ViewModel (holds restaurant   │  │
  │  │  list, user data, etc.)        │  │
  │  │  restaurantList = [500 items]  │  │
  │  │  userName = "Rohit"            │  │
  │  └────────────────────────────────┘  │
  └──────────────────────────────────────┘

DURING ROTATION:
  Activity #1 is destroyed
  ViewModel is DETACHED but NOT destroyed
  ViewModel stays in memory, managed by the OS

AFTER ROTATION:
  ┌──────────────────────────────────────┐
  │  Activity Instance #2 (NEW object!)  │
  │  ┌────────────────────────────────┐  │
  │  │  SAME ViewModel (reconnected!) │  │
  │  │  restaurantList = [500 items]  │  │ ← Still here!
  │  │  userName = "Rohit"            │  │ ← Still here!
  │  └────────────────────────────────┘  │
  └──────────────────────────────────────┘

  The new Activity gets the EXACT SAME ViewModel.
  No data loss. No re-fetching. No Bundle size limits.
```

---

### 🏗️ Basic ViewModel Code

```kotlin
// STEP 1: Create a ViewModel class
class RestaurantViewModel : ViewModel() {

    // This data SURVIVES rotation!
    var restaurantList: List<Restaurant> = emptyList()
        private set

    var isLoading: Boolean = false
        private set

    var errorMessage: String? = null
        private set

    // Fetch data only once (not on every rotation!)
    fun loadRestaurants() {
        if (restaurantList.isNotEmpty()) {
            // Already loaded! No need to fetch again.
            // This prevents duplicate network calls on rotation.
            Log.d("ViewModel", "Data already loaded, skipping fetch")
            return
        }

        isLoading = true
        Log.d("ViewModel", "Fetching restaurants from API...")

        viewModelScope.launch {
            try {
                val response = api.getRestaurants("Bangalore")
                restaurantList = response
                isLoading = false
            } catch (e: Exception) {
                errorMessage = e.message
                isLoading = false
            }
        }
    }
}

// STEP 2: Use the ViewModel in your Activity
class RestaurantListActivity : AppCompatActivity() {

    // Get the ViewModel — SAME instance survives rotation!
    private val viewModel: RestaurantViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_list)

        // Load data — ViewModel ensures this only happens ONCE
        viewModel.loadRestaurants()

        // Observe the data (simplified — you'll learn LiveData/Flow later)
        displayRestaurants(viewModel.restaurantList)
    }
}

// WHAT HAPPENS ON ROTATION:
// 1. Activity #1 is destroyed
// 2. ViewModel stays alive (restaurantList still has 500 items)
// 3. Activity #2 is created
// 4. viewModel.loadRestaurants() is called
// 5. ViewModel sees list is already populated → SKIPS network call!
// 6. Activity #2 displays the cached data instantly
// Result: No data loss, no duplicate API call, instant rotation!
```

---

### 📊 ViewModel vs `onSaveInstanceState()`

```text
┌───────────────────────┬──────────────────────┬──────────────────────┐
│                       │ onSaveInstanceState  │ ViewModel            │
├───────────────────────┼──────────────────────┼──────────────────────┤
│ Survives rotation?    │ ✅ Yes               │ ✅ Yes               │
├───────────────────────┼──────────────────────┼──────────────────────┤
│ Survives process      │ ✅ Yes (saved to     │ ❌ No (lives in RAM  │
│ death?                │    disk by OS)       │    only)             │
├───────────────────────┼──────────────────────┼──────────────────────┤
│ Data size limit       │ ~500 KB - 1 MB       │ Limited only by      │
│                       │                      │ available RAM        │
├───────────────────────┼──────────────────────┼──────────────────────┤
│ Best for              │ Small UI state:      │ Large data:          │
│                       │ scroll position,     │ lists, API responses,│
│                       │ selected tab,        │ complex objects,     │
│                       │ form drafts          │ user data            │
├───────────────────────┼──────────────────────┼──────────────────────┤
│ Data types            │ Primitives, Strings, │ Any Kotlin/Java      │
│                       │ Parcelable,          │ object               │
│                       │ Serializable         │                      │
├───────────────────────┼──────────────────────┼──────────────────────┤
│ When destroyed?       │ Cleared when back    │ Destroyed when       │
│                       │ stack is cleared     │ Activity is finished │
│                       │ (user swipe)         │ (Back button)        │
├───────────────────────┼──────────────────────┼──────────────────────┤
│ Complexity            │ Simple Bundle API    │ Separate class,      │
│                       │                      │ lifecycle-aware      │
└───────────────────────┴──────────────────────┴──────────────────────┘
```

> **💡 Best Practice:** Use **BOTH** together!
> - **ViewModel** → holds large data (survives rotation)
> - **`onSaveInstanceState`** → holds small UI state (survives process death)
> - **ViewModel + SavedStateHandle** → best of both worlds (Phase 5)

---

---

## 🛠️ Part 6: `android:configChanges` in the Manifest

### ⚠️ The "Quick Fix" That Is Not Recommended

```text
Android provides a way to PREVENT Activity recreation
on specific configuration changes:
```

```xml
<activity
    android:name=".MainActivity"
    android:configChanges="orientation|screenSize|keyboardHidden" />
```

```text
When you add this, Android does NOT destroy and recreate
the Activity when the screen rotates. Instead, it calls
a single callback: onConfigurationChanged()

This SOUNDS like a great solution. But it is generally
NOT RECOMMENDED by Google. Here is why.
```

---

### 🏗️ How It Works

```kotlin
// In AndroidManifest.xml:
// <activity
//     android:name=".VideoPlayerActivity"
//     android:configChanges="orientation|screenSize|keyboardHidden" />

class VideoPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        // Normal setup
    }

    // Called INSTEAD of destroy/recreate when config changes:
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Check what changed:
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("Config", "Switched to landscape!")
            // Manually adjust UI for landscape:
            enterFullscreenMode()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d("Config", "Switched to portrait!")
            // Manually adjust UI for portrait:
            exitFullscreenMode()
        }
    }

    fun enterFullscreenMode() {
        // Hide status bar, expand video to full screen
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        videoView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
    }

    fun exitFullscreenMode() {
        // Show status bar, restore normal layout
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        videoView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
    }
}
```

---

### ❌ Why This is NOT Recommended

```text
REASON 1: YOU MUST HANDLE EVERYTHING MANUALLY
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  When you use configChanges, Android does NOT reload
  resources automatically. You must:
    - Manually load the correct layout for the new orientation
    - Manually update all strings if language changed
    - Manually update all colors if dark mode changed
    - Manually adjust all dimensions if font size changed
  
  This is a LOT of work and very error-prone.

REASON 2: IT ONLY HANDLES WHAT YOU DECLARE
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  If you declare configChanges="orientation|screenSize"
  but the user changes the LANGUAGE, your Activity will
  still be destroyed and recreated for the language change.
  
  You would need to list EVERY possible config change:
  android:configChanges="orientation|screenSize|keyboardHidden|
  locale|layoutDirection|fontScale|uiMode|density|smallestScreenWidth"
  
  This is unmaintainable and fragile.

REASON 3: NEW CONFIG TYPES CAN APPEAR
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Android adds new configuration types in new versions.
  Foldable phones introduced "smallestScreenWidth" changes.
  If you hardcoded the list, you will miss new types.
  Your Activity will be destroyed for the new config type
  even though you thought you handled everything.

REASON 4: IT BREAKS THE RESOURCE SYSTEM
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Android's resource system (res/layout-land/, res/values-night/)
  is designed to work with Activity recreation.
  By preventing recreation, you bypass this entire system.
  You lose the benefit of Android's automatic resource selection.

WHEN IS IT ACCEPTABLE?
  ✅ Video players (video must not restart on rotation)
  ✅ Camera preview (camera cannot be reinitialized quickly)
  ✅ Games (game loop cannot be interrupted)
  ✅ Very specific, well-understood use cases
  
  For 95% of Activities, let Android handle it normally
  and use ViewModel + onSaveInstanceState() for state.
```

---

---

## 🎬 Part 7: Real Example — Video Player Rotation

### 📖 The Scenario

```text
SCENARIO:
  User is watching a cooking tutorial video in your food app.
  The video is at 3:42 out of 10:00 minutes.
  User rotates their phone to landscape for a better view.

WHAT SHOULD HAPPEN:
  ✅ Video continues playing from 3:42
  ✅ Video expands to fullscreen in landscape
  ✅ Controls adjust for landscape layout
  ✅ No buffering, no restart, no interruption

WHAT SHOULD NOT HAPPEN:
  ❌ Video restarts from 0:00
  ❌ Video pauses and shows loading spinner
  ❌ User loses their playback position
  ❌ Audio continues but video resets (desync)
```

---

### ❌ The Wrong Way (Default Behavior)

```kotlin
// ❌ WITHOUT handling configuration changes:

class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private var videoUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        videoView = findViewById(R.id.videoView)
        videoUrl = intent.getStringExtra("VIDEO_URL") ?: ""

        // This runs on EVERY Activity creation, including rotation!
        videoView.setVideoPath(videoUrl)
        videoView.start()  // Video starts from 0:00 every time!
    }
}

// USER EXPERIENCE:
// 1. User watches video to 3:42
// 2. User rotates phone
// 3. Activity destroyed → video stops
// 4. New Activity created → video starts from 0:00
// 5. User is frustrated: "I was at 3:42!"
```

---

### ✅ The Right Way — Using `configChanges` (Acceptable Here!)

```kotlin
// ✅ FOR VIDEO PLAYERS, configChanges IS acceptable:

// AndroidManifest.xml:
// <activity
//     android:name=".VideoPlayerActivity"
//     android:configChanges="orientation|screenSize|smallestScreenWidth"
//     android:screenOrientation="unspecified" />

class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var containerPortrait: View
    private lateinit var containerLandscape: View
    private var videoUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        videoView = findViewById(R.id.videoView)
        containerPortrait = findViewById(R.id.containerPortrait)
        containerLandscape = findViewById(R.id.containerLandscape)
        videoUrl = intent.getStringExtra("VIDEO_URL") ?: ""

        // This runs ONLY ONCE (no recreation on rotation!)
        videoView.setVideoPath(videoUrl)
        videoView.start()

        // Adjust initial layout based on current orientation:
        adjustLayoutForOrientation(resources.configuration.orientation)
    }

    // Called on rotation INSTEAD of destroy/recreate:
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Video keeps playing! No interruption!
        // Just adjust the UI layout:
        adjustLayoutForOrientation(newConfig.orientation)
    }

    private fun adjustLayoutForOrientation(orientation: Int) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // LANDSCAPE: Fullscreen video, hide extra UI
            containerPortrait.visibility = View.GONE
            containerLandscape.visibility = View.VISIBLE

            // Make video fullscreen
            videoView.layoutParams = videoView.layoutParams.apply {
                height = ViewGroup.LayoutParams.MATCH_PARENT
                width = ViewGroup.LayoutParams.MATCH_PARENT
            }

            // Hide system bars for immersive experience
            hideSystemBars()

        } else {
            // PORTRAIT: Normal layout with video on top
            containerPortrait.visibility = View.VISIBLE
            containerLandscape.visibility = View.GONE

            // Restore normal video size
            videoView.layoutParams = videoView.layoutParams.apply {
                height = (resources.displayMetrics.widthPixels * 9 / 16)
                width = ViewGroup.LayoutParams.MATCH_PARENT
            }

            showSystemBars()
        }
    }

    private fun hideSystemBars() {
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )
    }

    private fun showSystemBars() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }
}

// USER EXPERIENCE:
// 1. User watches video to 3:42 in portrait
// 2. User rotates phone to landscape
// 3. onConfigurationChanged() is called
// 4. Video CONTINUES from 3:42 (no restart!)
// 5. Layout adjusts to fullscreen landscape
// 6. User is happy 😊
```

---

### ✅ Alternative: ViewModel Approach (Without `configChanges`)

```kotlin
// ✅ ALTERNATIVE: Let Activity recreate but save playback position

class VideoViewModel : ViewModel() {
    var playbackPositionMs: Int = 0
    var videoUrl: String = ""
    var isPlaying: Boolean = false
}

class VideoPlayerActivity : AppCompatActivity() {

    private val viewModel: VideoViewModel by viewModels()
    private lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        videoView = findViewById(R.id.videoView)

        if (viewModel.videoUrl.isEmpty()) {
            // First launch — get URL from Intent
            viewModel.videoUrl = intent.getStringExtra("VIDEO_URL") ?: ""
        }

        videoView.setVideoPath(viewModel.videoUrl)

        // Restore playback position after rotation!
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.seekTo(viewModel.playbackPositionMs)
            if (viewModel.isPlaying) {
                mediaPlayer.start()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Save current position before Activity might be destroyed
        viewModel.playbackPositionMs = videoView.currentPosition
        viewModel.isPlaying = videoView.isPlaying
        videoView.pause()
    }
}

// This approach:
// ✅ Lets Android handle resource reloading naturally
// ✅ Video position is saved in ViewModel (survives rotation)
// ✅ Brief pause during rotation (acceptable for most apps)
// ❌ Video does briefly pause and resume (not seamless)
// For truly seamless playback, use configChanges approach above.
```

---

---

## 📋 Complete Summary

```text
┌──────────────────────────────────────────────────────────────┐
│           CONFIGURATION CHANGES SUMMARY                      │
├───────────────────────┬──────────────────────────────────────┤
│ CONCEPT               │ KEY POINTS                           │
├───────────────────────┼──────────────────────────────────────┤
│ Configuration Change  │ Any change to device environment     │
│                       │ Rotation, language, dark mode,       │
│                       │ font size, keyboard, screen size     │
├───────────────────────┼──────────────────────────────────────┤
│ Default Behavior      │ Activity is DESTROYED and RECREATED  │
│                       │ Old instance dies, new one is born   │
│                       │ Resources reloaded for new config    │
├───────────────────────┼──────────────────────────────────────┤
│ Why Destroy/Recreate  │ Clean resource reloading             │
│                       │ Consistent state                     │
│                       │ Simpler framework design             │
│                       │ Works for all config types           │
├───────────────────────┼──────────────────────────────────────┤
│ Bugs It Causes        │ Data loss (variables reset)          │
│                       │ Restarted operations (API calls)     │
│                       │ Memory leaks (stale references)      │
├───────────────────────┼──────────────────────────────────────┤
│ onSaveInstanceState() │ Save small UI state to Bundle        │
│                       │ Survives rotation AND process death  │
│                       │ Size limit ~1 MB                     │
│                       │ Best for: scroll pos, tab index      │
├───────────────────────┼──────────────────────────────────────┤
│ ViewModel             │ Survives rotation (NOT process death)│
│                       │ No size limit (RAM only)             │
│                       │ Best for: lists, API data, objects   │
│                       │ Prevents duplicate network calls     │
├───────────────────────┼──────────────────────────────────────┤
│ configChanges         │ Prevents Activity recreation         │
│                       │ Must handle everything manually      │
│                       │ NOT recommended for most cases       │
│                       │ OK for: video, camera, games         │
├───────────────────────┼──────────────────────────────────────┤
│ Best Practice         │ ViewModel for large data             │
│                       │ + onSaveInstanceState for small UI   │
│                       │ + SavedStateHandle for both          │
│                       │ Let Android recreate by default      │
└───────────────────────┴──────────────────────────────────────┘
```

---

---

## 📝 Quiz — Test Your Understanding

> Answer these in your head or write them out before checking!

---

### ❓ Question 1: Configuration Change Fundamentals

```text
a) List at least 5 types of configuration changes that can
   happen on an Android device. For each, describe the
   real-world user action that triggers it.

b) Explain in your own words WHY Android destroys and
   recreates the Activity during a configuration change.
   Why not just "resize" the existing Activity?
   Give at least 2 technical reasons.

c) A developer says: "Configuration changes only happen
   when the user rotates the screen."
   Is this correct? Give 3 examples of configuration
   changes that have NOTHING to do with rotation.

d) Your app has these resource folders:
     res/layout/activity_main.xml
     res/layout-land/activity_main.xml
     res/values/strings.xml
     res/values-hi/strings.xml
     res/values-night/colors.xml
   
   For each configuration change, which resource files
   will Android load AFTER the change?
   1. User rotates to landscape
   2. User changes language to Hindi
   3. User enables dark mode
   4. User rotates to landscape AND enables dark mode
```

---

### ❓ Question 2: Lifecycle During Configuration Change

```text
a) Write the EXACT sequence of lifecycle method calls
   that happen during a screen rotation.
   Include both the OLD Activity dying and the NEW one being born.
   Mark which method receives the saved Bundle.

b) A developer adds logging to their Activity:

   override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
       Log.d("Life", "onCreate: saved=${savedInstanceState != null}")
   }
   override fun onStart() { Log.d("Life", "onStart") }
   override fun onResume() { Log.d("Life", "onResume") }
   override fun onPause() { Log.d("Life", "onPause") }
   override fun onStop() { Log.d("Life", "onStop") }
   override fun onDestroy() { Log.d("Life", "onDestroy") }
   override fun onSaveInstanceState(outState: Bundle) {
       super.onSaveInstanceState(outState)
       Log.d("Life", "onSaveInstanceState")
   }
   override fun onRestoreInstanceState(savedInstanceState: Bundle) {
       super.onRestoreInstanceState(savedInstanceState)
       Log.d("Life", "onRestoreInstanceState")
   }

   The user opens the app, then rotates the screen.
   Write the EXACT log output in order.

c) Is the Activity object after rotation the SAME object
   as before rotation? How can you verify this?
   (Hint: Think about object identity — === vs ==)

d) How many times is onCreate() called if the user
   rotates the phone 5 times? Explain why.
```

---

### ❓ Question 3: Bug Identification and Fixing

Read this code and find **ALL** configuration change bugs:

```kotlin
class ShoppingActivity : AppCompatActivity() {

    var cartItems = mutableListOf<String>()
    var totalPrice = 0.0
    var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping)

        loadProductsFromAPI()

        val btnAdd = findViewById<Button>(R.id.btnAdd)
        btnAdd.setOnClickListener {
            cartItems.add("Biryani")
            totalPrice += 280.0
            updateCartDisplay()
        }
    }

    fun loadProductsFromAPI() {
        isLoading = true
        lifecycleScope.launch {
            val products = api.getProducts()  // Network call
            displayProducts(products)
            isLoading = false
        }
    }

    fun updateCartDisplay() {
        val tvCart = findViewById<TextView>(R.id.tvCart)
        tvCart.text = "Cart: ${cartItems.size} items | ₹$totalPrice"
    }
}
```

```text
a) What happens to cartItems and totalPrice when the user
   rotates the screen? Why?

b) What happens to the API call when the user rotates?
   How many times will loadProductsFromAPI() be called
   if the user rotates 3 times?

c) Fix this code using BOTH onSaveInstanceState() and ViewModel.
   - Use ViewModel for the product list (large data)
   - Use onSaveInstanceState for the cart state (small data)
   - Prevent duplicate API calls on rotation
   Write the complete corrected code.

d) After your fix, trace what happens step by step when:
   1. User opens the app (fresh launch)
   2. User adds 3 items to cart
   3. User rotates the phone
   4. User adds 1 more item
   5. User rotates back to portrait
```

---

### ❓ Question 4: ViewModel vs `onSaveInstanceState`

```text
a) Complete this comparison table:

                    │ onSaveInstanceState │ ViewModel
   ─────────────────┼─────────────────────┼──────────
   Survives rotation│                     │
   Survives process │                     │
     death          │                     │
   Data size limit  │                     │
   Data types       │                     │
   When destroyed   │                     │
   Best used for    │                     │

b) A developer stores a list of 2000 Restaurant objects
   in onSaveInstanceState():

   override fun onSaveInstanceState(outState: Bundle) {
       outState.putParcelableArrayList("restaurants", restaurantList)
       // 2000 objects × ~1 KB each = ~2 MB
   }

   What will happen? Why?
   What is the correct approach?

c) A developer stores the user's login token in a ViewModel:

   class AuthViewModel : ViewModel() {
       var authToken: String = ""
   }

   The app is killed by the OS due to low memory.
   When the user reopens the app, is the token still there?
   Why or why not?
   What should the developer use instead for persistent data?

d) Explain the "ViewModel + SavedStateHandle" approach.
   How does it combine the benefits of both ViewModel
   and onSaveInstanceState?
   (Brief explanation — you will learn this deeply in Phase 5)
```

---

### ❓ Question 5: `configChanges` and Real-World Design

```text
a) What does android:configChanges="orientation|screenSize"
   do in the Manifest?
   What lifecycle method is called INSTEAD of destroy/recreate?
   What must the developer do inside that method?

b) A developer adds configChanges to ALL Activities in their
   app to "avoid the hassle of handling rotation."
   List 3 problems this will cause.

c) DESIGN CHALLENGE: You are building a Camera app.
   The camera preview must NOT restart when the user rotates.
   The camera hardware takes 2-3 seconds to initialize.
   
   Should you use:
   Option A: Default behavior + ViewModel to save state
   Option B: configChanges to prevent recreation
   Option C: Both
   
   Justify your choice. What are the trade-offs?

d) DESIGN CHALLENGE: You are building a Chat app.
   The user is typing a long message in an EditText.
   They rotate the phone.
   
   The EditText has an android:id.
   
   Will the typed text survive rotation automatically?
   Why or why not?
   What about the scroll position of the chat messages
   in a RecyclerView above the EditText?
   Will that survive automatically?
   What do you need to do to ensure the chat scroll
   position is preserved?

e) WRITE A TESTING CHECKLIST for configuration changes.
   List at least 8 things you should test when your
   Activity handles configuration changes:
   - What to rotate
   - What data to check
   - What operations to verify
   - What edge cases to consider
```