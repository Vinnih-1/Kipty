package io.github.vinnih.kipty.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.data.FakeAudioData
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.json
import io.github.vinnih.kipty.ui.theme.AppTheme
import java.io.File

@Composable
fun AudioCard(
    audioEntity: AudioEntity,
    onNavigate: () -> Unit,
    onPlay: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typography = MaterialTheme.typography
    val image = File(audioEntity.path, "image.jpg")
    var showModal by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current

    ElevatedCard(
        modifier = modifier.padding(8.dp).combinedClickable(
            onClick = onNavigate,
            onLongClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                showModal = true
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 12.dp
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(top = 16.dp, start = 8.dp).weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = audioEntity.name,
                    style = typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = audioEntity.description ?: "Audio without any description",
                    style = typography.bodySmall,
                    maxLines = 7,
                    overflow = TextOverflow.Ellipsis
                )
            }
            AsyncImage(
                model = image,
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.width(144.dp).height(200.dp)
            )
        }
    }
    ModalBottomSheetDetails(
        showModal = showModal,
        onDismiss = { showModal = false },
        onPlay = onPlay,
        onDelete = onDelete
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModalBottomSheetDetails(
    showModal: Boolean,
    onDismiss: () -> Unit,
    onPlay: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (showModal) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            modifier = modifier
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                ModalItem(
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.play_arrow),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                    },
                    text = { Text("Play item") },
                    onClick = {
                        onDismiss.invoke()
                        onPlay.invoke()
                    }
                )
                ModalItem(
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.delete),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                    },
                    text = { Text("Remove") },
                    onClick = {
                        onDismiss.invoke()
                        onDelete.invoke()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModalItem(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().clickable(onClick = {
            onClick.invoke()
        }).padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        icon.invoke()
        text.invoke()
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
            onNavigate = {},
            onPlay = {},
            onDelete = {},
            modifier = Modifier.fillMaxWidth().height(200.dp)
        )
    }
}
