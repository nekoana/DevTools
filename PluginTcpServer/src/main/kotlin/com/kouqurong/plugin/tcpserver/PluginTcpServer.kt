package com.kouqurong.plugin.tcpserver

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.google.auto.service.AutoService
import com.kouqurong.plugin.tcpserver.viewmodel.TcpServerViewModel
import com.kouqurong.plugin.view.IPluginView

@AutoService(IPluginView::class)
class PluginTcpServer : IPluginView {
  private val viewModel = TcpServerViewModel()

  override val view: @Composable () -> Unit
    get() = { App(viewModel) }
  override val icon: @Composable () -> Painter
    get() = { rememberVectorPainter(Icons.Filled.Edit) }
  override val label: String
    get() = "Tcp Server"
}
