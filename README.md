# Floating Bottom Navigation Bar for Jetpack Compose

<p align="center">
  <strong>A custom, animated floating bottom navigation bar built from scratch with Jetpack Compose.</strong>
</p>

<p align="center">
  Smooth path-based animations, dynamic colors, custom geometry, and Navigation 3 integration — without external UI libraries.
</p>

<p align="center">
  <img src="screenshots/preview.gif" alt="Floating Bottom Navigation Bar Preview" width="420"/>
</p>

<p align="center">

![Kotlin](https://img.shields.io/badge/Kotlin-2.x-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Material 3](https://img.shields.io/badge/Material%203-Design-6750A4?style=for-the-badge&logo=materialdesign&logoColor=white)
![Navigation 3](https://img.shields.io/badge/Navigation%203-AndroidX-3DDC84?style=for-the-badge&logo=android&logoColor=white)

</p>

---

## ✨ Overview

`FloatingBottomNavBar` is a custom bottom navigation component for Android built entirely with Kotlin and Jetpack Compose.

Instead of relying on the default Material Navigation Bar indicator, this project uses custom `Path` geometry and path-based animation to create a smooth floating navigation experience.

The selected item is surrounded by a custom elliptical indicator that dynamically adapts its horizontal line distribution depending on the selected position.

The component is designed to be:

- 🎨 Highly customizable
- 🧩 Reusable
- ⚡ Lightweight
- 🧠 Geometry-driven
- 🎬 Animation-focused
- 🧭 Navigation 3 compatible
- 🚫 Free from third-party UI libraries

---

# 🎬 Preview

The demo shows the navigation indicator moving between all navigation items:

**Home → Search → Create → Inbox → Saved**

The animation demonstrates:

- Custom elliptical indicator
- Smooth 500ms Path Reveal animation
- Dynamic indicator colors
- Selected icon tint
- Left/right line redistribution
- Edge-aware trimming
- Exact icon/indicator alignment

<p align="center">
  <img src="screenshots/preview.gif" alt="Floating Bottom Navigation Bar Animation" width="420"/>
</p>

---

# 🎯 What Makes It Different?

Most bottom navigation implementations use standard Material components such as:

```kotlin
NavigationBar
NavigationBarItem
```

with a predefined indicator.

This project takes a different approach.

The indicator is treated as **custom geometry** rather than a conventional composable.

```text
Selected Item
      │
      ▼
Icon Center
      │
      ▼
Ellipse Geometry
      │
      ▼
Horizontal Line Distribution
      │
      ▼
Complete Path
      │
      ▼
PathMeasure
      │
      ▼
Animated Path Reveal
```

This gives precise control over the visual result.

---

# 🚀 Features

## 🎨 Custom Elliptical Indicator

The selected item uses a custom smooth elliptical/arched indicator.

It is not:

- A rounded rectangle
- A Material pill
- A default NavigationBar indicator

The shape is generated manually using Compose drawing APIs.

```text
          ╭────────╮
        ╭─          ─╮
       │     ICON     │
        ╰─          ─╯
          ╰──────────────→
```

---

## 🎬 Path-Based Reveal Animation

When the selected item changes, the indicator is revealed progressively from left to right over approximately **500ms**.

The animation is based on the actual length of the Path rather than simply:

- Fading
- Scaling
- Translating
- Animating the entire component

Conceptually:

```text
0%

╭


25%

╭────


50%

╭──────────╮


75%

╭──────────╮────────


100%

╭──────────╮────────────────────
```

The animation follows the actual curve of the indicator.

---

## 🎯 Exact Icon Alignment

The indicator is positioned relative to the **actual icon center**.

This prevents visual misalignment caused by centering the indicator against the entire navigation item instead of the icon itself.

```text
        ╭────────╮
        │  ICON  │
        ╰────────╯
             ↑
        exact center
```

The ellipse remains centered around the selected icon even when the horizontal line distribution changes.

---

## ↔️ Dynamic Line Distribution

One of the main visual features is the dynamic redistribution of the horizontal line.

As the selected item moves from left to right:

- The right-side line becomes shorter.
- The left-side line becomes longer.
- The amount removed from the right is added to the left.
- The total horizontal line length remains constant.

### First Item

```text
        ╭────────╮────────────────────→
        │  ICON  │
        ╰────────╯
```

Minimal line on the left.

Maximum line on the right.

### Middle Item

```text
      ───────╭────────╮───────
             │  ICON  │
             ╰────────╯
```

The line is distributed approximately equally on both sides.

### Last Item

```text
←────────────────────╭────────╮
                     │  ICON  │
                     ╰────────╯
```

Maximum line on the left.

Minimal or no line on the right.

### The underlying idea

The distribution can be represented as:

```kotlin
progress = selectedItemIndex / lastItemIndex
```

Then:

```kotlin
leftLineLength  = lerp(minLineLength, maxLineLength, progress)
rightLineLength = lerp(maxLineLength, minLineLength, progress)
```

Therefore:

```text
leftLineLength + rightLineLength = constant
```

The visual weight moves across the navigation bar without changing the overall indicator size.

---

# 🎨 Per-Item Colors

Each navigation item can define its own accent color.

The selected item's color becomes the single source of truth for:

- Indicator stroke
- Icon tint
- Indicator glow

For example:

```kotlin
NavItem(
    id = "home",
    icon = Icons.Default.Home,
    label = "Home",
    color = Color(...)
)
```

When another item is selected, the indicator and icon automatically adopt that item's color.

```text
Home selected

Indicator → Home color
Icon      → Home color


Search selected

Indicator → Search color
Icon      → Search color


Create selected

Indicator → Create color
Icon      → Create color
```

This avoids maintaining separate color definitions for the indicator and selected icon.

---

# 📱 Edge-Aware Indicator

The indicator is aware of the navigation bar boundaries.

When the first item is selected, the beginning of the Path is trimmed so that it does not unnecessarily extend beyond the left edge.

When the last item is selected, the end of the Path is trimmed on the right side.

```text
FIRST ITEM

      ╭────────╮────────────────→
      │  ICON  │


LAST ITEM

←────────────────╭────────╮
                  │  ICON  │
```

The ellipse itself is not moved or resized to achieve this.

The Path geometry is trimmed instead.

---

# 🧩 Navigation 3 Integration

Navigation state is intentionally kept outside the component.

`FloatingBottomNavBar` does not own the navigation back stack.

Instead, it receives the currently selected item and reports user interaction.

```kotlin
FloatingBottomNavBar(
    items = items,
    selectedItemId = selectedItemId,
    onItemSelect = { item ->
        // Navigation 3 handles navigation
    }
)
```

This keeps the component independent from the application's navigation implementation.

---

# 🏗️ Architecture

The component follows a simple separation of responsibilities:

```text
┌───────────────────────────────┐
│          Navigation 3         │
│                               │
│       Owns navigation state   │
└───────────────┬───────────────┘
                │
                │ selectedItemId
                ▼
┌───────────────────────────────┐
│     FloatingBottomNavBar      │
│                               │
│       UI + interaction        │
└───────────────┬───────────────┘
                │
                ▼
┌───────────────────────────────┐
│       Custom Path Engine      │
│                               │
│ • Icon alignment              │
│ • Ellipse geometry            │
│ • Line distribution            │
│ • Edge trimming               │
│ • Path measurement            │
│ • Path reveal animation       │
└───────────────────────────────┘
```

The component does not perform navigation itself.

---

# 🛠️ Tech Stack

- **Kotlin**
- **Jetpack Compose**
- **Material 3**
- **AndroidX Navigation 3**
- `Canvas`
- `Path`
- `PathMeasure`
- Compose Animation APIs

## Dependencies

No third-party UI libraries are required.

The component is built using Android and Jetpack Compose APIs.

---

# 📦 Usage

## Define Navigation Items

```kotlin
val items = listOf(
    NavItem(
        id = "home",
        icon = Icons.Default.Home,
        label = "Home",
        color = Color(...)
    ),
    NavItem(
        id = "search",
        icon = Icons.Default.Search,
        label = "Search",
        color = Color(...)
    ),
    NavItem(
        id = "create",
        icon = Icons.Default.Add,
        label = "Create",
        color = Color(...)
    ),
    NavItem(
        id = "inbox",
        icon = Icons.Default.Email,
        label = "Inbox",
        color = Color(...)
    ),
    NavItem(
        id = "saved",
        icon = Icons.Default.Bookmark,
        label = "Saved",
        color = Color(...)
    )
)
```

## Add the Component

```kotlin
FloatingBottomNavBar(
    items = items,
    selectedItemId = selectedItemId,
    onItemSelect = { item ->
        // Update Navigation 3 state
    }
)
```

---

# 🧱 Component API

```kotlin
@Composable
fun FloatingBottomNavBar(
    items: List<NavItem>,
    selectedItemId: String,
    onItemSelect: (NavItem) -> Unit,
    modifier: Modifier = Modifier,
)
```

Navigation item:

```kotlin
data class NavItem(
    val id: String,
    val icon: ImageVector,
    val label: String,
    val color: Color,
    val badgeCount: Int? = null,
)
```

---

# 🧠 Implementation Details

The most interesting part of this project is the indicator rendering system.

Instead of composing multiple UI elements to approximate the design, the indicator is represented as a single geometric Path.

The process can be summarized as:

```text
1. Find selected item
        ↓
2. Measure actual icon bounds
        ↓
3. Calculate icon center
        ↓
4. Build ellipse around icon center
        ↓
5. Calculate left/right line lengths
        ↓
6. Apply edge trimming
        ↓
7. Build complete Path
        ↓
8. Measure Path length
        ↓
9. Animate Path reveal
        ↓
10. Render stroke + glow
```

---

# 📐 Geometry

The indicator consists conceptually of three parts:

```text
LEFT LINE
    │
    ▼
────────╭────────╮────────
        │  ICON  │
        ╰────────╯
                  ▲
                  │
             RIGHT LINE
```

The ellipse remains centered around the selected icon.

Only the horizontal line distribution changes according to the selected item's position.

This allows the indicator to move naturally across the navigation bar without changing the core ellipse geometry.

---

# 🎬 Animation Internals

The animation is driven by Path length.

Conceptually:

```kotlin
val progress = animateFloatAsState(
    targetValue = 1f,
    animationSpec = tween(500)
)
```

The current progress determines how much of the measured Path is rendered.

```text
Path length = 100%

Progress:

0%    → nothing
25%   → first quarter
50%   → half
75%   → three quarters
100%  → complete Path
```

This makes the animation follow the actual geometry of the indicator.

---

# 🎨 Rendering

The indicator can be rendered with:

- Stroke
- Custom stroke width
- Glow
- Dynamic selected-item color

The same selected-item color is reused for the selected icon tint.

This keeps the visual system consistent.

---

# 🔬 What This Project Explores

This repository is also a practical exploration of advanced Jetpack Compose drawing techniques.

Topics include:

- Custom `Canvas` drawing
- `Path` construction
- Bézier curves
- Elliptical geometry
- Path measurement
- Partial Path rendering
- Path-based animations
- Dynamic layout measurement
- Icon bounds measurement
- State-driven rendering
- Custom indicators
- Navigation 3 integration
- Edge-aware geometry
- Dynamic color systems

---

# 📂 Project Structure

```text
FloatingBottomNavigation/
│
├── app/
│   └── src/
│       └── main/
│           └── java/
│               └── ...
│                   └── FloatingBottomNavBar/
│                       ├── FloatingBottomNavBar.kt
│                       ├── NavItem.kt
│                       └── ...
│
├── screenshots/
│   ├── preview.gif
│   ├── first-item.png
│   ├── middle-item.png
│   └── last-item.png
│
├── README.md
├── LICENSE
└── .gitignore
```

---

# 📸 Screenshots

### First Item

<p align="center">
  <img src="screenshots/first-item.png" alt="First Navigation Item" width="420"/>
</p>

### Middle Item

<p align="center">
  <img src="screenshots/middle-item.png" alt="Middle Navigation Item" width="420"/>
</p>

### Last Item

<p align="center">
  <img src="screenshots/last-item.png" alt="Last Navigation Item" width="420"/>
</p>

---

# ⚡ Performance Considerations

The indicator is rendered using Compose drawing primitives rather than a collection of heavyweight UI components.

The implementation focuses on:

- Reusing calculated geometry where appropriate
- Measuring only the required layout information
- Animating Path progress rather than rebuilding the entire UI
- Keeping navigation state outside the component

The component is intended to remain lightweight while providing a highly customized visual result.

---

# 🧪 Example Navigation Items

The demo contains five navigation destinations:

| Item | Purpose |
|------|---------|
| 🏠 Home | Main screen |
| 🔍 Search | Search |
| ＋ Create | Create new content |
| ✉ Inbox | Messages |
| 🔖 Saved | Saved content |

Each item can have its own accent color.

---

# 🎛️ Customization

The component can be extended to support additional customization such as:

- Indicator dimensions
- Stroke width
- Glow intensity
- Animation duration
- Navigation item count
- Badge appearance
- Color palettes
- Indicator geometry

The current implementation intentionally keeps the API small and focused.

---

# 🗺️ Roadmap

- [ ] Configurable indicator dimensions
- [ ] Configurable animation duration
- [ ] Configurable glow intensity
- [ ] Additional indicator shapes
- [ ] More badge styles
- [ ] RTL layout support
- [ ] Additional animation modes
- [ ] Compose Preview examples
- [ ] UI tests
- [ ] Performance benchmarks
- [ ] More customization options

---

# 🤝 Contributing

Contributions and ideas are welcome.

If you find an interesting way to improve:

- Indicator geometry
- Path animation
- Rendering performance
- API design
- Accessibility
- RTL support

feel free to open an issue or submit a pull request.

---

# ⭐ Support the Project

If you find this project useful or learn something from the implementation, consider giving it a ⭐ on GitHub.

It helps other Android developers discover the project.

---

# 📄 License

This project is licensed under the MIT License.

See the [LICENSE](LICENSE) file for details.

---

<p align="center">

### Built with Kotlin + Jetpack Compose

Custom geometry.  
Custom animation.  
No shortcuts.

</p>
