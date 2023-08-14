package com.kouqurong.plugin.tcpclient.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS")

fun LocalDateTime.toDateTimeString(): String {
  return formatter.format(this)
}
