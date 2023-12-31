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

package com.kouqurong.iso8583.componet

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.SwipeableState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.kouqurong.iso8583.data.DisplayFieldItem
import com.kouqurong.iso8583.data.FieldItem
import com.kouqurong.iso8583.data.FieldMenuItem
import com.kouqurong.iso8583.viewmodel.IFieldItemIntent
import com.kouqurong.iso8583.viewmodel.IISO8583HexIntent
import com.kouqurong.plugin.view.defaultDashedBorder

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ISO8583Content(
    modifier: Modifier = Modifier,
    iso8583Hex: String,
    fieldItems: List<FieldItem>,
    fieldMenuItems: List<FieldMenuItem>,
    onMenuClick: (FieldMenuItem) -> Unit,
    displayFieldItems: List<DisplayFieldItem>,
    scrollFieldDetailState: LazyListState = rememberLazyListState(),
    swipeEnabled: Boolean = true,
    swipeCrossFadeState: SwipeableState<SwipeCrossFadeState>,
    onFieldItemIntent: (IFieldItemIntent) -> Unit,
    onISO8583HexIntent: (IISO8583HexIntent) -> Unit,
) {

  val clipboard = LocalClipboardManager.current

  Box(modifier = modifier, contentAlignment = Alignment.Center) {
    SwipeCrossFadeLayout(
        swipeEnabled = swipeEnabled,
        swipeState = swipeCrossFadeState,
        modifier = Modifier.fillMaxSize(),
        background = {
          Surface(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
              Crossfade(targetState = displayFieldItems.isNotEmpty()) {
                if (it) {
                  LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(displayFieldItems.size) {
                      DisplayFieldItemEditor(
                          modifier = Modifier.fillMaxWidth().padding(8.dp),
                          display = displayFieldItems[it],
                          onDisplayValueChange = {})
                    }
                  }
                  FloatingActionButton(
                      modifier = Modifier.padding(8.dp).align(Alignment.BottomEnd),
                      onClick = {
                        clipboard.setText(
                            buildAnnotatedString { append(displayFieldItems.joinToString("\n")) })
                      }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                      }
                } else {

                  ISO8583HexInput(
                      modifier = Modifier.fillMaxSize(),
                      iso8583Hex = iso8583Hex,
                      onISO8583HexIntent = onISO8583HexIntent,
                  )

                  FloatingActionButton(
                      modifier = Modifier.padding(8.dp).align(Alignment.BottomEnd),
                      onClick = {
                        if (iso8583Hex.isNotBlank()) {
                          onISO8583HexIntent(IISO8583HexIntent.Parsing)
                        } else {
                          onISO8583HexIntent(IISO8583HexIntent.Generate)
                        }
                      }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Forward")
                      }
                }
              }
            }
          }
        },
        foreground = {
          Surface(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxSize().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround) {
                  FieldDetailContent(
                      modifier = Modifier.width(500.dp).fillMaxHeight().padding(8.dp),
                      fieldItems = fieldItems,
                      scrollState = scrollFieldDetailState,
                      onFieldItemIntent = onFieldItemIntent,
                  )
                  FieldMenuContent(
                      modifier = Modifier.width(200.dp).fillMaxSize(),
                      fieldMenuItems = fieldMenuItems,
                      onMenuClick = onMenuClick,
                  )
                }
          }
        },
        indicate = { SwipeRefreshContent(swipeCrossFadeState.currentValue) })
  }
}

@Composable
fun FieldDetailContent(
    modifier: Modifier = Modifier,
    fieldItems: List<FieldItem>,
    scrollState: LazyListState = rememberLazyListState(),
    onFieldItemIntent: (IFieldItemIntent) -> Unit,
) {
  LazyColumn(
      modifier = modifier.defaultDashedBorder(),
      state = scrollState,
      verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    items(fieldItems.size, key = { it }) { i ->
      AnimationSizeFieldItem(
          modifier = Modifier.fillMaxWidth().padding(1.dp).height(52.dp),
          fieldItem = fieldItems[i],
          onFieldItemIntent = {
            // todo 优化该方式
            when (it) {
              is IFieldItemIntent.AlignChange -> onFieldItemIntent(it.copy(index = i))
              is IFieldItemIntent.AttrChange -> onFieldItemIntent(it.copy(index = i))
              is IFieldItemIntent.Delete -> onFieldItemIntent(it.copy(index = i))
              is IFieldItemIntent.FieldChange -> onFieldItemIntent(it.copy(index = i))
              is IFieldItemIntent.FormatChange -> onFieldItemIntent(it.copy(index = i))
              is IFieldItemIntent.LengthChange -> onFieldItemIntent(it.copy(index = i))
              is IFieldItemIntent.PaddingChange -> onFieldItemIntent(it.copy(index = i))
            }
          })
    }
  }
}

@Composable
fun FieldMenuContent(
    modifier: Modifier = Modifier,
    fieldMenuItems: List<FieldMenuItem>,
    onMenuClick: (FieldMenuItem) -> Unit
) {
  Column(
      modifier = modifier,
      horizontalAlignment = Alignment.End,
      verticalArrangement = Arrangement.SpaceAround) {
        fieldMenuItems.forEach {
          AnimationSizeButton(
              modifier = Modifier.size(160.dp, 52.dp), onClick = { onMenuClick(it) }) {
                Text(it.name)
              }
        }
      }
}

@Composable
fun ISO8583HexInput(
    modifier: Modifier = Modifier,
    iso8583Hex: String,
    onISO8583HexIntent: (IISO8583HexIntent) -> Unit,
) {
  Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
    OutlinedTextField(
        modifier = Modifier.weight(1F).fillMaxWidth(),
        value = iso8583Hex,
        onValueChange = { onISO8583HexIntent(IISO8583HexIntent.HexChange(it)) },
        placeholder = {
          if (iso8583Hex.isBlank()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
              Text(text = "Paste Here")
            }
          }
        })

    //    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    //      Button(
    //          modifier = Modifier.width(120.dp),
    //          onClick = { onISO8583HexIntent(IISO8583HexIntent.Parsing) }) {
    //            Text("Parsing")
    //          }
    //      Button(
    //          modifier = Modifier.width(120.dp),
    //          onClick = { onISO8583HexIntent(IISO8583HexIntent.Generate) }) {
    //            Text("Generate")
    //          }
    //    }
  }
}
