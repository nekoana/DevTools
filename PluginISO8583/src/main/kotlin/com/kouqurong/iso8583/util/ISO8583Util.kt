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
import com.kouqurong.shard.bitset.BitSet
import com.kouqurong.shard.utils.toHexByteArray
import java.nio.ByteBuffer

fun parseISO8583HexString(hexString: String, fieldItems: List<FieldItem>): Map<Int, String> {
  // 将fieldItems去除重复的field 保证field唯一 并转换为map
  if (hexString.isEmpty()) return emptyMap()
  if (hexString.length < 8) return emptyMap()

  val fieldsMap = fieldItems.associateBy { it.field }
  if (fieldsMap.isEmpty()) return emptyMap()

  val bs = hexString.toHexByteArray()

  val bitset = bitset(bs)
  val buffer = ByteBuffer.wrap(bs).apply { position(bitset.size()) }

  val retMap = mutableMapOf<Int, String>()

  // field 1..64
  for (i in 1..bitset.length()) {
    if (bitset[i - 1]) {
      val fieldItem = fieldsMap[i.toString()] ?: throw Throwable("field $i not found")

      retMap[i] = fieldItem.parse(buffer)
    }
  }

  return retMap
}

private fun bitset(bs: ByteArray): BitSet {

  val bitset = BitSet.bytesOf(bs, 0, 8)

  if (bitset[0]) {
    return BitSet.bytesOf(bs, 0, 16)
  }

  return bitset
}
