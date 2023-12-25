/*
 * Copyright 2023 The Open Source Project
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

package com.kouqurong.plugin.adbtool

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kouqurong.plugin.adbtool.component.CommandDialog
import com.kouqurong.plugin.adbtool.component.CommandItem
import com.kouqurong.plugin.adbtool.model.Command
import com.kouqurong.plugin.view.SearchDispatcher

fun onSearch(search: String) {
  println("onSearch: $search")
}

@Composable
fun App(searchDispatcher: SearchDispatcher) {
  DisposableEffect(Unit) {
    searchDispatcher.register(::onSearch)
    onDispose { searchDispatcher.unregister(::onSearch) }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    var clickedCommand by remember { mutableStateOf<Command?>(null) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
          item {
            CommandItem(
                command =
                    Command(
                        name = "Top Activity",
                        description = "This is description for this command,",
                        command = "shell dumpy acitity activities",
                        keywords = "activity,")) {
                  clickedCommand = it
                }
          }

          item {
            CommandItem(
                command =
                    Command(
                        name = "Top Activity",
                        description = "This is description for this command,",
                        command = "shell dumpy acitity activities",
                        keywords = "activity,")) {}
          }

          item {
            CommandItem(
                command =
                    Command(
                        name = "Top Activity",
                        description = "This is description for this command,",
                        command = "shell dumpy acitity activities",
                        keywords = "activity,")) {}
          }

          item {
            CommandItem(
                command =
                    Command(
                        name = "Top Activity",
                        description = "This is description for this command,",
                        command = "shell dumpy acitity activities",
                        keywords = "activity,")) {}
          }

          item {
            CommandItem(
                command =
                    Command(
                        name = "Top Activity",
                        description = "This is description for this command,",
                        command = "shell dumpy acitity activities",
                        keywords = "activity,")) {}
          }

          item {
            CommandItem(
                command =
                    Command(
                        name = "Top Activity",
                        description = "This is description for this command,",
                        command = "shell dumpy acitity activities",
                        keywords = "activity,")) {}
          }

          item {
            CommandItem(
                command =
                    Command(
                        name = "Top Activity",
                        description = "This is description for this command,",
                        command = "shell dumpy acitity activities",
                        keywords = "activity,")) {}
          }

          item {
            CommandItem(
                command =
                    Command(
                        name = "Top Activity",
                        description = "This is description for this command,",
                        command = "shell dumpy acitity activities",
                        keywords = "activity,")) {}
          }
        }

    if (clickedCommand != null) {
      CommandDialog(command = clickedCommand!!, onDismissRequest = { clickedCommand = null }) {}
    }
  }
}

@Composable fun CheckADB() {}
