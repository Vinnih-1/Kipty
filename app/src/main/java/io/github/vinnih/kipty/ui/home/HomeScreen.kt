package io.github.vinnih.kipty.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.ui.components.AudioCard
import io.github.vinnih.kipty.ui.theme.AppTheme

@Composable
fun HomeScreen(
    controller: HomeController,
    onNotificationClick: () -> Unit,
    onCreateClick: () -> Unit,
    onClick: (AudioEntity) -> Unit,
    onTopBarChange: (@Composable () -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val audioState = controller.value.collectAsState()

    LaunchedEffect(Unit) {
        controller.updateAudioFiles()
    }

    onTopBarChange {
        HomeTopBar(onNotificationClick = onNotificationClick, onCreateClick = onCreateClick)
    }

    Column(modifier = modifier.fillMaxSize().padding(top = 20.dp)) {
        audioState.value.forEach { audioData ->
            AudioCard(audioEntity = audioData, onClick = {
                onClick(audioData)
            }, modifier = Modifier.fillMaxWidth().height(128.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    onNotificationClick: () -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    TopAppBar(
        title = {
            Text(
                text = "Home",
                style = typography.displayMedium
            )
        },
        actions = {
            Row(
                modifier = Modifier.padding(end = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(onClick = onNotificationClick, Modifier.size(36.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.notifications),
                        contentDescription = "Account circle icon button",
                        modifier = Modifier.fillMaxSize(),
                        tint = colors.primary
                    )
                }
                IconButton(onClick = onCreateClick, Modifier.size(36.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.speech_to_text),
                        contentDescription = "Create new audio icon button",
                        modifier = Modifier.fillMaxSize(),
                        tint = colors.primary
                    )
                }
            }
        },
        modifier = modifier,
        expandedHeight = 78.dp
    )
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
        HomeScreen(controller = FakeHomeViewModel(), onClick = {
        }, onNotificationClick = {}, onCreateClick = {}, onTopBarChange = {
        }, modifier = Modifier)
    }
}
