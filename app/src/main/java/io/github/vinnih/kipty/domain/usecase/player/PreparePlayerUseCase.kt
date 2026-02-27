package io.github.vinnih.kipty.domain.usecase.player

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.service.player.PlayerService
import javax.inject.Inject

class PreparePlayerUseCase @Inject constructor(private val playerService: PlayerService) {
    operator fun invoke(audioEntity: AudioEntity) {
        val metadata = MediaMetadata.Builder().apply {
            setTitle(audioEntity.name)
            setDescription(audioEntity.description)
        }.build()

        val mediaItem = MediaItem.Builder().apply {
            setMediaMetadata(metadata)
            setMediaId("${audioEntity.uid}")
            setUri(
                Uri.Builder()
                    .scheme(if (audioEntity.isDefault) "asset" else "file")
                    .path(audioEntity.audioPath)
                    .build()
            )
        }.build()

        playerService.prepare(mediaItem)
    }
}
