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

package com.kouqurong.plugin.tcpserver.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.kouqurong.plugin.tcpserver.model.Message
import com.kouqurong.plugin.tcpserver.model.Whoami

@Composable
fun ChatMessage(
    message: Message,
    modifier: Modifier = Modifier.padding(top = 8.dp),
) {
  Row(
      modifier = modifier,
  ) {
    ChatAuthorAndText(message = message, modifier = Modifier.padding(16.dp).weight(1F))
  }
}

@Composable
fun ChatAuthorAndText(message: Message, modifier: Modifier = Modifier) {
  Column(modifier = modifier) {
    AuthorAndTimestamp(
        message = message,
    )
    TextBubble(message = message, modifier = Modifier.padding(top = 4.dp))
  }
}

@Composable
fun AuthorAndTimestamp(message: Message, modifier: Modifier = Modifier) {
  // Combine author and timestamp for a11y.
  Row(modifier = Modifier.then(modifier).semantics(mergeDescendants = true) {}) {
    Text(
        text = message.whoami.name,
        style = MaterialTheme.typography.titleMedium,
        modifier =
            Modifier.alignBy(LastBaseline)
                .paddingFrom(LastBaseline, after = 8.dp) // Space to 1st bubble
        )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
        text = message.timestamp,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.alignBy(LastBaseline),
        color = MaterialTheme.colorScheme.onSurfaceVariant)
  }
}

private val ChatBubbleShape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)

@Composable
fun TextBubble(message: Message, modifier: Modifier = Modifier) {
  val selectionBubbleColor =
      if (message.whoami == Whoami.Me) {
        MaterialTheme.colorScheme.surfaceVariant
      } else {
        MaterialTheme.colorScheme.primary
      }

  val backgroundBubbleColor =
      if (message.whoami == Whoami.Me) {
        MaterialTheme.colorScheme.primary
      } else {
        MaterialTheme.colorScheme.surfaceVariant
      }

  val textSelectionColors =
      TextSelectionColors(
          handleColor = selectionBubbleColor,
          backgroundColor = selectionBubbleColor.copy(alpha = 0.6F),
      )

  CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
    Surface(color = backgroundBubbleColor, modifier = Modifier.clip(ChatBubbleShape)) {
      SelectionContainer(modifier = Modifier.focusable(enabled = false)) {
        Text(
            text = message.content,
            style = MaterialTheme.typography.bodyLarge.copy(color = LocalContentColor.current),
            modifier = Modifier.padding(16.dp),
        )
      }
    }
  }
}

@Preview
@Composable
fun PreviewChatMeMessage() {
  ChatMessage(
      message =
          Message(
              id = 1,
              whoami = Whoami.Me,
              content = "Hello",
              timestamp = "2021-10-01 12:00:00",
          ),
      modifier = Modifier)
}
