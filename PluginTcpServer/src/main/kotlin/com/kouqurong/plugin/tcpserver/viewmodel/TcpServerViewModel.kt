/*
 * Copyright 2023 The Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kouqurong.plugin.tcpserver.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.kouqurong.plugin.tcpserver.model.ISendType
import com.kouqurong.plugin.tcpserver.model.Message
import com.kouqurong.plugin.tcpserver.utils.toHexByteArray
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.net.SocketException
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
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

  val scrollState: LazyListState = LazyListState()

  private val buffer = ByteBuffer.allocate(1024)

  private val _messages = mutableStateListOf<Message>()

  val messages: List<Message> = _messages

  private val sendList = ArrayBlockingQueue<ByteArray>(8)

  var sendData by mutableStateOf("")

  var sendType by mutableStateOf<ISendType>(ISendType.Hex)

  @OptIn(ExperimentalCoroutinesApi::class)
  val sendEnabled =
      snapshotFlow { sendData }
          .mapLatest { it.isNotEmpty() }
          .stateIn(scope, SharingStarted.WhileSubscribed(), false)

  @Throws(SocketException::class)
  suspend fun read(channel: SocketChannel) =
      withContext(Dispatchers.IO) {
        val read = channel.read(buffer)

        if (read > 0) {
          buildString {
            do {
              buffer.flip()
              val bytes = ByteArray(buffer.limit())
              buffer.get(bytes)
              append(bytes.decodeToString())
            } while (isActive && channel.read(buffer) > 0)
            buffer.clear()
          }
        } else {
          throw SocketException("read error")
        }
      }

  fun sendRequest() {
    _messages.add(0, Message.fromMeNow(sendData))

    // todo 性能优化 可能会阻塞
    sendList.add(
        when (sendType) {
          ISendType.Hex -> sendData.toHexByteArray()
          ISendType.Str -> sendData.toByteArray()
        })
  }

  fun sendDataChanged(text: String) {
    sendData = text
  }

  fun sendTypeChanged(type: ISendType) {
    sendType = type
  }

  fun send(channel: SocketChannel) =
      scope.launch(Dispatchers.IO) {
        val data = sendList.poll() ?: return@launch
        val buffer = ByteBuffer.wrap(data)
        while (buffer.hasRemaining()) {
          channel.write(buffer)
        }
      }

  suspend fun received(data: String) {
    if (data.isEmpty()) return

    withContext(MainUIDispatcher) { _messages.add(0, Message.fromOtherNow(data)) }
  }
}

@Stable
data class UiState(
    private val _port: MutableState<String> = mutableStateOf(""),
    private val _selectedClient: MutableState<Client?> = mutableStateOf(null),
    private val _clients: SnapshotStateList<Client> = mutableStateListOf(),
    private val scope: CoroutineScope,
) {
  @OptIn(ExperimentalCoroutinesApi::class)
  val isAvailableAddress =
      snapshotFlow { _port.value }
          .mapLatest { regex.matches(it) }
          .stateIn(scope, SharingStarted.WhileSubscribed(), false)

  val selectedClient: Client?
    get() = _selectedClient.value
  internal val port: String
    get() = _port.value

  val clients: List<Client>
    get() = _clients

  internal fun remove(client: Client?) {
    _clients.remove(client)
    if (_selectedClient.value == client) {
      _selectedClient.value = null
    }
  }

  fun add(client: Client) {
    _clients.add(client)
    if (_selectedClient.value == null) {
      _selectedClient.value = client
    }
  }

  fun select(client: Client) {
    _selectedClient.value = client
  }

  fun setPort(port: String) {
    _port.value = port
  }
}

class TcpServerViewModel {
  val uiState = UiState(scope = scope)

  private var listenJob: Job? = null

  val listenState = MutableStateFlow<IListenState>(IListenState.Closed)

  private val channelClientMap = ConcurrentHashMap<SocketAddress, Client>()

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
                bind(InetSocketAddress(uiState.port.toInt()))
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
                withContext(Dispatchers.IO) {
                  while (isActive) {
                    if (selector.select(300) <= 0) {
                      continue
                    }

                    val keys = selector.selectedKeys()
                    val iter = keys.iterator()

                    while (iter.hasNext()) {
                      val key = iter.next()
                      if (key.isAcceptable) {
                        accept(selector, key.channel() as ServerSocketChannel)
                      } else {
                        val channel = key.channel() as SocketChannel

                        if (!channel.isOpen) {
                          val client = channelClientMap.remove(channel.remoteAddress)
                          scope.launch { uiState.remove(client) }
                          continue
                        }

                        if (key.isReadable) {
                          channelClientMap[channel.remoteAddress]?.run {
                            runCatching { received(read(channel)) }
                                .onFailure {
                                  it.printStackTrace()
                                  val client = channelClientMap.remove(channel.remoteAddress)
                                  scope.launch { uiState.remove(client) }
                                }
                          }
                        }

                        if (key.isWritable) {
                          channelClientMap[channel.remoteAddress]?.run { send(channel) }
                        }
                      }
                      iter.remove()
                    }
                  }
                }
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

              withContext(MainUIDispatcher) { uiState.add(client) }
            }
      }
}
