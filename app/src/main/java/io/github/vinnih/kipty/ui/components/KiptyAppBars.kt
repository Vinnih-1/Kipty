package io.github.vinnih.kipty.ui.components

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.ui.home.HomeUiController
import io.github.vinnih.kipty.ui.player.PlayerScreen
import io.github.vinnih.kipty.ui.theme.AppTheme

enum class BottomBarDestinations(
    val size: Dp = 32.dp,
    val icon: Int,
    val route: String,
) {
    HOME(icon = R.drawable.home_48px, route = "home"),
    ADD(icon = R.drawable.add_diamond_48px, route = "add", size = 52.dp),
    HISTORY(icon = R.drawable.manage_history_48px, route = "history"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KiptyTopBar(modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "vosk-model-small",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            TextButton(
                onClick = {},
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Arrow dropdown menu icon",
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        },
        actions = {
            TextButton(
                onClick = {},
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.folder_code_48px),
                    contentDescription = "Resource code icon",
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        },
    )
}

@Composable
fun KiptyBottomBar(
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

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        KiptyPlayerBar()
        NavigationBar(
            modifier = modifier,
        ) {
            BottomBarDestinations.entries.forEach { destinations ->
                NavigationBarItem(
                    selected = false,
                    onClick = { openTranscriptionCreator = true },
                    icon = {
                        Icon(
                            painter = painterResource(destinations.icon),
                            destinations.route,
                            modifier = Modifier.size(destinations.size),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KiptyPlayerBar() {
    var showSheet by remember { mutableStateOf(false) }

    if (showSheet) {
        PlayerScreen(
            onClick = {},
            onClose = { showSheet = false },
        )
    }

    Button(
        onClick = { showSheet = true },
        modifier = Modifier.fillMaxWidth(),
        shape = RectangleShape,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.image_48px),
                contentDescription = "",
            )
            Column(
                modifier = Modifier.fillMaxWidth().weight(1f),
            ) {
                Text("title")
                Text("description")
            }
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "",
            )
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true,
    showSystemUi = false,
    name = "Light Mode",
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true,
    name = "Dark Mode",
)
@Composable
fun KiptyPlayerBarPreview() {
    AppTheme {
        KiptyPlayerBar()
    }
}
