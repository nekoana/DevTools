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

private fun String.toBcd(): ByteArray {
  val ret = ByteArray(length / 2)

  windowed(2, 2, false).withIndex().forEach { (index, value) ->
    val high = value[0] - '0'
    val low = value[1] - '0'
    if (low < 0 || low > 9 || high < 0 || high > 9) {
      throw IllegalArgumentException("Invalid bcd digit $value in $this")
    }

    ret[index] = (high shl 4 or low).toByte()
  }

  return ret
}

fun String.toBcd(align: Align = Align.L, padding: Char = '0'): ByteArray {
  if (length % 2 != 0) {
    val sb = StringBuilder()
    if (align == Align.L) {
      sb.append(padding)
    }
    sb.append(this)
    if (align == Align.R) {
      sb.append(padding)
    }
    return sb.toString().toBcd()
  }

  return toBcd()
}
