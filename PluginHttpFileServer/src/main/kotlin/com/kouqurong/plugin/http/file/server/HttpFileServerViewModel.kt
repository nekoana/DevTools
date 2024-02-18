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

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.kouqurong.plugin.view.ViewModel
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class ServerStatus {
  RUNNING,
  STOPPED
}

class HttpFileServerViewModel : ViewModel() {

  init {
    val path = System.getProperty("compose.application.resources.dir")
  }

  private val _port = mutableStateOf(DEFAULT_PORT.toString())

  private val _shardPath = mutableStateOf("")

  val snackBarHostState = SnackbarHostState()

  private val _serverStatus = mutableStateOf(ServerStatus.STOPPED)

  val port: String
    get() = _port.value

  val enabled by derivedStateOf { port.toIntOrNull() != null && shardPath.isNotBlank() }

  val serverStatus: ServerStatus
    get() = _serverStatus.value

  val isServerRunning by derivedStateOf { serverStatus == ServerStatus.RUNNING }

  val shardPath: String
    get() = _shardPath.value

  fun setPort(port: String) {
    _port.value = port
  }

  fun setPath(path: String) {
    _shardPath.value = path
  }

  fun startServer() =
      viewModelScope.launch(Dispatchers.IO) {
        val listen = port.toIntOrNull()
        if (listen == null) {
          snackBarHostState.showSnackbar("Port is invalid")
          return@launch
        }

        if (shardPath.isBlank()) {
          snackBarHostState.showSnackbar("Path is invalid")
          return@launch
        }

        val file = File(shardPath)
        if (!file.isDirectory) {
          snackBarHostState.showSnackbar("Path is invalid")
          return@launch
        }

        runCatching {}
            .onFailure { snackBarHostState.showSnackbar("Start server failed: ${it.message}") }
            .onSuccess { _serverStatus.value = ServerStatus.RUNNING }
      }

  fun stopServer() = viewModelScope.launch(Dispatchers.IO) {}

  companion object {
    const val HOST = "0.0.0.0"

    const val DEFAULT_PORT = 8080
  }
}
