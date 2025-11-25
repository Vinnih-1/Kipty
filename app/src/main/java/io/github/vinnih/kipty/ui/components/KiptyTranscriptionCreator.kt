package io.github.vinnih.kipty.ui.components

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.ui.home.HomeUiController
import io.github.vinnih.kipty.utils.copyFile

@Composable
fun KiptyTranscriptionCreator(
    modifier: Modifier = Modifier,
    controller: HomeUiController,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var description by remember { mutableStateOf("") }
    var pickedUri by remember { mutableStateOf<Uri?>(null) }
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument(),
        ) {
            if (it != null) {
                pickedUri = it
            }
        }

    Dialog(
        onDismissRequest = onCancel,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
        ) {
            Column(
                modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Create transcription icon",
                    modifier = Modifier.size(48.dp),
                )
                Text(
                    text = stringResource(R.string.create_transcription_dialog_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(R.string.create_transcription_dialog_description),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Light,
                )
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.create_transcription_dialog_label)) },
                    singleLine = true,
                )
                TextButton(
                    onClick = { launcher.launch(arrayOf("audio/*")) },
                ) {
                    Text(
                        text = ""
                    )
                }
                Button(
                    modifier = Modifier.padding(bottom = 8.dp),
                    onClick = {
                        if (pickedUri == null) {
                            Toast.makeText(context, R.string.create_transcription_dialog_no_file_toast, Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val file = copyFile(context, pickedUri!!)
                        controller.convertFile(file) {
                            Toast.makeText(context, "File converted successfully: ${file.name.take(10)}.${file.extension}", Toast.LENGTH_SHORT).show()
                        }
                    },
                ) { Text(stringResource(R.string.create_transcription_dialog_confirm_button)) }
            }
        }
    }
}
