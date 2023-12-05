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

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.kouqurong.plugin.view.defaultDashedBorder

@Composable
fun App() {

  val viewModel = remember { QRCodeViewModel() }

  val bitmapState = viewModel.qrCodeImage.collectAsState(null)

  Box(modifier = Modifier.fillMaxSize().padding(10.dp)) {
    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      EnterQRCode(
          modifier = Modifier.fillMaxHeight().fillMaxWidth(0.3F),
          qrCode = viewModel.qrCode,
          onQRCodeChanged = viewModel::onQRCodeChanged)

      PreviewQRCode(
          modifier = Modifier.fillMaxHeight().fillMaxWidth(),
          qrCode = {
            val bitmap = bitmapState.value
            if (bitmap == null) null else BitmapPainter(bitmap)
          },
      )
    }
  }
}

@Composable
fun EnterQRCode(modifier: Modifier = Modifier, qrCode: String, onQRCodeChanged: (String) -> Unit) {
  Card(
      modifier = modifier,
  ) {
    OutlinedTextField(
        modifier = Modifier.fillMaxSize().padding(8.dp),
        value = qrCode,
        onValueChange = onQRCodeChanged)
  }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PreviewQRCode(
    modifier: Modifier = Modifier,
    qrCode: () -> Painter?,
) {

  val painter = qrCode()

  Card(
      modifier = modifier.clickable {},
  ) {
    if (painter == null) {
      Box(modifier = Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.align(Alignment.Center).defaultDashedBorder().padding(8.dp),
            text = "Select QR Image",
            style = MaterialTheme.typography.titleLarge)
      }
    } else {
      Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Image(
            modifier = Modifier.aspectRatio(1F).align(Alignment.Center),
            painter = painter,
            contentDescription = "QR Code")
      }
    }
  }
}
