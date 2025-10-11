package io.github.vinnih.kipty.ui.home

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
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
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.ui.components.KiptyBottomBar
import io.github.vinnih.kipty.ui.components.KiptyTopBar
import io.github.vinnih.kipty.ui.components.KiptyTranscriptionCreator
import io.github.vinnih.kipty.ui.components.KiptyTranscriptionList
import io.github.vinnih.kipty.ui.theme.AppTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context: Context = LocalContext.current
    val transcriptions = viewModel.transcriptions.collectAsState()
    var openTranscriptionCreator by remember { mutableStateOf(false) }

    if (openTranscriptionCreator) {
        KiptyTranscriptionCreator(
            viewModel = viewModel,
            onConfirm = {
                Toast.makeText(context, R.string.create_transcription_dialog_created_toast, Toast.LENGTH_SHORT).show()
                openTranscriptionCreator = false
            },
            onCancel = { openTranscriptionCreator = false }
        )
    }

    KiptyTranscriptionList(
        modifier = modifier,
        transcriptions = transcriptions,
        onEmptyButtonClick = { openTranscriptionCreator = true }
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