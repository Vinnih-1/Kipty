package io.github.vinnih.kipty.ui.components

import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.data.local.entity.Transcription
import io.github.vinnih.kipty.ui.home.HomeUiController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KiptyTranscriptionItem(
    modifier: Modifier = Modifier,
    transcription: Transcription,
    controller: HomeUiController,
) {
    val context = LocalContext.current
    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            dragHandle = null
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        controller.deleteTranscription(transcription)
                        Toast.makeText(context, R.string.delete_transcription_successful_toast, Toast.LENGTH_SHORT).show()
                        showBottomSheet = false
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.delete_48px),
                        contentDescription = "Delete icon"
                    )
                }
            }
        }
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        // TODO: Tap implementation
                    },
                    onLongPress = { showBottomSheet = true }
                )
            },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(end = 8.dp),
        ) {
            Icon(
                modifier = Modifier.size(128.dp),
                painter = painterResource(R.drawable.image_48px),
                contentDescription = "No image found icon",
                tint = MaterialTheme.colorScheme.onBackground
            )
            Column(modifier = Modifier.padding(top = 14.dp, bottom = 14.dp)) {
                Text(
                    text = transcription.transcriptionName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = transcription.transcriptionDescription!!,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Light,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}