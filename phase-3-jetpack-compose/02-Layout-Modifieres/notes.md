# 📐 Complete Guide to Layouts and Modifiers in Jetpack Compose

![Layouts and Modifiers](./layout%20and%20modifiers.png)

---

## ⬇️ Part 1: Column and Row in Depth

### 📦 Column — Vertical Layout

```text
COLUMN arranges children from TOP to BOTTOM.

  ┌─────────────────┐
  │   Child 1       │  ← Top
  │   Child 2       │  ← Middle
  │   Child 3       │  ← Bottom
  └─────────────────┘

Two key properties:
  verticalArrangement   → How children are spaced VERTICALLY
  horizontalAlignment   → How children are aligned HORIZONTALLY
```

```kotlin
@Composable
fun ColumnArrangementExamples() {

    // ─── Arrangement.Top (default) ─────────────────────
    // Children packed at the top
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.LightGray),
        verticalArrangement = Arrangement.Top
    ) {
        Box(Modifier.size(40.dp).background(Color.Red))
        Box(Modifier.size(40.dp).background(Color.Blue))
        Box(Modifier.size(40.dp).background(Color.Green))
    }
    // ┌──────────┐
    // │ ■ Red    │ ← All packed at top
    // │ ■ Blue   │
    // │ ■ Green  │
    // │          │
    // │ (empty)  │
    // └──────────┘

    // ─── Arrangement.Bottom ────────────────────────────
    // Children packed at the bottom
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.LightGray),
        verticalArrangement = Arrangement.Bottom
    ) {
        Box(Modifier.size(40.dp).background(Color.Red))
        Box(Modifier.size(40.dp).background(Color.Blue))
        Box(Modifier.size(40.dp).background(Color.Green))
    }
    // ┌──────────┐
    // │ (empty)  │
    // │          │
    // │ ■ Red    │ ← All packed at bottom
    // │ ■ Blue   │
    // │ ■ Green  │
    // └──────────┘

    // ─── Arrangement.Center ────────────────────────────
    // Children centered vertically
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.LightGray),
        verticalArrangement = Arrangement.Center
    ) {
        Box(Modifier.size(40.dp).background(Color.Red))
        Box(Modifier.size(40.dp).background(Color.Blue))
        Box(Modifier.size(40.dp).background(Color.Green))
    }
    // ┌──────────┐
    // │ (empty)  │
    // │ ■ Red    │ ← Centered
    // │ ■ Blue   │
    // │ ■ Green  │
    // │ (empty)  │
    // └──────────┘

    // ─── Arrangement.SpaceBetween ──────────────────────
    // First child at top, last at bottom, equal space between
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.LightGray),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(Modifier.size(40.dp).background(Color.Red))
        Box(Modifier.size(40.dp).background(Color.Blue))
        Box(Modifier.size(40.dp).background(Color.Green))
    }
    // ┌──────────┐
    // │ ■ Red    │ ← Top
    // │          │ ← Equal space
    // │ ■ Blue   │ ← Middle
    // │          │ ← Equal space
    // │ ■ Green  │ ← Bottom
    // └──────────┘

    // ─── Arrangement.SpaceAround ───────────────────────
    // Equal space AROUND each child (half-space at edges)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.LightGray),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Box(Modifier.size(40.dp).background(Color.Red))
        Box(Modifier.size(40.dp).background(Color.Blue))
        Box(Modifier.size(40.dp).background(Color.Green))
    }
    // ┌──────────┐
    // │  (half)  │ ← Half space at top edge
    // │ ■ Red    │
    // │  (full)  │ ← Full space between
    // │ ■ Blue   │
    // │  (full)  │ ← Full space between
    // │ ■ Green  │
    // │  (half)  │ ← Half space at bottom edge
    // └──────────┘

    // ─── Arrangement.SpaceEvenly ───────────────────────
    // Equal space everywhere (including edges)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.LightGray),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Box(Modifier.size(40.dp).background(Color.Red))
        Box(Modifier.size(40.dp).background(Color.Blue))
        Box(Modifier.size(40.dp).background(Color.Green))
    }
    // ┌──────────┐
    // │  (equal) │ ← Same space everywhere
    // │ ■ Red    │
    // │  (equal) │
    // │ ■ Blue   │
    // │  (equal) │
    // │ ■ Green  │
    // │  (equal) │
    // └──────────┘

    // ─── Arrangement.spacedBy() ────────────────────────
    // Fixed gap between children (MOST USEFUL in practice)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Item 1")
        Text("Item 2")
        Text("Item 3")
        // Exactly 12dp gap between each item
        // No gap before first or after last item
    }
}
```

---

### 🔀 Column Horizontal Alignment

```kotlin
@Composable
fun ColumnAlignmentExamples() {

    // horizontalAlignment controls where children sit HORIZONTALLY

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray),
        horizontalAlignment = Alignment.Start  // Left-aligned (default)
    ) {
        Text("Left")
        Text("Aligned")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray),
        horizontalAlignment = Alignment.CenterHorizontally  // Centered
    ) {
        Text("Centered")
        Text("Text")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray),
        horizontalAlignment = Alignment.End  // Right-aligned
    ) {
        Text("Right")
        Text("Aligned")
    }
}
```

---

### ➡️ Row — Horizontal Layout

```text
ROW arranges children from LEFT to RIGHT.

  ┌───────────────────────────────────┐
  │  Child 1  │  Child 2  │  Child 3 │
  └───────────────────────────────────┘

Two key properties:
  horizontalArrangement → How children are spaced HORIZONTALLY
  verticalAlignment     → How children are aligned VERTICALLY
```

```kotlin
@Composable
fun RowExamples() {

    // ─── Basic Row with spacedBy ───────────────────────
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Star, contentDescription = null, tint = Color.Yellow)
        Text("4.5", fontWeight = FontWeight.Bold)
        Text("(2,340 reviews)", color = Color.Gray, fontSize = 12.sp)
    }

    // ─── Row with SpaceBetween (common for headers) ────
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Settings", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Icon(Icons.Default.Settings, contentDescription = "Settings")
    }
    // ┌──────────────────────────────────┐
    // │ Settings              ⚙️        │
    // └──────────────────────────────────┘

    // ─── Row vertical alignment comparison ─────────────
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color.LightGray),
        verticalAlignment = Alignment.Top  // All children at top
    ) {
        Box(Modifier.size(30.dp).background(Color.Red))
        Box(Modifier.size(60.dp).background(Color.Blue))
        Box(Modifier.size(40.dp).background(Color.Green))
    }
    // ┌──────────────────────────┐
    // │ ■  ■■  ■                │ ← All tops aligned
    // │    ■■                    │
    // │    ■■                    │
    // └──────────────────────────┘

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color.LightGray),
        verticalAlignment = Alignment.CenterVertically  // Centered
    ) {
        Box(Modifier.size(30.dp).background(Color.Red))
        Box(Modifier.size(60.dp).background(Color.Blue))
        Box(Modifier.size(40.dp).background(Color.Green))
    }
    // ┌──────────────────────────┐
    // │       ■■                 │
    // │ ■     ■■    ■           │ ← All centers aligned
    // │       ■■                 │
    // └──────────────────────────┘

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color.LightGray),
        verticalAlignment = Alignment.Bottom  // All at bottom
    ) {
        Box(Modifier.size(30.dp).background(Color.Red))
        Box(Modifier.size(60.dp).background(Color.Blue))
        Box(Modifier.size(40.dp).background(Color.Green))
    }
    // ┌──────────────────────────┐
    // │       ■■                 │
    // │       ■■                 │
    // │ ■     ■■    ■           │ ← All bottoms aligned
    // └──────────────────────────┘
}
```

---

---

## 🗂️ Part 2: Box — Layering Elements

### 💡 How Box Works

```text
BOX stacks children ON TOP of each other like layers.
The LAST child is drawn on TOP.

  Layer 3 (top):    Text "Hello"
  Layer 2 (middle): Semi-transparent overlay
  Layer 1 (bottom): Background image

  ┌──────────────────────┐
  │  Background Image    │  ← First child (bottom)
  │  ┌────────────────┐  │
  │  │ Dark Overlay   │  │  ← Second child (middle)
  │  │  ┌──────────┐  │  │
  │  │  │  Hello   │  │  │  ← Third child (top)
  │  │  └──────────┘  │  │
  │  └────────────────┘  │
  └──────────────────────┘
```

```kotlin
@Composable
fun BoxExamples() {

    // ─── Basic Box with contentAlignment ───────────────
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.LightGray),
        contentAlignment = Alignment.Center  // Center all children
    ) {
        Text("I am centered!", fontSize = 24.sp)
    }

    // ─── Box with multiple alignment positions ─────────
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.LightGray)
            .padding(8.dp)
    ) {
        Text("TopStart", modifier = Modifier.align(Alignment.TopStart))
        Text("TopEnd", modifier = Modifier.align(Alignment.TopEnd))
        Text("Center", modifier = Modifier.align(Alignment.Center))
        Text("BottomStart", modifier = Modifier.align(Alignment.BottomStart))
        Text("BottomEnd", modifier = Modifier.align(Alignment.BottomEnd))
    }
    // ┌──────────────────────────┐
    // │ TopStart       TopEnd   │
    // │                          │
    // │        Center            │
    // │                          │
    // │ BottomStart   BottomEnd │
    // └──────────────────────────┘

    // ─── Badge on top of icon (real-world use case) ────
    Box(
        modifier = Modifier.padding(16.dp)
    ) {
        // Bottom layer: Bell icon
        Icon(
            Icons.Default.Notifications,
            contentDescription = "Notifications",
            modifier = Modifier.size(48.dp),
            tint = Color.Gray
        )

        // Top layer: Red badge with count
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 4.dp, y = (-4).dp)  // Slight offset for positioning
                .size(20.dp)
                .background(Color.Red, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "3",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
    //    ┌──┐
    //   │ 3 │  ← Badge on top-right
    //  🔔
}
```

---

---

## 🧩 Part 3: ConstraintLayout in Compose

### 🤔 When and Why to Use ConstraintLayout

```text
Column and Row are great for simple, linear layouts.
But for COMPLEX layouts with many interdependent positions,
nesting Column/Row/Box creates deep hierarchies that are:
  - Hard to read
  - Hard to maintain
  - Slightly less performant (more nesting = more measurement passes)

ConstraintLayout lets you position elements relative to each other
using CONSTRAINTS (links between edges of elements).

USE ConstraintLayout WHEN:
  ✅ You have a complex layout with many overlapping elements
  ✅ Elements need to be positioned relative to each other
  ✅ You would need 3+ levels of nested Column/Row/Box
  ✅ You are migrating from XML ConstraintLayout

USE Column/Row/Box WHEN:
  ✅ Simple vertical or horizontal lists
  ✅ 1-2 levels of nesting is enough
  ✅ The layout is straightforward
  ✅ Most of your daily UI work (90% of cases!)
```

---

### 🏗️ ConstraintLayout Code Example

```kotlin
// Add to build.gradle:
// implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")

import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension

@Composable
fun ConstraintLayoutExample() {

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
    ) {
        // Create references for each composable:
        val (avatar, name, subtitle, followBtn, badge) = createRefs()

        // Avatar — constrained to top-start of parent
        Image(
            painter = painterResource(id = R.drawable.profile_photo),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .constrainAs(avatar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
        )

        // Name — constrained to the right of avatar, top of parent
        Text(
            text = "Rohit Kumar",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.constrainAs(name) {
                top.linkTo(avatar.top)
                start.linkTo(avatar.end, margin = 12.dp)
            }
        )

        // Subtitle — constrained below name, right of avatar
        Text(
            text = "Android Developer",
            color = Color.Gray,
            modifier = Modifier.constrainAs(subtitle) {
                top.linkTo(name.bottom, margin = 4.dp)
                start.linkTo(name.start)
            }
        )

        // Follow button — constrained to end of parent, centered vertically
        Button(
            onClick = { },
            modifier = Modifier.constrainAs(followBtn) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
            }
        ) {
            Text("Follow")
        }

        // Badge — constrained to top-end of avatar
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(Color.Green, CircleShape)
                .constrainAs(badge) {
                    bottom.linkTo(avatar.bottom)
                    end.linkTo(avatar.end)
                }
        )
    }
}

// COMPARISON: Same layout with nested Column/Row:
@Composable
fun SameLayoutWithColumnRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            Image(
                painter = painterResource(id = R.drawable.profile_photo),
                contentDescription = "Avatar",
                modifier = Modifier.size(60.dp).clip(CircleShape)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(16.dp)
                    .background(Color.Green, CircleShape)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Rohit Kumar", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Android Developer", color = Color.Gray)
        }
        Button(onClick = { }) { Text("Follow") }
    }
}

// In this case, Column/Row is actually simpler!
// ConstraintLayout shines when the layout is MORE complex
// than what Column/Row can handle cleanly.
```

---

---

## 🏗️ Part 4: Nested Layouts

### 🔀 Combining Column, Row, and Box

```kotlin
@Composable
fun RestaurantCard() {
    // OUTER: Column for the entire card (vertical stack)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {

        // TOP SECTION: Box for image with overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            // Layer 1: Food image
            Image(
                painter = painterResource(id = R.drawable.biryani),
                contentDescription = "Biryani",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Layer 2: Delivery time badge (top-right)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(
                        Color.Black.copy(alpha = 0.7f),
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("30 min", color = Color.White, fontSize = 12.sp)
            }

            // Layer 3: Discount badge (top-left)
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .background(Color(0xFF4CAF50), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("20% OFF", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // MIDDLE SECTION: Row for name and rating
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Name and cuisine (nested Column inside Row)
            Column {
                Text("Biryani House", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Indian • Mughlai", color = Color.Gray, fontSize = 12.sp)
            }

            // Right side: Rating (nested Row inside Row)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("4.5", fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // BOTTOM SECTION: Row for delivery info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("🕐 30-40 min", fontSize = 12.sp, color = Color.DarkGray)
            Text("📍 2.5 km", fontSize = 12.sp, color = Color.DarkGray)
            Text("💰 ₹40", fontSize = 12.sp, color = Color.DarkGray)
        }
    }
}
```

---

### 🌳 Layout Structure Diagram

```text
RestaurantCard (Column)
├── Box (image area)
│   ├── Image (background)
│   ├── Box (delivery time badge, TopEnd)
│   │   └── Text "30 min"
│   └── Box (discount badge, TopStart)
│       └── Text "20% OFF"
├── Spacer (8dp)
├── Row (name + rating)
│   ├── Column (left side)
│   │   ├── Text "Biryani House"
│   │   └── Text "Indian • Mughlai"
│   └── Row (right side)
│       ├── Icon (star)
│       ├── Spacer (4dp)
│       └── Text "4.5"
├── Spacer (4dp)
└── Row (delivery info)
    ├── Text "🕐 30-40 min"
    ├── Text "📍 2.5 km"
    └── Text "💰 ₹40"
```

---

---

## ⚠️ Part 5: Modifier Order — Why It Matters

### 🏆 The Golden Rule

```text
MODIFIERS ARE APPLIED FROM OUTSIDE TO INSIDE.

The FIRST modifier in the chain is the OUTERMOST layer.
The LAST modifier in the chain is the INNERMOST layer.

Think of it like wrapping a gift:
  1. First you put the gift in a box (first modifier)
  2. Then you wrap it in paper (second modifier)
  3. Then you tie a ribbon (third modifier)
  
  The ribbon is on the outside, the box is on the inside.
  Similarly, the first modifier wraps around everything else.
```

---

### 🎨 The Classic Example: Padding vs Background

```kotlin
@Composable
fun ModifierOrderDemo() {

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ─── CASE A: background THEN padding ───────────
        Text(
            text = "Background → Padding",
            color = Color.White,
            modifier = Modifier
                .background(Color.Blue)     // 1. Blue fills entire area
                .padding(24.dp)             // 2. Padding INSIDE the blue
        )
        // ┌──────────────────────────────┐
        // │██████████████████████████████│  ← Blue extends to edges
        // │██████                  ██████│
        // │██████  Background →   ██████│  ← Text is 24dp inside blue
        // │██████  Padding        ██████│
        // │██████                  ██████│
        // │██████████████████████████████│
        // └──────────────────────────────┘

        // ─── CASE B: padding THEN background ───────────
        Text(
            text = "Padding → Background",
            color = Color.White,
            modifier = Modifier
                .padding(24.dp)             // 1. 24dp space OUTSIDE first
                .background(Color.Blue)     // 2. Blue starts AFTER padding
        )
        // ┌──────────────────────────────┐
        // │                              │  ← Empty space (padding)
        // │    ┌──────────────────┐      │
        // │    │ Padding →       │      │  ← Blue wraps text tightly
        // │    │ Background      │      │
        // │    └──────────────────┘      │
        // │                              │  ← Empty space (padding)
        // └──────────────────────────────┘

        // ─── CASE C: The practical pattern ─────────────
        // Background → Padding (for buttons and cards)
        Text(
            text = "Order Now",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF4CAF50), RoundedCornerShape(8.dp))
                .padding(16.dp)  // Padding INSIDE the green background
        )
        // ┌──────────────────────────────┐
        // │██████████████████████████████│
        // │██████                  ██████│
        // │██████    Order Now     ██████│  ← Green background with
        // │██████                  ██████│    text padded inside
        // │██████████████████████████████│
        // └──────────────────────────────┘
    }
}
```

---

### 🔍 More Order Examples

```kotlin
@Composable
fun MoreOrderExamples() {

    // ─── Clip THEN Border vs Border THEN Clip ──────────

    // Clip first → border follows the clipped shape
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)              // 1. Clip to circle
            .border(4.dp, Color.Red)        // 2. Border follows circle shape
            .background(Color.Blue)
    )
    // Result: Red circular border around blue circle ✅

    // Border first → clip cuts the border!
    Box(
        modifier = Modifier
            .size(100.dp)
            .border(4.dp, Color.Red)        // 1. Square border
            .clip(CircleShape)              // 2. Clips EVERYTHING to circle
            .background(Color.Blue)         //    including the border edges
    )
    // Result: Border might be partially clipped ⚠️

    // ─── Clickable area depends on order ───────────────

    // Clickable AFTER padding → larger tap area (includes padding)
    Text(
        text = "Tap me",
        modifier = Modifier
            .clickable { Log.d("Click", "Tapped!") }
            .padding(24.dp)  // Padding is INSIDE the clickable area
    )
    // Tap area: includes the 24dp padding → EASY to tap ✅

    // Clickable BEFORE padding → smaller tap area (only the text)
    Text(
        text = "Tap me",
        modifier = Modifier
            .padding(24.dp)
            .clickable { Log.d("Click", "Tapped!") }
            // Clickable only covers the text, not the padding
    )
    // Tap area: only the text itself → HARD to tap ❌
}
```

---

---

## 📏 Part 6: Sizing Modifiers

### 🛠️ All Sizing Options

```kotlin
@Composable
fun SizingModifiersDemo() {

    // ─── FIXED SIZE ────────────────────────────────────
    Box(Modifier.width(200.dp).height(100.dp).background(Color.Red))
    Box(Modifier.size(100.dp).background(Color.Blue))  // 100×100 square
    Box(Modifier.size(width = 200.dp, height = 50.dp).background(Color.Green))

    // ─── FILL PARENT ───────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxWidth()       // Takes 100% of parent's width
            .height(50.dp)
            .background(Color.Red)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth(0.5f)   // Takes 50% of parent's width
            .height(50.dp)
            .background(Color.Blue)
    )

    Box(
        modifier = Modifier
            .fillMaxHeight()      // Takes 100% of parent's height
            .width(50.dp)
            .background(Color.Green)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()        // Takes 100% width AND height
            .background(Color.Yellow)
    )

    // ─── WRAP CONTENT ──────────────────────────────────
    // Size wraps tightly around the content (default behavior)
    Text(
        text = "I wrap my content",
        modifier = Modifier
            .wrapContentWidth()   // Width = exactly as wide as text
            .wrapContentHeight()  // Height = exactly as tall as text
            .background(Color.LightGray)
    )

    // ─── COMBINATION ───────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxWidth()       // Full width
            .wrapContentHeight()  // Height wraps content
            .background(Color.LightGray)
            .padding(16.dp)
    ) {
        Text("Full width, height wraps me")
    }

    // ─── REQUIRED SIZE (forces exact size) ─────────────
    Box(
        modifier = Modifier
            .requiredSize(100.dp)  // Forces exactly 100dp, ignores parent constraints
            .background(Color.Purple)
    )

    // ─── DEFAULT MIN SIZE ──────────────────────────────
    Box(
        modifier = Modifier
            .defaultMinSize(minWidth = 100.dp, minHeight = 50.dp)
            .background(Color.Cyan)
    ) {
        Text("Hi")  // Box will be at least 100×50 even though text is small
    }
}
```

---

---

## 📐 Part 7: Padding vs Margin in Compose

### 💡 There Is No "Margin" in Compose!

```text
In XML layouts, you had TWO spacing concepts:
  android:padding  → Space INSIDE the view (between border and content)
  android:layout_margin → Space OUTSIDE the view (between views)

In Compose, there is ONLY padding. No margin keyword exists.

WHY? Because padding BEFORE a modifier acts like margin,
and padding AFTER a modifier acts like padding.
The ORDER of the padding modifier determines its behavior!
```

---

### 🏗️ How Spacing Works in Compose

```kotlin
@Composable
fun PaddingVsMarginDemo() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(16.dp)  // This is "margin" from the screen edges
    ) {

        // ─── "MARGIN" (space OUTSIDE the element) ──────
        // Achieved by adding padding to the PARENT
        // or using Arrangement.spacedBy()

        // Method 1: spacedBy (RECOMMENDED for gaps between siblings)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Item 1", modifier = Modifier.background(Color.White))
            Text("Item 2", modifier = Modifier.background(Color.White))
            Text("Item 3", modifier = Modifier.background(Color.White))
            // 12dp gap between each item — this is "margin"
        }

        // Method 2: Spacer (explicit gap)
        Column {
            Text("Item 1", modifier = Modifier.background(Color.White))
            Spacer(modifier = Modifier.height(12.dp))  // "margin" between items
            Text("Item 2", modifier = Modifier.background(Color.White))
        }

        // Method 3: Padding modifier BEFORE background (acts like margin)
        Text(
            text = "I have margin",
            modifier = Modifier
                .padding(16.dp)           // Space OUTSIDE the background
                .background(Color.Blue)   // Background starts after padding
        )
        // ┌──────────────────────────┐
        // │  (16dp empty space)      │ ← "margin"
        // │  ┌──────────────────┐    │
        // │  │ I have margin    │    │ ← Blue background
        // │  └──────────────────┘    │
        // │  (16dp empty space)      │ ← "margin"
        // └──────────────────────────┘

        // ─── "PADDING" (space INSIDE the element) ──────
        // Achieved by adding padding AFTER background

        Text(
            text = "I have padding",
            modifier = Modifier
                .background(Color.Blue)   // Background fills the area
                .padding(16.dp)           // Space INSIDE the background
        )
        // ┌──────────────────────────┐
        // │██████████████████████████│
        // │████                  ████│ ← Blue extends to edges
        // │████  I have padding  ████│ ← Text is 16dp inside blue
        // │████                  ████│
        // │██████████████████████████│
        // └──────────────────────────┘

        // ─── BOTH "MARGIN" AND "PADDING" ───────────────
        Text(
            text = "Margin + Padding",
            color = Color.White,
            modifier = Modifier
                .padding(16.dp)           // "Margin" (outside)
                .background(Color.Blue, RoundedCornerShape(8.dp))
                .padding(12.dp)           // "Padding" (inside)
        )
        // ┌──────────────────────────┐
        // │  (16dp margin)           │
        // │  ┌──────────────────┐    │
        // │  │██(12dp padding)██│    │
        // │  │██ Margin+Pad  ██│    │
        // │  │██████████████████│    │
        // │  └──────────────────┘    │
        // │  (16dp margin)           │
        // └──────────────────────────┘
    }
}
```

---

---

## ✂️ Part 8: `clip` and `shape`

```kotlin
@Composable
fun ClipAndShapeExamples() {

    // ─── Rounded corners ───────────────────────────────
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(16.dp))  // 16dp rounded corners
            .background(Color.Blue)
    )

    // ─── Different corner radii ────────────────────────
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 0.dp,
                bottomStart = 0.dp,
                bottomEnd = 20.dp
            ))
            .background(Color.Green)
    )
    // Only top-left and bottom-right corners are rounded

    // ─── Circle ────────────────────────────────────────
    Image(
        painter = painterResource(id = R.drawable.profile),
        contentDescription = "Profile",
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)  // Perfect circle
            .border(2.dp, Color.Gray, CircleShape)
    )

    // ─── CutCornerShape (diagonal corners) ─────────────
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CutCornerShape(16.dp))  // Diagonal cut corners
            .background(Color.Red)
    )

    // ─── IMPORTANT: clip vs background shape ───────────
    // clip actually CUTS the content to the shape
    // background shape only paints the background in that shape

    // clip: Image is actually cut into a circle
    Image(
        painter = painterResource(id = R.drawable.photo),
        contentDescription = null,
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)  // Image pixels outside circle are GONE
    )

    // background shape: Only the background is circular, image overflows!
    Image(
        painter = painterResource(id = R.drawable.photo),
        contentDescription = null,
        modifier = Modifier
            .size(100.dp)
            .background(Color.Gray, CircleShape)  // Only BG is circular
            // Image corners will stick out! ❌
    )
}
```

> **📌 Important:** Use `clip()` when you want to **physically cut** content to a shape (e.g., circular profile images). Use `background(shape = ...)` when you only want the **background painted** in a shape without clipping the content.

---

---

## 🎨 Part 9: `background` and `border`

```kotlin
@Composable
fun BackgroundAndBorderExamples() {

    // ─── Solid background ──────────────────────────────
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(Color.Blue)
    )

    // ─── Background with shape ─────────────────────────
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(
                color = Color(0xFF6200EE),
                shape = RoundedCornerShape(12.dp)
            )
    )

    // ─── Gradient background ───────────────────────────
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Blue, Color.Cyan)
                ),
                shape = RoundedCornerShape(8.dp)
            )
    )

    // ─── Vertical gradient ─────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                )
            )
    )

    // ─── Simple border ─────────────────────────────────
    Box(
        modifier = Modifier
            .size(100.dp)
            .border(2.dp, Color.Gray)
    )

    // ─── Border with shape ─────────────────────────────
    Box(
        modifier = Modifier
            .size(100.dp)
            .border(
                width = 2.dp,
                color = Color.Blue,
                shape = RoundedCornerShape(12.dp)
            )
    )

    // ─── Dashed border (using drawBehind) ──────────────
    Box(
        modifier = Modifier
            .size(100.dp)
            .drawBehind {
                drawRoundRect(
                    color = Color.Gray,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(10f, 10f), 0f
                        )
                    ),
                    cornerRadius = CornerRadius(12.dp.toPx())
                )
            }
    )
}
```

---

---

## 👆 Part 10: `clickable` and `combinedClickable`

```kotlin
@Composable
fun ClickableExamples() {

    // ─── Basic clickable ───────────────────────────────
    Text(
        text = "Tap me!",
        modifier = Modifier
            .clickable {
                Log.d("Click", "Single tap!")
            }
            .padding(16.dp)
    )

    // ─── Clickable with ripple effect (default) ────────
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = Color.Blue)
            ) {
                Log.d("Click", "Custom ripple!")
            }
    )

    // ─── combinedClickable (single, double, long press) ─
    Text(
        text = "Tap, Double-tap, or Long-press me!",
        modifier = Modifier
            .combinedClickable(
                onClick = { Log.d("Click", "Single tap") },
                onDoubleClick = { Log.d("Click", "Double tap!") },
                onLongClick = { Log.d("Click", "Long press!") }
            )
            .padding(16.dp)
    )

    // ─── Clickable with enabled/disabled state ─────────
    var isEnabled by remember { mutableStateOf(true) }

    Button(
        onClick = { Log.d("Click", "Button pressed") },
        enabled = isEnabled  // When false, button is grayed out and not clickable
    ) {
        Text(if (isEnabled) "Active" else "Disabled")
    }

    // ─── Making an entire card clickable ───────────────
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .clickable { Log.d("Click", "Card tapped!") }
            .padding(16.dp)
    ) {
        Text("Restaurant Name", fontWeight = FontWeight.Bold)
        Text("Indian • Mughlai", color = Color.Gray)
    }
}
```

---

---

## ⚖️ Part 11: `weight()` — Distributing Space

### 💡 How weight Works

```text
weight() distributes REMAINING space among children in a Row or Column.

IMPORTANT: weight() only works INSIDE a Row or Column.
It tells the parent: "Give me this proportion of the leftover space."

HOW IT WORKS:
  1. Parent measures all children WITHOUT weight first
  2. Calculates remaining space
  3. Distributes remaining space proportionally by weight

EXAMPLE:
  Row has 300dp width.
  Child A: fixed 100dp
  Child B: weight(1f)
  Child C: weight(2f)

  Remaining space = 300 - 100 = 200dp
  Total weight = 1 + 2 = 3
  Child B gets: 200 × (1/3) = 66.7dp
  Child C gets: 200 × (2/3) = 133.3dp
```

```kotlin
@Composable
fun WeightExamples() {

    // ─── Equal weights (50/50 split) ───────────────────
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .weight(1f)        // Gets 50% of width
                .height(50.dp)
                .background(Color.Red)
        )
        Box(
            modifier = Modifier
                .weight(1f)        // Gets 50% of width
                .height(50.dp)
                .background(Color.Blue)
        )
    }
    // ┌──────────────┬──────────────┐
    // │   Red (50%)  │  Blue (50%)  │
    // └──────────────┴──────────────┘

    // ─── Unequal weights (1:2 ratio) ───────────────────
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .weight(1f)        // Gets 33% of width
                .height(50.dp)
                .background(Color.Red)
        )
        Box(
            modifier = Modifier
                .weight(2f)        // Gets 67% of width
                .height(50.dp)
                .background(Color.Blue)
        )
    }
    // ┌─────────┬───────────────────┐
    // │Red (33%)│   Blue (67%)      │
    // └─────────┴───────────────────┘

    // ─── Fixed + weight (most common pattern) ──────────
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Search, contentDescription = null)  // Fixed size
        Spacer(modifier = Modifier.width(8.dp))                // Fixed size
        TextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.weight(1f),  // Takes ALL remaining space!
            placeholder = { Text("Search...") }
        )
    }
    // ┌───┬─┬─────────────────────────┐
    // │ 🔍│ │ Search...               │
    // └───┴─┴─────────────────────────┘

    // ─── weight in Column (vertical distribution) ──────
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f)          // Gets 2/3 of height
                .background(Color.Blue)
        ) {
            Text("Header (2/3)", color = Color.White)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)          // Gets 1/3 of height
                .background(Color.Green)
        ) {
            Text("Footer (1/3)", color = Color.White)
        }
    }

    // ─── weight with fill = false ──────────────────────
    // fill = false: weight distributes space but child can be smaller
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Short",
            modifier = Modifier
                .weight(1f, fill = false)  // Won't force fill if content is small
                .background(Color.LightGray)
        )
        Text(
            "This is a much longer text that takes more space",
            modifier = Modifier
                .weight(1f, fill = false)
                .background(Color.LightGray)
        )
    }
}
```

---

---

## 💬 Part 12: Real Example — WhatsApp-Style Chat Bubble

### 🏗️ The Complete Chat Bubble Layout

```kotlin
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── DATA MODEL ───────────────────────────────────────────

data class ChatMessage(
    val text: String,
    val time: String,
    val isSentByMe: Boolean,
    val isRead: Boolean = false
)

// ─── SINGLE CHAT BUBBLE ───────────────────────────────────

@Composable
fun ChatBubble(message: ChatMessage) {

    // The entire row: bubble is pushed left or right using weight
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (message.isSentByMe) 60.dp else 8.dp,  // Indent sent messages
                end = if (message.isSentByMe) 8.dp else 60.dp,    // Indent received messages
                top = 2.dp,
                bottom = 2.dp
            ),
        horizontalArrangement = if (message.isSentByMe) Arrangement.End else Arrangement.Start
    ) {

        // The bubble itself (Box for layering message + time)
        Box(
            modifier = Modifier
                .background(
                    color = if (message.isSentByMe)
                        Color(0xFFDCF8C6)  // WhatsApp green for sent
                    else
                        Color.White,        // White for received
                    shape = RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = if (message.isSentByMe) 12.dp else 0.dp,
                        bottomEnd = if (message.isSentByMe) 0.dp else 12.dp
                    )
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {

            // Inside the bubble: Column for message text + time row
            Column {

                // Message text
                Text(
                    text = message.text,
                    fontSize = 15.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Time + read receipt row (aligned to end)
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = message.time,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )

                    // Double tick for sent messages
                    if (message.isSentByMe) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = if (message.isRead) "Read" else "Delivered",
                            modifier = Modifier.size(14.dp),
                            tint = if (message.isRead) Color(0xFF34B7F1) else Color.Gray
                            // Blue ticks = read, Gray ticks = delivered
                        )
                    }
                }
            }
        }
    }
}

// ─── CHAT SCREEN ──────────────────────────────────────────

@Composable
fun ChatScreen() {

    val messages = listOf(
        ChatMessage("Hey! How are you? 😊", "10:30 AM", isSentByMe = false),
        ChatMessage("I'm great! Just finished the Android project", "10:31 AM", isSentByMe = true, isRead = true),
        ChatMessage("That's awesome! Can you share the code?", "10:32 AM", isSentByMe = false),
        ChatMessage("Sure! I'll push it to GitHub tonight", "10:33 AM", isSentByMe = true, isRead = true),
        ChatMessage("Perfect! Let me know when it's up 🚀", "10:34 AM", isSentByMe = false),
        ChatMessage("Will do! 👍", "10:35 AM", isSentByMe = true, isRead = false),
    )

    var messageText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFECE5DD))  // WhatsApp chat background color
    ) {

        // ── TOP BAR ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF075E54))  // WhatsApp dark green
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(id = R.drawable.profile_photo),
                contentDescription = "Contact photo",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Priya Sharma", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("online", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
            Icon(Icons.Default.Call, contentDescription = "Call", tint = Color.White)
            Spacer(modifier = Modifier.width(16.dp))
            Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
        }

        // ── MESSAGES LIST ──
        Column(
            modifier = Modifier
                .weight(1f)  // Takes all remaining vertical space
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
        ) {
            messages.forEach { message ->
                ChatBubble(message = message)
            }
        }

        // ── MESSAGE INPUT BAR ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFECE5DD))
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Text input field (takes most of the space using weight)
            Row(
                modifier = Modifier
                    .weight(1f)  // Takes all space except the send button
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.EmojiEmotions,
                    contentDescription = "Emoji",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Message", color = Color.Gray) },
                    modifier = Modifier.weight(1f),  // TextField takes remaining space inside the Row
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                Icon(
                    Icons.Default.AttachFile,
                    contentDescription = "Attach",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Send button (fixed size, does NOT use weight)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF075E54), CircleShape)
                    .clickable { /* send message */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// ─── PREVIEW ──────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChatScreenPreview() {
    ChatScreen()
}
```

---

### 🖼️ Visual Structure of the Chat Screen

```text
┌──────────────────────────────────────┐
│ ←  👤 Priya Sharma        📞  ⋮    │  ← Top Bar (Row)
│      online                          │
├──────────────────────────────────────┤
│                                      │
│  ┌─────────────────────┐             │
│  │ Hey! How are you? 😊│             │  ← Received (left)
│  │            10:30 AM │             │
│  └─────────────────────┘             │
│                                      │
│        ┌─────────────────────────┐   │
│        │ I'm great! Just finished│   │  ← Sent (right, green)
│        │ the Android project     │   │
│        │         10:31 AM  ✓✓   │   │  ← Blue ticks = read
│        └─────────────────────────┘   │
│                                      │
│  ┌─────────────────────────┐         │
│  │ That's awesome! Can you │         │  ← Received
│  │ share the code?         │         │
│  │                10:32 AM │         │
│  └─────────────────────────┘         │
│                                      │
│        ┌──────────────────────┐      │
│        │ Sure! I'll push it to│      │  ← Sent
│        │ GitHub tonight       │      │
│        │       10:33 AM  ✓✓  │      │
│        └──────────────────────┘      │
│                                      │
├──────────────────────────────────────┤
│ 😊 ┌─ Message ──────────┐ 📎  🟢➤  │  ← Input Bar (Row with weight)
│    └─────────────────────┘          │
└──────────────────────────────────────┘
```

---

### 🌳 Layout Breakdown

```text
LAYOUT BREAKDOWN:
  Outer: Column (fills screen)
  ├── Row (top bar, fixed height)
  │   ├── Icon (back)
  │   ├── Image (avatar, CircleShape)
  │   ├── Column.weight(1f) (name + status)
  │   ├── Icon (call)
  │   └── Icon (more)
  ├── Column.weight(1f) (messages, scrollable)
  │   └── ChatBubble × N
  │       └── Row (pushed left or right)
  │           └── Box (bubble background)
  │               └── Column
  │                   ├── Text (message)
  │                   └── Row (time + ticks)
  └── Row (input bar)
      ├── Row.weight(1f) (text field area)
      │   ├── Icon (emoji)
      │   ├── TextField.weight(1f)
      │   └── Icon (attach)
      └── Box (send button, fixed 48dp)
```

---

---

## 📋 Complete Summary

```text
┌──────────────────────────────────────────────────────────────┐
│           LAYOUTS AND MODIFIERS SUMMARY                      │
├───────────────────────┬──────────────────────────────────────┤
│ LAYOUT                │ KEY POINTS                           │
├───────────────────────┼──────────────────────────────────────┤
│ Column                │ Vertical stack. verticalArrangement  │
│                       │ + horizontalAlignment                │
├───────────────────────┼──────────────────────────────────────┤
│ Row                   │ Horizontal line. horizontalArrange-  │
│                       │ ment + verticalAlignment             │
├───────────────────────┼──────────────────────────────────────┤
│ Box                   │ Layered stack. contentAlignment +    │
│                       │ Modifier.align() per child           │
├───────────────────────┼──────────────────────────────────────┤
│ ConstraintLayout      │ Position relative to other elements  │
│                       │ Use for complex layouts only         │
├───────────────────────┼──────────────────────────────────────┤
│ MODIFIER              │ KEY POINTS                           │
├───────────────────────┼──────────────────────────────────────┤
│ Order                 │ First = outermost. Order MATTERS.    │
│                       │ background→padding ≠ padding→bg      │
├───────────────────────┼──────────────────────────────────────┤
│ Sizing                │ size, fillMaxWidth, fillMaxSize,     │
│                       │ wrapContentSize, requiredSize        │
├───────────────────────┼──────────────────────────────────────┤
│ Padding/Margin        │ No margin in Compose!                │
│                       │ padding BEFORE bg = margin           │
│                       │ padding AFTER bg = padding           │
│                       │ spacedBy() for gaps between siblings │
├───────────────────────┼──────────────────────────────────────┤
│ clip/shape            │ clip(CircleShape), RoundedCorner-    │
│                       │ Shape, CutCornerShape                │
├───────────────────────┼──────────────────────────────────────┤
│ background/border     │ Solid, gradient, with shape          │
├───────────────────────┼──────────────────────────────────────┤
│ clickable             │ Single tap. combinedClickable for    │
│                       │ double-tap and long-press            │
├───────────────────────┼──────────────────────────────────────┤
│ weight()              │ Distributes remaining space in       │
│                       │ Row/Column. Proportional.            │
└───────────────────────┴──────────────────────────────────────┘
```

---

---

## 📝 Quiz — Test Your Understanding

> Answer these in your head or write them out before checking!

---

### ❓ Question 1: Layout Arrangement and Alignment

```text
a) Draw a text diagram showing how 3 boxes (Red 40dp, Blue 40dp, 
   Green 40dp) would be positioned in a Column with height 200dp 
   for EACH of these arrangements:
   - Arrangement.Top
   - Arrangement.Center
   - Arrangement.SpaceBetween
   - Arrangement.spacedBy(20.dp)

b) What is the difference between Arrangement.SpaceBetween, 
   Arrangement.SpaceAround, and Arrangement.SpaceEvenly?
   Draw a text diagram for each with 3 items in a Row.

c) You want to build this layout:
   ┌──────────────────────────────────┐
   │  Title                    Close  │  ← Title on left, X on right
   │  Subtitle                        │  ← Subtitle below title
   └──────────────────────────────────┘
   
   Which layout containers do you need? How do you nest them?
   Write the Compose code.

d) In a Row with verticalAlignment = Alignment.CenterVertically,
   what happens if one child is 20dp tall and another is 80dp tall?
   Draw a diagram showing the result.
```

---

### ❓ Question 2: Modifier Order

```text
a) For each pair, explain the VISUAL difference:

   Pair 1:
   Modifier.size(100.dp).background(Color.Red).padding(16.dp)
   vs
   Modifier.size(100.dp).padding(16.dp).background(Color.Red)

   Pair 2:
   Modifier.clickable { }.padding(16.dp).background(Color.Blue)
   vs
   Modifier.padding(16.dp).background(Color.Blue).clickable { }

   Pair 3:
   Modifier.clip(CircleShape).border(2.dp, Color.Red).background(Color.Blue)
   vs
   Modifier.border(2.dp, Color.Red).clip(CircleShape).background(Color.Blue)

b) A developer writes this code for a button:

   Text(
       "Submit",
       modifier = Modifier
           .padding(16.dp)
           .background(Color.Green, RoundedCornerShape(8.dp))
           .clickable { submitForm() }
   )

   The button looks wrong — there is no space between the text 
   and the edges of the green background. Why?
   How do you fix it? Write the corrected modifier chain.

c) Write a modifier chain that creates:
   - A card with 16dp margin from screen edges
   - White background with 12dp rounded corners
   - 2dp gray border
   - 16dp internal padding
   - Clickable with ripple effect
   Explain what each modifier does and why it is in that position.
```

---

### ❓ Question 3: `weight()` Deep Dive

```text
a) In this Row, calculate the EXACT width of each child
   if the parent is 360dp wide:

   Row(modifier = Modifier.fillMaxWidth()) {
       Box(Modifier.width(60.dp).height(40.dp))          // Child A
       Box(Modifier.weight(1f).height(40.dp))            // Child B
       Box(Modifier.weight(2f).height(40.dp))            // Child C
   }

   Show your math step by step.

b) What happens if you use weight() OUTSIDE of a Row or Column?
   For example: Box(modifier = Modifier.weight(1f))
   Will it compile? Will it crash? Explain.

c) Build this exact layout using weight():
   ┌──────┬──────────────────────┬──────┐
   │ Back │   Search products... │  🔍  │
   │  ←   │                      │      │
   └──────┴──────────────────────┴──────┘
   
   - Back button: fixed 48dp width
   - Search field: takes all remaining space
   - Search icon: fixed 48dp width
   Write the complete Compose code.

d) Explain the difference between:
   Modifier.weight(1f, fill = true)   (default)
   Modifier.weight(1f, fill = false)
   
   Give a scenario where fill = false is useful.
```

---

### ❓ Question 4: Padding vs Margin and Spacing

```text
a) In XML, you had android:layout_margin and android:padding.
   In Compose, there is only Modifier.padding().
   Explain HOW you achieve "margin" behavior in Compose.
   Give 3 different methods with code examples.

b) What is the difference between these two approaches for 
   adding space between items in a Column?

   Approach 1:
   Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
       Text("A")
       Text("B")
       Text("C")
   }

   Approach 2:
   Column {
       Text("A")
       Spacer(modifier = Modifier.height(8.dp))
       Text("B")
       Spacer(modifier = Modifier.height(8.dp))
       Text("C")
   }

   Which is better and why? Are there cases where Spacer is preferred?

c) A developer wants a button with:
   - 24dp space around the button (from other elements)
   - Green background
   - 16dp space between the text and the button edges
   
   They write:
   Modifier.padding(24.dp).padding(16.dp).background(Color.Green)
   
   Is this correct? What will it look like?
   Write the CORRECT modifier chain.
```

---

### ❓ Question 5: Build a Complex Layout

```text
Build a Product Detail Screen header using Compose.

REQUIREMENTS:

The layout should look like this:

┌──────────────────────────────────────┐
│  ←                    ♡     🛒(3)  │  ← Top bar
├──────────────────────────────────────┤
│                                      │
│         [Product Image]              │  ← Image area (250dp height)
│         [with gradient               │
│          overlay at bottom]          │
│                                      │
├──────────────────────────────────────┤
│  Nike Air Max 270        ₹12,999    │  ← Name (left) + Price (right)
│  ⭐ 4.5 (2,340 reviews)             │  ← Rating row
│                                      │
│  ┌──────┐ ┌──────┐ ┌──────┐        │  ← Size chips (Row)
│  │  7   │ │  8   │ │  9   │        │
│  └──────┘ └──────┘ └──────┘        │
│                                      │
│  ┌──────────────────────────────┐    │
│  │        Add to Cart 🛒        │    │  ← Full-width button
│  └──────────────────────────────┘    │
└──────────────────────────────────────┘

TECHNICAL REQUIREMENTS:
  a) Use Column as the main container
  b) Use Box for the image area with gradient overlay
  c) Use Row for the top bar with weight() for the title space
  d) Use Row with Arrangement.SpaceBetween for name + price
  e) Use Row with spacedBy for the size chips
  f) Use at least 10 different modifiers
  g) Use clip for the product image (rounded top corners)
  h) Use combinedClickable on the heart icon for tap + long press
  i) Include a @Preview function
  j) Add a state variable for selected shoe size

BONUS:
  Make the "Add to Cart" button change to "Added ✓" 
  when tapped, using a remember { mutableStateOf() } variable.
  Explain why this works automatically in Compose without 
  any findViewById or manual view updates.
```