package io.github.vinnih.kipty.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vinnih.kipty.ui.components.KiptyBottomBar
import io.github.vinnih.kipty.ui.components.KiptyTopBar
import io.github.vinnih.kipty.ui.components.KiptyTranscriptionList
import io.github.vinnih.kipty.ui.theme.AppTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    controller: HomeUiController,
) {
    KiptyTranscriptionList(
        modifier = modifier,
        controller = controller,
    )
}

@Preview(
    showBackground = true,
    showSystemUi = false,
    name = "Light Mode",
    uiMode = Configuration.UI_MODE_TYPE_NORMAL,
    device = "id:pixel_9",
)
@Preview(
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    device = "id:pixel_9",
    showBackground = true,
    showSystemUi = false,
)
@Composable
fun KiptyTranscriptionListPreview() {
    val controller: HomeUiController = FakeHomeViewModel()

    AppTheme {
        Scaffold(
            topBar = { KiptyTopBar() },
            bottomBar = { KiptyBottomBar(controller = controller) },
        ) { paddingValues ->
            Surface(
                modifier = Modifier.padding(paddingValues),
            ) {
                HomeScreen(controller = controller)
            }
        }
    }
}
