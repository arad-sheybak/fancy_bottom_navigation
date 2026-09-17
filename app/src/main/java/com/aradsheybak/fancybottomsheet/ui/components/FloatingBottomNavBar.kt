package com.aradsheybak.fancybottomsheet.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * A single destination shown inside [FloatingBottomNavBar].
 *
 * @param id stable identifier used to match [FloatingBottomNavBar.selectedItemId].
 * @param icon leading glyph drawn for the destination.
 * @param label caption drawn under the icon.
 * @param badgeCount optional red notification badge. `null` or `<= 0` hides it.
 */
data class NavItem(
    val id: String,
    val icon: ImageVector,
    val label: String,
    val badgeCount: Int? = null,
)

private val BarColor = Color(0xFF1A1A1E)
private val AccentColor = Color(0xFFE53946)
private val UnselectedIconColor = Color(0xFF9A9AA5)
private val UnselectedLabelColor = Color(0xFF8A8A94)
private val LogoBackground = Color(0xFF242429)
private val BarBorderColor = Color(0xFF2E2E36)

private val BarHeight = 74.dp
private val LogoSize = 44.dp
private val ItemIconSize = 22.dp
private val ItemHorizontalInset = 4.dp
private val NotchStrokeWidth = 1.8.dp
private val NotchTailEndInset = 12.dp

/** Ellipse half-width as a fraction of the selected item's cell width. */
private const val NotchWidthFactor = 0.62f

/** Vertical padding above/below the item content, as a fraction of its height. */
private const val NotchVerticalPaddingFactor = 0.22f

/** Bézier approximation constant for a quarter arc (4/3 * (sqrt(2) - 1)). */
private const val ArcConstant = 0.5523f

/**
 * Floating, dark, pill-shaped bottom navigation bar with a custom neon-red
 * indicator: one continuous, organic Bézier arch that wraps the selected
 * destination and trails off horizontally toward the next item.
 *
 * The bar itself owns no navigation state: [selectedItemId] is passed in and is
 * expected to be derived from the host navigation layer (e.g. Navigation 3).
 *
 * The indicator is drawn with a custom [Path] so it has soft, elliptical curves
 * with no corners, no flat top and no straight vertical edges. Its position and
 * size are measured from the selected item's real layout bounds and animated
 * when the selection changes.
 */
@Composable
fun FloatingBottomNavBar(
    items: List<NavItem>,
    selectedItemId: String,
    onItemSelect: (NavItem) -> Unit,
    modifier: Modifier = Modifier,
    logo: Painter? = null,
    onLogoClick: (() -> Unit)? = null,
) {
    val pill = RoundedCornerShape(percent = 50)
    val itemBounds = remember { mutableStateMapOf<String, Rect>() }
    val contentBounds = remember { mutableStateMapOf<String, Rect>() }
    // Center X of each item's ICON, in root coordinates.
    val iconCentersInRoot = remember { mutableStateMapOf<String, Float>() }
    var barLeftInRoot by remember { mutableStateOf<Float?>(null) }
    val density = LocalDensity.current
    val insetPx = with(density) { ItemHorizontalInset.toPx() }

    val selectedBounds = itemBounds[selectedItemId]
    val targetLeft = selectedBounds?.let { it.left + insetPx }
    val targetRight = selectedBounds?.let { it.right - insetPx }
    // Measured bounds of the selected item's content (icon + label), so the
    // outline is centered vertically on the item rather than on the bar.
    val selectedContent = contentBounds[selectedItemId]
    // The ellipse center is the ACTUAL icon center, converted from root into the
    // bar's local coordinate space. Never the item/label center.
    val targetCenterX = iconCentersInRoot[selectedItemId]?.let { iconCenter ->
        barLeftInRoot?.let { barLeft -> iconCenter - barLeft }
    }

    val leftAnim = remember { Animatable(0f) }
    val rightAnim = remember { Animatable(0f) }
    val centerAnim = remember { Animatable(0f) }
    var indicatorReady by remember { mutableStateOf(false) }

    LaunchedEffect(targetLeft, targetRight, targetCenterX) {
        val left = targetLeft ?: return@LaunchedEffect
        val right = targetRight ?: return@LaunchedEffect
        val center = targetCenterX ?: return@LaunchedEffect
        if (!indicatorReady) {
            leftAnim.snapTo(left)
            rightAnim.snapTo(right)
            centerAnim.snapTo(center)
            indicatorReady = true
        } else {
            val spec = spring<Float>(
                dampingRatio = 0.82f,
                stiffness = Spring.StiffnessMediumLow,
            )
            coroutineScope {
                launch { leftAnim.animateTo(left, spec) }
                launch { rightAnim.animateTo(right, spec) }
                launch { centerAnim.animateTo(center, spec) }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(BarHeight)
                .onGloballyPositioned { coordinates ->
                    barLeftInRoot = coordinates.boundsInRoot().left
                },
        ) {
            // Layer 1: dark pill background.
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .shadow(
                        elevation = 28.dp,
                        shape = pill,
                        clip = false,
                        ambientColor = AccentColor.copy(alpha = 0.30f),
                        spotColor = AccentColor.copy(alpha = 0.30f),
                    )
                    .background(color = BarColor, shape = pill)
                    .border(width = 1.dp, color = BarBorderColor, shape = pill),
            )

            // Layer 2: custom selected notch (behind the content, above the pill).
            Canvas(modifier = Modifier.matchParentSize()) {
                if (!indicatorReady) return@Canvas
                val bodyWidth = (rightAnim.value - leftAnim.value).coerceAtLeast(1f)
                val content = selectedContent ?: return@Canvas
                drawSelectedNotch(
                    centerX = centerAnim.value,
                    bodyWidth = bodyWidth,
                    contentTop = content.top,
                    contentBottom = content.bottom,
                    accent = AccentColor,
                    strokeWidth = NotchStrokeWidth.toPx(),
                )
            }

            // Layers 3 & 4: logo and navigation items.
            Row(
                modifier = Modifier
                    .matchParentSize()
                    .clip(pill)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                LogoButton(logo = logo, onLogoClick = onLogoClick)
                Spacer(modifier = Modifier.width(2.dp))
                items.forEach { item ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .onGloballyPositioned { coordinates ->
                                itemBounds[item.id] = coordinates.boundsInParent()
                            }
                            .selectable(
                                selected = item.id == selectedItemId,
                                role = Role.Tab,
                                onClick = { onItemSelect(item) },
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        NavBarItemContent(
                            item = item,
                            selected = item.id == selectedItemId,
                            accent = AccentColor,
                            onContentPositioned = { rect -> contentBounds[item.id] = rect },
                            onIconPositioned = { centerXInRoot ->
                                iconCentersInRoot[item.id] = centerXInRoot
                            },
                        )
                    }
                }
            }
        }
    }
}

/**
 * Draws the selected indicator as one continuous, organic red path.
 *
 * The crown is the upper half of an ellipse (two quarter-arc cubic Béziers
 * using [ArcConstant]), so it is broad and rounded with continuously changing
 * direction — no apex, no flat top, no straight vertical sides. Short concave
 * fillets blend the ellipse's vertical sides into the horizontal tails, and the
 * right tail continues along the bar to the next item on the same [Path].
 *
 * [centerX] is the selected item's measured ICON center, so the ellipse is
 * horizontally centered on the icon (never on the item/label). [bodyWidth] is
 * only used to derive the width.
 */
private fun DrawScope.drawSelectedNotch(
    centerX: Float,
    bodyWidth: Float,
    contentTop: Float,
    contentBottom: Float,
    accent: Color,
    strokeWidth: Float,
) {
    val halfWidth = bodyWidth.coerceAtLeast(1f) * NotchWidthFactor

    // Vertical geometry comes from the item's measured content bounds so the
    // arch is centered on the icon + label and emerges from the lower bar area.
    val contentHeight = (contentBottom - contentTop).coerceAtLeast(1f)
    val verticalPadding = contentHeight * NotchVerticalPaddingFactor
    val topY = contentTop - verticalPadding
    val baseY = contentBottom + verticalPadding

    // Concave fillet that blends the horizontal tail into the ellipse side.
    val fillet = minOf(contentHeight * 0.18f, halfWidth * 0.24f)
    val ellipseBaseY = baseY - fillet
    val verticalRadius = (ellipseBaseY - topY).coerceAtLeast(1f)

    val leftFilletX = centerX - halfWidth - fillet
    val leftTailStart = leftFilletX - bodyWidth * 0.5f
    val rightFilletX = centerX + halfWidth + fillet
    val rightTailEnd =
        (size.width - NotchTailEndInset.toPx()).coerceAtLeast(rightFilletX)

    val path = Path().apply {
        // Left tail.
        moveTo(leftTailStart, baseY)
        lineTo(leftFilletX, baseY)
        // Left concave fillet: horizontal tail -> vertical ellipse side.
        quadraticTo(centerX - halfWidth, baseY, centerX - halfWidth, ellipseBaseY)
        // Elliptical upper-left quarter: vertical -> horizontal at the crown.
        cubicTo(
            centerX - halfWidth, ellipseBaseY - verticalRadius * ArcConstant,
            centerX - halfWidth * ArcConstant, topY,
            centerX, topY,
        )
        // Elliptical upper-right quarter: horizontal -> vertical.
        cubicTo(
            centerX + halfWidth * ArcConstant, topY,
            centerX + halfWidth, ellipseBaseY - verticalRadius * ArcConstant,
            centerX + halfWidth, ellipseBaseY,
        )
        // Right concave fillet: vertical ellipse side -> horizontal tail.
        quadraticTo(centerX + halfWidth, baseY, rightFilletX, baseY)
        // Continuous trailing line toward the next item (same path).
        lineTo(rightTailEnd, baseY)
    }

    // Thin, slightly glowing red stroke.
    drawPath(
        path = path,
        color = accent.copy(alpha = 0.07f),
        style = Stroke(strokeWidth * 4.5f, cap = StrokeCap.Round, join = StrokeJoin.Round),
    )
    drawPath(
        path = path,
        color = accent.copy(alpha = 0.16f),
        style = Stroke(strokeWidth * 2.2f, cap = StrokeCap.Round, join = StrokeJoin.Round),
    )
    drawPath(
        path = path,
        color = accent,
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        ),
    )
}

@Composable
private fun LogoButton(
    logo: Painter?,
    onLogoClick: (() -> Unit)?,
) {
    Box(
        modifier = Modifier
            .size(LogoSize)
            .clip(CircleShape)
            .background(LogoBackground)
            .border(width = 1.dp, color = Color.White.copy(alpha = 0.10f), shape = CircleShape)
            .then(
                if (onLogoClick != null) {
                    Modifier.clickable(onClick = onLogoClick)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (logo != null) {
            Image(
                painter = logo,
                contentDescription = "Logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Icon(
                imageVector = PlaceholderLogo,
                contentDescription = "Logo",
                tint = Color.White.copy(alpha = 0.70f),
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun NavBarItemContent(
    item: NavItem,
    selected: Boolean,
    accent: Color,
    onContentPositioned: (Rect) -> Unit,
    onIconPositioned: (Float) -> Unit,
) {
    val iconColor by animateColorAsState(
        targetValue = if (selected) accent else UnselectedIconColor,
        label = "navIconColor",
    )
    val labelColor by animateColorAsState(
        targetValue = if (selected) Color.White else UnselectedLabelColor,
        label = "navLabelColor",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .onGloballyPositioned { onContentPositioned(it.boundsInParent()) }
            .padding(horizontal = 2.dp),
    ) {
        Box(
            modifier = Modifier.onGloballyPositioned {
                onIconPositioned(it.boundsInRoot().center.x)
            },
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = iconColor,
                modifier = Modifier.size(ItemIconSize),
            )
            val count = item.badgeCount
            if (count != null && count > 0) {
                NotificationBadge(
                    count = count,
                    accent = accent,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 7.dp, y = (-5).dp),
                )
            }
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = item.label,
            color = labelColor,
            fontSize = 10.sp,
            lineHeight = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun NotificationBadge(
    count: Int,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 15.dp, minHeight = 15.dp)
            .clip(CircleShape)
            .background(accent)
            .border(width = 1.5.dp, color = BarColor, shape = CircleShape)
            .padding(horizontal = 3.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (count > 9) "9+" else count.toString(),
            color = Color.White,
            fontSize = 8.sp,
            lineHeight = 8.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

// ---------------------------------------------------------------------------
// Preview-only helpers (self-contained: no Material icons dependency).
// ---------------------------------------------------------------------------

private fun previewIcon(name: String, pathData: String): ImageVector =
    ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).addPath(
        pathData = addPathNodes(pathData),
        fill = SolidColor(Color.White),
    ).build()

private val PreviewHome = previewIcon("Home", "M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z")

private val PreviewSearch = previewIcon(
    "Search",
    "M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 " +
        "5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 " +
        "14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z",
)

private val PreviewCreate = previewIcon("Create", "M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z")

private val PreviewInbox = previewIcon(
    "Inbox",
    "M19 3H4.99c-1.11 0-1.98.89-1.98 2L3 19c0 1.1.88 2 1.99 2H19c1.1 0 2-.9 2-2V5c0-1.11-.9-2-2-2zm0 " +
        "12h-4c0 1.66-1.35 3-3 3s-3-1.34-3-3H4.99V5H19v10z",
)

private val PreviewSaved = previewIcon(
    "Saved",
    "M17 3H7c-1.1 0-1.99.9-1.99 2L5 21l7-3 7 3V5c0-1.1-.9-2-2-2z",
)

private val PreviewItems = listOf(
    NavItem(id = "home", icon = PreviewHome, label = "Home"),
    NavItem(id = "search", icon = PreviewSearch, label = "Search"),
    NavItem(id = "create", icon = PreviewCreate, label = "Create"),
    NavItem(id = "inbox", icon = PreviewInbox, label = "Inbox", badgeCount = 3),
    NavItem(id = "saved", icon = PreviewSaved, label = "Saved"),
)

@Preview(showBackground = true, backgroundColor = 0xFF0D0D10, widthDp = 412, heightDp = 220)
@Composable
private fun FloatingBottomNavBarPreview() {
    var selected by remember { mutableStateOf("home") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D10)),
        contentAlignment = Alignment.BottomCenter,
    ) {
        FloatingBottomNavBar(
            items = PreviewItems,
            selectedItemId = selected,
            onItemSelect = { selected = it.id },
            onLogoClick = {},
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

private val PlaceholderLogo: ImageVector = ImageVector.Builder(
    name = "PlaceholderLogo",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
).addPath(
    pathData = addPathNodes(
        "M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4z" +
            "M12 14c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"
    ),
    fill = SolidColor(Color.White),
).build()
