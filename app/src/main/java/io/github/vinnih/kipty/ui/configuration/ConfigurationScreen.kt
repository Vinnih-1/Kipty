package io.github.vinnih.kipty.ui.configuration

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vinnih.kipty.BuildConfig
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.Screen
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.ui.components.BaseButton
import io.github.vinnih.kipty.ui.components.ProfilePicture
import io.github.vinnih.kipty.ui.create.rememberAudioPicker
import io.github.vinnih.kipty.ui.theme.AppTheme
import io.github.vinnih.kipty.utils.formatListenedTime
import io.github.vinnih.kipty.utils.processUriToFile
import java.io.File

@Composable
fun ProfileSection(
    uiState: ConfigurationsUiState,
    onPickPhoto: (File) -> Unit,
    onUsernameChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val context = LocalContext.current
    val rememberAudioPicker = rememberAudioPicker(
        mimeType = "image/*",
        arrayOf("image/png", "image/jpeg")
    ) {
        val file = it.processUriToFile(context)
        if (file != null) onPickPhoto(file)
    }

    Card(modifier = modifier.padding(horizontal = 16.dp)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfilePicture(
                iconPath = uiState.appSettings!!.profileIconPath,
                onClick = { rememberAudioPicker.invoke() },
                updatedAt = uiState.appSettings.profileIconUpdatedAt
            )
            UsernameSection(
                username = uiState.appSettings.username,
                onUsernameChange = { onUsernameChange(it) }
            )
            Text(
                text = "Tap photo to change, tap name to edit",
                style = typography.bodyMedium,
                color = colors.onBackground.copy(alpha = 0.7f)
            )
            StatsRow(audioList = uiState.audioList, isLoading = uiState.isLoadingAudioList)
        }
    }
}

@Composable
fun UsernameSection(
    username: String,
    onUsernameChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    var isEditing by remember { mutableStateOf(false) }
    var draft by remember(username) { mutableStateOf(username) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isEditing) {
        if (isEditing) focusRequester.requestFocus()
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.clickable { isEditing = true }
    ) {
        if (isEditing) {
            BasicTextField(
                value = draft,
                onValueChange = { if (it.length <= 16) draft = it },
                textStyle = typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        val trimmed = draft.trim()
                        if (trimmed.isNotEmpty()) onUsernameChange(trimmed)
                        isEditing = false
                    }
                ),
                cursorBrush = SolidColor(colors.primary),
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .widthIn(min = 48.dp, max = 200.dp)
            )
            Icon(
                painter = painterResource(id = R.drawable.check),
                contentDescription = "Confirm",
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        val trimmed = draft.trim()
                        if (trimmed.isNotEmpty()) onUsernameChange(trimmed)
                        isEditing = false
                    },
                tint = colors.primary
            )
        } else {
            Text(
                text = username,
                style = typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                painter = painterResource(id = R.drawable.pencil),
                contentDescription = "Edit name",
                modifier = Modifier.size(20.dp),
                tint = colors.onBackground.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun StatsRow(audioList: List<AudioEntity>, isLoading: Boolean, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val audios = audioList.size
    val transcribed = audioList.filter { it.transcription != null }.size
    val totalListened = audioList.sumOf { it.playTime }

    Box(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(
                color = colors.secondaryContainer.copy(alpha = .3f),
                shape = MaterialTheme.shapes.medium
            )
    ) {
        AnimatedVisibility(!isLoading) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(count = "$audios", label = "Audios", modifier = Modifier.weight(1f))
                VerticalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = Color.Gray.copy(alpha = 0.5f)
                )
                StatItem(
                    count = "$transcribed",
                    label = "Transcribed",
                    modifier = Modifier.weight(1f)
                )
                VerticalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = Color.Gray.copy(alpha = 0.5f)
                )
                StatItem(
                    count = totalListened.formatListenedTime(),
                    label = "Listened",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StatItem(count: String, label: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 4.dp)
    ) {
        Text(text = count, style = MaterialTheme.typography.bodyLarge)
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun SettingItemLayout(
    icon: @Composable () -> Unit,
    title: String,
    description: String,
    settingControl: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp)
            .padding(horizontal = 12.dp)
    ) {
        icon()
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Light,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        settingControl()
    }
}

@Composable
private fun SettingIcon(
    @DrawableRes iconRes: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colors.secondaryContainer.copy(alpha = .3f))
            .size(48.dp)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = colors.onSecondaryContainer,
            modifier = Modifier
                .align(Alignment.Center)
                .size(36.dp)
        )
    }
}

@Composable
fun SwitchSettingItem(
    title: String,
    description: String,
    @DrawableRes iconRes: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    SettingItemLayout(
        icon = { SettingIcon(iconRes, title) },
        title = title,
        description = description,
        settingControl = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        modifier = modifier
    )
}

@Composable
fun IncrementSettingItem(
    title: String,
    description: String,
    @DrawableRes iconRes: Int,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    SettingItemLayout(
        icon = { SettingIcon(iconRes, title) },
        title = title,
        description = description,
        settingControl = {
            Box(
                modifier = Modifier.width(110.dp)
            ) {
                BaseButton(
                    onClick = { onValueChange(value - 1) },
                    content = {
                        Icon(
                            painter = painterResource(R.drawable.remove),
                            contentDescription = null,
                            tint = colors.onSecondaryContainer,
                            modifier = Modifier.size(36.dp)
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clip(RoundedCornerShape(12.dp))
                        .size(42.dp)
                        .background(colors.secondaryContainer.copy(.3f))
                )
                Text(
                    text = "$value",
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onSecondaryContainer,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
                BaseButton(
                    onClick = { onValueChange(value + 1) },
                    content = {
                        Icon(
                            painter = painterResource(R.drawable.add),
                            contentDescription = null,
                            tint = colors.onSecondaryContainer,
                            modifier = Modifier.size(36.dp)
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clip(RoundedCornerShape(12.dp))
                        .size(42.dp)
                        .background(colors.secondaryContainer.copy(.3f))
                )
            }
        },
        modifier = modifier
    )
}

@Composable
fun ButtonSettingItem(
    title: String,
    description: String,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    buttonText: String? = null
) {
    val colors = MaterialTheme.colorScheme

    SettingItemLayout(
        icon = { SettingIcon(iconRes, title) },
        title = title,
        description = description,
        settingControl = {
            Box(
                modifier = Modifier.fillMaxWidth(.32f)
            ) {
                if (buttonText != null) {
                    Text(
                        text = buttonText,
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onSecondaryContainer.copy(
                            alpha = if (enabled) 1f else .5f
                        ),
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                }
                Icon(
                    painter = painterResource(R.drawable.chevron_right),
                    contentDescription = null,
                    tint = colors.onSecondaryContainer.copy(
                        alpha = if (enabled) 1f else .5f
                    ),
                    modifier = Modifier
                        .size(18.dp)
                        .align(Alignment.CenterEnd)
                )
            }
        },
        modifier = modifier.clickable(onClick = onClick)
    )
}

@Composable
fun ConfigurationSection(
    title: String,
    items: List<@Composable () -> Unit>,
    modifier: Modifier = Modifier
) {
    val typography = MaterialTheme.typography
    val colors = MaterialTheme.colorScheme

    Column(modifier = modifier) {
        Text(
            text = title,
            modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp),
            style = typography.labelSmall,
            color = colors.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
        Card(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            items.forEachIndexed { index, item ->
                item()

                if (index < items.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun ConfigurationScreen(
    configurationController: ConfigurationController,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by configurationController.uiState.collectAsState()

    if (uiState.isLoadingSettings) return

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        ConfigurationTopBar(onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ProfileSection(
                uiState = uiState,
                onPickPhoto = {
                    configurationController.updateProfileIcon(it)
                },
                onUsernameChange = {
                    configurationController.updateUsername(it)
                }
            )
            ConfigurationSection(
                title = "TRANSCRIPTION",
                items = listOf(
                    {
                        SwitchSettingItem(
                            title = "Show timestamp",
                            description = "Display time markers in transcriptions",
                            iconRes = R.drawable.schedule,
                            checked = uiState.appSettings!!.showTimestamp,
                            onCheckedChange = { configurationController.updateShowTimestamp(it) }
                        )
                    },
                    {
                        IncrementSettingItem(
                            title = "Processing threads",
                            description = "Number of threads (1-8)",
                            iconRes = R.drawable.memory,
                            value = uiState.appSettings!!.minimumThreads,
                            onValueChange = {
                                configurationController.updateMinimumThreads(it.coerceIn(1, 8))
                            }
                        )
                    },
                    {
                        ButtonSettingItem(
                            title = "New transcription",
                            description = "Transcribe your favourite podcast",
                            iconRes = R.drawable.mic,
                            onClick = { onNavigate(Screen.Create) },
                            enabled = uiState.canCreate
                        )
                    },
                    {
                        ButtonSettingItem(
                            title = "Language",
                            description = "Select app language",
                            buttonText = "English",
                            iconRes = R.drawable.language,
                            onClick = { },
                            enabled = false
                        )
                    }
                )
            )
            ConfigurationSection(
                title = "NOTIFICATIONS",
                items = listOf(
                    {
                        SwitchSettingItem(
                            title = "Notifications",
                            description = "Receive notifications about new episodes",
                            iconRes = R.drawable.notifications,
                            checked = uiState.appSettings!!.receiveAlert,
                            onCheckedChange = { configurationController.updateReceiveAlert(it) }
                        )
                    }
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Kipty v${BuildConfig.VERSION_NAME} · Made with care",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfigurationTopBar(onBack: () -> Unit, modifier: Modifier = Modifier) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Customize your experience",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        },
        navigationIcon = {
            BaseButton(onClick = onBack, content = {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )
            })
        },
        modifier = modifier
    )
    HorizontalDivider()
}

@Preview(
    showSystemUi = false,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun ConfigurationScreenPreview() {
    AppTheme {
        ConfigurationScreen(
            configurationController = FakeConfigurationViewModel(),
            onNavigate = {},
            onBack = {}
        )
    }
}
