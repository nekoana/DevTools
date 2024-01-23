package com.kouqurong.plugin.adbtool.util

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ProgressTest {
    @Test
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