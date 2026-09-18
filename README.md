# Floating Bottom Navigation Bar for Jetpack Compose

<p align="center">
  <strong>A smooth, animated, fully customizable floating bottom navigation bar built from scratch with Jetpack Compose.</strong>
</p>

<p align="center">
  No external UI libraries. No prebuilt navigation component. Just Compose, custom Path geometry, and a lot of attention to detail.
</p>

<p align="center">

![Kotlin](https://img.shields.io/badge/Kotlin-2.x-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Material 3](https://img.shields.io/badge/Material%203-Design-6750A4?style=for-the-badge&logo=materialdesign&logoColor=white)
![Android](https://img.shields.io/badge/Android-Platform-3DDC84?style=for-the-badge&logo=android&logoColor=white)

</p>

---

## ✨ Preview

<p align="center">
  <img src="screenshots/preview.gif" alt="Floating Bottom Navigation Bar Preview" width="360"/>
</p>

> A floating navigation bar with a custom animated indicator that dynamically adapts to the selected item.

---

## 🎯 Why This Project?

Most bottom navigation implementations rely on standard Material components and predefined indicators.

This project takes a different approach.

The selected indicator is built using a **custom Compose `Path`**, allowing precise control over:

- Curves
- Ellipse geometry
- Stroke rendering
- Glow
- Path length
- Animation progress
- Left/right line distribution
- Dynamic colors
- Icon alignment

The result is a navigation bar that feels more like a custom-designed UI than a standard navigation component.

---

# 🚀 Features

### 🎨 Custom Indicator

The selected item is surrounded by a custom smooth elliptical indicator.

It is **not** a rounded rectangle, pill, or standard Material indicator.

The geometry is generated manually using Compose drawing APIs.

---

### ✏️ Path Reveal Animation

When the selected item changes, the indicator is revealed progressively from left to right.

The animation is based on the **actual Path length**, rather than simply fading, scaling, or translating the indicator.

```text
Path
───────────────────────────────→

Animation
▏
▏━━━━
▏━━━━━━━━━━
▏━━━━━━━━━━━━━━━━
▏━━━━━━━━━━━━━━━━━━━━━━
The total horizontal line length remains constant while its distribution changes.

🎨 Per-Item Colors

Every navigation item can define its own accent color.

The selected item's color is used as the single source of truth for:

Indicator stroke
Icon tint
Indicator glow

For example:

NavItem(
    id = "home",
    icon = Icons.Default.Home,
    label = "Home",
    color = Color(...)
)

Selecting another item automatically updates the indicator and icon color.

📱 Edge-Aware Indicator

The indicator adapts when it reaches the edges of the navigation bar.

The first and last items don't cause the indicator to visually overflow outside the bar.

The Path is trimmed appropriately at the edges while preserving the ellipse and icon alignment.

🧩 Navigation 3 Ready

Navigation state remains outside the UI component.

The bottom bar does not own navigation state.

Instead, it receives the current selection and emits user interaction:

FloatingBottomNavBar(
    items = items,
    selectedItemId = selectedItemId,
    onItemSelect = { item ->
        // Navigation 3 handles navigation
    }
)

This keeps the component reusable and independent from the navigation implementation.

🏗️ Architecture

The component follows a simple separation of responsibilities:

┌──────────────────────────────┐
│        Navigation 3          │
│                              │
│   Owns navigation state      │
└──────────────┬───────────────┘
               │
               │ selectedItemId
               ▼
┌──────────────────────────────┐
│   FloatingBottomNavBar       │
│                              │
│   UI + interaction only      │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│      Custom Path Engine      │
│                              │
│ • Ellipse                    │
│ • Line distribution          │
│ • Edge trimming              │
│ • Path measurement           │
│ • Reveal animation           │
└──────────────────────────────┘

The component itself does not perform navigation.

🛠️ Tech Stack
Kotlin
Jetpack Compose
Material 3
AndroidX Navigation 3
Canvas
Path
PathMeasure
Compose animation APIs
Dependencies

No third-party UI libraries are required.

The component is built entirely with Android / Jetpack Compose APIs.

📦 Usage

Define your navigation items:

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

Then place the component in your UI:

FloatingBottomNavBar(
    items = items,
    selectedItemId = selectedItemId,
    onItemSelect = { item ->
        // Update Navigation 3 back stack
    }
)
🎛️ Component API
@Composable
fun FloatingBottomNavBar(
    items: List<NavItem>,
    selectedItemId: String,
    onItemSelect: (NavItem) -> Unit,
    modifier: Modifier = Modifier,
)

Navigation item:

data class NavItem(
    val id: String,
    val icon: ImageVector,
    val label: String,
    val color: Color,
    val badgeCount: Int? = null,
)
🧠 Interesting Implementation Details

This project is primarily an exploration of custom UI rendering in Jetpack Compose.

Instead of treating the indicator as a conventional composable, the indicator is treated as geometry.

That makes it possible to reason about the UI in terms of:

Icon Center
     │
     ▼
Ellipse Geometry
     │
     ▼
Horizontal Segments
     │
     ▼
Complete Path
     │
     ▼
PathMeasure
     │
     ▼
Animated Path Reveal

This approach provides significantly more control than combining standard Row, Surface, and NavigationBarItem indicators.

🎬 Animation

The indicator animation is driven by the geometry of the actual Path.

Conceptually:

progress: 0f → 1f

The current animation progress determines how much of the Path is visible.

This means the animation follows the actual curve:

0%

╭


25%

╭────


50%

╭──────────╮


75%

╭──────────╮────────


100%

╭──────────╮────────────────

The animation remains consistent regardless of which navigation item is selected.

🔍 Design Principles

This project intentionally follows a few principles:

UI component ≠ navigation controller

The bottom bar renders UI and reports user interaction.

Navigation state belongs to the navigation layer.

Geometry > hacks

The indicator is positioned using actual measured geometry rather than arbitrary offsets wherever possible.

One source of truth

The selected item's color drives both the indicator and selected icon tint.

Animation follows geometry

The animation reveals the actual Path instead of animating a separate visual approximation.

📂 Project Structure
app/
└── src/
    └── main/
        └── java/
            └── ...
                └── FloatingBottomNavBar/
                    ├── FloatingBottomNavBar.kt
                    ├── NavItem.kt
                    └── ...
🧪 What This Project Explores

This repository is also a practical experiment with advanced Compose drawing techniques:

Custom Path construction
Bézier curves
Elliptical geometry
PathMeasure
Partial path rendering
Animated drawing
Dynamic layout measurement
Icon-bound measurement
Canvas-based UI
Compose state-driven rendering
Navigation 3 integration
🗺️ Roadmap

Possible future improvements:

 More indicator shapes
 Configurable animation duration
 Configurable indicator dimensions
 Custom glow intensity
 More badge styles
 RTL layout support
 Additional animation modes
 Preview configurations
 UI tests
🤝 Contributing

Contributions, ideas, and experiments are welcome.

If you find an interesting way to improve the geometry, animation, performance, or API design, feel free to open an issue or pull request.

⭐ Support

If this project helped you learn something about custom drawing in Jetpack Compose, consider giving it a ⭐ on GitHub.

It helps the project get discovered by other Android developers.

📄 License

This project is licensed under the MIT License.

See LICENSE for details.
