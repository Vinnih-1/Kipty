package io.github.vinnih.kipty.ui.components

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.ui.home.HomeUiController

@Composable
fun KiptyTranscriptionList(
    modifier: Modifier = Modifier,
    controller: HomeUiController,
) {
    val context = LocalContext.current
    var openTranscriptionCreator by remember { mutableStateOf(false) }

    if (openTranscriptionCreator) {
        KiptyTranscriptionCreator(
            controller = controller,
            onConfirm = {
                Toast.makeText(context, R.string.create_transcription_dialog_created_toast, Toast.LENGTH_SHORT).show()
                openTranscriptionCreator = false
            },
            onCancel = { openTranscriptionCreator = false },
        )
    }
}
