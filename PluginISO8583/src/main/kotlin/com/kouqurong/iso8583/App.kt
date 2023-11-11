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

package com.kouqurong.iso8583

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kouqurong.iso8583.componet.ISO8583Content
import com.kouqurong.iso8583.data.FieldMenuItem
import com.kouqurong.iso8583.viewmodel.IFieldMenuIntent
import com.kouqurong.iso8583.viewmodel.PluginISO8583ViewModel
import com.kouqurong.plugin.view.BackDispatcher
import jFileChooser

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun App(backDispatcher: BackDispatcher) {
  val viewModel = remember { PluginISO8583ViewModel() }

  DisposableEffect(backDispatcher) {
    val onBack = {
      viewModel.displayFieldItems.isNotEmpty().also {
        if (it) {
          viewModel.clearDisplayFieldItems()
        }
      }
    }

    backDispatcher.register(onBack)

    onDispose { backDispatcher.unregister(onBack) }
  }

  Surface(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    ISO8583Content(
        modifier = Modifier.fillMaxSize(),
        iso8583Hex = viewModel.iso8583Hex,
        fieldItems = viewModel.fieldItems,
        fieldMenuItems = FieldMenuItem.entries,
        onMenuClick = {
          when (it) {
            FieldMenuItem.Add -> viewModel.fieldMenuIntent(IFieldMenuIntent.Add)
            FieldMenuItem.Export -> {
              if (viewModel.fieldItems.isEmpty()) {
                viewModel.addDialogMessage("没有可导出的配置")
              } else {
                val file = jFileChooser(FileChooserType.SAVE, true, "conf", ".conf")
                if (file != null) {
                  viewModel.fieldMenuIntent(IFieldMenuIntent.Export(file))
                }
              }
            }
            FieldMenuItem.Import -> {
              val file = jFileChooser(FileChooserType.OPEN, true, "conf", ".conf")
              if (file != null) {
                viewModel.fieldMenuIntent(IFieldMenuIntent.Import(file))
              }
            }
            FieldMenuItem.Clear -> viewModel.fieldMenuIntent(IFieldMenuIntent.Clear)
            FieldMenuItem.Temeplate -> viewModel.fieldMenuIntent(IFieldMenuIntent.Template)
          }
        },
        displayFieldItems = viewModel.displayFieldItems,
        scrollFieldDetailState = viewModel.scrollFieldDetailState,
        swipeEnabled = viewModel.displayFieldItems.isEmpty(),
        swipeCrossFadeState = viewModel.swipeCrossFadeState,
        onFieldItemIntent = viewModel::fieldItemIntent,
        onISO8583HexIntent = viewModel::iso8583HexIntent,
    )
  }

  MessageDialog(
      message = { viewModel.dialogMessage.value }, onConfirm = viewModel::removeDialogMessage)
}

@Composable
fun MessageDialog(
    modifier: Modifier = Modifier,
    message: () -> String?,
    onConfirm: () -> Unit,
) {
  val text by remember { derivedStateOf { message() } }

  text?.let {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {},
        confirmButton = { Button(onClick = onConfirm) { Text(text = "OK") } },
        title = { Text(text = "Message") },
        text = { Text(text = it) })
  }
}
