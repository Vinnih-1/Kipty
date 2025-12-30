package io.github.vinnih.kipty.ui.home

import android.Manifest
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.Screen
import io.github.vinnih.kipty.ui.audio.AudioController
import io.github.vinnih.kipty.ui.audio.FakeAudioViewModel
import io.github.vinnih.kipty.ui.components.AudioCard
import io.github.vinnih.kipty.ui.notification.FakeNotificationViewModel
import io.github.vinnih.kipty.ui.notification.NotificationController
import io.github.vinnih.kipty.ui.player.FakePlayerViewModel
import io.github.vinnih.kipty.ui.player.PlayerController
import io.github.vinnih.kipty.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    homeController: HomeController,
    audioController: AudioController,
    playerController: PlayerController,
    notificationController: NotificationController,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val audioState = audioController.allAudios.collectAsState()
    var notificationWarn by remember { mutableStateOf(true) }

    Column(modifier = modifier.fillMaxSize()) {
        HomeTopBar(
            notificationController = notificationController,
            onNotificationClick = { onNavigate(Screen.Notification) },
            onCreateClick = { onNavigate(Screen.Create) }
        )

        if (notificationWarn) {
            NotificationPermissionWarn(
                onEnable = {
                    homeController.openNotificationSettings()
                },
                onDismiss = { notificationWarn = false }
            )
        }

        LazyColumn {
            items(audioState.value) { audioData ->
                AudioCard(
                    audioEntity = audioData,
                    onNavigate = {
                        onNavigate(Screen.Audio(audioData.uid))
                    },
                    onPlay = { playerController.playAudio(audioData) },
                    onDelete = {
                        audioController.deleteAudio(audioData)
                        if (audioData.uid == playerController.currentAudio.value?.uid) {
                            playerController.stopAudio()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    notificationController: NotificationController,
    onNotificationClick: () -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val notificationState = notificationController.unreadNotifications.collectAsState()

    TopAppBar(
        title = {
            Text(
                text = "Kipty",
                style = typography.displayMedium,
                color = colors.onBackground
            )
        },
        actions = {
            Row(
                modifier = Modifier.padding(end = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(onClick = onNotificationClick, Modifier.size(48.dp)) {
                    BadgedBox(badge = {
                        if (notificationState.value.isNotEmpty()) {
                            Badge()
                        }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.notifications),
                            contentDescription = "Notification icon button",
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
                IconButton(onClick = onCreateClick, Modifier.size(48.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.speech_to_text),
                        contentDescription = "Create new audio icon button",
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        },
        modifier = modifier,
        expandedHeight = 90.dp
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun NotificationPermissionWarn(onEnable: () -> Unit, onDismiss: () -> Unit) {
    val permission = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

    if (permission.status.isGranted) return onDismiss()

    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 32.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().height(86.dp),
            colors = CardDefaults.cardColors(
                containerColor = colors.secondaryContainer.copy(alpha = 0.3f),
                contentColor = colors.onSecondaryContainer
            )
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                Icon(
                    painter = painterResource(R.drawable.notifications),
                    contentDescription = "Notification icon",
                    modifier = Modifier.size(36.dp).align(Alignment.TopStart)
                )
                Column(
                    modifier = Modifier.align(
                        Alignment.Center
                    ).padding(horizontal = 48.dp)
                ) {
                    Text(
                        text = "Receive transcription notifications",
                        style = typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Be notified when transcription are ready",
                        style = typography.bodySmall,
                        fontWeight = FontWeight.Light
                    )
                    Text(
                        text = "Enable",
                        style = typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary,
                        modifier = Modifier.padding(top = 8.dp).clickable(onClick = onEnable)
                    )
                }
                Icon(
                    painter = painterResource(R.drawable.close),
                    contentDescription = null,
                    modifier = Modifier.size(
                        24.dp
                    ).align(Alignment.TopEnd).clickable(onClick = onDismiss)
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
private fun HomeScreenPreview() {
    AppTheme {
        HomeScreen(
            homeController = FakeHomeViewModel(),
            audioController = FakeAudioViewModel(),
            playerController = FakePlayerViewModel(),
            notificationController = FakeNotificationViewModel(),
            onNavigate = {},
            modifier = Modifier
        )
    }
}
