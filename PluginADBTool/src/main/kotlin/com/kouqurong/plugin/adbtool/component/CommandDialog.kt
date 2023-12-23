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

package com.kouqurong.plugin.adbtool.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import com.kouqurong.plugin.adbtool.model.Command
import com.kouqurong.plugin.view.recomposeHighlighter

@Composable
fun CommandDialog(
    command: Command,
    onDismissRequest: () -> Unit,
    onCommandChange: (Command) -> Unit,
) {
  var editCommand by remember { mutableStateOf(command.copy()) }

  val isCommandChanged by derivedStateOf { command != editCommand }

  val title by derivedStateOf {
    if (isCommandChanged) {
      "Save"
    } else {
      "Detail"
    }
  }

  var isShowRunningWindow by remember { mutableStateOf(false) }

  AlertDialog(
      onDismissRequest = onDismissRequest,
      title = { Text(text = title) },
      text = {
        Box(modifier = Modifier.size(620.dp, 260.dp)) {
          Column(modifier = Modifier.fillMaxSize()) {
            TextField(
                value = editCommand.name,
                onValueChange = { editCommand = editCommand.copy(name = it) },
                label = { Text(text = "Name") },
                modifier = Modifier.fillMaxWidth().recomposeHighlighter())

            TextField(
                value = editCommand.description,
                onValueChange = { editCommand = editCommand.copy(description = it) },
                label = { Text(text = "Description") },
                modifier = Modifier.fillMaxWidth().recomposeHighlighter())

            TextField(
                value = editCommand.command,
                onValueChange = { editCommand = editCommand.copy(command = it) },
                label = { Text(text = "Command") },
                modifier = Modifier.fillMaxWidth().recomposeHighlighter())

            TextField(
                value = editCommand.keywords,
                onValueChange = { editCommand = editCommand.copy(keywords = it) },
                label = { Text(text = "Keywords") },
                modifier = Modifier.fillMaxWidth().recomposeHighlighter())
          }
        }
      },
      confirmButton = {
        IconButton(
            onClick = { isShowRunningWindow = true },
        ) {
          Icon(Icons.Default.PlayArrow, "Run")
        }
      },
      dismissButton = {
        if (isCommandChanged) {
          IconButton(onClick = {}) { Icon(Icons.Default.Done, "Done") }
        }
      })

  CommandRunningWindow(
      visible = isShowRunningWindow,
      command = { editCommand },
      onDismissRequest = { isShowRunningWindow = false })
}

@Composable
fun CommandRunningWindow(
    visible: Boolean,
    command: () -> Command,
    onDismissRequest: () -> Unit,
) {
  val runningCommand by derivedStateOf { command() }

  Window(
      onCloseRequest = onDismissRequest,
      title = "Running: ${runningCommand.name}",
      visible = visible,
  ) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
      SelectionContainer {
        Text(
            text = "",
            overflow = TextOverflow.Visible,
            modifier = Modifier.fillMaxSize().padding(8.dp))
      }
    }
  }
}
