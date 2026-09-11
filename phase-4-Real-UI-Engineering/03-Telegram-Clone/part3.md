# ✈️ Telegram UI Clone — Part 3: Navigation Drawer, Multi-Account Switcher & Attachment Bottom Sheet

> **🎯 What You Will Build:** The hallmark Telegram **Navigation Drawer (`ModalNavigationDrawer`)** featuring an expandable **Multi-Account Switcher** with smooth chevron rotation, system night-mode toggle, custom menu items with badges, and the Telegram **Attachment Modal Bottom Sheet (`ModalBottomSheet`)** containing a horizontal mini-media roll and a 2-row action grid (Gallery, Files, Location, Poll, Contact, Audio).

---

## 📋 Table of Contents

- [Overview — What We're Building](#-overview--what-were-building)
  - [Visual Layout Architecture](#-visual-layout-architecture)
  - [Core Engineering Concepts Introduced](#-core-engineering-concepts-introduced)
- [Telegram Clone Roadmap — Where We Stand](#-telegram-clone-roadmap--where-we-stand)
- [Target File Structure](#-target-file-structure)
- [Step 0: Models & State Definitions](#-step-0-models--state-definitions)
  - [1. Drawer & Attachment Models — data/DrawerModels.kt](#-1-drawer--attachment-models--datadrawermodelskt)
  - [2. Mock Accounts & Media Dataset — data/DrawerFakeData.kt](#-2-mock-accounts--media-dataset--datadrawerfakedatakt)
- [Step 1: Telegram Drawer Header with Multi-Account Switcher](#-step-1-telegram-drawer-header-with-multi-account-switcher)
  - [Header Hierarchy & Layout Mechanics](#-header-hierarchy--layout-mechanics)
  - [Chevron Animation & Expandable Accounts List](#-chevron-animation--expandable-accounts-list)
  - [Implementation — ui/drawer/TelegramDrawerHeader.kt](#-implementation--uidrawertelegramdrawerheaderkt)
- [Step 2: Drawer Navigation Items & Night Mode Switch](#-step-2-drawer-navigation-items--night-mode-switch)
  - [Menu Layout & Telegram Grouping](#-menu-layout--telegram-grouping)
  - [Implementation — ui/drawer/TelegramDrawerBody.kt](#-implementation--uidrawertelegramdrawerbodykt)
- [Step 3: Complete Navigation Drawer Wrapper](#-step-3-complete-navigation-drawer-wrapper)
  - [ModalNavigationDrawer Scaffold Architecture](#-modalnavigationdrawer-scaffold-architecture)
  - [Implementation — ui/drawer/TelegramNavigationDrawer.kt](#-implementation--uidrawertelegramnavigationdrawerkt)
- [Step 4: Telegram Attachment Modal Bottom Sheet](#-step-4-telegram-attachment-modal-bottom-sheet)
  - [Attachment Sheet Visual Anatomy](#-attachment-sheet-visual-anatomy)
  - [Horizontal Recent Media Preview Row](#-horizontal-recent-media-preview-row)
  - [Circular Action Grid Tiles](#-circular-action-grid-tiles)
  - [Implementation — ui/chat/TelegramAttachmentBottomSheet.kt](#-implementation--uichattelegramattachmentbottomsheetkt)
- [Step 5: Full Application Integration](#-step-5-full-application-integration)
  - [Connecting Drawer & Attachment Sheet to TelegramApp.kt](#-connecting-drawer--attachment-sheet-to-telegramappkt)
- [🔍 Deep Dive: Advanced Jetpack Compose Mechanics](#-deep-dive-advanced-jetpack-compose-mechanics)
  - [1. ModalNavigationDrawer vs Standard Scaffold Drawer](#-1-modalnavigationdrawer-vs-standard-scaffold-drawer)
  - [2. Coordinated Rotation & Height Expansion](#-2-coordinated-rotation--height-expansion)
  - [3. Material 3 ModalBottomSheet & Window Inset Clearance](#-3-material-3-modalbottomsheet--window-inset-clearance)
  - [4. Coroutine Scope Scrim Dismissal & Predictive Back](#-4-coroutine-scope-scrim-dismissal--predictive-back)
- [🧠 Jetpack Compose Principles Applied: Where & Why](#-jetpack-compose-principles-applied-where--why)
- [🧪 Self-Assessment & Knowledge Check](#-self-assessment--knowledge-check)
- [🏁 Checkpoint: What You Should Have Working](#-checkpoint-what-you-should-have-working)
- [🏋️ Hands-On Coding Exercises](#-hands-on-coding-exercises)

---

## 📱 Overview — What We're Building

Telegram is renowned across mobile platforms for two signature UI elements:
1. **The Navigation Drawer:** Unlike standard Android drawers that simply show a list of text links, Telegram's drawer hosts a rich profile header with a collapsible **Multi-Account Switcher** (allowing users to instantly hop between multiple phone numbers/identities with a rotating chevron), followed by grouped menu items with unread count badges and a quick-action dark mode switch.
2. **The Attachment Bottom Sheet:** Tapping the paperclip (`📎`) does not bring up a generic system file picker. Instead, it slides up an elegant modal bottom sheet featuring a horizontal filmstrip of recent photos and a clean 2x3 or 2x4 grid of rounded colorful icons (Gallery, File, Location, Poll, Contact, Audio).

### 🖼️ Visual Layout Architecture

#### 1. Navigation Drawer (Left Edge Swipe / Hamburger Tap)
```text
┌───────────────────────────────────────┬──────────────┐
│ [Avatar]   Ankit Choudhary     🌙 ⌄   │              │
│            +91 98765 43210            │              │
│ ───────────────────────────────────── │              │  ← Expandable Account Tray
│   [Avatar 2] Work Profile             │              │     (Revealed when chevron ⌄ is clicked)
│   [ + ]      Add Account              │              │
├───────────────────────────────────────┤              │
│  👥  New Group                        │              │
│  👤  Contacts                         │   Dimmed     │
│  📞  Calls                            │   Scrim      │
│  🔖  Saved Messages                   │   Overlay    │
│  ⚙️  Settings                          │              │
│ ───────────────────────────────────── │              │
│  👥  Invite Friends                   │              │
│  ❓  Telegram Features                │              │
└───────────────────────────────────────┴──────────────┘
```

#### 2. Attachment Modal Bottom Sheet (Chat Paperclip Tap)
```text
┌─────────────────────────────────────────────────────────────┐
│                           ───                               │  ← Drag Handle
│  ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐                    │
│  │📷/📸│ │🖼️ 1 │ │🖼️ 2 │ │🖼️ 3 │ │🖼️ 4 │  ... (Horizontal) │  ← Recent Media Filmstrip
│  └─────┘ └─────┘ └─────┘ └─────┘ └─────┘                    │
│                                                             │
│       ┌───┐             ┌───┐             ┌───┐             │
│       │🖼️ │             │📄 │             │📍 │             │  ← 2x3 Circular Action Grid
│      Gallery            File            Location            │
│                                                             │
│       ┌───┐             ┌───┐             ┌───┐             │
│       │📊 │             │👤 │             │🎵 │             │
│       Poll             Contact            Music             │
└─────────────────────────────────────────────────────────────┘
```

---

## 🧭 Telegram Clone Roadmap — Where We Stand

To give you complete visibility over this series:

| Part | Focus & Modules | Status |
| :--- | :--- | :--- |
| **Part 1** | Top Bar, Search Expansion Animation, Chat List, Scroll Detection & FAB | ✅ **Completed** |
| **Part 2** | Inverted Chat Feed (`reverseLayout`), Mint Bubbles, Input Bar & Auto-Reply Engine | ✅ **Completed** |
| **Part 3** | **Navigation Drawer, Multi-Account Switcher & Attachment Modal Bottom Sheet** | 🚀 **CURRENT PART** |
| **Part 4** | **Channels & Supergroups, Floating Message Reactions, Voice Note Gesture & Settings** | ⏳ **1 PART LEFT (Finale)** |

> **Summary:** After completing Part 3, there is **exactly 1 part remaining** (Part 4) to conclude the entire Telegram UI Engineering course!

---

## 📁 Target File Structure

```text
app/src/main/java/com/example/telegramclone/
├── MainActivity.kt
├── TelegramApp.kt                         # Updated to wire Drawer & Attachment Sheet
├── data/
│   ├── Models.kt                          # (From Part 1 & 2) Chat & Message Models
│   ├── FakeData.kt                        # (From Part 1 & 2)
│   ├── DrawerModels.kt                    # [NEW] TelegramUserAccount, DrawerItem, AttachmentAction
│   └── DrawerFakeData.kt                  # [NEW] Mock accounts, media items & attachment actions
├── ui/
│   ├── theme/
│   │   ├── Color.kt                       # Telegram Sky Blue, Navy, Mint & Attachment Palette
│   │   └── Theme.kt
│   ├── components/
│   │   ├── TelegramTopAppBar.kt           # (From Part 1)
│   │   ├── SearchBarComponent.kt          # (From Part 1)
│   │   └── TelegramChatItem.kt            # (From Part 1)
│   ├── chat/
│   │   ├── ChatScreen.kt                  # (From Part 2 - updated with onAttachClick)
│   │   ├── TelegramChatTopBar.kt          # (From Part 2)
│   │   ├── TelegramMessageBubble.kt       # (From Part 2)
│   │   ├── TelegramChatInputBar.kt        # (From Part 2)
│   │   ├── TelegramDatePill.kt            # (From Part 2)
│   │   └── TelegramAttachmentBottomSheet.kt # [NEW] ModalBottomSheet with gallery & action grid
│   └── drawer/
│       ├── TelegramDrawerHeader.kt        # [NEW] Account switch dropdown with rotating chevron
│       ├── TelegramDrawerBody.kt          # [NEW] Drawer item rows, dividers & theme toggle
│       └── TelegramNavigationDrawer.kt    # [NEW] ModalNavigationDrawer container
```

---

## 📦 Step 0: Models & State Definitions

Let's model the data structures needed for account switching, drawer menu navigation, and attachment actions.

### 1. Drawer & Attachment Models — `data/DrawerModels.kt`

Create `data/DrawerModels.kt` to represent:
- A user account (for multi-account support).
- A drawer menu option with icon, label, and optional badge count.
- An attachment action tile with title, icon, and background gradient colors.
- A recent gallery media preview item.

```kotlin
package com.example.telegramclone.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Represents a registered Telegram account available in the multi-account switcher.
 */
data class TelegramAccount(
    val id: String,
    val fullName: String,
    val phoneNumber: String,
    val initials: String,
    val avatarColor: Color,
    val isActive: Boolean = false,
    val unreadCount: Int = 0
)

/**
 * Represents a menu item within the Telegram navigation drawer.
 */
data class DrawerMenuItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val badgeCount: Int = 0,
    val isDividerAfter: Boolean = false
)

/**
 * Represents an action tile in the attachment modal bottom sheet (e.g. Gallery, Location).
 */
data class AttachmentAction(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val backgroundColor: Color
)

/**
 * Represents a thumbnail item in the recent media horizontal preview strip.
 */
data class RecentMediaItem(
    val id: String,
    val durationLabel: String? = null, // e.g. "0:42" for video, null for photo
    val placeholderColor: Color,
    val isCameraTile: Boolean = false
)
```

---

### 2. Mock Accounts & Media Dataset — `data/DrawerFakeData.kt`

Create `data/DrawerFakeData.kt` to provide realistic mock data:

```kotlin
package com.example.telegramclone.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.ui.graphics.Color

object DrawerFakeData {

    val accounts = listOf(
        TelegramAccount(
            id = "acc_1",
            fullName = "Ankit Choudhary",
            phoneNumber = "+91 98765 43210",
            initials = "AC",
            avatarColor = Color(0xFF0088CC),
            isActive = true
        ),
        TelegramAccount(
            id = "acc_2",
            fullName = "Ankit (Work Profile)",
            phoneNumber = "+91 91234 56789",
            initials = "AW",
            avatarColor = Color(0xFFE56767),
            isActive = false,
            unreadCount = 14
        ),
        TelegramAccount(
            id = "acc_3",
            fullName = "Open Source / Dev",
            phoneNumber = "+1 415 555 0199",
            initials = "OS",
            avatarColor = Color(0xFF50B848),
            isActive = false,
            unreadCount = 3
        )
    )

    val drawerMenuItems = listOf(
        DrawerMenuItem(
            id = "new_group",
            title = "New Group",
            icon = Icons.Default.Group
        ),
        DrawerMenuItem(
            id = "contacts",
            title = "Contacts",
            icon = Icons.Default.Person
        ),
        DrawerMenuItem(
            id = "calls",
            title = "Calls",
            icon = Icons.Default.Call,
            badgeCount = 2
        ),
        DrawerMenuItem(
            id = "saved_messages",
            title = "Saved Messages",
            icon = Icons.Default.Bookmark
        ),
        DrawerMenuItem(
            id = "settings",
            title = "Settings",
            icon = Icons.Default.Settings,
            isDividerAfter = true // Visual separator between account actions and app actions
        ),
        DrawerMenuItem(
            id = "invite_friends",
            title = "Invite Friends",
            icon = Icons.Default.PersonAdd
        ),
        DrawerMenuItem(
            id = "telegram_features",
            title = "Telegram Features",
            icon = Icons.Default.HelpOutline
        )
    )

    val attachmentActions = listOf(
        AttachmentAction(
            id = "gallery",
            title = "Gallery",
            icon = Icons.Default.Image,
            backgroundColor = Color(0xFF2CA5E0) // Sky Blue
        ),
        AttachmentAction(
            id = "file",
            title = "File",
            icon = Icons.Default.Description,
            backgroundColor = Color(0xFF007EE5) // Rich Blue
        ),
        AttachmentAction(
            id = "location",
            title = "Location",
            icon = Icons.Default.LocationOn,
            backgroundColor = Color(0xFF00C853) // Green
        ),
        AttachmentAction(
            id = "poll",
            title = "Poll",
            icon = Icons.Default.Poll,
            backgroundColor = Color(0xFFFF9100) // Vibrant Orange
        ),
        AttachmentAction(
            id = "contact",
            title = "Contact",
            icon = Icons.Default.Person,
            backgroundColor = Color(0xFF00BCD4) // Cyan
        ),
        AttachmentAction(
            id = "music",
            title = "Music",
            icon = Icons.Default.MusicNote,
            backgroundColor = Color(0xFFE040FB) // Magenta
        )
    )

    val recentMediaList = listOf(
        RecentMediaItem(id = "cam", placeholderColor = Color(0xFF333333), isCameraTile = true),
        RecentMediaItem(id = "m1", placeholderColor = Color(0xFF546E7A)),
        RecentMediaItem(id = "m2", durationLabel = "0:35", placeholderColor = Color(0xFF78909C)),
        RecentMediaItem(id = "m3", placeholderColor = Color(0xFF8D6E63)),
        RecentMediaItem(id = "m4", placeholderColor = Color(0xFF5C6BC0)),
        RecentMediaItem(id = "m5", durationLabel = "1:12", placeholderColor = Color(0xFF26A69A)),
        RecentMediaItem(id = "m6", placeholderColor = Color(0xFFFFA726))
    )
}
```

---

## 🗂️ Step 1: Telegram Drawer Header with Multi-Account Switcher

In Telegram on Android, the drawer header displays:
1. The active user's round avatar with their initials.
2. A night mode quick toggle (moon / sun icon) at the top right.
3. The user's bold display name and phone number.
4. An expandable chevron (`⌄`) on the right of the name row. When tapped:
   - The chevron smoothly rotates 180° (`0f` -> `180f`).
   - The accounts list smoothly expands downward with `AnimatedVisibility(expandVertically + fadeIn)`.
   - Each secondary account shows its avatar, name, and unread bubble.
   - An **"Add Account"** row appears with a `+` icon.

```text
┌─────────────────────────────────────────────────────────────┐
│  [ (AC) ]                                            🌙     │  ← Avatar & Dark Theme Toggle
│                                                             │
│  Ankit Choudhary                                            │
│  +91 98765 43210                                     ^      │  ← Rotating Chevron
├─────────────────────────────────────────────────────────────┤
│  [ (AW) ]  Ankit (Work Profile)                 [14]        │  ← Collapsible Account 2
│  [ (OS) ]  Open Source / Dev                     [3]        │  ← Collapsible Account 3
│  [  +   ]  Add Account                                      │  ← Add Account Tile
└─────────────────────────────────────────────────────────────┘
```

### Implementation — `ui/drawer/TelegramDrawerHeader.kt`

```kotlin
package com.example.telegramclone.ui.drawer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.telegramclone.data.TelegramAccount

/**
 * Top header of the Telegram Navigation Drawer.
 * Contains user info, night-mode switch, and expandable multi-account picker.
 */
@Composable
fun TelegramDrawerHeader(
    accounts: List<TelegramAccount>,
    selectedAccountId: String,
    isExpanded: Boolean,
    isDarkTheme: Boolean,
    onToggleExpand: () -> Unit,
    onSelectAccount: (String) -> Unit,
    onToggleDarkTheme: () -> Unit,
    onAddAccountClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeAccount = accounts.find { it.id == selectedAccountId } ?: accounts.first()

    // Smooth 180-degree rotation animation for the chevron
    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "ChevronRotation"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF517DA2)) // Telegram signature classic drawer header blue
            .padding(top = 16.dp, bottom = 8.dp)
    ) {
        // ── Top Row: User Avatar & Night Mode Toggle ─────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Large circular profile avatar
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(activeAccount.avatarColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = activeAccount.initials,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }

            // Quick Night Mode Toggle Button
            IconButton(
                onClick = onToggleDarkTheme,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Night Mode",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Name & Phone Row with Expandable Chevron ─────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleExpand() }
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activeAccount.fullName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = activeAccount.phoneNumber,
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 13.sp
                )
            }

            // Animated Chevron indicator
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Collapse Accounts" else "Expand Accounts",
                tint = Color.White,
                modifier = Modifier
                    .size(28.dp)
                    .rotate(chevronRotation)
            )
        }

        // ── Expandable Multi-Account List ────────────────────────────────────
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
            exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(animationSpec = tween(200))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF456B8D)) // Slightly deeper tone for account tray
                    .padding(vertical = 4.dp)
            ) {
                // Secondary non-active accounts
                accounts.filter { it.id != selectedAccountId }.forEach { account ->
                    AccountRow(
                        account = account,
                        onClick = { onSelectAccount(account.id) }
                    )
                }

                // "Add Account" Action Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAddAccountClick() }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Account",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "Add Account",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Individual account row rendered inside the expanded multi-account tray.
 */
@Composable
private fun AccountRow(
    account: TelegramAccount,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Circular mini avatar
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(account.avatarColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = account.initials,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = account.fullName,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        // Unread Badge (if any)
        if (account.unreadCount > 0) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF00C853) // Telegram bright green account badge
            ) {
                Text(
                    text = account.unreadCount.toString(),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                )
            }
        }
    }
}
```

---

## 📋 Step 2: Drawer Navigation Items & Night Mode Switch

Below the header, the Telegram drawer features an icon-and-label vertical menu. Let's build `TelegramDrawerBody.kt` to handle:
- Standard menu items with muted slate-grey icons.
- Unread call badges.
- Horizontal hairline dividers.
- Clean ripple touch targets adhering to standard 48dp touch heights.

### Implementation — `ui/drawer/TelegramDrawerBody.kt`

```kotlin
package com.example.telegramclone.ui.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.telegramclone.data.DrawerMenuItem

/**
 * Scrollable body containing navigation rows, badges, and separators.
 */
@Composable
fun TelegramDrawerBody(
    menuItems: List<DrawerMenuItem>,
    onItemClick: (DrawerMenuItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 8.dp)
    ) {
        menuItems.forEach { item ->
            DrawerItemRow(
                item = item,
                onClick = { onItemClick(item) }
            )

            // Optional Divider between major sections
            if (item.isDividerAfter) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 0.8.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun DrawerItemRow(
    item: DrawerMenuItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = Color(0xFF707579), // Telegram neutral icon tint
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(28.dp))

        // Title
        Text(
            text = item.title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        // Badge if unread calls or messages exist
        if (item.badgeCount > 0) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF0088CC).copy(alpha = 0.15f)
            ) {
                Text(
                    text = item.badgeCount.toString(),
                    color = Color(0xFF0088CC),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}
```

---

## 🚪 Step 3: Complete Navigation Drawer Wrapper

In Material 3, the canonical implementation of a sliding drawer is `ModalNavigationDrawer`.
It wraps the content (the main chat list) and accepts `drawerContent`, which renders `ModalDrawerSheet`.

### Implementation — `ui/drawer/TelegramNavigationDrawer.kt`

```kotlin
package com.example.telegramclone.ui.drawer

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.example.telegramclone.data.DrawerMenuItem
import com.example.telegramclone.data.TelegramAccount
import kotlinx.coroutines.launch

/**
 * Full Telegram Navigation Drawer container enclosing the app's main viewport.
 */
@Composable
fun TelegramNavigationDrawer(
    drawerState: DrawerState,
    accounts: List<TelegramAccount>,
    selectedAccountId: String,
    menuItems: List<DrawerMenuItem>,
    isDarkTheme: Boolean,
    onSelectAccount: (String) -> Unit,
    onToggleDarkTheme: () -> Unit,
    onMenuItemClick: (DrawerMenuItem) -> Unit,
    onAddAccountClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isAccountSwitcherExpanded by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(300.dp) // Telegram standard drawer width
                    .fillMaxHeight(),
                drawerShape = RectangleShape // Telegram drawer has square edges on modern Android
            ) {
                // Top Header (Profile + Account Switcher)
                TelegramDrawerHeader(
                    accounts = accounts,
                    selectedAccountId = selectedAccountId,
                    isExpanded = isAccountSwitcherExpanded,
                    isDarkTheme = isDarkTheme,
                    onToggleExpand = { isAccountSwitcherExpanded = !isAccountSwitcherExpanded },
                    onSelectAccount = { accId ->
                        onSelectAccount(accId)
                        isAccountSwitcherExpanded = false
                        coroutineScope.launch { drawerState.close() }
                    },
                    onToggleDarkTheme = onToggleDarkTheme,
                    onAddAccountClick = {
                        onAddAccountClick()
                        coroutineScope.launch { drawerState.close() }
                    }
                )

                // Navigation Items List
                TelegramDrawerBody(
                    menuItems = menuItems,
                    onItemClick = { item ->
                        coroutineScope.launch { drawerState.close() }
                        onMenuItemClick(item)
                    }
                )
            }
        },
        content = content
    )
}
```

---

## 📎 Step 4: Telegram Attachment Modal Bottom Sheet

When a user taps the paperclip icon (`📎`) in the chat input bar, Telegram reveals its rich **Attachment Sheet**. 

Key structural aspects:
1. **Material 3 `ModalBottomSheet`**: Provides hardware-accelerated swipe-down gestures, native scrim dimming, and automatic window insets handling.
2. **Top Drag Handle**: A discreet rounded pill indicating drag affordance.
3. **Recent Media Filmstrip (`LazyRow`)**:
   - Camera tile with camera icon and live capture hint.
   - Recent images and video clips with timestamp badges (e.g., `"0:35"`).
4. **Action Tiles Grid**:
   - 2 rows of 3 vibrant circular icon buttons:
     - **Gallery** (Sky Blue `#2CA5E0`)
     - **File** (Rich Blue `#007EE5`)
     - **Location** (Emerald Green `#00C853`)
     - **Poll** (Bright Orange `#FF9100`)
     - **Contact** (Cyan `#00BCD4`)
     - **Music** (Magenta `#E040FB`)

### Implementation — `ui/chat/TelegramAttachmentBottomSheet.kt`

```kotlin
package com.example.telegramclone.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
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
import com.example.telegramclone.data.AttachmentAction
import com.example.telegramclone.data.RecentMediaItem

/**
 * Telegram Modal Bottom Sheet displaying mini-gallery filmstrip and action tiles.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelegramAttachmentBottomSheet(
    sheetState: SheetState,
    recentMedia: List<RecentMediaItem>,
    actions: List<AttachmentAction>,
    onDismissRequest: () -> Unit,
    onActionClick: (AttachmentAction) -> Unit,
    onMediaClick: (RecentMediaItem) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding() // Ensures bottom sheet clears Android system navigation bar
                .padding(bottom = 20.dp)
        ) {
            // ── 1. Recent Media Horizontal Filmstrip ─────────────────────────
            Text(
                text = "Recent media",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recentMedia, key = { it.id }) { media ->
                    MediaThumbnailTile(
                        item = media,
                        onClick = { onMediaClick(media) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── 2. Action Icons Grid (2 Rows x 3 Columns) ───────────────────
            val row1 = actions.take(3)
            val row2 = actions.drop(3).take(3)

            ActionGridRow(actions = row1, onActionClick = onActionClick)
            Spacer(modifier = Modifier.height(18.dp))
            ActionGridRow(actions = row2, onActionClick = onActionClick)
        }
    }
}

/**
 * Individual media preview box (Camera icon or simulated photo thumbnail).
 */
@Composable
private fun MediaThumbnailTile(
    item: RecentMediaItem,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(76.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(item.placeholderColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (item.isCameraTile) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Take Photo",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        } else {
            // If it's a video, render the duration overlay pill
            item.durationLabel?.let { duration ->
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(4.dp)
                ) {
                    Text(
                        text = duration,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
        }
    }
}

/**
 * Renders a row of circular action tiles evenly distributed horizontally.
 */
@Composable
private fun ActionGridRow(
    actions: List<AttachmentAction>,
    onActionClick: (AttachmentAction) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        actions.forEach { action ->
            AttachmentActionTile(
                action = action,
                onClick = { onActionClick(action) }
            )
        }
    }
}

/**
 * Single circular button with label below it (Telegram signature attachment button).
 */
@Composable
private fun AttachmentActionTile(
    action: AttachmentAction,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        // Vibrant circle background
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(action.backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.title,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Label
        Text(
            text = action.title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
```

---

## 🔗 Step 5: Full Application Integration

Let's now update `TelegramApp.kt` to coordinate the entire application:
1. Wrap the home screen inside `TelegramNavigationDrawer`.
2. Connect the top bar's Hamburger Menu button to `coroutineScope.launch { drawerState.open() }`.
3. Switch active accounts dynamically when tapped.
4. Support opening the `TelegramAttachmentBottomSheet` when the chat input bar's paperclip (`📎`) is clicked.
5. Provide a Snackbar feedback message when attachment options or drawer actions are selected.

### Updated `TelegramApp.kt`

```kotlin
package com.example.telegramclone

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.telegramclone.data.Chat
import com.example.telegramclone.data.DrawerFakeData
import com.example.telegramclone.data.FakeData
import com.example.telegramclone.ui.chat.ChatScreen
import com.example.telegramclone.ui.chat.TelegramAttachmentBottomSheet
import com.example.telegramclone.ui.components.TelegramChatItem
import com.example.telegramclone.ui.components.TelegramFab
import com.example.telegramclone.ui.components.TelegramTopAppBar
import com.example.telegramclone.ui.drawer.TelegramNavigationDrawer
import com.example.telegramclone.ui.theme.TelegramCloneTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import kotlinx.coroutines.launch

/**
 * Root Application Composable orchestrating:
 * - Navigation Drawer (Part 3)
 * - Multi-Account Switching (Part 3)
 * - Main Searchable Chat List (Part 1)
 * - Inverted Chat Conversation Screen (Part 2)
 * - Attachment Modal Bottom Sheet (Part 3)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelegramApp() {
    var isDarkTheme by remember { mutableStateOf(false) }

    TelegramCloneTheme(darkTheme = isDarkTheme) {
        val coroutineScope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        // ── Drawer State ─────────────────────────────────────────────────────
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        var selectedAccountId by remember { mutableStateOf("acc_1") }

        // ── Navigation & Chat Selection State ────────────────────────────────
        var activeChat by remember { mutableStateOf<Chat?>(null) }
        var searchQuery by remember { mutableStateOf("") }
        var isSearchActive by remember { mutableStateOf(false) }

        // ── Attachment Bottom Sheet State ────────────────────────────────────
        var showAttachmentSheet by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        // ── Main Drawer Shell ────────────────────────────────────────────────
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
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Clicked: ${item.title}")
                }
            },
            onAddAccountClick = {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Add Account flow triggered")
                }
            }
        ) {
            // Primary Screen Content
            if (activeChat != null) {
                // Render Conversation Screen (From Part 2)
                ChatScreen(
                    chat = activeChat!!,
                    onBackClick = { activeChat = null },
                    onAttachClick = { showAttachmentSheet = true }
                )
            } else {
                // Render Main Chat List (From Part 1)
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
                        TelegramFab(listState = listState, onClick = {})
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
                                onClick = { activeChat = chat }
                            )
                        }
                    }
                }
            }

            // ── Attachment Bottom Sheet (Overlaid when active) ───────────────
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
                            snackbarHostState.showSnackbar("Selected photo/video ${media.id}")
                        }
                    }
                )
            }
        }
    }
}
```

---

## 🔍 Deep Dive: Advanced Jetpack Compose Mechanics

### 1. `ModalNavigationDrawer` vs Standard Scaffold Drawer

In older Compose versions (Material 2), drawers were bundled directly into `Scaffold(drawerContent = ...)`. In modern **Material 3**, drawers are distinct composables (`ModalNavigationDrawer`, `DismissibleNavigationDrawer`, `PermanentNavigationDrawer`):

```kotlin
ModalNavigationDrawer(
    drawerState = drawerState,
    gesturesEnabled = !isInChatScreen, // Cleanly disable edge gestures when in detail screens
    drawerContent = {
        ModalDrawerSheet(
            modifier = Modifier.width(300.dp),
            drawerShape = RectangleShape
        ) { ... }
    }
) {
    // Application Viewport Content
}
```

#### Why Material 3 separated this:
- **Separation of Concerns:** Avoids inflating heavy drawer hierarchies when you only want a simple top bar scaffold.
- **Adaptive Form Factors:** Allows seamless switching to `PermanentNavigationDrawer` on tablets/foldables without restructuring the `Scaffold`.
- **Granular Gesture Control:** You can set `gesturesEnabled = false` dynamically (e.g. when viewing a full-screen image or when an inverted chat feed needs horizontal swipe gestures).

---

### 2. Coordinated Rotation & Height Expansion

In `TelegramDrawerHeader`, tapping the user row simultaneously triggers two independent animations:
1. `animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)` rotates the chevron icon.
2. `AnimatedVisibility(visible = isExpanded, enter = expandVertically() + fadeIn())` animates the accounts tray.

```kotlin
val chevronRotation by animateFloatAsState(
    targetValue = if (isExpanded) 180f else 0f,
    animationSpec = tween(durationMillis = 300),
    label = "ChevronRotation"
)

Icon(
    imageVector = Icons.Default.KeyboardArrowDown,
    modifier = Modifier.rotate(chevronRotation)
)
```

Because both properties share the exact same `durationMillis = 300` spec, the arrow flip and layout expansion stay perfectly synchronized frame-by-frame with zero jank.

---

### 3. Material 3 `ModalBottomSheet` & Window Inset Clearance

When rendering bottom sheets on edge-to-edge Android devices, navigation bars (gesture handles) can collide with the bottom row of action buttons.

Compose Material 3 addresses this with `navigationBarsPadding()`:

```kotlin
ModalBottomSheet(
    onDismissRequest = onDismissRequest,
    sheetState = sheetState,
    dragHandle = { BottomSheetDefaults.DragHandle() }
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding() // Automatically offsets contents above the system gesture line
            .padding(bottom = 16.dp)
    ) {
        ...
    }
}
```

Furthermore, calling `sheetState.hide()` before setting `showAttachmentSheet = false` ensures the sheet plays its downward slide animation instead of abruptly disappearing from the composition!

```kotlin
coroutineScope.launch {
    sheetState.hide()
    showAttachmentSheet = false // Reset visibility only after dismissal completes
}
```

---

### 4. Coroutine Scope Scrim Dismissal & Predictive Back

Both `DrawerState` and `SheetState` manage their positions as internal coroutine suspend functions (`drawerState.open()`, `drawerState.close()`, `sheetState.hide()`).

When the user taps the scrim (the dimmed area outside the drawer or sheet):
- The Compose framework automatically dispatches `drawerState.close()` or `onDismissRequest()`.
- If the hardware back button or predictive back gesture is triggered, Compose's built-in `BackHandler` inside `ModalNavigationDrawer` and `ModalBottomSheet` intercepts it first, closing the sheet or drawer without exiting the app!

---

## 🧠 Jetpack Compose Principles Applied: Where & Why

| Compose Concept | Where It Is Used | Engineering Rationale |
| :--- | :--- | :--- |
| **`ModalNavigationDrawer`** | `TelegramNavigationDrawer` | Modern Material 3 sliding drawer with edge-swipe detection and scrim dimming. |
| **`ModalBottomSheet`** | `TelegramAttachmentBottomSheet` | Hardware-accelerated bottom modal sheet with drag gestures and system navigation insets. |
| **`animateFloatAsState`** | Account Switcher Chevron | Produces a smooth 180° rotation when expanding/collapsing accounts without layout shifts. |
| **`AnimatedVisibility`** | Multi-Account Tray | Provides fluid vertical height expansion (`expandVertically`) combined with `fadeIn`. |
| **`navigationBarsPadding()`** | Attachment Bottom Sheet | Guarantees bottom action buttons never collide with Android's system navigation bar. |
| **`LazyRow`** | Recent Media Filmstrip | Efficiently recycles horizontal thumbnail views without loading out-of-screen images into memory. |
| **`SnackbarHost`** | `TelegramApp.Scaffold` | Centralized asynchronous user feedback for drawer actions and account switching. |

---

## 🧪 Self-Assessment & Knowledge Check

Test your understanding of navigation drawers and modal bottom sheets:

### 1. In Jetpack Compose Material 3, why is `ModalNavigationDrawer` placed outside `Scaffold` rather than as a parameter inside `Scaffold`?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
Material 3 decoupled drawers from `Scaffold` to improve architectural flexibility and support adaptive layouts. By keeping `ModalNavigationDrawer` as an independent wrapper, you can conditionally replace it with `PermanentNavigationDrawer` (for desktop/tablet split-screen) or `DismissibleNavigationDrawer` without altering the inner `Scaffold` or screen UI code.
</details>

---

### 2. When dismissing a `ModalBottomSheet` programmatically in response to a button click, why should you call `sheetState.hide()` before setting `showSheet = false`?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
If you immediately toggle `showSheet = false`, the composable is removed from the composition tree instantly, causing the sheet to vanish abruptly without animating. Calling `sheetState.hide()` first awaits the smooth slide-down exit animation, after which you can cleanly update your state to remove the composable.
</details>

---

### 3. What modifier should always be added to the bottom of a `ModalBottomSheet` content column to prevent clipping on edge-to-edge screens?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
`Modifier.navigationBarsPadding()`. This dynamically reads the system navigation bar window insets and adds sufficient bottom padding so that interactive buttons or labels are never obscured by Android's navigation bar or 3-button navigation controls.
</details>

---

### 4. How do you disable edge-swipe gestures for opening the `ModalNavigationDrawer` while the user is inside a child screen (like the Chat Screen)?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
Set `gesturesEnabled = false` on `ModalNavigationDrawer`:
```kotlin
ModalNavigationDrawer(
    drawerState = drawerState,
    gesturesEnabled = activeChat == null, // Enabled ONLY on the root chat list
    ...
)
```
This ensures swiping from the left edge inside the chat conversation does not accidentally reveal the drawer.
</details>

---

### 5. Why is `LazyRow` preferable to a horizontal `Row(Modifier.horizontalScroll())` for the recent media filmstrip in the attachment bottom sheet?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
A standard `Row` with `horizontalScroll()` composes and lays out all items upfront, which causes significant memory spikes and frame drops when dealing with dozens or hundreds of media files. `LazyRow` only composes and renders the thumbnails that are currently visible in the viewport, recycling views as the user scrolls.
</details>

---

## 🏁 Checkpoint: What You Should Have Working

Verify that your Part 3 implementation functions smoothly:

- [x] **Hamburger Trigger:** Tapping the 3-line menu icon in the top app bar smoothly slides out the navigation drawer from the left.
- [x] **Profile Header:** Displays user initials in a bold circular avatar, full name, phone number, and night-mode toggle button.
- [x] **Chevron Rotation:** Tapping the user name row smoothly rotates the chevron arrow 180° (`0f` -> `180f`).
- [x] **Account Switcher Tray:** Expanding the chevron reveals alternative accounts with unread count badges and the "Add Account" option.
- [x] **Account Switching:** Tapping an alternative account updates the active profile, collapses the tray, closes the drawer, and notifies the user via Snackbar.
- [x] **Navigation Menu:** Displays all standard Telegram menu options (New Group, Contacts, Calls, Saved Messages, Settings, Invite Friends) with custom styling and dividers.
- [x] **Attachment Sheet:** Tapping the paperclip (`📎`) in the chat input bar slides up the Telegram Attachment Modal Bottom Sheet.
- [x] **Mini-Media Filmstrip:** Displays a camera tile and horizontal scrollable list of recent media items with video timestamp pills.
- [x] **2x3 Action Grid:** Displays the 6 Telegram circular action buttons (Gallery, File, Location, Poll, Contact, Music) with vibrant background colors.
- [x] **Safe Dismissal:** Dragging down the sheet handle or tapping the dimmed scrim smoothly closes the sheet with native spring physics.

---

## 🏋️ Hands-On Coding Exercises

Take your Jetpack Compose architecture to the next level with these practical exercises:

### 🎯 Exercise 1: Dynamic Account Counter Badge in Top Bar
When multiple accounts are registered and at least one secondary account has unread messages, render a small red or green badge dot directly on the top app bar's hamburger icon (`☰`) to alert the user of unread messages waiting in their other account!

---

### 🎯 Exercise 2: Animated Dark Mode Theme Transition
Wire the `onToggleDarkTheme` callback in `TelegramDrawerHeader` to dynamically swap the Compose `MaterialTheme.colorScheme` between a crisp Telegram Light Palette (White & Sky Blue `#0088CC`) and a Telegram Dark Palette (OLED Black `#0E1621` & Deep Slate `#17212B`) with a smooth animated color transition.

---

### 🎯 Exercise 3: Multi-Select Media Counter in Attachment Sheet
Add a selection checkbox to each thumbnail in the `RecentMediaItem` filmstrip:
- When 1 or more photos are clicked, display a floating blue **"Send (N)"** button at the bottom of the attachment sheet, matching the official Telegram Android media picker behavior!
