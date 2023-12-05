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

package com.kouqurong.plugin.qrcode

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.kouqurong.plugin.view.ViewModel
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.jetbrains.skia.Image

class QRCodeViewModel : ViewModel() {
  private val _qrCode = mutableStateOf("")

  val qrCode: String
    get() = _qrCode.value

  @OptIn(FlowPreview::class)
  val qrCodeImage =
      snapshotFlow { qrCode }
          .debounce(1000)
          .distinctUntilChanged()
          .map { generateQRCode(it) }
          .shareIn(viewModelScope, SharingStarted.Lazily, 1)

  fun onQRCodeChanged(qrCode: String) {
    _qrCode.value = qrCode
  }

  private fun generateQRCode(qrCode: String): ImageBitmap? {
    if (qrCode.isEmpty()) return null

    val hintMap = mapOf<EncodeHintType, Any>(EncodeHintType.ERROR_CORRECTION to 'L')
    val qrCodeWriter = MultiFormatWriter()
    val bitMatrix: BitMatrix = qrCodeWriter.encode(qrCode, BarcodeFormat.QR_CODE, 600, 600, hintMap)
    val width = bitMatrix.width
    val height = bitMatrix.height

    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    image.createGraphics()

    for (i in 0 until width) {
      for (j in 0 until height) {
        image.setRGB(i, j, if (bitMatrix[i, j]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
      }
    }

    val bos = ByteArrayOutputStream()

    ImageIO.write(image, "bmp", bos)

    return bos.toByteArray().toImageBitmap()
  }

  private fun ByteArray.toImageBitmap(): ImageBitmap =
      Image.makeFromEncoded(this).toComposeImageBitmap()
}
