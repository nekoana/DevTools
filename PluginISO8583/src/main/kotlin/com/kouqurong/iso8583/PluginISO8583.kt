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

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadSvgPainter
import com.google.auto.service.AutoService
import com.kouqurong.plugin.view.IPluginView
import com.kouqurong.plugin.view.PluginView

@AutoService(IPluginView::class)
class PluginISO8583 : PluginView() {
  override val view: @Composable () -> Unit
    get() = { App(backDispatcher) }

  override val icon: @Composable () -> Painter
    get() = { loadSvgPainter(SVG.byteInputStream(), LocalDensity.current) }

  override val label: String
    get() = "ISO8583"

  companion object {
    const val SVG =
        """<svg t="1699627903578" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4085" width="128" height="128"><path d="M704 921.6H211.2c-83.2 0-147.2-64-147.2-147.2V275.2C64 192 128 128 211.2 128H704c83.2 0 147.2 64 147.2 147.2 0 25.6-12.8 32-38.4 32s-38.4-12.8-38.4-32c0-38.4-32-70.4-70.4-70.4H211.2c-38.4 0-70.4 32-70.4 70.4v499.2c0 38.4 32 70.4 70.4 70.4H704c38.4 0 70.4-32 70.4-70.4 0-19.2 19.2-38.4 38.4-38.4s38.4 19.2 38.4 38.4c0 76.8-64 147.2-147.2 147.2z m-12.8-556.8h192c19.2 0 32 12.8 32 32s-12.8 32-32 32h-192c-19.2 0-32-12.8-32-32s12.8-32 32-32z m0 128h192c19.2 0 32 12.8 32 32s-12.8 32-32 32h-192c-19.2 0-32-12.8-32-32s12.8-32 32-32z m0 128h192c19.2 0 32 12.8 32 32s-12.8 32-32 32h-192c-19.2 0-32-12.8-32-32s12.8-32 32-32z m-320-326.4c44.8 0 83.2 12.8 115.2 38.4 25.6 25.6 38.4 51.2 38.4 83.2 0 25.6-6.4 44.8-19.2 64-12.8 19.2-32 32-51.2 38.4 25.6 6.4 44.8 19.2 64 38.4 19.2 19.2 25.6 44.8 25.6 76.8 0 38.4-12.8 70.4-44.8 96-32 25.6-70.4 38.4-121.6 38.4-51.2 0-96-12.8-121.6-38.4-25.6-25.6-44.8-57.6-44.8-96 0-32 6.4-57.6 25.6-76.8 19.2-19.2 38.4-32 64-38.4-25.6-6.4-44.8-25.6-57.6-38.4-12.8-19.2-19.2-38.4-19.2-64 0-32 12.8-64 38.4-83.2 25.6-25.6 64-38.4 108.8-38.4z m0 57.6c-32 0-51.2 6.4-70.4 19.2-12.8 12.8-19.2 25.6-19.2 44.8 0 19.2 6.4 38.4 19.2 44.8 12.8 12.8 38.4 19.2 70.4 19.2 32 0 57.6-6.4 70.4-19.2 12.8-12.8 19.2-25.6 19.2-44.8 0-19.2-6.4-32-19.2-44.8-12.8-12.8-38.4-19.2-70.4-19.2z m6.4 204.8c-32 0-57.6 6.4-76.8 25.6-19.2 12.8-25.6 32-25.6 57.6s6.4 38.4 25.6 57.6c19.2 12.8 44.8 19.2 76.8 19.2s57.6-6.4 76.8-25.6c19.2-12.8 25.6-32 25.6-57.6s-6.4-44.8-25.6-57.6c-19.2-12.8-44.8-19.2-76.8-19.2z" fill="#d4237a" p-id="4086"></path></svg>"""
  }
}
