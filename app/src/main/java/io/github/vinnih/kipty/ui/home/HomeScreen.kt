package io.github.vinnih.kipty.ui.home

import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import io.github.vinnih.kipty.data.local.entity.Transcription
import io.github.vinnih.kipty.ui.components.KiptyBottomBar
import io.github.vinnih.kipty.ui.components.KiptyTopBar
import io.github.vinnih.kipty.ui.components.KiptyTranscriptionCreator
import io.github.vinnih.kipty.ui.components.KiptyTranscriptionList
import io.github.vinnih.kipty.ui.theme.AppTheme
import io.github.vinnih.kipty.utils.getFileName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context: Context = LocalContext.current
    val transcriptions = viewModel.transcriptions.collectAsState()
    var pickedUri by remember { mutableStateOf<Uri?>(null) }
    var openTranscriptionCreator by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) {
        if (it != null) {
            pickedUri = it
            openTranscriptionCreator = true
        }
    }

    if (openTranscriptionCreator) {
        KiptyTranscriptionCreator(
            onConfirm = { description ->
                val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())
                val transcription = Transcription(
                    transcriptionName = getFileName(context, pickedUri!!) ?: "",
                    transcriptionUri = pickedUri!!.toString(),
                    transcriptionDescription = description,
                    createdAt = date
                )
                viewModel.createTranscription(transcription)
                openTranscriptionCreator = false
            },
            onCancel = { openTranscriptionCreator = false }
        )
    }

    KiptyTranscriptionList(
        modifier = modifier,
        transcriptions = transcriptions,
        onEmptyButtonClick = { launcher.launch(arrayOf("audio/*")) }
    )
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
fun HomeScreenPreview() {
    AppTheme {
        Scaffold(
            topBar = { KiptyTopBar() },
            bottomBar = { KiptyBottomBar() }
        ) { paddingValues ->
            Surface(
                modifier = Modifier.padding(paddingValues),
                tonalElevation = 5.dp
            ) {
                HomeScreen()
            }
        }
    }
}