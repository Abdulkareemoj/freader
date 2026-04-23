package com.wiztek.freader.di

import com.wiztek.freader.database.DatabaseDriverFactory
import com.wiztek.freader.database.createDatabase
import com.wiztek.freader.library.repository.LibraryRepository
import com.wiztek.freader.library.LibraryImporter
import com.wiztek.freader.library.LibraryImporterImpl
import okio.FileSystem
import okio.Path.Companion.toPath
import org.koin.dsl.module

val jvmAppModule = module {
    single {
        val driverFactory = DatabaseDriverFactory()
        val database = createDatabase(driverFactory)
        LibraryRepository(database)
    }

    single<LibraryImporter> {
        val userHome = System.getProperty("user.home")
        val appDataDir = userHome.toPath().div(".freader")
        LibraryImporterImpl(
            fileSystem = FileSystem.SYSTEM,
            appStorageDir = appDataDir
        )
    }

    single<com.wiztek.freader.library.BookMetadataExtractor> {
        com.wiztek.freader.library.BookMetadataExtractor()
    }
}
