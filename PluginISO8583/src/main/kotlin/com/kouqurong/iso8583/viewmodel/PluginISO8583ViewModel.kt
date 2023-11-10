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
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.kouqurong.iso8583.componet.SwipeCrossFadeState
import com.kouqurong.iso8583.data.*
import com.kouqurong.iso8583.util.parseISO8583HexString
import com.kouqurong.plugin.view.ViewModel
import kotlinx.coroutines.launch

sealed interface IFieldItemIntent {
  data class Delete(val index: Int) : IFieldItemIntent

  data class AttrChange(val index: Int, val attr: Attr) : IFieldItemIntent

  data class FormatChange(val index: Int, val format: Format) : IFieldItemIntent

  data class AlignChange(val index: Int, val align: Align) : IFieldItemIntent

  data class LengthChange(val index: Int, val length: String) : IFieldItemIntent

  data class PaddingChange(val index: Int, val padding: String) : IFieldItemIntent

  data class FieldChange(val index: Int, val field: String) : IFieldItemIntent
}

sealed interface IISO8583HexIntent {
  data class HexChange(val hex: String) : IISO8583HexIntent

  data object Parsing : IISO8583HexIntent

  object Generate : IISO8583HexIntent
}

sealed interface IFieldMenuIntent {
  data object Add : IFieldMenuIntent

  data object Export : IFieldMenuIntent

  data object Import : IFieldMenuIntent

  data object Clear : IFieldMenuIntent

  data object Template : IFieldMenuIntent
}

data class FieldMenuItem(val text: String, val onClick: () -> Unit)

class PluginISO8583ViewModel : ViewModel() {
  private val _fieldItems = mutableStateListOf<FieldItem>()
  private val _iso8583Hex = mutableStateOf("")
  private val _dialogMessage = mutableStateListOf<String?>()
  private val _displayFieldItems = mutableStateListOf<DisplayFieldItem>()

  val dialogMessage: State<String?> = derivedStateOf { _dialogMessage.firstOrNull() }

  val displayFieldItems: List<DisplayFieldItem>
    get() = _displayFieldItems

  val iso8583Hex: String
    get() = _iso8583Hex.value

  val fieldItems: List<FieldItem>
    get() = _fieldItems

  val fieldMenuItems =
      listOf(
          FieldMenuItem("添加") { fieldMenuIntent(IFieldMenuIntent.Add) },
          FieldMenuItem("导出") { fieldMenuIntent(IFieldMenuIntent.Export) },
          FieldMenuItem("导入") { fieldMenuIntent(IFieldMenuIntent.Import) },
          FieldMenuItem("清空") { fieldMenuIntent(IFieldMenuIntent.Clear) },
          FieldMenuItem("模版") { fieldMenuIntent(IFieldMenuIntent.Template) },
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
        if (iso8583Hex.isBlank()) {
          addDialogMessage("Error, empty input")
        } else {
          try {
            val displayFieldItems = parseISO8583HexString(iso8583Hex, fieldItems)
            _displayFieldItems.clear()
            _displayFieldItems.addAll(
                displayFieldItems.map { DisplayFieldItem(it.key.toString(), it.value) })
          } catch (e: Throwable) {
            addDialogMessage("Error, ${e.message}")
          }
        }
      }
      is IISO8583HexIntent.Generate -> {
        if (fieldItems.isEmpty()) {
          addDialogMessage("Error,empty field")
        } else {
          TODO()
        }
      }
    }
  }

  private fun fieldMenuIntent(intent: IFieldMenuIntent) {
    when (intent) {
      IFieldMenuIntent.Add -> {
        addNewFieldItem()
      }
      IFieldMenuIntent.Clear -> {
        clearFieldItems()
      }
      IFieldMenuIntent.Export -> {
        _dialogMessage.add("Error,Not Impl")
      }
      IFieldMenuIntent.Import -> {
        _dialogMessage.add("Error,Not Impl")
      }
      IFieldMenuIntent.Template -> {
        _dialogMessage.add("Error,Not Impl")
      }
    }
  }

  fun removeDialogMessage() {
    viewModelScope.launch { _dialogMessage.removeAt(0) }
  }

  private fun addDialogMessage(msg: String) {
    _dialogMessage.add(msg)
  }

  fun clearDisplayFieldItems() {
    _displayFieldItems.clear()
  }
}
