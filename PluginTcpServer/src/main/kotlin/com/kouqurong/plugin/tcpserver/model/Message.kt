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
