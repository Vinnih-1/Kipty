package io.github.vinnih.kipty.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vinnih.kipty.data.FakeAudioData
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import io.github.vinnih.kipty.json
import io.github.vinnih.kipty.ui.player.PlayerController
import io.github.vinnih.kipty.ui.theme.AppTheme
import io.github.vinnih.kipty.utils.drawIfSelected
import io.github.vinnih.kipty.utils.timestamp
import kotlinx.serialization.ExperimentalSerializationApi

@Composable
fun TextViewer(
    transcription: List<AudioTranscription>,
    onClick: (Long, Long) -> Unit,
    showTimestamp: Boolean,
    modifier: Modifier = Modifier
) {
    TextViewerBase(
        transcription = transcription,
        onClick = onClick,
        showTimestamp = showTimestamp,
        modifier = modifier
    )
}

@Composable
fun TextViewer(
    playerController: PlayerController,
    onClick: (Long, Long) -> Unit,
    showTimestamp: Boolean,
    modifier: Modifier = Modifier
) {
    val uiState = playerController.uiState.collectAsState()
    val audioEntity = uiState.value.audioEntity

    if (audioEntity == null || audioEntity.transcription.isNullOrEmpty()) {
        return
    }
    TextViewerBase(
        playerController = playerController,
        onClick = onClick,
        showTimestamp = showTimestamp,
        modifier = modifier
    )
}

@Composable
private fun TextViewerBase(
    transcription: List<AudioTranscription>,
    onClick: (Long, Long) -> Unit,
    showTimestamp: Boolean,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(transcription) {
            TextSection(
                transcription = it,
                onClick = onClick,
                selected = false,
                showTimestamp = showTimestamp
            )
        }
    }
}

@Composable
private fun TextViewerBase(
    playerController: PlayerController,
    onClick: (Long, Long) -> Unit,
    showTimestamp: Boolean,
    modifier: Modifier = Modifier
) {
    val uiState = playerController.uiState.collectAsState()
    val audioEntity = uiState.value.audioEntity
    val listState = rememberLazyListState()

    val activeIndex by remember(uiState.value.currentPosition) {
        derivedStateOf {
            audioEntity!!.transcription!!.indexOfLast {
                it.start <=
                    playerController.uiState.value.currentPosition
            }
        }
    }

    LaunchedEffect(activeIndex) {
        listState.animateScrollToItem(index = activeIndex, scrollOffset = -400)
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(audioEntity!!.transcription!!) { index, item ->
            val isActive = index == activeIndex

            TextSection(
                transcription = item,
                onClick = onClick,
                selected = isActive,
                showTimestamp = showTimestamp
            )
        }
    }
}

@Composable
private fun TextSection(
    transcription: AudioTranscription,
    onClick: (Long, Long) -> Unit,
    selected: Boolean,
    showTimestamp: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(
        modifier = modifier
            .drawIfSelected(selected)
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(32.dp))
            .clickable(onClick = {
                onClick(transcription.start, transcription.end)
            })
            .background(
                if (selected) {
                    colors.secondaryContainer.copy(alpha = .2f)
                } else {
                    Color.Unspecified
                }
            )

    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (showTimestamp) {
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            if (selected) colors.primaryContainer else colors.secondaryContainer
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = transcription.start.timestamp(),
                        color = if (selected) {
                            colors.onPrimaryContainer
                        } else {
                            colors.onSecondaryContainer
                        },
                        style = typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Text(
                text = transcription.text.removePrefix(":  "),
                color = if (selected) {
                    colors.onSecondaryContainer
                } else {
                    colors.secondary.copy(
                        alpha = 0.5f
                    )
                },
                style = typography.titleLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Suppress("unused")
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
    val audioEntity = json.decodeFromString<AudioEntity>(FakeAudioData.audio_1888_11_13)

    AppTheme {
        TextViewer(
            transcription = audioEntity.transcription!!,
            onClick = { start, end -> },
            showTimestamp = true
        )
    }
}
