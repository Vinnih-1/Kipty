package io.github.vinnih.kipty.ui.player

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class PlayerViewModel @Inject constructor(
    override val player: ExoPlayer,
    @ApplicationContext private val context: Context
) : ViewModel(),
    PlayerController {
    private val _currentAudio = MutableStateFlow<AudioEntity?>(null)

    override val currentAudio: StateFlow<AudioEntity?> = _currentAudio.asStateFlow()

    override fun playAudio(audioEntity: AudioEntity) {
        val medatada = MediaMetadata.Builder().apply {
            setTitle(audioEntity.name)
            setDescription(audioEntity.description)
        }
        val mediaItem = MediaItem.Builder().apply {
            setMediaMetadata(medatada.build())
            setUri(Uri.fromFile(File(audioEntity.path, "audio.wav")))
        }

        player.setMediaItem(mediaItem.build())
        player.prepare()
        player.play()
        _currentAudio.value = audioEntity
    }

    override fun pauseAudio() {
        player.pause()
    }

    override fun seekTo(position: Long) {
        player.seekTo(position)
    }
}
