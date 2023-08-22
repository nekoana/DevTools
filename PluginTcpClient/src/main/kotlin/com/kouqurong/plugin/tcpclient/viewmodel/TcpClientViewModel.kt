package com.kouqurong.plugin.tcpclient.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.kouqurong.plugin.tcpclient.model.ISendType
import com.kouqurong.plugin.tcpclient.model.Message
import com.kouqurong.plugin.tcpclient.utils.toHexByteArray
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SocketChannel
import java.util.concurrent.CopyOnWriteArrayList
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
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
  private val sendList = CopyOnWriteArrayList<ByteArray>()

  private val _messages = mutableStateListOf<Message>()
  val messages: List<Message> = _messages

  val connectState = MutableStateFlow<IConnectionState>(IConnectionState.Disconnected)

  private var sendJob: Job? = null

  fun updateAddress(addr: String) {
    address = addr
    isAvailableAddress = isAvailableAddress(addr)
  }

  private fun isAvailableAddress(ip: String): Boolean {
    return regex.matches(ip)
  }

  fun connect() {
    if (sendJob != null) {
      sendJob?.cancel()
      sendJob = null
      return
    }

    val ipPort = address.split(":")

    val selector = Selector.open()

    val socketChannel =
        runCatching {
              SocketChannel.open(InetSocketAddress(ipPort[0], ipPort[1].toInt()))
                  .run { configureBlocking(false) }
                  .also { it.register(selector, SelectionKey.OP_WRITE or SelectionKey.OP_READ) }
            }
            .onFailure {
              scope.launch { connectState.emit(IConnectionState.Error(it)) }
              println("connect error: $it")
            }
            .onSuccess { scope.launch { connectState.emit(IConnectionState.Connected) } }
            .getOrNull()

    if (socketChannel != null) {
      val sendDataChannelFlow =
          channelFlow {
            try {
              withContext(Dispatchers.IO) {
                val buffer = ByteBuffer.allocate(1024)

                while (selector.select() > 0) {
                  if (!isActive) return@withContext

                  val keys = selector.selectedKeys()
                  val iter = keys.iterator()

                  while (iter.hasNext()) {
                    val key = iter.next()

                    if (key.isReadable) {
                      val channel = key.channel() as SocketChannel

                      val text = buildString {
                        while (channel.read(buffer) > 0) {
                          buffer.flip()
                          val bytes = ByteArray(buffer.limit())
                          buffer.get(bytes)

                          append(bytes.decodeToString())
                        }
                      }

                      buffer.clear()

                      send(text)
                    }

                    if (key.isWritable) {
                      val channel = key.channel() as SocketChannel

                      val data = sendList.firstOrNull() ?: continue
                      val buffer = ByteBuffer.wrap(data)

                      while (buffer.hasRemaining()) {
                        channel.write(buffer)
                      }
                      sendList.remove(data)
                    }
                  }
                  iter.remove()
                }
              }
            } catch (e: Exception) {
              println("sendDataChannelFlow error: $e")
            } finally {
              awaitClose {
                println("close socket")
                socketChannel.close()

                scope.launch { connectState.emit(IConnectionState.Disconnected) }
              }
            }
          }

      sendJob =
          scope.launch {
            sendDataChannelFlow.collect {
              if (it.isNotEmpty()) {
                _messages.add(0, Message.fromOtherNow(it))
              }
            }
          }
    }
  }

  fun sendRequest() =
      scope.launch {
        _messages.add(0, Message.fromMeNow(sendData))
        sendList.add(
            when (sendType) {
              ISendType.Hex -> sendData.toHexByteArray()
              ISendType.Str -> sendData.toByteArray()
            })
      }

  fun sendDataChanged(text: String) {
    sendData = text
    sendEnabled = sendData.isNotEmpty() && connectState.value == IConnectionState.Connected
  }

  fun sendTypeChanged(type: ISendType) {
    sendType = type
  }

  fun clear() {
    _messages.clear()
    sendJob?.cancel()
  }
}
