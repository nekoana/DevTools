package com.kouqurong.devtools.viewmodel/*
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

import com.kouqurong.devtools.utils.PathClassLoader
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import com.kouqurong.plugin.view.IPluginView
import com.kouqurong.plugin.view.ViewModel
import java.util.*

class HostViewModel : ViewModel() {
  val pluginViewWindowState = mutableStateListOf<PluginViewWindowState>()

  val pluginViews = mutableStateListOf<IPluginView>()

  fun openNewPluginViewWindow(pluginView: IPluginView) {
    pluginViewWindowState.add(PluginViewWindowState(pluginView) { closePluginViewWindow(it) })
  }

  private fun closePluginViewWindow(state: PluginViewWindowState) {
    pluginViewWindowState.removeIf(state::equals)
  }

  fun loadThirdPluginView(paths: Array<String>) {
    val classLoader = PathClassLoader(*paths)
    ServiceLoader.load(IPluginView::class.java, classLoader).toCollection(pluginViews)
  }

  fun loadSelfPluginView() {
    ServiceLoader.load(IPluginView::class.java).toCollection(pluginViews)
  }

  fun loadTestPluginView() {
    val paths =
        arrayOf(
            "/Users/codin/MyCode/DevTools/PluginHello/build/libs/PluginHello.jar",
            "/Users/codin/MyCode/DevTools/PluginHex/build/libs/PluginHex.jar",
            "/Users/codin/MyCode/DevTools/PluginTcpClient/build/libs/PluginTcpClient.jar",
            "/Users/codin/MyCode/DevTools/PluginTcpServer/build/libs/PluginTcpServer.jar")

    loadThirdPluginView(paths)
  }

  fun exit() {
    pluginViewWindowState.clear()
  }
}

@Stable
data class PluginViewWindowState(
    val pluginView: IPluginView,
    val close: (PluginViewWindowState) -> Unit
) {
  fun close() = close(this)
}
