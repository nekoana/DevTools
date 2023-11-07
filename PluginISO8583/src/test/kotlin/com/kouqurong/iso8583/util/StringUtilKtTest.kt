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

import com.kouqurong.iso8583.data.IAlign
import org.junit.Assert.*
import org.junit.Test

class StringUtilKtTest {

  @Test
  fun toBcd() {
    val bcdStr = "1234567890".toBcd()

    assertArrayEquals(byteArrayOf(0x12, 0x34, 0x56, 0x78, 0x90.toByte()), bcdStr)

    val bcdStr3 = "123456789".toBcd(IAlign.RIGHT)

    assertArrayEquals(byteArrayOf(0x01, 0x23, 0x45, 0x67, 0x89.toByte()), bcdStr3)

    val bcdStr4 = "123456789".toBcd(IAlign.LEFT)

    assertArrayEquals(byteArrayOf(0x12, 0x34, 0x56, 0x78, 0x90.toByte()), bcdStr4)
  }
}
