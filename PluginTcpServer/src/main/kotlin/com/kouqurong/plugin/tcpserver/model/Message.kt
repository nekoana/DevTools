package com.kouqurong.plugin.tcpserver.model

import androidx.compose.runtime.Immutable
import com.kouqurong.plugin.tcpserver.utils.toDateTimeString
import java.time.LocalDateTime

sealed class Whoami(val name: String) {
  object Me : Whoami("Me")
  object Other : Whoami("Other")
}

@Immutable
data class Message(val id: Long, val whoami: Whoami, val content: String, val timestamp: String) {
  companion object {
    fun fromMe(id: Long, content: String, timestamp: String) =
        Message(id, Whoami.Me, content, timestamp)

    fun fromMeNow(content: String) =
        fromMe(System.currentTimeMillis(), content, LocalDateTime.now().toDateTimeString())

    fun fromOther(content: String, timestamp: String) =
        Message(System.currentTimeMillis(), Whoami.Other, content, timestamp)

    fun fromOtherNow(content: String) = fromOther(content, LocalDateTime.now().toDateTimeString())
  }
}
