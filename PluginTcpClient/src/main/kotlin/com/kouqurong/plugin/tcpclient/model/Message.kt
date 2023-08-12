package com.kouqurong.plugin.tcpclient.model

import androidx.compose.runtime.Immutable


sealed class Whoami(val name: String) {
    object Me : Whoami("Me")
    object Other : Whoami("Other")
}


@Immutable
data class Message(
    val whoami: Whoami,
    val content: String,
    val timestamp: String
)