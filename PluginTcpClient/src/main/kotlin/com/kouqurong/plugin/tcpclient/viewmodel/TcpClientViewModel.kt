package com.kouqurong.plugin.tcpclient.viewmodel

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import kotlinx.coroutines.flow.*
import org.jetbrains.skiko.MainUIDispatcher

private val scope =
    CoroutineScope(
        MainUIDispatcher +
            SupervisorJob() +
            CoroutineExceptionHandler { coroutineContext, throwable ->
              throwable.printStackTrace()
            })

sealed interface IConnectionState {
  object Disconnected : IConnectionState
  object Connecting : IConnectionState
  object Connected : IConnectionState
  class Error(val throwable: Throwable) : IConnectionState
}

// 判断是否是合法的IP 端口
val regex =
    """^((\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])\.){3}(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5]):\d{1,5}$""".toRegex()

data class UiState(
    private val _address: MutableState<String> = mutableStateOf(""),
    private val _sendData: MutableState<String> = mutableStateOf(""),
    private val _sendType: MutableState<ISendType> = mutableStateOf(ISendType.Hex),
    private val _connectState: MutableState<IConnectionState> =
        mutableStateOf(IConnectionState.Disconnected),
    private val _messages: SnapshotStateList<Message> = mutableStateListOf(),
) {

  val address: String
    @Composable get() = _address.value

  val sendData: String
    @Composable get() = _sendData.value

  val sendType: ISendType
    @Composable get() = _sendType.value

  val connectState: IConnectionState
    @Composable get() = _connectState.value

  val messages: List<Message>
    @Composable get() = _messages

  fun setAddress(addr: String) {
    _address.value = addr
  }

  fun getIpPort() = _address.value.split(":")

  fun addMessage(index: Int, msg: Message) {
    _messages.add(index, msg)
  }

  fun getSendData() = _sendData.value

  fun getSendDataBytes() =
      when (_sendType.value) {
        ISendType.Hex -> _sendData.value.toHexByteArray()
        ISendType.Str -> _sendData.value.toByteArray()
      }

  fun setSendData(text: String) {
    _sendData.value = text
  }

  fun setSendType(type: ISendType) {
    _sendType.value = type
  }

  fun setConnectState(state: IConnectionState) {
    _connectState.value = state
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  val isAvailableAddress: StateFlow<Boolean> =
      snapshotFlow { _address.value }
          .mapLatest { regex.matches(it) }
          .stateIn(scope, SharingStarted.WhileSubscribed(), false)

  val sendEnabled: StateFlow<Boolean> =
      snapshotFlow { _sendData.value }
          .combine(snapshotFlow { _connectState.value }) { data, state ->
            data.isNotEmpty() && state == IConnectionState.Connected
          }
          .stateIn(scope, SharingStarted.WhileSubscribed(), false)
}

class TcpClientViewModel {

  val uiState = UiState()

  private val sendList = CopyOnWriteArrayList<ByteArray>()

  private var sendJob: Job? = null

  fun updateAddress(address: String) {
    uiState.setAddress(address)
  }

  fun connect() {
    if (sendJob != null) {
      sendJob?.cancel()
      sendJob = null
      return
    }

    val ipPort = uiState.getIpPort()

    val selector = Selector.open()

    val socketChannel =
        runCatching {
              SocketChannel.open(InetSocketAddress(ipPort[0], ipPort[1].toInt()))
                  .run { configureBlocking(false) }
                  .also { it.register(selector, SelectionKey.OP_WRITE or SelectionKey.OP_READ) }
            }
            .onFailure {
              scope.launch { uiState.setConnectState(IConnectionState.Error(it)) }
              println("connect error: $it")
            }
            .onSuccess { scope.launch { uiState.setConnectState(IConnectionState.Connected) } }
            .getOrNull()

    if (socketChannel != null) {
      val sendDataChannelFlow = channelFlow {
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

                  val read = channel.read(buffer)

                  if (read > 0) {
                    val text = buildString {
                      do {
                        buffer.flip()
                        val bytes = ByteArray(buffer.limit())
                        buffer.get(bytes)
                        append(bytes.decodeToString())
                      } while (channel.read(buffer) > 0)
                    }
                    send(text)
                  }
                  buffer.clear()
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
            selector.close()
            socketChannel.close()

            scope.launch { uiState.setConnectState(IConnectionState.Disconnected) }
          }
        }
      }

      sendJob =
          scope.launch {
            sendDataChannelFlow.collect {
              if (it.isNotEmpty()) {
                uiState.addMessage(0, Message.fromOtherNow(it))
              }
            }
          }
    }
  }

  fun sendRequest() =
      scope.launch {
        uiState.addMessage(0, Message.fromMeNow(uiState.getSendData()))
        sendList.add(uiState.getSendDataBytes())
      }

  fun sendDataChanged(text: String) {
    uiState.setSendData(text)
  }

  fun sendTypeChanged(type: ISendType) {
    uiState.setSendType(type)
  }

  fun clear() {
    sendJob?.cancel()
  }
}
