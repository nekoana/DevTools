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

import kotlinx.serialization.Serializable

data class FieldItem(
    val field: String,
    val attr: Attr = Attr.ASCII,
    val format: Format = Format.FIX,
    val align: Align = Align.L,
    val length: String = "0",
    val padding: String = "0",
)

@Serializable
data class SerializeFieldItem(
    val attr: Attr,
    val format: Format,
    val length: String,
    val align: Align,
    val padding: String
)

@Serializable
enum class Attr {
  ASCII,
  BCD,
  BINARY
}

@Serializable
enum class Format {
  VAR,
  FIX
}

@Serializable
enum class Align {
  L,
  R
}
