package io.github.vinnih.kipty.ui.notification

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.ui.components.BackButton
import io.github.vinnih.kipty.ui.theme.AppTheme

@Composable
fun NotificationScreen(
    notificationController: NotificationController,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val notifications = notificationController.allNotifications.collectAsState()

    Column(
        modifier = modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp, top = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Most recent",
            style = typography.titleLarge,
            color = colors.scrim,
            fontWeight = FontWeight.Normal
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            items(notifications.value) { notification ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.notification_icon),
                        contentDescription = "Notification icon",
                        modifier = Modifier.size(48.dp).clip(CircleShape)
                    )
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row {
                            Text(
                                text = notification.title,
                                style = typography.titleMedium,
                                color = colors.scrim,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth(.7f)
                            )

                            Text(
                                text = notification.createdAt,
                                style = typography.bodyMedium,
                                color = colors.secondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = notification.content,
                            style = typography.bodyMedium,
                            color = colors.secondary,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationTopBar(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    LargeTopAppBar(title = {
        Text(
            text = "Notifications",
            style = typography.titleLarge,
            color = colors.scrim,
            fontWeight = FontWeight.Bold
        )
    }, navigationIcon = {
        BackButton(onClick = onBack)
    }, actions = {
        IconButton(onClick = {}) {
            Icon(
                painter = painterResource(id = R.drawable.settings),
                contentDescription = "Settings icon button"
            )
        }
    }, modifier = modifier)
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
private fun NotificationScreenPreview() {
    AppTheme {
        NotificationScreen(notificationController = FakeNotificationViewModel())
    }
}
