package io.github.vinnih.kipty.ui.welcome

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.vinnih.kipty.domain.usecase.settings.UpdateProfileIconUseCase
import io.github.vinnih.kipty.domain.usecase.settings.UpdateUsernameUseCase
import io.github.vinnih.kipty.domain.usecase.worker.PopulateDatabaseUseCase
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class WelcomeUiState(
    val step: WelcomeStep = WelcomeStep.INTRO,
    val username: String = "",
    val profileIconPath: String = "",
    val profileIconUpdatedAt: Long = 0L
)

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val populateDatabaseUseCase: PopulateDatabaseUseCase,
    private val updateUsernameUseCase: UpdateUsernameUseCase,
    private val updateProfileIconUseCase: UpdateProfileIconUseCase
) : ViewModel(),
    WelcomeController {

    private val _uiState = MutableStateFlow(WelcomeUiState())
    override val uiState: StateFlow<WelcomeUiState> = _uiState.asStateFlow()

    override fun nextStep() {
        _uiState.update { current ->
            val next = (current.step.ordinal + 1).coerceAtMost(WelcomeStep.entries.size - 1)
            current.copy(step = WelcomeStep.entries[next])
        }
    }

    override fun previousStep() {
        _uiState.update { current ->
            val next = (current.step.ordinal - 1).coerceAtLeast(0)
            current.copy(step = WelcomeStep.entries[next])
        }
    }

    override fun updateUsername(username: String) {
        _uiState.update { it.copy(username = username) }
    }

    override fun updateProfileIcon(file: File) {
        _uiState.update { it.copy(profileIconPath = file.absolutePath) }
    }

    override suspend fun saveProfile() {
        updateUsernameUseCase(_uiState.value.username)
        updateProfileIconUseCase(File(_uiState.value.profileIconPath))
    }

    override suspend fun populateDatabase(onSuccess: () -> Unit) {
        populateDatabaseUseCase(onSuccess)
    }
}
