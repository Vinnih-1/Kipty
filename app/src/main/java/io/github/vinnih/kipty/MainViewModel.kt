package io.github.vinnih.kipty

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.vinnih.kipty.data.settings.AppPreferencesRepository
import io.github.vinnih.kipty.domain.usecase.settings.GetAppSettingsUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val repository: AppPreferencesRepository
) : ViewModel() {

    private val _initialScreen = MutableStateFlow<Screen?>(null)
    val initialScreen = _initialScreen.asStateFlow()

    private val _keepSplash = MutableStateFlow(true)
    val keepSplash = _keepSplash.asStateFlow()

    init {
        viewModelScope.launch {
            val settings = getAppSettingsUseCase().first { it != null } ?: return@launch
            _initialScreen.value = if (settings.username.isBlank()) Screen.Welcome else Screen.Home
        }

        viewModelScope.launch {
            combine(
                getAppSettingsUseCase(),
                repository.hasDatabasePopulatedFlow
            ) { settings, hasPopulated ->
                val goingToWelcome = settings?.username.isNullOrBlank()
                goingToWelcome || hasPopulated
            }.first { it }

            _keepSplash.value = false
        }
    }
}
