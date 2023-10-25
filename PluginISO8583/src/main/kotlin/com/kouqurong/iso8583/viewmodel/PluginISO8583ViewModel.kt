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

package com.kouqurong.iso8583.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.mutableStateListOf
import com.kouqurong.iso8583.componet.SwipeCrossFadeState
import com.kouqurong.plugin.view.ViewModel
import kotlinx.coroutines.launch

sealed class IAttr(val value: String) {
  data object ASCII : IAttr("ASCII")
  data object BCD : IAttr("BCD")
  data object BINARY : IAttr("BINARY")
}

val AttrList = listOf(IAttr.ASCII, IAttr.BCD, IAttr.BINARY)

sealed class IFormat(val value: String) {
  data object VAR : IFormat("VAR")
  data object FIX : IFormat("FIX")
}

val FormatList = listOf(IFormat.VAR, IFormat.FIX)

sealed class IAlign(val value: String) {
  data object LEFT : IAlign("LEFT")
  data object RIGHT : IAlign("RIGHT")
}

val AlignList = listOf(IAlign.LEFT, IAlign.RIGHT)

data class FieldItem(
    val field: Int,
    val attr: IAttr = IAttr.ASCII,
    val format: IFormat = IFormat.FIX,
    val align: IAlign = IAlign.LEFT,
    val length: String = "0",
    val padding: String = "0",
    val value: String = "",
)

data class FieldMenuItem(val text: String, val onClick: () -> Unit)

class PluginISO8583ViewModel : ViewModel() {
  private val _fieldItems = mutableStateListOf<FieldItem>()

  val fieldItems: List<FieldItem>
    get() = _fieldItems

  val fieldMenuItems =
      listOf(
          FieldMenuItem("添加") { addNewFieldItem() },
          FieldMenuItem("导出") {},
          FieldMenuItem("导入") {},
          FieldMenuItem("清空") { clearFieldItems() },
          FieldMenuItem("模版") {},
      )

  @OptIn(ExperimentalMaterialApi::class)
  val swipeCrossFadeState = SwipeableState(SwipeCrossFadeState.FORE)

  val scrollFieldDetailState = LazyListState()

  private fun addNewFieldItem() {
    _fieldItems.add(FieldItem(field = _fieldItems.size + 1))

    viewModelScope.launch { scrollFieldDetailState.scrollToItem(_fieldItems.size - 1) }
  }

  private fun clearFieldItems() {
    _fieldItems.clear()
  }
}
