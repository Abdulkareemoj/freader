package com.wiztek.freader.di

import com.wiztek.freader.database.DatabaseDriverFactory
import com.wiztek.freader.database.createDatabase
import com.wiztek.freader.library.repository.LibraryRepository

/**
 * A simple manual DI provider for the shared module.
 */
class SharedModule(driverFactory: DatabaseDriverFactory) {
    val database = createDatabase(driverFactory)
    val libraryRepository = LibraryRepository(database)
}
