package com.aradsheybak.fancybottomsheet.navigation

import androidx.compose.runtime.saveable.listSaver
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

sealed interface AppNavKey : NavKey

data object HomeRoute : AppNavKey

data object SearchRoute : AppNavKey

data object CreateRoute : AppNavKey

data object InboxRoute : AppNavKey

data object SavedRoute : AppNavKey

val NavKey.routeId: String
    get() = when (this) {
        HomeRoute -> "home"
        SearchRoute -> "search"
        CreateRoute -> "create"
        InboxRoute -> "inbox"
        SavedRoute -> "saved"
        else -> "home"
    }

fun routeForId(id: String): AppNavKey = when (id) {
    "search" -> SearchRoute
    "create" -> CreateRoute
    "inbox" -> InboxRoute
    "saved" -> SavedRoute
    else -> HomeRoute
}

/**
 * Saves the [NavBackStack] as a list of route ids so the selected page survives
 * configuration changes and process death without requiring kotlinx.serialization.
 */
val AppNavBackStackSaver = listSaver<NavBackStack<NavKey>, String>(
    save = { backStack -> backStack.map { it.routeId } },
    restore = { ids ->
        NavBackStack<NavKey>().apply {
            ids.forEach { id -> add(routeForId(id)) }
        }
    },
)
