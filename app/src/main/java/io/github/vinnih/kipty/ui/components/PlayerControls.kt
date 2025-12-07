package io.github.vinnih.kipty.ui.components

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.state.rememberNextButtonState
import androidx.media3.ui.compose.state.rememberPlayPauseButtonState
import androidx.media3.ui.compose.state.rememberPreviousButtonState
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.ui.player.FakePlayerViewModel
import io.github.vinnih.kipty.ui.player.PlayerController
import io.github.vinnih.kipty.ui.theme.AppTheme
import io.github.vinnih.kipty.utils.formatTime
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun PlayerControls(controller: PlayerController) {
    val player = controller.player
    val playPause = rememberPlayPauseButtonState(player)
    val previous = rememberPreviousButtonState(player)
    val next = rememberNextButtonState(player)
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var isSeeking by remember { mutableStateOf(false) }
    val currentPositionMs = (sliderPosition * player.duration).toLong()
    val totalDurationMs = player.duration

    LaunchedEffect(Unit) {
        while (true) {
            if (!isSeeking && player.isPlaying) {
                val current = player.currentPosition.toFloat()
                val duration = player.duration.toFloat()

                sliderPosition = (current / duration)
            }
            delay(500)
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = previous::onClick) {
                Icon(painter = painterResource(R.drawable.previous), contentDescription = "")
            }
            IconButton(onClick = playPause::onClick) {
                Icon(
                    painter =
                        painterResource(
                            if (playPause.showPlay) R.drawable.play else R.drawable.pause
                        ),
                    contentDescription = ""
                )
            }
            IconButton(onClick = next::onClick) {
                Icon(painter = painterResource(R.drawable.next), contentDescription = "")
            }
        }
        Column(modifier = Modifier.fillMaxWidth(0.8f)) {
            Slider(
                value = sliderPosition,
                onValueChange = {
                    isSeeking = true
                    sliderPosition = it
                },
                onValueChangeFinished = {
                    controller.seekTo(sliderPosition)
                    isSeeking = false
                }
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = currentPositionMs.formatTime())
                Text(text = totalDurationMs.formatTime())
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlayerControlsPreview() {
    AppTheme {
        PlayerControls(controller = FakePlayerViewModel())
    }
}
