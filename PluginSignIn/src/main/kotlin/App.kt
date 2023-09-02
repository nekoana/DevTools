import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.TimePicker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(viewModel: SignInViewModel) {
  val libraryVersion = viewModel.libraryVersion.collectAsState()
  val bmp = viewModel.bmp.collectAsState()

  val isSignInEnabled = remember {
    derivedStateOf {
      libraryVersion.value is Option.Some &&
          bmp.value is Option.Some &&
          viewModel.token.value.isNotBlank() &&
          viewModel.number.value.isNotBlank()
    }
  }

  Box(modifier = Modifier.padding(16.dp).fillMaxSize()) {
    SnackbarHost(
        hostState = viewModel.snackbarHostState, modifier = Modifier.align(Alignment.TopCenter))
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
          LoginInfo(
              token = viewModel.token.value,
              number = viewModel.number.value,
              onTokenChange = viewModel::setToken,
              onNumberChange = viewModel::setNumber)

          ApiLibInfo(
              version =
                  when (val v = libraryVersion.value) {
                    Option.None -> "Select Api Library"
                    is Option.Some -> v.value
                  },
              onLibChanged = { viewModel.setLibraryFile(it) })

          Row {
            TimePicker(state = viewModel.timePickerState, modifier = Modifier.weight(1F))

            Column(
                modifier = Modifier.weight(1F), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  BmpInfo(
                      paintFor = {
                        when (val v = bmp.value) {
                          Option.None -> null
                          is Option.Some -> v.value
                        }
                      },
                      onBmpChanged = { viewModel.setBmpFile(it) },
                      modifier = Modifier.fillMaxWidth().weight(1F))

                  OutlinedButton(
                      onClick = viewModel::signin,
                      enabled = isSignInEnabled.value,
                      modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Sign In")
                      }
                }
          }
        }
  }
}

@Composable
fun LoginInfo(
    token: String,
    number: String,
    onTokenChange: (String) -> Unit,
    onNumberChange: (String) -> Unit
) {
  Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = number,
            singleLine = true,
            onValueChange = { onNumberChange(it) },
            label = { Text(text = "Number") },
            modifier = Modifier.weight(1F))

        OutlinedTextField(
            value = token,
            singleLine = true,
            onValueChange = onTokenChange,
            label = { Text(text = "Token") },
            modifier = Modifier.weight(7F))
      }
}

@Composable
fun ApiLibInfo(version: String, onLibChanged: (String) -> Unit) {
  Text(
      text = version,
      textAlign = TextAlign.Center,
      fontSize = TextUnit(22.sp.value, TextUnitType.Sp),
      style = MaterialTheme.typography.body1,
      modifier =
          Modifier.height(32.dp)
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .dashedBorder(4.dp, Color.Gray, 16.dp)
              .clickable { fileChooser { onLibChanged(it) } })
}

@Composable
fun BmpInfo(paintFor: () -> Painter?, onBmpChanged: (String) -> Unit, modifier: Modifier) {
  Box(
      modifier =
          modifier.clip(RoundedCornerShape(16.dp)).dashedBorder(4.dp, Color.Gray, 16.dp).clickable {
            fileChooser { onBmpChanged(it) }
          }) {
        Text(
            text = "Photos",
            textAlign = TextAlign.Center,
            fontSize = TextUnit(22.sp.value, TextUnitType.Sp),
            style = MaterialTheme.typography.body1,
            modifier = Modifier.align(Alignment.Center))

        paintFor()?.let {
          Image(painter = it, contentDescription = "bmp", modifier = Modifier.fillMaxSize())
        }
      }
}

fun fileChooser(onFileChoose: (String) -> Unit) {
  val dialog = java.awt.FileDialog(ComposeWindow()).apply { isVisible = true }

  if (dialog.file == null) return
  if (dialog.directory == null) return

  onFileChoose(dialog.directory + dialog.file)
}

fun Modifier.dashedBorder(strokeWidth: Dp, color: Color, cornerRadius: Dp) =
    composed(
        factory = {
          val density = LocalDensity.current
          val strokeWidthPx = density.run { strokeWidth.toPx() }
          val cornerRadiusPx = density.run { cornerRadius.toPx() }

          then(
              Modifier.drawWithCache {
                onDrawBehind {
                  val stroke =
                      Stroke(
                          width = strokeWidthPx,
                          pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                  drawRoundRect(
                      color = color, style = stroke, cornerRadius = CornerRadius(cornerRadiusPx))
                }
              })
        })
