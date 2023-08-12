package com.kouqurong.plugin.tcpclient.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import java.net.Socket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface IConnectionState {
  object Disconnected : IConnectionState
  object Connecting : IConnectionState
  object Connected : IConnectionState
  class Error(val throwable: Throwable) : IConnectionState
}

sealed class DataType(val value: String) {
  class Hex(value: String) : DataType(value)
  class Str(value: String) : DataType(value)
}

class TcpClientViewModel {
  private val scope = MainScope()

  private var socket: Socket? = null

  private val _address = mutableStateOf("")
  private val _isAvailableAddress = mutableStateOf(false)
  private val _connectionState = MutableStateFlow<IConnectionState>(IConnectionState.Disconnected)

  val address: State<String> = _address
  val isAvailableAddress: State<Boolean> = _isAvailableAddress
  val connectionState: SharedFlow<IConnectionState> = _connectionState.asSharedFlow()

  fun updateIp(ip: String) {
    _address.value = ip
    _isAvailableAddress.value = isAvailableAddress(ip)
  }

  private fun isAvailableAddress(ip: String): Boolean {
    // 判断是否是合法的IP 端口
    val regex =
        """^((\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])\.){3}(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5]):\d{1,5}$""".toRegex()
    return regex.matches(ip)
  }

  fun connect() =
      scope.launch(Dispatchers.IO) {
        runCatching {
              _connectionState.emit(IConnectionState.Connecting)
              socket = Socket(_address.value.split(":")[0], _address.value.split(":")[1].toInt())
            }
            .onFailure { _connectionState.emit(IConnectionState.Error(it)) }
            .onSuccess { _connectionState.emit(IConnectionState.Connected) }
      }

  suspend fun send(dataType: DataType) = withContext(Dispatchers.IO) {}

  fun clear() {}
}
