package com.wiztek.freader.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.wiztek.freader.di.LocalAppModule
import com.wiztek.freader.ui.screens.*
import com.wiztek.freader.ui.screens.about.AboutScreen
import com.wiztek.freader.ui.screens.collections.CollectionsScreen
import com.wiztek.freader.ui.screens.discover.DiscoverScreen
import com.wiztek.freader.ui.screens.home.HomeScreen
import com.wiztek.freader.ui.screens.home.HomeViewModel
import com.wiztek.freader.ui.screens.library.LibraryScreen
import com.wiztek.freader.ui.screens.library.LibraryViewModel
import com.wiztek.freader.ui.screens.settings.SettingsScreen
import com.wiztek.freader.ui.screens.stats.StatsScreen

@Composable
fun ScreenContent(screen: AppScreen) {
    val appModule = LocalAppModule.current

    when (screen) {
        AppScreen.Home -> {
            val viewModel = remember { HomeViewModel(appModule?.libraryRepository ?: error("HomeViewModel not provided")) }
            val state by viewModel.state.collectAsState()
            HomeScreen(
                books = state.recentBooks
            )
        }
        AppScreen.Library -> {
            val viewModel = remember { LibraryViewModel(appModule?.libraryRepository ?: error("LibraryViewModel not provided")) }
            val state by viewModel.state.collectAsState()
            LibraryScreen(
                state = state,
                onImportClick = { /* Handle navigation in Voyager */ },
                onBookClick = { /* Handle navigation in Voyager */ },
                onSortOrderChange = { viewModel.onSortOrderChange(it) }
            )
        }
        AppScreen.Discover -> {
            val screenModel = org.koin.compose.koinInject<com.wiztek.freader.ui.screens.discover.DiscoverScreenModel>()
            DiscoverScreen(screenModel = screenModel)
        }
        AppScreen.Collections -> CollectionsScreen()
        AppScreen.Stats -> StatsScreen()
        AppScreen.Settings -> SettingsScreen()
        AppScreen.About -> AboutScreen()
    }
}
