package io.github.vinnih.kipty.domain.usecase.player

import io.github.vinnih.kipty.data.service.player.PlayerService
import io.github.vinnih.kipty.ui.player.PlaybackSpeed
import javax.inject.Inject

class ChangePlaybackSpeedUseCase @Inject constructor(private val playerService: PlayerService) {

    operator fun invoke(playbackSpeed: PlaybackSpeed): PlaybackSpeed {
        val allSpeeds = PlaybackSpeed.entries
        val nextSpeed = allSpeeds[(allSpeeds.indexOf(playbackSpeed) + 1) % allSpeeds.size]

        playerService.setPlaybackSpeed(nextSpeed.value)

        return nextSpeed
    }
}
