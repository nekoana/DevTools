package com.kouqurong.plugin.tcpclient.model

sealed interface ISendType {
  object Hex : ISendType
  object Str : ISendType
}
