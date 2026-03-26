package com.wiztek.freader.ui.layout

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.wiztek.freader.navigation.*

@Composable
fun DesktopLayout(navigator: Navigator) {
    var collapsed by remember { mutableStateOf(false) }

    // Helper to get current AppScreen enum from Voyager Screen
    val currentScreen = when (navigator.lastItem) {
        is VoyagerScreen.Home -> AppScreen.Home
        is VoyagerScreen.Library -> AppScreen.Library
        is VoyagerScreen.Discover -> AppScreen.Discover
        is VoyagerScreen.Collections -> AppScreen.Collections
        is VoyagerScreen.Stats -> AppScreen.Stats
        is VoyagerScreen.Settings -> AppScreen.Settings
        is VoyagerScreen.About -> AppScreen.About
        else -> AppScreen.Home
    }

    val sidebarWidth by animateDpAsState(
        if (collapsed) 72.dp else 240.dp,
        label = "SidebarWidth"
    )

    Row(modifier = Modifier.fillMaxSize()) {
        // Sidebar
        Surface(
            modifier = Modifier.width(sidebarWidth).fillMaxHeight(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                IconButton(
                    onClick = { collapsed = !collapsed },
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Icon(Icons.Default.Menu, "Toggle Sidebar")
                }

                Spacer(Modifier.height(24.dp))

                // Navigation Items
                AppScreen.entries.forEach { screen ->
                    if (screen == AppScreen.Settings) {
                        Spacer(Modifier.weight(1f))
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    }

                    SidebarItem(
                        screen = screen,
                        collapsed = collapsed,
                        selected = currentScreen == screen,
                        onClick = {
                            val target = when(screen) {
                                AppScreen.Home -> VoyagerScreen.Home
                                AppScreen.Library -> VoyagerScreen.Library
                                AppScreen.Discover -> VoyagerScreen.Discover
                                AppScreen.Collections -> VoyagerScreen.Collections
                                AppScreen.Stats -> VoyagerScreen.Stats
                                AppScreen.Settings -> VoyagerScreen.Settings
                                AppScreen.About -> VoyagerScreen.About
                            }
                            navigator.replaceAll(target)
                        }
                    )
                }
            }
        }

        // Main Content Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            SlideTransition(navigator)
        }
    }
}
