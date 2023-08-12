package com.kouqurong.plugin.hex.tool

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun App() {
  val viewModel = remember { HexViewModel() }

  val scope = rememberCoroutineScope()

  val hexString = viewModel.hexString.collectAsState()

  Box(modifier = Modifier.padding(16.dp).fillMaxSize()) {
    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp)) {
      OutlinedTextField(
          value = hexString.value,
          onValueChange = {
            scope.launch {
              viewModel.updateHexString(it)
              println(it)
            }
          },
          isError = viewModel.isErrorHexString.value,
          label = { Text("Hex String") },
          modifier = Modifier.fillMaxWidth().height(120.dp))

      Button(onClick = { scope.launch { viewModel.formatHexString(hexString.value.text) } }) {
        Text("Format")
      }

      Button(onClick = { scope.launch { viewModel.upperCaseHexString(hexString.value.text) } }) {
        Text("UpperCase")
      }

      Button(
          onClick = {
            scope.launch {
              with(hexString.value) {
                val selection =
                    text.replace(" ", "").chunked(2).map { it.toInt(16).toChar() }.joinToString("")

                println("asciiString: $selection")
                println(this)
              }
            }
          }) {
            Text("ASCII")
          }
    }
  }
}
