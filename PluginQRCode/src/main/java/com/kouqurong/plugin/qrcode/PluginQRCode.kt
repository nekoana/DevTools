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

package com.kouqurong.plugin.qrcode

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadSvgPainter
import com.google.auto.service.AutoService
import com.kouqurong.plugin.view.IPluginView

@AutoService(IPluginView::class)
class PluginQRCode : IPluginView {
  override val view: @Composable () -> Unit
    get() = { App() }

  override val icon: @Composable () -> Painter
    get() = { loadSvgPainter(SVG.byteInputStream(), LocalDensity.current) }

  override val label: String
    get() = "QR Code"

  companion object {
    private const val SVG =
        """<svg t="1701694835635" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4250" width="128" height="128"><path d="M64 448h384V64H64v384zM192 192h128v128H192V192z m384-128v384h384V64H576z m256 256h-128V192h128v128zM64 960h384V576H64v384z m128-256h128v128H192v-128z m704-128h64v256h-192v-64h-64v192h-128V576h192v64h128v-64z m0 320h64v64h-64v-64z m-128 0h64v64h-64v-64z" p-id="4251" fill="#319f38"></path></svg>"""
  }
}
