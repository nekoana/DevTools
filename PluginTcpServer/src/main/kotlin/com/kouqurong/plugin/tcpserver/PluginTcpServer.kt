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

package com.kouqurong.plugin.tcpserver

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadSvgPainter
import com.google.auto.service.AutoService
import com.kouqurong.plugin.view.IPluginView

@AutoService(IPluginView::class)
class PluginTcpServer : IPluginView {

  override val view: @Composable () -> Unit
    get() = { App() }

  override val icon: @Composable () -> Painter
    get() = { loadSvgPainter(SVG.byteInputStream(), LocalDensity.current) }

  override val label: String
    get() = "Tcp Server"

  companion object {
    const val SVG =
        """<svg t="1699628385019" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="11941" width="128" height="128"><path d="M947.52 384H76.48a32 32 0 0 1-32-32V76.16a32 32 0 0 1 32-32h871.04a32 32 0 0 1 32 32V352a32 32 0 0 1-32 32zM108.48 320h807.04V108.16H108.48z" p-id="11942" fill="#d81e06"></path><path d="M816.96 267.2a32 32 0 0 1-32-32V192a32 32 0 0 1 64 0v43.2a32 32 0 0 1-32 32zM947.52 781.12H76.48a32 32 0 0 1-32-32v-274.24a32 32 0 0 1 32-32h871.04a32 32 0 0 1 32 32v274.24a32 32 0 0 1-32 32z m-839.04-64h807.04v-210.24H108.48z" p-id="11943" fill="#d81e06"></path><path d="M816.96 665.92a32 32 0 0 1-32-32v-43.84a32 32 0 1 1 64 0v43.84a32 32 0 0 1-32 32zM512 943.04a32 32 0 0 1-32-32V768a32 32 0 0 1 64 0v142.72a32 32 0 0 1-32 32.32z" p-id="11944" fill="#d81e06"></path><path d="M909.44 980.48H114.56a32 32 0 1 1 0-64h794.88a32 32 0 0 1 0 64z" p-id="11945" fill="#d81e06"></path></svg>"""
  }
}
