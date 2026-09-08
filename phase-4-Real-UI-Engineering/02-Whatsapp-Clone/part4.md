# 👤 WhatsApp UI Clone — Part 4: Contact Profile, Shared Media Gallery & Settings Architecture

> **🎯 What You Will Build:** The capstone experience of the WhatsApp UI Clone: the **Contact Profile & Media Screen**. You will engineer a collapsing hero avatar profile header, quick communication action buttons (Voice, Video, Pay, Search), an interactive shared media carousel with pinch-to-zoom fullscreen photo preview, end-to-end encryption verification cards, reactive toggle switches for notifications, shared group listings, and a complete multi-tier navigation router.

---

## 📋 Table of Contents

- [Overview — What We're Building](#-overview--what-were-building)
  - [Visual Layout Architecture (Contact Profile & Gallery)](#-visual-layout-architecture-contact-profile--gallery)
  - [Complete 4-Part Application Journey](#-complete-4-part-application-journey)
- [Target File Structure](#-target-file-structure)
- [Step 0: Extended Domain Models & Fake Datasets](#-step-0-extended-domain-models--fake-datasets)
  - [1. Extended Domain Models — data/Models.kt](#-1-extended-domain-models--datamodelskt)
  - [2. Mock Media & Groups — data/FakeData.kt](#-2-mock-media--groups--datafakedatakt)
- [Step 1: Reusable Settings & Option Row Component](#-step-1-reusable-settings--option-row-component)
  - [Slot API Design](#-slot-api-design)
  - [Implementation — ui/profile/ProfileOptionRow.kt](#-implementation--uiprofileprofileoptionrowkt)
- [Step 2: Shared Media, Links & Docs Carousel](#-step-2-shared-media-links--docs-carousel)
  - [Media Thumbnail Geometry](#-media-thumbnail-geometry)
  - [Implementation — ui/profile/MediaGalleryRow.kt](#-implementation--uiprofilemediagalleryrowkt)
- [Step 3: Fullscreen Pinch-to-Zoom Media Viewer](#-step-3-fullscreen-pinch-to-zoom-media-viewer)
  - [Interactive Transform Gestures](#-interactive-transform-gestures)
  - [Implementation — ui/profile/FullscreenMediaViewer.kt](#-implementation--uiprofilefullscreenmediaviewerkt)
- [Step 4: The Contact Info Screen (Master Profile View)](#-step-4-the-contact-info-screen-master-profile-view)
  - [Visual Hierarchy & Sections](#-visual-hierarchy--sections)
  - [Implementation — ui/profile/ContactInfoScreen.kt](#-implementation--uiprofilecontactinfoscreenkt)
- [Step 5: Master Multi-Tier Navigation Router](#-step-5-master-multi-tier-navigation-router)
  - [Coordinating All 4 Modules — WhatsAppApp.kt](#-coordinating-all-4-modules--whatsappappkt)
- [🔍 Deep Dive: Key Jetpack Compose Mechanics](#-deep-dive-key-jetpack-compose-mechanics)
  - [1. Pinch-to-Zoom with detectTransformGestures](#-1-pinch-to-zoom-with-detecttransformgestures)
  - [2. Flexible Slot APIs for Setting Cells](#-2-flexible-slot-apis-for-setting-cells)
  - [3. Multi-Tier State-Driven Navigation Stack](#-3-multi-tier-state-driven-navigation-stack)
- [🧠 Jetpack Compose Principles Applied: Where & Why](#-jetpack-compose-principles-applied-where--why)
- [🧪 Self-Assessment & Knowledge Check](#-self-assessment--knowledge-check)
- [🏁 Capstone Checkpoint: Complete WhatsApp UI Clone Checklist](#-capstone-checkpoint-complete-whatsapp-ui-clone-checklist)
- [🚀 Looking Ahead: Transitioning to Phase 5 (Architecture)](#-looking-ahead-transitioning-to-phase-5-architecture)

---

## 📱 Overview — What We're Building

In Parts 1, 2, and 3, we built the primary chat feed, the conversation detail screen with chat bubbles, and the status stories and calls tabs. In **Part 4**, tapping the top bar in `ChatDetailScreen` opens the comprehensive **Contact Profile Screen**. 

This screen organizes high-density profile metadata: a large hero avatar, quick action buttons, a horizontal shared media gallery with fullscreen zoom view, notification preferences, privacy/encryption badges, common groups, and danger-zone actions (Block and Report).

### 🖼️ Visual Layout Architecture (Contact Profile & Gallery)

```text
┌─────────────────────────────────────────────────────────────┐
│  ←                                                       ⋮  │  ← Top Bar (Back button, overflow menu)
├─────────────────────────────────────────────────────────────┤
│                           ┌─────┐                           │
│                           │ AB  │                           │  ← Hero Avatar (100dp)
│                           └─────┘                           │
│                         Alice Brown                         │  ← 22sp Bold Contact Name
│                       +1 555 019 2834                       │  ← Phone Number
│                                                             │
│       ┌─────┐         ┌─────┐         ┌─────┐       ┌─────┐ │
│       │ 📞  │         │ 📹  │         │ 💳  │       │ 🔍  │ │  ← Quick Action Buttons
│        Audio           Video            Pay         Search  │     (Audio, Video, Pay, Search)
├─────────────────────────────────────────────────────────────┤
│  Living my best life ✨                                     │  ← "About" Status Quote
│  October 14, 2026                                           │  ← Status Date
├─────────────────────────────────────────────────────────────┤
│  Media, links, and docs                                28 > │  ← Shared Media Section Header
│  ┌────┐  ┌────┐  ┌────┐  ┌────┐  ┌────┐                     │
│  │ 🏖️ │  │ 🗻 │  │ 📄 │  │ 🎵 │  │ 🔗 │                     │  ← Horizontal LazyRow of Media Cards
│  └────┘  └────┘  └────┘  └────┘  └────┘                     │     (Tapping opens Fullscreen Viewer)
├─────────────────────────────────────────────────────────────┤
│  🔔  Mute notifications                                [○─] │  ← Interactive Switch Toggle
│  🎵  Custom notifications                                   │
│  👁️  Media visibility                                       │
├─────────────────────────────────────────────────────────────┤
│  🔒  Encryption                                             │  ← End-to-End Encryption Tile
│      Messages and calls are end-to-end encrypted. Tap to    │     (Golden security lock icon)
│      verify.                                                │
│  ⏲️  Disappearing messages                              Off │
├─────────────────────────────────────────────────────────────┤
│  3 groups in common                                         │  ← Common Groups Section
│  ┌────┐ Dev Team 🚀 (Alice, John, You...)                   │
│  ┌────┐ Weekend Trekkers 🏔️ (Alice, Sarah, You...)          │
├─────────────────────────────────────────────────────────────┤
│  🚫  Block Alice Brown                                      │  ← Danger Zone Actions
│  👎  Report Alice Brown                                     │     (Highlighted in alert red)
└─────────────────────────────────────────────────────────────┘
```

---

### 🗺️ Complete 4-Part Application Journey

```text
┌─────────────────────────────────────────────────────────────────────────────┐
│                       WHATSAPP UI CLONE ARCHITECTURE                        │
├──────────────────┬──────────────────┬──────────────────┬────────────────────┤
│      PART 1      │      PART 2      │      PART 3      │       PART 4       │
│  Feed & Tabs     │  Chat Detail     │ Status & Calls   │  Profile & Gallery │
├──────────────────┼──────────────────┼──────────────────┼────────────────────┤
│ • TopAppBar      │ • Asymmetric     │ • Canvas story   │ • Collapsing Hero  │
│ • Custom Tabs    │   speech bubbles │   ring math      │   Avatar Header    │
│ • Unread Badges  │ • Read ticks     │ • Dual stacked   │ • Media Gallery    │
│ • Avatar Image   │ • Input Pill     │   FABs           │ • Pinch-to-Zoom    │
│ • ChatItemRow    │ • Animated Mic   │ • Fullscreen     │ • Settings Slots   │
│ • LazyColumn     │ • imePadding()   │   Story Viewer   │ • Master Router    │
│ • State Hoisting │ • Coroutine mock │ • Call Direction │ • BackHandler      │
│                  │   reply engine   │   Indicators     │   Navigation Tree  │
└──────────────────┴──────────────────┴──────────────────┴────────────────────┘
```

---

## 📁 Target File Structure

In Part 4, we introduce the `ui/profile` package to encapsulate profile, media gallery, and settings components:

```text
com.example.whatsappclone/
├── MainActivity.kt
├── WhatsAppApp.kt                     ← Master router: coordinates Tabs, Chat, Viewer & Profile
├── data/
│   ├── Models.kt                      ← Extended: SharedMedia, CommonGroup, Contact details
│   └── FakeData.kt                    ← Sample media gallery assets & shared groups
└── ui/
    ├── theme/
    ├── components/
    │   ├── AvatarImage.kt
    │   └── StatusAvatar.kt
    ├── chatlist/                      ← Part 1
    ├── chatdetail/                    ← Part 2
    ├── status/                        ← Part 3
    ├── calls/                         ← Part 3
    └── profile/                       ← NEW in Part 4
        ├── ProfileOptionRow.kt        ← Flexible slot-based settings row (Switch, Arrow, Icon)
        ├── MediaGalleryRow.kt         ← Horizontal thumbnail gallery for shared files
        ├── FullscreenMediaViewer.kt   ← Pinch-to-zoom, pan, and rotate image lightbox
        └── ContactInfoScreen.kt       ← Complete contact detail screen with all sections
```

---

## 🛠️ Step 0: Extended Domain Models & Fake Datasets

### 📝 1. Extended Domain Models — `data/Models.kt`

Open `data/Models.kt` and add definitions for shared media and common groups:

```kotlin
package com.example.whatsappclone.data

import androidx.compose.ui.graphics.Color

// ─── MEDIA CLASSIFICATIONS ─────────────────────────────────
enum class MediaType {
    PHOTO,
    VIDEO,
    DOCUMENT,
    LINK
}

/**
 * Represents a piece of media shared within a conversation.
 */
data class SharedMediaItem(
    val id: String,
    val type: MediaType,
    val previewColor: Color,
    val caption: String,
    val timestamp: String,
    val durationText: String? = null // e.g. "0:42" for videos
)

/**
 * Represents a group chat shared in common with a contact.
 */
data class CommonGroup(
    val id: String,
    val name: String,
    val membersSummary: String,
    val initials: String,
    val color: Color
)

// ─── UPDATED CONTACT MODEL ─────────────────────────────────
data class Contact(
    val id: String,
    val name: String,
    val initials: String,
    val avatarColor: Color,
    val phoneNumber: String = "+1 555 019 2834",
    val about: String = "Living my best life ✨",
    val aboutDate: String = "October 14, 2026",
    val sharedMedia: List<SharedMediaItem> = emptyList(),
    val commonGroups: List<CommonGroup> = emptyList()
)
```

---

### 👥 2. Mock Media & Groups — `data/FakeData.kt`

Update `data/FakeData.kt` to enrich our sample contact (`alice`) with shared media and groups:

```kotlin
package com.example.whatsappclone.data

import androidx.compose.ui.graphics.Color

object FakeData {

    // ─── SHARED MEDIA FIXTURES ─────────────────────────────
    val aliceMedia = listOf(
        SharedMediaItem("m1", MediaType.PHOTO, Color(0xFF1E3264), "Shibuya Crossing 🇯🇵", "Oct 14"),
        SharedMediaItem("m2", MediaType.PHOTO, Color(0xFFE13300), "Mount Fuji Summit 🗻", "Oct 12"),
        SharedMediaItem("m3", MediaType.VIDEO, Color(0xFF8D67AB), "Rooftop Party Clip 🎆", "Oct 10", durationText = "0:34"),
        SharedMediaItem("m4", MediaType.PHOTO, Color(0xFF148A08), "Tokyo Ramen Spot 🍜", "Oct 8"),
        SharedMediaItem("m5", MediaType.DOCUMENT, Color(0xFFBC5900), "Flight_Itinerary.pdf", "Oct 5"),
        SharedMediaItem("m6", MediaType.LINK, Color(0xFF0D73EC), "airbnb.com/rooms/tokyo", "Oct 1")
    )

    // ─── GROUPS IN COMMON ──────────────────────────────────
    val aliceCommonGroups = listOf(
        CommonGroup("g1", "Dev Team 🚀", "You, Alice, John, Sarah", "DT", Color(0xFF10B981)),
        CommonGroup("g2", "Weekend Trekkers 🏔️", "You, Alice, Mike, Dave", "WT", Color(0xFF06B6D4)),
        CommonGroup("g3", "Book Club 📚", "You, Alice, Emma", "BC", Color(0xFFF59E0B))
    )

    // ─── CONTACTS ──────────────────────────────────────────
    val alice = Contact(
        id = "c1",
        name = "Alice Brown",
        initials = "AB",
        avatarColor = Color(0xFF6366F1),
        phoneNumber = "+1 555 019 2834",
        about = "Living my best life ✨",
        aboutDate = "October 14, 2026",
        sharedMedia = aliceMedia,
        commonGroups = aliceCommonGroups
    )

    val john = Contact("c2", "John Doe", "JD", Color(0xFFEC4899))
    val mom = Contact("c3", "Mom ❤️", "M", Color(0xFFF59E0B))

    // ─── CHATS & CALLS (PRESERVED FROM PARTS 1-3) ──────────
    val chatList = listOf(
        ChatItem(alice, "Hey! Are you coming tonight?", "9:41 AM", unreadCount = 2, isOnline = true),
        ChatItem(john, "Thanks for the help! 🙏", "10:15 AM", unreadCount = 0)
    )

    fun getInitialMessages(contactId: String): List<Message> = listOf(
        Message("m1", contactId, "Hey! How are you doing today?", "9:30 AM", isSentByMe = false),
        Message("m2", "me", "I'm good! Wrapping up work now", "9:32 AM", isSentByMe = true)
    )

    val recentStatuses = emptyList<UserStatus>()
    val viewedStatuses = emptyList<UserStatus>()
    val recentCalls = emptyList<CallRecord>()
}
```

---

## ⚙️ Step 1: Reusable Settings & Option Row Component

### Slot API Design

In WhatsApp profile views, each setting item (Mute Notifications, Media Visibility, Encryption, Disappearing Messages) shares a consistent layout rhythm:
1. **Leading Vector Icon** (optional tint)
2. **Title & Optional Subtitle**
3. **Trailing Content Slot** (e.g., a `Switch`, text value `"Off"`, right arrow chevron `>`, or empty)

Creating a flexible **Slot-based composable** prevents UI duplication across dozens of setting items.

---

### 🧩 Implementation — `ui/profile/ProfileOptionRow.kt`

Create `ui/profile/ProfileOptionRow.kt`:

```kotlin
package com.example.whatsappclone.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsappclone.ui.theme.TextPrimary
import com.example.whatsappclone.ui.theme.TextSecondary

@Composable
fun ProfileOptionRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    iconTint: Color = TextSecondary,
    titleColor: Color = TextPrimary,
    onClick: (() -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Leading Icon
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
        }

        // Title and Subtitle Column
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = titleColor
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        // Trailing Content Slot (Switch, Arrow, Text)
        if (trailingContent != null) {
            Spacer(modifier = Modifier.width(12.dp))
            trailingContent()
        }
    }
}
```

---

## 🖼️ Step 2: Shared Media, Links & Docs Carousel

### Media Thumbnail Geometry

WhatsApp displays a horizontal gallery carousel at the top of contact details:
- Header displays total count: `"Media, links, and docs (28 >)"`.
- Horizontally scrollable row with 84dp square preview tiles.
- Photo and video tiles display subtle gradient tints with play icons and timestamp duration tags.

---

### 💻 Implementation — `ui/profile/MediaGalleryRow.kt`

Create `ui/profile/MediaGalleryRow.kt`:

```kotlin
package com.example.whatsappclone.ui.profile

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsappclone.data.MediaType
import com.example.whatsappclone.data.SharedMediaItem
import com.example.whatsappclone.ui.theme.TextPrimary
import com.example.whatsappclone.ui.theme.TextSecondary

@Composable
fun MediaGallerySection(
    mediaList: List<SharedMediaItem>,
    onMediaClick: (SharedMediaItem) -> Unit,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 12.dp)
    ) {
        // Section Header Row: Title + Count Chevron
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onViewAllClick)
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Media, links, and docs",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${mediaList.size}",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.size(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "View all",
                    tint = TextSecondary,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal Thumbnail Carousel
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = mediaList,
                key = { it.id }
            ) { item ->
                MediaThumbnailTile(item = item, onClick = { onMediaClick(item) })
            }
        }
    }
}

@Composable
private fun MediaThumbnailTile(
    item: SharedMediaItem,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(86.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.linearGradient(
                    listOf(item.previewColor, item.previewColor.copy(alpha = 0.6f))
                )
            )
            .clickable(onClick = onClick)
    ) {
        when (item.type) {
            MediaType.VIDEO -> {
                // Video Play Overlay + Duration
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.Center)
                )
                if (item.durationText != null) {
                    Text(
                        text = item.durationText,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(4.dp)
                    )
                }
            }
            MediaType.DOCUMENT -> {
                Icon(
                    imageVector = Icons.Filled.Description,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(32.dp).align(Alignment.Center)
                )
            }
            MediaType.LINK -> {
                Icon(
                    imageVector = Icons.Filled.Link,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(32.dp).align(Alignment.Center)
                )
            }
            MediaType.PHOTO -> {
                // Photo gradient placeholder
            }
        }
    }
}
```

---

## 🔍 Step 3: Fullscreen Pinch-to-Zoom Media Viewer

### Interactive Transform Gestures

When a user taps a media preview tile, the app presents a fullscreen lightbox (`FullscreenMediaViewer`):
- **Pinch-to-Zoom:** Scales the image dynamically from $1\times$ to $5\times$ zoom using `detectTransformGestures`.
- **Pan & Translate:** When zoomed in, the user can drag their finger to pan across different areas of the photo.
- **Double-Tap Reset:** Double-tapping smoothly snaps the scale back to $1\times$.

---

### 💻 Implementation — `ui/profile/FullscreenMediaViewer.kt`

Create `ui/profile/FullscreenMediaViewer.kt`:

```kotlin
package com.example.whatsappclone.ui.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsappclone.data.SharedMediaItem

@Composable
fun FullscreenMediaViewer(
    mediaItem: SharedMediaItem,
    senderName: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onClose)

    // Gesture State: Zoom scale and Pan translation offset
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // ─── 1. INTERACTIVE PHOTO CANVAS ───────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    // Double-tap to reset zoom
                    detectTapGestures(
                        onDoubleTap = {
                            scale = if (scale > 1f) 1f else 2.5f
                            offset = Offset.Zero
                        }
                    )
                }
                .pointerInput(Unit) {
                    // Pinch-to-zoom & pan gestures
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(1f, 5f)
                        if (scale > 1f) {
                            offset += pan
                        } else {
                            offset = Offset.Zero
                        }
                    }
                }
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                },
            contentAlignment = Alignment.Center
        ) {
            // High-resolution image canvas simulation
            Box(
                modifier = Modifier
                    .size(340.dp)
                    .background(mediaItem.previewColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mediaItem.caption,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // ─── 2. TOP ACTION OVERLAY BAR ─────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = senderName,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = mediaItem.timestamp,
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 12.sp
                )
            }

            IconButton(onClick = { /* Star photo */ }) {
                Icon(Icons.Filled.StarBorder, contentDescription = "Star", tint = Color.White)
            }
            IconButton(onClick = { /* Share media */ }) {
                Icon(Icons.Filled.Share, contentDescription = "Share", tint = Color.White)
            }
            IconButton(onClick = { /* More options */ }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "Menu", tint = Color.White)
            }
        }
    }
}
```

---

## 📱 Step 4: The Contact Info Screen (Master Profile View)

### Visual Hierarchy & Sections

`ContactInfoScreen` brings all sub-components together within a vertically scrolling `LazyColumn`:
1. **Hero Header:** Large 100dp circular avatar, bold name, telephone number.
2. **Quick Communication Actions:** Audio call, video call, payment, and chat search in a horizontal button bar.
3. **About Section:** User's status quote with creation date.
4. **Media Section:** `MediaGallerySection` carousel.
5. **Notification Toggles:** Mute switch, custom alerts, media visibility.
6. **Privacy & Encryption:** End-to-end security card with golden lock icon.
7. **Common Groups:** List of shared group chats.
8. **Danger Actions:** Block and report buttons highlighted in crimson.

---

### 💻 Implementation — `ui/profile/ContactInfoScreen.kt`

Create `ui/profile/ContactInfoScreen.kt`:

```kotlin
package com.example.whatsappclone.ui.profile

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsappclone.data.CommonGroup
import com.example.whatsappclone.data.Contact
import com.example.whatsappclone.data.SharedMediaItem
import com.example.whatsappclone.ui.components.AvatarImage
import com.example.whatsappclone.ui.theme.BackgroundLight
import com.example.whatsappclone.ui.theme.DividerColor
import com.example.whatsappclone.ui.theme.TextPrimary
import com.example.whatsappclone.ui.theme.TextSecondary
import com.example.whatsappclone.ui.theme.WhatsAppGreen
import com.example.whatsappclone.ui.theme.WhatsAppGreenLight

@Composable
fun ContactInfoScreen(
    contact: Contact,
    onBackClick: () -> Unit,
    onMediaClick: (SharedMediaItem) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBackClick)
    val context = LocalContext.current

    var isNotificationsMuted by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // ─── 1. TOP APP BAR ────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .background(Color.White)
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { /* Profile menu */ }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "Menu", tint = TextPrimary)
            }
        }

        // ─── 2. SCROLLABLE PROFILE SECTIONS ────────────────────
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ─── HERO HEADER ───────────────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AvatarImage(
                        initials = contact.initials,
                        backgroundColor = contact.avatarColor,
                        size = 104.dp,
                        fontSize = 36
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = contact.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Text(
                        text = contact.phoneNumber,
                        fontSize = 15.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Quick Actions Row: Audio, Video, Pay, Search
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileActionButton(icon = Icons.Filled.Call, label = "Audio") {
                            Toast.makeText(context, "Calling ${contact.name}", Toast.LENGTH_SHORT).show()
                        }
                        ProfileActionButton(icon = Icons.Filled.Videocam, label = "Video") {
                            Toast.makeText(context, "Video calling ${contact.name}", Toast.LENGTH_SHORT).show()
                        }
                        ProfileActionButton(icon = Icons.Filled.CurrencyRupee, label = "Pay") {
                            Toast.makeText(context, "Initiating payment", Toast.LENGTH_SHORT).show()
                        }
                        ProfileActionButton(icon = Icons.Filled.Search, label = "Search") {
                            Toast.makeText(context, "Search conversation", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            // ─── ABOUT & PHONE ─────────────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Text(text = contact.about, fontSize = 16.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                    Text(text = contact.aboutDate, fontSize = 13.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))
                    HorizontalDivider(color = DividerColor, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 12.dp))
                    Text(text = contact.phoneNumber, fontSize = 16.sp, color = TextPrimary)
                    Text(text = "Mobile", fontSize = 13.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))
                }
            }

            // ─── SHARED MEDIA GALLERY ──────────────────────────
            if (contact.sharedMedia.isNotEmpty()) {
                item {
                    MediaGallerySection(
                        mediaList = contact.sharedMedia,
                        onMediaClick = onMediaClick,
                        onViewAllClick = {
                            Toast.makeText(context, "All media gallery", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            // ─── NOTIFICATION PREFERENCES ──────────────────────
            item {
                Column(modifier = Modifier.fillMaxWidth().background(Color.White)) {
                    ProfileOptionRow(
                        title = "Mute notifications",
                        icon = Icons.Filled.Notifications,
                        trailingContent = {
                            Switch(
                                checked = isNotificationsMuted,
                                onCheckedChange = { isNotificationsMuted = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = WhatsAppGreenLight
                                )
                            )
                        }
                    )
                    ProfileOptionRow(
                        title = "Custom notifications",
                        icon = Icons.Filled.Notifications,
                        onClick = { }
                    )
                    ProfileOptionRow(
                        title = "Media visibility",
                        icon = Icons.Filled.Photo,
                        onClick = { }
                    )
                }
            }

            // ─── ENCRYPTION & DISAPPEARING MESSAGES ───────────
            item {
                Column(modifier = Modifier.fillMaxWidth().background(Color.White)) {
                    ProfileOptionRow(
                        title = "Encryption",
                        subtitle = "Messages and calls are end-to-end encrypted. Tap to verify.",
                        icon = Icons.Filled.Lock,
                        iconTint = WhatsAppGreen,
                        onClick = {
                            Toast.makeText(context, "Safety numbers verified", Toast.LENGTH_SHORT).show()
                        }
                    )
                    ProfileOptionRow(
                        title = "Disappearing messages",
                        subtitle = "Off",
                        icon = Icons.Filled.Timer,
                        onClick = { }
                    )
                }
            }

            // ─── COMMON GROUPS ─────────────────────────────────
            if (contact.commonGroups.isNotEmpty()) {
                item {
                    Text(
                        text = "${contact.commonGroups.size} groups in common",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }
                items(
                    items = contact.commonGroups,
                    key = { it.id }
                ) { group ->
                    CommonGroupRow(group = group)
                }
            }

            // ─── DANGER ZONE ACTIONS ───────────────────────────
            item {
                Column(modifier = Modifier.fillMaxWidth().background(Color.White)) {
                    ProfileOptionRow(
                        title = "Block ${contact.name}",
                        icon = Icons.Filled.Block,
                        iconTint = Color(0xFFEF4444),
                        titleColor = Color(0xFFEF4444),
                        onClick = {
                            Toast.makeText(context, "${contact.name} blocked", Toast.LENGTH_SHORT).show()
                        }
                    )
                    ProfileOptionRow(
                        title = "Report ${contact.name}",
                        icon = Icons.Filled.ThumbDown,
                        iconTint = Color(0xFFEF4444),
                        titleColor = Color(0xFFEF4444),
                        onClick = {
                            Toast.makeText(context, "${contact.name} reported", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ProfileActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick).padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .border(1.dp, Color(0xFFE9EDEF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = WhatsAppGreen,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = WhatsAppGreen
        )
    }
}

@Composable
private fun CommonGroupRow(group: CommonGroup) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable { }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarImage(
            initials = group.initials,
            backgroundColor = group.color,
            size = 46.dp,
            fontSize = 16
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = group.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(text = group.membersSummary, fontSize = 13.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))
        }
    }
}
```

---

## 🔌 Step 5: Master Multi-Tier Navigation Router

### Coordinating All 4 Modules — `WhatsAppApp.kt`

We now connect the full application navigation tree in `WhatsAppApp.kt`:
1. `selectedTab`: Chats, Status, Calls.
2. `activeChat`: Conversation Detail view.
3. `activeViewingStatus`: Fullscreen Story viewer.
4. `activeViewingContact`: Contact Profile & Info screen.
5. `activeViewingMedia`: Fullscreen Pinch-to-Zoom media viewer.

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
import com.example.whatsappclone.data.Contact
import com.example.whatsappclone.data.FakeData
import com.example.whatsappclone.data.SharedMediaItem
import com.example.whatsappclone.data.UserStatus
import com.example.whatsappclone.ui.calls.CallsScreen
import com.example.whatsappclone.ui.chatdetail.ChatDetailScreen
import com.example.whatsappclone.ui.chatlist.ChatListScreen
import com.example.whatsappclone.ui.chatlist.WhatsAppTab
import com.example.whatsappclone.ui.chatlist.WhatsAppTabs
import com.example.whatsappclone.ui.profile.ContactInfoScreen
import com.example.whatsappclone.ui.profile.FullscreenMediaViewer
import com.example.whatsappclone.ui.status.StatusScreen
import com.example.whatsappclone.ui.status.StatusViewerScreen
import com.example.whatsappclone.ui.theme.WhatsAppGreen

@Composable
fun WhatsAppApp() {
    // 📌 Navigation Stack States
    var activeChat: ChatItem? by remember { mutableStateOf(null) }
    var activeViewingStatus: UserStatus? by remember { mutableStateOf(null) }
    var activeViewingContact: Contact? by remember { mutableStateOf(null) }
    var activeViewingMedia: SharedMediaItem? by remember { mutableStateOf(null) }

    var selectedTab by remember { mutableStateOf(WhatsAppTab.CHATS) }
    val totalUnread = remember { FakeData.chatList.sumOf { it.unreadCount } }

    // ─── 1. FULLSCREEN PINCH-TO-ZOOM MEDIA VIEWER ──────────────
    if (activeViewingMedia != null) {
        FullscreenMediaViewer(
            mediaItem = activeViewingMedia!!,
            senderName = activeViewingContact?.name ?: activeChat?.contact?.name ?: "Media",
            onClose = { activeViewingMedia = null }
        )
        return
    }

    // ─── 2. CONTACT PROFILE & INFO SCREEN ──────────────────────
    if (activeViewingContact != null) {
        ContactInfoScreen(
            contact = activeViewingContact!!,
            onBackClick = { activeViewingContact = null },
            onMediaClick = { selectedMedia -> activeViewingMedia = selectedMedia }
        )
        return
    }

    // ─── 3. FULLSCREEN STORY VIEWER ────────────────────────────
    if (activeViewingStatus != null) {
        StatusViewerScreen(
            userStatus = activeViewingStatus!!,
            onClose = { activeViewingStatus = null }
        )
        return
    }

    // ─── 4. CHAT DETAIL CONVERSATION SCREEN ────────────────────
    if (activeChat != null) {
        BackHandler { activeChat = null }
        ChatDetailScreen(
            chat = activeChat!!,
            onBackClick = { activeChat = null }
        )
        return
    }

    // ─── 5. PRIMARY 3-TAB DASHBOARD ────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhatsAppGreen)
    ) {
        WhatsAppTabs(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            unreadChatCount = totalUnread
        )

        when (selectedTab) {
            WhatsAppTab.CHATS -> ChatListScreen(
                onChatClick = { selectedChat -> activeChat = selectedChat }
            )
            WhatsAppTab.STATUS -> StatusScreen(
                onStatusClick = { selectedStatus -> activeViewingStatus = selectedStatus }
            )
            WhatsAppTab.CALLS -> CallsScreen()
        }
    }
}
```

---

## 🔍 Deep Dive: Key Jetpack Compose Mechanics

### 1. Pinch-to-Zoom with `detectTransformGestures`

```kotlin
.pointerInput(Unit) {
    detectTransformGestures { _, pan, zoom, _ ->
        scale = (scale * zoom).coerceIn(1f, 5f)
        if (scale > 1f) {
            offset += pan
        } else {
            offset = Offset.Zero
        }
    }
}
```
- `detectTransformGestures` handles multi-touch events without external libraries.
- Scaling is constrained via `coerceIn(1f, 5f)` to prevent inverted or infinite zoom.
- Pan translations are applied only while the image is zoomed in (`scale > 1f`), snapping back cleanly when zoomed out.

---

### 2. Flexible Slot APIs for Setting Cells

By leveraging Kotlin trailing lambdas (`trailingContent: (@Composable () -> Unit)?`), a single `ProfileOptionRow` accommodates:
- Toggles: `trailingContent = { Switch(...) }`
- Subtitles & text status: `trailingContent = { Text("Off") }`
- Simple navigation rows: `trailingContent = null`

---

### 3. Multi-Tier State-Driven Navigation Stack

Rather than relying on heavier navigation libraries, modal overlays are evaluated in priority sequence:
1. `activeViewingMedia` (Topmost lightbox)
2. `activeViewingContact` (Profile view)
3. `activeViewingStatus` (Story viewer)
4. `activeChat` (Conversation view)
5. Primary Dashboard Tabs (`Chats`, `Status`, `Calls`)

Each modal registers its own `BackHandler`, peeling off one layer at a time upon back gestures!

---

## 🧠 Jetpack Compose Principles Applied: Where & Why

| Compose Concept | Where It Is Used | Engineering Rationale |
| :--- | :--- | :--- |
| **`detectTransformGestures`** | `FullscreenMediaViewer` | Implements pinch-to-zoom scaling and panning. |
| **`detectTapGestures`** | `FullscreenMediaViewer` | Supports quick double-tap zoom toggles ($1\times \leftrightarrow 2.5\times$). |
| **`graphicsLayer`** | `FullscreenMediaViewer` | Performs hardware-accelerated transforms without triggering recomposition loops. |
| **Slot-based APIs** | `ProfileOptionRow` | Maximizes composable reusability for diverse profile settings. |
| **`SwitchDefaults.colors`** | Notifications row | Customizes Material 3 switch thumb and track tokens to WhatsApp brand colors. |
| **Multi-tier conditional rendering** | `WhatsAppApp` | Creates a lightweight, type-safe navigation stack without external dependencies. |
| **`border` & `CircleShape`** | Profile quick action buttons | Outlines compact circular action buttons with precise borders. |

---

## 🧪 Self-Assessment & Knowledge Check

Test your mastery of advanced UI architecture:

### 1. Why does `FullscreenMediaViewer` apply zoom transforms using `Modifier.graphicsLayer` rather than modifying `Modifier.size()`?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
`Modifier.graphicsLayer` operates directly on the RenderNode at the drawing phase without invalidating the layout or measurement phase. Modifying `size()` during gesture gestures triggers continuous, expensive remeasurements and recompositions on every frame, causing dropped frames and stutter.
</details>

---

### 2. How does the multi-tier `if (modal != null) return` routing pattern maintain proper back-stack behavior?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
By ordering modal evaluations from highest priority (deepest overlay) to lowest, each modal composable mounts a `BackHandler` that resets its corresponding state to `null`. When dismissed, composition cascades down to the previous screen in the stack automatically.
</details>

---

### 3. Why is `scale.coerceIn(1f, 5f)` applied during pinch gestures?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
Without `coerceIn(1f, 5f)`, pinching inward would allow negative scaling (inverting the image upside down or shrinking it infinitely), while pinching outward could expand memory consumption beyond GPU texture limits.
</details>

---

### 4. What architectural advantage does a Slot API provide in `ProfileOptionRow`?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
A Slot API decouples the row container layout from the specific trailing widget. Instead of creating distinct composables like `SwitchOptionRow`, `ArrowOptionRow`, and `TextOptionRow`, a single `ProfileOptionRow` accepts any composable lambda slot, dramatically reducing boilerplate.
</details>

---

### 5. Why is `offset` reset to `Offset.Zero` when `scale <= 1f` in `FullscreenMediaViewer`?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
If the user pans the image while zoomed in and then zooms back out to $1\times$, the image should re-center itself cleanly within the viewport rather than remaining offset off-center.
</details>

---

## 🏁 Capstone Checkpoint: Complete WhatsApp UI Clone Checklist

Verify that the complete WhatsApp clone functions across all 4 parts:

- [x] **Part 1 (Chats):**
  - Brand header with Camera, Search, and Overflow.
  - Tab bar with unread message badge count.
  - Recycled conversation list with initials avatars and indented dividers.
- [x] **Part 2 (Detail Chat):**
  - Asymmetric speech-tail message bubbles with cyan read checkmarks (`✓✓`).
  - Expanding pill input bar with dynamic Mic ⇄ Send animation.
  - Responsive soft keyboard lifting via `imePadding()`.
  - Coroutine mock reply engine.
- [x] **Part 3 (Status & Calls):**
  - Custom Canvas-drawn segmented story rings (green unread vs. gray viewed).
  - Dual floating action buttons (pencil for text, camera for media).
  - Fullscreen story viewer with auto-advancing progress timers and pause gestures.
  - Call logs with directional arrows (incoming, outgoing, missed).
- [x] **Part 4 (Profile & Gallery):**
  - Collapsing hero avatar profile header with quick action buttons.
  - Horizontal shared media gallery with counts.
  - Fullscreen lightbox viewer with pinch-to-zoom and pan gestures.
  - Interactive notification switches and end-to-end encryption badges.
  - Danger zone actions (Block / Report).

---

## 🚀 Looking Ahead: Transitioning to Phase 5 (Architecture)

Congratulations on completing **Phase 4: Real UI Engineering (WhatsApp Clone)**! 

You have mastered:
- Custom Canvas trigonometry and arc drawing.
- Multi-gesture pointer input (tap, hold, pinch, pan).
- Dense responsive layout engineering with weights, slots, and modifier chains.
- Window insets, soft keyboard padding, and hardware back-stack dispatching.

In **Phase 5 (Architecture)**, we will take these visual engineering skills and connect them to production architectural backbones: **MVVM & MVI patterns**, **Android Architecture Components (ViewModel, StateFlow, SharedFlow)**, **Room Local Database Persistence**, **Dependency Injection with Hilt**, and **Navigation Compose with NavHost**.
