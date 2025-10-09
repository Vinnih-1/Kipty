package io.github.vinnih.kipty.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.ui.theme.AppTheme

enum class BottomBarDestinations(
    val size: Dp = 32.dp,
    val icon: Int,
    val route: String
) {
    HOME(icon = R.drawable.home_48px, route = "home"),
    ADD(icon = R.drawable.add_diamond_48px, route = "add", size = 52.dp),
    HISTORY(icon = R.drawable.manage_history_48px, route = "history")
}

@Composable
fun KiptyTopBar(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = {}
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "Arrow dropdown menu icon",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        Text(
            text = "vosk-model-small",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium
        )
        TextButton(
            onClick = {}
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(R.drawable.folder_code_48px),
                contentDescription = "Resource code icon",
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

@Composable
fun KiptyBottomBar(
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        BottomBarDestinations.entries.forEach { destinations ->
            NavigationBarItem(
                selected = false,
                onClick = {
                    // TODO: Handle click
                },
                icon = {
                    Icon(
                        painter = painterResource(destinations.icon), destinations.route,
                        modifier = Modifier.size(destinations.size),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
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
fun KiptyTopBarPreview() {
    AppTheme {
        KiptyTopBar()
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
fun KiptyBottomBarPreview() {
    AppTheme {
        KiptyBottomBar()
    }
}