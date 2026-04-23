package com.wiztek.freader.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wiztek.freader.data.preview.sampleBooks
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.library.repository.LibraryRepository
import com.wiztek.freader.reader.model.BookFormat
import com.wiztek.freader.ui.screens.about.AboutScreen
import com.wiztek.freader.ui.screens.collections.CollectionsScreen
import com.wiztek.freader.ui.screens.discover.DiscoverScreen
import com.wiztek.freader.ui.screens.discover.ProcessingLibraryScreen
import com.wiztek.freader.ui.screens.home.HomeScreen
import com.wiztek.freader.ui.screens.home.HomeViewModel
import com.wiztek.freader.ui.screens.library.LibraryScreen
import com.wiztek.freader.ui.screens.library.LibraryViewModel
import com.wiztek.freader.ui.screens.settings.SettingsScreen
import com.wiztek.freader.ui.screens.details.BookDetailsScreen
import com.wiztek.freader.ui.screens.details.BookMetadataEditorScreen
import com.wiztek.freader.ui.screens.details.SeriesDetailsScreen
import com.wiztek.freader.ui.screens.onboarding.OnboardingScreen
import com.wiztek.freader.ui.screens.player.PlayerScreen
import com.wiztek.freader.ui.screens.search.SearchScreen
import com.wiztek.freader.ui.screens.reader.ReaderScreen
import com.wiztek.freader.ui.screens.reader.ComicReaderScreen
import com.wiztek.freader.ui.screens.reader.ReaderContentsScreen
import com.wiztek.freader.ui.screens.stats.StatsScreen
import org.koin.compose.koinInject

sealed class VoyagerScreen : Screen {
    object Onboarding : VoyagerScreen() {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            OnboardingScreen(onFinish = { navigator.replaceAll(Home) })
        }
    }

    object Home : VoyagerScreen() {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            val repository = koinInject<LibraryRepository>()
            val viewModel = remember { 
                HomeViewModel(repository).apply {
                    addSampleData(sampleBooks)
                }
            }
            val state by viewModel.state.collectAsState()

            HomeScreen(
                books = state.recentBooks
            )
        }
    }

    object Library : VoyagerScreen() {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            val repository = koinInject<LibraryRepository>()
            val viewModel = remember { LibraryViewModel(repository) }
            val state by viewModel.state.collectAsState()

            LibraryScreen(
                state = state,
                onImportClick = { navigator.push(ProcessingLibrary) },
                onBookClick = { book -> 
                    val sName = book.seriesName
                    if (sName != null) {
                        navigator.push(SeriesDetails(sName, state.books.filter { it.seriesName == sName }))
                    } else {
                        navigator.push(BookDetails(book))
                    }
                },
                onSortOrderChange = { viewModel.onSortOrderChange(it) }
            )
        }
    }

    object Search : VoyagerScreen() {
        @Composable
        override fun Content() {
            SearchScreen()
        }
    }

    data class BookDetails(val book: LibraryBook) : VoyagerScreen() {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            BookDetailsScreen(
                book = book,
                onReadClick = { 
                    if (book.format == BookFormat.CBZ || book.format == BookFormat.CBR) {
                        navigator.push(ComicReader(book))
                    } else {
                        navigator.push(Reader(book))
                    }
                },
                onListenClick = { navigator.push(Player(book)) },
                onEditClick = { navigator.push(BookMetadataEditor(book)) },
                onBack = { navigator.pop() }
            )
        }
    }

    data class SeriesDetails(val seriesName: String, val books: List<LibraryBook>) : VoyagerScreen() {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            SeriesDetailsScreen(
                seriesName = seriesName,
                books = books,
                onBookClick = { navigator.push(BookDetails(it)) },
                onBack = { navigator.pop() }
            )
        }
    }

    data class BookMetadataEditor(val book: LibraryBook) : VoyagerScreen() {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            BookMetadataEditorScreen(
                book = book,
                onSave = { navigator.pop() },
                onBack = { navigator.pop() }
            )
        }
    }

    data class Reader(val book: LibraryBook) : VoyagerScreen() {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            ReaderScreen(
                book = book
            )
        }
    }

    data class ComicReader(val book: LibraryBook) : VoyagerScreen() {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            ComicReaderScreen(
                book = book,
                onBack = { navigator.pop() }
            )
        }
    }

    data class ReaderContents(val book: LibraryBook) : VoyagerScreen() {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            ReaderContentsScreen(
                book = book,
                onBack = { navigator.pop() },
                onChapterClick = { chapterIndex -> 
                    navigator.pop()
                }
            )
        }
    }

    data class Player(val book: LibraryBook) : VoyagerScreen() {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            PlayerScreen(
                book = book,
                onBack = { navigator.pop() }
            )
        }
    }

    object ProcessingLibrary : VoyagerScreen() {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            ProcessingLibraryScreen(onFinish = { navigator.pop() })
        }
    }

    object Discover : VoyagerScreen() {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            // Assuming ScreenModel is available via Koin injection
            val screenModel = org.koin.compose.koinInject<com.wiztek.freader.ui.screens.discover.DiscoverScreenModel>()
            com.wiztek.freader.ui.screens.discover.DiscoverScreen(
                screenModel = screenModel
            )
        }
    }

    object Collections : VoyagerScreen() {
        @Composable
        override fun Content() {
            CollectionsScreen()
        }
    }

    object Stats : VoyagerScreen() {
        @Composable
        override fun Content() {
            StatsScreen()
        }
    }

    object Settings : VoyagerScreen() {
        @Composable
        override fun Content() {
            SettingsScreen()
        }
    }

    object About : VoyagerScreen() {
        @Composable
        override fun Content() {
            AboutScreen()
        }
    }
}
