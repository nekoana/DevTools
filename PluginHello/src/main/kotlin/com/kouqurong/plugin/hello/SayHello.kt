package com.kouqurong.plugin.hello

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window


@Composable
fun sayHello() {
    var visible by remember { mutableStateOf(false) }
    Button(
        onClick = {
            visible = true
        },
        content = {
            Text("Hello, World! From Plugin")
        }
    )


    Window(
        onCloseRequest = {
            visible = false
        },
        visible = visible
    ) {
        Text("Hello, World! From Plugin")
    }
}