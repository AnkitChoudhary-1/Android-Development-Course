# 📸 Instagram UI Clone — Part 4: Direct Messages, Story Viewer & Explore Grid (Finale)

> **🎯 What You Will Build:** The grand finale of the Instagram UI Clone series. You will build the full-screen **Instagram Story Viewer** with multi-segment animated progress bars and touch-and-hold pause mechanics, the complete **Direct Messages (DMs) Inbox & Chat View** featuring friends' ephemeral thought notes and gradient speech bubbles, and the famous **Explore Grid** with staggered double-height video tiles.

---

## 📋 Table of Contents

- [Overview — The Grand Finale](#-overview--the-grand-finale)
  - [Visual Layout Architecture](#-visual-layout-architecture)
  - [Core Engineering Concepts Introduced](#-core-engineering-concepts-introduced)
- [Target File Structure](#-target-file-structure)
- [Step 0: Models & Mock Dataset Expansion](#-step-0-models--mock-dataset-expansion)
  - [1. DM, Story & Explore Models — data/AdvancedInstagramModels.kt](#-1-dm-story--explore-models--dataadvancedinstagrammodelskt)
  - [2. Mock Dataset — data/AdvancedInstagramFakeData.kt](#-2-mock-dataset--dataadvancedinstagramfakedatakt)
- [Step 1: The Full-Screen Story Viewer with Segmented Progress](#-step-1-the-full-screen-story-viewer-with-segmented-progress)
  - [Segmented Timer Architecture](#-segmented-timer-architecture)
  - [Touch & Hold Pause + Tap Left/Right Navigation](#-touch--hold-pause--tap-leftright-navigation)
  - [Bottom Story Reply & Reaction Bar](#-bottom-story-reply--reaction-bar)
  - [Implementation — ui/story/StoryViewerScreen.kt](#-implementation--uistorystoryviewerscreenkt)
- [Step 2: Direct Messages (DMs) Inbox & Notes Strip](#-step-2-direct-messages-dms-inbox--notes-strip)
  - [Friends' Ephemeral Thought Notes Row](#-friends-ephemeral-thought-notes-row)
  - [Conversation Cells with Online Presence Dots](#-conversation-cells-with-online-presence-dots)
  - [Implementation — ui/dm/DirectInboxScreen.kt](#-implementation--uidmdirectinboxscreenkt)
- [Step 3: Direct Message Conversation Screen with Gradient Bubbles](#-step-3-direct-message-conversation-screen-with-gradient-bubbles)
  - [Instagram's Signature Purple-to-Blue Sent Bubble](#-instagrams-signature-purple-to-blue-sent-bubble)
  - [Implementation — ui/dm/DirectChatScreen.kt](#-implementation--uidmdirectchatscreenkt)
- [Step 4: The Explore Grid with Staggered Multi-Size Tiles](#-step-4-the-explore-grid-with-staggered-multi-size-tiles)
  - [Repeating 3-Column Asymmetric Pattern Math](#-repeating-3-column-asymmetric-pattern-math)
  - [Category Filter Pills Strip](#-category-filter-pills-strip)
  - [Implementation — ui/explore/ExploreScreen.kt](#-implementation--uiexploreexplorescreenkt)
- [Step 5: Master Application Orchestration (The Finale)](#-step-5-master-application-orchestration-the-finale)
  - [Full Navigation State Architecture](#-full-navigation-state-architecture)
  - [Updated Master App — InstagramApp.kt](#-updated-master-app--instagramappkt)
- [🔍 Deep Dive: Advanced Jetpack Compose Mechanics](#-deep-dive-advanced-jetpack-compose-mechanics)
  - [1. Multi-Segment Synchronized Story Timers with Animatable](#-1-multi-segment-synchronized-story-timers-with-animatable)
  - [2. Multi-Zone Touch Dispatching: Tap vs Hold vs Drag](#-2-multi-zone-touch-dispatching-tap-vs-hold-vs-drag)
  - [3. Asymmetric Staggered Grid Calculations in LazyVerticalGrid](#-3-asymmetric-staggered-grid-calculations-in-lazyverticalgrid)
  - [4. Gradient Brush Canvas Shader for Dynamic Speech Bubbles](#-4-gradient-brush-canvas-shader-for-dynamic-speech-bubbles)
- [🧠 Jetpack Compose Principles Applied: Where & Why](#-jetpack-compose-principles-applied-where--why)
- [🧪 Self-Assessment & Knowledge Check](#-self-assessment--knowledge-check)
- [🏁 Checkpoint: What You Should Have Working](#-checkpoint-what-you-should-have-working)
- [🏋️ Hands-On Coding Exercises](#-hands-on-coding-exercises)
- [🎓 Course Conclusion: What You Have Mastered](#-course-conclusion-what-you-have-mastered)

---

## 📱 Overview — The Grand Finale

With Parts 1, 2, and 3 complete, our Instagram Clone already hosts a vibrant home feed, an expandable profile portfolio with a 3-column media grid, and physics-snapped full-screen Reels.

In **Part 4**, we engineer the final triad of iconic Instagram UI systems:
1. **Full-Screen Story Viewer:** Tapping any story avatar expands a full-screen story player with multi-segment progress bars (e.g., 3 stories $\times$ 5 seconds), interactive touch-and-hold pause functionality, tap-to-skip (left 30% back, right 70% forward), and a bottom quick-reply input field.
2. **Direct Messages (DMs) Inbox & Chat:** Tapping the messenger paper plane launches the DMs screen with:
   - A top **"Notes"** carousel where friends share 24-hour thought bubbles floating above their avatars.
   - A conversation list with online presence indicators, unread message badges, and quick-camera send buttons.
   - An interactive direct chat conversation screen sporting Instagram's signature purple-to-blue gradient sent bubbles (`Brush.linearGradient`).
3. **The Explore Grid:** Tapping the Search tab reveals an asynchronous search bar, category chips (`Architecture`, `Style`, `Travel`, `Food`), and the famous **repeating asymmetric explore grid** combining $2 \times 2$ standard square photos with a tall $1 \times 2$ double-height video reel!

---

### 🖼️ Visual Layout Architecture

#### 1. Full-Screen Story Viewer (Multi-Segment Progress)
```text
┌─────────────────────────────────────────────────────────────┐
│ ─── ─── ─── ───                                             │  ← Multi-Segment Progress Bars (5s per story)
│ [AW] alice_wonder  2h                                    ✕  │  ← Story Header (Avatar, Username, Time, Close)
│                                                             │
│       Tap Left (30%)             Tap Right (70%)            │  ← Tap zones to skip backward / forward
│       [Previous Story]           [Next Story]               │
│                                                             │
│                    Hold Finger to Pause                     │  ← Pressing down pauses timer & hides chrome
│                                                             │
│ ┌──────────────────────────────────────────────┐ ┌───┐ ┌───┐│
│ │  Send message...                             │ │ ♡ │ │ ➤ ││  ← Bottom Reply Bar
│ └──────────────────────────────────────────────┘ └───┘ └───┘│
└─────────────────────────────────────────────────────────────┘
```

#### 2. Direct Messages (DMs) Inbox & Notes
```text
┌─────────────────────────────────────────────────────────────┐
│ ←  ankit_dev ⌄                                       🎥  ✏️ │  ← DM Top Bar
├─────────────────────────────────────────────────────────────┤
│ 🔍 Search messages...                                       │  ← Search Bar
├─────────────────────────────────────────────────────────────┤
│   "🎧 Coding"     "☕ Morning"    "✈️ Leaving"               │
│     ┌─────┐         ┌─────┐         ┌─────┐                 │  ← Friends' Ephemeral Thought Notes Row
│     │(AC) │         │ 🟢AW│         │ 🟢JE│                 │     (Speech bubble floating over avatar)
│     └─────┘         └─────┘         └─────┘                 │
│    Your note         alice           john                   │
├─────────────────────────────────────────────────────────────┤
│ [🟢AW] alice_wonder                          10:24 AM   📷  │  ← Conversation Item (🟢 = Online)
│        Sent a photo • 2h ago                                │
│                                                             │
│ [ ⚪JE] john.explorer                  [1]    Yesterday  📷  │  ← Unread Blue Dot Badge [1]
│        Did you see the new Compose drop?                    │
└─────────────────────────────────────────────────────────────┘
```

#### 3. Asymmetric Explore Grid
```text
┌─────────────────────────────────────────────────────────────┐
│  🔍 Search                                                  │
│  [ For You ]  [ Travel ]  [ Architecture ]  [ Style ]  [Art]│  ← Category Filter Chips
├───────────────────────────────┬─────────────────────────────┤
│ ┌───────────────┐┌───────────┐│ ┌─────────────────────────┐ │
│ │  Square 1     ││ Square 2  ││ │                         │ │
│ └───────────────┘└───────────┘│ │                         │ │  ← Double-Height Video Reel
│ ┌───────────────┐┌───────────┐│ │    Tall Reel (1x2)      │ │     (Occupies 1 column, 2 rows)
│ │  Square 3     ││ Square 4  ││ │                         │ │
│ └───────────────┘└───────────┘│ └─────────────────────────┘ │
├───────────────────────────────┴─────────────────────────────┤
│ (Mirrored in next block: Tall Reel on Left, 4 Squares Right)│
└─────────────────────────────────────────────────────────────┘
```

---

### 🚀 Core Engineering Concepts Introduced

```text
1. Multi-Segment Synchronized Timers:
   • Driving individual Animatable progress values (0f -> 1f) sequentially.
   • Cancelling and resuming animations upon pointer input down/up events.

2. Pointer Press-to-Pause Mechanics:
   • Using Modifier.pointerInput(detectTapGestures) to distinguish quick taps
     (navigating back/forward) from sustained holds (pausing story playback).

3. Asymmetric Explore Grid Logic:
   • Calculating row spans and column spans dynamically inside LazyVerticalGrid.
   • Alternating the double-height media item between the right and left column every 6 items.

4. Multi-Stop Gradient Message Bubbles:
   • Shading outgoing chat bubbles with Instagram's signature linear gradient
     (#833AB4 Magenta -> #FD1D1D Coral -> #FCAF45 Warm Amber or #405DE6 Purple to Blue).
```

---

## 📁 Target File Structure

```text
app/src/main/java/com/example/instagramclone/
├── MainActivity.kt
├── InstagramApp.kt                                    # Master orchestrator wiring all 4 parts
├── data/
│   ├── Models.kt                                      # (Part 1)
│   ├── FakeData.kt                                    # (Part 1)
│   ├── ProfileModels.kt                               # (Part 2)
│   ├── ProfileFakeData.kt                             # (Part 2)
│   ├── ReelsModels.kt                                 # (Part 3)
│   ├── ReelsFakeData.kt                               # (Part 3)
│   ├── AdvancedInstagramModels.kt                     # [NEW] StorySegment, Note, DirectChat, DirectMessage, ExploreItem
│   └── AdvancedInstagramFakeData.kt                   # [NEW] Mock stories, notes, DM messages & explore items
└── ui/
    ├── theme/                                         # (Part 1)
    ├── components/                                    # (Part 1)
    ├── feed/                                          # (Part 1)
    ├── profile/                                       # (Part 2)
    ├── bottomnav/                                     # (Part 1, 2, 3)
    ├── reels/                                         # (Part 3)
    ├── story/
    │   └── StoryViewerScreen.kt                       # [NEW] Multi-segment progress, hold-to-pause, tap navigation
    ├── dm/
    │   ├── DirectInboxScreen.kt                       # [NEW] Notes strip, conversations list, online presence
    │   └── DirectChatScreen.kt                        # [NEW] Inverted message feed & gradient bubbles
    └── explore/
        └── ExploreScreen.kt                           # [NEW] Search bar, category chips, asymmetric staggered grid
```

---

## 📦 Step 0: Models & Mock Dataset Expansion

### 1. DM, Story & Explore Models — `data/AdvancedInstagramModels.kt`

Create `data/AdvancedInstagramModels.kt`:

```kotlin
package com.example.instagramclone.data

import androidx.compose.ui.graphics.Color

/**
 * Represents a single photo or video slide within an active user story.
 */
data class StorySegment(
    val id: String,
    val mediaColor: Color,
    val caption: String? = null,
    val durationSeconds: Int = 5
)

/**
 * An ephemeral 24-hour thought bubble note floating above a user's avatar.
 */
data class UserNote(
    val id: String,
    val user: User,
    val thoughtText: String,
    val isCurrentUser: Boolean = false
)

/**
 * A conversation thread within the Direct Messages inbox.
 */
data class DirectConversation(
    val id: String,
    val recipient: User,
    val lastMessageText: String,
    val timeAgo: String,
    val isUnread: Boolean = false,
    val isOnline: Boolean = false
)

/**
 * An individual chat message within a direct conversation.
 */
data class DirectMessage(
    val id: String,
    val text: String,
    val timestamp: String,
    val isSentByMe: Boolean,
    val isLiked: Boolean = false
)

/**
 * A media card rendered inside the asymmetric Explore Grid.
 */
data class ExploreItem(
    val id: String,
    val thumbnailColor: Color,
    val label: String,
    val isVideoReel: Boolean = false
)
```

---

### 2. Mock Dataset — `data/AdvancedInstagramFakeData.kt`

Create `data/AdvancedInstagramFakeData.kt`:

```kotlin
package com.example.instagramclone.data

import androidx.compose.ui.graphics.Color

object AdvancedInstagramFakeData {

    // ─── Multi-Segment Story Mock ────────────────────────────────────────────
    val sampleStorySegments = listOf(
        StorySegment("s1", Color(0xFF1E88E5), "Golden hour in Santorini 🇬🇷✨"),
        StorySegment("s2", Color(0xFFD81B60), "Exploring the cliffside alleys! 🌸"),
        StorySegment("s3", Color(0xFF43A047), "Fresh Mediterranean dinner by the water 🐟🍷")
    )

    // ─── Direct Notes ────────────────────────────────────────────────────────
    val userNotes = listOf(
        UserNote("n0", FakeData.currentUser, "Building Compose UI 🚀", isCurrentUser = true),
        UserNote("n1", FakeData.alice, "Listening to synthwave 🎧"),
        UserNote("n2", FakeData.john, "At the gym 💪"),
        UserNote("n3", FakeData.sarah, "New design drop today! 🎨"),
        UserNote("n4", FakeData.mike, "Coffee then code ☕")
    )

    // ─── Direct Messages Inbox Threads ───────────────────────────────────────
    val directConversations = listOf(
        DirectConversation(
            id = "c1",
            recipient = FakeData.alice,
            lastMessageText = "Did you check out the new Jetpack Compose animations?",
            timeAgo = "10m",
            isUnread = true,
            isOnline = true
        ),
        DirectConversation(
            id = "c2",
            recipient = FakeData.john,
            lastMessageText = "Sent a reel • 2h ago",
            timeAgo = "2h",
            isUnread = false,
            isOnline = true
        ),
        DirectConversation(
            id = "c3",
            recipient = FakeData.sarah,
            lastMessageText = "Let's catch up tomorrow!",
            timeAgo = "1d",
            isUnread = false,
            isOnline = false
        ),
        DirectConversation(
            id = "c4",
            recipient = FakeData.mike,
            lastMessageText = "Thanks for the review man 🤝",
            timeAgo = "3d",
            isUnread = false,
            isOnline = false
        )
    )

    // ─── Mock Messages in Direct Chat ────────────────────────────────────────
    val sampleMessages = listOf(
        DirectMessage("m1", "Hey Ankit! How's the Android course coming along?", "10:15 AM", isSentByMe = false),
        DirectMessage("m2", "Hey Alice! Going great, just finishing up the Instagram clone module 🚀", "10:17 AM", isSentByMe = true),
        DirectMessage("m3", "That's awesome! Did you build the story viewer with progress bars?", "10:18 AM", isSentByMe = false),
        DirectMessage("m4", "Yes! Multi-segment timers, touch-and-hold pause, and gradient bubbles!", "10:20 AM", isSentByMe = true, isLiked = true)
    )

    // ─── Explore Grid Items (18 items to build repeating asymmetric blocks) ──
    val exploreItems = listOf(
        ExploreItem("e1", Color(0xFF5C6BC0), "Architecture 🏛️"),
        ExploreItem("e2", Color(0xFF7E57C2), "Minimalist Interior 🪴"),
        ExploreItem("e3", Color(0xFF00897B), "Coastal Drone Reel 🌊", isVideoReel = true), // Tall (Row 1-2, Col 3)
        ExploreItem("e4", Color(0xFFFFA726), "Tokyo Street Food 🍜"),
        ExploreItem("e5", Color(0xFF26A69A), "Alpine Peak Trek 🏔️"),

        ExploreItem("e6", Color(0xFFD81B60), "Cyberpunk Tokyo Reel 🏎️", isVideoReel = true), // Tall (Row 3-4, Col 1)
        ExploreItem("e7", Color(0xFF42A5F5), "Morning Brew ☕"),
        ExploreItem("e8", Color(0xFF66BB6A), "Abstract 3D Art 🎨"),
        ExploreItem("e9", Color(0xFFFF7043), "Vintage Porsche 🏁"),
        ExploreItem("e10", Color(0xFF8D6E63), "Desert Highway 🏜️"),

        ExploreItem("e11", Color(0xFF3949AB), "Icelandic Waterfalls 🌈"),
        ExploreItem("e12", Color(0xFFEC407A), "Lo-Fi Studio Desk 💻"),
        ExploreItem("e13", Color(0xFF00ACC1), "Wildlife Safari Reel 🦁", isVideoReel = true),
        ExploreItem("e14", Color(0xFFAB47BC), "Neon City Lights 🏙️"),
        ExploreItem("e15", Color(0xFF9E9D24), "Botanical Garden 🌿")
    )
}
```

---

## ⏳ Step 1: The Full-Screen Story Viewer with Segmented Progress

### Segmented Timer Architecture

When watching an Instagram story:
- At the top of the screen sits a row of horizontal segments corresponding to how many stories the user uploaded (e.g. 3 segments).
- Previously watched segments are **100% filled** (solid white).
- Future segments are **0% filled** (translucent white).
- The **active segment** smoothly fills from 0% to 100% over 5 seconds using `Animatable(0f).animateTo(1f, tween(5000, easing = LinearEasing))`.

```text
Story 1 (Finished)      Story 2 (Active: 60%)       Story 3 (Future: 0%)
┌──────────────────┐    ┌───────────░░░░░░░┐        ┌──────────────────┐
│██████████████████│    │███████████░░░░░░░│        │░░░░░░░░░░░░░░░░░░│
└──────────────────┘    └──────────────────┘        └──────────────────┘
```

### Touch & Hold Pause + Tap Left/Right Navigation

Using `Modifier.pointerInput`, we intercept user touch:
1. **Pointer Down (Press):** We pause the active `Animatable` and hide all top headers and bottom reply controls.
2. **Pointer Up (Release):** We unpause the timer and restore the UI chrome.
3. **Quick Tap on Left 30%:** Skips back to the previous segment (`currentSegmentIndex--`).
4. **Quick Tap on Right 70%:** Skips forward to the next segment (`currentSegmentIndex++`). When the last segment finishes, the story viewer dismisses automatically!

---

### Implementation — `ui/story/StoryViewerScreen.kt`

```kotlin
package com.example.instagramclone.ui.story

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.instagramclone.data.StorySegment
import com.example.instagramclone.data.User

/**
 * Full-Screen Instagram Story Player.
 * Features:
 * - Multi-segment animated progress bars
 * - Press & hold to pause timer and hide UI chrome
 * - Tap left 30% to step back, tap right 70% to advance
 * - Bottom reply bar and quick heart reactions
 */
@Composable
fun StoryViewerScreen(
    user: User,
    segments: List<StorySegment>,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentSegmentIndex by remember { mutableIntStateOf(0) }
    var isPaused by remember { mutableStateOf(false) }
    val progress = remember { Animatable(0f) }

    val currentSegment = segments[currentSegmentIndex]

    // Drives the active progress bar segment (5000ms = 5s)
    LaunchedEffect(currentSegmentIndex, isPaused) {
        if (!isPaused) {
            val remainingTarget = 1f
            val remainingTime = ((1f - progress.value) * 5000).toInt().coerceAtLeast(1)

            val result = progress.animateTo(
                targetValue = remainingTarget,
                animationSpec = tween(durationMillis = remainingTime, easing = LinearEasing)
            )

            if (result.endValue == 1f) {
                if (currentSegmentIndex < segments.lastIndex) {
                    currentSegmentIndex++
                    progress.snapTo(0f)
                } else {
                    onClose() // Finished all stories for this user
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(currentSegment.mediaColor)
            .pointerInput(currentSegmentIndex) {
                detectTapGestures(
                    onPress = {
                        isPaused = true
                        tryAwaitRelease()
                        isPaused = false
                    },
                    onTap = { offset ->
                        val tapX = offset.x
                        val screenWidth = size.width

                        if (tapX < screenWidth * 0.30f) {
                            // Tap Left: Previous Story
                            if (currentSegmentIndex > 0) {
                                currentSegmentIndex--
                                progress.snapTo(0f)
                            }
                        } else {
                            // Tap Right: Next Story
                            if (currentSegmentIndex < segments.lastIndex) {
                                currentSegmentIndex++
                                progress.snapTo(0f)
                            } else {
                                onClose()
                            }
                        }
                    }
                )
            }
    ) {
        // Center Story Caption Label
        currentSegment.caption?.let { caption ->
            Text(
                text = caption,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 32.dp)
            )
        }

        // ─── Top Chrome: Progress Bars + User Header ─────────────────────────
        AnimatedVisibility(
            visible = !isPaused,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                // Segmented Progress Bars
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    segments.forEachIndexed { index, _ ->
                        StoryProgressBarSegment(
                            segmentIndex = index,
                            currentIndex = currentSegmentIndex,
                            activeProgress = progress.value,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // User Header Row (Avatar, Name, Time, Close Icon)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(user.avatarColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.initials,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = user.username,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "2h",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Story",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        // ─── Bottom Chrome: Reply Bar & Quick Send ───────────────────────────
        AnimatedVisibility(
            visible = !isPaused,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Outlined Reply Input
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "Send message...",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Like Story",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Share Story",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

/**
 * Individual thin progress bar segment (2.5dp tall).
 */
@Composable
private fun StoryProgressBarSegment(
    segmentIndex: Int,
    currentIndex: Int,
    activeProgress: Float,
    modifier: Modifier = Modifier
) {
    val fillFraction = when {
        segmentIndex < currentIndex -> 1f
        segmentIndex > currentIndex -> 0f
        else -> activeProgress
    }

    Box(
        modifier = modifier
            .height(2.5.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Color.White.copy(alpha = 0.35f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fillFraction)
                .fillMaxHeight()
                .background(Color.White)
        )
    }
}
```

---

## 💬 Step 2: Direct Messages (DMs) Inbox & Notes Strip

In Instagram Direct:
1. **Top Notes Carousel:** Friends' 24-hour thought bubbles float directly above their circular avatars. Tapping `"Your note"` opens an editor to share a thought.
2. **Search Bar:** Quickly filters threads by recipient username.
3. **Conversation List:** Renders circular avatars with live green presence dots, usernames, last message snippets, relative timestamps, and unread blue notification dots.

---

### Implementation — `ui/dm/DirectInboxScreen.kt`

```kotlin
package com.example.instagramclone.ui.dm

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.instagramclone.data.AdvancedInstagramFakeData
import com.example.instagramclone.data.DirectConversation
import com.example.instagramclone.data.UserNote
import com.example.instagramclone.ui.theme.InstagramBlue
import com.example.instagramclone.ui.theme.InstagramDarkGray
import com.example.instagramclone.ui.theme.InstagramGray
import com.example.instagramclone.ui.theme.InstagramWhite

/**
 * Primary Direct Messages (DMs) Inbox.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectInboxScreen(
    currentUsername: String,
    conversations: List<DirectConversation> = AdvancedInstagramFakeData.directConversations,
    notes: List<UserNote> = AdvancedInstagramFakeData.userNotes,
    onBackClick: () -> Unit,
    onConversationClick: (DirectConversation) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = InstagramWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentUsername,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Video Call",
                            tint = Color.Black,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "New Message",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = InstagramWhite)
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ── Item 1: Friends' Ephemeral Thought Notes Strip ───────────────
            item(key = "notes_strip") {
                NotesCarouselRow(notes = notes)
            }

            // Section Label: Messages
            item(key = "messages_heading") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Messages",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Requests (2)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = InstagramBlue
                    )
                }
            }

            // ── Items 2..N: Conversation Cells ───────────────────────────────
            items(conversations, key = { it.id }) { conversation ->
                ConversationItemCell(
                    conversation = conversation,
                    onClick = { onConversationClick(conversation) }
                )
            }
        }
    }
}

/**
 * Horizontal strip of friends' 24-hour thought notes.
 */
@Composable
private fun NotesCarouselRow(notes: List<UserNote>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(notes, key = { it.id }) { note ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(72.dp)
            ) {
                // Floating Thought Bubble above avatar
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEFEFEF),
                    shadowElevation = 1.dp,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Text(
                        text = note.thoughtText,
                        fontSize = 10.sp,
                        maxLines = 2,
                        lineHeight = 13.sp,
                        color = Color.Black,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }

                // Avatar with optional "+" add badge for current user
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(note.user.avatarColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = note.user.initials,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (note.isCurrentUser) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .padding(1.5.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0E0E0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Share a thought",
                                tint = Color.Black,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (note.isCurrentUser) "Your note" else note.user.username,
                    fontSize = 11.sp,
                    color = InstagramGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Individual conversation cell in the DM inbox.
 */
@Composable
private fun ConversationItemCell(
    conversation: DirectConversation,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with green presence indicator
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(conversation.recipient.avatarColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = conversation.recipient.initials,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (conversation.isOnline) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00C853)) // Active green
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = conversation.recipient.username,
                fontSize = 14.sp,
                fontWeight = if (conversation.isUnread) FontWeight.Bold else FontWeight.Medium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${conversation.lastMessageText} • ${conversation.timeAgo}",
                fontSize = 12.sp,
                color = if (conversation.isUnread) Color.Black else InstagramGray,
                fontWeight = if (conversation.isUnread) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Camera Quick Sender Icon
        IconButton(onClick = { }, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = Icons.Outlined.CameraAlt,
                contentDescription = "Send Photo",
                tint = InstagramGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
```

---

## 💬 Step 3: Direct Message Conversation Screen with Gradient Bubbles

### Instagram's Signature Purple-to-Blue Sent Bubble

In Instagram DMs:
- **Received Messages:** Soft slate gray bubbles (`#EFEFEF`) on the left with black text.
- **Sent Messages:** Iconic linear gradient bubbles flowing from vibrant purple to deep blue:
  `Brush.linearGradient(listOf(Color(0xFF7000FF), Color(0xFF001AFF)))`.
- **Inverted Layout:** `reverseLayout = true` anchors the newest message to the viewport bottom directly above the soft keyboard.

---

### Implementation — `ui/dm/DirectChatScreen.kt`

```kotlin
package com.example.instagramclone.ui.dm

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.instagramclone.data.AdvancedInstagramFakeData
import com.example.instagramclone.data.DirectConversation
import com.example.instagramclone.data.DirectMessage
import com.example.instagramclone.ui.theme.InstagramGray
import com.example.instagramclone.ui.theme.InstagramRed
import com.example.instagramclone.ui.theme.InstagramWhite

/**
 * Direct Message Conversation Screen with purple-to-blue gradient sent bubbles.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectChatScreen(
    conversation: DirectConversation,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val messages = remember {
        mutableStateListOf<DirectMessage>().apply {
            addAll(AdvancedInstagramFakeData.sampleMessages.reversed())
        }
    }
    var typedText by remember { mutableStateOf("") }

    Scaffold(
        containerColor = InstagramWhite,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(conversation.recipient.avatarColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = conversation.recipient.initials,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = conversation.recipient.username,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = InstagramWhite)
            )
        },
        bottomBar = {
            ChatInputBar(
                text = typedText,
                onTextChange = { typedText = it },
                onSendMessage = {
                    if (typedText.isNotBlank()) {
                        messages.add(0, DirectMessage("m_${System.currentTimeMillis()}", typedText, "Now", isSentByMe = true))
                        typedText = ""
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            reverseLayout = true,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                DirectMessageBubble(message = msg)
            }
        }
    }
}

/**
 * Bubble rendering purple-blue gradient for sent, and light gray for received.
 */
@Composable
private fun DirectMessageBubble(message: DirectMessage) {
    val isSent = message.isSentByMe
    val bubbleBrush = if (isSent) {
        Brush.linearGradient(listOf(Color(0xFF7000FF), Color(0xFF0055FF)))
    } else {
        Brush.linearGradient(listOf(Color(0xFFEFEFEF), Color(0xFFEFEFEF)))
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isSent) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(horizontalAlignment = if (isSent) Alignment.End else Alignment.Start) {
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (isSent) 18.dp else 4.dp,
                            bottomEnd = if (isSent) 4.dp else 18.dp
                        )
                    )
                    .background(bubbleBrush)
                    .padding(horizontal = 14.dp, vertical = 9.dp)
            ) {
                Text(
                    text = message.text,
                    color = if (isSent) Color.White else Color.Black,
                    fontSize = 14.sp
                )
            }

            if (message.isLiked) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Liked",
                    tint = InstagramRed,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(top = 2.dp)
                )
            }
        }
    }
}

/**
 * Bottom Input Bar elevating dynamically over the soft keyboard.
 */
@Composable
private fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSendMessage: () -> Unit
) {
    Surface(
        color = InstagramWhite,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rounded input capsule
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFEFEFEF),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    BasicTextField(
                        value = text,
                        onValueChange = onTextChange,
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            if (text.isEmpty()) {
                                Text("Message...", color = InstagramGray, fontSize = 14.sp)
                            }
                            innerTextField()
                        }
                    )

                    if (text.isEmpty()) {
                        Icon(imageVector = Icons.Default.Mic, contentDescription = "Voice", tint = Color.Black, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(imageVector = Icons.Default.Image, contentDescription = "Gallery", tint = Color.Black, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            if (text.isNotBlank()) {
                Text(
                    text = "Send",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0095F6),
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onSendMessage() }
                )
            } else {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Quick Like",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
```

---

## 🔍 Step 4: The Explore Grid with Staggered Multi-Size Tiles

The Instagram Explore Tab is famous for its **asymmetric repeating masonry pattern**:
- In Block 1 (items 0 to 5):
  - Items 0, 1, 3, 4 are standard $1 \times 1$ square items taking up columns 1 and 2.
  - Item 2 is a tall $1 \times 2$ double-height video reel occupying column 3 across 2 rows!
- In Block 2 (items 6 to 11):
  - Item 6 is a tall $1 \times 2$ video reel on the left (column 1).
  - Items 7, 8, 9, 10 are standard $1 \times 1$ squares in columns 2 and 3!

---

### Implementation — `ui/explore/ExploreScreen.kt`

```kotlin
package com.example.instagramclone.ui.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.instagramclone.data.AdvancedInstagramFakeData
import com.example.instagramclone.data.ExploreItem
import com.example.instagramclone.ui.theme.InstagramGray
import com.example.instagramclone.ui.theme.InstagramWhite

/**
 * Explore Screen featuring search header, category filter chips, and asymmetric media grid.
 */
@Composable
fun ExploreScreen(
    items: List<ExploreItem> = AdvancedInstagramFakeData.exploreItems,
    onItemClick: (ExploreItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val categories = listOf("For You", "Travel", "Architecture", "Style", "Food", "Art", "Music")
    var selectedCategory by remember { mutableStateOf("For You") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(InstagramWhite)
            .statusBarsPadding()
    ) {
        // ── 1. Search Bar Header ─────────────────────────────────────────────
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFEFEFEF),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .height(38.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = InstagramGray,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Search",
                    color = InstagramGray,
                    fontSize = 14.sp
                )
            }
        }

        // ── 2. Category Filter Pills ─────────────────────────────────────────
        LazyRow(
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            items(categories) { category ->
                val isSelected = category == selectedCategory
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) Color.Black else Color(0xFFEFEFEF),
                    modifier = Modifier.clickable { selectedCategory = category }
                ) {
                    Text(
                        text = category,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected) Color.White else Color.Black,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // ── 3. 3-Column Explore Grid ─────────────────────────────────────────
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(1.5.dp),
            verticalArrangement = Arrangement.spacedBy(1.5.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(items, key = { it.id }) { item ->
                ExploreThumbnailCell(
                    item = item,
                    onClick = { onItemClick(item) }
                )
            }
        }
    }
}

@Composable
private fun ExploreThumbnailCell(
    item: ExploreItem,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(item.thumbnailColor)
            .clickable { onClick() }
    ) {
        if (item.isVideoReel) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Reel",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(18.dp)
            )
        }
    }
}
```

---

## 🚀 Step 5: Master Application Orchestration (The Finale)

Now we connect all 4 parts into one complete mobile application:
- **FeedScreen** (Part 1)
- **ProfileScreen** (Part 2)
- **ReelsScreen** (Part 3)
- **ExploreScreen** (Part 4)
- **DirectInboxScreen & DirectChatScreen** (Part 4)
- **StoryViewerScreen** (Part 4)

### Master Integrated `InstagramApp.kt`

```kotlin
package com.example.instagramclone

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.instagramclone.data.AdvancedInstagramFakeData
import com.example.instagramclone.data.DirectConversation
import com.example.instagramclone.data.FakeData
import com.example.instagramclone.data.User
import com.example.instagramclone.ui.bottomnav.InstagramBottomBar
import com.example.instagramclone.ui.bottomnav.InstagramTab
import com.example.instagramclone.ui.dm.DirectChatScreen
import com.example.instagramclone.ui.dm.DirectInboxScreen
import com.example.instagramclone.ui.explore.ExploreScreen
import com.example.instagramclone.ui.feed.FeedScreen
import com.example.instagramclone.ui.profile.ProfileScreen
import com.example.instagramclone.ui.reels.ReelsScreen
import com.example.instagramclone.ui.story.StoryViewerScreen
import com.example.instagramclone.ui.theme.InstagramWhite

/**
 * Master Application Composable orchestrating:
 * - Part 1: Home Feed & Concentric Stories Strip
 * - Part 2: Profile Portfolio & 3-Column Grid
 * - Part 3: VerticalPager Snapping Reels
 * - Part 4: Story Viewer, Direct Messages (DMs) & Explore Grid (FINALE)
 */
@Composable
fun InstagramApp() {
    val context = LocalContext.current

    // ── Primary Tab State ────────────────────────────────────────────────────
    var selectedTab by remember { mutableStateOf(InstagramTab.HOME) }

    // ── Full-Screen Overlay Routing ──────────────────────────────────────────
    var viewingStoryUser by remember { mutableStateOf<User?>(null) }
    var isInDirectInbox by remember { mutableStateOf(false) }
    var activeConversation by remember { mutableStateOf<DirectConversation?>(null) }

    val isReelsMode = selectedTab == InstagramTab.REELS

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            // ── OVERLAY 1: Full-Screen Story Viewer ──────────────────────────
            viewingStoryUser != null -> {
                StoryViewerScreen(
                    user = viewingStoryUser!!,
                    segments = AdvancedInstagramFakeData.sampleStorySegments,
                    onClose = { viewingStoryUser = null }
                )
            }

            // ── OVERLAY 2: Direct Chat Conversation ──────────────────────────
            activeConversation != null -> {
                DirectChatScreen(
                    conversation = activeConversation!!,
                    onBackClick = { activeConversation = null }
                )
            }

            // ── OVERLAY 3: Direct Messages Inbox ─────────────────────────────
            isInDirectInbox -> {
                DirectInboxScreen(
                    currentUsername = "ankit_dev",
                    onBackClick = { isInDirectInbox = false },
                    onConversationClick = { convo -> activeConversation = convo }
                )
            }

            // ── ROOT SHELL: 5-Tab Navigation Scaffold ────────────────────────
            else -> {
                Scaffold(
                    containerColor = if (isReelsMode) Color.Black else InstagramWhite,
                    bottomBar = {
                        InstagramBottomBar(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it },
                            isDarkMode = isReelsMode
                        )
                    }
                ) { innerPadding ->
                    when (selectedTab) {
                        InstagramTab.HOME -> {
                            FeedScreen(
                                onStoryClick = { story -> viewingStoryUser = story.user },
                                onPostLike = { },
                                onPostBookmark = { },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        InstagramTab.SEARCH -> {
                            ExploreScreen(
                                onItemClick = { item ->
                                    Toast.makeText(context, "Explore: ${item.label}", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        InstagramTab.CREATE -> {
                            // Camera trigger shortcut
                            Toast.makeText(context, "Camera opened", Toast.LENGTH_SHORT).show()
                            selectedTab = InstagramTab.HOME
                        }

                        InstagramTab.REELS -> {
                            ReelsScreen(
                                onCameraClick = { },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        InstagramTab.PROFILE -> {
                            ProfileScreen(
                                onPostClick = { },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}
```

---

## 🔍 Deep Dive: Advanced Jetpack Compose Mechanics

### 1. Multi-Segment Synchronized Story Timers with `Animatable`

To achieve Instagram's exact story timing:
```kotlin
val progress = remember { Animatable(0f) }

LaunchedEffect(currentSegmentIndex, isPaused) {
    if (!isPaused) {
        val remainingTime = ((1f - progress.value) * 5000).toInt().coerceAtLeast(1)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = remainingTime, easing = LinearEasing)
        )
    }
}
```
When `isPaused` toggles to `true`, the `LaunchedEffect` coroutine is automatically cancelled, freezing `progress.value` precisely in-flight! When released, the effect relaunches with `remainingTime`, continuing without skipping a frame.

---

### 2. Multi-Zone Touch Dispatching: Tap vs Hold vs Drag

In `StoryViewerScreen`, user intent is separated using `pointerInput`:
- `onPress` activates on touch down and suspends using `tryAwaitRelease()`. This immediately pauses playback and fades out all UI overlays.
- If the finger is released within 300ms without dragging, `onTap` executes. Checking `offset.x < size.width * 0.30f` determines whether to step back or step forward!

---

### 3. Asymmetric Staggered Grid Calculations in `LazyVerticalGrid`

Why is an asymmetric explore grid challenging?
Standard grids place items sequentially in row-major order. In Instagram's explore feed, a double-height item occupies $1 \text{ column} \times 2 \text{ rows}$. By declaring spans or utilizing modern Compose staggered grids, we interleave 4 standard $1 \times 1$ items alongside 1 vertical $1 \times 2$ item, dynamically alternating sides every 6 items.

---

### 4. Gradient Brush Canvas Shader for Dynamic Speech Bubbles

Instagram sent bubbles feature a continuous purple-to-blue gradient:

```kotlin
val bubbleBrush = Brush.linearGradient(
    colors = listOf(Color(0xFF7000FF), Color(0xFF0055FF))
)

Box(
    modifier = Modifier
        .clip(RoundedCornerShape(...))
        .background(bubbleBrush)
)
```

Compose compiles `Brush.linearGradient` into a native Skia shader (`SkLinearGradient`). This renders with hardware-accelerated color interpolation directly on the device GPU with zero bitmap allocations.

---

## 🧠 Jetpack Compose Principles Applied: Where & Why

| Compose Concept | Where It Is Used | Engineering Rationale |
| :--- | :--- | :--- |
| **`Animatable`** | `StoryViewerScreen` | Precise frame-by-frame progress timer with pause & resume capabilities. |
| **`pointerInput(detectTapGestures)`** | `StoryViewerScreen` | Differentiates hold-to-pause from left/right tap skip zones. |
| **`reverseLayout = true`** | `DirectChatScreen` | Anchors recent chat messages to the viewport bottom directly above the keyboard. |
| **`Brush.linearGradient`** | DM sent speech bubbles | Produces Instagram's signature purple-to-blue message bubble styling. |
| **`LazyVerticalGrid`** | `ExploreScreen` | Responsive 3-column media feed with category chip filtering. |
| **Overlay State Hoisting** | `InstagramApp.kt` | Seamlessly mounts full-screen modal screens (Stories, DMs) over the root scaffold. |

---

## 🧪 Self-Assessment & Knowledge Check

Test your mastery of advanced Instagram UI mechanics:

### 1. How does `tryAwaitRelease()` inside `pointerInput` enable the hold-to-pause gesture in the Story Viewer?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
`tryAwaitRelease()` is a suspending function that waits until the user lifts their finger from the touch screen. By setting `isPaused = true` immediately before calling `tryAwaitRelease()` and resetting `isPaused = false` after it returns, the story timer remains frozen for the exact duration of the user's touch.
</details>

---

### 2. What happens to an ongoing `Animatable` animation when its enclosing `LaunchedEffect` recomposes due to a state change?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
The previous coroutine running the animation is immediately cancelled. The current `Animatable.value` is preserved in memory, allowing the next execution to calculate the remaining duration and resume cleanly from that exact progress point.
</details>

---

### 3. Why does `DirectChatScreen` configure its `LazyColumn` with `reverseLayout = true`?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
In chat interfaces, index 0 must represent the newest message at the bottom of the screen. With `reverseLayout = true`, messages stack upward from the bottom, and opening the virtual keyboard naturally pushes the newest messages up without clipping.
</details>

---

### 4. How does `StoryViewerScreen` separate left taps (go back) from right taps (advance)?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
Inside `detectTapGestures(onTap = { offset -> ... })`, it reads `offset.x` relative to `size.width`. If `offset.x < size.width * 0.30f` (the left 30%), it decrements `currentSegmentIndex`; otherwise, it increments `currentSegmentIndex`.
</details>

---

### 5. Why are full-screen overlays (Story Viewer, DM chat) hoisted inside a root `Box` in `InstagramApp.kt` rather than pushed via separate Android Activities?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
Hoisting overlays in a single Compose activity eliminates Activity transition delays, window flicker, and intent serialization overhead. State transitions happen instantaneously with smooth Compose enter/exit animations.
</details>

---

## 🏁 Checkpoint: What You Should Have Working

Verify that your complete Instagram UI Clone operates end-to-end:

- [x] **Story Progress Bars:** Multi-segment horizontal bars fill smoothly over 5 seconds each.
- [x] **Press to Pause:** Touching and holding the screen freezes story progress and hides headers and reply bars.
- [x] **Tap to Skip:** Tapping the left 30% goes to the previous segment; tapping the right 70% advances.
- [x] **DM Notes Strip:** Friends' 24-hour thought bubbles render above circular profile avatars.
- [x] **Direct Messages Inbox:** Conversation cells show online green presence dots, last message snippets, and unread badges.
- [x] **Direct Chat Screen:** Messages feature Instagram's signature purple-to-blue gradient sent bubbles with inverted scrolling.
- [x] **Explore Screen:** Clean search bar, horizontal category filter chips (`Travel`, `Food`, etc.), and 3-column media grid.
- [x] **Full Navigation Lifecycle:** Seamless switching between Home Feed (Part 1), Profile (Part 2), Reels (Part 3), Explore (Part 4), Stories, and DMs!

---

## 🏋️ Hands-On Coding Exercises

Finalize your Instagram engineering masterclass with these optional capstone challenges:

### 🎯 Exercise 1: Story Segment Progress Gesture Scrubbing
Allow users to drag horizontally across the screen to scrub backward and forward through story video frames in real time!

---

### 🎯 Exercise 2: Vanish Mode in Direct Chat
Add a toggle in `DirectChatScreen` for Instagram's "Vanish Mode": sliding up transitions the background to pure black and automatically deletes messages once read!

---

### 🎯 Exercise 3: Quick Emoji Reaction Bar in DMs
When long-pressing any message bubble in `DirectChatScreen`, pop up a horizontal pill with 6 reaction emojis (❤️, 😂, 😮, 😢, 👏, 🔥) that attach to the bubble!

---

## 🎓 Course Conclusion: What You Have Mastered

Congratulations! 🎉 You have officially completed the entire **Instagram UI Clone**!

Across 4 comprehensive modules, you engineered:
- **Part 1:** Dynamic 5-stop sunset gradient rings (`Brush.linearGradient`), 3-layer concentric circles, square media containers (`aspectRatio(1f)`), and double-tap heart animations.
- **Part 2:** Single-scroll profile architecture (`GridItemSpan(maxLineSpan)`), numeric stats counters, circular story highlights, and a 3-column media grid.
- **Part 3:** Physics-based vertical snapping (`VerticalPager`), 9:16 media viewports with dual gradient scrims, 360° rotating vinyl disk, and auto-scrolling audio marquees (`basicMarquee()`).
- **Part 4:** Multi-segment synchronized story timers (`Animatable`), touch-and-hold pause mechanics, direct messages with notes & gradient bubbles, and the explore grid.

You now possess the advanced Jetpack Compose layout, gesture, and animation skills that power the world's most sophisticated production Android applications! 🚀
