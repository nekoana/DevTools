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

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun App() {
  Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    ISO8583HexInput(modifier = Modifier.fillMaxSize(), text = TextFieldValue("")) {}
  }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ISO8583HexInput(
    modifier: Modifier = Modifier,
    text: TextFieldValue,
    onValueChanged: (TextFieldValue) -> Unit
) {

  val state = rememberSwipeableState(SwipeCrossFadeState.FORE)
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    SwipeCrossFadeLayout(
        swipeState = state,
        modifier = Modifier.fillMaxSize(),
        background = { Box(modifier = Modifier.fillMaxSize().background(Color.Black)) },
        foreground = {
          LazyColumn(
              modifier = Modifier.fillMaxWidth().background(Color.Green),
              horizontalAlignment = Alignment.CenterHorizontally) {
                item {
                  androidx.compose.material3.Button(
                      onClick = {},
                  ) {
                    androidx.compose.material3.Text("Click To Add New Field")
                  }
                }
              }
        },
        indicate = {
          androidx.compose.material3.Text(
              "Swipe",
              modifier = Modifier.fillMaxWidth().background(Color.Green),
              textAlign = TextAlign.Center)
        })
  }
}

enum class SwipeCrossFadeState {
  FORE,
  BACK,
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeCrossFadeLayout(
    modifier: Modifier,
    swipeState: SwipeableState<SwipeCrossFadeState> =
        rememberSwipeableState(SwipeCrossFadeState.FORE),
    swipeEnabled: Boolean = true,
    background: @Composable () -> Unit,
    foreground: @Composable () -> Unit,
    indicate: @Composable () -> Unit = {},
) {
  var maxHeight by remember { mutableStateOf(100) }
  SubcomposeLayout(
      modifier =
          modifier.swipeable(
              state = swipeState,
              enabled = swipeEnabled,
              anchors =
                  mapOf(
                      maxHeight.toFloat() to SwipeCrossFadeState.FORE,
                      0F to SwipeCrossFadeState.BACK),
              orientation = Orientation.Vertical,
              thresholds = { _, _ -> FractionalThreshold(0.1F) })) { constraints ->
        // 上拉指示器
        val indicatePlaceable =
            subcompose("INDICATE", indicate)
                .first()
                .measure(constraints.copy(minWidth = 0, minHeight = 0))

        // 计算back的大小，减去指示器高度
        val backgroundPlaceable =
            subcompose("BACKGROUND", background)
                .first()
                .measure(
                    constraints.copy(
                        minWidth = 0,
                        minHeight = 0,
                        maxHeight = constraints.maxHeight - indicatePlaceable.height))
        // 可用的容器最大高度
        maxHeight = backgroundPlaceable.height
        // 根据滑动距离计算滑动进度
        val progress = 1F - ((maxHeight - swipeState.offset.value) / maxHeight).coerceIn(0F, 1F)

        val foregroundHeight = (maxHeight * progress).toInt()

        val foregroundPlaceable =
            subcompose("FOREGROUND", foreground)
                .first()
                .measure(
                    constraints.copy(
                        minHeight = foregroundHeight,
                        maxHeight = foregroundHeight,
                    ))

        layout(constraints.maxWidth, constraints.maxHeight) {
          backgroundPlaceable.placeRelativeWithLayer(0, indicatePlaceable.height)
          foregroundPlaceable.placeRelativeWithLayer(0, 0)
          indicatePlaceable.placeRelativeWithLayer(0, foregroundPlaceable.height)
        }
      }
}
