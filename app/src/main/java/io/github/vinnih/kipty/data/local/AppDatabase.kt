package io.github.vinnih.kipty.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.vinnih.kipty.data.local.dao.TranscriptionDao
import io.github.vinnih.kipty.data.local.entity.Transcription

@Database(entities = [Transcription::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transcriptionDao(): TranscriptionDao
}