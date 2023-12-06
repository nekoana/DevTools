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

package com.kouqurong.plugin.hex.tool

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadSvgPainter
import com.google.auto.service.AutoService
import com.kouqurong.plugin.view.IPluginView

@AutoService(IPluginView::class)
class HexToolPluginView : IPluginView {
  override val view: @Composable () -> Unit
    get() = { App() }

  override val icon: @Composable () -> Painter
    get() = { loadSvgPainter(SVG.byteInputStream(), LocalDensity.current) }

  override val label: String
    get() = "Hex Tools"

  companion object {
    const val SVG =
        """<svg t="1699628779011" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="10793" width="128" height="128"><path d="M132.266667 597.333333h76.8a4.266667 4.266667 0 0 0 4.266666-4.266666v-76.8a4.266667 4.266667 0 0 0-4.266666-4.266667H132.266667a4.266667 4.266667 0 0 0-4.266667 4.266667v76.8a4.266667 4.266667 0 0 0 4.266667 4.266666z m366.848-402.944a111.530667 111.530667 0 0 0-45.226667-21.76 142.933333 142.933333 0 0 0-71.253333 1.152A130.56 130.56 0 0 0 305.92 234.666667c-19.797333 34.816-28.16 77.397333-28.16 139.178666 0 61.184 8.405333 102.4 28.16 137.941334a125.866667 125.866667 0 0 0 112.768 67.797333c48.64 0 90.581333-25.6 113.365333-67.84 19.2-34.773333 28.16-77.354667 28.16-135.552 0-61.696-11.178667-141.653333-61.098666-181.888z m-79.872 313.173334c-38.954667 0-59.306667-45.568-59.306667-134.4 0-88.149333 20.394667-133.76 58.794667-133.76 39.594667 0 59.306667 45.653333 59.306666 135.552 0 87.04-20.394667 132.565333-58.794666 132.565333z m335.232 71.978666c48.64 0 90.581333-25.6 113.365333-67.84 19.2-34.730667 28.16-77.312 28.16-135.509333 0-47.786667-3.84-99.114667-27.093333-141.909333-27.178667-50.176-82.944-75.392-139.093334-64.554667l-12.8 3.157333A128.64 128.64 0 0 0 641.706667 234.666667c-19.797333 34.773333-28.16 77.354667-28.16 139.093333 0 61.184 8.405333 102.4 28.16 137.941333a125.866667 125.866667 0 0 0 112.768 67.754667z m0-340.053333c39.594667 0 59.306667 45.653333 59.306666 135.552 0 86.954667-20.394667 132.565333-58.794666 132.565333-38.954667 0-59.306667-45.568-59.306667-134.4 0-88.192 20.394667-133.76 58.794667-133.76z m34.858666 476.16a4.266667 4.266667 0 0 1-4.266666 4.266667H597.333333v53.333333h187.733334a4.266667 4.266667 0 0 1 4.266666 4.266667V853.333333l103.68-103.68a4.266667 4.266667 0 0 0 0-6.016L789.333333 640v75.733333z" p-id="10794" fill="#aa0feb"></path></svg>"""
  }
}
