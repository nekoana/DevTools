package com.kouqurong.plugin.hello

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.google.auto.service.AutoService
import com.kouqurong.plugin.view.IPluginView

@AutoService(IPluginView::class)
class PluginHello : IPluginView {
    override val view: @Composable () -> Unit
        get() = {
            Box {
                sayHello()
            }
        }

    override val icon: @Composable () -> Painter
        get() = {
            rememberVectorPainter(Icons.Filled.Add)
        }
    override val label: String
        get() = "Hello"
}