import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import com.kouqurong.plugin.view.IPluginView

class HostViewModel {
    val pluginViewWindowState = mutableStateListOf<PluginViewWindowState>()

    fun openNewPluginViewWindow(pluginView: IPluginView) {
        pluginViewWindowState.add(PluginViewWindowState(pluginView) {
            closePluginViewWindow(it)
        })
    }

    private fun closePluginViewWindow(state: PluginViewWindowState) {
        pluginViewWindowState.removeIf(state::equals)
    }

    fun exit() {
        pluginViewWindowState.clear()
    }
}

@Stable
data class PluginViewWindowState(
    val pluginView: IPluginView,
    val close: (PluginViewWindowState) -> Unit
) {
    fun close() = close(this)
}