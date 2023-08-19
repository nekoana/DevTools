package com.kouqurong.plugin.tcpserver.model

sealed interface ISendType {
  object Hex : ISendType
  object Str : ISendType
}
