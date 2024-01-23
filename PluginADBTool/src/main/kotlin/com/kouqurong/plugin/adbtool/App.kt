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
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kouqurong.plugin.adbtool.component.CommandAddDialog
import com.kouqurong.plugin.adbtool.component.CommandDetailDialog
import com.kouqurong.plugin.adbtool.component.CommandItem
import com.kouqurong.plugin.adbtool.component.CommandRunningWindow
import com.kouqurong.plugin.adbtool.util.isBackground
import com.kouqurong.plugin.adbtool.util.runningProcess
import com.kouqurong.plugin.adbtool.viewmodel.ADBToolViewModel
import com.kouqurong.plugin.database.Command
import com.kouqurong.plugin.view.SearchDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun App(searchDispatcher: SearchDispatcher) {
  val viewModel = remember { ADBToolViewModel() }

  val commands by viewModel.commands.collectAsState()

  var runningCommand by remember { mutableStateOf<Command?>(null) }

  var isAddNewCommand by remember { mutableStateOf(false) }

  DisposableEffect(Unit) {
    searchDispatcher.register(viewModel::search)
    onDispose { searchDispatcher.unregister(viewModel::search) }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    var clickedCommand by remember { mutableStateOf<Command?>(null) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
          items(commands.size, key = { commands[it].id }) { index ->
            val command = commands[index]

            CommandItem(command = command, onClick = { clickedCommand = command })
          }
        }

    if (clickedCommand != null) {
      val scope = rememberCoroutineScope()

      CommandDetailDialog(
          command = clickedCommand!!,
          onDismissRequest = { clickedCommand = null },
          onSaveRequest = {
            viewModel.updateCommand(it)
            clickedCommand = null
          },
          onDeleteRequest = {
            viewModel.deleteCommand(it)
            clickedCommand = null
          },
          onRunningRequest = { command, isRunningBackground ->
            if (isRunningBackground) {
              scope.launch {
                runningProcess(arguments = command.arguments, inBackground = command.isBackground())
                    .collect()
              }
            } else {
              runningCommand = command
            }
          })
    }

    if (runningCommand != null) {
      CommandRunningWindow(command = runningCommand!!, onDismiss = { runningCommand = null })
    }

    FloatingActionButton(
        modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp),
        onClick = { isAddNewCommand = true }) {
          Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Add",
          )
        }

    if (isAddNewCommand) {
      CommandAddDialog(
          onDismissRequest = { isAddNewCommand = false },
          onSaveRequest = {
            viewModel.insertCommand(it)
            isAddNewCommand = false
          })
    }
  }
}

@Composable fun CheckADB() {}
