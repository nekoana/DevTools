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

package com.kouqurong.iso8583

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kouqurong.iso8583.componet.*
import com.kouqurong.iso8583.viewmodel.PluginISO8583ViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun App(viewModel: PluginISO8583ViewModel) {
  Surface(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    ISO8583Content(
        modifier = Modifier.fillMaxSize(),
        iso8583Hex = viewModel.iso8583Hex,
        fieldItems = viewModel.fieldItems,
        fieldMenuItems = viewModel.fieldMenuItems,
        scrollFieldDetailState = viewModel.scrollFieldDetailState,
        swipeCrossFadeState = viewModel.swipeCrossFadeState,
        onFieldItemIntent = viewModel::fieldItemIntent,
        onISO8583HexIntent = viewModel::iso8583HexIntent,
    )
  }
}
