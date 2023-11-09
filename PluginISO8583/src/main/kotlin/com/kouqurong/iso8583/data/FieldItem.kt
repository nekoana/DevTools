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

package com.kouqurong.iso8583.data

data class FieldItem(
    val field: String,
    val attr: IAttr = IAttr.ASCII,
    val format: IFormat = IFormat.FIX,
    val align: IAlign = IAlign.LEFT,
    val length: String = "0",
    val padding: String = "0",
)

sealed class IAttr(val value: String) {
  data object ASCII : IAttr("ASCII")

  data object BCD : IAttr("BCD")

  data object BINARY : IAttr("BINARY")
}

val AttrList = listOf(IAttr.ASCII, IAttr.BCD, IAttr.BINARY)

sealed class IFormat(val value: String) {
  data object VAR : IFormat("VAR")

  data object FIX : IFormat("FIX")
}

val FormatList = listOf(IFormat.VAR, IFormat.FIX)

sealed class IAlign(val value: String) {
  data object LEFT : IAlign("LEFT")

  data object RIGHT : IAlign("RIGHT")
}

val AlignList = listOf(IAlign.LEFT, IAlign.RIGHT)
