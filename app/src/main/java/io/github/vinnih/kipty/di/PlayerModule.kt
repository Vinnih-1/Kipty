package io.github.vinnih.kipty.di

import androidx.media3.common.Player
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.github.vinnih.kipty.ui.player.ExoPlayerAdapter

@Module
@InstallIn(ViewModelComponent::class)
abstract class PlayerModule {

    @Binds
    abstract fun bindPlayer(impl: ExoPlayerAdapter): Player
}
