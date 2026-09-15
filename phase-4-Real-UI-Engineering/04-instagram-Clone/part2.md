# 📸 Instagram UI Clone — Part 2: Profile Screen, Story Highlights & 3-Column Grid

> **🎯 What You Will Build:** The canonical **Instagram Profile Screen** in Jetpack Compose. You will build a single-scroll container powered by `LazyVerticalGrid` with full-span headers (`GridItemSpan(maxLineSpan)`), numeric stat counters (Posts, Followers, Following), an expandable bio with clickable links and category badges, an interactive Story Highlights carousel with a "New" creation tile, and the famous 3-column media grid with video and carousel indicators.

---

## 📋 Table of Contents

- [Overview — What We're Building](#-overview--what-were-building)
  - [Visual Layout Architecture](#-visual-layout-architecture)
  - [Core Engineering Concepts Introduced](#-core-engineering-concepts-introduced)
- [Target File Structure](#-target-file-structure)
- [Step 0: Models & Mock Dataset Expansion](#-step-0-models--mock-dataset-expansion)
  - [1. Profile & Grid Models — data/ProfileModels.kt](#-1-profile--grid-models--dataprofilemodelskt)
  - [2. Mock Profile Dataset — data/ProfileFakeData.kt](#-2-mock-profile-dataset--dataprofilefakedatakt)
- [Step 1: Profile Top Bar & Account Switcher](#-step-1-profile-top-bar--account-switcher)
  - [Top Bar Layout Structure](#-top-bar-layout-structure)
  - [Implementation — ui/profile/ProfileTopBar.kt](#-implementation--uiprofileprofiletopbarkt)
- [Step 2: Profile Header, Stats Row & Action Buttons](#-step-2-profile-header-stats-row--action-buttons)
  - [Avatar & Numeric Stats Geometry](#-avatar--numeric-stats-geometry)
  - [Bio, Category Tag & Website Link](#-bio-category-tag--website-link)
  - [Action Buttons: "Edit profile" & "Share profile"](#-action-buttons-edit-profile--share-profile)
  - [Implementation — ui/profile/ProfileHeader.kt](#-implementation--uiprofileprofileheaderkt)
- [Step 3: Circular Story Highlights Carousel](#-step-3-circular-story-highlights-carousel)
  - [Highlight Covers & "New" Add Button](#-highlight-covers--new-add-button)
  - [Implementation — ui/profile/StoryHighlightsRow.kt](#-implementation--uiprofilestoryhighlightsrowkt)
- [Step 4: Profile Tab Strip (Grid, Reels, Tagged)](#-step-4-profile-tab-strip-grid-reels-tagged)
  - [Tab Icon Switching & Indicator Styling](#-tab-icon-switching--indicator-styling)
  - [Implementation — ui/profile/ProfileTabRow.kt](#-implementation--uiprofileprofiletabrowkt)
- [Step 5: The Iconic 3-Column Media Grid](#-step-5-the-iconic-3-column-media-grid)
  - [The Single-Scroll LazyVerticalGrid Architecture](#-the-single-scroll-lazyverticalgrid-architecture)
  - [Square Thumbnail Cell with Badge Overlays](#-square-thumbnail-cell-with-badge-overlays)
  - [Implementation — ui/profile/ProfileMediaGrid.kt](#-implementation--uiprofileprofilemediagridkt)
- [Step 6: Master Profile Screen Assembly](#-step-6-master-profile-screen-assembly)
  - [Combining Spanned Headers & Grid Items](#-combining-spanned-headers--grid-items)
  - [Implementation — ui/profile/ProfileScreen.kt](#-implementation--uiprofileprofilescreenkt)
- [Step 7: Master App Routing & Bottom Bar Synchronization](#-step-7-master-app-routing--bottom-bar-synchronization)
  - [Connecting Feed & Profile Tabs — InstagramApp.kt](#-connecting-feed--profile-tabs--instagramappkt)
- [🔍 Deep Dive: Advanced Jetpack Compose Mechanics](#-deep-dive-advanced-jetpack-compose-mechanics)
  - [1. Single-Scroll Architecture with GridItemSpan(maxLineSpan)](#-1-single-scroll-architecture-with-griditemspanmaxlinespan)
  - [2. The Nested Scrolling Anti-Pattern in Compose](#-2-the-nested-scrolling-anti-pattern-in-compose)
  - [3. Clickable URL Links & Mentions via AnnotatedString](#-3-clickable-url-links--mentions-via-annotatedstring)
  - [4. Zero-Overhead Square Grid Cells with aspectRatio(1f)](#-4-zero-overhead-square-grid-cells-with-aspectratio1f)
- [🧠 Jetpack Compose Principles Applied: Where & Why](#-jetpack-compose-principles-applied-where--why)
- [🧪 Self-Assessment & Knowledge Check](#-self-assessment--knowledge-check)
- [🏁 Checkpoint: What You Should Have Working](#-checkpoint-what-you-should-have-working)
- [🏋️ Hands-On Coding Exercises](#-hands-on-coding-exercises)
- [🧭 What's Next in Part 3](#-whats-next-in-part-3)

---

## 📱 Overview — What We're Building

On Instagram, the **Profile Screen** is the personal portfolio of every creator. It brings together several complex layout requirements into a cohesive, perfectly scrolling experience:
1. **The Header & Stats:** A large circular profile photo paired with three side-by-side numeric counters: **Posts**, **Followers**, and **Following**.
2. **Bio & Category Tags:** User's display name, gray niche category (`"Digital Creator"`), multi-line bio with hashtag links, and a clickable external website link.
3. **Action Bar:** Full-width rounded action buttons (**"Edit profile"**, **"Share profile"**) and an icon button for user discovery.
4. **Story Highlights Strip:** Horizontal carousel of saved highlight albums encased in thin gray outline rings, complete with a dashed/outline **"+"** button to create new highlights.
5. **3-Tab Navigation Bar:** Tab buttons switching between **Posts Grid** (`⊞`), **Reels** (`🎬`), and **Tagged Photos** (`👤`).
6. **3-Column Square Media Grid:** An infinite grid of square media thumbnails (`aspectRatio(1f)`) with badges for multi-photo carousels, video reels, and pinned posts.

---

### 🖼️ Visual Layout Architecture

```text
┌─────────────────────────────────────────────────────────────┐
│  🔒  your_username ⌄                                 ➕  ☰  │  ← Top Bar (Account Switcher, Create, Menu)
├─────────────────────────────────────────────────────────────┤
│   ┌───────┐        128           14.2K          482         │  ← Stats Row (Avatar + Posts/Followers/Following)
│   │ [YOU] │       Posts        Followers     Following      │
│   └───────┘                                                 │
│                                                             │
│   Ankit Choudhary                                           │  ← Display Name
│   Digital Creator                                           │  ← Category Tag (Muted Gray)
│   Android & Jetpack Compose Engineer 🚀                     │  ← Bio
│   Building beautiful mobile interfaces                      │
│   🔗 github.com/AnkitChoudhary-1                            │  ← Clickable External Link
│                                                             │
│   ┌─────────────────────┐ ┌─────────────────────┐ ┌─────┐   │
│   │    Edit profile     │ │    Share profile    │ │ 👤+ │   │  ← Profile Action Buttons
│   └─────────────────────┘ └─────────────────────┘ └─────┘   │
├─────────────────────────────────────────────────────────────┤
│   ┌─────┐    ┌─────┐    ┌─────┐    ┌─────┐    ┌─────┐       │
│   │  ➕ │    │ 🏔️  │    │ ☕  │    │ 💻  │    │ ✈️  │  ──→  │  ← Story Highlights Carousel
│   └─────┘    └─────┘    └─────┘    └─────┘    └─────┘       │
│     New      Travel     Coffee      Work      Trips         │
├─────────────────────────────────────────────────────────────┤
│         ⊞ (Grid)              🎬 (Reels)         👤 (Tagged)│  ← Profile Tab Row with Black Indicator
├───────────────┬─────────────────────────────┬───────────────┤
│ ┌───────────┐ │ ┌───────────┐               │ ┌───────────┐ │
│ │ 📌        │ │ │           │               │ │        🎬 │ │
│ │  Post 1   │ │ │  Post 2   │               │ │  Post 3   │ │  ← 3-Column Square Grid
│ └───────────┘ │ └───────────┘               │ └───────────┘ │     (aspectRatio(1f))
│ ┌───────────┐ │ ┌───────────┐               │ ┌───────────┐ │     📌 = Pinned Badge
│ │        ⧉  │ │ │           │               │ │           │ │     🎬 = Video/Reels Badge
│ │  Post 4   │ │ │  Post 5   │               │ │  Post 6   │ │     ⧉  = Carousel Badge
│ └───────────┘ │ └───────────┘               │ └───────────┘ │
└───────────────┴─────────────────────────────┴───────────────┘
```

---

### 🚀 Core Engineering Concepts Introduced

```text
1. Full-Span Grid Headers with GridItemSpan(maxLineSpan):
   • Avoids the catastrophic nested scrolling crash: LazyVerticalGrid inside a LazyColumn.
   • Makes the entire screen a SINGLE LazyVerticalGrid(columns = GridCells.Fixed(3)).
   • Headers, bio, buttons, highlights, and tabs consume all 3 columns (span = 3).

2. Unified Single-Scroll Physics:
   • The entire profile scrolls as one unified surface with standard Android fling physics.
   • Eliminates choppy scroll hitching and scroll-jacking.

3. Media Type Flag Badges:
   • Layering small indicator icons (📌 Pinned, ⧉ Carousel, 🎬 Video) in the top-right
     corner of thumbnail cells with Box(Alignment.TopEnd).

4. Clickable Links with LinkAnnotation:
   • Demonstrates modern Compose 1.7+ text linking for URLs, hashtags, and mentions.
```

---

## 📁 Target File Structure

```text
app/src/main/java/com/example/instagramclone/
├── MainActivity.kt
├── InstagramApp.kt                            # Updated: bottom navigation router (Feed <-> Profile)
├── data/
│   ├── Models.kt                              # (From Part 1)
│   ├── FakeData.kt                            # (From Part 1)
│   ├── ProfileModels.kt                       # [NEW] UserProfile, Highlight, GridPost, ProfileTab
│   └── ProfileFakeData.kt                     # [NEW] Mock profile details, highlights & 18 grid posts
└── ui/
    ├── theme/                                 # (From Part 1)
    ├── components/                            # (From Part 1)
    ├── feed/                                  # (From Part 1)
    ├── bottomnav/                             # (From Part 1)
    └── profile/
        ├── ProfileTopBar.kt                   # [NEW] Account switcher chevron, create & hamburger icons
        ├── ProfileHeader.kt                   # [NEW] Avatar, stats counters, bio & action buttons
        ├── StoryHighlightsRow.kt              # [NEW] Circular highlights carousel + "New" button
        ├── ProfileTabRow.kt                   # [NEW] Grid, Reels, Tagged tab bar with indicator
        ├── ProfileMediaGrid.kt                # [NEW] Square thumbnail cell with badges
        └── ProfileScreen.kt                   # [NEW] Master LazyVerticalGrid assembling everything
```

---

## 📦 Step 0: Models & Mock Dataset Expansion

### 1. Profile & Grid Models — `data/ProfileModels.kt`

Create `data/ProfileModels.kt` to model user profile metadata, story highlights, and grid media items:

```kotlin
package com.example.instagramclone.data

import androidx.compose.ui.graphics.Color

/**
 * Detailed user profile information.
 */
data class UserProfile(
    val username: String,
    val fullName: String,
    val avatarColor: Color,
    val initials: String,
    val category: String = "Digital Creator",
    val bio: String,
    val websiteUrl: String,
    val postsCount: Int,
    val followersCount: Int,
    val followingCount: Int,
    val isPrivate: Boolean = false,
    val isVerified: Boolean = false
)

/**
 * Ephemeral story saved into a permanent circular highlight album on the profile.
 */
data class StoryHighlight(
    val id: String,
    val title: String,
    val coverColor: Color,
    val iconEmoji: String,
    val isCreateNewTile: Boolean = false
)

/**
 * Media type displayed within the 3-column profile grid.
 */
enum class GridMediaType {
    PHOTO,
    VIDEO,
    CAROUSEL
}

/**
 * A post thumbnail rendered inside the 3-column profile grid.
 */
data class GridMediaItem(
    val id: String,
    val thumbnailColor: Color,
    val mediaType: GridMediaType = GridMediaType.PHOTO,
    val isPinned: Boolean = false,
    val likesCount: Int = 0
)

/**
 * Tabs available on the profile screen.
 */
enum class ProfileViewTab {
    GRID,      // Posts grid (⊞)
    REELS,     // Video reels (🎬)
    TAGGED     // Tagged photos (👤)
}
```

---

### 2. Mock Profile Dataset — `data/ProfileFakeData.kt`

Create `data/ProfileFakeData.kt` with realistic mock data:

```kotlin
package com.example.instagramclone.data

import androidx.compose.ui.graphics.Color

object ProfileFakeData {

    val userProfile = UserProfile(
        username = "ankit_dev",
        fullName = "Ankit Choudhary",
        avatarColor = Color(0xFF0088CC),
        initials = "AC",
        category = "Digital Creator",
        bio = "Building high-performance Android UIs in Jetpack Compose 🚀\nOpen source contributor • Clean Architecture\nPassionate about UI/UX & Mobile Engineering",
        websiteUrl = "https://github.com/AnkitChoudhary-1",
        postsCount = 42,
        followersCount = 14250,
        followingCount = 384,
        isPrivate = false,
        isVerified = true
    )

    val storyHighlights = listOf(
        StoryHighlight(id = "new", title = "New", coverColor = Color.Transparent, iconEmoji = "+", isCreateNewTile = true),
        StoryHighlight(id = "h1", title = "Android", coverColor = Color(0xFF3DDC84), iconEmoji = "🤖"),
        StoryHighlight(id = "h2", title = "Compose", coverColor = Color(0xFF4285F4), iconEmoji = "🎨"),
        StoryHighlight(id = "h3", title = "Setup", coverColor = Color(0xFF37474F), iconEmoji = "💻"),
        StoryHighlight(id = "h4", title = "Coffee", coverColor = Color(0xFF8D6E63), iconEmoji = "☕"),
        StoryHighlight(id = "h5", title = "Travel", coverColor = Color(0xFF26A69A), iconEmoji = "✈️"),
        StoryHighlight(id = "h6", title = "Fitness", coverColor = Color(0xFFEF5350), iconEmoji = "💪")
    )

    val gridPosts = listOf(
        GridMediaItem(id = "gp1", thumbnailColor = Color(0xFF5C6BC0), mediaType = GridMediaType.PHOTO, isPinned = true),
        GridMediaItem(id = "gp2", thumbnailColor = Color(0xFF7E57C2), mediaType = GridMediaType.CAROUSEL, isPinned = true),
        GridMediaItem(id = "gp3", thumbnailColor = Color(0xFF26A69A), mediaType = GridMediaType.VIDEO, isPinned = true),
        GridMediaItem(id = "gp4", thumbnailColor = Color(0xFFFFA726), mediaType = GridMediaType.CAROUSEL),
        GridMediaItem(id = "gp5", thumbnailColor = Color(0xFF29B6F6), mediaType = GridMediaType.PHOTO),
        GridMediaItem(id = "gp6", thumbnailColor = Color(0xFFEC407A), mediaType = GridMediaType.VIDEO),
        GridMediaItem(id = "gp7", thumbnailColor = Color(0xFFAB47BC), mediaType = GridMediaType.PHOTO),
        GridMediaItem(id = "gp8", thumbnailColor = Color(0xFF78909C), mediaType = GridMediaType.CAROUSEL),
        GridMediaItem(id = "gp9", thumbnailColor = Color(0xFF8D6E63), mediaType = GridMediaType.PHOTO),
        GridMediaItem(id = "gp10", thumbnailColor = Color(0xFF42A5F5), mediaType = GridMediaType.VIDEO),
        GridMediaItem(id = "gp11", thumbnailColor = Color(0xFF66BB6A), mediaType = GridMediaType.PHOTO),
        GridMediaItem(id = "gp12", thumbnailColor = Color(0xFFFF7043), mediaType = GridMediaType.CAROUSEL),
        GridMediaItem(id = "gp13", thumbnailColor = Color(0xFF5C6BC0), mediaType = GridMediaType.PHOTO),
        GridMediaItem(id = "gp14", thumbnailColor = Color(0xFF8E24AA), mediaType = GridMediaType.PHOTO),
        GridMediaItem(id = "gp15", thumbnailColor = Color(0xFF26C6DA), mediaType = GridMediaType.VIDEO)
    )
}
```

---

## 🧭 Step 1: Profile Top Bar & Account Switcher

The profile top bar displays:
1. A privacy lock indicator (if private) alongside the username and a dropdown chevron (`⌄`) for switching accounts.
2. A Threads logo or notification badge shortcut.
3. The Create Post button (`+` in a square) and the hamburger settings menu (`☰`).

### Implementation — `ui/profile/ProfileTopBar.kt`

```kotlin
package com.example.instagramclone.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.instagramclone.ui.theme.InstagramWhite

/**
 * Top App Bar for the Profile screen featuring:
 * - Username with account switch chevron
 * - Create post icon
 * - Options / Settings menu icon
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(
    username: String,
    isPrivate: Boolean,
    onAccountSwitchClick: () -> Unit,
    onCreatePostClick: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onAccountSwitchClick() }
            ) {
                if (isPrivate) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Private account",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }

                Text(
                    text = username,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Switch accounts",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        actions = {
            IconButton(onClick = onCreatePostClick) {
                Icon(
                    imageVector = Icons.Outlined.AddBox,
                    contentDescription = "Create",
                    tint = Color.Black,
                    modifier = Modifier.size(26.dp)
                )
            }
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu options",
                    tint = Color.Black,
                    modifier = Modifier.size(26.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = InstagramWhite),
        modifier = modifier
    )
}
```

---

## 👤 Step 2: Profile Header, Stats Row & Action Buttons

### Avatar & Numeric Stats Geometry

The top section of the profile features an 80dp circular avatar on the left, paired with 3 evenly distributed statistical columns on the right:

```text
┌─────────────────────────────────────────────────────────────┐
│   ┌───────────┐         128          14.2K          482     │
│   │   80dp    │        Posts       Followers     Following  │
│   │  Avatar   │        Bold          Bold          Bold     │
│   └───────────┘                                             │
└─────────────────────────────────────────────────────────────┘
```

Below this sits:
- **Full Name** in bold (`14sp`).
- **Category Tag** (`"Digital Creator"`) in soft slate gray.
- **Bio Paragraph** supporting multiple lines.
- **External Link** with a chain-link icon (`🔗`) in high-contrast navy/blue.
- **Action Buttons Bar:** `"Edit profile"` (flex weight `1f`), `"Share profile"` (flex weight `1f`), and an icon button for user discovery (`40dp`).

---

### Implementation — `ui/profile/ProfileHeader.kt`

```kotlin
package com.example.instagramclone.ui.profile

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
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import com.example.instagramclone.data.UserProfile
import com.example.instagramclone.ui.theme.InstagramBlue
import com.example.instagramclone.ui.theme.InstagramDarkGray
import com.example.instagramclone.ui.theme.InstagramGray
import com.example.instagramclone.ui.theme.InstagramLightGray

/**
 * Top Profile Section containing:
 * - Avatar with optional "+" badge
 * - Numeric stats (Posts, Followers, Following)
 * - Name, category, multi-line bio, and URL link
 * - "Edit profile" & "Share profile" action buttons
 */
@Composable
fun ProfileHeader(
    profile: UserProfile,
    onEditProfileClick: () -> Unit,
    onShareProfileClick: () -> Unit,
    onDiscoverPeopleClick: () -> Unit,
    onWebsiteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        // ── 1. Avatar & Numeric Stats Row ────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 80dp Profile Avatar with "+" Add Story Badge
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(profile.avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = profile.initials,
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Mini blue "+" story creation badge
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(InstagramBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add story",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 3 Stat Counters: Posts, Followers, Following
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatColumn(count = formatStatNumber(profile.postsCount), label = "Posts")
                StatColumn(count = formatStatNumber(profile.followersCount), label = "Followers")
                StatColumn(count = formatStatNumber(profile.followingCount), label = "Following")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ── 2. Bio & Metadata Section ────────────────────────────────────────
        Text(
            text = profile.fullName,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = profile.category,
            fontSize = 12.sp,
            color = InstagramGray
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = profile.bio,
            fontSize = 13.sp,
            color = InstagramDarkGray,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Clickable External Website Link
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onWebsiteClick(profile.websiteUrl) }
        ) {
            Icon(
                imageVector = Icons.Default.Link,
                contentDescription = "Website",
                tint = Color(0xFF00376B),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = profile.websiteUrl.removePrefix("https://"),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF00376B)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ── 3. Profile Action Buttons ────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileActionButton(
                text = "Edit profile",
                onClick = onEditProfileClick,
                modifier = Modifier.weight(1f)
            )

            ProfileActionButton(
                text = "Share profile",
                onClick = onShareProfileClick,
                modifier = Modifier.weight(1f)
            )

            // Discover people icon button
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFEFEFEF),
                modifier = Modifier
                    .size(34.dp)
                    .clickable { onDiscoverPeopleClick() }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "Discover people",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Individual column displaying a bold numeric tally above a descriptive label.
 */
@Composable
private fun StatColumn(
    count: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color.Black
        )
    }
}

/**
 * Styled rounded Instagram action button (light gray background, black text).
 */
@Composable
private fun ProfileActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFEFEFEF),
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = ButtonDefaults.ContentPadding,
        modifier = modifier.height(34.dp)
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Formats large integers into compact strings (e.g. 14250 -> "14.2K").
 */
private fun formatStatNumber(number: Int): String {
    return when {
        number >= 1_000_000 -> "%.1fM".format(number / 1_000_000.0)
        number >= 10_000 -> "%.1fK".format(number / 1_000.0)
        number >= 1_000 -> "%,d".format(number)
        else -> number.toString()
    }
}
```

---

## 🌟 Step 3: Circular Story Highlights Carousel

Story Highlights are permanently pinned story albums rendered as a horizontal carousel.
Key characteristics:
1. **Thin Ring Border:** Each highlight circle is encased in a delicate `0.8dp` gray border ring with a `2dp` gap from the album cover.
2. **Special "New" Tile:** The first item is a dashed or light gray outline circle with a centered `+` symbol for creating new highlight reels.

### Implementation — `ui/profile/StoryHighlightsRow.kt`

```kotlin
package com.example.instagramclone.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
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
import com.example.instagramclone.data.StoryHighlight

/**
 * Horizontal strip of Story Highlights albums.
 */
@Composable
fun StoryHighlightsRow(
    highlights: List<StoryHighlight>,
    onHighlightClick: (StoryHighlight) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(highlights, key = { it.id }) { highlight ->
            HighlightCircleItem(
                highlight = highlight,
                onClick = { onHighlightClick(highlight) }
            )
        }
    }
}

@Composable
private fun HighlightCircleItem(
    highlight: StoryHighlight,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 2.dp)
    ) {
        if (highlight.isCreateNewTile) {
            // "New" Highlight Creation Circle
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color(0xFFCCCCCC), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add new highlight",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            // Saved Highlight Album (Outer Ring + White Gap + Inner Cover)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color(0xFFDBDBDB), CircleShape)
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(highlight.coverColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = highlight.iconEmoji,
                    fontSize = 24.sp
                )
            }
        }

        Text(
            text = highlight.title,
            fontSize = 11.sp,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
```

---

## 🗂️ Step 4: Profile Tab Strip (Grid, Reels, Tagged)

Instagram's profile tab row features 3 tabs:
1. **Posts Grid (`⊞`):** Displays all photo & video posts in a 3-column square grid.
2. **Reels (`🎬`):** Displays short-form 9:16 vertical video thumbnails.
3. **Tagged Photos (`👤`):** Displays media where other users tagged this profile.

When selected, a high-contrast black underline indicator aligns with the active tab.

### Implementation — `ui/profile/ProfileTabRow.kt`

```kotlin
package com.example.instagramclone.ui.profile

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.outlined.AssignmentInd
import androidx.compose.material.icons.outlined.GridOn
import androidx.compose.material.icons.outlined.SlowMotionVideo
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.instagramclone.data.ProfileViewTab
import com.example.instagramclone.ui.theme.InstagramLightGray

/**
 * 3-Tab Bar switching between Grid, Reels, and Tagged media.
 */
@Composable
fun ProfileTabRow(
    selectedTab: ProfileViewTab,
    onTabSelected: (ProfileViewTab) -> Unit,
    modifier: Modifier = Modifier
) {
    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        containerColor = Color.White,
        contentColor = Color.Black,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                height = 1.5.dp,
                color = Color.Black
            )
        },
        divider = {
            HorizontalDivider(thickness = 0.5.dp, color = InstagramLightGray)
        },
        modifier = modifier.fillMaxWidth()
    ) {
        // Tab 1: Posts Grid
        Tab(
            selected = selectedTab == ProfileViewTab.GRID,
            onClick = { onTabSelected(ProfileViewTab.GRID) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == ProfileViewTab.GRID) Icons.Filled.GridOn else Icons.Outlined.GridOn,
                    contentDescription = "Posts Grid",
                    tint = if (selectedTab == ProfileViewTab.GRID) Color.Black else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            },
            modifier = Modifier.height(44.dp)
        )

        // Tab 2: Reels
        Tab(
            selected = selectedTab == ProfileViewTab.REELS,
            onClick = { onTabSelected(ProfileViewTab.REELS) },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.SlowMotionVideo,
                    contentDescription = "Reels",
                    tint = if (selectedTab == ProfileViewTab.REELS) Color.Black else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            },
            modifier = Modifier.height(44.dp)
        )

        // Tab 3: Tagged Photos
        Tab(
            selected = selectedTab == ProfileViewTab.TAGGED,
            onClick = { onTabSelected(ProfileViewTab.TAGGED) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == ProfileViewTab.TAGGED) Icons.Filled.AssignmentInd else Icons.Outlined.AssignmentInd,
                    contentDescription = "Tagged Photos",
                    tint = if (selectedTab == ProfileViewTab.TAGGED) Color.Black else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            },
            modifier = Modifier.height(44.dp)
        )
    }
}
```

---

## 🔲 Step 5: The Iconic 3-Column Media Grid

### The Single-Scroll `LazyVerticalGrid` Architecture

How do you combine a rich profile header, story highlights, and a 3-column media grid into a single smooth scrollable container?

#### ❌ The Crash-Prone Anti-Pattern:
```kotlin
// 💥 CRASH: Placing a LazyVerticalGrid inside a LazyColumn causes an IllegalStateException!
LazyColumn {
    item { ProfileHeader() }
    item { StoryHighlightsRow() }
    item {
        LazyVerticalGrid(columns = GridCells.Fixed(3)) { ... } // 💥 Infinite height measurement crash!
    }
}
```

#### ✅ The Production Solution: Full-Span Grid Items
In Jetpack Compose, `LazyVerticalGrid` supports full-width items via `GridItemSpan(maxLineSpan)`:

```kotlin
LazyVerticalGrid(
    columns = GridCells.Fixed(3),
    modifier = Modifier.fillMaxSize()
) {
    // ── Headers consume all 3 columns (span = maxLineSpan) ───────────
    item(span = { GridItemSpan(maxLineSpan) }) { ProfileHeader(...) }
    item(span = { GridItemSpan(maxLineSpan) }) { StoryHighlightsRow(...) }
    item(span = { GridItemSpan(maxLineSpan) }) { ProfileTabRow(...) }

    // ── Grid thumbnails naturally consume 1 column each ──────────────
    items(gridPosts, key = { it.id }) { post ->
        GridThumbnailCell(post)
    }
}
```

---

### Implementation — `ui/profile/ProfileMediaGrid.kt`

```kotlin
package com.example.instagramclone.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.instagramclone.data.GridMediaItem
import com.example.instagramclone.data.GridMediaType

/**
 * Square thumbnail cell inside the 3-column media grid.
 * Displays top-right corner badges for:
 * - Pinned posts (PushPin)
 * - Multi-photo carousels (Collections)
 * - Video Reels (PlayArrow)
 */
@Composable
fun GridThumbnailCell(
    post: GridMediaItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f) // Guarantees an exact 1:1 square cell
            .background(post.thumbnailColor)
            .clickable { onClick() }
    ) {
        // Corner Badges Container
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            when {
                post.isPinned -> {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pinned post",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                post.mediaType == GridMediaType.CAROUSEL -> {
                    Icon(
                        imageVector = Icons.Default.Collections,
                        contentDescription = "Photo carousel",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                post.mediaType == GridMediaType.VIDEO -> {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Video",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
```

---

## 📱 Step 6: Master Profile Screen Assembly

### Implementation — `ui/profile/ProfileScreen.kt`

```kotlin
package com.example.instagramclone.ui.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.instagramclone.data.GridMediaItem
import com.example.instagramclone.data.ProfileFakeData
import com.example.instagramclone.data.ProfileViewTab
import com.example.instagramclone.data.StoryHighlight
import com.example.instagramclone.data.UserProfile
import com.example.instagramclone.ui.theme.InstagramGray
import com.example.instagramclone.ui.theme.InstagramWhite

/**
 * Complete Instagram Profile Screen assembled inside a single LazyVerticalGrid.
 */
@Composable
fun ProfileScreen(
    profile: UserProfile = ProfileFakeData.userProfile,
    highlights: List<StoryHighlight> = ProfileFakeData.storyHighlights,
    posts: List<GridMediaItem> = ProfileFakeData.gridPosts,
    onPostClick: (GridMediaItem) -> Unit = {},
    onHighlightClick: (StoryHighlight) -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onShareProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(ProfileViewTab.GRID) }

    Scaffold(
        containerColor = InstagramWhite,
        topBar = {
            ProfileTopBar(
                username = profile.username,
                isPrivate = profile.isPrivate,
                onAccountSwitchClick = { /* Show account switcher bottom sheet */ },
                onCreatePostClick = { /* Open camera/gallery */ },
                onMenuClick = { /* Open settings drawer */ }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(1.5.dp), // Hairline gap between columns
            verticalArrangement = Arrangement.spacedBy(1.5.dp),   // Hairline gap between rows
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            // ── SPAN ITEM 1: Header Stats & Bio (Consumes 3 Columns) ───────────
            item(span = { GridItemSpan(maxLineSpan) }, key = "profile_header") {
                ProfileHeader(
                    profile = profile,
                    onEditProfileClick = onEditProfileClick,
                    onShareProfileClick = onShareProfileClick,
                    onDiscoverPeopleClick = { },
                    onWebsiteClick = { url ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    }
                )
            }

            // ── SPAN ITEM 2: Story Highlights Strip (Consumes 3 Columns) ──────
            item(span = { GridItemSpan(maxLineSpan) }, key = "story_highlights") {
                StoryHighlightsRow(
                    highlights = highlights,
                    onHighlightClick = onHighlightClick
                )
            }

            // ── SPAN ITEM 3: Profile Tabs Strip (Consumes 3 Columns) ──────────
            item(span = { GridItemSpan(maxLineSpan) }, key = "profile_tab_bar") {
                ProfileTabRow(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }

            // ── TAB CONTENT: 3-Column Square Media Grid ──────────────────────
            when (selectedTab) {
                ProfileViewTab.GRID -> {
                    items(items = posts, key = { it.id }) { post ->
                        GridThumbnailCell(
                            post = post,
                            onClick = { onPostClick(post) }
                        )
                    }
                }

                ProfileViewTab.REELS -> {
                    // Filter or display reels posts
                    val reels = posts.filter { it.mediaType == com.example.instagramclone.data.GridMediaType.VIDEO }
                    if (reels.isEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            EmptyTabPlaceholder(message = "No Reels Yet")
                        }
                    } else {
                        items(reels, key = { "reel_${it.id}" }) { post ->
                            GridThumbnailCell(post = post, onClick = { onPostClick(post) })
                        }
                    }
                }

                ProfileViewTab.TAGGED -> {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        EmptyTabPlaceholder(message = "Photos and videos of you\nWhen people tag you in photos, they'll appear here.")
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyTabPlaceholder(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = InstagramGray,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(24.dp)
        )
    }
}
```

---

## 🔗 Step 7: Master App Routing & Bottom Bar Synchronization

Let's update `InstagramApp.kt` to coordinate navigation between the **Feed Screen** (Home Tab) from Part 1 and the **Profile Screen** (Profile Tab) from Part 2.

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.instagramclone.ui.bottomnav.InstagramBottomBar
import com.example.instagramclone.ui.bottomnav.InstagramTab
import com.example.instagramclone.ui.feed.FeedScreen
import com.example.instagramclone.ui.profile.ProfileScreen
import com.example.instagramclone.ui.theme.InstagramGray
import com.example.instagramclone.ui.theme.InstagramWhite

/**
 * Root Composable orchestrating:
 * - Part 1: Home Feed Screen with Stories Carousel & Double-Tap Heart
 * - Part 2: Full Profile Screen with Header, Highlights & 3-Column Grid
 * - Bottom Navigation Bar Tab Switching
 */
@Composable
fun InstagramApp() {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(InstagramTab.HOME) }

    Scaffold(
        containerColor = InstagramWhite,
        bottomBar = {
            InstagramBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        when (selectedTab) {
            InstagramTab.HOME -> {
                FeedScreen(
                    onStoryClick = { story ->
                        Toast.makeText(context, "Viewing ${story.user.username}'s story", Toast.LENGTH_SHORT).show()
                    },
                    onPostLike = { post ->
                        // Synchronize like state with ViewModel/Repository
                    },
                    onPostBookmark = { post ->
                        Toast.makeText(context, "Post saved to collection", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            InstagramTab.PROFILE -> {
                ProfileScreen(
                    onPostClick = { post ->
                        Toast.makeText(context, "Selected post ${post.id}", Toast.LENGTH_SHORT).show()
                    },
                    onHighlightClick = { highlight ->
                        Toast.makeText(context, "Viewing highlight: ${highlight.title}", Toast.LENGTH_SHORT).show()
                    },
                    onEditProfileClick = {
                        Toast.makeText(context, "Edit profile clicked", Toast.LENGTH_SHORT).show()
                    },
                    onShareProfileClick = {
                        Toast.makeText(context, "Share profile clicked", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            InstagramTab.SEARCH, InstagramTab.CREATE, InstagramTab.REELS -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${selectedTab.name}\n(Coming in Part 3 & 4)",
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

## 🔍 Deep Dive: Advanced Jetpack Compose Mechanics

### 1. Single-Scroll Architecture with `GridItemSpan(maxLineSpan)`

A common pitfall for Android engineers transitioning to Compose is attempting to build complex screens by nesting lazy lists inside scrollable columns.
`GridItemSpan(maxLineSpan)` provides the canonical solution:

```kotlin
LazyVerticalGrid(columns = GridCells.Fixed(3)) {
    item(span = { GridItemSpan(maxLineSpan) }) {
        // This item spans across all 3 grid columns (full device width)
    }
    items(gridPosts) {
        // This item occupies exactly 1 column (1/3 device width)
    }
}
```

#### Key Advantages:
- **No Scroll-Jacking:** Android's scroll dispatching only manages a single layout coordinator.
- **View Recycling:** Compose only retains the items currently visible in the viewport. When you scroll past the header, memory for the avatar and bio is freed.

---

### 2. The Nested Scrolling Anti-Pattern in Compose

Why does nesting a `LazyVerticalGrid` inside a `LazyColumn` crash?

```kotlin
// ❌ CRASH:
LazyColumn {
    item { ProfileHeader() }
    item {
        LazyVerticalGrid(columns = GridCells.Fixed(3)) { ... }
    }
}
```

`LazyColumn` measures children by providing infinite vertical constraints (`Constraints(maxHeight = Infinity)`). A `LazyVerticalGrid` also attempts to measure its content by delegating to its scroll state. When two infinite scroll coordinators collide, Compose throws:
`java.lang.IllegalStateException: Vertically scrollable component was measured with an infinity maximum height constraints.`

Using `item(span = { GridItemSpan(maxLineSpan) })` inside a single `LazyVerticalGrid` completely avoids this error while optimizing memory and rendering performance.

---

### 3. Clickable URL Links & Mentions via `AnnotatedString`

In modern Jetpack Compose (1.7+), rich text links are implemented using `LinkAnnotation.Url`:

```kotlin
val annotatedBio = buildAnnotatedString {
    append("Android Engineer ")
    withLink(LinkAnnotation.Url("https://github.com/AnkitChoudhary-1")) {
        withStyle(SpanStyle(color = Color(0xFF00376B), fontWeight = FontWeight.Bold)) {
            append("@AnkitChoudhary-1")
        }
    }
}
Text(text = annotatedBio)
```

This integrates directly with accessibility services (TalkBack) and automatically dispatches browser intent launches upon click.

---

### 4. Zero-Overhead Square Grid Cells with `aspectRatio(1f)`

How does `aspectRatio(1f)` ensure thumbnails stay square across any phone, foldable, or tablet width?

```kotlin
Box(
    modifier = Modifier
        .aspectRatio(1f)
        .background(color)
)
```

In the Compose measurement phase:
1. `GridCells.Fixed(3)` divides the available device width into three equal columns ($W = \frac{\text{screenWidth}}{3}$).
2. `aspectRatio(1f)` intercepts the measurement constraints and sets height equal to width ($H = W$).
3. Even during screen rotations or split-screen resizing, the grid automatically recalculates dimensions without distortion!

---

## 🧠 Jetpack Compose Principles Applied: Where & Why

| Compose Concept | Where It Is Used | Engineering Rationale |
| :--- | :--- | :--- |
| **`GridItemSpan(maxLineSpan)`** | `ProfileScreen` headers | Allows full-width elements (Stats, Bio, Highlights, Tabs) to coexist inside a 3-column grid. |
| **`GridCells.Fixed(3)`** | `ProfileScreen` | Guarantees an exact 3-column layout regardless of screen density. |
| **`TabRowDefaults.SecondaryIndicator`** | `ProfileTabRow` | Implements Instagram's signature high-contrast black active tab underline. |
| **`aspectRatio(1f)`** | `GridThumbnailCell` | Forces every post thumbnail to maintain an authentic 1:1 square ratio. |
| **State Hoisting** | `InstagramApp.kt` | Manages root navigation between `FeedScreen` and `ProfileScreen` with zero external dependencies. |
| **`Arrangement.spacedBy(1.5.dp)`** | `LazyVerticalGrid` | Replicates Instagram's crisp 1.5dp hairline gutters between grid thumbnails. |

---

## 🧪 Self-Assessment & Knowledge Check

Test your understanding of grid layouts and profile architectures:

### 1. What fatal crash occurs if you place a `LazyVerticalGrid` as an item inside a `LazyColumn`?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
Compose throws an `IllegalStateException: Vertically scrollable component was measured with an infinity maximum height constraints`. A `LazyColumn` provides infinite height constraints to its items; a nested `LazyVerticalGrid` cannot calculate its own viewport height without bounded constraints.
</details>

---

### 2. How does `GridItemSpan(maxLineSpan)` allow non-grid items (like bio and highlights) to live inside `LazyVerticalGrid`?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
In a grid configured with `GridCells.Fixed(3)`, each standard item occupies 1 column span. Declaring `span = { GridItemSpan(maxLineSpan) }` instructs Compose's grid layout algorithm that the item must span across all columns in that row (span = 3), effectively behaving as a full-width header.
</details>

---

### 3. Why is `aspectRatio(1f)` used instead of hardcoding `size(120.dp)` on grid thumbnails?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
Device screen widths vary drastically (e.g. 360dp, 411dp, 480dp, or tablet widths). Hardcoding `size(120.dp)` causes clipping, gaps, or uneven margins. `aspectRatio(1f)` dynamically reads whatever column width the grid assigns ($W$) and enforces height ($H = W$), ensuring perfectly responsive square cells on every device.
</details>

---

### 4. How do you disable the default colored background pill on Material 3 `TabRow` to match Instagram's minimal black indicator?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
Customize the `indicator` parameter of `TabRow`:
```kotlin
indicator = { tabPositions ->
    TabRowDefaults.SecondaryIndicator(
        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
        height = 1.5.dp,
        color = Color.Black
    )
}
```
This renders a sleek 1.5dp black horizontal underline without any background pill highlight.
</details>

---

### 5. What is the performance benefit of using a single `LazyVerticalGrid` over chunking a list into `items(posts.chunked(3))` inside a `LazyColumn`?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
`LazyVerticalGrid` recycles each thumbnail cell independently. With `posts.chunked(3)`, Compose must recompose and recycle an entire 3-post Row at once. If one post updates, the entire row recomposes. `LazyVerticalGrid` enables granular cell-level composition and animations.
</details>

---

## 🏁 Checkpoint: What You Should Have Working

Verify that your Part 2 implementation functions correctly:

- [x] **Top Navigation Bar:** Shows username, lock icon (if private), dropdown chevron, and action icons (`+` and `☰`).
- [x] **Profile Avatar:** 80dp circular avatar with bold initials and mini blue `+` story badge.
- [x] **Numeric Counters:** Posts, Followers, and Following display bold numbers formatted cleanly (e.g., `"14.2K"`).
- [x] **Bio & Links:** Displays full name, category tag (`"Digital Creator"`), bio, and clickable external link (`🔗`).
- [x] **Action Buttons:** `"Edit profile"` and `"Share profile"` buttons with rounded corners and user discovery icon.
- [x] **Story Highlights:** Horizontal carousel of saved highlight albums with thin border rings and a `"New"` creation tile.
- [x] **Profile Tab Row:** 3 tabs (Grid, Reels, Tagged) with sleek 1.5dp black underline indicator.
- [x] **3-Column Square Grid:** Displays 15+ square media thumbnails with 1.5dp hairline gutters.
- [x] **Corner Badges:** Shows Pinned (`📌`), Carousel (`⧉`), and Video (`🎬`) badges anchored at the top-right of thumbnails.
- [x] **Bottom Navigation Router:** Seamlessly switches between the Home Feed (Part 1) and Profile Screen (Part 2) via the bottom bar.

---

## 🏋️ Hands-On Coding Exercises

Take your Instagram profile implementation further with these practical challenges:

### 🎯 Exercise 1: Story Highlights Viewer Bottom Sheet
When a user taps any highlight album circle in `StoryHighlightsRow`, slide up a modal bottom sheet displaying the highlight's saved stories with a circular progress timer!

---

### 🎯 Exercise 2: Post Long-Press Quick Preview (Peek & Pop)
Add `combinedClickable(onLongClick = ...)` to `GridThumbnailCell`: when a user long-presses a thumbnail in the 3-column grid, scale up a floating enlarged preview card of the post with a dimmed background overlay (Instagram's famous Peek & Pop gesture)!

---

### 🎯 Exercise 3: Dynamic Grid-to-Feed Expansion
When a user taps any thumbnail in the 3-column grid, navigate smoothly to a full-screen vertical feed positioned directly at that post's index!

---

## 🧭 What's Next in Part 3

In **Part 3**, we engineer the most visually immersive module: **Instagram Reels**:
- Immersive Vertical Pager (`VerticalPager`) with full-screen snapping.
- Overlay controls: Creator avatar with animated follow button, caption scroller, sound disk rotating animation, and vertical action icons (Like, Comment, Share, Audio).
- Audio Track Marquee: Scrolling music name ticker with music note animation.
