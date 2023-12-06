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

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp

enum class MultiFloatActionState {
  Expended,
  Collapsed
}

@Composable
fun MultiFloatActionButton(
    modifier: Modifier = Modifier,
    state: MultiFloatActionState,
    onMultiFloatActionStateChange: (MultiFloatActionState) -> Unit,
    items: List<FloatActionItem>,
) {
  val transition = updateTransition(targetState = state, label = "transition")

  val rotate by transition.animateFloat { if (it == MultiFloatActionState.Expended) 315F else 0F }

  val fabScale by transition.animateFloat { if (it == MultiFloatActionState.Expended) 1F else 0F }

  val fabAlpha by
      transition.animateFloat(
          transitionSpec = {
            if (targetState == MultiFloatActionState.Expended) {
              tween(durationMillis = 300)
            } else {
              tween(durationMillis = 300)
            }
          }) {
            if (it == MultiFloatActionState.Expended) 1F else 0F
          }

  Column(
      horizontalAlignment = Alignment.End,
      verticalArrangement = Arrangement.SpaceBetween,
  ) {
    // todo 在执行隐藏动画时，未执行，导致突然消失，需要解决
    if (state == MultiFloatActionState.Expended) {
      items.forEach {
        FloatAction(
            modifier = Modifier.scale(fabScale).alpha(fabAlpha).padding(bottom = 8.dp),
            item = it,
            onClick = {})
      }
    }

    FloatingActionButton(
        modifier = modifier,
        onClick = {
          if (transition.currentState == MultiFloatActionState.Expended) {
            onMultiFloatActionStateChange(MultiFloatActionState.Collapsed)
          } else {
            onMultiFloatActionStateChange(MultiFloatActionState.Expended)
          }
        }) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.rotate(rotate))
        }
  }
}

data class FloatActionItem(
    val label: String,
)

@Composable
fun FloatAction(modifier: Modifier = Modifier, item: FloatActionItem, onClick: () -> Unit) {
  FloatingActionButton(modifier = modifier, onClick = onClick) { Text(text = item.label) }
}
