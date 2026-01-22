package io.github.vinnih.kipty.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.data.FakeAudioData
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.json
import io.github.vinnih.kipty.ui.theme.AppTheme
import io.github.vinnih.kipty.utils.formatDate
import io.github.vinnih.kipty.utils.formatTime
import java.io.File

@Composable
fun AudioCard(
    audioEntity: AudioEntity,
    playTime: Long,
    onNavigate: (Int) -> Unit,
    onPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current

    ElevatedCard(
        modifier = modifier.padding(8.dp).combinedClickable(
            onClick = { onNavigate(audioEntity.uid) },
            onLongClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onPress.invoke()
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        AudioContainer(
            onClick = onPress,
            audioEntity = audioEntity,
            playTime = playTime
        )
    }
}

@Composable
private fun AudioContainer(
    onClick: () -> Unit,
    audioEntity: AudioEntity,
    playTime: Long,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            AudioIconSection(
                audioEntity = audioEntity
            )
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight()
            ) {
                AudioInformationSection(
                    onClick = onClick,
                    audioEntity = audioEntity
                )
                AudioStatusSection(
                    audioEntity = audioEntity,
                    playTime = playTime
                )
            }
        }
    }
}

@Composable
private fun AudioIconSection(audioEntity: AudioEntity, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val context = LocalContext.current

    val imageModel = remember(audioEntity.uid, audioEntity.imagePath) {
        if (audioEntity.isDefault) {
            "file:///android_asset/${audioEntity.imagePath}"
        } else {
            ImageRequest.Builder(context)
                .data(File(audioEntity.imagePath))
                .memoryCacheKey(audioEntity.imagePath)
                .diskCacheKey(audioEntity.imagePath)
                .crossfade(true)
                .size(90, 90)
                .build()
        }
    }

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.End,
        modifier = modifier
            .fillMaxHeight()
            .width(90.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .height(90.dp)
                .width(90.dp)
                .background(Color.Gray)
        ) {
            AsyncImage(
                model = imageModel,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(90.dp)
            )
        }
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .background(color = colors.secondaryContainer.copy(alpha = .7f))
        ) {
            Text(
                text = audioEntity.duration.formatTime(),
                color = colors.onSecondaryContainer,
                style = typography.bodyMedium,
                modifier = Modifier.padding(5.dp)
            )
        }
    }
}

@Composable
private fun AudioInformationSection(
    onClick: () -> Unit,
    audioEntity: AudioEntity,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(modifier = modifier) {
        Row {
            Column(Modifier.fillMaxWidth(.8f)) {
                Text(
                    text = audioEntity.name,
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = audioEntity.createdAt.formatDate(),
                    style = typography.bodySmall,
                    color = colors.onBackground.copy(alpha = .6f)
                )
            }
            BaseButton(
                onClick = onClick
            ) {
                Icon(
                    painter = painterResource(R.drawable.more_vertical),
                    contentDescription = null,
                    tint = colors.onBackground.copy(alpha = .6f)
                )
            }
        }
        Text(
            text = audioEntity.description ?: "Audio with no description",
            style = typography.bodyMedium,
            color = colors.onBackground.copy(alpha = .8f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AudioStatusSection(
    audioEntity: AudioEntity,
    playTime: Long,
    modifier: Modifier = Modifier
) {
    val typography = MaterialTheme.typography
    val seconds = audioEntity.duration / 1000
    val progress = (playTime.toFloat() / seconds.toFloat()).coerceIn(0f, 1f)

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.file_text),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = audioEntity.state.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = typography.bodySmall
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.clock),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = typography.bodySmall
                )
            }
        }
        LinearProgressIndicator(
            progress = { progress },
            gapSize = 0.dp,
            drawStopIndicator = {}
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
private fun AudioCardPreview() {
    val audioEntity = json.decodeFromString<AudioEntity>(FakeAudioData.audio_1865_02_01)

    AppTheme {
        AudioCard(
            audioEntity = audioEntity,
            playTime = 0L,
            onNavigate = {},
            onPress = {},
            modifier = Modifier.fillMaxWidth().height(200.dp)
        )
    }
}
