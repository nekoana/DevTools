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

package com.kouqurong.plugin.adbtool.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kouqurong.plugin.adbtool.util.isBackground
import com.kouqurong.plugin.database.Command

@Composable
fun CommandAddDialog(
    command: Command =
        Command(
            id = 0L,
            name = "",
            description = "",
            arguments = "",
            keywords = "",
            background = 0L,
        ),
    onDismissRequest: () -> Unit,
    onSaveRequest: (Command) -> Unit,
) {

  var editCommand by remember { mutableStateOf(command) }

  val isCommandChanged by derivedStateOf { editCommand != command }

  AlertDialog(
      onDismissRequest = onDismissRequest,
      title = { Text(text = "Add New Command") },
      text = {
        Box(modifier = Modifier.size(620.dp, 260.dp)) {
          CommandEditorContent(
              modifier = Modifier.fillMaxSize().padding(8.dp),
              command = command,
              onCommandChange = { editCommand = it })
        }
      },
      confirmButton = {
        ConfirmButton(
            isRunInBackground = editCommand.isBackground(),
            onRunInBackgroundChanged = {
              editCommand = editCommand.copy(background = if (it) 1L else 0L)
            },
        )
      },
      dismissButton = {
        DismissButton(isChanged = isCommandChanged, onSaveClick = { onSaveRequest(editCommand) })
      })
}

@Composable
private fun ConfirmButton(
    modifier: Modifier = Modifier,
    isRunInBackground: Boolean,
    onRunInBackgroundChanged: (Boolean) -> Unit,
) {
  Row(
      modifier = modifier,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Checkbox(
        checked = isRunInBackground,
        onCheckedChange = onRunInBackgroundChanged,
    )
    Text(text = "Run in background")
  }
}

@Composable
private fun DismissButton(
    modifier: Modifier = Modifier,
    isChanged: Boolean,
    onSaveClick: () -> Unit,
) {
  Row(
      modifier = modifier,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    if (isChanged) {
      IconButton(onClick = onSaveClick) { Icon(Icons.Default.Done, "Save") }
    }
  }
}
