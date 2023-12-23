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

package com.kouqurong.plugin.adbtool

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadSvgPainter
import com.google.auto.service.AutoService
import com.kouqurong.plugin.view.IPluginView

@AutoService(IPluginView::class)
class PluginADBTool : IPluginView {
  override val view: @Composable () -> Unit
    get() = { App() }

  override val icon: @Composable () -> Painter
    get() = { loadSvgPainter(SVG.byteInputStream(), LocalDensity.current) }

  override val label: String
    get() = "ADB Tool"

  companion object {
    private const val SVG =
        """<svg t="1703169133746" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4232" width="64" height="64"><path d="M213.333333 682.666667c0 164.906667 133.76 298.666667 298.666667 298.666666s298.666667-133.76 298.666667-298.666666v-170.666667H213.333333v170.666667zM688 186.453333l89.6-89.6-35.2-35.2-98.346667 98.346667C603.946667 140.16 559.573333 128 512 128c-47.573333 0-91.946667 12.16-132.053333 32L281.6 61.653333l-35.2 35.2 89.6 89.6C261.973333 240.64 213.333333 327.68 213.333333 426.666667v42.666666h597.333334v-42.666666c0-98.986667-48.64-186.026667-122.666667-240.213334zM384 384a42.666667 42.666667 0 1 1-0.021333-85.312A42.666667 42.666667 0 0 1 384 384z m256 0a42.666667 42.666667 0 1 1-0.021333-85.312A42.666667 42.666667 0 0 1 640 384z" p-id="4233" fill="#aa0feb"></path></svg>"""
  }
}
