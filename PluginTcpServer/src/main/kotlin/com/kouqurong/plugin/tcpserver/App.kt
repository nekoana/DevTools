package com.kouqurong.plugin.tcpserver

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kouqurong.plugin.tcpserver.components.ChatRoom
import com.kouqurong.plugin.tcpserver.viewmodel.Client
import com.kouqurong.plugin.tcpserver.viewmodel.IListenState
import com.kouqurong.plugin.tcpserver.viewmodel.TcpServerViewModel
import com.kouqurong.plugin.view.recomposeHighlighter
import java.net.InetSocketAddress

@Composable
fun App(viewModel: TcpServerViewModel) {

  val connectState = viewModel.listenState.collectAsState()
  val isAvailableAddress = viewModel.uiState.isAvailableAddress.collectAsState()

  Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Column(modifier = Modifier.weight(1F)) {
        PortEdit(
            port = viewModel.uiState.port,
            listenState = connectState.value,
            onUpdateIp = viewModel::updatePort,
            isAvailableAddress = isAvailableAddress.value,
            isEnabledEdit = true,
            onListen = viewModel::listen,
        )

        viewModel.uiState.selectedClient?.run {
          val sendEnabled = sendEnabled.collectAsState()

          ChatRoom(
              messages = messages,
              sendData = sendData,
              sendType = sendType,
              sendEnabled = sendEnabled.value,
              scrollState = scrollState,
              onSendRequest = ::sendRequest,
              onSendTextChanged = ::sendDataChanged,
              onSendTypeChanged = ::sendTypeChanged,
          )
        }
      }

      ClientList(
          modifier = Modifier.width(180.dp),
          clients = viewModel.clients,
          selectedClient = viewModel.uiState.selectedClient,
          onClientSelected = viewModel::selectClient)
    }
  }
}

@Composable
fun PortEdit(
    port: String,
    isEnabledEdit: Boolean,
    isAvailableAddress: Boolean,
    listenState: IListenState,
    onUpdateIp: (String) -> Unit,
    onListen: () -> Unit,
) {
  Row(
      modifier = Modifier,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically) {
        TextField(
            modifier = Modifier.width(260.dp).recomposeHighlighter(),
            value = port,
            enabled = isEnabledEdit && listenState == IListenState.Closed,
            label = {
              Text(
                  text = "Port",
              )
            },
            onValueChange = onUpdateIp,
            maxLines = 1,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
            isError = !isAvailableAddress,
        )

        Spacer(modifier = Modifier.width(8.dp))

        Button(onClick = onListen, enabled = isAvailableAddress) {
          Text(
              text =
                  when (listenState) {
                    IListenState.Closed -> "Listen"
                    IListenState.Listening -> "Close"
                    else -> "Listen"
                  })
        }
      }
}

@Composable
fun ClientList(
    modifier: Modifier = Modifier,
    clients: List<Client>,
    selectedClient: Client?,
    onClientSelected: (Client) -> Unit,
) {
  LazyColumn(
      modifier = modifier,
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    items(clients, key = { it.remoteAddress }) { client ->
      ClientItem(
          modifier = Modifier.fillMaxSize(),
          client = client,
          isSelected = selectedClient == client,
          onClientSelected = onClientSelected,
      )
    }
  }
}

@Composable
fun ClientItem(
    modifier: Modifier = Modifier,
    client: Client,
    isSelected: Boolean,
    onClientSelected: (Client) -> Unit,
) {
  val color =
      if (isSelected) {
        MaterialTheme.colorScheme.primary
      } else {
        MaterialTheme.colorScheme.surfaceVariant
      }

  Surface(
      modifier =
          Modifier.then(modifier)
              .clip(RoundedCornerShape(16.dp))
              .defaultMinSize(180.dp, 40.dp)
              .clickable { onClientSelected(client) },
      color = color,
  ) {
    Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
      Text(modifier = Modifier.padding(8.dp), text = client.remoteAddress)

      Spacer(modifier = Modifier.weight(1F))

      IconButton(onClick = {}) { Icon(Icons.Default.Close, contentDescription = "Close") }
    }
  }
}

@Preview
@Composable
fun PreviewClientList() {
  ClientList(
      clients =
          listOf(
              Client(InetSocketAddress(80)),
              Client(InetSocketAddress(81)),
              Client(InetSocketAddress(82)),
              Client(InetSocketAddress(83)),
              Client(InetSocketAddress(84)),
              Client(InetSocketAddress(85)),
          ),
      selectedClient = Client(InetSocketAddress(80)),
  ) {}
}

@Preview
@Composable
fun PreviewClientItem() {
  ClientItem(
      modifier = Modifier.size(180.dp, 40.dp),
      client = Client(InetSocketAddress(80)),
      isSelected = true) {}
}
