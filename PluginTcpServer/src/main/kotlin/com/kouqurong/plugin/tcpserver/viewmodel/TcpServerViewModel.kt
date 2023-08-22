package com.kouqurong.plugin.tcpserver.viewmodel

import androidx.compose.runtime.*
import com.kouqurong.plugin.tcpserver.model.ISendType
import com.kouqurong.plugin.tcpserver.model.Message
import com.kouqurong.plugin.tcpserver.utils.toHexByteArray
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.util.concurrent.CopyOnWriteArrayList
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jetbrains.skiko.MainUIDispatcher

private val scope =
    CoroutineScope(
        MainUIDispatcher +
            SupervisorJob() +
            CoroutineExceptionHandler { _, throwable -> throwable.printStackTrace() })

sealed interface IListenState {
  object Closed : IListenState
  object Listening : IListenState
  class Error(val throwable: Throwable) : IListenState
}

// 判断是否是合法的端口
val regex = """^\d{1,5}$""".toRegex()

@Immutable
data class Client(private val address: SocketAddress) {
  val remoteAddress: String = address.toString()

  private val buffer = ByteBuffer.allocate(1024)

  private val mutex = Mutex()

  private val _messages = mutableStateListOf<Message>()

  val messages: List<Message> = _messages

  private val sendList = CopyOnWriteArrayList<ByteArray>()

  var sendData by mutableStateOf("")

  var sendType by mutableStateOf<ISendType>(ISendType.Hex)

  var sendEnabled by mutableStateOf(false)

  suspend fun read(channel: SocketChannel) =
      withContext(Dispatchers.IO) {
        mutex.withLock {
          buildString {
            while (isActive && channel.read(buffer) > 0) {
              buffer.flip()
              val bytes = ByteArray(buffer.limit())
              buffer.get(bytes)
              append(bytes.decodeToString())
            }
            buffer.clear()
          }
        }
      }

  fun sendRequest() {
    _messages.add(0, Message.fromMeNow(sendData))

    sendList.add(
        when (sendType) {
          ISendType.Hex -> sendData.toHexByteArray()
          ISendType.Str -> sendData.toByteArray()
        })
  }

  fun sendDataChanged(text: String) {
    sendData = text
    sendEnabled = sendData.isNotEmpty()
  }

  fun sendTypeChanged(type: ISendType) {
    sendType = type
  }

  fun send(channel: SocketChannel) =
      scope.launch(Dispatchers.IO) {
        mutex.withLock {
          val data = sendList.firstOrNull() ?: return@launch
          val buffer = ByteBuffer.wrap(data)
          while (buffer.hasRemaining()) {
            channel.write(buffer)
          }
          sendList.remove(data)
        }
      }

  suspend fun received(data: String) {
    if (data.isEmpty()) return

    withContext(MainUIDispatcher) { _messages.add(0, Message.fromOtherNow(data)) }
  }
}

class TcpServerViewModel {
  var selectedClient: Client? by mutableStateOf(null)

  private var listenJob: Job? = null

  var port by mutableStateOf("")
  var isAvailableAddress by mutableStateOf(false)
  var addressEditable by mutableStateOf(true)

  private val _clients = mutableStateListOf<Client>()

  val clients = _clients

  val listenState = MutableStateFlow<IListenState>(IListenState.Closed)

  private val channelClientMap = mutableMapOf<SocketAddress, Client>()

  fun updatePort(p: String) {
    port = p
    isAvailableAddress = isAvailablePort(p)
  }

  private fun isAvailablePort(ip: String): Boolean {
    return regex.matches(ip)
  }

  fun listen() {
    if (listenJob != null) {
      listenJob!!.cancel()
      listenJob = null
      return
    }

    val server =
        runCatching {
              ServerSocketChannel.open().apply {
                configureBlocking(false)
                bind(InetSocketAddress(port.toInt()))
              }
            }
            .onFailure { listenState.value = IListenState.Error(it) }
            .onSuccess { listenState.value = IListenState.Listening }
            .getOrNull()

    if (server != null) {
      listenJob =
          scope.launch {
            val selector =
                runCatching { Selector.open().also { server.register(it, SelectionKey.OP_ACCEPT) } }
                    .getOrNull()

            if (selector != null) {
              listenState.emit(IListenState.Listening)

              try {
                listening(selector)
              } finally {
                selector.close()
                server.close()

                println("close server")

                listenState.emit(IListenState.Closed)
              }
            }
          }
    }
  }

  fun selectClient(client: Client) {
    selectedClient = client
  }

  private suspend fun accept(selector: Selector, server: ServerSocketChannel) =
      withContext(Dispatchers.IO) {
        server
            .accept()
            .apply {
              configureBlocking(false)
              register(selector, SelectionKey.OP_READ or SelectionKey.OP_WRITE)
            }
            .run {
              val client = channelClientMap.computeIfAbsent(remoteAddress) { Client(remoteAddress) }

              withContext(MainUIDispatcher) {
                _clients.add(client)

                if (selectedClient == null) {
                  selectedClient = client
                }
              }
            }
      }

  private suspend fun listening(selector: Selector) =
      withContext(Dispatchers.IO) {
        while (selector.select() > 0) {
          if (!isActive) return@withContext

          val keys = selector.selectedKeys()
          val iter = keys.iterator()

          while (iter.hasNext()) {
            val key = iter.next()
            if (key.isAcceptable) {
              accept(selector, key.channel() as ServerSocketChannel)
            } else {
              if (key.isReadable) {
                val channel = key.channel() as SocketChannel
                channelClientMap[channel.remoteAddress]?.run {
                  val data = read(channel)
                  received(data)
                }
              }
              if (key.isWritable) {
                val channel = key.channel() as SocketChannel
                channelClientMap[channel.remoteAddress]?.run { send(channel) }
              }
            }
            iter.remove()
          }
        }
      }
}