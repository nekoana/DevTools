package com.kouqurong.plugin.tcpserver.components

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
import com.kouqurong.plugin.tcpserver.model.ISendType
import com.kouqurong.plugin.tcpserver.model.Message
import com.kouqurong.plugin.tcpserver.model.Whoami

@Composable
fun ChatRoom(
    modifier: Modifier = Modifier,
    messages: List<Message>,
    sendData: String,
    sendType: ISendType,
    sendEnabled: Boolean,
    onSendRequest: () -> Unit,
    onSendTextChanged: (String) -> Unit,
    onSendTypeChanged: (ISendType) -> Unit,
) {
  val scrollState = rememberLazyListState()

  Box(modifier = modifier) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = scrollState,
        reverseLayout = true,
    ) {
      item {
        UserInput(
            modifier = Modifier.padding(bottom = 16.dp, top = 16.dp),
            sendData = sendData,
            sendType = sendType,
            sendEnabled = sendEnabled,
            onSendRequest = onSendRequest,
            onTextChanged = onSendTextChanged,
            onSendTypeChanged = onSendTypeChanged)
      }
      items(messages, key = { it }) {
        ChatMessage(
            message = it,
        )
      }
    }
  }
}

@Preview
@Composable
fun PreviewChatRoom() {
  val messages =
      listOf(
          Message(
              id = 1,
              whoami = Whoami.Me,
              content = "Hello, world!",
              timestamp = "2021-10-01 00:00:01",
          ),
          Message(
              id = 2,
              whoami = Whoami.Me,
              content = "Hello, world!",
              timestamp = "2021-10-01 00:00:02",
          ),
          Message(
              id = 3,
              whoami = Whoami.Me,
              content = "Hello, world!",
              timestamp = "2021-10-01 00:00:03",
          ),
          Message(
              id = 4,
              whoami = Whoami.Me,
              content = "Hello, world!",
              timestamp = "2021-10-01 00:00:04",
          ),
      )

  ChatRoom(
      messages = messages,
      sendData = "",
      sendType = ISendType.Hex,
      sendEnabled = false,
      onSendTypeChanged = {},
      onSendTextChanged = {},
      onSendRequest = {})
}
