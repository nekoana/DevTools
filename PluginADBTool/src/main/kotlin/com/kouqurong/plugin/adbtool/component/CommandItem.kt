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

package com.kouqurong.plugin.adbtool.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kouqurong.plugin.database.Command

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommandItem(
    modifier: Modifier = Modifier,
    command: Command,
    onClick: (Command) -> Unit,
) {
  ElevatedCard(
      modifier = modifier,
      onClick = { onClick(command) },
  ) {
    Column(modifier = Modifier.padding(8.dp)) {
      Text(
          text = command.name,
          style = MaterialTheme.typography.titleMedium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
      )

      Text(
          text = command.description,
          style = MaterialTheme.typography.bodySmall,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

@Composable
@Preview
fun PreviewCommandItem() {
  CommandItem(
      modifier = Modifier.size(120.dp, 80.dp),
      command =
          Command(
              id = 1,
              name = "Top Activity",
              description = "description  description description description description",
              arguments = "command",
              keywords = "keywords",
              background = 0L,
          ),
      onClick = {})
}
