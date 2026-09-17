# 📸 Instagram UI Clone — Part 3: Instagram Reels, Vertical Snapping Pager & Immersive Overlays

> **🎯 What You Will Build:** The complete **Instagram Reels Experience** in Jetpack Compose. You will engineer a full-screen, gesture-driven vertical snapping feed using `VerticalPager`, an immersive 9:16 media viewport with dual gradient scrims, an interactive right-hand vertical action stack (Like, Comment, Share, More), an infinite 360° rotating vinyl audio disk, an auto-scrolling marquee audio ticker (`basicMarquee`), an expandable caption with follow toggle, and tap-to-pause controls.

---

## 📋 Table of Contents

- [Overview — What We're Building](#-overview--what-were-building)
  - [Visual Layout Architecture](#-visual-layout-architecture)
  - [Core Engineering Concepts Introduced](#-core-engineering-concepts-introduced)
- [Target File Structure](#-target-file-structure)
- [Step 0: Models & Mock Dataset Expansion](#-step-0-models--mock-dataset-expansion)
  - [1. Reels Domain Models — data/ReelsModels.kt](#-1-reels-domain-models--datareelsmodelskt)
  - [2. Mock Reels Dataset — data/ReelsFakeData.kt](#-2-mock-reels-dataset--datareelsfakedatakt)
- [Step 1: The Reels Media Canvas & Gradient Scrims](#-step-1-the-reels-media-canvas--gradient-scrims)
  - [Dual Gradient Scrim Geometry](#-dual-gradient-scrim-geometry)
  - [Single-Tap Play/Pause & Double-Tap Heart Gestures](#-single-tap-playpause--double-tap-heart-gestures)
  - [Implementation — ui/reels/ReelVideoCanvas.kt](#-implementation--uireelsreelvideocanvaskt)
- [Step 2: Right-Hand Vertical Action Cluster](#-step-2-right-hand-vertical-action-cluster)
  - [Action Layout & Icon Hierarchy](#-action-layout--icon-hierarchy)
  - [Rotating Vinyl Audio Disk Animation](#-rotating-vinyl-audio-disk-animation)
  - [Implementation — ui/reels/ReelActionButtons.kt](#-implementation--uireelsreelactionbuttonskt)
- [Step 3: Bottom-Left Creator Info & Audio Marquee](#-step-3-bottom-left-creator-info--audio-marquee)
  - [Follow Button State Toggle](#-follow-button-state-toggle)
  - [Expandable Caption ("...more")](#-expandable-caption-more)
  - [Horizontal Audio Marquee Ticker with basicMarquee](#-horizontal-audio-marquee-ticker-with-basicmarquee)
  - [Implementation — ui/reels/ReelCreatorDetails.kt](#-implementation--uireelsreelcreatordetailskt)
- [Step 4: Top Transparent Header Bar](#-step-4-top-transparent-header-bar)
  - [Implementation — ui/reels/ReelsTopBar.kt](#-implementation--uireelsreelstopbarkt)
- [Step 5: Full-Screen Vertical Snapping Engine](#-step-5-full-screen-vertical-snapping-engine)
  - [VerticalPager Mechanics & Page Snap State](#-verticalpager-mechanics--page-snap-state)
  - [Single Reel Container Composition](#-single-reel-container-composition)
  - [Implementation — ui/reels/ReelsScreen.kt](#-implementation--uireelsreelsscreenkt)
- [Step 6: Master Navigation & Dark Theme Bottom Bar](#-step-6-master-navigation--dark-theme-bottom-bar)
  - [Adapting the Bottom Navigation Bar for Dark Media](#-adapting-the-bottom-navigation-bar-for-dark-media)
  - [Updating InstagramApp.kt](#-updating-instagramappkt)
- [🔍 Deep Dive: Advanced Jetpack Compose Mechanics](#-deep-dive-advanced-jetpack-compose-mechanics)
  - [1. VerticalPager vs LazyColumn for Full-Screen Feeds](#-1-verticalpager-vs-lazycolumn-for-full-screen-feeds)
  - [2. Infinite Hardware Rotation with rememberInfiniteTransition](#-2-infinite-hardware-rotation-with-rememberinfinitetransition)
  - [3. Zero-Allocation Text Ticker with Modifier.basicMarquee()](#-3-zero-allocation-text-ticker-with-modifierbasicmarquee)
  - [4. Dual Touch Pipelines: Click vs Double-Click vs Drag](#-4-dual-touch-pipelines-click-vs-double-click-vs-drag)
- [🧠 Jetpack Compose Principles Applied: Where & Why](#-jetpack-compose-principles-applied-where--why)
- [🧪 Self-Assessment & Knowledge Check](#-self-assessment--knowledge-check)
- [🏁 Checkpoint: What You Should Have Working](#-checkpoint-what-you-should-have-working)
- [🏋️ Hands-On Coding Exercises](#-hands-on-coding-exercises)
- [🧭 What's Next in Part 4 (Finale)](#-whats-next-in-part-4-finale)

---

## 📱 Overview — What We're Building

Short-form vertical video (Reels) has become the dominant engagement format in modern mobile apps. Unlike standard feed lists that scroll continuously, Reels requires:
1. **Physical Page Snapping:** Swiping up snaps directly to the next video with momentum physics, using `VerticalPager`.
2. **Immersive 9:16 Full-Screen Canvas:** Video content occupies 100% of the screen under transparent status and navigation bars.
3. **Dual Gradient Scrims:** Top and bottom black-to-transparent scrim overlays ensuring white action icons, usernames, and audio text remain legible regardless of background video brightness.
4. **Right-Side Interaction Stack:** Compact vertical column featuring like counts (`❤️ 342.8K`), comment counts (`💬 1,840`), share icon (`➤`), more menu (`⋮`), and a **continuously rotating vinyl record disk**.
5. **Creator & Audio Header:** Creator avatar with follow toggle, expandable multi-line caption, and an animated **horizontal marquee audio ticker** displaying the background song title.

---

### 🖼️ Visual Layout Architecture

```text
┌─────────────────────────────────────────────────────────────┐
│  Reels                                                   📷 │  ← Transparent Top Bar
├─────────────────────────────────────────────────────────────┤
│                                                             │
│                                                             │
│                                                             │
│                                                    ┌───┐    │
│                                                    │ ♡ │    │  ← Like Button (Toggles Red ❤️)
│                                                    └───┘    │
│                                                    142.5K   │  ← Formatted Like Count
│                                                             │
│                                                    ┌───┐    │
│                                                    │ 💬│    │  ← Comment Button
│                                                    └───┘    │
│                                                     2,840   │  ← Comment Count
│                                                             │
│                                                    ┌───┐    │
│                                                    │ ➤ │    │  ← Share Button
│                                                    └───┘    │
│                                                     14.2K   │
│                                                             │
│                                                    ┌───┐    │
│                                                    │ ⋮ │    │  ← More Options Button
│                                                    └───┘    │
│                                                             │
│  [AW]  alice_wonder • Follow                                │  ← Creator Row + Follow Button
│  Sunset timelapses in 4K are therapeutic ✨                 │  ← Caption with "...more"
│  🎵  Original Audio • Synthwave Sunset Dreams...   ┌─────┐  │  ← basicMarquee Audio Ticker
│                                                    │ 💿  │  │  ← 360° Rotating Vinyl Disk
├────────────────────────────────────────────────────└─────┘──┤
│    🏠            🔍            ➕            🎬            👤   │  ← Bottom Nav (Dark Mode for Reels)
└─────────────────────────────────────────────────────────────┘
```

---

### 🚀 Core Engineering Concepts Introduced

```text
1. Snapping Feed with VerticalPager:
   • Built-in physics snapping replacing custom fling behaviors.
   • Seamless index observation to trigger play/pause lifecycle when off-screen.

2. Infinite Rotation with rememberInfiniteTransition:
   • Continuously animates vinyl record rotation from 0f to 360f.
   • Executes entirely on the GPU render thread with zero recomposition thrashing.

3. Horizontal Text Marquee with Modifier.basicMarquee():
   • Modern Compose 1.7+ API that smoothly scrolls overflowing audio track titles.
   • Eliminates third-party ticker dependencies and manual horizontal offset calculations.

4. Multi-Touch Gesture Hierarchy:
   • Single-tap toggles transient Play/Pause indicator.
   • Double-tap fires a bursting scale-up heart animation directly over the video canvas.
```

---

## 📁 Target File Structure

```text
app/src/main/java/com/example/instagramclone/
├── MainActivity.kt
├── InstagramApp.kt                            # Master scaffold routing between Feed, Profile & Reels
├── data/
│   ├── Models.kt                              # (Part 1)
│   ├── FakeData.kt                            # (Part 1)
│   ├── ProfileModels.kt                       # (Part 2)
│   ├── ProfileFakeData.kt                     # (Part 2)
│   ├── ReelsModels.kt                         # [NEW] ReelItem, AudioTrack
│   └── ReelsFakeData.kt                       # [NEW] Mock 9:16 video reels dataset
└── ui/
    ├── theme/                                 # (Part 1)
    ├── components/                            # (Part 1)
    ├── feed/                                  # (Part 1)
    ├── profile/                               # (Part 2)
    ├── bottomnav/                             # (Part 1 & 2 - updated with dark theme)
    └── reels/
        ├── ReelsTopBar.kt                     # [NEW] Transparent "Reels" header with camera launcher
        ├── ReelVideoCanvas.kt                 # [NEW] 9:16 video surface with gradient scrims & gestures
        ├── ReelActionButtons.kt               # [NEW] Right-hand vertical action stack & rotating vinyl disk
        ├── ReelCreatorDetails.kt              # [NEW] Creator avatar, follow button, caption & marquee ticker
        └── ReelsScreen.kt                     # [NEW] Full-screen VerticalPager assembling everything
```

---

## 📦 Step 0: Models & Mock Dataset Expansion

### 1. Reels Domain Models — `data/ReelsModels.kt`

Create `data/ReelsModels.kt`:

```kotlin
package com.example.instagramclone.data

import androidx.compose.ui.graphics.Color

/**
 * Audio track information for a Reel.
 */
data class AudioTrack(
    val id: String,
    val title: String,
    val artistName: String,
    val albumArtColor: Color = Color(0xFF262626),
    val isOriginalAudio: Boolean = true
) {
    val displayTitle: String
        get() = if (isOriginalAudio) "$artistName • Original audio" else "$title • $artistName"
}

/**
 * Full-fidelity Instagram Reel post data class.
 */
data class ReelItem(
    val id: String,
    val creator: User,
    val videoBackgroundColor: Color, // Simulates 9:16 video stream
    val videoLabel: String,
    val caption: String,
    val audioTrack: AudioTrack,
    val likesCount: Int,
    val commentsCount: Int,
    val sharesCount: Int,
    val isLiked: Boolean = false,
    val isFollowed: Boolean = false,
    val isBookmarked: Boolean = false
)
```

---

### 2. Mock Reels Dataset — `data/ReelsFakeData.kt`

Create `data/ReelsFakeData.kt`:

```kotlin
package com.example.instagramclone.data

import androidx.compose.ui.graphics.Color

object ReelsFakeData {

    val reelsList = listOf(
        ReelItem(
            id = "r1",
            creator = FakeData.alice,
            videoBackgroundColor = Color(0xFF1A237E), // Deep Midnight Indigo
            videoLabel = "🌌 Timelapse of the Northern Lights in Tromsø, Norway",
            caption = "Woke up at 2 AM in -15°C weather for this view. Absolutely unreal dancing auroras! ✨❄️ #aurora #norway #travel",
            audioTrack = AudioTrack("a1", "Midnight Auroras", "Alice Wonder", Color(0xFF3949AB)),
            likesCount = 184200,
            commentsCount = 3420,
            sharesCount = 28100,
            isLiked = false,
            isFollowed = false
        ),
        ReelItem(
            id = "r2",
            creator = FakeData.john,
            videoBackgroundColor = Color(0xFFB71C1C), // Deep Crimson Red
            videoLabel = "🏎️ F1 Pit Stop Practice in 1.98 Seconds",
            caption = "Precision engineering at 200mph. Every millisecond counts in the pit lane! 🏎️💨 #f1 #motorsport #racing",
            audioTrack = AudioTrack("a2", "Track Speed", "Formula Sounds", Color(0xFFE53935), isOriginalAudio = false),
            likesCount = 429100,
            commentsCount = 8910,
            sharesCount = 54200,
            isLiked = true,
            isFollowed = true
        ),
        ReelItem(
            id = "r3",
            creator = FakeData.sarah,
            videoBackgroundColor = Color(0xFF004D40), // Deep Pine Emerald
            videoLabel = "🎨 Satisfying 3D Acrylic Fluid Art Pouring",
            caption = "Mixing custom pigments for my new gallery exhibition. Watch until the end for the cell reaction! 🧪🎨 #art #fluidart",
            audioTrack = AudioTrack("a3", "Chill Lo-Fi Vibes", "Sarah Studio", Color(0xFF00897B)),
            likesCount = 92400,
            commentsCount = 1240,
            sharesCount = 9800,
            isLiked = false,
            isFollowed = false
        ),
        ReelItem(
            id = "r4",
            creator = FakeData.mike,
            videoBackgroundColor = Color(0xFF4E342E), // Deep Espresso Brown
            videoLabel = "☕ Artisan Espresso Pour & Latte Art Rosetta",
            caption = "Morning ritual. 18g in, 36g out in 27 seconds. Smooth microfoam rosetta pour ☕✨ #coffee #latteart #barista",
            audioTrack = AudioTrack("a4", "Coffee Morning Acoustic", "Mike Fitness", Color(0xFF6D4C41)),
            likesCount = 67300,
            commentsCount = 940,
            sharesCount = 4300,
            isLiked = false,
            isFollowed = true
        )
    )
}
```

---

## 🎬 Step 1: The Reels Media Canvas & Gradient Scrims

### Dual Gradient Scrim Geometry

Because Reels displays white text and action icons directly over dynamic video backgrounds, we apply **two gradient scrims**:
1. **Top Scrim:** Dark gradient from `Color.Black.copy(alpha = 0.45f)` to `Transparent` over the top 120dp to maintain contrast for the header bar.
2. **Bottom Scrim:** Deep gradient from `Transparent` to `Color.Black.copy(alpha = 0.85f)` over the bottom 320dp to make creator handles, captions, and audio titles legible.

```text
┌─────────────────────────────────────────────────────────────┐
│  Top Scrim (Black 45% ➔ Transparent, 120dp)                 │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│                     9:16 Media Stream                       │
│                                                             │
├─────────────────────────────────────────────────────────────┤
│  Bottom Scrim (Transparent ➔ Black 85%, 320dp)              │
│  Creator Details, Caption & Action Buttons                  │
└─────────────────────────────────────────────────────────────┘
```

---

### Implementation — `ui/reels/ReelVideoCanvas.kt`

```kotlin
package com.example.instagramclone.ui.reels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.instagramclone.data.ReelItem
import kotlinx.coroutines.delay

/**
 * 9:16 Full-Screen Video Surface with:
 * - Dual gradient scrim overlays
 * - Single-tap to toggle play/pause indicator
 * - Double-tap to trigger bursting white heart animation
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReelVideoCanvas(
    reel: ReelItem,
    onDoubleTapLike: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPaused by remember { mutableStateOf(false) }
    var showPauseIndicator by remember { mutableStateOf(false) }
    var showBurstHeart by remember { mutableStateOf(false) }

    // Auto-hide pause indicator after 800ms
    LaunchedEffect(showPauseIndicator) {
        if (showPauseIndicator) {
            delay(800)
            showPauseIndicator = false
        }
    }

    // Auto-hide bursting double-tap heart after 750ms
    LaunchedEffect(showBurstHeart) {
        if (showBurstHeart) {
            delay(750)
            showBurstHeart = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(reel.videoBackgroundColor)
            .combinedClickable(
                onClick = {
                    isPaused = !isPaused
                    showPauseIndicator = true
                },
                onDoubleClick = {
                    onDoubleTapLike()
                    showBurstHeart = true
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        // Center Video Content Placeholder Label
        Text(
            text = reel.videoLabel,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 36.dp)
        )

        // ── 1. Top Gradient Scrim ────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent)
                    )
                )
        )

        // ── 2. Bottom Gradient Scrim ─────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                    )
                )
        )

        // ── 3. Transient Play/Pause Pill Indicator ───────────────────────────
        AnimatedVisibility(
            visible = showPauseIndicator,
            enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.6f),
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = if (isPaused) "Paused" else "Playing",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        // ── 4. Double-Tap Bursting Heart Animation ───────────────────────────
        AnimatedVisibility(
            visible = showBurstHeart,
            enter = scaleIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Liked",
                tint = Color.White.copy(alpha = 0.95f),
                modifier = Modifier.size(110.dp)
            )
        }
    }
}
```

---

## ⚡ Step 2: Right-Hand Vertical Action Cluster

The right side of each Reel features a compact vertical stack of actions:
1. **Like Button (`♡` / `❤️`):** Toggles like state with a red filled heart and updates formatted count (`"184.2K"`).
2. **Comment Button (`💬`):** Displays count (`"3,420"`).
3. **Share Button (`➤`):** Displays share tally (`"28.1K"`).
4. **More Options Button (`⋮`):** Triggers options sheet (Report, Not interested, Save, Remix).
5. **Rotating Vinyl Record:** A 32dp vinyl album thumbnail that continuously rotates 360° to indicate background music playback.

---

### Rotating Vinyl Audio Disk Animation

We drive the vinyl rotation using `rememberInfiniteTransition()`:

```kotlin
val infiniteTransition = rememberInfiniteTransition(label = "VinylSpin")
val rotationAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 4000, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
    ),
    label = "VinylRotation"
)
```

Applying `Modifier.rotate(rotationAngle)` executes smoothly at 60fps on the GPU without triggering layout remeasurements!

---

### Implementation — `ui/reels/ReelActionButtons.kt`

```kotlin
package com.example.instagramclone.ui.reels

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.instagramclone.data.ReelItem
import com.example.instagramclone.ui.theme.InstagramRed

/**
 * Vertical action column anchored on the bottom-right of the Reel.
 */
@Composable
fun ReelActionButtons(
    reel: ReelItem,
    isLiked: Boolean,
    likesCount: Int,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    onMoreClick: () -> Unit,
    onAudioClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 360-degree continuous rotating vinyl record transition
    val infiniteTransition = rememberInfiniteTransition(label = "VinylSpin")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "VinylRotation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.padding(end = 12.dp, bottom = 16.dp)
    ) {
        // ── 1. Like Button & Count ───────────────────────────────────────────
        ReelActionButton(
            icon = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            label = formatMetricCount(likesCount),
            tint = if (isLiked) InstagramRed else Color.White,
            onClick = onLikeClick
        )

        // ── 2. Comment Button & Count ────────────────────────────────────────
        ReelActionButton(
            icon = Icons.Default.ChatBubbleOutline,
            label = formatMetricCount(reel.commentsCount),
            tint = Color.White,
            onClick = onCommentClick
        )

        // ── 3. Share Button & Count ──────────────────────────────────────────
        ReelActionButton(
            icon = Icons.AutoMirrored.Filled.Send,
            label = formatMetricCount(reel.sharesCount),
            tint = Color.White,
            onClick = onShareClick
        )

        // ── 4. More Options Button ───────────────────────────────────────────
        IconButton(onClick = onMoreClick, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // ── 5. Rotating Vinyl Audio Disk ─────────────────────────────────────
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(6.dp))
                .border(2.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
                .background(reel.audioTrack.albumArtColor)
                .rotate(rotationAngle)
                .clickable { onAudioClick() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Audio track",
                    tint = Color.White,
                    modifier = Modifier.size(6.dp)
                )
            }
        }
    }
}

@Composable
private fun ReelActionButton(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Formats counts: 184200 -> "184.2K".
 */
private fun formatMetricCount(count: Int): String {
    return when {
        count >= 1_000_000 -> "%.1fM".format(count / 1_000_000.0)
        count >= 10_000 -> "%.1fK".format(count / 1_000.0)
        count >= 1_000 -> "%,d".format(count)
        else -> count.toString()
    }
}
```

---

## 🎵 Step 3: Bottom-Left Creator Info & Audio Marquee

The bottom-left section of the screen presents:
1. **Creator Row:** User avatar, bold username, and an interactive **"Follow"** button (outlines with white border, toggles to `"Following"`).
2. **Expandable Caption:** Shows 1 line by default; tapping `"...more"` expands the full multi-line caption.
3. **Audio Marquee Ticker:** A rounded pill housing a music note icon (`🎵`) and a text label that continuously scrolls horizontally across the screen using modern `Modifier.basicMarquee()`.

---

### Implementation — `ui/reels/ReelCreatorDetails.kt`

```kotlin
package com.example.instagramclone.ui.reels

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.instagramclone.data.ReelItem

/**
 * Bottom-left overlay presenting:
 * - Creator profile with follow button toggle
 * - Expandable caption with "...more" toggle
 * - Smooth basicMarquee horizontal audio ticker
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReelCreatorDetails(
    reel: ReelItem,
    isFollowed: Boolean,
    onFollowToggle: () -> Unit,
    onCreatorClick: () -> Unit,
    onAudioClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isCaptionExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth(0.80f) // Reserves space for the right-hand action cluster
            .padding(start = 14.dp, bottom = 16.dp)
    ) {
        // ── 1. Creator Info & Follow Button ──────────────────────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onCreatorClick() }
        ) {
            // 36dp Circular Avatar
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(reel.creator.avatarColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = reel.creator.initials,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = reel.creator.username,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Follow / Following Button
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isFollowed) Color.White.copy(alpha = 0.2f) else Color.Transparent,
                modifier = Modifier
                    .border(1.dp, Color.White, RoundedCornerShape(6.dp))
                    .clickable { onFollowToggle() }
            ) {
                Text(
                    text = if (isFollowed) "Following" else "Follow",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ── 2. Expandable Caption ────────────────────────────────────────────
        Text(
            text = reel.caption,
            color = Color.White,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            maxLines = if (isCaptionExpanded) 6 else 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .clickable { isCaptionExpanded = !isCaptionExpanded }
                .padding(end = 8.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // ── 3. Audio Marquee Ticker ──────────────────────────────────────────
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.Black.copy(alpha = 0.35f),
            modifier = Modifier.clickable { onAudioClick() }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Music",
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Smooth horizontal marquee scrolling ticker
                Text(
                    text = reel.audioTrack.displayTitle,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    modifier = Modifier
                        .width(180.dp)
                        .basicMarquee(
                            iterations = Int.MAX_VALUE,
                            delayMillis = 1200,
                            velocity = 35.dp
                        )
                )
            }
        }
    }
}
```

---

## 🎥 Step 4: Top Transparent Header Bar

The top header renders a minimal transparent overlay containing:
- `"Reels"` title in bold white font.
- A camera launcher icon (`📷`) for recording new reels.

### Implementation — `ui/reels/ReelsTopBar.kt`

```kotlin
package com.example.instagramclone.ui.reels

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Transparent Top Bar for Instagram Reels.
 */
@Composable
fun ReelsTopBar(
    onCameraClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Reels",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        IconButton(onClick = onCameraClick) {
            Icon(
                imageVector = Icons.Outlined.CameraAlt,
                contentDescription = "Create Reel",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}
```

---

## ↕️ Step 5: Full-Screen Vertical Snapping Engine

Now we bring all elements together using **`VerticalPager`**.
`VerticalPager` guarantees:
1. Every swipe snaps with authentic momentum to the adjacent video.
2. Only the current, previous, and next items are retained in memory.

### Implementation — `ui/reels/ReelsScreen.kt`

```kotlin
package com.example.instagramclone.ui.reels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.instagramclone.data.ReelItem
import com.example.instagramclone.data.ReelsFakeData

/**
 * Master Instagram Reels screen utilizing VerticalPager for physics-based snapping.
 */
@Composable
fun ReelsScreen(
    reels: List<ReelItem> = ReelsFakeData.reelsList,
    onCameraClick: () -> Unit = {},
    onCommentClick: (ReelItem) -> Unit = {},
    onShareClick: (ReelItem) -> Unit = {},
    onAudioClick: (ReelItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { reels.size })

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // ── 1. Vertical Snapping Pager ───────────────────────────────────────
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            val reel = reels[pageIndex]
            SingleReelContainer(
                reel = reel,
                onCommentClick = { onCommentClick(reel) },
                onShareClick = { onShareClick(reel) },
                onAudioClick = { onAudioClick(reel) }
            )
        }

        // ── 2. Top Transparent Reels Header ──────────────────────────────────
        ReelsTopBar(
            onCameraClick = onCameraClick,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

/**
 * Self-contained composable managing state for one active Reel.
 */
@Composable
private fun SingleReelContainer(
    reel: ReelItem,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    onAudioClick: () -> Unit
) {
    var isLiked by remember { mutableStateOf(reel.isLiked) }
    var likesCount by remember { mutableIntStateOf(reel.likesCount) }
    var isFollowed by remember { mutableStateOf(reel.isFollowed) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 9:16 Video Background Canvas + Tap Gestures
        ReelVideoCanvas(
            reel = reel,
            onDoubleTapLike = {
                if (!isLiked) {
                    isLiked = true
                    likesCount += 1
                }
            }
        )

        // Bottom-Left Creator & Audio Details
        ReelCreatorDetails(
            reel = reel,
            isFollowed = isFollowed,
            onFollowToggle = { isFollowed = !isFollowed },
            onCreatorClick = { },
            onAudioClick = onAudioClick,
            modifier = Modifier.align(Alignment.BottomStart)
        )

        // Bottom-Right Vertical Action Cluster
        ReelActionButtons(
            reel = reel,
            isLiked = isLiked,
            likesCount = likesCount,
            onLikeClick = {
                isLiked = !isLiked
                likesCount += if (isLiked) 1 else -1
            },
            onCommentClick = onCommentClick,
            onShareClick = onShareClick,
            onMoreClick = { },
            onAudioClick = onAudioClick,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}
```

---

## 🧭 Step 6: Master Navigation & Dark Theme Bottom Bar

Because Instagram Reels is an immersive, dark full-screen surface, the **Bottom Navigation Bar** must adapt:
- When on **Home (Feed)** or **Profile**: Bottom bar background is **White**, icons are **Black**.
- When on **Reels**: Bottom bar background is **Black** (or semi-transparent black), icons are **White**!

### Updated `InstagramApp.kt`

```kotlin
package com.example.instagramclone

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.instagramclone.ui.bottomnav.InstagramBottomBar
import com.example.instagramclone.ui.bottomnav.InstagramTab
import com.example.instagramclone.ui.feed.FeedScreen
import com.example.instagramclone.ui.profile.ProfileScreen
import com.example.instagramclone.ui.reels.ReelsScreen
import com.example.instagramclone.ui.theme.InstagramGray
import com.example.instagramclone.ui.theme.InstagramWhite

/**
 * Root Composable orchestrating:
 * - Part 1: Home Feed Screen
 * - Part 2: Profile Screen
 * - Part 3: Full-Screen Snapping Instagram Reels
 */
@Composable
fun InstagramApp() {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(InstagramTab.HOME) }
    val isReelsActive = selectedTab == InstagramTab.REELS

    Scaffold(
        containerColor = if (isReelsActive) Color.Black else InstagramWhite,
        bottomBar = {
            InstagramBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                isDarkMode = isReelsActive // Adapts icons to white and background to black
            )
        }
    ) { innerPadding ->
        when (selectedTab) {
            InstagramTab.HOME -> {
                FeedScreen(
                    onStoryClick = { story ->
                        Toast.makeText(context, "Story: ${story.user.username}", Toast.LENGTH_SHORT).show()
                    },
                    onPostLike = { },
                    onPostBookmark = { },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            InstagramTab.REELS -> {
                ReelsScreen(
                    onCameraClick = {
                        Toast.makeText(context, "Reels camera opened", Toast.LENGTH_SHORT).show()
                    },
                    onCommentClick = { reel ->
                        Toast.makeText(context, "Comments for: ${reel.id}", Toast.LENGTH_SHORT).show()
                    },
                    onShareClick = { reel ->
                        Toast.makeText(context, "Shared: ${reel.id}", Toast.LENGTH_SHORT).show()
                    },
                    onAudioClick = { reel ->
                        Toast.makeText(context, "Audio: ${reel.audioTrack.title}", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            InstagramTab.PROFILE -> {
                ProfileScreen(
                    onPostClick = { post ->
                        Toast.makeText(context, "Grid post ${post.id}", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            InstagramTab.SEARCH, InstagramTab.CREATE -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${selectedTab.name}\n(Coming in Part 4)",
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = InstagramGray
                    )
                }
            }
        }
    }
}
```

---

### Updating `InstagramBottomBar.kt` for Theme Switching

Update `ui/bottomnav/InstagramBottomBar.kt` to accept `isDarkMode: Boolean = false`:

```kotlin
package com.example.instagramclone.ui.bottomnav

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SlowMotionVideo
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.instagramclone.ui.theme.InstagramWhite

enum class InstagramTab {
    HOME, SEARCH, CREATE, REELS, PROFILE
}

@Composable
fun InstagramBottomBar(
    selectedTab: InstagramTab,
    onTabSelected: (InstagramTab) -> Unit,
    isDarkMode: Boolean = false
) {
    val barColor = if (isDarkMode) Color.Black else InstagramWhite
    val iconColor = if (isDarkMode) Color.White else Color.Black

    NavigationBar(
        containerColor = barColor,
        tonalElevation = 0.dp
    ) {
        // Home
        NavigationBarItem(
            selected = selectedTab == InstagramTab.HOME,
            onClick = { onTabSelected(InstagramTab.HOME) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == InstagramTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(26.dp)
                )
            },
            colors = instagramNavColors(iconColor)
        )

        // Search
        NavigationBarItem(
            selected = selectedTab == InstagramTab.SEARCH,
            onClick = { onTabSelected(InstagramTab.SEARCH) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == InstagramTab.SEARCH) Icons.Filled.Search else Icons.Outlined.Search,
                    contentDescription = "Search",
                    modifier = Modifier.size(26.dp)
                )
            },
            colors = instagramNavColors(iconColor)
        )

        // Create
        NavigationBarItem(
            selected = selectedTab == InstagramTab.CREATE,
            onClick = { onTabSelected(InstagramTab.CREATE) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == InstagramTab.CREATE) Icons.Filled.AddBox else Icons.Outlined.AddBox,
                    contentDescription = "Create",
                    modifier = Modifier.size(26.dp)
                )
            },
            colors = instagramNavColors(iconColor)
        )

        // Reels
        NavigationBarItem(
            selected = selectedTab == InstagramTab.REELS,
            onClick = { onTabSelected(InstagramTab.REELS) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == InstagramTab.REELS) Icons.Filled.SlowMotionVideo else Icons.Outlined.SlowMotionVideo,
                    contentDescription = "Reels",
                    modifier = Modifier.size(26.dp)
                )
            },
            colors = instagramNavColors(iconColor)
        )

        // Profile
        NavigationBarItem(
            selected = selectedTab == InstagramTab.PROFILE,
            onClick = { onTabSelected(InstagramTab.PROFILE) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == InstagramTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(26.dp)
                )
            },
            colors = instagramNavColors(iconColor)
        )
    }
}

@Composable
private fun instagramNavColors(iconColor: Color) = NavigationBarItemDefaults.colors(
    selectedIconColor   = iconColor,
    unselectedIconColor = iconColor,
    indicatorColor      = Color.Transparent
)
```

---

## 🔍 Deep Dive: Advanced Jetpack Compose Mechanics

### 1. `VerticalPager` vs `LazyColumn` for Full-Screen Feeds

Why can't you simply build Reels with `LazyColumn` and `Modifier.fillParentMaxHeight()`?

```kotlin
// ❌ Problematic Approach:
LazyColumn(flingBehavior = rememberSnapFlingBehavior(...)) { ... }

// ✅ Production Standard:
VerticalPager(state = rememberPagerState { reels.size }) { ... }
```

- **Built-in Gesture Inertia:** `VerticalPager` natively integrates target-page prediction. Even if the user flicks fast, the pager snaps decisively to the intended boundary without settling on a half-visible video.
- **Accurate Page Indexing:** `pagerState.currentPage` and `pagerState.settledPage` emit updates exclusively when the swipe settles, which is critical for pausing off-screen video players and releasing ExoPlayer decoders.

---

### 2. Infinite Hardware Rotation with `rememberInfiniteTransition`

```kotlin
val rotationAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 4000, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
    )
)
```

- **Linear Easing:** `LinearEasing` ensures constant angular velocity without acceleration bumps.
- **Render-Node Rotation:** Calling `Modifier.rotate(rotationAngle)` alters the transformation matrix of the underlying GPU RenderNode without invoking layout measurement passes.

---

### 3. Zero-Allocation Text Ticker with `Modifier.basicMarquee()`

Prior to Compose 1.7, creating a scrolling audio marquee required custom coroutines updating a scroll state offset in an endless loop—wasting CPU cycles and triggering continuous recompositions.

`Modifier.basicMarquee()` provides a native C++ text scrolling engine:
- `iterations = Int.MAX_VALUE`: Loops indefinitely.
- `delayMillis = 1200`: Pauses before scrolling so the user can read the first words.
- `velocity = 35.dp`: Maintains a comfortable reading speed regardless of screen pixel density.

---

### 4. Dual Touch Pipelines: Click vs Double-Click vs Drag

In Reels, three touch gestures compete on the exact same coordinate space:
1. Swiping up/down moves to another video (`VerticalPager`).
2. Single tapping pauses/plays the video.
3. Double tapping triggers the bursting heart like animation.

Compose solves this with `Modifier.combinedClickable`:
- It absorbs taps and double-taps cleanly.
- It yields immediately to vertical pointer movements, allowing `VerticalPager`'s drag recognizer to claim ownership of swipe-up gestures without latency.

---

## 🧠 Jetpack Compose Principles Applied: Where & Why

| Compose Concept | Where It Is Used | Engineering Rationale |
| :--- | :--- | :--- |
| **`VerticalPager`** | `ReelsScreen` | Physics-based vertical page snapping for full-screen video feeds. |
| **`basicMarquee()`** | `ReelCreatorDetails` audio strip | GPU-accelerated horizontal text ticker for music titles with zero allocation. |
| **`rememberInfiniteTransition`** | Vinyl disk rotation | Continuous 360° rotation animation running on the render thread. |
| **Dual Gradient Scrims** | `ReelVideoCanvas` | Guarantees contrast and legibility for white text over unpredictable video backgrounds. |
| **`combinedClickable`** | `ReelVideoCanvas` | Smoothly coordinates single-tap play/pause and double-tap like bursts. |
| **Dynamic Theme Adaptation** | `InstagramBottomBar` | Dynamically flips bottom navigation bar colors to dark mode when viewing Reels. |

---

## 🧪 Self-Assessment & Knowledge Check

Test your understanding of full-screen media pagers and immersive UI:

### 1. Why is `VerticalPager` superior to `LazyColumn` for implementing full-screen short-form video feeds?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
`VerticalPager` guarantees that every scroll gesture snaps decisively to an exact full-screen page boundary. It exposes `currentPage` and `settledPage` states, making it easy to know when a video is fully centered to start playback and when to pause off-screen media. `LazyColumn` requires manual snap flings and can easily stall midway between items.
</details>

---

### 2. How does `Modifier.basicMarquee()` improve battery life compared to manual coroutine scroll loops?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
Manual scroll loops read and mutate state at 60–120Hz, triggering constant Compose layout and recomposition passes. `basicMarquee()` operates directly at the text drawing phase on the GPU canvas, performing smooth scrolling without triggering layout recalculations or memory allocations.
</details>

---

### 3. Why are dual gradient scrims placed on top of the video canvas rather than applying a semi-transparent tinted overlay across the entire screen?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
Applying a dark tint over the entire screen washes out the video and destroys contrast. Dual gradient scrims restrict the dimming exclusively to the top (120dp) and bottom (320dp) zones where UI text and controls reside, leaving the center 60% of the video bright, vivid, and completely unobstructed.
</details>

---

### 4. What is the role of `LinearEasing` in the rotating vinyl disk animation?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
Default animation easings (like `FastOutSlowInEasing`) accelerate at the start and decelerate at the end, producing an unnatural wobble. `LinearEasing` enforces a strictly constant angular velocity, perfectly mimicking a mechanical record player spinning at 33 RPM.
</details>

---

### 5. Why must the Bottom Navigation Bar adapt its colors when navigating to the Reels tab?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
The standard feed uses a bright white background with black icons. Because Reels is an edge-to-edge dark video experience, a bright white bottom bar would cause glaring visual contrast and break immersion. Adapting to a black background with white icons preserves aesthetic cohesion.
</details>

---

## 🏁 Checkpoint: What You Should Have Working

Verify that your Part 3 Reels implementation functions smoothly:

- [x] **Vertical Snapping:** Swiping up/down snaps cleanly between full-screen Reels with momentum physics.
- [x] **Dual Gradient Scrims:** Top and bottom dark scrims ensure white text and action icons remain crisp and legible.
- [x] **Tap to Play/Pause:** Single tapping the video displays a transient translucent play icon in the center of the screen.
- [x] **Double-Tap Heart Burst:** Double-tapping the video pops a springing white heart and increments the like tally.
- [x] **Action Stack:** Right-side buttons (Like, Comment, Share, More) display formatted metrics (e.g. `"184.2K"`).
- [x] **360° Rotating Vinyl:** Audio disk in the bottom-right corner rotates continuously.
- [x] **Creator & Follow Toggle:** Avatar and username link to profile; tapping `"Follow"` updates to `"Following"`.
- [x] **Expandable Caption:** Tapping the caption toggles between a 1-line truncation and full multi-line text.
- [x] **Smooth Audio Marquee:** Background song title scrolls horizontally with `basicMarquee()`.
- [x] **Adaptive Bottom Bar:** Bottom bar shifts to black background with white icons while on the Reels tab.

---

## 🏋️ Hands-On Coding Exercises

Take your Reels engineering expertise further with these advanced challenges:

### 🎯 Exercise 1: Real ExoPlayer Video Surface Integration
Replace the `ReelVideoCanvas` placeholder with Google's Media3 `ExoPlayer` using an `AndroidView(factory = { PlayerView(...) })`. Hook into `pagerState.settledPage` to automatically play only the active video and pause off-screen players!

---

### 🎯 Exercise 2: Audio Waveform Equalizer Bars
Add 3 animated vertical bars next to the music note icon in the audio ticker that bounce up and down in sync with the audio track playback!

---

### 🎯 Exercise 3: Flying Heart Burst on Like Button Tap
When the user taps the heart icon in the right-side vertical stack, spawn 3 tiny flying floating hearts that drift upward and fade out using `Animatable` and random X-offsets!

---

## 🧭 What's Next in Part 4 (Finale)

In the grand finale, **Part 4**, we complete the Instagram Clone with:
- **Direct Messages (DMs):** Conversation list, search inbox, real-time message bubbles, and camera quick-send.
- **Instagram Story Viewer:** Full-screen story playback with segmented progress bars (5-second timers), tap left/right to skip, and swipe down to dismiss!
- **Explore Grid:** Staggered multi-size grid combining standard square thumbnails with double-height video previews.
