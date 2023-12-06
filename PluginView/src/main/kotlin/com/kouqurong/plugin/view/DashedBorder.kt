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

package com.kouqurong.plugin.view

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.dashedBorder(strokeWidth: Dp, color: Color, cornerRadius: Dp) =
    composed(
        factory = {
          val density = LocalDensity.current
          val strokeWidthPx = density.run { strokeWidth.toPx() }
          val cornerRadiusPx = density.run { cornerRadius.toPx() }

          then(
              Modifier.drawWithCache {
                onDrawBehind {
                  val stroke =
                      Stroke(
                          width = strokeWidthPx,
                          pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                  drawRoundRect(
                      color = color, style = stroke, cornerRadius = CornerRadius(cornerRadiusPx))
                }
              })
        })

fun Modifier.defaultDashedBorder() = dashedBorder(1.dp, Color.Gray, 8.dp)
