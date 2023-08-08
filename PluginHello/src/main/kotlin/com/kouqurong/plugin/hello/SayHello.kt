package com.kouqurong.plugin.hello

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*

import androidx.compose.ui.window.Dialog


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


    Dialog(
        visible = visible,
        onCloseRequest = {
            visible = false
        }
    ) {
        Text("Hello, World! From Plugin")
    }
}