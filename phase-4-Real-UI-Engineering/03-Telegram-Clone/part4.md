# ✈️ Telegram UI Clone — Part 4: Channels, Message Reactions, Voice Notes & Settings (Finale)

> **🎯 What You Will Build:** The grand finale of the Telegram UI Clone series. You will build **Telegram Broadcast Channels** with pinned message banners and broadcast mute bars, an interactive **Floating Message Reactions Pill Bar** triggered via long-press, a production-grade **Voice Note Recording Engine** with slide-to-cancel and hands-free lock-to-talk gestures, and the complete **Telegram Settings & Profile Dashboard**.

---

## 📋 Table of Contents

- [Overview — The Finale](#-overview--the-finale)
  - [Visual Layout Architecture](#-visual-layout-architecture)
  - [Core Engineering Concepts Introduced](#-core-engineering-concepts-introduced)
- [Target File Structure](#-target-file-structure)
- [Step 0: Models & State Definitions](#-step-0-models--state-definitions)
  - [1. Channel, Reaction, Voice Note & Settings Models — data/AdvancedModels.kt](#-1-channel-reaction-voice-note--settings-models--dataadvancedmodelskt)
  - [2. Mock Dataset — data/AdvancedFakeData.kt](#-2-mock-dataset--dataadvancedfakedatakt)
- [Step 1: Telegram Broadcast Channels & Pinned Banner](#-step-1-telegram-broadcast-channels--pinned-banner)
  - [Channel Header with Subscriber Count](#-channel-header-with-subscriber-count)
  - [Sticky Pinned Message Banner](#-sticky-pinned-message-banner)
  - [Post Views Counter & Forward Action](#-post-views-counter--forward-action)
  - [Bottom Broadcast Control Bar (Mute / Unmute)](#-bottom-broadcast-control-bar-mute--unmute)
  - [Implementation — ui/channel/TelegramChannelView.kt](#-implementation--uichanneltelegramchannelviewkt)
- [Step 2: Interactive Floating Message Reactions Bar](#-step-2-interactive-floating-message-reactions-bar)
  - [Long-Press Detection & Anchored Pop-In Animation](#-long-press-detection--anchored-pop-in-animation)
  - [Reaction Counter Pills on Bubbles](#-reaction-counter-pills-on-bubbles)
  - [Implementation — ui/chat/TelegramReactionPicker.kt](#-implementation--uichattelegramreactionpickerkt)
- [Step 3: Voice Note Recording Engine (Slide-to-Cancel & Lock)](#-step-3-voice-note-recording-engine-slide-to-cancel--lock)
  - [Gesture Pipeline: Long Press, Drag & Release](#-gesture-pipeline-long-press-drag--release)
  - [Horizontal Slide-to-Cancel Threshold](#-horizontal-slide-to-cancel-threshold)
  - [Vertical Lock-to-Talk Hands-Free Mode](#-vertical-lock-to-talk-hands-free-mode)
  - [Animated Waveform Visualizer & Timer](#-animated-waveform-visualizer--timer)
  - [Implementation — ui/chat/TelegramVoiceRecordOverlay.kt](#-implementation--uichattelegramvoicerecordoverlaykt)
- [Step 4: Telegram Settings & Profile Dashboard](#-step-4-telegram-settings--profile-dashboard)
  - [Profile Card with Handle `@username` & QR Code](#-profile-card-with-handle-username--qr-code)
  - [Grouped Settings Categories & Storage Indicator](#-grouped-settings-categories--storage-indicator)
  - [Implementation — ui/settings/TelegramSettingsScreen.kt](#-implementation--uisettingstelegramsettingsscreenkt)
- [Step 5: Full Application Integration](#-step-5-full-application-integration)
  - [Orchestrating Everything in TelegramApp.kt](#-orchestrating-everything-in-telegramappkt)
- [🔍 Deep Dive: Advanced Jetpack Compose Mechanics](#-deep-dive-advanced-jetpack-compose-mechanics)
  - [1. Complex PointerInput Gesture Pipeline](#-1-complex-pointerinput-gesture-pipeline)
  - [2. Multi-Target Animated Popups & Scrim Trapping](#-2-multi-target-animated-popups--scrim-trapping)
  - [3. Canvas-Driven Dynamic Waveform Audio Visualization](#-3-canvas-driven-dynamic-waveform-audio-visualization)
  - [4. State Hoisting in Hybrid Navigation Architectures](#-4-state-hoisting-in-hybrid-navigation-architectures)
- [🧠 Jetpack Compose Principles Applied: Where & Why](#-jetpack-compose-principles-applied-where--why)
- [🧪 Self-Assessment & Knowledge Check](#-self-assessment--knowledge-check)
- [🏁 Checkpoint: What You Should Have Working](#-checkpoint-what-you-should-have-working)
- [🏋️ Hands-On Coding Exercises](#-hands-on-coding-exercises)
- [🎓 Course Conclusion: What's Next in Phase 4](#-course-conclusion-whats-next-in-phase-4)

---

## 📱 Overview — The Finale

With Parts 1, 2, and 3 complete, our Telegram Clone already features a searchable conversation feed, inverted chat messages, speech bubbles, an expandable multi-account navigation drawer, and an attachment bottom sheet.

In **Part 4**, we complete the clone by tackling Telegram's most complex and celebrated features:
1. **Broadcast Channels:** Public channels that display subscriber counts, pinned notification strips, message view tallies (`👁️ 2.4K`), and full-width bottom **"Mute / Unmute"** controls instead of a message input field.
2. **Floating Message Reactions:** Long-pressing any speech bubble causes a spring-animated horizontal pill of emojis (👍 ❤️ 🔥 🎉 👏 😱 🤔) to float above the message. Selecting an emoji attaches an interactive pill counter directly to the bubble.
3. **Voice Note Recording Gestures:** Holding the mic button activates audio recording mode with an animated waveform visualizer and live timer. Sliding left (‹‹) cancels the voice note; sliding up (🔒) locks the recording into hands-free mode.
4. **Settings & Profile Dashboard:** A multi-section settings screen with user bio, QR code profile sharing, notification preferences, and storage management.

---

### 🖼️ Visual Layout Architecture

#### 1. Channel Broadcast Screen with Pinned Banner
```text
┌─────────────────────────────────────────────────────────────┐
│  ←  [📣]  Android Developers Channel                   ⋮    │
│           142,580 subscribers                               │
├─────────────────────────────────────────────────────────────┤
│  📌 Pinned Message: Compose 1.7 Stable Released!        ✕   │  ← Sticky Pinned Banner
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌───────────────────────────────────────────────────────┐  │
│  │ 🚀 Android Studio Koala Feature Drop is now live!    │  │  ← Channel Broadcast Post
│  │ Check out the latest release notes and compose tooling│  │
│  │ improvements...                                       │  │
│  │                                    11:45 AM  👁️ 4.2K ↗ │  │  ← Timestamp, Views & Forward
│  └───────────────────────────────────────────────────────┘  │
│                                                             │
├─────────────────────────────────────────────────────────────┤
│                         [ MUTE ]                            │  ← Broadcast Bottom Bar
└─────────────────────────────────────────────────────────────┘
```

#### 2. Floating Message Reaction Pill & Voice Recording Overlay
```text
        ┌───────────────────────────────────┐
        │  👍   ❤️   🔥   🎉   👏   😱   🤔  │  ← Floating Emoji Reaction Pill
        └─────────────────▼─────────────────┘
┌───────────────────────────────────────────┐
│ Hey check out this awesome Jetpack UI!    │
│                                  10:14 AM │
│ ┌─────────┐ ┌─────────┐                   │
│ │ 👍  12  │ │ 🔥   5  │                   │  ← Reaction Counter Badges
│ └─────────┘ └─────────┘                   │
└───────────────────────────────────────────┘

Voice Recording Gesture Active:
┌─────────────────────────────────────────────────────────────┐
│  🔴 0:03   ılılılılılı   ‹‹ Slide to cancel        [🔒 Lock]│  ← Live Waveform & Lock
│                                                    ┌──────┐ │
│                                                    │  🎤  │ │  ← Pressed Mic Button
│                                                    └──────┘ │
└─────────────────────────────────────────────────────────────┘
```

---

### 🚀 Core Engineering Concepts Introduced

```text
1. Gesture Coordination with pointerInput:
   • Combining long-press detection with directional drag offsets (X for cancel, Y for lock).
   • Clean state machines: IDLE -> RECORDING -> LOCKED -> COMPLETED / CANCELLED.

2. Contextual Popups & Reaction Anchors:
   • Calculating overlay coordinates relative to message bubbles.
   • Scale & Fade entry animations with bouncy spring physics.

3. Channel Mode State Swapping:
   • Seamlessly replacing the editable ChatInputBar with a static Channel Bottom Bar.
   • Displaying sticky pinned message banners with smooth dismiss animations.

4. Audio Waveform Canvas Rendering:
   • Drawing dynamic oscillating bars using Canvas drawRoundRect.
   • Infinite transition driven by animateFloat for fluid voice recording feedback.
```

---

## 📁 Target File Structure

```text
app/src/main/java/com/example/telegramclone/
├── MainActivity.kt
├── TelegramApp.kt                             # Updated to orchestrate all 4 parts
├── data/
│   ├── Models.kt                              # (Part 1 & 2)
│   ├── FakeData.kt                            # (Part 1 & 2)
│   ├── DrawerModels.kt                        # (Part 3)
│   ├── DrawerFakeData.kt                      # (Part 3)
│   ├── AdvancedModels.kt                      # [NEW] Channel, Reaction, VoiceNote, Settings
│   └── AdvancedFakeData.kt                    # [NEW] Mock channel posts, reactions & settings
├── ui/
│   ├── channel/
│   │   └── TelegramChannelView.kt             # [NEW] Channel screen, pinned banner, mute bar
│   ├── chat/
│   │   ├── ChatScreen.kt                      # (Part 2 - upgraded with reactions & voice)
│   │   ├── TelegramChatTopBar.kt              # (Part 2)
│   │   ├── TelegramMessageBubble.kt           # (Part 2 - upgraded with reaction badges)
│   │   ├── TelegramChatInputBar.kt            # (Part 2)
│   │   ├── TelegramDatePill.kt                # (Part 2)
│   │   ├── TelegramAttachmentBottomSheet.kt   # (Part 3)
│   │   ├── TelegramReactionPicker.kt          # [NEW] Floating emoji pill overlay
│   │   └── TelegramVoiceRecordOverlay.kt      # [NEW] Waveform, timer, slide-cancel, lock-talk
│   ├── drawer/
│   │   ├── TelegramDrawerHeader.kt            # (Part 3)
│   │   ├── TelegramDrawerBody.kt              # (Part 3)
│   │   └── TelegramNavigationDrawer.kt        # (Part 3)
│   └── settings/
│       └── TelegramSettingsScreen.kt          # [NEW] Profile card, handle, settings categories
```

---

## 📦 Step 0: Models & State Definitions

### 1. Channel, Reaction, Voice Note & Settings Models — `data/AdvancedModels.kt`

Create `data/AdvancedModels.kt` with all data classes required for advanced messaging:

```kotlin
package com.example.telegramclone.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Represents a broadcast channel message with views count and forward capabilities.
 */
data class ChannelPost(
    val id: String,
    val text: String,
    val timestamp: String,
    val viewsCount: String, // e.g. "4.2K"
    val isPinned: Boolean = false,
    val authorName: String = "Channel Admin"
)

/**
 * Represents an emoji reaction attached to a message bubble.
 */
data class MessageReaction(
    val emoji: String,
    val count: Int,
    val isSelectedByMe: Boolean = false
)

/**
 * State machine representing voice note recording phases.
 */
enum class VoiceRecordState {
    IDLE,       // Microphone resting in input bar
    RECORDING,  // User holding finger on mic, dragging allowed
    LOCKED      // User dragged up to lock icon; recording hands-free
}

/**
 * Represents a categorized settings item in the Telegram Settings dashboard.
 */
data class SettingsItem(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val icon: ImageVector,
    val iconBackgroundColor: Color,
    val isBadgeNew: Boolean = false,
    val isDividerAfter: Boolean = false
)
```

---

### 2. Mock Dataset — `data/AdvancedFakeData.kt`

Create `data/AdvancedFakeData.kt`:

```kotlin
package com.example.telegramclone.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Devices
import androidx.compose.ui.graphics.Color

object AdvancedFakeData {

    val channelPosts = listOf(
        ChannelPost(
            id = "p1",
            text = "🚀 Jetpack Compose 1.7.0 is officially STABLE!\n\nHighlights:\n• Modifier.animateItem() for LazyLists\n• Performance optimizations for text layout\n• Baseline Profiles updates for 30% faster startup",
            timestamp = "10:15 AM",
            viewsCount = "12.8K",
            isPinned = true
        ),
        ChannelPost(
            id = "p2",
            text = "💡 Tip of the day: Always wrap high-frequency scroll state checks in derivedStateOf {} to avoid recomposition thrashing on 120Hz displays!",
            timestamp = "12:30 PM",
            viewsCount = "8.4K"
        ),
        ChannelPost(
            id = "p3",
            text = "Kotlin 2.0 with the new K2 compiler is now enabled by default in all new Android Gradle plugins. Compile times are up to 2x faster!",
            timestamp = "3:45 PM",
            viewsCount = "15.1K"
        )
    )

    val availableReactions = listOf("👍", "❤️", "🔥", "🎉", "👏", "😱", "🤔")

    val settingsItems = listOf(
        SettingsItem(
            id = "chat_settings",
            title = "Chat Settings",
            subtitle = "Themes, stickers, message text size",
            icon = Icons.Default.Chat,
            iconBackgroundColor = Color(0xFF00C853)
        ),
        SettingsItem(
            id = "privacy",
            title = "Privacy and Security",
            subtitle = "Passcode, Two-Step Verification",
            icon = Icons.Default.Lock,
            iconBackgroundColor = Color(0xFF536DFE)
        ),
        SettingsItem(
            id = "notifications",
            title = "Notifications and Sounds",
            subtitle = "Messages, groups, channels",
            icon = Icons.Default.Notifications,
            iconBackgroundColor = Color(0xFFFF5252)
        ),
        SettingsItem(
            id = "data_storage",
            title = "Data and Storage",
            subtitle = "Network usage, auto-download",
            icon = Icons.Default.DataUsage,
            iconBackgroundColor = Color(0xFF00B0FF),
            isDividerAfter = true
        ),
        SettingsItem(
            id = "devices",
            title = "Devices",
            subtitle = "5 active sessions",
            icon = Icons.Default.Devices,
            iconBackgroundColor = Color(0xFFFF9100)
        ),
        SettingsItem(
            id = "chat_folders",
            title = "Chat Folders",
            subtitle = "Personal, Work, News",
            icon = Icons.Default.Folder,
            iconBackgroundColor = Color(0xFF7C4DFF)
        ),
        SettingsItem(
            id = "language",
            title = "Language",
            subtitle = "English",
            icon = Icons.Default.Language,
            iconBackgroundColor = Color(0xFF26A69A),
            isDividerAfter = true
        ),
        SettingsItem(
            id = "premium",
            title = "Telegram Premium",
            subtitle = "Double limits, 4GB uploads, exclusive icons",
            icon = Icons.Default.Star,
            iconBackgroundColor = Color(0xFF9C27B0),
            isBadgeNew = true
        )
    )
}
```

---

## 📢 Step 1: Telegram Broadcast Channels & Pinned Banner

Telegram channels are broadcast-only feeds where only admins post. 
Key UI characteristics:
1. **Top Bar:** Shows the channel's subscriber tally (`"142,580 subscribers"`) instead of `"online"`.
2. **Pinned Banner:** A sticky top strip showing the latest pinned broadcast with a pin icon (`📌`) and a dismiss (`✕`) button. Tapping it smoothly scrolls the list to the pinned post.
3. **Channel Post Cards:** Full-width bubbles displaying views count (`👁️ 4.2K`) and a quick-forward arrow (`↗`).
4. **Broadcast Mute Bar:** The traditional text input field is replaced with a single high-contrast **"MUTE" / "UNMUTE"** button at the bottom of the viewport.

### Implementation — `ui/channel/TelegramChannelView.kt`

```kotlin
package com.example.telegramclone.ui.channel

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.telegramclone.data.ChannelPost
import kotlinx.coroutines.launch

/**
 * Dedicated Telegram Broadcast Channel screen featuring:
 * - Top subscriber counter bar
 * - Collapsible pinned message banner
 * - Broadcast posts with view tallies
 * - Bottom Mute/Unmute action bar
 */
@Composable
fun TelegramChannelView(
    channelName: String,
    subscriberCount: String,
    posts: List<ChannelPost>,
    onBackClick: () -> Unit,
    onForwardPost: (ChannelPost) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var isMuted by remember { mutableStateOf(false) }
    var showPinnedBanner by remember { mutableStateOf(true) }
    val pinnedPost = posts.find { it.isPinned } ?: posts.firstOrNull()

    Scaffold(
        topBar = {
            ChannelTopAppBar(
                channelName = channelName,
                subscriberCount = subscriberCount,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            ChannelBottomBar(
                isMuted = isMuted,
                onToggleMute = { isMuted = !isMuted }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE3E9EE)) // Telegram classic channel background
                .padding(innerPadding)
        ) {
            // ── Sticky Pinned Message Banner ─────────────────────────────────
            AnimatedVisibility(
                visible = showPinnedBanner && pinnedPost != null,
                exit = shrinkVertically() + fadeOut()
            ) {
                pinnedPost?.let { post ->
                    PinnedMessageBanner(
                        post = post,
                        onClick = {
                            val index = posts.indexOf(post)
                            if (index != -1) {
                                coroutineScope.launch { listState.animateScrollToItem(index) }
                            }
                        },
                        onDismiss = { showPinnedBanner = false }
                    )
                }
            }

            // ── Channel Posts Feed ───────────────────────────────────────────
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(posts, key = { it.id }) { post ->
                    ChannelPostCard(
                        post = post,
                        onForwardClick = { onForwardPost(post) }
                    )
                }
            }
        }
    }
}

/**
 * Top App Bar specialized for Broadcast Channels.
 */
@Composable
private fun ChannelTopAppBar(
    channelName: String,
    subscriberCount: String,
    onBackClick: () -> Unit
) {
    Surface(
        color = Color(0xFF517DA2),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0088CC)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = channelName.take(2).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = channelName,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subscriberCount,
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 12.sp
                )
            }

            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Channel Options",
                    tint = Color.White
                )
            }
        }
    }
}

/**
 * Sticky Pinned Message Strip directly below the top bar.
 */
@Composable
private fun PinnedMessageBanner(
    post: ChannelPost,
    onClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Blue vertical bar accent
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(32.dp)
                    .background(Color(0xFF0088CC), RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.width(10.dp))

            Icon(
                imageVector = Icons.Default.PushPin,
                contentDescription = "Pinned",
                tint = Color(0xFF0088CC),
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Pinned Message",
                    color = Color(0xFF0088CC),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = post.text.replace("\n", " "),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss Pinned Banner",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Broadcast Post Card with author label, views tally, timestamp, and forward arrow.
 */
@Composable
private fun ChannelPostCard(
    post: ChannelPost,
    onForwardClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Channel post author
            Text(
                text = post.authorName,
                color = Color(0xFF0088CC),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Body content
            Text(
                text = post.text,
                fontSize = 14.sp,
                color = Color(0xFF1E293B),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Footer: Timestamp, Views tally, Forward action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Views icon & count
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = "Views",
                    tint = Color.Gray,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = post.viewsCount,
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Timestamp
                Text(
                    text = post.timestamp,
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Quick Forward Button
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F5F9))
                        .clickable { onForwardClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Forward Post",
                        tint = Color(0xFF0088CC),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

/**
 * Bottom Broadcast Control Bar with full-width MUTE / UNMUTE toggle.
 */
@Composable
private fun ChannelBottomBar(
    isMuted: Boolean,
    onToggleMute: () -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Button(
                onClick = onToggleMute,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isMuted) Color(0xFFE2E8F0) else Color(0xFF0088CC),
                    contentColor = if (isMuted) Color(0xFF475569) else Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.NotificationsOff else Icons.Default.Notifications,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isMuted) "UNMUTE" else "MUTE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
```

---

## 😃 Step 2: Interactive Floating Message Reactions Bar

In Telegram, when a user long-presses a message bubble:
1. A floating horizontal pill appears directly above or aligned to the message with a popping spring scale animation (`scaleIn(spring) + fadeIn()`).
2. Tapping any emoji (👍 ❤️ 🔥 🎉 👏 😱 🤔) immediately dismisses the picker and adds or increments the reaction counter on the bubble.
3. If the user taps a reaction they already selected, their count is removed/decremented.

### Implementation — `ui/chat/TelegramReactionPicker.kt`

```kotlin
package com.example.telegramclone.ui.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.telegramclone.data.MessageReaction

/**
 * Floating horizontal emoji reaction bar that pops up above a long-pressed message.
 */
@Composable
fun TelegramReactionPicker(
    visible: Boolean,
    reactions: List<String>,
    onReactionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(),
        exit = scaleOut() + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier
                .shadow(12.dp, shape = RoundedCornerShape(24.dp), ambientColor = Color.Black.copy(alpha = 0.2f))
                .padding(4.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                reactions.forEach { emoji ->
                    ReactionEmojiItem(
                        emoji = emoji,
                        onClick = { onReactionSelected(emoji) }
                    )
                }
            }
        }
    }
}

/**
 * Individual emoji inside the floating reaction picker.
 */
@Composable
private fun ReactionEmojiItem(
    emoji: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = 22.sp
        )
    }
}

/**
 * Small pill rendered at the base of a message bubble showing reaction tally.
 */
@Composable
fun TelegramReactionBadge(
    reaction: MessageReaction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = CircleShape,
        color = if (reaction.isSelectedByMe) Color(0xFF0088CC).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.85f),
        shadowElevation = 1.dp,
        modifier = modifier
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = reaction.emoji, fontSize = 12.sp)
            Text(
                text = " ${reaction.count}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (reaction.isSelectedByMe) Color(0xFF0088CC) else Color(0xFF334155)
            )
        }
    }
}
```

---

## 🎙️ Step 3: Voice Note Recording Engine (Slide-to-Cancel & Lock)

The voice note recording interaction in Telegram is an industry benchmark for gesture engineering:
1. **Touch & Hold (Long Press):** Touching and holding the microphone button enters `VoiceRecordState.RECORDING`. A live timer starts counting (`0:01`, `0:02`, ...) alongside an oscillating red dot and animated audio wave bars.
2. **Slide to Cancel (Horizontal Drag):** If the user drags left beyond a threshold (`offsetX < -80.dp`), the recording is abandoned (`VoiceRecordState.IDLE`) without sending.
3. **Lock to Talk (Vertical Drag):** If the user drags upward beyond a threshold (`offsetY < -100.dp`), the recording shifts to `VoiceRecordState.LOCKED`. The microphone icon stays locked on screen hands-free, allowing the user to release their finger and continue speaking. Stop, Send, and Trash buttons appear!

### Implementation — `ui/chat/TelegramVoiceRecordOverlay.kt`

```kotlin
package com.example.telegramclone.ui.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.telegramclone.data.VoiceRecordState
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/**
 * Advanced Telegram Voice Recording Overlay supporting:
 * - Hold to record
 * - Slide to cancel (‹‹ Slide to cancel)
 * - Slide up to lock
 * - Hands-free recording mode with waveform visualization
 */
@Composable
fun TelegramVoiceRecordOverlay(
    recordState: VoiceRecordState,
    onStateChange: (VoiceRecordState) -> Unit,
    onSendVoiceNote: (durationSeconds: Int) -> Unit,
    onCancelRecording: () -> Unit,
    modifier: Modifier = Modifier
) {
    var recordingSeconds by remember { mutableIntStateOf(0) }
    var dragOffsetX by remember { mutableFloatStateOf(0f) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }

    // Live second-by-second audio recording timer
    LaunchedEffect(recordState) {
        if (recordState == VoiceRecordState.RECORDING || recordState == VoiceRecordState.LOCKED) {
            recordingSeconds = 0
            while (true) {
                delay(1000)
                recordingSeconds++
            }
        } else {
            recordingSeconds = 0
            dragOffsetX = 0f
            dragOffsetY = 0f
        }
    }

    val formattedTime = remember(recordingSeconds) {
        val mins = recordingSeconds / 60
        val secs = recordingSeconds % 60
        "%d:%02d".format(mins, secs)
    }

    Box(modifier = modifier.fillMaxWidth()) {
        when (recordState) {
            VoiceRecordState.RECORDING -> {
                // Active Touch-and-Hold Bar
                RecordingActiveBar(
                    formattedTime = formattedTime,
                    dragOffsetX = dragOffsetX,
                    dragOffsetY = dragOffsetY
                )
            }
            VoiceRecordState.LOCKED -> {
                // Hands-Free Locked Bar with Stop & Send controls
                RecordingLockedBar(
                    formattedTime = formattedTime,
                    onCancel = {
                        onStateChange(VoiceRecordState.IDLE)
                        onCancelRecording()
                    },
                    onSend = {
                        onSendVoiceNote(recordingSeconds)
                        onStateChange(VoiceRecordState.IDLE)
                    }
                )
            }
            VoiceRecordState.IDLE -> {
                // Interactive Mic Trigger Button (Attach gesture recognizer)
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 6.dp)
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0088CC))
                        .pointerInput(Unit) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = {
                                    onStateChange(VoiceRecordState.RECORDING)
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    dragOffsetX += dragAmount.x
                                    dragOffsetY += dragAmount.y

                                    // Cancel threshold (dragged left > 90dp)
                                    if (dragOffsetX < -240f) {
                                        onStateChange(VoiceRecordState.IDLE)
                                        onCancelRecording()
                                    }
                                    // Lock threshold (dragged up > 100dp)
                                    if (dragOffsetY < -260f) {
                                        onStateChange(VoiceRecordState.LOCKED)
                                    }
                                },
                                onDragEnd = {
                                    if (recordState == VoiceRecordState.RECORDING) {
                                        onSendVoiceNote(recordingSeconds)
                                        onStateChange(VoiceRecordState.IDLE)
                                    }
                                },
                                onDragCancel = {
                                    onStateChange(VoiceRecordState.IDLE)
                                    onCancelRecording()
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Hold to Record Voice Note",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

/**
 * Rendered when user is holding finger on mic. Shows slide-to-cancel & lock hint.
 */
@Composable
private fun RecordingActiveBar(
    formattedTime: String,
    dragOffsetX: Float,
    dragOffsetY: Float
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pulsing Red Recording Indicator Dot
            PulsingRecordDot()

            Spacer(modifier = Modifier.width(8.dp))

            // Timer
            Text(
                text = formattedTime,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFFE53935)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Animated Audio Waveform
            AudioWaveformCanvas(
                modifier = Modifier
                    .weight(1f)
                    .height(24.dp)
            )

            // "Slide to cancel" text offset by finger drag
            Text(
                text = "‹‹ Slide to cancel",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.offset { IntOffset(dragOffsetX.roundToInt().coerceAtMost(0), 0) }
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Blue Active Mic Pill
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0088CC)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Recording active",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

/**
 * Hands-Free Locked Mode: displays trash, timer, and blue send button.
 */
@Composable
private fun RecordingLockedBar(
    formattedTime: String,
    onCancel: () -> Unit,
    onSend: () -> Unit
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Trash / Cancel Button
            IconButton(onClick = onCancel) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Cancel Recording",
                    tint = Color(0xFFE53935)
                )
            }

            PulsingRecordDot()

            Spacer(modifier = Modifier.width(8.dp))

            // Timer
            Text(
                text = formattedTime,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFFE53935)
            )

            Spacer(modifier = Modifier.width(12.dp))

            AudioWaveformCanvas(
                modifier = Modifier
                    .weight(1f)
                    .height(24.dp)
            )

            // Send Voice Note FAB
            IconButton(
                onClick = onSend,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0088CC))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send Voice Note",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Pulsing Red Circle indicating active microphone capture.
 */
@Composable
private fun PulsingRecordDot() {
    val infiniteTransition = rememberInfiniteTransition(label = "DotPulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "DotAlpha"
    )

    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(Color(0xFFE53935).copy(alpha = alpha))
    )
}

/**
 * Canvas that draws animated audio waveform amplitude bars.
 */
@Composable
private fun AudioWaveformCanvas(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "WaveformAnimation")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase"
    )

    Canvas(modifier = modifier) {
        val barCount = 14
        val barWidth = 3.dp.toPx()
        val spacing = (size.width - (barCount * barWidth)) / (barCount - 1)

        for (i in 0 until barCount) {
            val dynamicHeight = (kotlin.math.sin(phase + i * 0.5f) * 0.4f + 0.6f) * size.height
            val xOffset = i * (barWidth + spacing)
            val yOffset = (size.height - dynamicHeight) / 2f

            drawRoundRect(
                color = Color(0xFF0088CC),
                topLeft = Offset(xOffset, yOffset),
                size = Size(barWidth, dynamicHeight),
                cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
            )
        }
    }
}
```

---

## ⚙️ Step 4: Telegram Settings & Profile Dashboard

In Telegram, tapping **Settings** in the navigation drawer transitions to a settings screen:
1. **User Profile Card:** Large user avatar, user name, phone number, handle (`@ankit_dev`), and a QR code action button.
2. **Settings Categories:** Grouped list rows for Chat Settings, Privacy, Notifications, Data & Storage, Devices, Folders, Language, and **Telegram Premium** with an exclusive purple star badge.

### Implementation — `ui/settings/TelegramSettingsScreen.kt`

```kotlin
package com.example.telegramclone.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.telegramclone.data.AdvancedFakeData
import com.example.telegramclone.data.SettingsItem
import com.example.telegramclone.data.TelegramAccount

/**
 * Full Telegram Settings & Profile Screen.
 */
@Composable
fun TelegramSettingsScreen(
    account: TelegramAccount,
    onBackClick: () -> Unit,
    onItemClick: (SettingsItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            SettingsTopBar(onBackClick = onBackClick)
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF1F5F9))
                .verticalScroll(scrollState)
                .padding(innerPadding)
        ) {
            // ── 1. Profile Card Header ───────────────────────────────────────
            ProfileHeaderCard(account = account)

            Spacer(modifier = Modifier.height(12.dp))

            // ── 2. Settings Category List ────────────────────────────────────
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                Column {
                    AdvancedFakeData.settingsItems.forEach { item ->
                        SettingsRow(
                            item = item,
                            onClick = { onItemClick(item) }
                        )

                        if (item.isDividerAfter) {
                            HorizontalDivider(
                                color = Color(0xFFF1F5F9),
                                thickness = 1.dp,
                                modifier = Modifier.padding(start = 58.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SettingsTopBar(onBackClick: () -> Unit) {
    Surface(
        color = Color(0xFF517DA2),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "Settings",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = "Share Profile QR Code",
                    tint = Color.White
                )
            }

            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    tint = Color.White
                )
            }

            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More Options",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun ProfileHeaderCard(account: TelegramAccount) {
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User Avatar with Camera edit badge
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(account.avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = account.initials,
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0088CC)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Change Profile Picture",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = account.fullName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = account.phoneNumber,
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "@ankit_dev",
                    fontSize = 13.sp,
                    color = Color(0xFF0088CC),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun SettingsRow(
    item: SettingsItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Colored Icon Container
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(item.iconBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF0F172A)
            )
            item.subtitle?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        // New Badge for Telegram Premium
        if (item.isBadgeNew) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF9C27B0)
            ) {
                Text(
                    text = "NEW",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
```

---

## 🔗 Step 5: Full Application Integration

Let's update `TelegramApp.kt` to pull together all features from Parts 1, 2, 3, and 4 into a unified, interactive application:
- **Direct Chats** support the **Floating Reaction Picker** (via long press) and the **Voice Recording Engine** (hold/slide mic).
- **Broadcast Channels** open into the **Channel View** with pinned banner and Mute bar.
- **Settings Screen** opens directly from the Navigation Drawer's Settings item.

### Final Integrated `TelegramApp.kt`

```kotlin
package com.example.telegramclone

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.telegramclone.data.AdvancedFakeData
import com.example.telegramclone.data.Chat
import com.example.telegramclone.data.DrawerFakeData
import com.example.telegramclone.data.FakeData
import com.example.telegramclone.data.Message
import com.example.telegramclone.data.MessageReaction
import com.example.telegramclone.data.VoiceRecordState
import com.example.telegramclone.ui.channel.TelegramChannelView
import com.example.telegramclone.ui.chat.ChatScreen
import com.example.telegramclone.ui.chat.TelegramAttachmentBottomSheet
import com.example.telegramclone.ui.chat.TelegramReactionPicker
import com.example.telegramclone.ui.chat.TelegramVoiceRecordOverlay
import com.example.telegramclone.ui.components.TelegramChatItem
import com.example.telegramclone.ui.components.TelegramFab
import com.example.telegramclone.ui.components.TelegramTopAppBar
import com.example.telegramclone.ui.drawer.TelegramNavigationDrawer
import com.example.telegramclone.ui.settings.TelegramSettingsScreen
import com.example.telegramclone.ui.theme.TelegramCloneTheme
import kotlinx.coroutines.launch

/**
 * Master Application Composable orchestrating:
 * - Part 1: Searchable Chat List & Lazy Scroll Detection
 * - Part 2: Inverted Chat Conversations & Bubble Layouts
 * - Part 3: Navigation Drawer, Account Switcher & Attachment Bottom Sheet
 * - Part 4: Channels, Message Reactions, Voice Note Engine & Settings Dashboard
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelegramApp() {
    var isDarkTheme by remember { mutableStateOf(false) }

    TelegramCloneTheme(darkTheme = isDarkTheme) {
        val coroutineScope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        // ── Navigation State ─────────────────────────────────────────────────
        var activeChat by remember { mutableStateOf<Chat?>(null) }
        var isChannelMode by remember { mutableStateOf(false) }
        var isSettingsMode by remember { mutableStateOf(false) }

        // ── Drawer State ─────────────────────────────────────────────────────
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        var selectedAccountId by remember { mutableStateOf("acc_1") }
        val activeAccount = remember(selectedAccountId) {
            DrawerFakeData.accounts.find { it.id == selectedAccountId } ?: DrawerFakeData.accounts.first()
        }

        // ── Attachment Sheet State ───────────────────────────────────────────
        var showAttachmentSheet by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        // ── Reaction Picker State ────────────────────────────────────────────
        var showReactionPicker by remember { mutableStateOf(false) }
        var targetMessageId by remember { mutableStateOf<String?>(null) }

        // ── Voice Recording State ────────────────────────────────────────────
        var voiceRecordState by remember { mutableStateOf(VoiceRecordState.IDLE) }

        // ── Search State ─────────────────────────────────────────────────────
        var searchQuery by remember { mutableStateOf("") }
        var isSearchActive by remember { mutableStateOf(false) }

        TelegramNavigationDrawer(
            drawerState = drawerState,
            accounts = DrawerFakeData.accounts,
            selectedAccountId = selectedAccountId,
            menuItems = DrawerFakeData.drawerMenuItems,
            isDarkTheme = isDarkTheme,
            onSelectAccount = { newAccId ->
                selectedAccountId = newAccId
                coroutineScope.launch {
                    val acc = DrawerFakeData.accounts.find { it.id == newAccId }
                    snackbarHostState.showSnackbar("Switched to ${acc?.fullName}")
                }
            },
            onToggleDarkTheme = { isDarkTheme = !isDarkTheme },
            onMenuItemClick = { item ->
                when (item.id) {
                    "settings" -> isSettingsMode = true
                    else -> {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Clicked ${item.title}")
                        }
                    }
                }
            },
            onAddAccountClick = {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Add account flow triggered")
                }
            }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    // ── 1. Settings Screen ───────────────────────────────────
                    isSettingsMode -> {
                        TelegramSettingsScreen(
                            account = activeAccount,
                            onBackClick = { isSettingsMode = false },
                            onItemClick = { settingItem ->
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Opened: ${settingItem.title}")
                                }
                            }
                        )
                    }

                    // ── 2. Broadcast Channel Screen ──────────────────────────
                    isChannelMode -> {
                        TelegramChannelView(
                            channelName = "Android Developers",
                            subscriberCount = "142,580 subscribers",
                            posts = AdvancedFakeData.channelPosts,
                            onBackClick = { isChannelMode = false },
                            onForwardPost = { post ->
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Forwarded post: ${post.id}")
                                }
                            }
                        )
                    }

                    // ── 3. Direct Conversation Screen ────────────────────────
                    activeChat != null -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            ChatScreen(
                                chat = activeChat!!,
                                onBackClick = { activeChat = null },
                                onAttachClick = { showAttachmentSheet = true }
                            )

                            // Floating Reaction Picker Overlay
                            if (showReactionPicker) {
                                TelegramReactionPicker(
                                    visible = showReactionPicker,
                                    reactions = AdvancedFakeData.availableReactions,
                                    onReactionSelected = { emoji ->
                                        showReactionPicker = false
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Reacted with $emoji")
                                        }
                                    },
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(bottom = 80.dp)
                                )
                            }

                            // Voice Recording Floating Engine
                            TelegramVoiceRecordOverlay(
                                recordState = voiceRecordState,
                                onStateChange = { voiceRecordState = it },
                                onSendVoiceNote = { seconds ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Sent voice note (${seconds}s)")
                                    }
                                },
                                onCancelRecording = {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Voice recording discarded")
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(bottom = 6.dp)
                            )
                        }
                    }

                    // ── 4. Main Chat List (Root Screen) ──────────────────────
                    else -> {
                        val listState = rememberLazyListState()
                        val filteredChats = remember(searchQuery) {
                            if (searchQuery.isBlank()) FakeData.chats
                            else FakeData.chats.filter {
                                it.name.contains(searchQuery, ignoreCase = true) ||
                                it.lastMessage.contains(searchQuery, ignoreCase = true)
                            }
                        }

                        Scaffold(
                            snackbarHost = { SnackbarHost(snackbarHostState) },
                            topBar = {
                                TelegramTopAppBar(
                                    searchQuery = searchQuery,
                                    onSearchQueryChange = { searchQuery = it },
                                    isSearchActive = isSearchActive,
                                    onSearchActiveChange = { isSearchActive = it },
                                    onMenuClick = {
                                        coroutineScope.launch { drawerState.open() }
                                    }
                                )
                            },
                            floatingActionButton = {
                                TelegramFab(listState = listState, onClick = {
                                    // Quick demo shortcut to test Channel Mode
                                    isChannelMode = true
                                })
                            }
                        ) { innerPadding ->
                            LazyColumn(
                                state = listState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                items(filteredChats, key = { it.id }) { chat ->
                                    TelegramChatItem(
                                        chat = chat,
                                        onClick = {
                                            if (chat.isChannel) {
                                                isChannelMode = true
                                            } else {
                                                activeChat = chat
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Attachment Modal Bottom Sheet ────────────────────────────
                if (showAttachmentSheet) {
                    TelegramAttachmentBottomSheet(
                        sheetState = sheetState,
                        recentMedia = DrawerFakeData.recentMediaList,
                        actions = DrawerFakeData.attachmentActions,
                        onDismissRequest = { showAttachmentSheet = false },
                        onActionClick = { action ->
                            coroutineScope.launch {
                                sheetState.hide()
                                showAttachmentSheet = false
                                snackbarHostState.showSnackbar("Attached: ${action.title}")
                            }
                        },
                        onMediaClick = { media ->
                            coroutineScope.launch {
                                sheetState.hide()
                                showAttachmentSheet = false
                                snackbarHostState.showSnackbar("Selected media ${media.id}")
                            }
                        }
                    )
                }
            }
        }
    }
}
```

---

## 🔍 Deep Dive: Advanced Jetpack Compose Mechanics

### 1. Complex PointerInput Gesture Pipeline

In `TelegramVoiceRecordOverlay`, recognizing long press while simultaneously tracking drag distance without breaking touch dispatching requires `detectDragGesturesAfterLongPress`:

```kotlin
Modifier.pointerInput(Unit) {
    detectDragGesturesAfterLongPress(
        onDragStart = { onStateChange(VoiceRecordState.RECORDING) },
        onDrag = { change, dragAmount ->
            change.consume()
            dragOffsetX += dragAmount.x
            dragOffsetY += dragAmount.y
            if (dragOffsetX < -240f) { /* Trigger Cancel */ }
            if (dragOffsetY < -260f) { /* Trigger Lock */ }
        },
        onDragEnd = { /* Normal Send on finger lift */ }
    )
}
```

#### Why standard Click / Drag listeners fail here:
- Standard `draggable()` only tracks a single axis (`Orientation.Horizontal` or `Vertical`), making diagonal gestures impossible.
- `detectTapGestures` cannot maintain continuous touch updates once a drag movement starts.
- `detectDragGesturesAfterLongPress` provides the exact lifecycle needed: it verifies user intent (hold for ~400ms) before consuming touch events, preventing accidental recordings during regular list scrolling.

---

### 2. Multi-Target Animated Popups & Scrim Trapping

When the `TelegramReactionPicker` is displayed:
- We employ `scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy))` to create Telegram's signature bouncy pop-in effect.
- The overlay is placed inside a parent `Box` overlay above the `ChatScreen`, allowing it to break free of speech bubble layout constraints while maintaining crisp elevation shadows (`ambientColor` & `spotColor`).

---

### 3. Canvas-Driven Dynamic Waveform Audio Visualization

Drawing dynamic oscillating audio bars in real-time without bitmap allocations:

```kotlin
Canvas(modifier = modifier) {
    val barCount = 14
    val barWidth = 3.dp.toPx()
    val spacing = (size.width - (barCount * barWidth)) / (barCount - 1)

    for (i in 0 until barCount) {
        val dynamicHeight = (kotlin.math.sin(phase + i * 0.5f) * 0.4f + 0.6f) * size.height
        val xOffset = i * (barWidth + spacing)
        val yOffset = (size.height - dynamicHeight) / 2f

        drawRoundRect(
            color = Color(0xFF0088CC),
            topLeft = Offset(xOffset, yOffset),
            size = Size(barWidth, dynamicHeight),
            cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
        )
    }
}
```

Using pure mathematical sine wave harmonics (`phase + i * 0.5f`), this composable renders continuous 60fps audio waveform dynamics with zero memory allocations or garbage collection pauses.

---

### 4. State Hoisting in Hybrid Navigation Architectures

Notice how navigation state (`activeChat`, `isChannelMode`, `isSettingsMode`) is hoisted directly into `TelegramApp.kt`:
- Tapping a Channel item opens `TelegramChannelView`.
- Tapping a Direct Message opens `ChatScreen`.
- Tapping Settings in the Drawer opens `TelegramSettingsScreen`.
- Tapping Back on any sub-screen cleanly resets the state variable back to `false` or `null`.

This state-driven approach avoids navigation boilerplate while retaining 100% predictable back-stack behavior on Android.

---

## 🧠 Jetpack Compose Principles Applied: Where & Why

| Compose Concept | Where It Is Used | Engineering Rationale |
| :--- | :--- | :--- |
| **`detectDragGesturesAfterLongPress`** | `TelegramVoiceRecordOverlay` | Differentiates long-press voice recordings from drag gestures (slide-to-cancel & lock). |
| **`Canvas` & `drawRoundRect`** | Audio Waveform Visualizer | Hardware-accelerated 60fps waveform rendering without image assets or garbage collection. |
| **`spring()` Physics** | `TelegramReactionPicker` | Delivers Telegram's signature bouncy pop-in animation on emoji picker appearance. |
| **`AnimatedVisibility` (exit shrink)** | `PinnedMessageBanner` | Smoothly collapses the pinned notification bar when dismissed by the user. |
| **`verticalScroll` with Card grouping** | `TelegramSettingsScreen` | Structures multi-section settings rows with hairline dividers inside a single scroll viewport. |
| **State Hoisting** | `TelegramApp` Master Root | Decouples navigation transitions from presentation components for testability. |

---

## 🧪 Self-Assessment & Knowledge Check

Test your mastery of advanced Compose gesture engineering:

### 1. Why is `detectDragGesturesAfterLongPress` preferred over combining `combinedClickable` with `Modifier.draggable` for voice note recording?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
`Modifier.draggable` can only track drag offsets along a single axis (`Orientation.Horizontal` or `Orientation.Vertical`). In Telegram voice notes, the gesture is two-dimensional: dragging left cancels the recording, while dragging up locks it into hands-free mode. `detectDragGesturesAfterLongPress` provides continuous $(X, Y)$ offset deltas after verifying the user's initial touch-and-hold intent.
</details>

---

### 2. How does using mathematical sine waves (`sin(phase + offset)`) in `Canvas` eliminate memory leaks during live audio waveform animations?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
Pre-rendered GIF or image sequence animations consume substantial memory and cause constant frame-buffer uploads. Rendering via `Canvas` calculates geometric coordinates purely in-register on every draw pass. Driven by an `infiniteRepeatable` float animation, it produces zero object allocations and executes directly on the GPU.
</details>

---

### 3. In `TelegramChannelView`, how does the Sticky Pinned Banner allow users to jump to older messages in a long list?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
The banner's click listener captures the `ChannelPost` ID, locates its index in the `posts` list, and dispatches `coroutineScope.launch { listState.animateScrollToItem(index) }`. This causes the `LazyColumn` to smoothly glide directly to the target post.
</details>

---

### 4. What is the purpose of `change.consume()` inside `detectDragGesturesAfterLongPress`?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
`change.consume()` marks the pointer input event as consumed by this gesture handler, preventing parent layout containers (such as scrollable columns or system back gesture detectors) from intercepting and hijacking the drag movement while the user is sliding to cancel or lock their voice note.
</details>

---

### 5. Why is `scaleIn` with `Spring.DampingRatioMediumBouncy` used for the floating reaction picker instead of a standard `tween` fade?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
Spring physics model real-world momentum and elasticity. When an emoji bar pops open, the slight overshoot and rebound make the interface feel responsive, tactile, and alive, matching Telegram's signature design aesthetic.
</details>

---

## 🏁 Checkpoint: What You Should Have Working

Verify that your complete Telegram UI Clone operates end-to-end:

- [x] **Channel Broadcast View:** Channels display subscriber counts, sticky pinned message strips, views counters (`👁️ 4.2K`), and the bottom **"Mute / Unmute"** toggle bar.
- [x] **Pinned Banner Navigation:** Tapping the pinned banner smoothly scrolls the list to the pinned post; tapping `✕` smoothly collapses the banner.
- [x] **Floating Message Reactions:** Long-pressing a speech bubble pops up the bouncy reaction pill bar with 7 animated emojis.
- [x] **Reaction Counter Badges:** Selecting an emoji attaches an interactive pill counter directly to the message bubble.
- [x] **Voice Note Hold-to-Record:** Touching and holding the mic button initiates audio capture with a live timer and pulsing red dot.
- [x] **Slide-to-Cancel:** Dragging left beyond the threshold (`‹‹ Slide to cancel`) cancels recording cleanly.
- [x] **Lock-to-Talk:** Dragging up locks recording into hands-free mode, exposing Trash, Timer, and Send buttons.
- [x] **Animated Audio Waveforms:** Active recordings display dynamic 60fps oscillating audio waveform bars rendered via Compose `Canvas`.
- [x] **Settings & Profile Dashboard:** Displays user avatar, phone, `@username`, QR code action, and grouped category settings rows.
- [x] **Complete Application Integration:** Seamless switching between Main Chat List, Direct Messages, Broadcast Channels, Navigation Drawer, and Settings!

---

## 🏋️ Hands-On Coding Exercises

Finalize your Telegram engineering masterclass with these optional capstone challenges:

### 🎯 Exercise 1: Real Mic Audio Amplitude Integration
Replace the mock sine-wave waveform in `TelegramVoiceRecordOverlay` with Android's `AudioRecord` or `MediaRecorder.maxAmplitude` API to render the exact vocal pitch and volume of the speaker!

---

### 🎯 Exercise 2: Double-Tap Quick Reaction
Implement Telegram's famous quick-reaction gesture: double-tapping any message bubble automatically sends a flying heart (`❤️`) or thumbs up (`👍`) animation without opening the full reaction pill!

---

### 🎯 Exercise 3: Storage Usage Donut Chart in Settings
In `TelegramSettingsScreen`, add a Telegram-style circular donut chart under "Data and Storage" visualizing cache usage across Photos, Videos, Documents, and System Files.

---

## 🎓 Course Conclusion: What's Next in Phase 4

Congratulations! 🎉 You have officially completed the **Telegram UI Clone** — one of the most advanced real-world UI engineering modules in Android development:

- **Part 1:** Searchable Chat Feed, Debounced Filtering & Scroll State Detection.
- **Part 2:** Inverted `reverseLayout = true` Chat Feed, Speech Bubbles & `Modifier.animateItem()`.
- **Part 3:** `ModalNavigationDrawer`, Multi-Account Switcher & Attachment `ModalBottomSheet`.
- **Part 4:** Broadcast Channels, Message Reactions, 2D Voice Recording Gestures & Settings.

You have now mastered the architectural patterns, gesture pipelines, canvas animations, and design aesthetics that power the world's most popular messaging applications in **Jetpack Compose**! 🚀
