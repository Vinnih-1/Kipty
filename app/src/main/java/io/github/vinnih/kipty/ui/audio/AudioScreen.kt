package io.github.vinnih.kipty.ui.audio

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.Screen
import io.github.vinnih.kipty.data.FakeAudioData
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.NotificationCategory
import io.github.vinnih.kipty.data.database.entity.TranscriptionState
import io.github.vinnih.kipty.json
import io.github.vinnih.kipty.ui.components.AudioConfigSheet
import io.github.vinnih.kipty.ui.components.BaseButton
import io.github.vinnih.kipty.ui.components.TextViewer
import io.github.vinnih.kipty.ui.configuration.ConfigurationController
import io.github.vinnih.kipty.ui.configuration.FakeConfigurationViewModel
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
    configurationController: ConfigurationController,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit,
    id: Int,
    modifier: Modifier = Modifier
) {
    val loadedAudio by audioController.getFlowById(id).collectAsState(null)

    if (loadedAudio == null) return

    val audioEntity = loadedAudio!!
    val configurationUiState by configurationController.uiState.collectAsState()
    val playerUiState by playerController.uiState.collectAsState()
    val audioUiState by audioController.uiState.collectAsState()
    var selectedAudio by remember { mutableStateOf<AudioEntity?>(null) }

    AudioConfigSheet(
        audioController = audioController,
        playerController = playerController,
        notificationController = notificationController,
        audioEntity = selectedAudio,
        onDismiss = { selectedAudio = null },
        onNavigate = onNavigate,
        modifier = modifier
    )

    Scaffold(
        topBar = {
            AudioTopBar(
                audioEntity = audioEntity,
                audioUiState = audioUiState,
                onTranscribe = {
                    notificationController.notify(
                        audioEntity = audioEntity,
                        title = "Transcribing audio",
                        content = "Your transcript for this episode is being prepared.",
                        channel = NotificationCategory.TRANSCRIPTION_INIT
                    )
                    audioController.transcribeAudio(audioEntity = audioEntity)
                },
                onSettings = { selectedAudio = audioEntity },
                onCancel = {
                    audioController.cancelTranscriptionWork(audioEntity)
                },
                onBack = onBack
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                !audioEntity.transcription.isNullOrEmpty() &&
                    audioEntity.uid != playerUiState.currentAudio?.uid
            ) {
                PlayPauseButton(
                    audioEntity = audioEntity,
                    playerController = playerController
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            if (!audioEntity.transcription.isNullOrEmpty()) {
                TextViewer(
                    transcription = audioEntity.transcription,
                    onClick = { start, end ->
                        playerController.seekTo(audioEntity, start, end)
                    },
                    showTimestamp = configurationUiState.appSettings.showTimestamp
                )
            } else {
                NoTranscriptionFound()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioTopBar(
    audioEntity: AudioEntity,
    audioUiState: AudioUiState,
    onTranscribe: () -> Unit,
    onSettings: () -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val canTranscribe = audioEntity.state == TranscriptionState.NONE && audioUiState.canTranscribe
    val isCurrentAudio = audioEntity.uid == audioUiState.currentUid

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
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.basicMarquee()
                    )
                    Text(
                        text = audioEntity.description ?: "No description",
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.secondary.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.basicMarquee()
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
                AnimatedVisibility(canTranscribe || isCurrentAudio) {
                    TranscriptionButton(
                        audioEntity = audioEntity,
                        onStart = onTranscribe,
                        onCancel = onCancel
                    )
                }
                BaseButton(onClick = onSettings, content = {
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
            expandedHeight = 100.dp
        )
        HorizontalDivider(color = colors.secondary)
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
private fun PlayPauseButton(
    audioEntity: AudioEntity,
    playerController: PlayerController,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    BaseButton(
        onClick = {
            playerController.seekTo(audioEntity)
        },
        content = {
            Icon(
                painter = painterResource(R.drawable.play),
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
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
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
private fun NoTranscriptionFound(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.type),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
        Text(
            text = "This audio has no transcription",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = "Click the transcription button to generate one",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
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
            configurationController = FakeConfigurationViewModel(),
            onNavigate = {},
            onBack = {},
            id = audioEntity.uid
        )
    }
}
