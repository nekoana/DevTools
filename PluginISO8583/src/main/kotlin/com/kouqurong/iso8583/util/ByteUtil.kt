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

fun Byte.bcdToStr(): String {
  // 如果不检查的话那么与转hexStr的方法就没有区别了
  val high = (this.toInt() and 0xf0).ushr(4)
  val low = this.toInt() and 0x0f
  if (high > 9 || low > 9) {
    throw IllegalArgumentException("Invalid bcd digit $this")
  }
  return buildString {
    append(high)
    append(low)
  }
}

fun Byte.toHexStr(): String = String.format("%02X", this)
