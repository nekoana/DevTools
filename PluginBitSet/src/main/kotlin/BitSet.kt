import java.nio.ByteBuffer

class BitSet private constructor(private val buffer: ByteBuffer) {
  operator fun get(index: Int): Boolean {
    return buffer.get(index / 8).toInt() and (1 shl (index % 8)) != 0
  }

  fun length(): Int {
    return buffer.capacity() * 8
  }

  companion object {
    fun valueOf(bytes: ByteArray): BitSet {
      return BitSet(ByteBuffer.wrap(bytes))
    }

    fun binaryOf(bitset: String): BitSet {
      // bitset 末尾补0，每8个字符转换为一个字节
      val newBitset = bitset.padEnd((8 - bitset.length % 8), '0')

      val bytes = newBitset.chunked(8).map { it.reversed().toUByte(2).toByte() }.toByteArray()

      return valueOf(bytes)
    }

    fun hexOf(bitset: String): BitSet {
      // bitset 每两个字符转换为一个字节, 末尾补0
      val newBitset = bitset.padEnd((2 - bitset.length % 2), '0')
      val bytes = newBitset.chunked(2).map { it.reversed().toUByte(16).toByte() }.toByteArray()

      return valueOf(bytes)
    }
  }
}
