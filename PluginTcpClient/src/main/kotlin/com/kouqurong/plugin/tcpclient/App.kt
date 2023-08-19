package com.kouqurong.plugin.tcpclient

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kouqurong.plugin.tcpclient.components.ChatRoom
import com.kouqurong.plugin.tcpclient.viewmodel.IConnectionState
import com.kouqurong.plugin.tcpclient.viewmodel.TcpClientViewModel
import com.kouqurong.plugin.view.recomposeHighlighter

@Composable
fun App(viewModel: TcpClientViewModel) {

  val connectState = viewModel.connectState.collectAsState()

  Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Column {
      AddressEdit(
          address = viewModel.address,
          connectState = connectState.value,
          onUpdateIp = viewModel::updateAddress,
          isAvailableAddress = viewModel.isAvailableAddress,
          isEnabledEdit = viewModel.addressEditable,
          onConnect = viewModel::connect,
      )

      ChatRoom(
          messages = viewModel.sendDataList,
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
    connectState: IConnectionState,
    onUpdateIp: (String) -> Unit,
    onConnect: () -> Unit,
) {
  Row(
      modifier = Modifier,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically) {
        TextField(
            modifier = Modifier.width(260.dp).recomposeHighlighter(),
            value = address,
            enabled =
                isEnabledEdit &&
                    connectState != IConnectionState.Connecting &&
                    connectState != IConnectionState.Connected,
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

        Button(onClick = onConnect, enabled = isAvailableAddress) {
          Text(
              text =
                  when (connectState) {
                    IConnectionState.Connected -> "Disconnect"
                    IConnectionState.Connecting -> "Connecting"
                    else -> "Connect"
                  })
        }
      }
}
