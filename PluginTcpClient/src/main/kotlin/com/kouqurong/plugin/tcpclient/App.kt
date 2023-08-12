package com.kouqurong.plugin.tcpclient

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kouqurong.plugin.tcpclient.components.ChatRoom
import com.kouqurong.plugin.tcpclient.model.Message
import com.kouqurong.plugin.tcpclient.model.Whoami
import com.kouqurong.plugin.tcpclient.viewmodel.TcpClientViewModel

@Composable
fun App() {
    val viewModel = remember { TcpClientViewModel() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    modifier = Modifier
                        .width(260.dp),
                    value = viewModel.address.value,
                    label = {
                        Text(
                            text = "Address",
                        )
                    },
                    onValueChange = {
                        viewModel.updateIp(it)
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    isError = !viewModel.isAvailableAddress.value,
                )

                Spacer(modifier = Modifier.width(8.dp))


                Button(
                    onClick = {

                    },
                    enabled = viewModel.isAvailableAddress.value
                ) {
                    Text(
                        text = "Connect"
                    )
                }
            }

            ChatRoom(
                messages = listOf(
                    Message(
                        whoami = Whoami.Me,
                        content = "ajwnbdiubuiawhdibaiuwbdiubsiuhdaiuwhduihawuidbasuidhiuawhduiawhduiawhdui",
                        timestamp = "2021-09-09 12:00:00"
                    ),
                    Message(
                        whoami = Whoami.Other,
                        content = "ajwnbdiubuiawhdibaiuwbdiubsiuhdaiuwhduihawuidbasuidhiuawhduiawhduiawhdui",
                        timestamp = "2021-09-09 12:00:01"
                    ),
                    Message(
                        whoami = Whoami.Other,
                        content = "ajwnbdiubuiawhdibaiuwbdiubsiuhdaiuwhduihawuidbasuidhiuawhduiawhduiawhdui",
                        timestamp = "2021-09-09 12:00:02"
                    ),
                    Message(
                        whoami = Whoami.Me,
                        content = "ajwnbdiubuiawhdibaiuwbdiubsiuhdaiuwhduihawuidbasuidhiuawhduiawhduiawhdui",
                        timestamp = "2021-09-09 12:00:03"
                    ),
                    Message(
                        whoami = Whoami.Other,
                        content = "ajwnbdiubuiawhdibaiuwbdiubsiuhdaiuwhduihawuidbasuidhiuawhduiawhduiawhdui",
                        timestamp = "2021-09-09 12:00:04"
                    ),

                    ),
            )


        }

    }
}

