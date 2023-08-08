package com.kouqurong.plugin.hello

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
}