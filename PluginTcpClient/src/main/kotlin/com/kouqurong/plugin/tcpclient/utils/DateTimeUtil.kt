package com.kouqurong.plugin.tcpclient.utils

import java.time.format.DateTimeFormatter

val formater = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

fun Long.toDateTimeString(): String {
  return formater.format(java.time.Instant.ofEpochMilli(this))
}
