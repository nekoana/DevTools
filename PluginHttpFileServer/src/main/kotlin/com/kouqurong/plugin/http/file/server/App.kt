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

package com.kouqurong.plugin.http.file.server

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun App() {
  val viewModel = remember { HttpFileServerViewModel() }

  DisposableEffect(Unit) { onDispose { viewModel.stopServer() } }

  Surface(modifier = Modifier.fillMaxSize()) {
    Box {
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          OutlinedTextField(
              modifier = Modifier.width(120.dp),
              value = viewModel.port,
              onValueChange = viewModel::setPort,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              label = { Text("Port") })

          OutlinedTextField(
              value = viewModel.shardPath,
              onValueChange = viewModel::setPath,
              label = { Text("Path") })

          Button(
              onClick = {
                val file = jFileChooser(mode = FileChooserMode.OPEN, type = FileChooseType.Folder)
                if (file != null) {
                  viewModel.setPath(file.absolutePath)
                }
              }) {
                Text("Choose Shard Path")
              }
        }

        Button(onClick = { viewModel.startServer() }) { Text("Start") }
      }
    }
  }
}
