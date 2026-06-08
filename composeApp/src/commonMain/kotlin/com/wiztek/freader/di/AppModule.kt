package com.wiztek.freader.di

import com.wiztek.freader.library.repository.LibraryRepository
import org.koin.dsl.module

interface AppModule {
    val libraryRepository: LibraryRepository
}

val commonAppModule = module {
    // Shared dependencies
    single { com.wiztek.freader.ui.screens.discover.DiscoverScreenModel(get(), get(), get()) }
    single { com.wiztek.freader.ui.screens.stats.StatsScreenModel(get()) }
    single { com.wiztek.freader.ui.screens.search.SearchScreenModel(get()) }
    single { com.wiztek.freader.ui.screens.collections.CollectionsViewModel(get()) }
    factory { (collectionId: String) -> com.wiztek.freader.ui.screens.collections.CollectionDetailsViewModel(get(), collectionId) }
    factory { com.wiztek.freader.ui.screens.details.BookDetailsViewModel(get()) }
    factory { (bookId: String) -> com.wiztek.freader.ui.screens.reader.ReaderScreenModel(get(), get(), bookId) }
    factory { (book: com.wiztek.freader.library.model.LibraryBook) -> com.wiztek.freader.ui.screens.reader.ComicReaderViewModel(book, get(), get()) }

    single { com.wiztek.freader.reader.ReaderStrategyFactory(get(), get()) }
}
