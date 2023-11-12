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

import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import java.awt.Desktop
import java.net.URI

@Composable
fun App() {
  val viewModel = remember { HttpFileServerViewModel() }

  val enabled by remember {
    derivedStateOf { viewModel.port.toIntOrNull() != null && viewModel.shardPath.isNotBlank() }
  }

  val isServerRunning by remember {
    derivedStateOf { viewModel.serverStatus == ServerStatus.STARTED }
  }

  DisposableEffect(Unit) { onDispose { viewModel.stopServer() } }

  val offset by
      animateIntOffsetAsState(
          IntOffset(
              0, if (isServerRunning) 0 else with(LocalDensity.current) { 80.dp.toPx().toInt() }))

  Surface(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Box {
      Row(
          modifier = Modifier.fillMaxWidth().height(80.dp).align(Alignment.TopCenter),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                modifier = Modifier.width(120.dp),
                value = viewModel.port,
                onValueChange = viewModel::setPort,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Port") })

            OutlinedTextField(
                modifier = Modifier.weight(1F),
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
                  Text("Shard Path")
                }
          }

      Card(
          modifier = Modifier.fillMaxSize().offset { offset }.clip(RoundedCornerShape(8.dp)),
          shape = RoundedCornerShape(8.dp),
          elevation = CardDefaults.cardElevation(8.dp),
      ) {
        Box(modifier = Modifier.fillMaxSize()) {
          Button(
              modifier = Modifier.align(Alignment.Center).size(220.dp),
              elevation = ButtonDefaults.buttonElevation(8.dp),
              enabled = enabled,
              onClick = {
                when (viewModel.serverStatus) {
                  ServerStatus.STARTED -> viewModel.stopServer()
                  ServerStatus.STOPPED -> viewModel.startServer()
                }
              }) {
                Text(if (isServerRunning) "Stop" else "Start")
              }

          FloatingActionButton(
              modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
              onClick = {
                val desktop = Desktop.getDesktop()

                desktop.browse(
                    URI.create("http://${HttpFileServerViewModel.HOST}:${viewModel.port}"))
              }) {
                Icon(Icons.Default.TravelExplore, contentDescription = "Explore")
              }
        }
      }

      SnackbarHost(
          modifier = Modifier.align(Alignment.BottomCenter),
          hostState = viewModel.snackbarHostState,
          snackbar = { snackbarData ->
            Snackbar(
                modifier = Modifier.padding(8.dp),
                snackbarData = snackbarData,
            )
          })
    }
  }
}
