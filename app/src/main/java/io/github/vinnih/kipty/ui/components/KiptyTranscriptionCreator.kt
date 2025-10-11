package io.github.vinnih.kipty.ui.components

import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.data.local.entity.Transcription
import io.github.vinnih.kipty.ui.home.HomeViewModel
import io.github.vinnih.kipty.ui.theme.AppTheme
import io.github.vinnih.kipty.utils.getFileName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun KiptyTranscriptionCreator(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    var description by remember { mutableStateOf("") }
    var pickedUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) {
            println(it)
        if (it != null) {
            pickedUri = it
        }
    }

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
                TextButton(
                    onClick = { launcher.launch(arrayOf("audio/*")) }
                ) {
                    Text(
                        text = if (pickedUri != null) getFileName(context, pickedUri!!)!! else stringResource(R.string.create_transcription_dialog_select_button)
                    )
                }
                Button(
                    modifier = Modifier.padding(bottom = 8.dp),
                    onClick = {
                        if (pickedUri == null) {
                            Toast.makeText(context, R.string.create_transcription_dialog_no_file_toast, Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())
                        val transcription = Transcription(
                            transcriptionName = getFileName(context, pickedUri!!) ?: "",
                            transcriptionUri = pickedUri!!.toString(),
                            transcriptionDescription = description,
                            createdAt = date
                        )
                        viewModel.createTranscription(transcription).also { onConfirm.invoke() }
                    }
                ) { Text(stringResource(R.string.create_transcription_dialog_confirm_button)) }
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