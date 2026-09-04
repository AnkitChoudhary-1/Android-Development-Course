# 🎵 Spotify UI Clone — Part 1: Project Setup, Bottom Navigation & Home Screen

> **🎯 What You Will Build:** A production-grade, dark-themed Spotify Home Screen in Jetpack Compose featuring time-sensitive greetings, interactive filter pills, a 2-column quick-picks grid, horizontally scrolling playlist carousels (nested `LazyRow` inside `LazyColumn`), and a Material 3 bottom navigation bar with unidirectional data flow.

---

## 📋 Table of Contents

- [Overview — What We're Building](#-overview--what-were-building)
  - [Finished App Layout](#-finished-app-layout)
  - [A Note About Album Art & Images](#-a-note-about-album-art--images)
- [Step 0: Project Setup & Dependencies](#-step-0-project-setup--dependencies)
  - [1. Create the Project](#-1-create-the-project)
  - [2. Add Extended Icons Dependency](#-2-add-extended-icons-dependency)
  - [3. Target File Structure](#-3-target-file-structure)
- [Step 1: Design System & Theme (Spotify Colors)](#-step-1-design-system--theme-spotify-colors)
  - [1. Color Definitions — ui/theme/Color.kt](#-1-color-definitions--uithemecolorkt)
  - [2. Dark Theme Setup — ui/theme/Theme.kt](#-2-dark-theme-setup--uithemethemekt)
- [Step 2: Fake Data Models & Mock Dataset](#-step-2-fake-data-models--mock-dataset)
  - [1. Data Architecture](#-1-data-architecture)
  - [2. Data Models — data/Models.kt](#-2-data-models--datamodelskt)
  - [3. Mock Dataset — data/FakeData.kt](#-3-mock-dataset--datafakedatakt)
- [Step 3: Reusable CoverArt Component](#-step-3-reusable-coverart-component)
  - [Component Implementation — ui/components/CoverArt.kt](#-component-implementation--uicomponentscoverartkt)
- [Step 4: Bottom Navigation Bar](#-step-4-bottom-navigation-bar)
  - [Navigation Layout Structure](#-navigation-layout-structure)
  - [4a. Define Destinations — navigation/BottomNavItem.kt](#-4a-define-destinations--navigationbottomnavitemkt)
  - [4b. Stateless Bottom Bar — ui/components/SpotifyBottomBar.kt](#-4b-stateless-bottom-bar--uicomponentsspotifybottombarkt)
  - [Unidirectional Data Flow & State Hoisting](#-unidirectional-data-flow--state-hoisting)
- [Step 5: The Home Screen (Core Architecture)](#-step-5-the-home-screen-core-architecture)
  - [Component Hierarchy & Visual Tree](#-component-hierarchy--visual-tree)
  - [Complete Implementation — ui/home/HomeScreen.kt](#-complete-implementation--uihomehomescreenkt)
- [Step 6: Placeholder Screens](#-step-6-placeholder-screens)
  - [Screen Implementation — ui/PlaceholderScreen.kt](#-screen-implementation--uiplaceholderscreenkt)
- [Step 7: Wiring Everything Together](#-step-7-wiring-everything-together)
  - [State-Based Tab Switching](#-state-based-tab-switching)
  - [Root Composable — SpotifyApp.kt](#-root-composable--spotifyappkt)
  - [Activity Entry Point — MainActivity.kt](#-activity-entry-point--mainactivitykt)
- [Jetpack Compose Principles Applied: Where & Why](#-jetpack-compose-principles-applied-where--why)
- [Self-Assessment & Knowledge Check](#-self-assessment--knowledge-check)
- [Checkpoint: What You Should Have Working](#-checkpoint-what-you-should-have-working)
- [Hands-On Exercises Before Part 2](#-hands-on-exercises-before-part-2)

---

## 📱 Overview — What We're Building

Before writing any code, let's look at the finished architecture of the application for Part 1. We will construct a completely functional, data-driven home experience.

### 🖼️ Finished App Layout

```text
┌────────────────────────────────────┐
│  Good evening        🔔  🕒  ⚙️    │  ← Header (greeting + icons)
│  [All] [Music] [Podcasts]          │  ← Filter chips (State!)
│                                    │
│  ┌────────────┐ ┌────────────┐     │
│  │▣ Liked Song│ │▣ Discover  │     │  ← Quick picks grid
│  └────────────┘ └────────────┘     │     (2 columns, Row + weight)
│  ┌────────────┐ ┌────────────┐     │
│  │▣ Midnights │ │▣ Lo-Fi     │     │
│  └────────────┘ └────────────┘     │
│                                    │
│  Recently played                   │  ← Section title
│  ┌─────┐ ┌─────┐ ┌─────┐ ┌──→     │
│  │     │ │     │ │     │ │         │  ← LazyRow of cards
│  └─────┘ └─────┘ └─────┘ └──→     │
│  Title   Title   Title             │
│                                    │
│  Made for you                      │
│  ┌─────┐ ┌─────┐ ┌─────┐ ┌──→     │  ← Another LazyRow
│  └─────┘ └─────┘ └─────┘ └──→     │
│                                    │
│  Popular albums                    │
│  ...                               │
├────────────────────────────────────┤
│   🏠 Home    🔍 Search   📚 Library │  ← Bottom Navigation Bar
└────────────────────────────────────┘
```

---

### 🎨 A Note About Album Art & Images

> **💡 Architectural Design Tip:**
> In this stage, we do not bundle real high-resolution album art images or depend on network access. Instead, each item defines a `coverColor`, and our `CoverArt` composable renders a subtle gradient square with a centered music-note vector icon.
> 
> Later, when image loading libraries (such as **Coil**) are introduced, you only have to modify the internal body of `CoverArt.kt` — and every screen across the entire application will immediately render real network cover art with zero changes required to parent layouts! This demonstrates the power of composable design and single-responsibility components.

---

## 🛠️ Step 0: Project Setup & Dependencies

### 🆕 1. Create the Project

In Android Studio, initialize a fresh project with the following configuration:

```text
Android Studio → New Project → "Empty Activity" (Compose template)
Name: SpotifyClone
Package name: com.example.spotifyclone
Minimum SDK: 24 (Android 7.0 Nougat)
Build configuration language: Kotlin DSL (build.gradle.kts)
```

---

### 📦 2. Add Extended Icons Dependency

Open `app/build.gradle.kts` and add the extended Material icons library inside `dependencies { ... }`. We need this library for icons such as `LibraryMusic`, `MusicNote`, `History`, and others:

```kotlin
dependencies {
    // ... existing Compose dependencies generated by the template ...

    // Extended Material Icons (LibraryMusic, MusicNote, History, etc.)
    implementation("androidx.compose.material:material-icons-extended")
}
```

> **📌 Note:** Because your project template uses the Compose Bill of Materials (`platform(libs.androidx.compose.bom)`), you do **not** need to specify a version number for this artifact. Click **Sync Now** to download the dependency.

---

### 📁 3. Target File Structure

Organize your project files cleanly into dedicated architectural packages:

```text
com.example.spotifyclone/
├── MainActivity.kt                  ← Application Activity entry point
├── SpotifyApp.kt                    ← Root container: Scaffold + Bottom Bar + Tab State
├── data/
│   ├── Models.kt                    ← Pure domain data classes (MusicItem, HomeSection)
│   └── FakeData.kt                  ← Mock catalog & static data sources
├── navigation/
│   └── BottomNavItem.kt             ← Sealed class representing bottom navigation tabs
└── ui/
    ├── theme/
    │   ├── Color.kt                 ← Spotify brand & placeholder color palette
    │   └── Theme.kt                 ← Dark color scheme & theme wrapper
    ├── components/
    │   ├── CoverArt.kt              ← Reusable album cover placeholder
    │   └── SpotifyBottomBar.kt      ← Stateless bottom navigation bar
    ├── home/
    │   └── HomeScreen.kt            ← Full home screen (Header, Grid, Carousels)
    └── PlaceholderScreen.kt         ← Temporary screen for Search & Library tabs
```

> **💡 Action:** Create each package in Android Studio by right-clicking `com.example.spotifyclone` → **New** → **Package**.

---

## 🎨 Step 1: Design System & Theme (Spotify Colors)

Spotify's interface is renowned for its immersive dark mode: deep matte blacks, charcoal surfaces, clean white text, and a vibrant signature green accent.

---

### 🌈 1. Color Definitions — `ui/theme/Color.kt`

Replace the generated contents of `Color.kt` with Spotify's color definitions:

```kotlin
package com.example.spotifyclone.ui.theme

import androidx.compose.ui.graphics.Color

// ─── Spotify Brand Palette ────────────────────────────────
val SpotifyGreen     = Color(0xFF1DB954)   // Signature brand accent
val SpotifyBlack     = Color(0xFF121212)   // Main screen background
val SpotifyNavBlack  = Color(0xFF000000)   // Bottom navigation background
val SpotifyDarkGray  = Color(0xFF282828)   // Card backgrounds, quick-pick tiles
val SpotifyGray      = Color(0xFF535353)   // Unselected indicators, outlines
val SpotifyLightGray = Color(0xFFB3B3B3)   // Secondary text, artists, metadata
val SpotifyWhite     = Color(0xFFFFFFFF)   // Primary titles, active icons

// ─── Placeholder "Album Art" Colors ───────────────────────
// Each mock album/playlist selects one color for its gradient cover
val CoverColors = listOf(
    Color(0xFFE13300), Color(0xFF1E3264), Color(0xFF8D67AB), Color(0xFFE8115B),
    Color(0xFF148A08), Color(0xFFBC5900), Color(0xFF0D73EC), Color(0xFF503750),
    Color(0xFF477D95), Color(0xFFA56752), Color(0xFF7D4B32), Color(0xFF509BF5),
)
```

---

### 🌙 2. Dark Theme Setup — `ui/theme/Theme.kt`

Replace the generated `Theme.kt` with a streamlined, always-dark theme:

```kotlin
package com.example.spotifyclone.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SpotifyColorScheme = darkColorScheme(
    primary      = SpotifyGreen,
    onPrimary    = Color.Black,
    background   = SpotifyBlack,
    onBackground = SpotifyWhite,
    surface      = SpotifyBlack,
    onSurface    = SpotifyWhite,
)

@Composable
fun SpotifyCloneTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SpotifyColorScheme,
        content = content
    )
}
```

> **💡 Tip:** You may safely delete or ignore the template's `Type.kt`. Standard default typography suits our UI needs perfectly.

---

## 📦 Step 2: Fake Data Models & Mock Dataset

### 📐 1. Data Architecture

Every card rendered across the Home Screen (whether an album, playlist, or daily mix) contains the same three core presentation elements: **Cover**, **Title**, and **Subtitle**. Thus, a single clean data class handles all items:

```text
MusicItem
├── id              → Unique string ID (used as stable key in LazyRow / LazyColumn)
├── title           → Primary text (e.g., "Discover Weekly")
├── subtitle        → Secondary text (e.g., "Playlist • Spotify")
├── coverColor      → Color token used for the gradient cover background
└── durationSeconds → Song duration in seconds (used later in Now Playing)

HomeSection
├── id              → Unique section key (e.g., "recently_played")
├── title           → Section header text (e.g., "Recently played")
└── items           → List<MusicItem> for the section's horizontal carousel
```

---

### 📝 2. Data Models — `data/Models.kt`

Create `data/Models.kt` and define the domain models:

```kotlin
package com.example.spotifyclone.data

import androidx.compose.ui.graphics.Color

/**
 * Represents a single playable item or collection: song, album, playlist, or mix.
 */
data class MusicItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val coverColor: Color,
    val durationSeconds: Int = 210   // Used later by the Now Playing player screen
)

/**
 * Represents a titled horizontal section on the Home screen ("Recently played", etc.).
 */
data class HomeSection(
    val id: String,
    val title: String,
    val items: List<MusicItem>
)
```

---

### 🎧 3. Mock Dataset — `data/FakeData.kt`

Create `data/FakeData.kt` to provide sample music items:

```kotlin
package com.example.spotifyclone.data

import com.example.spotifyclone.ui.theme.CoverColors

object FakeData {

    val recentlyPlayed = listOf(
        MusicItem("rp1", "Liked Songs",            "Playlist • 312 songs",    CoverColors[0]),
        MusicItem("rp2", "Discover Weekly",        "Playlist • Spotify",      CoverColors[1]),
        MusicItem("rp3", "Midnights",              "Album • Taylor Swift",    CoverColors[2]),
        MusicItem("rp4", "Lo-Fi Beats",            "Playlist • Spotify",      CoverColors[3]),
        MusicItem("rp5", "Random Access Memories", "Album • Daft Punk",       CoverColors[4]),
        MusicItem("rp6", "Chill Hits",             "Playlist • Spotify",      CoverColors[5]),
        MusicItem("rp7", "Blonde",                 "Album • Frank Ocean",     CoverColors[6]),
        MusicItem("rp8", "Release Radar",          "Playlist • Spotify",      CoverColors[7]),
    )

    val madeForYou = listOf(
        MusicItem("mfy1", "Daily Mix 1",   "Arctic Monkeys, The Strokes and more", CoverColors[8]),
        MusicItem("mfy2", "Daily Mix 2",   "Billie Eilish, Lorde and more",        CoverColors[9]),
        MusicItem("mfy3", "Daily Mix 3",   "Kendrick Lamar, Drake and more",       CoverColors[10]),
        MusicItem("mfy4", "Daily Mix 4",   "Coldplay, Imagine Dragons and more",   CoverColors[11]),
        MusicItem("mfy5", "On Repeat",     "Songs you can't stop playing",         CoverColors[0]),
        MusicItem("mfy6", "Time Capsule",  "Songs from your past",                 CoverColors[1]),
    )

    val popularAlbums = listOf(
        MusicItem("pa1", "1989 (Taylor's Version)", "Taylor Swift",    CoverColors[2]),
        MusicItem("pa2", "SOS",                     "SZA",             CoverColors[3]),
        MusicItem("pa3", "Utopia",                  "Travis Scott",    CoverColors[4]),
        MusicItem("pa4", "Guts",                    "Olivia Rodrigo",  CoverColors[5]),
        MusicItem("pa5", "After Hours",             "The Weeknd",      CoverColors[6]),
        MusicItem("pa6", "Harry's House",           "Harry Styles",    CoverColors[7]),
    )

    val homeSections = listOf(
        HomeSection("recently_played", "Recently played", recentlyPlayed),
        HomeSection("made_for_you",    "Made for you",    madeForYou),
        HomeSection("popular_albums",  "Popular albums",  popularAlbums),
    )
}
```

> **⚡ Compose Performance Rule (Allocation Avoidance):**
> These lists reside inside a Kotlin `object` singleton. They are allocated exactly **once** when the class is first accessed, rather than re-instantiated on every recomposition. Always move static lists and constants out of composable bodies!

---

## 🖼️ Step 3: Reusable CoverArt Component

Every screen in Spotify features square cover art. Rather than repeating `Box`, gradient brushes, and `Icon` logic in multiple composables, we encapsulate it into a single clean component.

### 🧩 Component Implementation — `ui/components/CoverArt.kt`

```kotlin
package com.example.spotifyclone.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A placeholder for album art. Draws a gradient square in [color]
 * with a centered music-note icon.
 *
 * In future iterations, replace this Box with AsyncImage (Coil) to
 * load network images across the entire app automatically.
 */
@Composable
fun CoverArt(
    color: Color,
    modifier: Modifier = Modifier,   // Caller controls dimensions and external layout
    cornerRadius: Dp = 0.dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.linearGradient(
                    colors = listOf(color, color.copy(alpha = 0.55f))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.MusicNote,
            contentDescription = null,              // Decorative element
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.fillMaxSize(0.4f)   // Icon scales to 40% of parent box
        )
    }
}
```

> **💡 Compose Convention — Modifier Placement:**
> In accordance with standard Jetpack Compose guidelines, `modifier: Modifier = Modifier` is provided as the first optional parameter with a default value. This gives callers full autonomy to specify dimensions (e.g., `Modifier.size(56.dp)` or `Modifier.size(142.dp)`) without leaking layout details into the component.

---

## 🧭 Step 4: Bottom Navigation Bar

### 📐 Navigation Layout Structure

```text
NavigationBar                       ← Material 3 bottom bar container
 ├── NavigationBarItem (Home)       ← Icon + Label (Active: Filled / Inactive: Outlined)
 ├── NavigationBarItem (Search)     ← Icon + Label
 └── NavigationBarItem (Library)    ← Icon + Label
```

---

### 🏷️ 4a. Define Destinations — `navigation/BottomNavItem.kt`

We use a Kotlin sealed class to represent our navigation destinations. A sealed class guarantees exhaustive compile-time safety and prevents invalid tab states:

```kotlin
package com.example.spotifyclone.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : BottomNavItem(
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    data object Search : BottomNavItem(
        label = "Search",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search
    )

    data object Library : BottomNavItem(
        label = "Your Library",
        selectedIcon = Icons.Filled.LibraryMusic,
        unselectedIcon = Icons.Outlined.LibraryMusic
    )

    companion object {
        // Ordered list of tabs rendered in the bottom navigation bar
        val items = listOf(Home, Search, Library)
    }
}
```

> **💡 Tip:** If `data object` triggers a syntax error in your project, your Kotlin compiler version is older than 1.9. Simply replace `data object Home` with `object Home : BottomNavItem(...)`.

---

### 📱 4b. Stateless Bottom Bar — `ui/components/SpotifyBottomBar.kt`

This composable follows the **State Hoisting** pattern. It does not own any mutable state; it receives the currently selected destination downwards, and emits click events upwards:

```kotlin
package com.example.spotifyclone.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.spotifyclone.navigation.BottomNavItem
import com.example.spotifyclone.ui.theme.SpotifyLightGray
import com.example.spotifyclone.ui.theme.SpotifyNavBlack

@Composable
fun SpotifyBottomBar(
    selectedItem: BottomNavItem,                 // ⬇️ State down
    onItemSelected: (BottomNavItem) -> Unit      // ⬆️ Event up
) {
    NavigationBar(
        containerColor = SpotifyNavBlack
    ) {
        BottomNavItem.items.forEach { item ->
            val isSelected = item == selectedItem

            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemSelected(item) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = Color.White,
                    selectedTextColor   = Color.White,
                    unselectedIconColor = SpotifyLightGray,
                    unselectedTextColor = SpotifyLightGray,
                    indicatorColor      = Color.Transparent   // Spotify does not use M3 indicator pills
                )
            )
        }
    }
}
```

---

### 🔄 Unidirectional Data Flow & State Hoisting

```text
┌────────────────────────────────────────────────────────┐
│  Parent Composable (SpotifyApp)                        │
│  var selectedTab by remember { mutableStateOf(Home) }  │
└────────────────────────────────────────────────────────┘
          │                                 ▲
          │ 1. State flows DOWN             │ 2. Event flows UP
          │    (selectedItem = Home)        │    (onItemSelected(Search))
          ▼                                 │
┌────────────────────────────────────────────────────────┐
│  Stateless Child (SpotifyBottomBar)                    │
│  • Home tab: isSelected = true  → Filled icon, White   │
│  • Search:   isSelected = false → Outlined, Gray       │
│  • Library:  isSelected = false → Outlined, Gray       │
│                                                        │
│  User taps "Search"                                    │
│  → onClick invokes onItemSelected(Search)              │
└────────────────────────────────────────────────────────┘
```

---

## 🏠 Step 5: The Home Screen (Core Architecture)

### 📐 Component Hierarchy & Visual Tree

```text
HomeScreen
└── LazyColumn                                  ← Primary vertical scrolling container
    ├── item { HomeHeader() }                   ← Greeting + Top action icons + Filter chips
    ├── item { QuickPicksGrid() }               ← 2-column weighted tile grid
    └── items(sections, key = id) { section ->
            HomeSectionRow(section)             ← Section title + horizontal LazyRow
        }

HomeHeader
└── Column
    ├── Row (SpaceBetween)
    │   ├── Text("Good evening")
    │   └── Row of 3 IconButtons (Notifications, History, Settings)
    └── Row of FilterChipPill items (Stateful active chip)

QuickPicksGrid
└── Column
    └── Row × 3 (each Row holds 2 QuickPickTile composables with weight(1f))

HomeSectionRow
└── Column
    ├── Text(section.title)
    └── LazyRow (contentPadding, spacedBy)
        └── items(section.items, key = id) { MusicCard(it) }

MusicCard
└── Column (width = 150.dp, clickable)
    ├── CoverArt (142.dp square, rounded corners)
    ├── Text(title, 1 line, ellipsis)
    └── Text(subtitle, 2 lines, ellipsis)
```

---

### 💻 Complete Implementation — `ui/home/HomeScreen.kt`

Create `ui/home/HomeScreen.kt` with the complete implementation:

```kotlin
package com.example.spotifyclone.ui.home

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
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spotifyclone.data.FakeData
import com.example.spotifyclone.data.HomeSection
import com.example.spotifyclone.data.MusicItem
import com.example.spotifyclone.ui.components.CoverArt
import com.example.spotifyclone.ui.theme.SpotifyBlack
import com.example.spotifyclone.ui.theme.SpotifyCloneTheme
import com.example.spotifyclone.ui.theme.SpotifyDarkGray
import com.example.spotifyclone.ui.theme.SpotifyGreen
import com.example.spotifyclone.ui.theme.SpotifyLightGray
import java.util.Calendar

// ═══════════════════════════════════════════════════════════════
//  HOME SCREEN (Entry Point)
// ═══════════════════════════════════════════════════════════════

@Composable
fun HomeScreen(
    onItemClick: (MusicItem) -> Unit,                    // ⬆️ Event up (triggers playback/details)
    sections: List<HomeSection> = FakeData.homeSections, // ⬇️ Data down (defaulting to fake catalog)
    quickPicks: List<MusicItem> = FakeData.recentlyPlayed.take(6)
) {
    // 📌 LazyColumn: The entire viewport is a single, performant scrollable container.
    // Header, Quick Picks grid, and carousel sections are individual items.
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SpotifyBlack),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. Single Item → Top Header
        item { HomeHeader() }

        // 2. Single Item → 2-Column Quick Picks Grid
        item { QuickPicksGrid(items = quickPicks, onItemClick = onItemClick) }

        // 3. Repeating Items → One row per section with stable unique keys
        items(
            items = sections,
            key = { section -> section.id }
        ) { section ->
            HomeSectionRow(section = section, onItemClick = onItemClick)
        }
    }
}

// ═══════════════════════════════════════════════════════════════
//  HEADER — Dynamic Greeting + Action Icons + Filter Chips
// ═══════════════════════════════════════════════════════════════

@Composable
private fun HomeHeader() {
    // 📌 remember: Computes greeting string once upon composition, avoiding repeated clock lookups
    val greeting = remember {
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 5..11  -> "Good morning"
            in 12..17 -> "Good afternoon"
            else      -> "Good evening"
        }
    }

    // 📌 Local State: Tracks active category filter chip ("All", "Music", "Podcasts")
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = remember { listOf("All", "Music", "Podcasts") }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = greeting,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Row {
                IconButton(onClick = { /* Handle notifications click */ }) {
                    Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = Color.White)
                }
                IconButton(onClick = { /* Handle listening history click */ }) {
                    Icon(Icons.Outlined.History, contentDescription = "Recently played", tint = Color.White)
                }
                IconButton(onClick = { /* Handle settings click */ }) {
                    Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            filters.forEach { filter ->
                FilterChipPill(
                    label = filter,
                    selected = filter == selectedFilter,       // ⬇️ State down
                    onClick = { selectedFilter = filter }      // ⬆️ Event up
                )
            }
        }
    }
}

@Composable
private fun FilterChipPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    // 📌 Modifier Order Rule: clip → background → clickable → padding
    // Placing clickable AFTER clip ensures the touch ripple is rounded.
    // Placing padding AFTER clickable ensures the full pill boundary is interactive.
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (selected) SpotifyGreen else SpotifyDarkGray)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) Color.Black else Color.White
        )
    }
}

// ═══════════════════════════════════════════════════════════════
//  QUICK PICKS — 2-Column Grid of Horizontal Mini-Tiles
// ═══════════════════════════════════════════════════════════════

@Composable
private fun QuickPicksGrid(
    items: List<MusicItem>,
    onItemClick: (MusicItem) -> Unit
) {
    // 📌 remember(items): Only re-chunks the list when the input dataset reference changes
    val rows = remember(items) { items.chunked(2) }

    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rows.forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowItems.forEach { item ->
                    QuickPickTile(
                        item = item,
                        onClick = { onItemClick(item) },
                        modifier = Modifier.weight(1f) // 📌 weight(1f) creates equal column widths
                    )
                }
                // If a row has only 1 item, fill the remaining half so columns align
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun QuickPickTile(
    item: MusicItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(SpotifyDarkGray)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CoverArt(
            color = item.coverColor,
            modifier = Modifier.size(56.dp)
        )
        Text(
            text = item.title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(1f)
        )
    }
}

// ═══════════════════════════════════════════════════════════════
//  SECTION — Header Title + Horizontal LazyRow Carousel
// ═══════════════════════════════════════════════════════════════

@Composable
private fun HomeSectionRow(
    section: HomeSection,
    onItemClick: (MusicItem) -> Unit
) {
    Column {
        Text(
            text = section.title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // 📌 Nested LazyRow inside LazyColumn item:
        // Permitted in Compose because the scroll orientations are orthogonal (horizontal vs vertical).
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = section.items,
                key = { item -> item.id } // 📌 Stable identity prevents item churn
            ) { item ->
                MusicCard(
                    item = item,
                    onClick = { onItemClick(item) }
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
//  MUSIC CARD — Square Cover + Primary Title + Subtitle
// ═══════════════════════════════════════════════════════════════

@Composable
fun MusicCard(
    item: MusicItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(150.dp)
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick) // Ripple is clipped to the card's rounded edges
            .padding(4.dp)
    ) {
        CoverArt(
            color = item.coverColor,
            modifier = Modifier.size(142.dp),
            cornerRadius = 6.dp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = item.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = item.subtitle,
            fontSize = 12.sp,
            color = SpotifyLightGray,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ═══════════════════════════════════════════════════════════════
//  PREVIEW
// ═══════════════════════════════════════════════════════════════

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun HomeScreenPreview() {
    SpotifyCloneTheme {
        HomeScreen(onItemClick = {})
    }
}
```

---

## 🚧 Step 6: Placeholder Screens

The **Search** and **Your Library** tabs will be comprehensively engineered in upcoming lessons. For now, create a clean placeholder composable so bottom navigation tab clicks transition smoothly:

### 📄 Screen Implementation — `ui/PlaceholderScreen.kt`

```kotlin
package com.example.spotifyclone.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.spotifyclone.ui.theme.SpotifyBlack

@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpotifyBlack),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$title\n(coming soon)",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
```

---

## 🔌 Step 7: Wiring Everything Together

### 🔄 State-Based Tab Switching

At this stage, we implement tab switching using state-driven UI rendering without requiring the full `Navigation Compose` library:

```text
var selectedTab: BottomNavItem = BottomNavItem.Home
               │
               ▼
       when (selectedTab) {
           BottomNavItem.Home    → HomeScreen()
           BottomNavItem.Search  → PlaceholderScreen("Search")
           BottomNavItem.Library → PlaceholderScreen("Your Library")
       }
```

> **💡 Architectural Context:**
> In Phase 5, we will migrate this to `NavHost` and `rememberNavController()` to support back-stack management, argument bundles, and deep links. For UI architecture practice, state-based tab switching is lightweight, reliable, and straightforward.

---

### 📱 Root Composable — `SpotifyApp.kt`

Create `SpotifyApp.kt` at the project root (`com.example.spotifyclone`):

```kotlin
package com.example.spotifyclone

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
import androidx.compose.ui.platform.LocalContext
import com.example.spotifyclone.navigation.BottomNavItem
import com.example.spotifyclone.ui.PlaceholderScreen
import com.example.spotifyclone.ui.components.SpotifyBottomBar
import com.example.spotifyclone.ui.home.HomeScreen
import com.example.spotifyclone.ui.theme.SpotifyBlack

@Composable
fun SpotifyApp() {
    val context = LocalContext.current

    // 📌 State Hoisting: SpotifyApp is the single source of truth for the active tab.
    // Explicit type `: BottomNavItem` is REQUIRED — otherwise Kotlin infers the narrow type
    // `BottomNavItem.Home`, which prevents reassigning Search or Library!
    var selectedTab: BottomNavItem by remember { mutableStateOf(BottomNavItem.Home) }

    Scaffold(
        containerColor = SpotifyBlack,
        bottomBar = {
            SpotifyBottomBar(
                selectedItem = selectedTab,                  // ⬇️ State down
                onItemSelected = { selectedTab = it }        // ⬆️ Event up
            )
        }
    ) { innerPadding ->
        // 📌 innerPadding: Accounts for system bars and the bottom navigation bar height.
        // Applying this padding prevents screen content from getting clipped behind the bottom bar.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                BottomNavItem.Home -> HomeScreen(
                    onItemClick = { item ->
                        // Temporary Toast feedback. Part 3 expands this into the Now Playing player!
                        Toast.makeText(context, "Playing: ${item.title}", Toast.LENGTH_SHORT).show()
                    }
                )
                BottomNavItem.Search  -> PlaceholderScreen("Search")
                BottomNavItem.Library -> PlaceholderScreen("Your Library")
            }
        }
    }
}
```

---

### 🚀 Activity Entry Point — `MainActivity.kt`

Update `MainActivity.kt` to enable edge-to-edge rendering and host `SpotifyApp`:

```kotlin
package com.example.spotifyclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.spotifyclone.ui.theme.SpotifyCloneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Draw behind status and navigation bars; Scaffold correctly applies system insets
        enableEdgeToEdge()
        setContent {
            SpotifyCloneTheme {
                SpotifyApp()
            }
        }
    }
}
```

---

## 🧠 Jetpack Compose Principles Applied: Where & Why

The following matrix maps the foundational concepts learned throughout Phase 3 to their concrete applications in this project:

| Compose Concept | Where It Is Used | Engineering Rationale |
| :--- | :--- | :--- |
| **`remember { mutableStateOf }`** | `SpotifyApp.selectedTab`, `HomeHeader.selectedFilter` | Maintains active selection across recompositions without resetting state. |
| **State Hoisting** | `SpotifyBottomBar`, `FilterChipPill`, `HomeScreen` | Keeps leaf composables 100% stateless and reusable; parents retain control over state. |
| **`LazyColumn`** | `HomeScreen` | Recycles off-screen components; only currently visible items and carousels are composed. |
| **Orthogonal Nested Scrolling** | `HomeSectionRow` (`LazyRow` inside `LazyColumn`) | Compose supports horizontal scroll inside vertical scroll without gesture ambiguity. |
| **`item {}` vs `items()`** | `HomeScreen` | `item {}` models single custom views (header, grid), while `items()` iterates collections. |
| **`key = { it.id }`** | `sections` and `LazyRow` items | Provides stable identities across re-orderings, preventing unnecessary recompositions. |
| **`contentPadding`** | `LazyColumn`, `LazyRow` | Adds outer boundary breathing room while letting items scroll cleanly to the viewport edges. |
| **`Arrangement.spacedBy`** | Throughout rows and columns | Delivers consistent, declarative gaps between child items without manual spacer math. |
| **Modifier Order Rules** | `FilterChipPill`, `MusicCard` | `clip` → `background` → `clickable` → `padding` guarantees rounded ripple bounds and maximum hit-test area. |
| **`Modifier.weight(1f)`** | `QuickPicksGrid`, tile titles | Establishes 50/50 split columns regardless of screen width; titles absorb remaining width. |
| **`remember(key)` Caching** | `rows = remember(items) { items.chunked(2) }` | Prevents list slicing and chunk allocations on every single recomposition frame. |
| **Constants Outside Composable** | `FakeData` object, `CoverColors` | Allocates static mock lists once at app launch rather than recreating them during renders. |
| **Material 3 Token Overrides** | `NavigationBarItemDefaults.colors` | Removes default Material 3 indicator pills (`Color.Transparent`) to match Spotify's aesthetic. |

---

## 🧪 Self-Assessment & Knowledge Check

Test your understanding of the architecture and Compose patterns used in this project:

### 1. Why is the type annotation `: BottomNavItem` mandatory in `var selectedTab: BottomNavItem by remember { mutableStateOf(BottomNavItem.Home) }`?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
Without the explicit `: BottomNavItem` type declaration, Kotlin's type inference system infers the type of `selectedTab` as the narrow subclass `BottomNavItem.Home`. Because `BottomNavItem.Search` and `BottomNavItem.Library` are distinct singleton objects, the Kotlin compiler will throw a type mismatch error when attempting to assign any other tab.
</details>

---

### 2. Why is nesting a `LazyRow` inside an item of `LazyColumn` completely legal, whereas nesting a `LazyColumn` inside another vertical `LazyColumn` without fixed constraints throws an exception?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
Compose lazy layouts require finite constraints along their scroll axis to measure their viewports and determine which items to emit. Because `LazyRow` scrolls horizontally and `LazyColumn` scrolls vertically, their scrolling axes are orthogonal. The parent `LazyColumn` provides a bounded horizontal width to the row, so there is no scroll conflict or measurement ambiguity.
</details>

---

### 3. What happens if you forget to pass `innerPadding` from `Scaffold` to the child `Box` in `SpotifyApp`?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
The `Scaffold` calculates the exact dimensions of top bars, bottom bars, and system navigation insets, passing them down via `innerPadding`. If you ignore `innerPadding`, the bottom content of the screen (the last sections of `LazyColumn`) will scroll beneath and remain completely obscured by the opaque `SpotifyBottomBar`.
</details>

---

### 4. In `FilterChipPill`, why is the modifier ordered as `.clip(...).background(...).clickable(...).padding(...)`?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
Modifier order in Compose is evaluated sequentially:
1. `.clip(CircleShape)` clips the canvas to a pill shape.
2. `.background(...)` fills the clipped pill with the background color.
3. `.clickable(...)` attaches the touch target and ripple effect. Because it comes **after** `.clip()`, the ripple effect is confined to the rounded pill boundary rather than overflowing into a rectangle.
4. `.padding(...)` is applied **inside** the clickable surface, ensuring the entire padded pill area reacts to touches.
</details>

---

### 5. Why do we wrap `items.chunked(2)` in `remember(items)` inside `QuickPicksGrid`?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
`items.chunked(2)` iterates through the list and allocates brand new intermediate list instances every time it is evaluated. If not wrapped in `remember(items)`, every minor recomposition of the screen would cause unnecessary heap allocations and garbage collection overhead.
</details>

---

## 🏁 Checkpoint: What You Should Have Working

Before proceeding to Part 2, verify that your application satisfies the following criteria:

- [x] **Theme:** App launches into a dark interface (`0xFF121212`) with Spotify-green accents and crisp white typography.
- [x] **Dynamic Header:** Greeting accurately displays "Good morning", "Good afternoon", or "Good evening" based on your local device clock.
- [x] **Filter Pills:** Tapping "Music", "Podcasts", or "All" immediately highlights the tapped pill in green and sets others to dark gray.
- [x] **2-Column Grid:** Quick picks render in two balanced, equal-width columns (`Modifier.weight(1f)`).
- [x] **Horizontal Carousels:** Each section ("Recently played", "Made for you", "Popular albums") scrolls smoothly horizontally.
- [x] **Item Feedback:** Tapping any quick-pick tile or album card emits a `Toast` message displaying the item title.
- [x] **Bottom Navigation:** Bottom bar tabs switch seamlessly between Home and the temporary placeholder screens.
- [x] **Active Indicators:** Active tab displays a solid white filled icon; inactive tabs display outlined gray icons without indicator pills.
- [x] **Edge Insets:** Content cleanly scrolls above the bottom bar without clipping or overlapping.

---

## 🏋️ Coding Exercises Before Moving to Part 2

Deepen your engineering skills with these practical mini-challenges:

### 🎯 Exercise 1: Data-Driven Expansion
Add a fourth section titled **"Jump back in"** containing 5 new `MusicItem` entries in `FakeData.kt`.
> **Observation:** Notice that zero lines of UI code need to be touched. Because the UI is completely data-driven, modifying the data source automatically updates the layout!

---

### 🎯 Exercise 2: Horizontally Scrollable Filter Pills
Make the filter pills header row scrollable to accommodate more categories (e.g., `"All"`, `"Music"`, `"Podcasts"`, `"Audiobooks"`, `"Live Events"`, `"Wrapped"`).
> **Hint:** Replace the inner `Row` in `HomeHeader` with a `LazyRow` using `horizontalArrangement = Arrangement.spacedBy(8.dp)`.

---

### 🎯 Exercise 3: Touch Scale Interaction Animation
Add an animated scale bounce effect when a user presses down on a `MusicCard`, utilizing `animateFloatAsState` and `MutableInteractionSource`.
> **Hint:**
> ```kotlin
> val interactionSource = remember { MutableInteractionSource() }
> val isPressed by interactionSource.collectIsPressedAsState()
> val scale by animateFloatAsState(
>     targetValue = if (isPressed) 0.95f else 1f,
>     label = "CardPressScale"
> )
> 
> Column(
>     modifier = modifier
>         .graphicsLayer {
>             scaleX = scale
>             scaleY = scale
>         }
>         .clickable(
>             interactionSource = interactionSource,
>             indication = null, // or default ripple
>             onClick = onClick
>         )
> ) { ... }
> ```