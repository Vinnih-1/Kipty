package io.github.vinnih.kipty.data.service.player

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import dagger.hilt.android.scopes.ViewModelScoped
import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import javax.inject.Inject

@ViewModelScoped
class PlayerService @Inject constructor(val player: Player) {

    var section: AudioTranscription? = null

    fun prepare(mediaItem: MediaItem) {
        player.addMediaItem(mediaItem)
        player.prepare()
    }

    fun play() = player.play()

    fun pause() = player.pause()

    fun stop() {
        player.stop()
        player.clearMediaItems()
    }

    fun seekTo(position: Long) = player.seekTo(position)

    fun seekTo(index: Int, position: Long) = player.seekTo(index, position)

    fun seekToDefaultPosition(index: Int) = player.seekToDefaultPosition(index)

    fun setPlaybackSpeed(speed: Float) = player.setPlaybackSpeed(speed)

    fun seekToNextMediaItem() = player.seekToNextMediaItem()

    fun getMediaItemAt(index: Int): MediaItem = player.getMediaItemAt(index)

    fun findMediaItemIndexById(mediaId: Int): Int {
        for (i in 0 until player.mediaItemCount) {
            if (player.getMediaItemAt(i).mediaId == mediaId.toString()) {
                return i
            }
        }
        return -1
    }

    fun checkSectionBoundary(): Boolean {
        val current = section ?: return false
        if (current.end != 0L && player.currentPosition >= current.end) {
            player.pause()
            section = null
            return true
        }
        return false
    }

    fun onMediaItemTransition(callback: (mediaId: String) -> Unit) {
        player.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                if (mediaItem == null) return
                callback(mediaItem.mediaId)
            }
        })
    }

    val isPlaying get() = player.isPlaying
    val duration get() = player.duration
    val currentPosition get() = player.currentPosition
    val mediaItemCount get() = player.mediaItemCount
    val hasNextMediaItem get() = player.hasNextMediaItem()
}
