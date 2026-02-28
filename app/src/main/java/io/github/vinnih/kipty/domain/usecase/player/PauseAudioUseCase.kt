package io.github.vinnih.kipty.domain.usecase.player

import io.github.vinnih.kipty.data.service.player.PlayerService
import javax.inject.Inject

class PauseAudioUseCase @Inject constructor(private val player: PlayerService) {

    operator fun invoke() = player.pause()
}
