package io.github.vinnih.kipty.ui.welcome

import java.io.File
import kotlinx.coroutines.flow.StateFlow

interface WelcomeController {

    val uiState: StateFlow<WelcomeUiState>

    fun nextStep()

    fun previousStep()

    fun updateUsername(username: String)

    fun updateProfileIcon(file: File)

    suspend fun saveProfile()

    suspend fun populateDatabase(onSuccess: () -> Unit)
}
