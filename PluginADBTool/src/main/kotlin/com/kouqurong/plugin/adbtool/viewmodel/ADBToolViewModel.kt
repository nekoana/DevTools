/*
 * Copyright 2024 The Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kouqurong.plugin.adbtool.viewmodel

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.kouqurong.plugin.database.ADBToolsDatabase
import com.kouqurong.plugin.database.Command
import com.kouqurong.plugin.view.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ADBToolViewModel : ViewModel() {

  private val database: ADBToolsDatabase

  private val _commands = MutableStateFlow<List<Command>>(emptyList())

  val commands: StateFlow<List<Command>> = _commands

  init {
    val driver = JdbcSqliteDriver("jdbc:sqlite:adbtools.db")

    database = ADBToolsDatabase(driver)

    viewModelScope.launch {
      withContext(Dispatchers.IO) { ADBToolsDatabase.Schema.create(driver) }

      loadAll()
    }
  }

  private suspend fun loadAll() {
    database.commandQueries.queryAll().asFlow().mapToList(Dispatchers.IO).collect(_commands)
  }

  fun deleteCommand(command: Command) {
    viewModelScope.launch(Dispatchers.IO) { database.commandQueries.delete(command.id) }
  }

  fun updateCommand(command: Command) {
    viewModelScope.launch(Dispatchers.IO) {
      database.commandQueries.update(
          id = command.id,
          name = command.name,
          description = command.description,
          arguments = command.arguments,
          keywords = command.keywords,
      )
    }
  }

  fun insertCommand(command: Command) {
    viewModelScope.launch(Dispatchers.IO) {
      database.commandQueries.insert(
          name = command.name,
          description = command.description,
          arguments = command.arguments,
          keywords = command.keywords,
      )
    }
  }

  fun search(keyword: String) {
    viewModelScope.launch(Dispatchers.IO) {
      if (keyword.isEmpty()) {
        loadAll()
      } else {
        database.commandQueries
            .queryByKeywords(
                name = keyword,
                description = keyword,
                arguments = keyword,
                keywords = keyword,
            )
            .asFlow()
            .mapToList(Dispatchers.IO)
            .collect(_commands)
      }
    }
  }
}
