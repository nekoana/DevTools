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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.kouqurong.plugin.database.Command
import com.kouqurong.plugin.view.recomposeHighlighter

@Composable
fun CommandEditorContent(
    modifier: Modifier = Modifier,
    command: Command,
    onCommandChange: (Command) -> Unit,
) {
  var editCommand by remember { mutableStateOf(command) }

  SideEffect {
    if (editCommand !== command) {
      onCommandChange(editCommand)
    }
  }

  Column(modifier = modifier) {
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
        value = editCommand.arguments,
        onValueChange = { editCommand = editCommand.copy(arguments = it) },
        label = { Text(text = "Arguments") },
        modifier = Modifier.fillMaxWidth().recomposeHighlighter())

    TextField(
        value = editCommand.keywords,
        onValueChange = { editCommand = editCommand.copy(keywords = it) },
        label = { Text(text = "Keywords") },
        modifier = Modifier.fillMaxWidth().recomposeHighlighter())
  }
}
