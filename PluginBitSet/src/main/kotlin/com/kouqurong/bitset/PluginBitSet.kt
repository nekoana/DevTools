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

package com.kouqurong.bitset

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadSvgPainter
import com.google.auto.service.AutoService
import com.kouqurong.plugin.view.IPluginView

@AutoService(IPluginView::class)
class PluginBitSet : IPluginView {
  override val view: @Composable () -> Unit
    get() = { App() }

  override val icon: @Composable () -> Painter
    get() = { loadSvgPainter(SVG.byteInputStream(), LocalDensity.current) }

  override val label: String
    get() = "BitSet"

  companion object {
    const val SVG =
        """<svg t="1699628160903" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="7628" data-spm-anchor-id="a313x.search_index.0.i5.6b183a81uocmw0" width="128" height="128"><path d="M512 1024C229.248 1024 0 794.752 0 512S229.248 0 512 0s512 229.248 512 512-229.248 512-512 512z m-48.810667-466.346667c0-55.125333-32.554667-86.144-79.274666-86.144-14.549333 0-27.178667 3.072-36.778667 9.557334V407.552c0-13.781333-8.405333-20.693333-25.642667-20.693333s-25.642667 6.912-25.642666 20.693333v193.408c0 13.013333 0 25.642667 17.621333 35.626667 15.317333 8.789333 35.242667 13.013333 56.661333 13.013333 59.733333 0 93.056-36.010667 93.056-91.904z m-116.053333 44.8v-81.962666a39.381333 39.381333 0 0 1 24.106667-7.68c23.722667 0 39.424 17.621333 39.424 48.256 0 30.250667-15.317333 47.104-40.192 47.104-8.789333 0-16.853333-1.92-23.381334-5.76z m211.968-181.504c0-17.621333-12.629333-29.482667-30.250667-29.482666s-30.250667 11.861333-30.250666 29.482666c0 17.621333 12.629333 29.866667 30.250666 29.866667s30.250667-12.245333 30.250667-29.866667z m-4.608 71.210667c0-13.781333-8.405333-20.693333-25.642667-20.693333s-25.642667 6.912-25.642666 20.693333v135.936c0 13.781333 8.405333 20.693333 25.642666 20.693333s25.642667-6.912 25.642667-20.693333v-135.936z m155.306667 110.677333c-3.072 0-5.76 0.768-8.789334 1.536-3.456 0.768-7.68 1.92-11.861333 1.92-6.528 0-11.477333-2.304-14.165333-7.68-3.456-6.528-3.456-16.469333-3.456-23.381333v-57.429333h32.170666c13.781333 0 20.693333-6.912 20.693334-21.461334s-6.912-21.461333-20.693334-21.461333h-32.170666v-33.706667c0-13.781333-8.405333-20.693333-25.642667-20.693333s-25.642667 6.912-25.642667 20.693333v33.706667h-13.397333c-13.781333 0-20.693333 6.912-20.693333 21.461333s6.912 21.461333 20.693333 21.461334h13.397333v57.429333c0 17.621333 0.768 34.090667 8.405334 47.872 8.405333 15.317333 24.874667 26.410667 52.48 26.410667 14.933333 0 29.482667-3.456 37.930666-8.405334 7.296-4.224 9.173333-9.557333 9.173334-14.933333 0-8.405333-4.224-23.381333-18.389334-23.381333z" p-id="7629" fill="#d4237a" data-spm-anchor-id="a313x.search_index.0.i6.6b183a81uocmw0" class=""></path></svg>"""
  }
}
