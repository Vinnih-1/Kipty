package io.github.vinnih.kipty.domain.usecase.player

import io.github.vinnih.kipty.data.service.player.PlayerService
import javax.inject.Inject

class FindMediaUseCase @Inject constructor(private val playerService: PlayerService) {

    operator fun invoke(mediaId: Int): Int {
        for (i in 0 until playerService.mediaItemCount) {
            if (playerService.getMediaItemAt(i).mediaId == mediaId.toString()) {
                return i
            }
        }
        return -1
    }
}
