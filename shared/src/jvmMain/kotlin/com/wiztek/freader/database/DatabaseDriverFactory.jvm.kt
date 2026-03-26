package com.wiztek.freader.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val databaseDir = File(System.getProperty("user.home"), ".freader")
        if (!databaseDir.exists()) {
            databaseDir.mkdirs()
        }
        val databasePath = File(databaseDir, "freader.db")
        
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:${databasePath.absolutePath}")
        
        // Use a simple check to see if the database file is empty/missing
        if (databasePath.length() == 0L) {
            FreaderDatabase.Schema.create(driver)
        }
        
        return driver
    }
}
