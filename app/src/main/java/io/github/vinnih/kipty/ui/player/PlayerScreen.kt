
package io.github.vinnih.kipty.ui.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.state.rememberPlayPauseButtonState
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.ui.components.TextViewer
import io.github.vinnih.kipty.ui.theme.AppTheme
import io.github.vinnih.kipty.utils.formatTime
import kotlinx.coroutines.launch

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    playerController: PlayerController,
    scaffoldState: BottomSheetScaffoldState,
    modifier: Modifier = Modifier,
    peekHeight: Dp = 100.dp
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val uiState by playerController.uiState.collectAsState()
    var visible by remember { mutableStateOf(true) }
    val playPause = rememberPlayPauseButtonState(playerController.player)
    val scope = rememberCoroutineScope()

    LaunchedEffect(scaffoldState.bottomSheetState) {
        snapshotFlow {
            scaffoldState.bottomSheetState.requireOffset()
        }.collect {
            visible = it > 1000f
        }
    }

    Column(modifier = modifier.fillMaxSize().background(color = colors.surfaceContainer)) {
        AnimatedVisibility(visible) {
            LinearProgressIndicator(progress = {
                uiState.progress
            }, drawStopIndicator = {}, modifier = Modifier.fillMaxWidth())
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(peekHeight)
                    .clickable {
                        scope.launch { scaffoldState.bottomSheetState.expand() }
                    }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = uiState.audioEntity?.name ?: "Nothing playing",
                    style = typography.titleMedium,
                    color = colors.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(.7f)
                )
                Text(
                    text = "${
                        (uiState.progress * playerController.player.duration).toLong().formatTime()
                    } / ${playerController.player.duration.formatTime()}",
                    style = typography.bodySmall,
                    color = colors.primary
                )
            }
        }
        AnimatedVisibility(!visible) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().height(
                        peekHeight
                    ).padding(top = 24.dp, start = 16.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {
                        scope.launch { scaffoldState.bottomSheetState.partialExpand() }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.keyboard_arrow_down),
                            contentDescription = "Dismiss player screen modal"
                        )
                    }
                    Text(text = "Player")
                    IconButton(
                        onClick = playPause::onClick
                    ) {
                        Icon(
                            painter = painterResource(
                                if (playPause.showPlay) R.drawable.play else R.drawable.pause
                            ),
                            contentDescription = "Play and pause button"
                        )
                    }
                }
                TextViewer(playerController = playerController, onClick = { start, _ ->
                    playerController.seekTo(uiState.audioEntity!!, start, 0)
                }, modifier = Modifier.padding(bottom = 48.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PlayerScreenPreview() {
    val scaffoldState = rememberBottomSheetScaffoldState()

    AppTheme {
        PlayerScreen(playerController = FakePlayerViewModel(), scaffoldState = scaffoldState)
    }
}
