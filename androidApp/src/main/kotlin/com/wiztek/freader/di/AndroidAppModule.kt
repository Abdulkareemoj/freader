package com.wiztek.freader.di

import coil3.ImageLoader as CoilImageLoader
import com.wiztek.freader.reader.CbzImageFetcher
import com.wiztek.freader.settings.SettingsPersistence
import com.wiztek.freader.settings.SettingsPersistenceImpl
import org.koin.dsl.module
import org.koin.android.ext.koin.androidContext
import com.wiztek.freader.library.repository.LibraryRepository
import com.wiztek.freader.library.LibraryImporter
import com.wiztek.freader.library.LibraryImporterImpl
import com.wiztek.freader.library.AndroidBookMetadataExtractor
import com.wiztek.freader.library.BookMetadataExtractor
import com.wiztek.freader.database.DatabaseDriverFactory
import com.wiztek.freader.database.createDatabase
import okio.FileSystem
import okio.Path.Companion.toPath

val androidAppModule = module {
    single<FileSystem> { FileSystem.SYSTEM }
    single<CoilImageLoader> {
        CoilImageLoader.Builder(androidContext())
            .components {
                add(CbzImageFetcher.Factory())
            }
            .build()
    }
    
    // Library Importer
    single<LibraryImporter> {
        val context = androidContext()
        LibraryImporterImpl(
            fileSystem = FileSystem.SYSTEM,
            appStorageDir = context.filesDir.absolutePath.toPath()
        )
    }

    // Metadata Extractor
    single<BookMetadataExtractor> {
        AndroidBookMetadataExtractor(androidContext())
    }
    
    // Repository
    single {
        val driverFactory = DatabaseDriverFactory(androidContext())
        val database = createDatabase(driverFactory)
        LibraryRepository(database)
    }

    // Settings Persistence
    single<SettingsPersistence> {
        val context = androidContext()
        val path = context.filesDir.absolutePath.toPath().div("settings.json")
        SettingsPersistenceImpl(FileSystem.SYSTEM, path)
    }
}
