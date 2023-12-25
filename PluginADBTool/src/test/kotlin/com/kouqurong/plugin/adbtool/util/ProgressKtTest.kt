package com.kouqurong.plugin.adbtool.util

import kotlinx.coroutines.*

class ProgressKtTest {

    @OptIn(DelicateCoroutinesApi::class)
    @org.junit.Test
    fun `test running adb progress`() {
        runBlocking {
          val job =  launch{
                runningProcess("adb", "logcat")
                    .collect {
                        println(it)
                    }
            }

            delay(2000)

            job.cancel()

            delay(1000)
        }
    }
}