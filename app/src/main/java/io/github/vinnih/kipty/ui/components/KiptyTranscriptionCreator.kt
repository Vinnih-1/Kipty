package io.github.vinnih.kipty.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.ui.theme.AppTheme

@Composable
fun KiptyTranscriptionCreator(
    modifier: Modifier = Modifier,
    onConfirm: (String) -> Unit,
    onCancel: () -> Unit
) {
    var description by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onCancel
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Create transcription icon",
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = stringResource(R.string.create_transcription_dialog_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold)
                Text(
                    text = stringResource(R.string.create_transcription_dialog_description),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Light
                )
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.create_transcription_dialog_label)) },
                    singleLine = true
                )
                Button(onClick = { onConfirm(description) }) { Text(stringResource(R.string.create_transcription_dialog_confirm_button)) }
            }
        }
    }
}

@Preview(
    showBackground = true, showSystemUi = false, name = "Light Mode",
    uiMode = Configuration.UI_MODE_TYPE_NORMAL, device = "id:pixel_9"
)
@Preview(
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    device = "id:pixel_9", showBackground = true, showSystemUi = false
)
@Composable
fun KiptyTranscriptionPreview() {
    var description by remember { mutableStateOf("") }

    AppTheme {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Create transcription icon",
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = stringResource(R.string.create_transcription_dialog_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold)
                Text(
                    text = stringResource(R.string.create_transcription_dialog_description),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Light
                )
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.create_transcription_dialog_label)) },
                )
                Button(onClick = {}) { Text(stringResource(R.string.create_transcription_dialog_confirm_button)) }
            }
        }
    }
}