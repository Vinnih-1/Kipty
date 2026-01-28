package io.github.vinnih.kipty.ui.home

import io.github.vinnih.kipty.data.FakeAudioData
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.json.Json

class FakeHomeViewModel : HomeController {

    private val json = Json { ignoreUnknownKeys = true }

    private val fakeAudioList: List<AudioEntity> = listOf(
        json.decodeFromString(FakeAudioData.audio_1865_02_01),
        json.decodeFromString(FakeAudioData.audio_1888_11_13)
    )

    override val homeUiState: StateFlow<HomeUiState> =
        MutableStateFlow(HomeUiState(audioList = fakeAudioList, isLoading = false))

    override fun getPlayTimeById(id: Int): Flow<Long> {
        return flowOf(fakeAudioList.find { it.uid == id }?.playTime ?: 0L)
    }

    override fun loadAudios() {}

    override fun openNotificationSettings() {}

    override suspend fun populateDatabase(onSuccess: () -> Unit) {}
}
