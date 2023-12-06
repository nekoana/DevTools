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

package com.kouqurong.iso8583.util

import com.kouqurong.iso8583.data.Align
import com.kouqurong.iso8583.data.Attr
import com.kouqurong.iso8583.data.FieldItem
import com.kouqurong.iso8583.data.Format
import java.nio.ByteBuffer

fun FieldItem.parse(buffer: ByteBuffer): String {
  var dataLen = length.toInt()

  if (format == Format.VAR) {
    val dataLenBsCount =
        when (dataLen) {
          // 0..99 一个字节
          in 0..99 -> {
            1
          }
          // 100...9999 两个字节
          in 100..9999 -> {
            2
          }
          else -> throw Throwable("var length must in 0..9999")
        }

    val dataLenBs = ByteArray(dataLenBsCount)

    buffer.get(dataLenBs)

    dataLen = dataLenBs.bcdToInt()
  }
  return readData(dataLen, buffer)
}

private fun FieldItem.readData(dataLen: Int, buffer: ByteBuffer): String {
  var readLen = dataLen
  if (attr == Attr.BCD) {
    readLen = (dataLen + 1) / 2
  } else if (attr == Attr.BINARY) {
    readLen = (dataLen + 7) / 8
  }

  val dataBs = ByteArray(readLen)

  buffer.get(dataBs)

  return when (attr) {
    Attr.ASCII -> String(dataBs)
    Attr.BCD -> {
      val ret = dataBs.bcdToStr()
      if (readLen * 2 != dataLen) {
        when (align) {
          Align.R -> ret.substring(0, dataLen)
          Align.L -> ret.substring(ret.length - dataLen)
        }
      } else {
        ret
      }
    }
    Attr.BINARY -> dataBs.toHexStr()
  }
}
