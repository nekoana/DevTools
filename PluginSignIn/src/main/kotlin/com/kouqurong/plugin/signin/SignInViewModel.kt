package com.kouqurong.plugin.signin

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import com.kouqurong.plugin.signin.Option.None
import com.kouqurong.plugin.view.ViewModel
import java.nio.file.Paths
import kotlin.io.path.inputStream
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface Option<in T> {
  object None : Option<Any>
  data class Some<T>(val value: T) : Option<T>
}

@OptIn(ExperimentalMaterial3Api::class)
class SignInViewModel : ViewModel() {
  val snackBarHostState = SnackbarHostState()

  private val libraryPath = mutableStateOf<Option<String>>(None)
  private val bmpPath = mutableStateOf<Option<String>>(None)

  val libraryVersion =
      snapshotFlow { libraryPath.value }
          .map {
            when (it) {
              None -> None
              is Option.Some -> {
                runCatching {
                      System.load(it.value)
                      Option.Some(version())
                    }
                    .onFailure { it.printStackTrace() }
                    .getOrDefault(None)
              }
            }
          }
          .flowOn(Dispatchers.IO)
          .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), None)

  val bmp =
      snapshotFlow { bmpPath.value }
          .map {
            when (it) {
              None -> None
              is Option.Some -> {
                runCatching {
                      val img = Paths.get(it.value).inputStream().buffered().use(::loadImageBitmap)
                      Option.Some(BitmapPainter(img))
                    }
                    .onFailure { it.printStackTrace() }
                    .getOrDefault(None)
              }
            }
          }
          .flowOn(Dispatchers.IO)
          .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), None)

  val timePickerState = TimePickerState(initialHour = 0, initialMinute = 0, is24Hour = false)

  fun setLibraryFile(path: String) {
    libraryPath.value = if (path.isEmpty()) None else Option.Some(path)
  }

  fun setBmpFile(path: String) {
    bmpPath.value = if (path.isEmpty()) None else Option.Some(path)
  }

  fun signin(number: Int, token: String) {
    viewModelScope.launch {
      val result =
          withContext(Dispatchers.IO) {
            signin(number, token, (bmpPath.value as Option.Some).value)
          }

      snackBarHostState.showSnackbar(result)
    }
  }

  private external fun version(): String
  private external fun signin(number: Int, token: String, bmpPath: String): String
}
