# ✨ Animations in Jetpack Compose — Complete Beginner Guide

---

## 📋 Prerequisites

This lesson builds on everything you have learned so far. You should know:

- `remember { mutableStateOf() }` and the `by` keyword
- State hoisting
- Modifiers and how they chain
- `combinedClickable` (from the Gestures lesson)

---

---

## 🎬 Chapter 1: Why Animations Matter

### 📱 The Two Apps

Imagine two apps that do the exact same thing:

#### ❌ App A — No Animations

```text
You tap "Delete"
  → Item INSTANTLY vanishes. Pop. Gone.
  → Your brain: "Wait, what happened? Did it delete? Did it crash?"

You tap "Like"
  → Heart icon INSTANTLY changes from gray to red.
  → Your brain: "Did I tap it? I'm not sure. Let me tap again."
  → You accidentally unlike it.
```

#### ✅ App B — With Animations

```text
You tap "Delete"
  → Item slides to the left and fades out over 300ms.
  → Your brain: "Ah, it's being removed. Smooth." ✅

You tap "Like"
  → Heart bounces slightly bigger, then settles back to normal size.
  → Color smoothly transitions from gray to red over 200ms.
  → Your brain: "Yes! I liked it. Satisfying." ✅
```

---

### 💡 Why This Matters

Animations serve **THREE** purposes:

1. **FEEDBACK** — *"Yes, I registered your tap."*
   Without it, users tap things multiple times because they're not sure the app responded.

2. **ORIENTATION** — *"Here's where that thing went."*
   Without it, items appear and disappear instantly, and users lose track of where they are in the app.

3. **DELIGHT** — *"This app feels polished and premium."*
   Without it, the app feels like a spreadsheet. With it, the app feels alive.

---

### 🏆 The Golden Rule of Animations

```text
╔══════════════════════════════════════════════════════════╗
║                                                          ║
║   Good animations are FAST and SUBTLE.                   ║
║                                                          ║
║   ✅ 150-300ms for most UI transitions                   ║
║   ✅ Small movements (a few dp)                          ║
║   ✅ Smooth easing (not linear)                          ║
║                                                          ║
║   ❌ 1000ms+ for simple transitions (too slow!)          ║
║   ❌ Huge bouncing animations everywhere (distracting!)   ║
║   ❌ Animations that block the user from doing things    ║
║                                                          ║
║   The best animation is one the user FEELS but           ║
║   doesn't consciously NOTICE.                            ║
║                                                          ║
╚══════════════════════════════════════════════════════════╝
```

---

---

## 📐 Chapter 2: `animateContentSize()` — The Easiest Animation

### 💡 What It Does

When a composable's size changes (because its content grew or shrank), `animateContentSize()` smoothly animates the transition instead of snapping instantly.

---

### ❌ Without Animation (Jarring)

```kotlin
@Composable
fun ExpandableText() {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(16.dp)
    ) {
        Text("Tap to expand", fontWeight = FontWeight.Bold)

        if (expanded) {
            // ❌ This text INSTANTLY appears/disappears
            // The card jumps in size. Jarring!
            Text(
                "This is the extra content that appears when " +
                "you tap the card. It contains more details " +
                "about the item."
            )
        }
    }
}
```

---

### ✅ With `animateContentSize()` (Smooth)

```kotlin
@Composable
fun ExpandableTextSmooth() {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()  // ← ONE LINE! That's it!
            .clickable { expanded = !expanded }
            .padding(16.dp)
    ) {
        Text("Tap to expand", fontWeight = FontWeight.Bold)

        if (expanded) {
            // ✅ The card now SMOOTHLY grows/shrinks
            // to accommodate this text. Beautiful!
            Text(
                "This is the extra content that appears when " +
                "you tap the card. It contains more details " +
                "about the item."
            )
        }
    }
}
```

---

### 👁️ What Happens Visually

```text
WITHOUT animateContentSize():       WITH animateContentSize():
                                   
TAP → Instant jump                  TAP → Smooth grow
┌──────────────┐                    ┌──────────────┐
│ Tap to expand│                    │ Tap to expand│
└──────────────┘                    ├──────────────┤
   ↓ INSTANT                        │ This is the  │ ← growing...
┌──────────────┐                    │ extra content│ ← growing...
│ Tap to expand│                    └──────────────┘
│ This is the  │                    
│ extra content│                    
└──────────────┘                    
😬 Jarring!                         😊 Smooth!
```

---

### ⚙️ Customizing the Animation

```kotlin
Modifier.animateContentSize(
    animationSpec = tween(
        durationMillis = 500,       // Slower (default is 300ms)
        easing = LinearEasing       // Constant speed (default is EaseInOut)
    )
)
```

---

### 🎯 When to Use It

```text
✅ Perfect for:
   • Expandable/collapsible cards
   • Showing/hiding error messages below a form field
   • A list item that grows when selected
   • Any composable whose size changes dynamically

❌ Not for:
   • Showing/hiding an entire composable (use AnimatedVisibility)
   • Animating position changes (use animate*AsState for offset)
   • Animating between completely different screens
```

---

---

## 👁️ Chapter 3: `AnimatedVisibility` — Appear and Disappear with Style

### 💡 What It Does

`AnimatedVisibility` wraps a composable and animates it entering and exiting the screen based on a boolean condition.

---

### 🛠️ Basic Usage

```kotlin
@Composable
fun ToggleMessage() {
    var visible by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { visible = !visible }) {
            Text(if (visible) "Hide" else "Show")
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(visible = visible) {
            // This composable fades in/out and expands/shrinks
            // by default. That's the built-in animation!
            Card(
                modifier = Modifier.padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Text(
                    text = "Hello! I appeared with an animation! 🎉",
                    modifier = Modifier.padding(24.dp),
                    fontSize = 18.sp
                )
            }
        }
    }
}
```

---

### 📦 Default Animation

```text
When visible changes from false → true (ENTER):
  → fadeIn() + expandVertically()
  → The composable fades in from transparent to opaque
  → AND grows from zero height to full height

When visible changes from true → false (EXIT):
  → fadeOut() + shrinkVertically()
  → The composable fades out
  → AND shrinks from full height to zero height
```

---

### 🎨 Customizing Enter and Exit Transitions

```kotlin
@Composable
fun CustomAnimatedVisibility() {
    var visible by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { visible = !visible }) {
            Text("Toggle")
        }

        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(initialOffsetY = { it }) +  // Slide up from bottom
                    fadeIn(),                                       // + fade in
            exit = slideOutVertically(targetOffsetY = { it }) +   // Slide down to bottom
                   fadeOut()                                       // + fade out
        ) {
            Card(
                modifier = Modifier.padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Text(
                    text = "I slide in from below! ⬆️",
                    modifier = Modifier.padding(24.dp),
                    fontSize = 18.sp
                )
            }
        }
    }
}
```

---

### 📋 Available Transition Functions

```text
ENTER transitions (how it appears):
  fadeIn()                        → Fades from transparent to opaque
  slideInHorizontally { -it }     → Slides in from the left
  slideInHorizontally { it }      → Slides in from the right
  slideInVertically { -it }       → Slides in from the top
  slideInVertically { it }        → Slides in from the bottom
  scaleIn()                       → Grows from tiny to full size
  expandIn()                      → Expands from center to full size
  expandHorizontally()            → Expands width from 0 to full
  expandVertically()              → Expands height from 0 to full

EXIT transitions (how it disappears):
  fadeOut()                       → Fades from opaque to transparent
  slideOutHorizontally { -it }    → Slides out to the left
  slideOutHorizontally { it }     → Slides out to the right
  slideOutVertically { -it }      → Slides out to the top
  slideOutVertically { it }       → Slides out to the bottom
  scaleOut()                      → Shrinks from full size to tiny
  shrinkOut()                     → Shrinks from full size to center
  shrinkHorizontally()            → Shrinks width to 0
  shrinkVertically()              → Shrinks height to 0

COMBINING (use + to combine):
  enter = slideInVertically { it } + fadeIn()
  exit = slideOutVertically { -it } + fadeOut()
```

---

### ⚠️ Real-World Example — Error Message

```kotlin
@Composable
fun LoginField() {
    var email by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = email,
            onValueChange = {
                email = it
                showError = !it.contains("@")
            },
            label = { Text("Email") },
            isError = showError
        )

        // Error message slides in smoothly when the email is invalid
        AnimatedVisibility(
            visible = showError && email.isNotEmpty(),
            enter = slideInVertically { -it / 2 } + fadeIn(),
            exit = slideOutVertically { -it / 2 } + fadeOut()
        ) {
            Text(
                text = "⚠️ Please enter a valid email address",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}
```

---

---

## 🎯 Chapter 4: `animate*AsState` — Animating Single Value Changes

### 💡 The Concept

Sometimes you don't want to show/hide a composable. You want to smoothly change a value — like a color, a size, an opacity, or a position.

The `animate*AsState` family of functions does exactly this:

```text
When a value changes:
  ❌ Without animation: value jumps from A to B instantly
  ✅ With animation:    value smoothly transitions from A to B
                        over a duration (e.g., 300ms)
```

---

### 🔢 `animateFloatAsState` — Animating Numbers

```kotlin
@Composable
fun OpacityToggle() {
    var isVisible by remember { mutableStateOf(true) }

    // When isVisible changes, opacity smoothly animates
    // between 1f (fully visible) and 0.2f (nearly transparent)
    val opacity by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.2f,
        label = "opacity"  // Required for accessibility/debugging
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { isVisible = !isVisible }) {
            Text("Toggle Opacity")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .alpha(opacity)  // ← Uses the ANIMATED value!
                .background(Color.Blue, RoundedCornerShape(8.dp))
        )
    }
}
```

```text
What happens internally:

isVisible = true → targetValue = 1f
isVisible = false → targetValue = 0.2f

Frame 0:  opacity = 1.00  (start)
Frame 1:  opacity = 0.87
Frame 2:  opacity = 0.72
Frame 3:  opacity = 0.55
Frame 4:  opacity = 0.38
Frame 5:  opacity = 0.25
Frame 6:  opacity = 0.20  (arrived at target!)

Each frame takes ~16ms (60fps). The transition takes ~300ms by default.
```

---

### 🎨 `animateColorAsState` — Animating Colors

```kotlin
@Composable
fun ColorToggle() {
    var isDanger by remember { mutableStateOf(false) }

    // Color smoothly transitions between green and red
    val backgroundColor by animateColorAsState(
        targetValue = if (isDanger) Color.Red else Color.Green,
        label = "background"
    )

    Box(
        modifier = Modifier
            .size(120.dp)
            .background(backgroundColor, RoundedCornerShape(16.dp))  // ← Animated!
            .clickable { isDanger = !isDanger },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isDanger) "DANGER" else "SAFE",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}
```

---

### 📏 `animateDpAsState` — Animating Sizes and Positions

```kotlin
@Composable
fun SizeToggle() {
    var isExpanded by remember { mutableStateOf(false) }

    // Size smoothly transitions between 80dp and 200dp
    val boxSize by animateDpAsState(
        targetValue = if (isExpanded) 200.dp else 80.dp,
        label = "size"
    )

    Box(
        modifier = Modifier
            .size(boxSize)  // ← Animated size!
            .background(Color.Purple, RoundedCornerShape(8.dp))
            .clickable { isExpanded = !isExpanded },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isExpanded) "Expanded!" else "Tap",
            color = Color.White
        )
    }
}
```

---

### 📋 All `animate*AsState` Functions

```text
animateFloatAsState     → Float values (opacity, rotation, scale)
animateColorAsState     → Color values (background, tint, text color)
animateDpAsState        → Dp values (size, padding, offset, corner radius)
animateIntAsState       → Int values (count, index)
animateIntOffsetAsState → IntOffset values (position on screen)
animateSizeAsState      → Size values (width × height)
animateRectAsState      → Rect values (bounds)

All of them work the same way:
  val animatedValue by animate___AsState(
      targetValue = if (condition) valueA else valueB,
      label = "description"
  )
```

---

### 🏷️ Important: The `label` Parameter

```kotlin
// ❌ Compiler warning — label is required in newer Compose versions
val size by animateDpAsState(targetValue = if (big) 200.dp else 80.dp)

// ✅ Correct — always provide a label
val size by animateDpAsState(
    targetValue = if (big) 200.dp else 80.dp,
    label = "box size"  // Used for debugging in Layout Inspector
)
```

---

---

## 🔄 Chapter 5: `Crossfade` — Animating Between Two Composables

### 💡 What It Does

`Crossfade` smoothly transitions between two completely different composables by fading one out while fading the other in. Think of a slideshow: one image fades out while the next fades in.

---

### 🛠️ Basic Usage

```kotlin
@Composable
fun ImageSlideshow() {
    val images = listOf("🏔️", "🌊", "🌅", "🌲")
    var currentIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            currentIndex = (currentIndex + 1) % images.size
        }) {
            Text("Next Image")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Crossfade watches `currentIndex`.
        // When it changes, the old content fades out
        // and the new content fades in.
        Crossfade(
            targetState = currentIndex,
            label = "slideshow"
        ) { index ->
            // This lambda receives the CURRENT state.
            // Compose animates between the old and new content.
            Text(
                text = images[index],
                fontSize = 120.sp
            )
        }
    }
}
```

---

### 🔍 How It Works

```text
Frame 0:  currentIndex = 0
          Showing: "🏔️" (opacity 100%)

User taps "Next" → currentIndex = 1

Frame 1:  "🏔️" opacity 80%  |  "🌊" opacity 20%
Frame 2:  "🏔️" opacity 60%  |  "🌊" opacity 40%
Frame 3:  "🏔️" opacity 40%  |  "🌊" opacity 60%
Frame 4:  "🏔️" opacity 20%  |  "🌊" opacity 80%
Frame 5:  "🏔️" opacity 0%   |  "🌊" opacity 100%  ← Done!

Both composables exist simultaneously during the transition.
One fades out while the other fades in. That's the "cross" in Crossfade.
```

---

### 🌐 Real-World Example — Loading vs Content vs Error

```kotlin
sealed class ScreenState {
    data object Loading : ScreenState()
    data class Content(val data: String) : ScreenState()
    data class Error(val message: String) : ScreenState()
}

@Composable
fun DataScreen() {
    var state by remember {
        mutableStateOf<ScreenState>(ScreenState.Loading)
    }

    // Simulate loading → content after 2 seconds
    LaunchedEffect(Unit) {
        delay(2000)
        state = ScreenState.Content("Hello from the server!")
    }

    Crossfade(targetState = state, label = "screen") { currentState ->
        when (currentState) {
            is ScreenState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ScreenState.Content -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(currentState.data, fontSize = 24.sp)
                }
            }
            is ScreenState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(currentState.message, color = Color.Red)
                }
            }
        }
    }
}
```

---

---

## 🎰 Chapter 6: `AnimatedContent` — Advanced State Transitions

### 💡 What It Does

`AnimatedContent` is like `Crossfade` on steroids. Instead of just fading, it lets you define custom slide/fade/scale transitions between different states of content.

---

### 🔢 Basic Usage — Counter with Slide Animation

```kotlin
@Composable
fun AnimatedCounter() {
    var count by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = { count-- }) { Text("-") }
            Button(onClick = { count++ }) { Text("+") }
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedContent(
            targetState = count,
            transitionSpec = {
                // If the new number is bigger → slide up (new enters from bottom)
                // If the new number is smaller → slide down (new enters from top)
                if (targetState > initialState) {
                    slideInVertically { it } + fadeIn() togetherWith
                    slideOutVertically { -it } + fadeOut()
                } else {
                    slideInVertically { -it } + fadeIn() togetherWith
                    slideOutVertically { it } + fadeOut()
                }
            },
            label = "counter"
        ) { targetCount ->
            // This lambda displays the CURRENT count.
            // The transition animates between old and new.
            Text(
                text = "$targetCount",
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
```

```text
Count goes from 3 → 4 (increasing):
  "3" slides UP and fades out   ↑
  "4" slides in from BELOW      ↑

Count goes from 4 → 3 (decreasing):
  "4" slides DOWN and fades out  ↓
  "3" slides in from ABOVE       ↓

This creates a "slot machine" effect! 🎰
```

---

### 📊 `AnimatedContent` vs `Crossfade`

```text
┌──────────────────┬──────────────────────┬───────────────────────────┐
│                  │     Crossfade        │     AnimatedContent       │
├──────────────────┼──────────────────────┼───────────────────────────┤
│ Transition type  │ Fade only            │ Slide, fade, scale,       │
│                  │                      │ or any combination        │
│ Direction-aware? │ ❌ No                │ ✅ Yes (can check if      │
│                  │                      │    target > initial)      │
│ Complexity       │ Simple               │ More powerful             │
│ Use when         │ Swapping between     │ Content changes that      │
│                  │ unrelated screens    │ have a logical direction  │
│                  │ (loading → content)  │ (counter, page number)    │
└──────────────────┴──────────────────────┴───────────────────────────┘
```

---

---

## 📈 Chapter 7: Easing and Duration — `tween()` and `spring()`

### 💡 What Is Easing?

**Easing** controls how the speed changes during an animation:

```text
LINEAR:     Constant speed from start to finish
            → Feels robotic and unnatural
            Speed: ═══════════════════

EASE IN:    Starts slow, ends fast
            → Feels like something accelerating
            Speed: ▁▂▃▅▇█

EASE OUT:   Starts fast, ends slow
            → Feels like something decelerating (most natural!)
            Speed: █▇▅▃▂▁

EASE IN-OUT: Starts slow, speeds up, then slows down
            → Feels smooth and polished (DEFAULT in Compose!)
            Speed: ▁▃▅▇▅▃▁
```

---

### ⏱️ `tween()` — Duration-Based Animation

`tween` lets you specify an exact duration and easing curve:

```kotlin
val size by animateDpAsState(
    targetValue = if (big) 200.dp else 80.dp,
    animationSpec = tween(
        durationMillis = 500,          // Animation takes 500ms
        delayMillis = 100,             // Wait 100ms before starting
        easing = EaseInOutCubic        // Smooth ease-in-out curve
    ),
    label = "size"
)
```

---

### 📉 Common Easing Curves

```kotlin
LinearEasing       // Constant speed (robotic)
EaseIn             // Slow start, fast end
EaseOut            // Fast start, slow end (natural deceleration)
EaseInOut          // Slow start, fast middle, slow end (default-ish)
EaseInCubic        // More dramatic slow start
EaseOutCubic       // More dramatic slow end
EaseInOutCubic     // Very smooth, professional feel
FastOutSlowIn      // Material Design default! Quick start, gentle stop
LinearOutSlowIn    // Material Design. Immediate start, gentle stop
FastOutLinearIn    // Material Design. Quick start, abrupt stop
```

---

### 🌀 `spring()` — Physics-Based Animation

`spring` doesn't use a fixed duration. Instead, it simulates a physical spring — the value overshoots slightly and bounces before settling:

```kotlin
val size by animateDpAsState(
    targetValue = if (big) 200.dp else 80.dp,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,  // How bouncy?
        stiffness = Spring.StiffnessLow                  // How fast?
    ),
    label = "size"
)
```

---

### 🔩 Spring Parameters

```text
dampingRatio (how much bounce):
  Spring.DampingRatioNoBouncy       → No bounce at all (like tween)
  Spring.DampingRatioLowBouncy      → Slight overshoot
  Spring.DampingRatioMediumBouncy   → Noticeable bounce (fun!)
  Spring.DampingRatioHighBouncy     → Very bouncy (playful!)

stiffness (how fast it settles):
  Spring.StiffnessVeryLow    → Very slow, lazy movement
  Spring.StiffnessLow        → Slow, gentle
  Spring.StiffnessMedium     → Moderate (default)
  Spring.StiffnessHigh       → Fast, snappy
  Spring.StiffnessVeryHigh   → Very fast, almost instant
```

---

### ⚖️ `tween` vs `spring` — When to Use Which

```text
Use tween() when:
  ✅ You need a precise duration (e.g., exactly 300ms)
  ✅ You want a predictable, consistent animation
  ✅ You're animating loading spinners or progress bars
  ✅ The animation should feel controlled and deliberate

Use spring() when:
  ✅ You want a natural, physical feel
  ✅ The animation is triggered by user interaction (tap, drag)
  ✅ You want a playful bounce effect
  ✅ You don't care about exact duration, just feel

In practice:
  → Most UI transitions use tween with FastOutSlowIn (Material default)
  → Interactive elements (buttons, toggles) often use spring
  → You can't go wrong with either for most cases
```

---

### 🔍 Quick Examples Side by Side

```kotlin
// TWEEN: Exactly 400ms, smooth ease
val opacityTween by animateFloatAsState(
    targetValue = if (show) 1f else 0f,
    animationSpec = tween(
        durationMillis = 400,
        easing = FastOutSlowIn
    ),
    label = "opacity tween"
)

// SPRING: Bouncy, natural, no fixed duration
val opacitySpring by animateFloatAsState(
    targetValue = if (show) 1f else 0f,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    ),
    label = "opacity spring"
)
```

---

---

## ❤️ Chapter 8: Real Example — Animated Like Button

Let's build a heart button that:

1. Scales up (bounces) when tapped
2. Changes color from gray to red smoothly
3. Scales back to normal size after the bounce

---

### 💻 The Complete Code

```kotlin
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnimatedLikeButton(
    isLiked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    // ─── ANIMATION 1: Color ────────────────────────────
    // Smoothly transitions between gray (unliked) and red (liked)
    val heartColor by animateColorAsState(
        targetValue = if (isLiked) Color.Red else Color.Gray,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowIn
        ),
        label = "heart color"
    )

    // ─── ANIMATION 2: Scale (the bounce!) ──────────────
    // When liked: scale goes 1f → 1.4f → 1f (bounce effect)
    // When unliked: scale goes 1f → 0.8f → 1f (shrink effect)
    val scale by animateFloatAsState(
        targetValue = if (isLiked) 1.3f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "heart scale"
    )

    // ─── THE BUTTON ────────────────────────────────────
    IconButton(
        onClick = onToggle,
        modifier = modifier.scale(scale)  // ← Apply animated scale!
    ) {
        Icon(
            imageVector = if (isLiked)
                Icons.Filled.Favorite       // Filled heart ❤️
            else
                Icons.Outlined.FavoriteBorder, // Outline heart 🤍
            contentDescription = if (isLiked) "Unlike" else "Like",
            tint = heartColor,              // ← Apply animated color!
            modifier = Modifier.size(48.dp)
        )
    }
}
```

---

### 📱 The Screen That Uses It

```kotlin
@Composable
fun LikeButtonDemoScreen() {
    var isLiked by remember { mutableStateOf(false) }
    var likeCount by remember { mutableStateOf(42) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // A fake post card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Beautiful sunset at the beach! 🌅",
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Posted by Jane • 2 hours ago",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // The animated like button!
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AnimatedLikeButton(
                        isLiked = isLiked,
                        onToggle = {
                            isLiked = !isLiked
                            likeCount += if (isLiked) 1 else -1
                        }
                    )

                    // Animated count next to the heart
                    Text(
                        text = "$likeCount",
                        fontSize = 16.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }

        // Show multiple like buttons to demonstrate independence
        Text(
            text = "Try these too:",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 24.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            // Each button has its own independent state!
            LikeButtonWithIndependentState()
            LikeButtonWithIndependentState()
            LikeButtonWithIndependentState()
        }
    }
}

@Composable
fun LikeButtonWithIndependentState() {
    var isLiked by remember { mutableStateOf(false) }

    AnimatedLikeButton(
        isLiked = isLiked,
        onToggle = { isLiked = !isLiked }
    )
}
```

---

### 👁️ What Happens When You Tap

```text
BEFORE TAP (unliked):
  Heart: 🤍 (gray outline)
  Scale: 1.0x (normal size)
  Count: 42

USER TAPS → isLiked becomes true

ANIMATION SEQUENCE (happens over ~400ms):
  Frame 0:  🤍 gray,   scale 1.0x
  Frame 3:  ❤️ red-ish, scale 1.15x  (growing + reddening)
  Frame 6:  ❤️ red,    scale 1.35x  (overshoots! bouncy spring!)
  Frame 9:  ❤️ red,    scale 1.25x  (settling back)
  Frame 12: ❤️ red,    scale 1.30x  (final position)
  Count: 43

USER TAPS AGAIN → isLiked becomes false

ANIMATION SEQUENCE:
  Frame 0:  ❤️ red,    scale 1.3x
  Frame 3:  🤍 gray-ish, scale 0.9x  (shrinks slightly)
  Frame 6:  🤍 gray,   scale 1.0x   (settles back to normal)
  Count: 42
```

---

### ✨ Why This Approach is Good

```text
✅ Separation of concerns:
   AnimatedLikeButton is STATELESS — it receives isLiked and onToggle.
   The parent owns the state. This is state hoisting!

✅ Two independent animations:
   Color uses tween (smooth, predictable transition)
   Scale uses spring (bouncy, playful feel)
   Each animation type is chosen for the right feel.

✅ Accessibility:
   contentDescription changes between "Like" and "Unlike"
   for screen readers.

✅ Reusable:
   You can drop AnimatedLikeButton anywhere in your app.
   Each instance has its own animation state.
```

---

---

## 📋 Chapter 9: Complete Cheat Sheet

```text
╔══════════════════════════════════════════════════════════════════════╗
║               COMPOSE ANIMATIONS CHEAT SHEET                        ║
╠══════════════════════════════════════════════════════════════════════╣
║                                                                      ║
║  📐 animateContentSize()                                             ║
║     Modifier.animateContentSize()                                    ║
║     → Auto-animates when a composable's size changes                 ║
║     → Easiest animation. One line. Use for expand/collapse.          ║
║                                                                      ║
║  👁️ AnimatedVisibility                                               ║
║     AnimatedVisibility(visible = boolean) { Content() }              ║
║     → Animates a composable appearing/disappearing                   ║
║     → Custom enter/exit: slideIn, fadeIn, scaleIn, expandIn          ║
║                                                                      ║
║  🎯 animate*AsState                                                  ║
║     val v by animateFloatAsState(targetValue, label)                 ║
║     val c by animateColorAsState(targetValue, label)                 ║
║     val d by animateDpAsState(targetValue, label)                    ║
║     → Smoothly transitions a single value when it changes            ║
║     → Use for: opacity, color, size, position, rotation              ║
║                                                                      ║
║  🔄 Crossfade                                                        ║
║     Crossfade(targetState = state) { state -> Content(state) }       ║
║     → Fades between two different composables                        ║
║     → Use for: loading → content, tab switching                      ║
║                                                                      ║
║  🎬 AnimatedContent                                                  ║
║     AnimatedContent(targetState, transitionSpec) { state -> }        ║
║     → Slide/fade/scale between content states                        ║
║     → Direction-aware (can check target > initial)                   ║
║     → Use for: counters, step indicators, page numbers               ║
║                                                                      ║
║  ⏱️ Animation Specs                                                  ║
║     tween(durationMillis, easing)  → Fixed duration, precise         ║
║     spring(dampingRatio, stiffness) → Physics-based, bouncy          ║
║                                                                      ║
║  📈 Common Easings                                                   ║
║     FastOutSlowIn   → Material default (quick start, gentle stop)    ║
║     LinearEasing    → Constant speed (robotic)                       ║
║     EaseInOutCubic  → Very smooth, professional                      ║
║                                                                      ║
╚══════════════════════════════════════════════════════════════════════╝
```

---

---

## 📝 Quiz — Test Your Understanding

> Answer each question, then check the answer below it.

---

### ❓ Question 1

```kotlin
@Composable
fun ExpandableCard() {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(16.dp)
    ) {
        Text("Title")
        if (expanded) {
            Text("Extra details that make this card taller...")
        }
    }
}
```

The card currently snaps instantly between small and large when tapped. What is the **SIMPLEST** change to make the size transition smooth?

```text
A) Replace if (expanded) with AnimatedVisibility(visible = expanded)
B) Add .animateContentSize() to the Column's modifier chain
C) Wrap the entire Column in Crossfade(targetState = expanded)
D) Use animateDpAsState to manually animate the Column's height
```

<details> <summary>Click to reveal answer</summary>

**Answer: B**

Adding `.animateContentSize()` to the Column's modifier is the simplest
solution — it is literally one line of code. It automatically detects that
the Column's size is changing (because the `if (expanded)` block adds or
removes content) and smoothly animates the height transition.

- **Option A (`AnimatedVisibility`)** would also work but is more code and is
  better suited for completely showing/hiding a composable, not for animating
  a parent's size change.
- **Option C (`Crossfade`)** is for swapping between two completely different composables, not for size changes.
- **Option D (`animateDpAsState`)** would work but requires you to manually calculate
  and apply the height, which is unnecessary when `animateContentSize()` does it automatically.

</details>

---

### ❓ Question 2

```kotlin
@Composable
fun ColorBox() {
    var isRed by remember { mutableStateOf(false) }

    val color by animateColorAsState(
        targetValue = if (isRed) Color.Red else Color.Blue,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessVeryLow
        ),
        label = "color"
    )

    Box(
        modifier = Modifier
            .size(100.dp)
            .background(color)
            .clickable { isRed = !isRed }
    )
}
```

When the user taps the box, what will the color transition look like?

```text
A) An instant color swap from blue to red with no animation
B) A smooth, fast 300ms fade from blue to red
C) A slow, bouncy transition where the color oscillates between
   blue and red several times before settling on the final color,
   because of the high damping ratio (bouncy) and very low stiffness (slow)
D) A linear transition that takes exactly 1 second
```

<details> <summary>Click to reveal answer</summary>

**Answer: C**

The spring animation spec with `DampingRatioHighBouncy` and
`StiffnessVeryLow` creates a very playful, slow, bouncy animation.
"High bouncy" means the value will overshoot the target and oscillate
back and forth several times. "Very low stiffness" means the spring
is loose and slow, so the entire animation takes a long time to settle.
The color will appear to wobble between blue-ish and red-ish tones
before finally landing on the target color. This is a deliberately
exaggerated example — in a real app, you would use more moderate
spring values or a `tween` for color transitions.

</details>

---

### ❓ Question 3

You are building a screen that shows a loading spinner while data loads, then shows the actual content once the data arrives. The two screens are completely different layouts. Which animation API is the best fit?

```text
A) animateContentSize() on the parent Column
B) animateFloatAsState to animate the opacity of both screens
C) Crossfade(targetState = screenState) wrapping a when block
   that shows either the loading spinner or the content
D) AnimatedContent with a slideInVertically transition
```

<details> <summary>Click to reveal answer</summary>

**Answer: C**

`Crossfade` is designed exactly for this use case: smoothly transitioning
between two completely different composables based on a state change.
You pass the current screen state as `targetState`, and inside the lambda,
you use a `when` block to show the appropriate content. When the state
changes from `Loading` to `Content`, the spinner fades out while the
content fades in.

- **Option A (`animateContentSize`)** only animates size changes, not content swapping.
- **Option B (`animateFloatAsState`)** could technically work but requires you to manually manage two overlapping composables and their opacity values — much more code for the same result.
- **Option D (`AnimatedContent`)** would also work and gives you more control over the transition direction, but `Crossfade` is simpler and more appropriate when you just need a fade between unrelated screens.

</details>

---

### ❓ Question 4

```kotlin
@Composable
fun Counter() {
    var count by remember { mutableStateOf(0) }

    AnimatedContent(
        targetState = count,
        transitionSpec = {
            if (targetState > initialState) {
                slideInVertically { it } + fadeIn() togetherWith
                slideOutVertically { -it } + fadeOut()
            } else {
                slideInVertically { -it } + fadeIn() togetherWith
                slideOutVertically { it } + fadeOut()
            }
        },
        label = "counter"
    ) { targetCount ->
        Text("$targetCount", fontSize = 48.sp)
    }
}
```

The count changes from 5 to 6. What animation plays?

```text
A) "5" fades out and "6" fades in (no sliding)
B) "5" slides UP and fades out while "6" slides in from BELOW and fades in,
   because the new value (6) is greater than the old value (5), so the
   targetState > initialState branch runs
C) "5" slides DOWN and "6" slides in from ABOVE
D) Both "5" and "6" are shown side by side and the user swipes between them
```

<details> <summary>Click to reveal answer</summary>

**Answer: B**

The `transitionSpec` lambda has access to `targetState` (the new value, 6)
and `initialState` (the old value, 5). Since 6 > 5, the first branch runs:
`slideInVertically { it }` means the new content ("6") slides in from the
bottom (positive offset = below), and `slideOutVertically { -it }` means
the old content ("5") slides out toward the top (negative offset = above).
This creates a "slot machine" effect where numbers scroll upward when
increasing. If the count decreased (e.g., 6 → 5), the `else` branch would
run and the numbers would scroll downward instead.

</details>

---

### ❓ Question 5

```kotlin
@Composable
fun MyScreen() {
    var showDetails by remember { mutableStateOf(false) }

    Column {
        Button(onClick = { showDetails = true }) {
            Text("Show Details")
        }

        AnimatedVisibility(
            visible = showDetails,
            enter = fadeIn(animationSpec = tween(2000)),
            exit = fadeOut(animationSpec = tween(2000))
        ) {
            Text("Here are the details...")
        }
    }
}
```

What is the UX problem with this animation?

```text
A) AnimatedVisibility doesn't support fadeIn and fadeOut
B) The animation duration is 2000ms (2 seconds), which is far too slow
   for a simple fade-in. The user will think the app is laggy or frozen.
   Most UI transitions should be 150-300ms.
C) The exit animation will never play because showDetails never
   changes back to false
D) Both B and C are problems
```

<details> <summary>Click to reveal answer</summary>

**Answer: D**

There are two problems here:

- **Problem 1 (B):** A 2000ms (2-second) fade animation is extremely slow
  for a simple text reveal. The user taps "Show Details" and then waits
  2 full seconds for the text to fully appear. This feels broken and laggy.
  Good UI animations should be 150-300ms — fast enough to feel instant but
  slow enough to be perceived as smooth.

- **Problem 2 (C):** The button only sets `showDetails = true`. There is
  no way to set it back to `false`, which means the exit animation
  (`fadeOut`) will never play. The details text appears once and stays
  forever. If you define an exit animation, you should also provide a
  way to trigger it (e.g., a "Hide Details" button or a toggle).

Both issues together make this a poor user experience.

</details>