package io.github.vinnih.kipty.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.media3.exoplayer.ExoPlayer
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.vinnih.kipty.data.database.AppDatabase
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "settings"
)

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun getExoplayer(@ApplicationContext context: Context) = ExoPlayer.Builder(context).build()

    @Provides
    @Singleton
    fun getDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, "kipty").build()

    @Provides
    @Singleton
    fun getAudioDao(database: AppDatabase) = database.audioDao()

    @Provides
    @Singleton
    fun getNotificationDao(database: AppDatabase) = database.notificationDao()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.dataStore
}
