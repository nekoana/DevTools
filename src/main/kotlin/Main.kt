import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.kouqurong.plugin.view.IPluginView
import java.util.*

@Composable
fun Home(onDisplay: (IPluginView) -> Unit) {
    val classLoader =
        PathClassLoader(
            "/Users/codin/MyCode/DevTools/PluginHello/build/libs/PluginHello.jar",
            "/Users/codin/MyCode/DevTools/PluginHex/build/libs/PluginHex.jar"
        )
    val serviceLoader =
        ServiceLoader.load(IPluginView::class.java, classLoader)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        serviceLoader.forEach {
            Surface(
                modifier = Modifier.size(60.dp, 80.dp)
                    .clickable {
                        onDisplay(it)
                    }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        modifier = Modifier.size(60.dp, 60.dp),
                        painter = it.icon(),
                        contentDescription = it.label
                    )
                    Text(
                        it.label
                    )
                }
            }
        }
    }
}

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class
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
            ){
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
                if (displayPluginView != null) {
                    displayPluginView!!.view()
                } else {
                    Home(onDisplay = {
                        displayPluginView = it
                    })
                }
            }
        }
    }
}
