# 💬 WhatsApp UI Clone — Part 1: Chat List Screen

> **🎯 What You Will Build:** A pixel-perfect, light-themed WhatsApp Chat List Screen in Jetpack Compose featuring a signature WhatsApp-green `TopAppBar`, custom tab indicators with unread badges, circular initial avatars, nested `Row` + `Column` conversation rows with dynamic unread pill badges, an indented `HorizontalDivider`, a floating action button (`FAB`), and unidirectional state hoisting.

---

## 📋 Table of Contents

- [Overview — What We're Building](#-overview--what-were-building)
  - [Visual Layout Architecture](#-visual-layout-architecture)
- [Step 0: Project Setup & Dependencies](#-step-0-project-setup--dependencies)
  - [1. Create the Project](#-1-create-the-project)
  - [2. Add Extended Icons Dependency](#-2-add-extended-icons-dependency)
  - [3. Target File Structure](#-3-target-file-structure)
- [Step 1: Design System & Theme (WhatsApp Palette)](#-step-1-design-system--theme-whatsapp-palette)
  - [1. Color Definitions — ui/theme/Color.kt](#-1-color-definitions--uithemecolorkt)
  - [2. Light Theme Configuration — ui/theme/Theme.kt](#-2-light-theme-configuration--uithemethemekt)
- [Step 2: Data Models & Mock Dataset](#-step-2-data-models--mock-dataset)
  - [1. Domain Data Architecture](#-1-domain-data-architecture)
  - [2. Data Classes — data/Models.kt](#-2-data-classes--datamodelskt)
  - [3. Mock Dataset — data/FakeData.kt](#-3-mock-dataset--datafakedatakt)
- [Step 3: Reusable Avatar Component](#-step-3-reusable-avatar-component)
  - [Implementation — ui/components/AvatarImage.kt](#-implementation--uicomponentsavatarimagekt)
  - [Modifier Order Deep-Dive](#-modifier-order-deep-dive)
- [Step 4: WhatsApp Tab Bar (Chats / Status / Calls)](#-step-4-whatsapp-tab-bar-chats--status--calls)
  - [Tab Layout Structure](#-tab-layout-structure)
  - [Implementation — ui/chatlist/WhatsAppTabs.kt](#-implementation--uichatlistwhatsapptabskt)
  - [State Hoisting in the Tab Bar](#-state-hoisting-in-the-tab-bar)
- [Step 5: Chat Item Row (Conversation Cell)](#-step-5-chat-item-row-conversation-cell)
  - [Nested Row + Column Layout Structure](#-nested-row--column-layout-structure)
  - [Visual Cell Breakdown](#-visual-cell-breakdown)
  - [Implementation — ui/chatlist/ChatItemRow.kt](#-implementation--uichatlistchatitemrowkt)
  - [Layout Mechanics & Weight Distribution](#-layout-mechanics--weight-distribution)
- [Step 6: Full Chat List Screen](#-step-6-full-chat-list-screen)
  - [Implementation — ui/chatlist/ChatListScreen.kt](#-implementation--uichatlistchatlistscreenkt)
- [Step 7: Wiring Everything Together](#-step-7-wiring-everything-together)
  - [1. Temporary Screen — ui/PlaceholderScreen.kt](#-1-temporary-screen--uiplaceholderscreenkt)
  - [2. Root App Composable — WhatsAppApp.kt](#-2-root-app-composable--whatsappappkt)
  - [3. Activity Entry Point — MainActivity.kt](#-3-activity-entry-point--mainactivitykt)
- [💬 Deep Dive: How Chat Bubbles Work (Part 2 Preview)](#-deep-dive-how-chat-bubbles-work-part-2-preview)
  - [Visual Comparison: Sent vs. Received](#-visual-comparison-sent-vs-received)
  - [Bubble Implementation Mechanics](#-bubble-implementation-mechanics)
  - [Summary of Bubble Differences](#-summary-of-bubble-differences)
- [🧠 Jetpack Compose Principles Applied: Where & Why](#-jetpack-compose-principles-applied-where--why)
- [🧪 Self-Assessment & Knowledge Check](#-self-assessment--knowledge-check)
- [🏁 Checkpoint: What You Should Have Working](#-checkpoint-what-you-should-have-working)
- [🏋️ Hands-On Exercises Before Part 2](#-hands-on-exercises-before-part-2)

---

## 📱 Overview — What We're Building

In Part 1 of the WhatsApp UI Engineering module, we construct the flagship **Chat List Screen**. This screen forms the core entry point of WhatsApp and features dense information hierarchy, custom badges, weighted text truncations, and smooth tab switching.

### 🖼️ Visual Layout Architecture

```text
┌──────────────────────────────────────┐
│  WhatsApp           📷  🔍  ⋮       │  ← Top App Bar (Title + Camera, Search, Menu)
├──────────────────────────────────────┤
│   CHATS (3)    STATUS    CALLS      │  ← Material Tabs (White underline + badge)
├──────────────────────────────────────┤
│  ┌────┐                              │
│  │ AB │ Alice Brown                  │  ← Avatar (Initials on colored circle)
│  └────┘ Hey! Are you coming to…  9:41│  ← Contact name + Last message + Timestamp
│         ────────────────────────     │  ← Indented Divider (starts after avatar)
│  ┌────┐                              │
│  │ JD │ John Doe              10:15  │
│  └────┘ Thanks for the help!      ② │  ← Dynamic green unread count badge
│         ────────────────────────     │
│  ┌────┐                              │
│  │ MG │ Mom & Dad Group     Yester… │
│  └────┘ Dad: Don't forget to…       │
│         ────────────────────────     │
│  ... more scrollable chats ...       │
│                                      │
│                              💬      │  ← Floating Action Button (New Chat)
└──────────────────────────────────────┘
```

---

## 🛠️ Step 0: Project Setup & Dependencies

### 🆕 1. Create the Project

Initialize a new Jetpack Compose project inside Android Studio:

```text
Android Studio → New Project → "Empty Activity" (Compose)
Name: WhatsAppClone
Package name: com.example.whatsappclone
Minimum SDK: 24 (Android 7.0 Nougat)
Build configuration language: Kotlin DSL (build.gradle.kts)
```

---

### 📦 2. Add Extended Icons Dependency

Open `app/build.gradle.kts` and add the extended icons library inside `dependencies { ... }`. This library provides icons such as `PhotoCamera`, `MoreVert`, `Chat`, `Search`, and `DoneAll`:

```kotlin
dependencies {
    // ... existing Compose dependencies from the template BOM ...

    // Extended Material Icons (Chat, PhotoCamera, MoreVert, DoneAll, etc.)
    implementation("androidx.compose.material:material-icons-extended")
}
```

> **📌 Note:** Because your template already uses the Compose Bill of Materials (`platform(libs.androidx.compose.bom)`), you do **not** need to declare a version number here. Click **Sync Now** to integrate the library.

---

### 📁 3. Target File Structure

Organize your source files cleanly into dedicated architectural packages:

```text
com.example.whatsappclone/
├── MainActivity.kt                  ← Application Activity entry point
├── WhatsAppApp.kt                   ← Root composable: tab state + screen switching
├── data/
│   ├── Models.kt                    ← Domain models: Contact, ChatItem, Message
│   └── FakeData.kt                  ← Static mock conversations & contacts
└── ui/
    ├── theme/
    │   ├── Color.kt                 ← WhatsApp signature green palette
    │   └── Theme.kt                 ← Light theme configuration
    ├── components/
    │   └── AvatarImage.kt           ← Reusable circular avatar with initials
    ├── chatlist/
    │   ├── ChatListScreen.kt        ← Scaffold + TopBar + FAB + LazyColumn
    │   ├── ChatItemRow.kt           ← Single conversation row (Avatar + Name + Text + Badge)
    │   └── WhatsAppTabs.kt          ← Chats / Status / Calls tab bar with badges
    └── PlaceholderScreen.kt         ← Temporary screens for Status, Calls, and Details
```

> **💡 Action:** Right-click `com.example.whatsappclone` in Android Studio → **New** → **Package** to create each package.

---

## 🎨 Step 1: Design System & Theme (WhatsApp Palette)

WhatsApp utilizes a signature dark-teal top bar, an emerald-green accent for unread badges and floating action buttons, crisp white card surfaces, and subtle light-gray screen backgrounds.

---

### 🌈 1. Color Definitions — `ui/theme/Color.kt`

Create `ui/theme/Color.kt` and define the palette:

```kotlin
package com.example.whatsappclone.ui.theme

import androidx.compose.ui.graphics.Color

// ─── WhatsApp Brand Colors ─────────────────────────────────
val WhatsAppGreen       = Color(0xFF075E54)  // Top app bar, primary tab bar background
val WhatsAppGreenDark   = Color(0xFF054D44)  // System status bar background
val WhatsAppGreenLight  = Color(0xFF25D366)  // Unread badges, FAB, online indicators
val WhatsAppTeal        = Color(0xFF128C7E)  // Secondary accent green

// ─── Chat Bubbles (Prepared for Part 2) ───────────────────
val BubbleSent          = Color(0xFFDCF8C6)  // Light green (sent messages by me)
val BubbleReceived      = Color(0xFFFFFFFF)  // Pure white (received messages)
val ChatBackground      = Color(0xFFECE5DD)  // Classic beige wallpaper tint

// ─── General UI Surfaces & Typography ──────────────────────
val TextPrimary         = Color(0xFF111B21)  // Contact names, prominent headings
val TextSecondary       = Color(0xFF667781)  // Message snippets, timestamps
val DividerColor        = Color(0xFFE9EDEF)  // Subtle divider between chat items
val UnreadBadgeText     = Color(0xFFFFFFFF)  // White count label inside green badge
val SurfaceWhite        = Color(0xFFFFFFFF)  // List rows, card backgrounds
val BackgroundLight     = Color(0xFFF0F2F5)  // App scaffold canvas background
```

---

### ☀️ 2. Light Theme Configuration — `ui/theme/Theme.kt`

Replace the generated `Theme.kt` with WhatsApp's default light color scheme:

```kotlin
package com.example.whatsappclone.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val WhatsAppColorScheme = lightColorScheme(
    primary          = WhatsAppGreen,
    onPrimary        = SurfaceWhite,
    primaryContainer = WhatsAppGreenLight,
    background       = BackgroundLight,
    onBackground     = TextPrimary,
    surface          = SurfaceWhite,
    onSurface        = TextPrimary,
)

@Composable
fun WhatsAppCloneTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = WhatsAppColorScheme,
        content = content
    )
}
```

---

## 📦 Step 2: Data Models & Mock Dataset

### 📐 1. Domain Data Architecture

```text
Contact
├── id              → Unique stable key (e.g., "c1")
├── name            → Display name ("Alice Brown")
├── initials        → Initials for avatar fallback ("AB")
├── avatarColor     → Background tint for circular avatar
└── about           → Status quote ("Living my best life ✨")

ChatItem (Represents one conversation row in the chat list)
├── contact         → Contact domain model
├── lastMessage     → Latest message snippet ("Hey! Are you coming tonight?")
├── timestamp       → Sent/received time string ("9:41 AM", "Yesterday")
├── unreadCount     → Int (0 = no badge; >0 = green circular badge)
└── isOnline        → Boolean indicator for active status

Message (Prepared for Part 2: Chat Detail Screen)
├── id              → Unique message identifier
├── senderId        → ID of sender ("me" or contact ID)
├── text            → Message payload text
├── timestamp       → Sent timestamp
└── isSentByMe      → Boolean (true = right-aligned green, false = left-aligned white)
```

---

### 📝 2. Data Classes — `data/Models.kt`

Create `data/Models.kt` and define the data structures:

```kotlin
package com.example.whatsappclone.data

import androidx.compose.ui.graphics.Color

/**
 * Represents a contact or user profile.
 */
data class Contact(
    val id: String,
    val name: String,
    val initials: String,
    val avatarColor: Color,
    val about: String = "Hey there! I am using WhatsApp."
)

/**
 * Represents a single conversation row displayed in the main chat feed.
 */
data class ChatItem(
    val contact: Contact,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int = 0,
    val isOnline: Boolean = false
)

/**
 * Represents an individual chat bubble message (utilized in Part 2).
 */
data class Message(
    val id: String,
    val senderId: String,
    val text: String,
    val timestamp: String,
    val isSentByMe: Boolean
)
```

---

### 👥 3. Mock Dataset — `data/FakeData.kt`

Create `data/FakeData.kt` containing sample conversations, contacts, and messages:

```kotlin
package com.example.whatsappclone.data

import androidx.compose.ui.graphics.Color

object FakeData {

    // ─── Contacts ──────────────────────────────────────────
    val alice = Contact(
        id = "c1", name = "Alice Brown", initials = "AB",
        avatarColor = Color(0xFF6366F1),
        about = "Living my best life ✨"
    )
    val john = Contact(
        id = "c2", name = "John Doe", initials = "JD",
        avatarColor = Color(0xFFEC4899),
        about = "At work 💼"
    )
    val mom = Contact(
        id = "c3", name = "Mom ❤️", initials = "M",
        avatarColor = Color(0xFFF59E0B),
        about = "Call me when you're free"
    )
    val devTeam = Contact(
        id = "c4", name = "Dev Team 🚀", initials = "DT",
        avatarColor = Color(0xFF10B981),
        about = "Sprint ends Friday"
    )
    val sarah = Contact(
        id = "c5", name = "Sarah Wilson", initials = "SW",
        avatarColor = Color(0xFF8B5CF6),
        about = "Coffee addict ☕"
    )
    val mike = Contact(
        id = "c6", name = "Mike Johnson", initials = "MJ",
        avatarColor = Color(0xFFEF4444),
        about = "Gym rat 💪"
    )
    val emma = Contact(
        id = "c7", name = "Emma Davis", initials = "ED",
        avatarColor = Color(0xFF06B6D4),
        about = "Traveling the world 🌍"
    )
    val family = Contact(
        id = "c8", name = "Family Group 👨‍👩‍👧‍👦", initials = "FG",
        avatarColor = Color(0xFF84CC16),
        about = "Dad: Don't forget Sunday dinner"
    )

    // ─── Chat List (Displayed on main screen) ─────────────
    val chatList = listOf(
        ChatItem(alice,   "Hey! Are you coming tonight?",         "9:41 AM",   unreadCount = 2, isOnline = true),
        ChatItem(john,    "Thanks for the help! 🙏",              "10:15 AM",  unreadCount = 0),
        ChatItem(devTeam, "Alice: PR is ready for review",        "8:30 AM",   unreadCount = 5),
        ChatItem(mom,     "Don't forget to eat your vegetables",  "Yesterday", unreadCount = 0),
        ChatItem(sarah,   "That coffee place was amazing!",       "Yesterday", unreadCount = 1),
        ChatItem(mike,    "Bro, leg day tomorrow 💪",              "Monday",    unreadCount = 0),
        ChatItem(emma,    "Sent you the photos from Bali 📸",     "Monday",    unreadCount = 0),
        ChatItem(family,  "Dad: Sunday dinner at 6pm",            "Sunday",    unreadCount = 3),
    )

    // ─── Messages Preview for Part 2 Chat Screen ───────────
    val aliceMessages = listOf(
        Message("m1",  alice.id, "Hey! How are you?",              "9:30 AM", isSentByMe = false),
        Message("m2",  "me",     "I'm good! Just finished work",   "9:32 AM", isSentByMe = true),
        Message("m3",  alice.id, "Nice! Are you coming tonight?",  "9:35 AM", isSentByMe = false),
        Message("m4",  "me",     "Tonight? What's happening?",     "9:37 AM", isSentByMe = true),
        Message("m5",  alice.id, "Sarah's birthday party! 🎉",     "9:38 AM", isSentByMe = false),
        Message("m6",  alice.id, "At that new rooftop place",      "9:38 AM", isSentByMe = false),
        Message("m7",  "me",     "Oh right! I totally forgot 😅",  "9:40 AM", isSentByMe = true),
        Message("m8",  "me",     "What time does it start?",       "9:40 AM", isSentByMe = true),
        Message("m9",  alice.id, "8pm. I can pick you up!",        "9:41 AM", isSentByMe = false),
        Message("m10", alice.id, "Hey! Are you coming tonight?",   "9:41 AM", isSentByMe = false),
    )
}
```

> **⚡ Performance Rule — Move Constants Outside Composables:**
> `FakeData` is declared as a Kotlin `object` singleton. Its lists and models are created once upon initialization rather than reallocated repeatedly across recomposition cycles.

---

## 👤 Step 3: Reusable Avatar Component

### 🧩 Implementation — `ui/components/AvatarImage.kt`

Every conversation row and profile header displays a circular avatar. Building this as a dedicated stateless composable ensures consistency and lets you substitute network image loaders (like **Coil**) later without modifying consumer screens.

```kotlin
package com.example.whatsappclone.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A circular avatar displaying a contact's initials on a tinted background.
 *
 * Later, you can swap the Box body with Coil's AsyncImage to load
 * network profile photos across all screens automatically.
 */
@Composable
fun AvatarImage(
    initials: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 50.dp,
    fontSize: Int = 18
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)           // 📌 clip BEFORE background ensures circular fill
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
```

---

### 🔍 Modifier Order Deep-Dive

> **⚠️ Critical Modifier Sequence Rule:**
> Notice that `.clip(CircleShape)` is placed **before** `.background(backgroundColor)`.
> 
> In Compose, modifiers chain sequentially from outer to inner:
> 1. `Modifier.size(size)` establishes a 50×50 dp box.
> 2. `.clip(CircleShape)` masks subsequent drawing operations to a circle.
> 3. `.background(...)` paints the color inside the clipped circular path.
> 
> *If you reverse the order (`background` before `clip`), Compose paints a square background first, rendering the clipping visually ineffective!*

---

## 📑 Step 4: WhatsApp Tab Bar (Chats / Status / Calls)

### 📐 Tab Layout Structure

```text
Row (fillMaxWidth, background = WhatsAppGreen)
 ├── Tab "CHATS"    (weight 1f, centered, white underline + badge)
 ├── Tab "STATUS"   (weight 1f, centered)
 └── Tab "CALLS"    (weight 1f, centered)
```

---

### 🏷️ Implementation — `ui/chatlist/WhatsAppTabs.kt`

Create `ui/chatlist/WhatsAppTabs.kt`:

```kotlin
package com.example.whatsappclone.ui.chatlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsappclone.ui.theme.WhatsAppGreen

// Enumeration of all available top-level tabs
enum class WhatsAppTab {
    CHATS, STATUS, CALLS
}

@Composable
fun WhatsAppTabs(
    selectedTab: WhatsAppTab,                   // ⬇️ State down
    onTabSelected: (WhatsAppTab) -> Unit,       // ⬆️ Event up
    unreadChatCount: Int = 3                    // Aggregate unread badge count
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(WhatsAppGreen),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TabItem(
            label = "CHATS",
            selected = selectedTab == WhatsAppTab.CHATS,
            onClick = { onTabSelected(WhatsAppTab.CHATS) },
            badgeCount = if (unreadChatCount > 0) unreadChatCount else 0
        )
        TabItem(
            label = "STATUS",
            selected = selectedTab == WhatsAppTab.STATUS,
            onClick = { onTabSelected(WhatsAppTab.STATUS) }
        )
        TabItem(
            label = "CALLS",
            selected = selectedTab == WhatsAppTab.CALLS,
            onClick = { onTabSelected(WhatsAppTab.CALLS) }
        )
    }
}

@Composable
private fun TabItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    badgeCount: Int = 0
) {
    Column(
        modifier = Modifier
            .weight(1f)                          // 📌 Distribute tabs equally across screen width
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tab label with optional unread badge
        if (badgeCount > 0) {
            BadgedBox(
                badge = {
                    Badge(
                        containerColor = Color.White,
                        contentColor = WhatsAppGreen
                    ) {
                        Text(
                            text = "$badgeCount",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            ) {
                TabLabel(label = label, selected = selected)
            }
        } else {
            TabLabel(label = label, selected = selected)
        }

        // Active tab underline indicator
        if (selected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)          // Underline spans 50% of tab width
                    .height(3.dp)
                    .background(Color.White)
            )
        }
    }
}

@Composable
private fun TabLabel(label: String, selected: Boolean) {
    Text(
        text = label,
        color = if (selected) Color.White else Color.White.copy(alpha = 0.7f),
        fontSize = 14.sp,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        letterSpacing = 1.sp
    )
}
```

---

### 🔄 State Hoisting in the Tab Bar

> **💡 State Hoisting Concept:**
> `WhatsAppTabs` owns **no** internal state. It takes `selectedTab: WhatsAppTab` as an immutable parameter and fires `onTabSelected(tab)` whenever the user interacts with a tab item. The parent root composable (`WhatsAppApp`) retains ownership of the state and dictates recomposition.

---

## 💬 Step 5: Chat Item Row (Conversation Cell)

### 📐 Nested Row + Column Layout Structure

```text
Row (verticalAlignment = CenterVertically, padding = 16.dp horizontal)
│
├── AvatarImage (52.dp circle)
│
├── Spacer (14.dp width)
│
└── Column (weight = 1f)  ← Expands to absorb remaining horizontal width
    │
    ├── Row (SpaceBetween, fillMaxWidth)
    │   ├── Text(name)         ← Bold, dark, weight(1f), ellipsis
    │   └── Text(timestamp)    ← Gray / Green, small 12.sp
    │
    ├── Spacer (3.dp height)
    │
    └── Row (SpaceBetween, fillMaxWidth)
        ├── Text(lastMessage)  ← Gray, 14.sp, weight(1f), ellipsis
        └── UnreadBadge        ← Green pill circle with white count (if count > 0)
```

---

### 🔍 Visual Cell Breakdown

```text
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│  ┌──────┐  Alice Brown                          9:41 AM    │
│  │  AB  │  Hey! Are you coming tonight?              ②     │
│  └──────┘                                                 │
│  ─────────────────────────────────────────────────────────  │  ← Indented Divider (starts at 80dp)
│                                                             │
│  ┌──────┐  John Doe                             10:15 AM   │
│  │  JD  │  Thanks for the help! 🙏                        │
│  └──────┘                                                 │
│  ─────────────────────────────────────────────────────────  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

### 💻 Implementation — `ui/chatlist/ChatItemRow.kt`

Create `ui/chatlist/ChatItemRow.kt`:

```kotlin
package com.example.whatsappclone.ui.chatlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsappclone.data.ChatItem
import com.example.whatsappclone.ui.components.AvatarImage
import com.example.whatsappclone.ui.theme.DividerColor
import com.example.whatsappclone.ui.theme.TextPrimary
import com.example.whatsappclone.ui.theme.TextSecondary
import com.example.whatsappclone.ui.theme.UnreadBadgeText
import com.example.whatsappclone.ui.theme.WhatsAppGreenLight

@Composable
fun ChatItemRow(
    chat: ChatItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)    // 📌 The full cell width is interactive
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ─── LEFT: Avatar ──────────────────────────────
            AvatarImage(
                initials = chat.contact.initials,
                backgroundColor = chat.contact.avatarColor,
                size = 52.dp,
                fontSize = 18
            )

            Spacer(modifier = Modifier.width(14.dp))

            // ─── RIGHT: Name + Message + Time + Badge ──────
            // 📌 weight(1f) ensures this Column consumes all remaining width
            // after the avatar and spacer consume their fixed dimensions.
            Column(modifier = Modifier.weight(1f)) {

                // Top row: Contact Name (left) + Timestamp (right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chat.contact.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)  // 📌 Name truncates before timestamp clips
                    )

                    Text(
                        text = chat.timestamp,
                        fontSize = 12.sp,
                        color = if (chat.unreadCount > 0)
                            WhatsAppGreenLight          // Highlight time green when messages are unread
                        else
                            TextSecondary
                    )
                }

                Spacer(modifier = Modifier.size(3.dp))

                // Bottom row: Last Message snippet (left) + Unread Badge (right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chat.lastMessage,
                        fontSize = 14.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)  // 📌 Message truncates, preserving badge
                    )

                    // Unread badge: only emitted when unreadCount > 0
                    if (chat.unreadCount > 0) {
                        UnreadBadge(count = chat.unreadCount)
                    }
                }
            }
        }

        // Indented divider line: aligned with text start (52dp avatar + 14dp spacer + 14dp offset ≈ 80dp)
        HorizontalDivider(
            thickness = 0.5.dp,
            color = DividerColor,
            modifier = Modifier.padding(start = 80.dp)
        )
    }
}

@Composable
private fun UnreadBadge(count: Int) {
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(WhatsAppGreenLight),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$count",
            color = UnreadBadgeText,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
```

---

### 📊 Layout Mechanics & Weight Distribution

1. **Nested Rows & Columns:**
   - The outer `Row` positions the fixed `AvatarImage` against the expanding `Column`.
   - The inner `Column` stacks two child `Row` layouts (one for headers, one for body).
2. **Three-fold `weight(1f)` Application:**
   - **Content Column:** `weight(1f)` in the outer `Row` fills all available width up to the right screen edge.
   - **Name Text:** `weight(1f)` in the top inner `Row` prevents long names (e.g., *"Family Group 👨‍👩‍👧‍👦"*) from pushing the timestamp off-screen. The name truncates cleanly with an ellipsis.
   - **Message Text:** `weight(1f)` in the bottom inner `Row` prevents extended message previews from squeezing or clipping the circular unread badge.

---

## 📱 Step 6: Full Chat List Screen

The `ChatListScreen` ties together the `TopAppBar`, the scrollable `LazyColumn`, the floating action button, and unread counts.

### 💻 Implementation — `ui/chatlist/ChatListScreen.kt`

Create `ui/chatlist/ChatListScreen.kt`:

```kotlin
package com.example.whatsappclone.ui.chatlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsappclone.data.ChatItem
import com.example.whatsappclone.data.FakeData
import com.example.whatsappclone.ui.theme.BackgroundLight
import com.example.whatsappclone.ui.theme.WhatsAppGreen
import com.example.whatsappclone.ui.theme.WhatsAppGreenLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    onChatClick: (ChatItem) -> Unit,                          // ⬆️ Event up
    chats: List<ChatItem> = FakeData.chatList                 // ⬇️ Data down
) {
    // 📌 remember(chats): Recalculate total unread count only when the chats list changes
    val totalUnread = remember(chats) {
        chats.sumOf { it.unreadCount }
    }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            WhatsAppTopBar()
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Open new contact picker — Part 2 */ },
                containerColor = WhatsAppGreenLight,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Filled.Chat,
                    contentDescription = "New chat",
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ─── CONVERSATION FEED ─────────────────────────
            // 📌 LazyColumn: Recycles composables, emitting only visible chat items
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                items(
                    items = chats,
                    key = { chat -> chat.contact.id }   // 📌 Stable key enables smart recomposition
                ) { chat ->
                    ChatItemRow(
                        chat = chat,
                        onClick = { onChatClick(chat) }
                    )
                }
            }
        }
    }
}

// ─── WHATSAPP TOP APP BAR ──────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WhatsAppTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "WhatsApp",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        actions = {
            IconButton(onClick = { /* Open Camera */ }) {
                Icon(
                    Icons.Filled.PhotoCamera,
                    contentDescription = "Camera",
                    tint = Color.White
                )
            }
            IconButton(onClick = { /* Open Search */ }) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = Color.White
                )
            }
            IconButton(onClick = { /* Open Menu */ }) {
                Icon(
                    Icons.Filled.MoreVert,
                    contentDescription = "More options",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = WhatsAppGreen
        )
    )
}
```

---

## 🔌 Step 7: Wiring Everything Together

### 1. Temporary Screen — `ui/PlaceholderScreen.kt`

Create `ui/PlaceholderScreen.kt` to handle the Status and Calls tabs cleanly:

```kotlin
package com.example.whatsappclone.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.whatsappclone.ui.theme.BackgroundLight
import com.example.whatsappclone.ui.theme.TextSecondary

@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$title\n(coming soon)",
            color = TextSecondary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
```

---

### 2. Root App Composable — `WhatsAppApp.kt`

`WhatsAppApp` maintains the selected tab state, mounts the persistent `WhatsAppTabs`, and displays the active screen:

```kotlin
package com.example.whatsappclone

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.whatsappclone.data.FakeData
import com.example.whatsappclone.ui.PlaceholderScreen
import com.example.whatsappclone.ui.chatlist.ChatListScreen
import com.example.whatsappclone.ui.chatlist.WhatsAppTab
import com.example.whatsappclone.ui.chatlist.WhatsAppTabs
import com.example.whatsappclone.ui.theme.WhatsAppGreen

@Composable
fun WhatsAppApp() {
    val context = LocalContext.current

    // 📌 State Hoisted to Root: WhatsAppApp governs the active tab destination
    var selectedTab by remember { mutableStateOf(WhatsAppTab.CHATS) }

    // 📌 Total unread count dynamically calculated for the CHATS tab badge
    val totalUnread = remember { FakeData.chatList.sumOf { it.unreadCount } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhatsAppGreen)   // Maintains consistent brand background behind tabs
    ) {
        // ─── PERSISTENT TAB BAR ────────────────────────────
        WhatsAppTabs(
            selectedTab = selectedTab,                     // ⬇️ State down
            onTabSelected = { selectedTab = it },          // ⬆️ Event up
            unreadChatCount = totalUnread
        )

        // ─── ACTIVE SCREEN CONTENT ─────────────────────────
        when (selectedTab) {
            WhatsAppTab.CHATS -> ChatListScreen(
                onChatClick = { chat ->
                    // Part 2 will navigate to the full Chat Detail Screen
                    Toast.makeText(
                        context,
                        "Open conversation with ${chat.contact.name}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
            WhatsAppTab.STATUS -> PlaceholderScreen("Status")
            WhatsAppTab.CALLS  -> PlaceholderScreen("Calls")
        }
    }
}
```

---

### 3. Activity Entry Point — `MainActivity.kt`

Configure edge-to-edge support and launch the application theme:

```kotlin
package com.example.whatsappclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.whatsappclone.ui.theme.WhatsAppCloneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enables edge-to-edge drawing under system bars
        enableEdgeToEdge()
        setContent {
            WhatsAppCloneTheme {
                WhatsAppApp()
            }
        }
    }
}
```

---

## 💬 Deep Dive: How Chat Bubbles Work (Part 2 Preview)

Before concluding Part 1, let's explore the architectural layout of WhatsApp chat bubbles, which we will fully implement in Part 2.

### 🖼️ Visual Comparison: Sent vs. Received

```text
Sent message (by me):                    Received message (by them):

                              ┌──────────────────────┐
                              │ Hey! How are you?    │  ← White bubble
                              │ 9:30 AM              │     Aligned LEFT
                              └──────────────────────┘

    ┌──────────────────────┐
    │ I'm good! Just       │  ← Light-green bubble
    │ finished work        │     Aligned RIGHT
    │ 9:32 AM              │
    └──────────────────────┘
```

---

### 🧩 Bubble Implementation Mechanics

A WhatsApp chat bubble relies on three layout properties:
1. **`horizontalArrangement`**: Aligns sent messages to `Arrangement.End` (right) and received messages to `Arrangement.Start` (left).
2. **`widthIn(max = 280.dp)`**: Restricts the bubble from spanning the full screen width.
3. **Asymmetric Corner Radii (`RoundedCornerShape`)**: Creates the characteristic "speech tail" by zeroing out the bottom-right corner for sent messages (`bottomEnd = 0.dp`) or bottom-left for received messages (`bottomStart = 0.dp`).

```kotlin
// Architectural Preview for Part 2
@Composable
fun MessageBubble(message: Message) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isSentByMe)
            Arrangement.End
        else
            Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    color = if (message.isSentByMe) BubbleSent else BubbleReceived,
                    shape = RoundedCornerShape(
                        topStart = 8.dp,
                        topEnd = 8.dp,
                        bottomStart = if (message.isSentByMe) 8.dp else 0.dp, // Tail left
                        bottomEnd = if (message.isSentByMe) 0.dp else 8.dp    // Tail right
                    )
                )
                .padding(8.dp)
        ) {
            Column {
                Text(text = message.text, color = TextPrimary)
                Text(
                    text = message.timestamp,
                    fontSize = 10.sp,
                    color = TextSecondary,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
```

---

### 📋 Summary of Bubble Differences

| Property | Sent (By Me) | Received (By Contact) |
| :--- | :--- | :--- |
| **Row Alignment** | `Arrangement.End` (pushed to right edge) | `Arrangement.Start` (pushed to left edge) |
| **Bubble Background** | `BubbleSent` (`0xFFDCF8C6` light green) | `BubbleReceived` (`0xFFFFFFFF` pure white) |
| **Tail Shape** | `bottomEnd = 0.dp` (flat lower right) | `bottomStart = 0.dp` (flat lower left) |
| **Domain Trigger** | `message.isSentByMe == true` | `message.isSentByMe == false` |

---

## 🧠 Jetpack Compose Principles Applied: Where & Why

| Concept | Location in Code | Engineering Rationale |
| :--- | :--- | :--- |
| **`remember { mutableStateOf }`** | `WhatsAppApp.selectedTab` | Retains active tab selection across recomposition cycles without state loss. |
| **State Hoisting** | `WhatsAppTabs`, `ChatItemRow` | Keeps individual UI rows and tab bars stateless; events bubble upward to container. |
| **`LazyColumn`** | `ChatListScreen` | Only renders currently visible conversations, recycling list rows efficiently. |
| **`key = { chat.contact.id }`** | `LazyColumn` items block | Provides stable item identity, allowing Compose to skip recomposing unaffected rows. |
| **Three-Tier `weight(1f)`** | `ChatItemRow`, `WhatsAppTabs` | Distributes tab widths equally, expands message column, and prevents timestamp clipping. |
| **Modifier Sequencing** | `AvatarImage`, `UnreadBadge` | `.clip(CircleShape)` before `.background()` guarantees strict circular bounds. |
| **Nested Row + Column** | `ChatItemRow` | Establishes a responsive two-column, two-row information grid. |
| **`TextOverflow.Ellipsis`** | Contact name & message preview | Truncates long text strings with "…" rather than pushing adjacent metadata off-screen. |
| **`remember(chats)`** | `totalUnread` calculation | Caches aggregate sum calculation, preventing recalculation on every recomposition. |
| **Constants Outside Composable** | `FakeData` singleton | Prevents redundant object allocations on the garbage-collected heap during frames. |
| **`HorizontalDivider` Offset** | `ChatItemRow` | Matches WhatsApp's native design where dividers align with the text, skipping the avatar. |

---

## 🧪 Self-Assessment & Knowledge Check

Test your understanding of the architecture and layout techniques utilized in Part 1:

### 1. In `ChatItemRow`, why is `modifier.weight(1f)` applied to the contact name `Text` inside the top inner `Row`?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
Without `weight(1f)`, a lengthy contact name (e.g., *"Dev Team 🚀 - Android Core Engineers"*) would expand to fill the entire horizontal width, forcing the timestamp `Text` completely off the right edge of the screen. With `weight(1f)` and `maxLines = 1`, the name absorbs all available space up to the timestamp, cleanly truncating with an ellipsis (`...`) while guaranteeing the timestamp remains fully visible.
</details>

---

### 2. What visual bug occurs if you define `Modifier.background(color).clip(CircleShape)` on an `AvatarImage`?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
Because Compose modifiers are evaluated in sequential order, `.background(color)` draws a colored rectangle filling the entire bounding box first. Applying `.clip(CircleShape)` afterwards clips subsequent child content (the text), but does not alter the already-rendered rectangular background canvas.
</details>

---

### 3. Why is `key = { chat.contact.id }` preferable to using list index in `items(items = chats)`?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
Using list indices (`key = { index }`) causes visual bugs and performance penalties if chats are reordered (e.g., when a new message arrives and moves a conversation to the top). Stable unique keys (`chat.contact.id`) allow the Compose runtime to track items across re-orderings, preserving item animations and skipping recomposition for unchanged rows.
</details>

---

### 4. How does `BadgedBox` position the unread badge over the "CHATS" tab label?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
`BadgedBox` is a Material 3 container that accepts a primary content composable (`TabLabel`) and a `badge` slot (`Badge`). It automatically aligns the badge to the top-end (top-right in LTR languages) corner of the anchor content with standard Material elevation and spacing.
</details>

---

### 5. Why do we wrap the unread calculation in `val totalUnread = remember(chats) { chats.sumOf { it.unreadCount } }`?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
Iterating through the list using `.sumOf { ... }` runs in $O(N)$ time. Wrapping it in `remember(chats)` guarantees the sum is only recalculated when the `chats` collection reference changes, avoiding redundant CPU work on every recomposition.
</details>

---

## 🏁 Checkpoint: What You Should Have Working

Before proceeding to Part 2, verify that your application satisfies the following milestones:

- [x] **Top App Bar:** Renders dark green (`0xFF075E54`) with "WhatsApp" title, camera icon, search icon, and vertical overflow menu.
- [x] **Tabs:** Displays "CHATS", "STATUS", and "CALLS" with equal widths; the active tab features a white underline indicator.
- [x] **Badges:** The "CHATS" tab displays a white pill badge containing the aggregate count of all unread messages.
- [x] **Avatars:** Circular avatars display bold white initials on a tinted background.
- [x] **Cell Layout:** Each row features contact name, message preview, timestamp, and green unread badge.
- [x] **Indented Divider:** Horizontal dividers start 80dp from the left, directly below the text column.
- [x] **FAB:** A circular emerald-green floating action button with a chat bubble icon sits in the bottom-right corner.
- [x] **Interactions:** Tapping any conversation row displays a Toast with the contact's name. Tapping tabs switches views smoothly.

---

## 🏋️ Hands-On Exercises Before Part 2

Accelerate your Compose engineering skills by completing these practical challenges:

### 🎯 Exercise 1: Real-Time "typing..." Indicator
Modify `ChatItem` to support typing state. If a contact is typing, display the last message as `"typing..."` styled in italic green text (`WhatsAppGreenLight`).
> **Hint:**
> ```kotlin
> Text(
>     text = if (chat.isTyping) "typing..." else chat.lastMessage,
>     color = if (chat.isTyping) WhatsAppGreenLight else TextSecondary,
>     fontStyle = if (chat.isTyping) FontStyle.Italic else FontStyle.Normal
> )
> ```

---

### 🎯 Exercise 2: Outgoing Delivery Status Checks (✓✓)
Add a boolean flag `isLastMessageFromMe: Boolean` and an enum `DeliveryStatus { SENT, DELIVERED, READ }` to `ChatItem`. If the last message was sent by you, display a checkmark icon right before the message preview:
- Single check (`Icons.Filled.Done`) in gray for `SENT`.
- Double check (`Icons.Filled.DoneAll`) in gray for `DELIVERED`.
- Double check (`Icons.Filled.DoneAll`) in cyan/blue (`Color(0xFF53BDEB)`) for `READ`.

---

### 🎯 Exercise 3: Avatar Online Presence Dot
Display an 11dp bright-green circular badge on the bottom-right corner of the avatar if `chat.isOnline == true`.
> **Hint:** Wrap `AvatarImage` inside a `Box`, and place an additional circular `Box` with `Alignment.BottomEnd` modifier:
> ```kotlin
> Box(modifier = Modifier.size(52.dp)) {
>     AvatarImage(...)
>     if (chat.isOnline) {
>         Box(
>             modifier = Modifier
>                 .size(12.dp)
>                 .align(Alignment.BottomEnd)
>                 .clip(CircleShape)
>                 .background(WhatsAppGreenLight)
>         )
>     }
> }
> ```