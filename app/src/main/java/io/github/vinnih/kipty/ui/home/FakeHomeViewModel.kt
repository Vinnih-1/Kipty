package io.github.vinnih.kipty.ui.home

import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.preview.FakeAudioData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

private val fakeAudioList: List<AudioEntity> = listOf(
    json.decodeFromString(FakeAudioData.audio_1865_02_01),
    json.decodeFromString(FakeAudioData.audio_1888_11_13)
)

class FakeHomeViewModel(homeUiState: HomeUiState = HomeUiState(fakeAudioList)) : HomeController {

    override val homeUiState: StateFlow<HomeUiState> = MutableStateFlow(homeUiState)

    override fun getPlayTimeById(id: Int): Flow<Long> = flowOf(
        fakeAudioList.find {
            it.uid == id
        }?.playTime ?: 0L
    )

    override fun openNotificationSettings() {}
}
