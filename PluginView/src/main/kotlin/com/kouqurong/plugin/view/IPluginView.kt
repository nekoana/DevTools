package com.kouqurong.plugin.view

import androidx.compose.runtime.Composable

interface IPluginView {
    val view: @Composable () -> Unit
}