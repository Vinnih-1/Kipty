package io.github.vinnih.kipty.ui.notification

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.Screen
import io.github.vinnih.kipty.data.database.entity.NotificationCategory
import io.github.vinnih.kipty.data.database.entity.NotificationEntity
import io.github.vinnih.kipty.ui.components.BaseButton
import io.github.vinnih.kipty.ui.theme.AppTheme
import io.github.vinnih.kipty.utils.toRelativeTime

@Composable
fun NotificationScreen(
    notificationController: NotificationController,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by notificationController.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(modifier = modifier.fillMaxSize()) {
        NotificationTopBar(onBack = onBack, uiState.unread.size)
        Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
            AnimatedVisibility(visible = !uiState.today.isEmpty()) {
                NotificationSection(
                    title = "Today",
                    items = uiState.today,
                    onNavigate = {
                        onNavigate(it)
                    },
                    onRead = { notificationController.read(it) },
                    onDelete = { notificationController.delete(it) }
                )
            }
            AnimatedVisibility(visible = !uiState.yesterday.isEmpty()) {
                NotificationSection(
                    title = "Yesterday",
                    items = uiState.yesterday,
                    onNavigate = { onNavigate(it) },
                    onRead = { notificationController.read(it) },
                    onDelete = { notificationController.delete(it) }
                )
            }
            AnimatedVisibility(visible = !uiState.earlier.isEmpty()) {
                NotificationSection(
                    title = "Earlier",
                    items = uiState.earlier,
                    onNavigate = { onNavigate(it) },
                    onRead = { notificationController.read(it) },
                    onDelete = { notificationController.delete(it) }
                )
            }
            if (uiState.today.isEmpty() &&
                uiState.yesterday.isEmpty() &&
                uiState.earlier.isEmpty()
            ) {
                NotificationEmpty()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationTopBar(
    onBack: () -> Unit,
    unreadNotifications: Int,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    TopAppBar(
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notifications",
                    style = typography.titleLarge,
                    color = colors.onBackground,
                    fontWeight = FontWeight.Bold
                )
                if (unreadNotifications > 0) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .width(36.dp)
                            .height(32.dp)
                            .background(colors.secondaryContainer)
                    ) {
                        Text(
                            text = unreadNotifications.toString(),
                            style = typography.titleMedium,
                            color = colors.onSecondaryContainer,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        },
        navigationIcon = {
            BaseButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = null,
                    tint = colors.onBackground,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        expandedHeight = 100.dp,
        modifier = modifier
    )
    HorizontalDivider()
}

@Composable
private fun NotificationSection(
    title: String,
    items: List<NotificationEntity>,
    onNavigate: (Screen) -> Unit,
    onRead: (NotificationEntity) -> Unit,
    onDelete: (NotificationEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth().padding(vertical = 12.dp)
    ) {
        Text(
            text = title,
            style = typography.titleMedium,
            color = colors.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEach { audioEntity ->
                NotificationComponent(
                    notificationEntity = audioEntity,
                    onRead = { onRead(audioEntity) },
                    onNavigate = { onNavigate(it) },
                    onDelete = { onDelete(audioEntity) },
                    divider = audioEntity != items.last()
                )
            }
        }
    }
}

@Composable
private fun NotificationComponent(
    notificationEntity: NotificationEntity,
    onRead: () -> Unit,
    onDelete: () -> Unit,
    onNavigate: (Screen) -> Unit,
    divider: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val action: @Composable () -> Unit = {
        when (notificationEntity.channel) {
            NotificationCategory.TRANSCRIPTION_INIT -> {
                Button(onClick = { onRead() }) {
                    Text(
                        text = "View",
                        style = typography.bodyMedium,
                        color = colors.onPrimaryContainer
                    )
                }
            }

            NotificationCategory.TRANSCRIPTION_DONE -> {
                Button(onClick = { onNavigate(Screen.Audio(notificationEntity.audioId)) }) {
                    Text(
                        text = "Listen",
                        style = typography.bodyMedium,
                        color = colors.onPrimaryContainer
                    )
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = if (notificationEntity.read) {
                    Color.Transparent
                } else {
                    colors.secondaryContainer.copy(.3f)
                }
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(46.dp)
                    .background(colors.secondaryContainer.copy(alpha = .6f))
            ) {
                Icon(
                    painter = painterResource(notificationEntity.channel.iconId),
                    contentDescription = null,
                    tint = colors.onSecondaryContainer,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Column(modifier = Modifier.fillMaxWidth().weight(.8f)) {
                Column {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = notificationEntity.title,
                            style = typography.titleMedium,
                            color = colors.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                        NotificationItemMenu(onRead = onRead, onDelete = onDelete)
                    }
                    Text(
                        text = notificationEntity.content,
                        style = typography.bodyMedium,
                        color = colors.onBackground.copy(alpha = .6f)
                    )
                    Text(
                        text = notificationEntity.audioName,
                        style = typography.bodyLarge,
                        color = colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notificationEntity.createdAt.toRelativeTime(),
                        style = typography.bodyMedium,
                        color = colors.onBackground.copy(alpha = .6f)
                    )
                    action.invoke()
                }
            }
        }
    }
    if (divider) HorizontalDivider(thickness = 2.dp)
}

@Composable
private fun NotificationItemMenu(
    onRead: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        BaseButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.align(Alignment.Center)
        ) {
            Icon(
                painter = painterResource(R.drawable.more_vertical),
                contentDescription = null,
                tint = colors.onBackground.copy(alpha = .6f)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Mark as read", color = colors.onBackground) },
                onClick = {
                    expanded = false
                    onRead()
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.check),
                        contentDescription = null,
                        tint = colors.secondaryContainer.copy(.6f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            )
            DropdownMenuItem(
                text = { Text("Delete", color = colors.error) },
                onClick = {
                    expanded = false
                    onDelete()
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.delete),
                        contentDescription = null,
                        tint = colors.secondaryContainer.copy(.6f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            )
        }
    }
}

@Composable
private fun NotificationEmpty(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.notifications),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
        Text(
            text = "You have no notifications",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = "Check back later for updates",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
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
private fun NotificationScreenPreview() {
    AppTheme {
        NotificationScreen(
            notificationController = FakeNotificationViewModel(),
            onNavigate = {},
            onBack = {}
        )
    }
}
