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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.data.database.entity.NotificationEntity
import io.github.vinnih.kipty.ui.components.BackButton
import io.github.vinnih.kipty.ui.theme.AppTheme

@Composable
fun NotificationScreen(
    notificationController: NotificationController,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typography = MaterialTheme.typography
    val notifications = notificationController.allNotifications.collectAsState()

    LaunchedEffect(Unit) {
        notificationController.readAllNotifications()
    }

    Column(
        modifier = modifier.fillMaxSize().padding(top = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NotificationTopBar(onBack = onBack)
        Text(
            text = "Most recent",
            style = typography.titleLarge,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(notifications.value) { notification ->
                NotificationComponent(notificationEntity = notification)
            }
        }
    }
}

@Composable
private fun NotificationComponent(
    notificationEntity: NotificationEntity,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Row(
        modifier = modifier.fillMaxWidth(),
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
                    text = notificationEntity.title,
                    style = typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(.7f)
                )

                Text(
                    text = notificationEntity.createdAt,
                    style = typography.bodyMedium,
                    color = colors.secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = notificationEntity.content,
                style = typography.bodyMedium,
                color = colors.secondary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationTopBar(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val typography = MaterialTheme.typography

    LargeTopAppBar(title = {
        Text(
            text = "Notifications",
            style = typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }, navigationIcon = {
        BackButton(onClick = onBack, container = Color.Transparent, modifier = Modifier.size(36.dp))
    }, actions = {
        IconButton(onClick = {}) {
            Icon(
                painter = painterResource(id = R.drawable.settings),
                contentDescription = "Settings icon button",
                modifier = Modifier.size(36.dp)
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
        NotificationScreen(notificationController = FakeNotificationViewModel(), onBack = {})
    }
}
