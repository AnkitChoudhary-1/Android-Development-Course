# 🚀 Complete Guide to Activity, Intent, and AndroidManifest.xml

---

## 📱 Part 1: What is an Activity?

### 💡 The Simple Definition

```text
An ACTIVITY is a single screen in your Android app.

Every screen you see on your phone is an Activity:
  - The login screen → LoginActivity
  - The home feed → HomeActivity
  - The settings page → SettingsActivity
  - The chat screen → ChatActivity

Think of your app as a HOUSE:
  - The house = your entire app
  - Each room = one Activity
  - The door between rooms = Intent (you'll learn this next)

┌─────────────────────────────────────────────┐
│              YOUR APP (House)               │
│                                             │
│  ┌──────────┐   ┌──────────┐   ┌────────┐  │
│  │  Login   │ → │   Home   │ → │Profile │  │
│  │ Activity │   │ Activity │   │Activity│  │
│  │ (Room 1) │   │ (Room 2) │   │(Room 3)│  │
│  └──────────┘   └──────────┘   └────────┘  │
│       ↑                          ↓          │
│  ┌──────────┐              ┌──────────┐     │
│  │ Register │              │ Settings │     │
│  │ Activity │              │ Activity │     │
│  │ (Room 4) │              │ (Room 5) │     │
│  └──────────┘              └──────────┘     │
└─────────────────────────────────────────────┘
```

---

### 🏗️ The Activity Class

```text
In Android, every screen MUST extend the Activity class
(or its subclass AppCompatActivity).

AppCompatActivity is the modern version that provides:
  - Backward compatibility (works on older Android versions)
  - Support for the Action Bar / Toolbar
  - Material Design features
  - Theme support

You will ALMOST ALWAYS use AppCompatActivity.
```

**The Basic Structure:**

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This is where your screen is set up
        // Called ONCE when the Activity is first created
    }
}
```

```text
BREAKING IT DOWN:
  class MainActivity          → Your screen's name
  : AppCompatActivity()       → Inherits from Android's Activity class
  override fun onCreate()     → Called when the screen is created
  savedInstanceState          → Restores data if Activity was destroyed
  super.onCreate()            → Calls the parent class's setup code
                                (MUST be called first!)
```

---

### 🔄 How an Activity is Created and Displayed

```text
WHEN THE USER TAPS YOUR APP ICON:

Step 1: Android OS reads your AndroidManifest.xml
Step 2: Finds the Activity marked as LAUNCHER
Step 3: Creates a new process for your app (Linux process!)
Step 4: Creates the Main Thread (UI Thread)
Step 5: Creates an instance of your Launcher Activity
Step 6: Calls onCreate() on it
Step 7: Your screen appears!

THE LIFECYCLE (simplified):

  onCreate()     → Activity is being created (set up UI here)
       ↓
  onStart()      → Activity becomes visible to user
       ↓
  onResume()     → Activity is in foreground, user can interact
       ↓
  [USER IS USING YOUR APP]
       ↓
  onPause()      → User is leaving (another app coming to front)
       ↓
  onStop()       → Activity is no longer visible
       ↓
  onDestroy()    → Activity is being destroyed (memory freed)

You learned about this in the "How Android Works" lesson!
```

---

### 🖼️ `setContentView()` — Setting the Screen Content

```text
setContentView() tells Android:
"This is what my screen should look like."

There are TWO ways to do this:
```

**METHOD 1: XML Layout (Traditional Way)**

You design the UI in an XML file, then load it.

```xml
<!-- activity_main.xml (in res/layout/ folder): -->
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:gravity="center"
    android:padding="16dp">

    <TextView
        android:id="@+id/tvGreeting"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello, World!"
        android:textSize="24sp" />

    <Button
        android:id="@+id/btnClick"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Click Me" />

</LinearLayout>
```

```kotlin
// MainActivity.kt:
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load the XML layout onto this screen:
        setContentView(R.layout.activity_main)
        //              ↑ R = Resources class (auto-generated)
        //                layout = folder name
        //                activity_main = file name (without .xml)

        // Find views by their ID and interact with them:
        val tvGreeting = findViewById<TextView>(R.id.tvGreeting)
        val btnClick = findViewById<Button>(R.id.btnClick)

        btnClick.setOnClickListener {
            tvGreeting.text = "Button was clicked!"
        }
    }
}
```

**METHOD 2: Jetpack Compose (Modern Way)**

You build the UI directly in Kotlin code. No XML needed.

```kotlin
class MainActivity : ComponentActivity() {  // Note: ComponentActivity, not AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set Compose content directly:
        setContent {
            // This is Kotlin code that builds the UI!
            MyAppTheme {
                Surface {
                    GreetingScreen(name = "Rohit")
                }
            }
        }
    }
}

@Composable
fun GreetingScreen(name: String) {
    var clicked by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = if (clicked) "Button was clicked!" else "Hello, $name!",
            fontSize = 24.sp
        )
        Button(onClick = { clicked = true }) {
            Text("Click Me")
        }
    }
}
```

> **💡 Which Should You Learn?** Both! But start with XML (easier to understand the concepts). Most existing Android apps use XML. New apps increasingly use Compose. This lesson uses XML so you understand the fundamentals.

---

---

## 📨 Part 2: What is an Intent?

### 🤔 The Problem Intents Solve

```text
Your app has multiple screens (Activities).
How do you navigate from one screen to another?

In a normal program, you might just call a function:
  showSettingsScreen()

But Android is different. Remember:
  - Each Activity is managed by the Android OS
  - The OS controls the lifecycle
  - Activities can be in different apps!
  - The OS needs to know WHAT you want to do

So Android created INTENTS.

AN INTENT is a MESSAGE to the Android OS that says:
"I want to do THIS action" or "I want to go to THIS screen"

ANALOGY:
  Intent = A taxi ride request

  You (Activity A) tell the taxi driver (Android OS):
  "Take me to the airport" (Activity B)

  The taxi driver (OS) handles the navigation.
  You don't drive yourself — the OS manages the transition.

  You can also say:
  "Take me to ANY restaurant" (Implicit Intent)
  The OS finds all restaurants (apps) and lets you choose.
```

---

### 🎯 Explicit Intent — Navigating Within Your App

```text
EXPLICIT INTENT = "I want to go to THIS SPECIFIC Activity"

You name the exact destination. Used for navigation
within your OWN app.

SCENARIO: User taps "Login" button → go to LoginActivity
```

```kotlin
// In MainActivity.kt (Screen A):
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            // CREATE an explicit Intent:
            val intent = Intent(this, LoginActivity::class.java)
            //              ↑ context (current Activity)
            //                         ↑ destination Activity class

            // START the new Activity:
            startActivity(intent)
            // The OS creates LoginActivity and displays it
        }
    }
}
```

```text
WHAT HAPPENS WHEN startActivity() IS CALLED:
  1. Your app creates the Intent object
  2. startActivity() sends it to the Android OS
  3. OS reads the Intent: "Go to LoginActivity"
  4. OS checks your AndroidManifest.xml: "Is LoginActivity declared?"
  5. OS creates a new instance of LoginActivity
  6. OS calls LoginActivity.onCreate()
  7. LoginActivity's screen appears on top of MainActivity
  8. MainActivity goes to background (onPause → onStop)

THE BACK BUTTON:
  When user presses Back on LoginActivity:
  1. LoginActivity is destroyed (onDestroy)
  2. MainActivity comes back to foreground (onRestart → onStart → onResume)
  3. User sees MainActivity again

This is called the ACTIVITY BACK STACK:
  ┌──────────────┐
  │ LoginActivity│  ← Currently visible (top of stack)
  ├──────────────┤
  │ MainActivity │  ← In background (bottom of stack)
  └──────────────┘
  Press Back → LoginActivity pops off → MainActivity visible
```

---

### ✉️ Passing Data Between Activities — Intent Extras

```text
Often you need to send data from Screen A to Screen B.
For example: passing a username from Login to Home screen.

Intents have a built-in mechanism called EXTRAS.
Extras are key-value pairs attached to the Intent.

ANALOGY:
  Intent = Envelope
  Extras = The letter inside the envelope
  Key = Label on the letter ("username")
  Value = The actual data ("Rohit")
```

**Sending Data (Screen A → Screen B):**

```kotlin
// MainActivity.kt (Screen A — sending data):
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnGoToProfile = findViewById<Button>(R.id.btnProfile)

        btnGoToProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)

            // PUT extras (data) into the Intent:
            intent.putExtra("EXTRA_USERNAME", "Rohit Kumar")
            intent.putExtra("EXTRA_USER_ID", 1042)
            intent.putExtra("EXTRA_IS_PREMIUM", true)
            intent.putExtra("EXTRA_RATING", 4.5)
            //    ↑ key (String)        ↑ value (various types)

            startActivity(intent)
        }
    }
}
```

**Receiving Data (Screen B — reading data):**

```kotlin
// ProfileActivity.kt (Screen B — receiving data):
class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // GET the Intent that started this Activity:
        val receivedIntent = intent  // 'intent' is a built-in property

        // READ the extras using the SAME keys:
        val username = receivedIntent.getStringExtra("EXTRA_USERNAME")
        val userId = receivedIntent.getIntExtra("EXTRA_USER_ID", 0)
        //                                         ↑ default value if key missing
        val isPremium = receivedIntent.getBooleanExtra("EXTRA_IS_PREMIUM", false)
        val rating = receivedIntent.getDoubleExtra("EXTRA_RATING", 0.0)

        // Use the data:
        val tvName = findViewById<TextView>(R.id.tvName)
        tvName.text = "Welcome, $username!"

        val tvInfo = findViewById<TextView>(R.id.tvInfo)
        tvInfo.text = "User ID: $userId | Premium: $isPremium | Rating: $rating"
    }
}
```

```text
AVAILABLE putExtra/getExtra TYPES:
  putExtra("key", "string")        → getStringExtra("key")
  putExtra("key", 42)              → getIntExtra("key", default)
  putExtra("key", 3.14)            → getDoubleExtra("key", default)
  putExtra("key", true)            → getBooleanExtra("key", default)
  putExtra("key", 100L)            → getLongExtra("key", default)
  putExtra("key", arrayOf("a","b"))→ getStringArrayExtra("key")
  putExtra("key", listOf(1,2,3))   → getIntegerArrayListExtra("key")
```

> **⚠️ Important:** Keys must match **EXACTLY** (case-sensitive). Best practice — define keys as constants:

```kotlin
companion object {
    const val EXTRA_USERNAME = "com.rohit.app.EXTRA_USERNAME"
    // Using full package name prevents key collisions
}
```

---

### 🌐 Implicit Intent — Asking Another App to Do Something

```text
IMPLICIT INTENT = "I want to do THIS ACTION, but I don't
                   care WHICH app does it"

You describe the ACTION, not the destination.
The Android OS finds all apps that can handle it
and shows a chooser to the user.

EXAMPLES:
  - "Open this URL" → OS opens Chrome/Firefox/Samsung Browser
  - "Send this text" → OS opens WhatsApp/Gmail/SMS/Telegram
  - "Take a photo" → OS opens Camera app
  - "Pick a contact" → OS opens Contacts app
  - "Show this location" → OS opens Google Maps
```

**Example 1: Open a website**

```kotlin
val url = "https://www.google.com"
val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//              ↑ Action: "I want to VIEW something"
//                              ↑ Data: the URL to view
startActivity(intent)
// OS shows all browsers installed → user picks one
```

**Example 2: Share text**

```kotlin
val shareText = "Check out this amazing food delivery app!"
val intent = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_TEXT, shareText)
}
startActivity(Intent.createChooser(intent, "Share via"))
// OS shows: WhatsApp, Gmail, Telegram, Twitter, etc.
```

**Example 3: Make a phone call**

```kotlin
val phoneNumber = "9876543210"
val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
startActivity(intent)
// Opens the phone dialer with the number pre-filled
```

**Example 4: Send an email**

```kotlin
val intent = Intent(Intent.ACTION_SENDTO).apply {
    data = Uri.parse("mailto:rohit@gmail.com")
    putExtra(Intent.EXTRA_SUBJECT, "Order Confirmation")
    putExtra(Intent.EXTRA_TEXT, "Your order #1234 is confirmed!")
}
startActivity(intent)
// Opens Gmail/Outlook with pre-filled email
```

**Example 5: Open Google Maps**

```kotlin
val location = "Bangalore, India"
val intent = Intent(
    Intent.ACTION_VIEW,
    Uri.parse("geo:0,0?q=${Uri.encode(location)}")
)
startActivity(intent)
// Opens Google Maps showing Bangalore
```

**Example 6: Pick a photo from gallery**

```kotlin
val intent = Intent(Intent.ACTION_PICK).apply {
    type = "image/*"  // any image type
}
startActivity(intent)
// Opens gallery app to pick a photo
```

> **🛡️ Safety Check** — Always verify an app can handle the Intent:

```kotlin
val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com"))
if (intent.resolveActivity(packageManager) != null) {
    startActivity(intent)  // Safe — at least one app can handle it
} else {
    Toast.makeText(this, "No app found to handle this", Toast.LENGTH_SHORT).show()
}
// Without this check, your app CRASHES if no app can handle the Intent!
```

---

### 🔄 `startActivityForResult()` and the Modern Activity Result API

```text
SOMETIMES you need to get a RESULT back from the second Activity.

Example: Screen A opens Screen B to pick a photo.
         Screen B lets user pick a photo.
         Screen B sends the photo BACK to Screen A.
```

**Old Way (deprecated but you'll see it in old code):**

```kotlin
// Screen A — start for result:
val REQUEST_CODE_PHOTO = 1001
startActivityForResult(intent, REQUEST_CODE_PHOTO)

// Screen A — receive result:
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == REQUEST_CODE_PHOTO && resultCode == RESULT_OK) {
        val photoUri = data?.data
        // Use the photo
    }
}

// Screen B — send result back:
val resultIntent = Intent().apply {
    putExtra("selected_photo", photoUri)
}
setResult(RESULT_OK, resultIntent)
finish()  // Close Screen B, return to Screen A
```

**Modern Way — Activity Result API (recommended):**

```kotlin
// Screen A — register a launcher BEFORE onCreate:
class MainActivity : AppCompatActivity() {

    // Register the result launcher (do this at class level):
    private val pickPhotoLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { photoUri: Uri? ->
        // This callback runs when Screen B returns a result
        if (photoUri != null) {
            // User picked a photo!
            imageView.setImageURI(photoUri)
        } else {
            // User cancelled
            Toast.makeText(this, "No photo selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnPickPhoto = findViewById<Button>(R.id.btnPickPhoto)
        btnPickPhoto.setOnClickListener {
            // Launch the photo picker:
            pickPhotoLauncher.launch("image/*")
        }
    }
}
```

```text
COMMON ActivityResultContracts:
  GetContent()          → Pick a file (photo, document)
  TakePicturePreview()  → Take a photo with camera
  StartActivityForResult() → Custom result from any Activity
  RequestPermission()   → Ask for a runtime permission
  OpenDocument()        → Open a document file
```

**Custom Result between your own Activities (Modern Way):**

```kotlin
// Screen A:
private val profileLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) { result ->
    if (result.resultCode == RESULT_OK) {
        val updatedName = result.data?.getStringExtra("UPDATED_NAME")
        tvName.text = updatedName
    }
}

// Launch:
profileLauncher.launch(Intent(this, ProfileActivity::class.java))

// Screen B (ProfileActivity) — send result back:
val resultIntent = Intent().apply {
    putExtra("UPDATED_NAME", "Rohit K.")
}
setResult(RESULT_OK, resultIntent)
finish()
```

---

---

## 📋 Part 3: AndroidManifest.xml

### 📄 What is the Manifest?

```text
The AndroidManifest.xml is the IDENTITY CARD of your app.

It tells the Android OS EVERYTHING about your app:
  - What is the app's name?
  - What screens (Activities) does it have?
  - Which screen opens first?
  - What permissions does it need?
  - What is the minimum Android version?
  - What hardware features does it use?

LOCATION: app/src/main/AndroidManifest.xml

WITHOUT a manifest, Android will NOT install your app.
It is MANDATORY. Every Android app must have one.

ANALOGY:
  The Manifest is like a RESTAURANT LICENSE:
  - Restaurant name (app name)
  - Address (package name)
  - What food they serve (features/permissions)
  - Opening hours (minimum Android version)
  - Health department approval (signing certificate)
  Without this license, the restaurant cannot operate.
```

---

### 📝 Complete AndroidManifest.xml — Explained Line by Line

```xml
<?xml version="1.0" encoding="utf-8"?>

<!-- ROOT ELEMENT -->
<manifest
    xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.rohit.foodapp">
    <!-- ↑ PACKAGE NAME: Unique identifier for your app
         Like a domain name: com.company.appname
         No two apps on Play Store can have the same package name
         This is how Android identifies YOUR app uniquely -->

    <!-- ═══════════════════════════════════════════════════ -->
    <!-- PERMISSIONS: What your app is allowed to access     -->
    <!-- ═══════════════════════════════════════════════════ -->

    <uses-permission android:name="android.permission.INTERNET" />
    <!-- ↑ Required to make network calls (API requests)
         Without this, Retrofit/OkHttp will FAIL -->

    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <!-- ↑ Required to get precise GPS location
         Used for "restaurants near me" feature -->

    <uses-permission android:name="android.permission.CAMERA" />
    <!-- ↑ Required to use the camera
         Used for scanning QR codes or taking food photos -->

    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <!-- ↑ Required to read files from device storage -->

    <!-- ═══════════════════════════════════════════════════ -->
    <!-- APPLICATION: App-wide settings                      -->
    <!-- ═══════════════════════════════════════════════════ -->

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        <!-- ↑ App icon shown on home screen -->
        android:label="@string/app_name"
        <!-- ↑ App name shown under the icon
             Defined in res/values/strings.xml -->
        android:roundIcon="@mipmap/ic_launcher_round"
        <!-- ↑ Round version of the icon (for newer phones) -->
        android:supportsRtl="true"
        <!-- ↑ Support right-to-left languages (Arabic, Hebrew) -->
        android:theme="@style/Theme.FoodApp">
        <!-- ↑ App-wide visual theme (colors, styles)
             Defined in res/values/themes.xml -->

        <!-- ═══════════════════════════════════════════════ -->
        <!-- ACTIVITIES: Declare every screen in your app    -->
        <!-- ═══════════════════════════════════════════════ -->

        <!-- ACTIVITY 1: Main Screen (LAUNCHER) -->
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <!-- ↑ exported="true" means OTHER apps can launch this
                 Required for the launcher activity -->

            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <!-- ↑ This is the MAIN entry point of the app -->

                <category android:name="android.intent.category.LAUNCHER" />
                <!-- ↑ Show this activity in the app launcher
                     (home screen app drawer) -->
            </intent-filter>
            <!-- The intent-filter with MAIN + LAUNCHER tells Android:
                 "When user taps the app icon, open THIS Activity" -->
        </activity>

        <!-- ACTIVITY 2: Login Screen -->
        <activity
            android:name=".LoginActivity"
            android:exported="false" />
            <!-- ↑ exported="false" means only YOUR app can open this
                 No intent-filter = not accessible from outside -->

        <!-- ACTIVITY 3: Profile Screen -->
        <activity
            android:name=".ProfileActivity"
            android:exported="false"
            android:label="My Profile"
            android:parentActivityName=".MainActivity" />
            <!-- ↑ parentActivityName enables the UP button (← arrow)
                 in the action bar to navigate back to MainActivity -->

        <!-- ACTIVITY 4: Restaurant Detail Screen -->
        <activity
            android:name=".RestaurantDetailActivity"
            android:exported="false"
            android:screenOrientation="portrait" />
            <!-- ↑ Forces this screen to stay in portrait mode -->

    </application>

</manifest>
```

---

### 🔑 Key Manifest Concepts Explained

```text
1. PACKAGE NAME (android:package)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  - Unique ID for your app across all of Android
  - Format: com.companyname.appname
  - Examples:
      com.whatsapp       → WhatsApp
      com.instagram.android → Instagram
      com.rohit.foodapp  → Your app
  - CANNOT be changed after publishing on Play Store
  - Used in: app signing, data storage paths, permissions

2. LAUNCHER ACTIVITY (MAIN + LAUNCHER)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  - The Activity that opens when user taps your app icon
  - Must have this intent-filter:
      <action android:name="android.intent.action.MAIN" />
      <category android:name="android.intent.category.LAUNCHER" />
  - Only ONE Activity should have this (usually MainActivity)
  - Without it, your app installs but has no icon on home screen!

3. PERMISSIONS (uses-permission)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  - Declares what your app needs access to
  - Some are granted automatically (INTERNET)
  - Some require RUNTIME permission (CAMERA, LOCATION)
    → You must ask the user at runtime with a dialog
  
  COMMON PERMISSIONS:
    INTERNET              → Make network calls (auto-granted)
    ACCESS_FINE_LOCATION  → GPS location (runtime)
    CAMERA                → Use camera (runtime)
    READ_CONTACTS         → Read contacts (runtime)
    RECORD_AUDIO          → Use microphone (runtime)
    READ_EXTERNAL_STORAGE → Read files (runtime)
    POST_NOTIFICATIONS    → Show notifications (runtime, Android 13+)

4. EXPORTED (android:exported)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  - true  = Other apps can launch this Activity
  - false = Only your own app can launch this Activity
  - Launcher Activity MUST be exported="true"
  - All other Activities should be exported="false" (security!)
  - Required since Android 12 (API 31)
```

---

---

## 🛠️ Part 4: Complete Real Example — Screen A Opens Screen B

### 📋 The Scenario

```text
Screen A (MainActivity):
  - Has an EditText for username
  - Has a "Go to Profile" button
  - When button is tapped → open Screen B, pass the username

Screen B (ProfileActivity):
  - Receives the username from Screen A
  - Displays "Welcome, [username]!"
  - Has a "Send Rating Back" button
  - When tapped → close Screen B, send rating back to Screen A

Screen A receives the rating and displays it.
```

---

### 📄 Step 1: AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.rohit.myapp">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/Theme.MyApp">

        <!-- Screen A: Launcher -->
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <!-- Screen B: Profile (NOT a launcher) -->
        <activity
            android:name=".ProfileActivity"
            android:exported="false"
            android:label="User Profile"
            android:parentActivityName=".MainActivity" />

    </application>
</manifest>
```

---

### 🖼️ Step 2: Layout Files

```xml
<!-- res/layout/activity_main.xml (Screen A) -->
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:gravity="center"
    android:padding="24dp">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Welcome to MyApp!"
        android:textSize="24sp"
        android:textStyle="bold" />

    <EditText
        android:id="@+id/etUsername"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="24dp"
        android:hint="Enter your username"
        android:inputType="textPersonName" />

    <Button
        android:id="@+id/btnGoToProfile"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:text="Go to Profile →" />

    <TextView
        android:id="@+id/tvResult"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="24dp"
        android:textSize="18sp"
        android:textColor="#4CAF50" />

</LinearLayout>
```

```xml
<!-- res/layout/activity_profile.xml (Screen B) -->
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:gravity="center"
    android:padding="24dp">

    <TextView
        android:id="@+id/tvWelcome"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textSize="28sp"
        android:textStyle="bold" />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:text="How would you rate your experience?"
        android:textSize="16sp" />

    <Button
        android:id="@+id/btnSendRating"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:text="Send Rating ⭐ 5 Back" />

    <Button
        android:id="@+id/btnGoBack"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp"
        android:text="← Go Back Without Rating" />

</LinearLayout>
```

---

### 💻 Step 3: Kotlin Code

**Screen A: `MainActivity.kt`**

```kotlin
// ─── SCREEN A: MainActivity.kt ────────────────────────────────────

package com.rohit.myapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // Modern Activity Result API — register BEFORE onCreate
    private val profileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // This callback runs when ProfileActivity finishes
        if (result.resultCode == RESULT_OK) {
            // User sent a rating back!
            val rating = result.data?.getIntExtra(ProfileActivity.EXTRA_RATING, 0) ?: 0
            val username = result.data?.getStringExtra(ProfileActivity.EXTRA_USERNAME) ?: ""
            tvResult.text = "✅ $username rated: $rating ⭐"
        } else if (result.resultCode == RESULT_CANCELED) {
            // User pressed back without rating
            tvResult.text = "ℹ️ No rating received"
        }
    }

    // Views
    private lateinit var etUsername: EditText
    private lateinit var btnGoToProfile: Button
    private lateinit var tvResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        etUsername = findViewById(R.id.etUsername)
        btnGoToProfile = findViewById(R.id.btnGoToProfile)
        tvResult = findViewById(R.id.tvResult)

        // Set up button click
        btnGoToProfile.setOnClickListener {
            val username = etUsername.text.toString().trim()

            // Validate input
            if (username.isEmpty()) {
                Toast.makeText(this, "Please enter a username!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // CREATE EXPLICIT INTENT:
            val intent = Intent(this, ProfileActivity::class.java)

            // PASS DATA via extras:
            intent.putExtra(ProfileActivity.EXTRA_USERNAME, username)
            intent.putExtra(ProfileActivity.EXTRA_USER_ID, 1042)

            // LAUNCH Screen B and wait for result:
            profileLauncher.launch(intent)
        }
    }
}
```

**Screen B: `ProfileActivity.kt`**

```kotlin
// ─── SCREEN B: ProfileActivity.kt ─────────────────────────────────

package com.rohit.myapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    // Constants for Intent extra keys (best practice):
    companion object {
        const val EXTRA_USERNAME = "com.rohit.myapp.EXTRA_USERNAME"
        const val EXTRA_USER_ID = "com.rohit.myapp.EXTRA_USER_ID"
        const val EXTRA_RATING = "com.rohit.myapp.EXTRA_RATING"
    }

    private lateinit var tvWelcome: TextView
    private lateinit var btnSendRating: Button
    private lateinit var btnGoBack: Button

    private var receivedUsername: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize views
        tvWelcome = findViewById(R.id.tvWelcome)
        btnSendRating = findViewById(R.id.btnSendRating)
        btnGoBack = findViewById(R.id.btnGoBack)

        // RECEIVE DATA from Screen A:
        receivedUsername = intent.getStringExtra(EXTRA_USERNAME) ?: "Guest"
        val userId = intent.getIntExtra(EXTRA_USER_ID, 0)

        // Display the received data:
        tvWelcome.text = "Welcome, $receivedUsername!\n(User ID: $userId)"

        // SEND RESULT BACK to Screen A:
        btnSendRating.setOnClickListener {
            // Create a result Intent:
            val resultIntent = Intent().apply {
                putExtra(EXTRA_RATING, 5)
                putExtra(EXTRA_USERNAME, receivedUsername)
            }

            // Set the result and finish this Activity:
            setResult(RESULT_OK, resultIntent)
            finish()  // Closes ProfileActivity, returns to MainActivity
        }

        // GO BACK without sending result:
        btnGoBack.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }
}
```

---

### 🔁 The Complete Flow Diagram

```text
USER TAPS APP ICON
        │
        ▼
Android OS reads AndroidManifest.xml
Finds MAIN + LAUNCHER → MainActivity
        │
        ▼
┌─────────────────────────────────────────┐
│  SCREEN A: MainActivity                 │
│                                         │
│  "Welcome to MyApp!"                    │
│  ┌─────────────────────┐               │
│  │ Enter username...   │ ← User types  │
│  └─────────────────────┘   "Rohit"     │
│  ┌─────────────────────┐               │
│  │ Go to Profile →     │ ← User taps   │
│  └─────────────────────┘               │
│                                         │
│  Code runs:                             │
│  val intent = Intent(this,              │
│      ProfileActivity::class.java)       │
│  intent.putExtra("USERNAME", "Rohit")   │
│  profileLauncher.launch(intent)         │
└───────────────────┬─────────────────────┘
                    │
                    │ Intent sent to Android OS
                    │ OS creates ProfileActivity
                    ▼
┌─────────────────────────────────────────┐
│  SCREEN B: ProfileActivity              │
│                                         │
│  Code runs in onCreate():               │
│  val name = intent                      │
│      .getStringExtra("USERNAME")        │
│  // name = "Rohit"                      │
│                                         │
│  "Welcome, Rohit!                       │
│   (User ID: 1042)"                      │
│                                         │
│  ┌─────────────────────┐               │
│  │ Send Rating ⭐ 5    │ ← User taps   │
│  └─────────────────────┘               │
│                                         │
│  Code runs:                             │
│  val result = Intent()                  │
│  result.putExtra("RATING", 5)           │
│  setResult(RESULT_OK, result)           │
│  finish()                               │
└───────────────────┬─────────────────────┘
                    │
                    │ finish() called
                    │ ProfileActivity destroyed
                    │ Result sent back to MainActivity
                    ▼
┌─────────────────────────────────────────┐
│  SCREEN A: MainActivity (resumed)       │
│                                         │
│  Callback runs:                         │
│  result.resultCode == RESULT_OK         │
│  rating = result.data                   │
│      .getIntExtra("RATING", 0)          │
│  // rating = 5                          │
│                                         │
│  "Welcome to MyApp!"                    │
│  ┌─────────────────────┐               │
│  │ Rohit               │               │
│  └─────────────────────┘               │
│  ┌─────────────────────┐               │
│  │ Go to Profile →     │               │
│  └─────────────────────┘               │
│  ✅ Rohit rated: 5 ⭐    ← NEW!        │
└─────────────────────────────────────────┘
```

---

---

## 📋 Complete Summary

```text
┌──────────────────────────────────────────────────────────────┐
│              ACTIVITY, INTENT, MANIFEST SUMMARY              │
├───────────────────────┬──────────────────────────────────────┤
│ CONCEPT               │ KEY POINTS                           │
├───────────────────────┼──────────────────────────────────────┤
│ Activity              │ A single screen in your app          │
│                       │ Extends AppCompatActivity            │
│                       │ onCreate() sets up the screen        │
│                       │ setContentView() loads the UI        │
├───────────────────────┼──────────────────────────────────────┤
│ Explicit Intent       │ Navigate to a SPECIFIC Activity      │
│                       │ Intent(this, TargetActivity::class)  │
│                       │ Used within your own app             │
├───────────────────────┼──────────────────────────────────────┤
│ Implicit Intent       │ Ask ANY app to perform an action     │
│                       │ Intent(ACTION_VIEW, uri)             │
│                       │ Open browser, share, call, maps      │
├───────────────────────┼──────────────────────────────────────┤
│ Intent Extras         │ Pass data between Activities         │
│                       │ putExtra("key", value) to send       │
│                       │ getXxxExtra("key", default) to read  │
├───────────────────────┼──────────────────────────────────────┤
│ Activity Result API   │ Get data BACK from second Activity   │
│                       │ registerForActivityResult()          │
│                       │ setResult() + finish() to send back  │
├───────────────────────┼──────────────────────────────────────┤
│ AndroidManifest.xml   │ App's identity card                  │
│                       │ Declares Activities, permissions     │
│                       │ Defines launcher Activity            │
│                       │ Sets app name, icon, theme           │
├───────────────────────┼──────────────────────────────────────┤
│ MAIN + LAUNCHER       │ Intent-filter that marks the         │
│                       │ Activity opened when user taps icon  │
├───────────────────────┼──────────────────────────────────────┤
│ Permissions           │ Declared in manifest                 │
│                       │ Some auto-granted (INTERNET)         │
│                       │ Some need runtime request (CAMERA)   │
└───────────────────────┴──────────────────────────────────────┘
```

---

---

## 📝 Quiz — Test Your Understanding

> Answer these in your head or write them out before checking!

---

### ❓ Question 1: Activity Fundamentals

```text
a) What is an Activity in Android? How is it different from
   a "screen" in a normal desktop application?

b) Explain the purpose of EACH line in this code:

   class LoginActivity : AppCompatActivity() {
       override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           setContentView(R.layout.activity_login)
       }
   }

   Specifically:
   - Why extend AppCompatActivity and not just Activity?
   - What does 'override' mean here?
   - Why must super.onCreate() be called FIRST?
   - What does savedInstanceState contain? When is it not null?

c) A developer writes this code:

   class HomeActivity : AppCompatActivity() {
       override fun onCreate(savedInstanceState: Bundle?) {
           setContentView(R.layout.activity_home)
           super.onCreate(savedInstanceState)
       }
   }

   What is WRONG with this code? What will happen?
   Why does the order of super.onCreate() and setContentView() matter?

d) Your app has 5 screens. How many Activity classes do you need?
   How many XML layout files? Can two Activities share the same layout?
```

---

### ❓ Question 2: Intent Mastery

```text
a) Explain the difference between Explicit and Implicit Intents
   using a real-life analogy (not the taxi one — create your own).

b) For each scenario, write the EXACT Intent code:

   Scenario 1: Open the device's web browser to "https://kotlinlang.org"
   
   Scenario 2: Open the phone dialer with number "9876543210" pre-filled
   
   Scenario 3: Share the text "I love Android development!" via any app
   
   Scenario 4: Navigate from CartActivity to CheckoutActivity in your app,
               passing the cart total (Double: 1250.50) and item count (Int: 3)
   
   Scenario 5: Open Google Maps showing the location "Taj Mahal, Agra"

c) A developer writes this code to pass data:

   // Screen A:
   intent.putExtra("name", "Rohit")
   
   // Screen B:
   val name = intent.getStringExtra("Name")

   Why does 'name' end up being null? What is the bug?

d) What happens if you call startActivity() with an Implicit Intent
   but NO app on the device can handle it?
   How do you prevent this crash? Write the safety check code.
```

---

### ❓ Question 3: AndroidManifest Investigation

Look at this AndroidManifest.xml and answer the questions:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest package="com.rohit.quizapp">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.CAMERA" />

    <application
        android:icon="@mipmap/ic_launcher"
        android:label="Quiz Master"
        android:theme="@style/Theme.QuizApp">

        <activity
            android:name=".SplashActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <activity android:name=".QuizActivity" android:exported="false" />
        <activity android:name=".ResultActivity" android:exported="false" />
        <activity android:name=".SettingsActivity" android:exported="false" />

    </application>
</manifest>
```

```text
Questions:
a) What is the package name of this app?
   Can another app on the Play Store use the same package name?

b) Which Activity opens when the user taps the app icon?
   How do you know? What two lines tell you this?

c) How many screens does this app have? List them.

d) The app needs to load quiz questions from an API.
   Will the INTERNET permission allow this?
   Is INTERNET a runtime permission or auto-granted?

e) The app needs to scan QR codes using the camera.
   The CAMERA permission is declared. Is that ENOUGH?
   What else must the developer do at runtime?

f) A developer adds a new screen: LeaderboardActivity.
   They write the Kotlin class but forget to add it to the manifest.
   What happens when they try to navigate to it with an Intent?

g) What is the app's display name on the user's home screen?
   Where is this defined?
```

---

### ❓ Question 4: Data Passing Challenge

```text
You are building a food delivery app with these screens:

Screen A: RestaurantListActivity (shows list of restaurants)
Screen B: RestaurantDetailActivity (shows one restaurant's menu)
Screen C: OrderConfirmationActivity (shows order summary)

PART A:
  When user taps a restaurant in Screen A, you need to pass
  to Screen B:
    - Restaurant ID (Int): 42
    - Restaurant Name (String): "Biryani House"
    - Rating (Double): 4.5
    - Is Open (Boolean): true

  Write the COMPLETE code for:
    1. Creating the Intent in Screen A
    2. Adding all extras
    3. Starting Screen B
    4. Receiving all extras in Screen B's onCreate()

PART B:
  In Screen B, the user places an order.
  Screen B needs to send back to Screen A:
    - Order ID (String): "ORD-2024-001"
    - Total Amount (Double): 450.0
    - Number of Items (Int): 3

  Write the code using the Activity Result API:
    1. Register the launcher in Screen A
    2. Launch Screen B from Screen A
    3. Handle the result in Screen A's callback
    4. Set the result in Screen B before finishing

PART C:
  Screen C needs to open Google Maps to show the delivery
  location. The address is "42 MG Road, Bangalore".
  Write the Implicit Intent code with the safety check.
```

---

### ❓ Question 5: Debugging Scenario

A junior developer built a two-screen app but it keeps crashing. Read their code and find **ALL** the bugs:

**AndroidManifest.xml:**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest package="com.rohit.buggyapp">
    <application
        android:icon="@mipmap/ic_launcher"
        android:label="Buggy App">

        <activity android:name=".MainActivity" android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
            </intent-filter>
        </activity>

        <activity android:name=".SecondActivity" android:exported="true" />

    </application>
</manifest>
```

**MainActivity.kt:**

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)

        val btn = findViewById<Button>(R.id.btnNext)
        btn.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("user_age", "twenty five")
            startActivity(intent)
        }
    }
}
```

**SecondActivity.kt:**

```kotlin
class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val age = intent.getIntExtra("user_Age", 0)
        val tvAge = findViewById<TextView>(R.id.tvAge)
        tvAge.text = "Age: $age"

        val btnShare = findViewById<Button>(R.id.btnShare)
        btnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check this app!")
            startActivity(shareIntent)
        }
    }
}
```

```text
Find and explain ALL bugs:
a) Manifest bugs (there are at least 2)
b) MainActivity bugs (there are at least 2)
c) SecondActivity bugs (there are at least 2)
d) What will the user actually experience when they run this app?
e) Rewrite the corrected code for all three files.
```