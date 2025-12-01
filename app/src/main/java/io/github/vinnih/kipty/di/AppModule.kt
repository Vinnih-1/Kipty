package io.github.vinnih.kipty.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.vinnih.kipty.data.application.AppConfig
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun getExoplayer(@ApplicationContext context: Context) = ExoPlayer.Builder(context).build()

    @Provides
    @Singleton
    fun getAppConfig(@ApplicationContext context: Context) = AppConfig(context)
}
