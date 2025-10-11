package io.github.vinnih.kipty.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.data.local.entity.Transcription

@Composable
fun KiptyTranscriptionList(
    modifier: Modifier = Modifier,
    transcriptions: State<List<Transcription>>,
    onEmptyButtonClick: () -> Unit
) {

    if (transcriptions.value.isEmpty()) {
        Column(
            modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(64.dp, Alignment.CenterVertically)
        ) {
            Image(
                modifier = Modifier.size(256.dp),
                painter = painterResource(R.drawable.undraw_learning_qt7d),
                contentDescription = "Learning undraw image",
            )
            Text(
                text = stringResource(R.string.no_transcriptions_found),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Card(
                modifier = Modifier.width(256.dp).clickable(
                    enabled = true,
                    onClick = onEmptyButtonClick
                ),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = BorderStroke(4.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add_box_48px),
                        contentDescription = "Add transcription icon",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(R.string.add_transcription),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(transcriptions.value) { transcription ->
                KiptyTranscriptionItem(
                    transcription = transcription,
                    onClick = {
                        // TODO: onClick implementation
                    }
                )
            }
        }
    }
}