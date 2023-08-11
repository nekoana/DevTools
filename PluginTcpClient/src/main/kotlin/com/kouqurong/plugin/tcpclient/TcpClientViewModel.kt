package com.kouqurong.plugin.tcpclient

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TcpClientViewModel {
    private val _ip = MutableStateFlow<String>("")
    private val _port = MutableStateFlow<String>("")

    val ip: StateFlow<String> = _ip
    val port: StateFlow<String> = _port


    suspend fun updateIp(ip: String) {
        _ip.emit(ip)
    }

    fun updatePort(it: String) {
        _port.value = it
    }
}