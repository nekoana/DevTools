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

package com.kouqurong.devtools

import androidx.compose.animation.AnimatedContent
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.kouqurong.devtools.viewmodel.HostViewModel
import com.kouqurong.devtools.viewmodel.PluginViewWindowState
import com.kouqurong.plugin.view.IPluginView
import java.awt.MouseInfo
import java.awt.Point
import java.awt.Window
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter

@Composable
fun Home(views: List<IPluginView>, onDisplay: (IPluginView) -> Unit) {
  LazyVerticalGrid(
      columns = GridCells.FixedSize(120.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      contentPadding = PaddingValues(16.dp)) {
        items(views) {
          ElevatedCard(
              modifier =
                  Modifier.size(120.dp, 80.dp).clip(RoundedCornerShape(8.dp)).clickable {
                    onDisplay(it)
                  },
              elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                Column(modifier = Modifier.padding(8.dp)) {
                  Image(
                      modifier = Modifier.size(40.dp, 40.dp),
                      painter = it.icon(),
                      contentDescription = it.label)
                  Text(
                      it.label,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                  )
                }
              }
        }
      }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App(
    window: Window,
    onClose: () -> Unit,
    onMinimize: () -> Unit,
    onBack: () -> Unit,
    onDisplay: (IPluginView) -> Unit,
    onMoveUp: () -> Unit,
    content: @Composable () -> Unit
) {
  MaterialTheme {
    Scaffold(
        modifier = Modifier.clip(RoundedCornerShape(16.dp)),
        topBar = {
          TopAppBar(
              modifier = Modifier.draggableArea(window),
              title = { Text(text = "DevTools") },
              navigationIcon = {
                Row {
                  IconButton(onClick = { onClose() }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                  }

                  IconButton(onClick = { onMinimize() }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Minimize")
                  }

                  IconButton(onClick = { onBack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                  }
                }
              },
              actions = {
//                IconButton(
//                    onClick = {},
//                ) {
//                  Icon(imageVector = Icons.Default.Settings, contentDescription = "Setting")
//                }
                IconButton(onClick = { onMoveUp() }) {
                  Icon(imageVector = Icons.Default.MoveUp, contentDescription = "Float")
                }
              })
        },
    ) {
      Surface(modifier = Modifier.padding(top = it.calculateTopPadding()).fillMaxSize()) {
        content()
      }
    }
  }
}

@Composable
fun ApplicationScope.PluginViewWindow(state: PluginViewWindowState) =
    Window(onCloseRequest = state::close, title = state.pluginView.label, resizable = false) {
      MaterialTheme { Surface { state.pluginView.view() } }
    }

@Composable
fun ApplicationScope.HostWindow(
    viewModel: HostViewModel,
    windowState: WindowState = rememberWindowState(),
    onCloseRequest: () -> Unit = ::exitApplication,
    title: String = "DevTools",
    icon: Painter = painterResource("icon.svg")
) =
    Window(
        onCloseRequest = onCloseRequest,
        state = windowState,
        title = title,
        undecorated = true,
        transparent = true,
        resizable = false,
        icon = icon) {
          var displayPluginView by remember { mutableStateOf<IPluginView?>(null) }

          App(
              window,
              onClose = { onCloseRequest() },
              onMinimize = { windowState.isMinimized = true },
              onBack = {
                if (displayPluginView?.onBack()?.not() == true) {
                  displayPluginView = null
                }
              },
              onDisplay = { displayPluginView = it },
              onMoveUp = {
                if (displayPluginView != null) {
                  viewModel.openNewPluginViewWindow(displayPluginView!!)
                  displayPluginView = null
                }
              }) {
                AnimatedContent(
                    targetState = displayPluginView,
                ) {
                  if (it != null) {
                    it.view()
                  } else {
                    Home(views = viewModel.pluginViews, onDisplay = { displayPluginView = it })
                  }
                }
              }
        }

fun Modifier.draggableArea(window: Window) =
    then(
        Modifier.composed {
          class DragHandler(private val window: Window) {
            private var location = window.location.toComposeOffset()
            private var pointStart = MouseInfo.getPointerInfo().location.toComposeOffset()

            private val dragListener =
                object : MouseMotionAdapter() {
                  override fun mouseDragged(event: MouseEvent) = drag()
                }
            private val removeListener =
                object : MouseAdapter() {
                  override fun mouseReleased(event: MouseEvent) {
                    window.removeMouseMotionListener(dragListener)
                    window.removeMouseListener(this)
                  }
                }

            fun register() {
              location = window.location.toComposeOffset()
              pointStart = MouseInfo.getPointerInfo().location.toComposeOffset()
              window.addMouseListener(removeListener)
              window.addMouseMotionListener(dragListener)
            }

            private fun drag() {
              val point = MouseInfo.getPointerInfo().location.toComposeOffset()
              val location = location + (point - pointStart)
              window.setLocation(location.x, location.y)
            }

            private fun Point.toComposeOffset() = IntOffset(x, y)
          }

          val handler = remember { DragHandler(window) }

          Modifier.pointerInput(Unit) {
            awaitEachGesture {
              awaitFirstDown()
              handler.register()
            }
          }
        })

fun main() = application {
  val viewModel = remember { HostViewModel() }

  LaunchedEffect(viewModel) { viewModel.loadSelfPluginView() }

  val pluginViewWindowState = remember { viewModel.pluginViewWindowState }
  val windowState = rememberWindowState()

  var isOnTray by remember { mutableStateOf(false) }

  if (isOnTray) {
    Tray(
        icon = painterResource("icon.svg"),
        menu = {
          Item(text = "Quit", onClick = { exitApplication() })

          Separator()

          Item(text = "Open", onClick = { isOnTray = false })
        })
  } else {
    HostWindow(
        viewModel = viewModel, windowState = windowState, onCloseRequest = { isOnTray = true })

    for (state in pluginViewWindowState) {
      key(state) { PluginViewWindow(state = state) }
    }
  }
}
