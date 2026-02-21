package io.github.vinnih.kipty.ui.configuration

import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeConfigurationViewModel : ConfigurationController {
    override val uiState: StateFlow<ConfigurationsUiState>
        get() = MutableStateFlow(
            ConfigurationsUiState(canCreate = true, appSettings = null)
        )

    override fun updateShowTimestamp(showTimestamp: Boolean) {
        TODO("Not yet implemented")
    }

    override fun updateMinimumThreads(minimumThreads: Int) {
        TODO("Not yet implemented")
    }

    override fun updateReceiveAlert(receiveAlert: Boolean) {
        TODO("Not yet implemented")
    }

    override fun updateProfileIcon(file: File) {
        TODO("Not yet implemented")
    }
}
