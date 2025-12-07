package io.github.vinnih.kipty.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KiptyTopBar(title: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    TopAppBar(
        title = {
            Text(
                text = title,
                style = typography.displayMedium
            )
        },
        actions = {
            IconButton(onClick = {}, Modifier.size(64.dp).padding(end = 12.dp)) {
                Icon(
                    painter = painterResource(R.drawable.account_circle),
                    contentDescription = "Account circle icon button",
                    modifier = Modifier.fillMaxSize(),
                    tint = colors.primary
                )
            }
        },
        modifier = modifier
    )
}

@Composable
fun KiptyBottomBar(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(modifier = modifier.fillMaxWidth()) {
        LinearProgressIndicator(progress = {
            .7f
        }, drawStopIndicator = {}, modifier = Modifier.fillMaxWidth())
        BottomAppBar(
            modifier = Modifier.clickable(onClick = onClick)
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nothing playing",
                    style = typography.titleMedium,
                    color = colors.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(.7f)
                )
                Text(text = "00:00 / --:--", style = typography.bodySmall, color = colors.primary)
            }
        }
    }
}

@Preview(
    group = "Light",
    showSystemUi = false,
    showBackground = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Preview(
    group = "Dark",
    showSystemUi = false,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun KiptyTopBarPreview() {
    AppTheme {
        KiptyTopBar(title = "Home")
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
private fun KiptyBottomBarPreview() {
    AppTheme {
        KiptyBottomBar(onClick = {})
    }
}
