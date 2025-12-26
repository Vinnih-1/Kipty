package io.github.vinnih.kipty.ui.loading

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vinnih.kipty.data.database.entity.TranscriptionState
import io.github.vinnih.kipty.ui.audio.AudioController
import io.github.vinnih.kipty.ui.audio.FakeAudioViewModel
import io.github.vinnih.kipty.ui.home.FakeHomeViewModel
import io.github.vinnih.kipty.ui.home.HomeController
import io.github.vinnih.kipty.utils.convertTranscription

@Composable
fun LoadingScreen(
    homeController: HomeController,
    audioController: AudioController,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    LaunchedEffect(Unit) {
        homeController.createDefault { audio, transcription, image, description ->
            val audioEntity = homeController.createAudio(
                audio = audio,
                image = image,
                description = description.readText()
            )
            val transcriptionData = transcription.readText().convertTranscription()

            audioController.saveTranscription(
                audioEntity.copy(
                    transcription = transcriptionData,
                    state = TranscriptionState.TRANSCRIBED
                )
            ).also {
                audio.delete()
                transcription.delete()
                image.delete()
                description.delete()
            }
        }
        onBack.invoke()
    }

    Column(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize().padding(48.dp)) {
            Text(
                text = "Loading...",
                color = colors.primary,
                style = typography.displayMedium,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
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
private fun LoadingScreenPreview() {
    LoadingScreen(
        homeController = FakeHomeViewModel(),
        audioController = FakeAudioViewModel(),
        onBack = {}
    )
}
