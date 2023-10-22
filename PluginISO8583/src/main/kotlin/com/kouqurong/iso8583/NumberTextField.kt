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

package com.kouqurong.iso8583

import androidx.compose.animation.Animatable
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kouqurong.plugin.view.underline
import kotlinx.coroutines.delay

@Composable
fun NumberTextField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLength: Int = Int.MAX_VALUE,
    onValueChange: (TextFieldValue) -> Unit,
) {
  FixedLengthTextField(
      modifier = modifier,
      value = value,
      readOnly = readOnly,
      singleLine = singleLine,
      maxLength = maxLength,
      onValueChange = onValueChange,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center))
}

@Composable
fun SingleChatTextField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    readOnly: Boolean = false,
    onValueChange: (TextFieldValue) -> Unit,
) {
  FixedLengthTextField(
      modifier = modifier,
      value = value,
      readOnly = readOnly,
      singleLine = true,
      maxLength = 1,
      onValueChange = onValueChange,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center))
}

@Composable
fun FixedLengthTextField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLength: Int = Int.MAX_VALUE,
    onValueChange: (TextFieldValue) -> Unit,
    textStyle: TextStyle = LocalTextStyle.current,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
  val isOverLength by remember(value) { derivedStateOf { value.text.length > maxLength } }

  var lastValue by remember { mutableStateOf(value) }
  SideEffect { if (!isOverLength) lastValue = value }

  val errorColor = MaterialTheme.colorScheme.error
  val normalColor = MaterialTheme.colorScheme.onBackground

  val underlineColor = remember { Animatable(if (isOverLength) errorColor else normalColor) }

  LaunchedEffect(isOverLength, value) {
    if (isOverLength) underlineColor.animateTo(errorColor)
    delay(1000)
    underlineColor.animateTo(normalColor)
  }

  BasicTextField(
      modifier = modifier,
      value = if (isOverLength) lastValue else value,
      onValueChange = onValueChange,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      readOnly = readOnly,
      singleLine = singleLine,
      textStyle = textStyle,
      decorationBox = { innerTextField ->
        Box(
            modifier = Modifier.underline(underlineColor.value),
        ) {
          innerTextField()
        }
      })
}

@Composable
@Preview
fun PreviewNumberTextField() {
  NumberTextField(
      modifier = Modifier.width(72.dp).height(IntrinsicSize.Min).background(Color.Red),
      value = TextFieldValue("123"),
      onValueChange = {},
  )
}
