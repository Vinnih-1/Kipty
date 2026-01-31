package io.github.vinnih.kipty.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.vinnih.kipty.data.database.repository.audio.AudioRepository
import io.github.vinnih.kipty.data.database.repository.audio.impl.AudioRepositoryImpl
import io.github.vinnih.kipty.data.database.repository.notification.NotificationRepository
import io.github.vinnih.kipty.data.database.repository.notification.impl.NotificationRepositoryImpl
import io.github.vinnih.kipty.data.database.repository.speech.SpeechRepository
import io.github.vinnih.kipty.data.database.repository.speech.impl.SpeechRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAudioRepository(impl: AudioRepositoryImpl): AudioRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        impl: NotificationRepositoryImpl
    ): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindSpeechRepository(impl: SpeechRepositoryImpl): SpeechRepository
}
