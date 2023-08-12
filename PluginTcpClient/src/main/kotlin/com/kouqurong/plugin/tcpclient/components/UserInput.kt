package com.kouqurong.plugin.tcpclient.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserInput(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            UserInputText(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .semantics {
                        contentDescription = "User input"
                    },
                onTextChanged = { /*TODO*/ },
                textFieldValue = TextFieldValue(""),
                onTextFieldFocused = { /*TODO*/ },
                focusState = false
            )

            Spacer(modifier = Modifier.height(6.dp))

            UserInputType(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                onMessageSent = { /*TODO*/ },
                sendMessageEnabled = false,
            )
        }
    }
}


@ExperimentalFoundationApi
@Composable
private fun UserInputText(
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    onTextChanged: (TextFieldValue) -> Unit,
    textFieldValue: TextFieldValue,
    onTextFieldFocused: (Boolean) -> Unit,
    focusState: Boolean
) {
    var lastFocusState by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Surface {
            Box(
                modifier = Modifier
                    .height(64.dp)
                    .weight(1f)
                    .align(Alignment.Bottom)
            ) {
                BasicTextField(
                    value = textFieldValue,
                    onValueChange = { onTextChanged(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp)
                        .align(Alignment.CenterStart)
                        .focusRequester(focusRequester)
                        .onFocusChanged { state ->
                            if (lastFocusState != state.isFocused) {
                                onTextFieldFocused(state.isFocused)
                            }
                            lastFocusState = state.isFocused
                        },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
                        imeAction = ImeAction.Send
                    ),
                    maxLines = 1,
                    cursorBrush = SolidColor(LocalContentColor.current),
                    textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current)
                )

                val disableContentColor =
                    MaterialTheme.colorScheme.onSurfaceVariant
                if (textFieldValue.text.isEmpty() && !focusState) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 32.dp),
                        text = "User input",
                        style = MaterialTheme.typography.bodyLarge.copy(color = disableContentColor)
                    )
                }
            }
        }
    }
}


@Composable
fun UserInputType(
    modifier: Modifier = Modifier,
    sendMessageEnabled: Boolean,
    onMessageSent: () -> Unit
) {
    Row {
        TypeRadioButton(
            modifier = Modifier.height(36.dp),
            selected = true,
            onClick = { /*TODO*/ },
            label = "HEX"
        )

        Spacer(modifier = Modifier.width(8.dp))

        TypeRadioButton(
            modifier = Modifier.height(36.dp),
            selected = false,
            onClick = { /*TODO*/ },
            label = "ASCII"
        )

        Spacer(modifier = Modifier.weight(1f))


        val disabledContentColor =
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)

        val buttonColors = ButtonDefaults.buttonColors(
            disabledContainerColor = Color.Transparent,
            disabledContentColor = disabledContentColor
        )

        val border = if (!sendMessageEnabled) {
            BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        } else {
            null
        }

        // Send button
        Button(
            modifier = Modifier.height(36.dp),
            enabled = sendMessageEnabled,
            onClick = onMessageSent,
            colors = buttonColors,
            border = border,
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = "Send",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}


@Composable
fun TypeRadioButton(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = if (selected) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick,
            )

            Text(
                modifier = Modifier.padding(end = 4.dp),
                text = label
            )
        }
    }

}


@Preview
@Composable
fun PreviewUserInput() {
    UserInput()
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun previewUserInputText() {
    UserInputText(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        keyboardType = KeyboardType.Text,
        onTextChanged = {},
        textFieldValue = TextFieldValue(""),
        onTextFieldFocused = {},
        focusState = false
    )
}

@Preview
@Composable
fun PreviewUserInputType() {
    UserInputType(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        sendMessageEnabled = true,
        onMessageSent = {}
    )
}

@Preview
@Composable
fun PreviewTypeRadioButton() {
    TypeRadioButton(
        selected = true,
        onClick = {},
        label = "HEX"
    )
}

