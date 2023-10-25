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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.kouqurong.iso8583.viewmodel.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AnimationSizeFieldItem(modifier: Modifier = Modifier, fieldItem: FieldItem) {

  var isHover by remember { mutableStateOf(false) }

  val fractionAnim by animateFloatAsState(if (isHover) 1F else 0.95F)

  Box(
      modifier =
          modifier
              .onPointerEvent(PointerEventType.Enter) { isHover = true }
              .onPointerEvent(PointerEventType.Exit) { isHover = false },
      contentAlignment = Alignment.Center) {
        Card(modifier = Modifier.fillMaxSize(fraction = fractionAnim)) {
          Row(
              modifier = Modifier.padding(8.dp).fillMaxSize(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            FieldInputItem(field = fieldItem.field) {}
            AttrSelectItem(attr = fieldItem.attr) {}
            LengthAndFormatItem(
                length = fieldItem.length,
                format = fieldItem.format,
                onLengthChange = {},
                onFormatChange = {})
            PaddingAndAlignItem(
                padding = fieldItem.padding,
                align = fieldItem.align,
                onPaddingChange = {},
                onAlignChange = {})

            if (isHover) {
              IconButton(onClick = {}) { Icon(imageVector = Icons.Default.Delete, "Delete") }
            }
          }
        }
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
    length: String,
    format: IFormat,
    onLengthChange: (String) -> Unit,
    onFormatChange: (IFormat) -> Unit,
) {
  var isShowFormatMenu by remember { mutableStateOf(false) }
  Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
    NumberTextField(
        modifier = Modifier.width(64.dp),
        value = length,
        maxLength = 4,
        onValueChange = onLengthChange,
        tooltip = { Text(text = "Length") })

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
    SingleCharTextField(
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
