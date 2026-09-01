# 👆 Gestures & Interaction in Jetpack Compose — Complete Beginner Guide

![Gestures and Interaction](./gesture%20interaction.png)

---

## 📋 Prerequisites

This lesson builds on the previous lessons. You should already know:

- `remember { mutableStateOf() }` and the `by` keyword
- `LazyColumn` basics
- Modifiers and how they chain

---

---

## 🖱️ Chapter 1: The `clickable` Modifier — In Depth

### 💡 The Simplest Click

You already know this from previous lessons:

```kotlin
@Composable
fun SimpleButton() {
    Text(
        text = "Tap me!",
        modifier = Modifier
            .clickable {
                // This runs when the user taps the Text
                println("Tapped!")
            }
            .padding(16.dp)
    )
}
```

---

### ⚠️ Important: Modifier Order Matters!

```kotlin
// ✅ CORRECT — clickable BEFORE padding
// The clickable area INCLUDES the padding (larger tap target)
Text(
    text = "Tap me",
    modifier = Modifier
        .clickable { /* ... */ }
        .padding(16.dp)
)
// Tap area: [  padding + text + padding  ]  ← big and easy to tap

// ❌ WRONG — clickable AFTER padding
// The clickable area is ONLY the text (tiny tap target)
Text(
    text = "Tap me",
    modifier = Modifier
        .padding(16.dp)
        .clickable { /* ... */ }
)
// Tap area: [text]  ← tiny, hard to tap, users will be frustrated
```

```text
Visual comparison:

✅ clickable → padding          ❌ padding → clickable
┌─────────────────────┐         ┌─────────────────────┐
│                     │         │                     │
│   ┌───────────┐     │         │   ┌───────────┐     │
│   │  Tap me   │     │         │   │  Tap me   │     │
│   └───────────┘     │         │   └───────────┘     │
│                     │         │                     │
│  ← ALL clickable →  │         │  only [text] clicks │
└─────────────────────┘         └─────────────────────┘
```

---

### 🔒 The `enabled` Parameter

You can disable clicks conditionally:

```kotlin
@Composable
fun LikeButton(isLoggedIn: Boolean) {
    var liked by remember { mutableStateOf(false) }

    Text(
        text = if (liked) "❤️ Liked" else "🤍 Like",
        fontSize = 20.sp,
        modifier = Modifier
            .clickable(
                enabled = isLoggedIn  // ← Can't like if not logged in!
            ) {
                liked = !liked
            }
            .padding(16.dp)
    )

    if (!isLoggedIn) {
        Text("Log in to like posts", color = Color.Gray, fontSize = 12.sp)
    }
}
```

```text
When enabled = true:
  → Tap works normally
  → Ripple effect shows on tap
  → onClick lambda runs

When enabled = false:
  → Tap does NOTHING
  → No ripple effect
  → onClick lambda is NOT called
  → The composable looks the same (Compose doesn't auto-gray it out)
```

---

### 🎨 Making Disabled State Visible

Compose does **NOT** automatically change the appearance when `enabled = false`. You need to handle that yourself:

```kotlin
@Composable
fun SubmitButton(isValid: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isValid) { /* submit */ }
            .background(
                if (isValid) Color.Blue else Color.LightGray  // ← You control this!
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Submit",
            color = if (isValid) Color.White else Color.DarkGray
        )
    }
}
```

---

### 💧 `interactionSource` and `indication` (Preview of Ripple)

These are advanced parameters of `clickable`. We will cover them in detail in the Ripple chapter, but here is a preview:

```kotlin
@Composable
fun CustomClickDemo() {
    val interactionSource = remember { MutableInteractionSource() }

    Text(
        text = "Custom Click",
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null  // ← Removes the ripple effect entirely!
            ) {
                println("Clicked without ripple!")
            }
            .padding(16.dp)
    )
}
```

---

### 📋 Full `clickable` Signature

```kotlin
Modifier.clickable(
    enabled = true,                              // Can the user tap it?
    onClickLabel = "Delete item",                // Accessibility label
    role = Role.Button,                          // Accessibility role
    interactionSource = MutableInteractionSource(), // Tracks press/release state
    indication = rememberRipple(),               // Visual feedback (ripple)
    onClick = { /* your code */ }                // What happens on tap
)
```

> **💡 Tip:** For beginners: You will use `enabled` and `onClick` **95% of the time**. The other parameters are for advanced customization and accessibility.

---

---

## 👆👆 Chapter 2: `combinedClickable` — Click + Long Press + Double Tap

### 🤔 The Problem with `clickable`

`clickable` only handles single taps. What if you want:

- **Long press** → Show a context menu (like in WhatsApp)
- **Double tap** → Like a post (like in Instagram)
- **Single tap** → Open the item

`clickable` can't do this. You need `combinedClickable`.

---

### 🛠️ Basic Syntax

```kotlin
@Composable
fun MessageBubble(text: String) {
    Box(
        modifier = Modifier
            .combinedClickable(
                onClick = {
                    // Single tap → open message
                    println("Opened: $text")
                },
                onLongClick = {
                    // Long press → show options menu
                    println("Show options for: $text")
                },
                onDoubleClick = {
                    // Double tap → star the message
                    println("Starred: $text")
                }
            )
            .background(Color.LightGray)
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(text = text)
    }
}
```

---

### ⏳ How Compose Distinguishes the Gestures

```text
User touches the screen:
  │
  ├── Releases quickly (< 300ms)
  │     │
  │     ├── Touches again quickly → DOUBLE TAP ✅
  │     │
  │     └── Doesn't touch again → waits a moment → SINGLE TAP ✅
  │
  └── Holds finger down (> 500ms) → LONG PRESS ✅
```

> **📌 Important:** When you use `combinedClickable` with `onClick` and `onDoubleClick`, there is a slight delay on single tap. Compose waits ~300ms to see if a second tap is coming. This is normal and expected behavior (same as how your phone's home screen works).

---

### 📦 Required Import

```kotlin
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
```

`combinedClickable` is currently marked as experimental, so you need the annotation:

```kotlin
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyScreen() {
    Box(
        modifier = Modifier.combinedClickable(
            onClick = { },
            onLongClick = { }
        )
    ) {
        Text("Hello")
    }
}
```

---

### 💬 Real-World Example — Chat Message

```kotlin
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatMessage(
    text: String,
    isMine: Boolean,
    onReply: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Box {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .combinedClickable(
                    onClick = { /* Open full message */ },
                    onLongClick = { showMenu = true }  // ← Long press shows menu
                ),
            colors = CardDefaults.cardColors(
                containerColor = if (isMine) Color(0xFFDCF8C6) else Color.White
            )
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(12.dp)
            )
        }

        // Dropdown menu appears on long press
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Reply") },
                onClick = {
                    showMenu = false
                    onReply()
                }
            )
            DropdownMenuItem(
                text = { Text("Delete", color = Color.Red) },
                onClick = {
                    showMenu = false
                    onDelete()
                }
            )
        }
    }
}
```

---

---

## 📧 Chapter 3: Swipe to Dismiss — Gmail-Style Delete

### 💡 The Concept

Think of Gmail. You swipe an email to the left or right, and a delete/archive action is revealed behind it.

```text
BEFORE swipe:                    DURING swipe:
┌──────────────────────┐         ┌──────────────────────┐
│ 📧 Email from Alice  │         │ 🗑️ │  📧 Email from  │
│    Meeting at 3pm    │   →→→   │DELETE│    Alice       │
└──────────────────────┘         └──────────────────────┘
                                  ↑ red background revealed
```

---

### 🗑️ Basic Implementation with `SwipeToDismissBox` (Material 3)

```kotlin
import androidx.compose.material3.*
import androidx.compose.material3.SwipeToDismissBoxValue.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableEmail(
    subject: String,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == EndToStart) {
                // User swiped left → confirm deletion
                onDelete()
                true  // Allow the dismiss
            } else {
                false // Don't dismiss for other directions
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            // What's BEHIND the card (the red delete background)
            val color = when (dismissState.targetValue) {
                EndToStart -> Color.Red
                else -> Color.Transparent
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        },
        content = {
            // The actual email card (what the user sees normally)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    text = "📧 $subject",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 16.sp
                )
            }
        }
    )
}
```

---

### 📱 Using It in a List

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailList() {
    val emails = remember {
        mutableStateListOf(
            "Meeting at 3pm",
            "Lunch tomorrow?",
            "Project update",
            "Weekend plans"
        )
    }

    LazyColumn {
        items(emails) { email ->
            SwipeableEmail(
                subject = email,
                onDelete = { emails.remove(email) }  // ← Removes from list!
            )
        }
    }
}
```

> **💡 Tip:** For beginners: The swipe-to-dismiss API has changed across Compose versions. The example above uses Material 3 (`SwipeToDismissBox`). If you see older tutorials using `DismissState` or `SwipeToDismiss`, those are from Material 2 and are now deprecated.

---

---

## 🖐️ Chapter 4: Drag Gestures — Brief Introduction

### 🤔 What Is Drag?

**Drag** = the user touches the screen and moves their finger while holding down.

```text
Tap:    👆 (touch + release in place)
Drag:   👆→→→→ (touch + move + release)
```

---

### 📍 The `pointerInput` Modifier

All advanced gestures in Compose use the `pointerInput` modifier. It gives you access to the raw touch/pointer system.

```kotlin
@Composable
fun DraggableBox() {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .size(80.dp)
            .background(Color.Blue, RoundedCornerShape(8.dp))
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()  // Tell Compose "I handled this touch"
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
    )
}
```

---

### 🔍 Breaking It Down

```kotlin
Modifier.pointerInput(Unit) {  // ← "Unit" is the key (like LaunchedEffect!)
    // This block runs in a coroutine scope
    // You can call gesture detection functions here

    detectDragGestures { change, dragAmount ->
        // change:     Information about the touch (position, pressure, etc.)
        // dragAmount: How far the finger moved since the last frame (Offset)

        change.consume()  // ← IMPORTANT: Prevents other gestures from
                          //    also reacting to this touch

        offsetX += dragAmount.x  // Move the box horizontally
        offsetY += dragAmount.y  // Move the box vertically
    }
}
```

---

### 🛠️ Other Gesture Detectors (Brief Overview)

```kotlin
Modifier.pointerInput(Unit) {
    // Detect single/multi-tap
    detectTapGestures(
        onTap = { offset -> },
        onDoubleTap = { offset -> },
        onLongPress = { offset -> },
        onPress = { offset -> }
    )

    // Detect drag
    detectDragGestures { change, dragAmount -> }

    // Detect horizontal drag only
    detectHorizontalDragGestures { change, dragAmount -> }

    // Detect vertical drag only
    detectVerticalDragGestures { change, dragAmount -> }

    // Detect pinch-to-zoom and rotation
    detectTransformGestures { centroid, pan, zoom, rotation -> }
}
```

> **💡 Tip:** For beginners: You will rarely use `pointerInput` directly. Most common gestures (click, scroll, swipe) have higher-level APIs. Use `pointerInput` only when you need custom gesture handling like drawing on a canvas or building a custom slider.

---

---

## ↕️ Chapter 5: Scroll Modifiers — For Non-Lazy Content

### ⚖️ When to Use These vs `LazyColumn`

```text
Use verticalScroll / horizontalScroll when:
  ✅ You have a SMALL amount of content (e.g., a form, a details page)
  ✅ All items should exist in memory (they're few)
  ✅ You need the entire content to be one scrollable block

Use LazyColumn / LazyRow when:
  ✅ You have a LARGE or UNKNOWN amount of content (e.g., a feed)
  ✅ Items should be created lazily (only when visible)
  ✅ Performance matters for long lists
```

---

### 📄 `verticalScroll` — Making a Column Scrollable

```kotlin
@Composable
fun UserProfileDetails() {
    val scrollState = rememberScrollState()  // ← Tracks scroll position

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)  // ← Makes the Column scrollable!
            .padding(16.dp)
    ) {
        // All of this content is created at once (fine for ~20 items)
        Text("Name: Jane Doe", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Email: jane@example.com")
        Spacer(modifier = Modifier.height(8.dp))
        Text("Bio: Android developer who loves Compose...")
        Spacer(modifier = Modifier.height(8.dp))
        Text("Location: San Francisco")
        Spacer(modifier = Modifier.height(8.dp))
        Text("Joined: January 2023")
        // ... more fields ...
    }
}
```

---

### ↔️ `horizontalScroll` — Making a Row Scrollable

```kotlin
@Composable
fun FilterChips() {
    val categories = listOf(
        "All", "Electronics", "Clothing", "Books",
        "Home", "Sports", "Toys", "Food", "Beauty"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())  // ← Horizontal scroll!
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            SuggestionChip(
                onClick = { },
                label = { Text(category) }
            )
        }
    }
}
```

---

### 📊 `verticalScroll` vs `LazyColumn` — Side by Side

```kotlin
// ❌ BAD for 1000 items — creates all 1000 composables
Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
    repeat(1000) { index ->
        Text("Item $index")
    }
}

// ✅ GOOD for 1000 items — creates only visible ones
LazyColumn {
    items(1000) { index ->
        Text("Item $index")
    }
}

// ✅ FINE for 10 items — small fixed content
Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
    Text("Name")
    Text("Email")
    Text("Phone")
    Text("Address")
}
```

---

### 🚀 Programmatic Scrolling

```kotlin
@Composable
fun ScrollToTopDemo() {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    Column {
        Button(onClick = {
            scope.launch {
                scrollState.animateScrollTo(0)  // ← Smooth scroll to top!
            }
        }) {
            Text("Scroll to Top")
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            repeat(50) { Text("Item $it", modifier = Modifier.padding(8.dp)) }
        }
    }
}
```

---

---

## 🔄 Chapter 6: Pull to Refresh — Brief Awareness

### 💡 The Concept

The user pulls down from the top of a list to trigger a data refresh. You see this in almost every app: Gmail, Twitter, Instagram, etc.

```text
User pulls down:
    ↓
  ┌──────────────────────┐
  │    🔄 Refreshing...   │  ← Spinner appears
  │  ┌────────────────┐  │
  │  │ Item 1         │  │
  │  └────────────────┘  │
  └──────────────────────┘
    ↓
  Data reloads from server
    ↓
  Spinner disappears, new data shown
```

---

### 🏗️ Basic Material 3 Implementation

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefreshableList() {
    var isRefreshing by remember { mutableStateOf(false) }
    var items by remember { mutableStateOf(List(10) { "Item $it" }) }

    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            // In a real app, you'd call your API here
            // For demo, we simulate a 2-second network call
            // (In real code, use LaunchedEffect or ViewModel)
        },
        state = pullToRefreshState
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(items) { item ->
                Text(
                    text = item,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}
```

> **💡 Tip:** For beginners: Pull-to-refresh is typically connected to a `ViewModel` that handles the actual data loading. The UI just shows/hides the spinner based on a loading state. You will implement this properly when you learn ViewModel + Compose integration.

---

---

## 💧 Chapter 7: Ripple Effect — The Material Touch Feedback

### 🤔 What Is a Ripple?

When you tap a button in any Material Design app, you see a circular wave of color expanding from your touch point. That is the **ripple effect**.

```text
Frame 1:  👆 tap here
          ·

Frame 2:  (ripple starts expanding)
         (·)

Frame 3:  (ripple grows)
        (( · ))

Frame 4:  (ripple fades out)
       ((( · )))
```

---

### ✨ Compose Adds Ripple Automatically!

```kotlin
// You get a ripple for FREE with clickable!
Text(
    text = "I have a ripple!",
    modifier = Modifier
        .clickable { }  // ← Ripple is automatic
        .padding(16.dp)
)
```

---

### 🎨 Customizing the Ripple Color

```kotlin
@Composable
fun CustomRippleButton() {
    Box(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    color = Color.Red,       // ← Red ripple instead of default!
                    bounded = true,          // ← Ripple stays inside the composable
                    radius = 50.dp           // ← Size of the ripple
                )
            ) { }
            .background(Color.LightGray)
            .padding(24.dp)
    ) {
        Text("Red Ripple")
    }
}
```

---

### 🚫 Removing the Ripple Entirely

```kotlin
@Composable
fun NoRippleButton() {
    Text(
        text = "No ripple here",
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null  // ← No visual feedback at all!
            ) { }
            .padding(16.dp)
    )
}
```

---

### 🎯 When to Remove Ripple

```text
Keep ripple (default):
  ✅ Buttons
  ✅ List items
  ✅ Cards that are tappable
  ✅ Navigation items

Remove ripple:
  ✅ When you have your own custom animation on tap
  ✅ When the parent already has a ripple (to avoid double ripple)
  ✅ For decorative elements that happen to be clickable
```

---

### 📐 Bounded vs Unbounded Ripple

```kotlin
// Bounded (default) — ripple is clipped to the composable's shape
indication = rememberRipple(bounded = true)
//  ┌──────────────┐
//  │  ((( · )))   │  ← ripple stays inside the box
//  └──────────────┘

// Unbounded — ripple extends beyond the composable's bounds
indication = rememberRipple(bounded = false)
//     ((((( · )))))   ← ripple spills outside
//       ┌──────┐
//       │  ·   │
//       └──────┘
//  Used for small icon buttons (like the ⋮ menu icon)
```

---

---

## ⌨️ Chapter 8: Focus and Keyboard Handling — Brief Introduction

### 🎯 What Is Focus?

Focus determines which composable receives keyboard input. On a phone, this matters mostly for `TextField`. On a TV or desktop, it matters for navigation with arrow keys or a D-pad.

```text
Screen with two text fields:
┌──────────────────────┐
│  Name: [Jane    ] ← This one has FOCUS (cursor blinking)
│                      │
│  Email: [       ] ← This one does NOT have focus
│                      │
│  [Submit]           │
└──────────────────────┘
```

---

### 💻 Basic Focus Handling

```kotlin
@Composable
fun LoginForm() {
    val focusManager = LocalFocusManager.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next  // ← "Next" button on keyboard
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    // Move focus to the next field (email)
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done  // ← "Done" button on keyboard
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    // Hide the keyboard
                    focusManager.clearFocus()
                }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            focusManager.clearFocus()  // ← Hide keyboard on submit
            // Submit the form...
        }) {
            Text("Submit")
        }
    }
}
```

---

### 🔑 Key Concepts

```text
LocalFocusManager.current
  → Gives you control over focus
  → focusManager.clearFocus()    → Hides the keyboard
  → focusManager.moveFocus(Down) → Moves focus to the next field

KeyboardOptions
  → Configures the SOFTWARE KEYBOARD
  → imeAction = Next / Done / Search / Go / Send
  → keyboardType = Text / Email / Password / Number / Phone

KeyboardActions
  → What happens when the user presses the keyboard action button
  → onNext = { }, onDone = { }, onSearch = { }
```

> **💡 Tip:** For beginners: The most common use case is hiding the keyboard when the user taps outside a text field or presses "Done". Use `focusManager.clearFocus()` for that.

---

---

## 📇 Chapter 9: Real Example — Card with Ripple + Long Press Delete

Let's build a complete interactive card that:

1. Shows a ripple effect on normal tap (opens the item)
2. Responds to long press by revealing a delete option
3. Has a delete confirmation animation

---

### 💻 The Complete Code

```kotlin
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── DATA MODEL ──────────────────────────────────────────────
data class Note(
    val id: String,
    val title: String,
    val content: String,
    val isStarred: Boolean = false
)

// ─── STATELESS CARD COMPONENT ────────────────────────────────
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    note: Note,
    onTap: () -> Unit,
    onLongPress: () -> Unit,
    onDelete: () -> Unit,
    onToggleStar: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteMode by remember { mutableStateOf(false) }

    // When long press triggers delete mode, auto-hide after 5 seconds
    LaunchedEffect(showDeleteMode) {
        if (showDeleteMode) {
            kotlinx.coroutines.delay(5000)
            showDeleteMode = false
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (showDeleteMode) {
                        // If in delete mode, tap cancels it
                        showDeleteMode = false
                    } else {
                        onTap()
                    }
                },
                onLongClick = {
                    showDeleteMode = true
                    onLongPress()
                }
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (showDeleteMode) 8.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (showDeleteMode)
                Color(0xFFFFEBEE)  // Light red when in delete mode
            else
                Color.White
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Title
                Text(
                    text = note.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                // Star icon (tappable with its own ripple)
                IconButton(onClick = onToggleStar) {
                    Icon(
                        imageVector = if (note.isStarred)
                            Icons.Filled.Star
                        else
                            Icons.Outlined.StarBorder,
                        contentDescription = "Toggle star",
                        tint = if (note.isStarred) Color(0xFFFFD700) else Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Content preview
            Text(
                text = note.content,
                fontSize = 14.sp,
                color = Color.DarkGray,
                maxLines = 2
            )

            // Delete button — only visible in delete mode
            AnimatedVisibility(
                visible = showDeleteMode,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete Note")
                }
            }
        }
    }
}

// ─── SCREEN WITH LIST OF NOTES ───────────────────────────────
@Composable
fun NotesScreen() {
    val context = LocalContext.current

    val notes = remember {
        mutableStateListOf(
            Note("1", "Shopping List", "Milk, eggs, bread, butter, cheese"),
            Note("2", "Meeting Notes", "Discuss Q4 roadmap with the team at 2pm"),
            Note("3", "Workout Plan", "Monday: Chest, Tuesday: Back, Wednesday: Legs"),
            Note("4", "Book Ideas", "Write a chapter about Compose gestures"),
            Note("5", "Recipe", "Pasta: boil water, add salt, cook 10 minutes"),
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "My Notes",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Tap to open • Long press to delete",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(
            items = notes,
            key = { note -> note.id }
        ) { note ->
            NoteCard(
                note = note,
                onTap = {
                    Toast.makeText(context, "Opened: ${note.title}", Toast.LENGTH_SHORT).show()
                },
                onLongPress = {
                    Toast.makeText(context, "Delete mode activated!", Toast.LENGTH_SHORT).show()
                },
                onDelete = {
                    notes.remove(note)
                    Toast.makeText(context, "Deleted: ${note.title}", Toast.LENGTH_SHORT).show()
                },
                onToggleStar = {
                    val index = notes.indexOf(note)
                    if (index != -1) {
                        notes[index] = note.copy(isStarred = !note.isStarred)
                    }
                }
            )
        }
    }
}
```

---

### 👁️ How the Interaction Flow Works

```text
NORMAL STATE:
┌──────────────────────────────────────┐
│  Shopping List                 ☆     │ ← Tap: opens note (with ripple)
│  Milk, eggs, bread, butter...        │ ← Star: toggles star (with ripple)
└──────────────────────────────────────┘

USER LONG PRESSES (hold 500ms+):
┌──────────────────────────────────────┐
│  Shopping List                 ★     │ ← Card turns light red
│  Milk, eggs, bread, butter...        │ ← Elevation increases (shadow)
│  ┌──────────────────────────────┐    │
│  │  🗑️  Delete Note            │    │ ← Delete button slides in!
│  └──────────────────────────────┘    │
└──────────────────────────────────────┘

USER TAPS DELETE:
  → Note is removed from the list
  → Toast shows "Deleted: Shopping List"

USER TAPS CARD (while in delete mode):
  → Delete mode is cancelled
  → Card returns to normal

5 SECONDS PASS (no action):
  → Delete mode auto-cancels
  → Card returns to normal
```

---

### 💡 What Each Gesture Concept Does in This Example

```text
✅ clickable / combinedClickable:
   → Single tap opens the note or cancels delete mode
   → Long press activates delete mode

✅ Ripple effect:
   → Automatic on the Card (from combinedClickable)
   → Automatic on the IconButton (star toggle)
   → Automatic on the Delete Button

✅ State management:
   → showDeleteMode controls the visual state
   → notes list is a mutableStateListOf for reactive updates

✅ Animation:
   → AnimatedVisibility smoothly shows/hides the delete button
   → Card color and elevation change based on state

✅ Key in LazyColumn:
   → key = { note.id } ensures correct animations when items are deleted
```

---

---

## 📋 Chapter 10: Complete Cheat Sheet

```text
╔════════════════════════════════════════════════════════════════════╗
║           GESTURES & INTERACTION CHEAT SHEET                       ║
╠════════════════════════════════════════════════════════════════════╣
║                                                                    ║
║  👆 CLICK                                                          ║
║     Modifier.clickable { }                                         ║
║     → Single tap. Auto ripple. Use enabled = false to disable.     ║
║                                                                    ║
║  👆👆 COMBINED CLICK                                               ║
║     Modifier.combinedClickable(                                    ║
║         onClick = { }, onLongClick = { }, onDoubleClick = { }      ║
║     )                                                              ║
║     → Handles single tap, long press, and double tap.              ║
║     → Requires @OptIn(ExperimentalFoundationApi::class)            ║
║                                                                    ║
║  👆→ SWIPE TO DISMISS                                              ║
║     SwipeToDismissBox(state, backgroundContent, content)           ║
║     → Gmail-style swipe to delete/archive. Material 3.             ║
║                                                                    ║
║  👆→→ DRAG                                                         ║
║     Modifier.pointerInput(Unit) {                                  ║
║         detectDragGestures { change, dragAmount -> }               ║
║     }                                                              ║
║     → Custom drag handling. Use for drawing, custom sliders.       ║
║                                                                    ║
║  ↕️ SCROLL (non-lazy)                                              ║
║     Modifier.verticalScroll(rememberScrollState())                 ║
║     Modifier.horizontalScroll(rememberScrollState())               ║
║     → For small, fixed content. NOT for long lists.                ║
║                                                                    ║
║  🔄 PULL TO REFRESH                                                ║
║     PullToRefreshBox(isRefreshing, onRefresh) { LazyColumn }       ║
║     → Pull down to reload data. Material 3.                        ║
║                                                                    ║
║  💧 RIPPLE                                                         ║
║     Automatic with clickable.                                      ║
║     Customize: indication = rememberRipple(color = Color.Red)      ║
║     Remove:    indication = null                                   ║
║                                                                    ║
║  ⌨️ FOCUS & KEYBOARD                                               ║
║     val focusManager = LocalFocusManager.current                   ║
║     focusManager.clearFocus()        → Hide keyboard               ║
║     focusManager.moveFocus(Down)     → Next field                  ║
║     keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)  ║
║                                                                    ║
╚════════════════════════════════════════════════════════════════════╝
```

---

---

## 📝 Quiz — Test Your Understanding

> Answer each question, then check the answer below it.

---

### ❓ Question 1

```kotlin
@Composable
fun MyButton() {
    Text(
        text = "Click Me",
        modifier = Modifier
            .padding(20.dp)
            .clickable { println("Clicked!") }
    )
}
```

What is the problem with this code?

```text
A) clickable must come before padding in the modifier chain.
   Currently, only the text itself is clickable (tiny tap target),
   not the padding area around it. Users will struggle to tap it.
B) clickable cannot be used on Text composables, only on Button
C) The println will crash on Android
D) The padding will be ignored because clickable overrides it
```

<details> <summary>Click to reveal answer</summary>

**Answer: A**

Modifier order matters in Compose. Modifiers are applied from outside to
inside (or more precisely, each modifier wraps the previous one). When
`padding` comes before `clickable`, the clickable area is only the text
itself. The 20dp padding around it is NOT clickable. This creates a tiny
tap target that frustrates users. The fix is to put `clickable` first:
`Modifier.clickable { }.padding(20.dp)`. This makes the padding part of
the clickable area, creating a large, comfortable tap target.

</details>

---

### ❓ Question 2

```kotlin
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoItem(photo: String) {
    Box(
        modifier = Modifier
            .combinedClickable(
                onClick = { openPhoto(photo) },
                onLongClick = { showOptionsMenu(photo) },
                onDoubleClick = { likePhoto(photo) }
            )
    ) {
        Image(painter = painterResource(photo), contentDescription = null)
    }
}
```

A user single-taps the photo. What happens?

```text
A) openPhoto() is called immediately
B) openPhoto() is called after a brief delay (~300ms), because Compose
   waits to see if a second tap is coming (to detect a double-tap)
C) Both openPhoto() and likePhoto() are called
D) Nothing happens — single tap is not supported by combinedClickable
```

<details> <summary>Click to reveal answer</summary>

**Answer: B**

When you provide both `onClick` and `onDoubleClick` to `combinedClickable`,
Compose cannot immediately know if a single tap is truly a single tap or
the first half of a double tap. So it waits approximately 300ms after the
first tap. If no second tap comes, it fires `onClick`. If a second tap
comes within the window, it fires `onDoubleClick` instead. This slight
delay on single tap is the trade-off for supporting double-tap detection.
If you don't need double-tap, use `combinedClickable` with only `onClick`
and `onLongClick` — there will be no delay.

</details>

---

### ❓ Question 3

You have a settings screen with 8 items (Account, Notifications, Privacy,
Theme, Language, Storage, Help, About). Should you use `LazyColumn` or
`Column` with `verticalScroll`?

```text
A) LazyColumn — always use LazyColumn for any list
B) Column with verticalScroll — the list is small and fixed (8 items),
   so there is no performance benefit from lazy loading. All 8 items
   should exist in memory for instant display.
C) Neither — use a LazyRow since settings are horizontal
D) LazyVerticalGrid with 2 columns
```

<details> <summary>Click to reveal answer</summary>

**Answer: B**

For a small, fixed number of items (like 8 settings entries), a regular
`Column` with `verticalScroll` is perfectly fine and actually simpler.
`LazyColumn` is designed for large or unbounded lists where creating
all items at once would waste memory. With only 8 items, they all likely
fit on screen (or close to it), so there is no performance benefit from
lazy loading. The overhead of `LazyColumn` (item recycling, key management)
is unnecessary here. Use `LazyColumn` when the list might have dozens,
hundreds, or thousands of items, or when the count is dynamic/unknown.

</details>

---

### ❓ Question 4

```kotlin
@Composable
fun SilentButton() {
    Box(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { /* do something */ }
            .background(Color.Blue)
            .padding(16.dp)
    ) {
        Text("Tap Me", color = Color.White)
    }
}
```

What does `indication = null` do?

```text
A) It disables the click — the button won't respond to taps
B) It removes the ripple effect — the button responds to taps but shows
   no visual feedback (no expanding circle of color)
C) It makes the button invisible
D) It changes the ripple color to match the background
```

<details> <summary>Click to reveal answer</summary>

**Answer: B**

`indication = null` removes the visual feedback (the Material ripple)
that normally appears when you tap a clickable composable. The click still
works — the `onClick` lambda still runs — but the user sees no expanding
circle of color. This is useful when you have your own custom animation
for tap feedback, or when a parent composable already provides a ripple
and you want to avoid a "double ripple" effect. Note that you also need
to provide an `interactionSource` when setting `indication`, which is why
both parameters appear together.

</details>

---

### ❓ Question 5

```kotlin
@Composable
fun SearchScreen() {
    val focusManager = LocalFocusManager.current
    var query by remember { mutableStateOf("") }

    Column {
        TextField(
            value = query,
            onValueChange = { query = it },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
                    performSearch(query)
                }
            )
        )
    }
}
```

What happens when the user types a query and presses the "Search" button
on the software keyboard?

```text
A) The keyboard stays open and the search runs in the background
B) focusManager.clearFocus() removes focus from the TextField, which
   hides the software keyboard. Then performSearch(query) runs with
   the user's query text.
C) The app crashes because you can't call clearFocus inside keyboardActions
D) The TextField is cleared and the query is lost
```

<details> <summary>Click to reveal answer</summary>

**Answer: B**

When the user presses the "Search" action button on the keyboard (the
magnifying glass icon), the `onSearch` callback fires. Inside it,
`focusManager.clearFocus()` removes focus from the TextField, which
causes the software keyboard to dismiss (slide down). Then
`performSearch(query)` executes with whatever the user typed. This is
the standard pattern for search fields in Compose: configure the keyboard
to show a "Search" button with `imeAction = ImeAction.Search`, handle
the action with `keyboardActions`, clear focus to hide the keyboard,
and then perform the actual search operation.

</details>