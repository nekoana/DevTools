package com.kouqurong.plugin.tcpclient.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.kouqurong.plugin.tcpclient.model.ISendType
import com.kouqurong.plugin.tcpclient.model.Message
import com.kouqurong.plugin.tcpclient.utils.toHexByteArray
import java.net.Socket
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import org.jetbrains.skiko.MainUIDispatcher

sealed interface IConnectionState {
  object Disconnected : IConnectionState
  object Connecting : IConnectionState
  object Connected : IConnectionState
  class Received(val data: Byte) : IConnectionState
  class Error(val throwable: Throwable) : IConnectionState
}

class TcpClientViewModel {

  private val scope = CoroutineScope(MainUIDispatcher + SupervisorJob())

  var address by mutableStateOf("")
  var isAvailableAddress by mutableStateOf(false)
  var addressEditable by mutableStateOf(true)
  var sendData by mutableStateOf("")
  var sendType by mutableStateOf<ISendType>(ISendType.Hex)
  var sendEnabled by mutableStateOf(false)
  val sendDataList = mutableStateListOf<Message>()

  private var connectState = MutableStateFlow<IConnectionState>(IConnectionState.Disconnected)

  private var sendDataChannel: Channel<ByteArray>? = null

  fun updateIp(ip: String) {
    address = ip
    isAvailableAddress = isAvailableAddress(ip)
  }

  private fun isAvailableAddress(ip: String): Boolean {
    // 判断是否是合法的IP 端口
    val regex =
        """^((\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])\.){3}(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5]):\d{1,5}$""".toRegex()
    return regex.matches(ip)
  }

  fun connect() {
    sendDataChannel?.close()

    val ipPort = address.split(":")

    val socket =
        runCatching { Socket(ipPort[0], ipPort[1].toInt()) }
            .onFailure { connectState.value = IConnectionState.Error(it) }
            .onSuccess { connectState.value = IConnectionState.Connected }
            .getOrNull()

    if (socket != null) {
      val receiveDataJob =
          scope.launch {
            channelFlow<IConnectionState> {
                  withContext(Dispatchers.IO) {
                    while (isActive && socket.isConnected) {
                      val inputStream = socket.getInputStream()
                      val read = inputStream.read()
                      if (read != -1) {
                        send(IConnectionState.Received(read.toByte()))
                      } else {
                        delay(1000)
                      }
                    }
                  }
                  awaitClose { socket.close() }
                }
                .let { scope.launch { it.stateIn(scope).collectLatest { connectState.emit(it) } } }
          }

      sendDataChannel = Channel<ByteArray> {}

      sendDataChannel?.let {
        scope.launch {
          it.receiveAsFlow().collectLatest {
            withContext(Dispatchers.IO) {
              val outputStream = socket.getOutputStream()
              outputStream.write(it)
              outputStream.flush()
            }
          }
        }

        it.invokeOnClose { receiveDataJob.cancel() }
      }
    }
  }

  fun sendRequest() =
      scope.launch {
        sendDataList.add(Message.fromMeNow(sendData))

        sendDataChannel?.send(
            when (sendType) {
              ISendType.Hex -> sendData.toHexByteArray()
              ISendType.Str -> sendData.toByteArray()
            })
      }

  fun sendDataChanged(text: String) {
    sendData = text
    sendEnabled = sendData.isNotEmpty() && sendDataChannel != null
  }

  fun sendTypeChanged(type: ISendType) {
    sendType = type
  }

  fun clear() {
    sendDataChannel?.close()
  }
}
