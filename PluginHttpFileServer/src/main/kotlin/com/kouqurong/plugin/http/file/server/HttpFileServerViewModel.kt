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

import androidx.compose.runtime.mutableStateOf
import com.example.plugins.configFiles
import com.example.plugins.configureRouting
import com.kouqurong.plugin.view.ViewModel
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.File

class HttpFileServerViewModel : ViewModel() {

  private lateinit var server: ApplicationEngine

  private val _port = mutableStateOf(DEFAULT_PORT.toString())

  private val _shardPath = mutableStateOf("")

  val shardPath: String
    get() = _shardPath.value

  val port: String
    get() = _port.value

  fun setPort(port: String) {
    _port.value = port
  }

  fun setPath(path: String) {
    _shardPath.value = path
  }

  fun startServer() {
    val listen = port.toIntOrNull() ?: return

    if (shardPath.isBlank()) return

    val file = File(shardPath)
    if (!file.isDirectory) return

    server =
        embeddedServer(Netty, port = listen, host = HOST) { configFiles("/", file) }
            .start(wait = false)
  }

  fun stopServer() {
    server.stop(1000, 1000)
  }

  companion object {
    const val HOST = "0.0.0.0"

    const val DEFAULT_PORT = 8080
  }
}

fun Application.module() {
  configureRouting()
}
