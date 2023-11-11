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

package com.kouqurong.plugin.tcpclient

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadSvgPainter
import com.google.auto.service.AutoService
import com.kouqurong.plugin.view.IPluginView

@AutoService(IPluginView::class)
class PluginTcpClient : IPluginView {
  override val view: @Composable () -> Unit
    get() = { App() }

  override val icon: @Composable () -> Painter
    get() = { loadSvgPainter(SVG.byteInputStream(), LocalDensity.current) }

  override val label: String
    get() = "Tcp Client"

  companion object {
    const val SVG =
        """<svg t="1699628506470" class="icon" viewBox="0 0 1220 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="15717" data-spm-anchor-id="a313x.search_index.0.i14.6b183a81uocmw0" width="128" height="128"><path d="M823.532308 1024H397.390769a19.692308 19.692308 0 0 1 0-39.384615h426.141539a19.692308 19.692308 0 0 1 0 39.384615zM1122.461538 905.846154h-1024A98.461538 98.461538 0 0 1 0 807.384615v-708.923077A98.461538 98.461538 0 0 1 98.461538 0h1024A98.461538 98.461538 0 0 1 1220.923077 98.461538v708.923077a98.461538 98.461538 0 0 1-98.461539 98.461539zM98.461538 39.384615A59.076923 59.076923 0 0 0 39.384615 98.461538v708.923077A59.076923 59.076923 0 0 0 98.461538 866.461538h1024a59.076923 59.076923 0 0 0 59.076924-59.076923v-708.923077A59.076923 59.076923 0 0 0 1122.461538 39.384615z" p-id="15718" fill="#1296db"></path></svg>"""
  }
}
