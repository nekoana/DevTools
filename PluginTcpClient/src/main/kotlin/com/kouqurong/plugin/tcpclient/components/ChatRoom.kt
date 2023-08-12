package com.kouqurong.plugin.tcpclient.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kouqurong.plugin.tcpclient.model.Message
import com.kouqurong.plugin.tcpclient.model.Whoami

@Composable
fun ChatRoom(
    messages: List<Message>,
    modifier: Modifier = Modifier,
) {
  val scrollState = rememberLazyListState()

  Box(modifier = modifier) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = scrollState,
    ) {
      items(messages, key = { it }) {
        ChatMessage(
            message = it,
        )
      }

      item { UserInput(modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)) }
    }
  }
}

@Preview
@Composable
fun PreviewChatRoom() {
  val messages =
      listOf(
          Message(
              whoami = Whoami.Me,
              content = "Hello, world!",
              timestamp = "2021-10-01 00:00:01",
          ),
          Message(
              whoami = Whoami.Me,
              content = "Hello, world!",
              timestamp = "2021-10-01 00:00:02",
          ),
          Message(
              whoami = Whoami.Me,
              content = "Hello, world!",
              timestamp = "2021-10-01 00:00:03",
          ),
          Message(
              whoami = Whoami.Me,
              content = "Hello, world!",
              timestamp = "2021-10-01 00:00:04",
          ),
      )

  ChatRoom(
      messages = messages,
  )
}
