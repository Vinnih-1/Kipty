package io.github.vinnih.kipty.ui.audio

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.ui.compose.state.rememberPlayPauseButtonState
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.data.FakeAudioData
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.TranscriptionState
import io.github.vinnih.kipty.json
import io.github.vinnih.kipty.ui.components.TextViewer
import io.github.vinnih.kipty.ui.notification.FakeNotificationViewModel
import io.github.vinnih.kipty.ui.notification.NotificationController
import io.github.vinnih.kipty.ui.player.FakePlayerViewModel
import io.github.vinnih.kipty.ui.player.PlayerController
import io.github.vinnih.kipty.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioScreen(
    audioController: AudioController,
    playerController: PlayerController,
    notificationController: NotificationController,
    onBack: () -> Unit,
    id: Int,
    modifier: Modifier = Modifier
) {
    val audios = audioController.allAudios.collectAsState()
    val audioEntity = audios.value.first { it.uid == id }

    Scaffold(
        floatingActionButton = {
            PlayPauseButton(
                audioEntity = audioEntity,
                playerController = playerController
            )
        }
    ) { paddingValues ->
        Column(modifier = modifier.fillMaxWidth().padding(paddingValues)) {
            AudioTopBar(
                id = id,
                audioController = audioController,
                onBack = onBack
            )

            if (!audioEntity.transcription.isNullOrEmpty()) {
                TextViewer(transcription = audioEntity.transcription, onClick = { start, end ->
                    playerController.seekTo(audioEntity, start, end)
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioTopBar(
    id: Int,
    audioController: AudioController,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val audios = audioController.allAudios.collectAsState()
    val audioEntity = audios.value.first { it.uid == id }
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        TopAppBar(
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(.8f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = audioEntity.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.secondary,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = audioEntity.description ?: "No description",
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.secondary.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            },
            navigationIcon = {
                NavigateButton(onNavigate = onBack, content = {
                    Icon(
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = null,
                        tint = colors.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                })
            },
            actions = {
                TranscriptionButton(
                    audioEntity = audioEntity,
                    onStart = {
                        audioController.transcribeAudio(audioEntity) {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    },
                    onCancel = {
                        audioController.cancelTranscriptionWork(audioEntity)
                    }
                )
                BaseButton(onClick = {
                    Toast.makeText(
                        context,
                        "Settings not implemented yet!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // TODO: open modal with audio settings (name, description, image)
                }, content = {
                    Icon(
                        painter = painterResource(R.drawable.settings),
                        contentDescription = null,
                        tint = colors.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                })
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
            expandedHeight = 80.dp
        )
        HorizontalDivider(color = colors.secondary)
    }
}

@Composable
private fun PlayPauseButton(
    audioEntity: AudioEntity,
    playerController: PlayerController,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val uiState = playerController.uiState.collectAsState()
    val playPause = rememberPlayPauseButtonState(playerController.player)

    BaseButton(
        onClick = {
            if (uiState.value.audioEntity?.uid == audioEntity.uid) {
                playPause.onClick()
            } else {
                playerController.playPause(audioEntity)
            }
        },
        content = {
            Icon(
                painter = painterResource(
                    if (playPause.showPlay) {
                        R.drawable.play
                    } else {
                        R.drawable.pause
                    }
                ),
                contentDescription = null,
                tint = colors.onPrimaryContainer,
                modifier = Modifier.size(36.dp)
            )
        },
        modifier = modifier
            .padding(end = 24.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colors.primaryContainer)
            .size(64.dp)
    )
}

@Composable
private fun NavigateButton(
    onNavigate: () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    BaseButton(onClick = onNavigate, content = {
        content.invoke()
    }, modifier = modifier)
}

@Composable
private fun TranscriptionButton(
    audioEntity: AudioEntity,
    onStart: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    BaseButton(
        onClick = if (audioEntity.state == TranscriptionState.TRANSCRIBING) {
            onCancel
        } else {
            onStart
        },
        content = {
            Icon(
                painter = painterResource(R.drawable.type),
                contentDescription = null,
                tint = colors.secondary,
                modifier = Modifier.size(24.dp)
            )
        },
        modifier = modifier
            .clip(RoundedCornerShape(42.dp))
            .background(
                if (audioEntity.state == TranscriptionState.TRANSCRIBING) {
                    colors.surfaceContainer
                } else {
                    Color.Unspecified
                }
            )
    )
}

@Composable
private fun BaseButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(48.dp)
    ) {
        content.invoke()
    }
}

@Preview(
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Preview(
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun AudioScreenPreview() {
    val audioEntity = json.decodeFromString<AudioEntity>(FakeAudioData.audio_1888_11_13)

    AppTheme {
        AudioScreen(
            audioController = FakeAudioViewModel(),
            playerController = FakePlayerViewModel(),
            notificationController = FakeNotificationViewModel(),
            onBack = {},
            id = audioEntity.uid
        )
    }
}
