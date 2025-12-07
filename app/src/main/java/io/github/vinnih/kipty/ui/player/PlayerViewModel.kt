package io.github.vinnih.kipty.ui.player

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    override val player: ExoPlayer,
    @ApplicationContext private val context: Context
) : ViewModel(),
    PlayerController {

    override fun playAudio(audioEntity: AudioEntity) {
        TODO("Not yet implemented")
    }

    override fun pauseAudio() {
        TODO("Not yet implemented")
    }

    override fun seekTo(position: Float) {
        TODO("Not yet implemented")
    }
}
