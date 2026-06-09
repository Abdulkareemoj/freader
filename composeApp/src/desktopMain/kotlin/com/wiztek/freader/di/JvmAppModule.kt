package com.wiztek.freader.di

import coil3.ImageLoader
import com.wiztek.freader.database.DatabaseDriverFactory
import com.wiztek.freader.database.createDatabase
import com.wiztek.freader.library.repository.LibraryRepository
import com.wiztek.freader.library.LibraryImporter
import com.wiztek.freader.library.BookMetadataExtractor
import com.wiztek.freader.library.JvmBookMetadataExtractor
import com.wiztek.freader.library.LibraryImporterImpl
import com.wiztek.freader.reader.ZipFetcher
import com.wiztek.freader.settings.SettingsPersistence
import com.wiztek.freader.settings.SettingsPersistenceImpl
import okio.FileSystem
import okio.Path.Companion.toPath
import org.koin.dsl.module

val jvmAppModule = module {
    single {
        val driverFactory = DatabaseDriverFactory()
        val database = createDatabase(driverFactory)
        LibraryRepository(database)
    }

    single<FileSystem> { FileSystem.SYSTEM }

    single<ImageLoader> {
        ImageLoader.Builder(coil3.PlatformContext.INSTANCE)
            .components {
                add(ZipFetcher.Factory(get()))
            }
            .build()
    }

    single<LibraryImporter> {
        val userHome = System.getProperty("user.home")
        val appDataDir = userHome.toPath().div(".freader")
        LibraryImporterImpl(
            fileSystem = get(),
            appStorageDir = appDataDir
        )
    }


    single<BookMetadataExtractor> {
        JvmBookMetadataExtractor()
    }

    single { com.wiztek.freader.reader.DesktopStreamer(get()) }

    // Settings Persistence
    single<SettingsPersistence> {
        val userHome = System.getProperty("user.home")
        val appDataDir = userHome.toPath().div(".freader")
        val path = appDataDir.div("settings.json")
        SettingsPersistenceImpl(FileSystem.SYSTEM, path)
    }
}
