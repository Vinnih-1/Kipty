package io.github.vinnih.kipty.ui.player

import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import javax.inject.Inject

class ExoPlayerAdapter @Inject constructor(exoPlayer: ExoPlayer) : KiptyPlayer, Player by exoPlayer
