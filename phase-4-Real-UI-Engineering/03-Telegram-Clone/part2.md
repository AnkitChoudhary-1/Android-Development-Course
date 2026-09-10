# ✈️ Telegram UI Clone — Part 2: Interactive Chat Screen, Reverse Layout & List Animations

> **🎯 What You Will Build:** The complete Telegram **Chat Conversation Screen** in Jetpack Compose. You will engineer Telegram's signature soft-mint message bubbles (`#EEFFDE`), build an inverted lazy layout (`reverseLayout = true`), implement a live "Scroll-to-Bottom" floating button optimized with `derivedStateOf`, animate message entries with modern `Modifier.animateItem()`, and wire dynamic soft-keyboard insets with an automated mock reply engine.

---

## 📋 Table of Contents

- [Overview — What We're Building](#-overview--what-were-building)
  - [Visual Layout Architecture](#-visual-layout-architecture)
  - [Core Engineering Concepts Introduced](#-core-engineering-concepts-introduced)
- [Target File Structure](#-target-file-structure)
- [Step 0: Models & Mock Message Dataset](#-step-0-models--mock-message-dataset)
  - [1. Data Models — data/Models.kt](#-1-data-models--datamodelskt)
  - [2. Mock Conversation Dataset — data/FakeData.kt](#-2-mock-conversation-dataset--datafakedatakt)
- [Step 1: Telegram Speech Bubbles](#-step-1-telegram-speech-bubbles)
  - [Bubble Geometry & Visual Styling](#-bubble-geometry--visual-styling)
  - [Date Pill Component — ui/chat/TelegramDatePill.kt](#-date-pill-component--uichattelegramdatepillkt)
  - [Message Bubble Implementation — ui/chat/TelegramMessageBubble.kt](#-message-bubble-implementation--uichattelegrammessagebubblekt)
- [Step 2: Custom Chat Top Bar with Dynamic Presence](#-step-2-custom-chat-top-bar-with-dynamic-presence)
  - [Header Hierarchy & Layout](#-header-hierarchy--layout)
  - [Implementation — ui/chat/TelegramChatTopBar.kt](#-implementation--uichattelegramchattopbarkt)
- [Step 3: Telegram Bottom Input Bar](#-step-3-telegram-bottom-input-bar)
  - [Input Layout Architecture](#-input-layout-architecture)
  - [Implementation — ui/chat/TelegramChatInputBar.kt](#-implementation--uichattelegramchatinputbarkt)
- [Step 4: The Inverted Chat Screen Engine (reverseLayout = true)](#-step-4-the-inverted-chat-screen-engine-reverselayout--true)
  - [Why reverseLayout = true is Industry Standard](#-why-reverselayout--true-is-industry-standard)
  - [Scroll Tracking with derivedStateOf](#-scroll-tracking-with-derivedstateof)
  - [Modern List Animations with Modifier.animateItem()](#-modern-list-animations-with-modifieranimateitem)
  - [Implementation — ui/chat/ChatScreen.kt](#-implementation--uichatchatscreenkt)
- [Step 5: Full Navigation & Back Handling](#-step-5-full-navigation--back-handling)
  - [Wiring ChatScreen into TelegramApp.kt](#-wiring-chatscreen-into-telegramappkt)
- [🔍 Deep Dive: Advanced Jetpack Compose Mechanics](#-deep-dive-advanced-jetpack-compose-mechanics)
  - [1. reverseLayout Coordinate System & Indexing](#-1-reverselayout-coordinate-system--indexing)
  - [2. High-Frequency Scroll Optimization with derivedStateOf](#-2-high-frequency-scroll-optimization-with-derivedstateof)
  - [3. Zero-Boilerplate Item Animation with Modifier.animateItem()](#-3-zero-boilerplate-item-animation-with-modifieranimateitem)
  - [4. Keyboard Gliding with navigationBarsPadding().imePadding()](#-4-keyboard-gliding-with-navigationbarspaddingimepadding)
- [🧠 Jetpack Compose Principles Applied: Where & Why](#-jetpack-compose-principles-applied-where--why)
- [🧪 Self-Assessment & Knowledge Check](#-self-assessment--knowledge-check)
- [🏁 Checkpoint: What You Should Have Working](#-checkpoint-what-you-should-have-working)
- [🏋️ Hands-On Coding Exercises](#-hands-on-coding-exercises)

---

## 📱 Overview — What We're Building

In Part 1, we constructed the primary searchable chat list with real-time query filtering. In **Part 2**, tapping any conversation cell immediately opens the full-screen **Telegram Chat Screen**. 

This screen demonstrates how production messaging apps handle inverted list scrolling, automatic keyboard adjustments, floating jump-to-bottom controls, and hardware-accelerated message insertion animations.

### 🖼️ Visual Layout Architecture

```text
┌─────────────────────────────────────────────────────────────┐
│  ←  [AB]  Alice Brown                                  ⋮    │  ← Custom Top Bar (Back, Avatar, Name, Status, Menu)
│           online / typing...                                │
├─────────────────────────────────────────────────────────────┤
│                         [ TODAY ]                           │  ← Floating Date Pill (Translucent, rounded)
│                                                             │
│  ┌──────────────────────────────┐                           │
│  │ Hey! How are you?            │                           │  ← Received Bubble (White, left-aligned)
│  │                        9:30 AM│                          │     Tail on bottom-left
│  └──────────────────────────────┘                           │
│                                                             │
│               ┌───────────────────────────┐                 │
│               │ I'm good! Just finished   │                 │  ← Sent Bubble (Soft Light Green, right-aligned)
│               │ work              9:32 AM✓✓│                 │     Tail on bottom-right + Double checkmarks
│               └───────────────────────────┘                 │
│                                                             │
│  ┌──────────────────────────────┐                           │
│  │ Sarah's birthday party! 🎉   │                           │
│  │                        9:38 AM│                          │
│  └──────────────────────────────┘                           │
│                                                             │
│  ... scrollable conversation ...                            │
│                                                             │
│                                                  ┌───┐      │
│                                                  │ ↓ │      │  ← Floating Scroll-to-Bottom Button (FAB)
│                                                  └───┘      │     (Appears ONLY when scrolled up past 2 items)
├─────────────────────────────────────────────────────────────┤
│  📎  Message...                                   😊   🎤   │  ← Input Bar (Attach, BasicTextField, Emojis, Mic/Send)
└─────────────────────────────────────────────────────────────┘
```

---

### 🚀 Core Engineering Concepts Introduced

```text
1. reverseLayout = true:
   • List starts from the bottom (Index 0 = latest message).
   • Content naturally remains visible when the virtual keyboard expands.

2. derivedStateOf:
   • Guards against continuous 60–120Hz scroll recomposition thrashing.
   • Emits updates ONLY when scroll threshold crossings flip boolean states.

3. Modifier.animateItem():
   • Modern Compose 1.7+ API that auto-animates item additions, removals, and moves.
   • Eliminates manual AnimatedVisibility boilerplate for list mutations.
```

---

## 📁 Target File Structure

We introduce the `ui/chat` package to organize the chat conversation components:

```text
com.example.telegramclone/
├── MainActivity.kt
├── TelegramApp.kt                     ← Updated: Routes between ChatListScreen & ChatScreen
├── data/
│   ├── Models.kt                      ← Domain models: Contact, ChatItem, Message
│   └── FakeData.kt                    ← Mock conversations & message history
└── ui/
    ├── theme/
    │   ├── Color.kt                   ← Telegram blue, bubble colors, and chat background
    │   └── Theme.kt
    ├── components/
    │   └── AvatarImage.kt             ← From Part 1
    ├── chatlist/                      ← From Part 1
    │   ├── ChatItemRow.kt
    │   ├── TelegramSearchBar.kt
    │   └── ChatListScreen.kt
    └── chat/                          ← NEW in Part 2
        ├── TelegramDatePill.kt        ← Floating date separator badge
        ├── TelegramMessageBubble.kt   ← Sent vs. Received speech bubbles with asymmetric tails
        ├── TelegramChatTopBar.kt      ← Back arrow, contact info, status subtitle, and menu
        ├── TelegramChatInputBar.kt    ← Attachment, message field, emoji button, and mic/send FAB
        └── ChatScreen.kt              ← Inverted LazyColumn, scroll detection, and auto-reply engine
```

---

## 🛠️ Step 0: Models & Mock Message Dataset

### 📝 1. Data Models — `data/Models.kt`

Open `data/Models.kt` and confirm the domain model definitions:

```kotlin
package com.example.telegramclone.data

import androidx.compose.ui.graphics.Color

/**
 * Represents a Telegram contact.
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
 * Represents an individual message in a conversation.
 */
data class Message(
    val id: String,
    val text: String,
    val timestamp: String,
    val isSentByMe: Boolean
)
```

---

### 👥 2. Mock Conversation Dataset — `data/FakeData.kt`

Ensure `data/FakeData.kt` provides initial messages for our conversations:

```kotlin
package com.example.telegramclone.data

import androidx.compose.ui.graphics.Color

object FakeData {

    val contacts = listOf(
        Contact("c1", "Alice Brown", "AB", Color(0xFF6366F1), isOnline = true),
        Contact("c2", "John Doe", "JD", Color(0xFFEC4899)),
        Contact("c3", "Dev Team 🚀", "DT", Color(0xFF10B981), isOnline = true),
        Contact("c4", "Sarah Wilson", "SW", Color(0xFF8B5CF6)),
        Contact("c5", "Mom ❤️", "M", Color(0xFFF59E0B)),
        Contact("c6", "Mike Johnson", "MJ", Color(0xFFEF4444)),
        Contact("c7", "Emma Davis", "ED", Color(0xFF06B6D4), isOnline = true)
    )

    val chatList = listOf(
        ChatItem(contacts[0], "Hey! Are you coming tonight?", "9:41 AM", unreadCount = 2),
        ChatItem(contacts[1], "Thanks for the help! 🙏", "10:15 AM"),
        ChatItem(contacts[2], "Alice: PR is ready for review", "8:30 AM", unreadCount = 5),
        ChatItem(contacts[3], "That coffee place was amazing!", "Yesterday", unreadCount = 1),
        ChatItem(contacts[4], "Don't forget to eat vegetables", "Yesterday")
    )

    // Initial message history between Alice and the user
    fun getInitialMessages(contactId: String): List<Message> = listOf(
        Message("m1", "Hey! How are you doing today?", "9:30 AM", isSentByMe = false),
        Message("m2", "I'm good! Just finished work", "9:32 AM", isSentByMe = true),
        Message("m3", "Nice! Are you coming tonight?", "9:35 AM", isSentByMe = false),
        Message("m4", "Tonight? What's happening?", "9:37 AM", isSentByMe = true),
        Message("m5", "Sarah's birthday party! 🎉", "9:38 AM", isSentByMe = false),
        Message("m6", "At that new rooftop place", "9:38 AM", isSentByMe = false),
        Message("m7", "Oh right! I totally forgot 😅", "9:40 AM", isSentByMe = true),
        Message("m8", "What time does it start?", "9:40 AM", isSentByMe = true),
        Message("m9", "8pm. I can pick you up!", "9:41 AM", isSentByMe = false),
        Message("m10", "Hey! Are you coming tonight?", "9:41 AM", isSentByMe = false)
    )
}
```

---

## 💬 Step 1: Telegram Speech Bubbles

### Bubble Geometry & Visual Styling

Telegram bubbles feature rounded corners with an asymmetric tail:
- **Sent Bubble:** Light soft-mint background (`#EEFFDE`), aligned to the right edge (`Arrangement.End`), with a flat bottom-right corner (`bottomEnd = 2.dp`).
- **Received Bubble:** Crisp white background (`#FFFFFF`), aligned to the left edge (`Arrangement.Start`), with a flat bottom-left corner (`bottomStart = 2.dp`).
- **Trailing Metadata:** Timestamp and double checkmarks float at the bottom-right of the bubble content.

```text
SENT BUBBLE (Right, Mint Green #EEFFDE):
  topStart = 16.dp ───────── topEnd = 16.dp
  │                                       │
  │  Message Text Here                    │
  │                                       │
  bottomStart = 16.dp ────── bottomEnd = 2.dp  ← Subtle tail on bottom right!

RECEIVED BUBBLE (Left, White #FFFFFF):
  topStart = 16.dp ───────── topEnd = 16.dp
  │                                       │
  │  Message Text Here                    │
  │                                       │
  bottomStart = 2.dp ─────── bottomEnd = 16.dp ← Subtle tail on bottom left!
```

---

### 📅 Date Pill Component — `ui/chat/TelegramDatePill.kt`

Create `ui/chat/TelegramDatePill.kt`:

```kotlin
package com.example.telegramclone.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Centered, translucent date badge that floats over the chat wallpaper.
 */
@Composable
fun TelegramDatePill(
    dateText: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF4A6572).copy(alpha = 0.5f))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = dateText,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}
```

---

### 💬 Message Bubble Implementation — `ui/chat/TelegramMessageBubble.kt`

Create `ui/chat/TelegramMessageBubble.kt`:

```kotlin
package com.example.telegramclone.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.telegramclone.data.Message
import com.example.telegramclone.ui.theme.BubbleReceived
import com.example.telegramclone.ui.theme.BubbleSent
import com.example.telegramclone.ui.theme.TelegramGreen
import com.example.telegramclone.ui.theme.TextPrimary
import com.example.telegramclone.ui.theme.TextSecondary

@Composable
fun TelegramMessageBubble(
    message: Message,
    modifier: Modifier = Modifier
) {
    // 📌 Arrangement: Sent messages align to End (Right), Received align to Start (Left)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 2.dp),
        horizontalArrangement = if (message.isSentByMe) Arrangement.End else Arrangement.Start
    ) {
        // Asymmetric Telegram corner rounding
        val bubbleShape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = if (message.isSentByMe) 16.dp else 2.dp,
            bottomEnd = if (message.isSentByMe) 2.dp else 16.dp
        )

        Box(
            modifier = Modifier
                .shadow(elevation = 0.5.dp, shape = bubbleShape)
                .background(
                    color = if (message.isSentByMe) BubbleSent else BubbleReceived,
                    shape = bubbleShape
                )
                .widthIn(min = 60.dp, max = 290.dp)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Column {
                // Message Content
                Text(
                    text = message.text,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    lineHeight = 20.sp
                )

                // Timestamp & Checkmarks Row
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.timestamp,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    if (message.isSentByMe) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.DoneAll,
                            contentDescription = "Read",
                            tint = TelegramGreen,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}
```

---

## 📱 Step 2: Custom Chat Top Bar with Dynamic Presence

### Header Hierarchy & Layout

The Telegram conversation top bar hosts:
1. Back arrow (`ArrowBack`)
2. Contact initial avatar (38dp)
3. Column with Contact Name (16sp bold) and Presence status (`"online"` / `"typing..."` in light blue-white)
4. Overflow action icon (`MoreVert`)

```text
Row (TelegramBlue, fillMaxWidth, statusBarsPadding)
 ├── IconButton (ArrowBack)
 ├── AvatarImage (size = 38dp)
 ├── Column (weight = 1f, paddingStart = 10dp)
 │    ├── Text("Alice Brown", bold, white)
 │    └── Text("online" / "typing...", 12sp)
 └── IconButton (MoreVert)
```

---

### 💻 Implementation — `ui/chat/TelegramChatTopBar.kt`

Create `ui/chat/TelegramChatTopBar.kt`:

```kotlin
package com.example.telegramclone.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
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
import com.example.telegramclone.data.Contact
import com.example.telegramclone.ui.components.AvatarImage
import com.example.telegramclone.ui.theme.TelegramBlue

@Composable
fun TelegramChatTopBar(
    contact: Contact,
    isTyping: Boolean,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(TelegramBlue)
            .statusBarsPadding()
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

        Row(
            modifier = Modifier
                .weight(1f)
                .clickable { /* View profile */ }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarImage(
                initials = contact.initials,
                backgroundColor = contact.avatarColor,
                size = 38.dp,
                fontSize = 15
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = contact.name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                val statusSubtitle = when {
                    isTyping           -> "typing..."
                    contact.isOnline   -> "online"
                    else               -> "last seen recently"
                }

                Text(
                    text = statusSubtitle,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }

        IconButton(onClick = { /* More options */ }) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "Menu",
                tint = Color.White
            )
        }
    }
}
```

---

## ⌨️ Step 3: Telegram Bottom Input Bar

### Input Layout Architecture

Telegram's bottom input bar sits in a pure white panel at the bottom of the screen:
- **Leading Attachment Icon:** Paperclip (`AttachFile`)
- **Center Typing Field:** `BasicTextField` with custom `"Message..."` placeholder
- **Emoji / Sticker Icon:** Smiley face (`Mood`)
- **Trailing Action Button:** Microphone (`Mic`) when empty, morphing into a vibrant blue send airplane (`Send`) when text is typed!

```text
Row (background = White, fillMaxWidth, navigationBarsPadding, imePadding)
 ├── IconButton (AttachFile 📎)
 ├── BasicTextField (weight = 1f, "Message...")
 ├── IconButton (Mood 😊)
 └── IconButton (Mic 🎤 or Send ✈️)
```

---

### 💻 Implementation — `ui/chat/TelegramChatInputBar.kt`

Create `ui/chat/TelegramChatInputBar.kt`:

```kotlin
package com.example.telegramclone.ui.chat

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.telegramclone.ui.theme.TelegramBlue
import com.example.telegramclone.ui.theme.TextPrimary
import com.example.telegramclone.ui.theme.TextSecondary

@Composable
fun TelegramChatInputBar(
    text: String,
    onTextChanged: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .navigationBarsPadding() // Prevents colliding with system gesture pill
            .imePadding()            // Lifts bar cleanly above virtual soft keyboard
            .padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Attachment Paperclip
        IconButton(onClick = { /* Attach media */ }) {
            Icon(
                imageVector = Icons.Filled.AttachFile,
                contentDescription = "Attach",
                tint = TextSecondary,
                modifier = Modifier.size(24.dp)
            )
        }

        // Core Typing Surface
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 6.dp)
        ) {
            if (text.isEmpty()) {
                Text(
                    text = "Message...",
                    color = TextSecondary.copy(alpha = 0.65f),
                    fontSize = 16.sp
                )
            }

            BasicTextField(
                value = text,
                onValueChange = onTextChanged,
                textStyle = TextStyle(color = TextPrimary, fontSize = 16.sp),
                cursorBrush = SolidColor(TelegramBlue),
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Emoji / Sticker Button
        IconButton(onClick = { /* Open stickers */ }) {
            Icon(
                imageVector = Icons.Filled.Mood,
                contentDescription = "Emojis",
                tint = TextSecondary,
                modifier = Modifier.size(24.dp)
            )
        }

        // Dynamic Mic / Send Action
        AnimatedContent(
            targetState = text.isNotBlank(),
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "InputActionButton"
        ) { hasText ->
            if (hasText) {
                IconButton(
                    onClick = { onSendMessage(text.trim()) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = TelegramBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                IconButton(
                    onClick = { /* Record voice note */ },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = "Voice note",
                        tint = TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
```

---

## 🚀 Step 4: The Inverted Chat Screen Engine (`reverseLayout = true`)

### Why `reverseLayout = true` is Industry Standard

In standard lazy lists (`reverseLayout = false`), Index 0 is positioned at the top of the screen. In chat messaging, this causes serious architectural problems:
1. You must manually scroll to the bottom on initial load.
2. Opening the on-screen keyboard shrinks the viewport height, pushing bottom content out of view.

When you configure `reverseLayout = true`:
- **Index 0 is placed at the bottom of the viewport.**
- As new messages arrive, you insert them at index 0 (`messages.add(0, newMessage)`).
- When the soft keyboard slides up, the viewport naturally pins Index 0 directly above the keyboard with zero jumpiness!

```text
STANDARD LIST (reverseLayout = false):    INVERTED LIST (reverseLayout = true):
┌──────────────────────────────┐          ┌──────────────────────────────┐
│ Index 0: Oldest Message      │          │ Index 9: Oldest Message      │
│ Index 1: Old Message         │          │ Index 8: Old Message         │
│ ...                          │          │ ...                          │
│ Index 9: Newest Message  📍  │          │ Index 0: Newest Message  📍  │
└──────────────────────────────┘          └──────────────────────────────┘
 (Must manually scroll to bottom)          (Index 0 anchored at bottom by default!)
```

---

### 🛡️ Scroll Tracking with `derivedStateOf`

Because `reverseLayout = true` anchors Index 0 at the bottom, detecting if the user has scrolled up to read old message history is trivial:

```kotlin
// 📌 derivedStateOf: True only when scrolled past the first 2 messages from the bottom
val showScrollToBottom by remember {
    derivedStateOf {
        listState.firstVisibleItemIndex > 2
    }
}
```

---

### ✨ Modern List Animations with `Modifier.animateItem()`

In Compose 1.7+, we no longer need manual wrappers like `AnimatedVisibility`. Applying `Modifier.animateItem()` to individual bubbles instructs Compose to automatically calculate slide and fade transitions whenever messages are inserted or moved!

```kotlin
TelegramMessageBubble(
    message = message,
    modifier = Modifier.animateItem() // 📌 Auto-animates placement!
)
```

---

### 💻 Implementation — `ui/chat/ChatScreen.kt`

Create `ui/chat/ChatScreen.kt`:

```kotlin
package com.example.telegramclone.ui.chat

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.telegramclone.data.ChatItem
import com.example.telegramclone.data.FakeData
import com.example.telegramclone.data.Message
import com.example.telegramclone.ui.theme.ChatBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun ChatScreen(
    chat: ChatItem,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBackClick)

    // 📌 Reactive Message History:
    // With reverseLayout = true, index 0 is at the bottom.
    // We reverse the fake data list so the newest message sits at index 0.
    val messages = remember {
        mutableStateListOf<Message>().apply {
            addAll(FakeData.getInitialMessages(chat.contact.id).reversed())
        }
    }

    var inputText by remember { mutableStateOf("") }
    var isContactTyping by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // 📌 derivedStateOf: Show FAB only when scrolled away from bottom (index > 2)
    val showScrollToBottom by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 2 }
    }

    // Message transmission & simulated response engine
    fun sendMessage(content: String) {
        val currentTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        val newMessage = Message(
            id = UUID.randomUUID().toString(),
            text = content,
            timestamp = currentTime,
            isSentByMe = true
        )
        // 📌 In reverseLayout, inserting at index 0 places it at the bottom!
        messages.add(0, newMessage)
        inputText = ""

        // Scroll back to bottom if user sent while scrolled up
        scope.launch {
            listState.animateScrollToItem(0)
        }

        // Mock automated response
        scope.launch {
            delay(1200)
            isContactTyping = true
            delay(1600)
            isContactTyping = false

            val replyOptions = listOf(
                "Sounds awesome! 👍",
                "Got it! Let me check on that.",
                "Haha definitely! See you soon 🎉",
                "Sure thing, talk soon!"
            )
            val replyMessage = Message(
                id = UUID.randomUUID().toString(),
                text = replyOptions.random(),
                timestamp = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
                isSentByMe = false
            )
            messages.add(0, replyMessage)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ChatBackground) // Classic Telegram wallpaper tint
    ) {
        // ─── 1. TOP APP BAR ────────────────────────────────────
        TelegramChatTopBar(
            contact = chat.contact,
            isTyping = isContactTyping,
            onBackClick = onBackClick
        )

        // ─── 2. INVERTED CONVERSATION LIST ─────────────────────
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                state = listState,
                reverseLayout = true, // 📌 Key: Index 0 is positioned at bottom!
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(
                    items = messages,
                    key = { it.id } // 📌 Stable key enables Modifier.animateItem()
                ) { message ->
                    TelegramMessageBubble(
                        message = message,
                        modifier = Modifier.animateItem() // 📌 Auto-animates insertions!
                    )
                }

                // In reverseLayout, the item at the top of the viewport is at the END of the list!
                item {
                    TelegramDatePill(dateText = "TODAY")
                }
            }

            // ─── 3. FLOATING SCROLL-TO-BOTTOM BUTTON ───────────
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
                    },
                    containerColor = Color.White,
                    contentColor = Color(0xFF707579),
                    shape = CircleShape,
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Scroll to bottom",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // ─── 4. BOTTOM INPUT BAR ───────────────────────────────
        TelegramChatInputBar(
            text = inputText,
            onTextChanged = { inputText = it },
            onSendMessage = { sendMessage(it) }
        )
    }
}
```

---

## 🔌 Step 5: Full Navigation & Back Handling

### Wiring `ChatScreen` into `TelegramApp.kt`

Open `TelegramApp.kt` and add state-based navigation to transition between the searchable conversation feed and the interactive chat screen:

```kotlin
package com.example.telegramclone

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.telegramclone.data.ChatItem
import com.example.telegramclone.ui.chat.ChatScreen
import com.example.telegramclone.ui.chatlist.ChatListScreen

@Composable
fun TelegramApp() {
    // 📌 State: Currently active chat (null = showing chat list feed)
    var activeChat: ChatItem? by remember { mutableStateOf(null) }

    if (activeChat != null) {
        ChatScreen(
            chat = activeChat!!,
            onBackClick = { activeChat = null }
        )
    } else {
        ChatListScreen(
            onChatClick = { selectedChat ->
                activeChat = selectedChat
            }
        )
    }
}
```

---

## 🔍 Deep Dive: Advanced Jetpack Compose Mechanics

### 1. `reverseLayout` Coordinate System & Indexing

```kotlin
LazyColumn(
    state = listState,
    reverseLayout = true
) {
    items(messages, key = { it.id }) { message -> ... }
}
```
When `reverseLayout = true`:
- Compose inverts its layout measurement pass: item index `0` is measured and anchored at the **bottom** of the scrollable bounds.
- Scrolling down corresponds to moving toward index `0`, while scrolling up scrolls toward index `messages.size - 1`.
- When the on-screen soft keyboard pops up, the viewport container height shrinks from the bottom, keeping index `0` pinned perfectly above the input bar!

---

### 2. High-Frequency Scroll Optimization with `derivedStateOf`

```kotlin
val showScrollToBottom by remember {
    derivedStateOf { listState.firstVisibleItemIndex > 2 }
}
```
- `listState.firstVisibleItemIndex` changes rapidly during drag and fling gestures.
- Without `derivedStateOf`, reading snapshot state causes the entire parent composable to re-execute on every scroll offset change ($\sim 60\text{--}120\text{Hz}$).
- `derivedStateOf` acts as an internal state filter: Compose subscribes **only to the Boolean result**, triggering recomposition solely when the index crosses the boundary value of `2`.

---

### 3. Zero-Boilerplate Item Animation with `Modifier.animateItem()`

```kotlin
TelegramMessageBubble(
    message = message,
    modifier = Modifier.animateItem()
)
```
In modern Compose, `Modifier.animateItem()` coordinates layout placement animations. When a new message is appended at index 0, existing items automatically slide upward with hardware-accelerated physics, while the new bubble enters smoothly.

---

### 4. Keyboard Gliding with `navigationBarsPadding().imePadding()`

```kotlin
modifier
    .navigationBarsPadding()
    .imePadding()
```
By placing `imePadding()` on the bottom input bar container, Compose automatically reads window insets from the operating system and adds real-time padding matching the soft keyboard height, floating the input bar effortlessly above the keyboard.

---

## 🧠 Jetpack Compose Principles Applied: Where & Why

| Compose Concept | Where It Is Used | Engineering Rationale |
| :--- | :--- | :--- |
| **`reverseLayout = true`** | `ChatScreen.LazyColumn` | Anchors newest messages (index 0) to viewport bottom; prevents keyboard clipping. |
| **`derivedStateOf`** | `showScrollToBottom` FAB | Buffers continuous scroll state; emits recompositions only on threshold boolean flips. |
| **`Modifier.animateItem()`** | `TelegramMessageBubble` | Declaratively handles item appearance and placement slide animations. |
| **`mutableStateListOf`** | `ChatScreen.messages` | Observable list mutations (`add(0, msg)`) trigger targeted lazy item compositions. |
| **`BackHandler`** | `ChatScreen` | Hooks into Android's system back dispatcher to dismiss the conversation view cleanly. |
| **`imePadding()`** | `TelegramChatInputBar` | Dynamically elevates input bar above the soft keyboard without custom layout calculations. |
| **Asymmetric `RoundedCornerShape`** | `TelegramMessageBubble` | Generates speech-tail geometry without requiring SVG vector canvases. |

---

## 🧪 Self-Assessment & Knowledge Check

Test your understanding of inverted lazy layouts and scroll mechanics:

### 1. In a `LazyColumn` configured with `reverseLayout = true`, which item is rendered at the physical bottom of the device screen?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
Item **Index 0**. With `reverseLayout = true`, the layout direction is inverted: index 0 is anchored to the bottom of the viewport, and subsequent items (indexes 1, 2, 3...) stack upwards toward the top of the screen.
</details>

---

### 2. Why does inserting a new message require `messages.add(0, newMessage)` when `reverseLayout = true`?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
Because in an inverted layout, index 0 corresponds to the bottom of the screen. Inserting the new message at index 0 immediately places it at the very bottom of the conversation feed, directly above the input bar.
</details>

---

### 3. What performance defect occurs if `derivedStateOf` is omitted when observing `listState.firstVisibleItemIndex`?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
`firstVisibleItemIndex` and its companion scroll offsets update on every single frame during scrolling (60–120 times/sec). Without `derivedStateOf`, reading this value directly in a composable causes the entire composable function to recompose on every scroll pixel, producing severe frame drops and battery drain. `derivedStateOf` ensures recomposition only occurs when the boolean condition actually flips.
</details>

---

### 4. What requirement must be met in `items()` for `Modifier.animateItem()` to function correctly?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
You **must provide a stable, unique key** using `items(items = messages, key = { it.id })`. Without unique keys, Compose tracks list items by index rather than identity, making it impossible to detect which item was inserted, moved, or removed.
</details>

---

### 5. In `ChatScreen`, why is the `TelegramDatePill` placed at the end of the `LazyColumn` block instead of the beginning?

```kotlin
LazyColumn(reverseLayout = true) {
    items(messages) { ... }
    item {
        TelegramDatePill(dateText = "TODAY")
    }
}
```

<details>
<summary>Click to reveal answer</summary>

**Answer:**
Because `reverseLayout = true` inverts the rendering order. The first items defined in code appear at the bottom of the viewport, while items defined last in the block are positioned at the very top of the screen. Placing `TelegramDatePill` at the end ensures it renders above all message bubbles.
</details>

---

## 🏁 Checkpoint: What You Should Have Working

Verify that your Part 2 implementation functions correctly:

- [x] **Top Bar:** Shows contact avatar, bold name, presence status (`"online"` / `"typing..."`), and back button.
- [x] **Inverted Feed:** Messages naturally anchor to the bottom of the screen (`reverseLayout = true`).
- [x] **Telegram Bubbles:** Sent messages are soft mint green (`#EEFFDE`) on the right; received messages are white on the left.
- [x] **Metadata:** Bubbles display timestamps and double checkmarks.
- [x] **List Animations:** Sending a message smoothly animates the new bubble into view using `Modifier.animateItem()`.
- [x] **Scroll FAB:** The downward arrow button appears only when scrolling up past the first 2 messages, and clicking it smoothly animates back to index 0.
- [x] **Interactive Input:** Typing text transitions the microphone icon into a blue send button.
- [x] **Keyboard Insets:** Virtual keyboard smoothly lifts the input bar without covering messages (`imePadding`).
- [x] **Automated Reply:** Sending a message displays a `"typing..."` indicator after 1.2s and appends a mock reply after 2.8s!

---

## 🏋️ Hands-On Coding Exercises

Accelerate your Compose engineering expertise with these practical challenges:

### 🎯 Exercise 1: Channel Broadcast Banner
Add a Telegram-style "Broadcast Channel" mode:
- If a contact is a Channel (e.g. `"Telegram Tips"`), replace the bottom input bar with a static **"Mute"** / **"Unmute"** button panel.

---

### 🎯 Exercise 2: Unread Count Badge on Scroll-to-Bottom FAB
When the user scrolls up, display a small circular green badge over the floating downward arrow button showing the count of new unread messages that arrived while they were scrolled away!
> **Hint:** Track unread messages in state and display them with `BadgedBox` over the FAB.

---

### 🎯 Exercise 3: Quick Reply Swipe Gesture
Enable swipe-to-reply:
- Allow the user to swipe any `TelegramMessageBubble` slightly to the left to trigger a reply action, using `Modifier.pointerInput` or `SwipeToDismissBox`.
