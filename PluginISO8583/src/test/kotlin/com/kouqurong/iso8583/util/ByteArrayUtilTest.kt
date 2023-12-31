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

import org.junit.Assert.assertEquals
import org.junit.Test

class ByteArrayUtilTest {

  @Test
  fun bcd2Str() {
    val bcd = ByteArray(2)

    bcd[0] = 0x12.toByte()
    bcd[1] = 0x34.toByte()

    assertEquals("1234", bcd.bcdToStr())
  }

  @Test
  fun toHexStr() {
    val bs = ByteArray(2)

    bs[0] = 0x12.toByte()
    bs[1] = 0x34.toByte()

    assertEquals("1234", bs.toHexStr())
  }
}
