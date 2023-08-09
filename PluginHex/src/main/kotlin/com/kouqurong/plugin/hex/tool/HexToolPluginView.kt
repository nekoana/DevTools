package com.kouqurong.plugin.hex.tool

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.google.auto.service.AutoService
import com.kouqurong.plugin.view.IPluginView

@AutoService(IPluginView::class)
class HexToolPluginView : IPluginView {
    override val view: @Composable () -> Unit
        get() = {
            App()
        }
    override val icon: @Composable () -> Painter
        get() = {
            rememberVectorPainter(Icons.Filled.Phone)
        }
    override val label: String
        get() = "Hex Tools"


}