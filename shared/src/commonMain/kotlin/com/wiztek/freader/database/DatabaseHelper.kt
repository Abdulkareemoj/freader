package com.wiztek.freader.database

fun createDatabase(driverFactory: DatabaseDriverFactory): FreaderDatabase {
    val driver = driverFactory.createDriver()
    return FreaderDatabase(driver)
}
