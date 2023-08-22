package com.kouqurong.plugin.view

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.jetbrains.skiko.MainUIDispatcher

open class ViewModel {
  val viewModelScope = CoroutineScope(SupervisorJob() + MainUIDispatcher)
}
