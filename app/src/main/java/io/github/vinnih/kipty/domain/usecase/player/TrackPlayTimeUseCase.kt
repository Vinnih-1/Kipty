package io.github.vinnih.kipty.domain.usecase.player

import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.service.player.PlayerService
import io.github.vinnih.kipty.domain.repository.AudioRepository
import javax.inject.Inject
import kotlinx.coroutines.delay

class TrackPlayTimeUseCase @Inject constructor(
    private val audioRepository: AudioRepository,
    private val playerService: PlayerService
) {
    suspend operator fun invoke(getCurrentAudio: () -> AudioEntity?) {
        while (true) {
            if (playerService.isPlaying) {
                getCurrentAudio()?.let { audio ->
                    audioRepository.incrementPlayTime(audio.uid)
                }
            }
            delay(1000)
        }
    }
}
