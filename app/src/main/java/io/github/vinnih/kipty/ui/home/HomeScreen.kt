package io.github.vinnih.kipty.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.ui.components.KiptyBottomBar
import io.github.vinnih.kipty.ui.components.KiptyTopBar
import io.github.vinnih.kipty.ui.theme.AppTheme

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(64.dp, Alignment.CenterVertically)
    ) {
        Icon(
            modifier = Modifier.size(256.dp),
            painter = painterResource(R.drawable.undraw_learning_qt7d),
            contentDescription = "Learning undraw image",
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(R.string.no_transcriptions_found),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Card(
            modifier = Modifier.width(256.dp).clickable(
                enabled = true,
                onClick = {
                    // TODO: Handle click
                }
            ),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            border = BorderStroke(4.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.add_box_48px),
                    contentDescription = "Add transcription icon",
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = stringResource(R.string.add_transcription),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
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