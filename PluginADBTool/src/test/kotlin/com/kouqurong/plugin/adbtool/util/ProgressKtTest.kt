package com.kouqurong.plugin.adbtool.util

import kotlinx.coroutines.runBlocking

class ProgressKtTest {

    @org.junit.Test
    fun `test running adb progress`() {
        runBlocking {
            runningProcess(
                "/opt/homebrew/bin/adb",
                "version",
                onOutput = {
                    println(it)
                },
                onError = {
                    println(it)
                },
                onFinished = {
                    println("===============")
                }
            )
        }
    }
}