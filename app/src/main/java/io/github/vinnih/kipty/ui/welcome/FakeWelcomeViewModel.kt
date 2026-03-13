package io.github.vinnih.kipty.ui.welcome

import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeWelcomeViewModel(welcomeUiState: WelcomeUiState = WelcomeUiState()) : WelcomeController {

    override val uiState: StateFlow<WelcomeUiState> = MutableStateFlow(welcomeUiState)

    override fun nextStep() {}

    override fun previousStep() {}

    override fun updateUsername(username: String) {}

    override fun updateProfileIcon(file: File) {}

    override suspend fun saveProfile() {}

    override suspend fun populateDatabase(onSuccess: () -> Unit) {}
}
