# 📸 Instagram UI Clone — Part 1: Stories Row, Dynamic Gradient Rings & Feed Screen

> **🎯 What You Will Build:** The quintessential **Instagram Home Feed** in Jetpack Compose. You will engineer the iconic multi-color gradient story rings using 3-layer concentric Box geometry, build an edge-to-edge scrollable feed with an inline horizontal stories carousel (`LazyRow` inside `LazyColumn`), construct feed post cards with square aspect ratios (`aspectRatio(1f)`), interactive double-tap heart animations, and seamlessly wrapped captions via `buildAnnotatedString`.

---

## 📋 Table of Contents

- [Overview — What We're Building](#-overview--what-were-building)
  - [Visual Layout Architecture](#-visual-layout-architecture)
  - [Core Engineering Concepts Introduced](#-core-engineering-concepts-introduced)
- [Target File Structure](#-target-file-structure)
- [Step 0: Project Setup & Gradle Dependencies](#-step-0-project-setup--gradle-dependencies)
- [Step 1: Theme — Instagram Colors & Story Gradients](#-step-1-theme--instagram-colors--story-gradients)
  - [1. Color Tokens — ui/theme/Color.kt](#-1-color-tokens--uithemecolorkt)
  - [2. Theme Definition — ui/theme/Theme.kt](#-2-theme-definition--uithemethemekt)
- [Step 2: Data Models & Mock Feed Dataset](#-step-2-data-models--mock-feed-dataset)
  - [1. Domain Models — data/Models.kt](#-1-domain-models--datamodelskt)
  - [2. Realistic Mock Data — data/FakeData.kt](#-2-realistic-mock-data--datafakedatakt)
- [Step 3: The Story Avatar & 3-Layer Concentric Circles](#-step-3-the-story-avatar--3-layer-concentric-circles)
  - [Concentric Ring Geometry](#-concentric-ring-geometry)
  - [Why Three Nested Boxes Instead of Modifier.border?](#-why-three-nested-boxes-instead-of-modifierborder)
  - [Implementation — ui/components/StoryAvatar.kt](#-implementation--uicomponentsstoryavatarkt)
- [Step 4: Horizontal Stories Carousel](#-step-4-horizontal-stories-carousel)
  - [Why Stories Belong Inside the LazyColumn Header](#-why-stories-belong-inside-the-lazycolumn-header)
  - [Implementation — ui/feed/StoriesRow.kt](#-implementation--uifeedstoriesrowkt)
- [Step 5: The Post Card & Double-Tap Heart Animation](#-step-5-the-post-card--double-tap-heart-animation)
  - [Post Card Layout Breakdown](#-post-card-layout-breakdown)
  - [Seamless Paragraph Flow with buildAnnotatedString](#-seamless-paragraph-flow-with-buildannotatedstring)
  - [Double-Tap Heart Spring Overlay](#-double-tap-heart-spring-overlay)
  - [Implementation — ui/components/PostCard.kt](#-implementation--uicomponentspostcardkt)
- [Step 6: Top App Bar & Bottom Navigation Bar](#-step-6-top-app-bar--bottom-navigation-bar)
  - [1. Script Logo Top Bar — ui/components/InstagramTopBar.kt](#-1-script-logo-top-bar--uicomponentsinstagramtopbarkt)
  - [2. Custom Navigation Bar — ui/bottomnav/InstagramBottomBar.kt](#-2-custom-navigation-bar--uibottomnavinstagrambottombarkt)
- [Step 7: Master Feed Screen & App Integration](#-step-7-master-feed-screen--app-integration)
  - [1. Feed Screen — ui/feed/FeedScreen.kt](#-1-feed-screen--uifeedfeedscreenkt)
  - [2. Master Orchestrator — InstagramApp.kt](#-2-master-orchestrator--instagramappkt)
  - [3. Entry Point — MainActivity.kt](#-3-entry-point--mainactivitykt)
- [🔍 Deep Dive: Advanced Jetpack Compose Mechanics](#-deep-dive-advanced-jetpack-compose-mechanics)
  - [1. Concentric Box Layering vs Canvas Ring Drawing](#-1-concentric-box-layering-vs-canvas-ring-drawing)
  - [2. Inline Paragraph Flow with buildAnnotatedString](#-2-inline-paragraph-flow-with-buildannotatedstring)
  - [3. Image Geometry Control with Modifier.aspectRatio()](#-3-image-geometry-control-with-modifieraspectratio)
  - [4. Double-Tap Detection with combinedClickable & Coroutine Debounce](#-4-double-tap-detection-with-combinedclickable--coroutine-debounce)
- [🧠 Jetpack Compose Principles Applied: Where & Why](#-jetpack-compose-principles-applied-where--why)
- [🧪 Self-Assessment & Knowledge Check](#-self-assessment--knowledge-check)
- [🏁 Checkpoint: What You Should Have Working](#-checkpoint-what-you-should-have-working)
- [🏋️ Hands-On Coding Exercises](#-hands-on-coding-exercises)
- [🧭 What's Next in Part 2](#-whats-next-in-part-2)

---

## 📱 Overview — What We're Building

Instagram's user interface is a benchmark of modern minimalist mobile engineering: clean monochrome surfaces paired with vibrant, high-energy gradient accents. 

In **Part 1**, we engineer the complete **Home Feed Screen**, which contains:
1. **The Script Logo Top Bar:** An elegant top navigation bar featuring the iconic cursive wordmark, camera launcher, and direct messaging inbox.
2. **The Stories Carousel:** A horizontal strip of circular profile stories encased in Instagram's signature 5-stop sunset gradient rings (`#FEDA75` ➔ `#FA7E1E` ➔ `#D62976` ➔ `#962FBF` ➔ `#4F5BD5`), complete with viewed states (subtle monochrome gray) and a blue `+` badge for the current user's story.
3. **The Feed Posts:** Square aspect-ratio image cards with author avatars, verified badges, action icon bars (Like, Comment, Share, Bookmark), like tallies, smoothly wrapped captions via `buildAnnotatedString`, and full **double-tap heart animation**.
4. **The Instagram Bottom Navigation Bar:** Clean icon-driven navigation switching between Home, Search, Create, Reels, and Profile.

---

### 🖼️ Visual Layout Architecture

```text
┌─────────────────────────────────────────────────────────────┐
│  📷  Instagram                                      ♡   ✉️  │  ← Top App Bar (Script Logo, Camera, DM)
├─────────────────────────────────────────────────────────────┤
│  ┌─────┐   ┌─────┐   ┌─────┐   ┌─────┐   ┌─────┐            │
│  │(you)│   │🟣AW │   │🟣JE │   │⚪SD │   │🟣MF │   ──→      │  ← Stories Row (LazyRow inside LazyColumn)
│  │  +  │   │     │   │     │   │     │   │     │            │     🟣 = 5-color Sunset Gradient (Unseen)
│  └─────┘   └─────┘   └─────┘   └─────┘   └─────┘            │     ⚪ = Muted Light Gray (Viewed)
│   Your      alice     john      sarah     mike              │     +  = Blue "Add Story" Badge
│   story    _wonder   .explor   _designs  _fitness           │
├─────────────────────────────────────────────────────────────┤
│  [AW]  alice_wonder                                     ••• │  ← Post Header (Avatar, Username, Menu)
│  ┌───────────────────────────────────────────────────────┐  │
│  │                                                       │  │
│  │                                                       │  │  ← Square Post Image (aspectRatio(1f))
│  │                      📸   🌅                          │  │     Double-Tap triggers popping ❤️ overlay!
│  │                 Sunset at the beach                   │  │
│  │                                                       │  │
│  └───────────────────────────────────────────────────────┘  │
│  ♡   💬   ➤                                             🔖  │  ← Action Bar (Like, Comment, Share, Bookmark)
│  1,247 likes                                                │  ← Bold Likes Count
│  alice_wonder  Best sunset ever! The colors were unreal ✨  │  ← Caption (Inline Bold Username via AnnotatedString)
│  View all 48 comments                                       │  ← Comments Link
│  2 HOURS AGO                                                │  ← Formatted Timestamp
├─────────────────────────────────────────────────────────────┤
│  ... next post ...                                          │
├─────────────────────────────────────────────────────────────┤
│    🏠            🔍            ➕            🎬            👤   │  ← Bottom Bar (Outlined / Filled Tab Switcher)
└─────────────────────────────────────────────────────────────┘
```

---

### 🚀 Core Engineering Concepts Introduced

```text
1. Concentric Box Layering for Story Rings:
   • 3 nested Box composables: Outer Gradient (68dp) ➔ White Gap (60dp) ➔ Avatar (54dp).
   • Provides pixel-perfect gap control without complex canvas trigonometry.

2. Inline Paragraph Flow with buildAnnotatedString:
   • Seamlessly attaches bold username to normal-weight caption text in a SINGLE Text composable.
   • Eliminates word-wrap indent bugs caused by side-by-side Row(Text, Text).

3. Geometry Enforcement with Modifier.aspectRatio(1f):
   • Locks media containers into exact 1:1 square or 4:5 portrait proportions.
   • Prevents layout collapse when loading asynchronous remote images.

4. Gesture Chaining with combinedClickable:
   • Supports both single-tap and double-tap gestures on post media.
   • Drives transient heart animations via LaunchedEffect and Spring physics.

5. Unified Scroll Performance:
   • Nesting the StoriesRow as item {} inside the parent LazyColumn.
   • Allows stories to naturally scroll off-screen, maximizing screen real estate for feed posts.
```

---

## 📁 Target File Structure

```text
app/src/main/java/com/example/instagramclone/
├── MainActivity.kt                            # Edge-to-edge Activity Host
├── InstagramApp.kt                            # Master scaffold, bottom bar & tab router
├── data/
│   ├── Models.kt                              # User, Story, Post data classes
│   └── FakeData.kt                            # Mock users, active stories & feed posts
└── ui/
    ├── theme/
    │   ├── Color.kt                           # Instagram monochrome palette & 5-stop sunset gradient
    │   └── Theme.kt                           # Clean light theme configuration
    ├── components/
    │   ├── InstagramTopBar.kt                 # Script wordmark logo & action icons
    │   ├── StoryAvatar.kt                     # 3-layer gradient ring & blue "+" badge
    │   └── PostCard.kt                        # Post header, square image, actions, captions
    ├── feed/
    │   ├── StoriesRow.kt                      # Horizontal LazyRow of story avatars
    │   └── FeedScreen.kt                      # Master feed containing stories header & posts
    └── bottomnav/
        └── InstagramBottomBar.kt              # Bottom navigation bar with outlined/filled icons
```

---

## 🛠️ Step 0: Project Setup & Gradle Dependencies

In your `app/build.gradle.kts`, ensure you have the extended Material icons library enabled for Instagram's camera, bookmark, and direct message glyphs:

```kotlin
dependencies {
    // Jetpack Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Extended Material Icons for Instagram UI
    implementation("androidx.compose.material:material-icons-extended")

    // Foundation for advanced gestures (combinedClickable)
    implementation("androidx.compose.foundation:foundation")
}
```

---

## 🎨 Step 1: Theme — Instagram Colors & Story Gradients

Instagram's brand palette relies on an ultra-clean white canvas punctuated by bold blacks and the unmistakable 5-stop warm sunset story gradient.

### 1. Color Tokens — `ui/theme/Color.kt`

Create `ui/theme/Color.kt`:

```kotlin
package com.example.instagramclone.ui.theme

import androidx.compose.ui.graphics.Color

// ─── Instagram Brand Neutrals ────────────────────────────────────────────────
val InstagramWhite       = Color(0xFFFFFFFF)
val InstagramBlack       = Color(0xFF000000)
val InstagramGray        = Color(0xFF8E8E8E)  // Secondary metadata, timestamps, comment links
val InstagramLightGray   = Color(0xFFDBDBDB)  // Hairline post dividers, viewed story rings
val InstagramDarkGray    = Color(0xFF262626)  // Primary usernames and caption text
val InstagramBackground  = Color(0xFFFAFAFA)  // Subtle off-white canvas
val InstagramBlue        = Color(0xFF0095F6)  // Follow buttons, links, "Add Story" badge
val InstagramRed         = Color(0xFFED4956)  // Liked heart red

// ─── Iconic Instagram Story Sunset Gradient ──────────────────────────────────
// Flows smoothly: Yellow ➔ Orange ➔ Pink ➔ Purple ➔ Deep Blue
val StoryGradientColors = listOf(
    Color(0xFFFEDA75), // Golden Yellow
    Color(0xFFFA7E1E), // Vibrant Orange
    Color(0xFFD62976), // Hot Pink / Magenta
    Color(0xFF962FBF), // Purple
    Color(0xFF4F5BD5)  // Deep Blue
)

// Viewed stories display a subtle, muted monochrome ring
val StoryViewedColors = listOf(
    Color(0xFFDBDBDB),
    Color(0xFFDBDBDB)
)

// ─── Placeholder Post Image Palette ──────────────────────────────────────────
val PostImageColors = listOf(
    Color(0xFFE8D5B7), // Warm Beach Sunset
    Color(0xFFB7D5E8), // Ocean Blue
    Color(0xFFB7E8C9), // Emerald Nature
    Color(0xFFE8B7D5), // Soft Blossom
    Color(0xFFD5B7E8), // Urban Twilight
    Color(0xFFE8E0B7)  // Artisan Cream
)

// ─── User Avatar Background Palette ──────────────────────────────────────────
val AvatarColors = listOf(
    Color(0xFF6366F1), Color(0xFFEC4899), Color(0xFF10B981),
    Color(0xFFF59E0B), Color(0xFF8B5CF6), Color(0xFFEF4444),
    Color(0xFF06B6D4), Color(0xFF84CC16)
)
```

---

### 2. Theme Definition — `ui/theme/Theme.kt`

Create `ui/theme/Theme.kt`:

```kotlin
package com.example.instagramclone.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val InstagramColorScheme = lightColorScheme(
    primary      = InstagramBlack,
    onPrimary    = InstagramWhite,
    background   = InstagramWhite,
    onBackground = InstagramDarkGray,
    surface      = InstagramWhite,
    onSurface    = InstagramDarkGray
)

@Composable
fun InstagramCloneTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = InstagramColorScheme,
        content = content
    )
}
```

---

## 📦 Step 2: Data Models & Mock Feed Dataset

### 1. Domain Models — `data/Models.kt`

Create `data/Models.kt`:

```kotlin
package com.example.instagramclone.data

import androidx.compose.ui.graphics.Color

/**
 * Represents a registered Instagram profile.
 */
data class User(
    val id: String,
    val username: String,
    val avatarColor: Color,
    val initials: String,
    val isVerified: Boolean = false
)

/**
 * Represents an ephemeral story item in the horizontal stories carousel.
 */
data class Story(
    val user: User,
    val isViewed: Boolean = false,
    val isYourStory: Boolean = false
)

/**
 * Represents a media post in the main scrollable feed.
 */
data class Post(
    val id: String,
    val user: User,
    val imageColor: Color,
    val imageLabel: String,
    val likesCount: Int,
    val caption: String,
    val commentsCount: Int,
    val timeAgo: String,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false
)
```

---

### 2. Realistic Mock Data — `data/FakeData.kt`

Create `data/FakeData.kt`:

```kotlin
package com.example.instagramclone.data

import com.example.instagramclone.ui.theme.AvatarColors
import com.example.instagramclone.ui.theme.PostImageColors

object FakeData {

    // ─── Registered Users ────────────────────────────────────────────────────
    val currentUser = User("u0", "your_story", AvatarColors[0], "ME")
    val alice       = User("u1", "alice_wonder", AvatarColors[1], "AW", isVerified = true)
    val john        = User("u2", "john.explorer", AvatarColors[2], "JE")
    val sarah       = User("u3", "sarah_designs", AvatarColors[3], "SD", isVerified = true)
    val mike        = User("u4", "mike_fitness", AvatarColors[4], "MF")
    val emma        = User("u5", "emma.travels", AvatarColors[5], "ET")
    val david       = User("u6", "david_cooks", AvatarColors[6], "DC")
    val anna        = User("u7", "anna.music", AvatarColors[7], "AM")

    // ─── Stories Feed ────────────────────────────────────────────────────────
    val stories = listOf(
        Story(user = currentUser, isViewed = false, isYourStory = true),
        Story(user = alice,       isViewed = false),
        Story(user = john,        isViewed = false),
        Story(user = sarah,       isViewed = true),  // Viewed ➔ Displays Gray Ring
        Story(user = mike,        isViewed = false),
        Story(user = emma,        isViewed = true),  // Viewed ➔ Displays Gray Ring
        Story(user = david,       isViewed = false),
        Story(user = anna,        isViewed = false)
    )

    // ─── Posts Feed ──────────────────────────────────────────────────────────
    val posts = listOf(
        Post(
            id = "p1",
            user = alice,
            imageColor = PostImageColors[0],
            imageLabel = "🌅 Golden Hour by the Coast",
            likesCount = 1247,
            caption = "Best sunset ever! The colors over the ocean were absolutely unreal ✨",
            commentsCount = 48,
            timeAgo = "2 HOURS AGO"
        ),
        Post(
            id = "p2",
            user = john,
            imageColor = PostImageColors[1],
            imageLabel = "🏔️ Alpine Summit Trek",
            likesCount = 3891,
            caption = "Finally made it to the peak after 8 exhausting hours of hiking! 🥾",
            commentsCount = 124,
            timeAgo = "4 HOURS AGO"
        ),
        Post(
            id = "p3",
            user = sarah,
            imageColor = PostImageColors[3],
            imageLabel = "🎨 Studio Workspace Setup",
            likesCount = 892,
            caption = "New design project kicking off this week! Can't wait to share previews 🎉",
            commentsCount = 31,
            timeAgo = "6 HOURS AGO"
        ),
        Post(
            id = "p4",
            user = mike,
            imageColor = PostImageColors[2],
            imageLabel = "💪 5AM Morning Grind",
            likesCount = 2156,
            caption = "Discipline over motivation every single day. Let's get it! 🔥",
            commentsCount = 67,
            timeAgo = "8 HOURS AGO"
        ),
        Post(
            id = "p5",
            user = emma,
            imageColor = PostImageColors[4],
            imageLabel = "🗼 Paris After Midnight",
            likesCount = 5432,
            caption = "The city of lights never disappoints. Pure magic around every corner 🇫🇷❤️",
            commentsCount = 203,
            timeAgo = "12 HOURS AGO"
        ),
        Post(
            id = "p6",
            user = david,
            imageColor = PostImageColors[5],
            imageLabel = "🍝 Fresh Handmade Tagliatelle",
            likesCount = 1876,
            caption = "Grandma's secret recipe. Rolled and cut entirely by hand from scratch! 🇮🇹",
            commentsCount = 89,
            timeAgo = "1 DAY AGO"
        )
    )
}
```

---

## ⭕ Step 3: The Story Avatar & 3-Layer Concentric Circles

### Concentric Ring Geometry

The Instagram Story Avatar is built using **three concentric circles**:

```text
Visual Cross-Section:
┌─────────────────────────────────────────────────────────────┐
│ Layer 1 (Outer Ring):  68dp  Gradient Circle  (🟡🟠🔴🟣🔵)  │
│ Layer 2 (Middle Gap):  60dp  White Circle     (⬜ 4dp ring)  │
│ Layer 3 (Inner Photo): 54dp  Avatar Circle    (🟣 3dp gap)   │
└─────────────────────────────────────────────────────────────┘
```

```text
         ╭─────────────── 68dp (Gradient Ring)
       ╭─┴─────────────╮
     ╭─┴─────────────╮ │
     │ ╭───────────╮ │ │
     │ │   [AW]    │ │ │ 54dp (User Avatar)
     │ ╰───────────╯ │ │
     ╰───────────────╯ │ 60dp (White Gap Border)
       ╰───────────────╯
```

### Why Three Nested Boxes Instead of `Modifier.border`?

1. **Pixel-Perfect Spacing:** `Modifier.border` draws strictly at the boundary of a single node. Introducing a transparent or white gap between the gradient border and the avatar requires complex nested padding or custom canvas drawing.
2. **Predictable Layout Dimensions:** Nested `Box` composables make the 4dp ring thickness and 3dp white gap visually explicit and mathematically deterministic.
3. **Corner Badge Anchoring:** Wrapping the avatar inside a base `Box` allows us to position the blue `"Your Story"` `+` badge at `Alignment.BottomEnd` with exact overlap coordinates.

---

### Implementation — `ui/components/StoryAvatar.kt`

```kotlin
package com.example.instagramclone.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.instagramclone.data.Story
import com.example.instagramclone.ui.theme.InstagramBlue
import com.example.instagramclone.ui.theme.InstagramDarkGray
import com.example.instagramclone.ui.theme.InstagramWhite
import com.example.instagramclone.ui.theme.StoryGradientColors
import com.example.instagramclone.ui.theme.StoryViewedColors

/**
 * Renders an Instagram Story circle with:
 * - 5-stop sunset gradient ring (for unviewed stories)
 * - Muted gray ring (for viewed stories)
 * - Concentric white separator gap
 * - Circular avatar initials
 * - Bottom-right blue "+" badge for the user's personal story
 */
@Composable
fun StoryAvatar(
    story: Story,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // ─── 3 Concentric Boxes for the Gradient Ring ────────────────────────
        Box(contentAlignment = Alignment.Center) {

            // LAYER 1: Outermost Gradient Ring (68dp)
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = if (story.isViewed) StoryViewedColors else StoryGradientColors
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

                // LAYER 2: Middle White Gap Separator (60dp)
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(InstagramWhite),
                    contentAlignment = Alignment.Center
                ) {

                    // LAYER 3: Innermost Avatar Image / Initials (54dp)
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(story.user.avatarColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = story.user.initials,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // ─── "Your Story" Plus Badge Overlay ─────────────────────────────
            if (story.isYourStory) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(InstagramBlue)
                        .border(2.dp, InstagramWhite, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add to your story",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        // ─── Username Label Below Ring ───────────────────────────────────────
        Text(
            text = if (story.isYourStory) "Your story" else story.user.username,
            fontSize = 11.sp,
            color = InstagramDarkGray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.size(width = 68.dp, height = 15.dp)
        )
    }
}
```

---

## 🎞️ Step 4: Horizontal Stories Carousel

### Why Stories Belong Inside the `LazyColumn` Header

A common architectural anti-pattern is placing the stories row in a static `Column` directly above the `LazyColumn`:

```kotlin
// ❌ WRONG: Stories row stays permanently pinned at the top, wasting 100dp of screen space
Column {
    StoriesRow()
    LazyColumn { items(posts) { ... } }
}

// ✅ CORRECT: Stories row scrolls away naturally when the user swipes down the feed
LazyColumn {
    item(key = "stories_header") {
        StoriesRow()
    }
    items(items = posts, key = { it.id }) { post ->
        PostCard(post)
    }
}
```

Placing `StoriesRow` as the first `item {}` inside `LazyColumn`:
1. Mirrors the authentic Instagram mobile experience: the stories strip scrolls off-screen as you browse posts, maximizing visual focus on media.
2. Saves memory: When scrolled off-screen, Compose recycles the horizontal carousel items.

---

### Implementation — `ui/feed/StoriesRow.kt`

```kotlin
package com.example.instagramclone.ui.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.instagramclone.data.FakeData
import com.example.instagramclone.data.Story
import com.example.instagramclone.ui.components.StoryAvatar

/**
 * Horizontal scrolling row of story circles.
 * Built with LazyRow to lazily inflate only visible story avatars.
 */
@Composable
fun StoriesRow(
    stories: List<Story> = FakeData.stories,
    onStoryClick: (Story) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.padding(vertical = 6.dp),
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(
            items = stories,
            key = { story -> story.user.id }
        ) { story ->
            StoryAvatar(
                story = story,
                onClick = { onStoryClick(story) }
            )
        }
    }
}
```

---

## 🖼️ Step 5: The Post Card & Double-Tap Heart Animation

### Post Card Layout Breakdown

```text
PostCard (Column)
├── HEADER ROW
│   ├── User Avatar (34dp)
│   ├── Username (Bold) + Verified Badge (Blue Check)
│   └── More Options Icon (•••)
├── POST IMAGE (Box with aspectRatio(1f))
│   ├── Image Background + Centered Label
│   └── Animated Floating Heart Overlay (Popping ❤️ on double-tap)
├── ACTION ICONS ROW
│   ├── Left: Like (♡/❤️), Comment (💬), Share (➤)
│   └── Right: Bookmark (🔖)
├── LIKES TALLY ("1,247 likes", Bold)
├── CAPTION (buildAnnotatedString: bold username + inline body)
├── COMMENTS LINK ("View all 48 comments", Muted Gray)
├── TIMESTAMP ("2 HOURS AGO", 10sp)
└── HorizontalDivider (0.5dp Hairline)
```

### Seamless Paragraph Flow with `buildAnnotatedString`

If you place the username and caption into a `Row`:
```kotlin
// ❌ WRONG: Long captions produce awkward empty space below the username
Row {
    Text("alice_wonder", fontWeight = FontWeight.Bold)
    Text("This is a very long caption that wraps around to the next line...")
}
```

With `Row(Text, Text)`, the second line of text starts under the caption rather than under the username, leaving an unnatural white gap.

```text
Row(Text, Text) Defect:
alice_wonder  This is a very long caption that wraps
              to the next line and looks misaligned!

buildAnnotatedString Flow:
alice_wonder  This is a very long caption that wraps
to the next line seamlessly like a natural paragraph!
```

Using `buildAnnotatedString` renders the bold username and normal caption in a **single paragraph layout engine**, enabling flawless text wrapping.

---

### Double-Tap Heart Spring Overlay

When the user double-taps the photo:
1. `combinedClickable(onDoubleClick = { ... })` detects the gesture.
2. `showBigHeart` state flips to `true`.
3. `AnimatedVisibility(scaleIn(spring) + fadeIn, fadeOut)` pops a prominent white heart with a soft drop shadow into the center of the image.
4. A `LaunchedEffect(showBigHeart)` delays for 750ms and smoothly resets visibility, while automatically marking the post as liked!

---

### Implementation — `ui/components/PostCard.kt`

```kotlin
package com.example.instagramclone.ui.components

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.instagramclone.data.Post
import com.example.instagramclone.ui.theme.InstagramBlue
import com.example.instagramclone.ui.theme.InstagramDarkGray
import com.example.instagramclone.ui.theme.InstagramGray
import com.example.instagramclone.ui.theme.InstagramLightGray
import com.example.instagramclone.ui.theme.InstagramRed
import kotlinx.coroutines.delay

/**
 * Full-fidelity Instagram Feed Post Card.
 * Features:
 * - Square image container (aspectRatio(1f))
 * - Double-tap to like with springing heart animation
 * - Action icon row with animated state toggles
 * - AnnotatedString for continuous paragraph text wrapping
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PostCard(
    post: Post,
    onLikeClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isLiked by remember { mutableStateOf(post.isLiked) }
    var isBookmarked by remember { mutableStateOf(post.isBookmarked) }
    var likesCount by remember { mutableIntStateOf(post.likesCount) }
    var showBigHeart by remember { mutableStateOf(false) }

    // Auto-dismiss transient popping heart after 750ms
    LaunchedEffect(showBigHeart) {
        if (showBigHeart) {
            delay(750)
            showBigHeart = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {

        // ─── 1. Post Header: Avatar, Username, Verified Badge, Menu ──────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User Avatar
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(post.user.avatarColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = post.user.initials,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Username + Optional Verified Blue Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = post.user.username,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = InstagramDarkGray
                )
                if (post.user.isVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified Account",
                        tint = InstagramBlue,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            IconButton(onClick = { /* More options menu */ }, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "Post options",
                    tint = InstagramDarkGray
                )
            }
        }

        // ─── 2. Post Media Container: Square (aspectRatio = 1f) ──────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) // Enforces authentic 1:1 Instagram square ratio
                .background(post.imageColor)
                .combinedClickable(
                    onClick = { /* Open full screen photo */ },
                    onDoubleClick = {
                        if (!isLiked) {
                            isLiked = true
                            likesCount += 1
                            onLikeClick()
                        }
                        showBigHeart = true
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder Title
            Text(
                text = post.imageLabel,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.95f)
            )

            // Spring Animated Double-Tap Heart Overlay
            AnimatedVisibility(
                visible = showBigHeart,
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
                    tint = Color.White.copy(alpha = 0.92f),
                    modifier = Modifier.size(105.dp)
                )
            }
        }

        // ─── 3. Action Icons Bar ─────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Action Cluster: Like, Comment, Share
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    isLiked = !isLiked
                    likesCount += if (isLiked) 1 else -1
                    onLikeClick()
                }) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like post",
                        tint = if (isLiked) InstagramRed else InstagramDarkGray,
                        modifier = Modifier.size(26.dp)
                    )
                }

                IconButton(onClick = { /* Open comments modal sheet */ }) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comment",
                        tint = InstagramDarkGray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(onClick = { /* Share to direct messages */ }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Share",
                        tint = InstagramDarkGray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Right Action: Bookmark
            IconButton(onClick = {
                isBookmarked = !isBookmarked
                onBookmarkClick()
            }) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Save post",
                    tint = InstagramDarkGray,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        // ─── 4. Likes Tally ──────────────────────────────────────────────────
        Text(
            text = "${formatNumber(likesCount)} likes",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = InstagramDarkGray,
            modifier = Modifier.padding(horizontal = 14.dp)
        )

        // ─── 5. Seamless Caption via buildAnnotatedString ─────────────────────
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = InstagramDarkGray)) {
                    append(post.user.username)
                }
                append("  ${post.caption}")
            },
            fontSize = 13.sp,
            color = InstagramDarkGray,
            lineHeight = 18.sp,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 3.dp)
        )

        // ─── 6. Comments Link & Relative Timestamp ───────────────────────────
        if (post.commentsCount > 0) {
            Text(
                text = "View all ${post.commentsCount} comments",
                fontSize = 13.sp,
                color = InstagramGray,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp)
            )
        }

        Text(
            text = post.timeAgo,
            fontSize = 10.sp,
            color = InstagramGray,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 3.dp)
        )

        Spacer(modifier = Modifier.size(8.dp))

        // Hairline divider separating posts
        HorizontalDivider(thickness = 0.5.dp, color = InstagramLightGray)
    }
}

/**
 * Utility function to format 1247 into "1,247".
 */
private fun formatNumber(count: Int): String {
    return String.format("%,d", count)
}
```

---

## 🧭 Step 6: Top App Bar & Bottom Navigation Bar

### 1. Script Logo Top Bar — `ui/components/InstagramTopBar.kt`

```kotlin
package com.example.instagramclone.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.CameraAlt
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.instagramclone.ui.theme.InstagramWhite

/**
 * Top App Bar with authentic cursive branding wordmark and quick actions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstagramTopBar(
    onCameraClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onMessagesClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            // Emulates Instagram's calligraphy cursive script logo
            Text(
                text = "Instagram",
                fontFamily = FontFamily.Cursive,
                fontStyle = FontStyle.Italic,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        },
        navigationIcon = {
            IconButton(onClick = onCameraClick) {
                Icon(
                    imageVector = Icons.Outlined.CameraAlt,
                    contentDescription = "Story Camera",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        actions = {
            IconButton(onClick = onNotificationsClick) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Activity Notifications",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
            IconButton(onClick = onMessagesClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Send,
                    contentDescription = "Direct Messages",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = InstagramWhite)
    )
}
```

---

### 2. Custom Navigation Bar — `ui/bottomnav/InstagramBottomBar.kt`

Instagram's bottom navigation bar is distinguished by:
1. **Monochrome Aesthetic:** Unlike Material 3 defaults with colored indicator pills, Instagram icons remain strictly black in both active and inactive states.
2. **Icon State Swapping:** Active tabs render as **filled** vector glyphs, while inactive tabs render as **outlined** glyphs.

```kotlin
package com.example.instagramclone.ui.bottomnav

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
    onTabSelected: (InstagramTab) -> Unit
) {
    NavigationBar(
        containerColor = InstagramWhite,
        tonalElevation = 0.dp
    ) {
        // ── 1. HOME TAB ──────────────────────────────────────────────────────
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
            colors = instagramNavColors()
        )

        // ── 2. SEARCH TAB ────────────────────────────────────────────────────
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
            colors = instagramNavColors()
        )

        // ── 3. CREATE TAB ────────────────────────────────────────────────────
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
            colors = instagramNavColors()
        )

        // ── 4. REELS TAB ─────────────────────────────────────────────────────
        NavigationBarItem(
            selected = selectedTab == InstagramTab.REELS,
            onClick = { onTabSelected(InstagramTab.REELS) },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.SlowMotionVideo,
                    contentDescription = "Reels",
                    modifier = Modifier.size(26.dp)
                )
            },
            colors = instagramNavColors()
        )

        // ── 5. PROFILE TAB ───────────────────────────────────────────────────
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
            colors = instagramNavColors()
        )
    }
}

/**
 * Instagram uses all-black glyphs without rounded indicator pills.
 */
@Composable
private fun instagramNavColors() = NavigationBarItemDefaults.colors(
    selectedIconColor   = Color.Black,
    unselectedIconColor = Color.Black,
    indicatorColor      = Color.Transparent // Disables default Material 3 colored background pill
)
```

---

## 🚀 Step 7: Master Feed Screen & App Integration

### 1. Feed Screen — `ui/feed/FeedScreen.kt`

```kotlin
package com.example.instagramclone.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.instagramclone.data.FakeData
import com.example.instagramclone.data.Post
import com.example.instagramclone.data.Story
import com.example.instagramclone.ui.components.InstagramTopBar
import com.example.instagramclone.ui.components.PostCard
import com.example.instagramclone.ui.theme.InstagramWhite

/**
 * Primary Feed Screen assembling:
 * - Top calligraphy bar
 * - Collapsible horizontal stories header (first LazyColumn item)
 * - Vertical feed post cards with unique IDs
 */
@Composable
fun FeedScreen(
    onStoryClick: (Story) -> Unit,
    onPostLike: (Post) -> Unit,
    onPostBookmark: (Post) -> Unit,
    posts: List<Post> = FakeData.posts,
    stories: List<Story> = FakeData.stories,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = InstagramWhite,
        topBar = { InstagramTopBar() },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(InstagramWhite)
        ) {
            // ── Item 1: Horizontal Stories Carousel ──────────────────────────
            item(key = "stories_carousel_header") {
                StoriesRow(
                    stories = stories,
                    onStoryClick = onStoryClick
                )
            }

            // ── Items 2..N: Feed Posts ───────────────────────────────────────
            items(
                items = posts,
                key = { post -> post.id }
            ) { post ->
                PostCard(
                    post = post,
                    onLikeClick = { onPostLike(post) },
                    onBookmarkClick = { onPostBookmark(post) }
                )
            }
        }
    }
}
```

---

### 2. Master Orchestrator — `InstagramApp.kt`

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
import com.example.instagramclone.ui.theme.InstagramGray
import com.example.instagramclone.ui.theme.InstagramWhite

/**
 * Root Composable coordinating:
 * - Bottom Navigation Bar Tab Switching
 * - Home Feed Screen (Part 1)
 * - Placeholders for Search, Create, Reels, and Profile (Part 2)
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
                        Toast.makeText(
                            context,
                            "Viewing ${story.user.username}'s story",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onPostLike = { post ->
                        // Synchronize like event with backend repository
                    },
                    onPostBookmark = { post ->
                        Toast.makeText(context, "Post saved to collection", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${selectedTab.name}\n(Coming in Part 2)",
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

### 3. Entry Point — `MainActivity.kt`

```kotlin
package com.example.instagramclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.instagramclone.ui.theme.InstagramCloneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InstagramCloneTheme {
                InstagramApp()
            }
        }
    }
}
```

---

## 🔍 Deep Dive: Advanced Jetpack Compose Mechanics

### 1. Concentric Box Layering vs Canvas Ring Drawing

Why use three nested `Box` composables instead of `Canvas` or `Modifier.border`?

```kotlin
// Layer 1: 68dp Gradient
Box(Modifier.size(68.dp).clip(CircleShape).background(gradientBrush)) {
    // Layer 2: 60dp White Gap
    Box(Modifier.size(60.dp).clip(CircleShape).background(Color.White)) {
        // Layer 3: 54dp User Avatar
        Box(Modifier.size(54.dp).clip(CircleShape).background(avatarColor)) { ... }
    }
}
```

- **Visual Transparency Over Complexity:** With nested boxes, the dimensions (68dp ➔ 60dp ➔ 54dp) explicitly declare a 4dp gradient border followed by a 3dp clean white gap.
- **Hardware Acceleration:** Compose optimizes `clip(CircleShape).background(...)` directly on the GPU render node. It avoids custom Canvas draw calls and automatically preserves anti-aliased edge smoothing.

---

### 2. Inline Paragraph Flow with `buildAnnotatedString`

Consider what happens when trying to render a username and caption with two separate `Text` composables inside a `Row`:
- `Row` calculates layouts as distinct adjacent rectangular blocks.
- If the caption text is long enough to span multiple lines, the second line will wrap to the start of the *caption's* column, leaving an awkward gap underneath the username.
- `buildAnnotatedString` compiles the bold username and regular-weight caption into a single text span object. The layout engine treats it as a single cohesive sentence, wrapping lines naturally to the left margin!

---

### 3. Image Geometry Control with `Modifier.aspectRatio()`

Why is `Modifier.aspectRatio(1f)` essential for post images?
- `Modifier.fillMaxWidth()` only constrains horizontal width.
- A `Box` containing only a centered label has no intrinsic height—without `aspectRatio(1f)`, the image container collapses down to roughly ~24dp tall (the height of the text).
- `aspectRatio(1f)` instructs Compose: *"After measuring width $W$, force height $H = W \times 1.0$."* For modern 4:5 portrait feeds, simply set `aspectRatio(4f / 5f)`.

---

### 4. Double-Tap Detection with `combinedClickable` & Coroutine Debounce

Standard `Modifier.clickable` does not register double clicks. Compose provides `Modifier.combinedClickable`:

```kotlin
Modifier.combinedClickable(
    onClick = { /* regular tap */ },
    onDoubleClick = {
        isLiked = true
        showBigHeart = true
    }
)
```

Combining this with a `LaunchedEffect(showBigHeart)` creates a clean coroutine timer:

```kotlin
LaunchedEffect(showBigHeart) {
    if (showBigHeart) {
        delay(750)
        showBigHeart = false // Automatically hides after 750ms
    }
}
```

---

## 🧠 Jetpack Compose Principles Applied: Where & Why

| Compose Concept | Where It Is Used | Engineering Rationale |
| :--- | :--- | :--- |
| **`Brush.linearGradient`** | `StoryAvatar` outer ring | Recreates Instagram's signature 5-stop sunset gradient across a circular clip. |
| **Nested `Box` Composables** | `StoryAvatar` concentric circles | Delivers deterministic ring and gap widths without manual canvas math. |
| **`buildAnnotatedString`** | `PostCard` captions | Seamlessly blends bold username and caption text into a single wrapping paragraph. |
| **`Modifier.aspectRatio(1f)`** | `PostCard` media box | Locks post images into authentic 1:1 squares regardless of screen width. |
| **`combinedClickable`** | `PostCard` image interaction | Detects double-tap gestures to fire the transient popping heart animation. |
| **`LazyRow` in `LazyColumn`** | `StoriesRow` inside `FeedScreen` | Allows stories to naturally scroll off-screen, maximizing screen space for posts. |
| **Indicatorless `NavigationBar`**| `InstagramBottomBar` | Overrides Material 3's colored indicator pill to achieve Instagram's minimalist look. |

---

## 🧪 Self-Assessment & Knowledge Check

Test your understanding of concentric layout layering and media feed mechanics:

### 1. What happens if you attempt to build the Story Avatar using a single `Box` with `Modifier.border(gradientBrush)`?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
`Modifier.border` will draw the gradient directly against the avatar's outer edge without any spacing. Instagram's design requires a prominent 3dp white gap between the gradient ring and the profile photo. Achieving this with `border` alone requires nested padding tricks or custom draw phases, whereas the 3-Box concentric technique makes the geometry explicit and trivial to modify.
</details>

---

### 2. Why does `PostCard`'s caption use `buildAnnotatedString` instead of placing the username and caption in a `Row`?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
Placing two `Text` composables side-by-side in a `Row` breaks multiline wrapping. The second line of a long caption wraps to the start of the second `Text` rather than the left edge under the username, creating an awkward indentation. `buildAnnotatedString` passes both styles into a single layout paragraph, ensuring natural text flow across multiple lines.
</details>

---

### 3. What visual defect occurs if `Modifier.aspectRatio(1f)` is omitted from the post image `Box`?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
`Modifier.fillMaxWidth()` only dictates width; it provides no height constraint. Without `aspectRatio(1f)` or an explicit height, a container with only a text label collapses vertically to wrap its content (only ~20–30dp tall), ruining the square post layout.
</details>

---

### 4. Why should `StoriesRow` be an `item {}` inside `LazyColumn` rather than placed in a `Column` above the `LazyColumn`?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
Placing `StoriesRow` above `LazyColumn` permanently pins the stories strip to the top of the screen, consuming roughly 100dp of vertical space continuously. Placing it as the first `item {}` in `LazyColumn` allows the stories carousel to scroll away as the user browses the feed, matching the authentic Instagram experience and freeing memory when scrolled off-screen.
</details>

---

### 5. How does `instagramNavColors()` remove the default Material 3 colored selection pill in the bottom navigation bar?

<details>
<summary>Click to reveal answer</summary>

**Answer:**
By setting `indicatorColor = Color.Transparent` inside `NavigationBarItemDefaults.colors()`. This completely suppresses the background pill animation, allowing Instagram's pure outlined-to-filled icon transition to take center stage.
</details>

---

## 🏁 Checkpoint: What You Should Have Working

Verify that your Part 1 implementation is completely functional:

- [x] **Cursive Branding:** Top bar renders the italic cursive "Instagram" wordmark with camera and DM actions.
- [x] **Gradient Story Rings:** Unseen stories display the 5-color sunset gradient (`#FEDA75` ➔ `#4F5BD5`), while seen stories display a clean gray ring.
- [x] **Concentric Gaps:** Story avatars feature a clean white separation gap between the gradient ring and profile initials.
- [x] **Your Story Badge:** Current user's avatar displays the blue `+` icon anchored at the bottom-right.
- [x] **Inline Scroll:** Swiping down the feed smoothly scrolls away the stories carousel.
- [x] **Square Post Media:** Feed images render at an exact 1:1 square ratio (`aspectRatio(1f)`).
- [x] **Double-Tap to Like:** Double-tapping a post image triggers a popping white heart animation with spring physics and increments the like counter.
- [x] **Action Toggles:** Tapping the heart or bookmark icon toggles filled red or black states respectively.
- [x] **Inline Wrapped Captions:** Bold usernames seamlessly flow into caption sentences without indentation flaws.
- [x] **Minimalist Bottom Bar:** 5 tabs transition between outlined and filled states with pure black styling.

---

## 🏋️ Hands-On Coding Exercises

Take your Instagram clone to the next level with these practical UI challenges:

### 🎯 Exercise 1: Multi-Image Carousel with Indicator Dots
Extend `Post` to support `imageColors: List<Color>`. If a post has more than 1 image, wrap the image container in a horizontal `HorizontalPager` and render small circular pagination indicator dots centered below the media!

---

### 🎯 Exercise 2: Verified Account Checkmark Component
Create a reusable `VerifiedBadge` composable that draws the blue scalloped badge with white checkmark (`Icons.Default.CheckCircle`) and integrate it conditionally for select creators in the Stories row and Post headers.

---

### 🎯 Exercise 3: Flying Hearts Particle Effect
When the user taps the like heart button in the action bar, launch a small burst of 3 mini floating red hearts that float upward and fade out using `Animatable` and `Modifier.offset`!

---

## 🧭 What's Next in Part 2

In **Part 2**, we build the **Instagram Profile Screen**:
- User Profile Header: Large circular story avatar, followers / following / posts stats counters, and **"Edit Profile"** & **"Share Profile"** action buttons.
- Bio & Highlights Bar: Expandable bio with website links and circular Story Highlights carousel.
- Iconic 3-Column Profile Grid: Building a performant `LazyVerticalGrid(GridCells.Fixed(3))` of square image thumbnails with video badge indicators.
- Seamless Bottom Bar Navigation: Wire tab transitions between the Feed Screen and the Profile Grid!