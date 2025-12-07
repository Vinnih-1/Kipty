package io.github.vinnih.kipty.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vinnih.kipty.data.FakeAudio
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import io.github.vinnih.kipty.json
import io.github.vinnih.kipty.ui.audio.AudioController
import io.github.vinnih.kipty.ui.audio.FakeAudioViewModel
import io.github.vinnih.kipty.ui.theme.AppTheme
import io.github.vinnih.kipty.utils.timestamp
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@Composable
fun TextViewer(
    controller: AudioController,
    transcription: List<AudioTranscription>,
    modifier: Modifier = Modifier,
    showTimestamp: Boolean = true
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        transcription.forEach { transcription ->
            Column(modifier = Modifier.clickable(onClick = {})) {
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
            controller = FakeAudioViewModel(),
            transcription = json.decodeFromString<AudioEntity>(
                FakeAudio.audio_1865_02_01
            ).transcription!!
        )
    }
}
