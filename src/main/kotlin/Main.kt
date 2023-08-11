import androidx.compose.animation.AnimatedContent
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.kouqurong.plugin.view.IPluginView
import java.util.*


val classLoader =
    PathClassLoader(
        "/Users/codin/MyCode/DevTools/PluginHello/build/libs/PluginHello.jar",
        "/Users/codin/MyCode/DevTools/PluginHex/build/libs/PluginHex.jar",
        "/Users/codin/MyCode/DevTools/PluginTcpClient/build/libs/PluginTcpClient.jar"
    )

@Composable
fun Home(onDisplay: (IPluginView) -> Unit) {
    val views = remember {
        ServiceLoader.load(IPluginView::class.java, classLoader).map { it }
    }
    LazyVerticalGrid(
        columns = GridCells.FixedSize(120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(views) {
            ElevatedCard(
                modifier = Modifier
                    .size(120.dp, 80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        onDisplay(it)
                    },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Image(
                        modifier = Modifier.size(40.dp, 40.dp),
                        painter = it.icon(),
                        contentDescription = it.label
                    )
                    Text(
                        it.label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@OptIn(
    ExperimentalMaterial3Api::class
)

@Composable
@Preview
fun App(
    onClose: () -> Unit,
    onMinimize: () -> Unit,
    onBack: () -> Unit,
    onDisplay: (IPluginView) -> Unit,
    content: @Composable () -> Unit
) {
    MaterialTheme {
        Scaffold(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp)),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.id.APP_NAME)
                        )
                    },
                    navigationIcon = {
                        Row {
                            IconButton(
                                onClick = {
                                    onClose()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close"
                                )
                            }

                            IconButton(
                                onClick = {
                                    onMinimize()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Minimize"
                                )
                            }

                            IconButton(
                                onClick = {
                                    onBack()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    },
                )
            },
        ) {
            Surface(
                modifier = Modifier
                    .padding(top = it.calculateTopPadding())
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                content()
            }
        }
    }
}

fun main() = application {
    val windowState = rememberWindowState()
    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        undecorated = true,
        transparent = true,
        resizable = false,
        icon = painterResource("icon.svg")
    ) {

        var displayPluginView by remember {
            mutableStateOf<IPluginView?>(null)
        }

        WindowDraggableArea {
            App(
                onClose = ::exitApplication,
                onMinimize = {
                    windowState.isMinimized = true
                },
                onBack = {
                    displayPluginView = null
                },
                onDisplay = {
                    displayPluginView = it
                }
            ) {
                AnimatedContent(
                    targetState = displayPluginView,
                ) {
                    if (it != null) {
                        it.view()
                    } else {
                        Home(onDisplay = {
                            displayPluginView = it
                        })
                    }
                }
            }
        }
    }
}
