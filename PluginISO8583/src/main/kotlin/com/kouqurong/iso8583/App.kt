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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun App() {
  Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
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
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    SwipeCrossFadeLayout(
        swipeableState = state,
        modifier = Modifier.fillMaxSize().background(Color.Blue),
        background = { Box(modifier = Modifier.fillMaxSize().background(Color.Black)) },
        foreground = { Box(modifier = Modifier.fillMaxSize().background(Color.Red)) })
  }
  //  OutlinedTextField(
  //      modifier = modifier,
  //      value = text,
  //      onValueChange = onValueChanged,
  //      placeholder = {
  //
  //      })
}

enum class SwipeCrossFadeState {
  FORE,
  BACK,
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeCrossFadeLayout(
    modifier: Modifier,
    swipeableState: SwipeableState<SwipeCrossFadeState> =
        rememberSwipeableState(SwipeCrossFadeState.FORE),
    background: @Composable () -> Unit,
    foreground: @Composable () -> Unit,
) {
  var maxHeight by remember { mutableStateOf(100) }
  SubcomposeLayout(
      modifier =
          modifier.swipeable(
              state = swipeableState,
              anchors =
                  mapOf(
                      0F to SwipeCrossFadeState.FORE,
                      maxHeight.toFloat() to SwipeCrossFadeState.BACK),
              orientation = Orientation.Vertical,
              thresholds = { _, _ -> FractionalThreshold(0.1F) })) { constraints ->
        val backgroundPlaceable =
            subcompose("BACKGROUND", background)
                .first()
                .measure(constraints.copy(minWidth = 0, minHeight = 0))

        maxHeight = backgroundPlaceable.height

        val progress = 1F - (swipeableState.offset.value / maxHeight).coerceIn(0F, 1F)

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
          backgroundPlaceable.placeRelativeWithLayer(0, 0) { alpha = 1 - progress }
          foregroundPlaceable.placeRelativeWithLayer(0, maxHeight - foregroundHeight)
        }
      }
}
