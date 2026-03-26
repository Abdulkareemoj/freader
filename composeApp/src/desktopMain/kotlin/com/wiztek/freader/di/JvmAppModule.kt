package com.wiztek.freader.di

import com.wiztek.freader.database.DatabaseDriverFactory
import com.wiztek.freader.database.createDatabase
import com.wiztek.freader.library.repository.LibraryRepository
import org.koin.dsl.module

val jvmAppModule = module {
    single {
        val driverFactory = DatabaseDriverFactory()
        val database = createDatabase(driverFactory)
        LibraryRepository(database)
    }
}
