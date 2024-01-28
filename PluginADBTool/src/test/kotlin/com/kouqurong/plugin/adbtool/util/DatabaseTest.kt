package com.kouqurong.plugin.adbtool.util

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.kouqurong.plugin.database.ADBToolsDatabase
import org.junit.Before
import org.junit.Test

class DatabaseTest {
    private lateinit var db: ADBToolsDatabase

    @Before
    fun setUp() {
        val driver = JdbcSqliteDriver("jdbc:sqlite:adbtools.db")
        ADBToolsDatabase.Schema.create(driver)

        db = ADBToolsDatabase(driver)
    }

    @Test
    fun `query all commands`() {
        db.commandQueries.queryAll().executeAsList().forEach {
            println(it)
        }
    }
}