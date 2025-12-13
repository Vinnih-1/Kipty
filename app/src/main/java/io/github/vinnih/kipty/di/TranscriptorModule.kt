package io.github.vinnih.kipty.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.vinnih.kipty.data.transcriptor.Transcriptor
import io.github.vinnih.kipty.data.transcriptor.TranscriptorImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TranscriptorModule {

    @Binds
    @Singleton
    abstract fun bindTranscriptor(impl: TranscriptorImpl): Transcriptor
}
