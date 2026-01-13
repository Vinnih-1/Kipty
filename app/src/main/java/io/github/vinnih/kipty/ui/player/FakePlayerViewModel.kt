package io.github.vinnih.kipty.ui.player

import androidx.media3.exoplayer.ExoPlayer
import io.github.vinnih.kipty.data.FakeAudioData
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import io.github.vinnih.kipty.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakePlayerViewModel : PlayerController {
    override val player: ExoPlayer
        get() = TODO("Not yet implemented")
    override val uiState: StateFlow<PlayerUiState>
        get() = MutableStateFlow(
            PlayerUiState(
                json.decodeFromString<AudioEntity>(FakeAudioData.audio_1888_11_13),
                0f,
                0L
            )
        )
    override val currentAudio: StateFlow<AudioEntity?>
        get() = TODO("Not yet implemented")
    override val progress: StateFlow<Pair<Float, Long>>
        get() = TODO("Not yet implemented")

    override fun playPause(audioEntity: AudioEntity) {
        TODO("Not yet implemented")
    }

    override fun stopAudio() {
        TODO("Not yet implemented")
    }

    override fun seekTo(audioEntity: AudioEntity, start: Long, end: Long) {
        TODO("Not yet implemented")
    }

    override fun currentSection(): AudioTranscription? {
        TODO("Not yet implemented")
    }
}
