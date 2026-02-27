package io.github.vinnih.kipty.domain.usecase.player

import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import io.github.vinnih.kipty.data.service.player.PlayerService
import javax.inject.Inject

class SeekToUseCase @Inject constructor(
    private val playerService: PlayerService,
    private val preparePlayer: PreparePlayerUseCase
) {
    operator fun invoke(position: Long) {
        playerService.seekTo(position)
    }

    operator fun invoke(
        audioEntity: AudioEntity,
        start: Long,
        end: Long,
        onSectionSet: (AudioTranscription) -> Unit
    ) {
        val index = playerService.findMediaItemIndexById(audioEntity.uid)
        if (index == -1) preparePlayer(audioEntity)

        onSectionSet(AudioTranscription(start, end, ""))
        playerService.seekTo(index, start)
        playerService.play()
    }

    operator fun invoke(audioEntity: AudioEntity, onAudioChanged: (AudioEntity) -> Unit) {
        onAudioChanged(audioEntity)
        val index = playerService.findMediaItemIndexById(audioEntity.uid)

        if (index == -1) {
            preparePlayer(audioEntity)
            playerService.seekToDefaultPosition(playerService.mediaItemCount - 1)
        } else {
            playerService.seekToDefaultPosition(index)
        }
        playerService.play()
    }
}
