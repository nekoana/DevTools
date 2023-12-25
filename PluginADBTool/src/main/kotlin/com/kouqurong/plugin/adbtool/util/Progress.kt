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

package com.kouqurong.plugin.adbtool.util

import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Throws(IOException::class)
suspend fun runningProcess(
    program: String,
    command: String,
) =
    channelFlow {
          val exe =
              ProcessBuilder(program, command).start().apply {
                val input = inputStream.bufferedReader()

                launch(Dispatchers.IO) {
                  while (isActive) {
                    val line = input.readLine() ?: break
                    send(line)
                  }
                }
              }

          awaitClose { exe.destroy() }
        }
        .flowOn(Dispatchers.IO)
