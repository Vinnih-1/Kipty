package io.github.vinnih.kipty.ui.audio

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vinnih.kipty.data.transcription.AudioData
import io.github.vinnih.kipty.data.transcription.AudioDetails
import io.github.vinnih.kipty.ui.components.BackButton
import io.github.vinnih.kipty.ui.components.EditButton
import io.github.vinnih.kipty.ui.components.GenerateTranscriptionButton
import io.github.vinnih.kipty.ui.components.PlayPauseAudioButton
import io.github.vinnih.kipty.ui.theme.AppTheme

@Composable
fun AudioScreen(audioData: AudioData, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth().clip(
                shape = RoundedCornerShape(
                    bottomStart = 24.dp,
                    bottomEnd = 24.dp
                )
            ).fillMaxHeight(.6f).background(
                brush = Brush.linearGradient(
                    colors = listOf(colors.primary, colors.onPrimary),
                    start = Offset.Zero,
                    end = Offset.Infinite
                )
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton()
                Text(text = "vosk-model", color = colors.onPrimary, style = typography.titleMedium)
                EditButton()
            }
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = audioData.details.name,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 12.dp),
                    textAlign = TextAlign.Center,
                    style = typography.displayMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = colors.onPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = """
                        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut erat tortor, 
                        finibus sed magna vel, sodales elementum leo. Cras tellus ipsum, pulvinar 
                        in mattis quis, scelerisque eget metus. In dapibus aliquet dui, sit amet 
                        pretium nunc gravida vel. Nulla sed dictum eros, sed pulvinar ligula. 
                        Vivamus ornare risus felis, et vehicula dui gravida ac. Class aptent taciti
                        sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. 
                        Curabitur varius libero at nibh pretium ornare. Nullam commodo porttitor 
                        magna, non finibus libero feugiat nec.
                    """.trimIndent(),
                    modifier = Modifier.align(Alignment.Center).padding(top = 12.dp),
                    textAlign = TextAlign.Center,
                    style = typography.bodyLarge,
                    maxLines = 7,
                    overflow = TextOverflow.Ellipsis,
                    color = colors.secondary
                )
                if (audioData.transcription.isNullOrEmpty()) {
                    GenerateTranscriptionButton(
                        modifier = Modifier.width(
                            240.dp
                        ).height(
                            64.dp
                        ).align(
                            Alignment.BottomCenter
                        ).padding(bottom = 8.dp).shadow(elevation = 16.dp)
                    )
                } else {
                    PlayPauseAudioButton(
                        modifier = Modifier.width(
                            240.dp
                        ).height(
                            70.dp
                        ).align(
                            Alignment.BottomCenter
                        ).padding(bottom = 8.dp).shadow(elevation = 16.dp)
                    )
                }
            }
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
private fun AudioScreenPreview() {
    val audioData =
        AudioData(
            AudioDetails("1865-02-01 From Washington Abolition of Slavery", "", 0, 0L, "", "")
        )

    AppTheme {
        AudioScreen(audioData = audioData, modifier = Modifier.fillMaxSize())
    }
}
