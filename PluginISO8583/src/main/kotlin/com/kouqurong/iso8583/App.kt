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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.kouqurong.iso8583.componet.*

@Composable
fun App() {
  Surface(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    ISO8583HexInput(modifier = Modifier.fillMaxSize(), text = TextFieldValue("")) {}
  }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ISO8583HexInput(
    modifier: Modifier = Modifier,
    text: TextFieldValue,
    onValueChanged: (TextFieldValue) -> Unit
) {
  val state = rememberSwipeableState(SwipeCrossFadeState.FORE)

  val fieldMenuItems =
      listOf(
          FieldMenuItem("添加") {},
          FieldMenuItem("导出") {},
          FieldMenuItem("导入") {},
          FieldMenuItem("清空") {},
          FieldMenuItem("模版") {},
      )

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    SwipeCrossFadeLayout(
        swipeState = state,
        modifier = Modifier.fillMaxSize(),
        background = { Surface(modifier = Modifier.fillMaxSize()) { Text("BACK") } },
        foreground = {
          Surface(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxSize().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround) {
                  FieldDetailContent(
                      modifier = Modifier.width(500.dp).fillMaxHeight().padding(8.dp))
                  FieldMenuContent(
                      modifier = Modifier.width(200.dp).fillMaxSize(),
                      fieldMenuItems = fieldMenuItems)
                }
          }
        },
        indicate = { SwipeRefreshContent(state.currentValue) })
  }
}

data class FieldMenuItem(val text: String, val onClick: () -> Unit)

@Composable
fun FieldDetailContent(modifier: Modifier = Modifier) {
  LazyColumn(
      modifier = modifier,
      verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    for (i in 0..10) {
      item { AnimationSizeFieldItem(modifier = Modifier.fillMaxWidth().height(52.dp), field = i) }
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
          AnimationSizeButton(modifier = Modifier.size(160.dp, 52.dp), onClick = {}) {
            Text(it.text)
          }
        }
      }
}
