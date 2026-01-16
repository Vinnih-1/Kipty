package io.github.vinnih.kipty.ui.home

import android.Manifest
import android.content.res.Configuration
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.Screen
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.ui.audio.AudioController
import io.github.vinnih.kipty.ui.audio.FakeAudioViewModel
import io.github.vinnih.kipty.ui.components.AppWarn
import io.github.vinnih.kipty.ui.components.AudioCard
import io.github.vinnih.kipty.ui.components.BaseButton
import io.github.vinnih.kipty.ui.components.WarnType
import io.github.vinnih.kipty.ui.notification.FakeNotificationViewModel
import io.github.vinnih.kipty.ui.notification.NotificationController
import io.github.vinnih.kipty.ui.player.FakePlayerViewModel
import io.github.vinnih.kipty.ui.player.PlayerController
import io.github.vinnih.kipty.ui.theme.AppTheme

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
    var searchQuery by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }
    val filteredAudios = remember(searchQuery, audioState) {
        if (searchQuery.isEmpty()) {
            audioState.value
        } else {
            audioState.value.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (!isSearchExpanded) {
                HomeTopBar(
                    notificationController = notificationController,
                    onNotificationClick = { onNavigate(Screen.Notification) },
                    onNavigate = { onNavigate(it) },
                    onSearchClick = { isSearchExpanded = true }
                )
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    if (notificationWarn && !isSearchExpanded) {
                        NotificationPermissionWarn(
                            onEnable = { homeController.openNotificationSettings() },
                            onDismiss = { notificationWarn = false }
                        )
                    }
                }

                if (audioState.value.isEmpty()) {
                    item { AudioListEmpty(onNavigate = { onNavigate(it) }) }
                }

                items(audioState.value) { audioData ->
                    AudioCard(
                        audioEntity = audioData,
                        onNavigate = {
                            onNavigate(Screen.Audio(audioData.uid))
                        },
                        onPlay = { playerController.playPause(audioData) },
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
        SearchBarView(
            isSearchExpanded = isSearchExpanded,
            onSearchExpandedChange = { isSearchExpanded = it },
            searchQuery = searchQuery,
            onQueryChange = { searchQuery = it },
            filteredAudios = filteredAudios,
            onNavigate = { onNavigate(it) },
            playerController = playerController,
            audioController = audioController,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarView(
    isSearchExpanded: Boolean,
    onSearchExpandedChange: (Boolean) -> Unit,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    filteredAudios: List<AudioEntity>,
    onNavigate: (Screen) -> Unit,
    playerController: PlayerController,
    audioController: AudioController,
    modifier: Modifier = Modifier
) {
    if (!isSearchExpanded) return
    val colors = MaterialTheme.colorScheme
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    SearchBar(
        modifier = modifier.fillMaxWidth(),
        inputField = {
            SearchBarDefaults.InputField(
                query = searchQuery,
                onQueryChange = { onQueryChange(it) },
                onSearch = {
                    onQueryChange("")
                    onSearchExpandedChange(false)
                },
                expanded = isSearchExpanded,
                onExpandedChange = { onSearchExpandedChange(it) },
                placeholder = { Text("Search your audios...") },
                leadingIcon = {
                    BaseButton(
                        onClick = {
                            onQueryChange("")
                            onSearchExpandedChange(false)
                        },
                        content = {
                            Icon(
                                painter = painterResource(R.drawable.arrow_back),
                                contentDescription = null,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        BaseButton(onClick = { onQueryChange("") }, content = {
                            Icon(
                                painter = painterResource(R.drawable.close),
                                contentDescription = null,
                                modifier = Modifier.size(36.dp)
                            )
                        })
                    }
                },
                modifier = Modifier.focusRequester(focusRequester)
            )
        },
        expanded = isSearchExpanded,
        onExpandedChange = { onSearchExpandedChange(it) },
        colors = SearchBarDefaults.colors(
            containerColor = colors.background
        )
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            if (filteredAudios.isEmpty() && searchQuery.isNotEmpty()) {
                item { NoAudioSearchFound() }
            }

            items(filteredAudios) { audioData ->
                AudioCard(
                    audioEntity = audioData,
                    onNavigate = {
                        onNavigate(Screen.Audio(audioData.uid))
                    },
                    onPlay = { playerController.playPause(audioData) },
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
    onNavigate: (Screen) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val notificationState = notificationController.unreadNotifications.collectAsState()

    TopAppBar(
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BaseButton(
                    onClick = {
                        onNavigate(Screen.Configuration)
                    },
                    content = {
                        Icon(
                            painter = painterResource(R.drawable.user),
                            contentDescription = null,
                            tint = colors.onSecondaryContainer,
                            modifier = Modifier.size(36.dp)
                        )
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.secondaryContainer.copy(alpha = .6f))
                        .size(58.dp)
                )
                Column {
                    Text(
                        text = "Welcome",
                        style = typography.titleMedium,
                        fontWeight = FontWeight.Light,
                        color = colors.secondary.copy(alpha = .5f)
                    )
                    Text(
                        text = "Account User",
                        style = typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                }
            }
        },
        actions = {
            Row(
                modifier = Modifier.padding(end = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BaseButton(onClick = onSearchClick, content = {
                    Icon(
                        painter = painterResource(R.drawable.search),
                        contentDescription = "Search icon button",
                        modifier = Modifier.size(36.dp)
                    )
                })
                BaseButton(onClick = onNotificationClick, content = {
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
                })
            }
        },
        modifier = modifier,
        expandedHeight = 100.dp
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun NotificationPermissionWarn(onEnable: () -> Unit, onDismiss: () -> Unit) {
    val permission = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

    if (permission.status.isGranted) return onDismiss()

    val typography = MaterialTheme.typography

    AppWarn(
        warnType = WarnType.Notify,
        icon = {
            Icon(
                painter = painterResource(R.drawable.notifications),
                contentDescription = "Notification icon",
                modifier = Modifier.size(36.dp)
            )
        },
        content = {
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
                modifier = Modifier.padding(top = 8.dp).clickable(onClick = onEnable)
            )
        },
        dismiss = {
            IconButton(onClick = onDismiss) {
                Icon(
                    painter = painterResource(R.drawable.close),
                    contentDescription = null,
                    modifier = Modifier.size(
                        24.dp
                    )
                )
            }
        },
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp)
    )
}

@Composable
private fun NoAudioSearchFound(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.search),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
        Text(
            text = "No audios found",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = "Try searching with different keywords",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}

@Composable
private fun AudioListEmpty(onNavigate: (Screen) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(
            32.dp,
            Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { onNavigate(Screen.Create) },
            modifier = Modifier.height(48.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Create a Transcription")
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
