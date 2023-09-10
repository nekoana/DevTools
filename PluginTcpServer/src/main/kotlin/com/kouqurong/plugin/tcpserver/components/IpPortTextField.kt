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

package com.kouqurong.plugin.tcpserver.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun IpPortTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    focusRequester: FocusRequester,
) {
  BasicTextField(
      readOnly = false,
      value = value,
      onValueChange = onValueChange,
      modifier =
          Modifier.padding(horizontal = 10.dp)
              .clip(RoundedCornerShape(8.dp))
              .wrapContentSize()
              .background(Color.DarkGray)
              .focusRequester(focusRequester),
      maxLines = 1,
      decorationBox = { innerTextField ->
        Row(
            modifier = Modifier.background(Color.White),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
              Card(modifier = Modifier.fillMaxHeight().weight(4F)) { Text("HEllo") }
              Spacer(modifier = Modifier.width(16.dp))
              Card(modifier = Modifier.fillMaxHeight().weight(4F)) { innerTextField() }
            }
      },
      textStyle = TextStyle(color = Color.Black, fontSize = 26.sp),
      keyboardOptions =
          KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
      keyboardActions = KeyboardActions(onDone = null))
}
