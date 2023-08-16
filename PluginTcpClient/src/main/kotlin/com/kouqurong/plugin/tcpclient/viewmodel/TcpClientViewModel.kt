package com.kouqurong.plugin.tcpclient.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.kouqurong.plugin.tcpclient.model.ISendType
import com.kouqurong.plugin.tcpclient.model.Message
import com.kouqurong.plugin.tcpclient.model.Whoami
import com.kouqurong.plugin.tcpclient.utils.toHex
import com.kouqurong.plugin.tcpclient.utils.toHexByteArray
import java.net.Socket
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import org.jetbrains.skiko.MainUIDispatcher

sealed interface IConnectionState {
  object Disconnected : IConnectionState
  object Connecting : IConnectionState
  object Connected : IConnectionState
  class Error(val throwable: Throwable) : IConnectionState
}

// 判断是否是合法的IP 端口
val regex =
    """^((\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])\.){3}(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5]):\d{1,5}$""".toRegex()

class TcpClientViewModel {
  private val scope =
      CoroutineScope(
          MainUIDispatcher +
              SupervisorJob() +
              CoroutineExceptionHandler { coroutineContext, throwable ->
                throwable.printStackTrace()
              })

  var address by mutableStateOf("")
  var isAvailableAddress by mutableStateOf(false)
  var addressEditable by mutableStateOf(true)
  var sendData by mutableStateOf("")
  var sendType by mutableStateOf<ISendType>(ISendType.Hex)
  var sendEnabled by mutableStateOf(false)

  private val _sendDataList = mutableStateListOf<Message>()
  val sendDataList: List<Message> = _sendDataList

  val connectState = MutableStateFlow<IConnectionState>(IConnectionState.Disconnected)

  private var sendDataChannel: Channel<ByteArray>? = null

  fun updateAddress(addr: String) {
    address = addr
    isAvailableAddress = isAvailableAddress(addr)
  }

  private fun isAvailableAddress(ip: String): Boolean {
    return regex.matches(ip)
  }

  fun connect() {
    if (connectState.value == IConnectionState.Connecting) {
      return
    }

    if (connectState.value == IConnectionState.Connected) {
      sendDataChannel?.close()
      return
    }

    sendDataChannel?.close()

    val ipPort = address.split(":")

    val socket =
        runCatching { Socket(ipPort[0], ipPort[1].toInt()) }
            .onFailure { connectState.value = IConnectionState.Error(it) }
            .onSuccess { connectState.value = IConnectionState.Connected }
            .getOrNull()

    if (socket != null) {
      val flow = channelFlow {
        try {
          withContext(Dispatchers.IO) {
            val inputStream = socket.getInputStream().buffered()

            while (isActive && socket.isConnected) {
              if (inputStream.available() == 0) {
                delay(1000)
                continue
              }
              val read = inputStream.read()
              if (read != -1) {
                send(read.toByte())
              }
            }
          }
        } finally {
          awaitClose {
            println("close socket")
            socket.close()

            scope.launch { connectState.emit(IConnectionState.Disconnected) }
          }
        }
      }

      val receiveDataJob =
          scope.launch {
            flow.collectLatest {
              val msg = _sendDataList.lastOrNull()
              if (msg == null || msg.whoami == Whoami.Me) {
                _sendDataList.add(Message.fromOtherNow(it.toHex()))
              } else {
                val newMsg = msg.copy(content = msg.content + it.toHex())
                _sendDataList[_sendDataList.lastIndex] = newMsg
              }
            }
          }

      sendDataChannel =
          Channel<ByteArray>().apply {
            scope.launch {
              receiveAsFlow().collectLatest {
                withContext(Dispatchers.IO) {
                  val outputStream = socket.getOutputStream()
                  outputStream.write(it)
                  outputStream.flush()
                }
              }
            }

            invokeOnClose { receiveDataJob.cancel() }
          }
    }
  }

  fun sendRequest() =
      scope.launch {
        _sendDataList.add(0, Message.fromMeNow(sendData))
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
