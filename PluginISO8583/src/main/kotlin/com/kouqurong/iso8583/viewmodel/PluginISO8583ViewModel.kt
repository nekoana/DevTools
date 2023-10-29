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
import androidx.compose.runtime.mutableStateOf
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
    val field: String,
    val attr: IAttr = IAttr.ASCII,
    val format: IFormat = IFormat.FIX,
    val align: IAlign = IAlign.LEFT,
    val length: String = "0",
    val padding: String = "0",
)

sealed interface IFieldItemIntent {
  data class Delete(val index: Int) : IFieldItemIntent

  data class AttrChange(val index: Int, val attr: IAttr) : IFieldItemIntent
  data class FormatChange(val index: Int, val format: IFormat) : IFieldItemIntent
  data class AlignChange(val index: Int, val align: IAlign) : IFieldItemIntent
  data class LengthChange(val index: Int, val length: String) : IFieldItemIntent
  data class PaddingChange(val index: Int, val padding: String) : IFieldItemIntent
  data class FieldChange(val index: Int, val field: String) : IFieldItemIntent
}

sealed interface IISO8583HexIntent {
  data class HexChange(val hex: String) : IISO8583HexIntent
  object Parsing : IISO8583HexIntent
  object Generate : IISO8583HexIntent
}

sealed interface IFieldMenuIntent {
  object Add : IFieldMenuIntent
  object Export : IFieldMenuIntent
  object Import : IFieldMenuIntent
  object Clear : IFieldMenuIntent
  object Template : IFieldMenuIntent
}

data class FieldMenuItem(val text: String, val onClick: () -> Unit)

class PluginISO8583ViewModel : ViewModel() {
  private val _fieldItems = mutableStateListOf<FieldItem>()
  private val _iso8583Hex = mutableStateOf("")

  val iso8583Hex: String
    get() = _iso8583Hex.value

  val fieldItems: List<FieldItem>
    get() = _fieldItems

  val fieldMenuItems =
      listOf(
          FieldMenuItem("添加") { fieldMenuIntent(IFieldMenuIntent.Add) },
          FieldMenuItem("导出") {},
          FieldMenuItem("导入") {},
          FieldMenuItem("清空") { fieldMenuIntent(IFieldMenuIntent.Clear) },
          FieldMenuItem("模版") {},
      )

  @OptIn(ExperimentalMaterialApi::class)
  val swipeCrossFadeState = SwipeableState(SwipeCrossFadeState.FORE)

  val scrollFieldDetailState = LazyListState()

  private fun addNewFieldItem() {
    _fieldItems.add(FieldItem(field = (_fieldItems.size + 1).toString()))

    viewModelScope.launch { scrollFieldDetailState.scrollToItem(_fieldItems.size - 1) }
  }

  private fun clearFieldItems() {
    _fieldItems.clear()
  }

  fun fieldItemIntent(intent: IFieldItemIntent) {
    when (intent) {
      is IFieldItemIntent.Delete -> {
        _fieldItems.removeAt(intent.index)
      }
      is IFieldItemIntent.AlignChange -> {
        _fieldItems[intent.index] = _fieldItems[intent.index].copy(align = intent.align)
      }
      is IFieldItemIntent.AttrChange -> {
        _fieldItems[intent.index] = _fieldItems[intent.index].copy(attr = intent.attr)
      }
      is IFieldItemIntent.FieldChange -> {
        _fieldItems[intent.index] =
            _fieldItems[intent.index].copy(field = intent.field.toIntOrNull()?.toString() ?: "")
      }
      is IFieldItemIntent.FormatChange -> {
        _fieldItems[intent.index] = _fieldItems[intent.index].copy(format = intent.format)
      }
      is IFieldItemIntent.LengthChange -> {
        _fieldItems[intent.index] =
            _fieldItems[intent.index].copy(length = intent.length.toIntOrNull()?.toString() ?: "")
      }
      is IFieldItemIntent.PaddingChange -> {
        _fieldItems[intent.index] = _fieldItems[intent.index].copy(padding = intent.padding)
      }
    }
  }

  fun iso8583HexIntent(intent: IISO8583HexIntent) {
    when (intent) {
      is IISO8583HexIntent.HexChange -> {
        _iso8583Hex.value = intent.hex
      }
      is IISO8583HexIntent.Parsing -> {
        // todo
      }
      is IISO8583HexIntent.Generate -> {
        // todo
      }
    }
  }

  fun fieldMenuIntent(intent: IFieldMenuIntent) {
    when (intent) {
      IFieldMenuIntent.Add -> {
        addNewFieldItem()
      }
      IFieldMenuIntent.Clear -> {
        clearFieldItems()
      }
      IFieldMenuIntent.Export -> TODO()
      IFieldMenuIntent.Import -> TODO()
      IFieldMenuIntent.Template -> TODO()
    }
  }
}
