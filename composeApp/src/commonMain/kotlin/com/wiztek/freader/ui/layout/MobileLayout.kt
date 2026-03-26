package com.wiztek.freader.ui.layout

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import kotlinx.coroutines.launch
import com.wiztek.freader.navigation.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileLayout(navigator: Navigator) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val lastItem = navigator.lastItem
    val isMainScreen = lastItem is VoyagerScreen.Home || 
                       lastItem is VoyagerScreen.Library || 
                       lastItem is VoyagerScreen.Discover || 
                       lastItem is VoyagerScreen.Collections

    val currentScreen = when (lastItem) {
        is VoyagerScreen.Home -> AppScreen.Home
        is VoyagerScreen.Library -> AppScreen.Library
        is VoyagerScreen.Discover -> AppScreen.Discover
        is VoyagerScreen.Collections -> AppScreen.Collections
        is VoyagerScreen.Stats -> AppScreen.Stats
        is VoyagerScreen.Settings -> AppScreen.Settings
        is VoyagerScreen.About -> AppScreen.About
        else -> AppScreen.Home
    }

    if (isMainScreen) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "FReader",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    val drawerScreens = listOf(AppScreen.Stats, AppScreen.Settings, AppScreen.About)
                    drawerScreens.forEach { screen ->
                        NavigationDrawerItem(
                            label = { Text(screen.title) },
                            selected = currentScreen == screen,
                            onClick = {
                                val target = when(screen) {
                                    AppScreen.Stats -> VoyagerScreen.Stats
                                    AppScreen.Settings -> VoyagerScreen.Settings
                                    AppScreen.About -> VoyagerScreen.About
                                    else -> VoyagerScreen.Settings
                                }
                                navigator.push(target)
                                scope.launch { drawerState.close() }
                            },
                            icon = { Icon(screen.icon, null) },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            }
        ) {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text(currentScreen.title) },
                        navigationIcon = {
                            TooltipBox(
                                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Below),
                                tooltip = { PlainTooltip { Text("Open menu") } },
                                state = rememberTooltipState()
                            ) {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Default.Menu, "Menu")
                                }
                            }
                        },
                        actions = {
                            TooltipBox(
                                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Below),
                                tooltip = { PlainTooltip { Text("Search library") } },
                                state = rememberTooltipState()
                            ) {
                                IconButton(onClick = { navigator.push(VoyagerScreen.Search) }) {
                                    Icon(Icons.Default.Search, "Search")
                                }
                            }
                            IconButton(onClick = { 
                                scope.launch { 
                                    snackbarHostState.showSnackbar("No new notifications")
                                }
                            }) {
                                BadgedBox(badge = { Badge { Text("3") } }) {
                                    Icon(Icons.Outlined.Notifications, "Notifications")
                                }
                            }
                        }
                    )
                },
                bottomBar = {
                    NavigationBar {
                        val navItems = listOf(AppScreen.Home, AppScreen.Library, AppScreen.Discover, AppScreen.Collections)
                        navItems.forEach { screen ->
                            NavigationBarItem(
                                selected = currentScreen == screen,
                                onClick = {
                                    val target = when(screen) {
                                        AppScreen.Home -> VoyagerScreen.Home
                                        AppScreen.Library -> VoyagerScreen.Library
                                        AppScreen.Discover -> VoyagerScreen.Discover
                                        AppScreen.Collections -> VoyagerScreen.Collections
                                        else -> VoyagerScreen.Home
                                    }
                                    navigator.replaceAll(target)
                                },
                                icon = { Icon(screen.icon, null) },
                                label = { Text(screen.title) }
                            )
                        }
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                    SlideTransition(navigator)
                }
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            SlideTransition(navigator)
        }
    }
}
