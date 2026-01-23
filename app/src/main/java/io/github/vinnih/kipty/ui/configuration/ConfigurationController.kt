package io.github.vinnih.kipty.ui.configuration

import kotlinx.coroutines.flow.StateFlow

interface ConfigurationController {
    val uiState: StateFlow<ConfigurationsUiState>

    fun updateShowTimestamp(showTimestamp: Boolean)

    fun updateMinimumThreads(minimumThreads: Int)

    fun updateReceiveAlert(receiveAlert: Boolean)
}
