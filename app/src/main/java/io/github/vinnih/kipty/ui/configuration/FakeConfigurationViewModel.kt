package io.github.vinnih.kipty.ui.configuration

import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeConfigurationViewModel(
    configurationUiState: ConfigurationsUiState = ConfigurationsUiState()
) : ConfigurationController {
    override val uiState: StateFlow<ConfigurationsUiState> = MutableStateFlow(configurationUiState)

    override fun updateShowTimestamp(showTimestamp: Boolean) {}

    override fun updateMinimumThreads(minimumThreads: Int) {}

    override fun updateReceiveAlert(receiveAlert: Boolean) {}

    override fun updateProfileIcon(file: File) {}

    override fun updateUsername(username: String) {}
}
