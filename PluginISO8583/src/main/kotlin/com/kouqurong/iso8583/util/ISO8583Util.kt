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

// todo
fun parseISO8583HexString(hexString: String, fieldItems: List<FieldItem>): List<Int> {
  // 将fieldItems去除重复的field 保证field唯一 并转换为map
  if (hexString.isEmpty()) return emptyList()
  if (hexString.length < 8) return emptyList()

  val fields = fieldItems.associateBy { it.field }
  if (fields.isEmpty()) return emptyList()

  val bs = hexString.toHexByteArray()

  val bitset = bitset(bs)
  val buffer = ByteBuffer.wrap(bs).apply { position(bitset.bytesCount()) }

  val list = mutableListOf<Int>()

  // field 1..64
  for (i in 1..bitset.length()) {
    if (bitset[i - 1]) {
      list.add(i)
    }
  }

  return list
}

private fun bitset(bs: ByteArray): BitSet {
  // todo 暂时认为前8字节为位图
  val buffer = ByteBuffer.allocate(8)

  buffer.put(bs, 0, 8)

  return BitSet.bufferOf(buffer)
}
