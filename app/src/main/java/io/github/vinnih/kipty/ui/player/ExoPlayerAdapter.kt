package io.github.vinnih.kipty.ui.player

import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class ExoPlayerAdapter @Inject constructor(exoPlayer: ExoPlayer) :
    KiptyPlayer,
    Player by exoPlayer
