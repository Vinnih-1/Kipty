package io.github.vinnih.kipty.ui.configuration

import io.github.vinnih.kipty.data.settings.AppSettings
import kotlinx.coroutines.flow.StateFlow

interface ConfigurationController {
    val uiState: StateFlow<AppSettings>

    fun updateShowTimestamp(showTimestamp: Boolean)

    fun updateMinimumThreads(minimumThreads: Int)

    fun updateReceiveAlert(receiveAlert: Boolean)
}
