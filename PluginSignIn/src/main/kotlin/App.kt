import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.TimePicker
import androidx.compose.runtime.*
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
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(viewModel: SignInViewModel) {
  val libraryVersion = viewModel.libraryVersion.collectAsState()
  val bmp = viewModel.bmp.collectAsState()

  Box(modifier = Modifier.padding(16.dp).fillMaxSize()) {
    SnackbarHost(
        hostState = viewModel.snackbarHostState, modifier = Modifier.align(Alignment.TopCenter))
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
              value = viewModel.token.value,
              onValueChange = viewModel::setToken,
              label = { Text(text = "Token") },
              modifier = Modifier.fillMaxWidth())

          Text(
              text =
                  when (val lib = libraryVersion.value) {
                    Option.None -> "Select Api Library"
                    is Option.Some<String> -> lib.value
                  },
              textAlign = TextAlign.Center,
              fontSize = TextUnit(22.sp.value, TextUnitType.Sp),
              style = MaterialTheme.typography.body1,
              modifier =
                  Modifier.height(32.dp)
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(16.dp))
                      .dashedBorder(4.dp, Color.Gray, 16.dp)
                      .clickable {
                        val dialog = java.awt.FileDialog(ComposeWindow()).apply { isVisible = true }

                        if (dialog.file == null) return@clickable
                        if (dialog.directory == null) return@clickable

                        viewModel.setLibraryFile(dialog.directory + dialog.file)

                        //
                        // JFileChooser(System.getProperty("user.home")).apply {
                        //                            showOpenDialog(null)
                        //                            libraryPath = selectedFile.absolutePath
                        //                        }
                      })

          Row {
            TimePicker(state = viewModel.timePickerState, modifier = Modifier.weight(1F))

            Box(
                modifier =
                    Modifier.weight(1F)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp))
                        .dashedBorder(4.dp, Color.Gray, 16.dp)
                        .clickable {
                          val dialog =
                              java.awt.FileDialog(ComposeWindow()).apply { isVisible = true }

                          if (dialog.file == null) return@clickable
                          if (dialog.directory == null) return@clickable

                          viewModel.setBmpFile(dialog.directory + dialog.file)
                        }) {
                  when (val b = bmp.value) {
                    Option.None ->
                        Text(
                            text = "Photos",
                            textAlign = TextAlign.Center,
                            fontSize = TextUnit(22.sp.value, TextUnitType.Sp),
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.align(Alignment.Center))
                    is Option.Some<BitmapPainter> ->
                        Image(
                            painter = b.value,
                            contentDescription = "bmp",
                            modifier = Modifier.fillMaxSize())
                  }
                }
          }
        }
  }
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
