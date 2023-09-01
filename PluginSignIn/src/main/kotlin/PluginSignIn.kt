import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.google.auto.service.AutoService
import com.kouqurong.plugin.view.IPluginView

@AutoService(IPluginView::class)
class PluginSignIn : IPluginView {
  private val viewModel = SignInViewModel()

  override val view: @Composable () -> Unit
    get() = { App(viewModel) }
  override val icon: @Composable () -> Painter
    get() = { rememberVectorPainter(Icons.Default.Face) }
  override val label: String
    get() = "SignIn"
}
