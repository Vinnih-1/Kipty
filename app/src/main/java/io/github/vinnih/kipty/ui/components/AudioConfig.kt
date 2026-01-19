package io.github.vinnih.kipty.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.Screen
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.ui.create.Step
import io.github.vinnih.kipty.utils.formatTime
import io.github.vinnih.kipty.utils.getAssetAudioInfo
import io.github.vinnih.kipty.utils.getFormattedSize
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioConfigSheet(
    audioEntity: AudioEntity?,
    onDismiss: () -> Unit,
    onPlay: () -> Unit,
    onTranscript: () -> Unit,
    onNavigate: (Screen) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (audioEntity == null) return

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            AudioConfigTop(
                audioEntity = audioEntity,
                onDismiss = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider()
            AudioConfigButtons(
                audioEntity = audioEntity,
                onPlay = {
                    onPlay()
                    onDismiss()
                },
                onTranscript = {
                    onTranscript()
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider()
            Column {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                ) {
                    Text(
                        text = "EDIT",
                        color = colors.onBackground.copy(.6f),
                        style = typography.bodySmall
                    )
                    AudioConfigItem(
                        onClick = {
                            onNavigate(Screen.Edit(audioEntity.uid, Step.DETAILS))
                        },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.pencil),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        text = {
                            Text(
                                text = "Edit Title",
                                color = colors.onBackground,
                                style = typography.titleMedium
                            )
                        },
                        description = {
                            Text(
                                text = "Change the audio name",
                                color = colors.onBackground.copy(alpha = .6f),
                                style = typography.bodySmall
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    AudioConfigItem(
                        onClick = {
                            onNavigate(Screen.Edit(audioEntity.uid, Step.DETAILS))
                        },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.file_text),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        text = {
                            Text(
                                text = "Edit Description",
                                color = colors.onBackground,
                                style = typography.titleMedium
                            )
                        },
                        description = {
                            Text(
                                text = "Update audio details",
                                color = colors.onBackground.copy(alpha = .6f),
                                style = typography.bodySmall
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    AudioConfigItem(
                        onClick = {
                            if (audioEntity.isDefault) {
                                Toast.makeText(
                                    context,
                                    "Default audio icon cannot be changed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                onNavigate(Screen.Edit(audioEntity.uid, Step.IMAGE))
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.image),
                                contentDescription = null,
                                tint = colors.onBackground.copy(
                                    alpha = if (audioEntity.isDefault) .5f else 1f
                                ),
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        text = {
                            Text(
                                text = "Change Cover",
                                color = colors.onBackground.copy(
                                    alpha = if (audioEntity.isDefault) .5f else 1f
                                ),
                                style = typography.titleMedium
                            )
                        },
                        description = {
                            Text(
                                text = "Select a new thumbnail",
                                color = colors.onBackground.copy(
                                    alpha = if (audioEntity.isDefault) .3f else .6f
                                ),
                                style = typography.bodySmall
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                HorizontalDivider()
                AudioConfigItem(
                    onClick = {
                        onDelete()
                        onDismiss()
                    },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.delete),
                            contentDescription = null,
                            tint = colors.onErrorContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    text = {
                        Text(
                            text = "Delete Audio",
                            color = colors.error,
                            style = typography.titleMedium
                        )
                    },
                    description = {
                        Text(
                            text = "Remove permanently",
                            color = colors.onBackground.copy(alpha = .6f),
                            style = typography.bodySmall
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun AudioConfigTop(
    audioEntity: AudioEntity,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val (image, audioInfo) = if (audioEntity.isDefault) {
        Pair(
            "file:///android_asset/${audioEntity.imagePath}",
            audioEntity.audioPath.getAssetAudioInfo(context)
        )
    } else {
        val audio = File(audioEntity.audioPath)
        Pair(
            File(audioEntity.imagePath),
            Pair(audio.length().formatTime(), audio.length().getFormattedSize())
        )
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
        modifier = modifier.fillMaxWidth().padding(vertical = 16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = image,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .width(64.dp)
                    .height(64.dp)
            )
            Column(
                modifier = Modifier.fillMaxWidth(.8f)
            ) {
                Text(
                    text = audioEntity.name,
                    style = typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.clock),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Text(
                            text = audioInfo.first,
                            style = typography.bodyMedium,
                            color = colors.onSurface.copy(alpha = 0.5f)
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.hard_drive),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = colors.onSurface.copy(alpha = 0.5f)
                        )
                        Text(
                            text = audioInfo.second,
                            style = typography.bodyMedium,
                            color = colors.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
        BaseButton(
            onClick = onDismiss,
            modifier = Modifier
                .clip(CircleShape)
                .size(28.dp)
                .background(colors.secondaryContainer.copy(alpha = .3f))
        ) {
            Icon(
                painter = painterResource(R.drawable.close),
                contentDescription = null,
                tint = colors.onSecondaryContainer,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun AudioConfigButtons(
    audioEntity: AudioEntity,
    onPlay: () -> Unit,
    onTranscript: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Row(
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        modifier = modifier.fillMaxWidth().padding(vertical = 16.dp)
    ) {
        Button(
            onClick = onPlay,
            enabled = !audioEntity.transcription.isNullOrEmpty(),
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = colors.primaryContainer.copy(alpha = .2f),
                disabledContentColor = colors.onPrimaryContainer.copy(alpha = .4f)
            ),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.play_arrow_filled),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Text("Play")
            }
        }
        Button(
            onClick = onTranscript,
            enabled = audioEntity.transcription.isNullOrEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.secondaryContainer.copy(alpha = .4f),
                contentColor = colors.onSecondaryContainer,
                disabledContainerColor = colors.secondaryContainer.copy(alpha = .4f),
                disabledContentColor = colors.onSecondaryContainer.copy(alpha = .2f)
            ),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.type),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Text("Transcript")
            }
        }
    }
}

@Composable
private fun AudioConfigItem(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    description: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth().height(64.dp).clickable(onClick = onClick)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(42.dp)
                    .background(colors.onSecondaryContainer.copy(alpha = .2f))
            ) {
                icon.invoke()
            }
            Column {
                text.invoke()
                description.invoke()
            }
        }
        Icon(
            painter = painterResource(R.drawable.chevron_right),
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
    }
}
