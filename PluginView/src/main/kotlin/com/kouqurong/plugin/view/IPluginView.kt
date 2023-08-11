package com.kouqurong.plugin.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

interface IPluginView {
    val view: @Composable () -> Unit
    val icon: @Composable () -> Painter
    val label: String
}