package io.github.vinnih.kipty.ui.player

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.state.rememberPlayPauseButtonState
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.ui.components.TextViewer
import io.github.vinnih.kipty.ui.theme.AppTheme

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    playerController: PlayerController,
    onDismiss: () -> Unit,
    onTopBarChange: (@Composable () -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val player = playerController.player
    val playPause = rememberPlayPauseButtonState(player)
    val scroll = rememberScrollState()

    LaunchedEffect(Unit) {
        onTopBarChange {
        }
    }

    ModalBottomSheet(
        modifier = modifier.fillMaxHeight(),
        scrimColor = Color.Transparent,
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        shape = RectangleShape,
        dragHandle = {}
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(72.dp).padding(start = 16.dp, end = 16.dp)
        ) {
            IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(
                    painter = painterResource(R.drawable.keyboard_arrow_down),
                    contentDescription = "Dismiss player screen modal"
                )
            }
            Text(text = "Player", modifier = Modifier.align(Alignment.Center))
            IconButton(
                onClick = playPause::onClick,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    painter = painterResource(
                        if (playPause.showPlay) R.drawable.play else R.drawable.pause
                    ),
                    contentDescription = "Play and pause button"
                )
            }
        }
        HorizontalDivider(thickness = 2.dp)
        TextViewer(playerController = playerController, onClick = { start, end ->
            playerController.seekTo(start)
        }, modifier = Modifier.verticalScroll(scroll))
    }
}

@Preview
@Composable
private fun PlayerScreenPreview() {
    AppTheme {
        PlayerScreen(playerController = FakePlayerViewModel(), onDismiss = {
        }, onTopBarChange = {})
    }
}
