import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import com.kouqurong.plugin.view.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalMaterial3Api::class)
class SignInViewModel : ViewModel() {
  val token = mutableStateOf("")
  private val libraryPath = mutableStateOf("")

  val libraryVersion =
      snapshotFlow { libraryPath.value }
          .filter { it.isNotEmpty() }
          .map {
            runCatching {
                  System.load(it)
                  version()
                }
                .onFailure { it.printStackTrace() }
                .getOrDefault("Error Api Library")
          }
          .catch { emit("Error ${it.message}") }
          .flowOn(Dispatchers.IO)
          .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "Select Api Library")

  val timePickerState = TimePickerState(initialHour = 0, initialMinute = 0, is24Hour = false)

  private external fun version(): String

  fun setToken(t: String) {
    token.value = t
  }

  fun setLibraryFile(path: String) {
    libraryPath.value = path
  }
}
