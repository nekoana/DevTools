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

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun FieldItem(
    modifier: Modifier = Modifier,
    field: Int,
) {
  var fieldTextValue by remember { mutableStateOf(TextFieldValue(field.toString())) }
  var padding by remember { mutableStateOf(TextFieldValue("0")) }

  var isShowFormatMenu by remember { mutableStateOf(false) }
  var isShowLengthMenu by remember { mutableStateOf(false) }
  var length by remember { mutableStateOf(TextFieldValue("11")) }
  var isShowAlignMenu by remember { mutableStateOf(false) }

  Row(
      modifier = modifier,
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    NumberTextField(
        modifier = Modifier.width(48.dp).height(IntrinsicSize.Min),
        value = fieldTextValue,
        maxLength = 3,
        onValueChange = { fieldTextValue = it },
    )

    androidx.compose.material.IconButton(onClick = { isShowFormatMenu = true }) {
      Row {
        Text(text = "ASCII")
        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Drop Down")

        DropdownMenu(isShowFormatMenu, onDismissRequest = { isShowFormatMenu = false }) {
          DropdownMenuItem(text = { Text("ASCII") }, onClick = {})
          DropdownMenuItem(text = { Text("BCD") }, onClick = {})
          DropdownMenuItem(text = { Text("BINARY") }, onClick = {})
        }
      }
    }

    Row(modifier = Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
      NumberTextField(
          modifier = Modifier.width(64.dp),
          value = length,
          maxLength = 4,
          onValueChange = { length = it })

      androidx.compose.material.IconButton(onClick = { isShowLengthMenu = true }) {
        Row {
          Text(text = "VAR")
          Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Drop Down")

          DropdownMenu(isShowLengthMenu, onDismissRequest = { isShowLengthMenu = false }) {
            DropdownMenuItem(text = { Text("VAR") }, onClick = {})
            DropdownMenuItem(text = { Text("FIX") }, onClick = {})
          }
        }
      }
    }

    SingleChatTextField(
        modifier = Modifier.width(24.dp), value = padding, onValueChange = { padding = it })

    androidx.compose.material.IconButton(onClick = { isShowLengthMenu = true }) {
      Row {
        Text(text = "Left")
        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Drop Down")

        DropdownMenu(isShowAlignMenu, onDismissRequest = { isShowAlignMenu = false }) {
          DropdownMenuItem(text = { Text("Left") }, onClick = {})
          DropdownMenuItem(text = { Text("Right") }, onClick = {})
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
