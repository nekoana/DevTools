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

package com.kouqurong.iso8583.util

import androidx.compose.ui.awt.ComposeWindow

fun awtFileChooser(filter: String = "*"): String? {
  val dialog =
      java.awt.FileDialog(ComposeWindow()).apply {
        // file 过滤
        setFilenameFilter { _, name ->
          if (filter == "*") return@setFilenameFilter true
          name.endsWith(filter)
        }
        isVisible = true
      }

  if (dialog.file == null) return null
  if (dialog.directory == null) return null

  return dialog.directory + dialog.file
}
