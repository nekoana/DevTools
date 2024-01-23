/*
 * Copyright 2024 The Open Source Project
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

package com.kouqurong.plugin.adbtool.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import com.kouqurong.plugin.adbtool.util.isBackground
import com.kouqurong.plugin.adbtool.util.runningProcess
import com.kouqurong.plugin.database.Command
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.time.delay

@Composable
fun CommandRunningWindow(
    command: Command,
    onDismiss: () -> Unit,
) {
  var visible by remember { mutableStateOf(true) }

  Window(
      onCloseRequest = {
        visible = false
        onDismiss()
      },
      title = "Running: ${command.name}",
      visible = visible,
  ) {
    val scrollState = rememberLazyListState()
    val textList = remember { mutableStateListOf<AnnotatedString>() }
    LaunchedEffect(Unit) {
      val timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
      val style = SpanStyle(color = Color.Gray, background = Color.Blue)

      runningProcess(arguments = command.arguments, inBackground = command.isBackground())
          .buffer(500, BufferOverflow.DROP_OLDEST)
          .onEach { delay(Duration.ofMillis(1)) }
          .collect {
            val text = buildAnnotatedString {
              pushStyle(style)
              append(timeFormatter.format(LocalDateTime.now()))
              pop()
              append("\t")
              append(it)
            }
            textList.add(text)
            if (textList.size > 500) {
              textList.removeFirst()
            }

            scrollState.animateScrollToItem(textList.size - 1)
          }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = scrollState,
        verticalArrangement = Arrangement.spacedBy(0.dp)) {
          items(textList.size, key = { textList[it] }) { index ->
            SelectionContainer {
              Text(text = textList[index], overflow = TextOverflow.Ellipsis, maxLines = 100)
            }
          }
        }
  }
}
