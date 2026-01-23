package io.github.vinnih.kipty.ui.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.state.rememberNextButtonState
import androidx.media3.ui.compose.state.rememberPlayPauseButtonState
import androidx.media3.ui.compose.state.rememberPreviousButtonState
import coil3.compose.AsyncImage
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.ui.components.BaseButton
import io.github.vinnih.kipty.ui.components.TextViewer
import io.github.vinnih.kipty.ui.configuration.ConfigurationController
import io.github.vinnih.kipty.ui.configuration.FakeConfigurationViewModel
import io.github.vinnih.kipty.ui.theme.AppTheme
import io.github.vinnih.kipty.utils.formatTime
import java.io.File
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    playerController: PlayerController,
    configurationController: ConfigurationController,
    scaffoldState: BottomSheetScaffoldState,
    modifier: Modifier = Modifier,
    peekHeight: Dp = 100.dp
) {
    val colors = MaterialTheme.colorScheme
    val playerUiState by playerController.uiState.collectAsState()
    val configurationUiState by configurationController.uiState.collectAsState()
    var visible by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(scaffoldState.bottomSheetState) {
        snapshotFlow {
            runCatching { scaffoldState.bottomSheetState.requireOffset() }.getOrNull()
        }.filterNotNull()
            .collect { offset ->
                visible = offset > 1000f
            }
    }

    Column(modifier = modifier.fillMaxSize().background(color = colors.surfaceContainer)) {
        AnimatedVisibility(visible) {
            MiniPlayer(
                onExpand = {
                    scope.launch { scaffoldState.bottomSheetState.expand() }
                },
                player = playerController.player,
                playerUiState = playerUiState,
                peekHeight = peekHeight
            )
        }
        AnimatedVisibility(!visible) {
            Player(
                onCollapse = {
                    scope.launch { scaffoldState.bottomSheetState.partialExpand() }
                },
                player = playerController.player,
                peekHeight = peekHeight
            ) {
                TextViewer(
                    playerController = playerController,
                    onClick = { start, _ ->
                        playerController.seekTo(playerUiState.currentAudio!!, start, 0)
                    },
                    showTimestamp = configurationUiState.appSettings.showTimestamp,
                    modifier = Modifier.padding(bottom = 48.dp)
                )
            }
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Player(
    onCollapse: () -> Unit,
    player: Player,
    peekHeight: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val playPause = rememberPlayPauseButtonState(player)

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .height(peekHeight)
                .padding(top = 24.dp, start = 16.dp, end = 16.dp)
        ) {
            IconButton(onClick = onCollapse) {
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
        content.invoke()
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MiniPlayer(
    onExpand: () -> Unit,
    player: Player,
    playerUiState: PlayerUiState,
    peekHeight: Dp,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val playPause = rememberPlayPauseButtonState(player)
    val next = rememberNextButtonState(player)
    val previous = rememberPreviousButtonState(player)
    val time = (playerUiState.progress * playerUiState.duration).toLong()
    val image = if (playerUiState.currentAudio?.isDefault == true) {
        "file:///android_asset/${playerUiState.currentAudio.imagePath}"
    } else if (playerUiState.currentAudio != null) {
        File(playerUiState.currentAudio.imagePath)
    } else {
        ""
    }

    Column(modifier = modifier) {
        LinearProgressIndicator(
            progress = {
                playerUiState.progress
            },
            drawStopIndicator = {},
            gapSize = 0.dp,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(peekHeight)
                .clickable { onExpand.invoke() }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .fillMaxWidth(.65f)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .height(64.dp)
                            .width(64.dp)
                            .background(Color.Gray)
                    ) {
                        AsyncImage(
                            model = image,
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.width(64.dp).height(64.dp)
                        )
                    }
                    Column {
                        Text(
                            text = playerUiState.currentAudio?.name ?: "Nothing playing",
                            style = typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE)
                        )
                        Text(
                            text = "${time.formatTime()} / ${playerUiState.duration.formatTime()}",
                            style = typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    BaseButton(onClick = { previous.onClick() }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            painter = painterResource(R.drawable.skip_previous),
                            contentDescription = null,
                            tint = colors.onBackground.copy(alpha = .6f),
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    BaseButton(
                        onClick = { playPause.onClick() },
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(48.dp)
                            .background(color = colors.secondaryContainer.copy(.6f))
                    ) {
                        Icon(
                            painter = painterResource(
                                if (playPause.showPlay) R.drawable.play else R.drawable.pause
                            ),
                            contentDescription = null,
                            tint = colors.onBackground.copy(alpha = .6f),
                            modifier = Modifier.size(38.dp)
                        )
                    }
                    BaseButton(onClick = { next.onClick() }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            painter = painterResource(R.drawable.skip_next),
                            contentDescription = null,
                            tint = colors.onBackground.copy(alpha = .6f),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
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
        PlayerScreen(
            playerController = FakePlayerViewModel(),
            configurationController = FakeConfigurationViewModel(),
            scaffoldState = scaffoldState
        )
    }
}
