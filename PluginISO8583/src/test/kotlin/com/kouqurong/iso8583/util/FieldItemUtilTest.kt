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

import com.kouqurong.iso8583.data.FieldItem
import com.kouqurong.iso8583.data.Align
import com.kouqurong.iso8583.data.Attr
import com.kouqurong.iso8583.data.Format
import java.nio.ByteBuffer
import org.junit.Assert.assertEquals
import org.junit.Test

class FieldItemUtilTest {

  @Test
  fun `parse ascii, fix len 8, align left, padding 0`() {
    val fieldItem =
        FieldItem(
            field = "1",
            attr = Attr.ASCII,
            format = Format.FIX,
            align = Align.LEFT,
            length = "8",
            padding = "0")
    val readable = "00000001"

    val bs = readable.toByteArray()

    val buffer = ByteBuffer.wrap(bs)

    val data = fieldItem.parse(buffer)

    assertEquals(readable, data)
  }

  @Test
  fun `parse ascii, fix len 8, align right, padding 0`() {
    val fieldItem =
        FieldItem(
            field = "1",
            attr = Attr.ASCII,
            format = Format.FIX,
            align = Align.RIGHT,
            length = "8",
            padding = "0")
    val readable = "00000001"

    val bs = readable.toByteArray()

    val buffer = ByteBuffer.wrap(bs)

    val data = fieldItem.parse(buffer)

    assertEquals("00000001", data)
  }

  @Test
  fun `parse ascii,var len 9, align left, padding 0`() {
    val fieldItem =
        FieldItem(
            field = "1",
            attr = Attr.ASCII,
            format = Format.VAR,
            align = Align.RIGHT,
            length = "9",
            padding = "0")
    val readable = "1234567890"

    val bs = readable.toByteArray()

    val buffer = ByteBuffer.allocate(bs.size + 1)

    buffer.put("9".toBcd())
    buffer.put(bs)
    buffer.flip()

    val data = fieldItem.parse(buffer)

    assertEquals("123456789", data)
  }

  @Test
  fun `parse bcd,var len 9, align left, padding 0`() {
    val fieldItem =
        FieldItem(
            field = "1",
            attr = Attr.BCD,
            format = Format.VAR,
            align = Align.RIGHT,
            length = "9",
            padding = "0")
    val readable = "1234567890"

    val bs = readable.toBcd()

    val buffer = ByteBuffer.allocate(bs.size + 1)

    buffer.put("9".toBcd())
    buffer.put(bs)
    buffer.flip()

    val data = fieldItem.parse(buffer)

    assertEquals("123456789", data)
  }

  @Test
  fun `parse bcd,var len 9, align right, padding 0`() {
    val fieldItem =
        FieldItem(
            field = "1",
            attr = Attr.BCD,
            format = Format.VAR,
            align = Align.LEFT,
            length = "9",
            padding = "0")
    val readable = "1234567890"

    val bs = readable.toBcd()

    val buffer = ByteBuffer.allocate(bs.size + 1)

    buffer.put("9".toBcd())
    buffer.put(bs)
    buffer.flip()

    val data = fieldItem.parse(buffer)

    assertEquals("234567890", data)
  }

  @Test
  fun `parse bcd,fix len 9, align right, padding 0`() {
    val fieldItem =
        FieldItem(
            field = "1",
            attr = Attr.BCD,
            format = Format.FIX,
            align = Align.LEFT,
            length = "9",
            padding = "0")
    val readable = "1234567890"

    val bs = readable.toBcd()

    val buffer = ByteBuffer.allocate(bs.size)

    buffer.put(bs)
    buffer.flip()

    val data = fieldItem.parse(buffer)

    assertEquals("234567890", data)
  }

  @Test
  fun `parse bcd,fix len 9, align left, padding 0`() {
    val fieldItem =
        FieldItem(
            field = "1",
            attr = Attr.BCD,
            format = Format.FIX,
            align = Align.RIGHT,
            length = "9",
            padding = "0")
    val readable = "1234567890"

    val bs = readable.toBcd()

    val buffer = ByteBuffer.allocate(bs.size)

    buffer.put(bs)
    buffer.flip()

    val data = fieldItem.parse(buffer)

    assertEquals("123456789", data)
  }

  @Test
  fun `parse binary,fix len 80, align left, padding 0`() {
    val fieldItem =
        FieldItem(
            field = "1",
            attr = Attr.BINARY,
            format = Format.FIX,
            align = Align.LEFT,
            length = "80",
            padding = "0")
    val readable = "1234567890"

    val bs = readable.toByteArray()

    val buffer = ByteBuffer.allocate(bs.size)

    buffer.put(bs)
    buffer.flip()

    val data = fieldItem.parse(buffer)

    assertEquals(bs.toHexStr(), data)
  }

  @Test
  fun `parse binary,var len 72, align left, padding 0`() {
    val fieldItem =
        FieldItem(
            field = "1",
            attr = Attr.BINARY,
            format = Format.VAR,
            align = Align.LEFT,
            length = "72",
            padding = "0")
    val readable = "1234567890"

    val bs = readable.toByteArray()

    val buffer = ByteBuffer.allocate(bs.size + 1)

    buffer.put("72".toBcd())

    buffer.put(bs)
    buffer.flip()

    val data = fieldItem.parse(buffer)

    assertEquals("123456789".toByteArray().toHexStr(), data)
  }
}
