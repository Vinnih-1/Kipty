package io.github.vinnih.kipty.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.ui.components.AudioCard
import io.github.vinnih.kipty.ui.components.FloatingAddButton
import io.github.vinnih.kipty.ui.components.KiptyBottomBar
import io.github.vinnih.kipty.ui.components.KiptyTopBar
import io.github.vinnih.kipty.ui.player.FakePlayerViewModel
import io.github.vinnih.kipty.ui.theme.AppTheme

@Composable
fun HomeScreen(
    controller: HomeController,
    onClick: (AudioEntity) -> Unit,
    onTopBarChange: (@Composable () -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val audioState = controller.value.collectAsState()

    LaunchedEffect(Unit) {
        controller.updateAudioFiles()
    }

    onTopBarChange {
        KiptyTopBar("Home")
    }

    Column(modifier = modifier.fillMaxSize().padding(top = 20.dp)) {
        audioState.value.forEach { audioData ->
            AudioCard(audioEntity = audioData, onClick = {
                onClick(audioData)
            }, modifier = Modifier.fillMaxWidth().height(128.dp))
        }
    }
}

@Preview(
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Preview(
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun HomeScreenPreview() {
    AppTheme {
        Scaffold(
            topBar = { KiptyTopBar("Home") },
            bottomBar = { KiptyBottomBar(onClick = {}, playerController = FakePlayerViewModel()) },
            floatingActionButton = {
                FloatingAddButton(onClick = {}, modifier = Modifier.size(72.dp))
            }
        ) { paddingValues ->
            HomeScreen(controller = FakeHomeViewModel(), onClick = {
            }, onTopBarChange = {}, modifier = Modifier.padding(paddingValues))
        }
    }
}
