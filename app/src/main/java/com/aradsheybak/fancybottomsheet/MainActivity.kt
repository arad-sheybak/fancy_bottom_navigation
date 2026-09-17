package com.aradsheybak.fancybottomsheet

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.aradsheybak.fancybottomsheet.navigation.AppNavBackStackSaver
import com.aradsheybak.fancybottomsheet.navigation.CreateRoute
import com.aradsheybak.fancybottomsheet.navigation.HomeRoute
import com.aradsheybak.fancybottomsheet.navigation.InboxRoute
import com.aradsheybak.fancybottomsheet.navigation.SavedRoute
import com.aradsheybak.fancybottomsheet.navigation.SearchRoute
import com.aradsheybak.fancybottomsheet.navigation.routeForId
import com.aradsheybak.fancybottomsheet.navigation.routeId
import com.aradsheybak.fancybottomsheet.ui.components.FloatingBottomNavBar
import com.aradsheybak.fancybottomsheet.ui.components.NavItem
import com.aradsheybak.fancybottomsheet.ui.screens.CreateScreen
import com.aradsheybak.fancybottomsheet.ui.screens.HomeScreen
import com.aradsheybak.fancybottomsheet.ui.screens.InboxScreen
import com.aradsheybak.fancybottomsheet.ui.screens.SavedScreen
import com.aradsheybak.fancybottomsheet.ui.screens.SearchScreen
import com.aradsheybak.fancybottomsheet.ui.theme.FancyBottomSheetTheme

private fun materialIcon(name: String, pathData: String): ImageVector = ImageVector.Builder(
    name = name,
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
).addPath(
    pathData = addPathNodes(pathData),
    fill = SolidColor(Color.White),
).build()

private val HomeIcon = materialIcon("Home", "M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z")

private val SearchIcon = materialIcon(
    "Search",
    "M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 " +
        "5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 " +
        "14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"
)

private val CreateIcon = materialIcon("Create", "M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z")

private val InboxIcon = materialIcon(
    "Inbox",
    "M19 3H4.99c-1.11 0-1.98.89-1.98 2L3 19c0 1.1.88 2 1.99 2H19c1.1 0 2-.9 2-2V5c0-1.11-.9-2-2-2zm0 " +
        "12h-4c0 1.66-1.35 3-3 3s-3-1.34-3-3H4.99V5H19v10z"
)

private val SavedIcon = materialIcon(
    "Saved",
    "M17 3H7c-1.1 0-1.99.9-1.99 2L5 21l7-3 7 3V5c0-1.1-.9-2-2-2z"
)

private val DemoItems = listOf(
    NavItem(id = "home", icon = HomeIcon, label = "Home"),
    NavItem(id = "search", icon = SearchIcon, label = "Search"),
    NavItem(id = "create", icon = CreateIcon, label = "Create"),
    NavItem(id = "inbox", icon = InboxIcon, label = "Inbox", badgeCount = 3),
    NavItem(id = "saved", icon = SavedIcon, label = "Saved"),
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FancyBottomSheetTheme {
                FancyNavApp()
            }
        }
    }
}

@Composable
fun FancyNavApp() {
    val backStack = rememberSaveable(saver = AppNavBackStackSaver) {
        NavBackStack<NavKey>(HomeRoute)
    }
    val activity = LocalContext.current as? Activity

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0E0E11), Color(0xFF1B1B22))
                )
            ),
    ) {
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.fillMaxSize(),
            onBack = {
                if (backStack.size > 1) {
                    backStack.removeLast()
                } else {
                    activity?.finish()
                }
            },
            entryProvider = entryProvider<NavKey> {
                entry<HomeRoute> { HomeScreen() }
                entry<SearchRoute> { SearchScreen() }
                entry<CreateRoute> { CreateScreen() }
                entry<InboxRoute> { InboxScreen() }
                entry<SavedRoute> { SavedScreen() }
            },
        )

        FloatingBottomNavBar(
            items = DemoItems,
            selectedItemId = backStack.last().routeId,
            onItemSelect = { item ->
                val route = routeForId(item.id)
                if (backStack.last() != route) {
                    backStack.clear()
                    backStack.add(route)
                }
            },
            onLogoClick = {},
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0E0E11, widthDp = 412)
@Composable
private fun FancyNavAppPreview() {
    FancyBottomSheetTheme(dynamicColor = false) {
        FancyNavApp()
    }
}
