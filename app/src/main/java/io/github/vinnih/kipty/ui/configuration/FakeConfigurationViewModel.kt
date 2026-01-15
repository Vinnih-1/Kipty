package io.github.vinnih.kipty.ui.configuration

import io.github.vinnih.kipty.data.settings.AppSettings
import kotlinx.coroutines.flow.StateFlow

class FakeConfigurationViewModel : ConfigurationController {
    override val uiState: StateFlow<AppSettings>
        get() = TODO("Not yet implemented")

    override fun updateShowTimestamp(showTimestamp: Boolean) {
        TODO("Not yet implemented")
    }

    override fun updateMinimumThreads(minimumThreads: Int) {
        TODO("Not yet implemented")
    }

    override fun updateReceiveAlert(receiveAlert: Boolean) {
        TODO("Not yet implemented")
    }
}
