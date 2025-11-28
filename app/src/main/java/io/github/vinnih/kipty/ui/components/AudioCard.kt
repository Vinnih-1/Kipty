package io.github.vinnih.kipty.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.ui.theme.AppTheme

@Composable
fun AudioCard(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    ElevatedCard(
        modifier = modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 12.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = colors.primaryContainer
        ),
        onClick = {
            // TODO: Open audio details screen
        }
    ) {
        Row(
            modifier = Modifier.padding(8.dp).fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.broken_image),
                contentDescription = "Card without image icon",
                modifier = Modifier.size(72.dp)
            )
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                Text(
                    text = "Simplified Speech EP 227",
                    style = typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "finibus sed magna vel, sodales elementum leo. Cras",
                    style = typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
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
private fun AudioCardPreview() {
    AppTheme {
        AudioCard(modifier = Modifier.fillMaxWidth().height(128.dp))
    }
}
