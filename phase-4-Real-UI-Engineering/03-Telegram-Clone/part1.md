# ✈️ Telegram UI Clone — Part 1: Searchable Chat List, Real-Time Filtering & Scroll Detection

> **🎯 What You Will Build:** A production-grade Telegram Android UI in Jetpack Compose featuring a custom Telegram-blue theme, a lightweight `BasicTextField` search bar with real-time reactive filtering, a state-hoisted chat list with empty-state feedback, and a foundational introduction to scroll position tracking using `LazyListState` and `derivedStateOf`.

---

## 📋 Table of Contents

- [Overview — What's Different from WhatsApp](#-overview--whats-different-from-whatsapp)
  - [Visual Layout Architecture](#-visual-layout-architecture)
  - [New Core Skills Mastered in This Module](#-new-core-skills-mastered-in-this-module)
- [Step 0: Project Setup & Dependencies](#-step-0-project-setup--dependencies)
  - [1. Create the Project](#-1-create-the-project)
  - [2. Add Dependencies — app/build.gradle.kts](#-2-add-dependencies--appbuildgradlekts)
  - [3. Target File Structure](#-3-target-file-structure)
- [Step 1: Telegram Design System & Theme](#-step-1-telegram-design-system--theme)
  - [1. Color Definitions — ui/theme/Color.kt](#-1-color-definitions--uithemecolorkt)
  - [2. Theme Configuration — ui/theme/Theme.kt](#-2-theme-configuration--uithemethemekt)
- [Step 2: Domain Data Models & Mock Dataset](#-step-2-domain-data-models--mock-dataset)
  - [1. Domain Models — data/Models.kt](#-1-domain-models--datamodelskt)
  - [2. Mock Dataset — data/FakeData.kt](#-2-mock-dataset--datafakedatakt)
- [Step 3: Reusable Circular Avatar Component](#-step-3-reusable-circular-avatar-component)
  - [Implementation — ui/components/AvatarImage.kt](#-implementation--uicomponentsavatarimagekt)
- [Step 4: The Telegram Search Bar (Real-Time Filtering Engine)](#-step-4-the-telegram-search-bar-real-time-filtering-engine)
  - [The Reactive Search Theory & Step-by-Step Flow](#-the-reactive-search-theory--step-by-step-flow)
  - [Why remember(searchQuery) Matters for Performance](#-why-remembersearchquery-matters-for-performance)
  - [Implementation — ui/chatlist/TelegramSearchBar.kt](#-implementation--uichatlisttelegramsearchbarkt)
  - [Deep Dive: BasicTextField vs. Material TextField](#-deep-dive-basictextfield-vs-material-textfield)
- [Step 5: Telegram Chat Item Row](#-step-5-telegram-chat-item-row)
  - [Implementation — ui/chatlist/ChatItemRow.kt](#-implementation--uichatlistchatitemrowkt)
- [Step 6: The Searchable Chat List Screen](#-step-6-the-searchable-chat-list-screen)
  - [Implementation — ui/chatlist/ChatListScreen.kt](#-implementation--uichatlistchatlistscreenkt)
  - [The Complete Unidirectional Data Flow](#-the-complete-unidirectional-data-flow)
- [Step 7: Wiring Everything Together](#-step-7-wiring-everything-together)
  - [Root Composable — TelegramApp.kt](#-root-composable--telegramappkt)
  - [Activity Entry Point — MainActivity.kt](#-activity-entry-point--mainactivitykt)
- [Step 8: Architectural Preview — Scroll Detection & Floating Jump Button](#-step-8-architectural-preview--scroll-detection--floating-jump-button)
  - [The Problem with Direct Scroll Observation](#-the-problem-with-direct-scroll-observation)
  - [The Solution: LazyListState & derivedStateOf](#-the-solution-lazyliststate--derivedstateof)
  - [Preview Implementation Pattern](#-preview-implementation-pattern)
- [Step 9: Architectural Preview — List Item Animations (Modifier.animateItem)](#-step-9-architectural-preview--list-item-animations-modifieranimateitem)
  - [The Evolution of Compose List Animations](#-the-evolution-of-compose-list-animations)
- [🧠 Jetpack Compose Principles Applied: Where & Why](#-jetpack-compose-principles-applied-where--why)
- [🧪 Self-Assessment & Knowledge Check](#-self-assessment--knowledge-check)
- [🏁 Checkpoint: What You Should Have Working](#-checkpoint-what-you-should-have-working)
- [🏋️ Hands-On Coding Exercises](#-hands-on-coding-exercises)
- [🚀 Looking Ahead: Part 2 (The Interactive Chat Screen)](#-looking-ahead-part-2-the-interactive-chat-screen)

---

## 📱 Overview — What's Different from WhatsApp

In the WhatsApp Clone, you mastered foundational list recycling, state hoisting across tabs, and responsive layout constraints using weights and padding. 

In **Telegram UI Clone Part 1**, we build upon that foundation by introducing three essential mobile engineering skills required for top-tier product apps:
1. **Reactive Search Filtering:** Subscribing UI lists to search input with high-performance caching (`remember(searchQuery)`).
2. **High-Frequency Scroll Tracking:** Monitoring viewport positions using `LazyListState` and `derivedStateOf` to prevent frame drops.
3. **Custom Low-Level Text Inputs:** Engineering streamlined search bars with `BasicTextField` and custom decoration boxes.

### 🖼️ Visual Layout Architecture

```text
┌──────────────────────────────────────┐
│  ☰  Telegram              🔍        │  ← Top App Bar (Hamburger Menu + Search Icon)
├──────────────────────────────────────┤
│  🔍  Search chats...          ✕     │  ← Custom BasicTextField Search Bar
├──────────────────────────────────────┤
│  ┌────┐                              │
│  │ AB │ Alice Brown          9:41 AM │  ← Dynamic filtered list item
│  └────┘ Hey! Are you coming…     ②  │
│  ────────────────────────────────    │
│  ┌────┐                              │  ← Rows filter in REAL TIME:
│  │ JD │ John Doe            10:15 AM │     • "ali" → Only Alice shows
│  └────┘ Thanks for the help!        │     • "dev" → Only Dev Team shows
│  ────────────────────────────────    │     • ""    → All chats show
│  ... more conversations ...          │
│                                      │
│                                ✏️    │  ← Floating Action Button (New Message)
└──────────────────────────────────────┘
```

---

### 🆕 New Core Skills Mastered in This Module

```text
WHATSAPP FOUNDATIONS (Mastered):
  ✅ LazyColumn with sticky keys
  ✅ State hoisting for tab switching
  ✅ Avatar + name + message + unread badge layout

TELEGRAM ENGINEERING (New in This Part):
  🆕 Real-time multi-field search filtering (name + message)
  🆕 Performance caching with multi-key remember(query, items)
  🆕 Custom text decoration with BasicTextField
  🆕 High-frequency scroll observation with derivedStateOf
```

---

## 🛠️ Step 0: Project Setup & Dependencies

### 🆕 1. Create the Project

Initialize a new project inside Android Studio:

```text
Android Studio → New Project → "Empty Activity" (Compose)
Name: TelegramClone
Package name: com.example.telegramclone
Minimum SDK: 24 (Android 7.0 Nougat)
Build configuration language: Kotlin DSL (build.gradle.kts)
```

---

### 📦 2. Add Dependencies — `app/build.gradle.kts`

Open `app/build.gradle.kts` and ensure the extended Material icons library is present inside `dependencies { ... }`:

```kotlin
dependencies {
    // ... existing Compose BOM dependencies from template ...

    // Extended Material Icons (Search, Close, Menu, Edit, DoneAll, etc.)
    implementation("androidx.compose.material:material-icons-extended")
}
```

> **📌 Note:** Because the project uses the Compose BOM (`platform(libs.androidx.compose.bom)`), you do not need to specify a version number. Click **Sync Now**.

---

### 📁 3. Target File Structure

Organize the project cleanly into the following structure:

```text
com.example.telegramclone/
├── MainActivity.kt                  ← Application Activity entry point
├── TelegramApp.kt                   ← Root application composable
├── data/
│   ├── Models.kt                    ← Contact, ChatItem, Message data classes
│   └── FakeData.kt                  ← Static mock conversations & contacts
└── ui/
    ├── theme/
    │   ├── Color.kt                 ← Telegram bright blue palette
    │   └── Theme.kt                 ← Light theme configuration
    ├── components/
    │   └── AvatarImage.kt           ← Reusable circular avatar
    └── chatlist/
        ├── ChatItemRow.kt           ← Telegram-styled chat list row
        ├── TelegramSearchBar.kt     ← Custom BasicTextField search bar
        └── ChatListScreen.kt        ← Master searchable chat list container
```

---

## 🎨 Step 1: Telegram Design System & Theme

Telegram features a vibrant, energetic aesthetic: sky-blue top app bars (`#2AABEE`), light-gray input surfaces (`#F4F4F5`), crisp white list canvases, and emerald green unread badges (`#4DCD5E`).

---

### 🌈 1. Color Definitions — `ui/theme/Color.kt`

Create `ui/theme/Color.kt`:

```kotlin
package com.example.telegramclone.ui.theme

import androidx.compose.ui.graphics.Color

// ─── Telegram Brand Palette ────────────────────────────────
val TelegramBlue        = Color(0xFF2AABEE)  // Top app bar, active links, accents
val TelegramBlueDark    = Color(0xFF1E96D1)  // Status bar background
val TelegramBlueLight   = Color(0xFFE3F2FD)  // Search bar background tint
val TelegramGreen       = Color(0xFF4DCD5E)  // Online indicator dot, unread count pill

// ─── Chat Bubbles (Prepared for Part 2) ───────────────────
val BubbleSent          = Color(0xFFEEFFDE)  // Soft light green (my sent messages)
val BubbleReceived      = Color(0xFFFFFFFF)  // Pure white (received messages)
val ChatBackground      = Color(0xFF8EB5C7)  // Default Telegram wallpaper blue-gray tint

// ─── General UI & Surfaces ─────────────────────────────────
val TextPrimary         = Color(0xFF000000)  // Contact names, primary titles
val TextSecondary       = Color(0xFF707579)  // Last message snippet, timestamps
val DividerColor        = Color(0xFFE0E0E0)  // Subtle row dividers
val SurfaceWhite        = Color(0xFFFFFFFF)  // Screen canvas background
val BackgroundLight     = Color(0xFFFFFFFF)  // Pure white list background
val SearchBarGray       = Color(0xFFF4F4F5)  // Light gray search input field pill
```

---

### ☀️ 2. Theme Configuration — `ui/theme/Theme.kt`

Create `ui/theme/Theme.kt`:

```kotlin
package com.example.telegramclone.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val TelegramColorScheme = lightColorScheme(
    primary      = TelegramBlue,
    onPrimary    = SurfaceWhite,
    background   = BackgroundLight,
    onBackground = TextPrimary,
    surface      = SurfaceWhite,
    onSurface    = TextPrimary,
)

@Composable
fun TelegramCloneTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TelegramColorScheme,
        content = content
    )
}
```

---

## 📦 Step 2: Domain Data Models & Mock Dataset

### 📝 1. Domain Models — `data/Models.kt`

Create `data/Models.kt`:

```kotlin
package com.example.telegramclone.data

import androidx.compose.ui.graphics.Color

/**
 * Represents a Telegram contact or channel author.
 */
data class Contact(
    val id: String,
    val name: String,
    val initials: String,
    val avatarColor: Color,
    val isOnline: Boolean = false
)

/**
 * Represents a conversation item displayed in the main feed.
 */
data class ChatItem(
    val contact: Contact,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int = 0
)

/**
 * Represents a single message bubble (used in Part 2).
 */
data class Message(
    val id: String,
    val text: String,
    val timestamp: String,
    val isSentByMe: Boolean
)
```

---

### 👥 2. Mock Dataset — `data/FakeData.kt`

Create `data/FakeData.kt`:

```kotlin
package com.example.telegramclone.data

import androidx.compose.ui.graphics.Color

object FakeData {

    val contacts = listOf(
        Contact("c1",  "Alice Brown",       "AB", Color(0xFF6366F1), isOnline = true),
        Contact("c2",  "John Doe",          "JD", Color(0xFFEC4899)),
        Contact("c3",  "Dev Team 🚀",       "DT", Color(0xFF10B981), isOnline = true),
        Contact("c4",  "Sarah Wilson",      "SW", Color(0xFF8B5CF6)),
        Contact("c5",  "Mom ❤️",            "M",  Color(0xFFF59E0B)),
        Contact("c6",  "Mike Johnson",      "MJ", Color(0xFFEF4444)),
        Contact("c7",  "Emma Davis",        "ED", Color(0xFF06B6D4), isOnline = true),
        Contact("c8",  "Telegram Tips",     "TT", Color(0xFF2AABEE)),
        Contact("c9",  "David Kim",         "DK", Color(0xFF84CC16)),
        Contact("c10", "Anna Martinez",    "AM", Color(0xFFF97316)),
    )

    val chatList = listOf(
        ChatItem(contacts[0], "Hey! Are you coming tonight?",     "9:41 AM",   unreadCount = 2),
        ChatItem(contacts[1], "Thanks for the help! 🙏",          "10:15 AM"),
        ChatItem(contacts[2], "Alice: PR is ready for review",    "8:30 AM",   unreadCount = 5),
        ChatItem(contacts[3], "That coffee place was amazing!",   "Yesterday", unreadCount = 1),
        ChatItem(contacts[4], "Don't forget to eat vegetables",   "Yesterday"),
        ChatItem(contacts[5], "Bro, leg day tomorrow 💪",          "Monday"),
        ChatItem(contacts[6], "Sent you the photos from Bali 📸", "Monday"),
        ChatItem(contacts[7], "New feature: scheduled messages",  "Sunday",    unreadCount = 1),
        ChatItem(contacts[8], "See you at the conference!",       "Saturday"),
        ChatItem(contacts[9], "The recipe turned out great!",     "Friday"),
    )

    val aliceMessages = listOf(
        Message("m1",  "Hey! How are you?",              "9:30 AM", isSentByMe = false),
        Message("m2",  "I'm good! Just finished work",   "9:32 AM", isSentByMe = true),
        Message("m3",  "Nice! Are you coming tonight?",  "9:35 AM", isSentByMe = false),
        Message("m4",  "Tonight? What's happening?",     "9:37 AM", isSentByMe = true),
        Message("m5",  "Sarah's birthday party! 🎉",     "9:38 AM", isSentByMe = false),
        Message("m6",  "At that new rooftop place",      "9:38 AM", isSentByMe = false),
        Message("m7",  "Oh right! I totally forgot 😅",  "9:40 AM", isSentByMe = true),
        Message("m8",  "What time does it start?",       "9:40 AM", isSentByMe = true),
        Message("m9",  "8pm. I can pick you up!",        "9:41 AM", isSentByMe = false),
        Message("m10", "Hey! Are you coming tonight?",   "9:41 AM", isSentByMe = false),
    )
}
```

---

## 👤 Step 3: Reusable Circular Avatar Component

### 🧩 Implementation — `ui/components/AvatarImage.kt`

Create `ui/components/AvatarImage.kt`:

```kotlin
package com.example.telegramclone.ui.components

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

@Composable
fun AvatarImage(
    initials: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 52.dp,
    fontSize: Int = 18
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)           // 📌 Clip before background ensures circular render
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

## 🔍 Step 4: The Telegram Search Bar (Real-Time Filtering Engine)

### The Reactive Search Theory & Step-by-Step Flow

Search filtering in modern declarative UI connects a continuous state mutation with an optimized list transform:

```text
┌────────────────────────────────────────────────────────┐
│ STEP 1: Full dataset of 10 items                      │
│ allChats = [Alice, John, Dev Team, Sarah, Mom, ...]   │
└────────────────────────────────────────────────────────┘
                           │
                           ▼
┌────────────────────────────────────────────────────────┐
│ STEP 2: User types "ali" into the search bar           │
│ searchQuery = "ali"                                    │
└────────────────────────────────────────────────────────┘
                           │
                           ▼
┌────────────────────────────────────────────────────────┐
│ STEP 3: Multi-key remember filter block executes      │
│ filtered = allChats.filter {                           │
│   name.contains("ali", true) || msg.contains("ali", true)│
│ }                                                      │
│ Result: [Alice Brown] (1 item matches)                │
└────────────────────────────────────────────────────────┘
                           │
                           ▼
┌────────────────────────────────────────────────────────┐
│ STEP 4: LazyColumn recomposes with filtered dataset   │
│ Screen renders ONLY Alice Brown                        │
└────────────────────────────────────────────────────────┘
                           │
                           ▼
┌────────────────────────────────────────────────────────┐
│ STEP 5: User taps (✕) Clear Button                     │
│ searchQuery = "" → filtered = allChats                │
│ Screen instantly restores all 10 conversations!        │
└────────────────────────────────────────────────────────┘
```

---

### ⚡ Why `remember(searchQuery)` Matters for Performance

> **⚠️ Critical Performance Concept:**
> In Compose, any state read in a composable causes that composable function to re-execute on recomposition.
> 
> ```kotlin
> // 🔴 WRONG: Runs filter on EVERY recomposition
> val filtered = allChats.filter { it.contact.name.contains(query) }
> 
> // 🟢 CORRECT: Runs filter ONLY when query or dataset reference changes
> val filtered = remember(query, allChats) {
>     if (query.isBlank()) allChats
>     else allChats.filter { it.contact.name.contains(query, ignoreCase = true) }
> }
> ```
> 
> Without `remember(query)`, scrolling the list or running an icon animation triggers a recomposition of the screen, forcing Kotlin to re-filter the entire dataset on every single frame. With `remember(query)`, Compose caches the filtered result, returning the existing list instance in $O(1)$ time!

---

### 💻 Implementation — `ui/chatlist/TelegramSearchBar.kt`

Create `ui/chatlist/TelegramSearchBar.kt`:

```kotlin
package com.example.telegramclone.ui.chatlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.telegramclone.ui.theme.SearchBarGray
import com.example.telegramclone.ui.theme.TextSecondary

@Composable
fun TelegramSearchBar(
    query: String,                      // ⬇️ State down
    onQueryChange: (String) -> Unit,    // ⬆️ Event up
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(SearchBarGray)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Leading Magnifying Glass Icon
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = "Search",
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )

        // 📌 BasicTextField: Bare-metal text field without Material decoration overhead.
        // Custom placeholder text is positioned directly via the decorationBox slot.
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            textStyle = TextStyle(fontSize = 16.sp, color = TextSecondary),
            cursorBrush = SolidColor(TextSecondary),
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            decorationBox = { innerTextField ->
                if (query.isEmpty()) {
                    Text(
                        text = "Search chats...",
                        color = TextSecondary.copy(alpha = 0.6f),
                        fontSize = 16.sp
                    )
                }
                innerTextField() // Emits the active typing surface
            }
        )

        // Trailing Clear Icon Button: Displayed only when the search query has characters
        if (query.isNotEmpty()) {
            IconButton(
                onClick = { onQueryChange("") },
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Clear search",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
```

---

### 🔍 Deep Dive: `BasicTextField` vs. Material `TextField`

| Feature | Material `TextField` | Foundation `BasicTextField` |
| :--- | :--- | :--- |
| **Out-of-the-box UI** | Pre-styled Material container, indicator line, label | Bare-metal text canvas (zero default padding or borders) |
| **Minimum Height** | Fixed at $56\text{dp}$ | Completely flexible (determined by your content padding) |
| **Custom Styling** | High friction to override internal shape & colors | $100\%$ control via `decorationBox` slot |
| **Best Used For** | Standard data forms (Login, checkout, profile) | High-fidelity branded UI (Search bars, chat pills) |

---

## 💬 Step 5: Telegram Chat Item Row

### 💻 Implementation — `ui/chatlist/ChatItemRow.kt`

Create `ui/chatlist/ChatItemRow.kt`:

```kotlin
package com.example.telegramclone.ui.chatlist

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.telegramclone.data.ChatItem
import com.example.telegramclone.ui.components.AvatarImage
import com.example.telegramclone.ui.theme.DividerColor
import com.example.telegramclone.ui.theme.TelegramBlue
import com.example.telegramclone.ui.theme.TelegramGreen
import com.example.telegramclone.ui.theme.TextPrimary
import com.example.telegramclone.ui.theme.TextSecondary

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
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular Avatar
            AvatarImage(
                initials = chat.contact.initials,
                backgroundColor = chat.contact.avatarColor,
                size = 54.dp
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Details Column (weight = 1f expands to absorb space before right edge)
            Column(modifier = Modifier.weight(1f)) {
                // Top Row: Contact Name + Sent Timestamp
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
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = chat.timestamp,
                        fontSize = 12.sp,
                        color = if (chat.unreadCount > 0) TelegramBlue else TextSecondary
                    )
                }

                Spacer(modifier = Modifier.size(4.dp))

                // Bottom Row: Message snippet + Green Unread Badge
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
                        modifier = Modifier.weight(1f)
                    )

                    if (chat.unreadCount > 0) {
                        UnreadBadge(count = chat.unreadCount)
                    }
                }
            }
        }

        // Indented Divider: Aligns with start of text column (54dp avatar + 14dp space + 12dp pad = 80dp)
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
            .background(TelegramGreen),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$count",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
```

---

## 📱 Step 6: The Searchable Chat List Screen

### 💻 Implementation — `ui/chatlist/ChatListScreen.kt`

Create `ui/chatlist/ChatListScreen.kt`:

```kotlin
package com.example.telegramclone.ui.chatlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.telegramclone.data.ChatItem
import com.example.telegramclone.data.FakeData
import com.example.telegramclone.ui.theme.TelegramBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    onChatClick: (ChatItem) -> Unit,
    allChats: List<ChatItem> = FakeData.chatList
) {
    // ─── 1. REACTIVE SEARCH STATE ──────────────────────────────
    var searchQuery by remember { mutableStateOf("") }

    // ─── 2. CACHED FILTER CALCULATION ──────────────────────────
    // Recalculates ONLY when searchQuery or allChats references change
    val filteredChats = remember(searchQuery, allChats) {
        if (searchQuery.isBlank()) {
            allChats
        } else {
            allChats.filter { chat ->
                chat.contact.name.contains(searchQuery, ignoreCase = true) ||
                chat.lastMessage.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // ─── 3. SCAFFOLD & VIEWPORT ────────────────────────────────
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Telegram",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* Open Navigation Drawer */ }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Menu",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TelegramBlue
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* New conversation */ },
                containerColor = TelegramBlue,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "New chat",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar Component (Stateless, State Hoisted)
            TelegramSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it }
            )

            // Conversation Feed or Empty State
            if (filteredChats.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No chats found",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    items(
                        items = filteredChats,
                        key = { chat -> chat.contact.id } // 📌 Stable key enables smart recycling
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
}
```

---

### 🔄 The Complete Unidirectional Data Flow

```text
User enters "dev" into search bar
         │
         ▼
TelegramSearchBar fires onQueryChange("dev")   ⬆️ Event up
         │
         ▼
ChatListScreen updates: searchQuery = "dev"   (State changes)
         │
         ▼
Compose triggers recomposition of ChatListScreen
         │
         ▼
remember("dev", allChats) recalculates:
  allChats.filter { name/msg contains "dev" } → [Dev Team 🚀]
  filteredChats evaluates to a 1-item list
         │
         ▼
LazyColumn receives filteredChats with 1 item
         │
         ▼
Viewport immediately updates to display only "Dev Team 🚀" ✅
```

---

## 🔌 Step 7: Wiring Everything Together

### 📱 Root Composable — `TelegramApp.kt`

Create `TelegramApp.kt`:

```kotlin
package com.example.telegramclone

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.telegramclone.ui.chatlist.ChatListScreen

@Composable
fun TelegramApp() {
    val context = LocalContext.current

    ChatListScreen(
        onChatClick = { chat ->
            Toast.makeText(
                context,
                "Open chat with ${chat.contact.name}",
                Toast.LENGTH_SHORT
            ).show()
        }
    )
}
```

---

### 🚀 Activity Entry Point — `MainActivity.kt`

Create `MainActivity.kt`:

```kotlin
package com.example.telegramclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.telegramclone.ui.theme.TelegramCloneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TelegramCloneTheme {
                TelegramApp()
            }
        }
    }
}
```

---

## 📜 Step 8: Architectural Preview — Scroll Detection & Floating Jump Button

### The Problem with Direct Scroll Observation

In chat applications, when a user scrolls up to review message history, a floating **"Scroll-to-Bottom" (↓)** button should fade in.

```kotlin
// 🔴 FATAL PERFORMANCE MISTAKE:
val listState = rememberLazyListState()
val showButton = listState.firstVisibleItemIndex > 3 // 💀 RECOMPOSES ON EVERY SCROLL PIXEL!
```
Because `listState.firstVisibleItemScrollOffset` changes during every frame of a touch scroll ($\sim 60\text{--}120\text{Hz}$), reading `listState` directly in a composable body forces the **entire screen to recompose on every scroll event**, causing immediate stutter and frame drops!

---

### 🛡️ The Solution: `LazyListState` & `derivedStateOf`

`derivedStateOf` acts as a **change-detection gatekeeper**: it observes underlying high-frequency states, but **only notifies the Compose runtime when the derived Boolean flips** (`false` $\leftrightarrow$ `true`):

```kotlin
// 🟢 HIGH-PERFORMANCE PATTERN:
val showScrollToBottom by remember {
    derivedStateOf {
        listState.firstVisibleItemIndex > 3
    }
}
```

```text
User scrolls 500 pixels up:
Without derivedStateOf:  500 recompositions triggered ❌ (Severe Jank)
With derivedStateOf:     EXACTLY 1 recomposition triggered ✅ (Smooth 120 FPS)
```

---

### 💻 Preview Implementation Pattern

```kotlin
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ScrollDetectionPreview() {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Derived state only emits when the condition flips across the index 3 boundary
    val showScrollToBottom by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 3 }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(state = listState) {
            // ... message list items ...
        }

        // Floating jump-to-bottom action button
        AnimatedVisibility(
            visible = showScrollToBottom,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    scope.launch { listState.animateScrollToItem(0) }
                }
            ) {
                Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Scroll to bottom")
            }
        }
    }
}
```

---

## ✨ Step 9: Architectural Preview — List Item Animations (`Modifier.animateItem`)

### The Evolution of Compose List Animations

In older Compose versions, animating items arriving or departing from a list required complex `AnimatedVisibility` wrappers and manual slide calculations.

In modern Jetpack Compose, **`Modifier.animateItem()`** achieves automatic list entry, exit, and reordering animations with a single line of code:

```kotlin
LazyColumn {
    items(
        items = messages,
        key = { it.id } // 📌 Required: Item keys allow Compose to track item movement
    ) { message ->
        MessageBubble(
            message = message,
            modifier = Modifier.animateItem() // 📌 Auto-animates placement, addition & deletion!
        )
    }
}
```

When a new incoming message is added to the list, Compose detects the layout addition and smoothly slides the bubble into position without requiring any boilerplate animation drivers. We will fully connect this in Part 2!

---

## 🧠 Jetpack Compose Principles Applied: Where & Why

| Compose Concept | Where It Is Used | Engineering Rationale |
| :--- | :--- | :--- |
| **`remember(searchQuery, allChats)`** | `ChatListScreen` | Caches search filter results, avoiding re-filtering during scrolling or UI recompositions. |
| **`BasicTextField`** | `TelegramSearchBar` | Eliminates Material container padding to create a bespoke, compact search pill. |
| **`derivedStateOf`** | Scroll position tracking | Converts continuous scroll offsets into a stable boolean, preventing 60fps recomposition thrashing. |
| **`decorationBox`** | `TelegramSearchBar` | Positions custom placeholder text and icons seamlessly around the raw typing surface. |
| **State Hoisting** | `TelegramSearchBar` | Keeps the search input component 100% stateless and reusable across different screens. |
| **Stable Keys (`key = { it.id }`)** | `LazyColumn` items | Enables Compose to recycle view nodes accurately as the filtered collection changes size. |
| **`Modifier.animateItem()`** | Item animation preview | Declaratively handles additions, removals, and reordering animations in lazy layouts. |

---

## 🧪 Self-Assessment & Knowledge Check

Test your mastery of reactive search and scroll optimization:

### 1. What is the performance defect in this code snippet?

```kotlin
@Composable
fun SearchableList(allItems: List<String>) {
    var query by remember { mutableStateOf("") }
    val filtered = allItems.filter { it.contains(query, ignoreCase = true) }

    LazyColumn {
        items(filtered) { item -> Text(item) }
    }
}
```

<details>
<summary>Click to reveal answer</summary>

**Answer:**
`val filtered` is calculated directly in the composable body without `remember(query)`. Whenever `SearchableList` recomposes (e.g., when the user scrolls the `LazyColumn`, an animation runs, or an ancestor recomposes), the `.filter { ... }` lambda re-executes across the entire list, allocating a new list instance on every single frame and causing severe UI jank.
</details>

---

### 2. Why is `derivedStateOf` preferred over a standard `remember` when observing scroll position?

```kotlin
// Option A:
val showButton = listState.firstVisibleItemIndex > 3

// Option B:
val showButton by remember { derivedStateOf { listState.firstVisibleItemIndex > 3 } }
```

<details>
<summary>Click to reveal answer</summary>

**Answer:**
`LazyListState` updates multiple times per frame during user flings and drag gestures. With Option A, the composable directly reads snapshot state, causing recomposition on every pixel change (up to 120 times/sec). `derivedStateOf` in Option B acts as an internal state buffer: it tracks the scroll offset but only emits recompositions when the computed boolean flips (`false` $\leftrightarrow$ `true`), maintaining a smooth 60–120 FPS.
</details>

---

### 3. When a user types `"Alice"` into `TelegramSearchBar`, what exact sequence occurs under the hood?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
1. `BasicTextField` triggers `onValueChange("Alice")`.
2. `searchQuery` state updates to `"Alice"`.
3. Compose detects the state change and schedules a recomposition of `ChatListScreen`.
4. `remember(searchQuery, allChats)` detects that the `searchQuery` key has changed and re-runs the filter lambda.
5. `filteredChats` receives a new list containing only Alice's conversation.
6. `LazyColumn` receives the updated collection and recycles visible rows, showing only Alice.
</details>

---

### 4. What is the difference between conditionally rendering a clear button using `if (query.isNotEmpty())` vs. `AnimatedVisibility(query.isNotEmpty())`?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
Using `if (query.isNotEmpty())` mounts and unmounts the clear icon immediately with zero transitional animation. `AnimatedVisibility` applies smooth fade and scale transitions (`fadeIn() + scaleIn()`), providing visual polish and continuity.
</details>

---

### 5. If `expensiveSearch(query)` is wrapped in `remember(query)` and the user types `"hello"` letter-by-letter (`h` → `he` → `hel` → `hell` → `hello`), how many times will it execute?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
It will execute **6 times** (once on initial empty composition, plus 5 times for each individual keystroke). In production enterprise applications, this pattern should be debounced using Kotlin Coroutines (`delay(300)` inside a `LaunchedEffect`) to avoid executing expensive search calculations until the user pauses typing.
</details>

---

## 🏁 Checkpoint: What You Should Have Working

Verify that your Part 1 implementation meets all engineering requirements:

- [x] **Theme:** Telegram sky-blue top bar (`#2AABEE`) with hamburger menu icon and clean white list canvas.
- [x] **Search Field:** Custom `BasicTextField` search pill with magnifying glass icon and `"Search chats..."` placeholder.
- [x] **Real-Time Filtering:** Entering text instantly filters the list matching both contact names and message bodies.
- [x] **Clear Button:** The (✕) icon appears only when text is entered and clears the query on click, instantly restoring the full list.
- [x] **Empty State:** Typing a non-existent name (e.g., `"xyz"`) displays a centered `"No chats found"` message.
- [x] **List Styling:** Rows feature 54dp initial avatars, unread counts with emerald green pills, and indented dividers aligned with text.
- [x] **FAB:** Bottom-right circular floating action button with pencil icon.

---

## 🏋️ Hands-On Coding Exercises

Solidify your reactive Compose engineering skills with these exercises:

### 🎯 Exercise 1: Search Query Debouncing
Avoid running the filter on every rapid keystroke by adding a 300ms debounce using `LaunchedEffect`:
```kotlin
var debouncedQuery by remember { mutableStateOf("") }
LaunchedEffect(searchQuery) {
    delay(300) // Wait for user typing pause
    debouncedQuery = searchQuery
}
val filteredChats = remember(debouncedQuery, allChats) { ... }
```

---

### 🎯 Exercise 2: Text Match Highlighting
When searching `"ali"`, make the matching characters in `"Alice Brown"` bold and colored in `TelegramBlue` using Compose's `buildAnnotatedString` and `SpanStyle`.

---

### 🎯 Exercise 3: Filter Chips for Message Categories
Add a horizontal row of filter chips (`"All"`, `"Channels"`, `"Groups"`, `"Bots"`) directly beneath the search bar that combines category filters with search query filtering.

---

## 🚀 Looking Ahead: Part 2 (The Interactive Chat Screen)

In **Part 2 of the Telegram Clone**, we will build the full interactive conversation experience:
- Chat bubbles with Telegram's signature soft-green styling (`#EEFFDE`).
- `reverseLayout = true` so the list naturally scrolls from the bottom.
- Live **Scroll-to-Bottom** floating button powered by `LazyListState` and `derivedStateOf`.
- Auto-sliding message insertion animations using modern `Modifier.animateItem()`.
- Interactive input bar with send mechanics and simulated responses.