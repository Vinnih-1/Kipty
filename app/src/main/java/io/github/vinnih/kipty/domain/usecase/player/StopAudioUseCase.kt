package io.github.vinnih.kipty.domain.usecase.player

import io.github.vinnih.kipty.data.service.player.PlayerService
import javax.inject.Inject

class StopAudioUseCase @Inject constructor(private val player: PlayerService) {

    operator fun invoke(onStop: () -> Unit) {
        if (player.hasNextMediaItem) {
            player.seekToNextMediaItem()
        } else {
            onStop()
            player.stop()
        }
    }
}
