package io.github.vinnih.kipty.ui.audio

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vinnih.kipty.data.database.entity.TranscriptionState
import io.github.vinnih.kipty.ui.components.BackButton
import io.github.vinnih.kipty.ui.components.CancelAudioButton
import io.github.vinnih.kipty.ui.components.GenerateTranscriptionButton
import io.github.vinnih.kipty.ui.components.PlayPauseAudioButton
import io.github.vinnih.kipty.ui.components.TextViewer
import io.github.vinnih.kipty.ui.notification.NotificationController
import io.github.vinnih.kipty.ui.player.PlayerController
import io.github.vinnih.kipty.ui.theme.AppTheme
import kotlinx.coroutines.launch

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

    Column(modifier = modifier.fillMaxWidth()) {
        AudioTopBar(
            id = id,
            notificationController = notificationController,
            audioController = audioController,
            playerController = playerController,
            onBack = onBack
        )

        if (!audioEntity.transcription.isNullOrEmpty()) {
            TextViewer(transcription = audioEntity.transcription, onClick = { start, end ->
                playerController.seekTo(audioEntity, start, end)
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioTopBar(
    id: Int,
    notificationController: NotificationController,
    audioController: AudioController,
    playerController: PlayerController,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val audios = audioController.allAudios.collectAsState()
    val audioEntity = audios.value.first { it.uid == id }
    val scope = rememberCoroutineScope()

    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        CenterAlignedTopAppBar(
            title = {
                when (audioEntity.state) {
                    TranscriptionState.NONE -> {
                        TranscriptionButton(onClick = {
                            scope.launch {
                                val notification = notificationController.createNotification(
                                    title = "Creating a new transcription",
                                    content = "Preparing ${audioEntity.name} to be transcript."
                                )
                                notificationController.submitNotification(notification)
                            }
                            audioController.transcribeAudio(audioEntity, onSuccess = {
                            })
                        }, modifier = Modifier)
                    }

                    TranscriptionState.TRANSCRIBING -> {
                        CancelButton(onClick = {
                            audioController.cancelTranscriptionWork(audioEntity)
                        })
                    }

                    TranscriptionState.TRANSCRIBED -> {
                        PlayPauseButton(onClick = {
                            playerController.playAudio(audioEntity)
                        }, modifier = Modifier)
                    }
                }
            },
            navigationIcon = {
                BackButton(
                    onClick = onBack,
                    modifier = Modifier.padding(start = 10.dp),
                    container = Color.Transparent
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
            expandedHeight = 84.dp
        )
    }
}

@Composable
private fun PlayPauseButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    PlayPauseAudioButton(
        onClick = onClick,
        modifier = modifier.width(
            240.dp
        ).height(
            70.dp
        ).padding(bottom = 8.dp)
    )
}

@Composable
private fun CancelButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    CancelAudioButton(
        onClick = onClick,
        modifier = modifier.width(
            240.dp
        ).height(
            70.dp
        ).padding(bottom = 8.dp)
    )
}

@Composable
private fun TranscriptionButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    GenerateTranscriptionButton(
        onClick = onClick,
        modifier = modifier.width(
            240.dp
        ).height(
            70.dp
        ).padding(bottom = 8.dp)
    )
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
    AppTheme {
        // AudioScreen(audioEntity = audioEntity, modifier = Modifier.fillMaxSize())
    }
}
