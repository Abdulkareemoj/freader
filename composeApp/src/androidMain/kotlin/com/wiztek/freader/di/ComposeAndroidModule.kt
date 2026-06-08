package com.wiztek.freader.di

import coil3.ImageLoader
import coil3.PlatformContext
import com.wiztek.freader.library.AndroidBookMetadataExtractor
import com.wiztek.freader.library.BookMetadataExtractor
import com.wiztek.freader.library.repository.LibraryRepository
import com.wiztek.freader.reader.ZipFetcher
import com.wiztek.freader.settings.SettingsPersistence
import com.wiztek.freader.settings.SettingsPersistenceImpl
import okio.FileSystem
import okio.Path.Companion.toPath
import org.koin.dsl.module
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind

val composeAppAndroidModule = module {
    single<FileSystem> { FileSystem.SYSTEM }

    single<ImageLoader> {
        ImageLoader.Builder(androidContext())
            .components {
                add(ZipFetcher.Factory(get()))
            }
            .build()
    }
    single { AndroidBookMetadataExtractor(androidContext()) } bind BookMetadataExtractor::class

    single<SettingsPersistence> {
        val context = androidContext()
        val path = context.filesDir.absolutePath.toPath().div("settings.json")
        SettingsPersistenceImpl(FileSystem.SYSTEM, path)
    }
}

