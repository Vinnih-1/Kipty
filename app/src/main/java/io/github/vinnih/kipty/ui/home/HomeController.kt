package io.github.vinnih.kipty.ui.home

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface HomeController {

    val homeUiState: StateFlow<HomeUiState>

    fun loadAudios(override: Boolean = false)

    fun openNotificationSettings()

    fun getPlayTimeById(id: Int): Flow<Long>

    suspend fun populateDatabase(onSuccess: () -> Unit)
}
