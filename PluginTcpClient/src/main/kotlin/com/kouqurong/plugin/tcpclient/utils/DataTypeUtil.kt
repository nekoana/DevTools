package com.kouqurong.plugin.tcpclient.utils

import com.kouqurong.plugin.tcpclient.viewmodel.DataType

val DataType.bytes
    get() = when (this) {
        is DataType.Hex -> value.toHexByteArray()
        is DataType.Str -> value.toByteArray()
    }



