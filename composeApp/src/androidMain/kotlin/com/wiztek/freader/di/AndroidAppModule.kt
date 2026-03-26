package com.wiztek.freader.di

import coil3.ImageLoader
import coil3.PlatformContext
import com.wiztek.freader.reader.CbzImageFetcher
import org.koin.dsl.module
import org.koin.android.ext.koin.androidContext
import com.wiztek.freader.library.repository.LibraryRepository

val androidAppModule = module {
    single<ImageLoader> {
        ImageLoader.Builder(androidContext())
            .components {
                add(CbzImageFetcher.Factory())
            }
            .build()
    }
    // Assume you have a way to provide your repository from DI as well
    // single { LibraryRepository(get()) }
}
