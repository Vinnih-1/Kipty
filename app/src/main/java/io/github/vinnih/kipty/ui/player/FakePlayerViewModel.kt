package io.github.vinnih.kipty.ui.player

import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import io.github.vinnih.kipty.data.FakeAudioData
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakePlayerViewModel : PlayerController {

    override val player: Player
        @OptIn(UnstableApi::class)
        get() = FakeKiptyPlayer()

    override val uiState: StateFlow<PlayerUiState>
        get() = MutableStateFlow(
            PlayerUiState(
                json.decodeFromString<AudioEntity>(FakeAudioData.audio_1888_11_13),
                0.5f,
                1800000L,
                3600000L
            )
        )

    override fun stopAudio() {}

    override fun seekTo(audioEntity: AudioEntity, start: Long, end: Long) {}

    override fun seekTo(audioEntity: AudioEntity) {}
}
