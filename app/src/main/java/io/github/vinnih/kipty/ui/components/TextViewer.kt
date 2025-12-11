package io.github.vinnih.kipty.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import io.github.vinnih.kipty.ui.player.FakePlayerViewModel
import io.github.vinnih.kipty.ui.player.PlayerController
import io.github.vinnih.kipty.ui.theme.AppTheme
import io.github.vinnih.kipty.utils.timestamp
import kotlinx.serialization.ExperimentalSerializationApi

@Composable
fun TextViewer(
    transcription: List<AudioTranscription>,
    onClick: (Long, Long) -> Unit,
    modifier: Modifier = Modifier,
    showTimestamp: Boolean = true
) {
    TextViewerBase(
        transcription = transcription,
        onClick = onClick,
        modifier = modifier,
        showTimestamp = showTimestamp
    )
}

@Composable
fun TextViewer(
    playerController: PlayerController,
    onClick: (Long, Long) -> Unit,
    modifier: Modifier = Modifier,
    showTimestamp: Boolean = true
) {
    val currentAudio = playerController.currentAudio.collectAsState()

    if (currentAudio.value == null || currentAudio.value?.transcription.isNullOrEmpty()) {
        return
    }

    TextViewerBase(
        transcription = currentAudio.value!!.transcription!!,
        onClick = onClick,
        modifier = modifier,
        showTimestamp = showTimestamp
    )
}

@Composable
private fun TextViewerBase(
    transcription: List<AudioTranscription>,
    onClick: (Long, Long) -> Unit,
    modifier: Modifier = Modifier,
    showTimestamp: Boolean = true
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val scroll = rememberScrollState()

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        transcription.forEach { transcription ->
            Column(
                modifier = Modifier.clickable(onClick = {
                    onClick((transcription.start - 150).coerceAtLeast(0), transcription.end - 100)
                })
            ) {
                if (showTimestamp) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = transcription.start.timestamp(),
                            color = colors.secondary,
                            style = typography.bodyMedium
                        )
                        Text(
                            text = transcription.end.timestamp(),
                            color = colors.secondary,
                            style = typography.bodyMedium
                        )
                    }
                }
                Text(
                    text = transcription.text.removePrefix(":  "),
                    color = colors.secondary,
                    style = typography.headlineMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
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
private fun TextViewerPreview() {
    AppTheme {
        TextViewer(
            playerController = FakePlayerViewModel(),
            onClick = { start, end -> }
        )
    }
}
