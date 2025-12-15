package io.github.vinnih.kipty.ui.audio

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
    id: Int,
    modifier: Modifier = Modifier
) {
    val audios = audioController.allAudios.collectAsState()
    val scroll = rememberScrollState()
    val audioEntity = audios.value.first { it.uid == id }

    if (!audioEntity.transcription.isNullOrEmpty()) {
        TextViewer(transcription = audioEntity.transcription, onClick = { start, end ->
            playerController.playSection(audioEntity, start, end)
        }, modifier = modifier.verticalScroll(scroll))
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
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val audios = audioController.allAudios.collectAsState()
    val audioEntity = audios.value.first { it.uid == id }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier.fillMaxWidth().animateContentSize().clip(
            shape = RoundedCornerShape(
                bottomStart = 24.dp,
                bottomEnd = 24.dp
            )
        ).height(400.dp).background(
            brush = Brush.linearGradient(
                colors = listOf(colors.primary, colors.onPrimary),
                start = Offset.Zero,
                end = Offset.Infinite
            )
        )
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "ggml-tiny.en-q5_1",
                    color = colors.onPrimary,
                    style = typography.titleMedium
                )
            },
            navigationIcon = {
                BackButton(onClick = onBack, modifier = Modifier.padding(start = 10.dp))
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )
        Text(
            text = audioEntity.name,
            modifier = Modifier.align(Alignment.Center).padding(bottom = 100.dp),
            textAlign = TextAlign.Center,
            style = typography.displayMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = colors.onPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = audioEntity.description ?: "",
            modifier = Modifier.align(Alignment.Center).padding(top = 130.dp),
            textAlign = TextAlign.Center,
            style = typography.bodyLarge,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
            color = colors.secondary
        )

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
    }
}

@Composable
private fun BoxScope.PlayPauseButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    PlayPauseAudioButton(
        onClick = onClick,
        modifier = modifier.width(
            240.dp
        ).height(
            70.dp
        ).align(
            Alignment.BottomCenter
        ).padding(bottom = 8.dp).shadow(elevation = 16.dp)
    )
}

@Composable
private fun BoxScope.CancelButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    CancelAudioButton(
        onClick = onClick,
        modifier = modifier.width(
            240.dp
        ).height(
            70.dp
        ).align(
            Alignment.BottomCenter
        ).padding(bottom = 8.dp).shadow(elevation = 16.dp)
    )
}

@Composable
private fun BoxScope.TranscriptionButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    GenerateTranscriptionButton(
        onClick = onClick,
        modifier = modifier.width(
            240.dp
        ).height(
            70.dp
        ).align(
            Alignment.BottomCenter
        ).padding(bottom = 8.dp).shadow(elevation = 16.dp)
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
