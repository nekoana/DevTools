package com.kouqurong.plugin.tcpclient.components

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private enum class Visibility {
  Visible,
  Gone,
}

@Composable
fun JumpToBottom(enabled: Boolean, onClicked: () -> Unit, modifier: Modifier = Modifier) {
  val transition =
      updateTransition(
          if (enabled) Visibility.Visible else Visibility.Gone,
      )

  val bottomOffset by
      transition.animateDp {
        if (it == Visibility.Gone) {
          (-32).dp
        } else {
          32.dp
        }
      }

  if (bottomOffset > 0.dp) {
    ExtendedFloatingActionButton(
        icon = {
          Icon(
              imageVector = Icons.Filled.ArrowDropDown,
              contentDescription = null,
              modifier = Modifier.height(18.dp))
        },
        text = { Text(text = "Jump to bottom") },
        onClick = onClicked,
        modifier = modifier.offset(x = 0.dp, y = bottomOffset).height(36.dp),
    )
  }
}

@Preview
@Composable
fun JumpToBottomPreview() {
  JumpToBottom(enabled = true, onClicked = {})
}
