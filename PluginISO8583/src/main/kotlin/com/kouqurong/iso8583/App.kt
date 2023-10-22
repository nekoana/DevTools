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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

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

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    SwipeCrossFadeLayout(
        swipeState = state,
        modifier = Modifier.fillMaxSize(),
        background = {
          Surface(modifier = Modifier.fillMaxSize()) { androidx.compose.material3.Text("BACK") }
        },
        foreground = {
          Surface(modifier = Modifier.fillMaxSize()) {
            FieldTitle(modifier = Modifier.fillMaxWidth().height(32.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(top = 32.dp)) {
                  item {
                    FieldItem(
                        modifier =
                            Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1F))
                                .padding(8.dp),
                        field = 1)
                  }

                  item {
                    FieldItem(
                        modifier =
                            Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1F))
                                .padding(8.dp),
                        field = 1)
                  }

                  item {
                    FieldItem(
                        modifier =
                            Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1F))
                                .padding(8.dp),
                        field = 1)
                  }

                  item {
                    FieldItem(
                        modifier =
                            Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1F))
                                .padding(8.dp),
                        field = 1)
                  }
                }
          }
        },
        indicate = { SwipeRefreshContent(state.currentValue) })
  }
}

@Composable
fun FieldTitle(modifier: Modifier = Modifier) {
  Row(modifier = modifier) {
    Text(modifier = Modifier.weight(1F), text = "Field", textAlign = TextAlign.Center)
    Text(modifier = Modifier.weight(1F), text = "Format", textAlign = TextAlign.Center)
    Text(modifier = Modifier.weight(1F), text = "Length", textAlign = TextAlign.Center)
    Text(modifier = Modifier.weight(1F), text = "Attr", textAlign = TextAlign.Center)
    Text(modifier = Modifier.weight(1F), text = "Padding", textAlign = TextAlign.Center)
    Text(modifier = Modifier.weight(1F), text = "Align", textAlign = TextAlign.Center)
  }
}
