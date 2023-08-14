package com.kouqurong.plugin.tcpclient

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kouqurong.plugin.tcpclient.components.ChatRoom
import com.kouqurong.plugin.tcpclient.viewmodel.TcpClientViewModel

@Composable
fun App() {
  val viewModel = remember { TcpClientViewModel() }

  DisposableEffect(Unit) { onDispose { viewModel.clear() } }

  Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Column {
      AddressEdit(
          address = viewModel.address,
          onUpdateIp = viewModel::updateIp,
          isAvailableAddress = viewModel.isAvailableAddress,
          isEnabledEdit = viewModel.addressEditable,
          onConnect = viewModel::connect,
      )

      ChatRoom(
          messages = viewModel.sendDataList.toList().reversed(),
          sendData = viewModel.sendData,
          sendType = viewModel.sendType,
          sendEnabled = viewModel.sendEnabled,
          onSendRequest = viewModel::sendRequest,
          onSendTextChanged = viewModel::sendDataChanged,
          onSendTypeChanged = viewModel::sendTypeChanged,
      )
    }
  }
}

@Composable
fun AddressEdit(
    address: String,
    isEnabledEdit: Boolean,
    isAvailableAddress: Boolean,
    onUpdateIp: (String) -> Unit,
    onConnect: () -> Unit,
) {
  Row(
      modifier = Modifier,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically) {
        TextField(
            modifier = Modifier.width(260.dp),
            value = address,
            enabled = isEnabledEdit,
            label = {
              Text(
                  text = "Address",
              )
            },
            onValueChange = onUpdateIp,
            maxLines = 1,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
            isError = !isAvailableAddress,
        )

        Spacer(modifier = Modifier.width(8.dp))

        Button(onClick = onConnect, enabled = isAvailableAddress) { Text(text = "Connect") }
      }
}
