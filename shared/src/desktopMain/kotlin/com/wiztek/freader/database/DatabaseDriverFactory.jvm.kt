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
        
        var driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:${databasePath.absolutePath}")
        
        val schema = FreaderDatabase.Schema
        val currentVersion = getVersion(driver)
        
        if (currentVersion == 0L) {
            // Check if tables already exist before creating
            if (!isTableExists(driver, "BookEntity")) {
                schema.create(driver)
            }
            setVersion(driver, schema.version)
        } else if (currentVersion < schema.version) {
            // No migrations yet, just recreate the database
            driver.close()
            databasePath.delete()
            driver = JdbcSqliteDriver("jdbc:sqlite:${databasePath.absolutePath}")
            schema.create(driver)
            setVersion(driver, schema.version)
        }
        
        return driver
    }

    private fun isTableExists(driver: SqlDriver, tableName: String): Boolean {
        return driver.executeQuery(
            null, 
            "SELECT name FROM sqlite_master WHERE type='table' AND name=?;", 
            { cursor ->
                app.cash.sqldelight.db.QueryResult.Value(cursor.next().value)
            }, 
            1
        ) { 
            bindString(0, tableName)
        }.value
    }

    private fun getVersion(driver: SqlDriver): Long {
        return driver.executeQuery(null, "PRAGMA user_version;", { cursor ->
            val next = cursor.next()
            if (next.value) {
                app.cash.sqldelight.db.QueryResult.Value(cursor.getLong(0))
            } else {
                app.cash.sqldelight.db.QueryResult.Value(0L)
            }
        }, 0).value ?: 0L
    }

    private fun setVersion(driver: SqlDriver, version: Long) {
        driver.execute(null, "PRAGMA user_version = $version;", 0)
    }
}
