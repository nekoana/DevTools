package com.kouqurong.plugin.tcpclient.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.kouqurong.plugin.tcpclient.model.Message
import com.kouqurong.plugin.tcpclient.model.Whoami

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
    Column {
      Surface(
          color = backgroundBubbleColor,
          shape = ChatBubbleShape,
      ) {
        SelectionContainer(modifier) {
          Text(
              text = message.content,
              style = MaterialTheme.typography.bodyLarge.copy(color = LocalContentColor.current),
              modifier = Modifier.padding(16.dp),
          )
        }
      }
    }
  }
}

@Preview
@Composable
fun PrewViewChatMeMessage() {
  ChatMessage(
      message =
          Message(
              whoami = Whoami.Me,
              content = "Hello",
              timestamp = "2021-10-01 12:00:00",
          ),
      modifier = Modifier)
}
