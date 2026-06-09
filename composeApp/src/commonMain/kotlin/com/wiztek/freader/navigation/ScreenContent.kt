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
            val collections by viewModel.collections.collectAsState()
            val repo = appModule?.libraryRepository ?: error("repository not provided")
            HomeScreen(
                recentlyReadBooks = state.recentlyReadBooks,
                newlyAddedBooks = state.newlyAddedBooks,
                recentBooks = state.recentBooks,
                repository = repo,
                collections = collections,
                onDeleteBooks = { viewModel.deleteBooks(it) },
                onRenameBook = { id, title -> viewModel.renameBook(id, title) },
                onAddToCollection = { cid, bids -> viewModel.addToCollection(cid, bids) },
                onCreateCollection = { viewModel.createCollection(it) }
            )
        }
        AppScreen.Library -> {
            val viewModel = remember { LibraryViewModel(appModule?.libraryRepository ?: error("LibraryViewModel not provided")) }
            val state by viewModel.state.collectAsState()
            val collections by viewModel.collections.collectAsState()
            LibraryScreen(
                state = state,
                collections = collections,
                onImportClick = { },
                onBookClick = { },
                onSortOrderChange = { viewModel.onSortOrderChange(it) },
                onFilterFormatChange = { viewModel.onFilterFormatChange(it) },
                onDeleteBooks = { viewModel.deleteBooks(it) },
                onRenameBook = { id, title -> viewModel.renameBook(id, title) },
                onAddToCollection = { cid, bids -> viewModel.addToCollection(cid, bids) },
                onCreateCollection = { viewModel.createCollection(it) }
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
