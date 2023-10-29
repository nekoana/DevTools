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

package com.kouqurong.shard.bitset

import java.nio.ByteBuffer

sealed class BitSet private constructor(protected val buffer: ByteBuffer) {
  class BinaryBitSet(buffer: ByteBuffer) : BitSet(buffer)

  class HexBitSet(buffer: ByteBuffer) : BitSet(buffer) {
    override operator fun get(index: Int): Boolean {
      return buffer.get(index / 8).toInt() and (1 shl (7 - (index % 8))) != 0
    }
  }

  open operator fun get(index: Int): Boolean {
    return buffer.get(index / 8).toInt() and (1 shl (index % 8)) != 0
  }

  fun length(): Int {
    return buffer.capacity() * 8
  }

  companion object {

    fun binaryOf(bitset: String): BitSet {
      // bitset 末尾补0，每8个字符转换为一个字节
      val newBitset = bitset.padEnd((8 - bitset.length % 8), '0')

      val bytes = newBitset.chunked(8).map { it.reversed().toUByte(2).toByte() }.toByteArray()

      return BinaryBitSet(ByteBuffer.wrap(bytes))
    }

    fun hexOf(bitset: String): BitSet {
      // bitset 每两个字符转换为一个字节, 末尾补0
      val newBitset = bitset.padEnd((2 - bitset.length % 2), '0')
      val bytes = newBitset.chunked(2).map { it.toUByte(16).toByte() }.toByteArray()

      return HexBitSet(ByteBuffer.wrap(bytes))
    }
  }
}
