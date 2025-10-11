package io.github.vinnih.kipty.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.data.local.entity.Transcription

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KiptyTranscriptionItem(
    modifier: Modifier = Modifier,
    transcription: Transcription,
    onClick: (Transcription) -> Unit = {}
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        ModalBottomSheet(onDismissRequest = { showBottomSheet = false }) {
            Text("Hi")
        }
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick },
                    onLongPress = { showBottomSheet = true }
                )
            },
    ) {
        Row {
            Icon(
                modifier = Modifier.size(128.dp),
                painter = painterResource(R.drawable.image_48px),
                contentDescription = "No image found icon",
                tint = MaterialTheme.colorScheme.onBackground
            )
            Column(modifier = Modifier.padding(top = 14.dp)) {
                Text(transcription.transcriptionName)
                Text(transcription.transcriptionDescription!!)
            }
        }
    }
}