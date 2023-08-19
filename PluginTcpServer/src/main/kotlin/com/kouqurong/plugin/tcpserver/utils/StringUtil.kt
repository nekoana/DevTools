package com.kouqurong.plugin.tcpserver.utils

val String.hexBytes
  get() = ByteArray(this.length / 2) { this.substring(it * 2, it * 2 + 2).toInt(16).toByte() }

fun String.toHexByteArray() = hexBytes
