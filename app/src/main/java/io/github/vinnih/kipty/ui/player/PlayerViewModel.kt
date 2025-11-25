package io.github.vinnih.kipty.ui.player

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel
    @Inject
    constructor(
        private val exoPlayer: ExoPlayer,
        @ApplicationContext private val context: Context,
    ) : ViewModel() {
        private val _selectedTranscription = MutableStateFlow<MediaItem?>(null)

        private val currentMediaItem: StateFlow<MediaItem?> = _selectedTranscription.asStateFlow()

        fun stopTranscription() = exoPlayer.stop()

        fun startTranscription() = exoPlayer.play()
    }
