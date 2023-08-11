package com.kouqurong.plugin.tcpclient

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

class TcpClientViewModel {
    private val _address = mutableStateOf("")

    val address: State<String> = _address

    fun updateIp(ip: String) {
        _address.value = ip
    }
}