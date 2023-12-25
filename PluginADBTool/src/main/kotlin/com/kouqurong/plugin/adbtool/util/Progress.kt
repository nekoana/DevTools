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
import kotlinx.coroutines.withContext

@Throws(IOException::class)
suspend fun runningProcess(
    exePath: String,
    command: String,
    onOutput: (String) -> Unit,
    onFinished: () -> Unit,
    onError: (String) -> Unit,
) {
  withContext(Dispatchers.IO) {
    ProcessBuilder(exePath, command).start().apply {
      inputStream.bufferedReader().useLines { lines -> lines.forEach(onOutput) }
      errorStream.bufferedReader().useLines { lines -> lines.forEach(onError) }
      waitFor()
      onFinished()
    }
  }
}
