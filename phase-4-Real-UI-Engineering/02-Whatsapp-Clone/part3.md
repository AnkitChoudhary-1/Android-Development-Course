# 🟢 WhatsApp UI Clone — Part 3: Status (Stories) & Calls Screens

> **🎯 What You Will Build:** The final two core tabs of the WhatsApp Android application in Jetpack Compose: the **Status Screen** and the **Calls Screen**. You will engineer custom Canvas-drawn segmented story rings (indicating seen vs. unseen status counts), dual floating action buttons, a full-screen story viewer with auto-advancing progress timers, call direction indicators (incoming, outgoing, missed), and integrated tab routing.

---

## 📋 Table of Contents

- [Overview — What We're Building](#-overview--what-were-building)
  - [Visual Layout Architecture (Status & Calls)](#-visual-layout-architecture-status--calls)
  - [Tab Integration Flow](#-tab-integration-flow)
- [Target File Structure](#-target-file-structure)
- [Step 0: Domain Models & Mock Datasets](#-step-0-domain-models--mock-datasets)
  - [1. Domain Models — data/Models.kt](#-1-domain-models--datamodelskt)
  - [2. Mock Statuses & Calls — data/FakeData.kt](#-2-mock-statuses--calls--datafakedatakt)
- [Step 1: Custom Canvas Segmented Story Rings](#-step-1-custom-canvas-segmented-story-rings)
  - [Mathematical Geometry of Story Segments](#-mathematical-geometry-of-story-segments)
  - [Implementation — ui/components/StatusAvatar.kt](#-implementation--uicomponentsstatusavatarkt)
- [Step 2: The Status Screen (Stories Feed)](#-step-2-the-status-screen-stories-feed)
  - [Dual Floating Action Buttons Architecture](#-dual-floating-action-buttons-architecture)
  - [Status Item Row — ui/status/StatusItemRow.kt](#-status-item-row--uistatusstatusitemrowkt)
  - [Full Status Screen — ui/status/StatusScreen.kt](#-full-status-screen--uistatusstatusscreenkt)
- [Step 3: Fullscreen Interactive Status Story Viewer](#-step-3-fullscreen-interactive-status-story-viewer)
  - [Story Viewer Architecture](#-story-viewer-architecture)
  - [Implementation — ui/status/StatusViewerScreen.kt](#-implementation--uistatusstatusviewerscreenkt)
- [Step 4: The Calls Screen (Call Logs & Call Links)](#-step-4-the-calls-screen-call-logs--call-links)
  - [Call Row & Status Icons — ui/calls/CallItemRow.kt](#-call-row--status-icons--uicallscallitemrowkt)
  - [Full Calls Screen — ui/calls/CallsScreen.kt](#-full-calls-screen--uicallscallsscreenkt)
- [Step 5: Wiring the Complete 3-Tab Architecture](#-step-5-wiring-the-complete-3-tab-architecture)
  - [Updating the Root Controller — WhatsAppApp.kt](#-updating-the-root-controller--whatsappappkt)
- [🔍 Deep Dive: Advanced Jetpack Compose Concepts](#-deep-dive-advanced-jetpack-compose-concepts)
  - [1. Canvas Coordinate Math with drawArc](#-1-canvas-coordinate-math-with-drawarc)
  - [2. Multi-Bar Segmented Progress Timer](#-2-multi-bar-segmented-progress-timer)
  - [3. Touch-and-Hold Story Pause with pointerInput](#-3-touch-and-hold-story-pause-with-pointerinput)
  - [4. Dual Floating Action Button Layout](#-4-dual-floating-action-button-layout)
- [🧠 Jetpack Compose Principles Applied: Where & Why](#-jetpack-compose-principles-applied-where--why)
- [🧪 Self-Assessment & Knowledge Check](#-self-assessment--knowledge-check)
- [🏁 Checkpoint: What You Should Have Working](#-checkpoint-what-you-should-have-working)
- [🏋️ Hands-On Exercises to Master Phase 4](#-hands-on-exercises-to-master-phase-4)

---

## 📱 Overview — What We're Building

In Parts 1 and 2, we engineered the conversation feed, tab bar navigation, and the interactive detail chat screen with asymmetric speech bubbles. In **Part 3**, we replace both temporary placeholder screens with production-grade implementations of the **STATUS** and **CALLS** tabs.

### 🖼️ Visual Layout Architecture (Status & Calls)

```text
STATUS TAB:                                    CALLS TAB:
┌──────────────────────────────────────┐       ┌──────────────────────────────────────┐
│  WhatsApp           📷  🔍  ⋮       │       │  WhatsApp           📷  🔍  ⋮       │
├──────────────────────────────────────┤       ├──────────────────────────────────────┤
│   CHATS    [STATUS]    CALLS         │       │   CHATS    STATUS    [CALLS]         │
├──────────────────────────────────────┤       ├──────────────────────────────────────┤
│  ┌────┐                              │       │  ┌────┐                              │
│  │ ME ⊕ My status                    │       │  │ 🔗 │ Create call link             │
│  └────┘ Tap to add status update     │       │  └────┘ Share a link for your call   │
├──────────────────────────────────────┤       ├──────────────────────────────────────┤
│  Recent updates                      │       │  Recent                              │
│  ┌────┐                              │       │  ┌────┐                              │
│  │(AB)│ Alice Brown                  │       │  │ AB │ Alice Brown              📹  │
│  └────┘ 35 minutes ago               │       │  └────┘ ↙️ Today, 10:15 AM            │
│         (3 green segments = 3 unread)│       │         (Red arrow = Missed call)    │
│  ┌────┐                              │       │  ┌────┐                              │
│  │(JD)│ John Doe                     │       │  │ JD │ John Doe                 📞  │
│  └────┘ Today, 8:40 AM               │       │  └────┘ ↘️ Yesterday, 4:20 PM        │
│         (1 green segment = 1 unread) │       │         (Green arrow = Incoming call)│
│                                      │       │                                      │
│  Viewed updates                      │       │  ... more call logs ...              │
│  ┌────┐                              │       │                                      │
│  │(SW)│ Sarah Wilson                 │       │                                      │
│  └────┘ Yesterday, 11:15 PM (gray)   │       │                                      │
│                                      │       │                                      │
│                                ✏️    │       │                                      │
│                                📷    │       │                                📞    │
└──────────────────────────────────────┘       └──────────────────────────────────────┘
```

---

### 🔄 Tab Integration Flow

```text
┌─────────────────────────────────────────────────────────────────────────┐
│                             WhatsAppApp.kt                              │
│                                                                         │
│           ┌─────────────────────────────────────────────────┐           │
│           │                 WhatsAppTabs                    │           │
│           │     [ CHATS ]       [ STATUS ]      [ CALLS ]   │           │
│           └─────────────────────────────────────────────────┘           │
│                                     │                                   │
│            ┌────────────────────────┼────────────────────────┐          │
│            ▼                        ▼                        ▼          │
│   ┌──────────────────┐    ┌──────────────────┐    ┌──────────────────┐  │
│   │  ChatListScreen  │    │   StatusScreen   │    │   CallsScreen    │  │
│   │ (Part 1 & 2)     │    │ (Stories + FABs) │    │ (Logs + Links)   │  │
│   └──────────────────┘    └──────────────────┘    └──────────────────┘  │
│            │                        │                                   │
│            ▼                        ▼                                   │
│   ┌──────────────────┐    ┌──────────────────┐                          │
│   │ ChatDetailScreen │    │StatusViewerScreen│                          │
│   │ (Part 2)         │    │(Auto-timer bars) │                          │
│   └──────────────────┘    └──────────────────┘                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 📁 Target File Structure

We introduce two clean packages under `ui`: `ui/status` and `ui/calls`:

```text
com.example.whatsappclone/
├── MainActivity.kt
├── WhatsAppApp.kt                     ← Updated: Routes between Chats, Status, Calls & Story Viewer
├── data/
│   ├── Models.kt                      ← Updated: StatusStory, UserStatus, CallRecord
│   └── FakeData.kt                    ← Updated: Mock statuses & recent call logs
└── ui/
    ├── theme/                         ← From Part 1 & 2
    ├── components/
    │   ├── AvatarImage.kt             ← From Part 1
    │   └── StatusAvatar.kt            ← NEW: Custom Canvas segmented status ring avatar
    ├── chatlist/                      ← From Part 1
    ├── chatdetail/                    ← From Part 2
    ├── status/                        ← NEW in Part 3
    │   ├── StatusItemRow.kt           ← Single status item row with segmented border
    │   ├── StatusScreen.kt            ← Full Status tab with dual FABs
    │   └── StatusViewerScreen.kt      ← Full-screen story viewer with timer bars & pause gestures
    └── calls/                         ← NEW in Part 3
        ├── CallItemRow.kt             ← Call log cell (Direction arrow + Audio/Video icon)
        └── CallsScreen.kt             ← Full Calls tab with Create Link & Call FAB
```

---

## 🛠️ Step 0: Domain Models & Mock Datasets

### 📝 1. Domain Models — `data/Models.kt`

Open `data/Models.kt` and append the models for Status Stories and Call Logs:

```kotlin
package com.example.whatsappclone.data

import androidx.compose.ui.graphics.Color

// ─── PART 2 MODELS (PRESERVED) ─────────────────────────────
enum class DeliveryStatus { PENDING, SENT, DELIVERED, READ }

data class Contact(
    val id: String,
    val name: String,
    val initials: String,
    val avatarColor: Color,
    val about: String = "Hey there! I am using WhatsApp."
)

data class ChatItem(
    val contact: Contact,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int = 0,
    val isOnline: Boolean = false
)

data class Message(
    val id: String,
    val senderId: String,
    val text: String,
    val timestamp: String,
    val isSentByMe: Boolean,
    val deliveryStatus: DeliveryStatus = DeliveryStatus.READ
)

// ─── PART 3 NEW MODELS: STATUS & CALLS ─────────────────────

/**
 * Represents an individual story slide posted by a user.
 */
data class StatusStory(
    val id: String,
    val caption: String,
    val timestamp: String,
    val storyColor: Color, // Background gradient color for text stories
    val isViewed: Boolean = false
)

/**
 * Represents a contact's full status bundle containing one or more stories.
 */
data class UserStatus(
    val contact: Contact,
    val stories: List<StatusStory>,
    val lastUpdated: String
) {
    // True if every story in the bundle has already been viewed
    val isAllViewed: Boolean get() = stories.all { it.isViewed }
}

/**
 * Represents call direction and outcome.
 */
enum class CallDirection {
    INCOMING,  // Green down-left arrow ↘️
    OUTGOING,  // Green up-right arrow ↗️
    MISSED     // Red down-left arrow ↙️
}

enum class CallType {
    AUDIO,
    VIDEO
}

/**
 * Represents a historical call log record.
 */
data class CallRecord(
    val id: String,
    val contact: Contact,
    val timestamp: String,
    val direction: CallDirection,
    val callType: CallType
)
```

---

### 👥 2. Mock Statuses & Calls — `data/FakeData.kt`

Update `data/FakeData.kt` to supply realistic data for statuses and calls:

```kotlin
package com.example.whatsappclone.data

import androidx.compose.ui.graphics.Color

object FakeData {

    // ─── CONTACTS ──────────────────────────────────────────
    val alice = Contact("c1", "Alice Brown", "AB", Color(0xFF6366F1))
    val john = Contact("c2", "John Doe", "JD", Color(0xFFEC4899))
    val mom = Contact("c3", "Mom ❤️", "M", Color(0xFFF59E0B))
    val devTeam = Contact("c4", "Dev Team 🚀", "DT", Color(0xFF10B981))
    val sarah = Contact("c5", "Sarah Wilson", "SW", Color(0xFF8B5CF6))
    val mike = Contact("c6", "Mike Johnson", "MJ", Color(0xFFEF4444))

    // ─── CHATS & MESSAGES (From Part 1 & 2) ────────────────
    val chatList = listOf(
        ChatItem(alice, "Hey! Are you coming tonight?", "9:41 AM", unreadCount = 2, isOnline = true),
        ChatItem(john,  "Thanks for the help! 🙏",      "10:15 AM", unreadCount = 0, isOnline = false),
        ChatItem(mom,   "Don't forget your jacket",     "Yesterday", unreadCount = 0, isOnline = false)
    )

    fun getInitialMessages(contactId: String): List<Message> = listOf(
        Message("m1", contactId, "Hey! How are you doing today?", "9:30 AM", isSentByMe = false),
        Message("m2", "me", "I'm good! Wrapping up work now", "9:32 AM", isSentByMe = true)
    )

    // ─── STATUS UPDATES (Part 3) ───────────────────────────
    val myStatus = UserStatus(
        contact = Contact("me", "My status", "ME", Color(0xFF128C7E)),
        stories = emptyList(), // Empty list displays the "Tap to add status update" prompt
        lastUpdated = "Tap to add status update"
    )

    val recentStatuses = listOf(
        UserStatus(
            contact = alice,
            stories = listOf(
                StatusStory("s1", "At the airport! ✈️ Off to Tokyo!", "35 minutes ago", Color(0xFF1E3264), isViewed = false),
                StatusStory("s2", "Mount Fuji from the plane window 🗻", "20 minutes ago", Color(0xFFE13300), isViewed = false),
                StatusStory("s3", "Finally landed! Hello Shibuya 🇯🇵", "10 minutes ago", Color(0xFF8D67AB), isViewed = false)
            ),
            lastUpdated = "10 minutes ago"
        ),
        UserStatus(
            contact = john,
            stories = listOf(
                StatusStory("s4", "New personal record on deadlift! 🏋️ 220kg", "Today, 8:40 AM", Color(0xFF148A08), isViewed = false)
            ),
            lastUpdated = "Today, 8:40 AM"
        ),
        UserStatus(
            contact = devTeam,
            stories = listOf(
                StatusStory("s5", "Compose 1.8 build passed all unit tests ✅", "Today, 7:15 AM", Color(0xFF0D73EC), isViewed = false),
                StatusStory("s6", "Sprint retro starts at 4 PM ☕", "Today, 7:16 AM", Color(0xFF503750), isViewed = false)
            ),
            lastUpdated = "Today, 7:16 AM"
        )
    )

    val viewedStatuses = listOf(
        UserStatus(
            contact = sarah,
            stories = listOf(
                StatusStory("s7", "Morning coffee brew ☕", "Yesterday, 11:15 PM", Color(0xFF7D4B32), isViewed = true)
            ),
            lastUpdated = "Yesterday, 11:15 PM"
        ),
        UserStatus(
            contact = mike,
            stories = listOf(
                StatusStory("s8", "Sunday cycling around the bay 🚴", "Yesterday, 6:30 PM", Color(0xFF477D95), isViewed = true),
                StatusStory("s9", "Sunset views 🌅", "Yesterday, 6:45 PM", Color(0xFFBC5900), isViewed = true)
            ),
            lastUpdated = "Yesterday, 6:45 PM"
        )
    )

    // ─── RECENT CALLS (Part 3) ─────────────────────────────
    val recentCalls = listOf(
        CallRecord("cl1", alice, "Today, 10:15 AM", CallDirection.MISSED, CallType.VIDEO),
        CallRecord("cl2", john, "Yesterday, 4:20 PM", CallDirection.INCOMING, CallType.AUDIO),
        CallRecord("cl3", mom, "Yesterday, 1:05 PM", CallDirection.OUTGOING, CallType.AUDIO),
        CallRecord("cl4", sarah, "October 12, 9:30 PM", CallDirection.MISSED, CallType.AUDIO),
        CallRecord("cl5", devTeam, "October 11, 11:00 AM", CallDirection.INCOMING, CallType.VIDEO)
    )
}
```

---

## 🎨 Step 1: Custom Canvas Segmented Story Rings

### Mathematical Geometry of Story Segments

In WhatsApp, the circular border around a contact's avatar communicates two critical pieces of information:
1. **Unviewed vs. Viewed Status:** Unviewed stories have a bright green ring (`WhatsAppGreenLight`); viewed stories have a subtle gray ring (`Color(0xFFB0B3B8)`).
2. **Number of Stories:** If a contact has **1 story**, the ring is a single continuous $360^\circ$ circle. If they have **3 stories**, the ring is divided into **3 equal arcs** with a $4^\circ$ gap separating each arc:

$$\text{Sweep Angle per Segment} = \frac{360^\circ - (N \times \text{Gap Angle})}{N}$$

```text
1 STORY:                             3 STORIES:
    ╭─────────╮                          ╭────   ────╮
   │   (AB)    │                        │     (AB)    │
   │  Avatar   │                        │    Avatar   │
    ╰─────────╯                          ╰────   ────╯
  (Single continuous                   (3 distinct segments
   360° green stroke)                   separated by small gaps)
```

---

### 🧩 Implementation — `ui/components/StatusAvatar.kt`

Create `ui/components/StatusAvatar.kt`:

```kotlin
package com.example.whatsappclone.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.whatsappclone.ui.theme.WhatsAppGreenLight

/**
 * Draws a circular avatar framed with WhatsApp's signature segmented story border.
 */
@Composable
fun StatusAvatar(
    initials: String,
    backgroundColor: Color,
    storyCount: Int,
    isAllViewed: Boolean,
    modifier: Modifier = Modifier,
    avatarSize: Dp = 52.dp,
    strokeWidth: Dp = 2.5.dp
) {
    val totalSize = avatarSize + (strokeWidth * 2) + 6.dp
    val ringColor = if (isAllViewed) Color(0xFFB0B3B8) else WhatsAppGreenLight

    Box(
        modifier = modifier.size(totalSize),
        contentAlignment = Alignment.Center
    ) {
        // 📌 Canvas Drawing: Renders the segmented circle border
        if (storyCount > 0) {
            Canvas(modifier = Modifier.size(totalSize)) {
                val strokePx = strokeWidth.toPx()

                if (storyCount == 1) {
                    // Single continuous 360-degree circle
                    drawCircle(
                        color = ringColor,
                        style = Stroke(width = strokePx)
                    )
                } else {
                    // Segmented arcs
                    val gapAngle = 4f
                    val sweepAngle = (360f - (storyCount * gapAngle)) / storyCount
                    var startAngle = -90f // Start from the top (12 o'clock position)

                    for (i in 0 until storyCount) {
                        drawArc(
                            color = ringColor,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = strokePx, cap = StrokeCap.Round)
                        )
                        startAngle += sweepAngle + gapAngle
                    }
                }
            }
        }

        // Inner Avatar Circle
        AvatarImage(
            initials = initials,
            backgroundColor = backgroundColor,
            size = avatarSize,
            fontSize = 18,
            modifier = Modifier.clip(CircleShape)
        )
    }
}
```

---

## 📷 Step 2: The Status Screen (Stories Feed)

### Dual Floating Action Buttons Architecture

The WhatsApp Status screen features a unique dual-FAB setup:
1. **Secondary Edit FAB (`Edit` pencil):** A smaller 44dp button with a muted light-surface background, positioned slightly above the primary FAB, used for typing text updates.
2. **Primary Camera FAB (`PhotoCamera`):** A standard 56dp emerald-green button used for capturing photo/video stories.

```text
Column (align = Alignment.BottomEnd, padding = 16dp)
 ├── SmallFloatingActionButton (size = 44dp, containerColor = SurfaceLight, Icon = Edit)
 ├── Spacer (height = 16dp)
 └── FloatingActionButton (size = 56dp, containerColor = WhatsAppGreenLight, Icon = PhotoCamera)
```

---

### 📄 Status Item Row — `ui/status/StatusItemRow.kt`

Create `ui/status/StatusItemRow.kt`:

```kotlin
package com.example.whatsappclone.ui.status

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsappclone.data.UserStatus
import com.example.whatsappclone.ui.components.StatusAvatar
import com.example.whatsappclone.ui.theme.TextPrimary
import com.example.whatsappclone.ui.theme.TextSecondary

@Composable
fun StatusItemRow(
    userStatus: UserStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Segmented Status Ring Avatar
        StatusAvatar(
            initials = userStatus.contact.initials,
            backgroundColor = userStatus.contact.avatarColor,
            storyCount = userStatus.stories.size,
            isAllViewed = userStatus.isAllViewed
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = userStatus.contact.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = userStatus.lastUpdated,
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
```

---

### 📱 Full Status Screen — `ui/status/StatusScreen.kt`

Create `ui/status/StatusScreen.kt`:

```kotlin
package com.example.whatsappclone.ui.status

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsappclone.data.FakeData
import com.example.whatsappclone.data.UserStatus
import com.example.whatsappclone.ui.components.AvatarImage
import com.example.whatsappclone.ui.theme.BackgroundLight
import com.example.whatsappclone.ui.theme.TextPrimary
import com.example.whatsappclone.ui.theme.TextSecondary
import com.example.whatsappclone.ui.theme.WhatsAppGreen
import com.example.whatsappclone.ui.theme.WhatsAppGreenLight

@Composable
fun StatusScreen(
    onStatusClick: (UserStatus) -> Unit,
    modifier: Modifier = Modifier,
    recentStatuses: List<UserStatus> = FakeData.recentStatuses,
    viewedStatuses: List<UserStatus> = FakeData.viewedStatuses
) {
    Box(modifier = modifier.fillMaxSize().background(Color.White)) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            // ─── 1. MY STATUS ROW ──────────────────────────────
            item {
                MyStatusRow(onClick = { /* Add status */ })
            }

            // ─── 2. RECENT UPDATES HEADER & ITEMS ──────────────
            if (recentStatuses.isNotEmpty()) {
                item {
                    StatusSectionHeader(title = "Recent updates")
                }
                items(
                    items = recentStatuses,
                    key = { it.contact.id }
                ) { status ->
                    StatusItemRow(
                        userStatus = status,
                        onClick = { onStatusClick(status) }
                    )
                }
            }

            // ─── 3. VIEWED UPDATES HEADER & ITEMS ──────────────
            if (viewedStatuses.isNotEmpty()) {
                item {
                    StatusSectionHeader(title = "Viewed updates")
                }
                items(
                    items = viewedStatuses,
                    key = { it.contact.id }
                ) { status ->
                    StatusItemRow(
                        userStatus = status,
                        onClick = { onStatusClick(status) }
                    )
                }
            }

            // Bottom breathing room for dual FABs
            item {
                Spacer(modifier = Modifier.height(88.dp))
            }
        }

        // ─── 4. DUAL FLOATING ACTION BUTTONS ───────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Secondary Small FAB: Pencil for Text Status
            SmallFloatingActionButton(
                onClick = { /* Open Text Status Composer */ },
                containerColor = Color(0xFFE9EDEF),
                contentColor = Color(0xFF54656F),
                shape = CircleShape,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Text Status",
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Primary Large FAB: Camera for Photo/Video Status
            FloatingActionButton(
                onClick = { /* Open Status Camera */ },
                containerColor = WhatsAppGreen,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.PhotoCamera,
                    contentDescription = "Camera",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun MyStatusRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            AvatarImage(
                initials = "ME",
                backgroundColor = Color(0xFF128C7E),
                size = 52.dp,
                fontSize = 18
            )

            // Green Plus (+) Badge
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(WhatsAppGreenLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Status",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = "My status",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = "Tap to add status update",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun StatusSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = TextSecondary,
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
```

---

## 📽️ Step 3: Fullscreen Interactive Status Story Viewer

### Story Viewer Architecture

When a user taps on any status row, the app launches the full-screen story viewer (`StatusViewerScreen`):
1. **Top Segmented Progress Bars:** $N$ horizontal bars representing the user's stories. Active stories smoothly fill from $0\%$ to $100\%$ over a 5-second duration.
2. **Interactive Taps:**
   - Tap **Right 60%** of the screen: Advances immediately to the next story slide.
   - Tap **Left 40%** of the screen: Navigates back to the previous slide.
3. **Press-and-Hold to Pause:** Pressing down on the screen suspends the animation timer; lifting your finger resumes playback seamlessly!

```text
┌─────────────────────────────────────────────────────────────┐
│  [========] [======    ] [          ]  ← 3 Progress Bars    │
│  ←  [AB] Alice Brown    35 minutes ago           ⋮          │
│                                                             │
│                                                             │
│                                                             │
│               At the airport! ✈️ Off to Tokyo!              │
│                                                             │
│                                                             │
│                                                             │
│  [Tap Left 40% = Prev]       [Tap Right 60% = Next]         │
│  [Hold Down Screen = Pause Animation]                       │
└─────────────────────────────────────────────────────────────┘
```

---

### 💻 Implementation — `ui/status/StatusViewerScreen.kt`

Create `ui/status/StatusViewerScreen.kt`:

```kotlin
package com.example.whatsappclone.ui.status

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsappclone.data.UserStatus
import com.example.whatsappclone.ui.components.AvatarImage
import kotlinx.coroutines.isActive

@Composable
fun StatusViewerScreen(
    userStatus: UserStatus,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onClose)

    val stories = userStatus.stories
    var currentStoryIndex by remember { mutableIntStateOf(0) }
    var isPaused by remember { mutableStateOf(false) }

    val progress = remember { Animatable(0f) }

    // 📌 Timer Effect: Drives 5-second automatic story progression
    LaunchedEffect(currentStoryIndex) {
        progress.snapTo(0f)
        while (isActive) {
            if (!isPaused) {
                val remainingTime = ((1f - progress.value) * 5000).toInt()
                val result = progress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = remainingTime, easing = LinearEasing)
                )
                if (result.endValue >= 1f) {
                    if (currentStoryIndex < stories.size - 1) {
                        currentStoryIndex++
                    } else {
                        onClose() // Finished all stories
                        break
                    }
                }
            }
            kotlinx.coroutines.delay(16) // ~60fps tick check
        }
    }

    val currentStory = stories.getOrNull(currentStoryIndex) ?: return

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(currentStory.storyColor)
            // 📌 Touch Gestures: Detect tap navigation & press-and-hold pause
            .pointerInput(currentStoryIndex) {
                detectTapGestures(
                    onPress = {
                        isPaused = true
                        tryAwaitRelease()
                        isPaused = false
                    },
                    onTap = { offset ->
                        val screenWidth = size.width
                        if (offset.x < screenWidth * 0.4f) {
                            // Tap Left: Previous Slide
                            if (currentStoryIndex > 0) currentStoryIndex--
                        } else {
                            // Tap Right: Next Slide
                            if (currentStoryIndex < stories.size - 1) currentStoryIndex++ else onClose()
                        }
                    }
                )
            }
    ) {
        // ─── 1. TOP PROGRESS BARS & HEADER ─────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            // Horizontal Timer Bars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                stories.forEachIndexed { index, _ ->
                    val barFill = when {
                        index < currentStoryIndex  -> 1f
                        index == currentStoryIndex -> progress.value
                        else                       -> 0f
                    }
                    ProgressBarSegment(fillFraction = barFill, modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Contact Info Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                AvatarImage(
                    initials = userStatus.contact.initials,
                    backgroundColor = userStatus.contact.avatarColor,
                    size = 36.dp,
                    fontSize = 14
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(text = userStatus.contact.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = currentStory.timestamp, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                }
            }
        }

        // ─── 2. STORY TEXT PAYLOAD ─────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = currentStory.caption,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp
            )
        }
    }
}

@Composable
private fun ProgressBarSegment(fillFraction: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(2.5.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Color.White.copy(alpha = 0.35f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fillFraction)
                .background(Color.White)
        )
    }
}
```

---

## 📞 Step 4: The Calls Screen (Call Logs & Call Links)

### 📄 Call Row & Status Icons — `ui/calls/CallItemRow.kt`

Create `ui/calls/CallItemRow.kt`:

```kotlin
package com.example.whatsappclone.ui.calls

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallMissed
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsappclone.data.CallDirection
import com.example.whatsappclone.data.CallRecord
import com.example.whatsappclone.data.CallType
import com.example.whatsappclone.ui.components.AvatarImage
import com.example.whatsappclone.ui.theme.TextPrimary
import com.example.whatsappclone.ui.theme.TextSecondary
import com.example.whatsappclone.ui.theme.WhatsAppGreen

@Composable
fun CallItemRow(
    call: CallRecord,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Contact Avatar
        AvatarImage(
            initials = call.contact.initials,
            backgroundColor = call.contact.avatarColor,
            size = 50.dp,
            fontSize = 18
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Center Details Column: Name + Direction Icon + Timestamp
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = call.contact.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (call.direction == CallDirection.MISSED) Color(0xFFEF4444) else TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.padding(top = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Call Direction Arrow Icon
                val (directionIcon, iconTint) = when (call.direction) {
                    CallDirection.INCOMING -> Icons.AutoMirrored.Filled.CallReceived to Color(0xFF25D366)
                    CallDirection.OUTGOING -> Icons.AutoMirrored.Filled.CallMade to Color(0xFF25D366)
                    CallDirection.MISSED   -> Icons.AutoMirrored.Filled.CallMissed to Color(0xFFEF4444)
                }

                Icon(
                    imageVector = directionIcon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = call.timestamp,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        // Trailing Communication Action Icon (Audio vs Video Call)
        IconButton(onClick = { /* Re-dial contact */ }) {
            Icon(
                imageVector = if (call.callType == CallType.VIDEO) Icons.Filled.Videocam else Icons.Filled.Call,
                contentDescription = "Call",
                tint = WhatsAppGreen,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
```

---

### 📱 Full Calls Screen — `ui/calls/CallsScreen.kt`

Create `ui/calls/CallsScreen.kt`:

```kotlin
package com.example.whatsappclone.ui.calls

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddIcCall
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsappclone.data.CallRecord
import com.example.whatsappclone.data.FakeData
import com.example.whatsappclone.ui.theme.BackgroundLight
import com.example.whatsappclone.ui.theme.TextPrimary
import com.example.whatsappclone.ui.theme.TextSecondary
import com.example.whatsappclone.ui.theme.WhatsAppGreen

@Composable
fun CallsScreen(
    modifier: Modifier = Modifier,
    calls: List<CallRecord> = FakeData.recentCalls
) {
    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize().background(Color.White)) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            // ─── 1. CREATE CALL LINK TILE ──────────────────────
            item {
                CreateCallLinkRow(onClick = {
                    Toast.makeText(context, "Call link generated!", Toast.LENGTH_SHORT).show()
                })
            }

            // ─── 2. RECENT HEADER ──────────────────────────────
            item {
                Text(
                    text = "Recent",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundLight)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // ─── 3. CALL LOG ITEMS ─────────────────────────────
            items(
                items = calls,
                key = { it.id }
            ) { callRecord ->
                CallItemRow(
                    call = callRecord,
                    onClick = {
                        Toast.makeText(context, "Calling ${callRecord.contact.name}...", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // ─── 4. NEW CALL FAB ───────────────────────────────────
        FloatingActionButton(
            onClick = {
                Toast.makeText(context, "Select contact to call", Toast.LENGTH_SHORT).show()
            },
            containerColor = WhatsAppGreen,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.AddIcCall,
                contentDescription = "New Call",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun CreateCallLinkRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Circular Link Icon
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(WhatsAppGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Link,
                contentDescription = "Call Link",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = "Create call link",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = "Share a link for your WhatsApp call",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
```

---

## 🔌 Step 5: Wiring the Complete 3-Tab Architecture

### Updating the Root Controller — `WhatsAppApp.kt`

We now update `WhatsAppApp.kt` to coordinate all 3 core tabs (`CHATS`, `STATUS`, `CALLS`) as well as the full-screen story viewer (`StatusViewerScreen`):

```kotlin
package com.example.whatsappclone

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.whatsappclone.data.ChatItem
import com.example.whatsappclone.data.FakeData
import com.example.whatsappclone.data.UserStatus
import com.example.whatsappclone.ui.calls.CallsScreen
import com.example.whatsappclone.ui.chatdetail.ChatDetailScreen
import com.example.whatsappclone.ui.chatlist.ChatListScreen
import com.example.whatsappclone.ui.chatlist.WhatsAppTab
import com.example.whatsappclone.ui.chatlist.WhatsAppTabs
import com.example.whatsappclone.ui.status.StatusScreen
import com.example.whatsappclone.ui.status.StatusViewerScreen
import com.example.whatsappclone.ui.theme.WhatsAppGreen

@Composable
fun WhatsAppApp() {
    // 📌 Screen Destinations
    var activeChat: ChatItem? by remember { mutableStateOf(null) }
    var activeViewingStatus: UserStatus? by remember { mutableStateOf(null) }

    // Tab state for the primary dashboard
    var selectedTab by remember { mutableStateOf(WhatsAppTab.CHATS) }
    val totalUnread = remember { FakeData.chatList.sumOf { it.unreadCount } }

    // ─── 1. FULLSCREEN STORY VIEWER OVERLAY ───────────────────
    if (activeViewingStatus != null) {
        StatusViewerScreen(
            userStatus = activeViewingStatus!!,
            onClose = { activeViewingStatus = null }
        )
        return
    }

    // ─── 2. CHAT DETAIL SCREEN OVERLAY ────────────────────────
    if (activeChat != null) {
        BackHandler { activeChat = null }
        ChatDetailScreen(
            chat = activeChat!!,
            onBackClick = { activeChat = null }
        )
        return
    }

    // ─── 3. PRIMARY 3-TAB DASHBOARD ───────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhatsAppGreen)
    ) {
        // Main Tab Bar
        WhatsAppTabs(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            unreadChatCount = totalUnread
        )

        // Tab Content Switching
        when (selectedTab) {
            WhatsAppTab.CHATS -> ChatListScreen(
                onChatClick = { chat -> activeChat = chat }
            )
            WhatsAppTab.STATUS -> StatusScreen(
                onStatusClick = { status -> activeViewingStatus = status }
            )
            WhatsAppTab.CALLS -> CallsScreen()
        }
    }
}
```

---

## 🔍 Deep Dive: Advanced Jetpack Compose Concepts

### 1. Canvas Coordinate Math with `drawArc`

When drawing segmented story circles:
- **`startAngle = -90f`:** $0^\circ$ corresponds to the right edge (3 o'clock). Starting at $-90^\circ$ positions the first segment at the top (12 o'clock).
- **`useCenter = false`:** Ensures we stroke the outline curve of the circle without drawing radiating pie-slice lines back to the center origin.
- **`StrokeCap.Round`:** Adds smooth, rounded edge caps to each arc segment for a modern polished finish.

---

### 2. Multi-Bar Segmented Progress Timer

In `StatusViewerScreen`, each status bar binds its fill fraction dynamically:
```kotlin
val barFill = when {
    index < currentStoryIndex  -> 1f             // Fully filled (past story)
    index == currentStoryIndex -> progress.value // Actively animating (0f -> 1f)
    else                       -> 0f             // Empty (future story)
}
```
This guarantees crisp state restoration when jumping between slides.

---

### 3. Touch-and-Hold Story Pause with `pointerInput`

```kotlin
.pointerInput(currentStoryIndex) {
    detectTapGestures(
        onPress = {
            isPaused = true
            tryAwaitRelease() // Suspends execution until the user lifts their finger!
            isPaused = false
        },
        onTap = { offset -> ... }
    )
}
```
`tryAwaitRelease()` is a specialized coroutine helper that waits for the `MotionEvent.ACTION_UP` or `ACTION_CANCEL` event, enabling seamless press-to-pause functionality.

---

### 4. Dual Floating Action Button Layout

Placing two FABs in Compose is cleanly achieved by nesting `SmallFloatingActionButton` and `FloatingActionButton` inside a single `Column` anchored to `Alignment.BottomEnd`.

---

## 🧠 Jetpack Compose Principles Applied: Where & Why

| Compose Concept | Where It Is Used | Engineering Rationale |
| :--- | :--- | :--- |
| **`Canvas` & `drawArc`** | `StatusAvatar` | Draws custom segmented status circles based on dynamic story counts. |
| **`Animatable`** | `StatusViewerScreen` | Powers the linear 5-second progress bar animation with real-time pausing. |
| **`detectTapGestures`** | `StatusViewerScreen` | Differentiates single taps (prev/next) from continuous press-and-hold gestures. |
| **`tryAwaitRelease()`** | Story pause interaction | Non-blocking suspension until touch release, resuming story progress cleanly. |
| **Multi-layer Overlay Routing** | `WhatsAppApp` | Conditionally short-circuits UI tree to display modals (`activeViewingStatus != null`). |
| **`SmallFloatingActionButton`** | `StatusScreen` | Material 3 compliant compact FAB for the secondary text status button. |
| **`StrokeCap.Round`** | Segmented story arcs | Creates smooth, pill-shaped circular arc segments without jagged edges. |
| **`LazyColumn` with stable keys** | `StatusScreen`, `CallsScreen` | Efficient recycling of status rows and call logs across feed updates. |

---

## 🧪 Self-Assessment & Knowledge Check

Test your mastery of the Phase 4 UI engineering concepts:

### 1. In `StatusAvatar`, why is `startAngle` initialized to `-90f`?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
In Android Canvas trigonometry, $0^\circ$ corresponds to the positive X-axis (the 3 o'clock position on an analog clock). Setting `startAngle = -90f` rotates the coordinate system counter-clockwise by $90^\circ$, ensuring the first story segment starts cleanly at the top (12 o'clock position), matching WhatsApp and Instagram story ring conventions.
</details>

---

### 2. How does `tryAwaitRelease()` in `detectTapGestures` pause the story progress timer?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
When a finger touches the screen, `onPress` fires and sets `isPaused = true`. Calling `tryAwaitRelease()` suspends the coroutine until the user lifts their touch. Because the animation loop in `LaunchedEffect` checks `if (!isPaused)`, the progress animation halts for the exact duration of the hold, resuming seamlessly as soon as `tryAwaitRelease()` completes.
</details>

---

### 3. How does `StatusAvatar` handle a user with only 1 story versus a user with 4 stories?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
When `storyCount == 1`, it executes `drawCircle()` to render a continuous $360^\circ$ unbroken stroke. When `storyCount > 1`, it calculates `sweepAngle = (360f - (count * gapAngle)) / count` and iterates in a loop, invoking `drawArc()` with spacing gaps to render discrete, evenly spaced segments.
</details>

---

### 4. Why is `StatusViewerScreen` rendered above `WhatsAppTabs` rather than inside a tab scaffold?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
Stories are immersive full-screen visual experiences that must draw edge-to-edge behind system status bars, completely obscuring top tabs and floating buttons. In `WhatsAppApp`, evaluating `if (activeViewingStatus != null)` before the tab layout short-circuits the composition, giving the story viewer full-screen dominance.
</details>

---

### 5. Why do missed calls display red arrows and red text names while incoming/outgoing calls use green arrows and dark text?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
This follows WhatsApp's visual hierarchy standards: missed calls represent an unaddressed alert state requiring user attention, so they are highlighted in red (`0xFFEF4444`) using `CallDirection.MISSED` with the `CallMissed` icon. Completed incoming and outgoing calls are normal status events, rendered in neutral text with green (`#25D366`) direction arrows.
</details>

---

## 🏁 Checkpoint: What You Should Have Working

Verify your complete WhatsApp UI Clone across all 3 phases:

- [x] **Chats Tab:** Scrollable conversations with avatars, timestamps, last messages, unread badges, and indented dividers.
- [x] **Chat Detail:** Speech bubbles with asymmetric speech tails, timestamps, cyan checkmarks, auto-scroll, and interactive message sending with automated replies.
- [x] **Status Tab:**
  - "My Status" tile with a green `+` badge.
  - "Recent updates" and "Viewed updates" sections.
  - Custom Canvas-drawn segmented rings (e.g. 3 distinct green arcs for Alice, 1 green arc for John, gray arcs for viewed stories).
  - Dual stacked FABs (pencil for text, camera for photo).
- [x] **Fullscreen Story Viewer:**
  - Tapping a status opens the story viewer with top segmented timer bars.
  - Tap right advances slide; tap left rewinds slide.
  - Press-and-hold pauses the timer; releasing resumes.
- [x] **Calls Tab:**
  - "Create call link" tile with green link icon.
  - Recent call logs showing missed (red), incoming (green), and outgoing (green) calls with audio/video icons.
  - New call floating action button.

---

## 🏋️ Hands-On Exercises to Master Phase 4

Solidify your Compose mastery with these advanced engineering challenges:

### 🎯 Exercise 1: Mute Status Updates Action
Add a long-press context dialog or dropdown menu on any status item to **Mute** that contact's status. Muted statuses should collapse into a third accordion section titled **"Muted updates"** that can be expanded or collapsed with an arrow indicator.

---

### 🎯 Exercise 2: Animated Segment Progress Arc
In `StatusAvatar`, when a user has viewed 2 out of 3 stories, render the first 2 segments in **gray** and the final unviewed segment in **green**!
> **Hint:** Modify `StatusAvatar` to accept `viewedCount: Int` and conditionally change the `color` argument inside the `for` loop:
> ```kotlin
> val segmentColor = if (i < viewedCount) Color.Gray else WhatsAppGreenLight
> ```

---

### 🎯 Exercise 3: Clear Call History Menu
Add a `DropdownMenu` to the top bar overflow icon (`MoreVert`) on the Calls tab with the action **"Clear call log"**. Tapping it should show an `AlertDialog` confirming the action and clearing the call logs list!
