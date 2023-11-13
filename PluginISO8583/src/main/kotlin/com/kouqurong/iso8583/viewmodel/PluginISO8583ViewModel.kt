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
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigRenderOptions
import java.io.File
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.hocon.decodeFromConfig
import kotlinx.serialization.hocon.encodeToConfig

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

  data object Generate : IISO8583HexIntent
}

sealed interface IFieldMenuIntent {
  data object Add : IFieldMenuIntent

  data class Export(val file: File) : IFieldMenuIntent

  data class Import(val file: File) : IFieldMenuIntent

  data object Clear : IFieldMenuIntent

  data object Template : IFieldMenuIntent
}

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
          addDialogMessage("Error,not impl yes")
        }
      }
    }
  }

  @OptIn(ExperimentalSerializationApi::class)
  fun fieldMenuIntent(intent: IFieldMenuIntent) {
    when (intent) {
      IFieldMenuIntent.Add -> {
        addNewFieldItem()
      }
      IFieldMenuIntent.Clear -> {
        clearFieldItems()
      }
      is IFieldMenuIntent.Export -> {
        viewModelScope.launch(Dispatchers.IO) {
          val items =
              fieldItems.associate {
                it.field to
                    SerializeFieldItem(
                        attr = it.attr,
                        length = it.length,
                        format = it.format,
                        padding = it.padding,
                        align = it.align,
                    )
              }

          val content =
              Hocon.encodeToConfig(items)
                  .root()
                  .render(ConfigRenderOptions.defaults().setOriginComments(false))

          val comment = "$COMMENT\r\n$content"

          intent.file.writeText(comment)
        }
      }
      is IFieldMenuIntent.Import -> {
        viewModelScope.launch(
            Dispatchers.IO +
                CoroutineExceptionHandler { _, throwable ->
                  _dialogMessage.add(throwable.message)
                }) {
              val config = ConfigFactory.parseFile(intent.file)
              val items = Hocon.decodeFromConfig<Map<String, SerializeFieldItem>>(config)

              val fieldItems =
                  items
                      .map {
                        FieldItem(
                            field = it.key,
                            attr = it.value.attr,
                            format = it.value.format,
                            length = it.value.length,
                            align = it.value.align,
                            padding = it.value.padding,
                        )
                      }
                      .sortedBy { it.field.toInt() }

              withContext(Dispatchers.Main) {
                _fieldItems.clear()
                _fieldItems.addAll(fieldItems)
              }
            }
      }
      IFieldMenuIntent.Template -> {
        viewModelScope.launch(
            Dispatchers.IO +
                CoroutineExceptionHandler { _, throwable ->
                  _dialogMessage.add(throwable.message)
                }) {
              val config = ConfigFactory.parseString(TEMPLATE)
              val items = Hocon.decodeFromConfig<Map<String, SerializeFieldItem>>(config)

              val fieldItems =
                  items
                      .map {
                        FieldItem(
                            field = it.key,
                            attr = it.value.attr,
                            format = it.value.format,
                            length = it.value.length,
                            align = it.value.align,
                            padding = it.value.padding,
                        )
                      }
                      .sortedBy { it.field.toInt() }

              withContext(Dispatchers.Main) {
                _fieldItems.clear()
                _fieldItems.addAll(fieldItems)
              }
            }
      }
    }
  }

  fun removeDialogMessage() {
    viewModelScope.launch { _dialogMessage.removeAt(0) }
  }

  fun addDialogMessage(msg: String) {
    _dialogMessage.add(msg)
  }

  fun clearDisplayFieldItems() {
    _displayFieldItems.clear()
  }
}

private const val COMMENT =
    """
# Generated by ISO8583 Plugin
# attr = ASCII | BCD | BINARY
# format = FIX | VAR , if length <= 99, length is one byte, else length is two bytes
# length = 0..9999
# align = L | R, decide how to padding, if content length < length , L is padding left, R is padding right
# padding  if content length < length, padding content to length
"""

private const val TEMPLATE =
    """
{
    "2" : {
        "align" : "L",
        "attr" : "BCD",
        "format" : "VAR",
        "length" : "19",
        "padding" : "0"
    },
    "3" : {
        "align" : "L",
        "attr" : "BCD",
        "format" : "FIX",
        "length" : "6",
        "padding" : "0"
    },
    "4" : {
        "align" : "L",
        "attr" : "BCD",
        "format" : "FIX",
        "length" : "12",
        "padding" : "0"
    },
    "11" : {
        "align" : "L",
        "attr" : "BCD",
        "format" : "FIX",
        "length" : "6",
        "padding" : "0"
    },
    "12" : {
        "align" : "L",
        "attr" : "BCD",
        "format" : "FIX",
        "length" : "6",
        "padding" : "0"
    },
    "13" : {
        "align" : "L",
        "attr" : "BCD",
        "format" : "FIX",
        "length" : "4",
        "padding" : "0"
    },
    "14" : {
        "align" : "L",
        "attr" : "BCD",
        "format" : "FIX",
        "length" : "4",
        "padding" : "0"
    },
    "15" : {
        "align" : "L",
        "attr" : "BCD",
        "format" : "FIX",
        "length" : "4",
        "padding" : "0"
    },
    "22" : {
        "align" : "L",
        "attr" : "BCD",
        "format" : "FIX",
        "length" : "3",
        "padding" : "0"
    },
    "23" : {
        "align" : "L",
        "attr" : "BCD",
        "format" : "FIX",
        "length" : "3",
        "padding" : "0"
    },
    "24" : {
        "align" : "L",
        "attr" : "BCD",
        "format" : "FIX",
        "length" : "3",
        "padding" : "0"
    },
    "25" : {
        "align" : "L",
        "attr" : "BCD",
        "format" : "FIX",
        "length" : "2",
        "padding" : "0"
    },
    "26" : {
        "align" : "L",
        "attr" : "BCD",
        "format" : "FIX",
        "length" : "2",
        "padding" : "0"
    },
    "32" : {
        "align" : "L",
        "attr" : "BCD",
        "format" : "VAR",
        "length" : "11",
        "padding" : "0"
    },
    "35" : {
        "align" : "L",
        "attr" : "BCD",
        "format" : "VAR",
        "length" : "99",
        "padding" : "0"
    },
    "36" : {
        "align" : "R",
        "attr" : "BCD",
        "format" : "VAR",
        "length" : "999",
        "padding" : "0"
    },
    "37" : {
        "align" : "L",
        "attr" : "ASCII",
        "format" : "FIX",
        "length" : "12",
        "padding" : "0"
    },
    "38" : {
        "align" : "L",
        "attr" : "ASCII",
        "format" : "FIX",
        "length" : "6",
        "padding" : "0"
    },
    "39" : {
        "align" : "L",
        "attr" : "ASCII",
        "format" : "FIX",
        "length" : "2",
        "padding" : "0"
    },
    "41" : {
        "align" : "L",
        "attr" : "ASCII",
        "format" : "FIX",
        "length" : "8",
        "padding" : "0"
    },
    "42" : {
        "align" : "L",
        "attr" : "ASCII",
        "format" : "FIX",
        "length" : "15",
        "padding" : "0"
    },
    "44" : {
        "align" : "L",
        "attr" : "ASCII",
        "format" : "VAR",
        "length" : "25",
        "padding" : "0"
    },
    "45" : {
        "align" : "L",
        "attr" : "ASCII",
        "format" : "VAR",
        "length" : "99",
        "padding" : "0"
    },
    "48" : {
        "align" : "L",
        "attr" : "ASCII",
        "format" : "VAR",
        "length" : "999",
        "padding" : "0"
    },
    "49" : {
        "align" : "L",
        "attr" : "BCD",
        "format" : "FIX",
        "length" : "3",
        "padding" : "0"
    },
    "50" : {
        "align" : "L",
        "attr" : "BCD",
        "format" : "FIX",
        "length" : "3",
        "padding" : "0"
    },
    "51" : {
        "align" : "L",
        "attr" : "BCD",
        "format" : "FIX",
        "length" : "3",
        "padding" : "0"
    },
    "52" : {
        "align" : "L",
        "attr" : "BINARY",
        "format" : "FIX",
        "length" : "64",
        "padding" : "0"
    },
    "53" : {
        "align" : "L",
        "attr" : "BCD",
        "format" : "FIX",
        "length" : "16",
        "padding" : "0"
    },
    "54" : {
        "align" : "L",
        "attr" : "ASCII",
        "format" : "VAR",
        "length" : "120",
        "padding" : "0"
    },
    "55" : {
        "align" : "L",
        "attr" : "ASCII",
        "format" : "VAR",
        "length" : "999",
        "padding" : "0"
    },
    "56" : {
        "align" : "L",
        "attr" : "ASCII",
        "format" : "VAR",
        "length" : "999",
        "padding" : "0"
    },
    "57" : {
        "align" : "L",
        "attr" : "ASCII",
        "format" : "VAR",
        "length" : "999",
        "padding" : "0"
    },
    "60" : {
        "align" : "L",
        "attr" : "ASCII",
        "format" : "VAR",
        "length" : "999",
        "padding" : "0"
    },
    "61" : {
        "align" : "L",
        "attr" : "ASCII",
        "format" : "VAR",
        "length" : "999",
        "padding" : "0"
    },
    "62" : {
        "align" : "L",
        "attr" : "ASCII",
        "format" : "VAR",
        "length" : "999",
        "padding" : "0"
    },
    "63" : {
        "align" : "L",
        "attr" : "ASCII",
        "format" : "VAR",
        "length" : "999",
        "padding" : "0"
    },
    "64" : {
        "align" : "L",
        "attr" : "ASCII",
        "format" : "FIX",
        "length" : "64",
        "padding" : "0"
    }
}

"""
