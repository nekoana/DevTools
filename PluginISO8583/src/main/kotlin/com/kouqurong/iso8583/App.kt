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

package com.kouqurong.iso8583

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kouqurong.iso8583.componet.*
import com.kouqurong.iso8583.viewmodel.FieldItem
import com.kouqurong.iso8583.viewmodel.FieldMenuItem
import com.kouqurong.iso8583.viewmodel.IFieldItemIntent
import com.kouqurong.iso8583.viewmodel.PluginISO8583ViewModel
import com.kouqurong.plugin.view.defaultDashedBorder

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun App(viewModel: PluginISO8583ViewModel) {
  Surface(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    ISO8583Content(
        modifier = Modifier.fillMaxSize(),
        fieldItems = viewModel.fieldItems,
        fieldMenuItems = viewModel.fieldMenuItems,
        scrollFieldDetailState = viewModel.scrollFieldDetailState,
        swipeCrossFadeState = viewModel.swipeCrossFadeState,
        onFieldItemIntent = viewModel::makeFieldItemIntent,
    )
  }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ISO8583Content(
    modifier: Modifier = Modifier,
    fieldItems: List<FieldItem>,
    fieldMenuItems: List<FieldMenuItem>,
    scrollFieldDetailState: LazyListState = rememberLazyListState(),
    swipeCrossFadeState: SwipeableState<SwipeCrossFadeState>,
    onFieldItemIntent: (IFieldItemIntent) -> Unit,
) {

  Box(modifier = modifier, contentAlignment = Alignment.Center) {
    SwipeCrossFadeLayout(
        swipeState = swipeCrossFadeState,
        modifier = Modifier.fillMaxSize(),
        background = {
          Surface(modifier = Modifier.fillMaxSize()) {
            ISO8583HexInput(modifier = Modifier.fillMaxSize(), hex = "", onHexInputChange = {})
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
                      fieldMenuItems = fieldMenuItems)
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
fun FieldMenuContent(modifier: Modifier = Modifier, fieldMenuItems: List<FieldMenuItem>) {
  Column(
      modifier = modifier,
      horizontalAlignment = Alignment.End,
      verticalArrangement = Arrangement.SpaceAround) {
        fieldMenuItems.forEach {
          AnimationSizeButton(modifier = Modifier.size(160.dp, 52.dp), onClick = it.onClick) {
            Text(it.text)
          }
        }
      }
}

@Composable
fun ISO8583HexInput(
    modifier: Modifier = Modifier,
    hex: String,
    onHexInputChange: (String) -> Unit,
) {
  Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
    OutlinedTextField(
        modifier = Modifier.weight(1F).fillMaxWidth(),
        value = hex,
        onValueChange = onHexInputChange,
        placeholder = {
          if (hex.isBlank()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
              Text(text = "Paste Here")
            }
          }
        })

    Button(onClick = {}) { Text("Parse") }
  }
}
