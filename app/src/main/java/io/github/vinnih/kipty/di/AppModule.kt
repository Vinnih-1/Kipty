package io.github.vinnih.kipty.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.vinnih.kipty.data.local.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun getDatabase(@ApplicationContext context: Context) = Room
        .databaseBuilder(context, AppDatabase::class.java, "app_database")
        .fallbackToDestructiveMigration(true)
        .build()

    @Provides
    @Singleton
    fun getTranscriptionDao(db: AppDatabase) = db.transcriptionDao()

    @Provides
    @Singleton
    fun getExoplayer(@ApplicationContext context: Context) = ExoPlayer.Builder(context).build()
}