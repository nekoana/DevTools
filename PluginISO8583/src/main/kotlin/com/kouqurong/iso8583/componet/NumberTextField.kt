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

package com.kouqurong.iso8583.componet

import androidx.compose.animation.Animatable
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
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
    value: String,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    isError: Boolean = false,
    maxLength: Int = Int.MAX_VALUE,
    onValueChange: (String) -> Unit,
    tooltip: @Composable () -> Unit,
) {
  FixedLengthTextField(
      modifier = modifier,
      value = value,
      readOnly = readOnly,
      singleLine = singleLine,
      isError = isError,
      maxLength = maxLength,
      onValueChange = onValueChange,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
      tooltip = tooltip)
}

@Composable
fun NumberTextField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    isError: Boolean = false,
    maxLength: Int = Int.MAX_VALUE,
    onValueChange: (TextFieldValue) -> Unit,
    tooltip: @Composable () -> Unit,
) {
  FixedLengthTextField(
      modifier = modifier,
      value = value,
      readOnly = readOnly,
      singleLine = singleLine,
      isError = isError,
      maxLength = maxLength,
      onValueChange = onValueChange,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
      tooltip = tooltip)
}

@Composable
fun SingleCharTextField(
    modifier: Modifier = Modifier,
    value: String,
    readOnly: Boolean = false,
    isError: Boolean = false,
    onValueChange: (String) -> Unit,
    tooltip: @Composable () -> Unit,
) {
  FixedLengthTextField(
      modifier = modifier,
      value = value,
      readOnly = readOnly,
      singleLine = true,
      isError = isError,
      maxLength = 1,
      onValueChange = onValueChange,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
      textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
      tooltip = tooltip)
}

@Composable
fun FixedLengthTextField(
    modifier: Modifier = Modifier,
    value: String,
    readOnly: Boolean = false,
    isError: Boolean = false,
    singleLine: Boolean = true,
    maxLength: Int = Int.MAX_VALUE,
    onValueChange: (String) -> Unit,
    textStyle: TextStyle = LocalTextStyle.current,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    tooltip: @Composable () -> Unit,
) {
  // Holds the latest internal TextFieldValue state. We need to keep it to have the correct value
  // of the composition.
  var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }
  // Holds the latest TextFieldValue that BasicTextField was recomposed with. We couldn't simply
  // pass `TextFieldValue(text = value)` to the CoreTextField because we need to preserve the
  // composition.
  val textFieldValue = textFieldValueState.copy(text = value)

  SideEffect {
    if (textFieldValue.selection != textFieldValueState.selection ||
        textFieldValue.composition != textFieldValueState.composition) {
      textFieldValueState = textFieldValue
    }
  }

  var lastTextValue by remember(value) { mutableStateOf(value) }

  FixedLengthTextField(
      modifier = modifier,
      value = textFieldValue,
      readOnly = readOnly,
      singleLine = singleLine,
      isError = isError,
      maxLength = maxLength,
      onValueChange = { newTextFieldValueState ->
        textFieldValueState = newTextFieldValueState

        val stringChangedSinceLastInvocation = lastTextValue != newTextFieldValueState.text
        lastTextValue = newTextFieldValueState.text

        if (stringChangedSinceLastInvocation) {
          onValueChange(newTextFieldValueState.text)
        }
      },
      textStyle = textStyle,
      keyboardOptions = keyboardOptions,
      tooltip = tooltip)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FixedLengthTextField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    isError: Boolean = false,
    maxLength: Int = Int.MAX_VALUE,
    onValueChange: (TextFieldValue) -> Unit,
    textStyle: TextStyle = LocalTextStyle.current,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    tooltip: @Composable () -> Unit,
) {
  val isOverLength by remember(value) { derivedStateOf { value.text.length > maxLength } }

  var lastTextValue by remember { mutableStateOf(value) }
  SideEffect { if (!isOverLength) lastTextValue = value }

  val errorColor = MaterialTheme.colorScheme.error
  val normalColor = MaterialTheme.colorScheme.onBackground

  val underlineColor =
      remember(isOverLength, isError) {
        Animatable(if (isOverLength || isError) errorColor else normalColor)
      }

  LaunchedEffect(isOverLength, isError, value) {
    if (isOverLength) underlineColor.animateTo(errorColor)
    if (!isError) {
      delay(1000)
      underlineColor.animateTo(normalColor)
    }
  }
  TooltipArea(tooltip = tooltip) {
    BasicTextField(
        modifier = modifier,
        value = if (isOverLength) lastTextValue else value,
        onValueChange = onValueChange,
        keyboardOptions = keyboardOptions,
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
}

@Composable
@Preview
fun PreviewNumberTextField() {
  NumberTextField(
      modifier = Modifier.width(72.dp).height(IntrinsicSize.Min).background(Color.Red),
      value = TextFieldValue("123"),
      onValueChange = {},
      tooltip = {},
  )
}
