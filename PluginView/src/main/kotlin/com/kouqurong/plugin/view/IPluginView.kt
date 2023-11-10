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

package com.kouqurong.plugin.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

interface IPluginView {
  val view: @Composable () -> Unit
  val icon: @Composable () -> Painter
  val label: String

  /** @return true if the back event is consumed by the plugin view. */
  fun onBack(): Boolean = false

  /** @return if return null, will not display search bar */
  fun onSearch(): ((String) -> Unit)? = null
}

abstract class PluginView : IPluginView {
  val backDispatcher by lazy { BackDispatcher() }

  override fun onBack(): Boolean {
    return backDispatcher.dispatch()
  }
}

class BackDispatcher {
  private val backListeners = mutableListOf<() -> Boolean>()

  fun register(listener: () -> Boolean) {
    backListeners.add(listener)
  }

  fun unregister(listener: () -> Boolean) {
    backListeners.remove(listener)
  }

  fun dispatch(): Boolean {
    backListeners.forEach {
      if (it()) {
        return true
      }
    }
    return false
  }
}
