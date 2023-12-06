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

import androidx.compose.ui.awt.ComposeWindow
import java.io.File
import javax.swing.JFileChooser

enum class FileChooserMode {
  OPEN,
  SAVE
}

enum class FileChooseType {
  File,
  Folder,
}

fun jFileChooser(
    mode: FileChooserMode = FileChooserMode.SAVE,
    type: FileChooseType = FileChooseType.Folder,
    extension: String? = null,
    description: String? = null,
): File? {
  val fileChooser =
      JFileChooser().apply {
        fileSelectionMode =
            when (type) {
              FileChooseType.File -> JFileChooser.FILES_ONLY
              FileChooseType.Folder -> JFileChooser.DIRECTORIES_ONLY
            }

        fileFilter =
            object : javax.swing.filechooser.FileFilter() {
              override fun accept(f: File): Boolean {
                if (extension == null) return true
                return f.extension == extension
              }

              override fun getDescription(): String {
                return description ?: "*"
              }
            }
      }

  when (mode) {
    FileChooserMode.OPEN -> fileChooser.showOpenDialog(ComposeWindow())
    FileChooserMode.SAVE -> fileChooser.showSaveDialog(ComposeWindow())
  }

  return fileChooser.selectedFile
}
