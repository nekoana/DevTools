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

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.kouqurong.iso8583.viewmodel.*

@Composable
fun FieldItem(
    modifier: Modifier = Modifier,
    field: Int,
    attr: IAttr = IAttr.ASCII,
    format: IFormat = IFormat.FIX,
    align: IAlign = IAlign.LEFT,
) {
  var fieldTextValue by remember { mutableStateOf(TextFieldValue(field.toString())) }
  var padding by remember { mutableStateOf(TextFieldValue("0")) }

  var isShowAttrMenu by remember { mutableStateOf(false) }
  var isShowFormatMenu by remember { mutableStateOf(false) }
  var isShowAlignMenu by remember { mutableStateOf(false) }
  var length by remember { mutableStateOf(11) }

  Row(
      modifier = modifier,
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    FieldInputItem(field = 1) {}
    AttrSelectItem(attr = attr) {}
    LengthAndFormatItem(
        length = length, format = format, onLengthChange = { length = it }, onFormatChange = {})
    PaddingAndAlignItem(padding = "0", align = align, onPaddingChange = {}, onAlignChange = {})
  }
}

@Composable
fun FieldInputItem(modifier: Modifier = Modifier, field: Int, onFieldChange: (Int) -> Unit) {
  // Holds the latest internal TextFieldValue state. We need to keep it to have the correct value
  // of the composition.
  var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = field.toString())) }
  // Holds the latest TextFieldValue that BasicTextField was recomposed with. We couldn't simply
  // pass `TextFieldValue(text = value)` to the CoreTextField because we need to preserve the
  // composition.
  val textFieldValue = textFieldValueState.copy(text = field.toString())

  SideEffect {
    if (textFieldValue.selection != textFieldValueState.selection ||
        textFieldValue.composition != textFieldValueState.composition) {
      textFieldValueState = textFieldValue
    }
  }

  var lastTextValue by remember(field) { mutableStateOf(field.toString()) }

  NumberTextField(
      modifier = Modifier.width(48.dp).height(IntrinsicSize.Min),
      value = textFieldValue,
      maxLength = 3,
      onValueChange = { newTextFieldValueState ->
        textFieldValueState = newTextFieldValueState

        val stringChangedSinceLastInvocation = lastTextValue != newTextFieldValueState.text
        lastTextValue = newTextFieldValueState.text

        if (stringChangedSinceLastInvocation) {
          onFieldChange(newTextFieldValueState.text.toIntOrNull() ?: 0)
        }
      },
      tooltip = { Text("Field") })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AttrSelectItem(modifier: Modifier = Modifier, attr: IAttr, onAttrChange: (IAttr) -> Unit) {
  var isShowAttrMenu by remember { mutableStateOf(false) }

  TooltipArea(tooltip = { Text(text = "Attr") }) {
    Row(
        modifier = modifier.clickable { isShowAttrMenu = !isShowAttrMenu },
    ) {
      Text(text = attr.value)
      Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Drop Down")

      DropdownMenu(isShowAttrMenu, onDismissRequest = { isShowAttrMenu = false }) {
        AttrList.forEach { attr ->
          DropdownMenuItem(text = { Text(attr.value) }, onClick = { onAttrChange(attr) })
        }
      }
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LengthAndFormatItem(
    modifier: Modifier = Modifier,
    length: Int,
    format: IFormat,
    onLengthChange: (Int) -> Unit,
    onFormatChange: (IFormat) -> Unit,
) {
  // Holds the latest internal TextFieldValue state. We need to keep it to have the correct value
  // of the composition.
  var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = length.toString())) }
  // Holds the latest TextFieldValue that BasicTextField was recomposed with. We couldn't simply
  // pass `TextFieldValue(text = value)` to the CoreTextField because we need to preserve the
  // composition.
  val textFieldValue = textFieldValueState.copy(text = length.toString())

  SideEffect {
    if (textFieldValue.selection != textFieldValueState.selection ||
        textFieldValue.composition != textFieldValueState.composition) {
      textFieldValueState = textFieldValue
    }
  }

  var lastTextValue by remember(length) { mutableStateOf(length.toString()) }

  var isShowFormatMenu by remember { mutableStateOf(false) }
  Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
    NumberTextField(
        modifier = Modifier.width(64.dp),
        value = textFieldValue,
        maxLength = 4,
        onValueChange = { newTextFieldValueState ->
          textFieldValueState = newTextFieldValueState

          val stringChangedSinceLastInvocation = lastTextValue != newTextFieldValueState.text
          lastTextValue = newTextFieldValueState.text

          if (stringChangedSinceLastInvocation) {
            onLengthChange(newTextFieldValueState.text.toIntOrNull() ?: 0)
          }
        },
        tooltip = { Text(text = "Length") })

    TooltipArea(tooltip = { Text(text = "Format") }) {
      Row(
          modifier = Modifier.clickable { isShowFormatMenu = !isShowFormatMenu },
      ) {
        Text(text = format.value)
        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Drop Down")

        DropdownMenu(isShowFormatMenu, onDismissRequest = { isShowFormatMenu = false }) {
          FormatList.forEach { format ->
            DropdownMenuItem(text = { Text(format.value) }, onClick = { onFormatChange(format) })
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PaddingAndAlignItem(
    modifier: Modifier = Modifier,
    padding: String,
    align: IAlign,
    onPaddingChange: (String) -> Unit,
    onAlignChange: (IAlign) -> Unit
) {
  var isShowAlignMenu by remember { mutableStateOf(false) }

  Row(modifier = modifier) {
    SingleChatTextField(
        modifier = Modifier.width(24.dp),
        value = padding,
        onValueChange = { onPaddingChange(it) },
        tooltip = { Text(text = "Padding") })
    TooltipArea(tooltip = { Text(text = "Align") }) {
      Row(
          modifier = Modifier.clickable { isShowAlignMenu = !isShowAlignMenu },
      ) {
        Text(text = align.value)
        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Drop Down")

        DropdownMenu(isShowAlignMenu, onDismissRequest = { isShowAlignMenu = false }) {
          AlignList.forEach { align ->
            DropdownMenuItem(text = { Text(align.value) }, onClick = { onAlignChange(align) })
          }
        }
      }
    }
  }
}

@Composable
@Preview
fun PreviewFieldItem() {
  FieldItem(
      modifier =
          Modifier.fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(Color.Gray)
              .padding(8.dp),
      field = 1,
  )
}
