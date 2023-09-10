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

package com.kouqurong.plugin.signin

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import java.time.Duration
import java.time.LocalDateTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(viewModel: SignInViewModel) {
  val libraryVersion = viewModel.libraryVersion.collectAsState()
  val bmp = viewModel.bmp.collectAsState()

  var number by remember { mutableStateOf(TextFieldValue("")) }

  var token by remember { mutableStateOf("") }

  var isWaitSignIn by remember { mutableStateOf(false) }

  val isSignInEnabled = remember {
    derivedStateOf {
      libraryVersion.value is Option.Some &&
          bmp.value is Option.Some &&
          token.isNotBlank() &&
          number.text.isNotBlank()
    }
  }

  Box(modifier = Modifier.padding(16.dp).fillMaxSize()) {
    WarningDialog()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
          LoginInfo(
              token = token,
              number = number,
              editabled = !isWaitSignIn,
              onTokenChange = { token = it },
              onNumberChange = {
                // 判断是否是数字
                if (it.text.all { c -> c.isDigit() }) {
                  number = it
                }
              })

          ApiLibInfo(
              version =
                  when (val v = libraryVersion.value) {
                    Option.None -> "Select Api Library"
                    is Option.Some -> v.value
                  },
              clickabled = !isWaitSignIn,
              onLibChanged = { viewModel.setLibraryFile(it) })

          Row {
            AnimatedContent(
                targetState = isWaitSignIn,
                modifier = Modifier.weight(1F).fillMaxHeight(),
            ) {
              if (it) {
                val countDownTimeState =
                    ProduceCountDownTimeState(
                        viewModel.timePickerState.hour, viewModel.timePickerState.minute)

                val isSignIn by remember {
                  derivedStateOf { countDownTimeState.value == "00:00:00" }
                }
                if (isSignIn) {
                  LaunchedEffect(Unit) { viewModel.signin(number.text.toInt(), token) }

                  isWaitSignIn = false
                }

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                  Text(
                      text = countDownTimeState.value,
                      textAlign = TextAlign.Center,
                      fontSize = MaterialTheme.typography.h3.fontSize,
                      style = MaterialTheme.typography.h3)
                }
              } else {
                TimePicker(state = viewModel.timePickerState, modifier = Modifier.fillMaxSize())
              }
            }

            Column(
                modifier = Modifier.weight(1F), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  BmpInfo(
                      clickable = !isWaitSignIn,
                      paintFor = {
                        when (val v = bmp.value) {
                          Option.None -> null
                          is Option.Some -> v.value
                        }
                      },
                      onBmpChanged = { viewModel.setBmpFile(it) },
                      modifier = Modifier.fillMaxWidth().weight(1F))

                  OutlinedButton(
                      onClick = { isWaitSignIn = !isWaitSignIn },
                      enabled = isSignInEnabled.value,
                      modifier = Modifier.fillMaxWidth()) {
                        Text(text = if (isWaitSignIn) "Cancel" else "Sign In")
                      }
                }
          }
        }

    SnackbarHost(
        hostState = viewModel.snackBarHostState, modifier = Modifier.align(Alignment.BottomCenter))
  }
}

@Composable
fun WarningDialog() {
  var isShowWaringDialog by remember { mutableStateOf(true) }
  if (isShowWaringDialog) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = "Warning", fontWeight = FontWeight.Bold) },
        text = {
          val text = buildAnnotatedString {
            // 加粗
            pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
            append("This application is only for learning and reference, please do not spread it!")
            pop()
            // 如何获取number和token,以firefox为例
            append("\n\n")
            append("How to get number and token? (firefox)")
            append("\n")
            append("1. Open the firefox browser, press F12 to open the developer tool")
            append("\n")
            // 选择localStorage
            append("2. Storage -> Local Storage -> https://info2.paxsz.com")
            append("\n")
            append("3. Click the user key value, and then click the value to copy")
            append("\n")
          }

          Column {
            Text(text = text)
            Image(
                painter = painterResource("firefox.jpg"),
                contentDescription = "firefox",
            )
          }
        },
        confirmButton = {
          TextButton(onClick = { isShowWaringDialog = false }) { Text(text = "OK") }
        },
    )
  }
}

@Composable
fun LoginInfo(
    token: String,
    number: TextFieldValue,
    editabled: Boolean = true,
    onTokenChange: (String) -> Unit,
    onNumberChange: (TextFieldValue) -> Unit
) {
  Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = number,
            enabled = editabled,
            singleLine = true,
            onValueChange = { onNumberChange(it) },
            label = { Text(text = "Number") },
            modifier = Modifier.weight(1F))

        OutlinedTextField(
            value = token,
            enabled = editabled,
            singleLine = true,
            onValueChange = onTokenChange,
            label = { Text(text = "Token") },
            modifier = Modifier.weight(7F))
      }
}

@Composable
fun ApiLibInfo(version: String, clickabled: Boolean, onLibChanged: (String) -> Unit) {
  Text(
      text = version,
      textAlign = TextAlign.Center,
      fontSize = TextUnit(22.sp.value, TextUnitType.Sp),
      style = MaterialTheme.typography.body1,
      modifier =
          Modifier.height(32.dp)
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .dashedBorder(4.dp, Color.Gray, 16.dp)
              .clickable(enabled = clickabled) { fileChooser { onLibChanged(it) } })
}

@Composable
fun BmpInfo(
    clickable: Boolean,
    paintFor: () -> Painter?,
    onBmpChanged: (String) -> Unit,
    modifier: Modifier
) {
  Box(
      modifier =
          modifier.clip(RoundedCornerShape(16.dp)).dashedBorder(4.dp, Color.Gray, 16.dp).clickable(
              enabled = clickable) {
                fileChooser { onBmpChanged(it) }
              }) {
        Text(
            text = "Photos",
            textAlign = TextAlign.Center,
            fontSize = TextUnit(22.sp.value, TextUnitType.Sp),
            style = MaterialTheme.typography.body1,
            modifier = Modifier.align(Alignment.Center))

        paintFor()?.let {
          Image(painter = it, contentDescription = "bmp", modifier = Modifier.fillMaxSize())
        }
      }
}

@Composable
fun ProduceCountDownTimeState(hour: Int, minute: Int) =
    produceState(initialValue = "99:99") {
      // 获取当前时间,判断是否在签到时间范围内，不是则为下一天
      var nowTime = LocalDateTime.now()
      // 判断是否是今天的签到时间
      var signinTime =
          LocalDateTime.of(nowTime.year, nowTime.month, nowTime.dayOfMonth, hour, minute, 0)
      if (signinTime.isBefore(nowTime)) {
        signinTime = signinTime.plusDays(1)
      }
      // 计算等待时间
      while (isActive) {
        nowTime = LocalDateTime.now()
        // 如果当前时间大于等待时间，则开始签到
        if (nowTime.isAfter(signinTime)) {
          value = "00:00:00"
          break
        } else {
          // 否则等待1s,倒计时
          delay(1000 * 1)

          // 更新倒计时，使用waitTime - nowTime
          val duration = Duration.between(nowTime, signinTime).toSeconds()

          val diffHour = duration / (60 * 60)
          val diffMinute = (duration - diffHour * 60 * 60) / 60
          val diffSecond = duration - diffHour * 60 * 60 - diffMinute * 60

          value = String.format("%02d:%02d:%02d", diffHour, diffMinute, diffSecond)
        }
      }
    }

fun fileChooser(filter: String = "*", onFileChoose: (String) -> Unit) {
  val dialog =
      java.awt.FileDialog(ComposeWindow()).apply {
        // file 过滤
        setFilenameFilter { _, name ->
          if (filter == "*") return@setFilenameFilter true
          name.endsWith(filter)
        }
        isVisible = true
      }

  if (dialog.file == null) return
  if (dialog.directory == null) return

  onFileChoose(dialog.directory + dialog.file)
}

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
