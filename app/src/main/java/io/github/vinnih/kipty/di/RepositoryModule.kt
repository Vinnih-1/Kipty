package io.github.vinnih.kipty.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.vinnih.kipty.data.database.repository.AudioRepositoryImpl
import io.github.vinnih.kipty.data.database.repository.NotificationRepositoryImpl
import io.github.vinnih.kipty.data.database.repository.SpeechRepositoryImpl
import io.github.vinnih.kipty.domain.repository.AudioRepository
import io.github.vinnih.kipty.domain.repository.NotificationRepository
import io.github.vinnih.kipty.domain.repository.SpeechRepository
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
