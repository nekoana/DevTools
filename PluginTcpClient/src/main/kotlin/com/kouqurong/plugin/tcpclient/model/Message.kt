package com.kouqurong.plugin.tcpclient.model

import androidx.compose.runtime.Immutable
import com.kouqurong.plugin.tcpclient.utils.toDateTimeString
import java.time.LocalDateTime

sealed class Whoami(val name: String) {
  object Me : Whoami("Me")
  object Other : Whoami("Other")
}

@Immutable
data class Message(val whoami: Whoami, val content: String, val timestamp: String) {
  companion object {
    fun fromMe(content: String, timestamp: String) = Message(Whoami.Me, content, timestamp)

    fun fromMeNow(content: String) = fromMe(content, LocalDateTime.now().toDateTimeString())
  }
}
