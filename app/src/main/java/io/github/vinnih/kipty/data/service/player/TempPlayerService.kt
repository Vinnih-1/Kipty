package io.github.vinnih.kipty.data.service.player

import android.content.Context
import android.os.Looper
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@ViewModelScoped
class TempPlayerService @Inject constructor(@ApplicationContext private val context: Context) {
    @delegate:UnstableApi
    private val player: ExoPlayer by lazy {
        ExoPlayer.Builder(context)
            .setLooper(Looper.getMainLooper())
            .build()
    }

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    fun play(audioFilePath: String) {
        val mediaItem = MediaItem.fromUri(audioFilePath.toUri())
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
        _isPlaying.value = true

        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    _isPlaying.value = false
                    player.removeListener(this)
                }
            }
        })
    }

    fun stop() {
        player.stop()
        _isPlaying.value = false
    }

    fun release() = player.release()
}
