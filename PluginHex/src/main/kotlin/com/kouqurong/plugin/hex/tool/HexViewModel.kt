package com.kouqurong.plugin.hex.tool

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HexViewModel {
    private val _hexString = MutableStateFlow(TextFieldValue(""))

    private val _isErrorHexString = mutableStateOf(false)

    val hexString: StateFlow<TextFieldValue> = _hexString
    val isErrorHexString: State<Boolean> = _isErrorHexString


    suspend fun updateHexString(hexString: TextFieldValue) {
        _isErrorHexString.value = !isHexString(hexString.text)
        _hexString.emit(hexString)
    }

    suspend fun formatHexString(hexString: String) {
        //每两个字符加一个空格
        //比如： 12345678 -> 12 34 56 78
        //如果是奇数个字符，最后一个字符不加空格
        //如果已经是格式化的字符串，不再格式化

        val newHexString = hexString
            .replace(" ", "")
            .replace(Regex("[0-9a-fA-F]{2}"), "$0 ")
        _hexString.emit(TextFieldValue(newHexString))
    }

    suspend fun upperCaseHexString(hexString: String) {
        //将字符串中的小写字母转换为大写字母
        //如果已经是大写字母，不再转换
        val newHexString = hexString.uppercase()
        _hexString.emit(TextFieldValue(newHexString))
    }

    private fun isHexString(hexString: String): Boolean {
        //十六进制字符串只能包含0-9，a-f，A-F, 空格, 制表符
        return hexString.isEmpty() || hexString.matches(Regex("[0-9a-fA-F\\s\t]+"))
    }
}