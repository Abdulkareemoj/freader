package com.wiztek.freader.di

import com.wiztek.freader.library.repository.LibraryRepository
import org.koin.dsl.module

interface AppModule {
    val libraryRepository: LibraryRepository
}

val commonAppModule = module {
    // Shared dependencies
}
