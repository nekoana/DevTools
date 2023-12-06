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

package com.kouqurong.devtools.viewmodel

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import com.kouqurong.devtools.utils.PathClassLoader
import com.kouqurong.plugin.view.IPluginView
import com.kouqurong.plugin.view.ViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import java.util.*

class HostViewModel : ViewModel() {
  private val _pluginViews = mutableStateOf(emptyList<IPluginView>())
  private val _searchKeyword = mutableStateOf("")

  val pluginViewWindowState = mutableStateListOf<PluginViewWindowState>()

  val pluginViews = snapshotFlow { _pluginViews.value }
    .distinctUntilChanged()
    .combine(snapshotFlow { _searchKeyword.value }.distinctUntilChanged()) { pluginViews, searchKeyword ->
      if (searchKeyword.isEmpty()) return@combine pluginViews
      pluginViews.filter { it.label.contains(searchKeyword, true) }
    }
    .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

  fun openNewPluginViewWindow(pluginView: IPluginView) {
    pluginViewWindowState.add(PluginViewWindowState(pluginView) { closePluginViewWindow(it) })
  }

  private fun closePluginViewWindow(state: PluginViewWindowState) {
    pluginViewWindowState.removeIf(state::equals)
  }

  fun loadThirdPluginView(paths: Array<String>) {
    val classLoader = PathClassLoader(*paths)
    loadPluginView(classLoader)
  }

  fun loadSelfPluginView() {
    loadPluginView(javaClass.classLoader)
  }

  private fun loadPluginView(classLoader: ClassLoader) {
    val pluginViews = ServiceLoader.load(IPluginView::class.java, classLoader).toList()
    _pluginViews.value = pluginViews
  }

  fun search(keyword: String) {
    _searchKeyword.value = keyword
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
