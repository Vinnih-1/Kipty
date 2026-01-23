package io.github.vinnih.kipty.ui.configuration

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vinnih.kipty.BuildConfig
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.Screen
import io.github.vinnih.kipty.ui.components.BaseButton
import io.github.vinnih.kipty.ui.theme.AppTheme

sealed interface SettingsType {
    class Switch(val value: Boolean) : SettingsType

    class Increment(val value: Int) : SettingsType

    class Button(val onClick: () -> Unit) : SettingsType
}

abstract class ConfigurationProvider(val title: String, val description: String) {

    @Composable
    abstract fun Icon(colors: ColorScheme, typography: Typography, modifier: Modifier = Modifier)

    @Composable
    abstract fun Settings(
        colors: ColorScheme,
        typography: Typography,
        modifier: Modifier = Modifier
    )

    class ShowTimestampConfig(val value: Boolean, val onValueChange: (Boolean) -> Unit) :
        ConfigurationProvider(
            title = "Show timestamp",
            description = "Display time markers in transcriptions"
        ),
        SettingsType by SettingsType.Switch(value) {

        @Composable
        override fun Icon(colors: ColorScheme, typography: Typography, modifier: Modifier) {
            Box(
                modifier = modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.secondaryContainer.copy(alpha = .3f))
                    .size(48.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.schedule),
                    contentDescription = null,
                    tint = colors.onSecondaryContainer,
                    modifier = Modifier.align(Alignment.Center).size(36.dp)
                )
            }
        }

        @Composable
        override fun Settings(colors: ColorScheme, typography: Typography, modifier: Modifier) {
            androidx.compose.material3.Switch(
                checked = value,
                onCheckedChange = {
                    onValueChange(it)
                },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = colors.primary
                )
            )
        }
    }

    class MinimumThreadsConfig(val value: Int, val onValueChange: (Int) -> Unit) :
        ConfigurationProvider(
            title = "Minimum threads",
            description = "Number of processing threads (1-8)"
        ),
        SettingsType by SettingsType.Increment(value) {

        @Composable
        override fun Icon(colors: ColorScheme, typography: Typography, modifier: Modifier) {
            Box(
                modifier = modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.secondaryContainer.copy(alpha = .3f))
                    .size(48.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.memory),
                    contentDescription = null,
                    tint = colors.onSecondaryContainer,
                    modifier = Modifier.align(Alignment.Center).size(36.dp)
                )
            }
        }

        @Composable
        override fun Settings(colors: ColorScheme, typography: Typography, modifier: Modifier) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                        .clip(RoundedCornerShape(12.dp))
                        .size(42.dp)
                        .background(colors.secondaryContainer.copy(.3f))
                )
                Text(
                    text = "$value",
                    style = typography.titleLarge,
                    color = colors.onSecondaryContainer,
                    fontWeight = FontWeight.Bold
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
                        .clip(RoundedCornerShape(12.dp))
                        .size(42.dp)
                        .background(colors.secondaryContainer.copy(.3f))
                )
            }
        }
    }

    class CreateNewAudio(val uiState: ConfigurationsUiState, val onClick: () -> Unit) :
        ConfigurationProvider(
            title = "Transcript your audio",
            description = "Create a transcription of your favourite podcast"
        ),
        SettingsType by SettingsType.Button(onClick) {

        @Composable
        override fun Icon(colors: ColorScheme, typography: Typography, modifier: Modifier) {
            Box(
                modifier = modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.secondaryContainer.copy(alpha = .3f))
                    .size(48.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.mic),
                    contentDescription = null,
                    tint = colors.onSecondaryContainer,
                    modifier = Modifier.align(Alignment.Center).size(36.dp)
                )
            }
        }

        @Composable
        override fun Settings(colors: ColorScheme, typography: Typography, modifier: Modifier) {
            androidx.compose.material3.Button(
                onClick = onClick,
                shape = MaterialTheme.shapes.medium,
                enabled = uiState.canCreate,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.secondaryContainer.copy(alpha = .6f),
                    disabledContainerColor = colors.secondaryContainer.copy(alpha = .3f)
                )
            ) {
                Text(
                    text = "Transcript",
                    style = typography.titleMedium,
                    color = colors.onSecondaryContainer.copy(
                        alpha = if (uiState.canCreate) 1f else .5f
                    )
                )
            }
        }
    }

    class ReceiveAlertConfig(val value: Boolean, val onValueChange: (Boolean) -> Unit) :
        ConfigurationProvider(
            title = "Notifications",
            description = "Receive alerts when transcription completes"
        ),
        SettingsType by SettingsType.Switch(value) {

        @Composable
        override fun Icon(colors: ColorScheme, typography: Typography, modifier: Modifier) {
            Box(
                modifier = modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.secondaryContainer.copy(alpha = .3f))
                    .size(48.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.notifications),
                    contentDescription = null,
                    tint = colors.onSecondaryContainer,
                    modifier = Modifier.align(Alignment.Center).size(36.dp)
                )
            }
        }

        @Composable
        override fun Settings(colors: ColorScheme, typography: Typography, modifier: Modifier) {
            androidx.compose.material3.Switch(
                checked = value,
                onCheckedChange = {
                    onValueChange(it)
                },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = colors.primary
                )
            )
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
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val scrollState = rememberScrollState()
    val uiState by configurationController.uiState.collectAsState()

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(scrollState)
    ) {
        ConfigurationTopBar(onBack = onBack)

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Card(
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    ConfigurationItem(
                        ConfigurationProvider.ShowTimestampConfig(
                            uiState.appSettings.showTimestamp
                        ) {
                            configurationController.updateShowTimestamp(it)
                        },
                        true
                    )
                    ConfigurationItem(
                        ConfigurationProvider.MinimumThreadsConfig(
                            uiState.appSettings.minimumThreads
                        ) {
                            configurationController.updateMinimumThreads(it.coerceIn(1, 8))
                        },
                        true
                    )
                    ConfigurationItem(
                        ConfigurationProvider.CreateNewAudio(uiState = uiState) {
                            onNavigate(Screen.Create)
                        },
                        true
                    )
                    ConfigurationItem(
                        ConfigurationProvider.ReceiveAlertConfig(uiState.appSettings.receiveAlert) {
                            configurationController.updateReceiveAlert(it)
                        },
                        false
                    )
                }
            }
            Text(
                text = "Kipty v${BuildConfig.VERSION_NAME}",
                style = typography.bodyMedium,
                color = colors.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun ConfigurationItem(
    configurationProvider: ConfigurationProvider,
    divider: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.padding(vertical = 36.dp)
    ) {
        configurationProvider.Icon(colors, typography)
        Column(modifier = Modifier.fillMaxWidth().weight(.7f)) {
            Text(
                text = configurationProvider.title,
                style = typography.titleMedium,
                color = colors.onBackground,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = configurationProvider.description,
                style = typography.bodyMedium,
                color = colors.onBackground,
                fontWeight = FontWeight.Light
            )
        }
        configurationProvider.Settings(colors, typography)
    }
    if (divider) HorizontalDivider()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfigurationTopBar(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.secondary.copy(.1f))
                ) {
                    Icon(
                        painter = painterResource(R.drawable.settings),
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(36.dp)
                    )
                }
                Text(
                    text = "Settings",
                    style = typography.displaySmall,
                    color = colors.onBackground,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        navigationIcon = {
            BaseButton(onClick = onBack, content = {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = null,
                    tint = colors.onBackground,
                    modifier = Modifier.size(36.dp)
                )
            })
        },
        modifier = modifier,
        expandedHeight = 100.dp
    )
    HorizontalDivider(color = colors.primary)
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
