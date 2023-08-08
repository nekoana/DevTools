import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.kouqurong.plugin.view.IPluginView
import java.util.*

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Button(onClick = {
            text = "Hello, Desktop!"
        }) {
            Text(text)
        }
    }
}

fun main() = application {
    val classLoader =
        PathClassLoader("/Users/codin/MyCode/DevTools/PluginHello/build/libs/PluginHello.jar")
    val serviceLoader = ServiceLoader.load(IPluginView::class.java, classLoader)

    val view = serviceLoader.findFirst().get()


    Window(onCloseRequest = ::exitApplication) {
        view.view()
    }
}
