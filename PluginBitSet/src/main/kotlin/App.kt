import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

@Composable
fun App() {
  var bitset by remember { mutableStateOf("") }
  var type by remember { mutableStateOf<IBitSetType>(IBitSetType.Hex) }

  Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Column(
        modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Row(
          modifier = Modifier.height(120.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        BitSetInput(bitset = bitset, onBitSetChanged = { bitset = it })
        BitSetType(type = type, onBitSetTypeChanged = { type = it })
      }

      BitSetIndicator(
          bitset = bitset,
          type = type,
          modifier = Modifier.align(Alignment.CenterHorizontally).weight(1F))
    }
  }
}

@Composable
fun BitSetInput(bitset: String, onBitSetChanged: (String) -> Unit, modifier: Modifier = Modifier) {
  OutlinedTextField(value = bitset, onValueChange = onBitSetChanged, modifier = modifier)
}

@Composable
fun BitSetType(
    type: IBitSetType,
    onBitSetTypeChanged: (IBitSetType) -> Unit,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier,
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    types.forEach {
      TypeRadioButton(
          modifier = modifier,
          selected = type == it,
          onClick = { onBitSetTypeChanged(it) },
          label = it.label(),
      )
    }
  }
}

@Composable
fun TypeRadioButton(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
) {
  Surface(
      modifier = modifier,
      shape = MaterialTheme.shapes.small,
      color =
          if (selected) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
          else MaterialTheme.colorScheme.surface,
  ) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
      androidx.compose.material3.RadioButton(
          selected = selected,
          onClick = onClick,
      )

      Text(modifier = Modifier.padding(end = 4.dp), text = label)
    }
  }
}

@Composable
fun BitSetIndicator(bitset: String, type: IBitSetType, modifier: Modifier = Modifier) {
  val isAvailable by remember(bitset, type) { derivedStateOf { type.isAvailable(bitset) } }
  if (isAvailable) {
    val bitsets = type.toBitSet(bitset)

    Column(modifier = modifier) {
      LazyVerticalGrid(
          modifier = Modifier.fillMaxWidth(),
          columns = GridCells.Fixed(8),
          contentPadding = PaddingValues(0.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp),
          horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        for (i in 0 until bitsets.length()) {
          item(key = i) {
            Text(
                text = "${i + 1}",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = TextUnit(32.sp.value, TextUnitType.Sp),
                modifier =
                    Modifier.fillMaxWidth()
                        .background(
                            color =
                                if (bitsets[i]) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(16.dp)),
            )
          }
        }
      }
    }
  }
}

@Preview
@Composable
fun PreviewBitSetTypeRadioButton() {
  TypeRadioButton(selected = true, label = "Hex", onClick = {})
}

val types = listOf(IBitSetType.Hex, IBitSetType.Binary, IBitSetType.Decimal)

sealed interface IBitSetType {
  fun toBitSet(bitset: String): BitSet
  fun isAvailable(bitset: String): Boolean
  fun label(): String {
    return when (this) {
      Hex -> "Hex"
      Binary -> "Binary"
      Decimal -> "Decimal"
    }
  }

  // 16进制
  object Hex : IBitSetType {
    override fun toBitSet(bitset: String): BitSet {
      // bitset 末尾补0，每两个字符转换为一个字节
      val newBitset = bitset.padEnd(bitset.length + bitset.length % 2, '0')

      val bytes = newBitset.chunked(2).map { it.toInt(16).toByte() }.toByteArray()

      return BitSet.valueOf(bytes)
    }

    override fun isAvailable(bitset: String): Boolean {
      // 检查是否是16进制
      return bitset.matches(Regex("[0-9a-fA-F]+"))
    }
  }

  object Binary : IBitSetType {
    override fun toBitSet(bitset: String): BitSet {
      // bitset 末尾补0，每8个字符转换为一个字节
      val newBitset = bitset.padEnd(bitset.length + bitset.length % 8, '0')

      val bytes = newBitset.chunked(8).map { it.toInt(2).toByte() }.toByteArray()

      return BitSet.valueOf(bytes)
    }

    override fun isAvailable(bitset: String): Boolean {
      // 检查是否是二进制
      return bitset.matches(Regex("[0-1]+"))
    }
  }

  object Decimal : IBitSetType {
    override fun toBitSet(bitset: String): BitSet {
      // 十进制转换为字节数组
      val bytes = bitset.toInt().toByteArray()

      return BitSet.valueOf(bytes)
    }

    override fun isAvailable(bitset: String): Boolean {
      // 检查是否是十进制, 不能以0开头
      // todo 检查 最大值为Int.MAX_VALUE ???
      return bitset.matches(Regex("[1-9][0-9]*"))
    }
  }
}

private fun Int.toByteArray(): ByteArray {
  val result = ByteArray(4)

  result[0] = (this shr 24 and 0xFF).toByte()
  result[1] = (this shr 16 and 0xFF).toByte()
  result[2] = (this shr 8 and 0xFF).toByte()
  result[3] = (this and 0xFF).toByte()

  return result
}
